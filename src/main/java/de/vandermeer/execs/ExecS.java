/* Copyright 2014 Sven van der Meer <vdmeer.sven@mykolab.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.vandermeer.execs;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrTokenizer;

/**
 * A service executor.
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.0.5 build 150623 (23-Jun-15) for Java 1.8
 */
public class ExecS {
	/** Name of the application for help/usage and printouts. */
	String appName = "execs";

	/** Filter for jar files to be considered during search, this can speed up search process. */
	List<String> jarFilter = null;

	/** Filter for package names, this can speed up search process. */
	String packageFilter = null;

	/**
	 * Map of pre-registered services.
	 * Use {@link #addService(String, Class)} to add your own services.
	 */
	final TreeMap<String, Class<? extends ExecutableService>> byClass;

	/** Set of all classes filled during runtime search */
	final TreeSet<String> byName;

	/**
	 * Standard constructor as used via direct command line execution.
	 */
	public ExecS(){
		this(null);
	}

	/**
	 * Constructor for sub-classes that wish to overwrite the application name.
	 * @param appName name of the application
	 */
	public ExecS(String appName){
		this.appName = (appName==null)?this.appName:appName;

		this.byClass = new TreeMap<String, Class<? extends ExecutableService>>();
		this.byName = new TreeSet<String>();
	}

	/**
	 * Sets the jar filter for the executor.
	 * This filter can contain any name of a jar file that should be considered during search.
	 * If not set, no filter is applied. If the argument is null, no filter is used. Otherwise, all
	 * strings in the argument are used as jar files and only those jar files will be used in a search.
	 * Using filters can speed up the search process.
	 * Filters can be set any time, a new filter will overwrite an existing one (using null will disable filtering).
	 * @param filter new array of jar file names for filters, null means do not use any filter. 
	 */
	protected final void setJarFilter(String[] filter){
		this.jarFilter = (filter==null)?null:Collections.unmodifiableList(Arrays.asList(filter));
	}

	/**
	 * Sets the filter for package names.
	 * The string is used using a "starts-with" comparison of any package name during the search process.
	 * A filter set to "null" means no package filter is applied.
	 * Filters can be set any time, a new filter will overwrite an existing one (using null will disable filtering).
	 * @param filter package filter, null means no filter will be applied
	 */
	protected final void setPackageFiler(String filter){
		this.packageFilter = filter;
	}

	/**
	 * Adds a new service at runtime, with name (shortcut to start) and related class.
	 * @param name a unique name for the service
	 * @param clazz the class of the service for instantiation
	 */
	protected final void addService(String name, Class<? extends ExecutableService> clazz){
		this.byClass.put(name, clazz);
	}

	/**
	 * Adds a set of service at runtime, as in all found services that can be executed
	 * @param set a set of services
	 */
	protected final void addAllServices(Set<Class<?>> set){
		for(Class<?> cls:set){
			if(!cls.isInterface() && !Modifier.isAbstract(cls.getModifiers())){
				if(!this.byClass.containsValue(cls)){
					this.byName.add(cls.getName());
				}
			}
		}
	}

	/**
	 * Main method
	 * @param args command line arguments
	 * @return 0 on success, -1 on error with message on STDERR
	 */
	public final int execute(String[] args){
		String arguments = StringUtils.join(args, ' ');
		StrTokenizer tokens = new StrTokenizer(arguments);

		String arg = tokens.nextToken();
		int ret = 0;

		if(arg==null || "-h".equals(arg) || "--help".equals(arg)){
			//First help: no arguments or -h or --help -> print usage and exit(0)
			this.printUsage();
			return 0;
		}
		else if("-l".equals(arg) || "--list".equals(arg)){
			//Second list: if -l or --list -> trigger search and exit(0)
			this.addAllServices(new ClassFinder().findSubclasses(
					ExecutableService.class.getName(),
					(ArrayUtils.contains(args, "-j"))?this.jarFilter:null,
					(ArrayUtils.contains(args, "-p"))?this.packageFilter:null
			));
			this.helpScreen();
			return 0;
		}
		else if(this.byClass.containsKey(arg)){
			try{
				Object svc=this.byClass.get(arg).newInstance();
				if(svc instanceof ExecutableService){
					arg = tokens.nextToken();
					if("-h".equals(arg)){
						((ExecutableService)svc).serviceHelpScreen();
					}
					else{
						ret = ((ExecutableService)svc).executeService(ArrayUtils.remove(args, 0));
					}
				}
			}
			catch(IllegalAccessException | InstantiationException iex){
				System.err.println(this.appName + ": tried to execute <" + args[0] + "> by registered name -> exception");
//				System.err.println(iex);
//				iex.printStackTrace();
				ret = -1;
			}
		}
		else{
			try{
				Class<?> c = Class.forName(arg);
				Object svc = c.newInstance();
				if(svc instanceof ExecutableService){
					arg = tokens.nextToken();
					if("-h".equals(arg)){
						((ExecutableService)svc).serviceHelpScreen();
					}
					else{
						ret = ((ExecutableService)svc).executeService(ArrayUtils.remove(args, 0));
					}
				}
				else{
					System.err.println("given class is not instance of " + ExecutableService.class.getName());
					ret = -1;
				}
			}
			catch(ClassNotFoundException | IllegalAccessException | InstantiationException ex){
				System.err.println(this.appName + ": tried to execute <" + args[0] + "> as class name -> exception");
//				ex.printStackTrace();
				ret = -1;
			}
		}

		if(ret==-1){
			//now we are in trouble, nothing we could do worked, so print that and quit
			System.err.println(this.appName + ": no service could be started and nothing else could be done, try '-h' or '--help' for help");
			System.err.println();
		}
		return ret;
	}

	/**
	 * Prints a help screen with options and found executable services
	 */
	protected void helpScreen(){
		System.out.println(" -> defined servers:");
		for(String key : this.byClass.keySet()){
			System.out.println("                -> "+key);
		}
		System.out.println();
		System.out.println(" -> servers in CP:");
		for(String key : this.byName){
			System.out.println("                -> "+key);
		}
		System.out.println();
		System.out.println(" -> start any other ES_Service using <package> and <class> name");
		System.out.println(" -> list all servers in classpath using '-l' or '-list'");
	}

	/**
	 * Prints a list of pre-registered and found services.
	 */
	protected final void printList(){
		System.out.println(this.appName + ": list of available services");
		System.out.println();

		System.out.print("  --> defined services: ");
		if(this.byClass.keySet().size()==0){
			System.out.println("none\n");
		}
		else{
			System.out.println();
			for(String key : this.byClass.keySet()){
				System.out.println("      - " + key);
			}
		}
		System.out.println();

		System.out.print("  --> servers in class path: ");
		if(this.byName.size()==0){
			System.out.println("none\n");
		}
		else{
			System.out.println();
			for(String key : this.byName){
				System.out.println("      - " + key);
			}
		}

		System.out.println();
	}

	/**
	 * Prints usage information to standard out.
	 */
	protected final void printUsage(){
		System.out.println(this.appName + ": requires class, service name, or arguments");
		System.out.println();

		System.out.println("usage: " + this.appName + " <class> [class-options]");
		System.out.println("  Executes a service by classname, class must implement the EXECS service interface.");
		System.out.println("  <class> must be a fully qualified class name, i.e. with package and class name");
		System.out.println("  [class-options] are command line options forwarded to the executed service");
		System.out.println();

		System.out.println("usage: " + this.appName + " <service> [service-options]");
		System.out.println("  Executes a service by registered name, EXECS service interface must be implemented.");
		System.out.println("  <service> must be name registered with the application");
		System.out.println("  [service-options] are command line options forwarded to the executed service");
		System.out.println();

		System.out.println("usage: " + this.appName + " [-l | --list] [-j] [-p]");
		System.out.println("  Lists all available services, i.e. classes implementing the service interface");
		System.out.println("  Services are found by searching through all jars in the classpath (can be SLOW!).");
		System.out.println("  -j - activate a jar filter if set for the executor");
		System.out.println("  -p - activate a package filter if set for the executor");

		System.out.println("usage: " + this.appName + " [-h | --help]");
		System.out.println("  Prints this help screen.");
		System.out.println();

		System.out.println("set values: " + this.appName);
		System.out.println("  package filter: " + this.packageFilter);
		System.out.println("  jar filter: " + this.jarFilter);

		System.out.println();
		System.out.println();
	}

	/**
	 * Returns the set application name for the executor.
	 * This name is used for print outs only.
	 * @return set application name
	 */
	public final String getAppName(){
		return this.appName;
	}

	/**
	 * Public main to start any service.
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
//		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
//		lc.getBirthTime();
//		StatusPrinter.print(lc);

		ExecS run = new ExecS();
		int ret = run.execute(args);
		System.exit(ret);
	}
}

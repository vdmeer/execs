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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrTokenizer;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

import de.vandermeer.execs.cf.CF;

/**
 * A service executor.
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.1.0 build 150812 (12-Aug-15) for Java 1.8
 * @since      v0.0.1
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
	protected final TreeMap<String, Class<? extends ExecutableService>> classmap;

	/** Set of all classes filled during runtime search */
	final TreeSet<String> classNames;

	/** Local ST Group for printouts. */
	final STGroupFile stg;

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

		this.classmap = new TreeMap<String, Class<? extends ExecutableService>>();
		this.classNames = new TreeSet<String>();

		this.addService(Gen_RunScripts.SERVICE_NAME, Gen_RunScripts.class);
		this.addService(Gen_RunSh.SERVICE_NAME, Gen_RunSh.class);
		this.addService(Gen_RebaseSh.SERVICE_NAME, Gen_RebaseSh.class);

		this.stg = new STGroupFile("de/vandermeer/execs/execs.stg");
	}

	/**
	 * Returns a set of names that have been registered as services with associated execution services.
	 * @return list of names for registered execution services, empty list if none registered
	 */
	public Set<String> getRegisteredServices(){
		return this.classmap.keySet();
	}

//	/**
//	 * Returns the full class map of the executor.
//	 * @return full class map, empty if no classes set
//	 */
//	Map<String, Class<? extends ExecutableService>> getClassMap(){
//		return this.classmap;
//	}

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
		this.classmap.put(name, clazz);
	}

	/**
	 * Adds a set of service at runtime, as in all found services that can be executed
	 * @param set a set of services
	 */
	protected final void addAllServices(Set<Class<?>> set){
		for(Class<?> cls:set){
			if(!cls.isInterface() && !Modifier.isAbstract(cls.getModifiers())){
				if(!this.classmap.containsValue(cls)){
					this.classNames.add(cls.getName());
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
			CF cf = new CF()
				.setJarFilter((ArrayUtils.contains(args, "-j"))?this.jarFilter:null)
				.setPkgFilter((ArrayUtils.contains(args, "-p"))?this.packageFilter:null);
			this.addAllServices(cf.getSubclasses(ExecutableService.class));
			this.printList();
			return 0;
		}
		else if(this.classmap.containsKey(arg)){
			ret = this.executeByClassmap(args, tokens, arg);
		}
		else{
			ret = this.executeByClassName(args, tokens, arg);
		}

		if(ret==-1){
			//now we are in trouble, nothing we could do worked, so print that and quit
			System.err.println(this.appName + ": no service could be started and nothing else could be done, try '-h' or '--help' for help");
			System.err.println();
		}
		return ret;
	}

	/**
	 * Executes a service by class mapping.
	 * @param args command line arguments
	 * @param tokens tokenized CLI arguments
	 * @param arg current argument
	 * @return 0 on success, -1 on error
	 */
	protected int executeByClassmap(String[] args, StrTokenizer tokens, String arg){
		int ret = 0;
		try{
			Object svc = this.classmap.get(arg).newInstance();
			if(svc instanceof ExecutableService){
				arg = tokens.nextToken();
				if("-h".equals(arg)){
					((ExecutableService)svc).serviceHelpScreen();
				}
				else{
					if(svc instanceof Gen_RunScripts){
						//hook for GenRunScripts to get currrent class map - registered services
						((Gen_RunScripts)svc).setClassMap(this.classmap);
					}
					ret = ((ExecutableService)svc).executeService(ArrayUtils.remove(args, 0));
				}
			}
		}
		catch(IllegalAccessException | InstantiationException iex){
			System.err.println(this.appName + ": tried to execute <" + args[0] + "> by registered name -> exception");
//			System.err.println(iex);
//			iex.printStackTrace();
			ret = -1;
		}

		return ret;
	}

	/**
	 * Executes a service by class name, that is using a fully qualified class name.
	 * @param args command line arguments
	 * @param tokens tokenized CLI arguments
	 * @param arg current argument
	 * @return 0 on success, -1 on error
	 */
	protected int executeByClassName(String[] args, StrTokenizer tokens, String arg){
		int ret = 0;
		try{
			Class<?> c = Class.forName(arg);
			Object svc = c.newInstance();
			if(svc instanceof ExecutableService){
				arg = tokens.nextToken();
				if("-h".equals(arg)){
					((ExecutableService)svc).serviceHelpScreen();
				}
				else{
					if(svc instanceof Gen_RunScripts){
						//hook for GenRunScripts to get currrent class map - registered services
						((Gen_RunScripts)svc).setClassMap(this.classmap);
					}
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
//			ex.printStackTrace();
			ret = -1;
		}

		return ret;
	}

	/**
	 * Prints a list of pre-registered and found services.
	 */
	protected final void printList(){
		ST list = this.stg.getInstanceOf("list");
		list.add("appName", this.appName);
		if(this.classmap.size()>0){
			List<Map<String, String>> l = new ArrayList<>();
			for(String key : this.classmap.keySet()){
				Map<String, String> m = new HashMap<>();
				m.put("key", key);
				m.put("val", this.classmap.get(key).getName());
				l.add(m);
			}
			list.add("classMap", l);
		}
		list.add("className", this.classNames);
		System.out.println(list.render());
	}

	/**
	 * Prints usage information to standard out.
	 */
	protected final void printUsage(){
		ST usage = this.stg.getInstanceOf("usage");
		usage.add("appName", this.appName);
		usage.add("packageFilter", this.packageFilter);
		usage.add("jarFilter", this.jarFilter);
		System.out.println(usage.render());
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

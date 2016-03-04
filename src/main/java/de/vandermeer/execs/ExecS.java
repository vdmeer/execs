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
 * The application executor.
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.3.6 build 160304 (04-Mar-16) for Java 1.8
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
	 * Map of pre-registered applications.
	 * Use {@link #addApplication(String, Class)} to add your own applications.
	 */
	protected final TreeMap<String, Class<? extends ExecS_Application>> classmap;

	/** Application version, should be same as the version in the class JavaDoc. */
	public final static String APP_VERSION = "v0.3.6 build 160304 (04-Mar-16) for Java 1.8";

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

		this.classmap = new TreeMap<String, Class<? extends ExecS_Application>>();
		this.classNames = new TreeSet<String>();

		this.addApplication(Gen_RunScripts.APP_NAME, Gen_RunScripts.class);
		this.addApplication(Gen_ConfigureSh.APP_NAME, Gen_ConfigureSh.class);

		this.stg = new STGroupFile("de/vandermeer/execs/execs.stg");
	}

	/**
	 * Returns a set of names that have been registered as applications with associated execution applications.
	 * @return list of names for registered execution applications, empty list if none registered
	 */
	public Set<String> getRegisteredApplications(){
		return this.classmap.keySet();
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
	 * Adds a new application at runtime, with name (shortcut to start) and related class.
	 * @param name a unique name for the application
	 * @param clazz the class of the application for instantiation
	 */
	protected final void addApplication(String name, Class<? extends ExecS_Application> clazz){
		this.classmap.put(name, clazz);
	}

	/**
	 * Adds a set of application at runtime, as in all found applications that can be executed
	 * @param set a set of applications
	 */
	protected final void addAllApplications(Set<Class<?>> set){
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

		if(arg==null || "-?".equals(arg) || "-h".equals(arg) || "--help".equals(arg)){
			//First help: no arguments or -? or --help -> print usage and exit(0)
			this.printUsage();
			return 0;
		}
		else if("-v".equals(arg) || "--version".equals(arg)){
			System.out.println(this.appName + " - " + ExecS.APP_VERSION);
			System.out.println();
		}
		else if("-l".equals(arg) || "--list".equals(arg)){
			//Second list: if -l or --list -> trigger search and exit(0)
			CF cf = new CF()
				.setJarFilter((ArrayUtils.contains(args, "-j"))?this.jarFilter:null)
				.setPkgFilter((ArrayUtils.contains(args, "-p"))?this.packageFilter:null);
			this.addAllApplications(cf.getSubclasses(ExecS_Application.class));
			this.printList();
			return 0;
		}
		else{
			Object svc = null;
			if(this.classmap.containsKey(arg)){
				try{
					svc = this.classmap.get(arg).newInstance();
				}
				catch(IllegalAccessException | InstantiationException iex){
					System.err.println(this.appName + ": tried to execute <" + args[0] + "> by registered name -> exception: " + iex.getMessage());
//					iex.printStackTrace();
					ret = -99;
				}
			}
			else{
				try{
					Class<?> c = Class.forName(arg);
					svc = c.newInstance();
				}
				catch(ClassNotFoundException | IllegalAccessException | InstantiationException ex){
					System.err.println(this.appName + ": tried to execute <" + args[0] + "> as class name -> exception: " + ex.getMessage());
//					ex.printStackTrace();
					ret = -99;
				}
			}
			ret = this.executeApplication(svc, args, arg);
		}

		if(ret==-99){
			//now we are in trouble, nothing we could do worked, so print that and quit
			System.err.println(this.appName + ": no application could be started and nothing else could be done, try '-?' or '--help' for help");
			System.err.println();
		}
		return ret;
	}

	/**
	 * Executes an application.
	 * @param svc the application, must not be null
	 * @param args original command line arguments
	 * @param orig the original name or class for the application for error messages
	 * @return 0 on success, other than zero on failure (including failure of the executed application)
	 */
	protected int executeApplication(Object svc, String[] args, String orig){
		if(svc!=null && (svc instanceof ExecS_Application)){
			if(svc instanceof Gen_RunScripts){
				//hook for GenRunScripts to get current class map - registered applications
				((Gen_RunScripts)svc).setClassMap(this.classmap);
			}
			return ((ExecS_Application)svc).executeApplication(ArrayUtils.remove(args, 0));
		}
		else if(svc==null){
			System.err.println("could not create object for class or application name <" + orig + ">");
			return -1;
		}
		else if(!(svc instanceof ExecS_Application)){
			System.err.println("given class or application name <" + orig + "> is not instance of " + ExecS_Application.class.getName());
			return -2;
		}
		else{
			System.err.println("unexpected error processing for class or application name <" + orig + ">");
			return -3;
		}
	}

	/**
	 * Prints a list of pre-registered and found applications.
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
		usage.add("excludedNames", new TreeSet<>(Arrays.asList(new CF().excludedNames)));
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
	 * Public main to start the application executor.
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		ExecS run = new ExecS();
		int ret = run.execute(args);
		System.exit(ret);
	}

}

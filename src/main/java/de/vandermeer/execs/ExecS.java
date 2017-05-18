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
import de.vandermeer.skb.interfaces.MessageType;
import de.vandermeer.skb.interfaces.application.IsApplication;
import de.vandermeer.skb.interfaces.console.MessageConsole;
import de.vandermeer.skb.interfaces.messages.MessageManager;

/**
 * The application executor.
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.4.0 build 170413 (13-Apr-17) for Java 1.8
 * @since      v0.0.1
 */
public class ExecS {

	/** Application version, should be same as the version in the class JavaDoc. */
	public final static String APP_VERSION = "v0.4.0 build 170413 (13-Apr-17) for Java 1.8";

	/** The short CLI option for help defined as `-h`. */
	public final static String CLI_HELP_SHORT = "-h";

	/** The long CLI option for help defined as `--help`. */
	public final static String CLI_HELP_LONG = "--help";

	/** The short CLI option for version defined as `-v`. */
	public final static String CLI_VERSION_SHORT = "-v";

	/** The long CLI option for version defined as `--version`. */
	public final static String CLI_VERSION_LONG = "--version";

	/** The short CLI option for list defined as `-l`. */
	public final static String CLI_LIST_SHORT = "-l";

	/** The long CLI option for list defined as `--list`. */
	public final static String CLI_LIST_LONG = "--list";

	/**
	 * Public main to start the application executor.
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		final ExecS run = new ExecS();
		run.execute(args);
		System.exit(run.getErrNo());
	}

	/** Name of the application for help/usage and printouts. */
	protected transient String appName = "execs";

	/** Filter for jar files to be considered during search, this can speed up search process. */
	protected transient List<String> jarFilter;

	/** Filter for package names, this can speed up search process. */
	protected transient String packageFilter;

	/** Map of registered applications. */
	protected final transient TreeMap<String, Class<? extends IsApplication>> classmap;

	/** Set of all classes filled during runtime search */
	protected final transient TreeSet<String> classNames;

	/** Local ST Group for printouts. */
	protected final transient STGroupFile stg = new STGroupFile("de/vandermeer/execs/execs.stg");

	/** Message manager. */
	protected final transient MessageManager msgMgr;

	/** Error number, holds the number of the last error, 0 if none occurred. */
	protected transient int errNo;

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

		this.classmap = new TreeMap<String, Class<? extends IsApplication>>();
		this.classNames = new TreeSet<String>();

		this.addApplication(Gen_RunScripts.APP_NAME, Gen_RunScripts.class);
		this.addApplication(Gen_ConfigureSh.APP_NAME, Gen_ConfigureSh.class);
		this.addApplication(Gen_ExecJarScripts.APP_NAME, Gen_ExecJarScripts.class);

		MessageConsole.activateAll();
		MessageConsole.setApplicationName(this.appName);
		this.msgMgr = MessageManager.create();
	}

	/**
	 * Adds a set of application at runtime, as in all found applications that can be executed.
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
	 * Adds a new application at runtime, with name (shortcut to start) and related class.
	 * @param name a unique name for the application
	 * @param clazz the class of the application for instantiation
	 */
	protected final void addApplication(String name, Class<? extends IsApplication> clazz){
		this.classmap.put(name, clazz);
	}

	/**
	 * Main method.
	 * @param args command line arguments
	 * @return 0 on success, -1 on error with message on STDERR
	 */
	public final int execute(String[] args){
		final String arguments = StringUtils.join(args, ' ');
		final StrTokenizer tokens = new StrTokenizer(arguments);

		String arg = tokens.nextToken();
		this.errNo = 0;

		if(arg==null || CLI_HELP_SHORT.equals(arg) || CLI_HELP_LONG.equals(arg)){
			//help: no arguments or -h or --help -> print usage and exit(1)
			this.printUsage();
			this.errNo = 1;
		}
		else if(CLI_VERSION_SHORT.equals(arg) || CLI_VERSION_LONG.equals(arg)){
			System.out.println(this.appName + " - " + ExecS.APP_VERSION);
			System.out.println();
			this.errNo = 1;
		}
		else if(CLI_LIST_SHORT.equals(arg) || CLI_LIST_LONG.equals(arg)){
			//list: if -l or --list -> trigger search and exit(1)
			CF cf = new CF()
				.setJarFilter((ArrayUtils.contains(args, "-j"))?this.jarFilter:null)
				.setPkgFilter((ArrayUtils.contains(args, "-p"))?this.packageFilter:null);
			this.addAllApplications(cf.getSubclasses(IsApplication.class));
			this.printList();
			this.errNo = 1;
		}
		else{
			Object svc = null;
			if(this.classmap.containsKey(arg)){
				try{
					svc = this.classmap.get(arg).newInstance();
				}
				catch(IllegalAccessException | InstantiationException iex){
					this.msgMgr.add(MessageType.ERROR, "tried to execute <{}> by registered name -> exception: {}", args[0], iex.getMessage());
//					iex.printStackTrace();
					this.errNo = -1;
				}
			}
			else{
				try{
					Class<?> c = Class.forName(arg);
					svc = c.newInstance();
				}
				catch(ClassNotFoundException | IllegalAccessException | InstantiationException ex){
					this.msgMgr.add(MessageType.ERROR, "tried to execute <{}> as class name -> exception: {}", args[0], ex.getMessage());
//					ex.printStackTrace();
					this.errNo = -2;
				}
			}

			if(this.errNo==0){
				this.executeApplication(svc, args, arg);
			}
			else{
				this.msgMgr.add(MessageType.ERROR, "no application could be started and nothing else could be done, try '-h' or '--help' for help");
			}
		}

		return this.errNo;
	}

	/**
	 * Executes an application.
	 * @param svc the application, must not be null
	 * @param args original command line arguments
	 * @param orig the original name or class for the application for error messages
	 */
	protected void executeApplication(Object svc, String[] args, String orig){
		this.errNo = 0;
		if(svc!=null && (svc instanceof IsApplication)){
			if(svc instanceof Gen_RunScripts){
				//hook for GenRunScripts to get current class map - registered applications
				((Gen_RunScripts)svc).setClassMap(this.classmap);
			}
			if(svc instanceof Gen_ExecJarScripts){
				//hook for Gen_ExecJarScripts to get current class map - registered applications
				((Gen_ExecJarScripts)svc).setClassMap(this.classmap);
			}
			((IsApplication)svc).executeApplication(ArrayUtils.remove(args, 0));
			this.errNo = ((IsApplication)svc).getErrNo();
		}
		else if(svc==null){
			this.msgMgr.add(MessageType.ERROR, "could not create object for class or application name <{}>", orig);
			this.errNo = -10;
		}
		else if(!(svc instanceof IsApplication)){
			this.msgMgr.add(MessageType.ERROR, "given class or application name <{}> is not instance of <{}>", orig, IsApplication.class.getName());
			this.errNo = -11;
		}
		else{
			this.msgMgr.add(MessageType.ERROR, "unexpected error processing for class or application name <{}>", orig);
			this.errNo = -12;
		}
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
	 * Returns the number of the last error, 0 if none occurred.
	 * @return last error number
	 */
	public int getErrNo(){
		return this.errNo;
	}

	/**
	 * Returns a set of names that have been registered as applications with associated execution applications.
	 * @return list of names for registered execution applications, empty list if none registered
	 */
	public Set<String> getRegisteredApplications(){
		return this.classmap.keySet();
	}

	/**
	 * Prints a list of pre-registered and found applications.
	 */
	protected final void printList(){
		final ST list = this.stg.getInstanceOf("list");
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
		final ST usage = this.stg.getInstanceOf("usage");
		usage.add("appName", this.appName);
		usage.add("packageFilter", this.packageFilter);
		usage.add("jarFilter", this.jarFilter);
		usage.add("excludedNames", new TreeSet<>(Arrays.asList(new CF().excludedNames)));
		System.out.println(usage.render());
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
	 * The string is used using a `starts-with` comparison of any package name during the search process.
	 * A filter set to `null` means no package filter is applied.
	 * Filters can be set any time, a new filter will overwrite an existing one (using null will disable filtering).
	 * @param filter package filter, null means no filter will be applied
	 */
	protected final void setPackageFiler(String filter){
		this.packageFilter = filter;
	}
}

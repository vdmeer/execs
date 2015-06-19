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
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang3.ArrayUtils;

/**
 * An SKB service executor.
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.0.4 build 150619 (19-Jun-15) for Java 1.8
 */
public class Skb_Exec {
	/** Cli parser */
	private Skb_ExecCli cli;

	/** Name of the application for help/usage and printouts. */
	protected String appName;

	/** Prefix for printing help/usage information */
	protected String linePrefix;

	/**
	 * An array of SKB jars, used to optimize for speed when searching in all known jars.
	 * Overwrite this member to use a different filter.
	 */
	protected String[] jarFilters = new String[]{
			"skb.asciitable",
			"skb.base",
			"skb.categories",
			"skb.collections",
			"skb.commons",
			"skb.composite",
			"skb.configuration",
			"skb.dal",
			"skb.dsl",
			"skb.tribe"
	};

	/**
	 * The main package for all SKB definitions, used to optimism for speed when searching in all known jars.
	 * Overwrite this member to use a different package filter.
	 */
	protected String pkgFilter = "de.vandermeer.skb.";

	/**
	 * Map of pre-registered services.
	 * Use {@link #addService(String, Class)} to add your own services.
	 */
	protected TreeMap<String, Class<? extends Skb_Executable>> byClass;

	/** Set of all classes filled during runtime search */
	private TreeSet<String> byName;

	/**
	 * Standard constructor as used via direct command line execution.
	 */
	public Skb_Exec(){
		this(null);
	}

	/**
	 * Constructor for sub-classes that wish to overwrite the application name.
	 * @param appName name of the application
	 */
	public Skb_Exec(String appName){
		this.appName = (appName==null)?"skb-execs":appName;
		this.linePrefix = this.appName + ": ";

		this.byClass = new TreeMap<String, Class<? extends Skb_Executable>>();
		this.byName = new TreeSet<String>();
		this.cli = new Skb_ExecCli(this.appName);
	}

	/**
	 * Adds a new service at runtime, with name (shortcut to start) and related class.
	 * @param name a unique name for the service
	 * @param clazz the class of the service for instantiation
	 */
	protected void addService(String name, Class<? extends Skb_Executable> clazz){
		this.byClass.put(name, clazz);
	}

	/**
	 * Adds a set of service at runtime, as in all found SKB services that can be executed
	 * @param set a set of services
	 */
	protected void addAllServices(Set<Class<?>> set){
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
	public int execute(String[] args){
		try{
			this.cli.parse(args);
		}
		catch(Exception pe){
			System.out.println("\n" + this.linePrefix + pe.getMessage());
			System.out.println(this.linePrefix + "requires class, service name, or arguments");
			System.out.println(this.linePrefix + "try -h for help");
			return -1;
		}

		//first exit criteria: no arguments
		if(args.length==0){
			this.printUsage();
			return 0;
		}

		//next, check if we can work with the first argument as named service or class name
		String cn = args[0];

		//start with named services, if we found one execute it
		if(this.byClass.containsKey(cn)){
			try{
				Object svc = this.byClass.get(cn).newInstance();
				if(svc instanceof Skb_Executable){
					if(ArrayUtils.contains(args, "-h")){
						((Skb_Executable)svc).serviceHelpScreen();
						return 0;
					}
					else{
						return ((Skb_Executable)svc).executeService(ArrayUtils.remove(args, 0));
					}
				}
			}
			catch(IllegalAccessException | InstantiationException iex){
				//exception here means we could not start a named service, more processing before we give in
//				System.err.println("exception caught while starting service by class, see details below");
//				System.err.println(iex);
//				iex.printStackTrace();
			}
		}

		//now try if we can instantiate a class from cn
		try{
			Class<?> c = Class.forName(cn);
			Object svc = c.newInstance();
			if(svc instanceof Skb_Executable){
				if(ArrayUtils.contains(args, "-h")){
					((Skb_Executable)svc).serviceHelpScreen();
					return 0;
				}
				else{
					return ((Skb_Executable)svc).executeService(ArrayUtils.remove(args, 0));
				}
			}
		}
		catch(ClassNotFoundException | IllegalAccessException | InstantiationException ex){
			//exception here means we could not start a FQCN, more processing before we give in
//			System.err.println("exception caught while starting service by name, see details below");
//			ex.printStackTrace();
//			ret = -1;
		}

		//ok, cn was not anything we could execute, now we can try if we were ment to do something else

		//exit option
		if(this.cli.hasOptionHelp()){
			this.printUsage();
			return 0;
		}

		if(this.cli.hasOptionList()){
			this.addAllServices(new ClassFinder().findSubclasses(
					Skb_Executable.class.getName(),
					(this.cli.hasOptionJarFilter())?Collections.unmodifiableList(Arrays.asList(this.jarFilters)):null,
					(this.cli.hasOptionPkgFilter())?this.pkgFilter:null
			));
			this.printList();
			return 0;
		}

		//now we are in trouble, nothing we could do worked, so print that and quit
		System.err.println(this.linePrefix + "no service could be started and nothing else could be done, try -h for help");
		return -1;
	}

	/**
	 * Prints a help screen with options and found executable services
	 */
	protected void helpScreen(){
		System.out.println(" -> defined servers: "+this.byClass.keySet());
		System.out.println(" -> servers in CP: ");
		for(String key : this.byName){
			System.out.println("                -> "+key);
		}
		System.out.println();
		System.out.println(" -> start any other found service using <package> and <class> name");
		System.out.println(" -> list all servers in classpath using '-l' or '-list'");
	}

	/**
	 * Prints a list of pre-registered and found services.
	 */
	protected final void printList(){
		System.out.println(this.linePrefix + "list of available services");
		System.out.println();

		System.out.print("  --> defined services: ");
		if(this.byClass.keySet().size()==0){
			System.out.println("none\n");
		}
		else{
			System.out.println();
			for(String key : this.byClass.keySet()){
				System.out.println("      - "+key);
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
				System.out.println("      - "+key);
			}
		}

		System.out.println();
	}

	/**
	 * Prints usage information to standard out.
	 */
	protected final void printUsage(){
		System.out.println(this.linePrefix + "requires class, service name, or arguments");
		System.out.println();

		System.out.println("usage: " + this.appName + " <class> [class-options]");
		System.out.println("  <class> must be a fully qualified class name with package and class name");
		System.out.println("  [class-options] are command line options forwarded to the executed service");
		System.out.println();

		System.out.println("usage: " + this.appName + " <service> [service-options]");
		System.out.println("  <service> must be name registered with the application");
		System.out.println("  [service-options] are command line options forwarded to the executed service");
		System.out.println();

		this.cli.usage();
		System.out.println();
		System.out.println();
	}

	/**
	 * Public main to start any SKB Service.
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
//		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
//		lc.getBirthTime();
//		StatusPrinter.print(lc);

		Skb_Exec run = new Skb_Exec();
		int ret = run.execute(args);
		System.exit(ret);
	}
}

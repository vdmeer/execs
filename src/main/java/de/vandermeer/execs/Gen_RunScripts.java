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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

/**
 * Executable service to generate run scripts for other executable services, supporting windows, CygWin and bash.
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.1.0 build 150812 (12-Aug-15) for Java 1.8
 * @since      v0.0.6
 */
public class Gen_RunScripts implements ExecutableService {

	/** Service name. */
	public final static String SERVICE_NAME = "gen-run-scripts";

	/** A CLI option to specify a StringTemplate (stg) file with templates for generating run scripts. */
	public final static ExecS_CliOption CLIOPT_STGFILE = ExecS_Factory.newCliOption(null, "stg-file", null, "specifies a string template (stg) file");

	/** A CLI option to specify a a property file with class names (executable services) mapped to script names. */
	public final static ExecS_CliOption CLIOPT_CLASSMAP_FILE = ExecS_Factory.newCliOption("classmap-file", "FILE", "a property file with class names (executable services) mapped to script names");

	/** A CLI option to specify a property file with configurations for the generator. */
	public final static ExecS_CliOption CLIOPT_PROP_FILE = ExecS_Factory.newCliOption("property-file", "FILE", "a property file with configurations for the generator");

	/** A CLI option to specify the application home directory specific to a given target format. */
	public final static ExecS_CliOption CLIOPT_APPLICATION_HOME_DIR = ExecS_Factory.newCliOption("application-dir", "DIR", "application home directory specific to a given target format");

	/** A property key for the script name of the generic run script, without file extension. */
	public final static String PROP_RUN_SCRIPT_NAME = "run.script.name";

	/** A property key for the class name of the ExexS executor to be used, must be a fully qualified class name. */
	public final static String PROP_RUN_CLASS = "run.script.class";

	/** A property key for the JAVA classpath. */
	public final static String PROP_JAVA_CP = "java.classpath";

	/** The string all java properties for running the JVM must start with. */
	public final static String PROP_JAVAPROP_START = "java.property.";

	/** A property key to set auto-script generation for registered executable services. */
	public final static String PROP_EXECS_AUTOGEN_REGISTERED = "execs.autogenerate.registered";

	/** Local CLI options for CLI parsing. */
	protected ExecS_Cli cli;

	/** Properties for configuration options. */
	protected Properties configuration;

	/** Properties with a class map. */
	protected Properties classMap;

	/** String template group loaded from STG file. */
	protected STGroupFile stg;

	/** The target set for generation. */
	protected String target;

	/** The application home directory, ideally something like "abc/xyz" as root for generating scripts. */
	protected String applicationDir;

	/** The name of the output directory, null means cannot output or not tested yet. */
	protected String outputDir;

	/** Local class map, must be set by calling ExecS instance. */
	Map<String, Class<? extends ExecutableService>> execClassMap;

	/**
	 * Returns a new generator with parameterized CLI object.
	 */
	public Gen_RunScripts() {
		this.cli = new ExecS_Cli();
		this.cli.addOption(StandardOptions.TARGET);
		this.cli.addOption(CLIOPT_STGFILE);
		this.cli.addOption(CLIOPT_CLASSMAP_FILE);
		this.cli.addOption(CLIOPT_APPLICATION_HOME_DIR);
		this.cli.addOption(CLIOPT_PROP_FILE);
	}

	void setClassMap(Map<String, Class<? extends ExecutableService>> execClassMap){
		this.execClassMap = execClassMap;
	}

	@Override
	public int executeService(String[] args) {
		// parse command line, exit with help screen if error
		int ret = ExecS_Cli.doParse(args, this.cli, this.getName());
		if(ret!=0){
			this.serviceHelpScreen();
			return ret;
		}

		// init generator, exit on error (init methods will have printed error to standard error)
		ret = this.initConfiguration();
		if(ret!=0){
			return ret;
		}
		ret = this.initApplicationDir();
		if(ret!=0){
			return ret;
		}
		ret = this.initTargetAndStg();
		if(ret!=0){
			return ret;
		}
		ret = this.initClassmap();
		if(ret==-1){
			return ret;
		}
		ret = this.initOutputDir();
		if(ret!=0){
			return ret;
		}

		HashMap<String, Boolean> targetMap = new HashMap<>();
		targetMap.put(target, true);
		String fnExtension = this.stg.getInstanceOf("fnExtension").add("target", targetMap).render();
		String targetFileSep = this.stg.getInstanceOf("pathSeparator").add("target", targetMap).render();

		//build main run script
		String outFN = this.outputDir + File.separator + this.configuration.get(PROP_RUN_SCRIPT_NAME) + fnExtension;
		System.out.println(" --> generating main run script - " + outFN);
		ST targetRunST = this.stg.getInstanceOf("generateRun");
		targetRunST.add("target", targetMap);
		targetRunST.add("class", this.configuration.get(PROP_RUN_CLASS));
		targetRunST.add("applicationHome", this.applicationDir);
		for(Object key : this.configuration.keySet()){
			if(StringUtils.startsWith(key.toString(), PROP_JAVAPROP_START)){
				String[] kv = StringUtils.split(this.configuration.getProperty(key.toString()), ":");
				HashMap<String, String> javaProperties = new HashMap<>();
				javaProperties.put("key", kv[0]);
				ST execHomeVar = this.stg.getInstanceOf("execHomeVar");
				javaProperties.put("val", StringUtils.replace(kv[1], "{APPLICATION_HOME}", execHomeVar.add("target", targetMap).render()));
				targetRunST.add("javaProperties", javaProperties);
			}
			if(PROP_JAVA_CP.equals(key.toString())){
				String[] cp = StringUtils.split(this.configuration.getProperty(key.toString()), " , ");
				ST classpath = this.stg.getInstanceOf("classpath");
				classpath.add("target", targetMap);
				for(String s : cp){
					classpath.add("classpath", StringUtils.replace(s, "/", targetFileSep));
				}
				targetRunST.add("classPath", classpath.render());
			}
		}
		this.writeFile(outFN, targetRunST);

		//build all scripts for classmap, if set
		if(this.classMap!=null){
			for(Object key : this.classMap.keySet()){
				outFN = this.outputDir + File.separator + this.classMap.get(key) + fnExtension;
				System.out.println(" --> generating script from class map - " + outFN);

				ST targetExecST = this.generateScript(key.toString(), targetMap);
//				this.stg.getInstanceOf("generateExec");
//				targetExecST.add("target", targetMap);
//				targetExecST.add("applicationHome", this.applicationDir);
//				targetExecST.add("runName", this.configuration.get(PROP_RUN_SCRIPT_NAME));
//				targetExecST.add("class", key);
				this.writeFile(outFN, targetExecST);
			}
		}

		//build scripts for all registered services using the specified run class
		if(this.configuration.get(PROP_EXECS_AUTOGEN_REGISTERED)!=null && BooleanUtils.toBoolean(this.configuration.get(PROP_EXECS_AUTOGEN_REGISTERED).toString())){
//			try{
//				Class<?> c = Class.forName(this.configuration.get(PROP_RUN_CLASS).toString());
//				Object svc = c.newInstance();
//				Map<String, Class<? extends ExecutableService>> map = ((ExecS)svc).getClassMap();//TODO
				if(this.execClassMap!=null){
					for(String s : execClassMap.keySet()){
						outFN = this.outputDir + File.separator + s + fnExtension;
						System.out.println(" --> generating script from auto-gen-reg - " + outFN);

						ST targetExecST = this.generateScript(execClassMap.get(s).getName(), targetMap);
						this.writeFile(outFN, targetExecST);
					}
				}
//			}
//			catch(ClassNotFoundException | InstantiationException | IllegalAccessException e){
//				e.printStackTrace();
//			}
		}

		//build a generic header that can be used outside this class for generating other scripts
		ST headerST = this.stg.getInstanceOf("header");
		headerST.add("target", targetMap);
		headerST.add("applicationHome", this.applicationDir);
		this.writeFile(this.outputDir + File.separator + "_header", headerST);

		return 0;
	}

	protected final ST generateScript(String clazz, HashMap<String, Boolean> targetMap){
		ST ret = this.stg.getInstanceOf("generateExec");
		ret.add("target", targetMap);
		ret.add("applicationHome", this.applicationDir);
		ret.add("runName", this.configuration.get(PROP_RUN_SCRIPT_NAME));
		ret.add("class", clazz);
		return ret;
	}

	/**
	 * Loads and tests the configuration from configuration properties file.
	 * The default configuration file name is "de/vandermeer/execs/configuration.properties".
	 * The file name can be overwritten using the "--property-file" CLI option.
	 * The method will also test for some configuration keys to exist and fail if they are not defined.
	 * @return 0 on success with configuration loaded, -1 on error with errors printed on standard error
	 */
	protected final int initConfiguration(){
		String propFile = "de/vandermeer/execs/configuration.properties";
		if(ExecS_Cli.testOption(this.cli, CLIOPT_PROP_FILE)){
			propFile = this.cli.getOption(CLIOPT_PROP_FILE);
		}

		this.configuration = this.loadProperties(propFile);
		if(this.configuration==null){
			System.err.println(this.getName() + ": could not load configuration properties, exiting");
			return -1;
		}

		if(this.configuration.get(PROP_RUN_SCRIPT_NAME)==null){
			System.err.println(this.getName() + ": configuration does not contain key <" + PROP_RUN_SCRIPT_NAME + ">, exiting");
			return -1;
		}
		if(this.configuration.get(PROP_RUN_CLASS)==null){
			System.err.println(this.getName() + ": configuration does not contain key <" + PROP_RUN_CLASS + ">, exiting");
			return -1;
		}
		if(this.configuration.get(PROP_JAVA_CP)==null){
			System.err.println(this.getName() + ": configuration does not contain key <" + PROP_JAVA_CP + ">, exiting");
			return -1;
		}

		System.out.println(this.getName() + ": using configuration: ");
		System.out.println("  - run script name: " + this.configuration.get(PROP_RUN_SCRIPT_NAME));
		System.out.println("  - run class      : " + this.configuration.get(PROP_RUN_CLASS));
		System.out.println("  - java cp        : " + this.configuration.get(PROP_JAVA_CP));
		System.out.println("  - auto-gen reg   : " + this.configuration.get(PROP_EXECS_AUTOGEN_REGISTERED));

		for(Object key : this.configuration.keySet()){
			if(StringUtils.startsWith(key.toString(), PROP_JAVAPROP_START)){
				System.out.println("  - java property  : " + key + " = " + this.configuration.getProperty(key.toString()));
			}
		}
		System.out.println();

		return 0;
	}

	/**
	 * Sets target for generation and initializes an STG object from an stg template file.
	 * The default template file name is "de/vandermeer/execs/executable-script.stg".
	 * This default can be overwritten using the property "stg.file" in the configuration properties file.
	 * The default and the property file name can be overwritten using the "--stg-file" CLI option.
	 * The set target (CLI option "--target") must be supported by the template file, otherwise this method will fail.
	 * @return 0 on success with configuration loaded, -1 on error with errors printed on standard error
	 */
	protected final int initTargetAndStg(){
		if(ExecS_Cli.testOption(this.cli, StandardOptions.TARGET)){
			this.target = this.cli.getOption(StandardOptions.TARGET);
		}
		if(this.target==null){
			System.err.println(this.getName() + ": no target set");
			return -1;
		}

		String fileName = "de/vandermeer/execs/executable-script.stg";
		if(this.configuration!=null && this.configuration.get("stg.file")!=null){
			fileName = this.configuration.get("stg.file").toString();
		}
		if(ExecS_Cli.testOption(this.cli, CLIOPT_STGFILE)){
			fileName = this.cli.getOption(CLIOPT_STGFILE);
		}

		try{
			this.stg = new STGroupFile(fileName);
		}
		catch(Exception e){
			System.err.println(this.getName() + ": cannot load stg file <" + fileName + ">, general exception\n--> " + e + "");
			return -1;
		}
		String[] availableTargets = null;
		try{
			availableTargets = StringUtils.split(this.stg.getInstanceOf("supportedTargets").render(), " , ");
		}
		catch(Exception e){
			System.err.println(this.getName() + ": stg file <" + fileName + "> does not contain <supportedTargets> function");
			return -1;
		}
		if(availableTargets.length==0){
			System.err.println(this.getName() + ": stg file <" + fileName + "> does not have a list of targets in <supportedTargets> function");
			return -1;
		}
		if(!ArrayUtils.contains(availableTargets, this.target)){
			System.err.println(this.getName() + ": target " + this.target + " not supported in stg file <" + fileName + ">");
			return -1;
		}

		System.out.println(this.getName() + ": generating scripts for target: " + this.target);
		System.out.println();
		return 0;
	}

	/**
	 * Initializes the application directory for the generator.
	 * There is no default set and no configuration property can be used.
	 * The application directory has to be set using the CLI option "--application-directory".
	 * Otherwise this method will fail.
	 * @return 0 on success with configuration loaded, -1 on error with errors printed on standard error
	 */
	protected final int initApplicationDir(){
		if(ExecS_Cli.testOption(this.cli, CLIOPT_APPLICATION_HOME_DIR)){
			this.applicationDir = this.cli.getOption(CLIOPT_APPLICATION_HOME_DIR);
		}
		else{
			System.err.println(this.getName() + ": no application directory set");
			return -1;
		}

		return 0;
	}

	/**
	 * Loads a classmap from a property file.
	 * A classmap maps class names to script names.
	 * A class name must be an implementation of the executable service interface {@link ExecutableService}.
	 * No default map is used.
	 * This default can be overwritten using the property "execs.classmap" in the configuration properties file.
	 * The default and the property file name can be overwritten using the "--classmap-file" CLI option.
	 * @return 0 on success with configuration loaded, -1 on error with errors printed on standard error
	 */
	protected final int initClassmap(){
		String fileName = null;
		if(this.configuration!=null && this.configuration.get("execs.classmap")!=null){
			fileName = this.configuration.get("execs.classmap").toString();
		}
		if(ExecS_Cli.testOption(this.cli, CLIOPT_CLASSMAP_FILE)){
			fileName = this.cli.getOption(CLIOPT_CLASSMAP_FILE);
		}
		if(fileName==null){
			System.err.println(this.getName() + ": no classmap file name given");
			return -2;
		}

		this.classMap = this.loadProperties(fileName);
		if(this.classMap==null){
			System.err.println(this.getName() + ": could not load classmap, exiting");
			return -1;
		}

		System.out.println(this.getName() + ": generating scripts for:");
		for(Object key : this.classMap.keySet()){
			System.out.println("  - " + key + " --> " + this.classMap.getProperty(key.toString()));
		}
		System.out.println();

		return 0;
	}

	/**
	 * Tests and if necessary creates an output directory.
	 * The root path is the current directory as given by the system property "user.dir".
	 * The created output directory has the name of the specified target for the generator.
	 * The method fails if the output directory cannot be created or if it exists and is write protected.
	 * @return 0 on success with configuration loaded, -1 on error with errors printed on standard error
	 */
	protected final int initOutputDir(){
		String parent = System.getProperty("user.dir");
		String target = parent + File.separator + this.target;
		File targetDir = new File(target);
		File parentDir = targetDir.getParentFile();

		if(targetDir.exists()){
			//target dir exists, let's see if it is what we want it to be
			if(!targetDir.isDirectory()){
				System.err.println(this.getName() + ": target dir <" + target + "> exists but is not a directory, exiting");
				return -1;
			}
			if(!targetDir.canWrite()){
				System.err.println(this.getName() + ": target dir <" + target + "> exists but but cannot write into it, exiting");
				return -1;
			}
		}
		else{
			//target dir does not exist, let's see if we can create it the way we need
			if(!parentDir.isDirectory()){
				System.err.println(this.getName() + ": target dir parent <" + parent + "> exists but is not a directory, exiting");
				return -1;
			}
			if(!parentDir.canWrite()){
				System.err.println(this.getName() + ": target dir parent <" + parent + "> exists but but cannot write into it, exiting");
				return -1;
			}

			if(!targetDir.mkdir()){
				System.err.println(this.getName() + ": could not create target dir <" + target + ">, exiting");
				return -1;
			}
		}

		this.outputDir = target;
		return 0;
	}

	/**
	 * Loads properties from a file.
	 * @param filename property file loaded from class path or file system
	 * @return loaded properties, null if nothing could be loaded plus error messages to standard error if exceptions are caught
	 */
	protected final Properties loadProperties(String filename){
		Properties ret = new Properties();

		URL url = null;
		File f = new File(filename.toString());
		if(f.exists()){
			try{
				url = f.toURI().toURL();
			}
			catch(Exception ignore){}
		}
		else{
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			url = loader.getResource(filename);
			if(url==null){
				loader = Gen_RunScripts.class.getClassLoader();
				url = loader.getResource(filename);
			}
		}

		try{
			ret.load(url.openStream());
		}
		catch (IOException e){
			System.err.println(this.getName() + ": cannot load property file <" + filename + ">, IO exception\n--><" + e + ">");
		}
		catch (Exception e){
			System.err.println(this.getName() + ": cannot load property file <" + filename + ">, general exception\n--><" + e + ">");
		}
		return ret;
	}

	/**
	 * Writes an ST object to a file.
	 * @param fn file name
	 * @param st ST template
	 * @return 0 on success with configuration loaded, -1 on error with errors printed on standard error
	 */
	protected final int writeFile(String fn, ST st){
		try {
			FileWriter fs = new FileWriter(fn);
			BufferedWriter bw = new BufferedWriter(fs);
			bw.write(st.render());
			bw.close();
		}
		catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
		return 0;
	}

	@Override
	public ExecS_Cli getCli() {
		return this.cli;
	}

	@Override
	public void serviceHelpScreen() {
		System.out.println("Generates run scripts for executable services.");
		System.out.println();
		this.cli.usage(this.getName());
	}

	@Override
	public String getName() {
		return Gen_RunScripts.SERVICE_NAME;
	}

}

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

import de.vandermeer.execs.options.AO_AppHomeDirectory;
import de.vandermeer.execs.options.AO_ClassmapFile;
import de.vandermeer.execs.options.AO_PropertyFile;
import de.vandermeer.execs.options.AO_StgFile;
import de.vandermeer.execs.options.AO_Target;
import de.vandermeer.execs.options.ApplicationOption;
import de.vandermeer.execs.options.ExecS_CliParser;

/**
 * Application to generate run scripts for other applications, supporting windows, CygWin and bash.
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.3.0 build 150928 (28-Sep-15) for Java 1.8
 * @since      v0.0.6
 */
public class Gen_RunScripts implements ExecS_Application {

	/** Application name. */
	public final static String APP_NAME = "gen-run-scripts";

	/** Application display name. */
	public final static String APP_DISPLAY_NAME = "Generate Application Run Scripts";

	/** Application version, should be same as the version in the class JavaDoc. */
	public final static String APP_VERSION = "v0.3.0 build 150928 (28-Sep-15) for Java 1.8";

	/** A property key for the script name of the generic run script, without file extension. */
	public final static String PROP_RUN_SCRIPT_NAME = "run.script.name";

	/** A property key for the class name of the ExexS executor to be used, must be a fully qualified class name. */
	public final static String PROP_RUN_CLASS = "run.script.class";

	/** A property key for the JAVA classpath. */
	public final static String PROP_JAVA_CP = "java.classpath";

	/** The string all java properties for running the JVM must start with. */
	public final static String PROP_JAVAPROP_START = "java.property.";

	/** A property key to set auto-script generation for registered executable applications. */
	public final static String PROP_EXECS_CLASSMAP = "execs.classmap";

	/** A property key to set class map for executable applications. */
	public final static String PROP_EXECS_AUTOGEN_REGISTERED = "execs.autogenerate.registered";

	/** Local CLI options for CLI parsing. */
	protected ExecS_CliParser cli;

	/** The application option for the property file. */
	protected AO_PropertyFile optionPropFile;

	/** The application option for the target. */
	protected AO_Target optionTarget;

	/** The application option for the STG file. */
	protected AO_StgFile optionStgFile;

	/** The application option for the class map file. */
	protected AO_ClassmapFile optionClassMapFile;

	/** The application option for the application home directory. */
	protected AO_AppHomeDirectory optionAppHome;

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
	Map<String, Class<? extends ExecS_Application>> execClassMap;

	/**
	 * Returns a new generator with parameterized CLI object.
	 */
	public Gen_RunScripts() {
		this.cli = new ExecS_CliParser();

		this.optionPropFile = new AO_PropertyFile(false, "de/vandermeer/execs/configuration.properties", "File name of a property file with specific configuration options to generate run scripts.");
		this.cli.addOption(this.optionPropFile);

		this.optionTarget = new AO_Target(true, "The target defines if the script is generated for bat (Windows batch file), sh (UNIX bash script) or cyg (Cygwin bash script).");
		this.cli.addOption(this.optionTarget);

		this.optionStgFile = new AO_StgFile(false, "de/vandermeer/execs/executable-script.stg", "The STG (String Template Group) file must define a large set of templates for the generation of run scripts. Details are in the JavaDoc of the application implementation.");
		this.cli.addOption(this.optionStgFile);

		this.optionClassMapFile = new AO_ClassmapFile(false, PROP_EXECS_CLASSMAP, "The class map file contains mappings from a class name to a script name. This mapping is used to generate run scripts for applications that are not registered with an executor, or if automated generation (for registered applications) is not required or wanted.");
		this.cli.addOption(this.optionClassMapFile);

		this.optionAppHome = new AO_AppHomeDirectory(true, "The application home directory will be used as the absolute path in which the script is started in. All other paths are calculated from this absolute path.");
		this.cli.addOption(this.optionAppHome);
	}

	/**
	 * Hook for a calling ExecS instance to set its class map for the script generator
	 * @param execClassMap calling executor class map to create run scripts from
	 */
	public void setClassMap(Map<String, Class<? extends ExecS_Application>> execClassMap){
		this.execClassMap = execClassMap;
	}

	@Override
	public int executeApplication(String[] args) {
		// parse command line, exit with help screen if error
		int ret = ExecS_Application.super.executeApplication(args);
		if(ret!=0){
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
				this.writeFile(outFN, targetExecST);
			}
		}

		//build scripts for all registered applications using the specified run class
		if(this.configuration.get(PROP_EXECS_AUTOGEN_REGISTERED)!=null && BooleanUtils.toBoolean(this.configuration.get(PROP_EXECS_AUTOGEN_REGISTERED).toString())){
			if(this.execClassMap!=null){
				for(String s : execClassMap.keySet()){
					outFN = this.outputDir + File.separator + s + fnExtension;
					System.out.println(" --> generating script from auto-gen-reg - " + outFN);
					ST targetExecST = this.generateScript(execClassMap.get(s).getName(), targetMap);
					this.writeFile(outFN, targetExecST);
				}
			}
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
		String propFile = this.optionPropFile.getValue();

		this.configuration = this.loadProperties(propFile);
		if(this.configuration==null){
			System.err.println(this.getAppName() + ": could not load configuration properties from file <" + propFile + ">, exiting");
			return -1;
		}

		if(this.configuration.get(PROP_RUN_SCRIPT_NAME)==null){
			System.err.println(this.getAppName() + ": configuration does not contain key <" + PROP_RUN_SCRIPT_NAME + ">, exiting");
			return -1;
		}
		if(this.configuration.get(PROP_RUN_CLASS)==null){
			System.err.println(this.getAppName() + ": configuration does not contain key <" + PROP_RUN_CLASS + ">, exiting");
			return -1;
		}
		if(this.configuration.get(PROP_JAVA_CP)==null){
			System.err.println(this.getAppName() + ": configuration does not contain key <" + PROP_JAVA_CP + ">, exiting");
			return -1;
		}

		System.out.println(this.getAppName() + ": using configuration: ");
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
		this.target = this.optionTarget.getValue();
		if(this.target==null){
			System.err.println(this.getAppName() + ": no target set");
			return -1;
		}

		String fileName = this.optionStgFile.getValue();
		try{
			this.stg = new STGroupFile(fileName);
		}
		catch(Exception e){
			System.err.println(this.getAppName() + ": cannot load stg file <" + fileName + ">, general exception\n--> " + e);
			return -1;
		}
		String[] availableTargets = null;
		try{
			availableTargets = StringUtils.split(this.stg.getInstanceOf("supportedTargets").render(), " , ");
		}
		catch(Exception e){
			System.err.println(this.getAppName() + ": stg file <" + fileName + "> does not contain <supportedTargets> function");
			return -1;
		}
		if(availableTargets.length==0){
			System.err.println(this.getAppName() + ": stg file <" + fileName + "> does not have a list of targets in <supportedTargets> function");
			return -1;
		}
		if(!ArrayUtils.contains(availableTargets, this.target)){
			System.err.println(this.getAppName() + ": target " + this.target + " not supported in stg file <" + fileName + ">");
			return -1;
		}

		System.out.println(this.getAppName() + ": generating scripts for target: " + this.target);
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
		this.applicationDir = this.optionAppHome.getValue();
		if(this.applicationDir==null){
			System.err.println(this.getAppName() + ": no application directory set");
			return -1;
		}

		return 0;
	}

	/**
	 * Loads a classmap from a property file.
	 * A classmap maps class names to script names.
	 * A class name must be an implementation of the executable application interface {@link ExecS_Application}.
	 * No default map is used.
	 * This default can be overwritten using the property "execs.classmap" in the configuration properties file.
	 * The default and the property file name can be overwritten using the "--classmap-file" CLI option.
	 * @return 0 on success with configuration loaded, -1 on error with errors printed on standard error
	 */
	protected final int initClassmap(){
		this.optionClassMapFile.setPropertyValue(this.configuration);
		String fileName = this.optionClassMapFile.getValue();

		if(fileName==null){
			System.err.println(this.getAppName() + ": no classmap file name given");
			return -2;
		}

		this.classMap = this.loadProperties(fileName);
		if(this.classMap==null){
			System.err.println(this.getAppName() + ": could not load classmap, exiting");
			return -1;
		}

		System.out.println(this.getAppName() + ": generating scripts for:");
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
				System.err.println(this.getAppName() + ": target dir <" + target + "> exists but is not a directory, exiting");
				return -1;
			}
			if(!targetDir.canWrite()){
				System.err.println(this.getAppName() + ": target dir <" + target + "> exists but but cannot write into it, exiting");
				return -1;
			}
		}
		else{
			//target dir does not exist, let's see if we can create it the way we need
			if(!parentDir.isDirectory()){
				System.err.println(this.getAppName() + ": target dir parent <" + parent + "> exists but is not a directory, exiting");
				return -1;
			}
			if(!parentDir.canWrite()){
				System.err.println(this.getAppName() + ": target dir parent <" + parent + "> exists but but cannot write into it, exiting");
				return -1;
			}

			if(!targetDir.mkdir()){
				System.err.println(this.getAppName() + ": could not create target dir <" + target + ">, exiting");
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
			System.err.println(this.getAppName() + ": cannot load property file <" + filename + ">, IO exception\n--><" + e + ">");
		}
		catch (Exception e){
			System.err.println(this.getAppName() + ": cannot load property file <" + filename + ">, general exception\n--><" + e + ">");
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
	public ExecS_CliParser getCli() {
		return this.cli;
	}

	@Override
	public String getAppDescription() {
		return "Generates run scripts for executable applications.";
	}

	@Override
	public String getAppName() {
		return APP_NAME;
	}

	@Override
	public String getAppDisplayName(){
		return APP_DISPLAY_NAME;
	}

	@Override
	public ApplicationOption<?>[] getAppOptions() {
		return new ApplicationOption[]{this.optionAppHome, this.optionClassMapFile, this.optionPropFile, this.optionStgFile, this.optionTarget};
	}

	@Override
	public String getAppVersion() {
		return APP_VERSION;
	}

	@Override
	public void appHelpScreen(){
		ExecS_Application.super.appHelpScreen();

		System.out.println("Property file keys:");
		System.out.println(" - " + PROP_RUN_CLASS + " - the class name for executing applications");
		System.out.println(" - " + PROP_RUN_SCRIPT_NAME + " - the script name for running the main application executor");
		System.out.println(" - " + PROP_JAVA_CP + " - JAVA classpath, comma separates list, {APPLICATION_HOME} will be added to all entries");
		System.out.println(" - " + PROP_JAVAPROP_START + " - start of a particular JAVA runtime property");
		System.out.println("      + any key with the start should have the form of key:value and will be translated to -Dkey:value");
		System.out.println("      + for example: 'java.property.encoding = file.encoding:UTF-8' will be translated to -Dfile.encoding:UTF-8");
		System.out.println("      + {APPLICATION_HOME} will be added to all entries");
		System.out.println(" - " + PROP_EXECS_CLASSMAP + " - class map file with mappings from class to executable name");
		System.out.println(" - " + PROP_EXECS_AUTOGEN_REGISTERED + " - flag to auto generate all registered applications");

		System.out.println();
	}

}

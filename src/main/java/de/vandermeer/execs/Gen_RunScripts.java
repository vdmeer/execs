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
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

import de.vandermeer.execs.options.Option_TypedP_Boolean;
import de.vandermeer.execs.options.Option_TypedP_String;
import de.vandermeer.execs.options.simple.AO_HelpSimple;
import de.vandermeer.execs.options.simple.AO_Version;
import de.vandermeer.execs.options.typed.AO_ApplicationDir;
import de.vandermeer.execs.options.typed.AO_ClassmapFilename_CP;
import de.vandermeer.execs.options.typed.AO_PropertyFilename;
import de.vandermeer.execs.options.typed.AO_StgFilename;
import de.vandermeer.execs.options.typed.AO_Target;
import de.vandermeer.skb.interfaces.antlr.IsSTGroup;
import de.vandermeer.skb.interfaces.application.ApoCliParser;
import de.vandermeer.skb.interfaces.application.IsApplication;
import de.vandermeer.skb.interfaces.console.MessageConsole;
import de.vandermeer.skb.interfaces.messages.errors.IsError;
import de.vandermeer.skb.interfaces.messages.errors.Templates_InputDirectory;
import de.vandermeer.skb.interfaces.messages.errors.Templates_InputFile;
import de.vandermeer.skb.interfaces.messages.errors.Templates_OutputDirectory;
import de.vandermeer.skb.interfaces.messages.errors.Templates_Target;

/**
 * Application to generate run scripts for other applications, supporting windows, CygWin, and bash.
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.4.0 build 170413 (13-Apr-17) for Java 1.8
 * @since      v0.0.6
 */
public class Gen_RunScripts extends AbstractAppliction {

	/** Application name. */
	public final static String APP_NAME = "gen-run-scripts";

	/** Application display name. */
	public final static String APP_DISPLAY_NAME = "Generate Application Run Scripts";

	/** Application version, should be same as the version in the class JavaDoc. */
	public final static String APP_VERSION = "v0.4.0 build 170413 (13-Apr-17) for Java 1.8";

	/** The application option for the property file. */
	protected final AO_PropertyFilename optionPropFile;

	/** The application option for the target. */
	protected final transient AO_Target optionTarget;

	/** The application option for the STG file. */
	protected final transient AO_StgFilename optionStgFile;

	/** The application option for the class map file. */
	protected final transient AO_ClassmapFilename_CP optionClassMapFile;

	/** The application option for the application home directory. */
	protected final transient AO_ApplicationDir optionAppHome;

	/** Properties with a class map. */
	protected Properties classMap;

	/** String template group loaded from STG file. */
	protected STGroup stg;

	/** The target set for generation. */
	protected String target;

	/** The application home directory, ideally something like "abc/xyz" as root for generating scripts. */
	protected String applicationDir;

	/** The name of the output directory, null means cannot output or not tested yet. */
	protected String outputDir;

	/** Local class map, must be set by calling ExecS instance. */
	protected Map<String, Class<? extends IsApplication>> execClassMap;

	/** Property option for RUN_CLASS. */
	protected final transient Option_TypedP_String propRunClass;

	/** Property option for JAVA_CP. */
	protected final transient Option_TypedP_String propJavaCP;

	/** Property option for RUN_SCRIPT_NAME. */
	protected final transient Option_TypedP_String propRunScriptname;

	/** Property option for JVM_RUNTIME_OPTIONS. */
	protected final transient Option_TypedP_String propJvmOptions;

	/** Property option for AUTOGEN_REGISTERED. */
	protected final transient Option_TypedP_Boolean propAutogen;

	/**
	 * Returns a new generator with parameterized CLI object.
	 */
	public Gen_RunScripts() {
		super(APP_NAME, ApoCliParser.defaultParser(), new AO_HelpSimple('h', null), null, new AO_Version('v', null) );

		this.optionPropFile = new AO_PropertyFilename(
				null, false,
				"a propery file", " sets the filename for a configuration property file",
				"File name of a property file with specific configuration options to generate run scripts."
		);
		this.optionPropFile.setDefaultValue("de/vandermeer/execs/configuration.properties");
		this.addOption(this.optionPropFile);

		this.optionTarget = new AO_Target(
				null, true,
				"the target, valid targets are: bat, s, cyg", "specifies the target operating system for run scripts",
				"The target defines if the script is generated for bat (Windows batch file), sh (UNIX bash script) or cyg (Cygwin bash script)."
		);
		this.addOption(this.optionTarget);

		this.optionStgFile = new AO_StgFilename(
				null, false,
				"a plain text file, in STG syntax, with all required methods", "specifies a string template (stg) file for generting run scripts",
				"The STG (String Template Group) file must define a large set of templates for the generation of run scripts. Details are in the JavaDoc of the application implementation."
		);
		this.optionStgFile.setDefaultValue("de/vandermeer/execs/executable-script.stg");
		this.addOption(this.optionStgFile);

		this.optionClassMapFile = new AO_ClassmapFilename_CP(null);
		this.addOption(this.optionClassMapFile);

		this.optionAppHome = new AO_ApplicationDir(
				null, true,
				"a directory name, will be created if not exists", "application home directory specific to a given target format",
				"The application home directory will be used as the absolute path in which the script is started in. All other paths are calculated from this absolute path."
		);
		this.addOption(this.optionAppHome);

		this.propRunClass = GenAop.RUN_CLASS();
		this.addOption(this.propRunClass);

		this.propJavaCP = GenAop.JAVA_CP();
		this.addOption(this.propJavaCP);

		this.propRunScriptname = GenAop.RUN_SCRIPT_NAME();
		this.addOption(this.propRunScriptname);

		this.propJvmOptions = GenAop.JVM_RUNTIME_OPTIONS();
		this.addOption(this.propJvmOptions);

		this.propAutogen = GenAop.AUTOGEN_REGISTERED();
		this.propAutogen.setDefaultValue(false);
		this.addOption(this.propAutogen);

		MessageConsole.activateAll();
		MessageConsole.setApplicationName(this.appName);
	}

	/**
	 * Generates a script.
	 * @param clazz the class for the script
	 * @param targetMap map for targets
	 * @return the final template for the script
	 */
	protected final ST generateScript(String clazz, HashMap<String, Boolean> targetMap){
		ST ret = this.stg.getInstanceOf("generateExec");
		ret.add("target", targetMap);
		ret.add("applicationHome", this.applicationDir);
		ret.add("runName", this.propRunScriptname.getValue());
		ret.add("class", clazz);
		return ret;
	}

	@Override
	public String getDescription() {
		return "Generates run scripts for executable applications.";
	}

	@Override
	public String getDisplayName(){
		return APP_DISPLAY_NAME;
	}

	@Override
	public String getVersion() {
		return APP_VERSION;
	}

	/**
	 * Initializes the application directory for the generator.
	 * There is no default set and no configuration property can be used.
	 * The application directory has to be set using the CLI option `--application-directory`.
	 * Otherwise this method will fail.
	 */
	protected final void initApplicationDir(){
		if(this.errNo<0){
			return;
		}

		this.applicationDir = this.optionAppHome.getValue();
		if(this.applicationDir==null){
			this.msgMgr.add(Templates_InputDirectory.DN_NULL.getError("application"));
			this.errNo = Templates_InputDirectory.DN_NULL.getCode();
		}
	}

	/**
	 * Loads a classmap from a property file.
	 * A classmap maps class names to script names.
	 * A class name must be an implementation of the executable application interface {@link IsApplication}.
	 * No default map is used.
	 * This default can be overwritten using the property `execs.classmap` in the configuration properties file.
	 * The default and the property file name can be overwritten using the `--classmap-file` CLI option.
	 */
	protected final void initClassmap(){
		if(this.errNo<0){
			return;
		}

		String fileName = this.optionClassMapFile.getValue();
		if(StringUtils.isBlank(fileName)){
//			this.errorSet.addError(Templates_InputFile.FN_BLANK.getError(this.getAppName(), "property"));
//			this.errNo = Templates_InputFile.FN_BLANK.getCode();
			return;
		}

		this.classMap = this.loadProperties(fileName);
		if(classMap==null){
			return;
		}

		//TODO
		System.out.println(this.getName() + ": generating scripts for:");
		for(Object key : this.classMap.keySet()){
			System.out.println("  - " + key + " --> " + this.classMap.getProperty(key.toString()));
		}
		System.out.println();
	}

	/**
	 * Loads and tests the configuration from configuration properties file.
	 * The default configuration file name is `de/vandermeer/execs/configuration.properties`.
	 * The file name can be overwritten using the `--property-file` CLI option.
	 * The method will also test for some configuration keys to exist and fail if they are not defined.
	 */
	protected final void initConfiguration(){
		if(this.errNo<0){
			return;
		}

		String propFile = this.optionPropFile.getValue();
		Properties configuration = this.loadProperties(propFile);
		if(configuration==null){
			return;
		}
		else{
			this.getPropertyParser().parse(configuration);
		}

//		if(this.configuration==null){
//			this.getErrorSet().addError(Templates_PropertiesGeneral.LOADING_FROM_FILE.getError(this.getAppName(), propFile));
//			this.errNo = Templates_PropertiesGeneral.LOADING_FROM_FILE.getCode();
//			return;
//		}
//		if(this.configuration.get(PROP_RUN_SCRIPT_NAME)==null){
//			this.getErrorSet().addError(Templates_PropertiesOptions.MISSING_KEY.getError(this.getAppName(), PROP_RUN_SCRIPT_NAME, propFile));
//			this.errNo = Templates_PropertiesOptions.MISSING_KEY.getCode();
//			return;
//		}
//		if(this.configuration.get(PROP_RUN_CLASS)==null){
//			this.getErrorSet().addError(Templates_PropertiesOptions.MISSING_KEY.getError(this.getAppName(), PROP_RUN_CLASS, propFile));
//			this.errNo = Templates_PropertiesOptions.MISSING_KEY.getCode();
//			return;
//		}
//		if(this.configuration.get(PROP_JAVA_CP)==null){
//			this.getErrorSet().addError(Templates_PropertiesOptions.MISSING_KEY.getError(this.getAppName(), PROP_JAVA_CP, propFile));
//			this.errNo = Templates_PropertiesOptions.MISSING_KEY.getCode();
//			return;
//		}

//		System.out.println(this.getAppName() + ": using configuration: ");
//		System.out.println("  - run script name: " + this.configuration.get(PROP_RUN_SCRIPT_NAME));
//		System.out.println("  - run class      : " + this.configuration.get(PROP_RUN_CLASS));
//		System.out.println("  - java cp        : " + this.configuration.get(PROP_JAVA_CP));
//		System.out.println("  - auto-gen reg   : " + this.configuration.get(PROP_EXECS_AUTOGEN_REGISTERED));
//
//		for(Object key : this.configuration.keySet()){
//			if(StringUtils.startsWith(key.toString(), PROP_JAVAPROP_START)){
//				System.out.println("  - java property  : " + key + " = " + this.configuration.getProperty(key.toString()));
//			}
//		}
//		System.out.println();

	}

	/**
	 * Tests and if necessary creates an output directory.
	 * The root path is the current directory as given by the system property `user.dir`.
	 * The created output directory has the name of the specified target for the generator.
	 * The method fails if the output directory cannot be created or if it exists and is write protected.
	 */
	protected final void initOutputDir(){
		if(this.errNo<0){
			return;
		}

		String parent = System.getProperty("user.dir");
		String target = parent + File.separator + this.target;
		File targetDir = new File(target);
		File parentDir = targetDir.getParentFile();

		if(targetDir.exists()){
			if(!targetDir.isDirectory()){
				this.msgMgr.add(Templates_OutputDirectory.DIR_NOTDIR.getError("target", target));
				this.errNo = Templates_OutputDirectory.DIR_NOTDIR.getCode();
				return;
			}
			if(!targetDir.canWrite()){
				this.msgMgr.add(Templates_OutputDirectory.DIR_CANT_WRITE.getError("target", target));
				this.errNo = Templates_OutputDirectory.DIR_CANT_WRITE.getCode();
				return;
			}
		}
		else{
			//target dir does not exist, let's see if we can create it the way we need
			if(!parentDir.isDirectory()){
				this.msgMgr.add(Templates_OutputDirectory.PARENT_NOTDIR.getError("target"));
				this.errNo = Templates_OutputDirectory.PARENT_NOTDIR.getCode();
				return;
			}
			if(!parentDir.canWrite()){
				this.msgMgr.add(Templates_OutputDirectory.PARENT_CANT_WRITE.getError("target", parent));
				this.errNo = Templates_OutputDirectory.PARENT_CANT_WRITE.getCode();
				return;
			}

			if(!targetDir.mkdir()){
				this.msgMgr.add(Templates_OutputDirectory.DIR_CANT_CREATE.getError("target", target));
				this.errNo = Templates_OutputDirectory.DIR_CANT_CREATE.getCode();
				return;
			}
		}
		this.outputDir = target;
	}

	/**
	 * Sets target for generation and initializes an STG object from an stg template file.
	 * The default template file name is `de/vandermeer/execs/executable-script.stg`.
	 * This default can be overwritten using the property `stg.file` in the configuration properties file.
	 * The default and the property file name can be overwritten using the `--stg-file` CLI option.
	 * The set target (CLI option `--target`) must be supported by the template file, otherwise this method will fail.
	 */
	protected final void initTargetAndStg(){
		if(this.errNo<0){
			return;
		}

		this.target = this.optionTarget.getValue();

		Map<String, String[]> expectedChunks = new HashMap<>();
		expectedChunks.put("supportedTargets", new String[0]);
		expectedChunks.put("generateRun", new String[]{"target", "applicationHome", "javaProperties", "classPath", "class"});
		expectedChunks.put("generateExec", new String[]{"target", "applicationHome", "runName", "class"});
		expectedChunks.put("header", new String[]{"target", "applicationHome"});
		expectedChunks.put("classpath", new String[]{"target", "classpath"});
		expectedChunks.put("fnExtension", new String[]{"target"});
		expectedChunks.put("pathSeparator", new String[]{"target"});

		IsSTGroup istg = IsSTGroup.fromFile(this.optionStgFile.getValue(), expectedChunks);
		Set<IsError> err = istg.validate();
		if(err.size()>0){
			this.msgMgr.addAll(err);
			return;
		}
		this.stg = istg.getSTGroup();

		String[] availableTargets = null;
		availableTargets = StringUtils.split(this.stg.getInstanceOf("supportedTargets").render(), " , ");
		if(availableTargets.length==0){
			this.msgMgr.add(Templates_Target.NO_TARGETS.getError("expected targets in STG, template <supportedTargets>"));
			this.errNo = Templates_Target.NO_TARGETS.getCode();
			return;
		}
		if(!ArrayUtils.contains(availableTargets, this.target)){
			this.msgMgr.add(Templates_Target.NOT_SUPPORTED.getError(target, this.stg.getInstanceOf("supportedTargets").render()));
			this.errNo = Templates_Target.NOT_SUPPORTED.getCode();
			return;
		}

		//TODO
		System.out.println(this.getName() + ": generating scripts for target: " + this.target);//TODO
		System.out.println();//TODO
	}

	/**
	 * Loads properties from a file.
	 * @param filename property file loaded from class path or file system
	 * @return loaded properties, null if nothing could be loaded plus error messages to standard error if exceptions are caught
	 */
	protected final Properties loadProperties(String filename){
		URL url = null;
		File file = new File(filename);
		if(file.exists()){
			try{
				url = file.toURI().toURL();
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

		if(url==null){
			this.msgMgr.add(Templates_InputFile.URL_NULL.getError("property", filename));
			this.errNo = Templates_InputFile.URL_NULL.getCode();
		}
		else{
			try{
				Properties ret = new Properties();
				ret.load(url.openStream());
				return ret;
			}
			catch (IOException iox){
				this.msgMgr.add(Templates_InputFile.IO_EXCEPTION_READING.getError("property", filename, iox.getMessage()));
				this.errNo = Templates_InputFile.IO_EXCEPTION_READING.getCode();
			}
//			catch (Exception ex){
//				this.errorSet.addError(Templates_PropertiesGeneral.LOADING_FROM_FILE.getError(this.getAppName(), filename, ex));
//			}
		}
		return null;
	}

	@Override
	public void runApplication() {
		// initialize if possible (errNo==0)
		this.initConfiguration();
		this.initApplicationDir();
		this.initTargetAndStg();
		this.initClassmap();
		this.initOutputDir();

		if(this.errNo==0){
			HashMap<String, Boolean> targetMap = new HashMap<>();
			targetMap.put(target, true);
			String fnExtension = this.stg.getInstanceOf("fnExtension").add("target", targetMap).render();
			String targetFileSep = this.stg.getInstanceOf("pathSeparator").add("target", targetMap).render();

			//build main run script
			String outFN = this.outputDir + File.separator + this.propRunScriptname.getValue() + fnExtension;
			System.out.println(" --> generating main run script - " + outFN);
			ST targetRunST = this.stg.getInstanceOf("generateRun");
			targetRunST.add("target", targetMap);
			targetRunST.add("class", this.propRunClass.getValue());
			targetRunST.add("applicationHome", this.applicationDir);

			if(this.propJavaCP.getValue()!=null){
				String[] cp = StringUtils.split(this.propJavaCP.getValue(), " , ");
				ST classpath = this.stg.getInstanceOf("classpath");
				classpath.add("target", targetMap);
				for(String s : cp){
					classpath.add("classpath", StringUtils.replace(s, "/", targetFileSep));
				}
				targetRunST.add("classPath", classpath.render());
			}

			if(this.propJvmOptions.getValue()!=null){
				for(String value : StringUtils.split(this.propJvmOptions.getValue(), " , ")){
					String[] kv = StringUtils.split(value, ":");
					HashMap<String, String> javaProperties = new HashMap<>();
					javaProperties.put("key", kv[0]);
					ST execHomeVar = this.stg.getInstanceOf("execHomeVar");
					javaProperties.put("val", StringUtils.replace(kv[1], "{APPLICATION_HOME}", execHomeVar.add("target", targetMap).render()));
					targetRunST.add("javaProperties", javaProperties);
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
			if(this.propAutogen.getValue()){
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
		}
	}

	/**
	 * Hook for a calling ExecS instance to set its class map for the script generator
	 * @param execClassMap calling executor class map to create run scripts from
	 */
	public void setClassMap(Map<String, Class<? extends IsApplication>> execClassMap){
		this.execClassMap = execClassMap;
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
}

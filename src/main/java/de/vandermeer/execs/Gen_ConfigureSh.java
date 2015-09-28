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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import de.vandermeer.execs.options.AO_LibDir;
import de.vandermeer.execs.options.AO_PropertyFile;
import de.vandermeer.execs.options.AO_TemplateDir;
import de.vandermeer.execs.options.ApplicationOption;
import de.vandermeer.execs.options.ExecS_CliParser;

/**
 * Application to generate a configuration shell script.
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.3.0 build 150928 (28-Sep-15) for Java 1.8
 * @since      v0.0.6
 */
public class Gen_ConfigureSh implements ExecS_Application {

	/** Application name. */
	public final static String APP_NAME = "gen-configure";

	/** Application display name. */
	public final static String APP_DISPLAY_NAME = "Generate Configure.SH";

	/** Application version, should be same as the version in the class JavaDoc. */
	public final static String APP_VERSION = "v0.3.0 build 150928 (28-Sep-15) for Java 1.8";

	/** Local CLI options for CLI parsing. */
	protected ExecS_CliParser cli;

	/** The application option for the library directory. */
	protected AO_LibDir optionLibDir;

	/** The application option for the template directory. */
	protected AO_TemplateDir optionTemplateDir;

	/** The application option for the property file. */
	protected AO_PropertyFile optionPropFile;

	/**
	 * Returns a new configure shell script generator.
	 */
	public Gen_ConfigureSh(){
		this.cli = new ExecS_CliParser();

		this.optionLibDir = new AO_LibDir(false, "The library home needs to point to a directory with all jar files required to run an ExecS.");
		this.cli.addOption(this.optionLibDir);

		this.optionTemplateDir = new AO_TemplateDir(false, "The template directory needs to point to a directory with templates for scripts.");
		this.cli.addOption(this.optionTemplateDir);

		this.optionPropFile = new AO_PropertyFile(true, null, "A file name that is added to the run script for reading class map properties from.");
		this.cli.addOption(this.optionPropFile);
	}

	@Override
	public int executeApplication(String[] args) {
		// parse command line, exit with help screen if error
		int ret = ExecS_Application.super.executeApplication(args);
		if(ret!=0){
			return ret;
		}

		String fn = "/de/vandermeer/execs/bin/configure.sh";

		String propFile = this.optionPropFile.getValue();
		Properties configuration = this.loadProperties(propFile);
		if(configuration==null){
			System.err.println(this.getAppName() + ": could not load configuration properties from file <" + propFile + ">, exiting");
			return -1;
		}
		if(configuration.get(Gen_RunScripts.PROP_RUN_CLASS)==null){
			System.err.println(this.getAppName() + ": configuration does not contain key <" + Gen_RunScripts.PROP_RUN_CLASS + ">, exiting");
			return -1;
		}
		String execClass = configuration.get(Gen_RunScripts.PROP_RUN_CLASS).toString();

		try {
			InputStream in = getClass().getResourceAsStream(fn);
			BufferedReader input = new BufferedReader(new InputStreamReader(in));
			String line;
			while((line=input.readLine())!=null){
				if(StringUtils.startsWith(line, "LIB_HOME=") && this.optionLibDir.getValue()!=null){
					System.out.println("LIB_HOME=" + this.optionLibDir.getValue());
				}
				else if(StringUtils.startsWith(line, "PROP_FILE=")){
					System.out.println("PROP_FILE=" + propFile);
				}
				else if(StringUtils.startsWith(line, "EXECS_CLASS=")){
					System.out.println("EXECS_CLASS=" + execClass);
				}
				else if(StringUtils.startsWith(line, "BIN_TEMPLATES=") && this.optionTemplateDir.getValue()!=null){
					System.out.println("BIN_TEMPLATES=" + this.optionTemplateDir.getValue());
				}
				else{
					System.out.println(line);
				}
			}
		}
		catch(NullPointerException ne){
			System.err.println(this.getAppName() + ": exception while reading shell script from resource <" + fn + ">: " + ne.getMessage());
			return -1;
		}
		catch (IOException e) {
			System.err.println(this.getAppName() + ": IO exception while reading shell script: " + e.getMessage());
			return -1;
		}
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

	@Override
	public String getAppName() {
		return APP_NAME;
	}

	@Override
	public String getAppDisplayName(){
		return APP_DISPLAY_NAME;
	}

	@Override
	public ExecS_CliParser getCli() {
		return this.cli;
	}

	@Override
	public String getAppDescription() {
		return "Generates configuration shell script for an application.";
	}

	@Override
	public ApplicationOption<?>[] getAppOptions() {
		return new ApplicationOption[]{this.optionLibDir, this.optionTemplateDir, this.optionPropFile};
	}

	@Override
	public String getAppVersion() {
		return APP_VERSION;
	}

}

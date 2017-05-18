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

import de.vandermeer.execs.options.Option_TypedP_String;
import de.vandermeer.execs.options.simple.AO_HelpSimple;
import de.vandermeer.execs.options.simple.AO_Version;
import de.vandermeer.execs.options.typed.AO_LibDir;
import de.vandermeer.execs.options.typed.AO_PropertyFilename;
import de.vandermeer.execs.options.typed.AO_TemplateDir;
import de.vandermeer.skb.interfaces.MessageType;
import de.vandermeer.skb.interfaces.application.ApoCliParser;
import de.vandermeer.skb.interfaces.console.MessageConsole;
import de.vandermeer.skb.interfaces.messages.errors.Templates_InputFile;

/**
 * Application to generate a configuration shell script.
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.4.0 build 170413 (13-Apr-17) for Java 1.8
 * @since      v0.0.6
 */
public class Gen_ConfigureSh extends AbstractAppliction {

	/** Application name. */
	public final static String APP_NAME = "gen-configure";

	/** Application display name. */
	public final static String APP_DISPLAY_NAME = "Generate Configure.SH";

	/** Application version, should be same as the version in the class JavaDoc. */
	public final static String APP_VERSION = "v0.4.0 build 170413 (13-Apr-17) for Java 1.8";

	/** The application option for the library directory. */
	protected final transient AO_LibDir optionLibDir;

	/** The application option for the template directory. */
	protected final transient AO_TemplateDir optionTemplateDir;

	/** The application option for the property file. */
	protected final transient AO_PropertyFilename optionPropFile;

	/** Property option for RUN_CLASS. */
	protected final transient Option_TypedP_String propRunClass;

	/**
	 * Returns a new configure shell script generator.
	 */
	public Gen_ConfigureSh(){
		super(APP_NAME, ApoCliParser.defaultParser(), new AO_HelpSimple('h', null), null, new AO_Version('v', null));

		this.optionLibDir = new AO_LibDir(
				null, false, "directory with jar files, must exist",
				"specifies a directory with required jar files",
				"The library home needs to point to a directory with all jar files required to run an ExecS."
		);
		this.addOption(optionLibDir);

		this.optionTemplateDir = new AO_TemplateDir(
				null, false,
				"template directory, must exist", "specifies a directory with templates for an application",
				"The template directory needs to point to a directory with templates for scripts."
		);
		this.addOption(this.optionTemplateDir);

		this.optionPropFile = new AO_PropertyFilename(
				null, true,
				"filename of an existing property file", "filename for a property file with configuration information",
				"A file name that is added to the run script for reading class map properties from."
		);
		this.addOption(this.optionPropFile);

		this.propRunClass = GenAop.RUN_CLASS();
		this.addOption(this.propRunClass);

		MessageConsole.activateAll();
		MessageConsole.setApplicationName(this.appName);
	}

	@Override
	public String getDescription() {
		return "Generates configuration shell script for an application.";
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
		}
		else{
			try{
				Properties ret = new Properties();
				ret.load(url.openStream());
				return ret;
			}
			catch (IOException iox){
				this.msgMgr.add(Templates_InputFile.IO_EXCEPTION_READING.getError("property", filename, iox.getMessage()));
			}
//			catch (Exception ex){
//				this.errorSet.addError(Templates_PropertiesGeneral.LOADING_FROM_FILE.getError(this.getAppName(), filename, ex));
//			}
		}
		return null;
	}

	@Override
	public void runApplication() {
		String propFile = this.optionPropFile.getValue();
		Properties configuration = this.loadProperties(propFile);
		if(configuration==null){
			return;
		}
		else{
			this.getPropertyParser().parse(configuration);
		}

		if(this.getErrNo()==0){
			MessageConsole.setApplicationName(null);
			MessageConsole.setInfoPrefix(null);
			String filename = "/de/vandermeer/execs/bin/configure.sh";
			try {
				InputStream inStream = getClass().getResourceAsStream(filename);
				BufferedReader input = new BufferedReader(new InputStreamReader(inStream));
				String line;
				while((line=input.readLine())!=null){
					if(StringUtils.startsWith(line, "LIB_HOME=") && this.optionLibDir.getValue()!=null){
						this.msgMgr.add(MessageType.INFO, "LIB_HOME={}", this.optionLibDir.getValue());
					}
					else if(StringUtils.startsWith(line, "PROP_FILE=")){
						this.msgMgr.add(MessageType.INFO, "PROP_FILE={}", propFile);
					}
					else if(StringUtils.startsWith(line, "EXECS_CLASS=")){
						this.msgMgr.add(MessageType.INFO, "EXECS_CLASS={}", this.propRunClass.getValue());
					}
					else if(StringUtils.startsWith(line, "BIN_TEMPLATES=") && this.optionTemplateDir.getValue()!=null){
						this.msgMgr.add(MessageType.INFO, "BIN_TEMPLATES={}", this.optionTemplateDir.getValue());
					}
					else{
						System.out.println(line);
					}
				}
			}
			catch(NullPointerException ne){
//				this.errorSet.addError("{}: exception while reading shell script from resource <{}>: {}", this.getAppName(), filename, ne);
//				this.errNo = -32;
				//TODO
			}
			catch (IOException e) {
//				this.errorSet.addError("{}: IO exception while reading shell script: {}", this.getAppName(), e.getMessage());
//				this.errNo = -33;
				//TODO
			}
		}
	}

}

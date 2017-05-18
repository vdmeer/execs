/* Copyright 2017 Sven van der Meer <vdmeer.sven@mykolab.com>
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import de.vandermeer.execs.options.simple.AO_HelpSimple;
import de.vandermeer.execs.options.simple.AO_Version;
import de.vandermeer.skb.interfaces.application.ApoCliParser;
import de.vandermeer.skb.interfaces.application.IsApplication;
import de.vandermeer.skb.interfaces.console.MessageConsole;
import de.vandermeer.skb.interfaces.messages.errors.Templates_AppStart;
import de.vandermeer.skb.interfaces.messages.errors.Templates_OutputFile;

/**
 * Application to generate starts scripts when running ExecS from an executable JAR.
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.4.0 build 170413 (13-Apr-17) for Java 1.8
 * @since      v0.4.0
 */
public class Gen_ExecJarScripts extends AbstractAppliction {

	/** Application name. */
	public final static String APP_NAME = "gen-exec-jar-scripts";

	/** Application display name. */
	public final static String APP_DISPLAY_NAME = "Generate JAR Scripts for Executable JARs";

	/** Application version, should be same as the version in the class JavaDoc. */
	public final static String APP_VERSION = "v0.4.0 build 170413 (13-Apr-17) for Java 1.8";

	/** Local class map, must be set by calling ExecS instance. */
	protected transient Map<String, Class<? extends IsApplication>> execClassMap;

	/**
	 * Creates a new application.
	 */
	public Gen_ExecJarScripts(){
		super(APP_NAME, ApoCliParser.defaultParser(), new AO_HelpSimple('h', null), null, new AO_Version('v', null));

		MessageConsole.activateAll();
		MessageConsole.setApplicationName(this.appName);
	}

	@Override
	public String getDescription() {
		return "Generates scripts (OS specific) for running S2V applications from the JAR with all dependencies";
	}

	@Override
	public String getDisplayName(){
		return APP_DISPLAY_NAME;
	}

	@Override
	public String getName() {
		return APP_NAME;
	}

	@Override
	public String getVersion() {
		return APP_VERSION;
	}

	/**
	 * Tests if the class is run from an executable JAR.
	 * @return true if run from an executable JAR (JAR with Main-Class in manifest), false otherwise with errors printed
	 */
	protected boolean inExecJar(){
		Class<Gen_ExecJarScripts> clazz = Gen_ExecJarScripts.class;
		String className = clazz.getSimpleName() + ".class";
		String classPath = clazz.getResource(className).toString();
		if (!classPath.startsWith("jar")) {
			System.err.println(this.getName() + ": not started in a jar, cannot proceed");
			return false;
		}

		String manifestPath = classPath.substring(0, classPath.lastIndexOf("!") + 1) + "/META-INF/MANIFEST.MF";
		Manifest manifest;
		try {
			manifest = new Manifest(new URL(manifestPath).openStream());
		}
		catch (IOException ex) {
			System.err.println(this.getName() + ": exception while retrieving manifest: " + ex.getMessage());
			return false;
		}
		Attributes attr = manifest.getMainAttributes();
		if(StringUtils.isBlank(attr.getValue("Main-Class"))){
			System.err.println(this.getName() + ": no main class in manifest, probably not an executable JAR, cannot continue");
			return false;
		}
		return true;
	}

	@Override
	public void runApplication() {
		String jarFn = null;

		if(!this.inExecJar()){
			this.msgMgr.add(Templates_AppStart.MUST_BE_IN_EXEC_JAR.getError());
		}
		else{
			try {
				File file = new File(Gen_ExecJarScripts.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
				jarFn = file.toString();
			}
			catch (URISyntaxException ex) {
				this.msgMgr.add(Templates_AppStart.NO_JAR_PATH.getError(ex.getMessage()));
			}

			if(jarFn==null){
				this.msgMgr.add(Templates_AppStart.NO_JAR_PATH.getError("value was null"));
			}

			if(!SystemUtils.IS_OS_UNIX && !SystemUtils.IS_OS_WINDOWS){
				this.msgMgr.add(Templates_AppStart.OS_NOT_SUPPORTED.getError("Unix nor Windows"));
			}
		}

		if(this.getErrNo()==0){
			ArrayList<String> cmds = new ArrayList<>();
			if(this.execClassMap!=null){
				for(String s : execClassMap.keySet()){
					if(s.equals(Gen_ConfigureSh.APP_NAME)){
						continue;
					}
					if(s.equals(Gen_RunScripts.APP_NAME)){
						continue;
					}
					if(s.equals(APP_NAME)){
						continue;
					}
					cmds.add(s);
				}
			}

			String dir = System.getProperty("user.dir");
			for(String s : cmds){
				if(SystemUtils.IS_OS_UNIX){
					String filename = dir + "/" + s + ".sh";
					File file = new File(filename);
					List<String> lines = new ArrayList<>();
					lines.add("#!/usr/bin/env bash");
					lines.add("");
					lines.add("java -jar " + jarFn + " " + s + " $*");
					lines.add("");
					this.writeFile(file, lines);
				}
				else if(SystemUtils.IS_OS_WINDOWS){
					String filename = dir + "/" + s + ".bat";
					File file = new File(filename);
					List<String> lines = new ArrayList<>();
					lines.add("@echo off");
					lines.add("");
					lines.add("java -jar " + jarFn + " " + s + " %*");
					lines.add("");
					this.writeFile(file, lines);
				}
			}
		}
	}

	/**
	 * Hook for a calling ExecS instance to set its class map for the script generator.
	 * @param execClassMap calling executor class map to create run scripts from
	 */
	public void setClassMap(Map<String, Class<? extends IsApplication>> execClassMap){
		this.execClassMap = execClassMap;
	}

	/**
	 * Writes the given lines to the given file, if possible.
	 * @param file file to write to
	 * @param lines lines to write to the file
	 */
	public void writeFile(File file, List<String> lines){
		if(file.exists()){
			file.delete();
		}
		try {
			FileWriter out = new FileWriter(file);
			for(String s : lines){
				out.write(s);
				out.write(System.lineSeparator());
			}
			out.close();
			file.setExecutable(true);
		}
		catch (IOException ex) {
			this.msgMgr.add(Templates_OutputFile.IO_EXCEPTION_WRITING.getError("script", file.toString(), ex.getMessage()));
		}
	}
}

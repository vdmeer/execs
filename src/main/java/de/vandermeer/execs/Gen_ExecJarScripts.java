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

import de.vandermeer.execs.options.ApplicationOption;
import de.vandermeer.execs.options.ExecS_CliParser;

/**
 * Application to generate starts scripts when running ExecS from an executable JAR.
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.3.9-SNAPSHOT build 170411 (11-Apr-17) for Java 1.8
 * @since      v0.4.0
 */
public class Gen_ExecJarScripts implements ExecS_Application {

	/** Application name. */
	public final static String APP_NAME = "gen-exec-jar-scripts";

	/** Application display name. */
	public final static String APP_DISPLAY_NAME = "Generate JAR Scripts for Executable JARs";

	/** Application version, should be same as the version in the class JavaDoc. */
	public final static String APP_VERSION = "v2.0.0-SNAPSHOT build 170411 (11-Apr-17) for Java 1.8";

	/** CLI parser. */
	final private ExecS_CliParser cli;

	/** List of application options. */
	final private ArrayList<ApplicationOption<?>> options = new ArrayList<>();

	/** Local class map, must be set by calling ExecS instance. */
	Map<String, Class<? extends ExecS_Application>> execClassMap;

	/**
	 * Creates a new application.
	 */
	public Gen_ExecJarScripts(){
		this.cli = new ExecS_CliParser();
	}

	@Override
	public int executeApplication(String[] args) {
		int ret = ExecS_Application.super.executeApplication(args);
		if(ret!=0){
			return ret;
		}

		if(!this.inExecJar()){
			return -1;
		}

		String jarFn;
		try {
			File file = new File(Gen_ExecJarScripts.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
			jarFn = file.toString();
		}
		catch (URISyntaxException ex) {
			System.err.println(this.getAppName() + ": error retrieving JAR path - " + ex.getMessage());
			return -2;
		}

		if(jarFn==null){
			System.err.println(this.getAppName() + ": could not retrieve JAR path");
			return -3;
		}

		if(!SystemUtils.IS_OS_UNIX && !SystemUtils.IS_OS_WINDOWS){
			System.err.println(this.getAppName() + ": OS not supported, neither Unix nor Windows");
			return -4;
		}

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
				String fn = dir + "/" + s + ".sh";
				File file = new File(fn);
				List<String> lines = new ArrayList<>();
				lines.add("#!/usr/bin/env bash");
				lines.add("java -jar " + jarFn + " " + s + " $*");
				if(!this.writeFile(file, lines)){
					return -5;
				}
			}
			else if(SystemUtils.IS_OS_WINDOWS){
				String fn = dir + "/" + s + ".bat";
				File file = new File(fn);
				List<String> lines = new ArrayList<>();
				lines.add("java -jar " + jarFn + " " + s + " %*");
				if(!this.writeFile(file, lines)){
					return -6;
				}
			}
		}

		return 0;
	}

	/**
	 * Writes the given lines to the given file, if possible.
	 * @param file file to write to
	 * @param lines lines to write to the file
	 * @return true if lines where written to the file, false otherwise with errors printed
	 */
	public boolean writeFile(File file, List<String> lines){
		if(file.exists()){
			file.delete();
		}
		try {
			FileWriter out = new FileWriter(file);
			for(String s : lines){
				out.write(s);
			}
			out.close();
			file.setExecutable(true);
		}
		catch (IOException ex) {
			System.err.println(this.getAppName() + ": IO exception while writing to file - " + file + " with message: " + ex.getMessage());
			return false;
		} 
		return true;
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
			System.err.println(this.getAppName() + ": not started in a jar, cannot proceed");
			return false;
		}

		String manifestPath = classPath.substring(0, classPath.lastIndexOf("!") + 1) + "/META-INF/MANIFEST.MF";
		Manifest manifest;
		try {
			manifest = new Manifest(new URL(manifestPath).openStream());
		}
		catch (IOException ex) {
			System.err.println(this.getAppName() + ": exception while retrieving manifest: " + ex.getMessage());
			return false;
		}
		Attributes attr = manifest.getMainAttributes();
		if(StringUtils.isBlank(attr.getValue("Main-Class"))){
			System.err.println(this.getAppName() + ": no main class in manifest, probably not an executable JAR, cannot continue");
			return false;
		}
		return true;
	}

	/**
	 * Adds a new option to CLI parser and option list.
	 * @param option new option, ignored if null
	 */
	protected void addOption(ApplicationOption<?> option){
		if(option!=null){
			this.getCli().addOption(option);
			this.options.add(option);
		}
	}

	@Override
	public String getAppDescription() {
		return "Generates scripts (OS specific) for running S2V applications from the JAR with all dependencies";
	}

	@Override
	public String getAppDisplayName(){
		return APP_DISPLAY_NAME;
	}

	@Override
	public String getAppName() {
		return APP_NAME;
	}

	@Override
	public ApplicationOption<?>[] getAppOptions() {
		return this.options.toArray(new ApplicationOption<?>[]{});
	}

	@Override
	public String getAppVersion() {
		return APP_VERSION;
	}

	@Override
	public ExecS_CliParser getCli() {
		return this.cli;
	}

	/**
	 * Hook for a calling ExecS instance to set its class map for the script generator
	 * @param execClassMap calling executor class map to create run scripts from
	 */
	public void setClassMap(Map<String, Class<? extends ExecS_Application>> execClassMap){
		this.execClassMap = execClassMap;
	}
}

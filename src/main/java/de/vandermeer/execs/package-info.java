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

/**
 * A simple set of classes to execute programs from the command line with automated generation of run scripts.
 * 
 * The package offers the following components:
 * <ul>
 * 		<li>An interface to implement an executable application - {@link de.vandermeer.execs.ExecS_Application},</li>
 * 		<li>A class to execute those implemented applications at runtime - {@link de.vandermeer.execs.ExecS},</li>
 * 		<li>A generator for a configuration script - {@link de.vandermeer.execs.Gen_ConfigureSh},</li>
 * 		<li>A generator for run scripts for implemented applications - {@link de.vandermeer.execs.Gen_RunScripts},<li>
 * </ul>
 * 
 * 
 * <h3>Implementing an executable application</h3>
 * 
 * <h4>A simple application with manual CLI parsing</h4>
 * Implementing an executable application means to implement the methods in the provided interface {@link de.vandermeer.execs.ExecS_Application}.
 * The interface defines methods for:
 * <ul>
 * 		<li>
 * 			Printing a help screen for the application in case it is invoked with the "-h" help option - {@link de.vandermeer.execs.ExecS_Application#appHelpScreen()}.
 * 			This methods has a default implementation which will use the application's CLI object to automatically generate the help screen, if such an object is available.
 * 			For individual help screens simply overwrite the default implementation.
 * 		</li>
 * 		<li>Get the application name, i.e. the name a user should see when interacting with the application - {@link de.vandermeer.execs.ExecS_Application#getAppName()}.</li>
 * 		<li>A name to be used for automated run script generation - {@link de.vandermeer.execs.ExecS_Application#getAppName()}.</li>
 * 		<li>A CLI object that can parse a command line for the application and produce a help screen - {@link de.vandermeer.execs.ExecS_Application#getCli()}.</li>
 * 		<li>Finally the actual method to execute the application with the original command line arguments - {@link de.vandermeer.execs.ExecS_Application#executeApplication(String[])}.</li>
 * </ul>
 * 
 * To start, we create a new class implementing the interface.
 * The class we are building is called "Application_Lang". This class will implement it's own CLI simple CLI parsing.
 * <pre>{@code
	public class Application_Lang implements ExecutableApplication {
	}
 * }</pre>
 * 
 * The first two methods we are adding are for the name of the application and the script name:
 * <pre>{@code
	public String getName() {
		return "Application-Lang";
	}

	public String getAppName(){
		return "application-lang";
	}
 * }</pre>
 * 
 * The class does not use the standard CLI object, so it implements its own help screen:
 * <pre>{@code
	public void appHelpScreen() {
		System.out.println(this.getName() + " help:");
		System.out.println("- this is a simple application called " + this.getName());

		System.out.println("-g for a German greeting");
		System.out.println("-e for an English greeting");
		System.out.println("-f for a French greeting");
	}
 * }</pre>
 * 
 * The actual execution method can now parse the command line and print some messages depending on the command line arguments:
 * <pre>{@code
	public int executeApplication(String[] args) {
		if(args.length==0){
			this.appHelpScreen();
			return -1;
		}

		switch(args[0]){
			case "-g":
				System.out.println("Hallo, hier ist die Anwendung Sprache.");
				break;
			case "-f":
				System.out.println("Bonjour, ceci est le application lang.");
				break;
			case "-e":
				System.out.println("Hi, this is application language.");
				break;
			default:
				System.out.println(this.getName() + ": unknown option: " + args[0]);
				return -1;
		}
		return 0;
	}
 * }</pre>
 * 
 * <h4>A simple application with automated CLI parsing</h4>
 * Writing your own CLI parsing can be very tedious. Instead, we can define our CLI arguments and use the provided CLI parser {@link de.vandermeer.execs.options.ExecS_CliParser}.
 * This parser can also be used to auto-generate usage information.
 * To define a CLI argument we create a new class with all instructions in the constructor:
 * <pre>{@code
	public class CliOption_Help extends AbstractClioption {
		public CliOption_Help(){
			Option.Builder builder = Option.builder("h");
			builder.longOpt("help");
			builder.desc("for a German greeting");
			builder.required(false);
			this.option = builder.build();
		}
	}
 * }</pre>
 * 
 * Repeat this for the other two options for French and English language.
 * Now we can implement an advanced version of our language application use those CLI options and the CLI parser.
 * <pre>{@code
	public class Application_Lang_CLI implements ExecutableApplication {
	}
 * }</pre>
 * 
 * First add the CLI parser and the CLI options to the new class:
 * <pre>{@code
	ExecS_CliParser cli;
	CliOpt_G cliG = new CliOpt_G();
	CliOpt_F cliF = new CliOpt_F();
	CliOpt_E cliE = new CliOpt_E();
 * }</pre>
 * 
 * The add a constructor that create the CLI parser and adds our CLI options to it:
 * <pre>{@code
	Application_Lang_CLI(){
		this.cli = new ExecS_CliParser();
		this.cli.addOption(this.cliG);
		this.cli.addOption(this.cliF);
		this.cli.addOption(this.cliE);
	}
 * }</pre>
 * 
 * In the execution method, start with parsing the command line using our new CLI parser.
 * Then use the CLI parser to get the option (we also changed the logic slightly so that all options are used):
 * <pre>{@code
	public int executeApplication(String[] args) {
		int ret = ExecS_CliParser.doParse(args, this.cli, this.getName());
		if(ret!=0){
			return ret;
		}

		if(!this.cli.hasOption(this.cliG)){
			System.out.println("Hallo, hier ist die Anwendung Sprache.");
		}
		if(!this.cli.hasOption(this.cliF)){
			System.out.println("Bonjour, ceci est le application lang.");
		}
		if(!this.cli.hasOption(this.cliE)){
			System.out.println("Hi, this is application language.");
		}
		return 0;
	}
 * }</pre>
 * 
 * Finally, we can change the help screen method to make use of our CLI parser:
 * <pre>{@code
	public void appHelpScreen() {
		System.out.println(this.getName() + " help:");
		System.out.println("- this is a simple application called " + this.getName());
		ExecutableApplication.super.appHelpScreen();
	}
 * }</pre>
 * 
 * 
 * <h3>Auto-generate run scripts for an application</h3>
 * <p>
 * 		When the application is deployed and installed, we still need a script that runs it, e.g. configures a class path and start Java with the right class and all command line arguments.
 * 		If the application is moved (i.e. its install directory is changed), one needs to rebase those run scripts.
 * 		All required scripts can be created automatically using the provided generators
 * </p>
 * 
 * The run scripts are realized as follows: first, a script (by default) called {@code run} will be created. This script calls the {@link ExecS} main method and has all configuration for Java.
 * Then an individual script for each executable application (class implementing {@link de.vandermeer.execs.ExecS_Application} is created. Those scripts will call the main {@code run} script.
 * By default, all the scripts above will use absolute paths. This allows to execute them from any directory, instead of requiring to go to the application directory and start them from there.
 * <ul>
 * 		<li>{@link de.vandermeer.execs.Gen_ConfigureSh} - creates a configuration script (create and rebase run scripts).</li>
 * 		<li>{@link de.vandermeer.execs.Gen_RunScripts} - creates individual run scripts.</li>
 * </ul>
 * 
 * 
 * 
 * <p style="text-align:center;">
 * 		<img src="doc-files/composition.png" alt="ExecS Composition">
 * </p>
 * 
 * 
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.3.9-SNAPSHOT build 170411 (11-Apr-17) for Java 1.8
 * @since      v0.0.1
 */
package de.vandermeer.execs;





/*
<p>
This package provides a standard way to find and execute all those tools, or as they are called here applications.
A simple interface should be used by any component that wants to be executed.
Implementing <code>ExecutableApplication</code> provides the main method <code>executeApplication()</code> and a method for printing a help screen.
The only requirement is that the default constructor must be provided, to allow for a generic instantiation at runtime.
</p>

<p>
The <code>CF</code> can be used to search all or filtered jar files (or URIs) for any class that implement the interface.
Once all implementations are found, one can use the <code>ExecS</code> object with its <code>static main()</code> to execute any application from the command line.
</p>

<p>
The <code>ExecS</code> class allows to list all executable applications, execute one application triggering a help screen and to register short names for applications.
These short names can be used as aliases to execute a application, otherwise a fully qualified class name will be required.
</p>

<p>
One can also extend the <code>ExecS</code> class to build more sophisticated versions of the execution object, to
register specific short names or to search for other interfaces and classes in jar files at runtime.
</p>

<p>
The package can also be used to automatically generate run scripts (batch files for windows or shell scripts for UNIX and Cygwin) to execute applications from command line.
The generators <code>Gen_RunScripts</code>, <code>Gen_RunSh</code>, and <code>Gen_RebaseSh</code> provide functionality for configuring the generation and will generate scripts with all required information (e.g. Java class path).
</p>

*/
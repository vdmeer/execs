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

import org.apache.commons.cli.CommandLine;

import de.vandermeer.execs.options.AO_Help;
import de.vandermeer.execs.options.AO_Version;
import de.vandermeer.execs.options.ApplicationOption;
import de.vandermeer.execs.options.ExecS_CliParser;

/**
 * Interface for an application that can be executed.
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.3.6 build 160319 (19-Mar-16) for Java 1.8
 * @since      v0.0.1
 */
public interface ExecS_Application {

	/**
	 * Executes the application.
	 * The default implementation will try to parse the command line with the application's CLI object and if that does not return success (0), call the help screen automatically.
	 * @param args arguments for execution
	 * @return 0 on success, negative integer on error, positive integer on no-error but exit application
	 */
	default int executeApplication(String[] args){
		if(this.getCli()!=null){
			//add the help option
			AO_Help optionHelp = new AO_Help();
			AO_Version optionVersion = new AO_Version();
			this.getCli().addOption(optionHelp);
			this.getCli().addOption(optionVersion);

			//if there are arguments, and the first one is either -? or --help we need to process a help request
			if(args.length>0 && (args[0].equals("-" + optionHelp.getCliOption().getOpt()) || args[0].equals("--" + optionHelp.getCliOption().getLongOpt()))){
				//we have some help requested in the command line, see how we deal with it
				if(args.length>2){
					//too many argument for help, only 0 and 1 works
					System.err.println(this.getAppName() + ": help requested but too many arguments given");
					return -1;
				}
				if(args.length==2){
					//help w/argument
					this.appHelpScreen(args[1]);
					return 1;
				}
				else if(args.length==1){
					//help w/o argument
					this.appHelpScreen();
					return 1;
				}
			}

			//if there are arguments and the first one is version then do version and return
			if(args.length==1 && args[0].equals("--" + optionVersion.getCliOption().getLongOpt())){
				System.out.println(this.getAppVersion());
				return 1;
			}

			//parse original command line and process errors
			Exception err = this.getCli().parse(args);
			if(err!=null){
				System.err.println(this.getAppName() + ": error parsing command line -> " + err.getMessage());
				return -1; 
			}

			//no help required and no error, so set all options with their command line arguments, return error if any occurred
			int ret = this.setCli4Options(this.getCli().getCommandLine());
			if(ret!=0){
				this.appHelpScreen();
				return ret;
			}
		}
		return 0;
	}

	/**
	 * Returns the application's CLI parser.
	 * @return CLI parser
	 */
	default ExecS_CliParser getCli(){
		return null;
	}

	/**
	 * Returns the name of the application, which is the name of the executable object for instance a script.
	 * This application name will be used for information and error messages and user interactions.
	 * For instance, a tool such as {@link Gen_RunScripts} will use this name to generate an executable script with all runtime configurations.
	 * @return application name
	 */
	String getAppName();

	/**
	 * Returns a 1 line description of the application, should not be null.
	 * @return 1-line application description, mainly used in default help screen implementation
	 */
	String getAppDescription();

	/**
	 * Returns the display name of the application.
	 * This display name will be used for documentation and general user interaction.
	 * The default is the original application name.
	 * @return the application's display name, default is the application name returned by {@link #getAppName()}
	 */
	default String getAppDisplayName(){
		return this.getAppName();
	}

	/**
	 * Returns version information of the application for command line processing of the version option.
	 * @return application version, should not be null
	 */
	String getAppVersion();

	/**
	 * Prints a help screen for the application, to be used by an executing component.
	 */
	default void appHelpScreen(){
		System.out.println(this.getAppDisplayName() + " - " + this.getAppVersion());
		if(this.getAppDescription()!=null){
			System.out.println();
			System.out.println(this.getAppDescription());
		}
		if(this.getCli()!=null){
			System.out.println();
			this.getCli().usage(this.getAppName());
		}
		System.out.println();
	}

	/**
	 * Returns the application options.
	 * @return application options
	 */
	ApplicationOption<?>[] getAppOptions();

	/**
	 * Prints specific help for a command line option of the application.
	 * @param arg the command line argument specific help is requested for
	 */
	default void appHelpScreen(String arg){
		if(arg==null){
			return;
		}

		ApplicationOption<?>[] options = this.getAppOptions();
		if(options!=null){
			boolean found = false;
			for(ApplicationOption<?> opt : options){
				if(opt.getCliOption()!=null){
					if(arg.equals(opt.getCliOption().getOpt()) || arg.equals(opt.getCliOption().getLongOpt())){
						System.out.println(opt.getUsage());
						found = true;
					}
				}
				else if(opt.getOptionKey()!=null){
					if(arg.equals(opt.getOptionKey())){
						System.out.println(opt.getUsage());
						found = true;
					}
				}
			}
			if(found==false){
				System.err.println(this.getAppName() + ": unknown CLI argument / option key -> " + arg);
			}
		}
	}

	/**
	 * Sets the CLI values for application options if they are set in the command line.
	 * @param cmdLine parsed command line with arguments for the application options
	 * @return 0 on success (execution can proceed), non-0 otherwise (execution should not proceed)
	 */
	default int setCli4Options(CommandLine cmdLine){
		if(cmdLine==null){
			return -99;
		}

		int ret = 0;
		ApplicationOption<?>[] options = this.getAppOptions();
		if(options!=null){
			for(ApplicationOption<?> opt : options){
				if(opt.getCliOption()!=null){
					ret += opt.setCliValue(cmdLine);
				}
			}
		}
		return ret;
	}
}

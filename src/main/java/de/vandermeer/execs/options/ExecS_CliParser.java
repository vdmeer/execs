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

package de.vandermeer.execs.options;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * CLI implementation for applications.
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.3.7 build 170404 (04-Apr-17) for Java 1.8
 * @since      v0.0.1
 */
public class ExecS_CliParser {

	/** Parsing options. */
	protected Options options;

	/** Parsed command line. */
	protected CommandLine cmdLine;

	/** Set of options already added, used. */
	protected final Set<String> usedOptions;

	/**
	 * Returns a new CLI parser.
	 */
	public ExecS_CliParser(){
		this.options = new Options();
		this.usedOptions = new HashSet<>();
	}

	/**
	 * Adds a CLI option to the parser.
	 * @param option new CLI option, ignored if the option is null or getOption() on the option is null
	 * @throws IllegalArgumentException if the option uses short/long options already in use
	 * @return self to allow for chaining
	 */
	public ExecS_CliParser addOption(ApplicationOption<?> option){
		if(option!=null && option.getCliOption()!=null){
			this._addOption(option.getCliOption());
		}
		return this;
	}

	/**
	 * Adds a CLI option to the parser.
	 * @param option new CLI option, ignored if the option is null or getOption() on the option is null
	 * @throws IllegalArgumentException if the option uses short/long options already in use
	 * @return self to allow for chaining
	 * @throws {@link IllegalArgumentException} if short or long option was in use already
	 */
	public ExecS_CliParser addOption(Option option){
		if(option!=null){
			this._addOption(option);
		}
		return this;
	}

	/**
	 * Adds a CLI option to the parser.
	 * @param option new CLI option, ignored if the option is null or getOption() on the option is null
	 * @throws {@link IllegalArgumentException} if short or long option was in use already
	 */
	protected void _addOption(Option option){
		if(option==null){
			return;
		}

		if(this.usedOptions.contains(option.getOpt())){
			throw new IllegalArgumentException("ExecS Cli: short option <" + option.getOpt() + "> already in use");
		}
		if(this.usedOptions.contains(option.getLongOpt())){
			throw new IllegalArgumentException("ExecS Cli: long option <" + option.getLongOpt() + "> already in use");
		}
		this.options.addOption(option);
		if(option.getOpt()!=null){
			this.usedOptions.add(option.getOpt());
		}
		if(option.getLongOpt()!=null){
			this.usedOptions.add(option.getLongOpt());
		}
	}

	/**
	 * Tests if an option is already added to the parser.
	 * @param option the option to test for
	 * @return false if option was null or n short and long version of this option is used, true otherwise
	 */
	public boolean hasOption(Option option){
		if(option==null){
			return false;
		}
		if(this.usedOptions.contains(option.getOpt())){
			return true;
		}
		if(this.usedOptions.contains(option.getLongOpt())){
			return true;
		}
		return false;
	}

	/**
	 * Parses command line arguments and fills set options.
	 * @param args command line arguments
	 * @return null on success, an exception with message on parsing error
	 */
	public ParseException parse(String[] args){
		ParseException ret = null;
		CommandLineParser parser = new DefaultParser();
		try{
			this.cmdLine = parser.parse(this.options, args);
		}
		catch(ParseException pe){
			ret = pe;
		}
		return ret;
	}

	/**
	 * Prints a usage screen based on set options.
	 * @param appName name of the application to be used for the usage screen
	 */
	public void usage(String appName){
		HelpFormatter formatter=new HelpFormatter();
		formatter.printHelp(appName, null, this.options, null, false);
	}

	/**
	 * Parses command line arguments for a given CLI parser.
	 * @param args command line arguments to parse
	 * @param cli CLI object with options to parse for
	 * @param appName application name for error messages
	 * @return 0 on success, -1 on error with error being logged
	 */
	static int doParse(String[] args, ExecS_CliParser cli, String appName){
		Exception err = cli.parse(args);
		if(err!=null){
			System.err.println(appName + ": error parsing command line -> " + err.getMessage());
			return -1; 
		}
		return 0;
	}

	/**
	 * Returns the command line.
	 * @return command line with arguments if parsed, null otherwise
	 */
	public CommandLine getCommandLine(){
		return this.cmdLine;
	}
}

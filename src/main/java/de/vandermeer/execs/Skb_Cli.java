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
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

/**
 * CLI implementation for servers.
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.0.3 build 150618 (18-Jun-15) for Java 1.8
 */
public class Skb_Cli {

	/** Parsing options. */
	protected Options options;

	/** Parsed command line. */
	protected CommandLine cmdLine;

	/**
	 * Returns a new CLI parser.
	 */
	public Skb_Cli(){
		this.options = new Options();
	}

	/**
	 * Adds a CLI option to the parser.
	 * @param option new CLI option, ignored if the option is null or getOption() on the option is null
	 * @return self to allow for chaining
	 */
	public Skb_Cli addOption(Skb_CliOptions option){
		if(option!=null && option.getOption()!=null){
			this.options.addOption(option.getOption());
		}
		return this;
	}

	/**
	 * Parses command line arguments and fills set options.
	 * @param args command line arguments
	 * @return null on success, an exception with message on parsing error
	 */
	public ParseException parse(String[] args){
		ParseException ret = null;
		CommandLineParser parser = new PosixParser();
		try{
			this.cmdLine = parser.parse(this.options, args);
		}
		catch(ParseException pe){
			ret = pe;
		}
		return ret;
	}

	/**
	 * Returns a string version of the value of an option set by the parser.
	 * @param option option to look for
	 * @return string value if set, null otherwise
	 */
	public String getOption(Skb_CliOptions option){
		String ret = null;
		String o = (option!=null)?option.getOptionString():null;
		if(o!=null && this.cmdLine.hasOption(o)){
			ret = this.cmdLine.getOptionValue(o);
		}
		return ret;
	}

	/**
	 * Tests if an option was used in the command line.
	 * @param option option to test for
	 * @return true if option was used, false otherwise
	 */
	public boolean hasOption(Skb_CliOptions option){
		String o = (option!=null)?option.getOptionString():null;
		if(o!=null){
			return this.cmdLine.hasOption(o);
		}
		return false;
	}

	/**
	 * Prints a usage screen based on set options
	 * @param appName name of the application to be used for the usage screen
	 */
	public void usage(String appName){
		HelpFormatter formatter=new HelpFormatter();
		formatter.printHelp(appName, null, this.options, null, false);
	}

	/**
	 * Parses command line arguments for a given CLI object.
	 * @param args command line arguments to parse
	 * @param cli CLI object with options to parse for
	 * @param appName application name for error messages
	 * @return 0 on success, -1 on error with error being logged
	 */
	public static int doParse(String[] args, Skb_Cli cli, String appName){
		int ret = 0;
		Exception err = cli.parse(args);
		if(err!=null){
			System.err.println(appName + ": : error parsing command line -> " + err.getMessage());
			return -1; 
		}
		return ret;
	}
}

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
 * Command line parser for the service executor.
 * 
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.0.4 build 150619 (19-Jun-15) for Java 1.8
 */
public class Skb_ExecCli {

	protected Options options;

	protected CommandLine line;

	protected String appName;

	/**
	 * Constructor.
	 * @param appName name of the application using the CLI class
	 */
	public Skb_ExecCli(String appName){
		this.appName = appName;
		this.options = new Options();
		this.buildCliOptions();
	}

	/**
	 * Registers a pre-defined set of options with all parameters.
	 */
	protected void buildCliOptions(){
		this.options.addOption("h", "help",          false, "prints usage information");
		this.options.addOption("l", "list",          false, "list all servers in classpath");
		this.options.addOption("j", "jar-filter",    false, "use jar filter");
		this.options.addOption("p", "pkg-filter",    false, "use pkg filter");
	}

	/**
	 * Parses command line and sets options.
	 * @param args command line arguments
	 * @throws ParseException an exception in case parsing went wrong
	 */
	public void parse(String[] args) throws ParseException {
		CommandLineParser parser = new PosixParser();
		try {
			this.line = parser.parse(this.options, args, true);
		}
		catch(ParseException ex){
			throw ex;
		}
	}

	/**
	 * Prints a usage screen with all options.
	 */
	public void usage(){
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(this.appName, this.options);
	}

	public boolean hasOptionHelp(){
		if(this.line.hasOption('h')){
			return true;
		}
		return false;
	}

	public boolean hasOptionList(){
		if(this.line.hasOption('l')){
			return true;
		}
		return false;
	}

	public boolean hasOptionJarFilter(){
		if(this.line.hasOption('j')){
			return true;
		}
		return false;
	}

	public boolean hasOptionPkgFilter(){
		if(this.line.hasOption('p')){
			return true;
		}
		return false;
	}
}

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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.lang3.StringUtils;

/**
 * Executable service to generate a shell script running {@link Gen_RunScripts}.
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.1.0 build 150812 (12-Aug-15) for Java 1.8
 * @since      v0.0.6
 */
public class Gen_RunSh implements ExecutableService {

	/** Service name. */
	public final static String SERVICE_NAME = "gen-run";

	/** A CLI option to specify a directory with all required jars. */
	public final static ExecS_CliOption CLIOPT_LIBDIR = ExecS_Factory.newCliOption(null, "lib-dir", "DIR", "specifies a directory with requried jar files");

	/** A CLI option to specify a property file with configurations for the generator. */
	public final static ExecS_CliOption CLIOPT_PROP_FILE = ExecS_Factory.newCliOption("property-file", "FILE", "a property file with configurations for the generator");

	/** Local CLI options for CLI parsing. */
	protected ExecS_Cli cli;

	/**
	 * Returns a new run shell script generator.
	 */
	public Gen_RunSh(){
		this.cli = new ExecS_Cli();
		this.cli.addOption(CLIOPT_LIBDIR);
		this.cli.addOption(CLIOPT_PROP_FILE);
	}

	@Override
	public int executeService(String[] args) {
		// parse command line, exit with help screen if error
		int ret = ExecS_Cli.doParse(args, this.cli, this.getName());
		if(ret!=0){
			this.serviceHelpScreen();
			return ret;
		}

		String fn = "/de/vandermeer/execs/bin/gen-run-script.sh";
		try {
			InputStream in = getClass().getResourceAsStream(fn);
			BufferedReader input = new BufferedReader(new InputStreamReader(in));
			String line;
			while((line=input.readLine())!=null){
				if(StringUtils.startsWith(line, "LIB_HOME=") && ExecS_Cli.testOption(this.cli, CLIOPT_LIBDIR)){
					System.out.println("LIB_HOME=" + this.cli.getOption(CLIOPT_LIBDIR));
				}
				else if(StringUtils.startsWith(line, "PROP_FILE=") && ExecS_Cli.testOption(this.cli, CLIOPT_PROP_FILE)){
					System.out.println("PROP_FILE=" + this.cli.getOption(CLIOPT_PROP_FILE));
				}
				else{
					System.out.println(line);
				}
			}
		}
		catch(NullPointerException ne){
			System.err.println(this.getName() + ": exception while reading shell script from resource <" + fn + ">: "+ ne.getMessage());
		}
		catch (IOException e) {
			System.err.println(this.getName() + ": IO exception while reading shell script: "+ e.getMessage());
		}
		return 0;
	}

	@Override
	public String getName() {
		return Gen_RunSh.SERVICE_NAME;
	}

	@Override
	public ExecS_Cli getCli() {
		return this.cli;
	}

	@Override
	public void serviceHelpScreen() {
		System.out.println("Generates run shell script to generate run scripts.");
		System.out.println();
		this.cli.usage(this.getName());
	}

}

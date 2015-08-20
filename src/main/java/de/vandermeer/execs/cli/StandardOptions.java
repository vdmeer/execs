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

package de.vandermeer.execs.cli;

/**
 * Standard CLI options.
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.1.0 build 150812 (12-Aug-15) for Java 1.8
 * @since      v0.0.2
 */
public abstract class StandardOptions {

	/** Option to print help information. */
	public final static ExecS_CliOption HELP = new CliOption_Help();

	/** Option to name a component ID as a mandatory option. */
	public final static ExecS_CliOption COMPONENT_ID = new CliOption_Id(true);

	/** Option to name an output file as a mandatory option. */
	public final static ExecS_CliOption FILE_OUTPUT = new CliOption_FileOut(true);

	/** Option to name an input directory as a mandatory option. */
	public final static ExecS_CliOption FILE_INPUT = new CliOption_FileIn(true);

	/** Option to name an output directory as a mandatory option. */
	public final static ExecS_CliOption DIRECTORY_OUTPUT = new CliOption_DirectoryOut(true);

	/** Option to name an input file as a mandatory option. */
	public final static ExecS_CliOption DIRECTORY_INPUT = new CliOption_DirectoryIn(true);

	/** Option for a server to run in background mode as an optional option. */
	public final static ExecS_CliOption SERVER_MODE = new CliOption_Servermode(false);

	/** Option to name a target, for instance for a compilation as a mandatory option. */
	public final static ExecS_CliOption TARGET = new CliOption_Target(true);

	/** A CLI option to specify a directory with all required jars. */
	public final static ExecS_CliOption LIB_DIR = new CliOption_LibDir(true);

	/** A CLI option to specify a property file with configurations. */
	public final static ExecS_CliOption PROP_FILE = new CliOption_PropertyFile(false);

	/** A CLI option to specify the application home directory specific to a given target format. */
	public final static ExecS_CliOption APPLICATION_HOME_DIR = new CliOption_AppHomeDirectory(true);

	/** A CLI option to specify a a property file with class names (executable services) mapped to script names. */
	public final static ExecS_CliOption CLASSMAP_FILE = new CliOption_ClassmapFile(false);

	/** A CLI option to specify a StringTemplate (stg) file with templates for generating run scripts. */
	public final static ExecS_CliOption STG_FILE = new CliOption_StgFile(false);

}

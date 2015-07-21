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

import org.apache.commons.cli.Option;

/**
 * Standard CLI options.
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.0.8 build 150721 (21-Jul-15) for Java 1.8
 * @since      v0.0.2
 */
public enum StandardOptions implements ExecS_CliOption {
	/** Option to print help information. */
	HELP				('h',	"help",					null,		"prints help and usage screen"),

	/** Option to name a component ID. */
	COMPONENT_ID		('i',	"id",					"ID",		"component identifier"),

	/** Option to name an output file. */
	FILE_OUTPUT			('o',	"output-file",			"FILE",		"output filename"),

	/** Option to name an input directory. */
	FILE_INPUT			('f',	"input-file",			"FILE",		"input filename"),

	/** Option to name an output directory. */
	DIRECTORY_OUTPUT	(null,	"output-directory",		"DIR",		"output directory"),

	/** Option to name an input file. */
	DIRECTORY_INPUT		(null,	"input-directory",		"DIR",		"input directory"),

	/** Option for a server to run in background mode. */
	SERVER_MODE			(null,	"srv-mode",				null,		"tells a server to run in background mode"),

	/** Option to name a target, for instance for a compilation. */
	TARGET				(null,	"target",				"TARGET",	"specifies a target, for instance for a compilation"),

	;

	/** A generated CLI option for the enumerate. */
	private ExecS_CliOption option;

	/**
	 * Creates a new option with short and long access along with argument and description
	 * @param shortOption the short option, i.e. a character
	 * @param longOption the long option, i.e. a string
	 * @param withArg option argument
	 * @param description option description
	 */
	StandardOptions(Character shortOption, String longOption, String withArg, String description){
		this.option = ExecS_Factory.newCliOption(shortOption, longOption, withArg, description);
	}

	@Override
	public Option getOption(){
		return this.option.getOption();
	}

	@Override
	public String getDescription(){
		return this.option.getDescription();
	}

	@Override
	public String getOptionString(){
		return this.option.getOptionString();
	}
}

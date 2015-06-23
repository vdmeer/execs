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
 * @version    v0.0.5 build 150623 (23-Jun-15) for Java 1.8
 */
public enum StandardOptions implements ExecS_CliOption {
	/** Option to print help information. */
	HELP				('h',	"help",					null,		"prints help and usage screen"),

	/** Option to name an Apache Zookeeper host (IP address or host name). */
	ZK_HOST				('z',	"zk-host",				"HOST",		"Apache Zookeeper host"),

	/** Option to name an Apache Zookeeper port (integer). */
	ZK_PORT				('p',	"zk-port",				"PORT",		"Apache Zookeeper port"),

	/** Option to name a timeout for trying to connect to Apache Zookeeper. */
	ZK_TIME_OUT			('t',	"zk-timeout",			"MS",		"Zookeeper connection timeout in milliseconds"),

	/** Option to name a specific path to search for in Apache Zookeeper. */
	ZK_PATH				('k',	"zk-path",				"PATH",		"Zookeeper path to write configuration to or read it from, translates to standard enum"),

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

	/** Option to name a W3C Websocket self port. */
	WS_SELF_PORT		('s',	"self-port",			"PORT",		"port number for self server"),

	/** Option to name a W3C Websocket URI for a connection. */
	WS_URI				(null,	"ws-uri",				"URI",		"URI for a wesocket connection"),

	/** Option to name a W3C Websocket URI for a connection. */
	WS_HOST				(null,	"ws-host",				"HOST",		"hostname (IP address or DNS name) for a wesocket connection"),

	/** Option to name a W3C Websocket URI for a connection. */
	WS_PORT				(null,	"ws-port",				"PORT",		"port number for a wesocket connection"),

	/** Option for a server to run in background mode. */
	SERVER_MODE			(null,	"srv-mode",				null,		"tells a server to run in background mode"),

	/** Option to activate automatic re-connections for messaging service clients. */
	DO_RECONNECT		('r',	"do-reconnect",			null,		"activates automatic reconnection for messaging service clients"),

	/** Option to name an Event DSL set to process. */
	EVENT_DSL_SET		(null,	"event-dsl-set",		"SET",		"names an Event DSL set to process (full class name)"),

	/** Option to name an Event DSL to process. */
	EVENT_DSL			(null,	"event-dsl",			"DSL",		"names an Event DSL to process (full class name)"),

	/** Option to name a target, for instance for a compilation. */
	TARGET				(null,	"target",				"TARGET",	"specifies a target, for instance for a compilation"),

	/** Option to advice a compiler to generate for ASCII-DOCTOR, not for classic ASCIIDOC. */
	ASCII_DOCTOR		(null,	"ascii-doctor",			null,		"specifies output for ASCII Doctor"),
	;

	/** Private option. */
	private Option option;

	/** Private short option if defined, null if not. */
	private Character shortOption;

	/** Private long option. */
	private String longOption;

	/**
	 * Creates a new short option with description.
	 * @param shortOption short option, i.e. a character
	 * @param description option description
	 */
	StandardOptions(Character shortOption, String description){
		this(shortOption, null, null, description);
	}

	/**
	 * Creates a new short option with an argument and description.
	 * @param shortOption short option, i.e. a character
	 * @param withArg an argument for the option
	 * @param description option description
	 */
	StandardOptions(Character shortOption, String withArg, String description){
		this(shortOption, null, withArg, description);
	}

	/**
	 * Creates a new long option with description.
	 * @param longOption long option, i.e. a full string
	 * @param description option description
	 */
	StandardOptions(String longOption, String description){
		this(null, longOption, null, description);
	}

	/**
	 * Creates a new long option with an argument and description.
	 * @param longOption long option, i.e. a full string
	 * @param withArg an argument for the option
	 * @param description option description
	 */
	StandardOptions(String longOption, String withArg, String description){
		this(null, longOption, withArg, description);
	}

	/**
	 * Creates a new option with short and long access along with argument and description
	 * @param shortOption the short option, i.e. a character
	 * @param longOption the long option, i.e. a string
	 * @param withArg option argument
	 * @param description option description
	 */
	StandardOptions(Character shortOption, String longOption, String withArg, String description){
		Option.Builder builder;
		if(shortOption!=null){
			builder = Option.builder(shortOption.toString());
		}
		else{
			builder = Option.builder();
		}

		builder.desc(description);
		if(longOption!=null){
			builder.longOpt(longOption);
		}
		if(withArg!=null){
			builder.hasArg().argName(withArg);
		}

		this.option = builder.build();

		this.shortOption = shortOption;
		this.longOption = longOption;
	}

	@Override
	public Option getOption(){
		return this.option;
	}

	@Override
	public String getOptionString(){
		String ret = null;
		if(this.shortOption!=null){
			ret = this.shortOption.toString();
		}
		else if(this.longOption!=null){
			ret = this.longOption;
		}
		return ret;
	}
}

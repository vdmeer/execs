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
 * Facory for ExecS artifacts, namely CLI options.
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.0.5 build 150623 (23-Jun-15) for Java 1.8
 * @since      v0.0.6
 */
public class ExecS_Factory {

	/**
	 * Creates a new short option with description.
	 * @param shortOption short option, i.e. a character
	 * @param description option description
	 * @return a new option object
	 */
	public static ExecS_CliOption newCliOption(Character shortOption, String description){
		return ExecS_Factory.newCliOption(shortOption, null, null, description);
	}

	/**
	 * Creates a new short option with an argument and description.
	 * @param shortOption short option, i.e. a character
	 * @param withArg an argument for the option
	 * @param description option description
	 * @return a new option object
	 */
	public static ExecS_CliOption newCliOption(Character shortOption, String withArg, String description){
		return ExecS_Factory.newCliOption(shortOption, null, withArg, description);
	}

	/**
	 * Creates a new long option with description.
	 * @param longOption long option, i.e. a full string
	 * @param description option description
	 * @return a new option object
	 */
	public static ExecS_CliOption newCliOption(String longOption, String description){
		return ExecS_Factory.newCliOption(null, longOption, null, description);
	}

	/**
	 * Creates a new long option with an argument and description.
	 * @param longOption long option, i.e. a full string
	 * @param withArg an argument for the option
	 * @param description option description
	 * @return a new option object
	 */
	public static ExecS_CliOption newCliOption(String longOption, String withArg, String description){
		return ExecS_Factory.newCliOption(null, longOption, withArg, description);
	}

	/**
	 * Creates a new option with short and long access along with argument and description
	 * @param shortOption the short option, i.e. a character
	 * @param longOption the long option, i.e. a string
	 * @param withArg option argument
	 * @param description option description
	 * @return a new option object
	 */
	public static ExecS_CliOption newCliOption(final Character shortOption, final String longOption, final String withArg, final String description){
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

		final Option option = builder.build();

		return new ExecS_CliOption() {
			@Override
			public String getOptionString() {
				String ret = null;
				if(shortOption!=null){
					ret = shortOption.toString();
				}
				else if(longOption!=null){
					ret = longOption;
				}
				return ret;
			}

			@Override
			public Option getOption() {
				return option;
			}

			@Override
			public String getDescription(){
				return description;
			}
		};
	}
}

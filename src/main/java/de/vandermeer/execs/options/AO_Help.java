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

import org.apache.commons.cli.Option;

/**
 * Application option "help", automatically added to an executable application.
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.3.0 build 160203 (03-Feb-16) for Java 1.8
 * @since      v0.2.0
 */
public class AO_Help extends AbstractApplicationOption<String> {

	/**
	 * Returns the new option.
	 */
	public AO_Help(){
		super("application and argument help", "Without argument, help will print a usage and help screen with all CLI arguments and further information for the application. With an argument, help will print specific help information for the given CLI argument if applicable to the application.");

		Option.Builder builder = Option.builder("?");
		builder.longOpt("help");
		builder.hasArg().argName("ARG").optionalArg(true);
		builder.required(false);
		this.setCliOption(builder.build());
	}

	/**
	 * Returns the server mode settings.
	 * @return true of server mode is set, false otherwise
	 */
	public boolean inServerMode(){
		return this.inCli;
	}

	@Override
	public String convertValue(Object value) {
		if(value==null){
			return null;
		}
		return value.toString();
	}

}

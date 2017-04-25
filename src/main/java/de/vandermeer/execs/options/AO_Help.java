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

/**
 * Application option `help`, automatically added to an executable application.
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.4.0 build 170413 (13-Apr-17) for Java 1.8
 * @since      v0.2.0
 */
public class AO_Help extends AbstractApplicationOption<String> {

	/**
	 * Returns the new option.
	 * @param useShort true for using the short option `-h`, false for no short option
	 */
	public AO_Help(boolean useShort){
		super("application and argument help", "Without argument, help will print a usage and help screen with all CLI arguments and further information for the application. With an argument, help will print specific help information for the given CLI argument if applicable to the application.");
		this.setCliArgument((useShort)?'h':null, "help", "ARG", false);
	}

	/**
	 * Returns the help settings.
	 * @return true if help is set, false otherwise
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

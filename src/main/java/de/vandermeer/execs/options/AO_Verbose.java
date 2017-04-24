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
 * Application option "verbose", activate extended progress messages.
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.4.0 build 170413 (13-Apr-17) for Java 1.8
 * @since      v0.3.1
 */
public class AO_Verbose extends AbstractApplicationOption<String> {

	/**
	 * Returns the new option.
	 */
	public AO_Verbose(){
		this(null);
	}

	/**
	 * Returns the new option.
	 * @param shortOption character for sort version of the option
	 */
	public AO_Verbose(Character shortOption){
		super("verbose mode for application", "Sets an application verbose mode, meaning the application will printout extended progress messages.");
		this.setCliArgument(shortOption, "verbose", null, false);
	}

	/**
	 * Returns the verbose mode settings.
	 * @return true if verbose mode is set, false otherwise
	 */
	public boolean inVerboseMode(){
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

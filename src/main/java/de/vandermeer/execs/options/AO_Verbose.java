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
 * Application option "verbose", activate extended progress messages.
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.3.6 build 170331 (31-Mar-17) for Java 1.8
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

		Option.Builder builder = (shortOption==null)?Option.builder():Option.builder(shortOption.toString());
		builder.longOpt("verbose");
		builder.required(false);
		this.setCliOption(builder.build());
	}

	/**
	 * Returns the verbose mode settings.
	 * @return true of verbose mode is set, false otherwise
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

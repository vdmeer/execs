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
 * Application option "target".
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.3.5-SNAPSHOT build 160303 (03-Mar-16) for Java 1.8
 * @since      v0.2.0
 */
public class AO_Target extends AbstractApplicationOption<String> {

	/**
	 * Returns the new option.
	 * @param required true if option is required, false of it is optional
	 * @param longDescription option long description
	 * @throws NullPointerException - if description parameter is null
	 * @throws IllegalArgumentException - if description parameter is empty
	 */
	public AO_Target(boolean required, String longDescription){
		this(required, null, longDescription);
	}

	/**
	 * Returns the new option.
	 * @param required true if option is required, false of it is optional
	 * @param shortOption character for sort version of the option
	 * @param longDescription option long description
	 * @throws NullPointerException - if description parameter is null
	 * @throws IllegalArgumentException - if description parameter is empty
	 */
	public AO_Target(boolean required, Character shortOption, String longDescription){
		super("specifies a target, for instance for a compilation", longDescription);

		Option.Builder builder = (shortOption==null)?Option.builder():Option.builder(shortOption.toString());
		builder.longOpt("target");
		builder.hasArg().argName("TARGET");
		builder.required(required);
		this.setCliOption(builder.build());
	}

	@Override
	public String convertValue(Object value) {
		if(value==null){
			return null;
		}
		return value.toString();
	}

}

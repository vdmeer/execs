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
 * Application option "property-file".
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.3.8 build 170405 (05-Apr-17) for Java 1.8
 * @since      v0.2.0
 */
public class AO_PropertyFile extends AbstractApplicationOption<String> {

	/**
	 * Returns the new option.
	 * @param required true if option is required, false of it is optional
	 * @param defaultValue option default value
	 * @param longDescription option long description
	 * @throws NullPointerException - if description parameter is null
	 * @throws IllegalArgumentException - if description parameter is empty
	 */
	public AO_PropertyFile(boolean required, String defaultValue, String longDescription){
		this(required, defaultValue, null, longDescription);
	}

	/**
	 * Returns the new option.
	 * @param required true if option is required, false of it is optional
	 * @param defaultValue option default value
	 * @param shortOption character for sort version of the option
	 * @param longDescription option long description
	 * @throws NullPointerException - if description parameter is null
	 * @throws IllegalArgumentException - if description parameter is empty
	 */
	public AO_PropertyFile(boolean required, String defaultValue, Character shortOption, String longDescription){
		super(defaultValue, "a property file with configuration", longDescription);

		Option.Builder builder = (shortOption==null)?Option.builder():Option.builder(shortOption.toString());
		builder.longOpt("property-file");
		builder.hasArg().argName("FILE");
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

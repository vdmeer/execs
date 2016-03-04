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
 * Application option "classmap-file".
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.3.6-SNAPSHOT build 160304 (04-Mar-16) for Java 1.8
 * @since      v0.2.0
 */
public class AO_ClassmapFile extends AbstractApplicationOption<String> {

	/**
	 * Returns the new option.
	 * @param required true if option is required, false of it is optional
	 * @param optionKey option key
	 * @param longDescription option long description
	 * @throws NullPointerException - if optioneKey or description parameter is null
	 * @throws IllegalArgumentException - if optionKey or description parameter is empty
	 */
	public AO_ClassmapFile(boolean required, String optionKey, String longDescription){
		this(required, optionKey, null, longDescription);
	}

	/**
	 * Returns the new option.
	 * @param required true if option is required, false of it is optional
	 * @param optionKey option key
	 * @param shortOption character for sort version of the option
	 * @param longDescription option long description
	 * @throws NullPointerException - if optioneKey or description parameter is null
	 * @throws IllegalArgumentException - if optionKey or description parameter is empty
	 */
	public AO_ClassmapFile(boolean required, String optionKey, Character shortOption, String longDescription){
		super(optionKey, null, "a property file with class names (executable applications) mapped to script names", longDescription);

		Option.Builder builder = (shortOption==null)?Option.builder():Option.builder(shortOption.toString());
		builder.longOpt("classmap-file");
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

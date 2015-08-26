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
 * Application option "output-directory".
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.2.0 build 150826 (26-Aug-15) for Java 1.8
 * @since      v0.2.0
 */
public class AO_DirectoryOut extends AbstractApplicationOption<String> {

	/**
	 * Returns the new option.
	 * @param required true if option is required, false of it is optional
	 * @param longDescription option long description
	 * @throws NullPointerException - if description parameter is null
	 * @throws IllegalArgumentException - if description parameter is empty
	 */
	public AO_DirectoryOut(boolean required, String longDescription){
		super("output directory", longDescription);

		Option.Builder builder = Option.builder();
		builder.longOpt("output-directory");
		builder.hasArg().argName("DIR");
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

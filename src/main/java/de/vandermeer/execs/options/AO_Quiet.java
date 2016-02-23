/* Copyright 2016 Sven van der Meer <vdmeer.sven@mykolab.com>
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
 * Application option "quiet".
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.3.3 build 160203 (03-Feb-16) for Java 1.8
 * @since      v0.3.3
 */
public class AO_Quiet extends AbstractApplicationOption<String> {

	/**
	 * Returns the new option.
	 * @param longDescription option long description
	 * @throws NullPointerException - if description parameter is null
	 * @throws IllegalArgumentException - if description parameter is empty
	 */
	public AO_Quiet(String longDescription){
		super("puts the application in quiet mode, no progres or error messages will be printed", longDescription);

		Option.Builder builder = Option.builder("q");
		builder.longOpt("quiet");
		builder.required(false);
		this.setCliOption(builder.build());
	}

	/**
	 * Returns the quiet flag setting.
	 * @return true of quiet flag is set, false otherwise
	 */
	public boolean isQuiet(){
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

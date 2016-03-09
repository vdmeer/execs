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
 * Application option "print-stack-trace".
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.3.6 build 160306 (06-Mar-16) for Java 1.8
 * @since      v0.3.4
 */
public class AO_PrintStackTrace extends AbstractApplicationOption<String> {

	/**
	 * Returns the new option.
	 * @param longDescription option long description
	 * @throws NullPointerException - if description parameter is null
	 * @throws IllegalArgumentException - if description parameter is empty
	 */
	public AO_PrintStackTrace(String longDescription){
		super("sets a flag to print the stack trace of exceptions", longDescription);

		Option.Builder builder = Option.builder();
		builder.longOpt("print-stack-trace");
		builder.required(false);
		this.setCliOption(builder.build());
	}

	/**
	 * Returns the print stack trace flag setting.
	 * @return true if stack trace should be printed, false otherwise
	 */
	public boolean shouldPrintStackTrace(){
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

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

package de.vandermeer.execs.cli;

import org.apache.commons.cli.Option;

/**
 * CLI option "property-file".
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.1.0 build 150812 (12-Aug-15) for Java 1.8
 * @since      v0.1.1 (was in StandardOptions before)
 */
public class CliOption_PropertyFile extends AbstractClioption {

	public CliOption_PropertyFile(boolean required){
		Option.Builder builder = Option.builder();
		builder.longOpt("property-file");
		builder.hasArg().argName("FILE");
		builder.desc("a property file with configuration");
		builder.required(required);

		this.option = builder.build();
	}

}

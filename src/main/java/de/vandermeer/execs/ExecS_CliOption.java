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

package de.vandermeer.execs;

import org.apache.commons.cli.Option;

/**
 * Option for the ExecS CLI parser.
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.0.6 build 150701 (01-Jul-15) for Java 1.8
 */
public interface ExecS_CliOption {
	/**
	 * Returns the created option.
	 * @return the created option
	 */
	public Option getOption();

	/**
	 * Returns the options a string.
	 * @return option as string using the short option if not null, long option otherwise
	 */
	public String getOptionString();

	/**
	 * Returns a description for the option.
	 * @return description for the option, should not be null
	 */
	public String getDescription();
}

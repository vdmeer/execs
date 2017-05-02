/* Copyright 2017 Sven van der Meer <vdmeer.sven@mykolab.com>
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

package de.vandermeer.execs.options.simple;

import de.vandermeer.execs.options.AbstractSimpleC;

/**
 * Simple CLI option `help`.
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.4.0 build 170413 (13-Apr-17) for Java 1.8
 * @since      v0.5.0
 */
public class AO_HelpSimple extends AbstractSimpleC {

	/**
	 * Creates a new option.
	 * @param cliShort the short CLI command, null if not required
	 * ]@param longDescription a long description for the option, null or objects resulting in a blank string will be ignored
	 */
	public AO_HelpSimple(Character cliShort, Object longDescription) {
		super(
				"Help",
				cliShort,
				"help",
				false,
				"prints a help screen with usage information",
				longDescription
		);
	}

}

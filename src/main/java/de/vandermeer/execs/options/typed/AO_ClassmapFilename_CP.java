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

package de.vandermeer.execs.options.typed;

import de.vandermeer.execs.options.AbstractTypedCP_String;

/**
 * Typed CLI / property option `classmap-file`.
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.4.0 build 170413 (13-Apr-17) for Java 1.8
 * @since      v0.5.0
 */
public class AO_ClassmapFilename_CP extends AbstractTypedCP_String {

	/**
	 * Creates a new option
	 * @param cliShort the short CLI command, null if not required
	 * @param propertyKey the property key for the option, must not be blank
	 */
	public AO_ClassmapFilename_CP(Character cliShort, String propertyKey) {
		super(
				cliShort, "classmap-file", false,
				"FILE", false, "a filename, file must exist as plain text file using propery syntax",
				propertyKey,
				"a property file with class names (executable applications) mapped to script names"
		);

		this.setLongDescription("The class map file contains mappings from a class name to a script name. This mapping is used to generate run scripts for applications that are not registered with an executor, or if automated generation (for registered applications) is not required or wanted.");
	}

}

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

package de.vandermeer.execs.options;

import org.apache.commons.lang3.StringUtils;

import de.vandermeer.skb.interfaces.messages.errors.IsError;
import de.vandermeer.skb.interfaces.messages.errors.Templates_CliOptions;

/**
 * Abstract implementation of a CLI option of type character.
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.4.0 build 170413 (13-Apr-17) for Java 1.8
 * @since      v0.5.0
 */
public class Option_TypedC_Character extends Abstract_TypedC<Character> {

	/**
	 * Creates a new option.
	 * @param displayName he display name of the option, must not be blank
	 * @param cliShort the short CLI command, null if not required
	 * @param cliLong the long CLI command, null if not required
	 * @param cliIsRequired flag for CLI option being required or not
	 * @param argName the name of the argument, must not be blank
	 * @param argIsOptional flag for the argument being optional
	 * @param argDescr a short argument description, must not be blank
	 * @param description a short description for the option, must not be blank
	 * @param longDescription a long description for the option, null or objects resulting in a blank string will be ignored
	 */
	public Option_TypedC_Character(final String displayName, final Character cliShort, final String cliLong, final boolean cliIsRequired, final String argName, final boolean argIsOptional, final String argDescr, final String description, final Object longDescription){
		super(displayName, cliShort, cliLong, cliIsRequired, argName, argIsOptional, argDescr, description, longDescription);
	}

	@Override
	public IsError setCliValue(final Object value) {
		if(!this.cliArgIsOptional() && value==null){
			return Templates_CliOptions.MANDATORY_ARG_NULL.getError("CLI Option", this.getCliArgumentName(), this.getCliLong());
		}
		//TODO need some more type errors here, e.g. arg was not a character
		if(value!=null){
			final String ret = value.toString();
			if(!StringUtils.isBlank(ret)){
				this.cliValue = ret.charAt(0);
			}
		}
		return null;
	}

}
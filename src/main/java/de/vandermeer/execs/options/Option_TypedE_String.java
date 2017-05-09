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

import de.vandermeer.skb.interfaces.messagesets.errors.IsError;
import de.vandermeer.skb.interfaces.messagesets.errors.Templates_EnvironmentOptions;

/**
 * Abstract implementation of an environment option of type string.
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.4.0 build 170413 (13-Apr-17) for Java 1.8
 * @since      v0.5.0
 */
public class Option_TypedE_String extends Abstract_TypedE<String> {

	/**
	 * Creates a new option.
	 * @param displayName the display name of the option, must not be blank
	 * @param environmentKey the environment key
	 * @param environmentIsRequired flag for environment option being required or not
	 * @param description a short description for the option, must not be blank
	 * @param longDescription a long description for the option, null or objects resulting in a blank string will be ignored
	 */
	public Option_TypedE_String(final String displayName, final String environmentKey, final boolean environmentIsRequired, final String description, final Object longDescription) {
		super(displayName, environmentKey, environmentIsRequired, description, longDescription);
	}

	@Override
	public IsError setEnvironmentValue(String value) {
		if(!this.environmentIsRequired() && value==null){
			return Templates_EnvironmentOptions.VALUE_REQUIRED_BLANK.getError(this.displayName, this.environmentKey);
		}
		this.environmentValue = value;
		return null;
	}

}

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

import de.vandermeer.skb.interfaces.application.ApoBaseE;

/**
 * Base for an environment option.
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.4.0 build 170413 (13-Apr-17) for Java 1.8
 * @since      v0.5.0
 */
public abstract class AbstractApoBaseE extends AbstractApoBase implements ApoBaseE {

	/** Flag for option found in environment. */
	protected boolean inEnv = false;

	/** The environment key. */
	protected final String environmentKey;

	/**
	 * Creates a new option
	 * @param displayName the display name of the option, must not be blank
	 * @param environmentKey the environment key, must not be blank
	 * @param description a short description for the option, must not be blank
	 * @param longDescription a long description for the option, null or objects resulting in a blank string will be ignored
	 */
	protected AbstractApoBaseE(String displayName, String environmentKey, String description, Object longDescription) {
		super(displayName, description, longDescription);
		this.environmentKey = environmentKey;
	}

	@Override
	public boolean inEnvironment() {
		return this.inEnv;
	}

	@Override
	public String getEnvironmentKey() {
		return this.environmentKey;
	}

}

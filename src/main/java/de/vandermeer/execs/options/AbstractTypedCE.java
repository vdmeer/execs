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

import de.vandermeer.skb.interfaces.application.Apo_TypedCE;

/**
 * Abstract implementation of a typed CLI / environment option.
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.4.0 build 170413 (13-Apr-17) for Java 1.8
 * @since      v0.5.0
 */
public abstract class AbstractTypedCE<T> extends AbstractTypedC<T> implements Apo_TypedCE<T> {

	/** Flag for option found in environment. */
	protected boolean inEnv = false;

	/** The environment key. */
	protected final String environmentKey;

	/** The value read from the environment. */
	protected T environmentValue;

	/**
	 * Creates a new option.
	 * @param cliShort the short CLI command, null if not required
	 * @param cliLong the long CLI command, null if not required
	 * @param isRequired flag for CLI option being required or not
	 * @param argName the name of the argument, must not be blank
	 * @param argIsOptional flag for the argument being optional
	 * @param argDescr a short argument description, must not be blank
	 * @param environmentKey a key for the option in the environment, must not be blank
	 * @param description a short description for the option
	 */
	protected AbstractTypedCE(Character cliShort, String cliLong, boolean isRequired, String argName, boolean argIsOptional, String argDescr, String environmentKey, String description) {
		super(cliShort, cliLong, isRequired, argName, argIsOptional, argDescr, description);
		this.environmentKey = environmentKey;
	}

	@Override
	public void validate() throws IllegalStateException {
		Apo_TypedCE.super.validate();
	}

	@Override
	public boolean inEnvironment() {
		return this.inEnv;
	}

	@Override
	public String getEnvironmentKey() {
		return this.environmentKey;
	}

	@Override
	public T getEnvironmentValue() {
		return this.environmentValue;
	}

}

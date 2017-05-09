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

import de.vandermeer.skb.interfaces.application.Apo_TypedCP;

/**
 * Abstract implementation of a typed CLI / property option.
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.4.0 build 170413 (13-Apr-17) for Java 1.8
 * @since      v0.5.0
 */
public abstract class Abstract_TypedCP<T> extends Abstract_TypedC<T> implements Apo_TypedCP<T> {

	/** Flag for option found in properties. */
	protected transient boolean isInProps;

	/** The property key. */
	protected final transient String propertyKey;

	/** Flag for required property options. */
	protected final transient Boolean propertyIsRequired;

	/** The value set from a property. */
	protected transient T propertyValue;

	/**
	 * Creates a new option.
	 * @param displayName the display name of the option, must not be blank
	 * @param cliShort the short CLI command, null if not required
	 * @param cliLong the long CLI command, null if not required
	 * @param cliIsRequired flag for CLI option being required or not
	 * @param argName the name of the argument, must not be blank
	 * @param argIsOptional flag for the argument being optional
	 * @param argDescr a short argument description, must not be blank
	 * @param propertyKey a key for the option in properties, must not be blank
	 * @param propertyIsRequired flag for property option being required or not
	 * @param description a short description for the option, must not be blank
	 * @param longDescription a long description for the option, null or objects resulting in a blank string will be ignored
	 */
	protected Abstract_TypedCP(final String displayName, final Character cliShort, final String cliLong, final boolean cliIsRequired, final String argName, final boolean argIsOptional, final String argDescr, final String propertyKey, final boolean propertyIsRequired, final String description, final Object longDescription) {
		super(displayName, cliShort, cliLong, cliIsRequired, argName, argIsOptional, argDescr, description, longDescription);
		this.propertyKey = propertyKey;
		this.propertyIsRequired = propertyIsRequired;
	}

	@Override
	public String getPropertyKey() {
		return this.propertyKey;
	}

	@Override
	public T getPropertyValue() {
		return this.propertyValue;
	}

	@Override
	public boolean inProperties() {
		return this.isInProps;
	}

	@Override
	public void validate() throws IllegalStateException {
		Apo_TypedCP.super.validate();
	}

	@Override
	public boolean propertyIsRequired() {
		return this.propertyIsRequired;
	}

	@Override
	public void setInProperties(boolean inProp){
		this.isInProps = inProp;
	}

}

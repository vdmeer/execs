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

import de.vandermeer.skb.interfaces.application.Apo_TypedC;

/**
 * Abstract implementation of a typed CLI option.
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.4.0 build 170413 (13-Apr-17) for Java 1.8
 * @since      v0.5.0
 */
public abstract class Abstract_TypedC<T> extends AbstractApoBaseC implements Apo_TypedC<T> {

	/** Name of the option argument. */
	protected final transient String argName;

	/** Short description of the option argument. */
	protected final transient String argDescr;

	/** Flag for the argument being optional. */
	protected final transient Boolean argIsOptional;

	/** Value read from CLI. */
	protected transient T cliValue;

	/** A default value. */
	protected transient T defaultValue;

	/**
	 * Creates a new option.
	 * @param displayName the display name of the option, must not be blank
	 * @param cliShort the short CLI command, null if not required
	 * @param cliLong the long CLI command, null if not required
	 * @param isRequired flag for CLI option being required or not
	 * @param argName the name of the argument, must not be blank
	 * @param argIsOptional flag for the argument being optional
	 * @param argDescr a short argument description, must not be blank
	 * @param description a short description for the option, must not be blank
	 * @param longDescription a long description for the option, null or objects resulting in a blank string will be ignored
	 */
	protected Abstract_TypedC(final String displayName, final Character cliShort, final String cliLong, final boolean isRequired, final String argName, final boolean argIsOptional, final String argDescr, final String description, final Object longDescription){
		super(displayName, cliShort, cliLong, isRequired, description, longDescription);
		this.argName = argName;
		this.argDescr = argDescr;
		this.argIsOptional = argIsOptional;

		Apo_TypedC.super.validate();
	}

	@Override
	public boolean cliArgIsOptional(){
		return this.argIsOptional;
	}

	@Override
	public String getCliArgumentDescription() {
		return this.argDescr;
	}

	@Override
	public String getCliArgumentName() {
		return this.argName;
	}

	@Override
	public T getCliValue() {
		return this.cliValue;
	}

	@Override
	public T getDefaultValue() {
		return this.defaultValue;
	}

	public void setDefaultValue(final T value) {
		this.defaultValue = value;
	}

}
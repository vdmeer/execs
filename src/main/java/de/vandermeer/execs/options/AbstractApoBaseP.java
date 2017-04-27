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

import de.vandermeer.skb.interfaces.application.ApoBaseP;

/**
 * Base for a property option.
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.4.0 build 170413 (13-Apr-17) for Java 1.8
 * @since      v0.5.0
 */
public abstract class AbstractApoBaseP extends AbstractApoBase implements ApoBaseP {

	/** Flag for option found in properties. */
	protected boolean inProps = false;

	/** The property key. */
	protected final String propertyKey;

	/**
	 * Creates a new option
	 * @param propertyKey the property key
	 * @param description a short description for the option
	 */
	protected AbstractApoBaseP(String propertyKey, String description) {
		super(description);
		this.propertyKey = propertyKey;
	}

	@Override
	public boolean inProperties() {
		return this.inProps;
	}

	@Override
	public String getPropertyKey() {
		return this.propertyKey;
	}

}

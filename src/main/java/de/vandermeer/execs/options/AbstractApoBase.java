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

import de.vandermeer.skb.interfaces.application.ApoBase;

/**
 * Base for an application option.
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.4.0 build 170413 (13-Apr-17) for Java 1.8
 * @since      v0.5.0
 */
public abstract class AbstractApoBase implements ApoBase {

	/** Simple, 1-line description. */
	protected final transient String descr;

	/** Long description. */
	protected final transient Object longDescr;

	/** Display name of the option. */
	protected final transient String displayName;

	/**
	 * Creates a new base object.
	 * @param displayName the option display name, must not be blank
	 * @param description the option short description, should be 1 line, must not be blank
	 * @param longDescription a long description for the option, null or objects resulting in a blank string will be ignored
	 */
	protected AbstractApoBase(final String displayName, final String description, final Object longDescription){
		this.descr = description;
		this.displayName = displayName;
		this.longDescr = longDescription;
	}

	@Override
	public String getDescription() {
		return this.descr;
	}

	@Override
	public String getDisplayName() {
		return this.displayName;
	}

	@Override
	public Object getLongDescription() {
		return this.longDescr;
	}

}

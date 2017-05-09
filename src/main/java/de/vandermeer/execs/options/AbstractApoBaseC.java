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

import de.vandermeer.skb.interfaces.application.ApoBaseC;

/**
 * Base for a CLI option.
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.4.0 build 170413 (13-Apr-17) for Java 1.8
 * @since      v0.5.0
 */
public abstract class AbstractApoBaseC extends AbstractApoBase implements ApoBaseC {

	/** Flag for option being found in the CLI arguments. */
	protected transient boolean isInCli;

	/** The short CLI command. */
	protected final transient Character cliShort;

	/** The long CLI command. */
	protected final transient String cliLong;

	/** Flag for required CLI options. */
	protected final transient Boolean cliIsRequired;

	/**
	 * Creates a new option.
	 * @param displayName the display name of the option, must not be blank
	 * @param cliShort the short CLI command, null if not required
	 * @param cliLong the long CLI command, null if not required
	 * @param cliIsRequired flag for CLI option being required or not
	 * @param description a short description for the option, must not be blank
	 * @param longDescription a long description for the option, null or objects resulting in a blank string will be ignored
	 */
	protected AbstractApoBaseC(final String displayName, final Character cliShort, final String cliLong, final boolean cliIsRequired, final String description, final Object longDescription) {
		super(displayName, description, longDescription);
		this.cliShort = cliShort;
		this.cliLong = cliLong;
		this.cliIsRequired = cliIsRequired;
	}

	@Override
	public boolean cliIsRequired() {
		return this.cliIsRequired;
	}

	@Override
	public String getCliLong() {
		return cliLong;
	}

	@Override
	public Character getCliShort() {
		return cliShort;
	}

	@Override
	public boolean inCli() {
		return this.isInCli;
	}

	@Override
	public void setInCLi(final boolean isInCli) {
		this.isInCli = isInCli;
	}
}

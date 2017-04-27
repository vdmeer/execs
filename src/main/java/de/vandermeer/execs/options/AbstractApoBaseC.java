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
	protected boolean inCli = false;

	/** The short CLI command. */
	protected final Character cliShort;

	/** The long CLI command. */
	protected final String cliLong;

	/** Flag for required CLI options. */
	protected final Boolean isRequired;

	/**
	 * Creates a new option.
	 * @param cliShort the short CLI command, null if not required
	 * @param cliLong the long CLI command, null if not required
	 * @param isRequired flag for CLI option being required or not
	 * @param description a short description for the option
	 */
	protected AbstractApoBaseC(Character cliShort, String cliLong, boolean isRequired, String description) {
		super(description);
		this.cliShort = cliShort;
		this.cliLong = cliLong;
		this.isRequired = isRequired;
	}

	@Override
	public boolean inCli() {
		return this.inCli;
	}

	@Override
	public void setInCLi(boolean inCli) {
		this.inCli = inCli;
	}

	@Override
	public Character getCliShort() {
		return cliShort;
	}

	@Override
	public String getCliLong() {
		return cliLong;
	}

	@Override
	public boolean cliIsRequired() {
		return this.isRequired;
	}

}

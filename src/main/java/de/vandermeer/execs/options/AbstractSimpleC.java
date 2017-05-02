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

import de.vandermeer.skb.interfaces.application.Apo_SimpleC;

/**
 * Abstract implementation of a simple CLI option.
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.4.0 build 170413 (13-Apr-17) for Java 1.8
 * @since      v0.5.0
 */
public class AbstractSimpleC extends AbstractApoBaseC implements Apo_SimpleC {

	/**
	 * Creates a new option.
	 * @param displayName the display name of the option, must not be blank
	 * @param cliShort the short CLI command, null if not required
	 * @param cliLong the long CLI command, null if not required
	 * @param isRequired flag for CLI option being required or not
	 * @param description a short description for the option, must not be blank
	 * @param longDescription a long description for the option, null or objects resulting in a blank string will be ignored
	 */
	public AbstractSimpleC(String displayName, Character cliShort, String cliLong, boolean isRequired, String description, Object longDescription){
		super(displayName, cliShort, cliLong, isRequired, description, longDescription);
		this.validate();
	}

//	@Override
//	public String toString(){
//		StrBuilder ret = new StrBuilder();
//
//		ret.append("cli short : ").append(this.getCliShort()).appendNewLine();
//		ret.append("cli long  : ").append(this.getCliLong()).appendNewLine();
//		ret.append("cli sh/lo : ").append(this.getCliShortLong()).appendNewLine();
//		ret.append("required  : ").append(this.cliIsRequired()).appendNewLine();
//		ret.append("descr     : ").append(this.getDescription()).appendNewLine();
////		ret.append("descr long: ").append(this.getLongDescription()).appendNewLine();
//
//		return ret.toString();
//	}
}

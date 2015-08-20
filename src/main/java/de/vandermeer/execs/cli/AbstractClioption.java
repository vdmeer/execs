/* Copyright 2014 Sven van der Meer <vdmeer.sven@mykolab.com>
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

package de.vandermeer.execs.cli;

import org.apache.commons.cli.Option;

/**
 * Abstract CLI option implementation.
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.1.0 build 150812 (12-Aug-15) for Java 1.8
 * @since      v0.1.1
 */
public class AbstractClioption implements ExecS_CliOption {

	/** A generated CLI option. */
	protected Option option;

	/**
	 * Creates a new option with no option set.
	 */
	public AbstractClioption(){
		this(null);
	}

	/**
	 * Creates a new option.
	 * @param option the CLI option
	 */
	public AbstractClioption(Option option){
		this.option = option;
	}

	@Override
	public Option getOption() {
		return this.option;
	}

}

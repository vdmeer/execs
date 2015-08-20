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
 * Option for the ExecS CLI parser.
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.1.0 build 150812 (12-Aug-15) for Java 1.8
 * @since      v0.0.1
 */
public interface ExecS_CliOption {

	/**
	 * Returns the created option.
	 * @return the created option
	 */
	Option getOption();

	/**
	 * Returns the options a string.
	 * @return option as string using the short option if not null, long option otherwise
	 */
	default String getOptionAsString(){
		String ret = null;
		if(this.getOption()==null){
			return ret;
		}

		if(this.getOption().getOpt()!=null){
			ret = this.getOption().getOpt();
		}
		else if(this.getOption().getLongOpt()!=null){
			ret = this.getOption().getLongOpt();
		}
		return ret;
	}

	/**
	 * Returns a description for the option.
	 * @return description for the option, should not be null
	 */
	default String getDescription(){
		return (this.getOption()==null)?null:this.getOption().getDescription();
	}

}

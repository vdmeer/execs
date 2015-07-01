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

package de.vandermeer.execs;

/**
 * Interface for a service that can be executed.
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.0.6 build 150701 (01-Jul-15) for Java 1.8
 */
public interface ExecutableService {

	/**
	 * Executes the service.
	 * @param args arguments for execution
	 * @return 0 on success, error code otherwise
	 */
	int executeService(String[] args);

	/**
	 * Prints a help screen for the service, to be used by an executing component.
	 */
	default void serviceHelpScreen(){
		if(this.getCli()!=null){
			this.getCli().usage(this.getName());
		}
	}

	/**
	 * Returns the service's CLI parser.
	 * @return CLI parser
	 */
	default ExecS_Cli getCli(){
		return null;
	}

	/**
	 * Returns the name of the service.
	 * @return service name
	 */
	String getName();
}

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Executable service to generate a rebase shell script.
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.0.6-SNAPSHOT build 150630 (30-Jun-15) for Java 1.8
 * @since      v0.0.6
 */
public class Gen_RebaseSh implements ExecutableService {

	/** Service name. */
	public final static String SERVICE_NAME = "gen-rebase";

	@Override
	public int executeService(String[] args) {
		String fn = "/de/vandermeer/execs/bin/rebase.sh";
		try {
			InputStream in = getClass().getResourceAsStream(fn);
			BufferedReader input = new BufferedReader(new InputStreamReader(in));
			String line;
			while((line=input.readLine())!=null){
				System.out.println(line);
			}
		}
		catch(NullPointerException ne){
			System.err.println(this.getName() + ": exception while reading shell script from resource <" + fn + ">: "+ ne.getMessage());
		}
		catch (IOException e) {
			System.err.println(this.getName() + ": IO exception while reading shell script: "+ e.getMessage());
		}
		return 0;
	}

	@Override
	public String getName() {
		return Gen_RebaseSh.SERVICE_NAME;
	}

	@Override
	public void serviceHelpScreen() {
		System.out.println("Generates a rebase shell script, printed to standard out.");
		System.out.println();
	}
}

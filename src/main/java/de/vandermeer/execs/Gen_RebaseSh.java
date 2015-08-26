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

import de.vandermeer.execs.options.ApplicationOption;

/**
 * Application to generate a rebase shell script.
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.2.0 build 150826 (26-Aug-15) for Java 1.8
 * @since      v0.0.6
 */
public class Gen_RebaseSh implements ExecS_Application {

	/** Application name. */
	public final static String APP_NAME = "gen-rebase";

	/** Application display name. */
	public final static String APP_DISPLAY_NAME = "Generate Rebase.SH";

	/** Application version, should be same as the version in the class JavaDoc. */
	public final static String APP_VERSION = "v0.2.0 build 150826 (26-Aug-15) for Java 1.8";

	@Override
	public int executeApplication(String[] args) {
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
			System.err.println(this.getAppName() + ": exception while reading shell script from resource <" + fn + ">: " + ne.getMessage());
		}
		catch (IOException e) {
			System.err.println(this.getAppName() + ": IO exception while reading shell script: " + e.getMessage());
		}
		return 0;
	}

	@Override
	public String getAppDescription() {
		return "Generates a rebase shell script, printed to standard out.";
	}

	@Override
	public ApplicationOption<?>[] getAppOptions() {
		return null;
	}

	@Override
	public String getAppName() {
		return APP_NAME;
	}

	@Override
	public String getAppDisplayName(){
		return APP_DISPLAY_NAME;
	}

	@Override
	public String getAppVersion() {
		return APP_VERSION;
	}

}

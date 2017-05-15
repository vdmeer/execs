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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests for {@link Gen_RunScripts}.
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.4.0 build 170413 (13-Apr-17) for Java 1.8
 * @since      v0.0.2
 */
public class Test_Gen_RunScriptsSh {

	@Test
	public void testCliHelp(){
		ExecS execs = new ExecS();
		int run;

//		run = execs.execute(new String[]{Gen_RunScripts.class.getName(), "-h"});
//		assertEquals(1, run);
//
//		run = execs.execute(new String[]{Gen_RunScripts.class.getName(), "--help"});
//		assertEquals(1, run);
//
//		run = execs.execute(new String[]{Gen_RunScripts.class.getName(), "-h", "lib-dir"});
//		assertEquals(1, run);
//
//		run = execs.execute(new String[]{Gen_RunScripts.class.getName(), "--help", "lib-dir"});
//		assertEquals(1, run);
//
//		run = execs.execute(new String[]{Gen_RunScripts.class.getName(), "--help", "lib-dir", "-i"});
//		assertEquals(1, run);
//
//		run = execs.execute(new String[]{Gen_RunScripts.class.getName(), "--version"});
//		assertEquals(1, run);
//
//		run = execs.execute(new String[]{Gen_RunScripts.class.getName()});
//		assertEquals(-143, run);
//
//		run = execs.execute(new String[]{
//				Gen_RunScripts.class.getName(),
//				"--property-file", "prop",
//		});
//		assertEquals(-143, run);

		run = execs.execute(new String[]{
				Gen_RunScripts.class.getName(),
				"--application-dir", "target",
				"--property-file", "prop",
				"--target", "target"
		});
		assertEquals(-216, run);
	}

	@Test
	public void test_PropFile(){
		Gen_RunScripts gen = new Gen_RunScripts();
		gen.executeApplication(new String[]{
				"--application-dir", "target",
				"--property-file", " ",
				"--target", "sh"
		});
	}
}

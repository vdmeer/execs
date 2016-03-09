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
 * Tests for {@link Gen_ConfigureSh}.
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.3.6 build 160306 (06-Mar-16) for Java 1.8
 * @since      v0.0.2
 */
public class Test_Gen_ConfigureSh {

	@Test
	public void testCliHelp(){
		ExecS execs = new ExecS();
		int run;

		run = execs.execute(new String[]{Gen_ConfigureSh.class.getName(), "-?"});
		assertEquals(1, run);

		run = execs.execute(new String[]{Gen_ConfigureSh.class.getName(), "--help"});
		assertEquals(1, run);

		run = execs.execute(new String[]{Gen_ConfigureSh.class.getName(), "-?", "lib-dir"});
		assertEquals(1, run);

		run = execs.execute(new String[]{Gen_ConfigureSh.class.getName(), "--help", "lib-dir"});
		assertEquals(1, run);

		run = execs.execute(new String[]{Gen_ConfigureSh.class.getName(), "--help", "lib-dir", "-i"});
		assertEquals(-1, run);

		run = execs.execute(new String[]{Gen_ConfigureSh.class.getName(), "--version"});
		assertEquals(1, run);

		run = execs.execute(new String[]{Gen_ConfigureSh.class.getName()});
		assertEquals(-1, run);

		run = execs.execute(new String[]{
				Gen_ConfigureSh.class.getName(),
				"--property-file", "prop",
		});
		assertEquals(-1, run);

		run = execs.execute(new String[]{
				Gen_ConfigureSh.class.getName(),
				"--lib-dir", "lib",
				"--property-file", "prop",
		});
		assertEquals(-1, run);
	}

}

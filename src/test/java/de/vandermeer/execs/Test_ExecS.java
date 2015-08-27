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

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests for {@link ExecS}.
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.2.0 build 150827 (27-Aug-15) for Java 1.8
 * @since      v0.0.2
 */
public class Test_ExecS {

	@Test
	public void testCliAppName(){
		ExecS execs = new ExecS();
		assertEquals("execs", execs.getAppName());

		execs = new ExecS("blafoo");
		assertEquals("blafoo", execs.getAppName());
	}

	@Test
	public void testCliHelp(){
		ExecS execs = new ExecS();
		int run = execs.execute(new String[]{"-?"});
		assertEquals(0, run);
		run = execs.execute(new String[]{"--help"});
		assertEquals(0, run);
	}

	@Test
	public void testCliList(){
		ExecS execs = new ExecS();
		int run = execs.execute(new String[]{"-l"});
		assertEquals(0, run);
		run = execs.execute(new String[]{"--list"});
		assertEquals(0, run);
	}

}

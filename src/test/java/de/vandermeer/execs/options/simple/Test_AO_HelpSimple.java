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

package de.vandermeer.execs.options.simple;

import org.junit.Test;

public class Test_AO_HelpSimple {

	@Test
	public void test_Constructor(){
		Test_SimpleStatics.test_Constructor(new AO_HelpSimple(null, null), false, "help");
	}

	@Test
	public void test_ConstructorShort(){
		Test_SimpleStatics.test_ConstructorShort(new AO_HelpSimple('h', null), false, "help", 'h');
	}

	@Test
	public void test_Cli(){
		Test_SimpleStatics.test_Cli(new AO_HelpSimple(null, null));
	}

	@Test
	public void test_CliShort(){
		Test_SimpleStatics.test_CliShort(new AO_HelpSimple('h', null));
	}

	@Test
	public void test_CliParse() {
		Test_SimpleStatics.test_CliParse(new AO_HelpSimple(null, null));
	}

	@Test
	public void test_CliParseS() {
		Test_SimpleStatics.test_CliParseShort(new AO_HelpSimple('h', null));
	}
}
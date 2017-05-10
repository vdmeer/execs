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

package de.vandermeer.execs.options.typed;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.vandermeer.skb.interfaces.application.ApoCliParser;

public class Test_AO_HelpTyped {

	@Test
	public void test_Constructor(){
		Test_TypedStatics.test_Constructor(new AO_HelpTyped(null, null), "help", false, "OPTION");
	}

	@Test
	public void test_ConstructorShort(){
		Test_TypedStatics.test_ConstructorShort(new AO_HelpTyped('h', null), 'h', "help", false, "OPTION");
	}

	@Test
	public void test_Cli(){
		Test_TypedStatics.test_Cli(new AO_HelpTyped(null, null));
	}

	@Test
	public void test_CliShort(){
		Test_TypedStatics.test_CliShort(new AO_HelpTyped('h', null));
	}

	@Test
	public void test_CliParse() {
		Test_TypedStatics.test_CliParse(new AO_HelpTyped(null, null));
	}

	@Test
	public void test_CliParse_Noarg() {
		ApoCliParser cli = ApoCliParser.defaultParser();
		AO_HelpTyped ao = new AO_HelpTyped(null, null);
		cli.getOptions().addOption(ao);
		assertFalse(ao.inCli());
		cli.parse(new String[]{"--help"});
		assertTrue(ao.inCli());
		assertEquals((String)null, ao.getCliValue());
		assertEquals((String)null, ao.getValue());
	}

	@Test
	public void test_CliParseShort() {
		Test_TypedStatics.test_CliParseShort(new AO_HelpTyped('h', null));
	}

	@Test
	public void test_CliParseS_NoArg() {
		ApoCliParser cli = ApoCliParser.defaultParser();
		AO_HelpTyped ao = new AO_HelpTyped('h', null);
		cli.getOptions().addOption(ao);
		assertFalse(ao.inCli());
		cli.parse(new String[]{"-h"});
		assertTrue(ao.inCli());
		assertEquals((String)null, ao.getCliValue());
		assertEquals((String)null, ao.getValue());
	}
}

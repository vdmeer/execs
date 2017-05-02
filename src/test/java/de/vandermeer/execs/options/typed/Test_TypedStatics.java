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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang3.StringUtils;

import de.vandermeer.execs.DefaultCliParser;
import de.vandermeer.execs.options.AbstractTypedC;
import de.vandermeer.skb.interfaces.application.CliParseException;

public class Test_TypedStatics {

	public static void test_Constructor(AbstractTypedC<?> ao, String cliLong, boolean isRequired, String argName){
		assertTrue(ao.getCliShort()==null);
		assertTrue(ao.getCliLong().equals(cliLong));
		assertEquals(cliLong, ao.getCliShortLong());
		assertEquals(isRequired, ao.cliIsRequired());
		assertFalse(StringUtils.isBlank(ao.getDescription()));
		assertNull(ao.getLongDescription());
		assertEquals(argName, ao.getCliArgumentName());
		assertFalse(StringUtils.isBlank(ao.getCliArgumentDescription()));
	}

	public static void test_ConstructorShort(AbstractTypedC<?> ao, Character cliShort, String cliLong, boolean isRequired, String argName){
		assertTrue(ao.getCliShort().equals(cliShort));
		assertTrue(ao.getCliLong().equals(cliLong));
		assertEquals(cliShort.toString(), ao.getCliShortLong());
		assertEquals(isRequired, ao.cliIsRequired());
		assertFalse(StringUtils.isBlank(ao.getDescription()));
		assertNull(ao.getLongDescription());
		assertEquals(argName, ao.getCliArgumentName());
		assertFalse(StringUtils.isBlank(ao.getCliArgumentDescription()));
	}

	public static void test_Cli(AbstractTypedC<?> ao){
		DefaultCliParser cli = new DefaultCliParser();
		cli.addOption(ao);
		assertEquals(1, cli.getAddedOptions().size());
		assertTrue(cli.getAddedOptions().contains(ao.getCliLong()));
	}

	public static void test_CliShort(AbstractTypedC<?> ao){
		DefaultCliParser cli = new DefaultCliParser();
		cli.addOption(ao);
		assertEquals(2, cli.getAddedOptions().size());
		assertTrue(cli.getAddedOptions().contains(ao.getCliLong()));
		assertTrue(cli.getAddedOptions().contains(ao.getCliShort().toString()));
	}

	public static void test_CliParse(AbstractTypedC<?> ao) throws IllegalStateException, CliParseException{
		DefaultCliParser cli = new DefaultCliParser();
		cli.addOption(ao);
		assertFalse(ao.inCli());
		cli.parse(new String[]{"--" + ao.getCliLong(), "foo"});
		assertTrue(ao.inCli());
		assertEquals("foo", ao.getCliValue());
		assertEquals("foo", ao.getValue());
	}

	public static void test_CliParseShort(AbstractTypedC<?> ao) throws IllegalStateException, CliParseException{
		DefaultCliParser cli = new DefaultCliParser();
		cli.addOption(ao);
		assertFalse(ao.inCli());
		cli.parse(new String[]{"-" + ao.getCliShort(), "foo"});
		assertTrue(ao.inCli());
		assertEquals("foo", ao.getCliValue());
		assertEquals("foo", ao.getValue());
	}

}

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

import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import de.vandermeer.execs.DefaultCliParser;
import de.vandermeer.execs.DefaultPropertyParser;

public class Test_AO_ClassmapFilename_CP {

	@Test
	public void test_Constructor(){
		AO_ClassmapFilename_CP ao = new AO_ClassmapFilename_CP(null);
		assertTrue(ao.getCliShort()==null);
		assertTrue(ao.getCliLong().equals("classmap-file"));
		assertEquals("classmap-file", ao.getCliShortLong());
		assertFalse(ao.cliIsRequired());
		assertFalse(StringUtils.isBlank(ao.getDescription()));
		assertFalse(StringUtils.isBlank(ao.getLongDescription().toString()));
		assertEquals("FILE", ao.getCliArgumentName());
		assertFalse(StringUtils.isBlank(ao.getCliArgumentDescription()));
	}

	@Test
	public void test_ConstructorShort(){
		AO_ClassmapFilename_CP ao = new AO_ClassmapFilename_CP('o');
		assertTrue(ao.getCliShort().equals('o'));
		assertTrue(ao.getCliLong().equals("classmap-file"));
		assertEquals("o", ao.getCliShortLong());
		assertFalse(ao.cliIsRequired());
		assertFalse(StringUtils.isBlank(ao.getDescription()));
		assertFalse(StringUtils.isBlank(ao.getLongDescription().toString()));
	}

	@Test
	public void test_Cli(){
		DefaultCliParser cli = new DefaultCliParser("app");
		AO_ClassmapFilename_CP ao = new AO_ClassmapFilename_CP(null);
		cli.addOption(ao);
		assertEquals(1, cli.getAddedOptions().size());
		assertTrue(cli.getAddedOptions().contains(ao.getCliLong()));
	}

	@Test
	public void test_CliS(){
		DefaultCliParser cli = new DefaultCliParser("app");
		AO_ClassmapFilename_CP ao = new AO_ClassmapFilename_CP('o');
		cli.addOption(ao);
		assertEquals(2, cli.getAddedOptions().size());
		assertTrue(cli.getAddedOptions().contains(ao.getCliLong()));
		assertTrue(cli.getAddedOptions().contains(ao.getCliShort().toString()));
	}

	@Test
	public void test_CliParse(){
		DefaultCliParser cli = new DefaultCliParser("app");
		AO_ClassmapFilename_CP ao = new AO_ClassmapFilename_CP(null);
		cli.addOption(ao);
		assertFalse(ao.inCli());
		cli.parse(new String[]{"--classmap-file", "fn"});
		assertTrue(ao.inCli());
		assertEquals("fn", ao.getCliValue());
		assertEquals("fn", ao.getValue());
	}

	@Test
	public void test_CliParseS(){
		DefaultCliParser cli = new DefaultCliParser("app");
		AO_ClassmapFilename_CP ao = new AO_ClassmapFilename_CP('o');
		cli.addOption(ao);
		assertFalse(ao.inCli());
		cli.parse(new String[]{"-o", "fn"});
		assertTrue(ao.inCli());
		assertEquals("fn", ao.getCliValue());
		assertEquals("fn", ao.getValue());
	}

	@Test
	public void test_Property(){
		DefaultPropertyParser prop = new DefaultPropertyParser("app");
		AO_ClassmapFilename_CP ao = new AO_ClassmapFilename_CP(null);
		prop.addOption(ao);
		assertEquals(1, prop.getOptions().size());
		assertTrue(prop.getOptions().getKeys().contains(ao.getPropertyKey()));
	}

	@Test
	public void test_PropertyParse(){
		DefaultPropertyParser prop = new DefaultPropertyParser("app");
		AO_ClassmapFilename_CP ao = new AO_ClassmapFilename_CP(null);
		prop.addOption(ao);
		Properties args = new Properties();
		args.setProperty(ao.getPropertyKey(), "test-me");
		prop.parse(args);
		assertTrue(ao.inProperties());
		assertEquals("test-me", ao.getPropertyValue());
		assertEquals("test-me", ao.getValue());
	}
}

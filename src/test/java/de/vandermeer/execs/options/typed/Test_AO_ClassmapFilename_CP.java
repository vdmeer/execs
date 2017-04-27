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

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import de.vandermeer.execs.DefaultCliParser;
import de.vandermeer.execs.options.typed.AO_ClassmapFilename_CP;

public class Test_AO_ClassmapFilename_CP {

//	@Test
//	public void test_Constructor(){
//		AO_ClassmapFilename_New ao = new AO_ClassmapFilename_New(null, false, "my-arg", "short description");
//		assertTrue(ao.getCliShort()==null);
//		assertTrue(ao.getCliLong().equals("classmap-file"));
//		assertEquals("classmap-file", ao.getCliShortLong());
//		assertFalse(ao.cliIsRequired());
//		assertFalse(StringUtils.isBlank(ao.getDescription()));
//		assertFalse(StringUtils.isBlank(ao.getLongDescription()));
//		assertEquals("FILE", ao.getCliArgumentName());
//		assertEquals("my-arg", ao.getCliArgumentDescription());
//	}
//
//	@Test
//	public void test_ConstructorShort(){
//		AO_ClassmapFilename_New ao = new AO_ClassmapFilename_New('o', false, "my-arg", "short description");
//		assertTrue(ao.getCliShort().equals('o'));
//		assertTrue(ao.getCliLong().equals("classmap-file"));
//		assertEquals("o", ao.getCliShortLong());
//		assertFalse(ao.cliIsRequired());
//		assertFalse(StringUtils.isBlank(ao.getDescription()));
//		assertFalse(StringUtils.isBlank(ao.getLongDescription()));
//	}
//
//	@Test
//	public void test_Cli(){
//		DefaultCliParser cli = new DefaultCliParser();
//		AO_ClassmapFilename_New ao = new AO_ClassmapFilename_New(null, false, "my-arg", "short description");
//		cli.addOption(ao);
//		assertEquals(1, cli.getAddedOptions().size());
//		assertTrue(cli.getAddedOptions().contains("classmap-file"));
//	}
//
//	@Test
//	public void test_CliS(){
//		DefaultCliParser cli = new DefaultCliParser();
//		AO_ClassmapFilename_New ao = new AO_ClassmapFilename_New('o', false, "my-arg", "short description");
//		cli.addOption(ao);
//		assertEquals(2, cli.getAddedOptions().size());
//		assertTrue(cli.getAddedOptions().contains("classmap-file"));
//		assertTrue(cli.getAddedOptions().contains("o"));
//	}
//
//	@Test
//	public void test_CliParse(){
//		DefaultCliParser cli = new DefaultCliParser();
//		AO_ClassmapFilename_New ao = new AO_ClassmapFilename_New(null, false, "my-arg", "short description");
//		cli.addOption(ao);
//		assertFalse(ao.inCli());
//		cli.parse(new String[]{"--classmap-file", "fn"});
//		assertTrue(ao.inCli());
//		assertEquals("fn", ao.getCliValue());
//		assertEquals("fn", ao.getValue());
//	}
//
//	@Test
//	public void test_CliParseS(){
//		DefaultCliParser cli = new DefaultCliParser();
//		AO_ClassmapFilename_New ao = new AO_ClassmapFilename_New('o', false, "my-arg", "short description");
//		cli.addOption(ao);
//		assertFalse(ao.inCli());
//		cli.parse(new String[]{"-o", "fn"});
//		assertTrue(ao.inCli());
//		assertEquals("fn", ao.getCliValue());
//		assertEquals("fn", ao.getValue());
//	}
}

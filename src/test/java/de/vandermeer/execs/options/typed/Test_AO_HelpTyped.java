package de.vandermeer.execs.options.typed;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.vandermeer.execs.DefaultCliParser;

public class Test_AO_HelpTyped {

	@Test
	public void test_Constructor(){
		Test_TypedStatics.test_Constructor(new AO_HelpTyped(null), "help", false, "OPTION");
	}

	@Test
	public void test_ConstructorShort(){
		Test_TypedStatics.test_ConstructorShort(new AO_HelpTyped('h'), 'h', "help", false, "OPTION");
	}

	@Test
	public void test_Cli(){
		Test_TypedStatics.test_Cli(new AO_HelpTyped(null));
	}

	@Test
	public void test_CliShort(){
		Test_TypedStatics.test_CliShort(new AO_HelpTyped('h'));
	}

	@Test
	public void test_CliParse(){
		Test_TypedStatics.test_CliParse(new AO_HelpTyped(null));
	}

	@Test
	public void test_CliParse_Noarg(){
		DefaultCliParser cli = new DefaultCliParser();
		AO_HelpTyped ao = new AO_HelpTyped(null);
		cli.addOption(ao);
		assertFalse(ao.inCli());
		cli.parse(new String[]{"--help"});
		assertTrue(ao.inCli());
		assertEquals((String)null, ao.getCliValue());
		assertEquals((String)null, ao.getValue());
	}

	@Test
	public void test_CliParseShort(){
		Test_TypedStatics.test_CliParseShort(new AO_HelpTyped('h'));
	}

	@Test
	public void test_CliParseS_NoArg(){
		DefaultCliParser cli = new DefaultCliParser();
		AO_HelpTyped ao = new AO_HelpTyped('h');
		cli.addOption(ao);
		assertFalse(ao.inCli());
		cli.parse(new String[]{"-h"});
		assertTrue(ao.inCli());
		assertEquals((String)null, ao.getCliValue());
		assertEquals((String)null, ao.getValue());
	}
}

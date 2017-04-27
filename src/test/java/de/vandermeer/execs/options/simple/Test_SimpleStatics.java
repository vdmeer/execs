package de.vandermeer.execs.options.simple;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang3.StringUtils;

import de.vandermeer.execs.DefaultCliParser;
import de.vandermeer.execs.options.AbstractSimpleC;

public class Test_SimpleStatics {

	public static void test_Constructor(AbstractSimpleC ao, boolean isRequired, String cliLong){
		assertNull(ao.getLongDescription());
		ao.setLongDescription("");
		assertNull(ao.getLongDescription());

		assertTrue(ao.getCliShort()==null);
		assertTrue(ao.getCliLong().equals(cliLong));
		assertEquals(cliLong, ao.getCliShortLong());
		assertEquals(isRequired, ao.cliIsRequired());
		assertFalse(StringUtils.isBlank(ao.getDescription()));

		ao.setLongDescription("long description");
		assertFalse(StringUtils.isBlank(ao.getLongDescription()));
	}

	public static void test_ConstructorShort(AbstractSimpleC ao, boolean isRequired, String cliLong, Character cliShort){
		assertTrue(ao.getCliShort().equals(cliShort));
		assertTrue(ao.getCliLong().equals(cliLong));
		assertEquals(cliShort.toString(), ao.getCliShortLong());
		assertEquals(isRequired, ao.cliIsRequired());
		assertFalse(StringUtils.isBlank(ao.getDescription()));
		assertTrue(StringUtils.isBlank(ao.getLongDescription()));
	}

	public static void test_Cli(AbstractSimpleC ao){
		DefaultCliParser cli = new DefaultCliParser();
		cli.addOption(ao);
		assertEquals(1, cli.getAddedOptions().size());
		assertTrue(cli.getAddedOptions().contains(ao.getCliLong()));
	}

	public static void test_CliShort(AbstractSimpleC ao){
		DefaultCliParser cli = new DefaultCliParser();
		cli.addOption(ao);
		assertEquals(2, cli.getAddedOptions().size());
		assertTrue(cli.getAddedOptions().contains(ao.getCliLong()));
		assertTrue(cli.getAddedOptions().contains(ao.getCliShort().toString()));
	}

	public static void test_CliParse(AbstractSimpleC ao){
		DefaultCliParser cli = new DefaultCliParser();
		cli.addOption(ao);
		assertFalse(ao.inCli());
		cli.parse(new String[]{"--" + ao.getCliLong()});
		assertTrue(ao.inCli());
	}

	public static void test_CliParseShort(AbstractSimpleC ao){
		DefaultCliParser cli = new DefaultCliParser();
		cli.addOption(ao);
		assertFalse(ao.inCli());
		cli.parse(new String[]{"-" + ao.getCliShort()});
		assertTrue(ao.inCli());
	}

}

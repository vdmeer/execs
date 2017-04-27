package de.vandermeer.execs.options.simple;

import org.junit.Test;

public class Test_AO_HelpSimple {

	@Test
	public void test_Constructor(){
		Test_SimpleStatics.test_Constructor(new AO_HelpSimple(null), false, "help");
	}

	@Test
	public void test_ConstructorShort(){
		Test_SimpleStatics.test_ConstructorShort(new AO_HelpSimple('h'), false, "help", 'h');
	}

	@Test
	public void test_Cli(){
		Test_SimpleStatics.test_Cli(new AO_HelpSimple(null));
	}

	@Test
	public void test_CliShort(){
		Test_SimpleStatics.test_CliShort(new AO_HelpSimple('h'));
	}

	@Test
	public void test_CliParse(){
		Test_SimpleStatics.test_CliParse(new AO_HelpSimple(null));
	}

	@Test
	public void test_CliParseS(){
		Test_SimpleStatics.test_CliParseShort(new AO_HelpSimple('h'));
	}
}

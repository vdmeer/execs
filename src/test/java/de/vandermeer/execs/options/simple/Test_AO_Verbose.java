package de.vandermeer.execs.options.simple;

import org.junit.Test;

public class Test_AO_Verbose {

	@Test
	public void test_Constructor(){
		Test_SimpleStatics.test_Constructor(new AO_Verbose_New(null), false, "verbose");
	}

	@Test
	public void test_ConstructorShort(){
		Test_SimpleStatics.test_ConstructorShort(new AO_Verbose_New('b'), false, "verbose", 'b');
	}

	@Test
	public void test_Cli(){
		Test_SimpleStatics.test_Cli(new AO_Verbose_New(null));
	}

	@Test
	public void test_CliShort(){
		Test_SimpleStatics.test_CliShort(new AO_Verbose_New('b'));
	}

	@Test
	public void test_CliParse(){
		Test_SimpleStatics.test_CliParse(new AO_Verbose_New(null));
	}

	@Test
	public void test_CliParseS(){
		Test_SimpleStatics.test_CliParseShort(new AO_Verbose_New('b'));
	}
}

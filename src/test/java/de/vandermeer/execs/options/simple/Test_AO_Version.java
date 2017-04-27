package de.vandermeer.execs.options.simple;

import org.junit.Test;

public class Test_AO_Version {

	@Test
	public void test_Constructor(){
		Test_SimpleStatics.test_Constructor(new AO_Version_New(null), false, "version");
	}

	@Test
	public void test_ConstructorShort(){
		Test_SimpleStatics.test_ConstructorShort(new AO_Version_New('v'), false, "version", 'v');
	}

	@Test
	public void test_Cli(){
		Test_SimpleStatics.test_Cli(new AO_Version_New(null));
	}

	@Test
	public void test_CliShort(){
		Test_SimpleStatics.test_CliShort(new AO_Version_New('v'));
	}

	@Test
	public void test_CliParse(){
		Test_SimpleStatics.test_CliParse(new AO_Version_New(null));
	}

	@Test
	public void test_CliParseS(){
		Test_SimpleStatics.test_CliParseShort(new AO_Version_New('v'));
	}
}

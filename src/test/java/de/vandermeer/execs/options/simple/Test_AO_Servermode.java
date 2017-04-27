package de.vandermeer.execs.options.simple;

import org.junit.Test;

public class Test_AO_Servermode {

	@Test
	public void test_Constructor(){
		Test_SimpleStatics.test_Constructor(new AO_Servermode_New(null), false, "srv-mode");
	}

	@Test
	public void test_ConstructorShort(){
		Test_SimpleStatics.test_ConstructorShort(new AO_Servermode_New('m'), false, "srv-mode", 'm');
	}

	@Test
	public void test_Cli(){
		Test_SimpleStatics.test_Cli(new AO_Servermode_New(null));
	}

	@Test
	public void test_CliShort(){
		Test_SimpleStatics.test_CliShort(new AO_Servermode_New('m'));
	}

	@Test
	public void test_CliParse(){
		Test_SimpleStatics.test_CliParse(new AO_Servermode_New(null));
	}

	@Test
	public void test_CliParseS(){
		Test_SimpleStatics.test_CliParseShort(new AO_Servermode_New('m'));
	}

}

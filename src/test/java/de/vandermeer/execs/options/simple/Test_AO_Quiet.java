package de.vandermeer.execs.options.simple;

import org.junit.Test;

public class Test_AO_Quiet {

	@Test
	public void test_Constructor(){
		Test_SimpleStatics.test_Constructor(new AO_Quiet_New(null), false, "quiet");
	}

	@Test
	public void test_ConstructorShort(){
		Test_SimpleStatics.test_ConstructorShort(new AO_Quiet_New('q'), false, "quiet", 'q');
	}

	@Test
	public void test_Cli(){
		Test_SimpleStatics.test_Cli(new AO_Quiet_New(null));
	}

	@Test
	public void test_CliShort(){
		Test_SimpleStatics.test_CliShort(new AO_Quiet_New('q'));
	}

	@Test
	public void test_CliParse(){
		Test_SimpleStatics.test_CliParse(new AO_Quiet_New(null));
	}

	@Test
	public void test_CliParseS(){
		Test_SimpleStatics.test_CliParseShort(new AO_Quiet_New('q'));
	}

}

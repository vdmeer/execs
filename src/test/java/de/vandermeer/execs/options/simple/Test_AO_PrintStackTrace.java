package de.vandermeer.execs.options.simple;

import org.junit.Test;

public class Test_AO_PrintStackTrace {

	@Test
	public void test_Constructor(){
		Test_SimpleStatics.test_Constructor(new AO_PrintStackTrace_New(null), false, "print-stack-trace");
	}

	@Test
	public void test_ConstructorShort(){
		Test_SimpleStatics.test_ConstructorShort(new AO_PrintStackTrace_New('t'), false, "print-stack-trace", 't');
	}

	@Test
	public void test_Cli(){
		Test_SimpleStatics.test_Cli(new AO_PrintStackTrace_New(null));
	}

	@Test
	public void test_CliShort(){
		Test_SimpleStatics.test_CliShort(new AO_PrintStackTrace_New('t'));
	}

	@Test
	public void test_CliParse(){
		Test_SimpleStatics.test_CliParse(new AO_PrintStackTrace_New(null));
	}

	@Test
	public void test_CliParseS(){
		Test_SimpleStatics.test_CliParseShort(new AO_PrintStackTrace_New('t'));
	}
}

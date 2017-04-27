package de.vandermeer.execs.options.typed;

import org.junit.Test;

public class Test_AO_ID {

	AO_ID_New getAo(Character cliShort){
		return new AO_ID_New(cliShort, false, "my-arg", "short description");
	}

	@Test
	public void test_Constructor(){
		Test_TypedStatics.test_Constructor(getAo(null), "id", false, "ID");
	}

	@Test
	public void test_ConstructorShort(){
		Test_TypedStatics.test_ConstructorShort(getAo('d'), 'd', "id", false, "ID");
	}

	@Test
	public void test_Cli(){
		Test_TypedStatics.test_Cli(getAo(null));
	}

	@Test
	public void test_CliShort(){
		Test_TypedStatics.test_CliShort(getAo('d'));
	}

	@Test
	public void test_CliParse(){
		Test_TypedStatics.test_CliParse(getAo(null));
	}

	@Test
	public void test_CliParseShort(){
		Test_TypedStatics.test_CliParseShort(getAo('d'));
	}
}

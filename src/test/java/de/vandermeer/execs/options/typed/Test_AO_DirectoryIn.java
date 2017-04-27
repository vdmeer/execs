package de.vandermeer.execs.options.typed;

import org.junit.Test;

public class Test_AO_DirectoryIn {

	AO_DirectoryIn_New getAo(Character cliShort){
		return new AO_DirectoryIn_New(cliShort, false, "my-arg", "short description");
	}

	@Test
	public void test_Constructor(){
		Test_TypedStatics.test_Constructor(getAo(null), "input-directory", false, "DIR");
	}

	@Test
	public void test_ConstructorShort(){
		Test_TypedStatics.test_ConstructorShort(getAo('d'), 'd', "input-directory", false, "DIR");
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

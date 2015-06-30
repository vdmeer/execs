package de.vandermeer.execs;

import org.junit.Test;

public class MeTest {

	@Test
	public void testMe(){
		Gen_RunScripts generator = new Gen_RunScripts();

		generator.executeService(new String[]{
				"--target",
				"bat",
				"--deployment-dir",
				"bla"
		});

//		generator.serviceHelpScreen();
	}
}

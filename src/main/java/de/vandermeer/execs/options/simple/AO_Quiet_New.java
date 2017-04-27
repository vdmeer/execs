package de.vandermeer.execs.options.simple;

import de.vandermeer.execs.options.AbstractSimpleC;

public class AO_Quiet_New extends AbstractSimpleC {

	public AO_Quiet_New(Character cliShort) {
		super(
				cliShort,
				"quiet",
				false,
				"puts the application in quiet mode, no progres or error messages will be printed"
		);
	}

}

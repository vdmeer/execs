package de.vandermeer.execs.options.simple;

import de.vandermeer.execs.options.AbstractSimpleC;

public class AO_Servermode_New extends AbstractSimpleC {

	public AO_Servermode_New(Character cliShort) {
		super(
				cliShort,
				"srv-mode",
				false,
				"tells a server to run in background mode"
		);
	}

}

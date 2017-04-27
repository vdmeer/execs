package de.vandermeer.execs.options.simple;

import de.vandermeer.execs.options.AbstractSimpleC;

public class AO_PrintStackTrace_New extends AbstractSimpleC {

	public AO_PrintStackTrace_New(Character cliShort) {
		super(
				cliShort,
				"print-stack-trace",
				false,
				"sets a flag to print the stack trace of exceptions"
		);
	}

}

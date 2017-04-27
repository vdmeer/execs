package de.vandermeer.execs.options.simple;

import de.vandermeer.execs.options.AbstractSimpleC;

public class AO_Version_New extends AbstractSimpleC {

	public AO_Version_New(Character cliShort) {
		super(
				cliShort,
				"version",
				false,
				"application version"
		);
	}

}

//"Provides version and possibly related information about the application. The argument must be the first argument in a command line (otherwise it will be ignored)."
package de.vandermeer.execs.options.simple;

import de.vandermeer.execs.options.AbstractSimpleC;

public class AO_HelpSimple extends AbstractSimpleC {

	public AO_HelpSimple(Character cliShort) {
		super(
				cliShort,
				"help",
				false,
				"prints a help screen with usage information"
		);
	}

}

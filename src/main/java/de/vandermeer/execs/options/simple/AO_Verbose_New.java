package de.vandermeer.execs.options.simple;

import de.vandermeer.execs.options.AbstractSimpleC;

public class AO_Verbose_New extends AbstractSimpleC {

	public AO_Verbose_New(Character cliShort) {
		super(
				cliShort,
				"verbose",
				false,
				"verbose mode for application"
		);
	}

}


//"Sets an application verbose mode, meaning the application will printout extended progress messages."
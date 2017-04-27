package de.vandermeer.execs.options.typed;

import de.vandermeer.execs.options.AbstractTypedC_String;

public class AO_LibDir_New extends AbstractTypedC_String {

	public AO_LibDir_New(Character cliShort, boolean isRequired, String argDescr, String description) {
		super(
				cliShort, "lib-dir", isRequired,
				"DIR", false, argDescr,
				description
		);
	}

}
//"specifies a directory with required jar files"
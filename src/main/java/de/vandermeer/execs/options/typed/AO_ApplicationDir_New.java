package de.vandermeer.execs.options.typed;

import de.vandermeer.execs.options.AbstractTypedC_String;

public class AO_ApplicationDir_New extends AbstractTypedC_String {

	public AO_ApplicationDir_New(Character cliShort, boolean isRequired, String argDescr, String description) {
		super(
				cliShort, "application-dir", isRequired,
				"DIR", false, argDescr,
				description
		);
	}

}

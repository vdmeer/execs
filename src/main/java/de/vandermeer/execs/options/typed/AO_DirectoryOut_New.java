package de.vandermeer.execs.options.typed;

import de.vandermeer.execs.options.AbstractTypedC_String;

public class AO_DirectoryOut_New extends AbstractTypedC_String {

	public AO_DirectoryOut_New(Character cliShort, boolean isRequired, String argDescr, String description) {
		super(
				cliShort, "output-directory", isRequired,
				"DIR", false, argDescr,
				description
		);
	}

}

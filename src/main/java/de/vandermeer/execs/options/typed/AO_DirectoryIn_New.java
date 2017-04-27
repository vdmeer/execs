package de.vandermeer.execs.options.typed;

import de.vandermeer.execs.options.AbstractTypedC_String;

public class AO_DirectoryIn_New extends AbstractTypedC_String {

	public AO_DirectoryIn_New(Character cliShort, boolean isRequired, String argDescr, String description) {
		super(
				cliShort, "input-directory", isRequired,
				"DIR", false, argDescr,
				description
		);
	}

}

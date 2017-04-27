package de.vandermeer.execs.options.typed;

import de.vandermeer.execs.options.AbstractTypedC_String;

public class AO_FileIn_New extends AbstractTypedC_String {

	public AO_FileIn_New(Character cliShort, boolean isRequired, String argDescr, String description) {
		super(
				cliShort, "input-file", isRequired,
				"FILE", false, argDescr,
				description
		);
	}

}

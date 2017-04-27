package de.vandermeer.execs.options.typed;

import de.vandermeer.execs.options.AbstractTypedC_String;

public class AO_ID_New extends AbstractTypedC_String {

	public AO_ID_New(Character cliShort, boolean isRequired, String argDescr, String description) {
		super(
				cliShort, "id", isRequired,
				"ID", false, argDescr,
				description
		);
	}

}

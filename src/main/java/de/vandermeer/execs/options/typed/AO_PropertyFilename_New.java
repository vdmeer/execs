package de.vandermeer.execs.options.typed;

import de.vandermeer.execs.options.AbstractTypedC_String;

public class AO_PropertyFilename_New extends AbstractTypedC_String {

	public AO_PropertyFilename_New(Character cliShort, boolean isRequired, String argDescr, String description) {
		super(
				cliShort, "property-file", isRequired,
				"FILE", false, argDescr,
				description
		);
	}

}

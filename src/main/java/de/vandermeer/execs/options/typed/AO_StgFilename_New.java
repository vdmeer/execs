package de.vandermeer.execs.options.typed;

import de.vandermeer.execs.options.AbstractTypedC_String;

public class AO_StgFilename_New extends AbstractTypedC_String {

	public AO_StgFilename_New(Character cliShort, boolean isRequired, String argDescr, String description) {
		super(
				cliShort, "stg-file", isRequired,
				"FILE", false, argDescr,
				description
		);
//		"specifies a string template (STG) file"
	}

}

package de.vandermeer.execs.options.typed;

import de.vandermeer.execs.options.AbstractTypedC_String;

public class AO_TemplateDir_New extends AbstractTypedC_String {

	public AO_TemplateDir_New(Character cliShort, boolean isRequired, String argDescr, String description) {
		super(
				cliShort, "template-dir", isRequired,
				"DIR", false, argDescr,
				description
		);
	}

}
//"specifies a directory with templates for an application"
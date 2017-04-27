package de.vandermeer.execs.options.typed;

import de.vandermeer.execs.options.AbstractTypedC_String;

public class AO_HelpTyped extends AbstractTypedC_String {

	public AO_HelpTyped(Character cliShort) {
		super(
				cliShort, "help", false,
				"OPTION", true, "an optional CLI argument to get specific help for",
				"prints a help screen with usage information or specific help for an option"
//				"Without argument, help will print a usage and help screen with all CLI arguments and further information for the application. With an argument, help will print specific help information for the given CLI argument if applicable to the application."
		);
	}
}

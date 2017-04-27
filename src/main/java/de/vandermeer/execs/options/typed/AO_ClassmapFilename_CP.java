package de.vandermeer.execs.options.typed;

import de.vandermeer.execs.options.AbstractTypedCP_String;

public class AO_ClassmapFilename_CP extends AbstractTypedCP_String {

	public AO_ClassmapFilename_CP(Character cliShort, String propertyKey) {
		super(
				cliShort, "classmap-file", false,
				"FILE", false, "a filename, file must exist as plain text file using propery syntax",
				propertyKey,
				"a property file with class names (executable applications) mapped to script names"
		);

		this.setLongDescription("The class map file contains mappings from a class name to a script name. This mapping is used to generate run scripts for applications that are not registered with an executor, or if automated generation (for registered applications) is not required or wanted.");
	}

}

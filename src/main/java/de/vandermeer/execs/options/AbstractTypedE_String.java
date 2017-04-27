package de.vandermeer.execs.options;

public class AbstractTypedE_String extends AbstractTypedE<String> {

	protected AbstractTypedE_String(String environmentKey, String description) {
		super(environmentKey, description);

		this.environmentValue = System.getenv(this.getEnvironmentKey());
	}

}

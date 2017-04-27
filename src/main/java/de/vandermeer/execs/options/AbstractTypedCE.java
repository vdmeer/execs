package de.vandermeer.execs.options;

import de.vandermeer.skb.interfaces.application.Apo_TypedCE;

public abstract class AbstractTypedCE<T> extends AbstractTypedC<T> implements Apo_TypedCE<T> {

	protected boolean inEnv = false;

	protected final String environmentKey;

	protected T environmentValue;

	protected AbstractTypedCE(Character cliShort, String cliLong, boolean isRequired, String argName, boolean argIsOptional, String argDescr, String environmentKey, String description) {
		super(cliShort, cliLong, isRequired, argName, argIsOptional, argDescr, description);
		this.environmentKey = environmentKey;
	}

	@Override
	public void validate() throws IllegalStateException {
		Apo_TypedCE.super.validate();
	}

	@Override
	public boolean inEnvironment() {
		return this.inEnv;
	}

	@Override
	public String getEnvironmentKey() {
		return this.environmentKey;
	}

	@Override
	public T getEnvironmentValue() {
		return this.environmentValue;
	}

}

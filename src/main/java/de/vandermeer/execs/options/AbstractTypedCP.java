package de.vandermeer.execs.options;

import de.vandermeer.skb.interfaces.application.Apo_TypedCP;

public abstract class AbstractTypedCP<T> extends AbstractTypedC<T> implements Apo_TypedCP<T> {

	protected boolean inEnv = false;

	protected final String propertyKey;

	protected T propertyValue;

	protected AbstractTypedCP(Character cliShort, String cliLong, boolean isRequired, String argName, boolean argIsOptional, String argDescr, String propertyKey, String description) {
		super(cliShort, cliLong, isRequired, argName, argIsOptional, argDescr, description);
		this.propertyKey = propertyKey;
	}

	@Override
	public void validate() throws IllegalStateException {
		Apo_TypedCP.super.validate();
	}

	@Override
	public boolean inProperties() {
		return this.inEnv;
	}

	@Override
	public String getPropertyKey() {
		return this.propertyKey;
	}

	@Override
	public T getPropertyValue() {
		return this.propertyValue;
	}

}

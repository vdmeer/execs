package de.vandermeer.execs.options;

import de.vandermeer.skb.interfaces.application.Apo_TypedE;

public class AbstractTypedE<T> extends AbstractApoBaseE implements Apo_TypedE<T> {

	protected T environmentValue;

	protected T defaultValue;

	protected AbstractTypedE(String environmentKey, String description) {
		super(environmentKey, description);

		this.validate();
	}

	@Override
	public T getDefaultValue() {
		return this.defaultValue;
	}

	public void setDefaultValue(T value) {
		this.defaultValue = value;
	}

	@Override
	public T getEnvironmentValue() {
		return this.environmentValue;
	}

}

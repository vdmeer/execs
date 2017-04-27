package de.vandermeer.execs.options;

import de.vandermeer.skb.interfaces.application.Apo_TypedP;

public abstract class AbstractTypedP<T> extends AbstractApoBaseP implements Apo_TypedP<T> {

	protected T propertyValue;

	protected T defaultValue;

	protected AbstractTypedP(String propertyKey, String description) {
		super(propertyKey, description);

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
	public T getPropertyValue() {
		return this.propertyValue;
	}
}

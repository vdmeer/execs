package de.vandermeer.execs.options;

import de.vandermeer.skb.interfaces.application.Apo_TypedC;

public abstract class AbstractTypedC<T> extends AbstractApoBaseC implements Apo_TypedC<T> {

	protected final String argName;

	protected final String argDescr;

	protected final Boolean argIsOptional;

	protected T cliValue;

	protected T defaultValue;

	protected AbstractTypedC(Character cliShort, String cliLong, boolean isRequired, String argName, boolean argIsOptional, String argDescr, String description){
		super(cliShort, cliLong, isRequired, description);
		this.argName = argName;
		this.argDescr = argDescr;
		this.argIsOptional = argIsOptional;

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
	public boolean cliArgIsOptional(){
		return this.argIsOptional;
	}

	@Override
	public T getCliValue() {
		return this.cliValue;
	}

	@Override
	public String getCliArgumentName() {
		return this.argName;
	}

	@Override
	public String getCliArgumentDescription() {
		return this.argDescr;
	}

}

package de.vandermeer.execs.options;

import de.vandermeer.skb.interfaces.application.ApoBaseP;

public abstract class AbstractApoBaseP extends AbstractApoBase implements ApoBaseP {

	protected boolean inProp = false;

	protected final String propertyKey;

	protected AbstractApoBaseP(String propertyKey, String description) {
		super(description);
		this.propertyKey = propertyKey;
	}

	@Override
	public boolean inProperties() {
		return this.inProp;
	}

	@Override
	public String getPropertyKey() {
		return this.propertyKey;
	}

}

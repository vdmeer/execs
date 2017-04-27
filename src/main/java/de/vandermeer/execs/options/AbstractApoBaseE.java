package de.vandermeer.execs.options;

import de.vandermeer.skb.interfaces.application.ApoBaseE;

public abstract class AbstractApoBaseE extends AbstractApoBase implements ApoBaseE {

	protected boolean inEnv = false;

	protected final String environmentKey;

	protected AbstractApoBaseE(String environmentKey, String description) {
		super(description);
		this.environmentKey = environmentKey;
	}

	@Override
	public boolean inEnvironment() {
		return this.inEnv;
	}

	@Override
	public String getEnvironmentKey() {
		return this.environmentKey;
	}

}

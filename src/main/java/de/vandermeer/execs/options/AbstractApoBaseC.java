package de.vandermeer.execs.options;

import de.vandermeer.skb.interfaces.application.ApoBaseC;

public abstract class AbstractApoBaseC extends AbstractApoBase implements ApoBaseC {

	protected boolean inCli = false;

	protected final Character cliShort;

	protected final String cliLong;

	protected final Boolean isRequired;

	protected AbstractApoBaseC(Character cliShort, String cliLong, boolean isRequired, String description) {
		super(description);
		this.cliShort = cliShort;
		this.cliLong = cliLong;
		this.isRequired = isRequired;
	}

	@Override
	public boolean inCli() {
		return this.inCli;
	}

	@Override
	public void setInCLi(boolean inCli) {
		this.inCli = inCli;
	}

	@Override
	public Character getCliShort() {
		return cliShort;
	}

	@Override
	public String getCliLong() {
		return cliLong;
	}

	@Override
	public boolean cliIsRequired() {
		return this.isRequired;
	}

}

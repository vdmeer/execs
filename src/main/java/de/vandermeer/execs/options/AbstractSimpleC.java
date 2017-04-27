package de.vandermeer.execs.options;

import org.apache.commons.lang3.text.StrBuilder;

import de.vandermeer.skb.interfaces.application.Apo_SimpleC;

public class AbstractSimpleC extends AbstractApoBaseC implements Apo_SimpleC {

	public AbstractSimpleC(Character cliShort, String cliLong, boolean isRequired, String description){
		super(cliShort, cliLong, isRequired, description);
		this.validate();
	}

	@Override
	public String toString(){
		StrBuilder ret = new StrBuilder();

		ret.append("cli short : ").append(this.getCliShort()).appendNewLine();
		ret.append("cli long  : ").append(this.getCliLong()).appendNewLine();
		ret.append("cli sh/lo : ").append(this.getCliShortLong()).appendNewLine();
		ret.append("required  : ").append(this.cliIsRequired()).appendNewLine();
		ret.append("descr     : ").append(this.getDescription()).appendNewLine();
		ret.append("descr long: ").append(this.getLongDescription()).appendNewLine();

		return ret.toString();
	}
}

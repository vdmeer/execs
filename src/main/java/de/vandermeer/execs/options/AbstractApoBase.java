package de.vandermeer.execs.options;

import org.apache.commons.lang3.StringUtils;
import org.stringtemplate.v4.ST;

import de.vandermeer.skb.interfaces.application.ApoBase;

public abstract class AbstractApoBase implements ApoBase {

	protected final String descr;

	protected String longDescr;

	protected AbstractApoBase(String description){
		this.descr = description;
	}

	@Override
	public String getDescription() {
		return this.descr;
	}

	@Override
	public void setLongDescription(String description){
		if(!StringUtils.isBlank(description)){
			this.longDescr = description;
		}
	}

	@Override
	public void setLongDescription(ST description){
		if(description==null){
			return;
		}
		this.setLongDescription(description.render());
	}

	@Override
	public String getLongDescription() {
		return this.longDescr;
	}

}

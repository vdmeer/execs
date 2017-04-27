package de.vandermeer.execs.options;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

public class AbstractTypedC_String extends AbstractTypedC<String> {

	protected AbstractTypedC_String(Character cliShort, String cliLong, boolean isRequired, String argName, boolean argIsOptional, String argDescr, String description){
		super(cliShort, cliLong, isRequired, argName, argIsOptional, argDescr, description);
	}

	@Override
	public void setCliValue(Object value) throws IllegalStateException {
		if(!this.cliArgIsOptional()){
			Validate.validState(value!=null, this.getCliLong() + " argument <" + this.getCliArgumentName() +  "> mandatory but trying to set null");
		}
		if(value==null && this.cliArgIsOptional()){
			return;
		}
		//at this stage we should know that value is not null
		String ret = value.toString();
		if(!StringUtils.isBlank(ret)){
			this.cliValue = ret;
		}
	}

}

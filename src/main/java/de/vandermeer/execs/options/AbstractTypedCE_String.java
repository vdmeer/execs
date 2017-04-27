package de.vandermeer.execs.options;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

public class AbstractTypedCE_String extends AbstractTypedCE<String> {

	protected AbstractTypedCE_String(Character cliShort, String cliLong, boolean isRequired, String argName, boolean argIsOptional, String argDescr, String environmentKey, String description) {
		super(cliShort, cliLong, isRequired, argName, argIsOptional, argDescr, environmentKey, description);
	}

	@Override
	public void setCliValue(Object value) throws IllegalStateException {
		if(!this.cliArgIsOptional()){
			Validate.validState(value!=null, "AOP String: argument mandatory but trying to set null");
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

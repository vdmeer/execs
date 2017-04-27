package de.vandermeer.execs.options;

import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

public class AbstractTypedCP_String extends AbstractTypedCP<String> {

	protected AbstractTypedCP_String(Character cliShort, String cliLong, boolean isRequired, String argName, boolean argIsOptional, String argDescr, String propertyKey, String description) {
		super(cliShort, cliLong, isRequired, argName, argIsOptional, argDescr, propertyKey, description);
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

	@Override
	public void setPropertyValue(Properties properties) throws IllegalStateException {
		String value = properties.getProperty(this.getPropertyKey());
		if(!StringUtils.isBlank(value)){
			this.propertyValue = value;
		}
	}

}

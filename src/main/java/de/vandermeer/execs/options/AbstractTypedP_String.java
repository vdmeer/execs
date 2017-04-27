package de.vandermeer.execs.options;

import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

public class AbstractTypedP_String extends AbstractTypedP<String> {

	protected AbstractTypedP_String(String propertyKey, String description) {
		super(propertyKey, description);
	}

	@Override
	public void setPropertyValue(Properties properties) throws IllegalStateException {
		String value = properties.getProperty(this.getPropertyKey());
		if(!StringUtils.isBlank(value)){
			this.propertyValue = value;
		}
	}

}

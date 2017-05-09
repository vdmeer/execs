/* Copyright 2017 Sven van der Meer <vdmeer.sven@mykolab.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.vandermeer.execs;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.Validate;

import de.vandermeer.skb.interfaces.application.Apo_TypedP;
import de.vandermeer.skb.interfaces.application.App_PropertyParser;
import de.vandermeer.skb.interfaces.application.PropertyOptionSet;
import de.vandermeer.skb.interfaces.messagesets.IsErrorSet_IsError;
import de.vandermeer.skb.interfaces.messagesets.errors.IsError;
import de.vandermeer.skb.interfaces.messagesets.errors.Templates_PropertiesGeneral;

/**
 * The default property parser.
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.4.0 build 170413 (13-Apr-17) for Java 1.8
 * @since      v0.5.0
 */
public class DefaultPropertyParser implements App_PropertyParser {

	/** Property options. */
	protected final transient PropertyOptionSet options;

	/** Local set of errors, collected during execution printed at the end. */
	protected final transient IsErrorSet_IsError errorSet = IsErrorSet_IsError.create();

	/** Error number, holds the number of the last error, 0 if none occurred. */
	protected transient int errNo;

	/** Application name, mostly for error messages. */
	protected final transient String appName;

	/** Flag for how to treat unrecognized keys. */
	protected final transient boolean unknownKeyIsError;

	/**
	 * Creates a new parser not treating unrecognized keys as errors..
	 * @param appName name of application (or calling object/class) for error messages
	 */
	public DefaultPropertyParser(String appName){
		this(appName, false);
	}

	/**
	 * Creates a new parser.
	 * @param appName name of application (or calling object/class) for error messages
	 * @param unknownKeyIsError true if unrecognized keys in properties should be treated as an error, false otherwise
	 */
	public DefaultPropertyParser(String appName, boolean unknownKeyIsError){
		Validate.notBlank(appName);
		this.appName = appName;
		this.unknownKeyIsError = unknownKeyIsError;
		this.options = PropertyOptionSet.create();
	}

	@Override
	public IsErrorSet_IsError getErrorSet() {
		return this.errorSet;
	}

	@Override
	public String getAppName() {
		return this.appName;
	}

	@Override
	public int getErrNo() {
		return this.errNo;
	}

	@Override
	public App_PropertyParser addOption(Object option) throws IllegalStateException {
		if(option==null){
			return this;
		}
		if(ClassUtils.isAssignable(option.getClass(), Apo_TypedP.class)){
			Apo_TypedP<?> eo = (Apo_TypedP<?>)option;
			Validate.validState(
					!this.options.getOptions().keySet().contains(eo.getPropertyKey()),
					this.getAppName() + ": Property option <" + eo.getPropertyKey() + "> already in use"
			);
			this.options.getOptions().put(eo.getPropertyKey(), eo);
		}
		return this;
	}

	@Override
	public PropertyOptionSet getOptions() {
		return this.options;
	}

	@Override
	public void parse(Properties[] properties) {
		if(properties==null){
			return;
		}

		if(this.unknownKeyIsError){
			for(Properties prop : properties){
				for (Enumeration<?> names = prop.propertyNames(); names.hasMoreElements();){
					String key = names.nextElement().toString();
					if(!this.options.getOptions().keySet().contains(key)){
						this.errorSet.addError(Templates_PropertiesGeneral.UNRECOGNIZED_KEY.getError(this.getAppName(), key));
						this.errNo = Templates_PropertiesGeneral.UNRECOGNIZED_KEY.getCode();
					}
				}
			}
		}

		Set<String> setOptions = new HashSet<>();
		for(Entry<String, Apo_TypedP<?>> entry : this.options.getOptions().entrySet()){
			String key = entry.getKey();
			Apo_TypedP<?> value = entry.getValue();
			for(Properties prop : properties){
				String propValue = prop.getProperty(key);
				if(propValue!=null){
					if(setOptions.contains(key)){
						this.errorSet.addError(Templates_PropertiesGeneral.ALREADY_SELECTED.getError(this.getAppName(), key));
						this.errNo = Templates_PropertiesGeneral.ALREADY_SELECTED.getCode();
					}
					else{
						IsError error = value.setPropertyValue(propValue);
						if(error!=null){
							this.errorSet.addError(error);
							this.errNo = error.getErrorCode();
						}
						value.setInProperties(true);
						setOptions.add(key);
					}
				}
			}
			if(!setOptions.contains(key) && value.propertyIsRequired()){
				this.errorSet.addError(Templates_PropertiesGeneral.MISSING_KEY.getError(this.getAppName(), key));
				this.errNo = Templates_PropertiesGeneral.MISSING_KEY.getCode();
			}
		}
	}

	@Override
	public boolean unknownKeyIsError(){
		return this.unknownKeyIsError;
	}

}

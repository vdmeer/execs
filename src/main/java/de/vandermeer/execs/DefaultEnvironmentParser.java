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

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import de.vandermeer.skb.interfaces.application.Apo_TypedE;
import de.vandermeer.skb.interfaces.application.App_EnvironmentParser;
import de.vandermeer.skb.interfaces.application.EnvironmentOptionSet;
import de.vandermeer.skb.interfaces.messagesets.IsErrorSet_IsError;
import de.vandermeer.skb.interfaces.messagesets.errors.IsError;
import de.vandermeer.skb.interfaces.messagesets.errors.Templates_EnvironmentGeneral;

/**
 * The default environment parser.
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.4.0 build 170413 (13-Apr-17) for Java 1.8
 * @since      v0.5.0
 */
public class DefaultEnvironmentParser implements App_EnvironmentParser {

	/** Environment options. */
	protected final transient EnvironmentOptionSet options;

	/** Local set of errors, collected during execution printed at the end. */
	protected final transient IsErrorSet_IsError errorSet = IsErrorSet_IsError.create();

	/** Error number, holds the number of the last error, 0 if none occurred. */
	protected transient int errNo;

	/** Application name, mostly for error messages. */
	protected final transient String appName;

	/**
	 * Creates a new parser.
	 */
	public DefaultEnvironmentParser(String appName){
		Validate.notBlank(appName);
		this.appName = appName;
		this.options = EnvironmentOptionSet.create();
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
	public App_EnvironmentParser addOption(Object option) throws IllegalStateException {
		if(option==null){
			return this;
		}
		if(ClassUtils.isAssignable(option.getClass(), Apo_TypedE.class)){
			Apo_TypedE<?> eo = (Apo_TypedE<?>)option;
			Validate.validState(
					!this.options.getOptions().keySet().contains(eo.getEnvironmentKey()),
					this.getAppName() + ": environment option <" + eo.getEnvironmentKey() + "> already in use"
			);
			this.options.getOptions().put(eo.getEnvironmentKey(), eo);
		}
		return this;
	}

	@Override
	public EnvironmentOptionSet getOptions() {
		return this.options;
	}

	@Override
	public void parse() {
		Map<String, String> envSettings = null;
		try{
			envSettings = System.getenv();
		}
		catch(SecurityException se){
			this.errorSet.addError(Templates_EnvironmentGeneral.SECURITY_EXCEPTION.getError(this.getAppName(), se.getMessage()));
			this.errNo = Templates_EnvironmentGeneral.SECURITY_EXCEPTION.getCode();
			return;
		}
		if(envSettings==null){
			this.errorSet.addError(Templates_EnvironmentGeneral.SYSTEM_NO_ENV.getError(this.getAppName()));
			this.errNo = Templates_EnvironmentGeneral.SYSTEM_NO_ENV.getCode();
			return;
		}

		Set<String> setOptions = new HashSet<>();
		for(Entry<String, Apo_TypedE<?>> entry : this.options.getOptions().entrySet()){
			String key = entry.getKey();
			Apo_TypedE<?> value = entry.getValue();
			if(!envSettings.keySet().contains(key) && value.environmentIsRequired()){
				this.errorSet.addError(Templates_EnvironmentGeneral.MISSING_KEY.getError(this.getAppName(), key));
				this.errNo = Templates_EnvironmentGeneral.MISSING_KEY.getCode();
			}
			else if(StringUtils.isBlank(envSettings.get(key))){
				if(value.environmentIsRequired()){
					this.errorSet.addError(Templates_EnvironmentGeneral.MISSING_ARGUMENT.getError(this.getAppName(), key));
					this.errNo = Templates_EnvironmentGeneral.MISSING_ARGUMENT.getCode();
				}
			}
			else if(setOptions.contains(key)){
				this.errorSet.addError(Templates_EnvironmentGeneral.ALREADY_SELECTED.getError(this.getAppName(), key));
				this.errNo = Templates_EnvironmentGeneral.ALREADY_SELECTED.getCode();
			}
			else{
				IsError error = value.setEnvironmentValue(envSettings.get(key));
				if(error!=null){
					this.errorSet.addError(error);
					this.errNo = error.getErrorCode();
				}
				value.setInEnvironment(true);
				setOptions.add(key);
			}
		}
	}

}

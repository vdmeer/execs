/* Copyright 2014 Sven van der Meer <vdmeer.sven@mykolab.com>
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

package de.vandermeer.execs.options;

import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

/**
 * Abstract (but fully featured) implementation of {@link ApplicationOption}.
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.3.3 build 160203 (03-Feb-16) for Java 1.8
 * @since      v0.2.0
 */
public abstract class AbstractApplicationOption<T> implements ApplicationOption<T> {

	/** A generated CLI option. */
	protected Option cliOption;

	/** The option description, as a short form, if CLI option is set identical to that description. */
	protected String descr;

	/** Long description for the option. */
	protected String descrLong;

	/** Option value read from CLI, blank or null if not set. */
	protected T valueCli;

	/** Option value set as default value, if none of the other values is set, can itself be blank. */
	protected T ValueDefault;

	/** Option value set by or from a property file or object, blank or null if not set. */
	protected T valueProperty;

	/** Flag stating if the option was in a parsed command line. */
	protected boolean inCli;

	/** Key used for this option for example in a property file or object or in a map. */
	protected String optionKey;

	/**
	 * Returns a new application option with description and long description but without option key and default value.
	 * @param description option description
	 * @param longDescription option long description
	 * @throws NullPointerException - if any description parameter is null
	 * @throws IllegalArgumentException - if any description parameter is empty
	 */
	public AbstractApplicationOption(String description, String longDescription){
		Validate.notBlank(description, "description cannot be empty");
		Validate.notBlank(longDescription, "long description cannot be empty");

		this.descr = description;
		this.descrLong = longDescription;
	}

	/**
	 * Returns a new application option with description, long description, and default value but without option key.
	 * @param defaultValue option default value
	 * @param description option description
	 * @param longDescription option long description
	 * @throws NullPointerException - if any description parameter is null
	 * @throws IllegalArgumentException - if any description parameter is empty
	 */
	public AbstractApplicationOption(T defaultValue, String description, String longDescription){
		this(description, longDescription);
		this.ValueDefault = defaultValue;
	}

	/**
	 * Returns a new application option with description, long description, default value, and option key.
	 * @param optionKey option key
	 * @param defaultValue option default value
	 * @param description option description
	 * @param longDescription option long description
	 * @throws NullPointerException - if optioneKey or any description parameter is null
	 * @throws IllegalArgumentException - if optionKey or any description parameter is empty
	 */
	public AbstractApplicationOption(String optionKey, T defaultValue, String description, String longDescription){
		this(defaultValue, description, longDescription);

		Validate.notBlank(optionKey, "option key cannot be empty");
		this.optionKey = optionKey;
	}

	@Override
	public Option getCliOption(){
		return this.cliOption;
	}

	/**
	 * Sets the CLI option.
	 * @param option CLI option, only used if not null
	 */
	protected final void setCliOption(Option option){
		if(option!=null){
			this.cliOption = option;
		}
		if(this.cliOption.getDescription()!=null){
			this.descr = this.cliOption.getDescription();
		}
		else{
			this.cliOption.setDescription(this.descr);
		}
	}

	@Override
	public String getDescription(){
		return this.descr;
	}

	@Override
	public String getDescriptionLong(){
		return this.descrLong;
	}

	@Override
	public void setDescriptionLong(String longDescription){
		Validate.notBlank(longDescription, "long description cannot be empty");
		this.descrLong = longDescription;
	}

	@Override
	public int setCliValue(CommandLine cmdLine){
		if(!cmdLine.hasOption(this.cliOptionAsString())){
			return 0;
		}

		String arg = cmdLine.getOptionValue(this.cliOptionAsString());
		this.inCli = cmdLine.hasOption(this.cliOptionAsString());

		if(StringUtils.isBlank(arg)){
			if(this.cliOption.hasArg() && this.cliOption.hasOptionalArg()==false){
				return -1;
			}
			return 0;
		}
		this.valueCli = this.convertValue(arg);
		return 0;
	}

	@Override
	public int setPropertyValue(Properties properties) {
		if(properties==null || properties.get(this.getOptionKey())==null){
			if(this.cliOption.hasArg()){
				return -1;
			}
			return 0;
		}
		this.valueProperty = this.convertValue(properties.get(this.getOptionKey()));
		return 0;
	}

	@Override
	public T getCliValue(){
		return this.valueCli;
	}

	@Override
	public T getDefaultValue(){
		return this.ValueDefault;
	}

	@Override
	public T getPropertValue(){
		return this.valueProperty;
	}

	@Override
	public String getOptionKey(){
		return this.optionKey;
	}

	@Override
	public boolean inCli(){
		return this.inCli;
	}

	@Override
	public void setDefaultValue(T value){
		if(value!=null){
			this.ValueDefault = value;
		}
	}
}

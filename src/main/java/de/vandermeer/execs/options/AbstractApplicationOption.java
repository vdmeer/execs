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
import org.stringtemplate.v4.STGroupFile;

/**
 * Abstract (but fully featured) implementation of {@link ApplicationOption}.
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.4.0 build 170413 (13-Apr-17) for Java 1.8
 * @since      v0.2.0
 */
public abstract class AbstractApplicationOption<T> implements ApplicationOption<T> {

	/** A generated CLI option. */
	protected Option cliOption;

	/** A short description, used in a CLI option as description. */
	protected final String shortDescription;

	/** Long description with detailed information. */
	protected final String longDescription;

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

	/** A description of a an argument if used in the CLI option. */
	protected String cliArgDescription;

	/**
	 * Creates a new application option with description and long description.
	 * @param description option description
	 * @param longDescription option long description
	 * @throws NullPointerException - if any description parameter is null
	 * @throws IllegalArgumentException - if any description parameter is empty
	 */
	public AbstractApplicationOption(String description, String longDescription){
		Validate.notBlank(description, "description cannot be empty");
		Validate.notBlank(longDescription, "long description cannot be empty");

		this.shortDescription = description;
		this.longDescription = longDescription;
	}

	/**
	 * Creates a new application option with description, long description, and option key.
	 * @param optionKey option key
	 * @param description option description
	 * @param longDescription option long description
	 * @throws NullPointerException - if optioneKey or any description parameter is null
	 * @throws IllegalArgumentException - if optionKey or any description parameter is empty
	 */
	public AbstractApplicationOption(String optionKey, String description, String longDescription){
		this(description, longDescription);
		Validate.notBlank(optionKey, "option key cannot be empty");

		this.optionKey = optionKey;
	}

	/**
	 * Creates a new application option taking all settings from an STG file.
	 * @param stgFile file name for an STG file with short and long description, must not be empty
	 * @param required true for a required CLI option, false for an optional one
	 * @throws NullPointerException - if argument was null
	 * @throws IllegalArgumentException - if argument was empty
	 */
	public AbstractApplicationOption(String stgFile, boolean required){
		Validate.notBlank(stgFile, "stgFile cannot be empty");

		STGroupFile stgf = new STGroupFile(stgFile);

		this.shortDescription = stgf.getInstanceOf("shortDescription").render();
		Validate.notBlank(this.shortDescription, "short description cannot be empty");

		this.longDescription = stgf.getInstanceOf("longDescription").render();
		Validate.notBlank(this.longDescription, "long description cannot be empty");

		String shortOpt = stgf.getInstanceOf("shortOption").render();
		if(!StringUtils.isEmpty(shortOpt)){
			Validate.validState(shortOpt.length()==1, "a short option must be a single character");
			Validate.validState(StringUtils.isAlphanumeric(shortOpt), "a short option must be a alphanumeric");
		}

		String longOpt = stgf.getInstanceOf("longOption").render();
		Validate.notEmpty(longOpt);
		Validate.validState(!StringUtils.contains(longOpt, ' '), "long options must not contain blanks");

		String argument = stgf.getInstanceOf("argument").render();

		Option.Builder builder = (StringUtils.isEmpty(shortOpt))?Option.builder():Option.builder(shortOpt);
		builder.longOpt(longOpt);
		if(!StringUtils.isEmpty(argument)){
			builder.hasArg().argName(argument);
		}
		builder.required(required);
		this.cliOption = builder.build();
		this.cliOption.setDescription(this.shortDescription);

		if(!StringUtils.isBlank(argument)){
			String ad = stgf.getInstanceOf("argumentHelp").render();
			if(!StringUtils.isBlank(ad)){
				this.cliArgDescription = ad;
			}
		}
	}

	@Override
	public Option getCliOption(){
		return this.cliOption;
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
	public String getShortDescription(){
		return this.shortDescription;
	}

	@Override
	public String getLongDescription(){
		return this.longDescription;
	}

	@Override
	public String getOptionKey(){
		return this.optionKey;
	}

	@Override
	public T getPropertValue(){
		return this.valueProperty;
	}

	@Override
	public boolean inCli(){
		return this.inCli;
	}

	/**
	 * Sets the CLI argument for this application option.
	 * @param shortOpt the short option, null if none wanted, alphanumeric character otherwise
	 * @param longOpt the long option, must not be blank
	 * @param arg an optional argument, null if none wanted, none-empty string otherwise
	 * @param required true for a required CLI option, false for an optional one
	 */
	protected final void setCliArgument(Character shortOpt, String longOpt, String arg, boolean required){
		Validate.validState(shortOpt==null || StringUtils.isAlphanumeric(shortOpt.toString()));
		Validate.notNull(longOpt);
		Validate.validState(arg==null || !StringUtils.isEmpty(arg));

		Option.Builder builder = (shortOpt==null)?Option.builder():Option.builder(shortOpt.toString());
		builder.longOpt(longOpt);
		if(arg!=null){
			builder.hasArg().argName(arg);
		}
		builder.required(required);
		this.cliOption = builder.build();
		this.cliOption.setDescription(this.shortDescription);
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
	public void setDefaultValue(T value){
		if(value!=null){
			this.ValueDefault = value;
		}
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

	/**
	 * Sets the argument description.
	 * @param description the argument description, must not be empty
	 */
	public void setCliArgumentDescription(String description){
		Validate.notEmpty(description);
		this.cliArgDescription = description;
	}

	@Override
	public String getCliArgumentDescription(){
		return this.cliArgDescription;
	}
}

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
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

/**
 * An application option of generic type with CLI option, property value, default value, and multiple levels of descriptions.
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.4.0 build 170413 (13-Apr-17) for Java 1.8
 * @since      v0.2.0
 */
public interface ApplicationOption <T> {

	/**
	 * Returns the CLI options a string.
	 * @return CLI option as string using the short CLI option if not null, long CLI option otherwise, null if no CLI option defined
	 */
	default String cliOptionAsString(){
		String ret = null;
		if(this.getCliOption()==null){
			return ret;
		}

		if(this.getCliOption().getOpt()!=null){
			ret = this.getCliOption().getOpt();
		}
		else if(this.getCliOption().getLongOpt()!=null){
			ret = this.getCliOption().getLongOpt();
		}
		return ret;
	}

	/**
	 * Converts a value of some type to the type the option is using
	 * @param value the value to be converted
	 * @return the value converted to the type of the option, null if not possible
	 */
	T convertValue(Object value);

	/**
	 * Returns the created CLI option.
	 * @return the created CLI option, null if none set
	 */
	Option getCliOption();

	/**
	 * Returns a description of an argument if required for a CLI option.
	 * @return the argument description, null of none set
	 */
	default String getCliArgumentDescription(){
		return null;
	}

	/**
	 * Returns the CLI value of the option if any set.
	 * @return CLI value, null if none set
	 */
	T getCliValue();

	/**
	 * Returns the default value of the option.
	 * @return option default value, blank if not set
	 */
	T getDefaultValue();

	/**
	 * Returns a short description for the option.
	 * @return short description for the option, should not be null
	 */
	String getShortDescription();

	/**
	 * Returns a long description of the option, possibly with use case and application specific information.
	 * @return long description for the option, blank if not set
	 */
	String getLongDescription();

	/**
	 * Returns the key used for this option for instance in a property file or object or a map.
	 * @return option key
	 */
	String getOptionKey();

	/**
	 * Returns the property value of the option.
	 * @return property value, blank if none set
	 */
	T getPropertValue();

	/**
	 * Returns a string with a long help on the CLI option, generated using an STG template.
	 * @return string with long help
	 */
	default String getCliLongHelp(){
		STGroupFile stg = new STGroupFile("de/vandermeer/execs/options/option-help.stg");
		ST st = stg.getInstanceOf("longCliHelp");

		st.add("shortOption", this.getCliOption().getOpt());
		st.add("longOption", this.getCliOption().getLongOpt());
		if(this.getCliOption().hasArg()){
			st.add("argument", this.getCliOption().getArgName());
			st.add("argumentDescription", this.getCliArgumentDescription());
		}
		if(this.getCliOption().isRequired()){
			st.add("required", this.getCliOption().getArgName());
		}
		st.add("shortDescription", this.getShortDescription());
		st.add("longDescription", this.getLongDescription());
		st.add("defaultValue", this.getDefaultValue());

		return st.render();
	}

	/**
	 * Returns a string with a long help on the KEY option, generated using an STG template.
	 * @return usage information
	 */
	default String getKeyLongHelp(){
		STGroupFile stg = new STGroupFile("de/vandermeer/execs/options/option-help.stg");
		ST st = stg.getInstanceOf("longKeyHelp");

		st.add("key", this.getOptionKey());
		st.add("shortOption", this.getCliOption().getOpt());
		st.add("longOption", this.getCliOption().getLongOpt());

		return st.render();
	}

	/**
	 * Returns the value of the option calculated from all possible values.
	 * First, if the CLI value is set it will be returned.
	 * Second, of the property value is set it will be returned.
	 * Third, if the default value is set it will be returned.
	 * Last, null is returned.
	 * @return option value determined from CLI value, property value and default value, blank if none set
	 */
	default T getValue(){
		if(this.getCliValue()!=null){
			return this.getCliValue();
		}
		if(this.getPropertValue()!=null){
			return this.getPropertValue();
		}
		return this.getDefaultValue();
	}

	/**
	 * Tests if the option was present in a parsed command line.
	 * @return true if it was present, false otherwise
	 */
	boolean inCli();

	/**
	 * Sets the CLI value of the option.
	 * @param cmdLine the processed command line
	 * @return 0 if process was successful (i.e. program can proceed), non 0 otherwise (means the program should proceed)
	 */
	int setCliValue(CommandLine cmdLine);

	/**
	 * Sets (overwrites) the default value if the given value is not null.
	 * @param value new default value
	 */
	void setDefaultValue(T value);

	/**
	 * Sets the property value of the option read from a property object.
	 * @param properties a property object to read the value from
	 * @return 0 if process was successful (i.e. program can proceed), non 0 otherwise (means the program should proceed)
	 */
	int setPropertyValue(Properties properties);

}

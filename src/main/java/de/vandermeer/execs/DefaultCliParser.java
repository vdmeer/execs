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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.Validate;

import de.vandermeer.skb.interfaces.application.ApoBaseC;
import de.vandermeer.skb.interfaces.application.Apo_SimpleC;
import de.vandermeer.skb.interfaces.application.Apo_TypedC;
import de.vandermeer.skb.interfaces.application.App_CliParser;
import de.vandermeer.skb.interfaces.application.CliParseException;

/**
 * The default CLI parser using Apache cli.
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.4.0 build 170413 (13-Apr-17) for Java 1.8
 * @since      v0.5.0
 */
public class DefaultCliParser implements App_CliParser {

	/** Simple CLI options. */
	final Map<Apo_SimpleC, Option> simpleOptions;

	/** Typed CLI options. */
	final Map<Apo_TypedC<?>, Option> typedOptions;

	/** Set of options already added, used. */
	protected final Set<String> usedOptions;

	/** Number of CLI arguments with short option. */
	protected int numberShort = 0;

	/** Number of CLI arguments with long option. */
	protected int numberLong = 0;

	/**
	 * Creates a new parser.
	 */
	public DefaultCliParser(){
		this.simpleOptions = new HashMap<>();
		this.typedOptions = new HashMap<>();
		this.usedOptions = new HashSet<>();
	}

	@Override
	public App_CliParser addOption(Object option) throws IllegalStateException {
		if(option==null){
			return this;
		}
		if(ClassUtils.isAssignable(option.getClass(), Apo_SimpleC.class)){
			this.addOption((Apo_SimpleC)option);
		}
		if(ClassUtils.isAssignable(option.getClass(), Apo_TypedC.class)){
			this.addOption((Apo_TypedC<?>)option);
		}
		return this;
	}

	/**
	 * Adds a new option to the parser.
	 * @param option the option to be added, ignored if `null`
	 * <T> type, simple option
	 * @return self to allow chaining
	 * @throws IllegalStateException if the option is already in use
	 */
	protected <T extends Apo_SimpleC> App_CliParser addOption(T option) throws IllegalStateException {
		if(option==null){
			return this;
		}
		Validate.validState(
				!this.getAddedOptions().contains(option.getCliShort()),
				"DefaultCliParser: short option <" + option.getCliShort() + "> already in use"
		);
		Validate.validState(
				!this.getAddedOptions().contains(option.getCliLong()),
				"DefaultCliParser: long option <" + option.getCliLong() + "> already in use"
		);

		Option.Builder builder = (option.getCliShort()==null)?Option.builder():Option.builder(option.getCliShort().toString());
		builder.longOpt(option.getCliLong());
		builder.required(option.cliIsRequired());

		this.simpleOptions.put(option, builder.build());
		if(option.getCliShort()!=null){
			this.usedOptions.add(option.getCliShort().toString());
		}
		this.usedOptions.add(option.getCliLong());

		if(option.getCliShort()!=null){
			this.numberShort++;
		}
		if(option.getCliLong()!=null){
			this.numberLong++;
		}
		return this;
	}

	/**
	 * Adds a new option to the parser.
	 * @param option the option to be added, ignored if `null`
	 * <T> type, typed option
	 * @return self to allow chaining
	 * @throws IllegalStateException if the option is already in use
	 */
	protected <T extends Apo_TypedC<?>> App_CliParser addOption(T option) throws IllegalStateException {
		if(option==null){
			return this;
		}
		Validate.validState(
				!this.getAddedOptions().contains(option.getCliShort()),
				"DefaultCliParser: short option <" + option.getCliShort() + "> already in use"
		);
		Validate.validState(
				!this.getAddedOptions().contains(option.getCliLong()),
				"DefaultCliParser: long option <" + option.getCliLong() + "> already in use"
		);

		Option.Builder builder = (option.getCliShort()==null)?Option.builder():Option.builder(option.getCliShort().toString());
		builder.longOpt(option.getCliLong());
		builder.hasArg().argName(option.getCliArgumentName());
		builder.optionalArg(option.cliArgIsOptional());
		builder.required(option.cliIsRequired());

		this.typedOptions.put(option, builder.build());
		if(option.getCliShort()!=null){
			this.usedOptions.add(option.getCliShort().toString());
		}
		this.usedOptions.add(option.getCliLong());

		if(option.getCliShort()!=null){
			this.numberShort++;
		}
		if(option.getCliLong()!=null){
			this.numberLong++;
		}

		return this;
	}

	@Override
	public Set<String> getAddedOptions() {
		return this.usedOptions;
	}

	@Override
	public Set<Apo_SimpleC> getSimpleOptions() {
		return this.simpleOptions.keySet();
	}

	@Override
	public Set<Apo_TypedC<?>> getTypedOptions() {
		return this.typedOptions.keySet();
	}

	@Override
	public int numberLong() {
		return this.numberLong;
	}

	@Override
	public int numberShort() {
		return this.numberShort;
	}

	@Override
	public void parse(String[] args) throws IllegalStateException, CliParseException {
		try{
			CommandLineParser parser = new DefaultParser();
			Options options = new Options();
			for(Option opt : this.simpleOptions.values()){
				options.addOption(opt);
			}
			for(Option opt : this.typedOptions.values()){
				options.addOption(opt);
			}
			CommandLine cmdLine = parser.parse(options, args);
			for(Apo_SimpleC simple : this.simpleOptions.keySet()){
				simple.setInCLi(cmdLine.hasOption(simple.getCliShortLong()));
			}
			for(Apo_TypedC<?> typed : this.typedOptions.keySet()){
				typed.setInCLi(cmdLine.hasOption(typed.getCliShortLong()));
				if(typed.inCli()){
					typed.setCliValue(cmdLine.getOptionValue(typed.getCliShortLong()));
				}
			}
		}
		catch(ParseException pe){
			throw new IllegalStateException(pe.getMessage());
		}
	}

	@Override
	public Set<ApoBaseC> getAllOptions() {
		Set<ApoBaseC> ret = new HashSet<>();
		ret.addAll(this.getSimpleOptions());
		ret.addAll(this.getTypedOptions());
		return ret;
	}

}

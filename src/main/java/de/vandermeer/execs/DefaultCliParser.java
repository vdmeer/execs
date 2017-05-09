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

import org.apache.commons.cli.AlreadySelectedException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.Validate;

import de.vandermeer.skb.interfaces.application.ApoBaseC;
import de.vandermeer.skb.interfaces.application.Apo_SimpleC;
import de.vandermeer.skb.interfaces.application.Apo_TypedC;
import de.vandermeer.skb.interfaces.application.App_CliParser;
import de.vandermeer.skb.interfaces.messagesets.IsErrorSet_IsError;
import de.vandermeer.skb.interfaces.messagesets.errors.IsError;
import de.vandermeer.skb.interfaces.messagesets.errors.Templates_CliGeneral;

/**
 * The default CLI parser using Apache cli.
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.4.0 build 170413 (13-Apr-17) for Java 1.8
 * @since      v0.5.0
 */
public class DefaultCliParser implements App_CliParser {

	/** Simple CLI options. */
	protected final transient Map<Apo_SimpleC, Option> simpleOptions;

	/** Typed CLI options. */
	protected final transient Map<Apo_TypedC<?>, Option> typedOptions;

	/** Set of options already added, used. */
	protected final transient Set<String> usedOptions;

	/** Number of CLI arguments with short option. */
	protected transient int shortOptions;

	/** Number of CLI arguments with long option. */
	protected transient int longOptions;

	/** Local set of errors, collected during execution printed at the end. */
	protected final transient IsErrorSet_IsError errorSet = IsErrorSet_IsError.create();

	/** Error number, holds the number of the last error, 0 if none occurred. */
	protected transient int errNo;

	/** Application name, mostly for error messages. */
	protected final transient String appName;

	/**
	 * Creates a new parser.
	 */
	public DefaultCliParser(final String appName){
		Validate.notBlank(appName);
		this.appName = appName;

		this.simpleOptions = new HashMap<>();
		this.typedOptions = new HashMap<>();
		this.usedOptions = new HashSet<>();
	}

	@Override
	public App_CliParser addOption(final Object option) throws IllegalStateException {
		if(option==null){
			return this;
		}
		if(ClassUtils.isAssignable(option.getClass(), Apo_SimpleC.class)){
			Apo_SimpleC opt = (Apo_SimpleC)option;
			if(opt.getCliShort()!=null){
				Validate.validState(
						!this.getAddedOptions().contains(opt.getCliShort().toString()),
						"DefaultCliParser: short option <" + opt.getCliShort() + "> already in use"
				);
			}
			if(opt.getCliLong()!=null){
				Validate.validState(
						!this.getAddedOptions().contains(opt.getCliLong()),
						"DefaultCliParser: long option <" + opt.getCliLong() + "> already in use"
				);
			}
			this.addOption(opt);
		}
		else if(ClassUtils.isAssignable(option.getClass(), Apo_TypedC.class)){
			Apo_TypedC<?> opt = (Apo_TypedC<?>)option;
			if(opt.getCliShort()!=null){
				Validate.validState(
						!this.getAddedOptions().contains(opt.getCliShort().toString()),
						"DefaultCliParser: short option <" + opt.getCliShort() + "> already in use"
				);
			}
			if(opt.getCliLong()!=null){
				Validate.validState(
						!this.getAddedOptions().contains(opt.getCliLong()),
						"DefaultCliParser: long option <" + opt.getCliLong() + "> already in use"
				);
			}
			this.addOption(opt);
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
	protected <T extends Apo_SimpleC> App_CliParser addOption(final T option) throws IllegalStateException {
		final Option.Builder builder = (option.getCliShort()==null)?Option.builder():Option.builder(option.getCliShort().toString());
		builder.longOpt(option.getCliLong());
		builder.required(option.cliIsRequired());

		this.simpleOptions.put(option, builder.build());
		if(option.getCliShort()!=null){
			this.usedOptions.add(option.getCliShort().toString());
			this.shortOptions++;
		}
		if(option.getCliLong()!=null){
			this.usedOptions.add(option.getCliLong());
			this.longOptions++;
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
	protected <T extends Apo_TypedC<?>> App_CliParser addOption(final T option) throws IllegalStateException {
		final Option.Builder builder = (option.getCliShort()==null)?Option.builder():Option.builder(option.getCliShort().toString());
		builder.longOpt(option.getCliLong());
		builder.hasArg().argName(option.getCliArgumentName());
		builder.optionalArg(option.cliArgIsOptional());
		builder.required(option.cliIsRequired());

		this.typedOptions.put(option, builder.build());
		if(option.getCliShort()!=null){
			this.usedOptions.add(option.getCliShort().toString());
			this.shortOptions++;
		}
		if(option.getCliLong()!=null){
			this.usedOptions.add(option.getCliLong());
			this.longOptions++;
		}

		return this;
	}

	@Override
	public Set<String> getAddedOptions() {
		return this.usedOptions;
	}

	@Override
	public Set<ApoBaseC> getAllOptions() {
		final Set<ApoBaseC> ret = new HashSet<>();
		ret.addAll(this.getSimpleOptions());
		ret.addAll(this.getTypedOptions());
		return ret;
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
	public IsErrorSet_IsError getErrorSet() {
		return this.errorSet;
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
		return this.longOptions;
	}

	@Override
	public int numberShort() {
		return this.shortOptions;
	}

	@Override
	public void parse(final String[] args){
		final CommandLineParser parser = new DefaultParser();
		final Options options = new Options();
		for(final Option opt : this.simpleOptions.values()){
			options.addOption(opt);
		}
		for(final Option opt : this.typedOptions.values()){
			options.addOption(opt);
		}

		CommandLine cmdLine = null;
		try {
			cmdLine = parser.parse(options, args, true);
		}
		catch(AlreadySelectedException ase){
			this.getErrorSet().addError(Templates_CliGeneral.ALREADY_SELECTED.getError(this.getAppName(), ase.getMessage()));
			this.errNo = Templates_CliGeneral.ALREADY_SELECTED.getCode();
		}
		catch(MissingArgumentException mae){
			this.getErrorSet().addError(Templates_CliGeneral.MISSING_ARGUMENT.getError(this.getAppName(), mae.getMessage()));
			this.errNo = Templates_CliGeneral.MISSING_ARGUMENT.getCode();
		}
		catch(MissingOptionException moe){
			this.getErrorSet().addError(Templates_CliGeneral.MISSING_OPTION.getError(this.getAppName(), moe.getMessage()));
			this.errNo = Templates_CliGeneral.MISSING_OPTION.getCode();
		}
		catch(UnrecognizedOptionException uoe){
			this.getErrorSet().addError(Templates_CliGeneral.UNRECOGNIZED_OPTION.getError(this.getAppName(), uoe.getMessage()));
			this.errNo = Templates_CliGeneral.UNRECOGNIZED_OPTION.getCode();
		}
		catch (ParseException ex) {
			this.getErrorSet().addError(Templates_CliGeneral.PARSE_EXCEPTION.getError(this.getAppName(), ex.getMessage()));
			this.errNo = Templates_CliGeneral.PARSE_EXCEPTION.getCode();
		}

		if(cmdLine!=null){
			for(final Apo_SimpleC simple : this.simpleOptions.keySet()){
				simple.setInCLi(cmdLine.hasOption(simple.getCliShortLong()));
			}
			for(final Apo_TypedC<?> typed : this.typedOptions.keySet()){
				typed.setInCLi(cmdLine.hasOption(typed.getCliShortLong()));
				if(typed.inCli()){
					IsError error = typed.setCliValue(cmdLine.getOptionValue(typed.getCliShortLong()));
					if(error!=null){
						this.errorSet.addError(error);
						this.errNo = error.getErrorCode();
					}
				}
			}
		}
	}

}

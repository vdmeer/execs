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
import org.apache.commons.lang3.Validate;

import de.vandermeer.skb.interfaces.application.Apo_SimpleC;
import de.vandermeer.skb.interfaces.application.Apo_TypedC;
import de.vandermeer.skb.interfaces.application.App_CliParser;

public class DefaultCliParser implements App_CliParser {

	final Map<Apo_SimpleC, Option> simpleOptions;

	final Map<Apo_TypedC<?>, Option> typedOptions;

	/** Set of options already added, used. */
	protected final Set<String> usedOptions;

	public DefaultCliParser(){
		this.simpleOptions = new HashMap<>();
		this.typedOptions = new HashMap<>();
		this.usedOptions = new HashSet<>();
	}

	@Override
	public Set<String> getAddedOptions() {
		return this.usedOptions;
	}

	@Override
	public void parse(String[] args) {
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
	public void usage(String appName) {
		// TODO Auto-generated method stub

	}

	@Override
	public <T extends Apo_SimpleC> App_CliParser addOption(T option) throws IllegalStateException {
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

		return this;
	}

	@Override
	public <T extends Apo_TypedC<?>> App_CliParser addOption(T option) throws IllegalStateException {
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

		return this;
	}

	@Override
	public Set<Apo_SimpleC> getSimpleOptions() {
		return this.simpleOptions.keySet();
	}

	@Override
	public Set<Apo_TypedC<?>> getTypedOptions() {
		return this.typedOptions.keySet();
	}

}

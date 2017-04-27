package de.vandermeer.execs;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.Validate;

import de.vandermeer.execs.options.simple.AO_HelpSimple;
import de.vandermeer.execs.options.simple.AO_Version_New;
import de.vandermeer.execs.options.typed.AO_HelpTyped;
import de.vandermeer.skb.interfaces.application.Apo_SimpleC;
import de.vandermeer.skb.interfaces.application.Apo_TypedC;
import de.vandermeer.skb.interfaces.application.Apo_TypedE;
import de.vandermeer.skb.interfaces.application.Apo_TypedP;
import de.vandermeer.skb.interfaces.application.App_CliParser;
import de.vandermeer.skb.interfaces.application.IsApplication;

public abstract class AbstractAppliction implements IsApplication {

	public final static int NO_HELP					= 0;

	public final static int HELP_SIMPLE_LONG 		= 1;

	public final static int HELP_SIMPLE_SHORTLONG	= 2;

	public final static int HELP_TYPED_LONG			= 3;

	public final static int HELP_TYPED_SHORTLONG	= 4;

	public final static int NO_VERSION			= 0;

	public final static int VERSION_LONG		= 1;

	public final static int VERSION_SHORTLONG	= 2;

	/** Application CLI parser. */
	protected final App_CliParser cli;

	protected final Set<Apo_TypedE<?>> environmentOptions;

	protected final Set<Apo_TypedP<?>> propertyOptions;

	protected final Apo_SimpleC aoSimpleHelp;

	protected final Apo_TypedC<String> aoTypedHelp;

	protected final Apo_SimpleC aoVersion;

	protected AbstractAppliction(App_CliParser cli, int useHelp, int useVersion){
		Validate.validState(cli!=null, "ExecS: CLI parser cannot be null");
		this.cli = cli;

		this.environmentOptions = new HashSet<>();
		this.propertyOptions = new HashSet<>();

		if(useHelp==HELP_SIMPLE_LONG){
			this.aoSimpleHelp = new AO_HelpSimple(null);
			this.aoTypedHelp = null;
		}
		else if(useHelp==HELP_SIMPLE_SHORTLONG){
			this.aoSimpleHelp = new AO_HelpSimple('h');
			this.aoTypedHelp = null;
		}
		else if(useHelp==HELP_TYPED_LONG){
			this.aoTypedHelp = new AO_HelpTyped(null);
			this.aoSimpleHelp = null;
		}
		else if(useHelp==HELP_TYPED_SHORTLONG){
			this.aoTypedHelp = new AO_HelpTyped('h');
			this.aoSimpleHelp = null;
		}
		else{
			this.aoSimpleHelp = null;
			this.aoTypedHelp = null;
		}

		if(useVersion==VERSION_LONG){
			this.aoVersion = new AO_Version_New(null);
		}
		else if(useVersion==VERSION_SHORTLONG){
			this.aoVersion = new AO_Version_New('h');
		}
		else{
			this.aoVersion = null;
		}
	}

	@Override
	public Set<Apo_TypedE<?>> getEnvironmentOptions() {
		return this.environmentOptions;
	}

	@Override
	public Set<Apo_TypedP<?>> getPropertyOptions() {
		return this.propertyOptions;
	}

	@Override
	public Apo_SimpleC cliSimpleHelpOption(){
		return this.aoSimpleHelp;
	}

	@Override
	public Apo_TypedC<String> cliTypedHelpOption(){
		return this.aoTypedHelp;
	}

	@Override
	public Apo_SimpleC cliVersionOption(){
		return this.aoVersion;
	}

	@Override
	public App_CliParser getCliParser() {
		return this.cli;
	}
}

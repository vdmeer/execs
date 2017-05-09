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

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.stringtemplate.v4.ST;

import de.vandermeer.execs.options.simple.AO_HelpSimple;
import de.vandermeer.execs.options.typed.AO_Columns;
import de.vandermeer.skb.interfaces.MessageConsole;
import de.vandermeer.skb.interfaces.application.ApoCliParser;
import de.vandermeer.skb.interfaces.application.ApoEnvParser;
import de.vandermeer.skb.interfaces.application.ApoPropParser;
import de.vandermeer.skb.interfaces.application.Apo_SimpleC;
import de.vandermeer.skb.interfaces.application.Apo_TypedC;
import de.vandermeer.skb.interfaces.application.IsApplication;
import de.vandermeer.skb.interfaces.messagesets.IsErrorSet_IsError;
import de.vandermeer.skb.interfaces.messagesets.IsInfoSet_FT;
import de.vandermeer.skb.interfaces.messagesets.IsWarningSet_FT;

/**
 * Abstract implementation of an application.
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.4.0 build 170413 (13-Apr-17) for Java 1.8
 * @since      v0.5.0
 */
public abstract class AbstractAppliction implements IsApplication {

	/** Application name. */
	protected final transient String appName;

	/** Application CLI parser. */
	protected final transient ApoCliParser cliParser;

	/** Application Environment parser. */
	protected final transient ApoEnvParser envParser;

	/** Application Property parser. */
	protected final transient ApoPropParser propParser;

	/** A simple help object, null if not required. */
	protected final transient Apo_SimpleC optionSimpleHelp;

	/** A typed help object, null if not required. */
	protected final transient Apo_TypedC<String> optionTypedHelp;

	/** A simple version object, null if not required. */
	protected final transient Apo_SimpleC optionVersion;

	/** Option for getting column (console width). */
	protected final transient AO_Columns optionColumns = new AO_Columns(null);

	/** Local set of errors, collected during execution printed at the end. */
	protected final transient IsErrorSet_IsError errorSet = IsErrorSet_IsError.create();

	/** Local set of warnings, collected during execution printed at the end. */
	protected final transient IsWarningSet_FT warningSet = IsWarningSet_FT.create();

	/** Local set of information, collected during execution printed at the end. */
	protected final transient IsInfoSet_FT informationSet = IsInfoSet_FT.create();

	/** Error number, holds the number of the last error, 0 if none occurred. */
	protected transient int errNo;

	/**
	 * Creates a new abstract application.
	 * @param appName the application name, must not be blank
	 * @param cliParser the CLI parser, must not be null
	 * @param simpleHelp an optional simple help, null of not required
	 * @param typedHelp an optional typed help, null if not required, if used with simpleHelp only typedHelp will be added
	 * @param version an optional version, null if not required
	 */
	protected AbstractAppliction(final String appName, final ApoCliParser cliParser, final AO_HelpSimple simpleHelp, final Apo_TypedC<String> typedHelp, final Apo_SimpleC version){
		this(appName, cliParser, null, null, simpleHelp, typedHelp, version);
	}

	/**
	 * Creates a new abstract application.
	 * @param appName the application name, must not be blank
	 * @param cliParser the CLI parser, must not be null
	 * @param envParser the environment parser, null if default should be used
	 * @param propParser the property parser, null if default should be used
	 * @param simpleHelp an optional simple help, null of not required
	 * @param typedHelp an optional typed help, null if not required, if used with simpleHelp only typedHelp will be added
	 * @param version an optional version, null if not required
	 */
	protected AbstractAppliction(final String appName, final ApoCliParser cliParser, final ApoEnvParser envParser, final ApoPropParser propParser, final AO_HelpSimple simpleHelp, final Apo_TypedC<String> typedHelp, final Apo_SimpleC version){
		this.appName = appName;

		this.cliParser = cliParser;
		this.envParser = (envParser==null)?ApoEnvParser.create(appName):envParser;
		this.propParser = (propParser==null)?ApoPropParser.create(appName, true):propParser;

		this.addOption(this.optionColumns);
		this.optionSimpleHelp = (typedHelp!=null)?null:simpleHelp;
		this.optionTypedHelp = typedHelp;
		this.optionVersion = version;
	}

	@Override
	public Apo_SimpleC cliSimpleHelpOption(){
		return this.optionSimpleHelp;
	}

	@Override
	public Apo_TypedC<String> cliTypedHelpOption(){
		return this.optionTypedHelp;
	}

	@Override
	public Apo_SimpleC cliVersionOption(){
		return this.optionVersion;
	}

	@Override
	public String getAppName() {
		return this.appName;
	}

	@Override
	public ApoCliParser getCliParser() {
		return this.cliParser;
	}

	@Override
	public int getConsoleWidth(){
		return this.optionColumns.getValue();
	}

	@Override
	public ApoEnvParser getEnvironmentParser() {
		return this.envParser;
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
	public IsInfoSet_FT getInfoSet() {
		return this.informationSet;
	}

	@Override
	public ApoPropParser getPropertyParser() {
		return this.propParser;
	}

	@Override
	public IsWarningSet_FT getWarningSet() {
		return this.warningSet;
	}

	/**
	 * Prints errors using the message console.
	 */
	protected void printErrors(){
		if(this.errorSet.hasErrors()){
			MessageConsole.conError(this.errorSet.render());
		}
	}

	@Override
	public void setErrno(int errorNumber) {
		this.errNo = errorNumber;
	}

	@Override
	public String translateLongDescription(final Object longDescription) {
		if(longDescription==null){
			return null;
		}
		String ret = null;
		if(ClassUtils.isAssignable(longDescription.getClass(), String.class)){
			ret = (String)longDescription;
		}
		else if(ClassUtils.isAssignable(longDescription.getClass(), ST.class)){
			ret = ((ST)longDescription).render();
		}
		else{
			ret = longDescription.toString();
		}

		if(StringUtils.isBlank(ret)){
			return null;
		}
		return ret;
	}
}

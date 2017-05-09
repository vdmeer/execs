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

import de.vandermeer.execs.options.Option_TypedP_Boolean;
import de.vandermeer.execs.options.Option_TypedP_String;

/**
 * Options, mostly property options, for the local script generation applications.
 * 
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.4.0 build 170413 (13-Apr-17) for Java 1.8
 * @since      v0.0.1
 */
public interface GenAop {

	static Option_TypedP_Boolean AUTOGEN_REGISTERED(){
		return new Option_TypedP_Boolean(
				"AUTOGEN_REGISTERED",
				"execs.autogenerate.registered",
				false,
				"flag for auto-script generation for registered executable applications",
				"A property key to set auto-script generation for registered executable applications."
		);
	}

	static Option_TypedP_String JAVA_CP(){
		return new Option_TypedP_String(
				"JAVA_CP",
				"java.classpath",
				true,
				"the JAVA class path",
				"A property key for the JAVA class path, comma separates list, {APPLICATION_HOME} will be added to all entries"
		);
	}

	static Option_TypedP_String JVM_RUNTIME_OPTIONS(){
		return new Option_TypedP_String(
				"JVM_RUNTIME_OPTIONS",
				"jvm.runtime",
				false,
				"JVM runtime options",
				"JVM runtime options as comma-separated list in the form of 'key:value'. Each entry of the list will be translated to '-Dkey:value'. For example 'file.encoding:UTF-8' will be translated to '-Dfile.encoding:UTF-8'. {APPLICATION_HOME} will be translated for target platform."
		);
	}

	static Option_TypedP_String RUN_CLASS(){
		return new Option_TypedP_String(
				"RUN_CLASS",
				"run.script.class",
				true,
				"the class name of the ExexS executor to be used, must be a fully qualified class name",
				"A property key for the class name of the ExexS executor to be used, must be a fully qualified class name."
		);
	}

	static Option_TypedP_String RUN_SCRIPT_NAME(){
		return new Option_TypedP_String(
				"RUN_SCRIPT_NAME",
				"run.script.name",
				true,
				"the script name of the generic run script, without file extension",
				"A property key for the script name of the generic run script, without file extension."
		);
	}
}

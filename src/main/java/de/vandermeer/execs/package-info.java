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

/**
 * A simple set of classes to execute programs from the command line, without littering the code with <code>static void main(String[] args)</code>.
 * 
 * <p>
 * This package addresses the following problem: a package (or a set of packages) contain
 * a number of tools that should be run from outside java, e.g. using a <code>static main()</code> method.
 * Over time, it might (will) be hard to know (or find) all main methods.
 * </p>
 * 
 * <p>
 * This package provides a standard way to find and execute all those tools, or as they are called here services.
 * A simple interface should be used by any component that wants to be executed. Implementing {@link de.vandermeer.execs.ExecutableService}
 * provides the main method <code>executeService()</code> and a method for printing a help screen. The only requirement is that the default
 * constructor must be provided, to allow for a generic instantiation at runtime.
 * </p>
 * 
 * <p>
 * The {@link de.vandermeer.execs.cf.CF} can be used to search all or filtered jar files (or URIs) for any class that
 * implement the interface. Once all implementations are found, one can use the {@link de.vandermeer.execs.ExecS} object with
 * its <code>static main()</code> to execute any service from the command line.
 * </p>
 * 
 * <p>
 * The {@link de.vandermeer.execs.ExecS} class allows to list all executable services, execute one service triggering a help screen and
 * to register short names for services. These short names can be used as aliases to execute a service, otherwise a fully qualified class
 * name will be required.
 * </p>
 * 
 * <p>
 * One can also extend the {@link de.vandermeer.execs.ExecS} class to build more sophisticated versions of the execution object, to
 * register specific short names or to search for other interfaces and classes in jar files at runtime.
 * </p>
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.0.8 build 150721 (21-Jul-15) for Java 1.8
 * @since      v0.0.1
 */
package de.vandermeer.execs;
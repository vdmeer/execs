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

package de.vandermeer.execs.cf;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.lang3.StringUtils;

/**
 * A class finder searching for all subclasses of a given class with jar and package filters.
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.3.9-SNAPSHOT build 170411 (11-Apr-17) for Java 1.8
 * @since      v0.0.6
 */
public class CF {

	/** A locator object for jars in the class path. */
	protected final CF_Locator locator;

	/** A list of errors that might have been collected during a search. */
	protected final List<Throwable> errors;

	/** A boolean indicating if the locator need to run again, should be true when filters are changed. */
	protected boolean needsReRun;

	/** List of processed artifacts. */
	protected final Set<String> processed;

	/** Array of (partial) class and package names that are permanently excluded from search. */
	public String[] excludedNames = new String[]{
			"java.",
			"javax.",
			"org.apache.",
			"ch.qos.",
			"org.codehaus.",
			"junit.",
			"org.antlr.",
			"org.stringtemplate.",
			"org.jboss.",
			"org.slf4j."
	};

	/**
	 * Returns a new class finder object.
	 */
	public CF(){
		this.locator = new CF_Locator();
		this.errors = new ArrayList<Throwable>();
		this.processed = new HashSet<>();
		this.clearFilters();
	}

	/**
	 * Clears all settings, except the filters, on the class finder.
	 * @return self to allow for chaining
	 */
	public CF clear(){
		this.errors.clear();
		this.processed.clear();
		return this;
	}

	/**
	 * Clears all filters on the class finder.
	 * @return self to allow for chaining
	 */
	public CF clearFilters(){
		this.setJarFilter(null);
		this.setPkgFilter(null);
		return this;
	}

	/**
	 * Sets a jar filter.
	 * @param jarFilter new filter, use null or empty list to not filter. The list will be copied locally.
	 * @return self to allow for chaining
	 */
	public CF setJarFilter(List<String> jarFilter){
		this.locator.setJarFilter(jarFilter);
		this.needsReRun = true;
		return this;
	}

	/**
	 * Sets a package filter.
	 * @param pkgFilter new package filter, set to null to not filter
	 * @return self to allow for chaining
	 */
	public CF setPkgFilter(String pkgFilter){
		this.locator.setPkgFilter(pkgFilter);
		this.needsReRun = true;
		return this;
	}

	/**
	 * Returns a list of errors collected during a search.
	 * The list will be reset by any new search.
	 * @return list of collected errors
	 */
	public List<Throwable> getLastErrors(){
		return this.errors;
	}

	/**
	 * Returns all subclasses found for the given class.
	 * @param clazz class to search for
	 * @return all found subclasses
	 */
	public Set<Class<?>> getSubclasses(Class<?> clazz){
		Set<Class<?>> ret = new HashSet<Class<?>>();
		Set<Class<?>> w = null;

		if(clazz!=null){
			this.clear();
			Map<URI, String> locations = this.locator.getCfLocations();
			for(Entry<URI, String> entry : locations.entrySet()){
				try{
					w = search(clazz, entry.getKey(), locations.get(entry.getKey()));
					if(w!=null && (w.size()>0)){
						ret.addAll(w);
					}
				}
				catch(MalformedURLException ex){}
			}
		}

		return ret;
	}

	/**
	 * Returns all subclasses found for the given fully qualified class name.
	 * @param fqcn fully qualified class name, e.g. package and class name
	 * @return all found subclasses
	 */
	public Set<Class<?>> getSubclasses(String fqcn){
		if(fqcn==null){
			return new HashSet<Class<?>>();
		}
		else if(StringUtils.startsWith(fqcn, ".") || StringUtils.endsWith(fqcn, ".")){
			return new HashSet<Class<?>>();
		}

		Class<?> clazz = null;
		try{
			clazz = Class.forName(fqcn);
		}
		catch(ClassNotFoundException ex){
			this.clear();
			this.errors.add(ex);
			return new HashSet<Class<?>>();
		}
		return getSubclasses(clazz);
	}

	/**
	 * Returns all known subclasses for a given class, location and package name
	 * @param clazz class to search for
	 * @param location a URL to look into
	 * @param packageName a package name
	 * @return all found subclasses in location, empty set of none found
	 * @throws MalformedURLException
	 */
	private final Set<Class<?>> search(Class<?> clazz, URI location, String packageName) throws MalformedURLException{
		if(clazz==null || location==null){
			return new HashSet<Class<?>>();
		}

		File directory = new File(location.toURL().getFile());
		if(directory.exists()){
			return this.searchDirectory(clazz, directory, location, packageName).keySet();
		}
		else{
			return this.searchJar(clazz, location).keySet();
		}
	}

	/**
	 * Returns all known subclasses found in a given directory.
	 * @param clazz class name to look for
	 * @param directory search directory
	 * @param location a URI location for return map
	 * @param packageName a package name to test against class name
	 * @return a map with found classes, empty if none found
	 */
	protected final Map<Class<?>, URI> searchDirectory(Class<?> clazz, File directory, URI location, String packageName){
		Map<Class<?>, URI> ret = new HashMap<>();

		String[] files = directory.list();
		for(int i=0; i<files.length; i++){
			if(files[i].endsWith(".class")){
				String classname = files[i].substring(0, files[i].length()-6);
				try{
					Class<?> c = Class.forName(packageName + "." + classname);
					if(clazz.isAssignableFrom(c) && !clazz.getName().equals(packageName + "." + classname)){
						ret.put(c, location);
					}
				}
				catch(Exception ex){
					errors.add(ex);
				}
			}
		}
		return ret;
	}

	/**
	 * Returns all known subclasses found in a given location
	 * @param clazz class name to look for
	 * @param location a URI location for return map
	 * @return a map with found classes, empty if none found
	 */
	protected final Map<Class<?>, URI> searchJar(Class<?> clazz, URI location){
		Map<Class<?>, URI> ret = new HashMap<>();

		try{
			JarURLConnection conn = (JarURLConnection)location.toURL().openConnection();
			JarFile jarFile = conn.getJarFile();

			for(Enumeration<JarEntry> e = jarFile.entries(); e.hasMoreElements();){
				JarEntry entry = e.nextElement();
				String entryname = entry.getName();
				if(this.processed.contains(entryname)){
					continue;
				}
				this.processed.add(entryname);

				if(!entry.isDirectory() && entryname.endsWith(".class")){
					String classname = entryname.substring(0, entryname.length()-6);
					if(classname.startsWith("/")){
						classname = classname.substring(1);
					}
					classname = classname.replace('/','.');
					if(!StringUtils.startsWithAny(classname, this.excludedNames)){
						try{
							Class<?> c = Class.forName(classname);
							if(clazz.isAssignableFrom(c) && !clazz.getName().equals(classname)){
								ret.put(c, location);
							}
						}
						catch(Exception exception){
							errors.add(exception);
						}
						catch(Error error){
							errors.add(error);
						}
					}
				}
			}
		}
		catch(IOException ignore){
			errors.add(ignore);
		}

		return ret;
	}

}

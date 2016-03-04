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
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * A locator for jar files with jar and package filter.
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.3.6-SNAPSHOT build 160304 (04-Mar-16) for Java 1.8
 * @since      v0.0.6
 */
public class CF_Locator {

	/** Location map to be filled with jar locations. */
	protected final Map<URI, String> locationMap;

	/** A list of jar names to be filtered for, that is only look for those jars. */
	protected final List<String> jarFilter;

	/** A string naming the start of a package for filter, that is only use packages starting with that string. */
	protected String pkgFilter;

	/** A boolean indicating if the locator need to run again, should be true when filters are changed. */
	protected boolean needsReRun;

	/**
	 * Returns a new CF locator object.
	 */
	public CF_Locator(){
		this.locationMap = new HashMap<>();
		this.jarFilter = new ArrayList<>();
		this.needsReRun = true;
	}

	/**
	 * Sets a jar filter.
	 * @param jarFilter new filter, use null or empty list to not filter. The list will be copied locally.
	 * @return self to allow for chaining
	 */
	public CF_Locator setJarFilter(List<String> jarFilter){
		this.jarFilter.clear();
		if(jarFilter!=null){
			this.jarFilter.addAll(jarFilter);
		}
		this.needsReRun = true;
		return this;
	}

	/**
	 * Sets a package filter.
	 * @param pkgFilter new package filter, set to null to not filter
	 * @return self to allow for chaining
	 */
	public CF_Locator setPkgFilter(String pkgFilter){
		this.pkgFilter = pkgFilter;
		this.needsReRun = true;
		return this;
	}

	/**
	 * Runs the locator and collects all locations using the filters if set.
	 * The method can be called multiple times and will only result in a new map if any of the filters have been changed.
	 * If no filter has been changed, the current map will be returned.
	 * @return returns a map with all found locations, empty if none found
	 */
	public Map<URI, String> getCfLocations(){
		if(this.needsReRun==true){
			this.locationMap.clear();
			String pathSep = System.getProperty("path.separator");
			String classpath = System.getProperty("java.class.path");
			StringTokenizer st = new StringTokenizer(classpath, pathSep);

			File file = null;
			while(st.hasMoreTokens()){
				String path = st.nextToken();
				file = new File(path);
				this.include(file);
			}
		}

		this.needsReRun = false;
		return this.locationMap;
	}

	/**
	 * Include a file.
	 * @param file file to be included
	 */
	protected final void include(File file){
		this.include(null, file);
	}

	/**
	 * Include a name and file
	 * @param name name to be included, if null will be set to empty
	 * @param file file to be included
	 */
	protected final void include(String name, File file){
		if(!file.exists()){
			return;
		}
		if(!file.isDirectory()){
			if(this.jarFilter.size()>0){
				boolean ok = false;
				for(String s : this.jarFilter){
					if(file.getName().startsWith(s)){
						ok = true;
					}
				}
				if(ok==false){
					return;
				}
			}
			this.includeJar(file);
			return;
		}

		if(name==null){
			name = "";
		}
		else{
			name += ".";
		}

		File[] dirs = file.listFiles(CF_Utils.DIRECTORIES_ONLY);
		for(int i=0; i<dirs.length; i++){
			try{
				this.locationMap.put(new URI("file://" + dirs[i].getCanonicalPath()), name + dirs[i].getName());
			}
			catch(IOException ignore){return;}
			catch(URISyntaxException ignore){return;}

			this.include(name + dirs[i].getName(), dirs[i]);
		}
	}

	/**
	 * Include from a jar file
	 * @param file jar file
	 */
	private void includeJar(File file){
		if(file.isDirectory()){
			return;
		}

		URL jarURL = null;
		JarFile jar = null;
		try{
			jarURL = new URL("jar:" + new URL("file:/" + file.getCanonicalPath()).toExternalForm() + "!/");
			JarURLConnection conn = (JarURLConnection)jarURL.openConnection();
			jar = conn.getJarFile();
		}
		catch(MalformedURLException ignore){return;}
		catch(IOException ignore){return;}

		if(jar==null){
			return;
		}

		try{
			this.locationMap.put(jarURL.toURI(), "");
		}
		catch(URISyntaxException ignore){}

		for(Enumeration<JarEntry> e = jar.entries(); e.hasMoreElements();){
			JarEntry entry = e.nextElement();
			if(this.pkgFilter!=null && entry.getName().startsWith(this.pkgFilter)){
				continue;
			}
			if(entry.isDirectory()){
				if(entry.getName().toUpperCase(Locale.ENGLISH).equals("META-INF/")){
					continue;
				}
				try{
					this.locationMap.put(new URI(jarURL.toExternalForm() + entry.getName()), CF_Utils.getPkgName(entry));
				}
				catch(URISyntaxException ignore){continue;}
			}
		}
	}

}

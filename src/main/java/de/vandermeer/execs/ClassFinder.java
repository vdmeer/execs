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

package de.vandermeer.execs;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * A class finder.
 * Searches all known jars and folders from the class path for a given class.
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.0.6-SNAPSHOT build 150630 (30-Jun-15) for Java 1.8
 */
@Deprecated
public class ClassFinder {

	private List<Throwable> errors;

	private Set<String> processed;

	public ClassFinder(){
		this.errors = new ArrayList<Throwable>();
		this.processed = new HashSet<String>();
	}

	/**
	 * Returns a map with URI and names from the given class path, with filters if set.
	 * @param jarFilter filter for jar names, null for no filter
	 * @param pkgFilter filter for package name, the root package to look for, null for no filter
	 * @return map of URIs and names
	 */
	public final Map<URI, String> getClasspathLocations(List<String> jarFilter, String pkgFilter) {
		Map<URI, String> map = new HashMap<URI, String>();
		File file = null;

		String pathSep = System.getProperty("path.separator");
		String classpath = System.getProperty("java.class.path");

		StringTokenizer st = new StringTokenizer(classpath, pathSep);
		while(st.hasMoreTokens()){
			String path = st.nextToken();
			file = new File(path);
			this.include(null, file, map, jarFilter, pkgFilter);
		}
		return map;
	}

	/**
	 * A simple filter for directories.
	 */
	public final static FileFilter DIRECTORIES_ONLY = new FileFilter(){
		public boolean accept (File f){
			if (f.exists() && f.isDirectory()){
				return true;
			}
			return false;
		}
	};

	private final void include(String name, File file, Map<URI, String> map, List<String> jarFilter, String pkgFilter){
		if(!file.exists()){
			return;
		}
		if(!file.isDirectory()){
			if(jarFilter!=null){
				boolean ok=false;
				for(String s : jarFilter){
					if(file.getName().startsWith(s)){
						ok = true;
					}
				}
				if(ok==false){
					return;
				}
			}
			this._includeJar(file, map, pkgFilter);
			return;
		}

		if(name==null){
			name = "";
		}
		else{
			name += ".";
		}

		File [] dirs = file.listFiles(ClassFinder.DIRECTORIES_ONLY);
		for(int i=0; i<dirs.length; i++){
			try{
				map.put(new URI("file://" + dirs[i].getCanonicalPath()), name + dirs[i].getName());
			}
			catch(IOException ignore){return;}
			catch(URISyntaxException ignore){return;}

			this.include(name + dirs[i].getName(), dirs[i], map, jarFilter, pkgFilter);
		}
	}

	private void _includeJar(File file, Map<URI, String> map, String pkgFfilter){
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
			map.put(jarURL.toURI(), "");
		}
		catch(URISyntaxException ignore){}

		for(Enumeration<JarEntry> e = jar.entries(); e.hasMoreElements();){
			JarEntry entry = e.nextElement();
			if(pkgFfilter!=null && entry.getName().startsWith(pkgFfilter)){
				continue;
			}
			if(entry.isDirectory()){
				if(entry.getName().toUpperCase(Locale.ENGLISH).equals("META-INF/")){
					continue;
				}
				try{
					map.put(new URI(jarURL.toExternalForm() + entry.getName()), this._packageNameFor(entry));
				}
				catch(URISyntaxException ignore){continue;}
			}
		}
	}

	private String _packageNameFor(JarEntry entry){
		if(entry==null){
			return "";
		}
		String s=entry.getName();
		if(s==null){
			return "";
		}
		if(s.length()==0){
			return s;
		}
		if(s.startsWith("/")){
			s = s.substring(1, s.length());
		}
		if(s.endsWith("/")){
			s = s.substring(0, s.length()-1);
		}
		return s.replace('/', '.');
	}

	private Set<Class<?>> findSubclasses(Class<?> searchClass, Map<URI, String> locations){
		Set<Class<?>> ret = new HashSet<Class<?>>();
		Set<Class<?>> w = null;

		for(Entry<URI, String> entry : locations.entrySet()){
			try{
				w = findSubclasses(searchClass, entry.getKey(), locations.get(entry.getKey()));
				if(w!=null && (w.size()>0)){
					ret.addAll(w);
				}
			}
			catch(MalformedURLException ex){}
		}
		return ret;
	}

	private final Set<Class<?>> findSubclasses(Class<?> searchClass, URI location, String packageName) throws MalformedURLException{
		Map<Class<?>, URI> ret = new HashMap<Class<?>, URI>();
		if(searchClass==null || location==null){
			return null;
		}
		String fqcn = searchClass.getName();
		File directory = new File(location.toURL().getFile());

		if(directory.exists()){
			String[] files = directory.list();
			for(int i=0; i<files.length; i++){
				if(files[i].endsWith(".class")){
					String classname = files[i].substring(0, files[i].length()-6);
					try{
						Class<?> c = Class.forName(packageName + "." + classname);
						if(searchClass.isAssignableFrom(c) && !fqcn.equals(packageName + "." + classname)){
							ret.put(c, location);
						}
					}
					catch(Exception ex){
						errors.add(ex);
					}
				}
			}
		}
		else{
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
						try{
							Class<?> c=Class.forName(classname);
							if(searchClass.isAssignableFrom(c) && !fqcn.equals(classname)){
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
			catch(IOException ignore){
				errors.add(ignore);
			}
		}
		return ret.keySet();
	}

	/**
	 * Returns errors that might have occured during search.
	 * @return list of errors
	 */
	public final List<Throwable> getErrors(){
		return this.errors;
	}

	/**
	 * Searches for all classes that are a subclass of the given FQCN, with filters.
	 * @param fqcn fully qualified class name of the base class
	 * @param jarFilter filter for jar files, use null for no filter
	 * @param pkgFilter filter for packages, here the root package, use null for no filter
	 * @return a set of all classes found
	 */
	public Set<Class<?>> findSubclasses(String fqcn, List<String> jarFilter, String pkgFilter){
		this.errors.clear();
		this.processed.clear();
		if(fqcn.startsWith(".") || fqcn.endsWith(".")){
			return new HashSet<Class<?>>();
		}

		Class<?> searchClass = null;
		try{
			searchClass = Class.forName(fqcn);
		}
		catch(ClassNotFoundException ex){
			this.errors.add(ex);
			return new HashSet<Class<?>>();
		}
		return findSubclasses(searchClass, this.getClasspathLocations(jarFilter, pkgFilter));
	}

	/**
	 * Searches for all classes that are a subclass of the given FQCN.
	 * @param fqcn fully qualified class name of the base class
	 * @return a set of all classes found
	 */
	public Set<Class<?>> findSubclasses(String fqcn){
		return this.findSubclasses(fqcn, null, null);
	}
}

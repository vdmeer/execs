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
import java.io.FileFilter;
import java.util.jar.JarEntry;

/**
 * Class finder utilities.
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.3.4-SNAPSHOT build 160301 (01-Mar-16) for Java 1.8
 * @since      v0.0.6
 */
public abstract class CF_Utils {

	/**
	 * A simple filter for directories.
	 * The filter returns true if the given file exists and is a directory, false otherwise.
	 */
	public final static FileFilter DIRECTORIES_ONLY = new FileFilter(){
		public boolean accept (File f){
			if (f.exists() && f.isDirectory()){
				return true;
			}
			return false;
		}
	};

	/**
	 * Returns the package name for a jar entry.
	 * @param entry jar entry to extract package name from
	 * @return empty string on error (entry being null, or no name), package name otherwise
	 */
	public final static String getPkgName(JarEntry entry){
		if(entry==null){
			return "";
		}
		String s = entry.getName();
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

}

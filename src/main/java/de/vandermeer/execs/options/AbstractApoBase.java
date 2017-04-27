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

package de.vandermeer.execs.options;

import org.apache.commons.lang3.StringUtils;
import org.stringtemplate.v4.ST;

import de.vandermeer.skb.interfaces.application.ApoBase;

/**
 * Base for an application option.
 *
 * @author     Sven van der Meer &lt;vdmeer.sven@mykolab.com&gt;
 * @version    v0.4.0 build 170413 (13-Apr-17) for Java 1.8
 * @since      v0.5.0
 */
public abstract class AbstractApoBase implements ApoBase {

	protected final String descr;

	protected String longDescr;

	protected AbstractApoBase(String description){
		this.descr = description;
	}

	@Override
	public String getDescription() {
		return this.descr;
	}

	@Override
	public void setLongDescription(String description){
		if(!StringUtils.isBlank(description)){
			this.longDescr = description;
		}
	}

	@Override
	public void setLongDescription(ST description){
		if(description==null){
			return;
		}
		this.setLongDescription(description.render());
	}

	@Override
	public String getLongDescription() {
		return this.longDescr;
	}

}

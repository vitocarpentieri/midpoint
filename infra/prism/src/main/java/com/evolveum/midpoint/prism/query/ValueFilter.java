/*
 * Copyright (c) 2010-2013 Evolveum
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

package com.evolveum.midpoint.prism.query;

import org.w3c.dom.Element;

import com.evolveum.midpoint.prism.ItemDefinition;
import com.evolveum.midpoint.prism.path.ItemPath;

public abstract class ValueFilter extends ObjectFilter {
	
	private ItemPath parentPath;
	private ItemDefinition definition;
	private String matchingRule;
	
	public ValueFilter() {
		// TODO Auto-generated constructor stub
	}
	
	public ValueFilter(ItemPath parentPath, ItemDefinition definition){
		this.parentPath = parentPath;
		this.definition = definition;
	}
	
	public ValueFilter(ItemPath parentPath, ItemDefinition definition, String matchingRule){
		this.parentPath = parentPath;
		this.definition = definition;
		this.matchingRule = matchingRule;
	}
	
	public ValueFilter(ItemPath parentPath, ItemDefinition definition, String matchingRule, Element expression){
		super(expression);
		this.parentPath = parentPath;
		this.definition = definition;
		this.matchingRule = matchingRule;
	}
	
	public ValueFilter(ItemPath parentPath, ItemDefinition definition, Element expression){
		super(expression);
		this.parentPath = parentPath;
		this.definition = definition;
	}
	
	public ItemDefinition getDefinition() {
		return definition;
	}
	
	public void setDefinition(ItemDefinition definition) {
		this.definition = definition;
	}
	
	public ItemPath getParentPath() {
		return parentPath;
	}
	
	public void setParentPath(ItemPath path) {
		this.parentPath = path;
	}
	
	public String getMatchingRule() {
		return matchingRule;
	}
	
	public void setMatchingRule(String matchingRule) {
		this.matchingRule = matchingRule;
	}
	
	protected void cloneValues(ValueFilter clone) {
		super.cloneValues(clone);
		clone.parentPath = this.parentPath;
		clone.definition = this.definition;
		clone.matchingRule = this.matchingRule;
	}

}

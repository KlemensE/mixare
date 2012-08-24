/*
 * Copyright (C) 2012- Peer internet solutions
 * 
 * This file is part of mixare.
 * 
 * This program is free software: you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version. 
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License 
 * for more details. 
 * 
 * You should have received a copy of the GNU General Public License along with 
 * this program. If not, see <http://www.gnu.org/licenses/>
 */
package org.mixare.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * This class holds the additional Parameter to create the URL
 * 
 * @author KlemensE
 * 
 */
public class ParamHolder implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6725075369436430743L;
	private Map<String, String> map;

	public ParamHolder() {
		this.map = new HashMap<String, String>();
	}

	public ParamHolder(Map<String, String> map) {
		this.map = map;
	}

	public void addToMap(String key, String value) {
		this.map.put(key, value);
	}

	public Map<String, String> getMap() {
		return this.map;
	}
	
	@Override
	public String toString() {
		String ret = "map:{";
		if (this.map != null) {
			Set<Entry<String, String>> s = this.map.entrySet();
			// Move next key and value of Map by iterator
			Iterator<Entry<String, String>> it = s.iterator();
			while (it.hasNext()) {
				Map.Entry<String, String> m = (Entry<String, String>) it
						.next();
				ret += "[" + m.getKey() + "=" + m.getValue() + "]";
			}
		}
		return ret + "}";
	}
}
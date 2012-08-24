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

public class CustomTags implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7226768576162771343L;
	private String type, root, id, title, lat, lon, alt, detailPage, url,
			image, xmlParser;

	public CustomTags(String type, String root, String id, String title,
			String lat, String lon, String alt, String detailPage, String url,
			String image, String xmlParser) {
		this.type = type;
		this.root = root;
		this.id = id;
		this.title = title;
		this.lat = lat;
		this.lon = lon;
		this.alt = alt;
		this.detailPage = detailPage;
		this.url = url;
		this.image = image;
		this.setXmlParser(xmlParser);
	}

	@Override
	public String toString() {
		String ret = "CustomTags[";

		ret += "type=" + type;
		ret += ", root=" + root;
		ret += ", id=" + id;
		ret += ", title=" + title;
		ret += ", lat=" + lat;
		ret += ", lon=" + lon;
		ret += ", alt=" + alt;
		ret += ", detailPage=" + detailPage;
		ret += ", url=" + url;
		ret += ", image=" + image;
		ret += ", xmlParser=" + xmlParser;
		
		return ret + "]";
	}
	
	/**
	 * @return the type
	 */
	public final String getType() {
		return type;
	}

	/**
	 * @return the root
	 */
	public final String getRoot() {
		return root;
	}

	/**
	 * @return the id
	 */
	public final String getId() {
		return id;
	}

	/**
	 * @return the title
	 */
	public final String getTitle() {
		return title;
	}

	/**
	 * @return the lat
	 */
	public final String getLat() {
		return lat;
	}

	/**
	 * @return the lon
	 */
	public final String getLon() {
		return lon;
	}

	/**
	 * @return the alt
	 */
	public final String getAlt() {
		return alt;
	}

	/**
	 * @return the detailPage
	 */
	public final String getDetailPage() {
		return detailPage;
	}

	/**
	 * @return the url
	 */
	public final String getUrl() {
		return url;
	}

	/**
	 * @return the image
	 */
	public final String getImage() {
		return image;
	}

	public String getXmlParser() {
		return xmlParser;
	}

	public void setXmlParser(String xmlParser) {
		this.xmlParser = xmlParser;
	}
}
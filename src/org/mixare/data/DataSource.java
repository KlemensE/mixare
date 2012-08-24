/*
 * Copyright (C) 2010- Peer internet solutions
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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.mixare.R;
import org.mixare.data.convert.PanoramioDataProcessor;
import org.mixare.lib.MixUtils;

import android.graphics.Color;

/**
 * The DataSource class is able to create the URL where the information about a
 * place can be found.
 * 
 * @author KlemensE
 */
public class DataSource {
	private static int DataSourceId = 0;

	private int id;
	private String name;
	private String url;
	private boolean enabled;
	private CreateParams params;
	private CustomTags customTags;
	private TYPE type;
	private DISPLAY display;
	private boolean editable;
	private BLUR blur;
	private DATA_PROCESSOR processor;

	/**
	 * Recreate's a previously existing DataSource
	 * 
	 * @param id
	 *            The id the DataSource previously had
	 * @param name
	 *            The name of the DataSource
	 * @param url
	 *            The URL of the DataSource
	 * @param typeString
	 *            The type of the DataSource using DataSource.TYPE, it has to be
	 *            a integer in a String
	 * @param display
	 *            The type of the DataSource using DataSource.DISPLAY, it has to
	 *            be a integer in a String
	 * @param enabled
	 *            Whether the DataSource is enabled or not
	 */
	public DataSource(int id, String name, String url, String typeString,
			String displayString, String enabledString, boolean editable,
			CreateParams params, CustomTags customTags) {
		DataSource.DataSourceId = id + 1;
		this.id = id;
		this.name = name;
		this.url = url;
		this.type = TYPE.values()[Integer.parseInt(typeString)];
		this.display = DISPLAY.values()[Integer.parseInt(displayString)];
		this.enabled = Boolean.parseBoolean(enabledString);
		this.editable = editable;
		this.blur = BLUR.NONE;
		this.params = params;
		this.customTags = customTags;
	}
	
	/**
	 * Create's a new DataSource
	 * 
	 * @param name
	 *            The name of the DataSource
	 * @param url
	 *            The URL of the DataSource
	 * @param type
	 *            The type of the DataSource using DataSource.TYPE
	 * @param display
	 *            The type of the DataSource using DataSource.DISPLAY
	 * @param enabled
	 *            Whether the DataSource is enabled or not
	 */
	public DataSource(String name, String url, TYPE type, DISPLAY display,
			boolean enabled, CreateParams params, CustomTags customTags) {
		this.id = DataSourceId;
		this.name = name;
		this.url = url;
		this.type = type;
		this.display = display;
		this.enabled = enabled;
		this.editable = true;
		this.blur = BLUR.NONE;
		this.params = params;
		this.customTags = customTags;
		increasId();
	}

	/* Methods */

	public String createRequestParams(double lat, double lon, double alt,
			float radius, String locale) {
		String ret = "";
		if (!ret.startsWith("file://")) {
			switch (this.type) {

			case WIKIPEDIA:
				// Free service limited to 20km
				this.params = new CreateParams("lat", "lng", null, "radius",
						20, "lang", null);
				ret = this.params.createRequest(lat, lon, alt, radius, locale);

				break;

			case TWITTER:
				ret += "?geocode=" + lat + "%2C" + lon + "%2C"
						+ Math.max(radius, 1.0) + "km";
				break;

			case MIXARE:
				this.params = new CreateParams("latitude", "longitude",
						"altitude", "radius", null, null, null);
				ret = this.params.createRequest(lat, lon, alt, radius, locale);
				break;

			case ARENA:
				this.params = new CreateParams("lat", "lng", null, null, null,
						null, null);
				ret = this.params.createRequest(lat, lon, alt, radius, locale);
				break;

			case OSM:
				this.params = new CreateParams(0, null, null);
				ret = this.params.createRequest(lat, lon, alt, radius, locale);
				break;
			case PANORAMIO:
				Map<String, String> addidtionalParams = new HashMap<String, String>();
				addidtionalParams.put("set", "public");
				addidtionalParams.put("from", "0");
				addidtionalParams.put("to",
						PanoramioDataProcessor.MAX_JSON_OBJECTS + "");
				addidtionalParams.put("mapfilter", "true");
				addidtionalParams.put("size", "thumbnail");
				this.params = new CreateParams(1, null, addidtionalParams);
				ret = this.params.createRequest(lat, lon, alt, radius, locale);
			case CUSTOM:

				break;
			default:
				break;
			}

		}

		return ret;
	}

	private void increasId() {
		DataSourceId++;
	}

	/**
	 * Check the minimum required data
	 * 
	 * @return true if URL and Name are correct
	 */
	public boolean isWellFormed() {
		boolean out = false;
		try {
			// TODO better URL check
			new URL(getUrl());
		} catch (MalformedURLException e) {
			return false;
		}
		if (!MixUtils.isNullOrEmpty(getName())) {
			out = true;
		}
		return out;
	}

	@Override
	public String toString() {
		String paramsString = params == null ? "" : ", params=" + params.toString();
		String tagsString = customTags == null ? "" : ", tags=" + customTags.toString();
		return "DataSource [name=" + name + ", url=" + url + ", enabled="
				+ enabled + ", type=" + type + ", display=" + display
				+ ", blur=" + blur + ", processor=" + processor + paramsString + tagsString + "]";
	}

	/* Getter and Setter */

	public BLUR getBlur() {
		return this.blur;
	}

	public int getBlurId() {
		return this.blur.ordinal();
	}

	public void setBlur(BLUR blur) {
		this.blur = blur;
	}

	public void setBlur(int id) {
		this.blur = BLUR.values()[id];
	}

	public int getColor() {
		int ret;
		switch (this.type) {
		case TWITTER:
			ret = Color.rgb(50, 204, 255);
			break;
		case WIKIPEDIA:
			ret = Color.RED;
			break;
		case ARENA:
			ret = Color.RED;
			break;
		case PANORAMIO:
			ret = Color.GREEN;
			break;
		default:
			ret = Color.WHITE;
			break;
		}
		return ret;
	}

	public int getDataSourceIcon() {
		int ret;
		switch (this.type) {
		case TWITTER:
			ret = R.drawable.twitter;
			break;
		case OSM:
			ret = R.drawable.osm;
			break;
		case WIKIPEDIA:
			ret = R.drawable.wikipedia;
			break;
		case ARENA:
			ret = R.drawable.arena;
			break;
		case PANORAMIO:
			// ret = R.drawable.ic_launcher;
			/*
			 * Logo from http://www.tilo-hensel.de/free-glossy-community-icons
			 * Created by Tilo Hensel licensed under
			 * http://creativecommons.org/licenses/by-nc-sa/3.0/
			 */
			ret = R.drawable.panoramio;
			break;
		default:
			ret = R.drawable.ic_launcher;
			break;
		}
		return ret;
	}

	public int getDataSourceId() {
		return this.id;
	}

	public int getDisplayId() {
		return this.display.ordinal();
	}

	public int getTypeId() {
		return this.type.ordinal();
	}

	public DISPLAY getDisplay() {
		return this.display;
	}

	public void setDisplay(int id) {
		this.display = DISPLAY.values()[id];
	}

	public void setDisplay(DISPLAY display) {
		this.display = display;
	}

	public TYPE getType() {
		return this.type;
	}

	public void setType(int id) {
		this.type = TYPE.values()[id];
	}

	public void setType(TYPE type) {
		this.type = type;
	}

	public boolean getEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean isChecked) {
		this.enabled = isChecked;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isEditable() {
		return editable;
	}

	public CreateParams getParamCreater() {
		return this.params;
	}

	public void setParamCreater(CreateParams params) {
		this.params = params;
	}

	public CustomTags getCustomTags() {
		return customTags;
	}

	public void setCustomTags(CustomTags customTags) {
		this.customTags = customTags;
	}

	public DATA_PROCESSOR getProcessor() {
		return processor;
	}

	public int getProcessorId() {
		if (processor == null) {
			return -1;
		}
		return processor.ordinal();
	}
	
	public void setProcessor(DATA_PROCESSOR processor) {
		this.processor = processor;
	}
	
	public void setProcessor(int processorId) {
		this.processor = DATA_PROCESSOR.values()[processorId];
	}
	
	/* ENUM */

	public enum TYPE {
		WIKIPEDIA, TWITTER, OSM, MIXARE, ARENA, PANORAMIO, CUSTOM
	};

	public enum DISPLAY {
		CIRCLE_MARKER, NAVIGATION_MARKER, IMAGE_MARKER
	};

	public enum BLUR {
		NONE, ADD_RANDOM, TRUNCATE
	};
	
	public enum DATA_PROCESSOR {
		WIKIPEDIA, TWITTER, OSM, MIXARE, PANORAMIO, CUSTOM
	};
}

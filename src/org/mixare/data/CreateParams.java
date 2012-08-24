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
import java.security.InvalidParameterException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.mixare.data.convert.DataConvertor;
import org.mixare.lib.MixUtils;

import android.util.Log;

/**
 * This class can create the Request Parameters 
 * 
 * @author KlemensE
 *
 */
public class CreateParams implements Serializable {
	private static final long serialVersionUID = 4411541762036501193L;
	private static final int PARAM_BBOX = 0;
	private static final int PARAM_LOCATION = 1;
	private static final int BBOX_OSM = 0;
	private static final int BBOX_PANORAMIO = 1;
	private int type = -1;
	private int bbox_type = -1;
	private String lat = null;
	private String lng = null;
	private String altitude = null;
	private String local = null;
	private String radius = null;
	private int maxRadius = 80;
	private Map<String, String> params = null;

	/**
	 * Constructor for a BoundinBox request.
	 * 
	 * @param bbox_type
	 *            The BoundingBox to use 0 for OSM Bounding Box 1 for Panoramino
	 *            Bounding Box
	 * @param locale
	 *            the paramname to use for language or null || empty not to send
	 *            it
	 * @param params
	 *            Holds additional params, can be null
	 * @throws InvalidParameterException
	 *             when bbox_type is neither 0 or 1
	 */
	public CreateParams(int bbox_type, String locale, Map<String, String> params)
			throws InvalidParameterException {
		if (bbox_type == 0 || bbox_type == 1) {
			this.type = PARAM_BBOX;
			this.bbox_type = bbox_type;
			if (params != null) {
				this.params = params;
			}
		} else {
			throw new InvalidParameterException("bbox_type must be 0 or 1");
		}
	}

	/**
	 * 
	 * @param lat
	 * @param lng
	 * @param altitude
	 * @param maxRadius
	 * @param params
	 * @param radius
	 */
	public CreateParams(String lat, String lng, String altitude, String radius,
			Integer maxRadius, String local, Map<String, String> params) {
		if (MixUtils.isNullOrEmpty(lat)) {
			throw new InvalidParameterException("lat is null or empty");
		}
		if (MixUtils.isNullOrEmpty(lng)) {
			throw new InvalidParameterException("lng is null or empty");
		}
		this.type = PARAM_LOCATION;
		this.lat = lat;
		this.lng = lng;
		this.altitude = altitude;
		this.radius = radius;
		if (maxRadius != null) {
			if (0 < maxRadius && maxRadius < 80) {
				this.maxRadius = maxRadius;
			}
		}
		this.local = local;
		this.params = params;
	}

	/**
	 * Creates the request.
	 * 
	 * @param lat
	 *            The latitude to use in the request
	 * @param lon
	 *            The longitude to use in the request
	 * @param alt
	 *            The altitude to use in the request
	 * @param radius
	 *            The radius to use in the request
	 * @param local
	 *            The local to use in the request
	 * @return The params to use in the request
	 */
	public String createRequest(double lat, double lon, double alt,
			float radius, String locale) {
		String ret = "";
		if (this.type == PARAM_BBOX) {
			if (this.bbox_type == BBOX_OSM) {
				ret += DataConvertor.getOSMBoundingBox(lat, lon, radius);
			} else if (this.bbox_type == BBOX_PANORAMIO) {
				final float minLong = (float) (lon - radius / 100.0);
				final float minLat = (float) (lat - radius / 100.0);
				final float maxLong = (float) (lon + radius / 100.0);
				final float maxLat = (float) (lat + radius / 100.0);
				ret += "?&minx=" + minLong + "&miny=" + minLat + "&maxx="
						+ maxLong + "&maxy=" + maxLat;
			} else {
				// Should never reach this
				Log.d("CustomParams", "wrong BBox type");
			}

			if (!MixUtils.isNullOrEmpty(this.local)) {
				ret += "&" + this.local + "=" + locale;
			}
			if (this.params != null) {
				// Get Map in Set interface to get key and value
				Set<Entry<String, String>> s = params.entrySet();
				// Move next key and value of Map by iterator
				Iterator<Entry<String, String>> it = s.iterator();
				while (it.hasNext()) {
					Map.Entry<String, String> m = (Entry<String, String>) it
							.next();
					ret += "&" + m.getKey() + "=" + m.getValue();
				}
			}
		} else if (this.type == PARAM_LOCATION) {
			ret += "?" + this.lat + "=" + lat + "&" + this.lng + "=" + lon;
			if (!MixUtils.isNullOrEmpty(this.altitude)) {
				ret += "&" + this.altitude + "=" + alt;
			}
			if (!MixUtils.isNullOrEmpty(this.radius)) {
				radius = (radius < this.maxRadius) ? radius : this.maxRadius;
				ret += "&" + this.radius + "=" + radius;
			}
			if (!MixUtils.isNullOrEmpty(this.local)
					&& !MixUtils.isNullOrEmpty(locale)) {
				ret += "&" + this.local + "=" + locale;
			}
			if (this.params != null) {
				// Get Map in Set interface to get key and value
				Set<Entry<String, String>> s = params.entrySet();
				// Move next key and value of Map by iterator
				Iterator<Entry<String, String>> it = s.iterator();
				while (it.hasNext()) {
					Map.Entry<String, String> m = (Entry<String, String>) it
							.next();
					ret += "&" + m.getKey() + "=" + m.getValue();
				}
			}
		}
		return ret;
	}

	@Override
	public String toString() {
		return createRequest(0, 0, 0, 0, "");
	}
	
	/* Getter */

	/**
	 * <pre>
	 * -1 : Type not set (Should never be returned) 
	 * 0 : Type is set to Bounding Box 
	 * 1 : Type is set to Location
	 * </pre>
	 * 
	 * @return The type of request to create
	 */
	public int getType() {
		return this.type;
	}

	/**
	 * <pre>
	 * -1 : bbox isn't used 
	 * 0 : osm bbox 
	 * 1 : panoramino bbox
	 * </pre>
	 * 
	 * @return The type of bbox to use
	 */
	public int getBboxType() {
		return this.bbox_type;
	}

	/**
	 * Returns the selected paramName for the latitude returns null if type is 0
	 * (bounding box)
	 * 
	 * @return The name of the param
	 */
	public String getLatitudeParamName() {
		return this.lat;
	}

	/**
	 * Returns the selected paramName for the longitude returns null if type is
	 * 0 (bounding box)
	 * 
	 * @return The name of the param
	 */
	public String getLongitudeParamName() {
		return this.lng;
	}

	/**
	 * @return the name of the param or null if it should not be used
	 */
	public String getAltitudeParamName() {
		return this.altitude;
	}

	/**
	 * @return the name of the param or null if it should not be used
	 */
	public String getLocalParamName() {
		return this.local;
	}

	/**
	 * @return the name of the param or null if it should not be used
	 */
	public String getRadiusParamName() {
		return this.radius;
	}
	
	/**
	 * @return The selected maximum for the Radius Parameter
	 */
	public int getMaxRadius() {
		return this.maxRadius;
	}
	
	/**
	 * Holds additional parameters. The key holds the name of the parameter, the value holds the value
	 * @return
	 */
	public Map<String, String> getAdditionalParams() {
		return this.params;
	}
}
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

/**
 * This class holds the constants for saving and retrieving the Values for
 * CustomParams and CustomTags
 * 
 * @author KlemensE
 * 
 */
public class Constants {

	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "longitude";
	public static final String ALTITUDE = "altitude";

	public static final String EDITTEXT_LATITUDE = "edittext_latitude";
	public static final String EDITTEXT_LONGITUDE = "edittext_longitude";
	public static final String EDITTEXT_ALTITUDE = "edittext_altitude";

	public class CustomParams {
		public static final String LOCATION_TYPE = "location_type";

		public static final String BOUNDING_BOX = "bounding_box";
		public static final String BBOX_TYPE = "bbox_type";

		public static final String LOCATION = "location";
		public static final String RADIUS = "radius";
		public static final String MAXRADIUS = "maxradius";
		public static final String ADDITIONAL = "additional";
		public static final String LOCALE = "locale";

		public class Preferences {
			public static final String BOUNDING_BOX = "bounding_box";
			public static final String BBOX_LIST = "bounding_box_list";

			public static final String LOCATION = "location";
			public static final String RADIUS = "edittext_radius";
			public static final String MAXRADIUS = "seekbar_maxradius";
			public static final String ADDITIONAL = "additional";
			public static final String LOCALE = "edittext_locale";
		}
	}

	public class DataProcessor {
		public static final String RESULT_TYPE = "result_type";
		public static final String XML_PARSER = "xml_parser";
		public static final String ROOT = "root";
		public static final String ID = "id";
		public static final String TITLE = "title";
		public static final String DETAIL_PAGE = "detail_page";
		public static final String URL = "url";
		public static final String IMAGE = "image";

		public class Preferences {
			public static final String TYPE_CATEGORY = "dataprocessor_category_type";
			public static final String XML_PARSER = "edittext_xml_parser";
			public static final String RESULT_TYPE = "dataprocessor_type_list";
			public static final String ROOT = "edittext_root";
			public static final String ID = "edittext_id";
			public static final String TITLE = "edittext_title";
			public static final String DETAIL_PAGE = "edittext_detail_page";
			public static final String URL = "edittext_url";
			public static final String IMAGE = "edittext_image";
		}
	}

	public class AdditionalParams {
		public static final String POSITION = "position";
		public static final String KEY = "key";
		public static final String VALUE = "value";
	}
}

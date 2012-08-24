/*
 * Copyright (C) 2012- Peer internet solutions & Finalist IT Group
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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.mixare.R;
import org.mixare.lib.MixUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Singleton class that manages the storage of DataSources. You can add, edit or
 * delete a DataSource through this class.
 */
public class DataSourceStorage {
	private SharedPreferences settings;
	private static Context ctx;
	public static DataSourceStorage instance;
	private static String xmlPreferencesKey = "xmlDataSources";
	private static List<DataSource> dataSourceList = new ArrayList<DataSource>();

	/**
	 * Private constructor to ensure that only one instance can be created
	 * 
	 * @param ctx
	 *            The context to be able to access the SharedPreferences and the
	 *            Resources
	 */
	private DataSourceStorage(Context ctx) {
		DataSourceStorage.ctx = ctx;
		settings = ctx.getSharedPreferences(DataSourceList.SHARED_PREFS, 0);
		fillListFromXml();
	}

	/**
	 * Creates a new instance of a DataSourceSotrage
	 * 
	 * @param ctx
	 *            The context to create the DataSourceSotrage
	 */
	public static void init(Context ctx) {
		instance = new DataSourceStorage(ctx);
	}

	/**
	 * @return Returns the instance of a DataSourceStorage or null if no
	 *         instance was created yet
	 */
	public static DataSourceStorage getInstance() {
		if (instance == null) {
			if (ctx != null) {
				init(ctx);
			} else {
				Log.d("DataSourceStorage", "instance and ctx are null");
			}
		}
		return instance;
	}

	/**
	 * Returns the instance of a DataSourceStorage and creates an instance if no
	 * instance was created yet.
	 * 
	 * @param ctx
	 *            The context to create a new instance if no instance was
	 *            created yet
	 * @return The instance of a DataSourceStorage
	 */
	public static DataSourceStorage getInstance(Context ctx) {
		if (instance == null) {
			instance = new DataSourceStorage(ctx);
		}
		return instance;
	}

	/**
	 * Creates a XML Element in this form:
	 * 
	 * <pre>
	 * {@code
	 * <datasource id="0">
	 * 	<name></name>
	 * 	<url></url>
	 * 	<type></type>
	 * 	<display></display>
	 * 	<visible></visible>
	 * 	<blur></blur>
	 * </datasource>
	 * }
	 * </pre>
	 * 
	 * @param doc
	 *            The XML Document to create the Element
	 * @param id
	 *            The id of the DataSource
	 * @param name
	 *            The name of the DataSource
	 * @param url
	 *            The Url of the DataSource
	 * @param type
	 *            The Type of the DataSource
	 * @param display
	 *            The Displaytype of the DataSource
	 * @param visible
	 *            The Visibility of the DataSource
	 * @param blur
	 *            How the GPS location should be blurred
	 * @return The XML Element of the DataSource
	 */
	private Element createDataSourceElement(Document doc, String id,
			String name, String url, String type, String display,
			boolean visible, boolean editable, String blur,
			CreateParams params, CustomTags tags, String dataProcessorId) {
		// Set rootElement to "DataSource"
		Element rootElement = doc.createElement("datasource");

		// add attribute id to rootElement
		rootElement.setAttribute("id", id);

		// create "Name" Element and add it to rootElement
		Element nameElement = doc.createElement("name");
		nameElement.appendChild(doc.createTextNode(name));
		rootElement.appendChild(nameElement);

		// create "Url" Element and add it to rootElement
		Element urlElement = doc.createElement("url");
		urlElement.appendChild(doc.createTextNode(url));
		rootElement.appendChild(urlElement);

		// create "Type" Element and add it to rootElement
		Element typeElement = doc.createElement("type");
		typeElement.appendChild(doc.createTextNode(type));
		rootElement.appendChild(typeElement);

		// create "Display" Element and add it to rootElement
		Element displayElement = doc.createElement("display");
		displayElement.appendChild(doc.createTextNode(display));
		rootElement.appendChild(displayElement);

		// create "enabled" Element and add it to rootElement
		Element enabled = doc.createElement("visible");
		enabled.appendChild(doc.createTextNode(String.valueOf(visible)));
		rootElement.appendChild(enabled);

		// create "editable" Element and add it to rootElement
		Element editableElement = doc.createElement("editable");
		editableElement
				.appendChild(doc.createTextNode(String.valueOf(editable)));
		rootElement.appendChild(editableElement);

		// create "blur" Element and add it to rootElement
		Element blurElement = doc.createElement("blur");
		blurElement.appendChild(doc.createTextNode(blur));
		rootElement.appendChild(blurElement);

		// create "blur" Element and add it to rootElement
		Element processorElement = doc.createElement("processor");
		processorElement.appendChild(doc.createTextNode(dataProcessorId));
		rootElement.appendChild(processorElement);

		int ordinal = DataSource.TYPE.CUSTOM.ordinal();
		int typeInt = Integer.valueOf(type);
		if (ordinal == typeInt) {
			// create "request" Element
			Element requestElement = doc.createElement("request");
			requestElement.setAttribute("type", params.getType() + "");
			if (params.getType() == 0) {
				// bbox
				Element bboxElement = doc.createElement("bbox");
				bboxElement.appendChild(doc.createTextNode(params.getBboxType()
						+ ""));
				requestElement.appendChild(bboxElement);
			} else if (params.getType() == 1) {
				// latitude
				Element latElement = doc.createElement("latitude");
				latElement.appendChild(doc.createTextNode(params
						.getLatitudeParamName()));
				requestElement.appendChild(latElement);
				// longitude
				Element lngElement = doc.createElement("longitude");
				lngElement.appendChild(doc.createTextNode(params
						.getLongitudeParamName()));
				requestElement.appendChild(lngElement);
				// altitude
				if (!MixUtils.isNullOrEmpty(params.getAltitudeParamName())) {
					Element altitudeElement = doc.createElement("altitude");
					altitudeElement.appendChild(doc.createTextNode(params
							.getAltitudeParamName()));
					requestElement.appendChild(altitudeElement);
				}
				// radius
				if (!MixUtils.isNullOrEmpty(params.getRadiusParamName())) {
					Element radiusElement = doc.createElement("radius");
					radiusElement.appendChild(doc.createTextNode(params
							.getRadiusParamName()));
					requestElement.appendChild(radiusElement);
				}
				// maxradius
				if (params.getMaxRadius() != 0) {
					Element maxradiusElement = doc.createElement("maxradius");
					maxradiusElement.appendChild(doc.createTextNode(params
							.getMaxRadius() + ""));
					requestElement.appendChild(maxradiusElement);
				}
			}
			// locale
			if (!MixUtils.isNullOrEmpty(params.getLocalParamName())) {
				Element localeElement = doc.createElement("locale");
				localeElement.appendChild(doc.createTextNode(params
						.getLocalParamName()));
				requestElement.appendChild(localeElement);
			}

			// params
			if (params.getAdditionalParams() != null) {
				Element paramsElement = doc.createElement("params");
				Set<Entry<String, String>> s = params.getAdditionalParams()
						.entrySet();
				Iterator<Entry<String, String>> it = s.iterator();
				while (it.hasNext()) {
					Map.Entry<String, String> m = (Entry<String, String>) it
							.next();
					Element paramElement = doc.createElement("param");

					Element paramNameElement = doc.createElement("name");
					paramNameElement
							.appendChild(doc.createTextNode(m.getKey()));
					paramElement.appendChild(paramNameElement);

					Element paramValueElement = doc.createElement("value");
					paramValueElement.appendChild(doc.createTextNode(m
							.getValue()));
					paramElement.appendChild(paramValueElement);

					paramsElement.appendChild(paramElement);
				}
				requestElement.appendChild(paramsElement);
			}

			rootElement.appendChild(requestElement);

			// custom tags
			if (tags != null) {
				Element tagElement = doc.createElement("tags");

				Element processorTypeElement = doc.createElement("type");
				processorTypeElement.appendChild(doc.createTextNode(tags
						.getType()));
				tagElement.appendChild(processorTypeElement);

				Element xmlParserElement = doc.createElement("xml_parser");
				xmlParserElement
						.appendChild(doc.createTextNode(tags.getXmlParser()));
				tagElement.appendChild(xmlParserElement);

				Element processorRootElement = doc.createElement("root");
				processorRootElement.appendChild(doc.createTextNode(tags
						.getRoot()));
				tagElement.appendChild(processorRootElement);

				Element titleElement = doc.createElement("title");
				titleElement.appendChild(doc.createTextNode(tags.getTitle()));
				tagElement.appendChild(titleElement);

				Element latElement = doc.createElement("lat");
				latElement.appendChild(doc.createTextNode(tags.getLat()));
				tagElement.appendChild(latElement);

				Element lonElement = doc.createElement("lon");
				lonElement.appendChild(doc.createTextNode(tags.getLon()));
				tagElement.appendChild(lonElement);

				if (tags.getId() != null) {
					Element idElement = doc.createElement("id");
					idElement.appendChild(doc.createTextNode(tags.getId()));
					tagElement.appendChild(idElement);
				}
				if (tags.getAlt() != null) {
					Element altElement = doc.createElement("alt");
					altElement.appendChild(doc.createTextNode(tags.getAlt()));
					tagElement.appendChild(altElement);
				}
				if (tags.getDetailPage() != null) {
					Element detailPageElement = doc.createElement("detailPage");
					detailPageElement.appendChild(doc.createTextNode(tags
							.getDetailPage()));
					tagElement.appendChild(detailPageElement);
				}
				if (tags.getUrl() != null) {
					Element processorUrlElement = doc.createElement("url");
					processorUrlElement.appendChild(doc.createTextNode(tags
							.getUrl()));
					tagElement.appendChild(processorUrlElement);
				}
				if (tags.getImage() != null) {
					Element imageElement = doc.createElement("image");
					imageElement
							.appendChild(doc.createTextNode(tags.getImage()));
					tagElement.appendChild(imageElement);
				}
				rootElement.appendChild(tagElement);
			}
		}

		return rootElement;
	}

	/**
	 * Adds a DataSource to the list
	 * 
	 * @param dataSource
	 *            The DataSource to add
	 */
	public void add(DataSource dataSource) {
		dataSourceList.add(dataSource);
		save();
	}

	/**
	 * Removes the saved DataSources from the SharedPreferences and the internal
	 * List
	 */
	public void clear() {
		SharedPreferences.Editor dataSourceEditor = settings.edit();
		dataSourceEditor.clear();
		dataSourceEditor.commit();

		dataSourceList.clear();
	}

	/**
	 * Saves the default DataSources from the Resources to the SharedPreferences
	 * and adds them to the internal list
	 */
	public void fillDefaultDataSources() {
		String defaultXml = inputStreamToString(ctx.getResources()
				.openRawResource(R.raw.defaultdatasources));

		SharedPreferences.Editor editor = settings.edit();
		editor.putString(DataSourceStorage.xmlPreferencesKey, defaultXml);
		editor.commit();

		fillListFromXml();
	}

	/**
	 * Recreate's the dataSourceList out of the XML
	 */
	private void fillListFromXml() {
		int xmlLength = getDataSourceLengthFromXml();
		dataSourceList.clear();
		for (int i = 0; i < xmlLength; i++) {
			dataSourceList.add(getDataSourceFromXml(i));
		}
	}

	/**
	 * @return The XML out of the SharedPreferences or if the SharedPreferences
	 *         do not contain the xml, return XML out of the Resources
	 */
	private String getXml() {
		String defaultXml = inputStreamToString(ctx.getResources()
				.openRawResource(R.raw.defaultdatasources));

		return settings.getString(xmlPreferencesKey, defaultXml);
	}

	/**
	 * Calculates the length of available DataSources in the XML
	 * 
	 * @return How many DataSources exist
	 */
	private int getDataSourceLengthFromXml() {
		try {
			Document doc = convertToXmlDocument(getXml());
			return doc.getElementsByTagName("datasource").getLength();
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * Create's a DataSource out of the XML
	 * 
	 * @param id
	 *            The id of the DataSource to recreate
	 * @return The recreated DataSource
	 */
	private DataSource getDataSourceFromXml(int id) {
		try {
			Document doc = convertToXmlDocument(getXml());
			NodeList nList = doc.getElementsByTagName("datasource");

			// Loop over all datasource elements
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					if (Integer.valueOf(eElement.getAttribute("id")) == id) {
						CreateParams params = getCustomParamFromXml(eElement);
						CustomTags dataProcessor = getCustomTagsFromXml(eElement);
						DataSource ds = new DataSource(Integer.valueOf(eElement
								.getAttribute("id")), getTagValue("name",
								eElement), getTagValue("url", eElement),
								getTagValue("type", eElement), getTagValue(
										"display", eElement), getTagValue(
										"visible", eElement),
								Boolean.parseBoolean(getTagValue("editable",
										eElement)), params, dataProcessor);
						String dataProcessorId = getTagValue("processor",
								eElement);
						if (dataProcessorId != null
								&& !dataProcessorId.equals("-1")) {
							ds.setProcessor(DataSource.DATA_PROCESSOR.values()[Integer
									.parseInt(dataProcessorId)]);
						}
						ds.setBlur(DataSource.BLUR.values()[Integer
								.parseInt(getTagValue("blur", eElement))]);
						return ds;
					}
				}
			}
		} catch (Exception e) {
			Log.d("DataSourceStorage", "getDataSource: " + id + " Failed");
		}
		return null;
	}

	/**
	 * Recreate's the ParamCreater from the XML element
	 * 
	 * @param eElement
	 *            The element from which to recover the ParamCreater
	 * @return The ParamCreater or null if no ParamCreater was found
	 */
	private CreateParams getCustomParamFromXml(Element eElement) {
		NodeList request = eElement.getElementsByTagName("request");
		if (request.getLength() <= 0) {
			return null;
		}
		NodeList requestNodes = request.item(0).getChildNodes();
		int requestType = Integer.valueOf(request.item(0).getAttributes()
				.getNamedItem("type").getNodeValue());
		int bboxType = -1, maxRadius = 0;
		String locale = null, lat = null, lng = null, alt = null, radius = null;
		Map<String, String> params = null;
		for (int i = 0; i < requestNodes.getLength(); i++) {
			Node child = requestNodes.item(i);
			if (requestType == 0) {
				if (child.getNodeName().equals("bbox")) {
					bboxType = Integer.valueOf(getNodeValue(child));
				}
			} else if (requestType == 1) {
				if (child.getNodeName().equals("latitude")) {
					lat = getNodeValue(child);
				} else if (child.getNodeName().equals("longitude")) {
					lng = getNodeValue(child);
				} else if (child.getNodeName().equals("altitude")) {
					alt = getNodeValue(child);
				} else if (child.getNodeName().equals("radius")) {
					radius = getNodeValue(child);
				} else if (child.getNodeName().equals("maxradius")) {
					maxRadius = Integer.valueOf(getNodeValue(child));
				}
			}

			if (child.getNodeName().equals("locale")) {
				locale = getNodeValue(child);
			} else if (child.getNodeName().equals("params")) {
				params = new HashMap<String, String>();
				NodeList paramList = child.getChildNodes();
				for (int j = 0; j < paramList.getLength(); j++) {
					NodeList paramNode = paramList.item(j).getChildNodes();
					String key = null, value = null;
					for (int k = 0; k < paramNode.getLength(); k++) {
						if (paramNode.item(k).getNodeName().equals("name")) {
							key = getNodeValue(paramNode.item(k));
						} else if (paramNode.item(k).getNodeName()
								.equals("value")) {
							value = getNodeValue(paramNode.item(k));
						}
					}
					if (!MixUtils.isNullOrEmpty(key)
							&& !MixUtils.isNullOrEmpty(value)) {
						params.put(key, value);
					}
				}
			}
		}

		if (requestType == 0) {
			return new CreateParams(bboxType, locale, params);
		} else if (requestType == 1) {
			return new CreateParams(lat, lng, alt, radius, maxRadius, locale,
					params);
		}

		return null;
	}

	/**
	 * Recreate's the CustomTags from the XML element
	 * 
	 * @param eElement
	 *            The element from which to recover the CustomTags
	 * @return The CustomTags or null if no CustomTag was found
	 */
	private CustomTags getCustomTagsFromXml(Element eElement) {
		NodeList tags = eElement.getElementsByTagName("tags");
		if (tags.getLength() <= 0) {
			return null;
		}
		NodeList tagNodes = tags.item(0).getChildNodes();
		String type = null, root = null, id = null, title = null, lat = null, lon = null, alt = null, detailPage = null, url = null, image = null, xmlParser = null;
		for (int i = 0; i < tagNodes.getLength(); i++) {
			Node child = tagNodes.item(i);
			if (child.getNodeName().equals("type")) {
				type = getNodeValue(child);
			} else if (child.getNodeName().equals("root")) {
				root = getNodeValue(child);
			} else if (child.getNodeName().equals("title")) {
				title = getNodeValue(child);
			} else if (child.getNodeName().equals("lat")) {
				lat = getNodeValue(child);
			} else if (child.getNodeName().equals("lon")) {
				lon = getNodeValue(child);
			} else if (child.getNodeName().equals("alt")) {
				alt = getNodeValue(child);
			} else if (child.getNodeName().equals("detailPage")) {
				detailPage = getNodeValue(child);
			} else if (child.getNodeName().equals("url")) {
				url = getNodeValue(child);
			} else if (child.getNodeName().equals("image")) {
				image = getNodeValue(child);
			} else if (child.getNodeName().equals("xml_parser")) {
				xmlParser = getNodeValue(child);
			} else if (child.getNodeName().equals("id")) {
				id = getNodeValue(child);
			}
		}
		CustomTags customTags = new CustomTags(type, root, id, title, lat, lon,
				alt, detailPage, url, image, xmlParser);

		return customTags;
	}

	/**
	 * Retrieves a value of a Node
	 * 
	 * @param node
	 *            The Node which is holding the value
	 * @return The value of the Node
	 */
	public String getNodeValue(Node node) {
		if (node.hasChildNodes()) {
			NodeList child = node.getChildNodes();
			return child.item(0) != null ? child.item(0).getNodeValue() : null;
		}
		return null;
	}

	/**
	 * Converts a InputStream to a String
	 * 
	 * @param iS
	 *            The InputStream to convert
	 * @return The String that the InputStream was containing
	 */
	public String inputStreamToString(InputStream iS) {
		try {
			// create a buffer that has the same size as the InputStream
			byte[] buffer = new byte[iS.available()];
			// read the text file as a stream, into the buffer
			iS.read(buffer);
			// create a output stream to write the buffer into
			ByteArrayOutputStream oS = new ByteArrayOutputStream();
			// write this buffer to the output stream
			oS.write(buffer);
			// Close the Input and Output streams
			oS.close();
			iS.close();

			// return the output stream as a String
			return oS.toString();
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Retrieves a DataSource out of the saved Xml
	 * 
	 * @param id
	 *            The id of the DataSource to return
	 * @return The DataSource at the specified index
	 */
	public DataSource getDataSource(int id) {
		if (dataSourceList.get(id) == null) {
			fillListFromXml();

			if (dataSourceList.get(id) == null) {
				fillDefaultDataSources();
			}
		}

		return dataSourceList.get(id);
	}

	/**
	 * Retrieves a Value of a Tag out of an Xml Element
	 * 
	 * @param sTag
	 *            The Tag to look for
	 * @param element
	 *            The Element in which the Tag should be looked for
	 * @return The value of the Tag
	 */
	private static String getTagValue(String sTag, Element element) {
		try {
			NodeList nlList = element.getElementsByTagName(sTag).item(0)
					.getChildNodes();
			Node nValue = (Node) nlList.item(0);
			return nValue.getNodeValue();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Converts a String to a Xml Document
	 * 
	 * @param rawData
	 *            The String to convert
	 * @return The Xml Document or null if an error occurred
	 */
	public Document convertToXmlDocument(String rawData) {
		Document doc = null;
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			// Document doc = builder.parse(is);d
			doc = builder.parse(new InputSource(new StringReader(rawData)));
			builder = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return doc;
	}

	/**
	 * @return How many DataSources are added
	 */
	public int getSize() {
		if (dataSourceList == null || dataSourceList.size() == 0) {
			try {
				fillListFromXml();
			} catch (Exception e) {
				fillDefaultDataSources();
			}
		}
		return dataSourceList.size();
	}

	public static Document createNewXmlDocument() {
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory
					.newDocumentBuilder();
			return documentBuilder.newDocument();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Create's a XML String and saves it to the SharedPreferences
	 */
	public void save() {
		int length = dataSourceList.size();
		try {
			Document doc = createNewXmlDocument();
			doc.appendChild(doc.createElement("datasources"));
			Element documentRoot = doc.getDocumentElement();

			for (int i = 0; i < length; i++) {
				// Create the XML Element for the new DataSource
				Element dataSourceElement = createDataSourceElement(doc, String
						.valueOf(dataSourceList.get(i).getDataSourceId()),
						dataSourceList.get(i).getName(), dataSourceList.get(i)
								.getUrl(), String.valueOf(dataSourceList.get(i)
								.getTypeId()), String.valueOf(dataSourceList
								.get(i).getDisplayId()), dataSourceList.get(i)
								.getEnabled(), dataSourceList.get(i)
								.isEditable(), String.valueOf(dataSourceList
								.get(i).getBlurId()), dataSourceList.get(i)
								.getParamCreater(), dataSourceList.get(i)
								.getCustomTags(), String.valueOf(dataSourceList
								.get(i).getProcessorId()));

				documentRoot.appendChild(dataSourceElement);
			}

			// Convert Document to String
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
					"yes");
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(doc), new StreamResult(writer));
			// String xml = writer.getBuffer().toString().replaceAll("\n|\r",
			// "");
			String xml = writer.getBuffer().toString();

			tf = null;
			transformer = null;

			// Save it to the SharedPreferences
			SharedPreferences.Editor editor = settings.edit();
			editor.putString(xmlPreferencesKey, xml);
			editor.commit();
		} catch (Exception e) {

		}
	}
}
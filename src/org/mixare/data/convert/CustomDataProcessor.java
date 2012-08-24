package org.mixare.data.convert;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mixare.MixView;
import org.mixare.data.CustomTags;
import org.mixare.data.DataHandler;
import org.mixare.data.DataSource;
import org.mixare.lib.HtmlUnescape;
import org.mixare.lib.MixUtils;
import org.mixare.lib.marker.Marker;
import org.mixare.marker.ImageMarker;
import org.mixare.marker.NavigationMarker;
import org.mixare.marker.POIMarker;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.util.Log;

/**
 * This class handles the conversion from RawData to marker.
 * 
 * @author KlemensE
 * 
 */
public class CustomDataProcessor extends DataHandler implements DataProcessor {

	private int MAX_JSON_OBJECTS = 100;

	/**
	 * Custom URL match {@inheritDoc}
	 */
	@Override
	public String[] getUrlMatch() {
		String[] str = new String[0];
		return str;
	}

	/**
	 * Custom data match {@inheritDoc}
	 */
	@Override
	public String[] getDataMatch() {
		String[] str = {};
		return str;
	}

	/**
	 * Custom {@inheritDoc}
	 */
	@Override
	public boolean matchesRequiredType(String type) {
		if (type.equals(DataSource.TYPE.CUSTOM.name())) {
			return true;
		}
		return false;
	}

	@Override
	public List<Marker> load(String rawData, int taskId, int colour,
			DataSource ds) throws JSONException {
		List<Marker> marker = new ArrayList<Marker>();
		CustomTags tags = ds.getCustomTags();
		if (tags.getType().equals(TYPE.JSON.name())) {
			JSONObject root = convertToJSON(rawData);
			JSONArray dataArray = root.getJSONArray(tags.getRoot());
			int top = Math.min(MAX_JSON_OBJECTS, dataArray.length());

			for (int i = 0; i < top; i++) {
				JSONObject jo = dataArray.getJSONObject(i);
				Marker ma = null;

				if (jo.has(tags.getTitle()) && jo.has(tags.getLat())
						&& jo.has(tags.getLon())) {
					Log.d("test", "marker valid");
					String idContent = "";
					if (jo.has(tags.getId())) {
						idContent = jo.getString(tags.getId());
					}

					String link = null;
					if (tags.getDetailPage() != null) {
						if (jo.has(tags.getDetailPage())
								&& jo.getInt(tags.getDetailPage()) != 0) {
							if (jo.has(tags.getUrl())) {
								link = jo.getString(tags.getUrl());
							}
						}
					} else {
						if (jo.has(tags.getUrl())) {
							link = jo.getString(tags.getUrl());
						}
					}

					Log.v(MixView.TAG, "processing custom JSON object");

					switch (ds.getDisplay()) {
					case CIRCLE_MARKER:
						ma = new POIMarker(idContent,
								HtmlUnescape.unescapeHTML(jo.getString(tags
										.getTitle())), jo.getDouble(tags
										.getLat()),
								jo.getDouble(tags.getLon()), jo.getDouble(tags
										.getAlt()), link, taskId, colour);
						break;
					case IMAGE_MARKER:
						String imageOwner = "";
						String imageUrl = jo.getString(tags.getImage());
						ma = new ImageMarker(idContent,
								HtmlUnescape.unescapeHTML(jo.getString(tags
										.getTitle())), jo.getDouble(tags
										.getLat()),
								jo.getDouble(tags.getLon()), jo.getDouble(tags
										.getAlt()), link, taskId, colour,
								imageOwner, imageUrl);
						break;
					case NAVIGATION_MARKER:
						ma = new NavigationMarker(idContent,
								HtmlUnescape.unescapeHTML(jo.getString(tags
										.getTitle())), jo.getDouble(tags
										.getLat()),
								jo.getDouble(tags.getLon()), jo.getDouble(tags
										.getAlt()), link, taskId, colour);
						break;
					}
					marker.add(ma);
				}
			}
		} else if (tags.getType().equals(TYPE.XML.name())) {
			Log.d("test", "xml");
			if (tags.getXmlParser().equals("Default")) {
				Log.d("test", "default");
				Marker ma = null;
				Element root = convertToXmlDocument(rawData)
						.getDocumentElement();
				NodeList nodes = root.getElementsByTagName(tags.getRoot());

				for (int i = 0; i < nodes.getLength(); i++) {
					Node node = nodes.item(i);
					NodeList childNodes = node.getChildNodes();
					String id = "", title = "", lat = "", lon = "", alt = "", url = "", detailPage = "", image = "";
					for (int j = 0; j < childNodes.getLength(); j++) {
						Node child = childNodes.item(j);
						if (child.getNodeName().equals(tags.getId())) {
							id = getNodeValue(child);
						} else if (child.getNodeName().equals(tags.getTitle())) {
							title = getNodeValue(child);
						} else if (child.getNodeName().equals(tags.getLat())) {
							lat = getNodeValue(child);
						} else if (child.getNodeName().equals(tags.getLon())) {
							lon = getNodeValue(child);
						} else if (child.getNodeName().equals(tags.getAlt())) {
							alt = getNodeValue(child);
						} else if (child.getNodeName().equals(tags.getUrl())) {
							url = getNodeValue(child);
						} else if (child.getNodeName().equals(tags.getDetailPage())) {
							detailPage = getNodeValue(child);
						} else if (child.getNodeName().equals(tags.getImage())) {
							image = getNodeValue(child);
						}
					}

					switch (ds.getDisplay()) {
					case CIRCLE_MARKER:
						ma = new POIMarker(id, title, Double.valueOf(lat),
								Double.valueOf(lon), Double.valueOf(alt), url,
								taskId, colour);
						break;
					case IMAGE_MARKER:
						String imageOwner = "";
						ma = new ImageMarker(id, title, Double.valueOf(lat),
								Double.valueOf(lon), Double.valueOf(alt), url,
								taskId, colour, imageOwner, image);
						break;
					case NAVIGATION_MARKER:
						ma = new NavigationMarker(id, title,
								Double.valueOf(lat), Double.valueOf(lon),
								Double.valueOf(alt), url, taskId, colour);
						break;
					}
					marker.add(ma);
				}
			} else {
				// TODO delegate conversion to plugin
				// selected plugin can be retrieved with tags.getXmlParser()
				// which returns the ServiceInfo name for example:
				// "org.mixare.plugin.sercive.ArenaProcessorService"

			}

		}

		return marker;
	}

	public Document convertToXmlDocument(String rawData) {
		Document doc = null;
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			// Document doc = builder.parse(is);d
			doc = builder.parse(new InputSource(new StringReader(rawData)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return doc;
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

	private JSONObject convertToJSON(String rawData) {
		try {
			return new JSONObject(rawData);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}

	public enum TYPE {
		JSON, XML
	}
}

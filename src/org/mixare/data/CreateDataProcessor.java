package org.mixare.data;

import java.util.ArrayList;
import java.util.List;

import org.mixare.MainActivity;
import org.mixare.R;
import org.mixare.lib.MixUtils;
import org.mixare.plugin.Plugin;
import org.mixare.plugin.PluginStatus;
import org.mixare.plugin.PluginType;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

/**
 * This activity is used to create you own DataProcessor.
 * 
 * @author KlemensE
 * 
 */
public class CreateDataProcessor extends SherlockPreferenceActivity implements
		OnPreferenceChangeListener {
	private static final int MENU_SAVE_ID = 0;
	private SharedPreferences prefs;
	Bundle extras;
	CustomTags customTags;
	private Preference list;
	Context ctx;
	ListPreference xmlParser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.dataprocessor);

		list = (ListPreference) findPreference(Constants.DataProcessor.Preferences.RESULT_TYPE);
		list.setOnPreferenceChangeListener(this);
		ctx = this;
		// retrieve previous values
		extras = getIntent().getExtras();
		if (extras != null) {
			if (extras.containsKey("customTags")) {
				customTags = (CustomTags) extras.getSerializable("customTags");
				if (customTags != null) {
					((ListPreference) list).setValue(customTags.getType());
					PreferenceCategory category = ((PreferenceCategory) findPreference(Constants.DataProcessor.Preferences.TYPE_CATEGORY));
					if (customTags.getType().equals("XML")) {
						if (xmlParser == null) {
							xmlParser = new ListPreference(ctx);
							xmlParser
									.setKey(Constants.DataProcessor.Preferences.XML_PARSER);
							xmlParser.setTitle(R.string.edittext_xml_parser_title);
							xmlParser.setSummary(R.string.edittext_xml_parser_summary);
							xmlParser
									.setDialogTitle(R.string.edittext_xml_parser_title);
							List<CharSequence> list = createParserList();
							xmlParser.setEntries(list.toArray(new CharSequence[list.size()]));
							xmlParser.setEntryValues(list.toArray(new CharSequence[list.size()]));
							if (list.contains(customTags.getXmlParser())) {
								xmlParser.setValue(customTags.getXmlParser());
							}
							category.addPreference(xmlParser);
						}
					}
					((EditTextPreference) findPreference(Constants.DataProcessor.Preferences.ROOT))
							.setText(!MixUtils.isNullOrEmpty(customTags
									.getRoot()) ? customTags.getRoot() : "");
					((EditTextPreference) findPreference(Constants.DataProcessor.Preferences.ID))
							.setText(!MixUtils.isNullOrEmpty(customTags.getId()) ? customTags
									.getId() : "");
					((EditTextPreference) findPreference(Constants.DataProcessor.Preferences.TITLE))
							.setText(!MixUtils.isNullOrEmpty(customTags
									.getTitle()) ? customTags.getTitle() : "");
					((EditTextPreference) findPreference(Constants.EDITTEXT_LATITUDE))
							.setText(!MixUtils.isNullOrEmpty(customTags
									.getLat()) ? customTags.getLat() : "");
					((EditTextPreference) findPreference(Constants.EDITTEXT_LONGITUDE))
							.setText(!MixUtils.isNullOrEmpty(customTags
									.getLon()) ? customTags.getLon() : "");
					((EditTextPreference) findPreference(Constants.EDITTEXT_ALTITUDE))
							.setText(!MixUtils.isNullOrEmpty(customTags
									.getAlt()) ? customTags.getAlt() : "");
					((EditTextPreference) findPreference(Constants.DataProcessor.Preferences.DETAIL_PAGE))
							.setText(!MixUtils.isNullOrEmpty(customTags
									.getDetailPage()) ? customTags
									.getDetailPage() : "");
					((EditTextPreference) findPreference(Constants.DataProcessor.Preferences.URL))
							.setText(!MixUtils.isNullOrEmpty(customTags
									.getUrl()) ? customTags.getUrl() : "");
					((EditTextPreference) findPreference(Constants.DataProcessor.Preferences.IMAGE))
							.setText(!MixUtils.isNullOrEmpty(customTags
									.getImage()) ? customTags.getImage() : "");
				}
			}
		}

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	protected void onResume() {
		super.onResume();
		prefs = getPreferenceScreen().getSharedPreferences();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(MENU_SAVE_ID, MENU_SAVE_ID, MENU_SAVE_ID,
				getString(R.string.save)).setShowAsAction(
				MenuItem.SHOW_AS_ACTION_IF_ROOM
						| MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// Don't save here
			setResult(1);
			finish();
			break;
		case MENU_SAVE_ID:
			if (checkPrefs()) {
				// save data to intent
				Intent data = new Intent();
				String type = prefs.getString(
						Constants.DataProcessor.Preferences.RESULT_TYPE,
						getString(R.string.XML));
				data.putExtra(Constants.DataProcessor.RESULT_TYPE, type);
				if (type.equals("XML")) {
					data.putExtra(
							Constants.DataProcessor.XML_PARSER,
							prefs.getString(
									Constants.DataProcessor.Preferences.XML_PARSER,
									getString(R.string.default_string)));
				}
				data.putExtra(Constants.DataProcessor.ROOT, prefs.getString(
						Constants.DataProcessor.Preferences.ROOT, null));
				data.putExtra(Constants.DataProcessor.ID, prefs.getString(
						Constants.DataProcessor.Preferences.ID, null));
				data.putExtra(Constants.DataProcessor.TITLE, prefs.getString(
						Constants.DataProcessor.Preferences.TITLE, null));
				data.putExtra(
						Constants.DataProcessor.DETAIL_PAGE,
						prefs.getString(
								Constants.DataProcessor.Preferences.DETAIL_PAGE,
								null));
				data.putExtra(Constants.DataProcessor.URL, prefs.getString(
						Constants.DataProcessor.Preferences.URL, null));
				data.putExtra(Constants.LATITUDE,
						prefs.getString(Constants.EDITTEXT_LATITUDE, null));
				data.putExtra(Constants.LONGITUDE,
						prefs.getString(Constants.EDITTEXT_LONGITUDE, null));
				data.putExtra(Constants.ALTITUDE,
						prefs.getString(Constants.EDITTEXT_ALTITUDE, null));
				data.putExtra(Constants.DataProcessor.IMAGE, prefs.getString(
						Constants.DataProcessor.Preferences.IMAGE, null));
				setResult(0, data);
				prefs.edit().clear().commit();
				finish();
			} else {
				Toast.makeText(getApplicationContext(), "prefs invalid",
						Toast.LENGTH_LONG).show();
			}
			break;
		}
		return true;
	}

	/**
	 * Checks whether the entered values are valid
	 * 
	 * @return True if they are valid, false if not
	 */
	private boolean checkPrefs() {
		if (prefs.contains(Constants.DataProcessor.Preferences.TITLE)
				&& prefs.contains(Constants.EDITTEXT_LATITUDE)
				&& prefs.contains(Constants.EDITTEXT_LONGITUDE)
				&& prefs.contains(Constants.DataProcessor.Preferences.ROOT)
				&& prefs.contains(Constants.DataProcessor.Preferences.RESULT_TYPE)) {
			if (prefs.getString(
					Constants.DataProcessor.Preferences.RESULT_TYPE, "")
					.equals("XML")) {
				if (prefs
						.contains(Constants.DataProcessor.Preferences.XML_PARSER)) {
					return true;
				} else {
					return false;
				}
			}
			return true;
		}

		return false;
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (preference == list) {
			String type = (String) newValue;
			PreferenceCategory category = ((PreferenceCategory) findPreference(Constants.DataProcessor.Preferences.TYPE_CATEGORY));
			if (customTags.getType().equals("XML")) {
				if (xmlParser == null) {
					xmlParser = new ListPreference(ctx);
					xmlParser
							.setKey(Constants.DataProcessor.Preferences.XML_PARSER);
					xmlParser.setTitle(R.string.edittext_xml_parser_title);
					xmlParser.setSummary(R.string.edittext_xml_parser_summary);
					xmlParser
							.setDialogTitle(R.string.edittext_xml_parser_title);
					List<CharSequence> list = createParserList();
					xmlParser.setEntries(list.toArray(new CharSequence[list.size()]));
					xmlParser.setEntryValues(list.toArray(new CharSequence[list.size()]));
					if (list.contains(customTags.getXmlParser())) {
						xmlParser.setValue(customTags.getXmlParser());
					}
					category.addPreference(xmlParser);
				}
			} else {
				if (xmlParser != null) {
					category.removePreference(xmlParser);
					xmlParser = null;
				}
			}
		}
		return true;
	}

	private List<CharSequence> createParserList() {
		List<CharSequence> ret = new ArrayList<CharSequence>();
		ret.add(getString(R.string.default_string));
		List<Plugin> list = MainActivity.getPlugins();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getPluginType().equals(PluginType.DATAHANDLER)
					&& list.get(i).getPluginStatus()
							.equals(PluginStatus.Activated)) {
				ret.add(list.get(i).getServiceInfo().name);
			}
		}

		return ret;
	}
}
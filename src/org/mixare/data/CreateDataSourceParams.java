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

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.mixare.R;
import org.mixare.gui.AdditionalParamPreference;
import org.mixare.gui.SeekBarPreference;
import org.mixare.lib.MixUtils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

/**
 * This activity is used to create your own Request Parameter.
 * 
 * @author KlemensE
 * 
 */
public class CreateDataSourceParams extends SherlockPreferenceActivity {
	Preference bounding_box;
	Preference location;
	Preference additional;
	Bundle extras;
	CreateParams params;
	Context ctx;
	// Additional Prefs count
	int i = 0;
	SharedPreferences prefs;
	protected int ADDITIONAL_REQUEST_CODE = 1000;
	ParamHolder holder;

	private static final int MENU_SAVE_ID = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.ctx = this;

		addPreferencesFromResource(R.xml.preferences);
		bounding_box = findPreference(Constants.CustomParams.Preferences.BOUNDING_BOX);
		location = findPreference(Constants.CustomParams.Preferences.LOCATION);
		additional = findPreference(Constants.CustomParams.Preferences.ADDITIONAL);
		additional.setOnPreferenceClickListener(additionalClickListener);
		bounding_box.setOnPreferenceChangeListener(bboxChangeListener);
		location.setOnPreferenceChangeListener(bboxChangeListener);

		prefs = getPreferenceScreen().getSharedPreferences();

		// retrieve previous values
		extras = getIntent().getExtras();
		if (extras != null) {
			if (extras.containsKey("customParams")) {
				params = (CreateParams) extras.getSerializable("customParams");
				if (params != null) {
					if (params.getType() == 0) {
						((CheckBoxPreference) bounding_box).setChecked(true);
						((ListPreference) findPreference(Constants.CustomParams.Preferences.BBOX_LIST))
								.setValue(getResources().getStringArray(
										R.array.bbox_list)[params.getBboxType()]);
					} else if (params.getType() == 1) {
						((CheckBoxPreference) location).setChecked(true);

						((EditTextPreference) findPreference(Constants.EDITTEXT_LATITUDE))
								.setText(!MixUtils.isNullOrEmpty(params
										.getLatitudeParamName()) ? params
										.getLatitudeParamName() : "");

						((EditTextPreference) findPreference(Constants.EDITTEXT_LONGITUDE))
								.setText(!MixUtils.isNullOrEmpty(params
										.getLongitudeParamName()) ? params
										.getLongitudeParamName() : "");

						((EditTextPreference) findPreference(Constants.EDITTEXT_ALTITUDE))
								.setText(!MixUtils.isNullOrEmpty(params
										.getAltitudeParamName()) ? params
										.getAltitudeParamName() : "");

						((EditTextPreference) findPreference(Constants.CustomParams.Preferences.RADIUS))
								.setText(!MixUtils.isNullOrEmpty(params
										.getRadiusParamName()) ? params
										.getRadiusParamName() : "");

						((SeekBarPreference) findPreference(Constants.CustomParams.Preferences.MAXRADIUS))
								.setProgress(params.getMaxRadius());
					}
					((EditTextPreference) findPreference(Constants.CustomParams.Preferences.LOCALE))
							.setText(!MixUtils.isNullOrEmpty(params
									.getLocalParamName()) ? params
									.getLocalParamName() : "");

					holder = new ParamHolder(params.getAdditionalParams());
				}
			}
		}

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
			prefs.edit().clear().commit();
			finish();
			break;
		case MENU_SAVE_ID:
			if (checkPrefs()) {
				// save data to intent
				Intent data = new Intent();
				boolean use_bounding_box = checkBoxPreferenceChecked(bounding_box);
				data.putExtra(
						Constants.CustomParams.LOCATION_TYPE,
						use_bounding_box ? Constants.CustomParams.Preferences.BOUNDING_BOX
								: Constants.CustomParams.Preferences.LOCATION);
				if (use_bounding_box) {
					data.putExtra(
							Constants.CustomParams.BBOX_TYPE,
							prefs.getString(
									Constants.CustomParams.Preferences.BBOX_LIST,
									null));
				} else {
					data.putExtra(Constants.LATITUDE,
							prefs.getString(Constants.EDITTEXT_LATITUDE, null));
					data.putExtra(Constants.LONGITUDE,
							prefs.getString(Constants.EDITTEXT_LONGITUDE, null));
					data.putExtra(Constants.ALTITUDE,
							prefs.getString(Constants.EDITTEXT_ALTITUDE, null));
					data.putExtra(Constants.CustomParams.RADIUS, prefs
							.getString(
									Constants.CustomParams.Preferences.RADIUS,
									null));
					data.putExtra(
							Constants.CustomParams.MAXRADIUS,
							prefs.getInt(
									Constants.CustomParams.Preferences.MAXRADIUS,
									0));
				}
				data.putExtra(Constants.CustomParams.LOCALE, prefs.getString(
						Constants.CustomParams.Preferences.LOCALE, null));
				data.putExtra(Constants.CustomParams.ADDITIONAL, holder);
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ADDITIONAL_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				if (data != null) {
					holder = (ParamHolder) data
							.getSerializableExtra(Constants.CustomParams.ADDITIONAL);
				}
			}
		}
	}

	/**
	 * Checks if the entered values are valid.
	 * 
	 * @return True if the entered values are valid false otherwise.
	 */
	private boolean checkPrefs() {
		boolean valid = false;
		if (prefs.contains(Constants.CustomParams.Preferences.BOUNDING_BOX)
				&& checkBoxPreferenceChecked(bounding_box)) {
			if (prefs.contains(Constants.CustomParams.Preferences.BBOX_LIST)) {
				valid = prefs.getString(
						Constants.CustomParams.Preferences.BBOX_LIST, "") != null;
			}
		} else if (prefs.contains(Constants.CustomParams.Preferences.LOCATION)
				&& checkBoxPreferenceChecked(location)) {
			if (prefs.contains(Constants.EDITTEXT_LATITUDE)
					&& prefs.contains(Constants.EDITTEXT_LONGITUDE)) {
				if (prefs.getString(Constants.EDITTEXT_LATITUDE, "") != ""
						&& prefs.getString(Constants.EDITTEXT_LONGITUDE, "") != "") {
					valid = true;
				} else {
					valid = false;
				}
			}

		}
		return valid;
	}

	/**
	 * Checks if a CheckBox is checked
	 * 
	 * @param pref
	 *            The Preference to check
	 * @throws IllegalArgumentException
	 *             if the Preference is not a ChechBox
	 * @return True if the CheckBox is selected false if not
	 */
	private boolean checkBoxPreferenceChecked(Preference pref)
			throws IllegalArgumentException {
		if (!(pref instanceof CheckBoxPreference)) {
			throw new IllegalArgumentException(
					"pref is not an instance of CheckBoxPreference");
		}
		return ((CheckBoxPreference) pref).isChecked();
	}

	/**
	 * Gets fired when the AdditionalParameterPreference gets clicked. Opens a
	 * new Activity to add additional Parameter.
	 */
	OnPreferenceClickListener additionalClickListener = new OnPreferenceClickListener() {
		@Override
		public boolean onPreferenceClick(Preference preference) {
			if (preference.equals(additional)) {
				Intent add = new Intent(ctx, AdditionalParamActivity.class);
				add.putExtra(Constants.CustomParams.ADDITIONAL, holder);
				startActivityForResult(add, ADDITIONAL_REQUEST_CODE);
			}
			return false;
		}
	};

	/**
	 * Gets fired when either the BoundingBox or the Location CheckBox changed
	 * its value. Disables the other CheckBox.
	 */
	OnPreferenceChangeListener bboxChangeListener = new OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			if (preference.equals(bounding_box)) {
				if (newValue instanceof Boolean) {
					boolean enabled = (Boolean) newValue;
					CheckBoxPreference pref = (CheckBoxPreference) location;
					pref.setChecked(!enabled);
				}
			} else if (preference.equals(location)) {
				if (newValue instanceof Boolean) {
					boolean enabled = (Boolean) newValue;
					CheckBoxPreference pref = (CheckBoxPreference) bounding_box;
					pref.setChecked(!enabled);
				}
			}
			return true;
		}
	};
}
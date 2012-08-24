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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.mixare.R;
import org.mixare.gui.MixSpinner;
import org.mixare.lib.MixUtils;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

/**
 * Activity to add new DataSources
 * 
 * @author KlemensE
 */
public class AddDataSource extends SherlockActivity {
	private static final int MENU_SAVE_ID = Menu.FIRST;
	private static final int CUSTOM_DATA_SOURCE_ID = 898;
	private static final int CUSTOM_DATA_PROCESSOR_ID = 899;
	EditText nameField;
	EditText urlField;
	MixSpinner typeSpinner;
	Spinner displaySpinner;
	Spinner blurSpinner;
	MixSpinner dataProcessor;
	Bundle extras;
	CreateParams createParams;
	CustomTags tags;
	boolean launchCustom = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.datasourcedetails);

		nameField = (EditText) findViewById(R.id.name);
		urlField = (EditText) findViewById(R.id.url);
		typeSpinner = (MixSpinner) findViewById(R.id.type);
		typeSpinner
				.setOnItemSelectedEvenIfUnchangedListener(onItemSelectedListenerDataSource);
		displaySpinner = (Spinner) findViewById(R.id.displaytype);
		blurSpinner = (Spinner) findViewById(R.id.blurtype);
		dataProcessor = (MixSpinner) findViewById(R.id.dataProcessor);
		dataProcessor
				.setOnItemSelectedEvenIfUnchangedListener(onItemSelectedListenerDataProcessor);

		extras = getIntent().getExtras();
		if (extras != null) {
			// Get DataSource
			if (extras.containsKey("DataSourceId")) {
				DataSource ds = DataSourceStorage.getInstance().getDataSource(
						extras.getInt("DataSourceId"));
				nameField.setText(ds.getName(), TextView.BufferType.EDITABLE);
				urlField.setText(ds.getUrl(), TextView.BufferType.EDITABLE);
				launchCustom = false;
				typeSpinner.setSelection(ds.getTypeId());
				displaySpinner.setSelection(ds.getDisplayId());
				blurSpinner.setSelection(ds.getBlurId());
				if (ds.getType().equals(DataSource.TYPE.CUSTOM)
						&& ds.getProcessorId() != -1) {
					dataProcessor.setSelection(ds.getProcessorId());
					setDataProcessorVisible(true);
				}
				this.createParams = ds.getParamCreater();
				this.tags = ds.getCustomTags();
				launchCustom = true;
			}

			// Check whether DataSource can be edited or not
			if (extras.containsKey("isEditable")) {
				boolean activated = extras.getBoolean("isEditable");
				// nameField.setActivated(activated);
				nameField.setFocusable(activated);
				// urlField.setActivated(activated);
				urlField.setFocusable(activated);
				// typeSpinner.setActivated(activated);
				typeSpinner.setClickable(activated);
				// displaySpinner.setActivated(activated);
				displaySpinner.setClickable(activated);
				dataProcessor.setClickable(activated);
			}
		}

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	/**
	 * Creates a new DataSource and Saves it to the SharedPreferences
	 */
	private boolean saveNewDataSource() {
		String name = nameField.getText().toString();
		String url = urlField.getText().toString();
		int typeId = (int) typeSpinner.getItemIdAtPosition(typeSpinner
				.getSelectedItemPosition());
		int displayId = (int) displaySpinner.getItemIdAtPosition(displaySpinner
				.getSelectedItemPosition());
		int blurId = (int) blurSpinner.getItemIdAtPosition(blurSpinner
				.getSelectedItemPosition());
		int dataProcessorId = (int) dataProcessor
				.getItemIdAtPosition(dataProcessor.getSelectedItemPosition());
		if (!MixUtils.isNullOrEmpty(name) && !MixUtils.isNullOrEmpty(url)) {
			if (extras != null) {
				if (extras.containsKey("DataSourceId")) {
					// DataSource already exists
					DataSource ds = DataSourceStorage.getInstance()
							.getDataSource(extras.getInt("DataSourceId"));
					ds.setName(name);
					ds.setUrl(url);
					ds.setType(typeId);
					ds.setDisplay(displayId);
					ds.setBlur(blurId);
					ds.setProcessor(dataProcessorId);
					if (typeId == DataSource.TYPE.CUSTOM.ordinal()) {
						ds.setParamCreater(createParams);
						if (dataProcessor.getSelectedItem().equals(
								getString(R.string.CUSTOM))) {
							ds.setCustomTags(tags);
							ds.setProcessor(DataSource.DATA_PROCESSOR.CUSTOM);
						} else {
							ds.setProcessor(dataProcessorId);
						}
					} else {
						ds.setParamCreater(null);
						ds.setCustomTags(null);
					}

					DataSourceStorage.getInstance(getApplicationContext())
							.save();
					return true;
				}
			}

			// New DataSource
			DataSource ds = new DataSource(name, url,
					DataSource.TYPE.values()[typeId],
					DataSource.DISPLAY.values()[displayId], true, createParams,
					tags);
			ds.setBlur(DataSource.BLUR.values()[blurId]);
			ds.setProcessor(dataProcessorId);
			DataSourceStorage.getInstance().add(ds);
			DataSourceStorage.getInstance(getApplicationContext()).save();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean onMenuItemSelected(int featureId,
			com.actionbarsherlock.view.MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// Don't save here
			finish();
			break;
		case MENU_SAVE_ID:
			if (saveNewDataSource()) {
				finish();
			} else {
				Toast.makeText(this, "Error saving DataSource",
						Toast.LENGTH_LONG).show();
			}
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		menu.add(0, MENU_SAVE_ID, Menu.NONE, "Save").setShowAsAction(
				MenuItem.SHOW_AS_ACTION_IF_ROOM
						| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		return true;
	}

	/**
	 * Creates a new Dialog to describe the different Types of DataSources
	 * 
	 * @param v
	 */
	public void onDataSourceInfoClick(View v) {
		Builder builder = new Builder(this);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setMessage("This option tells mixare what informations your DataSource needs to process the request and send mixare the marker data. Some examples: \n\n"
				+ "Wikipedia: \n"
				+ "?lat=0.0&lng=0.0&radius=20.0&maxRows=50&lang=de&username=mixare \n\n"
				+ "Twitter: \n"
				+ "?geocode=0.0,0.0,20.0km \n\n"
				+ "Arena: \n"
				+ "&lat=0.0&lng=0.0 \n\n"
				+ "OSM: \n"
				+ "[bbox=-1.0,1.0,-2.0,2.0] \n\n"
				+ "Panoramio \n"
				+ "?set=public&from=0&to=20&minx=-180&miny=-90&maxx=180&maxy=90&size=medium&mapfilter=true \n\n"
				+ "Custom \n" + "You can create your own Request!");
		builder.setNegativeButton(getString(R.string.close_button),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				});
		AlertDialog alert1 = builder.create();
		alert1.setTitle("DataSource Info");
		alert1.show();
	}

	/**
	 * Gets fired when a item of the DataSourceSpinner was selected. If the
	 * selected item is "Custom" it opens a new Activity and makes a new Spinner
	 * visible.
	 */
	OnItemSelectedListener onItemSelectedListenerDataSource = new OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> parentView,
				View selectedItemView, int position, long id) {
			if (getResources().getStringArray(R.array.datasourcetypelist)[position]
					.equals(getString(R.string.CUSTOM)) && launchCustom) {
				setDataProcessorVisible(true);
				Intent paramIntent = new Intent(getApplicationContext(),
						CreateDataSourceParams.class);
				paramIntent.putExtra("customParams", createParams);
				startActivityForResult(paramIntent, CUSTOM_DATA_SOURCE_ID);
			} else {
				setDataProcessorVisible(false);
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parentView) {
			// your code here
		}
	};

	/**
	 * Gets fired when a item of the DataProcessorSpinner was selected. If the
	 * selected item is "Custom" it opens a new Activity where the different
	 * available tags can be set.
	 */
	OnItemSelectedListener onItemSelectedListenerDataProcessor = new OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> parentView,
				View selectedItemView, int position, long id) {
			if (getResources().getStringArray(R.array.dataprocessor)[position]
					.equals(getString(R.string.CUSTOM)) && launchCustom) {
				Intent dataProcesorIntent = new Intent(getApplicationContext(),
						CreateDataProcessor.class);
				dataProcesorIntent.putExtra("customTags", tags);
				startActivityForResult(dataProcesorIntent,
						CUSTOM_DATA_PROCESSOR_ID);
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parentView) {
			// your code here
		}
	};

	/**
	 * Changes the visibility of the DataProcessorLable and Spinner.
	 * @param visible Whether it should be visible or not
	 */
	private void setDataProcessorVisible(boolean visible) {
		TextView dataProcessorLable = (TextView) findViewById(R.id.dataProcessorLabel);
		if (visible) {
			dataProcessorLable.setVisibility(View.VISIBLE);
			dataProcessor.setVisibility(View.VISIBLE);
		} else {
			dataProcessorLable.setVisibility(View.GONE);
			dataProcessor.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case CUSTOM_DATA_SOURCE_ID:
			switch (resultCode) {
			case 0:
				if (data != null) {
					String type = data
							.getStringExtra(Constants.CustomParams.LOCATION_TYPE);
					// If BoundingBox was selected
					if (type.equals(Constants.CustomParams.BOUNDING_BOX)) {
						int bbox_type = data.getStringExtra(
								Constants.CustomParams.BBOX_TYPE).equals(
								getString(R.string.OSM)) ? 0 : 1;
						ParamHolder holder = (ParamHolder) data
								.getSerializableExtra(Constants.CustomParams.ADDITIONAL);
						createParams = new CreateParams(
								bbox_type,
								data.getStringExtra(Constants.CustomParams.LOCALE),
								holder == null ? null : holder.getMap());
						// If Location was selected
					} else if (type.equals(Constants.CustomParams.LOCATION)) {
						int maxRadius = data.getIntExtra(
								Constants.CustomParams.MAXRADIUS, 0);
						ParamHolder holder = (ParamHolder) data
								.getSerializableExtra(Constants.CustomParams.ADDITIONAL);
						createParams = new CreateParams(
								data.getStringExtra(Constants.LATITUDE),
								data.getStringExtra(Constants.LONGITUDE),
								data.getStringExtra(Constants.ALTITUDE),
								data.getStringExtra(Constants.CustomParams.RADIUS),
								maxRadius == 0 ? null : (Integer) maxRadius,
								data.getStringExtra(Constants.CustomParams.LOCALE),
								holder == null ? null : holder.getMap());
					} else {
						// Should never reach this
					}
					Log.d("test", createParams.createRequest(46.0, 11.5, 350,
							50, "de-DE"));
				} else {
					//TODO reset
				}
				break;
			case 1:
				//TODO reset
				break;
			}
			break;
		case CUSTOM_DATA_PROCESSOR_ID:
			switch (resultCode) {
			case 0:
				if (data != null) {
					tags = new CustomTags(
							data.getStringExtra(Constants.DataProcessor.RESULT_TYPE),
							data.getStringExtra(Constants.DataProcessor.ROOT),
							data.getStringExtra(Constants.DataProcessor.ID),
							data.getStringExtra(Constants.DataProcessor.TITLE),
							data.getStringExtra(Constants.LATITUDE),
							data.getStringExtra(Constants.LONGITUDE),
							data.getStringExtra(Constants.ALTITUDE),
							data.getStringExtra(Constants.DataProcessor.DETAIL_PAGE),
							data.getStringExtra(Constants.DataProcessor.URL),
							data.getStringExtra(Constants.DataProcessor.IMAGE),
							data.getStringExtra(Constants.DataProcessor.XML_PARSER));
					Log.d("test", tags.toString());
				} else {
					//TODO set to mixare dataProcessor
				}
				break;
			case 1:
				//TODO set to mixare dataProcessor
				break;
			}
		default:
			super.onActivityResult(requestCode, resultCode, data);
		}

	}
}
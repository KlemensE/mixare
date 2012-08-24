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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.mixare.R;
import org.mixare.gui.AdditionalParamPreference;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

/**
 * This class is used to create additional Parameter.
 * 
 * @author KlemensE
 * 
 */
public class AdditionalParamActivity extends SherlockActivity {
	private static final int MENU_ADD_PARAM_ID = 0;
	private static final int MENU_DELETE_ID = 1;
	private static final int MENU_EDIT_ID = 2;
	private static final int MENU_SAVE_ID = 3;
	private ParamAdapter paramAdapter;
	private ListView listView;
	private static final int ADD_REQUEST_CODE = 1;
	private Context ctx;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		ctx = this;
		Bundle extras = getIntent().getExtras();
		List<Param> list = createList(extras);

		setContentView(R.layout.list);
		paramAdapter = new ParamAdapter(this, 0, list);
		listView = (ListView) findViewById(R.id.section_list_view);
		listView.setAdapter(paramAdapter);
		registerForContextMenu(listView);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				Param param = paramAdapter.getItem(position);
				Intent edit = new Intent(ctx, AdditionalParamPreference.class);
				edit.putExtra(Constants.AdditionalParams.POSITION, position);
				edit.putExtra(Constants.AdditionalParams.KEY, param.key);
				edit.putExtra(Constants.AdditionalParams.VALUE, param.value);
				startActivityForResult(edit, MENU_EDIT_ID);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ADD_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				// add new param
				paramAdapter.add(new Param(data
						.getStringExtra(Constants.AdditionalParams.KEY), data
						.getStringExtra(Constants.AdditionalParams.VALUE)));
				paramAdapter.notifyDataSetChanged();
			}
		} else if (requestCode == MENU_EDIT_ID) {
			if (resultCode == RESULT_OK) {
				// replace previous param
				int position = data.getIntExtra(
						Constants.AdditionalParams.POSITION, -1);
				if (position != -1)
					paramAdapter.params
							.set(position,
									new Param(
											data.getStringExtra(Constants.AdditionalParams.KEY),
											data.getStringExtra(Constants.AdditionalParams.VALUE)));
			}
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		menu.add(MENU_DELETE_ID, MENU_DELETE_ID, MENU_DELETE_ID,
				R.string.data_source_delete);
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		AdapterView.AdapterContextMenuInfo info;
		try {
			info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		} catch (ClassCastException e) {
			return false;
		}
		final long idOfMenu = paramAdapter.getItemId(info.position);
		switch (item.getItemId()) {
		case MENU_DELETE_ID:
			paramAdapter.removeItem((int) idOfMenu);
			break;
		}
		return super.onContextItemSelected(item);
	}

	/**
	 * Creates the list to display
	 * 
	 * @param extras
	 *            The extras where the previous values could be stored
	 * @return The list to display
	 */
	private List<Param> createList(Bundle extras) {
		List<Param> params = new ArrayList<Param>();

		if (extras != null) {
			if (extras.containsKey(Constants.CustomParams.ADDITIONAL)) {
				ParamHolder holder = (ParamHolder) extras
						.getSerializable(Constants.CustomParams.ADDITIONAL);
				if (holder != null && holder.getMap() != null) {
					// iterate over map
					Set<Entry<String, String>> s = holder.getMap().entrySet();
					Iterator<Entry<String, String>> it = s.iterator();
					while (it.hasNext()) {
						Map.Entry<String, String> m = (Entry<String, String>) it
								.next();
						Param paramToAdd = new Param(m.getKey(), m.getValue());
						params.add(paramToAdd);
					}
				}
			}
		}

		return params;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(MENU_ADD_PARAM_ID, MENU_ADD_PARAM_ID, MENU_ADD_PARAM_ID,
				R.string.select_plugin)
				.setIcon(android.R.drawable.ic_input_add)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		menu.add(0, MENU_SAVE_ID, Menu.NONE, "Save").setShowAsAction(
				MenuItem.SHOW_AS_ACTION_IF_ROOM
						| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// Don't save here
			setResult(RESULT_CANCELED);
			finish();
			break;
		case MENU_SAVE_ID:
			// save
			ParamHolder holder = new ParamHolder();
			for (int i = 0; i < paramAdapter.getCount(); i++) {
				Param param = paramAdapter.getItem(i);
				holder.addToMap(param.key, param.value);
			}
			Intent result = new Intent();
			result.putExtra(Constants.CustomParams.ADDITIONAL, holder);
			setResult(RESULT_OK, result);
			finish();
			break;
		case MENU_ADD_PARAM_ID:
			// create a new
			Intent add = new Intent(ctx, AdditionalParamPreference.class);
			startActivityForResult(add, ADD_REQUEST_CODE);
			break;
		}
		return true;
	}

	/**
	 * The class holding the values
	 * 
	 * @author KlemensE
	 * 
	 */
	private class Param {
		private String key;
		private String value;

		public Param(String key, String value) {
			this.key = key;
			this.value = value;
		}
	}

	/**
	 * The Adapter which handles the displaying of the list
	 * 
	 * @author KlemensE
	 * 
	 */
	private class ParamAdapter extends ArrayAdapter<Param> {
		private List<Param> params;

		public ParamAdapter(Context context, int textViewResourceId,
				List<Param> objects) {
			super(context, textViewResourceId, objects);
			this.params = objects;
		}

		/**
		 * Removes the item at the given position
		 * @param position The position of the item wich should be removed
		 */
		public void removeItem(int position) {
			params.remove(position);
			this.notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return this.params.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.param_list,
						null);
				holder = new ViewHolder();
				holder.title = (TextView) convertView.findViewById(R.id.title);
				holder.desc = (TextView) convertView.findViewById(R.id.desc);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.title.setText(params.get(position).key);
			holder.desc.setText(params.get(position).value);

			return convertView;
		}

		private class ViewHolder {
			TextView title;
			TextView desc;
		}
	}
}
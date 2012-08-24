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
package org.mixare.gui;

import org.mixare.R;
import org.mixare.lib.MixUtils;

import com.actionbarsherlock.app.SherlockActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AdditionalParamPreference extends SherlockActivity {
	private EditText editTextKey, editTextValue;
	Bundle extras;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.additional_param);
		editTextKey = (EditText) findViewById(R.id.editTextKey);
		editTextValue = (EditText) findViewById(R.id.editTextValue);
		
		extras = getIntent().getExtras();
		if (extras!=null) {
			editTextKey.setText(extras.getString("key"));
			editTextValue.setText(extras.getString("value"));
		}
		
		findViewById(R.id.ok).setOnClickListener(onClickListener);
		findViewById(R.id.cancel).setOnClickListener(onClickListener);
	}

	OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.ok:
				// TODO: remove illegal chars like ',' ...
				Intent data = new Intent();
				if (extras != null) {
					data.putExtra("position", extras.getInt("position"));
				}
				data.putExtra("key", getEditTextKey());
				data.putExtra("value", getEditTextValue());
				setResult(RESULT_OK, data);
				finish();
				break;
			case R.id.cancel:
				setResult(RESULT_CANCELED);
				finish();
				break;
			}
		}
	};
	
	public String getEditTextKey() {
		if (editTextKey == null) {
			return null;
		}
		return editTextKey.getText().toString();
	}

	public String getEditTextValue() {
		if (editTextValue == null) {
			return null;
		}
		return editTextValue.getText().toString();
	}
}

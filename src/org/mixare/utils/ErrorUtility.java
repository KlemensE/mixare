/*
 * Copyright (C) 2011- Armando Miraglia &lt;arma&#64;lamortenera.it&gt;
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
package org.mixare.utils;

import android.util.Log;

/**
* Class intended to provide few utilities for printing errors, communicating
* the error to the user. This will also handle the logging of the errors.
*
* @author Armando Miraglia &lt;arma&#64;lamortenera.it&gt;
*/
public class ErrorUtility {
  /**
  * The constructor is set private because the class is not intended
  * to be instantiated.
  */
  private ErrorUtility() { /* private: not exposed for usage */ }

  public static void handleErrorDebug(String tag, Exception e, boolean isShown) {
    if(e != null) {

      /* if(isShown) setErrorDialog(e); */
      Log.d(tag, e.getStackTrace().toString());
    }
  }

  /**
  * This method takes care to handle the exception so that it is
  * correctly logged and also shown to the user, if needed.
  */
  public static void handleError(String tag, Exception e, boolean isShown) {
    if(e != null) {

      /* if(isShown) setErrorDialog(e); */
      Log.e(tag, e.getStackTrace().toString());
    }
  }

//	private static void setErrorDialog() {
//		AlertDialog.Builder builder = new AlertDialog.Builder(this);
//		builder.setMessage(getString(DataView.CONNECTION_ERROR_DIALOG_TEXT));
//		builder.setCancelable(false);
//
//		/*Retry*/
//		builder.setPositiveButton(DataView.CONNECTION_ERROR_DIALOG_BUTTON1, new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialog, int id) {
//				fError=false;
//				//TODO improve
//				try {
//					repaint();	       		
//				}
//				catch(Exception ex){
//					//Don't call doError, it will be a recursive call.
//					//doError(ex);
//				}
//			}
//		});
//		/*Open settings*/
//		builder.setNeutralButton(DataView.CONNECTION_ERROR_DIALOG_BUTTON2, new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialog, int id) {
//				Intent intent1 = new Intent(Settings.ACTION_WIRELESS_SETTINGS); 
//				startActivityForResult(intent1, 42);
//			}
//		});
//		/*Close application*/
//		builder.setNegativeButton(DataView.CONNECTION_ERROR_DIALOG_BUTTON3, new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialog, int id) {
//				System.exit(0);
//			}
//		});
//		AlertDialog alert = builder.create();
//		alert.show();
//	}
}

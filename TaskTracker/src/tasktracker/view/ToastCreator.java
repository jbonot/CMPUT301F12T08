package tasktracker.view;

import android.content.Context;
import android.view.*;
import android.widget.*;

/**
 * Copyright (C) 2012 Andrea Budac, Kurtis Morin, Christian Jukna
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * ToastCreator Creates toast items to display information to the user when
 * feedback is needed from the program.
 * 
 * @author Andrea Budac: abudac
 * @author Christian Jukna: jukna
 * @author Kurtis Morin: kmorin1
 * 
 *         April 06, 2012
 * @author Jeanine Bonot
 * 
 */
// NOV2012 - Jeanine Bonot - Modified to use TaskTracker layouts. Added
// documentation.
public final class ToastCreator {

	/**
	 * A function that takes in a string and displays this string in a toast.
	 * Whenever information is needed to be displayed we create a string and use
	 * this method to give the user feedback.
	 * 
	 * @param string
	 *            The message string.
	 */
//	public void showToast(String string) {
//		LayoutInflater inflater = (LayoutInflater) context
//				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//		View layout = (ViewGroup) inflater.inflate(R.layout.toast_layout, null);
//
//		TextView text = (TextView) layout.findViewById(R.id.toast_text);
//		text.setText(string);
//
//		Toast toast = new Toast(context);
//		toast.setGravity(Gravity.CENTER_VERTICAL, Gravity.CENTER_HORIZONTAL, 0);
//		toast.setDuration(Toast.LENGTH_SHORT);
//		toast.setView(layout);
//		toast.show();
//	}

	public static void showLongToast(Context context, String message) {
		Toast toast = Toast.makeText(context, message,
				Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
	
	public static void showShortToast(Context context, String message) {
		Toast toast = Toast.makeText(context, message,
				Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
}
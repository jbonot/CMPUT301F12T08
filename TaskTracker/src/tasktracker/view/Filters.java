package tasktracker.view;

/**
 * TaskTracker
 * 
 * Copyright 2012 Jeanine Bonot, Michael Dardis, Katherine Jasniewski,
 * Jason Morawski
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may 
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the 
 * specific language governing permissions and limitations under the License.
 */

import android.text.InputFilter;
import android.text.Spanned;

public class Filters {

	public static InputFilter specialCharFilter(){
		InputFilter filter = new InputFilter() { 
			public CharSequence filter(CharSequence source, int start, int end, 
					Spanned dest, int dstart, int dend) { 
				for (int i = start; i < end; i++) { 
					
					if ( (source.charAt(i)=='<') || (source.charAt(i)=='>')
							|| (source.charAt(i)=='{')|| (source.charAt(i)=='}')
							|| (source.charAt(i)=='"')|| (source.charAt(i)=='%')
							|| (source.charAt(i)=='&')) { 
						return ""; 
					} 
					//!Character.isLetterOrDigit(source.charAt(i))
				} 
				return null; 
			} 
		};
		return filter;
	}
}

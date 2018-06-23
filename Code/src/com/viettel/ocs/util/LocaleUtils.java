package com.viettel.ocs.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

public class LocaleUtils {

	static private HashMap<String, ResourceBundle> bundleMap = new HashMap<String, ResourceBundle>();

	public static String getString(Locale locale, String key, String... values) {

		String retVal;
		ResourceBundle bundle;
		try {

			if (bundleMap.containsKey(locale.getLanguage())) {
				bundle = bundleMap.get(locale.getLanguage());
			} else {
				bundle = ResourceBundle.getBundle("resources.lang", locale);
				bundleMap.put(locale.getLanguage(), bundle);
			}
			retVal = bundle.getString(key);
			if (values != null) {
				for (int i = 0; i < values.length; i++) {
					retVal = retVal.replace("{" + i + "}", values[i]);
				}
			}

			return retVal;
		} catch (Exception e) {
			// 
			return key;
		}
	}
}

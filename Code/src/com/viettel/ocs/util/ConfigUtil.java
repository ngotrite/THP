package com.viettel.ocs.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

public class ConfigUtil {

	private static final ResourceBundle cfgResource = ResourceBundle.getBundle("resources.config");
	public static String getCfg(String key) {
		return cfgResource.getString(key);
	}
}

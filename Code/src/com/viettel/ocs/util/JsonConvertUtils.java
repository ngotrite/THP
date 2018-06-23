package com.viettel.ocs.util;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.viettel.ocs.db.ExcludeFieldJson;

/**
 * *
 * 
 * @author Nampv
 *
 */
public class JsonConvertUtils {

	public static String convertObjectToJson(Object object) {
		if (object != null) {
			Gson gson = new GsonBuilder().setExclusionStrategies(new AnnotationExclusionStrategy()).create();
			String json = gson.toJson(object);
			return json;
		}
		return "";
	}

	public static Object convertJsonToObject(String jsonInString) {
		if (!jsonInString.isEmpty()) {
			Gson gson = new Gson();
			// Convert JSON to Java Object
			Object object = gson.fromJson(jsonInString, Object.class);
			return object;
		}
		return "";
	}
}

class AnnotationExclusionStrategy implements ExclusionStrategy {

    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        return f.getAnnotation(ExcludeFieldJson.class) != null;
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return false;
    }
}

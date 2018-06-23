package com.viettel.ocs.constant;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import org.hibernate.type.ListType;

public class Setting {

	public static class RoleType {
		
		public static String ADMIN = "admin";
		public static String OPERATOR = "operator";
		public static String NORMAL = "normal";
		public static String VIEWER = "viewer";
		public static String GUEST = "guest";
		
		public static String ADMIN_NAME = "Administrator";
		public static String OPERATOR_NAME = "Operator";
		public static String NORMAL_NAME = "Normal User";
		public static String VIEWER_NAME = "Viewer";
		public static String GUEST_NAME = "Guest";
		
		public static Map<String, String> listType = new LinkedHashMap<String, String>();
		static {
			listType.put(ADMIN, ADMIN_NAME);
			listType.put(OPERATOR, OPERATOR_NAME);
			listType.put(NORMAL, NORMAL_NAME);
			listType.put(VIEWER, VIEWER_NAME);
			listType.put(GUEST, GUEST_NAME);
		}
	}
}

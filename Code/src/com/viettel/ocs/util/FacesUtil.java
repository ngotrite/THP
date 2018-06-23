package com.viettel.ocs.util;

import javax.faces.context.FacesContext;

/**
 * Bean communication
 * 
 * @author THANHND
 *
 */
public class FacesUtil {
	public static Object getApplicationMapValue(String key) {
		return FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().get(key);
	}

	public static void setApplicationMapValue(String key, Object value) {
		FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().put(key, value);
	}

	public static Object getSessionMapValue(String key) {
		return FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(key);
	}

	public static void setSessionMapValue(String key, Object value) {
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(key, value);
	}
}

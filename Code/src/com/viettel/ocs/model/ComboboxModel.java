package com.viettel.ocs.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ComboboxModel implements Serializable {

	private String label;
	private Object value;

	public ComboboxModel(String label, Object value) {
		super();
		this.label = label;
		this.value = value;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}

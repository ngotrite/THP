package com.viettel.ocs.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ColumnModel implements Serializable, Cloneable {

	private String header;
	private long normalizerId;

	public ColumnModel(String header, long normalizerId) {
		super();
		this.header = header;
		this.normalizerId = normalizerId;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public long getNormalizerId() {
		return normalizerId;
	}

	public void setNormalizerId(long normalizerId) {
		this.normalizerId = normalizerId;
	}

	@Override
	public ColumnModel clone() throws CloneNotSupportedException {
		return (ColumnModel) super.clone();
	}

}
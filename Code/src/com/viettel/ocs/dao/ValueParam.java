package com.viettel.ocs.dao;

import com.viettel.ocs.entity.NormParam;
import com.viettel.ocs.entity.NormValue;

public class ValueParam {
	private NormParam normParam;

	private NormValue normValue;

	private int compareType;

	public ValueParam() {
	}

	public ValueParam(NormParam normParam, NormValue normValue, int compareType) {
		super();
		this.normParam = normParam;
		this.normValue = normValue;
		this.compareType = compareType;
	}

	public NormParam getNormParam() {
		return normParam;
	}

	public void setNormParam(NormParam normParam) {
		this.normParam = normParam;
	}

	public int getCompareType() {
		return compareType;
	}

	public void setCompareType(int compareType) {
		this.compareType = compareType;
	}

	public NormValue getNormValue() {
		return normValue;
	}

	public void setNormValue(NormValue normValue) {
		this.normValue = normValue;
	}
}

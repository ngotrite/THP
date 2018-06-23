package com.viettel.ocs.model;

import java.io.Serializable;

import com.viettel.ocs.entity.RowDecisionTableMap;
import com.viettel.ocs.entity.RowDt;

@SuppressWarnings("serial")
public class RowDecisionTableMapModel implements Serializable {
	private RowDecisionTableMap rowDecisionTableMap;
	private RowDt rowDt;

	public RowDecisionTableMapModel(RowDecisionTableMap rowDecisionTableMap, RowDt rowDt) {
		super();
		this.rowDecisionTableMap = rowDecisionTableMap;
		this.rowDt = rowDt;
	}

	public RowDecisionTableMap getRowDecisionTableMap() {
		return rowDecisionTableMap;
	}

	public void setRowDecisionTableMap(RowDecisionTableMap rowDecisionTableMap) {
		this.rowDecisionTableMap = rowDecisionTableMap;
	}

	public RowDt getRowDt() {
		return rowDt;
	}

	public void setRowDt(RowDt rowDt) {
		this.rowDt = rowDt;
	}

}

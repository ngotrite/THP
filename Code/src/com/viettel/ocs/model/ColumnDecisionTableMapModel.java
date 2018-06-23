package com.viettel.ocs.model;

import java.io.Serializable;

import com.viettel.ocs.entity.ColumnDecisionTableMap;
import com.viettel.ocs.entity.ColumnDt;

@SuppressWarnings("serial")
public class ColumnDecisionTableMapModel implements Serializable {
	private ColumnDecisionTableMap columnDecisionTableMap;
	private ColumnDt columnDt;

	public ColumnDecisionTableMapModel(ColumnDecisionTableMap columnDecisionTableMap, ColumnDt columnDt) {
		super();
		this.columnDecisionTableMap = columnDecisionTableMap;
		this.columnDt = columnDt;
	}

	public ColumnDecisionTableMap getColumnDecisionTableMap() {
		return columnDecisionTableMap;
	}

	public void setColumnDecisionTableMap(ColumnDecisionTableMap columnDecisionTableMap) {
		this.columnDecisionTableMap = columnDecisionTableMap;
	}

	public ColumnDt getColumnDt() {
		return columnDt;
	}

	public void setColumnDt(ColumnDt columnDt) {
		this.columnDt = columnDt;
	}

}

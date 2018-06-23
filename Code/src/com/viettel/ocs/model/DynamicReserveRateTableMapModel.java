package com.viettel.ocs.model;

import com.viettel.ocs.entity.DynamicReserveRateTableMap;
import com.viettel.ocs.entity.RateTable;

public class DynamicReserveRateTableMapModel {
	private DynamicReserveRateTableMap dynamicReserveRateTableMap;
	private RateTable rateTable;

	public DynamicReserveRateTableMapModel(DynamicReserveRateTableMap dynamicReserveRateTableMap, RateTable rateTable) {
		super();
		this.dynamicReserveRateTableMap = dynamicReserveRateTableMap;
		this.rateTable = rateTable;
	}

	public DynamicReserveRateTableMap getDynamicReserveRateTableMap() {
		return dynamicReserveRateTableMap;
	}

	public void setDynamicReserveRateTableMap(DynamicReserveRateTableMap dynamicReserveRateTableMap) {
		this.dynamicReserveRateTableMap = dynamicReserveRateTableMap;
	}

	public RateTable getRateTable() {
		return rateTable;
	}

	public void setRateTable(RateTable rateTable) {
		this.rateTable = rateTable;
	}

}

package com.viettel.ocs.model;

import java.io.Serializable;

import com.viettel.ocs.entity.RateTable;
import com.viettel.ocs.entity.SortPriceRateTableMap;

public class SortPriceRateTableMapModal implements Serializable {
	private SortPriceRateTableMap sortPriceRateTableMap;
	private RateTable rateTable;

	public SortPriceRateTableMapModal(SortPriceRateTableMap sortPriceRateTableMap, RateTable rateTable) {
		super();
		this.sortPriceRateTableMap = sortPriceRateTableMap;
		this.rateTable = rateTable;
	}

	public SortPriceRateTableMap getSortPriceRateTableMap() {
		return sortPriceRateTableMap;
	}

	public void setSortPriceRateTableMap(SortPriceRateTableMap sortPriceRateTableMap) {
		this.sortPriceRateTableMap = sortPriceRateTableMap;
	}

	public RateTable getRateTable() {
		return rateTable;
	}

	public void setRateTable(RateTable rateTable) {
		this.rateTable = rateTable;
	}

}

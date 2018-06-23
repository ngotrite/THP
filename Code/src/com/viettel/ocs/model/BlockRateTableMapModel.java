package com.viettel.ocs.model;

import java.io.Serializable;

import com.viettel.ocs.entity.BlockRateTableMap;
import com.viettel.ocs.entity.RateTable;

@SuppressWarnings("serial")
public class BlockRateTableMapModel implements Serializable {

	private BlockRateTableMap blockRateTableMap;
	private RateTable rateTable;

	public BlockRateTableMapModel(BlockRateTableMap blockRateTableMap, RateTable rateTable) {
		super();
		this.blockRateTableMap = blockRateTableMap;
		this.rateTable = rateTable;
	}

	public BlockRateTableMap getBlockRateTableMap() {
		return blockRateTableMap;
	}

	public void setBlockRateTableMap(BlockRateTableMap blockRateTableMap) {
		this.blockRateTableMap = blockRateTableMap;
	}

	public RateTable getRateTable() {
		return rateTable;
	}

	public void setRateTable(RateTable rateTable) {
		this.rateTable = rateTable;
	}

}

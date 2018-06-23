package com.viettel.ocs.entity;
// Generated Sep 2, 2016 4:54:08 PM by Hibernate Tools 3.2.1.GA

import javax.persistence.Transient;


public class RateTableDump extends BaseEntity implements java.io.Serializable, Cloneable {

	@Override
	@Transient
	public String getNodeName() {
		return this.rateTableName;
	}

	private long blockId;
	private String rateTableName;
	private long componentType;

	public RateTableDump() {
	}

	public String getRateTableName() {
		return rateTableName;
	}

	public void setRateTableName(String rateTableName) {
		this.rateTableName = rateTableName;
	}

	public long getComponentType() {
		return componentType;
	}

	public void setComponentType(long componentType) {
		this.componentType = componentType;
	}

	public long getBlockId() {
		return blockId;
	}

	public void setBlockId(long blockId) {
		this.blockId = blockId;
	}
	
}

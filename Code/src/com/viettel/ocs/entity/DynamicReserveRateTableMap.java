package com.viettel.ocs.entity;
// Generated Sep 2, 2016 4:54:08 PM by Hibernate Tools 3.2.1.GA

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * DynamicReserveRateTableMap generated by hbm2java
 */
@Entity
@Table(name = "dynamic_reserve_rate_table_map")
public class DynamicReserveRateTableMap extends BaseEntity implements java.io.Serializable, Cloneable {

	@Override
	@Transient
	public String getNodeName() {
		return null;
	}

	private Long domainId;

	@Column(name = "DOMAIN_ID")
	public Long getDomainId() {
		return domainId;
	}

	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}

	private long dynamicRateMapId;
	private Long rateTableId;
	private Long dynamicReserveId;
	private Long rateTableIndex;

	public DynamicReserveRateTableMap() {
	}

	public DynamicReserveRateTableMap(Long rateTableId, Long dynamicReserveId, Long rateTableIndex) {
		this.rateTableId = rateTableId;
		this.dynamicReserveId = dynamicReserveId;
		this.rateTableIndex = rateTableIndex;
	}

	public DynamicReserveRateTableMap(Long rateTableId, Long dynamicReserveId) {
		this.rateTableId = rateTableId;
		this.dynamicReserveId = dynamicReserveId;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)

	@Column(name = "DYNAMIC_RATE_MAP_ID", unique = true, nullable = false)
	public long getDynamicRateMapId() {
		return this.dynamicRateMapId;
	}

	public void setDynamicRateMapId(long dynamicRateMapId) {
		this.dynamicRateMapId = dynamicRateMapId;
	}

	@Column(name = "RATE_TABLE_ID")
	public Long getRateTableId() {
		return this.rateTableId;
	}

	public void setRateTableId(Long rateTableId) {
		this.rateTableId = rateTableId;
	}

	@Column(name = "DYNAMIC_RESERVE_ID")
	public Long getDynamicReserveId() {
		return this.dynamicReserveId;
	}

	public void setDynamicReserveId(Long dynamicReserveId) {
		this.dynamicReserveId = dynamicReserveId;
	}

	@Column(name = "RATE_TABLE_INDEX")
	public Long getRateTableIndex() {
		return this.rateTableIndex;
	}

	public void setRateTableIndex(Long rateTableIndex) {
		this.rateTableIndex = rateTableIndex;
	}

	@Override
	public DynamicReserveRateTableMap clone() throws CloneNotSupportedException {
		return (DynamicReserveRateTableMap) super.clone();
	}

}
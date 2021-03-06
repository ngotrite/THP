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
 * SortPriceRateTableMap generated by hbm2java
 */
@Entity
@Table(name = "sort_price_rate_table_map")
public class SortPriceRateTableMap extends BaseEntity implements java.io.Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4211483546967293774L;

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

	private long sortPriceRateTableMapId;
	private Long sortPriceComponentId;
	private Long rateTableId;
	private Long rateTableIndex;

	public SortPriceRateTableMap() {
	}

	public SortPriceRateTableMap(Long sortPriceComponentId, Long rateTableId, Long rateTableIndex) {
		this.sortPriceComponentId = sortPriceComponentId;
		this.rateTableId = rateTableId;
		this.rateTableIndex = rateTableIndex;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)

	@Column(name = "SORT_PRICE_RATE_TABLE_MAP_ID", unique = true, nullable = false)
	public long getSortPriceRateTableMapId() {
		return this.sortPriceRateTableMapId;
	}

	public void setSortPriceRateTableMapId(long sortPriceRateTableMapId) {
		this.sortPriceRateTableMapId = sortPriceRateTableMapId;
	}

	@Column(name = "SORT_PRICE_COMPONENT_ID")
	public Long getSortPriceComponentId() {
		return this.sortPriceComponentId;
	}

	public void setSortPriceComponentId(Long sortPriceComponentId) {
		this.sortPriceComponentId = sortPriceComponentId;
	}

	@Column(name = "RATE_TABLE_ID")
	public Long getRateTableId() {
		return this.rateTableId;
	}

	public void setRateTableId(Long rateTableId) {
		this.rateTableId = rateTableId;
	}

	@Column(name = "RATE_TABLE_INDEX")
	public Long getRateTableIndex() {
		return this.rateTableIndex;
	}

	public void setRateTableIndex(Long rateTableIndex) {
		this.rateTableIndex = rateTableIndex;
	}

	@Override
	public SortPriceRateTableMap clone() throws CloneNotSupportedException {
		return (SortPriceRateTableMap) super.clone();
	}

}

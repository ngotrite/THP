package com.viettel.ocs.entity;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;


@SuppressWarnings("serial")
@Entity
@Table(name = "statistic_item")
public class StatisticItem extends BaseEntity implements Serializable, Cloneable{
	
	@Override
	@Transient
	public String getNodeName() {
		return this.statisticItemName;
	}
	
	private Long domainId;

	@Column(name = "DOMAIN_ID")
	public Long getDomainId() {
		return domainId;
	}

	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}
	
	
	private long statisticItemId;
	private String statisticItemName;
	private String statisticItemDesc;
	private Long categoryId;
	private Long pos_Idx;
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "STATISTIC_ITEM_ID", unique = true, nullable = false)
	public long getStatisticItemId() {
		return statisticItemId;
	}

	public void setStatisticItemId(long statisticItemId) {
		this.statisticItemId = statisticItemId;
	}

	@Column(name = "STATISTIC_ITEM_NAME")
	public String getStatisticItemName() {
		return statisticItemName;
	}

	public void setStatisticItemName(String statisticItemName) {
		this.statisticItemName = statisticItemName;
	}

	@Column(name = "STATISTIC_ITEM_DESC")
	public String getStatisticItemDesc() {
		return statisticItemDesc;
	}

	public void setStatisticItemDesc(String statisticItemDesc) {
		this.statisticItemDesc = statisticItemDesc;
	}

	@Column(name = "CATEGORY_ID")
	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	@Column(name = "POS_IDX")
	public Long getPos_Idx() {
		return pos_Idx;
	}

	public void setPos_Idx(Long pos_Idx) {
		this.pos_Idx = pos_Idx;
	}
	
	
	
	@Override
	public StatisticItem clone() throws CloneNotSupportedException {
		return (StatisticItem) super.clone();
	}

}

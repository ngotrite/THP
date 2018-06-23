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
 * ReserveInfo generated by hbm2java
 */
@Entity
@Table(name = "reserve_info")
public class ReserveInfo extends BaseEntity implements java.io.Serializable, Cloneable {

	@Override
	@Transient
	public String getNodeName() {
		return this.resvName;
	}

	private Long domainId;

	@Column(name = "DOMAIN_ID")
	public Long getDomainId() {
		return domainId;
	}

	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}

	private long reserveInfoId;
	private Long maxReserve;
	private Long minReserve;
	private Long usageQuotaThreshold;
	private Boolean canRollback;
	private String resvName;
	private String resvDesc;
	private Long categoryId;
	private Long index;

	public ReserveInfo() {
	}

	public ReserveInfo(Long maxReserve, Long minReserve, Long usageQuotaThreshold, Boolean canRollback, String resvName,
			String resvDesc, Long categoryId, Long index) {
		this.maxReserve = maxReserve;
		this.minReserve = minReserve;
		this.usageQuotaThreshold = usageQuotaThreshold;
		this.canRollback = canRollback;
		this.resvName = resvName;
		this.resvDesc = resvDesc;
		this.categoryId = categoryId;
		this.index = index;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)

	@Column(name = "RESERVE_INFO_ID", unique = true, nullable = false)
	public long getReserveInfoId() {
		return this.reserveInfoId;
	}

	public void setReserveInfoId(long reserveInfoId) {
		this.reserveInfoId = reserveInfoId;
	}

	@Column(name = "MAX_RESERVE")
	public Long getMaxReserve() {
		return this.maxReserve;
	}

	public void setMaxReserve(Long maxReserve) {
		this.maxReserve = maxReserve;
	}

	@Column(name = "MIN_RESERVE")
	public Long getMinReserve() {
		return this.minReserve;
	}

	public void setMinReserve(Long minReserve) {
		this.minReserve = minReserve;
	}

	@Column(name = "USAGE_QUOTA_THRESHOLD")
	public Long getUsageQuotaThreshold() {
		return this.usageQuotaThreshold;
	}

	public void setUsageQuotaThreshold(Long usageQuotaThreshold) {
		this.usageQuotaThreshold = usageQuotaThreshold;
	}

	@Column(name = "CAN_ROLLBACK")
	public Boolean getCanRollback() {
		return this.canRollback;
	}

	public void setCanRollback(Boolean canRollback) {
		this.canRollback = canRollback;
	}

	@Column(name = "RESV_NAME", length = 65535)
	public String getResvName() {
		return this.resvName;
	}

	public void setResvName(String resvName) {
		this.resvName = resvName;
	}

	@Column(name = "RESV_DESC", length = 65535)
	public String getResvDesc() {
		return this.resvDesc;
	}

	public void setResvDesc(String resvDesc) {
		this.resvDesc = resvDesc;
	}

	@Column(name = "CATEGORY_ID")
	public Long getCategoryId() {
		return this.categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	@Column(name = "POS_INDEX")
	public Long getIndex() {
		return index;
	}

	public void setIndex(Long index) {
		this.index = index;
	}

	@Override
	public ReserveInfo clone() throws CloneNotSupportedException {
		return (ReserveInfo) super.clone();
	}

}

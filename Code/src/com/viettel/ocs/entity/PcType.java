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
@Table(name = "pc_type")
public class PcType extends BaseEntity implements Serializable {

	@Override
	@Transient
	public String getNodeName() {
		return this.pcTypeName;
	}

	private Long domainId;

	@Column(name = "DOMAIN_ID")
	public Long getDomainId() {
		return domainId;
	}

	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}

	private long pcTypeId;
	private String pcTypeName;
	private String remark;
	private Long categoryId;
	private Long posIdx;
	private Long filterId;

	@Id
	@GeneratedValue(strategy = IDENTITY)

	@Column(name = "PC_TYPE_ID", unique = true, nullable = false)
	public long getPcTypeId() {
		return pcTypeId;
	}

	public void setPcTypeId(long pcTypeId) {
		this.pcTypeId = pcTypeId;
	}

	@Column(name = "PC_TYPE_NAME")
	public String getPcTypeName() {
		return pcTypeName;
	}

	public void setPcTypeName(String pcTypeName) {
		this.pcTypeName = pcTypeName;
	}

//	@Column(name = "PC_TYPE_DESCRIPTION")
//	public String getPcTypeDescription() {
//		return pcTypeDescription;
//	}
//
//	public void setPcTypeDescription(String pcTypeDescription) {
//		this.pcTypeDescription = pcTypeDescription;
//	}
	@Column(name = "REMARK")
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Column(name = "CATEGORY_ID")
	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	@Column(name = "POS_IDX")
	public Long getPosIdx() {
		return posIdx;
	}

	public void setPosIdx(Long posIdx) {
		this.posIdx = posIdx;
	}

	@Column(name = "FILTER_ID")
	public Long getFilterId() {
		return filterId;
	}

	public void setFilterId(Long filterId) {
		this.filterId = filterId;
	}

}

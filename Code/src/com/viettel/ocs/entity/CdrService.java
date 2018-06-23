package com.viettel.ocs.entity;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "cdr_service")
public class CdrService extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	@Transient
	public String getNodeName() {
		return this.name;
	}

	@Column(name = "DOMAIN_ID")
	private Long domainId;

	public Long getDomainId() {
		return domainId;
	}

	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "CDR_SERVICE_ID")
	private long cdrServiceId;
	@Column(name = "CDR_SERVICE_CODE")
	private String cdrServiceCode;
	@Column(name = "NAME")
	private String name;
	@Column(name = "REMARK")
	private String remark;
	@Column(name = "CATEGORY_ID")
	private Long categoryId;
	@Column(name = "POS_INDEX")
	private Long index;

	public CdrService() {
	}

	public CdrService(Long cdrServiceId) {
		this.cdrServiceId = cdrServiceId;
	}

	public Long getCdrServiceId() {
		return cdrServiceId;
	}

	public void setCdrServiceId(Long cdrServiceId) {
		this.cdrServiceId = cdrServiceId;
	}

	public String getCdrServiceCode() {
		return cdrServiceCode;
	}

	public void setCdrServiceCode(String cdrServiceCode) {
		this.cdrServiceCode = cdrServiceCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public Long getIndex() {
		return index;
	}

	public void setIndex(Long index) {
		this.index = index;
	}

}

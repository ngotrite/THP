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
 * Parameter generated by hbm2java
 */
@Entity
@Table(name = "parameter")
public class Parameter extends BaseEntity implements java.io.Serializable, Cloneable {

	@Override
	@Transient
	public String getNodeName() {
		return this.parameterName;
	}

	private Long domainId;

	@Column(name = "DOMAIN_ID")
	public Long getDomainId() {
		return domainId;
	}

	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}

	private long parameterId;
	private Long ownerLevel;
	private String parameterValue;
	private String parameterName;
	private boolean forTemplate;
	private Long categoryId;
	private String remark;
	private Long index;

	public Parameter() {
	}

	public Parameter(Long ownerLevel, String parameterValue, String parameterName, boolean forTemplate, Long categoryId,
			String remark) {
		this.ownerLevel = ownerLevel;
		this.parameterValue = parameterValue;
		this.parameterName = parameterName;
		this.forTemplate = forTemplate;
		this.categoryId = categoryId;
		this.remark = remark;
	}

	@Id
	@Column(name = "PARAMETER_ID", unique = true, nullable = false)
	public long getParameterId() {
		return this.parameterId;
	}

	public void setParameterId(long parameterId) {
		this.parameterId = parameterId;
	}

	@Column(name = "OWNER_LEVEL")
	public Long getOwnerLevel() {
		return this.ownerLevel;
	}

	public void setOwnerLevel(Long ownerLevel) {
		this.ownerLevel = ownerLevel;
	}

	@Column(name = "PARAMETER_VALUE", length = 65535)
	public String getParameterValue() {
		return this.parameterValue;
	}

	public void setParameterValue(String parameterValue) {
		this.parameterValue = parameterValue;
	}

	@Column(name = "PARAMETER_NAME", length = 200)
	public String getParameterName() {
		return this.parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	@Column(name = "FOR_TEMPLATE")
	public boolean getForTemplate() {
		return this.forTemplate;
	}

	public void setForTemplate(boolean forTemplate) {
		this.forTemplate = forTemplate;
	}

	@Column(name = "CATEGORY_ID")
	public Long getCategoryId() {
		return this.categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	@Column(name = "REMARK", length = 200)
	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Column(name = "POS_INDEX", length = 20)
	public Long getIndex() {
		return index;
	}

	public void setIndex(Long index) {
		this.index = index;
	}

	@Override
	public Parameter clone() throws CloneNotSupportedException {
		return (Parameter) super.clone();
	}

}
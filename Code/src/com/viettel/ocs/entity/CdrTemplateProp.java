package com.viettel.ocs.entity;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author SOPEN
 */
@Entity
@Table(name = "cdr_template_prop")
public class CdrTemplateProp implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;
	@Transient
	private Boolean editRow;
	@Transient
	private String propName;

	@Transient
	public Boolean getEditRow() {
		return editRow;
	}

	public void setEditRow(Boolean editRow) {
		this.editRow = editRow;
	}

	@Transient
	public String getPropName() {
		return propName;
	}

	public void setPropName(String propName) {
		this.propName = propName;
	}

	@Transient
	public String getNodeName() {
		return null;
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
	@Column(name = "CDR_TEMPLATE_PROP_ID")
	private Long cdrTemplatePropId;
	@Column(name = "CDR_TEMPLATE_ID")
	private Long cdrTemplateId;
	@Column(name = "CDR_PROP_ID")
	private Long cdrPropId;
	@Column(name = "ORDER_PROP")
	private Long orderProp;
	@Column(name = "NULLABLE")
	private Boolean nullable;
	@Column(name = "STATUS")
	private Boolean status;

	@Column(name = "FORMAT")
	private String format;
	@Column(name = "DEFAULT_VALUE")
	private String defaultValue;
	@Column(name = "DISPLAYABLE")
	private Boolean displayable = false;

	public CdrTemplateProp() {
	}

	public CdrTemplateProp(Long cdrTemplatePropId) {
		this.cdrTemplatePropId = cdrTemplatePropId;
	}

	public CdrTemplateProp(Long cdrTemplatePropId, Long cdrTemplateId, Long orderProp) {
		this.cdrTemplatePropId = cdrTemplatePropId;
		this.cdrTemplateId = cdrTemplateId;
		this.orderProp = orderProp;
	}

	public Long getCdrTemplatePropId() {
		return cdrTemplatePropId;
	}

	public void setCdrTemplatePropId(Long cdrTemplatePropId) {
		this.cdrTemplatePropId = cdrTemplatePropId;
	}

	public Long getCdrTemplateId() {
		return cdrTemplateId;
	}

	public void setCdrTemplateId(Long cdrTemplateId) {
		this.cdrTemplateId = cdrTemplateId;
	}

	public Long getCdrPropId() {
		return cdrPropId;
	}

	public void setCdrPropId(Long cdrPropId) {
		this.cdrPropId = cdrPropId;
	}

	public Long getOrderProp() {
		return orderProp;
	}

	public void setOrderProp(Long orderProp) {
		this.orderProp = orderProp;
	}

	public Boolean getNullable() {
		return nullable;
	}

	public void setNullable(Boolean nullable) {
		this.nullable = nullable;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}


	public Boolean getDisplayable() {
		return displayable;
	}

	public void setDisplayable(Boolean displayable) {
		this.displayable = displayable;
	}

	@Override
	public CdrTemplateProp clone() throws CloneNotSupportedException {
		return (CdrTemplateProp) super.clone();
	}
}

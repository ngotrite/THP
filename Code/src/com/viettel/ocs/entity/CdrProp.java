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
@Table(name = "cdr_prop")
public class CdrProp extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Transient
	public String getNodeName() {
		return this.propName;
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
	@Column(name = "CDR_PROP_ID")
	private Long cdrPropId;
	@Column(name = "PROP_NAME")
	private String propName;
	@Column(name = "DESCRIPTION")
	private String description;
	@Column(name = "SOURCE")
	private String source;
	@Column(name = "DATA_TYPE")
	private String dataType;
	@Column(name = "PARAM")
	private String param;
	@Column(name = "PROP_ID")
	private Long propId;
	@Column(name = "DEFAULT_VALUE")
	private String defaultValue;
	@Column(name = "POS_INDEX")
	private Long index;

	public CdrProp() {
	}

	public CdrProp(Long cdrPropId) {
		this.cdrPropId = cdrPropId;
	}

	public CdrProp(Long cdrPropId, String propName) {
		this.cdrPropId = cdrPropId;
		this.propName = propName;
	}

	public Long getCdrPropId() {
		return cdrPropId;
	}

	public void setCdrPropId(Long cdrPropId) {
		this.cdrPropId = cdrPropId;
	}

	public String getPropName() {
		return propName;
	}

	public void setPropName(String propName) {
		this.propName = propName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public Long getPropId() {
		return propId;
	}

	public void setPropId(Long propId) {
		this.propId = propId;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public Long getIndex() {
		return index;
	}

	public void setIndex(Long index) {
		this.index = index;
	}

}

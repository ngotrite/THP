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
@Table(name = "cdr_std_io_prop")
public class CdrStdIo extends BaseEntity implements Serializable {

	@Transient
	public String getNodeName() {
		return null;
	}

//	@Column(name = "DOMAIN_ID")
//	private Long domainId;
//
//	public Long getDomainId() {
//		return domainId;
//	}
//
//	public void setDomainId(Long domainId) {
//		this.domainId = domainId;
//	}
	
	private long cdrStdIoPropId;
	private Long cdrStdConfigId;
	private Long propertyId;
	private Long propOrder;
	private Long type;
	
	public CdrStdIo() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CdrStdIo(long cdrStdIoPropId, Long cdrStdConfigId, Long propertyId, Long propOrder,
			Long type) {
		super();
//		this.domainId = domainId;
		this.cdrStdIoPropId = cdrStdIoPropId;
		this.cdrStdConfigId = cdrStdConfigId;
		this.propertyId = propertyId;
		this.propOrder = propOrder;
		this.type = type;
	}
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "CDR_STD_IO_PROP_ID", unique = true, nullable = false)
	public long getCdrStdIoPropId() {
		return cdrStdIoPropId;
	}

	public void setCdrStdIoPropId(long cdrStdIoPropId) {
		this.cdrStdIoPropId = cdrStdIoPropId;
	}

	@Column(name = "CDR_STD_CONFIG_ID")
	public Long getCdrStdConfigId() {
		return cdrStdConfigId;
	}

	public void setCdrStdConfigId(Long cdrStdConfigId) {
		this.cdrStdConfigId = cdrStdConfigId;
	}

	@Column(name = "PROPERTY_ID")
	public Long getPropertyId() {
		return propertyId;
	}

	public void setPropertyId(Long propertyId) {
		this.propertyId = propertyId;
	}

	@Column(name = "PROP_ORDER")
	public Long getPropOrder() {
		return propOrder;
	}

	public void setPropOrder(Long propOrder) {
		this.propOrder = propOrder;
	}

	@Column(name = "TYPE")
	public Long getType() {
		return type;
	}

	public void setType(Long type) {
		this.type = type;
	}

}

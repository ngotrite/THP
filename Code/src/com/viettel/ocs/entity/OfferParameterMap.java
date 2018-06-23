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
 * OfferParameterMap generated by hbm2java
 */
@Entity
@Table(name = "offer_parameter_map")
public class OfferParameterMap extends BaseEntity implements java.io.Serializable {

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

	private long offerParameterMapId;
	private Long offerId;
	private Long parameterId;
	private String value;

	public OfferParameterMap() {
	}

	public OfferParameterMap(Long offerId, Long parameterId) {
		this.offerId = offerId;
		this.parameterId = parameterId;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)

	@Column(name = "OFFER_PARAMETER_MAP_ID", unique = true, nullable = false)
	public long getOfferParameterMapId() {
		return this.offerParameterMapId;
	}

	public void setOfferParameterMapId(long offerParameterMapId) {
		this.offerParameterMapId = offerParameterMapId;
	}

	@Column(name = "OFFER_ID")
	public Long getOfferId() {
		return this.offerId;
	}

	public void setOfferId(Long offerId) {
		this.offerId = offerId;
	}

	@Column(name = "PARAMETER_ID")
	public Long getParameterId() {
		return this.parameterId;
	}

	public void setParameterId(Long parameterId) {
		this.parameterId = parameterId;
	}

	@Column(name = "VALUE")
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
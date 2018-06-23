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
 * RedirectAddress generated by hbm2java
 */
@Entity
@Table(name = "redirect_address")
public class RedirectAddress extends BaseEntity implements java.io.Serializable, Cloneable {

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

	private long redirectAddressId;
	private Long offerId;
	private String redirectAddress;
	private Long redirectType;

	public RedirectAddress() {
	}

	public RedirectAddress(Long offerId, String redirectAddress, Long redirectType) {
		this.offerId = offerId;
		this.redirectAddress = redirectAddress;
		this.redirectType = redirectType;
	}

	public RedirectAddress(Long redirectType) {
		this.redirectType = redirectType;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)

	@Column(name = "REDIRECT_ADDRESS_ID", unique = true, nullable = false)
	public long getRedirectAddressId() {
		return this.redirectAddressId;
	}

	public void setRedirectAddressId(long redirectAddressId) {
		this.redirectAddressId = redirectAddressId;
	}

	@Column(name = "OFFER_ID")
	public Long getOfferId() {
		return this.offerId;
	}

	public void setOfferId(Long offerId) {
		this.offerId = offerId;
	}

	@Column(name = "REDIRECT_ADDRESS", length = 200)
	public String getRedirectAddress() {
		return this.redirectAddress;
	}

	public void setRedirectAddress(String redirectAddress) {
		this.redirectAddress = redirectAddress;
	}

	@Column(name = "REDIRECT_TYPE")
	public Long getRedirectType() {
		return this.redirectType;
	}

	public void setRedirectType(Long redirectType) {
		this.redirectType = redirectType;
	}

	@Override
	public RedirectAddress clone() throws CloneNotSupportedException {
		return (RedirectAddress) super.clone();
	}

}

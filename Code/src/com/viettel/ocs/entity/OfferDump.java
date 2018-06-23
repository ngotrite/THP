package com.viettel.ocs.entity;
// Generated Sep 2, 2016 4:54:08 PM by Hibernate Tools 3.2.1.GA

import javax.persistence.Transient;

public class OfferDump extends BaseEntity implements java.io.Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	@Transient
	public String getNodeName() {
		return this.offerName;
	}

	private String offerName;
	private Long categoryId;
	private String offerExternalId;
	
	public OfferDump() {
	}

	public OfferDump(String offerName, Long categoryId, String offerExternalId) {
		super();
		this.offerName = offerName;
		this.categoryId = categoryId;
		this.offerExternalId = offerExternalId;
	}

	public String getOfferName() {
		return offerName;
	}

	public void setOfferName(String offerName) {
		this.offerName = offerName;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public String getOfferExternalId() {
		return offerExternalId;
	}

	public void setOfferExternalId(String offerExternalId) {
		this.offerExternalId = offerExternalId;
	}

	@Override
	public OfferDump clone() throws CloneNotSupportedException {
		return (OfferDump) super.clone();
	}

}

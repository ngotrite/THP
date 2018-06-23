package com.viettel.ocs.model;

import java.io.Serializable;

import com.viettel.ocs.entity.OfferpkgOfferMap;

@SuppressWarnings("serial")
public class OfferpkgOfferMapModel implements Serializable, Cloneable {

	private OfferpkgOfferMap offerPackageMap;
	private String offerPkgName;
	private String offerName;
	private Long state;
	private String description;

	public OfferpkgOfferMapModel() {
		super();
	}

	public OfferpkgOfferMapModel(OfferpkgOfferMap packMap, String offerPkgName,
			String offerName, Long state, String description) {

		this.offerPackageMap = packMap;
		this.offerPkgName = offerPkgName;
		this.offerName = offerName;
		this.state = state;
		this.description = description;
	}

	public String getOfferPkgName() {
		return offerPkgName;
	}

	public OfferpkgOfferMap getOfferPackageMap() {
		return offerPackageMap;
	}

	public void setOfferPackageMap(OfferpkgOfferMap offerPackageMap) {
		this.offerPackageMap = offerPackageMap;
	}

	public void setOfferPkgName(String offerPkgName) {
		this.offerPkgName = offerPkgName;
	}

	public String getOfferName() {
		return offerName;
	}

	public void setOfferName(String offerName) {
		this.offerName = offerName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getState() {
		return state;
	}

	public void setState(Long state) {
		this.state = state;
	}

	@Override
	public OfferpkgOfferMapModel clone() {
		try {
			return (OfferpkgOfferMapModel) super.clone();
		} catch (CloneNotSupportedException e) {
			
			return this;
		}
	}

}

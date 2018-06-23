package com.viettel.ocs.model;

import com.viettel.ocs.entity.Offer;

public class OfferRecompiledModel {
	private boolean selected;
	private Offer offer;

	public OfferRecompiledModel(Offer offer) {
		super();
		this.selected = false;
		this.offer = offer;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public Offer getOffer() {
		return offer;
	}

	public void setOffer(Offer offer) {
		this.offer = offer;
	}

}

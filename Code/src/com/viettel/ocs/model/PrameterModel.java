package com.viettel.ocs.model;

import com.viettel.ocs.entity.OfferParameterMap;
import com.viettel.ocs.entity.Parameter;

public class PrameterModel {
	private Parameter parameter;
	private OfferParameterMap offerParameterMap;

	public PrameterModel(Parameter parameter, OfferParameterMap offerParameterMap) {
		super();
		this.parameter = parameter;
		this.offerParameterMap = offerParameterMap;
	}

	public Parameter getParameter() {
		return parameter;
	}

	public void setParameter(Parameter parameter) {
		this.parameter = parameter;
	}

	public OfferParameterMap getOfferParameterMap() {
		return offerParameterMap;
	}

	public void setOfferParameterMap(OfferParameterMap offerParameterMap) {
		this.offerParameterMap = offerParameterMap;
	}

}

package com.viettel.ocs.bean.offer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;

import com.viettel.ocs.bean.BaseController;
import com.viettel.ocs.constant.ContantsUtil.OfferType;
import com.viettel.ocs.dao.OfferDAO;
import com.viettel.ocs.entity.Offer;
import com.viettel.ocs.entity.OfferParameterMap;
import com.viettel.ocs.entity.Parameter;
import com.viettel.ocs.model.OfferRecompiledModel;

@SuppressWarnings("serial")
@ManagedBean(name = "reCompiledOffes")
@ViewScoped
public class ReCompiledOffes extends BaseController implements Serializable {

	private Offer offer;
	private List<Parameter> parametersOfOffer;
	private List<OfferRecompiledModel> offerModels;
	private OfferDAO offerDAO;
	private HashMap<Long, List<Parameter>> parametersMap;

	@PostConstruct
	private void init() {
		this.offerDAO = new OfferDAO();
		this.parametersMap = new HashMap<Long, List<Parameter>>();
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String offerId = params.get("offerId");
		this.offer = offerDAO.get(Long.valueOf(offerId));
		this.offerModels = offerDAO.findAllCompiledOfferOfTemp(offer, OfferType.COMPILED);
		parametersOfOffer = this.hashMapParameter(offerDAO.findListParameterByOffer(this.offer));
		for (OfferRecompiledModel recompiledModel : offerModels) {
			parametersMap.put(recompiledModel.getOffer().getOfferId(), this.mixWithParamCompiled(parametersOfOffer,
					offerDAO.findOfferParameterMapsByOfferCompiled(recompiledModel.getOffer())));
		}
	}

	private List<Parameter> mixWithParamCompiled(List<Parameter> parameters,
			List<OfferParameterMap> offerParameterMaps) {
		List<Parameter> parametersPerOffer = new ArrayList<Parameter>();
		try {
			for (Parameter item : parameters) {
				Parameter parameter = item.clone();
				for (OfferParameterMap offerParameterMap : offerParameterMaps) {
					if (parameter.getParameterId() == offerParameterMap.getParameterId()) {
						parameter.setParameterValue(String.valueOf(offerParameterMap.getValue()));
					}
				}
				parametersPerOffer.add(parameter);
			}
		} catch (CloneNotSupportedException e) {
			getLogger().warn(e.getMessage(), e);
		}
		return parametersPerOffer;
	}

	private List<Parameter> hashMapParameter(List<Parameter> parameters) {
		for (int i = parameters.size() - 1; i > 0; i--) {
			for (int j = i - 1; j >= 0; j--) {
				if (parameters.get(i).getParameterId() == parameters.get(j).getParameterId()) {
					parameters.remove(j);
					i--;
				}
			}
		}
		return parameters;
	}

	public String findParameterInList(List<Parameter> parameters, Long parameterId) {
		for (Parameter parameter : parameters) {
			if (parameter.getParameterId() == parameterId) {
				return parameter.getParameterValue();
			}
		}
		return "";
	}

	public void reCopiled() throws Exception {
		if (offerDAO.reCompiledOffers(offerModels, offer, parametersMap, "_gen")) {
			RequestContext.getCurrentInstance().closeDialog(offerModels);
		} else {
			super.showMessageWARN("common.clone", " offer template ");
		}
	}

	public void oncancel() {
		RequestContext.getCurrentInstance().closeDialog(0);
	}

	// GET ,< SET

	public Offer getOffer() {
		return offer;
	}

	public void setOffer(Offer offer) {
		this.offer = offer;
	}

	public List<OfferRecompiledModel> getOfferModels() {
		return offerModels;
	}

	public void setOfferModels(List<OfferRecompiledModel> offerModels) {
		this.offerModels = offerModels;
	}

	public List<Parameter> getParametersOfOffer() {
		return parametersOfOffer;
	}

	public void setParametersOfOffer(List<Parameter> parametersOfOffer) {
		this.parametersOfOffer = parametersOfOffer;
	}

	public HashMap<Long, List<Parameter>> getParametersMap() {
		return parametersMap;
	}

	public void setParametersMap(HashMap<Long, List<Parameter>> parametersMap) {
		this.parametersMap = parametersMap;
	}

}

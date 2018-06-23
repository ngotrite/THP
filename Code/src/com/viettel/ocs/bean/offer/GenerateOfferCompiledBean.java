package com.viettel.ocs.bean.offer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import org.primefaces.context.RequestContext;

import com.viettel.ocs.bean.BaseController;
import com.viettel.ocs.constant.CategoryType;
import com.viettel.ocs.constant.ContantsUtil;
import com.viettel.ocs.constant.ContantsUtil.OfferType;
import com.viettel.ocs.dao.CategoryDAO;
import com.viettel.ocs.dao.OfferDAO;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.Offer;
import com.viettel.ocs.entity.OfferParameterMap;
import com.viettel.ocs.entity.Parameter;
import com.viettel.ocs.model.CloneIgnoreListModel;

@ManagedBean(name = "generateOfferCompiledBean")
@ViewScoped
public class GenerateOfferCompiledBean extends BaseController {

	private CategoryDAO categoryDAO;
	private OfferDAO offerDAO;
	private List<Offer> offers;
	private List<Offer> offerVersions;
	private Offer offer;
	private Offer compiledOffer;
	private List<Parameter> parameters;
	private List<OfferParameterMap> offerParameterMaps;

	private Long categoryId = 0L;
	private String temp = "";
	private String type = "";

	@PostConstruct
	public void init() {

		this.categoryDAO = new CategoryDAO();
		this.offerDAO = new OfferDAO();
		this.offers = new ArrayList<Offer>();
		this.offerVersions = new ArrayList<Offer>();
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String offerId = params.get("offerId");
		this.type = params.get("type");
		if (type.equalsIgnoreCase("generate")) {
			this.offer = offerDAO.get(Long.valueOf(offerId));
		} else if (type.equalsIgnoreCase("reconpiled")) {
			this.compiledOffer = offerDAO.get(Long.valueOf(offerId));
			this.offer = offerDAO.get(compiledOffer.getOfferTemplateId());

			this.offerParameterMaps = offerDAO.findOfferParameterMapsByOfferCompiled(compiledOffer);
		}
		offers.add(offer);
		this.temp = offer.getOfferName() + " v" + offer.getVersionInfo();
		if (offerParameterMaps != null) {
			this.parameters = this.mixWithParamCompiled(this.hashMapParameter(offerDAO.findListParameterByOffer(offer)),
					this.offerParameterMaps);
		} else {
			this.parameters = this.hashMapParameter(offerDAO.findListParameterByOffer(offer));
		}

		loadCategories();
	}

	private List<Parameter> mixWithParamCompiled(List<Parameter> parameters,
			List<OfferParameterMap> offerParameterMaps) {
		for (OfferParameterMap offerParameterMap : offerParameterMaps) {
			for (Parameter parameter : parameters) {
				if (parameter.getParameterId() == offerParameterMap.getParameterId()) {
					parameter.setParameterValue(String.valueOf(offerParameterMap.getValue()));
				}
			}
		}
		return parameters;
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

	public void onChangeCategory(ValueChangeEvent event) {
		if (event.getNewValue() != null && !event.getNewValue().toString().isEmpty()) {
			this.categoryId = Long.valueOf(event.getNewValue().toString());
		}
	}

	public void doGenerate() {
		if (offerIdChoosed != 0L) {
			Offer offer = offerDAO.get(offerIdChoosed);
			this.reCopiled(offer);
		} else {
			if (this.offerVersions.size() == 0) {
				this.offer.setCategoryId(this.categoryId);
			} else {
				this.offer.setCategoryId(offerVersions.get(0).getCategoryId());
			}
			this.offer.setOfferType(ContantsUtil.OfferType.COMPILED);
			this.offer.setOfferTemplateId(this.offer.getOfferId());
			this.offer.setVersionInfo(offerDAO.generateOfferVersion(this.offer));
			Offer offerCloned = null;
			if ((offerCloned = offerDAO.generateOffer(offer, 0L, parameters, "_gen")) != null) {
				RequestContext.getCurrentInstance().closeDialog(offerCloned);
			} else {
				showNotification(FacesMessage.SEVERITY_ERROR, "common.fail", "offer.msg.gen_failed");
			}
		}
	}

	public void reCopiled() {
		this.reCopiled(compiledOffer);
	}

	private void reCopiled(Offer compiledOffer) {
		this.offer.setOfferName(compiledOffer.getOfferName());
		this.offer.setVersionInfo(compiledOffer.getVersionInfo());
		this.offer.setOfferExternalId(compiledOffer.getOfferExternalId());
		this.offer.setCategoryId(compiledOffer.getCategoryId());
		this.offer.setEffDate(compiledOffer.getEffDate());
		this.offer.setPriority(compiledOffer.getPriority());
		this.offer.setState(compiledOffer.getState());
		this.offer.setOfferType(compiledOffer.getOfferType());
		this.offer.setOfferTemplateId(this.offer.getOfferId());

		Offer offerCloned = offerDAO.reCompiledOffer(compiledOffer, offer, parameters, "_gen");
		if (offerCloned != null) {
			RequestContext.getCurrentInstance().closeDialog(offerCloned);
		} else {
			super.showMessageWARN("common.clone", " offer template ");
		}
	}

	private Long offerIdChoosed = 0L;
	private List<Category> categories;

	public Long getOfferIdChoosed() {
		return offerIdChoosed;
	}

	public void setOfferIdChoosed(Long offerIdChoosed) {
		this.offerIdChoosed = offerIdChoosed;
	}

	public void validate() {
		Object object = offerDAO.validateOfferExist(this.offer.getOfferExternalId(), this.offer.getOfferName(), "");
		Object[] values = (Object[]) object;
		BigDecimal duplicateAll = (BigDecimal) values[0];
		// BigDecimal duplicateOnlyExternalId = (BigDecimal) values[1];
		// BigDecimal duplicateOnlyName = (BigDecimal) values[2];
		BigDecimal duplicate = (BigDecimal) values[3];

		if (duplicateAll.compareTo(new BigDecimal(0)) > 0) {
			this.offerVersions = offerDAO.findMoreVersionByOffer(this.offer, OfferType.COMPILED);
			if (offerVersions.size() == 0) {
				RequestContext.getCurrentInstance().execute("PF('confirm_1').show();");
				return;
			} else if (offerVersions.get(0).getOfferTemplateId().equals(this.offer.getOfferId())) {
				RequestContext.getCurrentInstance().execute("PF('confirm_2').show();");
				return;
			} else {
				RequestContext.getCurrentInstance().execute("PF('confirm_1').show();");
				return;
			}
		} else if (duplicate.compareTo(new BigDecimal(0)) > 0) {
			RequestContext.getCurrentInstance().execute("PF('confirm_1').show();");
			return;
		} else {
			offerIdChoosed = 0L;
			doGenerate();
		}
	}

	public void confirm2Close() {
		offerIdChoosed = 0L;
		offerVersions.clear();
	}

	public void oncancel() {
		RequestContext.getCurrentInstance().closeDialog(0);
	}

	// GET, SET
	public void loadCategories() {
		List<Long> catIds = new ArrayList<Long>();
		catIds.add(CategoryType.OFF_OFFER_SUBSCRIPTION_COMPILED);
		catIds.add(CategoryType.OFF_OFFER_ONETIME_COMPILED);
		categories = categoryDAO.findByTypeForSelectbox(catIds);
	}

	public String getTemp() {
		return temp;
	}

	public void setTemp(String temp) {
		this.temp = temp;
	}

	public List<Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}

	public Offer getOffer() {
		return offer;
	}

	public void setOffer(Offer offer) {
		this.offer = offer;
	}

	public List<Offer> getOffers() {
		return offers;
	}

	public void setOffers(List<Offer> offers) {
		this.offers = offers;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Offer getCompiledOffer() {
		return compiledOffer;
	}

	public void setCompiledOffer(Offer compiledOffer) {
		this.compiledOffer = compiledOffer;
	}

	public List<Offer> getOfferVersions() {
		return offerVersions;
	}

	public void setOfferVersions(List<Offer> offerVersions) {
		this.offerVersions = offerVersions;
	}

	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}

}

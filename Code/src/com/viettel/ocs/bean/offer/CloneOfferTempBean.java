package com.viettel.ocs.bean.offer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.primefaces.context.RequestContext;

import com.viettel.ocs.bean.BaseController;
import com.viettel.ocs.bean.TreeOfferBean;
import com.viettel.ocs.constant.CategoryType;
import com.viettel.ocs.constant.ContantsUtil.OfferState;
import com.viettel.ocs.constant.ContantsUtil.OfferType;
import com.viettel.ocs.dao.ActionDAO;
import com.viettel.ocs.dao.CategoryDAO;
import com.viettel.ocs.dao.OfferDAO;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.Action;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.Offer;
import com.viettel.ocs.entity.Parameter;
import com.viettel.ocs.model.LogicCloneModel;

@ManagedBean(name = "cloneOfferTempBean")
@ViewScoped
public class CloneOfferTempBean extends BaseController {
	private int step = 0;

	private CategoryDAO categoryDAO;
	private OfferDAO offerDAO;
	private List<Offer> offers;
	private Offer offer;
	private List<Parameter> parameters;
	// private List<ParaM>

	// private Long categoryId = 0L;

	private LogicCloneModel treeCloneModel;

	private String temp = "";

	private String type;

	private List<Category> categories;

	@PostConstruct
	public void init() {
		this.treeCloneModel = new LogicCloneModel();
		this.parameters = new ArrayList<Parameter>();
		this.categoryDAO = new CategoryDAO();
		this.offerDAO = new OfferDAO();
		this.offers = new ArrayList<Offer>();
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String offerId = params.get("offerId");
		type = params.get("type");

		this.offer = offerDAO.get(Long.valueOf(offerId));
		offers.add(offer);
		this.temp = offer.getOfferName();

		this.parameters = this.hashMapParameter(offerDAO.findListParameterByOffer(offer));
		loadCategories();
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

	public void onChangeChecked(Boolean newValue, String node) {
		switch (node) {
		case "offer":
			if (newValue) {

			} else {
				treeCloneModel.setAction(false);
				treeCloneModel.setPc(false);
				treeCloneModel.setBlock(false);
				treeCloneModel.setRateTable(false);
				treeCloneModel.setDt(false);
				treeCloneModel.setNormalizer(false);
			}
			break;
		case "action":
			if (newValue) {
				treeCloneModel.setOffer(true);
			} else {
				treeCloneModel.setPc(false);
				treeCloneModel.setBlock(false);
				treeCloneModel.setRateTable(false);
				treeCloneModel.setDt(false);
				treeCloneModel.setNormalizer(false);
			}
			break;
		case "pc":
			if (newValue) {
				treeCloneModel.setOffer(true);
				treeCloneModel.setAction(true);
			} else {
				treeCloneModel.setBlock(false);
				treeCloneModel.setRateTable(false);
				treeCloneModel.setDt(false);
				treeCloneModel.setNormalizer(false);
			}
			break;
		case "block":
			if (newValue) {
				treeCloneModel.setOffer(true);
				treeCloneModel.setAction(true);
				treeCloneModel.setPc(true);
			} else {
				treeCloneModel.setRateTable(false);
				treeCloneModel.setDt(false);
				treeCloneModel.setNormalizer(false);
			}
			break;
		case "rateTable":
			if (newValue) {
				treeCloneModel.setOffer(true);
				treeCloneModel.setAction(true);
				treeCloneModel.setPc(true);
				treeCloneModel.setBlock(true);
			} else {
				treeCloneModel.setDt(false);
				treeCloneModel.setNormalizer(false);
			}
			break;
		case "dt":
			if (newValue) {
				treeCloneModel.setOffer(true);
				treeCloneModel.setAction(true);
				treeCloneModel.setPc(true);
				treeCloneModel.setBlock(true);
				treeCloneModel.setRateTable(true);
				treeCloneModel.setDt(true);
			} else {
				treeCloneModel.setNormalizer(false);
			}
			break;
		case "normalizer":
			if (newValue) {
				treeCloneModel.setOffer(true);
				treeCloneModel.setAction(true);
				treeCloneModel.setPc(true);
				treeCloneModel.setBlock(true);
				treeCloneModel.setRateTable(true);
				treeCloneModel.setDt(true);
				treeCloneModel.setNormalizer(true);
			}
			break;

		default:
			break;
		}
	}

	public boolean validateConvertToTemp() {
		Object object = offerDAO.validateOfferExist(this.offer.getOfferExternalId(), this.offer.getOfferName(), "");
		Object[] values = (Object[]) object;
		// BigDecimal duplicateAll = (BigDecimal) values[0];
		// BigDecimal duplicateOnlyExternalId = (BigDecimal) values[1];
		// BigDecimal duplicateOnlyName = (BigDecimal) values[2];
		BigDecimal duplicate = (BigDecimal) values[3];

		if (duplicate.compareTo(new BigDecimal(0)) > 0) {
			RequestContext.getCurrentInstance().execute("PF('confirm_1').show();");
			return false;
		} else {
			return true;
		}
	}

	public boolean validateClone() {
		Object object = offerDAO.validateOfferExist(this.offer.getOfferExternalId(), this.offer.getOfferName(), "");
		Object[] values = (Object[]) object;
		BigDecimal duplicateAll = (BigDecimal) values[0];
		// BigDecimal duplicateOnlyExternalId = (BigDecimal) values[1];
		// BigDecimal duplicateOnlyName = (BigDecimal) values[2];
		BigDecimal duplicate = (BigDecimal) values[3];

		if (duplicateAll.compareTo(new BigDecimal(0)) > 0) {
			List<Offer> offers = offerDAO.findMoreVersionByOffer(offer);
			if (offers.get(0).getOfferType() == OfferType.COMPILED) {
				RequestContext.getCurrentInstance().execute("PF('confirm_2').show();");
				return false;
			} else if (!offers.get(0).getCategoryId().equals(this.offer.getCategoryId())) {
				RequestContext.getCurrentInstance().execute("PF('confirm_3').show();");
				return false;
			}
		} else if (duplicate.compareTo(new BigDecimal(0)) > 0) {
			RequestContext.getCurrentInstance().execute("PF('confirm_1').show();");
			return false;
		}
		return true;
	}

	public void doClone() {
		if (type.contentEquals("convertToTemp") && !validateConvertToTemp()) {
			return;
		} else if (!validateClone()) {
			return;
		}

		if (type.contentEquals("convertToTemp")) {
			offer.setOfferType(OfferType.TEMPLATE);
		}
		offer.setState(OfferState.IN_ACTIVE);
		offer.setVersionInfo(offerDAO.generateOfferVersion(offer));
		Offer offerCloned = offerDAO.cloneOffer(offer, treeCloneModel, "_cloned");

		if (offerCloned != null) {
			RequestContext.getCurrentInstance().closeDialog(offerCloned);
		}
	}

	public void nextStepAndPrevious() {
		if (step == 0) {
			step++;
		} else {
			step--;
		}
	}

	public void oncancel() {
		RequestContext.getCurrentInstance().closeDialog(0);
	}

	// GET, SET
	public void loadCategories() {
		Category category = categoryDAO.get(offer.getCategoryId());
		if ("convertToTemp".equals(type)) {
			categories = categoryDAO.findByTypeForSelectbox(CategoryType.OFF_OFFER_TEMPLATE);
		} else {
			categories = categoryDAO.findByTypeForSelectbox(category.getCategoryType());
		}
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public Offer getOffer() {
		return offer;
	}

	public void setOffer(Offer offer) {
		this.offer = offer;
	}

	public LogicCloneModel getTreeCloneModel() {
		return treeCloneModel;
	}

	public void setTreeCloneModel(LogicCloneModel treeCloneModel) {
		this.treeCloneModel = treeCloneModel;
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

	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}

}

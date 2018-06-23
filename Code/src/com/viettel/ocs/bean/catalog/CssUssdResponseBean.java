package com.viettel.ocs.bean.catalog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;

import com.viettel.ocs.bean.BaseController;
import com.viettel.ocs.bean.TreeCommonBean;
import com.viettel.ocs.constant.CategoryType;
import com.viettel.ocs.constant.TreeType;
import com.viettel.ocs.dao.CategoryDAO;
import com.viettel.ocs.dao.CssUssdResponseDAO;
import com.viettel.ocs.dao.LangDAO;
import com.viettel.ocs.dao.OfferDAO;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.CssUssdResponse;
import com.viettel.ocs.entity.Lang;
import com.viettel.ocs.entity.Offer;

@SuppressWarnings("serial")
@ManagedBean(name = "cssUssdResponseBean")
@ViewScoped
public class CssUssdResponseBean extends BaseController implements Serializable {
	private Category category;
	private String formType = "";
	private List<CssUssdResponse> cssUssdResponses;

	private CssUssdResponse cssUssdResponse;
	private CssUssdResponseDAO cssUssdResponseDAO;
	private CategoryDAO categoryDAO;
	private boolean isEditting;

	private List<Lang> lstLang;
	private List<Offer> offers;
	private OfferDAO offerDAO;

	@PostConstruct
	public void init() {
		this.category = new Category();
		this.categoryDAO = new CategoryDAO();
		this.cssUssdResponse = new CssUssdResponse();
		this.cssUssdResponseDAO = new CssUssdResponseDAO();
		this.cssUssdResponses = new ArrayList<CssUssdResponse>();
		this.isEditting = true;
		this.lstLang = new ArrayList<Lang>();
		this.offers = new ArrayList<Offer>();
		this.offerDAO = new OfferDAO();
	}

	// Context Menu
	public void moveUpDownInCat(NodeSelectEvent event, boolean isUp) {
		CssUssdResponse cssUssdResponse = (CssUssdResponse) event.getTreeNode().getData();
		Object object = cssUssdResponseDAO.upDownObjectInCatWithDomain(cssUssdResponse, "posIndex", isUp);
		if (object instanceof CssUssdResponse) {
			Category category = categoryDAO.get(cssUssdResponse.getCategoryId());
			CssUssdResponse nextCssUssdResponse = (CssUssdResponse) object;

			TreeCommonBean treeCommonBean = super.getTreeCommonBean();
			if (isUp) {
				treeCommonBean.moveUpTreeNode(cssUssdResponse);
			} else {
				treeCommonBean.moveDownTreeNode(cssUssdResponse);
			}
			treeCommonBean.updateTreeNode(nextCssUssdResponse, category, null);
			if (formType == "list-css" && nextCssUssdResponse.getCategoryId() == this.category.getCategoryId()) {
				refreshCategories(category);
			}

			super.showNotificationSuccsess();
		} else {
//			super.showNotificationFail();
		}
	}

	// Edit ContextMenu
	public void editContextMenu(NodeSelectEvent nodeSelectEvent) {
		super.getTreeCommonBean().onNodeSelect(nodeSelectEvent);
		if (nodeSelectEvent != null) {
			CssUssdResponse item = (CssUssdResponse) nodeSelectEvent.getTreeNode().getData();
			editCSS(item);
		}
	}

	// Remove ContextMenu
	public void removeContextMenu(NodeSelectEvent nodeSelectEvent) {
		super.getTreeCommonBean().onNodeSelect(nodeSelectEvent);
		if (nodeSelectEvent != null) {
			CssUssdResponse item = (CssUssdResponse) nodeSelectEvent.getTreeNode().getData();
			deleteCSS(item);
			loadCSSByCategory(item.getCategoryId());
		}
	}

	// Validate
	private boolean validateCSS() {
		boolean result = true;
		if (cssUssdResponseDAO.checkName(cssUssdResponse, cssUssdResponse.getName())) {
			this.showMessageWARN("cssUssdResponse.title", super.readProperties("validate.checkValueNameExist"));
			result = false;
		}
		return result;
	}

	public void saveCSS() {
		TreeCommonBean treeCommonBean = super.getTreeCommonBean();
		Category cat = categoryDAO.get(cssUssdResponse.getCategoryId());
		if (validateCSS()) {
			if (cssUssdResponseDAO.saveCssUssdResponse(cssUssdResponse)) {
				this.showMessageINFO("common.save", "USSD Response Template");
				treeCommonBean.updateTreeNode(cssUssdResponse, cat, null);
				this.isEditting = true;
			}
		}
	}

	public void chooseOffer() {
		super.openTreeOfferDialog(TreeType.OFFER_TREE_NORMAL_ALL_DLG, readProperties("cssUssdResponse.offer"), 0, false,
				null);
		// super.openTreeOfferDialog(TreeType.OFFER_TREE_NORMAL_DLG,
		// CategoryType.OFF_OFFER_NAME, 0, false, null);
	}

	public void notChooseOffer() {
		cssUssdResponse.setOfferExternalId(null);
		offers.clear();
		RequestContext.getCurrentInstance().update("form-css-detail:slOffer");
	}

	public void onDialogOfferReturn(SelectEvent event) {
		Object object = event.getObject();
		if (object instanceof Offer) {
			Offer offer = (Offer) object;
			offers.clear();
			this.offers.add(offer);
			cssUssdResponse.setOfferExternalId(offer.getOfferExternalId());
			RequestContext.getCurrentInstance().update("form-css-detail:slOffer");
		}
	}

	private void loadOffers() {
		offers.clear();
		if (cssUssdResponse.getOfferExternalId() != null) {
			// Offer offer = offerDAO.get(cssUssdResponse.getOfferExternalId());
			// if (offer != null) {
			// offers.add(offer);
			// }
			offers = offerDAO.findAll("");
		}
	}

	private void loadComboListLang() {
		LangDAO langDAO = new LangDAO();
		lstLang = langDAO.findAll("");
	}

	public void deleteCSS(CssUssdResponse item) {
		try {
			cssUssdResponseDAO.delete(item);
			cssUssdResponses.remove(item);
			refreshCategories(category);
			TreeCommonBean treeCommonBean = super.getTreeCommonBean();
			treeCommonBean.removeTreeNodeAll(item);
			this.showMessageINFO("validate.deleteSuccess", super.readProperties(""));
		} catch (Exception e) {
			throw e;
		}
	}

	public void editCSS(CssUssdResponse cssUssdResponse) {
		TreeCommonBean treeCommonBean = super.getTreeCommonBean();
		treeCommonBean.setContentTitle(super.readProperties("cssUssdResponse.title"));
		treeCommonBean.hideAllCategoryComponent();
		refreshCSS(cssUssdResponse);
		this.isEditting = false;
	}

	public void addNewCSS() {
		super.getTreeCommonBean().hideAllCategoryComponent();
		super.getTreeCommonBean().setContentTitle(super.readProperties("cssUssdResponse.title"));
		cssUssdResponse = new CssUssdResponse();
		refreshCSS(cssUssdResponse);
		cssUssdResponse.setCategoryId(category.getCategoryId());
		this.isEditting = false;
	}

	public void refreshCategories(Category category) {
		formType = "list-css";
		this.category = category;
		loadCSSByCategory(category.getCategoryId());
	}

	public void refreshCSS(CssUssdResponse cssUssdResponse) {
		try {
			this.cssUssdResponse = cssUssdResponse.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			getLogger().warn(e.getMessage(), e);
		}
		setFormType("css-detail");
		LoadCategoriesOfCSS();
		loadComboListLang();
		loadOffers();
		this.isEditting = true;
	}

	private List<Category> categoriesOfCSS;

	private void LoadCategoriesOfCSS() {
		categoriesOfCSS = categoryDAO.findByTypeForSelectbox(CategoryType.CTL_CSS_USSD_RESPONSE);
	}

	private List<CssUssdResponse> loadCSSByCategory(long categoryId) {
		cssUssdResponses = new ArrayList<CssUssdResponse>();
		cssUssdResponses = cssUssdResponseDAO.findCSSByCategoryId(categoryId);
		return cssUssdResponses;
	}

	// *** GET-SET ***//
	public List<Category> getCategoriesOfCSS() {
		return categoriesOfCSS;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public String getFormType() {
		return formType;
	}

	public void setFormType(String formType) {
		this.formType = formType;
	}

	public List<CssUssdResponse> getCssUssdResponses() {
		return cssUssdResponses;
	}

	public void setCssUssdResponses(List<CssUssdResponse> cssUssdResponses) {
		this.cssUssdResponses = cssUssdResponses;
	}

	public CssUssdResponse getCssUssdResponse() {
		return cssUssdResponse;
	}

	public void setCssUssdResponse(CssUssdResponse cssUssdResponse) {
		this.cssUssdResponse = cssUssdResponse;
	}

	public boolean isEditting() {
		return isEditting;
	}

	public void setEditting(boolean isEditting) {
		this.isEditting = isEditting;
	}

	public List<Lang> getLstLang() {
		return lstLang;
	}

	public void setLstLang(List<Lang> lstLang) {
		this.lstLang = lstLang;
	}

	public List<Offer> getOffers() {
		return offers;
	}

	public void setOffers(List<Offer> offers) {
		this.offers = offers;
	}

}

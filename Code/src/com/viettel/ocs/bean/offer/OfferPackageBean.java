package com.viettel.ocs.bean.offer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;

import com.viettel.ocs.bean.BaseController;
import com.viettel.ocs.constant.CategoryType;
import com.viettel.ocs.constant.ContantsUtil;
import com.viettel.ocs.constant.TreeType;
import com.viettel.ocs.dao.CategoryDAO;
import com.viettel.ocs.dao.OfferDAO;
import com.viettel.ocs.dao.OfferPackageDAO;
import com.viettel.ocs.entity.Action;
import com.viettel.ocs.entity.BaseEntity;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.Offer;
import com.viettel.ocs.entity.OfferPackage;
import com.viettel.ocs.entity.OfferpkgOfferMap;
import com.viettel.ocs.entity.RowDt;
import com.viettel.ocs.model.OfferpkgOfferMapModel;

/**
 * OfferPackageBean
 * 
 * @author THANHND
 *
 */
@SuppressWarnings("serial")
@ManagedBean(name = "offerPackageBean")
@ViewScoped
public class OfferPackageBean extends BaseController {
	private String formType;

	private Category category;

	private OfferPackage offerPackage;

	private Offer mainOffer;

	private List<OfferPackage> offerPackages;

	private OfferPackageDAO offerPackageDAO;
	private CategoryDAO categoryDAO;
	private OfferDAO offerDAO;

	private boolean ediable;

	private List<OfferpkgOfferMapModel> normOffersOfPackage;

	@PostConstruct
	public void init() {
		offerPackages = new ArrayList<OfferPackage>();
		offerPackageDAO = new OfferPackageDAO();
		category = new Category();
		offerPackage = new OfferPackage();
		categoryDAO = new CategoryDAO();
		mainOffer = new Offer();
		offerDAO = new OfferDAO();
		normOffersOfPackage = new ArrayList<OfferpkgOfferMapModel>();
	}

	public void refreshCategories(Category category) {
		this.formType = "form_offer_package_list";
		this.category = category;
		offerPackages = offerPackageDAO.findByCategory(category);
		loadCategoriesOfOfferPackage();
	}

	public void refreshOfferPackage(OfferPackage offerPackage) {
		this.formType = "form_offer_package_detail";
		try {
			this.offerPackage = offerPackage.clone();
		} catch (CloneNotSupportedException e) {
			this.offerPackage = offerPackage;
			getLogger().warn(e.getMessage(), e);
		}
		if (offerPackage.getOfferPkgId() != 0L) {
			this.normOffersOfPackage = offerDAO.findByOfferPackage(offerPackage.getOfferPkgId());
			// distinct(normOffersOfPackage);
			if (offerPackage.getMainOfferId() != null) {
				this.mainOffer = offerDAO.get(offerPackage.getMainOfferId());
			} else {
				this.mainOffer = new Offer();
			}
		} else {
			this.normOffersOfPackage.clear();
			this.mainOffer = new Offer();
		}
		mainOffer.setVersionName(true);
		this.ediable = false;
		loadCategoriesOfOfferPackage();
	}

	public String getSateName(Long id) {
		return ContantsUtil.OfferState.getOfferStates().get(id);
	}

	public void addNewOfferPackage() {
		super.getTreeOfferBean().hideCategory();
		super.getTreeOfferBean().setContentTitle(super.readProperties("title.offer_package"));
		OfferPackage offerPackage = new OfferPackage();
		offerPackage.setCategoryId(category.getCategoryId());
		this.refreshOfferPackage(offerPackage);
		this.ediable = true;
	}

	public void editOffePackage(OfferPackage offerPackage) {
		super.getTreeOfferBean().hideCategory();
		super.getTreeOfferBean().setContentTitle(super.readProperties("title.offer_package"));
		this.refreshOfferPackage(offerPackage);
		this.ediable = true;
	}

	public void removeOffePackage(OfferPackage offerPackage) {
		try {
			if (!isDependency(offerPackage)) {
				return;
			}

				offerPackageDAO.delete(offerPackage);
				super.getTreeOfferBean().removeTreeNodeAll(offerPackage);

				if (offerPackage.getCategoryId() == this.category.getCategoryId()) {
					for (OfferPackage offerPgk : offerPackages) {
						if (offerPgk.getOfferPkgId() == this.offerPackage.getOfferPkgId()) {
							offerPackages.remove(offerPgk);
						}
					}
				}
				super.showNotification(FacesMessage.SEVERITY_INFO, "common.success", "");
			
		} catch (Exception e) {
			getLogger().warn(e.getMessage(), e);
			super.showNotification(FacesMessage.SEVERITY_WARN, "common.fail", "common.failEx");
		}
	}

	private boolean isDependency(OfferPackage offerPackage) {
		if (offerPackageDAO.checkDependencies(offerPackage)) {
			super.showNotification(FacesMessage.SEVERITY_WARN, "common.fail", "offerpkg.isusing");
			return false;
		}
		return true;
	}

	private boolean isValidated() {
		if (offerPackageDAO.checkExisted(offerPackage)) {
			super.showNotification(FacesMessage.SEVERITY_WARN, "common.fail", "offerpgk.existed");
			return false;
		}
		return true;
	}

	public void chooseMainOffer() {
		super.openTreeOfferDialog(TreeType.OFFER_TREE_SUBCRIPTION_MAIN, CategoryType.OFF_OFFER_SUBSCRIPTION_NAME_MAIN,
				0, false, null);
	}

	public void onDialogMainOfferReturn(SelectEvent event) {
		Object object = event.getObject();
		if (object instanceof Offer) {
			Offer offer = (Offer) object;
			this.mainOffer = offer;
			this.mainOffer.setVersionName(true);
			this.offerPackage.setMainOfferId(offer.getOfferId());
		}
	}

	public void deleteMainOffer() {
		this.offerPackage.setMainOfferId(null);
		this.mainOffer = new Offer();
		mainOffer.setVersionName(true);
	}

	public void removeNormOfferOfPackage(OfferpkgOfferMapModel offerpkgOfferMapModel) {
		normOffersOfPackage.remove(offerpkgOfferMapModel);
	}

	public void upRow(OfferpkgOfferMapModel offerpkgOfferMapModel) {
		int index = normOffersOfPackage.indexOf(offerpkgOfferMapModel);
		if (index > 0) {
			OfferpkgOfferMapModel preRow = normOffersOfPackage.get(index - 1).clone();
			normOffersOfPackage.set(index - 1, offerpkgOfferMapModel.clone());
			normOffersOfPackage.set(index, preRow.clone());
		}
	}

	public void downRow(OfferpkgOfferMapModel offerpkgOfferMapModel) {
		int index = normOffersOfPackage.indexOf(offerpkgOfferMapModel);
		if (index < normOffersOfPackage.size() - 1 && index > -1) {
			OfferpkgOfferMapModel preRow = normOffersOfPackage.get(index + 1).clone();
			normOffersOfPackage.set(index + 1, offerpkgOfferMapModel.clone());
			normOffersOfPackage.set(index, preRow.clone());
		}
	}

	private OfferpkgOfferMapModel offerpkgOfferMapSelected;

	private List<Category> categoriesOfOfferPackage;

	public void changeNormOffer(OfferpkgOfferMapModel offerpkgOfferMapModel) {
		offerpkgOfferMapSelected = offerpkgOfferMapModel;
		super.openTreeOfferDialog(TreeType.OFFER_TREE_NORMAL_DLG, CategoryType.OFF_OFFER_SUBSCRIPTION_NAME_MAIN, 0,
				false, null);
	}

	public void chooseNormOffer() {
		
		List<Long> lstId = new ArrayList<Long>();
		if (normOffersOfPackage != null) {
			for (OfferpkgOfferMapModel map : normOffersOfPackage) {
				if(map.getOfferPackageMap() != null)
					lstId.add(map.getOfferPackageMap().getOfferId());
			}
		}
		
		offerpkgOfferMapSelected = new OfferpkgOfferMapModel();
		super.openTreeOfferDialog(TreeType.OFFER_TREE_NORMAL_DLG, CategoryType.OFF_OFFER_NAME, 0,
				true, lstId);
	}

	public void onDialogNormOfferReturn(SelectEvent event) {

		Object[] objArr = new Object[1];
		if (event.getObject() instanceof Object[]) {
			objArr = (Object[]) event.getObject();
		} else {
			objArr[0] = event.getObject();
		}
		
		for (Object object : objArr) {
			if (object instanceof Offer) {
				Offer offer = (Offer) object;
				if (!exitsOfferInTable(offer)) {
					if (offerpkgOfferMapSelected.getOfferPackageMap() != null) {
						OfferpkgOfferMapModel offerpkgOfferMapModel = new OfferpkgOfferMapModel(
								new OfferpkgOfferMap(offer.getOfferId(), null, true, true), "",
								offer.getOfferName() + " v" + offer.getVersionInfo(), offer.getState(),
								offer.getDescription());
						normOffersOfPackage.set(normOffersOfPackage.indexOf(offerpkgOfferMapSelected),
								offerpkgOfferMapModel);
					} else {
						OfferpkgOfferMapModel offerpkgOfferMapModel = new OfferpkgOfferMapModel(
								new OfferpkgOfferMap(offer.getOfferId(), null, true, true), "",
								offer.getOfferName() + " v" + offer.getVersionInfo(), offer.getState(),
								offer.getDescription());
						normOffersOfPackage.add(offerpkgOfferMapModel);
					}
				} else {
					if (offerpkgOfferMapSelected.getOfferPackageMap() == null
							|| offerpkgOfferMapSelected.getOfferPackageMap().getOfferId() != offer.getOfferId()) {
						super.showNotification(FacesMessage.SEVERITY_WARN, "common.duplicate", "");
					}
				}
			}
		}
	}

	private boolean exitsOfferInTable(Offer offer) {
		for (OfferpkgOfferMapModel offerpkgOfferMapModel : normOffersOfPackage) {
			if (offerpkgOfferMapModel.getOfferPackageMap().getOfferId() == offer.getOfferId()) {
				return true;
			}
		}
		return false;
	}

	public void saveOfferPackage() {
		if (!isValidated()) {
			return;
		}

		if (offerPackageDAO.saveOfferPackageAndMap(offerPackage, normOffersOfPackage)) {
			Category category = categoryDAO.get(offerPackage.getCategoryId());
			List<Offer> offers = offerDAO.findByOfferPkg(offerPackage.getOfferPkgId());
			if (offerPackage.getMainOfferId() != null) {
				Offer offer = offerDAO.get(offerPackage.getMainOfferId());
				if (offer != null) {
					offers.add(0, offer);
				}
			}

			for (Offer offer : offers) {
				offer.setVersionName(true);
			}

			super.getTreeOfferBean().updateTreeNode(offerPackage, category, offers);
			super.showNotification(FacesMessage.SEVERITY_INFO, "common.success", "");
			ediable = false;
		}
	}

	// CONTEXT

	public void editOfferPackage(NodeSelectEvent event) {
		super.getTreeOfferBean().onNodeSelect(event);
		this.ediable = true;
	}

	public void moveUpOfferPackage(NodeSelectEvent event) {
		OfferPackage offerPackage = (OfferPackage) event.getTreeNode().getData();
		BaseEntity objParent = (BaseEntity) event.getTreeNode().getParent().getData();
		if (objParent instanceof Category) {
			if (offerPackage.getIndex() > offerPackageDAO.getMinIndex(offerPackage.getCategoryId())) {
				OfferPackage offerPackageTarget = null;
				if ((offerPackageTarget = offerPackageDAO.moveObjectTo(offerPackage, true)) != null) {
					super.getTreeOfferBean().moveUpTreeNode(offerPackage, objParent);
					super.getTreeOfferBean().updateTreeNode(offerPackageTarget, (Category) objParent, null);
					try {
						if (offerPackage.getOfferPkgId() == this.offerPackage.getOfferPkgId()) {
							this.offerPackage = offerPackage.clone();
						} else if (offerPackageTarget.getOfferPkgId() == this.offerPackage.getOfferPkgId()) {
							this.offerPackage = offerPackageTarget.clone();
						}
					} catch (CloneNotSupportedException e) {
						getLogger().warn(e.getMessage(), e);
					}

					super.showNotification(FacesMessage.SEVERITY_INFO, "common.success", "");
				}
			}
		}
	}

	public void moveDownOfferPackage(NodeSelectEvent event) {
		OfferPackage offerPackage = (OfferPackage) event.getTreeNode().getData();
		BaseEntity objParent = (BaseEntity) event.getTreeNode().getParent().getData();
		if (objParent instanceof Category) {

			if (offerPackage.getIndex() < offerPackageDAO.getMaxIndex(offerPackage.getCategoryId())) {
				OfferPackage offerPackageTarget = null;
				if ((offerPackageTarget = offerPackageDAO.moveObjectTo(offerPackage, false)) != null) {
					super.getTreeOfferBean().moveDownTreeNode(offerPackage, objParent);
					super.getTreeOfferBean().updateTreeNode(offerPackageTarget, (Category) objParent, null);
					try {
						if (offerPackage.getOfferPkgId() == this.offerPackage.getOfferPkgId()) {
							this.offerPackage = offerPackage.clone();
						} else if (offerPackageTarget.getOfferPkgId() == this.offerPackage.getOfferPkgId()) {
							this.offerPackage = offerPackageTarget.clone();
						}
					} catch (CloneNotSupportedException e) {
						getLogger().warn(e.getMessage(), e);
					}
					super.showNotification(FacesMessage.SEVERITY_INFO, "common.success", "");
				}
			}
		}
	}

	public void deleteOfferPackage(NodeSelectEvent event) {
		OfferPackage offerPackage = (OfferPackage) event.getTreeNode().getData();
		removeOffePackage(offerPackage);
	}

	// GET, SET
	public String getFormType() {
		return formType;
	}

	public void setFormType(String formType) {
		this.formType = formType;
	}

	public List<OfferPackage> getOfferPackages() {
		return offerPackages;
	}

	public void setOfferPackages(List<OfferPackage> offerPackages) {
		this.offerPackages = offerPackages;
	}

	public OfferPackage getOfferPackage() {
		return offerPackage;
	}

	public void setOfferPackage(OfferPackage offerPackage) {
		this.offerPackage = offerPackage;
	}

	public void loadCategoriesOfOfferPackage() {
		categoriesOfOfferPackage = categoryDAO.findByType(CategoryType.OFF_OFFER_PACKAGE);
	}

	public Set<Map.Entry<Long, String>> getListStatus() {
		return ContantsUtil.OfferPackage.status.getStatus().entrySet();
	}

	public Offer getMainOffer() {
		return mainOffer;
	}

	public void setMainOffer(Offer mainOffer) {
		this.mainOffer = mainOffer;
	}

	public List<OfferpkgOfferMapModel> getNormOffersOfPackage() {
		return normOffersOfPackage;
	}

	public void setNormOffersOfPackage(List<OfferpkgOfferMapModel> normOffersOfPackage) {
		this.normOffersOfPackage = normOffersOfPackage;
	}

	public boolean isEdiable() {
		return ediable;
	}

	public void setEdiable(boolean ediable) {
		this.ediable = ediable;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public List<Category> getCategoriesOfOfferPackage() {
		return categoriesOfOfferPackage;
	}

	public void setCategoriesOfOfferPackage(List<Category> categoriesOfOfferPackage) {
		this.categoriesOfOfferPackage = categoriesOfOfferPackage;
	}

}

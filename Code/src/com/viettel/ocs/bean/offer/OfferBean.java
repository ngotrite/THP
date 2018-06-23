package com.viettel.ocs.bean.offer;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;

import com.viettel.ocs.bean.BaseController;
import com.viettel.ocs.constant.CategoryType;
import com.viettel.ocs.constant.ContantsUtil;
import com.viettel.ocs.constant.ContantsUtil.OfferState;
import com.viettel.ocs.constant.ContantsUtil.OfferType;
import com.viettel.ocs.constant.ContantsUtil.RedirectSetting;
import com.viettel.ocs.constant.TreeType;
import com.viettel.ocs.dao.ActionDAO;
import com.viettel.ocs.dao.BillingCycleTypeDAO;
import com.viettel.ocs.dao.CategoryDAO;
import com.viettel.ocs.dao.OfferDAO;
import com.viettel.ocs.dao.OfferPackageDAO;
import com.viettel.ocs.dao.OfferpkgOfferMapDAO;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.Action;
import com.viettel.ocs.entity.BillingCycleType;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.Offer;
import com.viettel.ocs.entity.OfferDump;
import com.viettel.ocs.entity.OfferPackage;
import com.viettel.ocs.entity.OfferParameterMap;
import com.viettel.ocs.entity.OfferpkgOfferMap;
import com.viettel.ocs.entity.Parameter;
import com.viettel.ocs.entity.RateTable;
import com.viettel.ocs.entity.RedirectAddress;
import com.viettel.ocs.model.OfferRecompiledModel;
import com.viettel.ocs.model.OfferpkgOfferMapModel;
import com.viettel.ocs.model.PrameterModel;

@SuppressWarnings("serial")
@ManagedBean(name = "offerBean")
@ViewScoped
public class OfferBean extends BaseController implements Serializable {

	private Category selectedCategory;
	private List<Category> categoriesForConvert;
	private Offer offerUpgrade;
	private Offer offerDowngrade;
	private Offer offer;
	private List<Offer> offerVersions;
	private List<RedirectAddress> redirectAddresses;

	private OfferPackage offerPackage;
	private List<OfferpkgOfferMapModel> normOffersOfPackage;
	private List<OfferpkgOfferMapModel> offerPackagesOfOffer;
	private List<Offer> normOffer;
	private List<OfferRecompiledModel> offerModels;
	private List<Parameter> parametersOfOffer;
	private HashMap<Long, List<Parameter>> parametersMap;

	private String formType;

	private OfferDump offerGroup;
	private List<OfferDump> listOfferByCat;
	private List<Action> actions;

	private Map<Long, String> mapOfferState;
	private Map<Long, String> mapOfferType;
	private Map<Long, String> mapPaymentType;

	private List<Category> lstCategory;

	private CategoryDAO categoryDAO;
	private OfferDAO offerDAO;
	private ActionDAO actionDAO;
	private OfferPackageDAO offerPackageDAO;
	private BillingCycleTypeDAO billingCycleTypeDAO;
	private OfferpkgOfferMapDAO offerpkgOfferMapDAO;

	private List<PrameterModel> prameterModels;

	private Offer offerToConvert;

	private boolean editable = false;
	private Long offerType;

	private Offer offerTemp;

	@PostConstruct
	public void init() {
		offer = new Offer();
		offerTemp = new Offer();
		offerUpgrade = new Offer();
		offerDowngrade = new Offer();
		selectedCategory = new Category();
		offerGroup = new OfferDump();
		parametersOfOffer = new ArrayList<Parameter>();
		offerModels = new ArrayList<OfferRecompiledModel>();
		parametersMap = new HashMap<Long, List<Parameter>>();
		listOfferByCat = new ArrayList<OfferDump>();
		categoriesForConvert = new ArrayList<Category>();

		categoryDAO = new CategoryDAO();
		offerDAO = new OfferDAO();
		offerPackageDAO = new OfferPackageDAO();
		offerpkgOfferMapDAO = new OfferpkgOfferMapDAO();
		mapOfferState = ContantsUtil.OfferState.getOfferStates();
		mapOfferType = ContantsUtil.OfferType.getOfferTypeAll();
		mapPaymentType = ContantsUtil.PaymentType.getPaymentTypes();
		actionDAO = new ActionDAO();
		actions = new ArrayList<Action>();
		offerVersions = new ArrayList<Offer>();
		redirectAddresses = new ArrayList<RedirectAddress>();
		prameterModels = new ArrayList<PrameterModel>();
		normOffer = new ArrayList<Offer>();
		offerPackagesOfOffer = new ArrayList<OfferpkgOfferMapModel>();
		offerPackage = new OfferPackage();
		normOffersOfPackage = new ArrayList<OfferpkgOfferMapModel>();
		billingCycleTypeDAO = new BillingCycleTypeDAO();
		offerToConvert = new Offer();
	}

	private void initRedirectAddress() {
		redirectAddresses = offerDAO.getRedirectAddress(offer);
		if (redirectAddresses.size() < 1) {
			redirectAddresses.add(new RedirectAddress(RedirectSetting.SUSPEND));
			redirectAddresses.add(new RedirectAddress(RedirectSetting.NORMAL));
		} else if (redirectAddresses.size() < 2) {
			if (redirectAddresses.get(0).getRedirectType() == RedirectSetting.NORMAL) {
				redirectAddresses.add(new RedirectAddress(RedirectSetting.SUSPEND));
			} else {
				redirectAddresses.add(new RedirectAddress(RedirectSetting.NORMAL));
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

	public String getNameRedirectType(Long value) {
		if (value == RedirectSetting.SUSPEND) {
			return RedirectSetting.SUSPEND_NAME;
		} else if (value == RedirectSetting.NORMAL) {
			return RedirectSetting.NORMAL_NAME;
		}
		return "";
	}

	private void loadCategory(long catId) {
		// Reload category selectbox
		Category cat = categoryDAO.get(catId);
		if (cat != null) {
			this.lstCategory = categoryDAO.findByTypeForSelectbox(cat.getCategoryType());
		}
	}

	public void refreshOfferGroup(OfferDump dump) {
		try {
			this.offerGroup = dump.clone();

			this.formType = "offer_detail_generic";
			this.selectedCategory = categoryDAO.get(dump.getCategoryId());
			if (offerGroup.getOfferExternalId() != null && !offerGroup.getOfferExternalId().isEmpty()) {
				this.offerVersions = offerDAO.findByConditions(new String[] { "offerExternalId", "offerName" },
						new Operator[] { Operator.LIKE, Operator.LIKE },
						new Object[] { offerGroup.getOfferExternalId(), offerGroup.getOfferName() },
						" CAST(versionInfo AS integer) DESC");
				if (offerVersions.size() > 0 && offerVersions.get(0).getOfferType() == OfferType.COMPILED) {
					offerTemp = offerDAO.get(offerVersions.get(0).getOfferTemplateId());
					offerTemp.setVersionName(true);
				}
				offerType = offerVersions.get(0).getOfferType();
			} else {
				this.offerVersions.clear();
				this.prameterModels.clear();
			}
			this.loadCategory(
					dump.getCategoryId() != null ? dump.getCategoryId() : this.selectedCategory.getCategoryId());

		} catch (CloneNotSupportedException e) {
			getLogger().warn(e.getMessage(), e);
		}
		this.editable = false;
	}

	public void refreshOfferVersionPage(Offer offer, Category category) {
		if (offer == null) {
			this.formType = "offer-list";
			this.selectedCategory = category;
			this.listOfferByCat = offerDAO.findOfferByCatId(category.getCategoryId());
			this.editable = true;
			clearFilter();
		} else {
			try {
				this.selectedCategory = categoryDAO.get(offer.getCategoryId());
				loadCategory(this.selectedCategory.getCategoryId());

				this.offer = offer.clone();

				this.formType = "offer_detail";
				if (offer.getOfferId() != 0L) {
					this.actions = actionDAO.findActionByOffer(offer.getOfferId());

					this.offerDowngrade = offerDAO.get(offer.getOfferDowngradeId());
					offerDowngrade = (offerDowngrade == null ? new Offer() : offerDowngrade);

					this.offerUpgrade = offerDAO.get(offer.getOfferUpgradeId());
					offerUpgrade = (offerUpgrade == null ? new Offer() : offerUpgrade);

					if (offer.getOfferType() != null && offer.getOfferType() == ContantsUtil.OfferType.MAIN) {
						if (offer.getOfferPackageId() != null) {
							OfferPackage offerPackage = offerPackageDAO.get(offer.getOfferPackageId());
							this.offerPackage = offerPackage;
							normOffersOfPackage = offerDAO.findByOfferPackage(offer.getOfferPackageId());
						} else {
							this.offerPackage = new OfferPackage();
							normOffersOfPackage.clear();
						}
					} else if (offer.getOfferType() != null) {
						this.offerPackage = new OfferPackage();
						offerPackagesOfOffer = offerpkgOfferMapDAO.findByOfferId(offer.getOfferId());
					}

					if (this.selectedCategory.getCategoryType() == CategoryType.OFF_OFFER_SUBSCRIPTION_COMPILED
							|| this.selectedCategory.getCategoryType() == CategoryType.OFF_OFFER_ONETIME_COMPILED) {
						this.prameterModels = offerDAO.findListParameterByOfferCompiled(offer);
						this.loadCategoriesForConvert();
					} else {
						this.prameterModels.clear();
					}

				} else {
					this.offer.setVersionInfo(offerDAO.generateOfferVersion(this.offer));
					this.actions.clear();
					this.normOffersOfPackage.clear();
					this.offerPackagesOfOffer.clear();
					this.offerUpgrade = new Offer();
					this.offerDowngrade = new Offer();
					this.redirectAddresses.clear();
				}

				initRedirectAddress();

				super.getTreeOfferBean().hideCategory();
				super.getTreeOfferBean().setContentPage("page_offer");
			} catch (CloneNotSupportedException e) {
				getLogger().warn(e.getMessage(), e);
			}
			this.editable = offer.getOfferId() == 0L;
			offerType = offer.getOfferType();
		}

		this.offerUpgrade.setVersionName(true);
		this.offerDowngrade.setVersionName(true);
	}

	public void btnAddNewFromCat() {
		super.getTreeOfferBean().hideCategory();
		formType = "offer_detail_generic";
		OfferDump dump = new OfferDump();
		dump.setCategoryId(this.selectedCategory.getCategoryId());
		refreshOfferGroup(dump);
		this.editable = true;
	}

	public boolean addNewable() {
		if (this.selectedCategory.getCategoryType() == CategoryType.OFF_OFFER_TEMPLATE
				|| this.selectedCategory.getCategoryType() == CategoryType.OFF_OFFER_SUBSCRIPTION_NORMAL
				|| this.selectedCategory.getCategoryType() == CategoryType.OFF_OFFER_ONETIME_NORMAL
				|| this.selectedCategory.getCategoryType() == CategoryType.OFF_OFFER_SUBSCRIPTION_MAIN) {
			return true;
		} else {
			return false;
		}
	}

	private boolean validateForChangeAllOffer(String oldOfferName) {
		Object object = offerDAO.validateOfferExist(this.offerGroup.getOfferExternalId(),
				this.offerGroup.getOfferName(), oldOfferName);
		Object[] values = (Object[]) object;
		BigDecimal duplicateAll = (BigDecimal) values[0];
		BigDecimal duplicateOnlyExternalId = (BigDecimal) values[1];
		BigDecimal duplicateOnlyName = (BigDecimal) values[2];
		BigDecimal duplicate = (BigDecimal) values[3];

		if (duplicateAll.compareTo(new BigDecimal(0)) > 0) {
			showNotification(FacesMessage.SEVERITY_WARN, "common.summary.warning", "common.msg.detail.existed");
			return false;
		} else if (duplicate.compareTo(new BigDecimal(0)) > 0) {
			if (duplicateOnlyExternalId.compareTo(new BigDecimal(0)) > 0
					&& duplicateOnlyName.compareTo(new BigDecimal(0)) > 0) {
				showNotification(FacesMessage.SEVERITY_WARN, "common.summary.warning",
						"common.msg.detail.existed_ext_id");
				return false;
			} else if (duplicateOnlyExternalId.compareTo(new BigDecimal(0)) > 0) {
				showNotification(FacesMessage.SEVERITY_WARN, "common.summary.warning",
						"common.msg.detail.existed_ext_id");
				return false;
			} else if (duplicateOnlyName.compareTo(new BigDecimal(0)) > 0) {
				showNotification(FacesMessage.SEVERITY_WARN, "common.summary.warning",
						"common.msg.detail.existed_name");
				return false;
			} else {
				showNotification(FacesMessage.SEVERITY_WARN, "common.summary.warning",
						"common.msg.detail.existed_name");
				return false;
			}
		}
		return true;
	}

	private boolean validateForConvertToNomal(String oldOfferName) {
		Object object = offerDAO.validateOfferExist(this.offerToConvert.getOfferExternalId(),
				this.offerToConvert.getOfferName(), oldOfferName);
		Object[] values = (Object[]) object;
		BigDecimal duplicateAll = (BigDecimal) values[0];
		BigDecimal duplicateOnlyExternalId = (BigDecimal) values[1];
		BigDecimal duplicateOnlyName = (BigDecimal) values[2];
		BigDecimal duplicate = (BigDecimal) values[3];

		if (duplicate.compareTo(new BigDecimal(0)) > 0) {
			List<Offer> offers = offerDAO.findMoreVersionByOffer(offerToConvert, OfferType.NORMAL);
			if (offers.size() == 0) {
				showNotification(FacesMessage.SEVERITY_WARN, "common.summary.warning", "common.msg.detail.existed");
				return false;
			} else if (!offers.get(0).getCategoryId().equals(offerToConvert.getCategoryId())) {
				return false;
			} else {
				return true;
			}
		} else if (duplicateOnlyExternalId.compareTo(new BigDecimal(0)) > 0) {
			showNotification(FacesMessage.SEVERITY_WARN, "common.summary.warning", "common.msg.detail.existed_ext_id");
			return false;
		} else if (duplicateOnlyName.compareTo(new BigDecimal(0)) > 0) {
			showNotification(FacesMessage.SEVERITY_WARN, "common.summary.warning", "common.msg.detail.existed_name");
			return false;
		}
		return true;
	}

	public void addNewVersion() {

		Long offerType = null;
		if (offerVersions.size() == 0) {
			if (!validateForChangeAllOffer("")) {
				return;
			}
		} else if (!offerVersions.get(0).getOfferExternalId().trim().equalsIgnoreCase(offerGroup.getOfferExternalId())
				|| !offerVersions.get(0).getOfferName().trim().equalsIgnoreCase(offerGroup.getOfferName())
				|| !offerVersions.get(0).getCategoryId().equals(offerGroup.getCategoryId())) {
			showNotification(FacesMessage.SEVERITY_WARN, "common.summary.warning", "common.msg.detail.save");
			return;
		} else {
			offerType = offerVersions.get(0).getOfferType();
		}

		selectedCategory = categoryDAO.get(this.offerGroup.getCategoryId());
		Offer offer = new Offer();
		if (selectedCategory.getCategoryType() == CategoryType.OFF_OFFER_TEMPLATE) {
			offer.setOfferType(ContantsUtil.OfferType.TEMPLATE);
		} else if (selectedCategory.getCategoryType() == CategoryType.OFF_OFFER_SUBSCRIPTION_MAIN) {
			offer.setOfferType(ContantsUtil.OfferType.MAIN);
		} else if (selectedCategory.getCategoryType() == CategoryType.OFF_OFFER_SUBSCRIPTION_NORMAL
				|| selectedCategory.getCategoryType() == CategoryType.OFF_OFFER_ONETIME_NORMAL) {
			offer.setOfferType(offerType != null ? offerType : ContantsUtil.OfferType.NORMAL);
		} else if (selectedCategory.getCategoryType() == CategoryType.OFF_OFFER_SUBSCRIPTION_COMPILED) {
			offer.setOfferType(ContantsUtil.OfferType.COMPILED);
		}

		getOfferType(selectedCategory);

		offer.setEffDate(new Date());
		offer.setOfferName(this.offerGroup.getOfferName());
		offer.setOfferExternalId(this.offerGroup.getOfferExternalId());
		offer.setCategoryId(this.offerGroup.getCategoryId());
		offer.setVersionInfo(offerDAO.generateOfferVersion(this.offer));
		offer.setState(OfferState.IN_ACTIVE);
		refreshOfferVersionPage(offer, selectedCategory);
	}

	private Action actionSelected;
	private Boolean insert;

	public void changeAction(Action action) {
		actionSelected = action;
		insert = false;
		
		List<Long> lstId = new ArrayList<Long>();
		if (actions != null) {
			for (Action rt : actions) {
				lstId.add(rt.getActionId());
			}
		}
		
		super.openTreeOfferDialog(TreeType.OFFER_ACTION, readProperties("common.action"), 0, false, lstId);
	}

	public void addActionAt(Action action) {
		actionSelected = action;
		insert = true;
		
		List<Long> lstId = new ArrayList<Long>();
		if (actions != null) {
			for (Action rt : actions) {
				lstId.add(rt.getActionId());
			}
		}
		
		super.openTreeOfferDialog(TreeType.OFFER_ACTION, readProperties("common.action"), 0, false, lstId);
	}

	public void chooseAction() {
		actionSelected = new Action();
		insert = false;
		
		List<Long> lstId = new ArrayList<Long>();
		if (actions != null) {
			for (Action rt : actions) {
				lstId.add(rt.getActionId());
			}
		}
		
		super.openTreeOfferDialog(TreeType.OFFER_ACTION, readProperties("common.action"), 0, true, lstId);
	}

	public void onDialogActionReturn(SelectEvent event) {
		Object[] objArr = new Object[1];
		if (event.getObject() instanceof Object[]) {
			objArr = (Object[]) event.getObject();
		} else {
			objArr[0] = event.getObject();
		}

		for (Object object : objArr) {
			if (object instanceof Action) {
				Action action = (Action) object;
				if (!exitsActionInTable(action, this.actions)) {
					if (actionSelected.getActionId() == 0L) {
						actions.add(action);
					} else {
						if (insert) {
							actions.add(actions.indexOf(actionSelected) + 1, action);
						} else {
							actions.set(actions.indexOf(actionSelected), action);
						}
					}
				} else {
					if (action.getActionId() == actionSelected.getActionId() && !insert) {
						actions.set(actions.indexOf(actionSelected), action);
					} else {
						showNotification(FacesMessage.SEVERITY_WARN, "common.duplicate", "offer.msg.duplicate_action");
					}
				}
			}
		}
	}

	public boolean showStateCombobox() {
		long count = offerDAO.countMoreVersionByOffer(offer);
		if (count == 0 && (selectedCategory.getCategoryType() == CategoryType.OFF_OFFER_SUBSCRIPTION_NORMAL
				|| selectedCategory.getCategoryType() == CategoryType.OFF_OFFER_ONETIME_NORMAL)) {
			return true;
		}

		return false;
	}

	private OfferpkgOfferMapModel offerpkgOfferMapModelSelected;

	public void changePackage(OfferpkgOfferMapModel item) {
		offerpkgOfferMapModelSelected = item;
		super.openTreeOfferDialog(TreeType.OFFER_PACKAGE, readProperties("offer.offerpackage"), 0, false, null);
	}

	public void choosePackage() {
		offerpkgOfferMapModelSelected = new OfferpkgOfferMapModel();
		if(offer != null && this.offer.getOfferType() != null 
				&& this.offer.getOfferType() == ContantsUtil.OfferType.MAIN) {
			
			super.openTreeOfferDialog(TreeType.OFFER_PACKAGE, readProperties("offer.offerpackage"), ContantsUtil.OfferPackage.offerPkgType.NORMAL, false, null);	
		} else {
			
			super.openTreeOfferDialog(TreeType.OFFER_PACKAGE, readProperties("offer.offerpackage"), 0, false, null);
		}		
	}

	public void changeNormOffer(OfferpkgOfferMapModel item) {
		offerpkgOfferMapModelSelected = item;
		super.openTreeOfferDialog(TreeType.OFFER_TREE_NORMAL_DLG, CategoryType.OFF_OFFER_NAME, 0, false, null);
	}

	public void chooseNormOffer() {
		offerpkgOfferMapModelSelected = new OfferpkgOfferMapModel();
		super.openTreeOfferDialog(TreeType.OFFER_TREE_NORMAL_DLG, CategoryType.OFF_OFFER_NAME, 0, false, null);
	}

	public void onDialogNormOfferReturn(SelectEvent event) {
		Object object = event.getObject();
		if (object instanceof Offer) {
			Offer offer = (Offer) object;
			if (!exitsOfferInTable(offer)) {
				if (offerpkgOfferMapModelSelected.getOfferPackageMap() != null) {
					OfferpkgOfferMapModel offerpkgOfferMapModel = new OfferpkgOfferMapModel(
							new OfferpkgOfferMap(offer.getOfferId(), null, true, true), "", offer.getOfferName(),
							offer.getState(), offer.getDescription());
					normOffersOfPackage.set(normOffersOfPackage.indexOf(offerpkgOfferMapModelSelected),
							offerpkgOfferMapModel);
				} else {
					OfferpkgOfferMapModel offerpkgOfferMapModel = new OfferpkgOfferMapModel(
							new OfferpkgOfferMap(offer.getOfferId(), null, true, true), "", offer.getOfferName(),
							offer.getState(), offer.getDescription());
					normOffersOfPackage.add(offerpkgOfferMapModel);
				}
			} else {
				if (offerpkgOfferMapModelSelected.getOfferPackageMap().getOfferId() == offer.getOfferId()) {
					OfferpkgOfferMapModel offerpkgOfferMapModel = new OfferpkgOfferMapModel(
							new OfferpkgOfferMap(offer.getOfferId(), null, true, true), "", offer.getOfferName(),
							offer.getState(), offer.getDescription());
					normOffersOfPackage.set(normOffersOfPackage.indexOf(offerpkgOfferMapModelSelected),
							offerpkgOfferMapModel);
				} else {
					showNotification(FacesMessage.SEVERITY_WARN, "common.duplicate", "");
				}
			}
		}
	}

	public void onDialogMainOfferUpgrateReturn(SelectEvent event) {
		Object object = event.getObject();
		if (object instanceof Offer) {
			Offer offer = (Offer) object;
			this.offerUpgrade = offer;
			this.offerUpgrade.setVersionName(true);
			this.offer.setOfferUpgradeId(offer.getOfferId());
		}
	}

	public String getSateName(Long id) {
		return mapOfferState.get(id);
	}

	public void onDialogMainOfferDowngrateReturn(SelectEvent event) {
		Object object = event.getObject();
		if (object instanceof Offer) {
			Offer offer = (Offer) object;
			this.offerDowngrade = offer;
			this.offerDowngrade.setVersionName(true);
			this.offer.setOfferDowngradeId(offer.getOfferId());
		}
	}

	public void onDialogPackageMainReturn(SelectEvent event) {
		Object object = event.getObject();
		if (object instanceof OfferPackage) {
			OfferPackage offerPackage = (OfferPackage) object;
			this.offer.setOfferPackageId(offerPackage.getOfferPkgId());
			this.offerPackage = offerPackage;
			this.normOffersOfPackage = offerDAO.findByOfferPackage(offerPackage.getOfferPkgId());
		}
	}

	public void deletePackage() {
		this.offerPackage = new OfferPackage();
		this.offer.setOfferPackageId(null);
		this.normOffersOfPackage.clear();
	}

	public void onDialogPackageNormReturn(SelectEvent event) {
		Object object = event.getObject();
		if (object instanceof OfferPackage) {
			OfferPackage offerPackage = (OfferPackage) object;
			if (!exitsOfferPackageInTable(offerPackage)) {
				if (offerpkgOfferMapModelSelected.getOfferPackageMap() == null) {
					OfferpkgOfferMapModel offerpkgOfferMapModel = new OfferpkgOfferMapModel(
							new OfferpkgOfferMap(null, offerPackage.getOfferPkgId(), true, true),
							offerPackage.getOfferPkgName(), "", offer.getState(), offerPackage.getRemark());
					offerPackagesOfOffer.add(offerpkgOfferMapModel);
				} else {
					OfferpkgOfferMapModel offerpkgOfferMapModel = new OfferpkgOfferMapModel(
							new OfferpkgOfferMap(null, offerPackage.getOfferPkgId(), true, true),
							offerPackage.getOfferPkgName(), "", offer.getState(), offerPackage.getRemark());
					offerPackagesOfOffer.set(offerPackagesOfOffer.indexOf(offerpkgOfferMapModelSelected),
							offerpkgOfferMapModel);
				}
			} else {
				if (offerpkgOfferMapModelSelected.getOfferPackageMap() != null && offerpkgOfferMapModelSelected
						.getOfferPackageMap().getOfferPackageId().equals(offerPackage.getOfferPkgId())) {
					// OfferpkgOfferMapModel offerpkgOfferMapModel = new
					// OfferpkgOfferMapModel(
					// new OfferpkgOfferMap(null, offerPackage.getOfferPkgId(),
					// true, true),
					// offerPackage.getOfferPkgName(), "",
					// offerPackage.getRemark());
					// offerPackagesOfOffer.set(offerPackagesOfOffer.indexOf(offerpkgOfferMapModelSelected),
					// offerpkgOfferMapModel);
				} else {
					super.showNotification(FacesMessage.SEVERITY_WARN, "common.duplicate", "");
				}
			}

		}
	}

	private boolean exitsOfferPackageInTable(OfferPackage offerPackage) {
		for (OfferpkgOfferMapModel offerpkgOfferMapModel : offerPackagesOfOffer) {
			if (offerpkgOfferMapModel.getOfferPackageMap().getOfferPackageId() == offerPackage.getOfferPkgId()) {
				return true;
			}
		}
		return false;
	}

	private boolean exitsActionInTable(Action action, List<Action> actions) {
		for (Action actionItem : actions) {
			if (actionItem.getActionId() == action.getActionId()) {
				return true;
			}
		}
		return false;
	}

	public void changeAllVersion() {
		this.changeAllVersion(offerVersions, offerGroup);
	}

	public void changeAllVersion(List<Offer> offerVersions, OfferDump offerGroup) {
		if (offerVersions.size() == 0
				|| (offerVersions.get(0).getOfferName().trim().equalsIgnoreCase(offerGroup.getOfferName().trim())
						&& offerVersions.get(0).getOfferExternalId().trim()
								.equalsIgnoreCase(offerGroup.getOfferExternalId().trim())
						&& offerVersions.get(0).getCategoryId().equals(offerGroup.getCategoryId()))
				|| !validateForChangeAllOffer(offerVersions.get(0).getOfferName())) {
			this.editable = false;
			return;
		}
		if (offerDAO.changeNameAndExtIdVersion(offerVersions, offerGroup)) {
			for (Offer offer : offerVersions) {
				offer.setOfferName(offerGroup.getOfferName().trim());
				offer.setOfferExternalId(offerGroup.getOfferExternalId().trim());
				offer.setCategoryId(offerGroup.getCategoryId());

				Category category = categoryDAO.get(offer.getCategoryId());
				List<Action> actions = actionDAO.findActionByOffer(offer.getOfferId());
				super.getTreeOfferBean().updateTreeNodeOffer(offer, category, actions, false);
			}
			super.showNotification(FacesMessage.SEVERITY_INFO, "common.success", "");
			this.editable = false;
		}
	}

	public void saveOfferWithMap() {
		if (!isValidated()) {
			return;
		}

		if (offerDAO.saveOfferAndMap(offer, actions, redirectAddresses, offerPackagesOfOffer)) {
			Category cat = categoryDAO.get(offer.getCategoryId());
			super.getTreeOfferBean().updateTreeNodeOffer(offer, cat, actions, true);
			this.showMessageINFO("common.save", " Offer ");
			this.editable = false;
		}
	}

	private boolean isValidated() {
		if (offer.getExpDate() != null && offer.getExpDate().compareTo(offer.getEffDate()) != 1) {
			super.showNotification(FacesMessage.SEVERITY_WARN, "common.fail", "offer.msg.expdate_invalid");
			return false;
		}
		return true;
	}

	public void commandEditTable(OfferDump offerDump) {
		super.getTreeOfferBean().hideCategory();
		refreshOfferGroup(offerDump);
		this.editable = true;
	}

	public void commandRemoveTable(OfferDump offerDump) {
		removeOfferGroup(offerDump);
	}

	public void editAction(Action action) {
		formType = "";
		super.getTreeOfferBean().setActionProperties(false, null, action, true, false);
	}

	public void removeAction(Action action) {
		actions.remove(action);
	}

	public void editOfferVersion(Offer offer) {
		this.refreshOfferVersionPage(offer, null);
		getOfferType(selectedCategory);
		this.editable = true;
	}

	public void removePackageOfOffer(OfferpkgOfferMapModel offerpkgOfferMapModel) {
		offerPackagesOfOffer.remove(offerpkgOfferMapModel);
	}

	public void removeNormOfferOfPackage(OfferpkgOfferMapModel offerpkgOfferMapModel) {
		normOffersOfPackage.remove(offerpkgOfferMapModel);
	}

	public void updateNormOfferPackage() {
		if (offerPackage != null && offerPackage.getOfferPkgId() != 0L) {
			if (offerPackageDAO.saveOfferPackageAndMap(offerPackage, normOffersOfPackage)) {
				this.showMessageINFO("common.save", " action ");
			} else {
				this.showMessageWARN("common.save", " action ");
			}
		}
	}

	public void showCatagoryTreeNormal() {
		super.openTreeCategoryDialog(TreeType.OFFER_SUBCRIPTION_NORMAL_ONLY, "Choose", 0);
	}

	public void convertToNormalOffer() {
		if (!validateForConvertToNomal("")) {
			return;
		}

		offerToConvert.setVersionInfo(offerDAO.generateOfferVersion(offerToConvert));
		if (offerDAO.convertToNormalOffer(this.offerToConvert)) {
			try {
				this.offer = offerToConvert.clone();
			} catch (CloneNotSupportedException e) {
				getLogger().warn(e.getMessage(), e);
			}
			Category category = categoryDAO.get(offerToConvert.getCategoryId());
			List<Action> actions = actionDAO.findActionByOffer(offerToConvert.getOfferId());
			super.getTreeOfferBean().updateTreeNodeOffer(offerToConvert, category, actions, true);
			RequestContext.getCurrentInstance().execute("PF('dlg_convert_to_normal_offer').hide();");
			this.editable = false;
			showNotification(FacesMessage.SEVERITY_INFO, "common.success", "");
		} else {
			showNotification(FacesMessage.SEVERITY_WARN, "common.fail", "offer.msg.error_in_process");
		}
	}

	public boolean isCompiled() {
		if (offerType != null && offerType == OfferType.COMPILED) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isMain() {
		return (this.offer.getOfferType() == OfferType.MAIN);
	}

	public boolean isNormal() {
		return (this.offer.getOfferType() == OfferType.NORMAL);
	}

	public boolean isTemp() {
		return (this.offer.getOfferType() == OfferType.TEMPLATE);
	}

	public void moveDownAction(Action action) {
		int index = actions.indexOf(action);
		if (index < actions.size() - 1 && index > -1) {
			try {
				Action nextAction = actions.get(index + 1).clone();
				actions.set(index + 1, action.clone());
				actions.set(index, nextAction.clone());
			} catch (CloneNotSupportedException e) {
				getLogger().warn(e.getMessage(), e);
			}
		}
	}

	public void moveUpAction(Action action) {
		int index = actions.indexOf(action);
		if (index > 0) {
			try {
				Action preAction = actions.get(index - 1).clone();
				actions.set(index - 1, action.clone());
				actions.set(index, preAction.clone());
			} catch (CloneNotSupportedException e) {
				getLogger().warn(e.getMessage(), e);
			}
		}
	}

	/************ CONTEXT MENU - BEGIN ************/
	public void addNewActionContext(NodeSelectEvent nodeSelectEvent) {
		Offer offer = (Offer) nodeSelectEvent.getTreeNode().getData();
		if (offer.getOfferId() != this.offer.getOfferId()) {
			super.getTreeOfferBean().onNodeSelect(nodeSelectEvent);
		}
		editable = true;
		chooseAction();
	}

	public boolean showAddNewVersionContext(NodeSelectEvent nodeSelectEvent) {
		if (nodeSelectEvent != null) {
			Object object = nodeSelectEvent.getTreeNode().getData();
			if (object instanceof Offer) {
				Offer offer = (Offer) object;
				if (!offer.getOfferType().equals(OfferType.COMPILED)) {
					return true;
				}
			}
		}
		return false;
	}

	public void addNewVersionContext(NodeSelectEvent nodeSelectEvent) {
		super.getTreeOfferBean().hideCategory();

		super.getTreeOfferBean().onNodeSelect(nodeSelectEvent);

		Offer offer = new Offer();
		offer.setEffDate(new Date());
		offer.setCategoryId(this.offer.getCategoryId());
		offer.setOfferName(this.offer.getOfferName());
		offer.setOfferExternalId(this.offer.getOfferExternalId());
		offer.setCategoryId(this.offer.getCategoryId());
		offer.setOfferType(this.offer.getOfferType());
		offer.setPriority(this.offer.getPriority());
		offer.setState(OfferState.IN_ACTIVE);

		selectedCategory = categoryDAO.get(this.offer.getCategoryId());
		getOfferType(selectedCategory);

		refreshOfferVersionPage(offer, null);
	}

	private void getOfferType(Category category) {
		if (category.getCategoryType() == CategoryType.OFF_OFFER_TEMPLATE) {
			mapOfferType = OfferType.getOfferTypeAll();
		} else if (category.getCategoryType() == CategoryType.OFF_OFFER_SUBSCRIPTION_MAIN) {
			mapOfferType = OfferType.getOfferTypeAll();
		} else if (category.getCategoryType() == CategoryType.OFF_OFFER_SUBSCRIPTION_NORMAL
				|| category.getCategoryType() == CategoryType.OFF_OFFER_ONETIME_NORMAL) {
			mapOfferType = OfferType.getOfferTypeForNorm();
		} else if (category.getCategoryType() == CategoryType.OFF_OFFER_SUBSCRIPTION_COMPILED) {
			mapOfferType = OfferType.getOfferTypeAll();
		}
	}

	public void editOfferVersionContext(NodeSelectEvent nodeSelectEvent) {
		super.getTreeOfferBean().onNodeSelect(nodeSelectEvent);
		Offer offer = (Offer) nodeSelectEvent.getTreeNode().getData();
		try {
			offerToConvert = offer.clone();
		} catch (CloneNotSupportedException e) {
			getLogger().warn(e.getMessage(), e);
		}
		this.editable = true;
	}

	public void clearFilter() {
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.execute("clearFilter('offer_list_id')");
	}

	public boolean showRemoveVersion(NodeSelectEvent nodeSelectEvent) {
		if (nodeSelectEvent != null) {
			Object object = nodeSelectEvent.getTreeNode().getData();
			if (object instanceof Offer) {
				Offer offer = (Offer) object;
				if (!offer.getOfferType().equals(OfferType.TEMPLATE)) {
					return true;
				}
			}
		}
		return false;
	}

	public void removeOfferVersion(Offer offer) {
		if (offer.getState() != OfferState.IN_ACTIVE && offer.getState() != OfferState.REMOVED) {
			showNotification(FacesMessage.SEVERITY_WARN, "common.fail", "offer.msg.remove_version");
		} else if (offer.getOfferType().equals(OfferType.TEMPLATE)) {
			this.offerModels = offerDAO.findAllCompiledOfferOfTemp(offer, OfferType.COMPILED);
			parametersOfOffer = this.hashMapParameter(offerDAO.findListParameterByOffer(this.offer));

			for (OfferRecompiledModel recompiledModel : offerModels) {
				parametersMap.put(recompiledModel.getOffer().getOfferId(), this.mixWithParamCompiled(parametersOfOffer,
						offerDAO.findOfferParameterMapsByOfferCompiled(recompiledModel.getOffer())));
			}
			this.selectedOffer = offer;
			RequestContext.getCurrentInstance().update("dlgDeleteOfferTemp");
			RequestContext.getCurrentInstance().execute("PF('dlgDeleteOfferTemp').show();");
		} else {
			if (offerDAO.deleteAndConvertToNorm(offer, null, new ArrayList<Offer>())) {
				// if
				// (offer.getOfferExternalId().equals(this.offerGroup.getOfferExternalId()))
				// {
				// for (Offer offerItem : offerVersions) {
				// if (offerItem.getOfferId() == offer.getOfferId()) {
				// offerVersions.remove(offerItem);
				// break;
				// }
				// }
				// }
				this.showMessageINFO("common.delete", " offer ");
				super.getTreeOfferBean().removeTreeNodeAll(offer);

				if (offerDAO.countMoreVersionByOffer(offer) > 0) {
					super.getTreeOfferBean().hideCategory();
					OfferDump dump = new OfferDump(offer.getOfferName(), offer.getCategoryId(),
							offer.getOfferExternalId());
					refreshOfferGroup(dump);
				} else {
					super.getTreeOfferBean().hideContentPage();
					Category category = categoryDAO.get(offer.getCategoryId());
					formType = "";
					super.getTreeOfferBean().setSelectCategoryNode(category);
				}
			} else {
				this.showMessageWARN("common.delete", " offer ");
			}

		}
	}

	public void removeOfferContext(NodeSelectEvent nodeSelectEvent) {
		Object object = nodeSelectEvent.getTreeNode().getData();
		this.selectedOffer = (Offer) object;
		this.removeOfferVersion(selectedOffer);
	}

	public void removeOfferTable(Offer offer) {
		this.selectedOffer = offer;
		this.removeOfferVersion(selectedOffer);
	}

	public void deleteOfferVersionTemp() {

		List<Offer> offersCompiled = offerDAO.findAllCompiledOfferOfTemp(selectedOffer);
		if (offerDAO.deleteAndConvertToNorm(selectedOffer, this.categoryId, offersCompiled)) {
			RequestContext.getCurrentInstance().execute("PF('dlgDeleteOfferTemp').hide();");
			super.getTreeOfferBean().removeTreeNodeAll(selectedOffer);

			for (Offer offer : offersCompiled) {
				if (offer.getState() != OfferState.REMOVED) {
					Category category = categoryDAO.get(offer.getCategoryId());
					List<Action> actions = actionDAO.findActionByOffer(offer.getOfferId());
					super.getTreeOfferBean().updateTreeNodeOffer(offer, category, actions, false);
				}
			}
			super.showNotification(FacesMessage.SEVERITY_INFO, "common.delete", "");

			if (offerDAO.countMoreVersionByOffer(selectedOffer) > 0) {
				super.getTreeOfferBean().hideCategory();
				OfferDump dump = new OfferDump(selectedOffer.getOfferName(), selectedOffer.getCategoryId(),
						selectedOffer.getOfferExternalId());
				refreshOfferGroup(dump);
			} else {
				super.getTreeOfferBean().hideContentPage();
				Category category = categoryDAO.get(selectedOffer.getCategoryId());
				formType = "";
				super.getTreeOfferBean().setSelectCategoryNode(category);
			}

		}
	}

	private Long categoryId;

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public void loadCategoriesForConvert() {
		List<Long> catTypes = new ArrayList<Long>();
		catTypes.add(CategoryType.OFF_OFFER_SUBSCRIPTION_NORMAL);
		catTypes.add(CategoryType.OFF_OFFER_ONETIME_NORMAL);
		categoriesForConvert = categoryDAO.findByTypeForSelectbox(catTypes);
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

	public void updateParameterList() {
		if (offerDAO.updateParameterMap(prameterModels)) {
			super.showNotification(FacesMessage.SEVERITY_INFO, "common.success", "");
			this.editable = false;
		}
	}

	public void commandCheckDependencies(NodeSelectEvent nodeSelectEvent) {
		if (nodeSelectEvent != null) {
			Offer item = (Offer) nodeSelectEvent.getTreeNode().getData();
			showDependencies(item.getOfferId(), Offer.class);
		}
	}

	public boolean showParameterList(NodeSelectEvent nodeSelectEvent) {
		if (nodeSelectEvent != null) {
			Object object = nodeSelectEvent.getTreeNode().getData();
			if (object instanceof Offer) {
				Offer offer = (Offer) object;
				if (offer.getOfferType() == OfferType.TEMPLATE) {
					return true;
				}
			}
		}
		return false;
	}

	public void parameterList(NodeSelectEvent nodeSelectEvent) {
		Map<String, Object> options = new HashMap<String, Object>();
		options.put("modal", true);
		options.put("widgetVar", "dlgTree");
		options.put("width", 1000);
		options.put("height", 550);
		options.put("resizable", false);
		options.put("contentWidth", "100%");
		options.put("contentHeight", "100%");
		Map<String, List<String>> mapPara = new HashMap<String, List<String>>();
		List<String> lstPara = new ArrayList<String>();
		Offer offer = (Offer) nodeSelectEvent.getTreeNode().getData();
		lstPara.add(String.valueOf(offer.getOfferId()));
		mapPara.put("offerId", lstPara);
		RequestContext.getCurrentInstance().openDialog("/pages/offer/context_offer/parameter_list.xhtml", options,
				mapPara);
	}

	public void cloneOfferVersion(Offer offer) {
		Map<String, Object> options = new HashMap<String, Object>();
		options.put("modal", true);
		options.put("widgetVar", "dlgTree");
		options.put("width", 800);
		options.put("height", 450);
		options.put("resizable", false);
		options.put("contentWidth", "100%");
		options.put("contentHeight", "100%");
		Map<String, List<String>> mapPara = new HashMap<String, List<String>>();
		List<String> lstPara = new ArrayList<String>();
		List<String> lstPara1 = new ArrayList<String>();
		lstPara.add(String.valueOf(offer.getOfferId()));
		lstPara1.add("cloneTemp");
		mapPara.put("offerId", lstPara);
		mapPara.put("type", lstPara1);
		RequestContext.getCurrentInstance().openDialog("/pages/offer/context_offer/clone_offer_temp.xhtml", options,
				mapPara);
	}

	public boolean showCloneOffer(NodeSelectEvent nodeSelectEvent) {
		if (nodeSelectEvent != null) {
			Object object = nodeSelectEvent.getTreeNode().getData();
			if (object instanceof Offer) {
				Offer offer = (Offer) object;
				if (offer.getOfferType() != OfferType.COMPILED) {
					return true;
				}
			}
		}
		return false;
	}

	public void cloneOffer(NodeSelectEvent nodeSelectEvent) {
		Offer offer = (Offer) nodeSelectEvent.getTreeNode().getData();
		this.cloneOfferVersion(offer);
	}

	public boolean showState(NodeSelectEvent nodeSelectEvent) {
		if (nodeSelectEvent != null) {
			Object object = nodeSelectEvent.getTreeNode().getData();
			if (object instanceof Offer) {
				Offer offer = (Offer) object;
				if (offer.getOfferType() != OfferType.TEMPLATE) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean showGenOfferTempToCom(NodeSelectEvent nodeSelectEvent) {
		if (nodeSelectEvent != null) {
			Object object = nodeSelectEvent.getTreeNode().getData();
			if (object instanceof Offer) {
				Offer offer = (Offer) object;
				if (offer.getOfferType() == OfferType.TEMPLATE) {
					return true;
				}
			}
		}
		return false;
	}

	public void genOfferTempToCom(NodeSelectEvent nodeSelectEvent) {
		Map<String, Object> options = new HashMap<String, Object>();
		options.put("modal", true);
		options.put("widgetVar", "dlgTree");
		options.put("width", 1000);
		options.put("height", 500);
		options.put("resizable", false);
		options.put("contentWidth", "100%");
		options.put("contentHeight", "100%");
		Map<String, List<String>> mapPara = new HashMap<String, List<String>>();
		List<String> lstPara = new ArrayList<String>();
		List<String> lstPara1 = new ArrayList<String>();
		Offer offer = (Offer) nodeSelectEvent.getTreeNode().getData();
		lstPara.add(String.valueOf(offer.getOfferId()));
		lstPara1.add("generate");
		mapPara.put("offerId", lstPara);
		mapPara.put("type", lstPara1);
		RequestContext.getCurrentInstance().openDialog("/pages/offer/context_offer/generate_offer.xhtml", options,
				mapPara);
	}

	public boolean showReCompiledComp(NodeSelectEvent nodeSelectEvent) {
		if (nodeSelectEvent != null) {
			Object object = nodeSelectEvent.getTreeNode().getData();
			if (object instanceof Offer) {
				Offer offer = (Offer) object;
				if (offer.getOfferType() == OfferType.COMPILED) {
					return true;
				}
			}
		}
		return false;
	}

	public void reCompiledComp(NodeSelectEvent nodeSelectEvent) {
		Map<String, Object> options = new HashMap<String, Object>();
		options.put("modal", true);
		options.put("width", 500);
		options.put("height", 300);
		options.put("resizable", false);
		options.put("contentWidth", "100%");
		options.put("contentHeight", "100%");
		Map<String, List<String>> mapPara = new HashMap<String, List<String>>();
		List<String> lstPara = new ArrayList<String>();
		List<String> lstPara1 = new ArrayList<String>();
		Offer offer = (Offer) nodeSelectEvent.getTreeNode().getData();
		lstPara.add(String.valueOf(offer.getOfferId()));
		lstPara1.add("reconpiled");
		mapPara.put("offerId", lstPara);
		mapPara.put("type", lstPara1);
		RequestContext.getCurrentInstance().openDialog("/pages/offer/context_offer/re_compiled.xhtml", options,
				mapPara);
	}

	public boolean showReCompiledTemp(NodeSelectEvent nodeSelectEvent) {
		if (nodeSelectEvent != null) {
			Object object = nodeSelectEvent.getTreeNode().getData();
			if (object instanceof Offer) {
				Offer offer = (Offer) object;
				if (offer.getOfferType() == OfferType.TEMPLATE) {
					if (offerDAO.countCompiledOfferOfTemp(offer) > 0) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public void reCompiledTemp(NodeSelectEvent nodeSelectEvent) {
		Map<String, Object> options = new HashMap<String, Object>();
		options.put("modal", true);
		options.put("width", 1000);
		options.put("height", 500);
		options.put("resizable", false);
		options.put("contentWidth", "100%");
		options.put("contentHeight", "100%");
		Map<String, List<String>> mapPara = new HashMap<String, List<String>>();
		List<String> lstPara = new ArrayList<String>();
		Offer offer = (Offer) nodeSelectEvent.getTreeNode().getData();
		lstPara.add(String.valueOf(offer.getOfferId()));
		mapPara.put("offerId", lstPara);
		RequestContext.getCurrentInstance().openDialog("/pages/offer/context_offer/re_compiled_from_temp.xhtml",
				options, mapPara);
	}

	public boolean showConvertToTemp(NodeSelectEvent nodeSelectEvent) {
		if (nodeSelectEvent != null) {
			Object object = nodeSelectEvent.getTreeNode().getData();
			if (object instanceof Offer) {
				Offer offer = (Offer) object;
				if (offer.getOfferType() == OfferType.NORMAL) {
					return true;
				}
			}
		}
		return false;
	}

	public void convertToTemp(NodeSelectEvent nodeSelectEvent) {
		Map<String, Object> options = new HashMap<String, Object>();
		options.put("modal", true);
		options.put("widgetVar", "dlgTree");
		options.put("width", 1000);
		options.put("height", 550);
		options.put("resizable", false);
		options.put("contentWidth", "100%");
		options.put("contentHeight", "100%");
		Map<String, List<String>> mapPara = new HashMap<String, List<String>>();
		List<String> lstPara = new ArrayList<String>();
		List<String> lstPara1 = new ArrayList<String>();
		Offer offer = (Offer) nodeSelectEvent.getTreeNode().getData();
		lstPara.add(String.valueOf(offer.getOfferId()));
		lstPara1.add("convertToTemp");
		mapPara.put("offerId", lstPara);
		mapPara.put("type", lstPara1);
		RequestContext.getCurrentInstance().openDialog("/pages/offer/context_offer/clone_offer_temp.xhtml", options,
				mapPara);
	}

	public boolean showTesting(NodeSelectEvent nodeSelectEvent) {
		if (nodeSelectEvent != null) {
			Object object = nodeSelectEvent.getTreeNode().getData();
			if (object instanceof Offer) {
				Offer offer = (Offer) object;
				if (offer.getState() == ContantsUtil.OfferState.IN_ACTIVE) {
					return true;
				}
			}
		}
		return false;
	}

	public void testing(NodeSelectEvent nodeSelectEvent) {
		Object object = nodeSelectEvent.getTreeNode().getData();
		if (object instanceof Offer) {
			Offer offer = (Offer) object;
			offer.setState(ContantsUtil.OfferState.TESTING);
			try {
				offerDAO.update(offer);
				Category category = categoryDAO.get(offer.getCategoryId());
				List<Action> actions = actionDAO.findActionByOffer(offer.getOfferId());
				super.getTreeOfferBean().updateTreeNodeOffer(offer, category, actions, false);

				if (offer.getOfferId() == this.offer.getOfferId()) {
					this.offer = offer.clone();
				}
				super.showNotification(FacesMessage.SEVERITY_INFO, "common.success", "");
			} catch (Exception e) {
				getLogger().warn(e.getMessage(), e);
				super.showNotification(FacesMessage.SEVERITY_ERROR, "common.fail", "common.failEx");
			}
		}
	}

	public boolean showUnTesting(NodeSelectEvent nodeSelectEvent) {
		if (nodeSelectEvent != null) {
			Object object = nodeSelectEvent.getTreeNode().getData();
			if (object instanceof Offer) {
				Offer offer = (Offer) object;
				if (offer.getState() == ContantsUtil.OfferState.TESTING) {
					return true;
				}
			}
		}
		return false;
	}

	public void unTesting(NodeSelectEvent nodeSelectEvent) {
		Object object = nodeSelectEvent.getTreeNode().getData();
		if (object instanceof Offer) {
			Offer offer = (Offer) object;
			offer.setState(ContantsUtil.OfferState.IN_ACTIVE);
			try {
				offerDAO.update(offer);
				Category category = categoryDAO.get(offer.getCategoryId());
				List<Action> actions = actionDAO.findActionByOffer(offer.getOfferId());
				super.getTreeOfferBean().updateTreeNodeOffer(offer, category, actions, false);

				if (offer.getOfferId() == this.offer.getOfferId()) {
					this.offer = offer.clone();
				}
				super.showNotification(FacesMessage.SEVERITY_INFO, "common.success", "");
			} catch (Exception e) {
				getLogger().warn(e.getMessage(), e);
				super.showNotification(FacesMessage.SEVERITY_ERROR, "common.fail", "common.failEx");
			}
		}
	}

	public boolean showDeploy(NodeSelectEvent nodeSelectEvent) {
		if (nodeSelectEvent != null) {
			Object object = nodeSelectEvent.getTreeNode().getData();
			if (object instanceof Offer) {
				Offer offer = (Offer) object;
				if (offer.getState() == ContantsUtil.OfferState.IN_ACTIVE && (offer.getOfferType() == OfferType.MAIN
						|| offer.getOfferType() == OfferType.COMPILED || offer.getOfferType() == OfferType.NORMAL
						|| offer.getOfferType() == OfferType.FELLOW_NUMBER
						|| offer.getOfferType() == OfferType.BLACKLIST || offer.getOfferType() == OfferType.FTTH
						|| offer.getOfferType() == OfferType.SERVICE)) {
					return true;
				}
			}
		}
		return false;
	}

	public void prepareDeployWith(NodeSelectEvent nodeSelectEvent) {
		Object object = nodeSelectEvent.getTreeNode().getData();
		if (object instanceof Offer) {
			selectedOffer = (Offer) object;
			offersForDeploy = offerDAO.findMoreVersionByOffer(selectedOffer);
			if (offersForDeploy.size() == 1) {
				RequestContext.getCurrentInstance().execute("PF('confirmation_normal_case_1_id').show();");
			} else {
				Offer activeOffer = null;
				for (Offer item : offersForDeploy) {
					if (item.getOfferId() != selectedOffer.getOfferId()
							&& item.getState() == ContantsUtil.OfferState.ACTIVE) {
						activeOffer = item;
						break;
					}
				}

				if (activeOffer == null) {
					RequestContext.getCurrentInstance().execute("PF('confirmation_normal_case_1_id').show();");
				} else {
					RequestContext.getCurrentInstance().execute("PF('confirmation_normal_case_3_id').show();");
				}
			}
		}
	}

	private Offer selectedOffer;
	private List<Offer> offersForDeploy;

	public void prepareDeployWithout(NodeSelectEvent nodeSelectEvent) {
		Object object = nodeSelectEvent.getTreeNode().getData();
		if (object instanceof Offer) {
			selectedOffer = (Offer) object;
			offersForDeploy = offerDAO.findMoreVersionByOffer(selectedOffer);
			if (offersForDeploy.size() == 1) {
				RequestContext.getCurrentInstance().execute("PF('confirmation_normal_case_1_id').show();");
			} else {
				Offer activeOffer = null;
				for (Offer item : offersForDeploy) {
					if (item.getOfferId() != selectedOffer.getOfferId()
							&& item.getState() == ContantsUtil.OfferState.ACTIVE) {
						activeOffer = item;
						break;
					}
				}

				if (activeOffer == null) {
					RequestContext.getCurrentInstance().execute("PF('confirmation_normal_case_1_id').show();");
				} else {
					RequestContext.getCurrentInstance().execute("PF('confirmation_normal_case_2_id').show();");
				}
			}
		}
	}

	public void doDeployWithoutCaseOne() {
		try {
			selectedOffer.setState(ContantsUtil.OfferState.ACTIVE);
			offerDAO.update(selectedOffer);
			Category category = categoryDAO.get(selectedOffer.getCategoryId());
			List<Action> actions = actionDAO.findActionByOffer(selectedOffer.getOfferId());
			super.getTreeOfferBean().updateTreeNodeOffer(selectedOffer, category, actions, false);

			if (selectedOffer.getOfferId() == this.offer.getOfferId()) {
				this.offer = selectedOffer.clone();
			}
			super.showNotification(FacesMessage.SEVERITY_INFO, "common.success", "");
		} catch (Exception e) {
			getLogger().warn(e.getMessage(), e);
			super.showNotification(FacesMessage.SEVERITY_ERROR, "common.fail", "common.failEx");
		}
	}

	public void doDeployWithoutCaseTwo() {
		if (offerDAO.doDeployWithoutCaseTwo(selectedOffer, offersForDeploy)) {
			for (Offer offer : offersForDeploy) {
				Category category = categoryDAO.get(offer.getCategoryId());
				List<Action> actions = actionDAO.findActionByOffer(offer.getOfferId());
				super.getTreeOfferBean().updateTreeNodeOffer(offer, category, actions, false);

				if (offer.getOfferId() == this.offer.getOfferId()) {
					try {
						this.offer = offer.clone();
					} catch (CloneNotSupportedException e) {
						getLogger().warn(e.getMessage(), e);
					}
				}
			}
			super.showNotification(FacesMessage.SEVERITY_INFO, "common.success", "");
		}
	}

	public void doDeployWithoutCaseThree() {
		if (offerDAO.doDeployWithoutCaseThree(selectedOffer, offersForDeploy)) {
			List<Offer> offers = offerDAO.findMoreVersionByOffer(selectedOffer);
			super.getTreeOfferBean().switchOfferDeploy(offers);

			for (Offer offer : offers) {
				if (offer.getOfferId() == this.offer.getOfferId()) {
					refreshOfferVersionPage(offer, null);
				}
			}

			super.showNotification(FacesMessage.SEVERITY_INFO, "common.success", "");
		}
	}

	public boolean showUndeploy(NodeSelectEvent nodeSelectEvent) {
		if (nodeSelectEvent != null) {
			Object object = nodeSelectEvent.getTreeNode().getData();
			if (object instanceof Offer) {
				Offer offer = (Offer) object;
				if (offer.getState() == ContantsUtil.OfferState.ACTIVE) {
					return true;
				}
			}
		}
		return false;
	}

	public void undeploy(NodeSelectEvent nodeSelectEvent) {
		Object object = nodeSelectEvent.getTreeNode().getData();
		if (object instanceof Offer) {
			Offer offer = (Offer) object;
			offer.setState(ContantsUtil.OfferState.IN_ACTIVE);
			try {
				offerDAO.update(offer);
				Category category = categoryDAO.get(offer.getCategoryId());
				List<Action> actions = actionDAO.findActionByOffer(offer.getOfferId());
				super.getTreeOfferBean().updateTreeNodeOffer(offer, category, actions, false);

				if (offer.getOfferId() == this.offer.getOfferId()) {
					this.offer = offer.clone();
				}
				super.showNotification(FacesMessage.SEVERITY_INFO, "common.success", "");
			} catch (Exception e) {
				getLogger().warn(e.getMessage(), e);
				super.showNotification(FacesMessage.SEVERITY_ERROR, "common.fail", "common.failEx");
			}
		}
	}

	public boolean showActive(NodeSelectEvent nodeSelectEvent) {
		if (nodeSelectEvent != null) {
			Object object = nodeSelectEvent.getTreeNode().getData();
			if (object instanceof Offer) {
				Offer offer = (Offer) object;
				if (offer.getState() == ContantsUtil.OfferState.SUSPEND) {
					return true;
				}
			}
		}
		return false;
	}

	public void active(NodeSelectEvent nodeSelectEvent) {

		Object object = nodeSelectEvent.getTreeNode().getData();
		if (object instanceof Offer) {
			selectedOffer = (Offer) object;
			offersForDeploy = offerDAO.findMoreVersionByOffer(selectedOffer);
			if (offersForDeploy.size() == 1) {
				RequestContext.getCurrentInstance().execute("PF('confirmActiveOffer').show();");
			} else {
				Offer activeOffer = null;
				for (Offer item : offersForDeploy) {
					if (item.getOfferId() != selectedOffer.getOfferId()
							&& item.getState() == ContantsUtil.OfferState.ACTIVE) {
						activeOffer = item;
						break;
					}
				}

				if (activeOffer == null) {
					offer.setState(ContantsUtil.OfferState.ACTIVE);
					try {
						offerDAO.update(offer);
						Category category = categoryDAO.get(offer.getCategoryId());
						List<Action> actions = actionDAO.findActionByOffer(offer.getOfferId());
						super.getTreeOfferBean().updateTreeNodeOffer(offer, category, actions, false);

						if (offer.getOfferId() == this.offer.getOfferId()) {
							this.offer = offer.clone();
						}
						super.showNotification(FacesMessage.SEVERITY_INFO, "common.success", "");
					} catch (Exception e) {
						getLogger().warn(e.getMessage(), e);
						super.showNotification(FacesMessage.SEVERITY_ERROR, "common.fail", "common.failEx");
					}
				} else {
					RequestContext.getCurrentInstance().execute("PF('confirmation_normal_case_2_id').show();");
				}
			}
		}
	}

	public void activeOneVersion() {
		selectedOffer.setState(ContantsUtil.OfferState.ACTIVE);
		try {
			offerDAO.update(selectedOffer);
			Category category = categoryDAO.get(selectedOffer.getCategoryId());
			super.getTreeOfferBean().updateTreeNodeOffer(selectedOffer, category, null, false);

			if (selectedOffer.getOfferId() == this.offer.getOfferId()) {
				this.offer = selectedOffer.clone();
			}
			super.showNotification(FacesMessage.SEVERITY_INFO, "common.success", "");
		} catch (Exception e) {
			getLogger().warn(e.getMessage(), e);
			super.showNotification(FacesMessage.SEVERITY_ERROR, "common.fail", "common.failEx");
		}
	}

	public boolean showSuspend(NodeSelectEvent nodeSelectEvent) {
		if (nodeSelectEvent != null) {
			Object object = nodeSelectEvent.getTreeNode().getData();
			if (object instanceof Offer) {
				Offer offer = (Offer) object;
				if (offer.getState() == ContantsUtil.OfferState.ACTIVE) {
					return true;
				}
			}
		}
		return false;
	}

	public void suspend(NodeSelectEvent nodeSelectEvent) {
		Object object = nodeSelectEvent.getTreeNode().getData();
		if (object instanceof Offer) {
			Offer offer = (Offer) object;
			offer.setState(ContantsUtil.OfferState.SUSPEND);
			try {
				offerDAO.update(offer);
				Category category = categoryDAO.get(offer.getCategoryId());
				List<Action> actions = actionDAO.findActionByOffer(offer.getOfferId());
				super.getTreeOfferBean().updateTreeNodeOffer(offer, category, actions, false);

				if (offer.getOfferId() == this.offer.getOfferId()) {
					this.offer = offer.clone();
				}
				super.showNotification(FacesMessage.SEVERITY_INFO, "common.success", "");
			} catch (Exception e) {
				getLogger().warn(e.getMessage(), e);
				super.showNotification(FacesMessage.SEVERITY_ERROR, "common.fail", "common.failEx");
			}
		}
	}

	public boolean showTemporatyDelete(NodeSelectEvent nodeSelectEvent) {
		if (nodeSelectEvent != null) {
			Object object = nodeSelectEvent.getTreeNode().getData();
			if (object instanceof Offer) {
				Offer offer = (Offer) object;
				if (offer.getState() == ContantsUtil.OfferState.SUSPEND
						|| offer.getState() == ContantsUtil.OfferState.IN_ACTIVE) {
					return true;
				}
			}
		}
		return false;
	}

	public void temporatyDelete(NodeSelectEvent nodeSelectEvent) {
		Object object = nodeSelectEvent.getTreeNode().getData();
		if (object instanceof Offer) {
			Offer offer = (Offer) object;
			offer.setState(ContantsUtil.OfferState.REMOVED);
			try {
				offerDAO.update(offer);
				// Category category = categoryDAO.get(offer.getCategoryId());
				// List<Action> actions =
				// actionDAO.findActionByOffer(offer.getOfferId());
				super.getTreeOfferBean().removeTreeNodeAll(offer);

				if (offer.getOfferId() == this.offer.getOfferId()) {
					this.offer = offer.clone();
				}
				super.showNotification(FacesMessage.SEVERITY_INFO, "common.success", "");
			} catch (Exception e) {
				getLogger().warn(e.getMessage(), e);
				super.showNotification(FacesMessage.SEVERITY_ERROR, "common.fail", "common.failEx");
			}
		}
	}

	public boolean showRecover(Offer offer) {
		if (offer.getState() == ContantsUtil.OfferState.REMOVED)
			return true;
		return false;
	}

	public boolean showRecover(NodeSelectEvent nodeSelectEvent) {
		if (nodeSelectEvent != null) {
			Object object = nodeSelectEvent.getTreeNode().getData();
			if (object instanceof Offer) {
				Offer offer = (Offer) object;
				return this.showRecover(offer);
			}
		}
		return false;
	}

	public void recover(NodeSelectEvent nodeSelectEvent) {
		Object object = nodeSelectEvent.getTreeNode().getData();
		if (object instanceof Offer) {
			Offer offer = (Offer) object;
			this.recover(offer);
		}
	}

	public void recover(Offer offer) {
		offer.setState(ContantsUtil.OfferState.IN_ACTIVE);
		try {
			offerDAO.update(offer);
			Category category = categoryDAO.get(offer.getCategoryId());
			List<Action> actions = actionDAO.findActionByOffer(offer.getOfferId());
			super.getTreeOfferBean().updateTreeNodeOffer(offer, category, actions, true);

			if (offer.getOfferId() == this.offer.getOfferId()) {
				this.offer = offer.clone();
			}
			super.showNotification(FacesMessage.SEVERITY_INFO, "common.success", "");
		} catch (Exception e) {
			getLogger().warn(e.getMessage(), e);
			super.showNotification(FacesMessage.SEVERITY_ERROR, "common.fail", "common.failEx");
		}
	}

	public void removeOfferGroup(NodeSelectEvent nodeSelectEvent) {
		Object object = nodeSelectEvent.getTreeNode().getData();
		OfferDump offerGroup = (OfferDump) object;
		removeOfferGroup(offerGroup);
	}

	public void removeOfferGroup(OfferDump offerDump) {
		List<Offer> offers = offerDAO.findByConditions(new String[] { "offerExternalId", "offerName" },
				new Operator[] { Operator.LIKE, Operator.LIKE },
				new Object[] { offerDump.getOfferExternalId(), offerDump.getOfferName() }, "versionInfo");
		if (offers.size() > 1) {
			showNotification(FacesMessage.SEVERITY_WARN, "common.fail", "offer.msg.cant_delete_more_version");
			return;
		} else {
			if (offers.get(0).getState() != OfferState.IN_ACTIVE && offers.get(0).getState() != OfferState.REMOVED) {
				showNotification(FacesMessage.SEVERITY_WARN, "common.fail", "offer.msg.cant_delete_2");
				return;
			} else {
				removeOfferVersion(offers.get(0));
			}
		}

		// if (offerDAO.deleteOffers(offerDump)) {
		// for (OfferDump dump : listOfferByCat) {
		// if (dump.getOfferExternalId().equals(offerDump.getOfferExternalId()))
		// {
		// listOfferByCat.remove(dump);
		// break;
		// }
		// }
		// clearFilter();
		// this.showMessageINFO("common.delete", " offer ");
		// for (Offer offer : offers) {
		// super.getTreeOfferBean().removeTreeNodeAll(offer);
		// }
		// } else {
		// this.showMessageERROR("common.delete", " offer ");
		// }
	}

	private OfferDump offerGroupSelected;

	public void moveToCategory(NodeSelectEvent nodeSelectEvent) {
		Object object = nodeSelectEvent.getTreeNode().getData();
		if (object instanceof OfferDump) {
			OfferDump offerDump = (OfferDump) object;
			offerGroupSelected = offerDump;
			Category category = categoryDAO.get(offerDump.getCategoryId());
			super.openTreeCategoryDialog(category.getTreeType(), "folder", category.getCategoryType());
		}
	}

	public void onMoveToDialogReturn(SelectEvent event) {
		Object object = event.getObject();
		if (object instanceof Category) {
			Category category = (Category) object;
			if (category.getCategoryId() != 0L) {
				offerGroupSelected.setCategoryId(category.getCategoryId());
				List<Offer> offers = offerDAO.findByConditions(new String[] { "offerExternalId", "offerName" },
						new Operator[] { Operator.LIKE, Operator.LIKE },
						new Object[] { offerGroupSelected.getOfferExternalId(), offerGroupSelected.getOfferName() },
						"versionInfo");
				if (offerDAO.changeNameAndExtIdVersion(offers, offerGroupSelected)) {
					for (Offer offer : offers) {
						offer.setOfferName(offerGroupSelected.getOfferName().trim());
						offer.setOfferExternalId(offerGroupSelected.getOfferExternalId().trim());
						offer.setCategoryId(offerGroupSelected.getCategoryId());
						List<Action> actions = actionDAO.findActionByOffer(offer.getOfferId());
						super.getTreeOfferBean().updateTreeNodeOffer(offer, category, actions, false);
					}

					if (offerGroupSelected.getOfferExternalId().equalsIgnoreCase(offerGroup.getOfferExternalId())) {
						refreshOfferGroup(offerGroupSelected);
					}
					super.showNotification(FacesMessage.SEVERITY_INFO, "common.success", "");
				}
			}
		}
	}

	public void onCloneDialogReturn(SelectEvent event) {
		Object object = event.getObject();
		if (object instanceof Offer) {
			Offer offer = (Offer) object;
			Category category = categoryDAO.get(offer.getCategoryId());
			List<Action> actions = actionDAO.findActionByOffer(offer.getOfferId());
			super.getTreeOfferBean().updateTreeNodeOffer(offer, category, actions, false);
			if (offer.getOfferExternalId().equals(this.offerGroup.getOfferExternalId())) {
				offerVersions.add(offer);
			}
			super.showNotificationSuccsess();
		} else if (object instanceof List) {
			List<?> list = (List<?>) object;
			if (list.size() > 0 && list.get(0) instanceof OfferRecompiledModel) {
				for (Object item : list) {
					OfferRecompiledModel offerRecompiledModel = (OfferRecompiledModel) item;
					Offer offer = offerRecompiledModel.getOffer();
					Category category = categoryDAO.get(offer.getCategoryId());
					List<Action> actions = actionDAO.findActionByOffer(offer.getOfferId());
					super.getTreeOfferBean().updateTreeNodeOffer(offer, category, actions, false);
				}
			}
			super.showNotificationSuccsess();
		}
	}

	public void onReCompileDialogReturn(SelectEvent event) {
		Object object = event.getObject();
		if (object instanceof Offer) {
			Offer offer = (Offer) object;
			Category category = categoryDAO.get(offer.getCategoryId());
			List<Action> actions = actionDAO.findActionByOffer(offer.getOfferId());
			super.getTreeOfferBean().updateTreeNodeOffer(offer, category, actions, false);
			if (offer.getOfferExternalId().equals(this.offerGroup.getOfferExternalId())) {
				offerVersions.add(offer);
			}
			showNotification(FacesMessage.SEVERITY_INFO, "common.success", "");
		} else if (object instanceof List) {
			List<OfferRecompiledModel> recompiledModels = (List<OfferRecompiledModel>) object;
			for (OfferRecompiledModel offerModel : recompiledModels) {

				if (!offerModel.isSelected()) {
					continue;
				}

				Offer offerToComp = offerModel.getOffer();
				Category category = categoryDAO.get(offerToComp.getCategoryId());
				List<Action> actions = actionDAO.findActionByOffer(offerToComp.getOfferId());
				super.getTreeOfferBean().updateTreeNodeOffer(offerToComp, category, actions, false);
			}
		}
	}

	public boolean showEditOffer(NodeSelectEvent nodeSelectEvent) {
		if (nodeSelectEvent != null) {
			Object object = nodeSelectEvent.getTreeNode().getData();
			if (object instanceof OfferDump) {
				OfferDump dump = (OfferDump) object;
				Offer offer = offerDAO.findJustOneOfferOfferDump(dump);
				if (offer != null && offer.getOfferType() != OfferType.COMPILED) {
					return true;
				}
			}
		}
		return false;
	}

	public void editOffer(NodeSelectEvent nodeSelectEvent) {
		super.getTreeOfferBean().onNodeSelect(nodeSelectEvent);
		this.editable = true;

	}

	public void deleteUpgrateOffer() {
		this.offerUpgrade = new Offer();
		this.offerUpgrade.setVersionName(true);
		this.offer.setOfferUpgradeId(null);
	}

	public void deleteDowngrateOffer() {
		this.offerDowngrade = new Offer();
		this.offerDowngrade.setVersionName(true);
		this.offer.setOfferDowngradeId(null);
	}

	/************ CONTEXT MENU - END ************/

	/** GET-SET **/
	public Offer getOffer() {
		return offer;
	}

	public void setOffer(Offer offer) {
		this.offer = offer;
	}

	public String getFormType() {
		return formType;
	}

	public void setFormType(String formType) {
		this.formType = formType;
	}

	public List<OfferDump> getListOfferByCat() {
		return listOfferByCat;
	}

	public void setListOfferByCat(List<OfferDump> listOfferByCat) {
		this.listOfferByCat = listOfferByCat;
	}

	public List<Category> getLstCategory() {
		return lstCategory;
	}

	public void setLstCategory(List<Category> lstCategory) {
		this.lstCategory = lstCategory;
	}

	public Map<Long, String> getMapPaymentType() {
		return mapPaymentType;
	}

	public void setMapPaymentType(Map<Long, String> mapPaymentType) {
		this.mapPaymentType = mapPaymentType;
	}

	public Map<Long, String> getMapOfferState() {
		return mapOfferState;
	}

	public void setMapOfferState(Map<Long, String> mapOfferState) {
		this.mapOfferState = mapOfferState;
	}

	public Map<Long, String> getMapOfferType() {
		return mapOfferType;
	}

	public void setMapOfferType(Map<Long, String> mapOfferType) {
		this.mapOfferType = mapOfferType;
	}

	public List<Action> getActions() {
		return actions;
	}

	public void setActions(List<Action> actions) {
		this.actions = actions;
	}

	public List<Offer> getOfferVersions() {
		return offerVersions;
	}

	public void setOfferVersions(List<Offer> offerVersions) {
		this.offerVersions = offerVersions;
	}

	public List<RedirectAddress> getRedirectAddresses() {
		return redirectAddresses;
	}

	public void setRedirectAddresses(List<RedirectAddress> redirectAddresses) {
		this.redirectAddresses = redirectAddresses;
	}

	public List<Offer> getNormOffer() {
		return normOffer;
	}

	public void setNormOffer(List<Offer> normOffer) {
		this.normOffer = normOffer;
	}

	public OfferPackage getOfferPackage() {
		return offerPackage;
	}

	public void setOfferPackage(OfferPackage offerPackage) {
		this.offerPackage = offerPackage;
	}

	public List<OfferpkgOfferMapModel> getNormOfferOfPackage() {
		return normOffersOfPackage;
	}

	public void setNormOfferOfPackage(List<OfferpkgOfferMapModel> normOfferOfPackage) {
		this.normOffersOfPackage = normOfferOfPackage;
	}

	public List<OfferpkgOfferMapModel> getNormOfferpkgOfferMaps() {
		return offerPackagesOfOffer;
	}

	public void setNormOfferpkgOfferMaps(List<OfferpkgOfferMapModel> normOfferpkgOfferMaps) {
		this.offerPackagesOfOffer = normOfferpkgOfferMaps;
	}

	public Offer getOfferUpgrate() {
		return offerUpgrade;
	}

	public void setOfferUpgrate(Offer offerUpgrate) {
		this.offerUpgrade = offerUpgrate;
	}

	public Offer getOfferDowngrade() {
		return offerDowngrade;
	}

	public void setOfferDowngrade(Offer offerDowngrade) {
		this.offerDowngrade = offerDowngrade;
	}

	public List<BillingCycleType> getbillingCycleTypes() {
		return billingCycleTypeDAO.findAll("");
	}

	public OfferDump getOfferGroup() {
		return offerGroup;
	}

	public void setOfferGroup(OfferDump offerGroup) {
		this.offerGroup = offerGroup;
	}

	public List<PrameterModel> getPrameterModels() {
		return prameterModels;
	}

	public void setPrameterModels(List<PrameterModel> prameterModels) {
		this.prameterModels = prameterModels;
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

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public Offer getOfferToConvert() {
		return offerToConvert;
	}

	public void setOfferToConvert(Offer offerToConvert) {
		this.offerToConvert = offerToConvert;
	}

	public Offer getOfferTemp() {
		return offerTemp;
	}

	public void setOfferTemp(Offer offerTemp) {
		this.offerTemp = offerTemp;
	}

	public Category getCategory() {
		return selectedCategory;
	}

	public void setCategory(Category category) {
		this.selectedCategory = category;
	}

	public List<Category> getCategoriesForConvert() {
		return categoriesForConvert;
	}

	public void setCategoriesForConvert(List<Category> categoriesForConvert) {
		this.categoriesForConvert = categoriesForConvert;
	}

}

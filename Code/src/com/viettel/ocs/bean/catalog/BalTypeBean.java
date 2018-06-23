package com.viettel.ocs.bean.catalog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;

import com.viettel.ocs.bean.BaseController;
import com.viettel.ocs.bean.TreeCommonBean;
import com.viettel.ocs.constant.CategoryType;
import com.viettel.ocs.constant.ContantsUtil;
import com.viettel.ocs.constant.Normalizer;
import com.viettel.ocs.constant.TreeType;
import com.viettel.ocs.dao.BalTypeDAO;
import com.viettel.ocs.dao.BillingCycleTypeDAO;
import com.viettel.ocs.dao.CategoryDAO;
import com.viettel.ocs.dao.MapAcmbalBalDAO;
import com.viettel.ocs.dao.MapSharebalBalDAO;
import com.viettel.ocs.dao.ThresholdDAO;
import com.viettel.ocs.dao.TriggerOcsDAO;
import com.viettel.ocs.dao.TriggerTypeDAO;
import com.viettel.ocs.dao.UnitTypeDAO;
import com.viettel.ocs.entity.BalType;
import com.viettel.ocs.entity.BillingCycleType;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.Threshold;
import com.viettel.ocs.entity.TriggerOcs;
import com.viettel.ocs.entity.UnitType;
import com.viettel.ocs.model.ThresHoldTrigerModel;
import com.viettel.ocs.util.ValidateUtil;

@ManagedBean(name = "balTypeBean", eager = true)
@ViewScoped
public class BalTypeBean extends BaseController implements Serializable {

	private static final long serialVersionUID = 1L;
	private String formType = "";
	private BalTypeDAO balTypeDAO;
	private Category category;
	private CategoryDAO categoryDAO;

	private List<SelectItem> listTypeOfBalType;
	private List<SelectItem> listPaymentType;
	private List<SelectItem> listEffDateType;
	private List<SelectItem> listExpDateType;
	private List<SelectItem> listPeriodicType;
	private List<SelectItem> listBalLevelType;
	private List<SelectItem> listComboUnitType;
	private List<BillingCycleType> listComboBillCycleType;
	private List<SelectItem> listComboCategory;
	private BalType balType;
	private List<BalType> listBalTypeByCategory;
	private boolean isEditting;
	private List<UnitType> listComboBTPrecision;
	private TriggerOcs triggerOcs;
	private boolean isApply;

	private UnitTypeDAO unitTypeDAO;
	private UnitType unitType;
	private List<UnitType> lstUnitType;

	// Threshold-----------------------------------------------
	private ThresHoldTrigerModel threshold;
	private List<ThresHoldTrigerModel> thresholds;
	private ThresholdDAO thresholdDAO;
	private TriggerOcsDAO triggerOcsDAO;
	private Double pattern;
	private Double thresHoldValue;

	// Init -----------------------------------------------------
	@PostConstruct
	public void init() {
		this.isApply = true;
		this.isEditting = true;
		this.category = new Category();
		this.categoryDAO = new CategoryDAO();
		this.balTypeDAO = new BalTypeDAO();
		this.balType = new BalType();
		this.listBalTypeByCategory = new ArrayList<BalType>();
		this.listComboBTPrecision = new ArrayList<UnitType>();

		// Threshold
		this.threshold = new ThresHoldTrigerModel();
		this.thresholds = new ArrayList<ThresHoldTrigerModel>();
		this.thresholdDAO = new ThresholdDAO();
		this.triggerOcsDAO = new TriggerOcsDAO();
		this.balType.setIsAcm(false);
		this.triggerOcs = new TriggerOcs();

		this.unitTypeDAO = new UnitTypeDAO();
		this.unitType = new UnitType();
		this.lstUnitType = new ArrayList<UnitType>();
		this.pattern = 1D;
		this.thresHoldValue = 0D;
	}

	public String getAbbreviation(Long unitTypeId) {
		if (balType.getUnitTypeId() != null) {
			unitType = unitTypeDAO.get(balType.getUnitTypeId());
			unitTypeId = balType.getUnitTypeId();
			String abbreviation = unitTypeDAO.get(unitTypeId).getAbbreviation();
			return abbreviation;
		}
		return null;
	}

//	public String getThresHoldValue(String value) {
//		if (value != null && unitType != null) {
//			if (unitType.getAbbreviation() == null || unitType.getAbbreviation() == ""
//					|| unitType.getAbbreviation().isEmpty()) {
//				return value;
//			} else {
//				return value + "(" + unitType.getAbbreviation() + ")";
//			}
//		} else {
//			return value;
//		}
//	}
	
	public String getThresHoldValueTB(Long value) {
		if (value != null && unitType != null) {
			Double result = value / (double) unitType.getDisplayRate();
			result = (double) Math.round(result * pattern) / pattern;
			
			if (unitType.getAbbreviation() == null || unitType.getAbbreviation() == ""
					|| unitType.getAbbreviation().isEmpty()) {
				return result.toString();
			} else {
				return result + "(" + unitType.getAbbreviation() + ")";
			}
		} else {
			return value.toString();
		}
	}

	public void refreshThresHold(Threshold threshold) {
		formType = "threshold-detail";
		List<TriggerOcs> triggerOcs = triggerOcsDAO.findTriggerOcsByThreshold(threshold.getThresholdId());
		this.threshold = new ThresHoldTrigerModel(threshold, triggerOcs);
		super.getTreeCommonBean().hideAllCategoryComponent();
	}

	/***** THRESHOLD *****/

	public void showDialogThreshold(ThresHoldTrigerModel threshold, boolean isClone) {
		if (threshold == null) {
			this.threshold = new ThresHoldTrigerModel();

			if (this.balType.getUnitTypeId() == null && !this.lstUnitType.isEmpty()) {
				this.unitType = this.lstUnitType.get(0);
				this.balType.setUnitTypeId(unitType.getUnitTypeId());
			}
		} else {
			this.threshold = threshold.clone();
			this.threshold.setIndex(isClone ? null : thresholds.indexOf(threshold));
		}
		calThresHoldValue();
		getAbbreviation(balType.getUnitTypeId());
		this.threshold.cal();
		RequestContext context = RequestContext.getCurrentInstance();
		context.execute("PF('dlgThresholdWV').show();");
	}

	private Double genPattern(Long pre) {
		String pattern = "1";
		for (int i = 0; i < pre; i++) {
			pattern += "0";
		}
		return Double.valueOf(pattern);
	}

	public void calThresHoldValue() {
		if (unitType != null) {
			thresHoldValue = threshold.getThreshold().getValue() / (double) unitType.getDisplayRate();
			thresHoldValue = (double) Math.round(thresHoldValue * pattern) / pattern;
		} else {
			thresHoldValue = threshold.getThreshold().getValue().doubleValue();
		}
	}

	public void chooseThresHoldValue() {
		if (unitType != null) {
			threshold.getThreshold().setValue(Math.round((thresHoldValue * unitType.getDisplayRate())));
		} else {
			threshold.getThreshold().setValue(thresHoldValue.longValue());
		}
	}

	// public void showDialogTrigger() {
	// super.openTreeCommonDialog(TreeType.CATALOG_TRIGGER_OCS,
	// CategoryType.CTL_TO_TRIGGER_OCS_NAME,
	// CategoryType.CTL_TO_TRIGGER_OCS, false);
	// }
	//
	// public void onDialogReturn(SelectEvent event) {
	// Object obj = event.getObject();
	// if (obj instanceof TriggerOcs) {
	// TriggerOcs triggerOcs = (TriggerOcs) obj;
	// if (!exitTrigger(triggerOcs)) {
	// threshold.getTriggerOcsofThresHold().add(triggerOcs);
	// }
	// }
	// }

	private boolean exitTrigger(TriggerOcs triggerOcsInput) {
		for (TriggerOcs triggerOcs : threshold.getTriggerOcsofThresHold()) {
			if (triggerOcs.getTriggerOcsId() == triggerOcsInput.getTriggerOcsId()) {
				return true;
			}
		}
		return false;
	}

	public void chooseTrigger(TriggerOcs item) {
		List<Long> lstId = new ArrayList<Long>();
		if (threshold.getTriggerOcsofThresHold() != null) {
			for (TriggerOcs to : threshold.getTriggerOcsofThresHold()) {
				lstId.add(to.getTriggerOcsId());
			}
		}
		if (item == null || item.getTriggerOcsId() <= 0) {
			super.openTreeCommonDialog(TreeType.CATALOG_TRIGGER_OCS, readProperties("triggerOcs"),
					CategoryType.CTL_TO_TRIGGER_OCS, true, lstId);
		} else {
			super.openTreeCommonDialog(TreeType.CATALOG_TRIGGER_OCS, readProperties("triggerOcs"),
					CategoryType.CTL_TO_TRIGGER_OCS, false, lstId);
		}
		triggerOcs = item;
	}

	public void commandAddNewTriggerOcs() {
		TriggerOcs itemNew = new TriggerOcs();
		chooseTrigger(itemNew);
	}

	public void onDialogTriggerReturn(SelectEvent event) {
		Object[] objArr = new Object[1];
		if (event.getObject() instanceof Object[]) {
			objArr = (Object[]) event.getObject();
		} else {
			objArr[0] = event.getObject();
		}
		for (Object obj : objArr) {
			if (obj instanceof TriggerOcs) {
				TriggerOcs triggerOcs = (TriggerOcs) obj;
				if (!exitTrigger(triggerOcs)) {
					threshold.getTriggerOcsofThresHold().add(triggerOcs);
				} else {
					this.showMessageWARN("", "Trigger", this.readProperties("common.nameAlreadyExists"));
				}
			}
		}
	}

	public List<ThresHoldTrigerModel> loadListThresholdByBalType(Long balTypeId) {
		List<ThresHoldTrigerModel> thresholds = new ArrayList<ThresHoldTrigerModel>();
		thresholds = thresholdDAO.findThresholdByBalType(balTypeId);
		this.fillListTriggerToThreshold(thresholds);
		return thresholds;
	}

	private void fillListTriggerToThreshold(List<ThresHoldTrigerModel> thresholds) {
		for (ThresHoldTrigerModel threshold : thresholds) {
			threshold.setTriggerOcsofThresHold(
					triggerOcsDAO.findTriggerOcsByThreshold(threshold.getThreshold().getThresholdId()));
		}

	}

	public void cmdApplyThreshold() {
		threshold.convertToThresholdType();
		if (validateThreshold()) {
			if (threshold.getThreshold().getThresholdType() != null) {
				if (threshold.getIndex() != null) {
					thresholds.set(threshold.getIndex(), threshold);
				} else {
					thresholds.add(threshold);
				}

				if (this.balType.getUnitTypeId() == null && !this.lstUnitType.isEmpty()) {
					for (int i = 0; i < lstUnitType.size(); i++) {
						this.unitType = this.lstUnitType.get(i);
						this.balType.setUnitTypeId(unitType.getUnitTypeId());
					}
				}
				chooseThresHoldValue();
				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("PF('dlgThresholdWV').hide();");
			}
		}
	}

	public void clsoeApplyThreshold() {
		this.threshold = new ThresHoldTrigerModel();
	}

	// Validate
	private boolean validateThreshold() {
		boolean result = true;
		if (ValidateUtil.checkStringNullOrEmpty(threshold.getThreshold().getThresholdName())) {
			this.showMessageWARN("threshold.thresholdName", super.readProperties("validate.checkValueNameNull"));
			result = false;
		} else if (ValidateUtil.checkStringNullOrEmpty(threshold.getThreshold().getExternalId())) {
			this.showMessageWARN("common.externalId", super.readProperties("validate.checkValueNameNull"));
			result = false;
		} else if (threshold.getThreshold().getValue() == null) {
			this.showMessageWARN("common.value", super.readProperties("validate.checkValueNameNull"));
			result = false;
		} else if (threshold.getThreshold().getThresholdType() == null) {
			this.showMessageWARN("threshold.thresholdType", super.readProperties("validate.fieldNull"));
			result = false;
		} else if (thresholdDAO.checkExistedName(threshold, balType.getBalTypeId())
				|| checkExistedNameTable(threshold)) {
			this.showMessageWARN("threshold.thresholdName", super.readProperties("validate.checkValueNameExist"));
			return false;
		} else if (thresholdDAO.checkExistedEx(threshold, balType.getBalTypeId()) || checkExistedExTable(threshold)) {
			this.showMessageWARN("common.externalId", super.readProperties("validate.checkValueNameExist"));
			return false;
		}

		return result;
	}

	private boolean checkExistedExTable(ThresHoldTrigerModel threshold) {
		for (ThresHoldTrigerModel holdTrigerModel : thresholds) {
			if (threshold.getIndex() != null) {
				if (threshold.equals(holdTrigerModel.getIndex()) && holdTrigerModel.getThreshold().getExternalId()
						.equals(threshold.getThreshold().getExternalId())) {
					return true;
				}
			} else {
				if (holdTrigerModel.getThreshold().getExternalId().equals(threshold.getThreshold().getExternalId())) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean checkExistedNameTable(ThresHoldTrigerModel threshold) {
		for (ThresHoldTrigerModel holdTrigerModel : thresholds) {
			if (threshold.getIndex() != null) {
				if (threshold.equals(holdTrigerModel.getIndex()) && holdTrigerModel.getThreshold().getThresholdName()
						.equals(threshold.getThreshold().getThresholdName())) {
					return true;
				}
			} else {
				if (holdTrigerModel.getThreshold().getThresholdName()
						.equals(threshold.getThreshold().getThresholdName())) {
					return true;
				}
			}
		}
		return false;
	}

	public void deleteThreshold(ThresHoldTrigerModel item) {
		thresholds.remove(item);
	}

	public void cloneThreshold(ThresHoldTrigerModel threshold) {
		try {
			ThresHoldTrigerModel thresholdToClone = threshold.clone();
			thresholdToClone.setThreshold(thresholdToClone.getThreshold().clone());
			thresholdToClone.getThreshold().setThresholdId(0L);
			thresholdToClone
					.setTriggerOcsofThresHold(new ArrayList<TriggerOcs>(thresholdToClone.getTriggerOcsofThresHold()));
			// thresholdToClone.getTriggerOcsofThresHold().clear();

			showDialogThreshold(thresholdToClone, true);
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			getLogger().warn(e.getMessage(), e);
		}

	}

	public String getTriggerTypeName(Long triggerTypeId) {
		TriggerTypeDAO triggerTypeDAO = new TriggerTypeDAO();
		return triggerTypeDAO.get(triggerTypeId).getTriggerTypeName();
	}

	public Long getBTPrecision(Long unitTypeId) {
		if (balType.getUnitTypeId() != null) {
			UnitTypeDAO unitTypeDAO = new UnitTypeDAO();
			unitTypeId = balType.getUnitTypeId();
			Long precision = unitTypeDAO.get(unitTypeId).getUnitPrecision();
			balType.setBalTypepercision(precision);
		}
		return null;
	}

	public String getThresholdTypeName(Long thresholdId) {
		if (thresholdDAO.get(thresholdId) != null && thresholdDAO.get(thresholdId).getThresholdType() != null) {
			if (thresholdDAO.get(thresholdId).getThresholdType() == 0L) {
				return readProperties("threshold.thresholdTypeInc");
			}
			if (thresholdDAO.get(thresholdId).getThresholdType() == 1L) {
				return readProperties("threshold.thresholdTypeDec");
			}
			if (thresholdDAO.get(thresholdId).getThresholdType() == 2L) {
				return readProperties("threshold.thresholdTypeDandI");
			}
		}
		return null;
	}

	/***** TRIGGER OCS *****/

	public void deleteTriggerOcs(TriggerOcs item) {
		threshold.getTriggerOcsofThresHold().remove(item);
	}

	/***** BALANCES *****/
	public void changeVisible() {
		this.isEditting = false;
	}

	public void refreshCategories(Category category) {
		setFormType("list-baltype-by-category");
		this.category = category;
		loadBalTypeByCategory(category.getCategoryType());
		// this.listBalTypeByCategory =
		// balTypeDAO.findBTByConditions(category.getCategoryId());
		loadBalTypeByCategory(category.getCategoryId());
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.execute("$('.balTypeClearFilter').click();");
	}

	public void refreshBalType(BalType item) {
		try {
			this.balType = item.clone();
			if (this.balType.getExpDateData() == null) {
				this.balType.setExpDateData(1L);
			}

			if (this.balType.getEffDateData() == null) {
				this.balType.setEffDateData(1L);
			}
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			getLogger().warn(e.getMessage(), e);
		}
		setFormType("baltype-detail");
		this.thresholds = loadListThresholdByBalType(balType.getBalTypeId());
		this.isEditting = true;
		this.isApply = true;
		category = categoryDAO.get(balType.getCategoryId());
		LoadCategoriesOfBalType(category.getCategoryType());
		loadComboBalTypePrecision();
		loadComboListBillCycleType();
		loadListUnitType();

		if (balType.getUnitTypeId() != null) {
			unitType = unitTypeDAO.get(balType.getUnitTypeId());
			getAbbreviation(balType.getUnitTypeId());
		}

		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.update("form-baltype-detail:DTThreshold");
	}

	public void setDefaultValue() {
		this.balType.setEffDateType(0L);
		this.balType.setExpDateType(2l);

		if (balType.getExpDateType() == null) {
			balType.setExpDate(null);
		} else {
			balType.setExpDate(new Date());
		}
		if (balType.getEffDateType() == null) {
			balType.setEffDate(null);
		} else {
			balType.setEffDate(new Date());
		}

		if (balType.getExpDateType() != null) {
			if (balType.getExpDateType() == ContantsUtil.ExpireDateType.HOURS
					|| balType.getExpDateType() == ContantsUtil.ExpireDateType.DAYS
					|| balType.getExpDateType() == ContantsUtil.ExpireDateType.WEEKS
					|| balType.getExpDateType() == ContantsUtil.ExpireDateType.MONTHS
					|| balType.getExpDateType() == ContantsUtil.ExpireDateType.YEARS) {
				balType.setExpDateData(1L);
			}
		}

		if (balType.getEffDateType() != null) {
			if (balType.getEffDateType() == ContantsUtil.EffectDateType.HOURS_FROM_START_OF_DAY
					|| balType.getEffDateType() == ContantsUtil.EffectDateType.DAYS_FROM_START_OF_WEEK
					|| balType.getEffDateType() == ContantsUtil.EffectDateType.WEEKS_FROM_START_OF_MONTH
					|| balType.getEffDateType() == ContantsUtil.EffectDateType.MONTHS_FROM_START_OF_YEAR) {
				balType.setEffDateData(1L);
			}
		}

	}

	public void commandAddNewBalType() {
		super.getTreeCommonBean().hideAllCategoryComponent();
		super.getTreeCommonBean().setContentTitle(super.readProperties("baltype.title"));
		BalType balType = new BalType();
		balType.setCategoryId(this.category.getCategoryId());
		if (this.category.getCategoryType() == 2802) {
			balType.setIsAcm(true);
		} else if (this.category.getCategoryType() == 2801) {
			balType.setIsAcm(false);
		}
		refreshBalType(balType);
		this.isEditting = false;
		this.isApply = false;
		setDefaultValue();
	}

	public void commandEditBalType(BalType balType) {
		super.getTreeCommonBean().setContentTitle(super.readProperties("baltype.title"));
		this.isEditting = true;
		super.getTreeCommonBean().setBalTypeProperties(false, balType.getCategoryId(), balType, false);
		this.isApply = false;
	}

	public void commandCloneBT(BalType balType) throws CloneNotSupportedException {
		if (balType != null) {
			TreeCommonBean treeCommonBean = super.getTreeCommonBean();
			Category cat = categoryDAO.get(balType.getCategoryId());

			BalType balTypeCloned = balTypeDAO.cloneBalType(balType, "_Cloned");
			if (balTypeCloned != null) {
				commandEditBalType(balTypeCloned);
				List<Threshold> lstThresHold = new ArrayList<Threshold>();
				Map<Long, List<TriggerOcs>> mapTrigger = new HashMap<>();
				for (ThresHoldTrigerModel thresHoldTrigerModel : thresholds) {
					lstThresHold.add(thresHoldTrigerModel.getThreshold());
					mapTrigger.put(thresHoldTrigerModel.getThreshold().getThresholdId(),
							thresHoldTrigerModel.getTriggerOcsofThresHold());
				}
				treeCommonBean.updateTreeNode(balTypeCloned, cat, lstThresHold);
				for (Threshold thresHold : lstThresHold) {
					treeCommonBean.updateTreeNode(thresHold, null, mapTrigger.get(thresHold.getThresholdId()));
				}

				// treeCommonBean.updateTreeNode(balTypeCloned, cat,
				// lstThresHold);
				this.showMessageINFO("common.clone", " Balances ");
			}
		}
	}
	// Context Menu

	public void cloneContextMenu(NodeSelectEvent nodeSelectEvent) throws CloneNotSupportedException {
		BalType balType = (BalType) nodeSelectEvent.getTreeNode().getData();
		commandCloneBT(balType);
	}

	public void moveUpDownInCat(NodeSelectEvent event, boolean isUp) {
		BalType balType = (BalType) event.getTreeNode().getData();
		Object object = balTypeDAO.upDownObjectInCatWithDomain(balType, "index", isUp);
		if (object instanceof BalType) {
			Category category = categoryDAO.get(balType.getCategoryId());
			BalType nextBalType = (BalType) object;

			TreeCommonBean treeCommonBean = super.getTreeCommonBean();
			if (isUp) {
				treeCommonBean.moveUpTreeNode(balType);
			} else {
				treeCommonBean.moveDownTreeNode(balType);
			}
			treeCommonBean.updateTreeNode(nextBalType, category, null);
			if (formType == "list-baltype-by-category"
					&& nextBalType.getCategoryId() == this.category.getCategoryId()) {
				refreshCategories(category);
			}
			super.showNotificationSuccsess();
		} else {
			// super.showNotificationFail();
		}
	}

	// Edit ContextMenu
	public void editContextMenu(NodeSelectEvent nodeSelectEvent) {
		super.getTreeCommonBean().onNodeSelect(nodeSelectEvent);
		if (nodeSelectEvent != null) {
			BalType item = (BalType) nodeSelectEvent.getTreeNode().getData();
			commandEditBalType(item);
		}
	}

	// Remove ContextMenu
	public void removeContextMenu(NodeSelectEvent nodeSelectEvent) {
		super.getTreeCommonBean().onNodeSelect(nodeSelectEvent);
		if (nodeSelectEvent != null) {
			BalType item = (BalType) nodeSelectEvent.getTreeNode().getData();
			commandDeleteBalType(item);
			loadBalTypeByCategory(item.getCategoryId());
		}
	}

	public void commandDeleteBalType(BalType item) {
		this.isEditting = true;
		MapAcmbalBalDAO mapAcmbalBalDAO = new MapAcmbalBalDAO();
		MapSharebalBalDAO mapSharebalBalDAO = new MapSharebalBalDAO();

		if (!mapSharebalBalDAO.checkBalTypeInMSBBF(item.getBalTypeId(), item.getBalTypeId())
				&& !mapAcmbalBalDAO.checkBalTypeInMABBF(item.getBalTypeId(), item.getBalTypeId())) {
			try {
				balTypeDAO.deleteBalTypeAndThresHold(item);
				refreshCategories(category);
				TreeCommonBean treeCommonBean = super.getTreeCommonBean();
				Category cat = categoryDAO.get(item.getCategoryId());
				treeCommonBean.removeTreeNodeAll(item);
				this.showMessageINFO("validate.deleteSuccess", super.readProperties("baltype.title"));
			} catch (Exception e) {
				getLogger().warn(e.getMessage(), e);
				throw e;
			}
		} else {
			this.showMessageWARN("common.summary.warning", super.readProperties("validate.fieldUseIn"));
		}

	}

	public void saveAll() {

		if (validateBalType()) {
			getAbbreviation(balType.getUnitTypeId());
			if (balType.getExpDateType() == null) {
				balType.setExpDate(null);
			}
			if (balType.getEffDateType() == null) {
				balType.setEffDate(null);
			}

			if (balType.getExpDateType() != null) {
				if (balType.getExpDateType() == ContantsUtil.ExpireDateType.HOURS
						|| balType.getExpDateType() == ContantsUtil.ExpireDateType.DAYS
						|| balType.getExpDateType() == ContantsUtil.ExpireDateType.WEEKS
						|| balType.getExpDateType() == ContantsUtil.ExpireDateType.MONTHS
						|| balType.getExpDateType() == ContantsUtil.ExpireDateType.YEARS) {

					balType.setExpDate(null);
				} else {
					balType.setExpDateData(null);
				}
			}

			if (balType.getEffDateType() != null) {
				if (balType.getEffDateType() == ContantsUtil.EffectDateType.HOURS_FROM_START_OF_DAY
						|| balType.getEffDateType() == ContantsUtil.EffectDateType.DAYS_FROM_START_OF_WEEK
						|| balType.getEffDateType() == ContantsUtil.EffectDateType.WEEKS_FROM_START_OF_MONTH
						|| balType.getEffDateType() == ContantsUtil.EffectDateType.MONTHS_FROM_START_OF_YEAR) {
					balType.setEffDate(null);
				} else {
					balType.setEffDateData(null);
				}
			}

			if (balType.getBalTypeType() != null) {
				if (balType.getBalTypeType() == Normalizer.TypeOfBalType.SINGLE
						|| balType.getBalTypeType() == Normalizer.TypeOfBalType.MULTIBAL) {
					balType.setPeriodicPeriodType(null);
					balType.setPeriodicPeriod(null);
					balType.setWindowSize(null);
					balType.setHighWaterMarkLevel(null);
					balType.setLowWaterMarkLevel(null);
				}
			}

			if (balTypeDAO.saveAll(balType, thresholds)) {
				TreeCommonBean treeCommonBean = super.getTreeCommonBean();
				Category cat = categoryDAO.get(balType.getCategoryId());
				List<Threshold> lstThresHold = new ArrayList<Threshold>();
				Map<Long, List<TriggerOcs>> mapTrigger = new HashMap<>();
				for (ThresHoldTrigerModel thresHoldTrigerModel : thresholds) {
					lstThresHold.add(thresHoldTrigerModel.getThreshold());
					mapTrigger.put(thresHoldTrigerModel.getThreshold().getThresholdId(),
							thresHoldTrigerModel.getTriggerOcsofThresHold());
				}
				treeCommonBean.updateTreeNode(balType, cat, lstThresHold);
				for (Threshold thresHold : lstThresHold) {
					treeCommonBean.updateTreeNode(thresHold, null, mapTrigger.get(thresHold.getThresholdId()));
				}
				this.isApply = true;
				this.showMessageINFO("common.save", " Balances ");
			} else {
				this.showMessageWARN(" Balances ", super.readProperties("common.error"));
			}
			this.isEditting = true;
		}
	}

	// Validate
	private boolean validateBalType() {
		boolean result = true;
		if (balTypeDAO.checkId(balType, isEditting)) {
			this.showMessageWARN("Balances", super.readProperties("validate.checkObjectExist"));
			result = false;
		} else if (balType.getBalTypeId() < 0) {
			this.showMessageWARN("baltype.balTypeId", super.readProperties("validate.checkValueLess"));
			result = false;
		} else if (balTypeDAO.checkName(balType, balType.getBalTypeName())) {
			this.showMessageWARN("Balances", super.readProperties("validate.checkValueNameExist"));
			result = false;
		} else if (ValidateUtil.checkStringNullOrEmpty(balType.getBalTypeName())) {
			this.showMessageWARN("Balances", super.readProperties("validate.checkValueNameNull"));
			result = false;
		} else if (ValidateUtil.checkStringNullOrEmpty(balType.getExternalId())) {
			this.showMessageWARN("common.externalId", super.readProperties("validate.checkValueNameNull"));
			result = false;
		}

		if (balType.getUnitTypeId() == null || balType.getUnitTypeId() == 0L) {
			this.showMessageWARN("unittype", super.readProperties("validate.checkValueNameNull"));
			result = false;
		}

		if (balType.getBalTypeType() == Normalizer.TypeOfBalType.PERIODIC) {
			if (balType.getWindowSize() == null || balType.getLowWaterMarkLevel() == null
					|| balType.getHighWaterMarkLevel() == null) {
				this.showMessageWARN("validate.sumNull", "");
				result = false;
			}

			if (balType.getWindowSize() != null && balType.getLowWaterMarkLevel() != null
					&& balType.getHighWaterMarkLevel() != null) {

				if (balType.getWindowSize() == 0 || balType.getLowWaterMarkLevel() == 0
						|| balType.getHighWaterMarkLevel() == 0) {
					this.showMessageWARN("validate.sumZero", "");
					result = false;
				}
				if (balType.getWindowSize() < 0 || balType.getLowWaterMarkLevel() < 0
						|| balType.getHighWaterMarkLevel() < 0) {
					this.showMessageWARN("validate.sumLarger", "");
					result = false;
				}
				if (balType.getWindowSize() < balType.getLowWaterMarkLevel()
						|| balType.getWindowSize() < balType.getHighWaterMarkLevel()) {
					this.showMessageWARN("validate.sumLarger", "");
					result = false;
				}
				if (balType.getWindowSize() != balType.getLowWaterMarkLevel() + balType.getHighWaterMarkLevel()) {
					this.showMessageWARN("validate.sum", "");
					result = false;
				}
				if (balType.getPeriodicPeriod() != null && balType.getPeriodicPeriod() < 0L) {
					this.showMessageWARN("Periodic Period", super.readProperties("validate.checkValueLess"));
					result = false;
				}
				if (balType.getWindowSize() < 0L) {
					this.showMessageWARN("Window Size", super.readProperties("validate.checkValueLess"));
					result = false;
				}
				if (balType.getHighWaterMarkLevel() < 0L) {
					this.showMessageWARN("High Level", super.readProperties("validate.checkValueLess"));
					result = false;
				}
				if (balType.getLowWaterMarkLevel() < 0L) {
					this.showMessageWARN("Low Level", super.readProperties("validate.checkValueLess"));
					result = false;
				}
			}

		}

		if (balType.getBalTypepercision() != null && balType.getBalTypepercision() < 0L) {
			this.showMessageWARN("Precision", super.readProperties("validate.checkValueLess"));
			result = false;
		}

		if (balType.getExpDate() != null && balType.getEffDate() != null
				&& balType.getExpDate().compareTo(balType.getEffDate()) < 0) {
			this.showMessageWARN("validate.expireDate", "");
			result = false;
		}

		if (balType.getExpDateData() != null && balType.getEffDateData() != null) {
			if (balType.getExpDateData() < balType.getEffDateData()) {
				this.showMessageWARN("validate.expireDate", "");
				result = false;
			}
		}

		if (balType.getExpDateType() != null) {
			if (balType.getExpDateType() == ContantsUtil.ExpireDateType.EXPIRE_DATE_FIELD
					|| balType.getExpDateType() == ContantsUtil.ExpireDateType.FROM_PROVISIONING
					|| balType.getExpDateType() == ContantsUtil.ExpireDateType.BILLING_CYCLE
					|| balType.getExpDateType() == ContantsUtil.ExpireDateType.END_OF_HOUR
					|| balType.getExpDateType() == ContantsUtil.ExpireDateType.END_OF_DAY
					|| balType.getExpDateType() == ContantsUtil.ExpireDateType.END_OF_WEEK
					|| balType.getExpDateType() == ContantsUtil.ExpireDateType.END_OF_MONTH
					|| balType.getExpDateType() == ContantsUtil.ExpireDateType.END_OF_YEAR) {
				if (balType.getExpDate() == null) {
					this.showMessageWARN("validate.expireDateNull", "");

					result = false;
				}
			} else {
				if (balType.getExpDateData() == null) {
					balType.setExpDateData(2099L);
					result = true;
					// this.showMessageWARN("validate.expireDateNull", "");
					// result = false;
				}
			}
		}

		if (balType.getEffDateType() != null) {
			if (balType.getEffDateType() == ContantsUtil.EffectDateType.START_DATE_FIELD
					|| balType.getEffDateType() == ContantsUtil.EffectDateType.FROM_PROVISIONING
					|| balType.getEffDateType() == ContantsUtil.EffectDateType.FIRST_EVENT
					|| balType.getEffDateType() == ContantsUtil.EffectDateType.BILLING_CYCLE
					|| balType.getEffDateType() == ContantsUtil.EffectDateType.PURCHASE_DATE
					|| balType.getEffDateType() == ContantsUtil.EffectDateType.START_OF_HOUR
					|| balType.getEffDateType() == ContantsUtil.EffectDateType.START_OF_DAY
					|| balType.getEffDateType() == ContantsUtil.EffectDateType.START_OF_WEEK
					|| balType.getEffDateType() == ContantsUtil.EffectDateType.START_OF_MONTH
					|| balType.getEffDateType() == ContantsUtil.EffectDateType.START_OF_YEAR) {
				if (balType.getEffDate() == null) {
					this.showMessageWARN("validate.startDateNull", "");

					result = false;
				}
			} else {
				if (balType.getEffDateData() == null) {
					this.showMessageWARN("validate.startDateNull", "");
					result = false;
				}

			}
		}

		return result;

	}

	public void editBalType(BalType item) {
		this.balType = item;

	}

	public void cancelBalType() {
		refreshCategories(category);
		Category category = new Category();
	}

	public List<SelectItem> loadComboListCategory() {
		listComboCategory = new ArrayList<SelectItem>();
		CategoryDAO categoryDAO = new CategoryDAO();
		List<Category> listCategory = categoryDAO.findByType(CategoryType.CTL_BL_BAL_TYPE);
		if (listComboCategory != null && !listCategory.isEmpty()) {
			for (Category category : listCategory) {
				listComboCategory.add(new SelectItem(category.getCategoryId(), category.getCategoryName()));
			}
		}

		return listComboCategory;
	}

	// load combo type of
	// baltype================================================================
	public List<SelectItem> loadComboTypeOfBalType() {
		listTypeOfBalType = new ArrayList<SelectItem>();
		listTypeOfBalType.add(new SelectItem(Normalizer.TypeOfBalType.SINGLE, Normalizer.TypeOfBalType.SINGLE_NAME));
		listTypeOfBalType
				.add(new SelectItem(Normalizer.TypeOfBalType.MULTIBAL, Normalizer.TypeOfBalType.MULTIBAL_NAME));
		listTypeOfBalType
				.add(new SelectItem(Normalizer.TypeOfBalType.PERIODIC, Normalizer.TypeOfBalType.PERIODIC_NAME));
		return listTypeOfBalType;
	}

	// load combo Payment Type=================================================
	public List<SelectItem> loadComboPaymentType() {
		listPaymentType = new ArrayList<SelectItem>();
		listPaymentType.add(new SelectItem(ContantsUtil.PaymentType.PAYMENT_TYPE_PREPAID,
				ContantsUtil.PaymentType.PAYMENT_TYPE_PREPAID_NAME));
		listPaymentType.add(new SelectItem(ContantsUtil.PaymentType.PAYMENT_TYPE_POSTPAID,
				ContantsUtil.PaymentType.PAYMENT_TYPE_POSTPAID_NAME));
		listPaymentType.add(new SelectItem(ContantsUtil.PaymentType.PAYMENT_TYPE_FTTH,
				ContantsUtil.PaymentType.PAYMENT_TYPE_FTTH_NAME));
		listPaymentType.add(new SelectItem(ContantsUtil.PaymentType.PAYMENT_TYPE_HYBRID,
				ContantsUtil.PaymentType.PAYMENT_TYPE_HYBRID_NAME));
		return listPaymentType;
	}

	// load combo EffDate Type=================================================
	public List<SelectItem> loadComboEffDateType() {
		listEffDateType = new ArrayList<SelectItem>();
		listEffDateType.add(new SelectItem(ContantsUtil.EffectDateType.START_DATE_FIELD,
				ContantsUtil.EffectDateType.START_DATE_FIELD_NAME));
		listEffDateType.add(new SelectItem(ContantsUtil.EffectDateType.FROM_PROVISIONING,
				ContantsUtil.EffectDateType.FROM_PROVISIONING_NAME));
		listEffDateType.add(
				new SelectItem(ContantsUtil.EffectDateType.FIRST_EVENT, ContantsUtil.EffectDateType.FIRST_EVENT_NAME));
		listEffDateType.add(new SelectItem(ContantsUtil.EffectDateType.BILLING_CYCLE,
				ContantsUtil.EffectDateType.BILLING_CYCLE_NAME));
		listEffDateType.add(new SelectItem(ContantsUtil.EffectDateType.PURCHASE_DATE,
				ContantsUtil.EffectDateType.PURCHASE_DATE_NAME));
		listEffDateType.add(new SelectItem(ContantsUtil.EffectDateType.START_OF_HOUR,
				ContantsUtil.EffectDateType.START_OF_HOUR_NAME));
		listEffDateType.add(new SelectItem(ContantsUtil.EffectDateType.START_OF_DAY,
				ContantsUtil.EffectDateType.START_OF_DAY_NAME));
		listEffDateType.add(new SelectItem(ContantsUtil.EffectDateType.START_OF_WEEK,
				ContantsUtil.EffectDateType.START_OF_WEEK_NAME));
		listEffDateType.add(new SelectItem(ContantsUtil.EffectDateType.START_OF_MONTH,
				ContantsUtil.EffectDateType.START_OF_MONTH_NAME));
		listEffDateType.add(new SelectItem(ContantsUtil.EffectDateType.START_OF_YEAR,
				ContantsUtil.EffectDateType.START_OF_YEAR_NAME));
		listEffDateType.add(new SelectItem(ContantsUtil.EffectDateType.HOURS_FROM_START_OF_DAY,
				ContantsUtil.EffectDateType.HOURS_FROM_START_OF_DAY_NAME));
		listEffDateType.add(new SelectItem(ContantsUtil.EffectDateType.DAYS_FROM_START_OF_WEEK,
				ContantsUtil.EffectDateType.DAYS_FROM_START_OF_WEEK_NAME));
		listEffDateType.add(new SelectItem(ContantsUtil.EffectDateType.WEEKS_FROM_START_OF_MONTH,
				ContantsUtil.EffectDateType.WEEKS_FROM_START_OF_MONTH_NAME));
		listEffDateType.add(new SelectItem(ContantsUtil.EffectDateType.MONTHS_FROM_START_OF_YEAR,
				ContantsUtil.EffectDateType.MONTHS_FROM_START_OF_YEAR_NAME));
		return listEffDateType;
	}

	// load combo ExpDate Type=================================================
	public List<SelectItem> loadComboExpDateType() {
		listExpDateType = new ArrayList<SelectItem>();
		listExpDateType.add(new SelectItem(ContantsUtil.ExpireDateType.EXPIRE_DATE_FIELD,
				ContantsUtil.ExpireDateType.EXPIRE_DATE_FIELD_NAME));
		listExpDateType.add(new SelectItem(ContantsUtil.ExpireDateType.FROM_PROVISIONING,
				ContantsUtil.ExpireDateType.FROM_PROVISIONING_NAME));
		listExpDateType.add(new SelectItem(ContantsUtil.ExpireDateType.BILLING_CYCLE,
				ContantsUtil.ExpireDateType.BILLING_CYCLE_NAME));
		listExpDateType.add(
				new SelectItem(ContantsUtil.ExpireDateType.END_OF_HOUR, ContantsUtil.ExpireDateType.END_OF_HOUR_NAME));
		listExpDateType.add(
				new SelectItem(ContantsUtil.ExpireDateType.END_OF_DAY, ContantsUtil.ExpireDateType.END_OF_DAY_NAME));
		listExpDateType.add(
				new SelectItem(ContantsUtil.ExpireDateType.END_OF_WEEK, ContantsUtil.ExpireDateType.END_OF_WEEK_NAME));
		listExpDateType.add(new SelectItem(ContantsUtil.ExpireDateType.END_OF_MONTH,
				ContantsUtil.ExpireDateType.END_OF_MONTH_NAME));
		listExpDateType.add(
				new SelectItem(ContantsUtil.ExpireDateType.END_OF_YEAR, ContantsUtil.ExpireDateType.END_OF_YEAR_NAME));
		listExpDateType.add(new SelectItem(ContantsUtil.ExpireDateType.HOURS, ContantsUtil.ExpireDateType.HOURS_NAME));
		listExpDateType.add(new SelectItem(ContantsUtil.ExpireDateType.DAYS, ContantsUtil.ExpireDateType.DAYS_NAME));
		listExpDateType.add(new SelectItem(ContantsUtil.ExpireDateType.WEEKS, ContantsUtil.ExpireDateType.WEEKS_NAME));
		listExpDateType
				.add(new SelectItem(ContantsUtil.ExpireDateType.MONTHS, ContantsUtil.ExpireDateType.MONTHS_NAME));
		listExpDateType.add(new SelectItem(ContantsUtil.ExpireDateType.YEARS, ContantsUtil.ExpireDateType.YEARS_NAME));

		return listExpDateType;
	}

	// load combo Periodic Type=================================================
	public List<SelectItem> loadComboPeriodicType() {
		listPeriodicType = new ArrayList<SelectItem>();
		listPeriodicType.add(
				new SelectItem(ContantsUtil.PeriodicType.BILLING_CYCLE, ContantsUtil.PeriodicType.BILLING_CYCLE_NAME));
		listPeriodicType.add(new SelectItem(ContantsUtil.PeriodicType.HOURS, ContantsUtil.PeriodicType.HOURS_NAME));
		listPeriodicType.add(new SelectItem(ContantsUtil.PeriodicType.DAYS, ContantsUtil.PeriodicType.DAYS_NAME));
		listPeriodicType.add(new SelectItem(ContantsUtil.PeriodicType.WEEKS, ContantsUtil.PeriodicType.WEEKS_NAME));
		listPeriodicType.add(new SelectItem(ContantsUtil.PeriodicType.MONTHS, ContantsUtil.PeriodicType.MONTHS_NAME));
		listPeriodicType.add(new SelectItem(ContantsUtil.PeriodicType.YEARS, ContantsUtil.PeriodicType.YEARS_NAME));

		return listPeriodicType;
	}

	// load combo Bal Level=================================================
	public List<SelectItem> loadComboBalLevel() {
		listBalLevelType = new ArrayList<SelectItem>();
		listBalLevelType.add(new SelectItem(ContantsUtil.BalLevel.CUSTOMER, ContantsUtil.BalLevel.CUSTOMER_NAME));
		listBalLevelType.add(new SelectItem(ContantsUtil.BalLevel.SUBSCRIBER, ContantsUtil.BalLevel.SUBSCRIBER_NAME));
		listBalLevelType.add(new SelectItem(ContantsUtil.BalLevel.GROUP, ContantsUtil.BalLevel.GROUP_NAME));
		if (balType.getIsAcm() != null && balType.getIsAcm() == true) {
			listBalLevelType
					.add(new SelectItem(ContantsUtil.BalLevel.MEMBERSHIP, ContantsUtil.BalLevel.MEMBERSHIP_NAME));
		}
		return listBalLevelType;
	}

	// load combo Unit
	// Type=====================================================================

	public List<SelectItem> loadComboListUnitType() {
		listComboUnitType = new ArrayList<SelectItem>();
		UnitTypeDAO unitTypeDAO = new UnitTypeDAO();
		List<UnitType> listUnitType = unitTypeDAO.findAll("");
		if (listComboUnitType != null && !listUnitType.isEmpty()) {
			for (UnitType unitType : listUnitType) {
				listComboUnitType.add(new SelectItem(unitType.getUnitTypeId(), unitType.getName()));
			}
		}
		return listComboUnitType;
	}

	private void loadListUnitType() {
		lstUnitType = unitTypeDAO.findAll("");
	}

	private void loadComboBalTypePrecision() {
		UnitTypeDAO unitTypeDAO = new UnitTypeDAO();
		listComboBTPrecision = unitTypeDAO.findAll("");
	}

	// load combo Billing Cycle
	// Type=====================================================================

	// public List<SelectItem> loadComboListBillCycleType() {
	// listComboBillCycleType = new ArrayList<SelectItem>();
	// BillingCycleTypeDAO billCycleTypeDAO = new BillingCycleTypeDAO();
	// List<BillingCycleType> listBillCycleType = billCycleTypeDAO.findAll("");
	// if (listComboBillCycleType != null && !listBillCycleType.isEmpty()) {
	// for (BillingCycleType billCycleType : listBillCycleType) {
	// listComboBillCycleType.add(
	// new SelectItem(billCycleType.getBillingCycleTypeId(),
	// billCycleType.getBillingCycleTypeName()));
	// }
	// }
	// return listComboBillCycleType;
	// }

	public void loadComboListBillCycleType() {
		BillingCycleTypeDAO billCycleTypeDAO = new BillingCycleTypeDAO();
		listComboBillCycleType = billCycleTypeDAO.findAll("");
	}

	// load list BalType by Category
	public List<BalType> loadBalTypeByCategory(long categoryId) {
		listBalTypeByCategory = new ArrayList<BalType>();
		BalTypeDAO balTypeDAO = new BalTypeDAO();
		listBalTypeByCategory = balTypeDAO.findBTByConditions(categoryId);
		return listBalTypeByCategory;
	}

	// GET SET=================================================================

	public BalType getBalType() {
		return balType;
	}

	public void setBalType(BalType balType) {
		this.balType = balType;
	}

	public List<SelectItem> getListTypeOfBalType() {
		return listTypeOfBalType;
	}

	public void setListTypeOfBalType(List<SelectItem> listTypeOfBalType) {
		this.listTypeOfBalType = listTypeOfBalType;
	}

	public List<SelectItem> getListPaymentType() {
		return listPaymentType;
	}

	public void setListPaymentType(List<SelectItem> listPaymentType) {
		this.listPaymentType = listPaymentType;
	}

	public List<SelectItem> getListEffDateType() {
		return listEffDateType;
	}

	public void setListEffDateType(List<SelectItem> listEffDateType) {
		this.listEffDateType = listEffDateType;
	}

	public List<SelectItem> getListExpDateType() {
		return listExpDateType;
	}

	public void setListExpDateType(List<SelectItem> listExpDateType) {
		this.listExpDateType = listExpDateType;
	}

	public List<SelectItem> getListPeriodicType() {
		return listPeriodicType;
	}

	public void setListPeriodicType(List<SelectItem> listPeriodicType) {
		this.listPeriodicType = listPeriodicType;
	}

	public List<SelectItem> getListComboUnitType() {
		return listComboUnitType;
	}

	public void setListComboUnitType(List<SelectItem> listComboUnitType) {
		this.listComboUnitType = listComboUnitType;
	}

	public List<BalType> getListBalTypeByCategory() {
		return listBalTypeByCategory;
	}

	public void setListBalTypeByCategory(List<BalType> listBalTypeByCategory) {
		this.listBalTypeByCategory = listBalTypeByCategory;
	}

	public List<SelectItem> getListBalLevelType() {
		return listBalLevelType;
	}

	public void setListBalLevelType(List<SelectItem> listBalLevelType) {
		this.listBalLevelType = listBalLevelType;
	}

	public String getFormType() {
		return formType;
	}

	public void setFormType(String formType) {
		this.formType = formType;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public boolean isEditting() {
		return isEditting;
	}

	public void setEditting(boolean isEditting) {
		this.isEditting = isEditting;
	}

	public List<SelectItem> getListComboCategory() {
		return listComboCategory;
	}

	public void setListComboCategory(List<SelectItem> listComboCategory) {
		this.listComboCategory = listComboCategory;
	}

	public ThresHoldTrigerModel getThreshold() {
		return threshold;
	}

	public void setThreshold(ThresHoldTrigerModel threshold) {
		this.threshold = threshold;
	}

	public List<ThresHoldTrigerModel> getThresholds() {
		return thresholds;
	}

	public void setThresholds(List<ThresHoldTrigerModel> thresholds) {
		this.thresholds = thresholds;
	}

	public List<UnitType> getListComboBTPrecision() {
		return listComboBTPrecision;
	}

	public void setListComboBTPrecision(List<UnitType> listComboBTPrecision) {
		this.listComboBTPrecision = listComboBTPrecision;
	}

	public List<BillingCycleType> getListComboBillCycleType() {
		return listComboBillCycleType;
	}

	public void setListComboBillCycleType(List<BillingCycleType> listComboBillCycleType) {
		this.listComboBillCycleType = listComboBillCycleType;
	}

	public List<Category> getCategoriesOfBalType() {
		return categoriesOfBalType;
	}

	public boolean isApply() {
		return isApply;
	}

	public void setApply(boolean isApply) {
		this.isApply = isApply;
	}

	public List<UnitType> getLstUnitType() {
		return lstUnitType;
	}

	public void setLstUnitType(List<UnitType> lstUnitType) {
		this.lstUnitType = lstUnitType;
	}

	private List<Category> categoriesOfBalType;

	private void LoadCategoriesOfBalType(Long catType) {

		categoriesOfBalType = categoryDAO.findByTypeForSelectbox(catType);

		// if (balType.getBalTypeId() != 0L) {
		// Category category = categoryDAO.get(balType.getCategoryId());
		// categoriesOfBalType =
		// categoryDAO.findByTypeForSelectbox(category.getCategoryType());
		// } else {
		// categoriesOfBalType =
		// categoryDAO.findByTypeForSelectbox(this.category.getCategoryType());
		// }
	}

	public UnitType getUnitType() {
		return unitType;
	}

	public void setUnitType(UnitType unitType) {
		this.unitType = unitType;
		if (unitType != null) {
			pattern = genPattern(unitType.getUnitPrecision());
		} else {
			pattern = 1D;
		}
	}

	public Double getThresHoldValue() {
		return thresHoldValue;
	}

	public void setThresHoldValue(Double thresHoldValue) {
		this.thresHoldValue = thresHoldValue;
	}

}

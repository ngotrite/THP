package com.viettel.ocs.bean.catalog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;

import com.viettel.ocs.bean.BaseController;
import com.viettel.ocs.bean.TreeCommonBean;
import com.viettel.ocs.constant.CategoryType;
import com.viettel.ocs.constant.ContantsUtil;
import com.viettel.ocs.dao.BalTypeDAO;
import com.viettel.ocs.dao.BillingCycleDAO;
import com.viettel.ocs.dao.BillingCycleTypeDAO;
import com.viettel.ocs.dao.CalcUnitDAO;
import com.viettel.ocs.dao.CategoryDAO;
import com.viettel.ocs.entity.BillingCycle;
import com.viettel.ocs.entity.BillingCycleType;
import com.viettel.ocs.entity.CalcUnit;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.util.ValidateUtil;

@ManagedBean(name = "billingCycleTypeBean")
@ViewScoped
public class BillingCycleTypeBean extends BaseController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// UI Obj --------------------------------------------------
	private BillingCycleType billingCycleTypeUI;
	private BillingCycleTypeDAO billingCycleTypeDAO;
	private BillingCycleDAO billingCycleDAO;

	private List<SelectItem> listItemCategory;
	private List<SelectItem> listItemCalcUnit;
	private List<CalcUnit> lstCalcUnit;
	private List<BillingCycle> listBillingCycleDB;
	private List<BillingCycle> listBillingCycleSelection;

	private boolean isEditting;
	private boolean isApply;

	private String formType = "";
	private List<BillingCycleType> listBillingCycleByCategory;
	private CategoryDAO categoryDAO;

	private List<SelectItem> listFromDay;
	// private int itemFromDay;
	private long categoryID;
	private Date fromDate;
	private Date toDate;
	// Entitry check Validate
	private String txtBillingCycleTypeName;

	// Init -----------------------------------------------------
	@PostConstruct
	public void init() {
		this.isApply = true;
		this.isEditting = true;
		categoryDAO = new CategoryDAO();
		billingCycleTypeDAO = new BillingCycleTypeDAO();
		billingCycleDAO = new BillingCycleDAO();
		fromDate = new Date();
		toDate = new Date();
		txtBillingCycleTypeName = "";
		this.categoryID = 0l;
		billingCycleTypeUI = new BillingCycleType();
		listItemCalcUnit = new ArrayList<SelectItem>();
		listItemCategory = new ArrayList<SelectItem>();
		listBillingCycleDB = new ArrayList<BillingCycle>();
		listBillingCycleSelection = new ArrayList<BillingCycle>();
		lstCalcUnit = new ArrayList<CalcUnit>();
		loadCalcUnitDB();
	}
	
	public void resetDataTable(){
		DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("form-billingcycle-detail:tabBillingCycle");
		if (!dataTable.getFilters().isEmpty()) {
			dataTable.reset();// working
			RequestContext requestContext = RequestContext.getCurrentInstance();
			requestContext.update("form-billingcycle-detail:tabBillingCycle");
		}
	}

	// Prepare---------------------------------------------------
	// Validation
	// load list CalcUnit
	public void loadCalcUnitDB() {
		CalcUnitDAO calcUnitDAO = new CalcUnitDAO();
		lstCalcUnit = calcUnitDAO.findCalcUnit();
	}
	
	public List<SelectItem> loadCalcUnit() {
		listItemCalcUnit = new ArrayList<SelectItem>();
		CalcUnitDAO calcUnitDAO = new CalcUnitDAO();
		List<CalcUnit> listCalcUnit = calcUnitDAO.findCalcUnit();
		if (listItemCalcUnit != null && !listCalcUnit.isEmpty()) {
			for (CalcUnit calcUnit : listCalcUnit) {
				listItemCalcUnit.add(new SelectItem(calcUnit.getCalcUnitId(), calcUnit.getCalcUnitName()));
			}
		}
		return listItemCalcUnit;
	}

	public List<SelectItem> loadCategory() {
		CategoryDAO categoryDAO = new CategoryDAO();
		List<Category> listCategory = categoryDAO.findByTypeForSelectbox(CategoryType.CTL_BILLING_CYCLE);
		listItemCategory = new ArrayList<SelectItem>();
		if (listItemCategory != null && !listCategory.isEmpty()) {
			for (Category category : listCategory) {
				listItemCategory.add(new SelectItem(category.getCategoryId(), category.getCategoryName()));
			}
		}
		return listItemCategory;
	}

	// load list Billing Cycle
	public List<BillingCycle> loadListBillingCycleDB(long billingCycleTypeId) {
		BillingCycleDAO billingCycleDAO = new BillingCycleDAO();
		listBillingCycleDB = billingCycleDAO.findBillingCycleByBillingCycleTypeLimit10(billingCycleTypeId);
		return listBillingCycleDB;
	}

	// load list list Bill by categoryId
	public List<BillingCycleType> loadBillingCycleTypeByCategory(long categoryId) {
		listBillingCycleByCategory = new ArrayList<BillingCycleType>();
		listBillingCycleByCategory = billingCycleTypeDAO.findBillingCycleTypeByConditions(categoryId);
		this.categoryID = categoryId;
		return listBillingCycleByCategory;
	}

	// load list list From day by categoryId
	public List<SelectItem> loadFromDay(long type) {
		billingCycleTypeUI.getCalcUnitId();
		if (type == 4) {
			listFromDay = new ArrayList<SelectItem>();
			listFromDay.add(new SelectItem(ContantsUtil.DefineDayWeek.SUNDAY, ContantsUtil.DefineDayWeek.SUNDAY_NAME));
			listFromDay.add(new SelectItem(ContantsUtil.DefineDayWeek.MONDAY, ContantsUtil.DefineDayWeek.MONDAY_NAME));
			listFromDay
					.add(new SelectItem(ContantsUtil.DefineDayWeek.TUESDAY, ContantsUtil.DefineDayWeek.TUESDAY_NAME));
			listFromDay
					.add(new SelectItem(ContantsUtil.DefineDayWeek.WENESDAY, ContantsUtil.DefineDayWeek.WENESDAY_NAME));
			listFromDay
					.add(new SelectItem(ContantsUtil.DefineDayWeek.THURSDAY, ContantsUtil.DefineDayWeek.THURSDAY_NAME));
			listFromDay.add(new SelectItem(ContantsUtil.DefineDayWeek.FRIDAY, ContantsUtil.DefineDayWeek.FRIDAY_NAME));
			listFromDay
					.add(new SelectItem(ContantsUtil.DefineDayWeek.SATURDAY, ContantsUtil.DefineDayWeek.SATURDAY_NAME));
		} else if (type == 6) {
			listFromDay = new ArrayList<SelectItem>();
			for (int i = 1; i < 32; i++) {
				listFromDay.add(new SelectItem(i, String.valueOf(i)));
			}
		}
		return listFromDay;
	}

	// Action
	public void commandAddNew() {
		this.isEditting = false;
		this.isApply = false;
		clearText();
		billingCycleTypeUI.setCategoryId(categoryID);
		loadListBillingCycleDB(0l);
		setFormType("detail-billing-cycle-type");
		TreeCommonBean treeCommonBean = super.getTreeCommonBean();
		treeCommonBean.hideAllCategoryComponent();
		treeCommonBean.setContentTitle(super.readProperties("billingcycleType.title"));
	}

	public void commandCancel() {
		this.isEditting = true;
		setFormType("list-billing-cycle-type-by-category");
		loadBillingCycleTypeByCategory(billingCycleTypeUI.getCategoryId());
	}

	public void commandSaveOrUpdate() {
		TreeCommonBean treeCommonBean = super.getTreeCommonBean();
		Category cat = categoryDAO.get(billingCycleTypeUI.getCategoryId());
		if (validateBillingCycleType()) {
			// Save Billing Cycle Type
			billingCycleTypeUI.setBillingCycleTypeName(txtBillingCycleTypeName);
			if (billingCycleTypeUI.getCalcUnitId() != 4 && billingCycleTypeUI.getCalcUnitId() != 6) {
				billingCycleTypeUI.setFromOfDay(null);
			}
//			if (isEditting) {
//				billingCycleTypeDAO.saveOrUpdate(billingCycleTypeUI);
//				loadListBillingCycleDB(billingCycleTypeUI.getBillingCycleTypeId());
//				this.showMessageINFO("billingcycleType", super.readProperties("validate.editSuccess"));
//
//			} else {
//				billingCycleTypeUI.setIndex(billingCycleTypeDAO.getMaxIndex() + 1);
//				billingCycleTypeDAO.save(billingCycleTypeUI);
//				this.showMessageINFO("billingcycleType", super.readProperties("validate.saveSuccess"));
//			}
//			this.isEditting = true;  
//			this.isApply = true;
//			treeCommonBean.updateTreeNode(billingCycleTypeUI, cat, null);
			if (billingCycleTypeDAO.saveBillingCycleType(billingCycleTypeUI)) {
				this.isEditting = true;  
				this.isApply = true;
				treeCommonBean.updateTreeNode(billingCycleTypeUI, cat, null);
				this.showMessageINFO("billingcycleType", super.readProperties("validate.saveSuccess"));
			}
		}
	}

	public void commandRemoveBillingCycle() {
		if (billingCycleTypeUI.getCalcUnitId() > 2 && billingCycleDAO.delListBillingCycle(listBillingCycleSelection)) {
			listBillingCycleSelection.clear();
			activeButtonDelete();
			activeButtonChangeStatus();
			loadListBillingCycleDB(billingCycleTypeUI.getBillingCycleTypeId());
			this.showMessageINFO("validate.deleteSuccess", super.readProperties(""));
		}
	}

	public void commandChangeStatusBillingCycle() {
		if (billingCycleTypeUI.getCalcUnitId() > 2) {
			if (listBillingCycleSelection.size() > 0
					&& billingCycleDAO.updateStatusBillingCycle(listBillingCycleSelection, billingCycleTypeUI.getBillingCycleTypeId())) {
				loadListBillingCycleDB(billingCycleTypeUI.getBillingCycleTypeId());
				activeButtonChangeStatus();
				this.showMessageINFO("validate.changeStatusSuccess", super.readProperties(""));
			}
		}
	}

	public boolean checkListBillingCycle(BillingCycleType billingCycleTypeUI) {
		if (billingCycleTypeUI.getBillingCycleTypeId() != 0
				&& loadListBillingCycleDB(billingCycleTypeUI.getBillingCycleTypeId()).size() > 0) {
			return true;
		}
		return false;
	}

	public boolean activeButtonDelete() {
		if (listBillingCycleSelection != null && listBillingCycleSelection.size() > 0 && isApply == false) {
			listBillingCycleSelection.sort(
					(BillingCycle o1, BillingCycle o2) -> o2.getCycleBeginDate().compareTo(o1.getCycleBeginDate()));
			boolean isValidateFirst = true;
			boolean isValidateLast = true;
			for (int i = 0; i < listBillingCycleSelection.size(); i++) {
				if (listBillingCycleSelection.get(i).getCycleBeginDate()
						.compareTo(listBillingCycleDB.get(i).getCycleBeginDate()) != 0) {
					isValidateFirst = false;
				}

				if (!isValidateFirst) {
					int lastIndex = listBillingCycleDB.size() - 1;
					for (int j = listBillingCycleSelection.size() - 1; j >= i; j--) {
						if (listBillingCycleSelection.get(j).getCycleBeginDate()
								.compareTo(listBillingCycleDB.get(lastIndex--).getCycleBeginDate()) != 0) {
							isValidateLast = false;
						}
					}
					break;
				}
			}
			return isValidateLast;
		}
		return false;
	}

	public boolean activeButtonChangeStatus() {
		if (listBillingCycleSelection != null && listBillingCycleSelection.size() > 0 && isApply == false) {
			int countInActive = 0;
			for (BillingCycle billingCycle : listBillingCycleSelection) {
				if (billingCycle.getState() != null && billingCycle.getState().equals(0L)) {
					if (++countInActive == 2) {
						return false;
					}
				}
			}
			return true;
		}
		return false;
	}

	// In dialog
	public void dialogGen() {
		this.isEditting = true;
		setFormType("detail-billing-cycle-gen");
		TreeCommonBean treeCommonBean = super.getTreeCommonBean();
		treeCommonBean.hideAllCategoryComponent();
		treeCommonBean.setContentTitle(super.readProperties("billingcycle.title"));
	}

	public void cancelGen() {
		this.isEditting = true;
		setFormType("detail-billing-cycle-type");
		TreeCommonBean treeCommonBean = super.getTreeCommonBean();
		treeCommonBean.hideAllCategoryComponent();
	}

	public Date commandGenFromDate() {
		if (listBillingCycleDB.size() > 0) {
			fromDate = listBillingCycleDB.get(0).getCycleEndDate();
		} else {
			fromDate = billingCycleTypeUI.getBeginDate();
		}
		return fromDate;
	}

	public void commandGenBillingCycle() {
		if (billingCycleTypeUI != null) {
			BillingCycleDAO billingCycleDAO = new BillingCycleDAO();
			if (billingCycleTypeUI.getCalcUnitId() != 1 && billingCycleTypeUI.getCalcUnitId() != 2) {
				if (validateGen()) {
					int save = billingCycleDAO.saveBillingCycleType(billingCycleTypeUI, fromDate, toDate);
					switch (save) {
					case 1:
						this.isEditting = true;
						setFormType("detail-billing-cycle-type");
						TreeCommonBean treeCommonBean = super.getTreeCommonBean();
						treeCommonBean.hideAllCategoryComponent();
						this.showMessageINFO("billingcycle", super.readProperties("validate.saveSuccess"));
						loadListBillingCycleDB(billingCycleTypeUI.getBillingCycleTypeId());
						break;
					case 2:
						this.showMessageWARN("billingcycle", super.readProperties("validate.toDateBeforeGenDate"));
						break;
					case 3:
						this.showMessageWARN("billingcycle", super.readProperties("validate.toDateBeforeFromDate"));
						break;
					case 4:
						this.showMessageWARN("billingcycle", super.readProperties("validate.saveErorr"));
						break;
					}
					super.getTreeCommonBean().setContentTitle(super.readProperties("billingcycleType.title"));
				}
			}
		}
	}

	public boolean validateGen() {
		if (toDate == null) {
			this.showMessageWARN("billingcycle", super.readProperties("validate.checkValueNameNull"));
			return false;
		}
		return true;
	}

	// In table
	public void commandEditTable(BillingCycleType item) {
		this.isEditting = true;
		this.isApply = false;
		billingCycleTypeUI = item;
		setBillingCycleTypeUI(item);
		setFormType("detail-billing-cycle-type");
		TreeCommonBean treeCommonBean = super.getTreeCommonBean();
		treeCommonBean.hideAllCategoryComponent();
		treeCommonBean.setContentTitle(super.readProperties("billingcycleType.title"));
	}

	public void commandRemoveTable(BillingCycleType item) {
		BalTypeDAO balTypeDAO = new BalTypeDAO();
		if (!balTypeDAO.checkBillingCycleInBT(item.getBillingCycleTypeId())) {
			if (!billingCycleDAO.checkBillingCycle(item)) {
				this.isEditting = true;
				clearText();
				TreeCommonBean treeCommonBean = super.getTreeCommonBean();
				Category cat = categoryDAO.get(item.getCategoryId());
				try {
					billingCycleTypeDAO.delete(item);
					treeCommonBean.removeTreeNode(item, cat);
					loadBillingCycleTypeByCategory(item.getCategoryId());
					this.showMessageINFO("validate.deleteSuccess", super.readProperties(""));	
				} catch (Exception e) {
					throw e;
				}
			} else {
				this.showMessageWARN("common.summary.warning", super.readProperties("validate.checkChildren"));
			}
		} else {
			this.showMessageWARN("common.summary.warning", super.readProperties("validate.fieldUseIn"));
		}
	}

	public void clearText() {
		txtBillingCycleTypeName = "";
		billingCycleTypeUI = new BillingCycleType();
	}

	public boolean validateBillingCycleType() {
		if (ValidateUtil.checkStringNullOrEmpty(txtBillingCycleTypeName)) {
			this.showMessageWARN("billingcycle", super.readProperties("validate.checkValueNameNull"));
			return false;
		} else {
			billingCycleTypeUI.setBillingCycleTypeName(txtBillingCycleTypeName);
		}
		if (billingCycleTypeDAO.checkName(billingCycleTypeUI, txtBillingCycleTypeName, isEditting)) {
			this.showMessageWARN("billingcycle", super.readProperties("validate.checkValueNameExist"));
			return false;
		}
		if (billingCycleTypeUI.getBeginDate() == null) {
			this.showMessageWARN("common.beginDate", super.readProperties("validate.fieldNull"));
			return false;
		} else if (ValidateUtil.checkStringNullOrEmpty(billingCycleTypeUI.getBeginDate().toString())) {
			this.showMessageWARN("common.beginDate", super.readProperties("validate.fieldNull"));
			return false;
		}
		if (billingCycleTypeUI.getQuantity() == null
				|| ValidateUtil.checkStringNullOrEmpty(billingCycleTypeUI.getQuantity().toString())) {
			this.showMessageWARN("common.quantity", super.readProperties("validate.fieldNull"));
			return false;
		}
		if (billingCycleTypeUI.getQuantity() <= 0) {
			this.showMessageWARN("common.quantity", super.readProperties("validate.checkValueLess"));
			return false;
		}

		return true;
	}

	/**
	 * 
	 * @param isUp
	 *            true is up
	 */
	public void moveUpDownInCat(NodeSelectEvent event, boolean isUp) {
		BillingCycleType billingCycleType = (BillingCycleType) event.getTreeNode().getData();
		Object object = billingCycleTypeDAO.upDownObjectInCatWithDomain(billingCycleType, "index", isUp);
		if (object instanceof BillingCycleType) {
			Category category = categoryDAO.get(billingCycleType.getCategoryId());
			BillingCycleType nextBillingCycleType = (BillingCycleType) object;

			TreeCommonBean treeCommonBean = super.getTreeCommonBean();
			if (isUp) {
				treeCommonBean.moveUpTreeNode(billingCycleType);
			} else {
				treeCommonBean.moveDownTreeNode(billingCycleType);
			}
			treeCommonBean.updateTreeNode(nextBillingCycleType, category, null);

			if (formType == "list-billing-cycle-type-by-category" && category.getCategoryId() == categoryID) {
				loadBillingCycleTypeByCategory(category.getCategoryId());
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
			BillingCycleType item = (BillingCycleType) nodeSelectEvent.getTreeNode().getData();
			commandEditTable(item);
		}
	}

	// Remove ContextMenu
	public void removeContextMenu(NodeSelectEvent nodeSelectEvent) {
		super.getTreeCommonBean().onNodeSelect(nodeSelectEvent);
		if (nodeSelectEvent != null) {
			BillingCycleType item = (BillingCycleType) nodeSelectEvent.getTreeNode().getData();
			commandRemoveTable(item);
			loadBillingCycleTypeByCategory(item.getCategoryId());
			formType = "list-billing-cycle-type-by-category";
		}
	}


	// Get-set ---------------------------------------------------------
	public List<SelectItem> getListItemCategory() {
		return listItemCategory;
	}

	public void setListItemCategory(List<SelectItem> listItemCategory) {
		this.listItemCategory = listItemCategory;
	}

	public List<SelectItem> getListItemCalcUnit() {
		return listItemCalcUnit;
	}

	public void setListItemCalcUnit(List<SelectItem> listItemCalcUnit) {
		this.listItemCalcUnit = listItemCalcUnit;
	}

	public BillingCycleType getBillingCycleTypeUI() {
		return billingCycleTypeUI;
	}

	public void setBillingCycleTypeUI(BillingCycleType billingCycleType) {
		this.billingCycleTypeUI = billingCycleType;
	}

	public boolean isEditting() {
		return isEditting;
	}

	public void setEditting(boolean isEditting) {
		this.isEditting = isEditting;
	}

	public String getFormType() {
		return formType;
	}

	public void setFormType(String formType) {
		this.formType = formType;
	}

	public List<BillingCycleType> getListBillingCycleByCategory() {
		return listBillingCycleByCategory;
	}

	public void setListBillingCycleByCategory(List<BillingCycleType> listBillingCycleByCategory) {
		this.listBillingCycleByCategory = listBillingCycleByCategory;
	}

	public void setListBillingCycleDB(List<BillingCycle> listBillingCycleDB) {
		this.listBillingCycleDB = listBillingCycleDB;
	}

	public List<SelectItem> getListFromDay() {
		return listFromDay;
	}

	public void setListFromDay(List<SelectItem> listFromDay) {
		this.listFromDay = listFromDay;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public String getTxtBillingCycleTypeName() {
		txtBillingCycleTypeName = billingCycleTypeUI.getBillingCycleTypeName();
		return txtBillingCycleTypeName;
	}

	public void setTxtBillingCycleTypeName(String txtBillingCycleTypeName) {
		this.txtBillingCycleTypeName = txtBillingCycleTypeName;
	}

	public long getCategoryID() {
		return categoryID;
	}

	public void setCategoryID(long categoryID) {
		this.categoryID = categoryID;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public List<BillingCycle> getListBillingCycleSelection() {
		return listBillingCycleSelection;
	}

	public void setListBillingCycleSelection(List<BillingCycle> listBillingCycleSelection) {
		this.listBillingCycleSelection = listBillingCycleSelection;
	}

	public boolean isApply() {
		return isApply;
	}

	public void setApply(boolean isApply) {
		this.isApply = isApply;
	}

	public List<CalcUnit> getLstCalcUnit() {
		return lstCalcUnit;
	}

	public void setLstCalcUnit(List<CalcUnit> lstCalcUnit) {
		this.lstCalcUnit = lstCalcUnit;
	}
	
}

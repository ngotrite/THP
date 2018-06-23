package com.viettel.ocs.bean.catalog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;

import com.viettel.ocs.bean.BaseController;
import com.viettel.ocs.bean.TreeCommonBean;
import com.viettel.ocs.constant.CategoryType;
import com.viettel.ocs.dao.BalTypeDAO;
import com.viettel.ocs.dao.CategoryDAO;
import com.viettel.ocs.dao.RateTableDAO;
import com.viettel.ocs.dao.UnitTypeDAO;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.UnitType;
import com.viettel.ocs.util.ValidateUtil;

@SuppressWarnings("serial")
@ManagedBean(name = "unitBean")
@ViewScoped
public class UnitTypeBean extends BaseController implements Serializable {
	private Category category;
	private String formType = "";
	private List<SelectItem> listComboCategory;

	private List<UnitType> listUnitTypeByCategory;

	private UnitType unitType;
	private UnitTypeDAO unitTypeDAO;
	private CategoryDAO categoryDAO;

	private boolean isEditting;
	private boolean isApply;

	// Init -----------------------------------------------------
	@PostConstruct
	public void init() {
		this.isApply = true;
		this.isEditting = true;
		category = new Category();
		this.categoryDAO = new CategoryDAO();
		this.unitType = new UnitType();
		this.unitTypeDAO = new UnitTypeDAO();
		this.listUnitTypeByCategory = new ArrayList<UnitType>();
	}

	public void changeVisible() {
		this.isEditting = false;
	}

	public void refreshUnitType(UnitType unitType) {
		try {
			this.unitType = unitType.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			getLogger().warn(e.getMessage(), e);
		}
		setFormType("unittype-detail");
		LoadCategoriesOfUnitType();
		this.isApply = true;
	}

	public void refreshCategories(Category category) {
		formType = "list-unittype-by-category";
		this.category = category;
		// this.listUnitTypeByCategory =
		// unitTypeDAO.findUTByCategoryId(category.getCategoryId());
		loadUnitTypeByCategory(category.getCategoryId());
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.execute("$('.unitClearFilter').click();");
	}

	public void commandAddNewUnitType() {
		super.getTreeCommonBean().hideAllCategoryComponent();
		super.getTreeCommonBean().setContentTitle(super.readProperties("unittype.title"));
		unitType = new UnitType();
		refreshUnitType(unitType);
		unitType.setCategoryId(category.getCategoryId());
		this.isEditting = false;
		this.isApply = false;
	}

	public void commandEditUnitType(UnitType unitType) {
		TreeCommonBean treeCommonBean = super.getTreeCommonBean();
		treeCommonBean.setContentTitle(super.readProperties("unittype.title"));
		treeCommonBean.hideAllCategoryComponent();
		refreshUnitType(unitType);
		this.isEditting = true;
		this.isApply = false;

	}

	public void commandCloneUnitType(UnitType unitType) {
		super.getTreeCommonBean().hideAllCategoryComponent();
		refreshUnitType(unitType);
		unitType.setUnitTypeId(0);
	}

	public void commandDeleteUnitType(UnitType item) {
		BalTypeDAO balTypeDAO = new BalTypeDAO();
		RateTableDAO rateTableDAO = new RateTableDAO();
		if (!balTypeDAO.checkUnitTypeInBT(item.getUnitTypeId())
				&& !rateTableDAO.checkUnitTypeInRT(item.getUnitTypeId())) {
			try {
				unitTypeDAO.delete(item);
				listUnitTypeByCategory.remove(item);
				refreshCategories(category);
				TreeCommonBean treeCommonBean = super.getTreeCommonBean();
				Category cat = categoryDAO.get(item.getCategoryId());
				treeCommonBean.removeTreeNodeAll(item);
				this.showMessageINFO("validate.deleteSuccess", super.readProperties(""));	
			} catch (Exception e) {
				throw e;
			}
		} else {
			this.showMessageWARN("common.summary.warning", super.readProperties("validate.fieldUseIn"));
		}

	}

	public void saveUnitType() {
		TreeCommonBean treeCommonBean = super.getTreeCommonBean();
		Category cat = categoryDAO.get(unitType.getCategoryId());
		if (validateUnitType()) {

			if (unitTypeDAO.saveUnitType(unitType)) {
				this.showMessageINFO("common.save", " UnitType ");
				this.isEditting = true;
				this.isApply = true;
				// refreshCategories(cat);
				treeCommonBean.updateTreeNode(unitType, cat, null);
			}
		}
	}

	// Validate
	private boolean validateUnitType() {
		boolean result = true;
		if (ValidateUtil.checkStringNullOrEmpty(unitType.getName())) {
			this.showMessageWARN("unittype", super.readProperties("validate.checkValueNameNull"));
			result = false;
		}
		if (unitTypeDAO.checkName(unitType, unitType.getName())) {
			this.showMessageWARN("unittype", super.readProperties("validate.checkValueNameExist"));
			result = false;
		} else if (unitType.getUnitPrecision() == null) {
			this.showMessageWARN("unit.prec", super.readProperties("validate.fieldNull"));
			result = false;
		} else if (unitType.getUnitPrecision() < 0) {
			this.showMessageWARN("unit.prec", super.readProperties("validate.checkValueLess"));
			result = false;
		} else if (unitType.getBaseRate() == null) {
			this.showMessageWARN("unit.baseRate", super.readProperties("validate.fieldNull"));
			result = false;
		} else if (unitType.getBaseRate() < 0) {
			this.showMessageWARN("unit.baseRate", super.readProperties("validate.checkValueLess"));
			result = false;
		} else if (unitType.getDisplayRate() == null) {
			this.showMessageWARN("unit.displayRate", super.readProperties("validate.fieldNull"));
			result = false;
		} else if (unitType.getDisplayRate() < 0) {
			this.showMessageWARN("unit.displayRate", super.readProperties("validate.checkValueLess"));
			result = false;
		} else if (unitTypeDAO.checkId(unitType, isEditting)) {
			this.showMessageWARN("unittype", super.readProperties("validate.checkObjectExist"));
			result = false;
		} else if (unitType.getUnitTypeId() < 0) {
			this.showMessageWARN("unittype", super.readProperties("validate.checkValueLess"));
			result = false;
		}
		return result;
	}

	public void setIndexForUniType() {
		UnitType lastItem = unitTypeDAO.findUnitTypeLastIndexMove();
		if (this.unitType == null || this.unitType.getUnitTypeId() == 0 || !unitTypeDAO.checkId(unitType, isEditting)) {

			if (lastItem != null) {
				this.unitType.setIndex(lastItem.getIndex() + 1);
			} else {
				this.unitType.setIndex(0l);
			}
		}
	}

	// command up
	public void commandUp(UnitType item) {

		if (unitTypeDAO.processMoveUpDown(item, true)) {
			refreshCategories(category);
			TreeCommonBean treeCommonBean = super.getTreeCommonBean();
			treeCommonBean.moveUpTreeNode(item);
		}

	}

	// command down
	public void commandDown(UnitType item) {

		if (unitTypeDAO.processMoveUpDown(item, false)) {
			refreshCategories(category);
			TreeCommonBean treeCommonBean = super.getTreeCommonBean();
			treeCommonBean.moveDownTreeNode(item);

		}
	}

	// load combo Category==============================================
	public List<SelectItem> loadComboListCategory() {
		listComboCategory = new ArrayList<SelectItem>();
		CategoryDAO categoryDAO = new CategoryDAO();
		List<Category> listCategory = categoryDAO.findByType(CategoryType.CTL_UT_UNIT_TYPE);
		if (listComboCategory != null && !listCategory.isEmpty()) {
			for (Category category : listCategory) {
				listComboCategory.add(new SelectItem(category.getCategoryId(), category.getCategoryName()));
			}
		}
		return listComboCategory;
	}

	// load list UnitType by Category
	public List<UnitType> loadUnitTypeByCategory(long categoryId) {
		listUnitTypeByCategory = new ArrayList<UnitType>();
		listUnitTypeByCategory = unitTypeDAO.findUTByCategoryId(categoryId);
		return listUnitTypeByCategory;
	}

	private List<Category> categoriesOfUnitType;

	private void LoadCategoriesOfUnitType() {
		categoriesOfUnitType = categoryDAO.findByTypeForSelectbox(CategoryType.CTL_UT_UNIT_TYPE);
	}

	public List<Category> getCategoriesOfUnitType() {
		return categoriesOfUnitType;
	}

	// Context Menu
	public void moveUpDownInCat(NodeSelectEvent event, boolean isUp) {
		UnitType unitType = (UnitType) event.getTreeNode().getData();
		Object object = unitTypeDAO.upDownObjectInCatWithDomain(unitType, "index", isUp);
		if (object instanceof UnitType) {
			Category category = categoryDAO.get(unitType.getCategoryId());
			UnitType nextUnitType = (UnitType) object;

			TreeCommonBean treeCommonBean = super.getTreeCommonBean();
			if (isUp) {
				treeCommonBean.moveUpTreeNode(unitType);
			} else {
				treeCommonBean.moveDownTreeNode(unitType);
			}
			treeCommonBean.updateTreeNode(nextUnitType, category, null);
			if (formType == "list-unittype-by-category"
					&& nextUnitType.getCategoryId() == this.category.getCategoryId()) {
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
			UnitType item = (UnitType) nodeSelectEvent.getTreeNode().getData();
			commandEditUnitType(item);
		}
	}

	// Remove ContextMenu
	public void removeContextMenu(NodeSelectEvent nodeSelectEvent) {
		super.getTreeCommonBean().onNodeSelect(nodeSelectEvent);
		if (nodeSelectEvent != null) {
			UnitType item = (UnitType) nodeSelectEvent.getTreeNode().getData();
			commandDeleteUnitType(item);
			loadUnitTypeByCategory(item.getCategoryId());
		}
	}

	// GET SET=========================================

	public UnitType getUnitType() {
		return unitType;
	}

	public List<UnitType> getListUnitTypeByCategory() {
		return listUnitTypeByCategory;
	}

	public void setListUnitTypeByCategory(List<UnitType> listUnitTypeByCategory) {
		this.listUnitTypeByCategory = listUnitTypeByCategory;
	}

	public void setUnitType(UnitType unitType) {
		this.unitType = unitType;
	}

	public List<SelectItem> getListComboCategory() {
		return listComboCategory;
	}

	public void setListComboCategory(List<SelectItem> listComboCategory) {
		this.listComboCategory = listComboCategory;
	}

	public String getFormType() {
		return formType;
	}

	public void setFormType(String formType) {
		this.formType = formType;
	}

	public boolean isEditting() {
		return isEditting;
	}

	public void setEditting(boolean isEditting) {
		this.isEditting = isEditting;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public UnitTypeDAO getUnitTypeDAO() {
		return unitTypeDAO;
	}

	public void setUnitTypeDAO(UnitTypeDAO unitTypeDAO) {
		this.unitTypeDAO = unitTypeDAO;
	}

	public CategoryDAO getCategoryDAO() {
		return categoryDAO;
	}

	public void setCategoryDAO(CategoryDAO categoryDAO) {
		this.categoryDAO = categoryDAO;
	}

	public boolean isApply() {
		return isApply;
	}

	public void setApply(boolean isApply) {
		this.isApply = isApply;
	}

}

package com.viettel.ocs.bean.catalog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.primefaces.event.NodeSelectEvent;

import com.viettel.ocs.bean.BaseController;
import com.viettel.ocs.bean.TreeCommonBean;
import com.viettel.ocs.constant.CategoryType;
import com.viettel.ocs.dao.CategoryDAO;
import com.viettel.ocs.dao.TriggerDestinationDAO;
import com.viettel.ocs.entity.BaseEntity;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.TriggerDestination;
import com.viettel.ocs.util.ValidateUtil;

@SuppressWarnings("serial")
@ManagedBean(name = "triggerDesBean", eager = true)
@ViewScoped
public class TriggerDestinationBean extends BaseController implements Serializable {

	private TriggerDestination triggerDestinationUI;
	private List<SelectItem> listItemCategory;
	private List<TriggerDestination> listTriggerDesByCategory;
	private String formType = "";
	private boolean isEditting;
	private CategoryDAO categoryDAO;
	private long categoryID;
	private TriggerDestinationDAO triggerDestinationDAO;
	private String txtTriggerDestinationName;
	private List<Category> categoriesOfTD;
	private boolean isApply;

	// Init -----------------------------------------------------
	@PostConstruct
	public void init() {
		this.isApply = true;
		this.isEditting = true;
		formType = "";
		txtTriggerDestinationName = "";
		categoryDAO = new CategoryDAO();
		triggerDestinationDAO = new TriggerDestinationDAO();
		this.categoryID = 0l;
		this.categoriesOfTD = new ArrayList<Category>();
		listTriggerDesByCategory = new ArrayList<TriggerDestination>();
	}

	// prepare
	// Trigger Msg ---------------------------
	// load list category Trigger Type
	public List<SelectItem> loadCategoryTriggerDes() {
		listItemCategory = new ArrayList<SelectItem>();
		CategoryDAO categoryDAO = new CategoryDAO();
		List<Category> listCategory = categoryDAO.findByType(CategoryType.CTL_TD_TRIGGER_DESTINATION);
		if (listItemCategory != null && !listCategory.isEmpty()) {
			for (Category category : listCategory) {
				listItemCategory.add(new SelectItem(category.getCategoryId(), category.getCategoryName()));
			}
		}
		return listItemCategory;
	}

	public void loadCategoriesOfTD() {
		categoriesOfTD = categoryDAO.findByTypeForSelectbox(CategoryType.CTL_TD_TRIGGER_DESTINATION);
	}

	// load list TriggerMsg by categoryId
	public List<TriggerDestination> loadTriggerDesByCategory(long categoryId) {
		listTriggerDesByCategory = new ArrayList<TriggerDestination>();
		listTriggerDesByCategory = triggerDestinationDAO.findTriggerDesByCategoryId(categoryId);
		this.categoryID = categoryId;
		return listTriggerDesByCategory;
	}

	public void refreshTriggerDes(TriggerDestination triggerDestination) {
		setFormType("triggerdes-detail");
		setTriggerDestinationUI(triggerDestination);
		if (triggerDestinationUI.getDestName() != null) {
			txtTriggerDestinationName = triggerDestinationUI.getDestName();
		}
	}

	// Action
	// Trigger Type --------------------------
	public void commandAddNewTriggerDes() {
		this.isEditting = false;
		triggerDestinationUI = new TriggerDestination();
		triggerDestinationUI.setCategoryId(categoryID);
		triggerDestinationUI.setStatus(true);
		txtTriggerDestinationName = "";
		setFormType("triggerdes-detail");
		TreeCommonBean treeCommonBean = super.getTreeCommonBean();
		treeCommonBean.hideAllCategoryComponent();
		treeCommonBean.setContentTitle(super.readProperties("triggerDes.title"));
		loadCategoriesOfTD();
		this.isApply = false;
	}

	public void commandCancelTriggerDes() {
		this.isEditting = true;
		setFormType("list-triggerdes-by-category");
		loadTriggerDesByCategory(triggerDestinationUI.getCategoryId());
	}

	public void commandApplyTriggerDes() {
		TreeCommonBean treeCommonBean = super.getTreeCommonBean();
		Category cat = categoryDAO.get(triggerDestinationUI.getCategoryId());
		if (validateTriggerDes()) {
			triggerDestinationUI.setDestName(txtTriggerDestinationName);
			if (triggerDestinationDAO.saveTrigerDes(triggerDestinationUI)) {
				treeCommonBean.updateTreeNode(triggerDestinationUI, cat, null);
				this.isApply = true;
				this.isEditting = true;
				this.showMessageINFO("common.save", super.readProperties("triggerDes"));
			}
		}
	}

	public void commandEditTriggerMsgTB(TriggerDestination item) {
		this.isEditting = true;
		this.triggerDestinationUI = item;
		if (triggerDestinationUI.getDestName() != null) {
			txtTriggerDestinationName = triggerDestinationUI.getDestName();
		}
		formType = "triggerdes-detail";
		TreeCommonBean treeCommonBean = super.getTreeCommonBean();
		treeCommonBean.hideAllCategoryComponent();
		treeCommonBean.setContentTitle(super.readProperties("triggerDes.title"));
		loadCategoriesOfTD();
		this.isApply = false;
	}

	public void commandRemoveTriggerMsgTB(TriggerDestination item) {
		this.isEditting = true;
		clearText();
		TreeCommonBean treeCommonBean = super.getTreeCommonBean();
		Category cat = categoryDAO.get(item.getCategoryId());
		try {
			triggerDestinationDAO.delete(item);
			treeCommonBean.removeTreeNode(item, cat);
			listTriggerDesByCategory.remove(item);
			this.showMessageINFO("common.delete", super.readProperties("triggerDes"));	
		} catch (Exception e) {
			throw e;
		}
		
	}

	public void clearText() {
		this.triggerDestinationUI = new TriggerDestination();
	}

	// Validation
	public boolean validateTriggerDes() {
		if (ValidateUtil.checkStringNullOrEmpty(txtTriggerDestinationName)) {
			this.showMessageWARN("normalizer.validateError", super.readProperties("normalizer.errorDataValueName"));
			return false;
		}
		if (triggerDestinationDAO.checkName(triggerDestinationUI, txtTriggerDestinationName, isEditting)) {
			this.showMessageWARN("normalizer.validateError", super.readProperties("common.nameAlreadyExists"));
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
		TriggerDestination triggerDestination = (TriggerDestination) event.getTreeNode().getData();
		Object object = triggerDestinationDAO.upDownObjectInCatWithDomain(triggerDestination, "index", isUp);
		if (object instanceof TriggerDestination) {
			TriggerDestination nextTrigger = (TriggerDestination) object;
			Category category = categoryDAO.get(nextTrigger.getCategoryId());

			TreeCommonBean treeCommonBean = super.getTreeCommonBean();
			if (isUp) {
				treeCommonBean.moveUpTreeNode((BaseEntity) triggerDestination);
			} else {
				treeCommonBean.moveDownTreeNode((BaseEntity) triggerDestination);
			}
			treeCommonBean.updateTreeNode(nextTrigger, category, null);
			if (formType == "list-triggerdes-by-category" && category.getCategoryId() == categoryID) {
				loadTriggerDesByCategory(category.getCategoryId());
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
			TriggerDestination item = (TriggerDestination) nodeSelectEvent.getTreeNode().getData();
			commandEditTriggerMsgTB(item);
		}
	}

	// Remove ContextMenu
	public void removeContextMenu(NodeSelectEvent nodeSelectEvent) {
		super.getTreeCommonBean().onNodeSelect(nodeSelectEvent);
		if (nodeSelectEvent != null) {
			TriggerDestination item = (TriggerDestination) nodeSelectEvent.getTreeNode().getData();
			commandRemoveTriggerMsgTB(item);
			loadTriggerDesByCategory(item.getCategoryId());
			formType = "list-triggerdes-by-category";
		}
	}

	// Get Set -----------------------------------------------------
	public TriggerDestination getTriggerDestinationUI() {
		return triggerDestinationUI;
	}

	public void setTriggerDestinationUI(TriggerDestination triggerDestinationUI) {
		this.triggerDestinationUI = triggerDestinationUI;
	}

	public List<SelectItem> getListItemCategory() {
		return listItemCategory;
	}

	public void setListItemCategory(List<SelectItem> listItemCategory) {
		this.listItemCategory = listItemCategory;
	}

	public List<TriggerDestination> getListTriggerDesByCategory() {
		return listTriggerDesByCategory;
	}

	public void setListTriggerDesByCategory(List<TriggerDestination> listTriggerDesByCategory) {
		this.listTriggerDesByCategory = listTriggerDesByCategory;
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

	public CategoryDAO getCategoryDAO() {
		return categoryDAO;
	}

	public void setCategoryDAO(CategoryDAO categoryDAO) {
		this.categoryDAO = categoryDAO;
	}

	public long getCategoryID() {
		return categoryID;
	}

	public void setCategoryID(long categoryID) {
		this.categoryID = categoryID;
	}

	public TriggerDestinationDAO getTriggerDestinationDAO() {
		return triggerDestinationDAO;
	}

	public void setTriggerDestinationDAO(TriggerDestinationDAO triggerDestinationDAO) {
		this.triggerDestinationDAO = triggerDestinationDAO;
	}

	public String getTxtTriggerDestinationName() {
		return txtTriggerDestinationName;
	}

	public void setTxtTriggerDestinationName(String txtTriggerDestinationName) {
		this.txtTriggerDestinationName = txtTriggerDestinationName;
	}

	public List<Category> getCategoriesOfTD() {
		return categoriesOfTD;
	}

	public void setCategoriesOfTD(List<Category> categoriesOfTD) {
		this.categoriesOfTD = categoriesOfTD;
	}

	public boolean isApply() {
		return isApply;
	}

	public void setApply(boolean isApply) {
		this.isApply = isApply;
	}
}

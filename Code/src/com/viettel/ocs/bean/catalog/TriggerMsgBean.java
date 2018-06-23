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
import com.viettel.ocs.dao.TriggerMsgDAO;
import com.viettel.ocs.entity.BaseEntity;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.TriggerMsg;
import com.viettel.ocs.util.ValidateUtil;

@SuppressWarnings("serial")
@ManagedBean(name = "triggerMsgBean")
@ViewScoped
public class TriggerMsgBean extends BaseController implements Serializable {

	private TriggerMsg triggerMsgUI;
	private List<SelectItem> listItemCategory;
	private List<TriggerMsg> listTriggerMsgByCategory;
	private String formType = "";
	private boolean isEditting;
	private boolean isApply;
	private CategoryDAO categoryDAO;
	private long categoryID;
	private TriggerMsgDAO triggerMsgDAO;
	private String txtTriggerMsgName = "";
	private List<Category> categoriesOfTM;

	// Init -----------------------------------------------------
	@PostConstruct
	public void init() {
		this.isApply = true;
		formType = "";
		this.isEditting = true;
		categoryDAO = new CategoryDAO();
		triggerMsgDAO = new TriggerMsgDAO();
		this.categoryID = 0l;
		this.categoriesOfTM = new ArrayList<Category>();
		listTriggerMsgByCategory = new ArrayList<TriggerMsg>();
	}

	// prepare
	// Trigger Msg ---------------------------
	// load list category Trigger Type
	public List<SelectItem> loadCategoryTriggerMsg() {
		listItemCategory = new ArrayList<SelectItem>();
		CategoryDAO categoryDAO = new CategoryDAO();
		List<Category> listCategory = categoryDAO.findByType(CategoryType.CTL_TM_TRIGGER_MESSAGE);
		if (listItemCategory != null && !listCategory.isEmpty()) {
			for (Category category : listCategory) {
				listItemCategory.add(new SelectItem(category.getCategoryId(), category.getCategoryName()));
			}
		}
		return listItemCategory;
	}

	public void loadCategoriesOfTM() {
		categoriesOfTM = categoryDAO.findByTypeForSelectbox(CategoryType.CTL_TM_TRIGGER_MESSAGE);
	}

	// load list TriggerMsg by categoryId
	public List<TriggerMsg> loadTriggerMsgByCategory(long categoryId) {
		listTriggerMsgByCategory = new ArrayList<TriggerMsg>();
		listTriggerMsgByCategory = triggerMsgDAO.findTMByCategoryId(categoryId);
		this.categoryID = categoryId;
		return listTriggerMsgByCategory;
	}

	// Action
	// Trigger msg --------------------------
	public void commandAddNewTriggerMsg() {
		this.isEditting = false;
		triggerMsgUI = new TriggerMsg();
		triggerMsgUI.setCategoryId(categoryID);
		setFormType("detail-trigger-msg");
		TreeCommonBean treeCommonBean = super.getTreeCommonBean();
		treeCommonBean.hideAllCategoryComponent();
		treeCommonBean.setContentTitle(super.readProperties("triggerMsg.title"));
		loadCategoriesOfTM();
		this.isApply = false;
	}

	public void commandCancelTriggerMsg() {
		this.isEditting = true;
		loadTriggerMsgByCategory(triggerMsgUI.getCategoryId());
		setFormType("list-trigger-msg");
	}

	public void commandApplyTriggerMsg() {
		TreeCommonBean treeCommonBean = super.getTreeCommonBean();
		Category cat = categoryDAO.get(triggerMsgUI.getCategoryId());
		if (validateTriggerMsg()) {
			triggerMsgUI.setMsgName(txtTriggerMsgName);
			if (triggerMsgDAO.saveTriggerMsg(triggerMsgUI)) {
				treeCommonBean.updateTreeNode(triggerMsgUI, cat, null);
				this.isEditting = true;
				this.isApply = true;
				this.showMessageINFO("common.save", super.readProperties("triggerMsg"));
			}
		}
	}

	public void commandEditTriggerMsgTB(TriggerMsg item) {
		this.isEditting = true;
		this.triggerMsgUI = item;
		if (item.getMsgName() != null) {
			txtTriggerMsgName = item.getMsgName();
		}
		formType = "detail-trigger-msg";
		TreeCommonBean treeCommonBean = super.getTreeCommonBean();
		treeCommonBean.hideAllCategoryComponent();
		treeCommonBean.setContentTitle(super.readProperties("triggerMsg.title"));
		loadCategoriesOfTM();
		this.isApply = false;
	}

	public void commandRemoveTriggerMsgTB(TriggerMsg item) {
		this.isEditting = true;
		clearText();
		TreeCommonBean treeCommonBean = super.getTreeCommonBean();
		Category cat = categoryDAO.get(item.getCategoryId());
		try {
			triggerMsgDAO.delete(item);
			treeCommonBean.removeTreeNode(item, cat);
			listTriggerMsgByCategory.remove(item);
			this.showMessageINFO("common.delete", super.readProperties("triggerMsg"));
		} catch (Exception e) {
			throw e;
		}

		
	}

	public void clearText() {
		this.triggerMsgUI = new TriggerMsg();
		txtTriggerMsgName = "";
	}

	// Validation
	public boolean validateTriggerMsg() {
		if (ValidateUtil.checkStringNullOrEmpty(txtTriggerMsgName)) {
			this.showMessageWARN("normalizer.validateError", super.readProperties("normalizer.errorDataValueName"));
			return false;
		}
		if (triggerMsgDAO.checkName(triggerMsgUI, txtTriggerMsgName, isEditting)) {
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
		TriggerMsg triggerMsg = (TriggerMsg) event.getTreeNode().getData();
		Object object = triggerMsgDAO.upDownObjectInCatWithDomain(triggerMsg, "index", isUp);
		if (object instanceof TriggerMsg) {
			TriggerMsg nextTrigger = (TriggerMsg) object;
			Category category = categoryDAO.get(nextTrigger.getCategoryId());

			TreeCommonBean treeCommonBean = super.getTreeCommonBean();
			if (isUp) {
				treeCommonBean.moveUpTreeNode((BaseEntity) triggerMsg);
			} else {
				treeCommonBean.moveDownTreeNode((BaseEntity) triggerMsg);
			}
			treeCommonBean.updateTreeNode(nextTrigger, category, null);
			if (formType == "list-trigger-msg" && category.getCategoryId() == categoryID) {
				loadTriggerMsgByCategory(category.getCategoryId());
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
			TriggerMsg item = (TriggerMsg) nodeSelectEvent.getTreeNode().getData();
			commandEditTriggerMsgTB(item);
		}
	}

	// Remove ContextMenu
	public void removeContextMenu(NodeSelectEvent nodeSelectEvent) {
		super.getTreeCommonBean().onNodeSelect(nodeSelectEvent);
		if (nodeSelectEvent != null) {
			TriggerMsg item = (TriggerMsg) nodeSelectEvent.getTreeNode().getData();
			commandRemoveTriggerMsgTB(item);
			loadTriggerMsgByCategory(item.getCategoryId());
			formType = "list-trigger-msg";
		}
	}
	
	// Get Set -----------------------------------------------------
	public TriggerMsg getTriggerMsgUI() {
		return triggerMsgUI;
	}

	public void setTriggerMsgUI(TriggerMsg triggerMsgUI) {
		this.triggerMsgUI = triggerMsgUI;
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

	public long getCategoryID() {
		return categoryID;
	}

	public void setCategoryID(long categoryID) {
		this.categoryID = categoryID;
	}

	public List<SelectItem> getListItemCategory() {
		return listItemCategory;
	}

	public void setListItemCategory(List<SelectItem> listItemCategory) {
		this.listItemCategory = listItemCategory;
	}

	public List<TriggerMsg> getListTriggerMsgByCategory() {
		return listTriggerMsgByCategory;
	}

	public void setListTriggerMsgByCategory(List<TriggerMsg> listTriggerMsgByCategory) {
		this.listTriggerMsgByCategory = listTriggerMsgByCategory;
	}

	public String getTxtTriggerMsgName() {
		txtTriggerMsgName = triggerMsgUI.getMsgName();
		return txtTriggerMsgName;
	}

	public void setTxtTriggerMsgName(String txtTriggerMsgName) {
		this.txtTriggerMsgName = txtTriggerMsgName;
	}

	public List<Category> getCategoriesOfTM() {
		return categoriesOfTM;
	}

	public void setCategoriesOfTM(List<Category> categoriesOfTM) {
		this.categoriesOfTM = categoriesOfTM;
	}

	public boolean isApply() {
		return isApply;
	}

	public void setApply(boolean isApply) {
		this.isApply = isApply;
	}
}

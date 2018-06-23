package com.viettel.ocs.bean.catalog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;

import com.viettel.ocs.bean.BaseController;
import com.viettel.ocs.bean.TreeCommonBean;
import com.viettel.ocs.constant.CategoryType;
import com.viettel.ocs.constant.ContantsUtil;
import com.viettel.ocs.dao.CategoryDAO;
import com.viettel.ocs.dao.StateGroupDAO;
import com.viettel.ocs.dao.StateTypeDAO;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.StateGroup;
import com.viettel.ocs.entity.StateType;
import com.viettel.ocs.util.ValidateUtil;

@ManagedBean(name = "stateSetBean")
@ViewScoped
public class StateSetBean extends BaseController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6567854140195443065L;
	// UI Obj --------------------------------------------------
	private StateType stateTypeUI;
	private List<SelectItem> listItemCategory;
	private List<SelectItem> listComboType;
	private List<SelectItem> listComboStateGroup;

	private List<StateType> listStateTypes;
	private List<StateType> listStateTypeByCat;

	private boolean isEditting;
	private boolean isEdittingStateGroup;
	private boolean isApply;
	private String formType = "";
	// State Group Bean
	private StateGroup stateGroupUI;
	private List<StateGroup> listStateGroupByCat;
	private CategoryDAO categoryDAO;
	private long categoryID;

	private StateGroupDAO stateGroupDAO;
	private StateTypeDAO stateTypeDAO;

	// Entitry check Validate
	private String txtStateGroupName;
	private String txtStateTypeName;

	// Init -----------------------------------------------------
	@PostConstruct
	public void init() {
		this.isApply = true;
		this.isEditting = true;
		this.isEdittingStateGroup = true;
		stateTypeUI = new StateType();
		stateGroupUI = new StateGroup();
		txtStateGroupName = "";
		txtStateTypeName = "";
		categoryDAO = new CategoryDAO();
		this.categoryID = 0l;
		stateGroupDAO = new StateGroupDAO();
		stateTypeDAO = new StateTypeDAO();
		listStateTypeByCat = new ArrayList<StateType>();
		listStateGroupByCat = new ArrayList<StateGroup>();
	}

	// Prepare---------------------------------------------------
	// load list State Type
	public List<StateType> loadListStateTypes() {
		listStateTypes = stateTypeDAO.findListStateTypeByIndex();
		return listStateTypes;
	}

	// load combo State Group
	public List<SelectItem> loadComboListStateGroup() {
		listComboStateGroup = new ArrayList<SelectItem>();
		List<StateGroup> listStateGroup = stateGroupDAO.findAll("");
		if (!listStateGroup.isEmpty()) {
			for (StateGroup stateGroup : listStateGroup) {
				listComboStateGroup.add(new SelectItem(stateGroup.getStateGroupId(), stateGroup.getStateGroupName()));
			}
		}
		return listComboStateGroup;
	}

	// load combo Type
	public List<SelectItem> loadComboListType() {
		listComboType = new ArrayList<SelectItem>();
		listComboType.add(new SelectItem(ContantsUtil.StateType.BASIC_TYPE, ContantsUtil.StateType.BASIC_TYPE_VALUE));
		listComboType.add(new SelectItem(ContantsUtil.StateType.EXTRA_TYPE, ContantsUtil.StateType.EXTRA_TYPE_VALUE));
		return listComboType;
	}

	// load list category State Type
	public List<SelectItem> loadCategoryStateType() {
		listItemCategory = new ArrayList<SelectItem>();
		CategoryDAO categoryDAO = new CategoryDAO();
		List<Category> listCategory = categoryDAO.findByTypeForSelectbox(CategoryType.CTL_STATETYPE);
		if (listItemCategory != null && !listCategory.isEmpty()) {
			for (Category category : listCategory) {
				listItemCategory.add(new SelectItem(category.getCategoryId(), category.getCategoryName()));
			}
		}
		return listItemCategory;
	}

	// load list list StateType by Category
	public List<StateType> loadStateTypeByCategory(long categoryId) {
		listStateTypeByCat = new ArrayList<StateType>();
		listStateTypeByCat = stateTypeDAO.findStateTypeByConditions(categoryId);
		this.categoryID = categoryId;
		return listStateTypeByCat;
	}

	// StateGroups
	// load list list StateGroup by Category
	public List<StateGroup> loadStateGroupByCategory(long categoryId) {
		listStateGroupByCat = new ArrayList<StateGroup>();
		listStateGroupByCat = stateGroupDAO.findParamByConditions(categoryId);
		this.categoryID = categoryId;
		return listStateGroupByCat;
	}

	// load list category StateGroup
	public List<SelectItem> loadCategoryStateGroup() {
		listItemCategory = new ArrayList<SelectItem>();
		CategoryDAO categoryDAO = new CategoryDAO();
		List<Category> listCategory = categoryDAO.findByTypeForSelectbox(CategoryType.CTL_STATEGROUP);
		if (listItemCategory != null && !listCategory.isEmpty()) {
			for (Category category : listCategory) {
				listItemCategory.add(new SelectItem(category.getCategoryId(), category.getCategoryName()));
			}
		}
		return listItemCategory;
	}

	public void clearTextStateGroup() {
		this.stateGroupUI = new StateGroup();
	}

	// Actions StateType --------------------------
	public void commandAddNew() {
		if (loadComboListStateGroup().size() != 0) {
			this.isApply = false;
			this.isEditting = false;
			stateTypeUI = new StateType();
			stateTypeUI.setStatus(true);
			// stateTypeUI.setStateMask("****-****-****-****");
			// stateTypeUI.setFilterValue("****-****-****-****");
			stateTypeUI.setCategoryId(categoryID);
			setFormType("detail-statetype");
			TreeCommonBean treeCommonBean = super.getTreeCommonBean();
			treeCommonBean.setContentTitle(super.readProperties("stateset.title"));
			treeCommonBean.hideAllCategoryComponent();
		} else {
			this.showMessageINFO("validate.stateSetCheckAddNew", super.readProperties(""));
		}
	}

	public void commandCancel() {
		this.isEditting = true;
		loadStateTypeByCategory(stateTypeUI.getCategoryId());
		setFormType("list-statetype-by-category");
	}

	// State Type
	public void commandSaveOrUpdateStateType() {
		if (validateStateType()) {
			TreeCommonBean treeCommonBean = super.getTreeCommonBean();
			Category cat = categoryDAO.get(stateTypeUI.getCategoryId());
			if (stateTypeUI.getStateTypeId() == 0L) {
				StateType stateTypeMaxId = stateTypeDAO.findStateTypeLastID();
				if (stateTypeMaxId != null) {
					stateTypeUI.setStateTypeId(stateTypeMaxId.getStateTypeId() + 1);
				} else {
					stateTypeUI.setStateTypeId(1l);
				}
			}
			stateTypeUI.setStateName(txtStateTypeName);
			stateTypeDAO.generateIndexByCat(stateTypeUI, "stateTypeId");
			if (isEditting) {
				stateTypeDAO.saveOrUpdate(stateTypeUI);
				this.showMessageINFO("validate.editSuccess", super.readProperties(""));
			} else {
				stateTypeDAO.save(stateTypeUI);
				this.showMessageINFO("validate.saveSuccess", super.readProperties(""));
			}
			treeCommonBean.updateTreeNode(stateTypeUI, cat, null);
			// commandCancelStateType();
			this.isEditting = true;
			this.isApply = true;
		}
	}

	public void commandEditTBStateType(StateType item) {
		this.isApply = false;
		this.isEditting = true;
		this.stateTypeUI = item;
		formType = "detail-statetype";
		TreeCommonBean treeCommonBean = super.getTreeCommonBean();
		treeCommonBean.hideAllCategoryComponent();
		treeCommonBean.setContentTitle(super.readProperties("stateset.title"));
	}

	public void commandDeleteTBStateType(StateType item) {
		this.isEditting = true;
		clearText();
		TreeCommonBean treeCommonBean = super.getTreeCommonBean();
		Category cat = categoryDAO.get(item.getCategoryId());
		try {
			stateTypeDAO.delete(item);
			treeCommonBean.removeTreeNode(item, cat);
			listStateTypeByCat.remove(item);
			this.showMessageINFO("validate.deleteSuccess", super.readProperties(""));	
		} catch (Exception e) {
			throw e;
		}
		
	}

	public void commandUp(StateType item) {
		List<StateType> listStateTypes = loadStateTypeByCategory(item.getCategoryId());
		for (int i = 0; i < listStateTypes.size(); i++) {
			if (item.getStateTypeId() == listStateTypes.get(i).getStateTypeId() && i != 0) {
				StateType upItem = listStateTypes.get(i - 1);
				if (stateTypeDAO.processMoveUpDown(upItem, item)) {
					loadStateTypeByCategory(item.getCategoryId());
					TreeCommonBean treeCommonBean = super.getTreeCommonBean();
					treeCommonBean.moveUpTreeNode(item);
					this.showMessageINFO("validate.upObjectSuccess", super.readProperties(""));
				}
				break;
			}
		}

	}

	public void commandDown(StateType item) {
		List<StateType> listStateTypes = loadStateTypeByCategory(item.getCategoryId());
		for (int i = 0; i < listStateTypes.size(); i++) {
			if (item.getStateTypeId() == listStateTypes.get(i).getStateTypeId() && i != (listStateTypes.size() - 1)) {
				StateType upItem = listStateTypes.get(i + 1);
				if (stateTypeDAO.processMoveUpDown(upItem, item)) {
					loadStateTypeByCategory(item.getCategoryId());
					TreeCommonBean treeCommonBean = super.getTreeCommonBean();
					treeCommonBean.moveDownTreeNode(item);
					this.showMessageINFO("validate.downObjectSuccess", super.readProperties(""));
				}
			}
		}
	}

	/**
	 * 
	 * @param isUp
	 *            true is up
	 */
	public void moveUpDownStateTypeInCat(NodeSelectEvent event, boolean isUp) {
		StateType stateType = (StateType) event.getTreeNode().getData();
		Object object = stateGroupDAO.upDownObjectInCatWithDomain(stateType, "index", isUp);
		if (object instanceof StateType) {
			Category category = categoryDAO.get(stateType.getCategoryId());
			StateType nextStateType = (StateType) object;

			TreeCommonBean treeCommonBean = super.getTreeCommonBean();
			if (isUp) {
				treeCommonBean.moveUpTreeNode(stateType);
			} else {
				treeCommonBean.moveDownTreeNode(stateType);
			}
			treeCommonBean.updateTreeNode(nextStateType, category, null);
			if (formType == "list-statetype-by-category" && category.getCategoryId() == categoryID) {
				loadStateTypeByCategory(category.getCategoryId());
				// RequestContext requestContext =
				// RequestContext.getCurrentInstance();
				// requestContext.execute("$('.stateGroupTableClearFilter').click();");
			}

			super.showNotificationSuccsess();
		} else {
//			super.showNotificationFail();
		}
	}

	/**
	 * 
	 * @param isUp
	 *            true is up
	 */
	public void moveUpDownInCat(NodeSelectEvent event, boolean isUp) {
		StateGroup stateGroup = (StateGroup) event.getTreeNode().getData();
		Object object = stateGroupDAO.upDownObjectInCatWithDomain(stateGroup, "index", isUp);
		if (object instanceof StateGroup) {
			Category category = categoryDAO.get(stateGroup.getCategoryId());
			StateGroup nextStateGroup = (StateGroup) object;

			TreeCommonBean treeCommonBean = super.getTreeCommonBean();
			if (isUp) {
				treeCommonBean.moveUpTreeNode(stateGroup);
			} else {
				treeCommonBean.moveDownTreeNode(stateGroup);
			}
			treeCommonBean.updateTreeNode(nextStateGroup, category, null);
			if (formType == "list-stategroup-by-category" && category.getCategoryId() == categoryID) {
				loadStateGroupByCategory(category.getCategoryId());
				RequestContext requestContext = RequestContext.getCurrentInstance();
				requestContext.execute("$('.stateGroupTableClearFilter').click();");
			}

			super.showNotificationSuccsess();
		} else {
//			super.showNotificationFail();
		}
	}

	public void commandCancelStateType() {
		this.isEditting = true;
		setFormType("list-statetype-by-category");
		loadStateTypeByCategory(stateTypeUI.getCategoryId());
	}

	// State Group
	public void commandAddNewStateGroup() {
		this.isApply = false;
		this.isEdittingStateGroup = false;
		clearTextStateGroup();
		stateGroupUI.setCategoryId(categoryID);
		setFormType("detail-stategroup");
		TreeCommonBean treeCommonBean = super.getTreeCommonBean();
		treeCommonBean.hideAllCategoryComponent();
		treeCommonBean.setContentTitle(super.readProperties("stateset.stateGroup.title"));
	}

	public void commandCancelStateGroup() {
		this.isEdittingStateGroup = true;
		setFormType("list-stategroup-by-category");
		loadStateGroupByCategory(stateGroupUI.getCategoryId());
	}

	public void commandSaveOrUpdateStateGroup() {
		if (validateStateGroup()) {
			TreeCommonBean treeCommonBean = super.getTreeCommonBean();
			Category cat = categoryDAO.get(stateGroupUI.getCategoryId());
			stateGroupUI.setStateGroupName(txtStateGroupName);
			stateGroupDAO.generateIndexByCat(stateGroupUI, "stateGroupId");
			if (isEdittingStateGroup) {
				stateGroupDAO.saveOrUpdate(stateGroupUI);
				this.showMessageINFO("validate.editSuccess", super.readProperties(""));
			} else {
				stateGroupUI.setIndex(stateGroupDAO.getMaxIndex() + 1);
				stateGroupDAO.save(stateGroupUI);
				this.showMessageINFO("validate.saveSuccess", super.readProperties(""));
			}
			this.isEdittingStateGroup = true;
			this.isApply = true;
			// commandCancelStateGroup();
			treeCommonBean.updateTreeNode(stateGroupUI, cat, null);
		}
	}

	public void commandDeleteStateGroup() {
		this.isEdittingStateGroup = true;
		stateGroupDAO.delete(stateGroupUI);
	}

	public void commandEditTBStateGroup(StateGroup item) {
		this.isApply = false;
		this.isEdittingStateGroup = true;
		this.stateGroupUI = item;
		formType = "detail-stategroup";
		TreeCommonBean treeCommonBean = super.getTreeCommonBean();
		treeCommonBean.hideAllCategoryComponent();
		treeCommonBean.setContentTitle(super.readProperties("stateset.stateGroup.title"));
	}

	public void commandDeleteTBStateGroup(StateGroup item) {
		if (!stateTypeDAO.checkTypeByGroup(item.getStateGroupId())) {
			this.isEdittingStateGroup = true;
			clearText();
			TreeCommonBean treeCommonBean = super.getTreeCommonBean();
			Category cat = categoryDAO.get(item.getCategoryId());
			try {
				stateGroupDAO.delete(item);
				treeCommonBean.removeTreeNode(item, cat);
				listStateGroupByCat.remove(item);
				this.showMessageINFO("validate.deleteSuccess", super.readProperties(""));	
			} catch (Exception e) {
				throw e;
			}
		} else {
			this.showMessageWARN("validate.checkChildren", super.readProperties(""));
		}
	}

	public void clearText() {
		this.stateGroupUI = new StateGroup();
		this.stateTypeUI = new StateType();
	}

	// ----------------------
	// Validate
	private boolean validateStateType() {
		if (ValidateUtil.checkStringNullOrEmpty(txtStateTypeName)) {
			this.showMessageWARN("stateset", super.readProperties("validate.checkValueNameNull"));
			return false;
		} else {
			stateTypeUI.setStateName(txtStateTypeName);
		}
		if (ValidateUtil.checkStringNullOrEmpty(stateTypeUI.getStateMask())) {
			this.showMessageWARN("stateset.stateMask", super.readProperties("validate.fieldNull"));
			return false;
		} else {
			Pattern pattern = Pattern.compile("[0-9*#]{4}-[0-9*#]{4}-[0-9*#]{4}-[0-9*#]{4}");
			Matcher matcher = pattern.matcher(stateTypeUI.getStateMask());
			if (!matcher.matches()) {
				this.showMessageWARN("stateset.stateMask", super.readProperties("validate.stateSetErrorFormat"));
				return false;
			}
		}

		if (ValidateUtil.checkStringNullOrEmpty(stateTypeUI.getFilterValue())) {
			this.showMessageWARN("stateset.filterValue", super.readProperties("validate.fieldNull"));
			return false;
		} else {
			Pattern pattern = Pattern.compile("[0-9*#]{4}-[0-9*#]{4}-[0-9*#]{4}-[0-9*#]{4}");
			Matcher matcher = pattern.matcher(stateTypeUI.getFilterValue());
			if (!matcher.matches()) {
				this.showMessageWARN("stateset.filterValue", super.readProperties("validate.stateSetErrorFormat"));
				return false;
			}
		}
		if (stateTypeDAO.checkName(stateTypeUI, txtStateTypeName, isEditting)) {
			this.showMessageWARN("stateset", super.readProperties("validate.checkValueNameExist"));
			return false;
		}
		if (stateTypeDAO.checkId(stateTypeUI, isEditting)) {
			this.showMessageWARN("stateset", super.readProperties("validate.checkObjectExist"));
			return false;
		}
		return true;
	}

	private boolean validateStateGroup() {
		if (ValidateUtil.checkStringNullOrEmpty(txtStateGroupName)) {
			this.showMessageWARN("stategroup", super.readProperties("validate.checkValueNameNull"));
			return false;
		} else {
			stateGroupUI.setStateGroupName(txtStateGroupName);
		}
		if (stateGroupDAO.checkName(stateGroupUI, txtStateGroupName, isEdittingStateGroup)) {
			this.showMessageWARN("stategroup", super.readProperties("validate.checkValueNameExist"));
			return false;
		}
		return true;
	}
	
	// Edit ContextMenu
	public void editContextMenu(NodeSelectEvent nodeSelectEvent) {
		super.getTreeCommonBean().onNodeSelect(nodeSelectEvent);
		Object object = nodeSelectEvent.getTreeNode().getData();
		if (nodeSelectEvent != null) {
			if (object instanceof StateType) {
				StateType item = (StateType) nodeSelectEvent.getTreeNode().getData();
				commandEditTBStateType(item);
			}else if (object instanceof StateGroup) {
				StateGroup item = (StateGroup) nodeSelectEvent.getTreeNode().getData();
				commandEditTBStateGroup(item);
			}

		}
	}

	// Remove ContextMenu
	public void removeContextMenu(NodeSelectEvent nodeSelectEvent) {
		super.getTreeCommonBean().onNodeSelect(nodeSelectEvent);
		Object object = nodeSelectEvent.getTreeNode().getData();
		if (nodeSelectEvent != null) {
			if (object instanceof StateType) {
				StateType item = (StateType) nodeSelectEvent.getTreeNode().getData();
				commandDeleteTBStateType(item);
				loadStateTypeByCategory(item.getCategoryId());
				formType = "list-statetype-by-category";
			}else if (object instanceof StateGroup) {
				StateGroup item = (StateGroup) nodeSelectEvent.getTreeNode().getData();
				commandDeleteTBStateGroup(item);
				loadStateGroupByCategory(item.getCategoryId());
				formType = "list-stategroup-by-category";
			}
			
		}
	}

	// Get-set ---------------------------------------------------------

	public List<SelectItem> getListComboType() {
		return listComboType;
	}

	public void setListComboType(List<SelectItem> listComboType) {
		this.listComboType = listComboType;
	}

	public List<SelectItem> getListComboStateGroup() {
		return listComboStateGroup;
	}

	public void setListComboStateGroup(List<SelectItem> listComboStateGroup) {
		this.listComboStateGroup = listComboStateGroup;
	}

	public void setListStateTypes(List<StateType> listStateTypes) {
		this.listStateTypes = listStateTypes;
	}

	public List<SelectItem> getListItemCategory() {
		return listItemCategory;
	}

	public void setListItemCategory(List<SelectItem> listItemCategory) {
		this.listItemCategory = listItemCategory;
	}

	public List<StateType> getListStateTypeByCat() {
		return listStateTypeByCat;
	}

	public void setListStateTypeByCat(List<StateType> listStateTypeByCat) {
		this.listStateTypeByCat = listStateTypeByCat;
	}

	public boolean isEditting() {
		return isEditting;
	}

	public void setEditting(boolean isEditting) {
		this.isEditting = isEditting;
	}

	public StateGroup getStateGroupUI() {
		return stateGroupUI;
	}

	public void setStateGroupUI(StateGroup stateGroupUI) {
		this.stateGroupUI = stateGroupUI;
	}

	public List<StateGroup> getListStateGroupByCat() {
		return listStateGroupByCat;
	}

	public void setListStateGroupByCat(List<StateGroup> listStateGroupByCat) {
		this.listStateGroupByCat = listStateGroupByCat;
	}

	public List<StateType> getListStateTypes() {
		return listStateTypes;
	}

	public boolean isEdittingStateGroup() {
		return isEdittingStateGroup;
	}

	public void setEdittingStateGroup(boolean isEdittingStateGroup) {
		this.isEdittingStateGroup = isEdittingStateGroup;
	}

	public String getFormType() {
		return formType;
	}

	public void setFormType(String formType) {
		this.formType = formType;
	}

	public StateType getStateTypeUI() {
		return stateTypeUI;
	}

	public void setStateTypeUI(StateType stateTypeUI) {
		this.stateTypeUI = stateTypeUI;
	}

	public long getCategoryID() {
		return categoryID;
	}

	public void setCategoryID(long categoryID) {
		this.categoryID = categoryID;
	}

	public String getTxtStateGroupName() {
		txtStateGroupName = stateGroupUI.getStateGroupName();
		return txtStateGroupName;
	}

	public void setTxtStateGroupName(String txtStateGroupName) {
		this.txtStateGroupName = txtStateGroupName;
	}

	public String getTxtStateTypeName() {
		txtStateTypeName = stateTypeUI.getStateName();
		return txtStateTypeName;
	}

	public void setTxtStateTypeName(String txtStateTypeName) {
		this.txtStateTypeName = txtStateTypeName;
	}

	public boolean isApply() {
		return isApply;
	}

	public void setApply(boolean isApply) {
		this.isApply = isApply;
	}
}

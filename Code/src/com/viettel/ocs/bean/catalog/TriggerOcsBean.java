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
import com.viettel.ocs.dao.TriggerOcsDAO;
import com.viettel.ocs.dao.TriggerTypeDAO;
import com.viettel.ocs.entity.BaseEntity;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.RateTable;
import com.viettel.ocs.entity.TriggerOcs;
import com.viettel.ocs.entity.TriggerType;
import com.viettel.ocs.util.ValidateUtil;

@ManagedBean(name = "triggerOcsBean")
@ViewScoped
public class TriggerOcsBean extends BaseController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3367866221526423100L;
	private TriggerOcs triggerOcsUI;
	private TriggerType triggerTypeUI;

	private List<SelectItem> listItemCategory;

	private List<TriggerType> listTriggerTypeByCategory;
	private List<TriggerOcs> listTriggerOcsByCategory;
	private List<SelectItem> listComboTriggerType;

	private List<SelectItem> listComboModule;

	private String formType = "";
	private boolean isEditting;
	private boolean isEdittingTriggerType;
	private boolean isApply;
	private CategoryDAO categoryDAO;
	private TriggerOcsDAO triggerOcsDAO;
	private TriggerTypeDAO triggerTypeDAO;
	private long categoryID;
	private String txtTriggerOcsName;
	private String txtTriggerTypeName;
	
	private List<Category> categoriesOfTO;
	private List<Category> categoriesOfTT;

	// Init -----------------------------------------------------
	@PostConstruct
	public void init() {
		formType = "";
		triggerOcsUI = new TriggerOcs();
		triggerTypeUI = new TriggerType();
		this.isEditting = true;
		this.isEdittingTriggerType = true;
		categoryDAO = new CategoryDAO();
		triggerOcsDAO = new TriggerOcsDAO();
		triggerTypeDAO = new TriggerTypeDAO();
		this.categoryID = 0l;
		this.categoriesOfTO = new ArrayList<Category>();
		this.categoriesOfTT = new ArrayList<Category>();
		this.isApply = true;
		listTriggerOcsByCategory = new ArrayList<TriggerOcs>();
		listTriggerTypeByCategory = new ArrayList<TriggerType>();
	}

	// prepare
	// Trigger Type ---------------------------
	// load list category Trigger Type
	public List<SelectItem> loadCategoryTriggerType() {
		listItemCategory = new ArrayList<SelectItem>();
		CategoryDAO categoryDAO = new CategoryDAO();
		List<Category> listCategory = categoryDAO.findByType(CategoryType.CTL_TT_TRIGGER_TYPE);
		if (listItemCategory != null && !listCategory.isEmpty()) {
			for (Category category : listCategory) {
				listItemCategory.add(new SelectItem(category.getCategoryId(), category.getCategoryName()));
			}
		}
		return listItemCategory;
	}
	
	public void loadCategoriesOfTT() {
		categoriesOfTT = categoryDAO.findByTypeForSelectbox(CategoryType.CTL_TT_TRIGGER_TYPE);
	}

	// load list Trigger Type by categoryId
	public List<TriggerType> loadTriggerTypeByCategory(long categoryId) {
		listTriggerTypeByCategory = new ArrayList<TriggerType>();
		TriggerTypeDAO triggerTypeDAO = new TriggerTypeDAO();
		listTriggerTypeByCategory = triggerTypeDAO.findListTriggerTypeByCategoryId(categoryId);
		this.categoryID = categoryId;
		return listTriggerTypeByCategory;
	}

	// Trigger Ocs ---------------------------
	// load list category Trigger Ocs
	public List<SelectItem> loadCategoryTriggerOCS() {
		listItemCategory = new ArrayList<SelectItem>();
		CategoryDAO categoryDAO = new CategoryDAO();
		List<Category> listCategory = categoryDAO.findByType(CategoryType.CTL_TO_TRIGGER_OCS);
		if (listItemCategory != null && !listCategory.isEmpty()) {
			for (Category category : listCategory) {
				listItemCategory.add(new SelectItem(category.getCategoryId(), category.getCategoryName()));
			}
		}
		return listItemCategory;
	}
	
	public void loadCategoriesOfTO() {
		categoriesOfTO = categoryDAO.findByTypeForSelectbox(CategoryType.CTL_TO_TRIGGER_OCS);
	}

	// load list Trigger Ocs by categoryId
	public List<TriggerOcs> loadTriggerOcsByCategory(long categoryId) {
		listTriggerOcsByCategory = new ArrayList<TriggerOcs>();
		TriggerOcsDAO triggerOcsDAO = new TriggerOcsDAO();
		listTriggerOcsByCategory = triggerOcsDAO.findTOcsByCategoryId(categoryId);
		this.categoryID = categoryId;
		return listTriggerOcsByCategory;
	}

	// load list Trigger Type by categoryId
	public List<SelectItem> loadComboTriggerType() {
		listComboTriggerType = new ArrayList<SelectItem>();
		TriggerTypeDAO triggerTypeDAO = new TriggerTypeDAO();
		List<TriggerType> listTriggerTypes = triggerTypeDAO.findAll("");
		if (!listTriggerTypes.isEmpty()) {
			for (TriggerType triggerType : listTriggerTypes) {
				listComboTriggerType
						.add(new SelectItem(triggerType.getTriggerType(), triggerType.getTriggerTypeName()));
			}
		}
		return listComboTriggerType;
	}

	public List<SelectItem> loadComboModule() {
		listComboModule = new ArrayList<SelectItem>();
		listComboModule.add(new SelectItem(1, "Consume"));
		listComboModule.add(new SelectItem(2, "Reserve"));
		listComboModule.add(new SelectItem(3, "All"));
		return listComboModule;
	}

	// Action
	// Trigger Type --------------------------
	public void commandAddNewTriggerType() {
		this.isEdittingTriggerType = false;
		triggerTypeUI = new TriggerType();
		txtTriggerTypeName = "";
		triggerTypeUI.setCategoryId(categoryID);
		setFormType("detail-trigger-type");
		TreeCommonBean treeCommonBean = super.getTreeCommonBean();
		treeCommonBean.hideAllCategoryComponent();
		treeCommonBean.setContentTitle(super.readProperties("triggerType.title"));
		loadCategoriesOfTT();
		this.isApply = false;
	}

	public void commandCancelTriggerType() {
		this.isEdittingTriggerType = true;
		setFormType("list-trigger-type");
		loadTriggerTypeByCategory(triggerTypeUI.getCategoryId());
	}

	public void commandApplyTriggerType() {
		TreeCommonBean treeCommonBean = super.getTreeCommonBean();
		Category cat = categoryDAO.get(triggerTypeUI.getCategoryId());
		if (validateTriggerType()) {
			triggerTypeUI.setTriggerTypeName(txtTriggerTypeName);
			if (triggerTypeDAO.saveTriggerType(triggerTypeUI)) {
				treeCommonBean.updateTreeNode(triggerTypeUI, cat, null);
				this.isEdittingTriggerType = true;
				this.isApply = true;
				this.showMessageINFO("common.save", super.readProperties("triggerType"));
			}
		}
	}

	public void commandEditTriggerTypeTB(TriggerType item) {
		this.isEdittingTriggerType = true;
		this.triggerTypeUI = item;
		if (item.getTriggerTypeName() != null) {
			txtTriggerTypeName = item.getTriggerTypeName();
		}
		setFormType("detail-trigger-type");
		TreeCommonBean treeCommonBean = super.getTreeCommonBean();
		treeCommonBean.hideAllCategoryComponent();
		treeCommonBean.setContentTitle(super.readProperties("triggerType.title"));
		loadCategoriesOfTT();
		this.isApply = false;
	}

	public void commandRemoveTriggerTypeTB(TriggerType item) {
		TreeCommonBean treeCommonBean = super.getTreeCommonBean();
		Category cat = categoryDAO.get(item.getCategoryId());
		if (!triggerOcsDAO.checkTOcsByType(item.getTriggerType())) {
			try {
				triggerTypeDAO.delete(item);
				listTriggerTypeByCategory.remove(item);
				treeCommonBean.removeTreeNode(item, cat);
				this.isEdittingTriggerType = true;
				this.showMessageINFO("common.delete", super.readProperties("triggerType"));
			} catch (Exception e) {
				throw e;
			}
		} else {
			this.showMessageWARN("common.delete", super.readProperties("triggerType.checkChild"));
		}
	}

	// Trigger Ocs--------------------------
	public void commandAddNewTriggerOcs() {
		if (loadComboTriggerType().size() != 0) {
			this.isEditting = false;
			triggerOcsUI = new TriggerOcs();
			txtTriggerOcsName = "";
			triggerOcsUI.setCategoryId(categoryID);
			triggerOcsUI.setStatus(true);
			setFormType("detail-trigger-ocs");
			TreeCommonBean treeCommonBean = super.getTreeCommonBean();
			treeCommonBean.hideAllCategoryComponent();
			treeCommonBean.setContentTitle(super.readProperties("triggerOcs.title"));
			loadCategoriesOfTO();
			this.isApply = false;
		} else {
			this.showMessageWARN("common.save", super.readProperties("stateset.checkAddNew"));
		}

	}

	public void commandEditTriggerOcs() {
		this.isEditting = false;
	}

	public void commandCancelTriggerOcs() {
		this.isEditting = true;
		setFormType("list-trigger-ocs");
		loadTriggerOcsByCategory(triggerOcsUI.getCategoryId());
	}

	public void commandApplyTriggerOcs() {
		TreeCommonBean treeCommonBean = super.getTreeCommonBean();
		Category cat = categoryDAO.get(triggerOcsUI.getCategoryId());
		if (checkValidateTriggerOcs()) {
			triggerOcsUI.setTriggerName(txtTriggerOcsName);

			if (triggerOcsDAO.saveTriggerOcs(triggerOcsUI)) {
				treeCommonBean.updateTreeNode(triggerOcsUI, cat, null);
				this.isEditting = true;
				this.isApply = true;
				this.showMessageINFO("common.save", super.readProperties("triggerOcs"));	
			}
		}
	}

	public void commandEditTriggerOcsTB(TriggerOcs item) {
		this.isEditting = true;
		this.triggerOcsUI = item;
		if (item.getTriggerName() != null) {
			txtTriggerOcsName = item.getTriggerName();
		}
		formType = "detail-trigger-ocs";
		TreeCommonBean treeCommonBean = super.getTreeCommonBean();
		treeCommonBean.hideAllCategoryComponent();
		treeCommonBean.setContentTitle(super.readProperties("triggerOcs.title"));
		loadCategoriesOfTO();
		this.isApply = false;
	}

	public void commandRemoveTriggerOcsTB(TriggerOcs item) {
		if (checkDelTriggerOcs(item)) {
			TreeCommonBean treeCommonBean = super.getTreeCommonBean();
			Category cat = categoryDAO.get(item.getCategoryId());
			try {
				triggerOcsDAO.delete(item);
				treeCommonBean.removeTreeNode(item, cat);
				listTriggerOcsByCategory.remove(item);
				this.isEditting = true;
				this.showMessageINFO("common.delete", super.readProperties("triggerOcs"));
			} catch (Exception e) {
				getLogger().warn(e.getMessage(), e);
				super.showNotificationFail();
			}

		}
	}

	// Validation
	public boolean validateTriggerType() {
		if (ValidateUtil.checkStringNullOrEmpty(txtTriggerTypeName)) {
			this.showMessageWARN("normalizer.validateError", super.readProperties("normalizer.errorDataValueName"));
			return false;
		}
		if (triggerTypeDAO.checkName(triggerTypeUI, txtTriggerTypeName, isEdittingTriggerType)) {
			this.showMessageWARN("normalizer.validateError", super.readProperties("common.nameAlreadyExists"));
			return false;
		}
		return true;
	}

	public boolean checkValidateTriggerOcs() {
		if (ValidateUtil.checkStringNullOrEmpty(txtTriggerOcsName)) {
			this.showMessageWARN("normalizer.validateError", super.readProperties("normalizer.errorDataValueName"));
			return false;
		}
		if (triggerOcsDAO.checkName(triggerOcsUI, txtTriggerOcsName, isEditting)) {
			this.showMessageWARN("normalizer.validateError", super.readProperties("common.nameAlreadyExists"));
			return false;
		}
		return true;
	}

	public boolean checkDelTriggerOcs(TriggerOcs triggerOcs) {
		if (triggerOcsDAO.checkTrigger(triggerOcs)) {
			this.showMessageWARN("", super.readProperties("validate.fieldUseIn"));
			return false;
		}

		if (triggerOcsDAO.checkTriggerDpdFomula(triggerOcs) > 0) {
			this.showMessageWARN("", super.readProperties("validate.fieldUseIn"));
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
		TriggerOcs triggerOcs = (TriggerOcs) event.getTreeNode().getData();
		Object object = triggerOcsDAO.upDownObjectInCatWithDomain(triggerOcs, "index", isUp);
		if (object instanceof TriggerOcs) {
			TriggerOcs nextTrigger = (TriggerOcs) object;
			Category category = categoryDAO.get(nextTrigger.getCategoryId());

			TreeCommonBean treeCommonBean = super.getTreeCommonBean();
			if (isUp) {
				treeCommonBean.moveUpTreeNode((BaseEntity) triggerOcs);
			} else {
				treeCommonBean.moveDownTreeNode((BaseEntity) triggerOcs);
			}
			treeCommonBean.updateTreeNode(nextTrigger, category, null);
			if (formType == "list-trigger-ocs" && category.getCategoryId() == categoryID) {
				loadTriggerOcsByCategory(category.getCategoryId());
			}
			super.showNotificationSuccsess();
		} else {
//			super.showNotificationFail();
		}
	}
	
	// Edit ContextMenu
	public void editContextMenu(NodeSelectEvent nodeSelectEvent) {
		super.getTreeCommonBean().onNodeSelect(nodeSelectEvent);
		Object object = nodeSelectEvent.getTreeNode().getData();
		if (nodeSelectEvent != null) {
			if (object instanceof TriggerOcs) {
				TriggerOcs item = (TriggerOcs) nodeSelectEvent.getTreeNode().getData();
				commandEditTriggerOcsTB(item);
			}else if (object instanceof TriggerType) {
				TriggerType item = (TriggerType) nodeSelectEvent.getTreeNode().getData();
				commandEditTriggerTypeTB(item);
			}

		}
	}

	// Remove ContextMenu
	public void removeContextMenu(NodeSelectEvent nodeSelectEvent) {
		super.getTreeCommonBean().onNodeSelect(nodeSelectEvent);
		Object object = nodeSelectEvent.getTreeNode().getData();
		if (nodeSelectEvent != null) {
			if (object instanceof TriggerOcs) {
				TriggerOcs item = (TriggerOcs) nodeSelectEvent.getTreeNode().getData();
				commandRemoveTriggerOcsTB(item);
				loadTriggerOcsByCategory(item.getCategoryId());
				formType = "list-trigger-ocs";
			}else if (object instanceof TriggerType) {
				TriggerType item = (TriggerType) nodeSelectEvent.getTreeNode().getData();
				commandRemoveTriggerTypeTB(item);
				loadTriggerTypeByCategory(item.getCategoryId());
				formType = "list-trigger-type";
			}

		}
	}
	
	// CommandCheckDependencies ContextMenu
	public void commandCheckDependencies(NodeSelectEvent nodeSelectEvent) {
		if (nodeSelectEvent != null) {
			TriggerOcs item = (TriggerOcs) nodeSelectEvent.getTreeNode().getData();
			showDependencies(item.getTriggerOcsId(), TriggerOcs.class);
		}
	}

	/**
	 * 
	 * @param isUp
	 *            true is up
	 */
	public void moveUpDownTriggerTypeInCat(NodeSelectEvent event, boolean isUp) {
		TriggerType triggerType = (TriggerType) event.getTreeNode().getData();
		Object object = triggerOcsDAO.upDownObjectInCatWithDomain(triggerType, "index", isUp);
		if (object instanceof TriggerType) {
			TriggerType nextTrigger = (TriggerType) object;
			Category category = categoryDAO.get(nextTrigger.getCategoryId());

			TreeCommonBean treeCommonBean = super.getTreeCommonBean();
			if (isUp) {
				treeCommonBean.moveUpTreeNode((BaseEntity) triggerType);
			} else {
				treeCommonBean.moveDownTreeNode((BaseEntity) triggerType);
			}
			treeCommonBean.updateTreeNode(nextTrigger, category, null);
			if (formType == "list-trigger-type" && category.getCategoryId() == categoryID) {
				loadTriggerTypeByCategory(category.getCategoryId());
			}
			super.showNotificationSuccsess();
		} else {
//			super.showNotificationFail();
		}
	}

	// Get Set -----------------------------------------------------
	public TriggerOcs getTriggerOcsUI() {
		return triggerOcsUI;
	}

	public void setTriggerOcsUI(TriggerOcs triggerOcsUI) {
		this.triggerOcsUI = triggerOcsUI;
		if (triggerOcsUI.getTriggerName() != null) {
			txtTriggerOcsName = triggerOcsUI.getTriggerName();
		}
	}

	public TriggerType getTriggerTypeUI() {
		return triggerTypeUI;
	}

	public void setTriggerTypeUI(TriggerType triggerTypeUI) {
		this.triggerTypeUI = triggerTypeUI;
		if (triggerTypeUI.getTriggerTypeName() != null) {
			txtTriggerTypeName = triggerTypeUI.getTriggerTypeName();
		}
	}

	public List<SelectItem> getListItemCategory() {
		return listItemCategory;
	}

	public void setListItemCategory(List<SelectItem> listItemCategory) {
		this.listItemCategory = listItemCategory;
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

	public boolean isEdittingTriggerType() {
		return isEdittingTriggerType;
	}

	public void setEdittingTriggerType(boolean isEdittingTriggerType) {
		this.isEdittingTriggerType = isEdittingTriggerType;
	}

	public List<TriggerType> getListTriggerTypeByCategory() {
		return listTriggerTypeByCategory;
	}

	public void setListTriggerTypeByCategory(List<TriggerType> listTriggerTypeByCategory) {
		this.listTriggerTypeByCategory = listTriggerTypeByCategory;
	}

	public List<TriggerOcs> getListTriggerOcsByCategory() {
		return listTriggerOcsByCategory;
	}

	public void setListTriggerOcsByCategory(List<TriggerOcs> listTriggerOcsByCategory) {
		this.listTriggerOcsByCategory = listTriggerOcsByCategory;
	}

	public List<SelectItem> getListComboTriggerType() {
		return listComboTriggerType;
	}

	public void setListComboTriggerType(List<SelectItem> listComboTriggerType) {
		this.listComboTriggerType = listComboTriggerType;
	}

	public long getCategoryID() {
		return categoryID;
	}

	public void setCategoryID(long categoryID) {
		this.categoryID = categoryID;
	}

	public String getTxtTriggerOcsName() {
		return txtTriggerOcsName;
	}

	public void setTxtTriggerOcsName(String txtTriggerOcsName) {
		this.txtTriggerOcsName = txtTriggerOcsName;
	}

	public String getTxtTriggerTypeName() {
		return txtTriggerTypeName;
	}

	public void setTxtTriggerTypeName(String txttriggerTypeName) {
		this.txtTriggerTypeName = txttriggerTypeName;
	}

	public List<SelectItem> getListComboModule() {
		return listComboModule;
	}

	public void setListComboModule(List<SelectItem> listComboModule) {
		this.listComboModule = listComboModule;
	}

	public List<Category> getCategoriesOfTO() {
		return categoriesOfTO;
	}

	public void setCategoriesOfTO(List<Category> categoriesOfTO) {
		this.categoriesOfTO = categoriesOfTO;
	}

	public List<Category> getCategoriesOfTT() {
		return categoriesOfTT;
	}

	public void setCategoriesOfTT(List<Category> categoriesOfTT) {
		this.categoriesOfTT = categoriesOfTT;
	}

	public boolean isApply() {
		return isApply;
	}

	public void setApply(boolean isApply) {
		this.isApply = isApply;
	}
	
}

package com.viettel.ocs.bean.catalog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.primefaces.event.NodeSelectEvent;

import com.viettel.ocs.bean.BaseController;
import com.viettel.ocs.bean.TreeCommonBean;
import com.viettel.ocs.constant.CategoryType;
import com.viettel.ocs.constant.Normalizer;
import com.viettel.ocs.dao.CategoryDAO;
import com.viettel.ocs.dao.NormParamDAO;
import com.viettel.ocs.dao.ParameterDAO;
import com.viettel.ocs.entity.BalType;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.Parameter;
import com.viettel.ocs.entity.Threshold;
import com.viettel.ocs.entity.TriggerOcs;
import com.viettel.ocs.model.ThresHoldTrigerModel;
import com.viettel.ocs.util.ValidateUtil;

@ManagedBean(name = "parameterBean")
@ViewScoped
public class ParameterBean extends BaseController implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// UI Obj --------------------------------------------------
	private Parameter parameterUI;

	private List<SelectItem> listParamOwnerLevel;

	private List<SelectItem> listitemTypeParam;

	private List<SelectItem> listItemCategory;

	private List<Parameter> listAllParameter;

	private List<Parameter> listParameterByCategory;

	private boolean isEditting;

	private String formType = "";

	private CategoryDAO categoryDAO;

	private long categoryID;

	private ParameterDAO parameterDAO;

	private String txtParameterName;

	private String txtParameterId;

	private List<Category> categoriesOfPara;

	private boolean isApply;

	// Init -----------------------------------------------------
	@PostConstruct
	public void init() {
		this.isApply = true;
		this.isEditting = true;
		txtParameterName = "";
		txtParameterId = null;
		categoryDAO = new CategoryDAO();
		this.parameterUI = new Parameter();
		this.parameterUI.setOwnerLevel(0L);
		this.parameterUI.setParameterValue(null);
		this.categoryID = 0l;
		parameterDAO = new ParameterDAO();
		this.categoriesOfPara = new ArrayList<Category>();
		listParameterByCategory = new ArrayList<Parameter>();
	}
	
	// Prepare---------------------------------------------------
	// load list level
	public List<SelectItem> loadListLevel() {
		listParamOwnerLevel = new ArrayList<SelectItem>();
		listParamOwnerLevel
				.add(new SelectItem(Normalizer.ParamOwnerLevel.CUSTOMER, Normalizer.ParamOwnerLevel.CUSTOMER_VALUE));
		listParamOwnerLevel.add(
				new SelectItem(Normalizer.ParamOwnerLevel.SUBSCRIBER, Normalizer.ParamOwnerLevel.SUBSCRIBER_VALUE));
		listParamOwnerLevel
				.add(new SelectItem(Normalizer.ParamOwnerLevel.GROUP, Normalizer.ParamOwnerLevel.GROUP_VALUE));
		listParamOwnerLevel.add(
				new SelectItem(Normalizer.ParamOwnerLevel.MEMBERSHIP, Normalizer.ParamOwnerLevel.MEMBERSHIP_VALUE));
		listParamOwnerLevel
				.add(new SelectItem(Normalizer.ParamOwnerLevel.OFFER, Normalizer.ParamOwnerLevel.OFFER_VALUE));
		listParamOwnerLevel
				.add(new SelectItem(Normalizer.ParamOwnerLevel.SYSTEM, Normalizer.ParamOwnerLevel.SYSTEM_VALUE));
		return listParamOwnerLevel;

	}

	// load list type
	public List<SelectItem> loadListType() {
		listitemTypeParam = new ArrayList<SelectItem>();
		listitemTypeParam.add(new SelectItem(Normalizer.ParamType.QUOTA, Normalizer.ParamType.QUOTA_VALUE));
		listitemTypeParam.add(new SelectItem(Normalizer.ParamType.TEMPLATE, Normalizer.ParamType.TEMPLATE_VALUE));
		listitemTypeParam.add(new SelectItem(Normalizer.ParamType.CBA100, Normalizer.ParamType.CBA100_VALUE));
		listitemTypeParam.add(new SelectItem(Normalizer.ParamType.NORMAL, Normalizer.ParamType.NORMAL_VALUE));
		return listitemTypeParam;

	}

	// load list category by type parameter
	public List<SelectItem> loadCategory() {
		listItemCategory = new ArrayList<SelectItem>();
		CategoryDAO categoryDAO = new CategoryDAO();
		List<Category> listCategory = categoryDAO.findByTypeForSelectbox(CategoryType.CTL_PARAMETER);
		if (listItemCategory != null && !listCategory.isEmpty()) {
			for (Category category : listCategory) {
				listItemCategory.add(new SelectItem(category.getCategoryId(), category.getCategoryName()));
			}
		}
		return listItemCategory;
	}

	public void loadCategoriesOfPara() {
		categoriesOfPara = categoryDAO.findByTypeForSelectbox(CategoryType.CTL_PARAMETER);
	}

	// load list list Parameter by categoryId
	public List<Parameter> loadParameterByCategory(long categoryId) {
		listParameterByCategory = new ArrayList<Parameter>();
		listParameterByCategory = parameterDAO.findParamByConditions(categoryId);
		this.categoryID = categoryId;
		return listParameterByCategory;
	}

	// Action
	public void commandAddNew() {
		this.isApply = false;
		this.isEditting = false;
		clearText();
		setFormType("detail-parameter");
		parameterUI.setCategoryId(categoryID);
		loadCategoriesOfPara();
		TreeCommonBean treeCommonBean = super.getTreeCommonBean();
		treeCommonBean.hideAllCategoryComponent();
		treeCommonBean.setContentTitle(super.readProperties("parameter.title"));
	}

	public void commandCancel() {
		loadParameterByCategory(parameterUI.getCategoryId());
		setFormType("list-parameter-by-category");
		this.isEditting = true;
	}

	public void commandSaveOrUpdate() {
		TreeCommonBean treeCommonBean = super.getTreeCommonBean();
		Category cat = categoryDAO.get(parameterUI.getCategoryId());
		if (validateParamter()) {
			if (parameterUI.getParameterId() == 0) {
				Parameter parameterMaxId = parameterDAO.findParameterLastID();
				if (parameterMaxId != null) {
					parameterUI.setParameterId(parameterMaxId.getParameterId() + 1);
				} else {
					parameterUI.setParameterId(1l);
				}
			}
			if (parameterUI.getParameterValue().equalsIgnoreCase("")) {
				parameterUI.setParameterValue(null);
			}
			parameterUI.setParameterName(txtParameterName);
			if (parameterDAO.saveParam(parameterUI)) {
				txtParameterId = parameterUI.getParameterId() + "";
				this.showMessageINFO("validate.saveSuccess", super.readProperties(""));
				this.isEditting = true;
				this.isApply = true;
				// commandCancel();
				treeCommonBean.updateTreeNode(parameterUI, cat, null);
			}
		}
	}

	public void commandRemove() {
		parameterDAO.delete(parameterUI);
		this.isEditting = true;
		this.showMessageINFO("validate.deleteSuccess", super.readProperties(""));
	}

	public void commandEditTable(Parameter item) {
		this.isApply = false;
		this.isEditting = true;
		setParameterUI(item);
		setFormType("detail-parameter");
		loadCategoriesOfPara();
		TreeCommonBean treeCommonBean = super.getTreeCommonBean();
		treeCommonBean.hideAllCategoryComponent();
		treeCommonBean.setContentTitle(super.readProperties("parameter.title"));
	}

	public void commandRemoveTable(Parameter item) {
		NormParamDAO normParamDAO = new NormParamDAO();
		if (!parameterDAO.checkParameterBlock(item) && !normParamDAO.checkParameterInNP(item.getParameterId())) {
			this.isEditting = true;
			clearText();
			TreeCommonBean treeCommonBean = super.getTreeCommonBean();
			Category cat = categoryDAO.get(item.getCategoryId());
			try {
				parameterDAO.delete(item);
				listParameterByCategory.remove(item);
				treeCommonBean.removeTreeNode(item, cat);
				this.showMessageINFO("validate.deleteSuccess", super.readProperties(""));
			} catch (Exception e) {
				getLogger().warn(e.getMessage(), e);
				throw e;
			}

		} else {
			this.showMessageWARN("parameter", super.readProperties("validate.fieldUseIn"));
		}

	}

	public void clearText() {
		this.parameterUI = new Parameter();
		txtParameterName = "";
		txtParameterId = "";
		this.parameterUI.setParameterId(0);
		this.parameterUI.setParameterName("");
		this.parameterUI.setOwnerLevel(0L);
	}

	public String loadData() {
		return "parameter.xhtml";
	}

	// Validate
	private boolean validateParamter() {
		if (ValidateUtil.checkStringNullOrEmpty(txtParameterId)) {
			parameterUI.setParameterId(0L);
		} else {
			parameterUI.setParameterId(Long.parseLong(txtParameterId));
		}
		if (ValidateUtil.checkStringNullOrEmpty(txtParameterName)) {
			this.showMessageWARN("parameter", super.readProperties("validate.checkValueNameNull"));
			return false;
		}
		if (parameterDAO.checkName(parameterUI, txtParameterName, isEditting)) {
			this.showMessageWARN("parameter", super.readProperties("validate.checkValueNameExist"));
			return false;
		}
		if (parameterDAO.checkId(parameterUI, isEditting)) {
			this.showMessageWARN("parameter", super.readProperties("validate.checkObjectExist"));
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
		Parameter parameter = (Parameter) event.getTreeNode().getData();
		Object object = parameterDAO.upDownObjectInCatWithDomain(parameter, "index", isUp);
		if (object instanceof Parameter) {
			Category category = categoryDAO.get(parameter.getCategoryId());
			Parameter nextParameter = (Parameter) object;

			TreeCommonBean treeCommonBean = super.getTreeCommonBean();
			if (isUp) {
				treeCommonBean.moveUpTreeNode(parameter);
			} else {
				treeCommonBean.moveDownTreeNode(parameter);
			}
			treeCommonBean.updateTreeNode(nextParameter, category, null);
			if (formType == "list-parameter-by-category" && category.getCategoryId() == categoryID) {
				loadParameterByCategory(category.getCategoryId());
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
			Parameter item = (Parameter) nodeSelectEvent.getTreeNode().getData();
			commandEditTable(item);
		}
	}

	// Remove ContextMenu
	public void removeContextMenu(NodeSelectEvent nodeSelectEvent) {
		super.getTreeCommonBean().onNodeSelect(nodeSelectEvent);
		if (nodeSelectEvent != null) {
			Parameter item = (Parameter) nodeSelectEvent.getTreeNode().getData();
			commandRemoveTable(item);
			loadParameterByCategory(item.getCategoryId());
			formType = "list-parameter-by-category";
		}
	}
	
	public void cloneContextMenu(NodeSelectEvent nodeSelectEvent) throws CloneNotSupportedException {
		Parameter parameter = (Parameter) nodeSelectEvent.getTreeNode().getData();
		commandCloneTable(parameter);
	}
	
	public void commandCloneTable(Parameter parameter) throws CloneNotSupportedException {
		if (parameter != null) {
			Parameter parameterCloned = parameterDAO.cloneParameter(parameter, "_Cloned");
			if (parameterCloned != null) {
				commandEditTable(parameterCloned);
				super.getTreeCommonBean().setParameterProperties(false, parameterCloned.getParameterId(),parameterCloned, false);
				Category cat = categoryDAO.get(parameterCloned.getCategoryId());
				TreeCommonBean treeCommonBean = super.getTreeCommonBean();
				treeCommonBean.updateTreeNode(parameterCloned, cat, null);
				this.isApply = false;
				this.isEditting = true;
				this.showMessageINFO("common.clone", " Balances ");
			}
		}
	}

	// Get-set ---------------------------------------------------------
	public Parameter getParameterUI() {
		return parameterUI;
	}

	public void setParameterUI(Parameter parameterUI) {
		if (parameterUI.getParameterName() != null) {
			txtParameterName = parameterUI.getParameterName();
		}
		txtParameterId = String.valueOf(parameterUI.getParameterId());
		this.parameterUI = parameterUI;
	}

	public List<SelectItem> getListParamOwnerLevel() {
		return listParamOwnerLevel;
	}

	public void setListParamOwnerLevel(List<SelectItem> listParamOwnerLevel) {
		this.listParamOwnerLevel = listParamOwnerLevel;
	}

	public List<SelectItem> getListitemTypeParam() {
		return listitemTypeParam;
	}

	public void setListitemTypeParam(List<SelectItem> listitemTypeParam) {
		this.listitemTypeParam = listitemTypeParam;
	}

	public List<Parameter> getListParameterByCategory() {
		return listParameterByCategory;
	}

	public void setListParameterByCategory(List<Parameter> listParameterByCategory) {
		this.listParameterByCategory = listParameterByCategory;
	}

	public List<Parameter> getListAllParameter() {
		return listAllParameter;
	}

	public void setListAllParameter(List<Parameter> listAllParameter) {
		this.listAllParameter = listAllParameter;
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

	public long getCategoryID() {
		return categoryID;
	}

	public void setCategoryID(long categoryID) {
		this.categoryID = categoryID;
	}

	public String getTxtParameterName() {
		return txtParameterName;
	}

	public void setTxtParameterName(String txtParameterName) {
		this.txtParameterName = txtParameterName;
	}

	public String getTxtParameterId() {
		return txtParameterId;
	}

	public void setTxtParameterId(String txtParameterId) {
		this.txtParameterId = txtParameterId;
	}

	public List<Category> getCategoriesOfPara() {
		return categoriesOfPara;
	}

	public void setCategoriesOfPara(List<Category> categoriesOfPara) {
		this.categoriesOfPara = categoriesOfPara;
	}

	public boolean isApply() {
		return isApply;
	}

	public void setApply(boolean isApply) {
		this.isApply = isApply;
	}

}

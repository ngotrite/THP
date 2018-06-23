package com.viettel.ocs.bean.catalog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;

import com.viettel.ocs.bean.BaseController;
import com.viettel.ocs.bean.TreeCommonBean;
import com.viettel.ocs.constant.CategoryType;
import com.viettel.ocs.constant.TreeType;
import com.viettel.ocs.dao.CategoryDAO;
import com.viettel.ocs.dao.CdrPropDAO;
import com.viettel.ocs.dao.CdrServiceDAO;
import com.viettel.ocs.dao.CdrTemplateDAO;
import com.viettel.ocs.dao.CdrTemplatePropDAO;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.CdrProp;
import com.viettel.ocs.entity.CdrService;
import com.viettel.ocs.entity.CdrTemplate;
import com.viettel.ocs.entity.CdrTemplateProp;
import com.viettel.ocs.entity.Offer;
import com.viettel.ocs.entity.RowDt;
import com.viettel.ocs.entity.UnitType;
import com.viettel.ocs.util.ValidateUtil;

@ManagedBean(name = "cdrTemplateBean")
@ViewScoped
public class CdrTemplateBean extends BaseController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3798969909981627580L;

	private CdrTemplate cdrTemplate;
	private CdrTemplateProp cdrTemplateProp;
	private Category category;

	private List<CdrTemplate> listCdrTemplate;
	private List<Category> listCategory;
	private List<CdrTemplateProp> listCdrTemplateProp;
	private List<SelectItem> listSelectProperty;
	private Long categoryId;
	private String selectedCdrServiceName;
	private String formType;

	private CategoryDAO categoryDAO;
	private CdrTemplateDAO cdrTemplateDAO;
	private CdrTemplatePropDAO cdrTemplatePropDAO;
	private CdrServiceDAO cdrServiceDAO;
	private CdrPropDAO cdrPropDAO;
	private Category catParentDump;
	private Boolean editRowTemplateProp;
	private List<CdrProp> lstProp;
	private Boolean editting;

	@PostConstruct
	public void init() {
		setCategoryDAO(new CategoryDAO());
		setCategory(new Category());
		this.cdrPropDAO = new CdrPropDAO();
		this.cdrServiceDAO = new CdrServiceDAO();
		this.cdrTemplateDAO = new CdrTemplateDAO();
		this.cdrTemplatePropDAO = new CdrTemplatePropDAO();
		this.listCdrTemplate = new ArrayList<CdrTemplate>();
		this.listCategory = new ArrayList<>();
		this.listCdrTemplateProp = new ArrayList<>();
		this.listSelectProperty = new ArrayList<>();
		lstProp = new ArrayList<>();
		catParentDump = new Category();
		catParentDump.setCategoryName("");
		clearData();
		editRowTemplateProp = false;
		editting = false;
	}

	public String codeOfCdrService(CdrTemplate item) {
		String name = "";
		if (item != null && item.getCdrServiceId() != null) {
			CdrService sv = cdrServiceDAO.get(item.getCdrServiceId());
			if (sv != null) {
				name = sv.getCdrServiceCode();
			}
		}
		return name;
	}

	public void clearData() {
		this.cdrTemplateProp = new CdrTemplateProp();
		this.cdrTemplateProp.setDomainId(cdrTemplatePropDAO.getDomainId());
		this.cdrTemplate = new CdrTemplate();
		this.cdrTemplate.setDomainId(cdrTemplateDAO.getDomainId());
		this.cdrTemplate.setCategoryId(getCategoryId());
		this.selectedCdrServiceName = null;
		this.listCdrTemplateProp = new ArrayList<>();
		this.listCdrTemplate = new ArrayList<>();
	}

	public void editTemplateProp(CdrTemplateProp item) {
		if (listCdrTemplateProp != null) {
			for (int i = 0; i < listCdrTemplateProp.size(); i++) {
				listCdrTemplateProp.get(i).setEditRow(false);
			}
			item.setEditRow(true);
		}
	}

	public void lockTemplateProp(CdrTemplateProp item) {
		item.setEditRow(false);
	}

	public void removeTemplateProp(CdrTemplateProp item) {
		if (listCdrTemplateProp != null) {
			this.listCdrTemplateProp.remove(item);
		}
	}

	public void addNewTemplate() {
		this.formType = "detail";
		clearData();
		TreeCommonBean treeCommonBean = getTreeCommonBean();
//		treeCommonBean.setCdrTemplateProperties(false, null, this.cdrTemplate);
		treeCommonBean.hideAllCategoryComponent();
		treeCommonBean.setContentTitle(super.readProperties("title.CdrTemplate"));		
		getLoadComboListCategory();
		setEditting(true);
	}

	private boolean insert = false;
	private CdrTemplateProp selectedProp;

	public void addItemTable() {
		selectedProp = null;
		insert = false;
		openCdrPropertiesDlg();
	}

	private void openCdrPropertiesDlg() {
		Map<String, Object> options = new HashMap<String, Object>();
		options.put("modal", true);
		options.put("widgetVar", "dlgTree");
		options.put("width", 1000);
		options.put("height", 450);
		options.put("resizable", false);
		options.put("contentWidth", "100%");
		options.put("contentHeight", "100%");
		Map<String, List<String>> mapPara = new HashMap<String, List<String>>();
		List<String> selectMode = new ArrayList<String>();
		selectMode.add("multiple");
		mapPara.put("selectMode", selectMode);
		String selectedIds = "-1";
		if(listCdrTemplateProp != null) {
			for(CdrTemplateProp pro: listCdrTemplateProp) {
				selectedIds += "," + pro.getCdrPropId();
			}
		}
		List<String> lstIds = new ArrayList<String>();
		lstIds.add(selectedIds);
		mapPara.put("selectedIds", lstIds);
		RequestContext.getCurrentInstance().openDialog("/pages/catalog/cdr_properties_dialog.xhtml", options, mapPara);
	}

	public void changeItemTable(CdrTemplateProp cdrTemplateProp) {
		selectedProp = cdrTemplateProp;
		insert = false;
		openCdrPropertiesDlg();
//		Map<String, Object> options = new HashMap<String, Object>();
//		options.put("modal", true);
//		options.put("widgetVar", "dlgTree");
//		options.put("width", 1000);
//		options.put("height", 450);
//		options.put("resizable", false);
//		options.put("contentWidth", "100%");
//		options.put("contentHeight", "100%");
//		Map<String, List<String>> mapPara = new HashMap<String, List<String>>();
//		List<String> selectMode = new ArrayList<String>();
//		selectMode.add("single");
//		mapPara.put("selectMode", selectMode);
//		RequestContext.getCurrentInstance().openDialog("/pages/catalog/cdr_properties_dialog.xhtml", options, mapPara);
	}

	public void addPropAt(CdrTemplateProp cdrTemplateProp) {
		selectedProp = cdrTemplateProp;
		insert = true;
		openCdrPropertiesDlg();
//		Map<String, Object> options = new HashMap<String, Object>();
//		options.put("modal", true);
//		options.put("widgetVar", "dlgTree");
//		options.put("width", 1000);
//		options.put("height", 450);
//		options.put("resizable", false);
//		options.put("contentWidth", "100%");
//		options.put("contentHeight", "100%");
//		Map<String, List<String>> mapPara = new HashMap<String, List<String>>();
//		List<String> selectMode = new ArrayList<String>();
//		selectMode.add("multiple");
//		mapPara.put("selectMode", selectMode);
//		RequestContext.getCurrentInstance().openDialog("/pages/catalog/cdr_properties_dialog.xhtml", options, mapPara);
	}

	public void onDialogPropertiesReturn(SelectEvent event) {
		Object object = event.getObject();
		if (object instanceof List<?>) {
			List<CdrProp> cdrProps = (List<CdrProp>) object;
			for (CdrProp cdrProp : cdrProps) {
				if (isValidate(cdrProp)) {
					CdrTemplateProp cdrTempProp = new CdrTemplateProp();
					cdrTempProp.setDomainId(cdrTemplatePropDAO.getDomainId());
					cdrTempProp.setCdrPropId(cdrProp.getCdrPropId());
					cdrTempProp.setCdrTemplateId(this.cdrTemplate.getCdrTemplateId());
					cdrTempProp.setPropName(cdrProp.getPropName());
					cdrTempProp.setEditRow(true);
					if (selectedProp != null) {
						if (insert) {
							this.listCdrTemplateProp.add(listCdrTemplateProp.indexOf(selectedProp) + 1, cdrTempProp);
						} else {
							this.listCdrTemplateProp.set(listCdrTemplateProp.indexOf(selectedProp), cdrTempProp);
							break;
						}
					} else {
						this.listCdrTemplateProp.add(cdrTempProp);
					}
				} else {
					super.showNotification(FacesMessage.SEVERITY_WARN, "common.duplicate", "");
					break;
				}
			}
		}
	}

	private boolean isValidate(CdrProp cdrProp) {
		for (CdrTemplateProp item : listCdrTemplateProp) {
			if (item.getCdrPropId().equals(cdrProp.getCdrPropId())) {
				return false;
			}
		}
		return true;
	}

	public void changePropertySetting(CdrTemplateProp item) {
		item.setPropName(cdrPropDAO.get(item.getCdrPropId()).getPropName());
	}

	public void moveUpProperty(CdrTemplateProp item) {
		int index = listCdrTemplateProp.indexOf(item);
		if (index > 0) {
			try {
				CdrTemplateProp PretemplateProp = listCdrTemplateProp.get(index - 1).clone();
				listCdrTemplateProp.set(index - 1, item.clone());
				listCdrTemplateProp.set(index, PretemplateProp.clone());
			} catch (CloneNotSupportedException e) {
				getLogger().warn(e.getMessage(), e);
			}
		}
	}

	public void moveDownProperty(CdrTemplateProp item) {
		int index = listCdrTemplateProp.indexOf(item);
		if (index < listCdrTemplateProp.size() - 1 && index > -1) {
			try {
				CdrTemplateProp PretemplateProp = listCdrTemplateProp.get(index + 1).clone();
				listCdrTemplateProp.set(index + 1, item.clone());
				listCdrTemplateProp.set(index, PretemplateProp.clone());
			} catch (CloneNotSupportedException e) {
				getLogger().warn(e.getMessage(), e);
			}
		}
	}

	public void submitTemplate() {
		if (validateProp()) {
			if (!checkTemplateCode(this.cdrTemplate)) {
				if (cdrTemplateDAO.saveCdrTemplateDetail(this.cdrTemplate, this.listCdrTemplateProp)) {
					TreeCommonBean treeCommonBean = super.getTreeCommonBean();
					treeCommonBean.updateTreeNode(cdrTemplate, categoryDAO.get(this.cdrTemplate.getCategoryId()), null);
					setEditting(false);
					this.showMessageINFO("common.save", " CDR Template");	
				}
			} else {
				this.showMessageWARN("common.save", this.readProperties("cdrProperties.validateError"),
						this.readProperties("cdrTemplate.CodeIsExist"));
			}
		}
	}

	private boolean checkTemplateCode(CdrTemplate cdrTemplate) {
		int count = cdrTemplateDAO.countCdrTemplate(cdrTemplate);
		if (count > 0)
			return true;
		else
			return false;
	}

	public Boolean validateProp() {
		boolean result = true;
		FacesContext context = FacesContext.getCurrentInstance();
		if (this.cdrTemplate.getCdrServiceId() == null) {
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_WARN, this.readProperties("cdrProperties.validateError"),
							this.readProperties("cdrTemplate.cdrServiceNull")));
			result = false;
		}
		return result;
	}

	public void editCdrTemplate(CdrTemplate item) {
		setFormType("detail");
		setCategoryId(item.getCategoryId());
		TreeCommonBean treeCommonBean = getTreeCommonBean();
//		treeCommonBean.setCdrTemplateProperties(false, null, item);
		treeCommonBean.hideAllCategoryComponent();
		treeCommonBean.setContentTitle(super.readProperties("title.CdrTemplate"));
		setDataByTreeNode(item);
		getLoadComboListCategory();
		
		setEditting(true);
	}

	public void deleteCdrTemplate(CdrTemplate item) {
		if (item != null) {
			if (cdrTemplateDAO.deleteCdrTemplate(item)) {
				TreeCommonBean treeCommonBean = super.getTreeCommonBean();
				treeCommonBean.removeTreeNodeAll(item);
				this.listCdrTemplate.remove(item);
				this.showMessageINFO("common.delete", " CDR Template");
			} else {
				FacesContext context = FacesContext.getCurrentInstance();
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
						this.readProperties("cdrService.deleteError"), this.readProperties("cdrService.recordInUsed")));
			}
		}
	}

	public List<CdrTemplate> getListCdrTemplateByCategoryId(Long categoryId) {
		listCdrTemplate = new ArrayList<>();
		return listCdrTemplate = cdrTemplateDAO.loadListCdrTemplate(categoryId);
	}

	// set Data khi click node CdrService Detail
	public void setDataByTreeNode(CdrTemplate item) {
		this.categoryId = item.getCategoryId();
		this.cdrTemplate = item;
		if (cdrServiceDAO.get(item.getCdrServiceId()) != null) {
			CdrService cdrService = cdrServiceDAO.get(item.getCdrServiceId());
			if (cdrService != null)
				this.selectedCdrServiceName = cdrService.getCdrServiceCode();
		}
		getLoadComboListCategory();
		loadDataForListSelectProperty();
		loadDataForTableTemplateProperties();

	}

	// load data for tableTemplateProperties
	public void loadDataForTableTemplateProperties() {
		if (this.cdrTemplate != null) {
			this.listCdrTemplateProp = cdrTemplatePropDAO.findByTemplateID(this.cdrTemplate.getCdrTemplateId());
		}
	}

	public void getLoadComboListCategory() {
		listCategory = new ArrayList<>();
		listCategory = categoryDAO.findByTypeForSelectbox(CategoryType.CTL_CDR_TEMPLATE);
		listCategory.add(0, catParentDump);
	}

	public void loadDataForListSelectProperty() {
		lstProp = cdrPropDAO.findAll("");
		this.listSelectProperty = new ArrayList<>();
		if (lstProp.size() > 0) {
			for (CdrProp cp : lstProp) {
				this.listSelectProperty.add(new SelectItem(cp.getCdrPropId(), cp.getPropName()));
			}
		}
	}

	public void chooseServieCode() {
		super.openTreeCommonDialog(TreeType.CATALOG_CDR_SERVICE, readProperties("title.CdrService"), 0, false, null);
	}

	public void onDialogServieCodeReturn(SelectEvent event) {
		Object object = event.getObject();
		if (object instanceof CdrService) {
			CdrService cdrService = (CdrService) object;
			this.cdrTemplate.setCdrServiceId(cdrService.getCdrServiceId());
			setSelectedCdrServiceName(cdrService.getCdrServiceCode());
		}
	}

	public void deleteServieCode() {
		this.cdrTemplate.setCdrServiceId(null);
		setSelectedCdrServiceName("");
	}
	
	/**
	 * 
	 * @param isUp
	 *            true is up
	 */
	public void moveUpDownInCat(NodeSelectEvent event, boolean isUp) {
		CdrTemplate parameter = (CdrTemplate) event.getTreeNode().getData();
		Object object = cdrTemplateDAO.upDownObjectInCatWithDomain(parameter, "index", isUp);
		if (object instanceof CdrTemplate) {
			Category category = categoryDAO.get(parameter.getCategoryId());
			CdrTemplate nextParameter = (CdrTemplate) object;

			TreeCommonBean treeCommonBean = super.getTreeCommonBean();
			if (isUp) {
				treeCommonBean.moveUpTreeNode(parameter);
			} else {
				treeCommonBean.moveDownTreeNode(parameter);
			}
			treeCommonBean.updateTreeNode(nextParameter, category, null);
			if (formType == "category" && category.getCategoryId() == categoryId) {
				getListCdrTemplateByCategoryId(category.getCategoryId());
			}

			super.showNotificationSuccsess();
		}
	}
	
	// Edit ContextMenu
	public void editContextMenu(NodeSelectEvent nodeSelectEvent) {
		super.getTreeCommonBean().onNodeSelect(nodeSelectEvent);
		if (nodeSelectEvent != null) {
			CdrTemplate item = (CdrTemplate) nodeSelectEvent.getTreeNode().getData();
			editCdrTemplate(item);
		}
	}

	// Remove ContextMenu
	public void removeContextMenu(NodeSelectEvent nodeSelectEvent) {
		super.getTreeCommonBean().onNodeSelect(nodeSelectEvent);
		if (nodeSelectEvent != null) {
			CdrTemplate item = (CdrTemplate) nodeSelectEvent.getTreeNode().getData();
			deleteCdrTemplate(item);
			getListCdrTemplateByCategoryId(item.getCategoryId());
			formType = "category";
		}
	}

	// GET SET

	public CdrTemplate getCdrTemplate() {
		return cdrTemplate;
	}

	public void setCdrTemplate(CdrTemplate cdrTemplate) {
		this.cdrTemplate = cdrTemplate;
	}

	public List<CdrTemplate> getListCdrTemplate() {
		return listCdrTemplate;
	}

	public void setListCdrTemplate(List<CdrTemplate> listCdrTemplate) {
		this.listCdrTemplate = listCdrTemplate;
	}

	public CategoryDAO getCategoryDAO() {
		return categoryDAO;
	}

	public void setCategoryDAO(CategoryDAO categoryDAO) {
		this.categoryDAO = categoryDAO;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public String getSelectedCdrServiceName() {
		return selectedCdrServiceName;
	}

	public void setSelectedCdrServiceName(String selectedCdrServiceName) {
		this.selectedCdrServiceName = selectedCdrServiceName;
	}

	public CdrTemplateDAO getCdrTemplateDAO() {
		return cdrTemplateDAO;
	}

	public void setCdrTemplateDAO(CdrTemplateDAO cdrTemplateDAO) {
		this.cdrTemplateDAO = cdrTemplateDAO;
	}

	public CdrTemplatePropDAO getCdrTemplatePropDAO() {
		return cdrTemplatePropDAO;
	}

	public void setCdrTemplatePropDAO(CdrTemplatePropDAO cdrTemplatePropDAO) {
		this.cdrTemplatePropDAO = cdrTemplatePropDAO;
	}

	public CdrTemplateProp getCdrTemplateProp() {
		return cdrTemplateProp;
	}

	public void setCdrTemplateProp(CdrTemplateProp cdrTemplateProp) {
		this.cdrTemplateProp = cdrTemplateProp;
	}

	public String getFormType() {
		return formType;
	}

	public void setFormType(String formType) {
		this.formType = formType;
	}

	public List<Category> getListCategory() {
		return listCategory;
	}

	public void setListCategory(List<Category> listCategory) {
		this.listCategory = listCategory;
	}

	public CdrServiceDAO getCdrServiceDAO() {
		return cdrServiceDAO;
	}

	public void setCdrServiceDAO(CdrServiceDAO cdrServiceDAO) {
		this.cdrServiceDAO = cdrServiceDAO;
	}

	public List<CdrTemplateProp> getListCdrTemplateProp() {
		return listCdrTemplateProp;
	}

	public void setListCdrTemplateProp(List<CdrTemplateProp> listCdrTemplateProp) {
		this.listCdrTemplateProp = listCdrTemplateProp;
	}

	public CdrPropDAO getCdrPropDAO() {
		return cdrPropDAO;
	}

	public void setCdrPropDAO(CdrPropDAO cdrPropDAO) {
		this.cdrPropDAO = cdrPropDAO;
	}

	public List<SelectItem> getListSelectProperty() {
		return listSelectProperty;
	}

	public void setListSelectProperty(List<SelectItem> listSelectProperty) {
		this.listSelectProperty = listSelectProperty;
	}

	public Boolean getEditRowTemplateProp() {
		return editRowTemplateProp;
	}

	public void setEditRowTemplateProp(Boolean editRowTemplateProp) {
		this.editRowTemplateProp = editRowTemplateProp;
	}

	public Boolean getEditting() {
		return editting;
	}

	public void setEditting(Boolean editting) {
		this.editting = editting;
	}

}

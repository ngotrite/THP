package com.viettel.ocs.bean.catalog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import com.mysql.cj.fabric.xmlrpc.base.Array;
import com.viettel.ocs.bean.BaseController;
import com.viettel.ocs.bean.TreeCommonBean;
import com.viettel.ocs.constant.CategoryType;
import com.viettel.ocs.constant.CdrType.CdrPaymentType;
import com.viettel.ocs.constant.TreeType;
import com.viettel.ocs.dao.CategoryDAO;
import com.viettel.ocs.dao.CdrGenFilenameDAO;
import com.viettel.ocs.dao.CdrPropDAO;
import com.viettel.ocs.dao.CdrServiceDAO;
import com.viettel.ocs.dao.CdrTemplateDAO;
import com.viettel.ocs.dao.CdrTemplatePropDAO;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.CdrGenFilename;
import com.viettel.ocs.entity.CdrProp;
import com.viettel.ocs.entity.CdrService;
import com.viettel.ocs.entity.CdrTemplate;
import com.viettel.ocs.entity.CdrTemplateProp;
import com.viettel.ocs.entity.PccRule;
import com.viettel.ocs.entity.UnitType;
import com.viettel.ocs.util.ValidateUtil;

@ManagedBean(name = "cdrGenFileNameBean")
@ViewScoped
public class CdrGenFileNameBean extends BaseController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3798969909981629680L;

	private String cdrTemplateName;
	private Category category;
	private CdrGenFilename cdrGenFileName;

	private List<Category> listCategory;
	private List<CdrGenFilename> listCdrGenFileName;
	private String formType;
	private Long categoryId;

	private CategoryDAO categoryDAO;
	private CdrTemplateDAO cdrTemplateDAO;
	private CdrTemplatePropDAO cdrTemplatePropDAO;
	private CdrGenFilenameDAO cdrGenFilenameDAO;
	private Category catParentDump;
	private Boolean editting;

	@PostConstruct
	public void init() {
		setCategoryDAO(new CategoryDAO());
		setCategory(new Category());
		this.cdrTemplateDAO = new CdrTemplateDAO();
		this.cdrTemplatePropDAO = new CdrTemplatePropDAO();
		this.cdrGenFilenameDAO = new CdrGenFilenameDAO();
		this.listCategory = new ArrayList<>();
		this.listCdrGenFileName = new ArrayList<>();
		catParentDump = new Category();
		catParentDump.setCategoryName("");
		clearData();
		getLoadComboListCategory();
		editting = false;
	}

	public void clearData() {
		this.cdrGenFileName = new CdrGenFilename();
		this.cdrGenFileName.setDomainId(cdrGenFileName.getDomainId());
		this.cdrTemplateName = null;
		this.cdrGenFileName.setCategoryId(categoryId);
	}

	public void addNewGenFileName() {
		clearData();
		TreeCommonBean treeCommonBean = getTreeCommonBean();
		// treeCommonBean.setCdrGenFileNameProperties(false, null,
		// this.cdrGenFileName);
		treeCommonBean.hideAllCategoryComponent();
		treeCommonBean.setContentTitle(super.readProperties("title.CdrGenFileName"));
		setFormType("detail");
		getLoadComboListCategory();
		setEditting(true);
	}

	public void submitGenFileName() {
		if (validateProp()) {
			if (cdrGenFilenameDAO.saveGenFileName(this.cdrGenFileName)) {
				TreeCommonBean treeCommonBean = super.getTreeCommonBean();
				treeCommonBean.updateTreeNode(cdrGenFileName, categoryDAO.get(cdrGenFileName.getCategoryId()), null);
				setEditting(false);
				this.showMessageINFO("common.save", " CDR Gen File Name");
			} else {
				this.showMessageERROR("common.save", " CDR Gen File Name");
			}
		}
	}

	public Boolean validateProp() {
		boolean result = true;
		FacesContext context = FacesContext.getCurrentInstance();
		if (cdrGenFilenameDAO.checkFieldIsExist("name", cdrGenFileName.getName(), cdrGenFileName)) {
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_WARN, this.readProperties("cdrProperties.validateError"),
							this.readProperties("cdrGenFileName.NameIsExist")));
			result = false;
		}
		return result;
	}

	public void editCdrGenFileName(CdrGenFilename item) {
		setCategoryId(item.getCategoryId());
		this.cdrGenFileName = item;
		TreeCommonBean treeCommonBean = getTreeCommonBean();
		// treeCommonBean.setCdrGenFileNameProperties(false, null,
		// this.cdrGenFileName);
		treeCommonBean.hideAllCategoryComponent();
		treeCommonBean.setContentTitle(super.readProperties("title.CdrGenFileName"));
		setFormType("detail");
		setDataByTreeNode(item);

		if (item.getTemplateId() != null) {
			CdrTemplate cdrTemplate = cdrTemplateDAO.get(item.getTemplateId());
			cdrTemplateName = cdrTemplate.getTemplateCode();
		}
		setEditting(true);
	}

	public void deleteCdrGenFileName(CdrGenFilename item) {
		if (item != null) {
			if (cdrGenFilenameDAO.deleteCdrGenFilename(item)) {
				TreeCommonBean treeCommonBean = super.getTreeCommonBean();
				treeCommonBean.removeTreeNodeAll(item);
				this.listCdrGenFileName.remove(item);
				this.showMessageINFO("common.delete", " CDR Gen File Name");
			} else {
				FacesContext context = FacesContext.getCurrentInstance();
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
						this.readProperties("cdrService.deleteError"), this.readProperties("cdrService.recordInUsed")));
			}
		}
	}

	public List<CdrGenFilename> getListCdrGenFileNameByCategoryId(Long categoryId, int type) {
		if (listCdrGenFileName.size() <= 0 || type == 1) {
			return listCdrGenFileName = cdrGenFilenameDAO.loadListCdrGenFileName(categoryId);
		}
		return listCdrGenFileName;
	}

	// set Data khi click node CdrService Detail
	public void setDataByTreeNode(CdrGenFilename item) {
		this.cdrGenFileName = item;
		this.categoryId = item.getCategoryId();
		getLoadComboListCategory();
	}

	// load ListCategory cho combobox

	public void getLoadComboListCategory() {
		listCategory = new ArrayList<>();
		listCategory = categoryDAO.findByTypeForSelectbox(CategoryType.CTL_CDR_GEN_FILENAME);
		// listCategory.add(0, catParentDump);
	}

	// Nampv 26102016
	public void chooseServieCode() {
		super.openTreeCommonDialog(TreeType.CATALOG_CDR_SERVICE, readProperties("title.CdrService"), 0, false, null);
	}

	public void onDialogServieCodeReturn(SelectEvent event) {
		Object object = event.getObject();
		if (object instanceof CdrService) {
			CdrService cdrService = (CdrService) object;
			this.cdrGenFileName.setServiceType(cdrService.getCdrServiceCode());
		}
	}

	public void deleteServieCode() {
		this.cdrGenFileName.setServiceType("");
	}
	
	public void chooseTemplate() {
		super.openTreeCommonDialog(TreeType.CATALOG_CDR_TEMPLATE, readProperties("title.CdrTemplate"), 0, false, null);
	}

	public void onDialogTemplateReturn(SelectEvent event) {
		Object object = event.getObject();
		if (object instanceof CdrTemplate) {
			CdrTemplate cdrTemplate = (CdrTemplate) object;
			this.cdrGenFileName.setTemplateId(cdrTemplate.getCdrTemplateId());
			this.cdrTemplateName = cdrTemplate.getTemplateCode();
		}
	}
	
	public void deleteTemplate() {
		this.cdrGenFileName.setTemplateId(null);
		this.cdrTemplateName = "";
	}

	/**
	 * 
	 * @param isUp
	 *            true is up
	 */
	public void moveUpDownInCat(NodeSelectEvent event, boolean isUp) {
		CdrGenFilename cdrGenFilename = (CdrGenFilename) event.getTreeNode().getData();
		Object object = cdrGenFilenameDAO.upDownObjectInCatWithDomain(cdrGenFilename, "index", isUp);
		if (object instanceof CdrGenFilename) {
			Category category = categoryDAO.get(cdrGenFilename.getCategoryId());
			CdrGenFilename nextGenFileName = (CdrGenFilename) object;

			TreeCommonBean treeCommonBean = super.getTreeCommonBean();
			if (isUp) {
				treeCommonBean.moveUpTreeNode(cdrGenFilename);
			} else {
				treeCommonBean.moveDownTreeNode(cdrGenFilename);
			}
			treeCommonBean.updateTreeNode(nextGenFileName, category, null);
			if (formType == "category" && category.getCategoryId() == categoryId) {
				getListCdrGenFileNameByCategoryId(category.getCategoryId(), 1);
			}

			super.showNotificationSuccsess();
		}
	}

	public void cloneCdrGenFileName(NodeSelectEvent event) {
		Object object = event.getTreeNode().getData();
		if (object instanceof CdrGenFilename) {
			CdrGenFilename cdrGenFilename = null;
			if ((cdrGenFilename = cdrGenFilenameDAO.cloneCdrGenFileName((CdrGenFilename) object)) != null) {
				Category category = categoryDAO.get(cdrGenFilename.getCategoryId());
				super.getTreeCommonBean().updateTreeNode(cdrGenFilename, category, null);
				super.showNotificationSuccsess();
			}
		}
	}
	
	// Edit ContextMenu
	public void editContextMenu(NodeSelectEvent nodeSelectEvent) {
		super.getTreeCommonBean().onNodeSelect(nodeSelectEvent);
		if (nodeSelectEvent != null) {
			CdrGenFilename item = (CdrGenFilename) nodeSelectEvent.getTreeNode().getData();
			editCdrGenFileName(item);
		}
	}

	// Remove ContextMenu
	public void removeContextMenu(NodeSelectEvent nodeSelectEvent) {
		super.getTreeCommonBean().onNodeSelect(nodeSelectEvent);
		if (nodeSelectEvent != null) {
			CdrGenFilename item = (CdrGenFilename) nodeSelectEvent.getTreeNode().getData();
			deleteCdrGenFileName(item);
			getListCdrGenFileNameByCategoryId(item.getCategoryId(), 1);
			formType = "category";
		}
	}

	// GET SET

	public List<CdrGenFilename> getListCdrGenFileName() {
		return listCdrGenFileName;
	}

	public String getCdrTemplateName() {
		return cdrTemplateName;
	}

	public void setCdrTemplateName(String cdrTemplateName) {
		this.cdrTemplateName = cdrTemplateName;
	}

	public void setListCdrGenFileName(List<CdrGenFilename> listCdrGenFileName) {
		this.listCdrGenFileName = listCdrGenFileName;
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

	public CdrGenFilenameDAO getCdrGenFilenameDAO() {
		return cdrGenFilenameDAO;
	}

	public void setCdrGenFilenameDAO(CdrGenFilenameDAO cdrGenFilenameDAO) {
		this.cdrGenFilenameDAO = cdrGenFilenameDAO;
	}

	public CdrGenFilename getCdrGenFileName() {
		return cdrGenFileName;
	}

	public void setCdrGenFileName(CdrGenFilename cdrGenFileName) {
		this.cdrGenFileName = cdrGenFileName;
	}

	public Boolean getEditting() {
		return editting;
	}

	public void setEditting(Boolean editting) {
		this.editting = editting;
	}

}

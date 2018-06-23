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

import com.viettel.ocs.bean.BaseController;
import com.viettel.ocs.bean.TreeCommonBean;
import com.viettel.ocs.constant.CategoryType;
import com.viettel.ocs.constant.CdrType.CdrDetailType;
import com.viettel.ocs.constant.TreeType;
import com.viettel.ocs.dao.CategoryDAO;
import com.viettel.ocs.dao.CdrGenFilenameDAO;
import com.viettel.ocs.dao.CdrPropDAO;
import com.viettel.ocs.dao.CdrServiceDAO;
import com.viettel.ocs.dao.CdrProcessConfigDAO;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.CdrGenFilename;
import com.viettel.ocs.entity.CdrProcessConfig;
import com.viettel.ocs.entity.CdrProp;
import com.viettel.ocs.entity.CdrService;
import com.viettel.ocs.entity.CdrTemplate;
import com.viettel.ocs.entity.UnitType;
import com.viettel.ocs.util.ValidateUtil;

@ManagedBean(name = "cdrProcessConfigBean")
@ViewScoped
public class CdrProcessConfigBean extends BaseController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3798969909981627890L;

	private CdrProcessConfig cdrProcessConfig;
	private Category category;

	private List<CdrProcessConfig> listCdrProcessConfig;
	private List<Category> listCategory;
	private List<SelectItem> listSelectCdrType;
	private List<SelectItem> listProcessOptions;

	private Long categoryId;
	private String cdrServiceCode;
	private String cdrPattern;
	private String formType;
	private Category catParentDump;

	private CategoryDAO categoryDAO;
	private CdrProcessConfigDAO cdrProcessConfigDAO;
	private CdrServiceDAO cdrServiceDAO;
	private CdrPropDAO cdrPropDAO;
	private CdrGenFilenameDAO cdrGenFilenameDAO;
	private boolean editting;

	@PostConstruct
	public void init() {
		setCategoryDAO(new CategoryDAO());
		setCategory(new Category());
		this.cdrPropDAO = new CdrPropDAO();
		this.cdrServiceDAO = new CdrServiceDAO();
		this.cdrProcessConfigDAO = new CdrProcessConfigDAO();
		this.cdrGenFilenameDAO = new CdrGenFilenameDAO();
		this.listCdrProcessConfig = new ArrayList<CdrProcessConfig>();
		this.listCategory = new ArrayList<>();
		this.listSelectCdrType = new ArrayList<>();
		catParentDump = new Category();
		catParentDump.setCategoryName("");
		getLoadComboListCategory();
		clearData();
		editting = false;
	}

	public void clearData() {
		this.cdrProcessConfig = new CdrProcessConfig();
		this.cdrProcessConfig.setCategoryId(getCategoryId());
		this.cdrServiceCode = this.cdrPattern = null;
	}

	public void addNewProcessConfig() {
		clearData();
		TreeCommonBean treeCommonBean = super.getTreeCommonBean();
//		treeCommonBean.setCdrProcessConfigProperties(false, null, this.cdrProcessConfig);
		treeCommonBean.hideAllCategoryComponent();
		treeCommonBean.setContentTitle(super.readProperties("title.CdrProcessConfig"));
		setFormType("detail");
		setDataByTreeNode(cdrProcessConfig);

		setEditting(true);
	}

	public void submitProcessConfig() {
		if (validate()) {
			if (cdrProcessConfigDAO.saveCdrProcess(this.cdrProcessConfig)) {
				TreeCommonBean treeCommonBean = super.getTreeCommonBean();
				treeCommonBean.updateTreeNode(cdrProcessConfig, categoryDAO.get(cdrProcessConfig.getCategoryId()), null);
				editting = false;
				this.showMessageINFO("common.save", " CDR Process Config");	
			}
			// clearData();
		}
	}

	public Boolean validate() {
		boolean result = true;
		FacesContext context = FacesContext.getCurrentInstance();
		
		if(this.cdrProcessConfig.getCdrServiceId() == null || this.cdrProcessConfig.getCdrServiceId() <= 0) {
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_WARN, this.readProperties("cdrProperties.validateError"),
							this.readProperties("cdrProcessConfig.serviceCodeNotEmpty")));
			result = false;
		}
		
		if(this.cdrProcessConfig.getFilenamePatternId() == null || this.cdrProcessConfig.getFilenamePatternId() <= 0) {
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_WARN, this.readProperties("cdrProperties.validateError"),
							this.readProperties("cdrProcessConfig.patternNotEmpty")));
			result = false;
		}

		if (cdrProcessConfigDAO.checkFieldIsExist("processName", this.cdrProcessConfig.getProcessName(),
				cdrProcessConfig)) {
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_WARN, this.readProperties("cdrProperties.validateError"),
							this.readProperties("cdrProcessConfig.NameIsExist")));
			result = false;
		}

		return result;
	}

	public void editCdrProcessConfig(CdrProcessConfig item) {
		this.cdrProcessConfig = item.clone();
		TreeCommonBean treeCommonBean = super.getTreeCommonBean();
//		treeCommonBean.setCdrProcessConfigProperties(false, null, this.cdrProcessConfig);
		treeCommonBean.hideAllCategoryComponent();
		treeCommonBean.setContentTitle(super.readProperties("title.CdrProcessConfig"));
		setFormType("detail");
		setDataByTreeNode(item);
		setCategoryId(cdrProcessConfig.getCategoryId());
				
		setEditting(true);
	}

	public void setDataByTreeNode(CdrProcessConfig item) {
		getLoadComboListCategory();
		this.cdrProcessConfig = item.clone();
		if (item.getFilenamePatternId() != null) {
			CdrGenFilename cdrGenFilename = cdrGenFilenameDAO.get(item.getFilenamePatternId());
			this.setCdrPattern(cdrGenFilename.getPattern());
		}
		if (item.getCdrServiceId() != null) {
			CdrService cdrService = cdrServiceDAO.get(item.getCdrServiceId());
			this.setCdrServiceCode(cdrService.getCdrServiceCode());
		}
	}

	public void deleteCdrProcessConfig(CdrProcessConfig item) {
		if (item != null) {
			if (cdrProcessConfigDAO.deleteCdrProcessConfig(item)) {
				TreeCommonBean treeCommonBean = super.getTreeCommonBean();
				treeCommonBean.removeTreeNodeAll(item);
				this.listCdrProcessConfig.remove(item);
				super.showNotificationSuccsess();
//				this.showMessageINFO("common.delete", " title.CdrProcessConfig ");
			} else {
				FacesContext context = FacesContext.getCurrentInstance();
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
						this.readProperties("cdrService.deleteError"), this.readProperties("cdrService.recordInUsed")));
			}
		}
	}

	public List<CdrProcessConfig> getListCdrProcessConfigByCategoryId(Long categoryId, int type) {
		if (listCdrProcessConfig.size() <= 0 || type == 1) {
			return listCdrProcessConfig = cdrProcessConfigDAO.loadListCdrProcessConfig(categoryId);
		}
		return listCdrProcessConfig;
	}

	public void getLoadComboListCategory() {
		listCategory = new ArrayList<>();
		listCategory = categoryDAO.findByTypeForSelectbox(CategoryType.CTL_CDR_PROCESS_CONFIG);
		listCategory.add(0, catParentDump);
	}

	public void chooseServieCode() {
		super.openTreeCommonDialog(TreeType.CATALOG_CDR_SERVICE, readProperties("title.CdrService"), 0, false, null);
	}

	public void onDialogServieCodeReturn(SelectEvent event) {
		Object object = event.getObject();
		if (object instanceof CdrService) {
			CdrService cdrService = (CdrService) object;
			this.cdrProcessConfig.setCdrServiceId(cdrService.getCdrServiceId());
			this.cdrServiceCode = cdrService.getCdrServiceCode();
		}
	}

	public void deleteServieCode() {
		this.cdrProcessConfig.setCdrServiceId(null);
		cdrServiceCode = null;
	}
	
	public void choosePattern() {
		super.openTreeCommonDialog(TreeType.CATALOG_CDR_GEN_FILENAME, readProperties("title.CdrGenFileName"), 0, false, null);
	}

	public void onDialogPatternReturn(SelectEvent event) {
		Object object = event.getObject();
		if (object instanceof CdrGenFilename) {
			CdrGenFilename cdrGenFilename = (CdrGenFilename) object;
			this.cdrProcessConfig.setFilenamePatternId(cdrGenFilename.getCdrGenFilenameId());
			this.setCdrPattern(cdrGenFilename.getPattern());
		}
	}

	public void deletePattern() {
		this.cdrProcessConfig.setFilenamePatternId(null);
		this.setCdrPattern("");
	}
	
	/**
	 * 
	 * @param isUp
	 *            true is up
	 */
	public void moveUpDownInCat(NodeSelectEvent event, boolean isUp) {
		CdrProcessConfig processConfig = (CdrProcessConfig) event.getTreeNode().getData();
		Object object = cdrGenFilenameDAO.upDownObjectInCatWithDomain(processConfig, "index", isUp);
		if (object instanceof CdrProcessConfig) {
			Category category = categoryDAO.get(processConfig.getCategoryId());
			CdrProcessConfig nextProcessCfg = (CdrProcessConfig) object;

			TreeCommonBean treeCommonBean = super.getTreeCommonBean();
			if (isUp) {
				treeCommonBean.moveUpTreeNode(processConfig);
			} else {
				treeCommonBean.moveDownTreeNode(processConfig);
			}
			treeCommonBean.updateTreeNode(nextProcessCfg, category, null);
			if (formType == "category" && category.getCategoryId() == categoryId) {
				getListCdrProcessConfigByCategoryId(category.getCategoryId(), 1);
			}

			super.showNotificationSuccsess();
		}
	}

	/**
	 * @author THANHND
	 * @param event
	 */
	public void cloneCdrProcessConfig(NodeSelectEvent event) {
		Object object = event.getTreeNode().getData();
		if (object instanceof CdrProcessConfig) {
			CdrProcessConfig cdrProcessConfig = null;
			if ((cdrProcessConfig = cdrProcessConfigDAO.cloneCdrProcessConfig((CdrProcessConfig) object)) != null) {
				Category category = categoryDAO.get(cdrProcessConfig.getCategoryId());
				super.getTreeCommonBean().updateTreeNode(cdrProcessConfig, category, null);
				super.showNotificationSuccsess();
			}
		}
	}
	
	// Edit ContextMenu
	public void editContextMenu(NodeSelectEvent nodeSelectEvent) {
		super.getTreeCommonBean().onNodeSelect(nodeSelectEvent);
		if (nodeSelectEvent != null) {
			CdrProcessConfig item = (CdrProcessConfig) nodeSelectEvent.getTreeNode().getData();
			editCdrProcessConfig(item);
		}
	}

	// Remove ContextMenu
	public void removeContextMenu(NodeSelectEvent nodeSelectEvent) {
		super.getTreeCommonBean().onNodeSelect(nodeSelectEvent);
		if (nodeSelectEvent != null) {
			CdrProcessConfig item = (CdrProcessConfig) nodeSelectEvent.getTreeNode().getData();
			deleteCdrProcessConfig(item);
			getListCdrProcessConfigByCategoryId(item.getCategoryId(), 1);
			formType = "category";
		}
	}
	
	// GET SET

	public CdrProcessConfig getCdrProcessConfig() {
		return cdrProcessConfig;
	}

	public void setCdrProcessConfig(CdrProcessConfig cdrProcessConfig) {
		this.cdrProcessConfig = cdrProcessConfig;
	}

	public List<CdrProcessConfig> getListCdrProcessConfig() {
		return listCdrProcessConfig;
	}

	public void setListCdrProcessConfig(List<CdrProcessConfig> listCdrProcessConfig) {
		this.listCdrProcessConfig = listCdrProcessConfig;
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

	public CdrProcessConfigDAO getCdrProcessConfigDAO() {
		return cdrProcessConfigDAO;
	}

	public void setCdrProcessConfigDAO(CdrProcessConfigDAO cdrProcessConfigDAO) {
		this.cdrProcessConfigDAO = cdrProcessConfigDAO;
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

	public CdrPropDAO getCdrPropDAO() {
		return cdrPropDAO;
	}

	public void setCdrPropDAO(CdrPropDAO cdrPropDAO) {
		this.cdrPropDAO = cdrPropDAO;
	}

	public CdrGenFilenameDAO getCdrGenFilenameDAO() {
		return cdrGenFilenameDAO;
	}

	public void setCdrGenFilenameDAO(CdrGenFilenameDAO cdrGenFilenameDAO) {
		this.cdrGenFilenameDAO = cdrGenFilenameDAO;
	}

	public List<SelectItem> getListSelectCdrType() {
		this.listSelectCdrType = new ArrayList<>();
		this.listSelectCdrType.add(new SelectItem(CdrDetailType.GENERATE, CdrDetailType.GENERATE_TYPE));
		this.listSelectCdrType.add(new SelectItem(CdrDetailType.HOLDING, CdrDetailType.HOLDING_TYPE));
		this.listSelectCdrType.add(new SelectItem(CdrDetailType.PUSH, CdrDetailType.PUSH_TYPE));
		return listSelectCdrType;
	}

	public void setListSelectCdrType(List<SelectItem> listSelectCdrType) {
		this.listSelectCdrType = listSelectCdrType;
	}

	public List<SelectItem> getListProcessOptions() {
		this.listProcessOptions = new ArrayList<>();
		this.listProcessOptions.add(new SelectItem(0l, readProperties("cdrProcessConfig.processOption1")));
		this.listProcessOptions.add(new SelectItem(1l, readProperties("cdrProcessConfig.processOption2")));
		return listProcessOptions;
	}

	public void setListProcessOptions(List<SelectItem> listProcessOptions) {
		this.listProcessOptions = listProcessOptions;
	}

	public String getCdrServiceCode() {
		return cdrServiceCode;
	}

	public void setCdrServiceCode(String cdrServiceCode) {
		this.cdrServiceCode = cdrServiceCode;
	}

	public String getCdrPattern() {
		return cdrPattern;
	}

	public void setCdrPattern(String cdrPattern) {
		this.cdrPattern = cdrPattern;
	}

	public boolean isEditting() {
		return editting;
	}

	public void setEditting(boolean editting) {
		this.editting = editting;
	}

}

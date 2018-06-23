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
import com.viettel.ocs.bean.TreeOfferBean;
import com.viettel.ocs.constant.CategoryType;
import com.viettel.ocs.constant.TreeType;
import com.viettel.ocs.dao.CategoryDAO;
import com.viettel.ocs.dao.CdrServiceDAO;
import com.viettel.ocs.dao.ReserveInfoDAO;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.CdrService;
import com.viettel.ocs.entity.Parameter;
import com.viettel.ocs.entity.ReserveInfo;
import com.viettel.ocs.entity.UnitType;
import com.viettel.ocs.util.ValidateUtil;

@ManagedBean(name = "cdrServiceBean")
@ViewScoped
public class CdrServiceBean extends BaseController implements Serializable {
	// property UI
	private List<Category> listCategory;
	private List<CdrService> listCdrService;
	private CdrService cdrService;
	private UnitType unitType;
	private Long categoryId;
	public String[] data = { "1" };
	private CategoryDAO categoryDAO;
	private CdrServiceDAO cdrServiceDAO;
	private boolean edit = false;
	private boolean isAddNew;
	private String formType;
	private Category parentDump;

	private static final long serialVersionUID = 7995851503615283406L;

	public CdrServiceBean() {
	}

	@PostConstruct
	public void init() {
		parentDump = new Category();
		parentDump.setCategoryName("");
		categoryDAO = new CategoryDAO();
		cdrServiceDAO = new CdrServiceDAO();
		listCategory = new ArrayList<Category>();
		unitType = new UnitType();
		setConditionFormType("list");
		listCdrService = new ArrayList<>();
		cdrService = new CdrService();
		getLoadComboListCategory();
		isAddNew = false;
	}

	// load ListCategory cho combobox

	public void getLoadComboListCategory() {
		listCategory = new ArrayList<>();
		listCategory = categoryDAO.findByTypeForSelectbox(CategoryType.CTL_CDR_SERVICE);
		listCategory.add(0, parentDump);
	}

	public List<CdrService> getListCdrServiceByCategoryId(Long categoryId, int type) {
		if (listCdrService.size() <= 0 || type == 1) {
			return listCdrService = cdrServiceDAO.loadListCdrService(categoryId);
		}
		return listCdrService;
	}

	public void btnAddNew() {
		this.cdrService = new CdrService();
		cdrService.setCategoryId(categoryId);
		TreeCommonBean treeCommonBean = super.getTreeCommonBean();
		// treeCommonBean.setCdrServiceProperties(false, null, this.cdrService);
		treeCommonBean.hideAllCategoryComponent();
		treeCommonBean.setContentTitle(super.readProperties("title.CdrService"));
		getLoadComboListCategory();
		setConditionFormType("detail");
		setDataByTreeNode(cdrService);
		setCategoryId(cdrService.getCategoryId());

		edit = true;
		isAddNew = true;
	}

	public void editCdrService(CdrService item) {
		TreeCommonBean treeCommonBean = super.getTreeCommonBean();
		// treeCommonBean.setCdrServiceProperties(false, null, item);
		treeCommonBean.hideAllCategoryComponent();
		treeCommonBean.setContentTitle(super.readProperties("title.CdrService"));
		getLoadComboListCategory();
		setConditionFormType("detail");
		setDataByTreeNode(item);
		setCategoryId(item.getCategoryId());

		edit = true;
		isAddNew = false;
	}

	public void deleteCdrService(CdrService item) {
		if (item != null) {
			if (cdrServiceDAO.deleteCdrService(item)) {
				TreeCommonBean treeCommonBean = super.getTreeCommonBean();
				treeCommonBean.removeTreeNodeAll(item);
				this.listCdrService.remove(item);
			} else {
				FacesContext context = FacesContext.getCurrentInstance();
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
						this.readProperties("cdrService.deleteError"), this.readProperties("cdrService.recordInUsed")));
			}
		}
	}

	public void saveCdrService() {
		if (checkValidate()) {
			if (cdrServiceDAO.saveCdrService(this.cdrService)) {
				TreeCommonBean treeCommonBean = super.getTreeCommonBean();
				treeCommonBean.updateTreeNode(cdrService, categoryDAO.get(cdrService.getCategoryId()), null);
				edit = false;
				isAddNew = false;
				this.showMessageINFO("common.save", " CDR Service");
			}
		}
	}

	public Boolean checkValidate() {
		boolean result = true;
		FacesContext context = FacesContext.getCurrentInstance();
		if (cdrServiceDAO.checkFieldIsExist("cdrServiceCode", this.cdrService.getCdrServiceCode(), this.cdrService)) {
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_WARN, this.readProperties("cdrService.validateError"),
							this.readProperties("cdrService.serviceCodeIsExist")));
			result = false;
		}

		if (cdrServiceDAO.checkFieldIsExist("name", this.cdrService.getName(), this.cdrService)) {
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_WARN, this.readProperties("cdrService.validateError"),
							this.readProperties("cdrService.serviceNameIsExist")));
			result = false;
		}

		if (isAddNew) {
			if (cdrServiceDAO.get(this.cdrService.getCdrServiceId()) != null) {
				context.addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_WARN, this.readProperties("cdrService.validateError"),
								this.readProperties("cdrService.serviceIDIsExist")));
				result = false;
			}
		}

		return result;
	}

	private void setTreeCommonFormType() {
		TreeCommonBean treeCommonBean = getTreeCommonBean();
		treeCommonBean.hideAllCategoryComponent();
	}

	// set Data khi click node CdrService Detail
	public void setDataByTreeNode(CdrService item) {
		this.categoryId = item.getCategoryId();
		this.cdrService = item;
		getLoadComboListCategory();
		edit = false;
	}

	/**
	 * 
	 * @param isUp
	 *            true is up
	 */
	public void moveUpDownInCat(NodeSelectEvent event, boolean isUp) {
		CdrService parameter = (CdrService) event.getTreeNode().getData();
		Object object = cdrServiceDAO.upDownObjectInCatWithDomain(parameter, "index", isUp);
		if (object instanceof CdrService) {
			Category category = categoryDAO.get(parameter.getCategoryId());
			CdrService nextParameter = (CdrService) object;

			TreeCommonBean treeCommonBean = super.getTreeCommonBean();
			if (isUp) {
				treeCommonBean.moveUpTreeNode(parameter);
			} else {
				treeCommonBean.moveDownTreeNode(parameter);
			}
			treeCommonBean.updateTreeNode(nextParameter, category, null);
			if (formType == "category" && category.getCategoryId() == categoryId) {
				getListCdrServiceByCategoryId(category.getCategoryId(), 1);
			}

			super.showNotificationSuccsess();
		}
	}
	
	// Edit ContextMenu
	public void editContextMenu(NodeSelectEvent nodeSelectEvent) {
		super.getTreeCommonBean().onNodeSelect(nodeSelectEvent);
		if (nodeSelectEvent != null) {
			CdrService item = (CdrService) nodeSelectEvent.getTreeNode().getData();
			editCdrService(item);
		}
	}

	// Remove ContextMenu
	public void removeContextMenu(NodeSelectEvent nodeSelectEvent) {
		super.getTreeCommonBean().onNodeSelect(nodeSelectEvent);
		if (nodeSelectEvent != null) {
			CdrService item = (CdrService) nodeSelectEvent.getTreeNode().getData();
			deleteCdrService(item);
			getListCdrServiceByCategoryId(item.getCategoryId(), 1);
			formType = "category";
		}
	}
	

	// type = category: HIDDEN form ; type = deltail: view form detail
	public void setConditionFormType(String value) {
		setFormType(value);
	}

	public UnitType getUnitType() {
		return unitType;
	}

	public void setUnitType(UnitType unitType) {
		this.unitType = unitType;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public String[] getData() {
		return data;
	}

	public void setData(String[] data) {
		this.data = data;
	}

	public boolean isEdit() {
		return edit;
	}

	public void setEdit(boolean edit) {
		this.edit = edit;
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

	public List<CdrService> getListCdrService() {
		return listCdrService;
	}

	public void setListCdrService(List<CdrService> listCdrService) {
		this.listCdrService = listCdrService;
	}

	public CdrService getCdrService() {
		return cdrService;
	}

	public void setCdrService(CdrService cdrService) {
		this.cdrService = cdrService;
	}

	public CategoryDAO getCategoryDAO() {
		return categoryDAO;
	}

	public void setCategoryDAO(CategoryDAO categoryDAO) {
		this.categoryDAO = categoryDAO;
	}

	public CdrServiceDAO getCdrServiceDAO() {
		return cdrServiceDAO;
	}

	public void setCdrServiceDAO(CdrServiceDAO cdrServiceDAO) {
		this.cdrServiceDAO = cdrServiceDAO;
	}

	public Category getParentDump() {
		return parentDump;
	}

	public boolean isAddNew() {
		return isAddNew;
	}

	public void setAddNew(boolean isAddNew) {
		this.isAddNew = isAddNew;
	}

}

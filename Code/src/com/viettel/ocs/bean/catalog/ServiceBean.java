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
import org.primefaces.event.SelectEvent;

import com.viettel.ocs.bean.BaseController;
import com.viettel.ocs.bean.TreeCommonBean;
import com.viettel.ocs.constant.CategoryType;
import com.viettel.ocs.constant.TreeType;
import com.viettel.ocs.dao.CategoryDAO;
import com.viettel.ocs.dao.ServiceDAO;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.Service;

@ManagedBean(name = "serviceBean")
@ViewScoped
public class ServiceBean extends BaseController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// UI Obj --------------------------------------------------

	private Service serviceUI;
	private Service selectedServiceTreeContext;
	private ServiceDAO serviceDAO;
	private boolean isEditting;
	private boolean isApply;
	private List<Service> listServiceDB;
	private String formType = "";
	private CategoryDAO categoryDAO;
	private long categoryID;
	private List<SelectItem> listItemCategory;
	private List<Service> listServiceByCategory;
	private List<Category> categoriesOfService;

	// Init -----------------------------------------------------
	@PostConstruct
	public void init() {
		this.isApply = true;
		this.isEditting = true;
		categoryID = 0l;
		serviceUI = new Service();
		serviceDAO = new ServiceDAO();
		categoryDAO = new CategoryDAO();
		listServiceDB = new ArrayList<>();
		listItemCategory = new ArrayList<>();
		listServiceByCategory = new ArrayList<>();
		categoriesOfService = new ArrayList<>();
	}

	public void addNew(NodeSelectEvent nodeSelectEvent) {
	}

	public void moveTo(NodeSelectEvent nodeSelectEvent) {
		selectedServiceTreeContext = (Service) nodeSelectEvent.getTreeNode().getData();
		super.openTreeCategoryDialog(TreeType.CATALOG_SERVICE, "Category", CategoryType.CTL_SERVICE);
	}

	public void onDialogCatReturn(SelectEvent selectEvent) {
		Object object = selectEvent.getObject();
		if (object instanceof Category) {
			Category category = (Category) object;
			selectedServiceTreeContext.setCategoryId(category.getCategoryId());
			try {
				serviceDAO.update(selectedServiceTreeContext);
				super.getTreeCommonBean().updateTreeNode(selectedServiceTreeContext, category, null);
				if (selectedServiceTreeContext.getServiceId() == serviceUI.getServiceId()) {
					serviceUI.setCategoryId(category.getCategoryId());
				}
				super.showNotificationSuccsess();
			} catch (Exception e) {
				getLogger().warn(e.getMessage(), e);
				super.showNotificationFail();
			}
		}
	}

	/**
	 * 
	 * @param isUp
	 *            true is up
	 */
	public void moveUpDownInCat(NodeSelectEvent event, boolean isUp) {
		Service service = (Service) event.getTreeNode().getData();
		Object object = serviceDAO.upDownObjectInCatWithDomain(service, "index", isUp);
		if (object instanceof Service) {
			Category category = categoryDAO.get(service.getCategoryId());
			Service nextService = (Service) object;

			TreeCommonBean treeCommonBean = super.getTreeCommonBean();
			if (isUp) {
				treeCommonBean.moveUpTreeNode(service);
			} else {
				treeCommonBean.moveDownTreeNode(service);
			}
			treeCommonBean.updateTreeNode(nextService, category, null);

			if (formType == "list-service" && category.getCategoryId() == this.categoryID) {
				loadServiceByCategory(category.getCategoryId());
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
			Service item = (Service) nodeSelectEvent.getTreeNode().getData();
			commandEditTB(item);
		}
	}

	// Remove ContextMenu
	public void removeContextMenu(NodeSelectEvent nodeSelectEvent) {
		super.getTreeCommonBean().onNodeSelect(nodeSelectEvent);
		if (nodeSelectEvent != null) {
			Service item = (Service) nodeSelectEvent.getTreeNode().getData();
			commandRemoveTable(item);
			loadServiceByCategory(item.getCategoryId());
			formType = "list-service";
		}
	}
	
	// Prepare---------------------------------------------------
	public List<SelectItem> loadCategory() {
		listItemCategory = new ArrayList<SelectItem>();
		CategoryDAO categoryDAO = new CategoryDAO();
		List<Category> listCategory = categoryDAO.findByTypeForSelectbox(CategoryType.CTL_SERVICE);
		if (listItemCategory != null && !listCategory.isEmpty()) {
			for (Category category : listCategory) {
				listItemCategory.add(new SelectItem(category.getCategoryId(), category.getCategoryName()));
			}
		}
		return listItemCategory;
	}
	
	public void loadCategoriesOfService() {
		categoriesOfService = categoryDAO.findByTypeForSelectbox(CategoryType.CTL_SERVICE);
	}

	// load list list Parameter by categoryId
	public List<Service> loadServiceByCategory(long categoryId) {
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.execute("PF('service_id').clearFilters();");
		listServiceByCategory = new ArrayList<Service>();
		listServiceByCategory = serviceDAO.findServiceByConditions(categoryId);
		this.categoryID = categoryId;
		return listServiceByCategory;
	}

	// Action
	public void commandAddNew() {
		this.isEditting = false;
		clearText();
		setFormType("detail-service");
		serviceUI.setCategoryId(categoryID);
		loadCategoriesOfService();
		TreeCommonBean treeCommonBean = super.getTreeCommonBean();
		treeCommonBean.setContentTitle(super.readProperties("service.title"));
		treeCommonBean.hideAllCategoryComponent();
		this.isApply = false;
	}

	public void commandCancel() {
		this.isEditting = true;
		loadServiceByCategory(serviceUI.getCategoryId());
		setFormType("list-service");
	}

	public void commandEditTB(Service item) {
		this.isEditting = true;
		setServiceUI(item);
		setFormType("detail-service");
		loadCategoriesOfService();
		TreeCommonBean treeCommonBean = super.getTreeCommonBean();
		treeCommonBean.setContentTitle(super.readProperties("service.title"));
		treeCommonBean.hideAllCategoryComponent();
		this.isApply = false;
	}

	public void commandRemoveTable(Service item) {
		clearText();
		this.isEditting = true;
		serviceDAO.delete(item);
		removeItemInList(item);
		TreeCommonBean treeCommonBean = super.getTreeCommonBean();
		treeCommonBean.removeTreeNodeAll(item);
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.execute("PF('service_id').clearFilters();");
		this.showMessageINFO("common.delete", super.readProperties("service"));
	}

	private void removeItemInList(Service service) {
		for (Service item : listServiceByCategory) {
			if (service.getServiceId() == item.getServiceId()) {
				listServiceByCategory.remove(item);
				break;
			}
		}
	}

	public void commandApply() {
		TreeCommonBean treeCommonBean = super.getTreeCommonBean();
		Category cat = categoryDAO.get(serviceUI.getCategoryId());
		if (validateService()) {
//			if (isEditting) {
//				serviceDAO.saveOrUpdate(serviceUI);
//				treeCommonBean.updateTreeNode(serviceUI, cat, null);
//			} else {
//				serviceUI.setIndex(serviceDAO.getMaxIndex() + 1);
//				serviceDAO.save(serviceUI);
//				listServiceByCategory.add(serviceUI);
//				treeCommonBean.updateTreeNode(serviceUI, cat, null);
//			}
//			this.isEditting = true;
//			this.isApply = true;
//			this.showMessageINFO("common.save", super.readProperties("service"));
			if (serviceDAO.saveService(serviceUI)) {
				this.isEditting = true;
				this.isApply = true;
				this.showMessageINFO("common.save", super.readProperties("service"));
				treeCommonBean.updateTreeNode(serviceUI, cat, null);
			}
		}
	}

	public void clearText() {
		serviceUI = new Service();
	}

	// Validate
	private boolean validateService() {
		if (serviceDAO.checkName(serviceUI, serviceUI.getServiceName(), isEditting)) {
			this.showMessageWARN("service", super.readProperties("validate.checkValueNameExist"));
			return false;
		}
		return true;
	}

	// Get-set ---------------------------------------------------------
	public Service getServiceUI() {
		return serviceUI;
	}

	public void setServiceUI(Service serviceUI) {
		this.serviceUI = serviceUI.clone();
	}

	public ServiceDAO getServiceDAO() {
		return serviceDAO;
	}

	public void setServiceDAO(ServiceDAO serviceDAO) {
		this.serviceDAO = serviceDAO;
	}

	public boolean isEditting() {
		return isEditting;
	}

	public void setEditting(boolean isEditting) {
		this.isEditting = isEditting;
	}

	public List<Service> getListServiceDB() {
		return listServiceDB;
	}

	public void setListServiceDB(List<Service> listServiceDB) {
		this.listServiceDB = listServiceDB;
	}

	public String getFormType() {
		return formType;
	}

	public void setFormType(String formType) {
		this.formType = formType;
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

	public List<SelectItem> getListItemCategory() {
		return listItemCategory;
	}

	public void setListItemCategory(List<SelectItem> listItemCategory) {
		this.listItemCategory = listItemCategory;
	}

	public List<Service> getListServiceByCategory() {
		return listServiceByCategory;
	}

	public void setListServiceByCategory(List<Service> listServiceByCategory) {
		this.listServiceByCategory = listServiceByCategory;
	}

	public List<Category> getCategoriesOfService() {
		return categoriesOfService;
	}

	public void setCategoriesOfService(List<Category> categoriesOfService) {
		this.categoriesOfService = categoriesOfService;
	}

	public boolean isApply() {
		return isApply;
	}

	public void setApply(boolean isApply) {
		this.isApply = isApply;
	}
}

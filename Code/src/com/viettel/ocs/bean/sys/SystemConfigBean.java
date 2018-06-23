package com.viettel.ocs.bean.sys;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.primefaces.event.NodeSelectEvent;

import com.viettel.ocs.bean.BaseController;
import com.viettel.ocs.bean.TreeCommonBean;
import com.viettel.ocs.constant.CategoryType;
import com.viettel.ocs.dao.CategoryDAO;
import com.viettel.ocs.dao.SystemConfigDAO;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.CdrProcessConfig;
import com.viettel.ocs.entity.Parameter;
import com.viettel.ocs.entity.SystemConfig;

@ManagedBean(name = "systemConfigBean")
@ViewScoped
public class SystemConfigBean extends BaseController implements Serializable {

	/**
	 * @author huannn
	 */
	private static final long serialVersionUID = 1;

	private List<Category> listCategory;
	private Long categoryID;
	private SystemConfig sysConf;
	private boolean isEditing;
	private String formType;
	private List<SystemConfig> listSystemConfig;
	private Category catParentDump;

	private SystemConfigDAO systemConfigDAO;
	private CategoryDAO categoryDAO;

	public SystemConfigBean() {
		systemConfigDAO = new SystemConfigDAO();
		categoryDAO = new CategoryDAO();
		catParentDump = new Category();
		catParentDump.setCategoryName("");
	}

	private void loadCategory() {
		listCategory = new ArrayList<>();
		listCategory = categoryDAO.findByTypeForSelectboxWithoutDomain(CategoryType.SYS_SYS_CONFIG);
	}

	public void loadListByCat(Long catId) {

		listSystemConfig = systemConfigDAO.findByCat(catId);
	}

	public void refreshSysConfig(Long catId, SystemConfig systemConfig) {

		if (systemConfig == null) {

			this.setFormType("form-list");
			loadListByCat(catId);
			this.setEditing(true);
			categoryID = catId;
		} else {
			loadCategory();
			this.setFormType("form-detail");
			this.sysConf = systemConfig;
			this.setEditing(true);
			categoryID = this.sysConf.getCategoryId();

			super.getTreeCommonBean().setContentTitle(super.readProperties("title.SystemConfig"));
			super.getTreeCommonBean().hideAllCategoryComponent();
		}
	}

	private void init() {

		sysConf = new SystemConfig();
		sysConf.setCategoryId(categoryID);
		refreshSysConfig(categoryID, sysConf);
	}

	public void btnNew() {

		init();
		isEditing = false;
	}

	public void btnCancel() {

		init();
		isEditing = false;
	}

	public void btnSave() {

		doSave(sysConf);
	}

	private boolean doSave(SystemConfig systemConfig) {

		if (!validateSave())
			return false;

		if (systemConfigDAO.saveSystemCfg(systemConfig)) {
			TreeCommonBean treeCommonBean = super.getTreeCommonBean();
			treeCommonBean.updateTreeNode(systemConfig, categoryDAO.get(systemConfig.getCategoryId()), null);

			this.showMessageINFO("common.save", " System Config ");
			isEditing = true;
			return true;
		}
		return false;
	}

	private boolean validateSave() {

		SystemConfig checkObj = systemConfigDAO.findByKey(sysConf.getKey(), sysConf.getId());
		if (checkObj != null) {

			super.showMessageERROR("common.save", " System Config ", "common.duplicateKey");
			return false;
		}

		return true;
	}

	public void onRowEdit(SystemConfig systemConfig) {

		sysConf = systemConfig;
		categoryID = sysConf.getCategoryId();
		refreshSysConfig(categoryID, sysConf);
		isEditing = false;
	}

	public void onRowDelete(SystemConfig systemConfig) {

		systemConfigDAO.delete(systemConfig);
		listSystemConfig.remove(systemConfig);
		TreeCommonBean treeCommonBean = super.getTreeCommonBean();
		treeCommonBean.removeTreeNodeAll(systemConfig);

		this.showMessageINFO("common.delete", " System Config ");
	}

	// Edit ContextMenu
	public void editContextMenu(NodeSelectEvent nodeSelectEvent) {
		super.getTreeCommonBean().onNodeSelect(nodeSelectEvent);
		if (nodeSelectEvent != null) {
			SystemConfig item = (SystemConfig) nodeSelectEvent.getTreeNode().getData();
			onRowEdit(item);
		}
	}
	
	/**
	 * 
	 * @param isUp
	 *            true is up
	 */
	public void moveUpDownInCat(NodeSelectEvent event, boolean isUp) {
		SystemConfig systemConfig = (SystemConfig) event.getTreeNode().getData();
		Object object = systemConfigDAO.upDownObjectInCatWithoutDomain(systemConfig, "index", isUp);
		if (object instanceof SystemConfig) {
			Category category = categoryDAO.get(systemConfig.getCategoryId());
			SystemConfig nextSystemConfig = (SystemConfig) object;

			TreeCommonBean treeCommonBean = super.getTreeCommonBean();
			if (isUp) {
				treeCommonBean.moveUpTreeNode(systemConfig);
			} else {
				treeCommonBean.moveDownTreeNode(systemConfig);
			}
			treeCommonBean.updateTreeNode(nextSystemConfig, category, null);
			if (formType == "form-list" && category.getCategoryId() == categoryID) {
				loadListByCat(categoryID);
			}

			super.showNotificationSuccsess();
		}
	}

	/** GET - SET **/

	public Long getCategoryID() {
		return categoryID;
	}

	public void setCategoryID(Long categoryID) {
		this.categoryID = categoryID;
	}

	public SystemConfig getSysConf() {
		return sysConf;
	}

	public void setSysConf(SystemConfig sysConf) {
		this.sysConf = sysConf;
	}

	public boolean isEditing() {
		return isEditing;
	}

	public void setEditing(boolean isEditing) {
		this.isEditing = isEditing;
	}

	public String getFormType() {
		return formType;
	}

	public void setFormType(String formType) {
		this.formType = formType;
	}

	public List<SystemConfig> getListSystemConfig() {
		return listSystemConfig;
	}

	public void setListSystemConfig(List<SystemConfig> listSystemConfig) {
		this.listSystemConfig = listSystemConfig;
	}

	public List<Category> getListCategory() {
		return listCategory;
	}

	public void setListCategory(List<Category> listCategory) {
		this.listCategory = listCategory;
	}
}

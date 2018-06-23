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

import com.viettel.ocs.bean.BaseController;
import com.viettel.ocs.bean.TreeCommonBean;
import com.viettel.ocs.constant.CategoryType;
import com.viettel.ocs.dao.ActionDAO;
import com.viettel.ocs.dao.CategoryDAO;
import com.viettel.ocs.dao.ReserveInfoDAO;
import com.viettel.ocs.entity.ActionType;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.ReserveInfo;
import com.viettel.ocs.util.ValidateUtil;

/**
 * @author Nampv
 * @since 22082016
 */

@SuppressWarnings("serial")
@ManagedBean(name = "reserveBean", eager = true)
@ViewScoped
public class ReserveInfoBean extends BaseController implements Serializable {

	private ReserveInfo reserveInfoUI;
	private ReserveInfoDAO reserveInfoDAO;
	private List<ReserveInfo> listReserveInfoByCategory;
	private boolean isEditting;
	private String formType = "";
	private CategoryDAO categoryDAO;
	private long categoryID;
	private List<SelectItem> listItemCategory;
	private String txtReserveName;
	private String txtMinReserve;
	private String txtMaxReserve;
	private String txtUsageQuotaThreshold;
	private List<Category> categoriesOfRI;
	private boolean isApply;

	// --------------------------------------------
	@PostConstruct
	public void init() {
		this.isApply = true;
		this.isEditting = true;
		categoryDAO = new CategoryDAO();
		reserveInfoUI = new ReserveInfo();
		this.categoryID = 0l;
		txtReserveName = "";
		txtMinReserve = "";
		txtMaxReserve = "";
		txtUsageQuotaThreshold = "";
		reserveInfoDAO = new ReserveInfoDAO();
		categoriesOfRI = new ArrayList<Category>();
		listReserveInfoByCategory = new ArrayList<ReserveInfo>();
	}

	// load list category by type ReserveInfo
	public List<SelectItem> loadCategory() {
		listItemCategory = new ArrayList<SelectItem>();
		CategoryDAO categoryDAO = new CategoryDAO();
		List<Category> listCategory = categoryDAO.findByTypeForSelectbox(CategoryType.CTL_UT_RESERVE_INFO);
		if (listItemCategory != null && !listCategory.isEmpty()) {
			for (Category category : listCategory) {
				listItemCategory.add(new SelectItem(category.getCategoryId(), category.getCategoryName()));
			}
		}
		return listItemCategory;
	}

	public void loadCategoriesOfRI() {
		categoriesOfRI = categoryDAO.findByTypeForSelectbox(CategoryType.CTL_UT_RESERVE_INFO);
	}

	// load list list ReserveInfo by categoryId
	public List<ReserveInfo> loadReserveInfoByCategory(long categoryId) {
		listReserveInfoByCategory = new ArrayList<ReserveInfo>();
		listReserveInfoByCategory = reserveInfoDAO.loadListReserveInfo(categoryId);
		this.categoryID = categoryId;
		return listReserveInfoByCategory;
	}

	// Action
	// In category
	public void commandAddNew() {
		this.isEditting = false;
		clearText();
		setFormType("detail-reserveinfo");
		reserveInfoUI.setCategoryId(categoryID);
		loadCategoriesOfRI();
		TreeCommonBean treeCommonBean = super.getTreeCommonBean();
		treeCommonBean.hideAllCategoryComponent();
		treeCommonBean.setContentTitle(super.readProperties("title.ReserveInfo"));
		this.isApply = false;
	}

	public void commandEditTable(ReserveInfo item) {
		this.isEditting = true;
		setReserveInfoUI(item);
		setFormType("detail-reserveinfo");
		TreeCommonBean treeCommonBean = super.getTreeCommonBean();
		treeCommonBean.hideAllCategoryComponent();
		treeCommonBean.setContentTitle(super.readProperties("title.ReserveInfo"));
		loadCategoriesOfRI();
		this.isApply = false;
	}

	public void commandRemoveTable(ReserveInfo item) {
		this.isEditting = true;
		clearText();
		TreeCommonBean treeCommonBean = super.getTreeCommonBean();
		Category cat = categoryDAO.get(item.getCategoryId());
		ActionDAO actionDAO = new ActionDAO();
		if (!actionDAO.checkReserveInfoInAction(item.getReserveInfoId())) {
			try {
				reserveInfoDAO.delete(item);
				treeCommonBean.removeTreeNodeAll(item);
				listReserveInfoByCategory.remove(item);
				loadReserveInfoByCategory(cat.getCategoryId());
				this.showMessageINFO("validate.deleteSuccess", super.readProperties(""));
				RequestContext requestContext = RequestContext.getCurrentInstance();
				requestContext.execute("$('.rVClearFilter').click();");
			} catch (Exception e) {
				throw e;
			}
		} else {
			this.showMessageWARN("common.summary.warning", super.readProperties("validate.fieldUseIn"));
		}

	}

	// command up
	public void commandUp(ReserveInfo item) {

		if (reserveInfoDAO.processMoveUpDown(item, true)) {
			Category cat = categoryDAO.get(item.getCategoryId());
			loadReserveInfoByCategory(cat.getCategoryId());
			RequestContext requestContext = RequestContext.getCurrentInstance();
			requestContext.execute("$('.rVClearFilter').click();");
			TreeCommonBean treeCommonBean = super.getTreeCommonBean();
			treeCommonBean.moveUpTreeNode(item);
		}

	}

	// command down
	public void commandDown(ReserveInfo item) {

		if (reserveInfoDAO.processMoveUpDown(item, false)) {
			Category cat = categoryDAO.get(item.getCategoryId());
			loadReserveInfoByCategory(cat.getCategoryId());
			RequestContext requestContext = RequestContext.getCurrentInstance();
			requestContext.execute("$('.rVClearFilter').click();");
			;
			TreeCommonBean treeCommonBean = super.getTreeCommonBean();
			treeCommonBean.moveDownTreeNode(item);
		}
	}

	public void setIndexForRV() {
		if (this.reserveInfoUI == null || this.reserveInfoUI.getReserveInfoId() == 0) {
			ReserveInfo lastItem = reserveInfoDAO.findReserveInfoLastIndex();
			if (lastItem != null) {
				this.reserveInfoUI.setIndex(lastItem.getIndex() + 1);
			} else {
				this.reserveInfoUI.setIndex(0l);
			}
		}
	}

	// In detail
	public void commandApply() {
		// setIndexForRV();
		// TreeCommonBean treeCommonBean = super.getTreeCommonBean();
		// Category cat = categoryDAO.get(reserveInfoUI.getCategoryId());
		// if (validate()) {
		// reserveInfoUI.setResvName(txtReserveName);
		// reserveInfoUI.setMaxReserve(Long.parseLong(txtMaxReserve));
		// reserveInfoUI.setMinReserve(Long.parseLong(txtMinReserve));
		// if (txtUsageQuotaThreshold != null) {
		// reserveInfoUI.setUsageQuotaThreshold(Long.parseLong(txtUsageQuotaThreshold));
		// }
		// if (isEditting) {
		// reserveInfoDAO.saveOrUpdate(reserveInfoUI);
		// this.showMessageINFO("validate.editSuccess",
		// super.readProperties(""));
		// } else {
		// reserveInfoUI.setIndex(reserveInfoDAO.getMaxIndex() + 1);
		// reserveInfoDAO.save(reserveInfoUI);
		// this.showMessageINFO("validate.saveSuccess",
		// super.readProperties(""));
		// }
		// this.showMessageINFO("validate.saveSuccess",
		// super.readProperties(""));
		// treeCommonBean.updateTreeNode(reserveInfoUI, cat, null);
		// this.isEditting = true;
		// this.isApply = true;
		// }
		TreeCommonBean treeCommonBean = super.getTreeCommonBean();
		Category cat = categoryDAO.get(reserveInfoUI.getCategoryId());
		if (validate()) {
			reserveInfoUI.setResvName(txtReserveName);
			reserveInfoUI.setMaxReserve(Long.parseLong(txtMaxReserve));
			reserveInfoUI.setMinReserve(Long.parseLong(txtMinReserve));
			if (txtUsageQuotaThreshold != null) {
				reserveInfoUI.setUsageQuotaThreshold(Long.parseLong(txtUsageQuotaThreshold));
			}
			if (reserveInfoDAO.saveReserveInfo(reserveInfoUI)) {
				treeCommonBean.updateTreeNode(reserveInfoUI, cat, null);
				this.isEditting = true;
				this.isApply = true;
				this.showMessageINFO("validate.saveSuccess", super.readProperties(""));
			}
		}
	}

	public void commandCancel() {
		this.isEditting = true;
		loadReserveInfoByCategory(reserveInfoUI.getCategoryId());
		setFormType("list-reserveinfo-by-category");
	}

	public void clearText() {
		txtReserveName = "";
		txtMaxReserve = "";
		txtMinReserve = "";
		txtUsageQuotaThreshold = "";
		reserveInfoUI = new ReserveInfo();
	}

	private boolean validate() {
		if (ValidateUtil.checkStringNullOrEmpty(txtReserveName)) {
			this.showMessageWARN("reserveInfo", super.readProperties("validate.checkValueNameNull"));
			return false;
		}
		if (ValidateUtil.checkStringNullOrEmpty(txtMaxReserve)) {
			this.showMessageWARN("reserveInfo.maxReserve", super.readProperties("validate.fieldNull"));
			return false;
		}

		if (ValidateUtil.checkStringNullOrEmpty(txtMinReserve)) {
			this.showMessageWARN("reserveInfo.minReserve", super.readProperties("validate.fieldNull"));
			return false;
		}
		if (Long.parseLong(txtMaxReserve) < Long.parseLong(txtMinReserve)) {
			this.showMessageWARN("reserveInfo", super.readProperties("validate.checkValueMinMax"));
			return false;
		}
		if (ValidateUtil.checkStringNullOrEmpty(txtUsageQuotaThreshold)) {
			txtUsageQuotaThreshold = null;
		}
		if (reserveInfoDAO.checkName(reserveInfoUI, txtReserveName, isEditting)) {
			this.showMessageWARN("reserveInfo", super.readProperties("validate.checkValueNameExist"));
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
		ReserveInfo stateGroup = (ReserveInfo) event.getTreeNode().getData();
		Object object = reserveInfoDAO.upDownObjectInCatWithDomain(stateGroup, "index", isUp);
		if (object instanceof ReserveInfo) {
			Category category = categoryDAO.get(stateGroup.getCategoryId());
			ReserveInfo nextStateGroup = (ReserveInfo) object;

			TreeCommonBean treeCommonBean = super.getTreeCommonBean();
			if (isUp) {
				treeCommonBean.moveUpTreeNode(stateGroup);
			} else {
				treeCommonBean.moveDownTreeNode(stateGroup);
			}
			treeCommonBean.updateTreeNode(nextStateGroup, category, null);
			if (formType == "list-reserveinfo-by-category" && category.getCategoryId() == categoryID) {
				loadReserveInfoByCategory(category.getCategoryId());
				RequestContext requestContext = RequestContext.getCurrentInstance();
				requestContext.execute("$('.rVClearFilter').click();");
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
			ReserveInfo item = (ReserveInfo) nodeSelectEvent.getTreeNode().getData();
			commandEditTable(item);
		}
	}

	// Remove ContextMenu
	public void removeContextMenu(NodeSelectEvent nodeSelectEvent) {
		super.getTreeCommonBean().onNodeSelect(nodeSelectEvent);
		if (nodeSelectEvent != null) {
			ReserveInfo item = (ReserveInfo) nodeSelectEvent.getTreeNode().getData();
			commandRemoveTable(item);
			loadReserveInfoByCategory(item.getCategoryId());
			formType = "list-reserveinfo-by-category";
		}
	}

	// get set
	public ReserveInfo getReserveInfoUI() {
		return reserveInfoUI;
	}

	public void setReserveInfoUI(ReserveInfo reserveInfoUI) {
		if (!ValidateUtil.checkStringNullOrEmpty(reserveInfoUI.getResvName())) {
			txtReserveName = reserveInfoUI.getResvName();
		}
		this.reserveInfoUI = reserveInfoUI;
	}

	public List<ReserveInfo> getListReserveInfoByCategory() {
		return listReserveInfoByCategory;
	}

	public void setListReserveInfoByCategory(List<ReserveInfo> listReserveInfoByCategory) {
		this.listReserveInfoByCategory = listReserveInfoByCategory;
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

	public String getTxtReserveName() {
		return txtReserveName;
	}

	public void setTxtReserveName(String txtReserveName) {
		this.txtReserveName = txtReserveName;
	}

	public String getTxtMinReserve() {
		if (reserveInfoUI.getMinReserve() != null) {
			txtMinReserve = reserveInfoUI.getMinReserve().toString();
		}
		return txtMinReserve;
	}

	public void setTxtMinReserve(String txtMinReserve) {
		this.txtMinReserve = txtMinReserve;
	}

	public String getTxtMaxReserve() {
		if (reserveInfoUI.getMaxReserve() != null) {
			txtMaxReserve = reserveInfoUI.getMaxReserve().toString();
		}
		return txtMaxReserve;
	}

	public void setTxtMaxReserve(String txtMaxReserve) {
		this.txtMaxReserve = txtMaxReserve;
	}

	public String getTxtUsageQuotaThreshold() {
		if (reserveInfoUI.getUsageQuotaThreshold() != null) {
			txtUsageQuotaThreshold = reserveInfoUI.getUsageQuotaThreshold().toString();
		}
		return txtUsageQuotaThreshold;
	}

	public void setTxtUsageQuotaThreshold(String txtUsageQuotaThreshold) {
		this.txtUsageQuotaThreshold = txtUsageQuotaThreshold;
	}

	public List<Category> getCategoriesOfRI() {
		return categoriesOfRI;
	}

	public void setCategoriesOfRI(List<Category> categoriesOfRI) {
		this.categoriesOfRI = categoriesOfRI;
	}

	public boolean isApply() {
		return isApply;
	}

	public void setApply(boolean isApply) {
		this.isApply = isApply;
	}

}

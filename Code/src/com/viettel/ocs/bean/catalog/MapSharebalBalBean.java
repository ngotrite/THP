
package com.viettel.ocs.bean.catalog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
import com.viettel.ocs.constant.ContantsUtil;
import com.viettel.ocs.constant.TreeType;
import com.viettel.ocs.dao.BalTypeDAO;
import com.viettel.ocs.dao.CategoryDAO;
import com.viettel.ocs.dao.MapSharebalBalDAO;
import com.viettel.ocs.dao.ShareTypeDAO;
import com.viettel.ocs.dao.UnitTypeDAO;
import com.viettel.ocs.entity.BalType;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.MapAcmbalBal;
import com.viettel.ocs.entity.MapSharebalBal;
import com.viettel.ocs.entity.ReserveInfo;
import com.viettel.ocs.entity.ShareType;
import com.viettel.ocs.entity.TriggerOcs;
import com.viettel.ocs.entity.UnitType;
import com.viettel.ocs.util.ValidateUtil;

@ManagedBean(name = "mapShareBalBean")
@ViewScoped
@SuppressWarnings("serial")
public class MapSharebalBalBean extends BaseController implements Serializable {

	private String formType = "";
	private Category category;
	private CategoryDAO categoryDAO;
	private BalType balTypeFromView;
	private BalType balTypeToView;

	// MapShareBal---------------------------------------------
	private MapSharebalBal mapShareBal;
	private List<MapSharebalBal> mapShareBals;
	private MapSharebalBalDAO mapShareBalDAO;

	// ShareType
	private List<ShareType> shareTypes;

	// Combobox
	private List<UnitType> unitTypes;
	private UnitTypeDAO unitTypeDAO;
	private BalTypeDAO balTypeDAO;
	private List<Category> categories;

	private List<SelectItem> listPaymentType;
	private List<SelectItem> listBalLevelType;

	private boolean isEditting;
	private boolean isApply;

	// Init -----------------------------------------------------
	@PostConstruct
	public void init() {
		this.category = new Category();
		this.categoryDAO = new CategoryDAO();
		this.balTypeFromView = new BalType();
		this.balTypeToView = new BalType();
		this.balTypeDAO = new BalTypeDAO();

		// MapShareBal
		this.mapShareBal = new MapSharebalBal();
		this.mapShareBals = new ArrayList<MapSharebalBal>();
		this.mapShareBalDAO = new MapSharebalBalDAO();

		// ShareType
		this.shareTypes = new ArrayList<ShareType>();

		// List Combobox
		this.unitTypes = new ArrayList<UnitType>();
		this.unitTypeDAO = new UnitTypeDAO();
		this.categories = new ArrayList<Category>();

		this.isEditting = true;
		this.isApply = true;

	}

	// ***** MAP SHAREBAL BAL *****//

	public void refreshCategoriesofMapShare(Category category) {
		setFormType("list-mapsharebal-by-category");
		this.category = category;
		// this.mapShareBals =
		// mapShareBalDAO.findMSBBByConditions(category.getCategoryId());
		loadMapShareByCategory(category.getCategoryId());
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.execute("$('.mapShareBalClearFilter').click();");
	}

	public void refreshMapShareBal(MapSharebalBal mapShareBal) {
		try {
			this.mapShareBal = mapShareBal.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			getLogger().warn(e.getMessage(), e);
		}
		setFormType("mapsharebal-detail");
		loadComboListShareType();

		if (mapShareBal.getBaltypeId() != null && mapShareBal.getBaltypeId() != 0L) {
			balTypeFromView = balTypeDAO.get(mapShareBal.getBaltypeId());
			loadunitTypes();
			loadCategories();
		}

		if (mapShareBal.getSharebalTypeId() != null && mapShareBal.getSharebalTypeId() != 0L) {
			balTypeToView = balTypeDAO.get(mapShareBal.getSharebalTypeId());
			loadunitTypes();
			loadCategories();
		}
		this.isApply = true;

	}

	public void showDialogBalType() {
		super.openTreeCommonDialog(TreeType.CATALOG_BALANCES, CategoryType.CTL_BL_BAL_TYPE_NAME,
				CategoryType.CTL_BL_BAL_TYPE, false, null);
	}

	public void onDialogReturnFrom(SelectEvent event) {
		Object obj = event.getObject();
		if (obj instanceof BalType) {
			BalType balType = (BalType) obj;
			if (!exitBalType(balType)) {
				balTypeFromView = (BalType) obj;
				mapShareBal.setBaltypeId(balTypeFromView.getBalTypeId());
				loadunitTypes();
				loadCategories();
			}

		}

	}

	public void onDialogReturnTo(SelectEvent event) {
		Object obj = event.getObject();
		if (obj instanceof BalType) {
			BalType balType = (BalType) obj;
			if (!exitBalType(balType)) {
				balTypeToView = (BalType) obj;
				mapShareBal.setSharebalTypeId(balTypeToView.getBalTypeId());
				loadunitTypes();
				loadCategories();
			}

		}

	}

	private boolean exitBalType(BalType balType) {
		if (balType.getBalTypeId() == balTypeFromView.getBalTypeId()
				|| balType.getBalTypeId() == balTypeToView.getBalTypeId()) {
			this.showMessageWARN("", super.readProperties("validate.checkObjectExist"));
			return true;
		}
		return false;
	}

	public void commandAddNewMapShareBal() {
		super.getTreeCommonBean().hideAllCategoryComponent();
		super.getTreeCommonBean().setContentTitle(super.readProperties("title.MapShareBal"));
		MapSharebalBal mapShareBal = new MapSharebalBal();
		mapShareBal.setCategoryId(this.category.getCategoryId());
		refreshMapShareBal(mapShareBal);
		balTypeFromView = new BalType();
		balTypeToView = new BalType();
		this.isApply = false;
	}

	public void commandEditMapShareBal(MapSharebalBal mapShareBal) {
		super.getTreeCommonBean().hideAllCategoryComponent();
		super.getTreeCommonBean().setContentTitle(super.readProperties("title.MapShareBal"));
		refreshMapShareBal(mapShareBal);
		this.isApply = false;
	}

	public void commandCloneMapShareBal(MapSharebalBal mapShareBal) {
		super.getTreeCommonBean().hideAllCategoryComponent();
		refreshMapShareBal(mapShareBal);
		mapShareBal.setMapSharebalBalId(0L);
	}

	public void commandDeleteMapShareBal(MapSharebalBal mapShareBal) {
		mapShareBalDAO.delete(mapShareBal);
		mapShareBals.remove(mapShareBal);
		refreshCategoriesofMapShare(category);
		TreeCommonBean treeCommonBean = super.getTreeCommonBean();
		Category cat = categoryDAO.get(mapShareBal.getCategoryId());
		treeCommonBean.removeTreeNodeAll(mapShareBal);
		this.showMessageINFO("common.delete", " AccountBalances Mapping");
	}

	public List<Category> getCategoriesOfMapShareBal() {
		if (mapShareBal.getMapSharebalBalId() != 0L) {
			Category category = categoryDAO.get(mapShareBal.getCategoryId());
			return categoryDAO.findByTypeForSelectbox(category.getCategoryType());
		} else {
			return categoryDAO.findByTypeForSelectbox(this.category.getCategoryType());
		}
	}

	private void loadunitTypes() {
		if (balTypeFromView.getUnitTypeId() != null && balTypeFromView.getUnitTypeId() != 0L) {
			UnitType unitType = unitTypeDAO.get(balTypeFromView.getUnitTypeId());
			if (unitType != null) {
				unitTypes.add(unitType);
			}
		}
		if (balTypeToView.getUnitTypeId() != null && balTypeToView.getUnitTypeId() != 0L) {
			UnitType unitType = unitTypeDAO.get(balTypeToView.getUnitTypeId());
			if (unitType != null) {
				unitTypes.add(unitType);
			}
		}

	}

	private void loadCategories() {
		if (balTypeFromView.getCategoryId() != null && balTypeFromView.getCategoryId() != 0L) {
			Category category = categoryDAO.get(balTypeFromView.getCategoryId());
			if (category != null) {
				categories.add(category);
			}
		}
		if (balTypeToView.getCategoryId() != null && balTypeToView.getCategoryId() != 0L) {
			Category category = categoryDAO.get(balTypeToView.getCategoryId());
			if (category != null) {
				categories.add(category);
			}
		}

	}

	// load list MapAcm by Category
	public List<MapSharebalBal> loadMapShareByCategory(long categoryId) {
		mapShareBals = new ArrayList<MapSharebalBal>();
		mapShareBals = mapShareBalDAO.findMSBBByConditions(categoryId);
		return mapShareBals;
	}

	// load combo Bal Level=================================================
	public List<SelectItem> loadComboBalLevel() {
		listBalLevelType = new ArrayList<SelectItem>();
		listBalLevelType.add(new SelectItem(ContantsUtil.BalLevel.CUSTOMER, ContantsUtil.BalLevel.CUSTOMER_NAME));
		listBalLevelType.add(new SelectItem(ContantsUtil.BalLevel.SUBSCRIBER, ContantsUtil.BalLevel.SUBSCRIBER_NAME));
		listBalLevelType.add(new SelectItem(ContantsUtil.BalLevel.GROUP, ContantsUtil.BalLevel.GROUP_NAME));

		return listBalLevelType;
	}

	// load combo Payment Type=================================================
	public List<SelectItem> loadComboPaymentType() {
		listPaymentType = new ArrayList<SelectItem>();
		listPaymentType.add(new SelectItem(ContantsUtil.PaymentType.PAYMENT_TYPE_PREPAID,
				ContantsUtil.PaymentType.PAYMENT_TYPE_PREPAID_NAME));
		listPaymentType.add(new SelectItem(ContantsUtil.PaymentType.PAYMENT_TYPE_POSTPAID,
				ContantsUtil.PaymentType.PAYMENT_TYPE_POSTPAID_NAME));
		return listPaymentType;
	}

	// Load combo ShareType
	private void loadComboListShareType() {
		ShareTypeDAO shareTypeDAO = new ShareTypeDAO();
		shareTypes = shareTypeDAO.findAllShareType();
	}

	public void commandApplyMapShare() {
		TreeCommonBean treeCommonBean = super.getTreeCommonBean();
		Category cat = categoryDAO.get(mapShareBal.getCategoryId());
		if (validateMapShareBal()) {
			if (mapShareBalDAO.saveMapShare(mapShareBal)) {
				mapShareBals.add(mapShareBal);
				// cancelMapShare();
				this.isApply = true;
				treeCommonBean.updateTreeNode(mapShareBal, cat, null);
				this.showMessageINFO("common.save", " Balance Mapping");
			}
		}
	}

	public void cancelMapShare() {
		refreshCategoriesofMapShare(category);
		Category category = new Category();
	}

	// Validate
	private boolean validateMapShareBal() {
		boolean result = true;
		if (ValidateUtil.checkStringNullOrEmpty(mapShareBal.getName())) {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_WARN, this.readProperties("normalizer.validateError"),
							this.readProperties("normalizer.errorDataValueName")));
			result = false;
		}
		if (mapShareBalDAO.checkName(mapShareBal, mapShareBal.getName())) {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
					this.readProperties("normalizer.validateError"), this.readProperties("common.nameAlreadyExists")));
			result = false;
		}
		if (mapShareBal.getBaltypeId() == null || mapShareBal.getSharebalTypeId() == null) {
			this.showMessageWARN("validate.checkNullMapShare", super.readProperties("validate.checkValueNameNull"));
			result = false;
		}

		return result;
	}

	// Context Menu
	public void moveUpDownInCat(NodeSelectEvent event, boolean isUp) {
		MapSharebalBal mapShareBal = (MapSharebalBal) event.getTreeNode().getData();
		Object object = balTypeDAO.upDownObjectInCatWithDomain(mapShareBal, "index", isUp);
		if (object instanceof MapSharebalBal) {
			Category category = categoryDAO.get(mapShareBal.getCategoryId());
			MapSharebalBal nextMapShareBal = (MapSharebalBal) object;

			TreeCommonBean treeCommonBean = super.getTreeCommonBean();
			if (isUp) {
				treeCommonBean.moveUpTreeNode(mapShareBal);
			} else {
				treeCommonBean.moveDownTreeNode(mapShareBal);
			}
			treeCommonBean.updateTreeNode(nextMapShareBal, category, null);
			if (formType == "list-mapsharebal-by-category"
					&& nextMapShareBal.getCategoryId() == this.category.getCategoryId()) {
				refreshCategoriesofMapShare(category);
			}
			super.showNotificationSuccsess();
		}
	}
	
	// Edit ContextMenu
	public void editContextMenu(NodeSelectEvent nodeSelectEvent) {
		super.getTreeCommonBean().onNodeSelect(nodeSelectEvent);
		if (nodeSelectEvent != null) {
			MapSharebalBal item = (MapSharebalBal) nodeSelectEvent.getTreeNode().getData();
			commandEditMapShareBal(item);
		}
	}

	// Remove ContextMenu
	public void removeContextMenu(NodeSelectEvent nodeSelectEvent) {
		super.getTreeCommonBean().onNodeSelect(nodeSelectEvent);
		if (nodeSelectEvent != null) {
			MapSharebalBal item = (MapSharebalBal) nodeSelectEvent.getTreeNode().getData();
			commandDeleteMapShareBal(item);
			loadMapShareByCategory(item.getCategoryId());
		}
	}

	public void commanCancelMapShare() {
		Category category = new Category();
		category.setCategoryId(mapShareBal.getCategoryId());
		refreshCategoriesofMapShare(category);
	}

	// **** GET-SET ****//
	public String getFormType() {
		return formType;
	}

	public void setFormType(String formType) {
		this.formType = formType;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public MapSharebalBal getMapShareBal() {
		return mapShareBal;
	}

	public void setMapShareBal(MapSharebalBal mapShareBal) {
		this.mapShareBal = mapShareBal;
	}

	public List<MapSharebalBal> getMapShareBals() {
		return mapShareBals;
	}

	public void setMapShareBals(List<MapSharebalBal> mapShareBals) {
		this.mapShareBals = mapShareBals;
	}

	public List<ShareType> getShareTypes() {
		return shareTypes;
	}

	public void setShareTypes(List<ShareType> shareTypes) {
		this.shareTypes = shareTypes;
	}

	public BalType getBalTypeFromView() {
		return balTypeFromView;
	}

	public void setBalTypeFromView(BalType balTypeFromView) {
		this.balTypeFromView = balTypeFromView;
	}

	public BalType getBalTypeToView() {
		return balTypeToView;
	}

	public void setBalTypeToView(BalType balTypeToView) {
		this.balTypeToView = balTypeToView;
	}

	public List<UnitType> getUnitTypes() {
		return unitTypes;
	}

	public void setUnitTypes(List<UnitType> unitTypes) {
		this.unitTypes = unitTypes;
	}

	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}

	public List<SelectItem> getListPaymentType() {
		return listPaymentType;
	}

	public void setListPaymentType(List<SelectItem> listPaymentType) {
		this.listPaymentType = listPaymentType;
	}

	public List<SelectItem> getListBalLevelType() {
		return listBalLevelType;
	}

	public void setListBalLevelType(List<SelectItem> listBalLevelType) {
		this.listBalLevelType = listBalLevelType;
	}

	public boolean isApply() {
		return isApply;
	}

	public void setApply(boolean isApply) {
		this.isApply = isApply;
	}

}

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
import com.viettel.ocs.dao.MapAcmbalBalDAO;
import com.viettel.ocs.dao.UnitTypeDAO;
import com.viettel.ocs.entity.BalType;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.MapAcmbalBal;
import com.viettel.ocs.entity.UnitType;
import com.viettel.ocs.util.ValidateUtil;

@ManagedBean(name = "mapAcmBalBean")
@ViewScoped
@SuppressWarnings("serial")
public class MapAcmbalBalBean extends BaseController implements Serializable {
	private String formType = "";
	private Category category;
	private CategoryDAO categoryDAO;
	private BalType balTypeFromView;
	private BalType balTypeToView;

	// MapAcmBal---------------------------------------------
	private MapAcmbalBal mapAcmBal;
	private List<MapAcmbalBal> mapAcmBals;
	private MapAcmbalBalDAO mapAcmBalDAO;

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

		// MapAcmBal
		this.mapAcmBal = new MapAcmbalBal();
		this.mapAcmBals = new ArrayList<MapAcmbalBal>();
		this.mapAcmBalDAO = new MapAcmbalBalDAO();

		// List Combobox
		this.unitTypes = new ArrayList<UnitType>();
		this.unitTypeDAO = new UnitTypeDAO();
		this.categories = new ArrayList<Category>();

		this.isEditting = true;
		this.isApply = true;
	}

	// ***** MAP SHAREBAL BAL *****//

	public void refreshCategoriesofMapAcm(Category category) {
		setFormType("list-mapacmbal-by-category");
		this.category = category;
		// this.mapAcmBals =
		// mapAcmBalDAO.findMABBByConditions(category.getCategoryId());
		loadMapAcmByCategory(category.getCategoryId());
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.execute("$('.mapAcmBalClearFilter').click();");
	}

	public void refreshMapAcmBal(MapAcmbalBal mapAcmBal) {
		try {
			this.mapAcmBal = mapAcmBal.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			getLogger().warn(e.getMessage(), e);
		}
		setFormType("mapacmbal-detail");
		if (mapAcmBal.getBaltypeId() != null && mapAcmBal.getBaltypeId() != 0L) {
			this.balTypeFromView = balTypeDAO.get(mapAcmBal.getBaltypeId());
		}

		if (mapAcmBal.getAcmBaltypeId() != null && mapAcmBal.getAcmBaltypeId() != 0L) {
			this.balTypeToView = balTypeDAO.get(mapAcmBal.getAcmBaltypeId());
		}
		loadunitTypes();
		loadCategories();
		this.isApply = true;
	}

	public void showDialogBalTypeFrom() {
		super.openTreeCommonDialog(TreeType.CATALOG_BALANCES, CategoryType.CTL_BL_BAL_TYPE_NAME,
				CategoryType.CTL_BL_BAL_TYPE, false, null);
	}

	public void onDialogReturnFrom(SelectEvent event) {
		Object obj = event.getObject();
		if (obj instanceof BalType) {
			BalType balType = (BalType) obj;
			if (!exitBalType(balType)) {
				balTypeFromView = (BalType) obj;
				mapAcmBal.setBaltypeId(balTypeFromView.getBalTypeId());
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
				mapAcmBal.setAcmBaltypeId(balTypeToView.getBalTypeId());
				loadunitTypes();
				loadCategories();
			}

		}

	}
	
	public void showDialogBalTypeTo() {
		super.openTreeCommonDialog(TreeType.CATALOG_BALANCES, CategoryType.CTL_BL_BAL_TYPE_ACC_NAME,
				CategoryType.CTL_BL_BAL_TYPE_ACC, false, null);
	}

	private boolean exitBalType(BalType balType) {
		if (balType.getBalTypeId() == balTypeFromView.getBalTypeId()
				|| balType.getBalTypeId() == balTypeToView.getBalTypeId()) {
			this.showMessageWARN("", super.readProperties("validate.checkObjectExist"));
			return true;
		}
		return false;
	}

	public void commandAddNewMapAcmBal() {
		super.getTreeCommonBean().hideAllCategoryComponent();
		super.getTreeCommonBean().setContentTitle(super.readProperties("title.MapAcmBal"));
		MapAcmbalBal mapAcmBal = new MapAcmbalBal();
		mapAcmBal.setCategoryId(this.category.getCategoryId());
		refreshMapAcmBal(mapAcmBal);
		balTypeFromView = new BalType();
		balTypeToView = new BalType();
		this.isApply = false;
	}

	public void commandEditMapAcmBal(MapAcmbalBal mapAcmBal) {
		super.getTreeCommonBean().hideAllCategoryComponent();
		super.getTreeCommonBean().setContentTitle(super.readProperties("title.MapAcmBal"));
		refreshMapAcmBal(mapAcmBal);
		this.isApply = false;
	}

	public void commandCloneMapAcmBal(MapAcmbalBal mapAcmBal) {
		super.getTreeCommonBean().hideAllCategoryComponent();
		refreshMapAcmBal(mapAcmBal);
		mapAcmBal.setMapAcmBaltypeId(0L);
	}

	public void commandDeleteMapAcmBal(MapAcmbalBal mapAcmBal) {
		mapAcmBalDAO.delete(mapAcmBal);
		mapAcmBals.remove(mapAcmBal);
		refreshCategoriesofMapAcm(category);
		TreeCommonBean treeCommonBean = super.getTreeCommonBean();
		Category cat = categoryDAO.get(mapAcmBal.getCategoryId());
		treeCommonBean.removeTreeNodeAll(mapAcmBal);
		this.showMessageINFO("common.delete", " AccountBalances Mapping");
	}

	public List<Category> getCategoriesOfMapAcmBal() {
		if (mapAcmBal.getMapAcmBaltypeId() != 0L) {
			Category category = categoryDAO.get(mapAcmBal.getCategoryId());
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
	public List<MapAcmbalBal> loadMapAcmByCategory(long categoryId) {
		mapAcmBals = new ArrayList<MapAcmbalBal>();
		mapAcmBals = mapAcmBalDAO.findMABBByConditions(categoryId);
		return mapAcmBals;
	}

	// load combo Bal Level=================================================
	public List<SelectItem> loadComboBalLevel() {
		listBalLevelType = new ArrayList<SelectItem>();
		listBalLevelType.add(new SelectItem(ContantsUtil.BalLevel.CUSTOMER, ContantsUtil.BalLevel.CUSTOMER_NAME));
		listBalLevelType.add(new SelectItem(ContantsUtil.BalLevel.SUBSCRIBER, ContantsUtil.BalLevel.SUBSCRIBER_NAME));
		listBalLevelType.add(new SelectItem(ContantsUtil.BalLevel.GROUP, ContantsUtil.BalLevel.GROUP_NAME));
		listBalLevelType.add(new SelectItem(ContantsUtil.BalLevel.MEMBERSHIP, ContantsUtil.BalLevel.MEMBERSHIP_NAME));

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

	public void commandApplyMapAcm() {
		TreeCommonBean treeCommonBean = super.getTreeCommonBean();
		Category cat = categoryDAO.get(mapAcmBal.getCategoryId());
		if (validateMapAcmBal()) {
			if (mapAcmBalDAO.saveMapAcm(mapAcmBal)) {
				mapAcmBals.add(mapAcmBal);
				this.isApply = true;
				treeCommonBean.updateTreeNode(mapAcmBal, cat, null);
				this.showMessageINFO("common.save", " Balance Mapping");
			}
		}
	}

	public void cancelMapAcm() {
		refreshCategoriesofMapAcm(category);
		Category category = new Category();
	}

	// Validate
	private boolean validateMapAcmBal() {
		boolean result = true;
		if (ValidateUtil.checkStringNullOrEmpty(mapAcmBal.getName())) {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_WARN, this.readProperties("normalizer.validateError"),
							this.readProperties("normalizer.errorDataValueName")));
			result = false;
		}
		if (mapAcmBalDAO.checkName(mapAcmBal, mapAcmBal.getName())) {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
					this.readProperties("normalizer.validateError"), this.readProperties("common.nameAlreadyExists")));
			result = false;
		}
		if (mapAcmBal.getBaltypeId() == null || mapAcmBal.getAcmBaltypeId() == null) {
			this.showMessageWARN("validate.checkNullMapShare", super.readProperties("validate.checkValueNameNull"));
			result = false;
		}

		return result;
	}

	// Context Menu
	public void moveUpDownInCat(NodeSelectEvent event, boolean isUp) {
		MapAcmbalBal mapAcmBal = (MapAcmbalBal) event.getTreeNode().getData();
		Object object = balTypeDAO.upDownObjectInCatWithDomain(mapAcmBal, "index", isUp);
		if (object instanceof MapAcmbalBal) {
			Category category = categoryDAO.get(mapAcmBal.getCategoryId());
			MapAcmbalBal nextMapAcmBal = (MapAcmbalBal) object;

			TreeCommonBean treeCommonBean = super.getTreeCommonBean();
			if (isUp) {
				treeCommonBean.moveUpTreeNode(mapAcmBal);
			} else {
				treeCommonBean.moveDownTreeNode(mapAcmBal);
			}
			treeCommonBean.updateTreeNode(nextMapAcmBal, category, null);
			if (formType == "list-mapacmbal-by-category"
					&& nextMapAcmBal.getCategoryId() == this.category.getCategoryId()) {
				refreshCategoriesofMapAcm(category);
			}
			super.showNotificationSuccsess();
		}
	}

	// Edit ContextMenu
	public void editContextMenu(NodeSelectEvent nodeSelectEvent) {
		super.getTreeCommonBean().onNodeSelect(nodeSelectEvent);
		if (nodeSelectEvent != null) {
			MapAcmbalBal item = (MapAcmbalBal) nodeSelectEvent.getTreeNode().getData();
			commandEditMapAcmBal(item);
		}
	}

	// Remove ContextMenu
	public void removeContextMenu(NodeSelectEvent nodeSelectEvent) {
		super.getTreeCommonBean().onNodeSelect(nodeSelectEvent);
		if (nodeSelectEvent != null) {
			MapAcmbalBal item = (MapAcmbalBal) nodeSelectEvent.getTreeNode().getData();
			commandDeleteMapAcmBal(item);
			loadMapAcmByCategory(item.getCategoryId());
		}
	}

	public void commanCancelMapAcm() {
		Category category = new Category();
		category.setCategoryId(mapAcmBal.getCategoryId());
		refreshCategoriesofMapAcm(category);

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

	public MapAcmbalBal getMapAcmBal() {
		return mapAcmBal;
	}

	public void setMapAcmBal(MapAcmbalBal mapAcmBal) {
		this.mapAcmBal = mapAcmBal;
	}

	public List<MapAcmbalBal> getMapAcmBals() {
		return mapAcmBals;
	}

	public void setMapAcmBals(List<MapAcmbalBal> mapAcmBals) {
		this.mapAcmBals = mapAcmBals;
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

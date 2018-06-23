package com.viettel.ocs.bean.offer;

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
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;

import com.viettel.ocs.bean.BaseController;
import com.viettel.ocs.bean.TreeOfferBean;
import com.viettel.ocs.constant.CategoryType;
import com.viettel.ocs.constant.TreeType;
import com.viettel.ocs.dao.ActionDAO;
import com.viettel.ocs.dao.CategoryDAO;
import com.viettel.ocs.dao.SortPriceComponentDAO;
import com.viettel.ocs.dao.SortPriceRateTableMapDAO;
import com.viettel.ocs.entity.Action;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.PriceComponent;
import com.viettel.ocs.entity.RateTable;
import com.viettel.ocs.entity.SortPriceComponent;

@ManagedBean(name = "sortPriceComponentBean")
@ViewScoped
public class SortPriceComponentBean extends BaseController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private SortPriceComponent sortPriceComponentUI;
	private SortPriceComponentDAO sortPriceComponentDAO;
	private List<SortPriceComponent> lstSortPriceComponentByCategory;
	private List<SelectItem> listItemCategory;

	private RateTable rateTable;
	private List<RateTable> lstRateTables;
	private List<RateTable> lstRateTablesOld;

	private String formType = "";
	private boolean isEditting;
	private CategoryDAO categoryDAO;
	private long categoryID;

	@PostConstruct
	public void init() {
		this.isEditting = true;
		sortPriceComponentUI = new SortPriceComponent();
		categoryDAO = new CategoryDAO();
		// itemRateTable = new RateTable();
		sortPriceComponentDAO = new SortPriceComponentDAO();
		lstSortPriceComponentByCategory = new ArrayList<SortPriceComponent>();
		lstRateTables = new ArrayList<RateTable>();
		lstRateTablesOld = new ArrayList<RateTable>();
		this.categoryID = 0l;
	}

	private void clearFilter() {
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.execute("clearFilter('tblSPCWidgetVar')");
	}

	// Load list
	public List<SortPriceComponent> loadListSortPriceComponentByCategory(Long categoryId) {
		clearFilter();
		lstSortPriceComponentByCategory = sortPriceComponentDAO.findSortPriceComponentByCatId(categoryId);
		this.categoryID = categoryId;
		return lstSortPriceComponentByCategory;
	}

	// load list category by type SPC
	public List<SelectItem> loadCategory() {
		listItemCategory = new ArrayList<SelectItem>();
		CategoryDAO categoryDAO = new CategoryDAO();
		List<Category> listCategory = categoryDAO.findByTypeForSelectbox(CategoryType.OFF_SPC_SORT_PRICE_COMPONENT);
		if (listItemCategory != null && !listCategory.isEmpty()) {
			for (Category category : listCategory) {
				listItemCategory.add(new SelectItem(category.getCategoryId(), category.getCategoryName()));
			}
		}
		return listItemCategory;
	}

	// load list Ratetable by SPC
	public List<RateTable> loadRatetableSPC(long sortPriceComponentId) {
		lstRateTables = sortPriceComponentDAO.findRatetableBySPC(sortPriceComponentId);
		lstRateTablesOld.addAll(lstRateTables);
		return lstRateTables;
	}

	// Dialog
	public void showDialogRatetable(RateTable item) {

		List<Long> lstId = new ArrayList<Long>();
		if (lstRateTables != null) {
			for (RateTable rt : lstRateTables) {
				lstId.add(rt.getRateTableId());
			}
		}

		if (item == null || item.getRateTableId() <= 0) {
			super.openTreeOfferDialog(TreeType.OFFER_RATE_TABLE, CategoryType.OFF_RT_RATE_TABLE_NAME, 0, true, lstId);
		} else {
			super.openTreeOfferDialog(TreeType.OFFER_RATE_TABLE, CategoryType.OFF_RT_RATE_TABLE_NAME, 0, false, lstId);
		}
		rateTable = item;
		insert = false;
	}

	public int disableUpDown(RateTable rateTable) {
		if (rateTable != null) {
			if (lstRateTables.get(0).getRateTableId() == rateTable.getRateTableId()) {
				return 0;
			} else if (lstRateTables.get(lstRateTables.size() - 1).getRateTableId() == rateTable.getRateTableId()) {
				return 1;
			}
		}
		return -1;
	}

	// SelectEvent import org.primefaces.event.SelectEvent
	public void onDialogReturn(SelectEvent event) {
		Object[] objArr = new Object[1];
		if (event.getObject() instanceof Object[]) {
			objArr = (Object[]) event.getObject();
		} else {
			objArr[0] = event.getObject();
		}

		for (Object obj : objArr) {
			if (obj instanceof RateTable) {
				RateTable rateTableChange = (RateTable) obj;
				if (!existRT(rateTableChange)) {
					if (rateTable.getRateTableId() != 0L) {
						if (insert) {
							lstRateTables.add(lstRateTables.indexOf(rateTable) + 1, rateTableChange);
						} else {
							lstRateTables.set(lstRateTables.indexOf(rateTable), rateTableChange);
						}
					} else {
						lstRateTables.add(rateTableChange);
					}

				} else {
					if (rateTable.getRateTableId() == rateTableChange.getRateTableId() && !insert) {
						lstRateTables.set(lstRateTables.indexOf(rateTable), rateTableChange);
					} else {
						this.showMessageWARN("common.summary.warning", super.readProperties("common.objAlreadyExists"));
					}
				}
			}
		}
	}

	private boolean existRT(RateTable objRateTable) {
		for (RateTable rateTable : lstRateTables) {
			if (rateTable.getRateTableId() == objRateTable.getRateTableId()) {
				return true;

			}
		}
		return false;
	}

	public void refreshSPC(SortPriceComponent sortPriceComponent) {
		setFormType("detail-sort-price-compornent");
		try {
			setSortPriceComponentUI(sortPriceComponent.clone());
		} catch (CloneNotSupportedException e) {
			getLogger().warn(e.getMessage(), e);
		}
		setEditting(false);
		loadRatetableSPC(sortPriceComponent.getSortPriceComponentId());
	}

	// Action
	public void commandAddNew() {
		super.getTreeOfferBean().setContentTitle(super.readProperties("title.sortPriceComponent"));
		this.isEditting = true;
		setFormType("detail-sort-price-compornent");
		sortPriceComponentUI = new SortPriceComponent();
		sortPriceComponentUI.setCategoryId(categoryID);
		categoryDAO = new CategoryDAO();
		lstSortPriceComponentByCategory = new ArrayList<SortPriceComponent>();
		lstRateTables = new ArrayList<RateTable>();
		lstRateTablesOld = new ArrayList<RateTable>();
		TreeOfferBean treeOfferBean = super.getTreeOfferBean();
		treeOfferBean.hideCategory();
	}

	private boolean insert = false;

	public void addRTAt(RateTable item) {

		List<Long> lstId = new ArrayList<Long>();
		if (lstRateTables != null) {
			for (RateTable rt : lstRateTables) {
				lstId.add(rt.getRateTableId());
			}
		}

		if (item == null || item.getRateTableId() <= 0) {
			super.openTreeOfferDialog(TreeType.OFFER_RATE_TABLE, readProperties("ratetable.title"), 0, true, lstId);
		} else {
			super.openTreeOfferDialog(TreeType.OFFER_RATE_TABLE, readProperties("ratetable.title"), 0, false, lstId);
		}

		rateTable = item;
		insert = true;
	}

	public void commandEditItem(RateTable item) {
		super.getTreeOfferBean().setContentTitle(super.readProperties("title.rateTable"));
		super.getTreeOfferBean().setRateTableProperties(false, item.getCategoryId(), item, true, false);
	}

	public void commandRemoveItem(RateTable item) {
		lstRateTables.remove(item);
	}

	public void commandCancel() {
		setFormType("list-sort-price-compornent-by-category");
		loadListSortPriceComponentByCategory(sortPriceComponentUI.getCategoryId());
		this.isEditting = true;
	}

	public void commandAddNewRatetable() {
		RateTable itemNew = new RateTable();
		showDialogRatetable(itemNew);
		insert = false;
	}

	public void commandApply() {
		commandCleanTable();
		if (validateSortPC()) {
			if (sortPriceComponentUI.getSortPriceComponentId() > 0) {
				this.showMessageINFO("validate.editSuccess", super.readProperties("sortPriceComponent"));
			} else {
				this.showMessageINFO("validate.saveSuccess", super.readProperties("sortPriceComponent"));
			}
			sortPriceComponentDAO.saveSortPrice(sortPriceComponentUI, lstRateTables, lstRateTablesOld);
			lstRateTablesOld.clear();
			lstRateTablesOld.addAll(lstRateTables);
			TreeOfferBean treeOfferBean = super.getTreeOfferBean();
			Category cat = categoryDAO.get(sortPriceComponentUI.getCategoryId());
			treeOfferBean.updateTreeNode(sortPriceComponentUI, cat, lstRateTables);
			this.isEditting = false;
		}
	}

	// command up
	public void commandUpOnMap(RateTable item) {
		int indexCondition = lstRateTables.indexOf(item);
		if (indexCondition > 0) {
			RateTable itemBefore = lstRateTables.get(indexCondition - 1);
			lstRateTables.set(indexCondition - 1, item);
			lstRateTables.set(indexCondition, itemBefore);
		}

	}

	// command down
	public void commandDownOnMap(RateTable item) {
		int indexBasic = lstRateTables.indexOf(item);
		if (indexBasic < lstRateTables.size() && lstRateTables.size() != 1) {
			RateTable itemAfter = lstRateTables.get(indexBasic + 1);
			lstRateTables.set(indexBasic + 1, item);
			lstRateTables.set(indexBasic, itemAfter);
		}
	}

	// Clean Ratetable empty
	public void commandCleanTable() {
		if (!lstRateTables.isEmpty()) {
			Iterator<RateTable> listNormValues = lstRateTables.iterator();
			while (listNormValues.hasNext()) {
				RateTable normValue = listNormValues.next();
				if (normValue.getRateTableId() <= 0) {
					listNormValues.remove();
				}
			}
		}
	}

	// In table Category
	public void commandEditSPC(SortPriceComponent item) {
		this.isEditting = true;
		TreeOfferBean treeOfferBean = super.getTreeOfferBean();
		treeOfferBean.hideCategory();
		setFormType("detail-sort-price-compornent");
		setSortPriceComponentUI(item);
		loadRatetableSPC(item.getSortPriceComponentId());
		super.getTreeOfferBean().setContentTitle(super.readProperties("title.sortPriceComponent"));
	}

	public void commandCloneSPC(SortPriceComponent item) {
		SortPriceComponent sortPriceComponentClone = item;
		Map<String, Object> options = new HashMap<String, Object>();
		options.put("modal", true);
		options.put("widgetVar", "dlgTree");
		options.put("width", 500);
		options.put("height", 450);
		options.put("resizable", false);
		options.put("contentWidth", "100%");
		options.put("contentHeight", "100%");
		Map<String, List<String>> mapPara = new HashMap<String, List<String>>();
		List<String> lstPara = new ArrayList<String>();
		lstPara.add("spc;" + sortPriceComponentClone.getSortPriceComponentId());
		mapPara.put("param", lstPara);
		List<String> posIndex = new ArrayList<>();
		posIndex.add("11");
		mapPara.put("index", posIndex);
		List<String> typeTreeClone = new ArrayList<>();
		typeTreeClone.add("2");
		mapPara.put("treeTypeClone", typeTreeClone);
		RequestContext.getCurrentInstance().openDialog("/pages/offer/context_offer/clone_common.xhtml", options,
				mapPara);
	}

	public void commandDeleteSPC(SortPriceComponent item) {
		SortPriceRateTableMapDAO mapDAO = new SortPriceRateTableMapDAO();
		ActionDAO actionDAO = new ActionDAO();
		if (!actionDAO.checkSortInAction(item.getSortPriceComponentId())
				&& !actionDAO.checkPriorityFilterInAction(item.getSortPriceComponentId())) {
			mapDAO.deleteSortPriceRateTableMapBySPCId(item);
			loadListSortPriceComponentByCategory(item.getCategoryId());
			TreeOfferBean treeOfferBean = super.getTreeOfferBean();
			treeOfferBean.removeTreeNodeAll(item);
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, this.readProperties("common.delete"),
					this.readProperties("title.sortPriceComponent")));
		} else {
			this.showMessageWARN("common.summary.warning", super.readProperties("sortPriceComponent.used"));
		}
	}

	// In Tree
	public void commandAddNewSPCTree(NodeSelectEvent nodeSelectEvent) {
		super.getTreeOfferBean().onNodeSelect(nodeSelectEvent);
		this.isEditting = true;
		setFormType("detail-sort-price-compornent");
		sortPriceComponentUI = new SortPriceComponent();
		SortPriceComponent item = (SortPriceComponent) nodeSelectEvent.getTreeNode().getData();
		if (item != null) {
			sortPriceComponentUI.setCategoryId(item.getCategoryId());
		}
		categoryDAO = new CategoryDAO();
		lstSortPriceComponentByCategory = new ArrayList<SortPriceComponent>();
		lstRateTables = new ArrayList<RateTable>();
		lstRateTablesOld = new ArrayList<RateTable>();
		TreeOfferBean treeOfferBean = super.getTreeOfferBean();
		treeOfferBean.hideCategory();
	}

	public void commandEditSPCTree(NodeSelectEvent nodeSelectEvent) {
		super.getTreeOfferBean().onNodeSelect(nodeSelectEvent);
		super.getTreeOfferBean().setContentTitle(super.readProperties("title.sortPriceComponent"));
		TreeOfferBean treeOfferBean = super.getTreeOfferBean();
		setFormType("detail-sort-price-compornent");
		this.isEditting = true;
		treeOfferBean.hideCategory();
	}

	public void commandDeleteSPCTree(NodeSelectEvent nodeSelectEvent) {

		super.getTreeOfferBean().onNodeSelect(nodeSelectEvent);
		if (nodeSelectEvent != null) {
			SortPriceComponent item = (SortPriceComponent) nodeSelectEvent.getTreeNode().getData();
			Object object = nodeSelectEvent.getTreeNode().getParent().getData();
			TreeOfferBean treeOfferBean = super.getTreeOfferBean();
			if (object instanceof Category) {
				SortPriceRateTableMapDAO mapDAO = new SortPriceRateTableMapDAO();
				ActionDAO actionDAO = new ActionDAO();
				if (!actionDAO.checkSortInAction(item.getSortPriceComponentId())
						&& !actionDAO.checkPriorityFilterInAction(item.getSortPriceComponentId())) {
					mapDAO.deleteSortPriceRateTableMapBySPCId(item);
					// setFormType("list-sort-price-compornent-by-category");
					setFormType("");
					// loadListSortPriceComponentByCategory(item.getCategoryId());
					this.isEditting = true;
					treeOfferBean.removeTreeNodeAll(item);
					FacesContext context = FacesContext.getCurrentInstance();
					context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
							this.readProperties("common.delete"), this.readProperties("title.sortPriceComponent")));
				} else {
					this.showMessageWARN("common.summary.warning", super.readProperties("sortPriceComponent.used"));
				}
			}
			if (object instanceof Action) {
				// Delete Map Action - SPC
				Action action = (Action) object;
				action.setSortPriceComponentId(null);
				ActionDAO actionDAO = new ActionDAO();
				actionDAO.saveOrUpdate(action);
				treeOfferBean.removeTreeNode(item, action);
			}
		}
	}

	public void commandCheckDependencies(NodeSelectEvent nodeSelectEvent) {
		if (nodeSelectEvent != null) {
			SortPriceComponent item = (SortPriceComponent) nodeSelectEvent.getTreeNode().getData();
			showDependencies(item.getSortPriceComponentId(), SortPriceComponent.class);
		}
	}

	public void commandCloneSPCTree() {
		SortPriceComponent sortPriceComponentClone = this.sortPriceComponentUI;
		Map<String, Object> options = new HashMap<String, Object>();
		options.put("modal", true);
		options.put("widgetVar", "dlgTree");
		options.put("width", 500);
		options.put("height", 300);
		options.put("resizable", false);
		options.put("contentWidth", "100%");
		options.put("contentHeight", "100%");
		Map<String, List<String>> mapPara = new HashMap<String, List<String>>();
		List<String> lstPara = new ArrayList<String>();
		lstPara.add("spc;" + sortPriceComponentClone.getSortPriceComponentId());
		mapPara.put("param", lstPara);
		List<String> posIndex = new ArrayList<>();
		posIndex.add("11");
		mapPara.put("index", posIndex);
		List<String> typeTreeClone = new ArrayList<>();
		typeTreeClone.add("2");
		mapPara.put("treeTypeClone", typeTreeClone);
		RequestContext.getCurrentInstance().openDialog("/pages/offer/context_offer/clone_common.xhtml", options,
				mapPara);
	}

	public void onDialogCloneReturn(SelectEvent event) {
		Object object = event.getObject();
		if (object instanceof SortPriceComponent) {
			TreeOfferBean treeOfferBean = super.getTreeOfferBean();
			treeOfferBean.updateTreeNode((SortPriceComponent) object,
					categoryDAO.get(((SortPriceComponent) object).getCategoryId()), null);
			commandEditSPC((SortPriceComponent) object);
			this.isEditting = false;
			this.showMessageINFO("validate.cloneSuccess", super.readProperties(""));
		}
	}

	/**
	 * 
	 * @param isUp
	 *            true is up
	 */
	public void moveUpDownInCat(NodeSelectEvent event, boolean isUp) {
		SortPriceComponent sortPriceComponent = (SortPriceComponent) event.getTreeNode().getData();
		Object object = sortPriceComponentDAO.upDownObjectInCatWithDomain(sortPriceComponent, "index", isUp);
		if (object instanceof SortPriceComponent) {
			Category category = categoryDAO.get(sortPriceComponent.getCategoryId());
			SortPriceComponent nextAction = (SortPriceComponent) object;

			TreeOfferBean treeOfferBean = super.getTreeOfferBean();
			if (isUp) {
				treeOfferBean.moveUpTreeNode(sortPriceComponent, category);
			} else {
				treeOfferBean.moveDownTreeNode(sortPriceComponent, category);
			}
			treeOfferBean.updateTreeNode(nextAction, category, null);
			if (formType == "list-sort-price-compornent-by-category" && category.getCategoryId() == categoryID) {
				loadListSortPriceComponentByCategory(categoryID);
			}
			super.showNotificationSuccsess();
		}
	}

	public void addChildren(NodeSelectEvent nodeSelectEvent) {
		super.getTreeOfferBean().onNodeSelect(nodeSelectEvent);
	}

	public void redirectChangeCate() {
		this.openTreeCategoryDialog(TreeType.OFFER_SORT_PRICE_COMPONENT, "SortPriceComponent", 0);
	}

	public void onDialogReturnCategory(SelectEvent event) {
		Object obj = event.getObject();
		if (obj instanceof Category) {
			Category cate = (Category) obj;
			this.sortPriceComponentUI.setCategoryId(cate.getCategoryId());
			if (sortPriceComponentDAO.moveToCate(sortPriceComponentUI)) {
				TreeOfferBean treeOfferBean = super.getTreeOfferBean();
				treeOfferBean.updateTreeNode(this.sortPriceComponentUI, cate, null);
				this.showMessageINFO("common.moveCate", " Success ");
				this.isEditting = false;
			}
		}
	}

	// Validate
	private boolean validateSortPC() {
		if (sortPriceComponentDAO.checkName(sortPriceComponentUI, isEditting)) {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
					this.readProperties("normalizer.validateError"), this.readProperties("common.nameAlreadyExists")));
			return false;
		}
		return true;
	}

	// GetSet
	public SortPriceComponent getSortPriceComponentUI() {
		return sortPriceComponentUI;
	}

	public void setSortPriceComponentUI(SortPriceComponent sortPriceComponentUI) {
		this.sortPriceComponentUI = sortPriceComponentUI;
	}

	public List<SortPriceComponent> getLstSortPriceComponentByCategory() {
		return lstSortPriceComponentByCategory;
	}

	public void setLstSortPriceComponentByCategory(List<SortPriceComponent> lstSortPriceComponentByCategory) {
		this.lstSortPriceComponentByCategory = lstSortPriceComponentByCategory;
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

	public boolean isEditting() {
		return isEditting;
	}

	public void setEditting(boolean isEditting) {
		this.isEditting = isEditting;
	}

	public List<SelectItem> getListItemCategory() {
		return listItemCategory;
	}

	public void setListItemCategory(List<SelectItem> listItemCategory) {
		this.listItemCategory = listItemCategory;
	}

	public List<RateTable> getLstRateTables() {
		return lstRateTables;
	}

	public void setLstRateTables(List<RateTable> lstRateTables) {
		this.lstRateTables = lstRateTables;
	}

	public RateTable getRateTable() {
		return rateTable;
	}

	public void setRateTable(RateTable rateTable) {
		this.rateTable = rateTable;
	}

	public List<RateTable> getLstRateTablesOld() {
		return lstRateTablesOld;
	}

	public void setLstRateTablesOld(List<RateTable> lstRateTablesOld) {
		this.lstRateTablesOld = lstRateTablesOld;
	}

	public long getCategoryID() {
		return categoryID;
	}

	public void setCategoryID(long categoryID) {
		this.categoryID = categoryID;
	}

}

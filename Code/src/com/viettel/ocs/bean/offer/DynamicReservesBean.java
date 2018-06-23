package com.viettel.ocs.bean.offer;

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

import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;

import com.viettel.ocs.bean.BaseController;
import com.viettel.ocs.bean.TreeOfferBean;
import com.viettel.ocs.constant.CategoryType;
import com.viettel.ocs.constant.TreeType;
import com.viettel.ocs.dao.ActionDAO;
import com.viettel.ocs.dao.CategoryDAO;
import com.viettel.ocs.dao.DynamicReserveDAO;
import com.viettel.ocs.dao.RateTableDAO;
import com.viettel.ocs.entity.Action;
import com.viettel.ocs.entity.ActionType;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.DynamicReserve;
import com.viettel.ocs.entity.RateTable;
import com.viettel.ocs.util.ValidateUtil;

/**
 * DynamicReservesBean
 * 
 * @author NAMPV
 *
 */
@ManagedBean(name = "dynamicReservesBean")
@ViewScoped
public class DynamicReservesBean extends BaseController implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5445196399233349130L;
	private String formType;
	private DynamicReserveDAO dynamicReserveDAO;
	private DynamicReserve dynamicReserve;
	// Property List
	private List<DynamicReserve> lstDynamicReserve;
	// Details
	private RateTableDAO rateTableDAO;
	private List<RateTable> lstRateTable;
	private Category category;
	private CategoryDAO categoryDAO;
	private RateTable rateTable;
	private boolean isEditting;
	private List<Category> categoriesOfDynamicReserve;

	public RateTable getRateTable() {
		return rateTable;
	}

	public void setRateTable(RateTable rateTable) {
		this.rateTable = rateTable;
	}

	public String getFormType() {
		return formType;
	}

	public void setFormType(String formType) {
		this.formType = formType;
	}

	public DynamicReserve getDynamicReserve() {
		return dynamicReserve;
	}

	public void setDynamicReserve(DynamicReserve dynamicReserve) {
		this.dynamicReserve = dynamicReserve;
	}

	public List<DynamicReserve> getLstDynamicReserve() {
		return lstDynamicReserve;
	}

	public void setLstDynamicReserve(List<DynamicReserve> lstDynamicReserve) {
		this.lstDynamicReserve = lstDynamicReserve;
	}

	public List<RateTable> getLstRateTable() {
		return lstRateTable;
	}

	public void setLstRateTable(List<RateTable> lstRateTable) {
		this.lstRateTable = lstRateTable;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	// DEFAULT DATA
	@PostConstruct
	public void init() {
		this.category = new Category();
		this.dynamicReserveDAO = new DynamicReserveDAO();
		// LIST
		this.lstDynamicReserve = new ArrayList<DynamicReserve>();
		// DETAIL
		this.dynamicReserve = new DynamicReserve();
		this.categoryDAO = new CategoryDAO();
		this.rateTableDAO = new RateTableDAO();
		this.lstRateTable = new ArrayList<RateTable>();
		// this.rateTable = new RateTable();

		this.isEditting = true;

	}

	// ***********EVENT LIST************

	private void initRateTable() {
		if (dynamicReserve != null && dynamicReserve.getDynamicReserveId() != 0L)
			lstRateTable = rateTableDAO.findRateTableByDynamicReserve(dynamicReserve.getDynamicReserveId());
		else
			lstRateTable = new ArrayList<RateTable>();
	}

	public void refreshDynamicReserve(DynamicReserve dynamicReserve) {
		formType = "form-dynamicreserves-detail";
		this.dynamicReserve = dynamicReserve;
		loadCategoriesOfDynamicReserve();
		initRateTable();
	}

	public void refreshCategories(Category category) {
		formType = "form-dynamicreserves-list";
		this.category = category;
		loadDRByCategory(category.getCategoryId());
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.execute("$('.DRClearFilter').click();");
		loadCategoriesOfDynamicReserve();
	}

	public List<DynamicReserve> loadDRByCategory(long categoryId) {
		lstDynamicReserve = new ArrayList<DynamicReserve>();
		lstDynamicReserve = dynamicReserveDAO.findDRByCategoryId(categoryId);
		return lstDynamicReserve;
	}

	public void editDynamicReserve(DynamicReserve dynamicReserve) {
		TreeOfferBean treeOfferBean = super.getTreeOfferBean();
		this.isEditting = true;
		treeOfferBean.hideCategory();
		treeOfferBean.setContentTitle(super.readProperties("dynamicreserve.title"));
		refreshDynamicReserve(dynamicReserve);

	}

	// Edit ContextMenu
	public void commandEditContextMenu(NodeSelectEvent nodeSelectEvent) {
		super.getTreeOfferBean().onNodeSelect(nodeSelectEvent);
		TreeOfferBean treeOfferBean = super.getTreeOfferBean();
		this.isEditting = true;
		treeOfferBean.hideCategory();
		refreshDynamicReserve(dynamicReserve);
	}

	public void cloneDynamicReserve(DynamicReserve dynamicReserve) {
		dynamicReserve.setDynamicReserveId(0L);
		TreeOfferBean treeOfferBean = super.getTreeOfferBean();
		treeOfferBean.hideCategory();
		treeOfferBean.setContentTitle(super.readProperties("dynamicreserve.title"));
		refreshDynamicReserve(dynamicReserve);
	}

	public void addNewDynamicReserve() {
		TreeOfferBean treeOfferBean = super.getTreeOfferBean();
		treeOfferBean.hideCategory();
		DynamicReserve dynamicReserve = new DynamicReserve();
		dynamicReserve.setCategoryId(category.getCategoryId());
		treeOfferBean.setContentTitle(super.readProperties("dynamicreserve.title"));
		refreshDynamicReserve(dynamicReserve);
		this.isEditting = true;
	}

	public void removeDynamicReserve(DynamicReserve dynamicReserve) {
		ActionDAO actionDAO = new ActionDAO();
		if (!actionDAO.checkDynamicInAction(dynamicReserve.getDynamicReserveId())) {
			dynamicReserveDAO.deleteDynamicByAction(dynamicReserve);
			setFormType("");
			TreeOfferBean treeOfferBean = super.getTreeOfferBean();
			treeOfferBean.removeTreeNodeAll(dynamicReserve);
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, this.readProperties("common.delete"),
					this.readProperties("title.dynamicReserve")));
		} else {
			this.showMessageWARN("common.summary.warning", super.readProperties("dynamicreserve.used"));
		}

	}

	// Context Menu

	public void commandAddNewContextMenu(NodeSelectEvent nodeSelectEvent) {
		super.getTreeOfferBean().onNodeSelect(nodeSelectEvent);
		TreeOfferBean treeOfferBean = super.getTreeOfferBean();
		treeOfferBean.hideCategory();
		DynamicReserve dynamicReserve = new DynamicReserve();
		dynamicReserve.setCategoryId(category.getCategoryId());
		refreshDynamicReserve(dynamicReserve);
		this.isEditting = true;

	}

	public void removeDynamicReserve(NodeSelectEvent nodeSelectEvent) {

		super.getTreeOfferBean().onNodeSelect(nodeSelectEvent);
		if (nodeSelectEvent != null) {
			DynamicReserve item = (DynamicReserve) nodeSelectEvent.getTreeNode().getData();
			Object object = nodeSelectEvent.getTreeNode().getParent().getData();
			TreeOfferBean treeOfferBean = super.getTreeOfferBean();
			if (object instanceof Category) {
				ActionDAO actionDAO = new ActionDAO();
				if (!actionDAO.checkDynamicInAction(item.getDynamicReserveId())) {
					dynamicReserveDAO.deleteDynamicByAction(item);
					setFormType("");
					treeOfferBean.removeTreeNodeAll(item);
					FacesContext context = FacesContext.getCurrentInstance();
					context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
							this.readProperties("common.delete"), this.readProperties("title.dynamicReserve")));
				} else {
					this.showMessageWARN("common.summary.warning", super.readProperties("dynamicreserve.used"));
				}
			}
			if (object instanceof Action) {
				// Delete Map Action - DR
				Action action = (Action) object;
				action.setDynamicReserveId(null);
				ActionDAO actionDAO = new ActionDAO();
				actionDAO.saveOrUpdate(action);
				treeOfferBean.removeTreeNode(item, action);
			}
		}

	}

	// CommandCheckDependencies ContextMenu
	public void commandCheckDependencies(NodeSelectEvent nodeSelectEvent) {
		if (nodeSelectEvent != null) {
			DynamicReserve item = (DynamicReserve) nodeSelectEvent.getTreeNode().getData();
			showDependencies(item.getDynamicReserveId(), DynamicReserve.class);
		}
	}

	// Command Clone ContextMenu
	public void commandCloneContextMenu() {
		DynamicReserve dynamicReserve = this.dynamicReserve;

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
		lstPara.add("dr;" + dynamicReserve.getDynamicReserveId());
		mapPara.put("param", lstPara);
		List<String> posIndex = new ArrayList<>();
		posIndex.add("11");
		mapPara.put("index", posIndex);
		List<String> typeTreeClone = new ArrayList<>();
		typeTreeClone.add("3");
		mapPara.put("treeTypeClone", typeTreeClone);
		RequestContext.getCurrentInstance().openDialog("/pages/offer/context_offer/clone_common.xhtml", options,
				mapPara);
	}

	// Command Clone Table
	public void commandCloneTable(DynamicReserve item) {
		DynamicReserve dynamicReserve = item;

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
		lstPara.add("dr;" + dynamicReserve.getDynamicReserveId());
		mapPara.put("param", lstPara);
		List<String> posIndex = new ArrayList<>();
		posIndex.add("11");
		mapPara.put("index", posIndex);
		List<String> typeTreeClone = new ArrayList<>();
		typeTreeClone.add("3");
		mapPara.put("treeTypeClone", typeTreeClone);
		RequestContext.getCurrentInstance().openDialog("/pages/offer/context_offer/clone_common.xhtml", options,
				mapPara);
	}

	public void onDialogCloneReturn(SelectEvent event) {
		Object object = event.getObject();
		if (object instanceof DynamicReserve) {
			TreeOfferBean treeOfferBean = super.getTreeOfferBean();
			treeOfferBean.updateTreeNode((DynamicReserve) object,
					categoryDAO.get(((DynamicReserve) object).getCategoryId()), null);
			editDynamicReserve((DynamicReserve) object);
			this.showMessageINFO("validate.cloneSuccess", super.readProperties(""));
		}
	}

	// Move to Category

	public void redirectChangeCate() {
		this.openTreeCategoryDialog(TreeType.OFFER_DYNAMIC_RESERVE, "Dynamic Reserve", 0);
	}

	public void onDialogReturnCategory(SelectEvent event) {
		Object obj = event.getObject();
		if (obj instanceof Category) {
			Category cate = (Category) obj;
			this.dynamicReserve.setCategoryId(cate.getCategoryId());
			dynamicReserveDAO.saveOrUpdate(this.dynamicReserve);
			TreeOfferBean treeOfferBean = super.getTreeOfferBean();
			treeOfferBean.updateTreeNode(this.dynamicReserve, cate, null);
			this.showMessageINFO("common.moveCate", " Success ");
			this.isEditting = false;
		}
	}

	// ***********EVENT DETAIL***************
	public void loadCategoriesOfDynamicReserve() {
		// return categoryDAO.findByType(CategoryType.OFF_DR_DYNAMIC_RESERVE);
		categoriesOfDynamicReserve = categoryDAO.findByTypeForSelectbox(CategoryType.OFF_DR_DYNAMIC_RESERVE);
	}

	private boolean insert = false;

	public void addRTAt(RateTable item) {
		
		List<Long> lstId = new ArrayList<Long>();
		if (lstRateTable != null) {
			for (RateTable rt : lstRateTable) {
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

	public void editRateTable(RateTable rateTable) {
		TreeOfferBean offerBean = getTreeOfferBean();
		offerBean.setContentTitle(super.readProperties("title.rateTable"));
		offerBean.setRateTableProperties(false, rateTable.getCategoryId(), rateTable, true, false);
	}

	public void cloneRateTable(RateTable rateTable) {
		rateTable.setRateTableId(0L);
		TreeOfferBean offerBean = getTreeOfferBean();
		offerBean.setContentTitle(super.readProperties("title.rateTable"));
		offerBean.setRateTableProperties(false, rateTable.getCategoryId(), rateTable, false, false);
	}

	public void removeRateTable(RateTable rateTable) {
		if (dynamicReserve != null && rateTable != null) {
			lstRateTable.remove(rateTable);
			// rateTableDAO.delete(rateTable);
			// rateTableDAO.deleteDynamicReserveRateTableMapByRateTableId(rateTable,
			// dynamicReserve.getDynamicReserveId());
			this.showMessageINFO("common.delete", " Dynamic Reserve RateTableMap ");
		}
	}

	public void chooseRT(RateTable item) {
		
		List<Long> lstId = new ArrayList<Long>();
		if (lstRateTable != null) {
			for (RateTable rt : lstRateTable) {
				lstId.add(rt.getRateTableId());
			}
		}
		
		if (item == null || item.getRateTableId() <= 0) {			
			super.openTreeOfferDialog(TreeType.OFFER_RATE_TABLE, readProperties("ratetable.title"), 0, true, lstId);
		} else {
			super.openTreeOfferDialog(TreeType.OFFER_RATE_TABLE, readProperties("ratetable.title"), 0, false, lstId);
		}
		rateTable = item;
		insert = false;
	}

	public void commandAddNewRatetable() {
		insert = false;
		RateTable itemNew = new RateTable();
		chooseRT(itemNew);
		// lstRateTable.add(itemNew);
	}

	// public void onDialogRTReturn(SelectEvent event) {
	// Object object = event.getObject();
	// if (object instanceof RateTable) {
	// RateTable rateTable = (RateTable) object;
	// if (!exitsRTs(rateTable)) {
	// lstRateTable.add(rateTable);
	// }
	// }
	// }
	//

	public void onDialogRTReturn(SelectEvent event) {
		Object[] objArr = new Object[1];
		if (event.getObject() instanceof Object[]) {
			objArr = (Object[]) event.getObject();
		} else {
			objArr[0] = event.getObject();
		}

		for (Object obj : objArr) {
			if (obj instanceof RateTable) {
				RateTable rateTableChange = (RateTable) obj;
				if (!exitsRTs(rateTableChange)) {
					if (rateTable.getRateTableId() != 0L) {
						if (insert) {
							lstRateTable.add(lstRateTable.indexOf(rateTable) + 1, rateTableChange);
						} else {
							lstRateTable.set(lstRateTable.indexOf(rateTable), rateTableChange);
						}
					} else {
						lstRateTable.add(rateTableChange);
					}

				} else {
					if (rateTable.getRateTableId() == rateTableChange.getRateTableId() && !insert) {
						lstRateTable.set(lstRateTable.indexOf(rateTable), rateTableChange);
					} else {
						this.showMessageWARN("common.summary.warning", super.readProperties("common.objAlreadyExists"));
					}
				}
			}
		}
	}

	private boolean exitsRTs(RateTable tableInput) {
		for (RateTable table : lstRateTable) {
			if (table.getRateTableId() == tableInput.getRateTableId()) {
				return true;
			}
		}
		return false;
	}

	public void saveDR() {
		// setIndexForDynamicReserve();
		if (validateDynamicReserve()) {
			if (dynamicReserveDAO.saveDynamicReserveDetail(dynamicReserve, lstRateTable)) {
				TreeOfferBean treeOfferBean = super.getTreeOfferBean();
				Category cat = categoryDAO.get(dynamicReserve.getCategoryId());
				treeOfferBean.updateTreeNode(dynamicReserve, cat, lstRateTable);
				this.isEditting = false;
				this.showMessageINFO("common.save", " Dynamic Reserve ");
			} else {
				this.showMessageWARN("common.save", " Dynamic Reserve ");
			}
		}

	}

	// Validate
	private boolean validateDynamicReserve() {
		boolean result = true;
		if (ValidateUtil.checkStringNullOrEmpty(dynamicReserve.getDynamicReserveName())) {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_WARN, this.readProperties("normalizer.validateError"),
							this.readProperties("normalizer.errorDataValueName")));
			result = false;
		}

		if (dynamicReserveDAO.checkName(dynamicReserve)) {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
					this.readProperties("normalizer.validateError"), this.readProperties("common.nameAlreadyExists")));
			result = false;
		}

		return result;
	}

	// command up
	public void commandUpOnMap(RateTable item) {
		int indexCondition = lstRateTable.indexOf(item);
		if (indexCondition > 0) {
			RateTable itemBefore = lstRateTable.get(indexCondition - 1);
			lstRateTable.set(indexCondition - 1, item);
			lstRateTable.set(indexCondition, itemBefore);
		}
		dynamicReserveDAO.saveDynamicReserveDetail(dynamicReserve, lstRateTable);
	}

	// command down
	public void commandDownOnMap(RateTable item) {
		int indexBasic = lstRateTable.indexOf(item);
		if (indexBasic < lstRateTable.size() && lstRateTable.size() != 1) {
			RateTable itemAfter = lstRateTable.get(indexBasic + 1);
			lstRateTable.set(indexBasic + 1, item);
			lstRateTable.set(indexBasic, itemAfter);
		}

		dynamicReserveDAO.saveDynamicReserveDetail(dynamicReserve, lstRateTable);
	}

	public void setIndexForDynamicReserve() {
		if (this.dynamicReserve == null || this.dynamicReserve.getDynamicReserveId() == 0) {
			DynamicReserve lastItem = dynamicReserveDAO.findDynamicReserveLastIndex();
			if (lastItem != null) {
				this.dynamicReserve.setPosIndex(lastItem.getPosIndex() + 1);
			} else {
				this.dynamicReserve.setPosIndex(0l);
			}
		}
	}

	/**
	 * 
	 * @param isUp
	 *            true is up
	 */
	public void moveUpDownInCat(NodeSelectEvent event, boolean isUp) {
		DynamicReserve block = (DynamicReserve) event.getTreeNode().getData();
		Object object = dynamicReserveDAO.upDownObjectInCatWithDomain(block, "posIndex", isUp);
		if (object instanceof DynamicReserve) {
			Category category = categoryDAO.get(block.getCategoryId());
			DynamicReserve nextBlock = (DynamicReserve) object;

			TreeOfferBean treeOfferBean = super.getTreeOfferBean();
			if (isUp) {
				treeOfferBean.moveUpTreeNode(block, category);
			} else {
				treeOfferBean.moveDownTreeNode(block, category);
			}
			treeOfferBean.updateTreeNode(nextBlock, category, null);
			if (formType == "form-dynamicreserves-list" && category.getCategoryId() == this.category.getCategoryId()) {
				refreshCategories(this.category);
			}
			super.showNotificationSuccsess();
		}
	}

	public boolean isEditting() {
		return isEditting;
	}

	public void setEditting(boolean isEditting) {
		this.isEditting = isEditting;
	}

	public List<Category> getCategoriesOfDynamicReserve() {
		return categoriesOfDynamicReserve;
	}

	public void setCategoriesOfDynamicReserve(List<Category> categoriesOfDynamicReserve) {
		this.categoriesOfDynamicReserve = categoriesOfDynamicReserve;
	}

}

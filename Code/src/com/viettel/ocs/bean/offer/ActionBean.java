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

import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;

import com.viettel.ocs.bean.BaseController;
import com.viettel.ocs.bean.TreeOfferBean;
import com.viettel.ocs.constant.CategoryType;
import com.viettel.ocs.constant.TreeType;
import com.viettel.ocs.dao.ActionDAO;
import com.viettel.ocs.dao.ActionPriceComponentMapDAO;
import com.viettel.ocs.dao.ActionTypeDAO;
import com.viettel.ocs.dao.CategoryDAO;
import com.viettel.ocs.dao.DynamicReserveDAO;
import com.viettel.ocs.dao.OfferActionMapDAO;
import com.viettel.ocs.dao.PriceComponentDAO;
import com.viettel.ocs.dao.ReserveInfoDAO;
import com.viettel.ocs.dao.SortPriceComponentDAO;
import com.viettel.ocs.entity.Action;
import com.viettel.ocs.entity.ActionType;
import com.viettel.ocs.entity.BaseEntity;
import com.viettel.ocs.entity.Block;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.DynamicReserve;
import com.viettel.ocs.entity.Offer;
import com.viettel.ocs.entity.OfferActionMap;
import com.viettel.ocs.entity.PriceComponent;
import com.viettel.ocs.entity.RateTable;
import com.viettel.ocs.entity.ReserveInfo;
import com.viettel.ocs.entity.SortPriceComponent;
import com.viettel.ocs.util.ValidateUtil;

/**
 * Action
 * 
 * @author THANHND
 *
 */
@SuppressWarnings("serial")
@ManagedBean(name = "actionBean")
@ViewScoped
public class ActionBean extends BaseController implements Serializable {

	// GENERIC
	private Category category;
	private CategoryDAO categoryDAO;
	// ACTION TABLE
	private List<Action> actions;
	private String formType = "";

	// ACTION DETAIL
	private Action action;
	private ActionDAO actionDAO;
	private PriceComponentDAO priceComponentDAO;
	private List<PriceComponent> priceComponents;
	private PriceComponent priceComponent;

	private List<DynamicReserve> dynamicReserves;
	private List<SortPriceComponent> sortPriceComponents;
	private List<SortPriceComponent> priorityFilters;
	private List<ActionType> actionTypes;
	private List<ReserveInfo> reserveInfos;
	private boolean isEdit;
	// DAO
	private ActionTypeDAO actionTypeDAO;
	private DynamicReserveDAO dynamicReserveDAO;
	private SortPriceComponentDAO sortPriceComponentDAO;
	private ReserveInfoDAO reserveInfoDAO;

	@PostConstruct
	public void init() {
		this.category = new Category();
		this.categoryDAO = new CategoryDAO();
		isEdit = false;
		this.priceComponent = new PriceComponent();
		// LIST ACTION
		this.actions = new ArrayList<Action>();
		this.priceComponents = new ArrayList<PriceComponent>();
		this.dynamicReserves = new ArrayList<DynamicReserve>();
		this.sortPriceComponents = new ArrayList<SortPriceComponent>();
		this.priorityFilters = new ArrayList<SortPriceComponent>();
		this.actionTypes = new ArrayList<ActionType>();
		this.reserveInfos = new ArrayList<ReserveInfo>();

		// ACTION DETAIL
		this.actionDAO = new ActionDAO();

		// DAO
		this.actionTypeDAO = new ActionTypeDAO();
		this.actionDAO = new ActionDAO();
		this.priceComponentDAO = new PriceComponentDAO();
		this.dynamicReserveDAO = new DynamicReserveDAO();
		this.sortPriceComponentDAO = new SortPriceComponentDAO();
		this.reserveInfoDAO = new ReserveInfoDAO();
	}

	// BUSINESS METHOD
	public void refreshAction(Action action) {
		formType = "form-action-detail";
		this.action = action;
		if (this.action.getActionId() != 0L) {
			this.action = actionDAO.get(action.getActionId());
			if (this.action == null) {
				formType = "";
			}
		}
		loadCategoriesOfAction();
		initPriceComponent();
		loadDynamicReserves();
		loadSortPriceComponents();
		loadreserveInfos();
		loadActionTypes();
	}

	private void loadDynamicReserves() {
		dynamicReserves.clear();
		if (action.getDynamicReserveId() != null) {
			DynamicReserve dynamicReserve = dynamicReserveDAO.get(action.getDynamicReserveId());
			if (dynamicReserve != null) {
				dynamicReserves.add(dynamicReserve);
			}
		}
	}

	private void loadSortPriceComponents() {
		sortPriceComponents.clear();
		priorityFilters.clear();
		if (action.getSortPriceComponentId() != null) {
			SortPriceComponent sortPriceComponent = sortPriceComponentDAO.get(action.getSortPriceComponentId());
			if (sortPriceComponent != null) {
				sortPriceComponents.add(sortPriceComponent);
			}
		}
		if (action.getPriorityFilterId() != null) {
			SortPriceComponent sortPriceComponent = sortPriceComponentDAO.get(action.getPriorityFilterId());
			if (sortPriceComponent != null) {
				priorityFilters.add(sortPriceComponent);
			}
		}
	}

	private void loadreserveInfos() {
		reserveInfos.clear();
		if (action.getReserveinfoId() != null) {
			ReserveInfo reserveInfo = reserveInfoDAO.get(action.getReserveinfoId());
			if (reserveInfo != null) {
				reserveInfos.add(reserveInfo);
			}
		}
	}

	private void loadActionTypes() {
		actionTypes.clear();
		if (action.getActionType() != null) {
			ActionType actionType = actionTypeDAO.get(action.getActionType());
			if (actionType != null) {
				actionTypes.add(actionTypeDAO.get(actionType.getActionTypeId()));
			}
		}
	}

	private void clearFilter() {
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.execute("clearFilter('tblActionWidgetVar')");
	}

	public void refreshCategories(Category category) {
		formType = "form-action-list";
		clearFilter();
		this.category = category;
		actions = actionDAO.findActionsByCategoryId(category);
		loadCategoriesOfAction();
	}

	public void editAction(Action action) {
		TreeOfferBean treeOfferBean = super.getTreeOfferBean();
		treeOfferBean.hideCategory();
		treeOfferBean.setContentTitle(super.readProperties("title.action"));
		refreshAction(action);
		isEdit = true;
	}

	public void editContext(NodeSelectEvent nodeSelectEvent) {
		Action action = (Action) nodeSelectEvent.getTreeNode().getData();
		if (this.action == null || action.getActionId() != this.action.getActionId()) {
			super.getTreeOfferBean().onNodeSelect(nodeSelectEvent);
		} else {
			super.getTreeOfferBean().setContentPage("page_action");
			super.getTreeOfferBean().setContentTitle(super.readProperties("title.action"));
		}
		isEdit = true;
	}

	public void commandCheckDependencies(NodeSelectEvent nodeSelectEvent) {
		if (nodeSelectEvent != null) {
			Action item = (Action) nodeSelectEvent.getTreeNode().getData();
			showDependencies(item.getActionId(), Action.class);
		}
	}

	public void addNewAction() {
		TreeOfferBean treeOfferBean = super.getTreeOfferBean();
		treeOfferBean.hideCategory();
		Action action = new Action();
		action.setState(true);
		action.setCategoryId(this.category.getCategoryId());
		treeOfferBean.setContentTitle(super.readProperties("title.action"));
		refreshAction(action);
		isEdit = true;
	}

	public void addNewAction(NodeSelectEvent nodeSelectEvent) {
		super.getTreeOfferBean().onNodeSelect(nodeSelectEvent);
		Action item = (Action) nodeSelectEvent.getTreeNode().getData();
		TreeOfferBean treeOfferBean = super.getTreeOfferBean();
		treeOfferBean.hideCategory();
		Action action = new Action();
		if (item != null) {
			action.setCategoryId(item.getCategoryId());
		}
		treeOfferBean.setContentTitle(super.readProperties("title.action"));
		refreshAction(action);
		isEdit = true;
	}

	public void actionDeploy(NodeSelectEvent nodeSelectEvent) {
		super.getTreeOfferBean().onNodeSelect(nodeSelectEvent);
		if (this.action != null) {
			if (this.action.getState()) {
				this.action.setState(false);
			} else {
				this.action.setState(true);
			}
			saveAction();
		}
	}

	/**
	 * 
	 * @param isUp
	 *            true is up
	 */
	private void moveUpDownInCat(NodeSelectEvent event, boolean isUp) {
		Action action = (Action) event.getTreeNode().getData();
		Object object = actionDAO.upDownObjectInCatWithDomain(action, "index", isUp);
		if (object instanceof Action) {
			Category category = categoryDAO.get(action.getCategoryId());
			Action nextAction = (Action) object;

			TreeOfferBean treeOfferBean = super.getTreeOfferBean();
			if (isUp) {
				treeOfferBean.moveUpTreeNode(action, category);
			} else {
				treeOfferBean.moveDownTreeNode(action, category);
			}
			treeOfferBean.updateTreeNode(nextAction, category, null);
			if (formType == "form-action-list" && category.getCategoryId() == this.category.getCategoryId()) {
				refreshCategories(category);
			}
			super.showNotificationSuccsess();
		}
	}

	// In tree
	// CommandUp ContextMenu
	public void moveUpAction(NodeSelectEvent nodeSelectEvent) {
		if (nodeSelectEvent != null) {
			Action item = (Action) nodeSelectEvent.getTreeNode().getData();
			BaseEntity objParent = (BaseEntity) nodeSelectEvent.getTreeNode().getParent().getData();
			if (objParent instanceof Category) {
				moveUpDownInCat(nodeSelectEvent, true);
			} else if (objParent instanceof Offer) {
				if (actionDAO.processMoveUpDownInOffer(item, (Offer) objParent, true)) {
					Action itemAfterMove = actionDAO.get(item.getActionId());
					TreeOfferBean treeOfferBean = super.getTreeOfferBean();
					treeOfferBean.moveUpTreeNode(itemAfterMove, objParent);
					refreshCategories(getCategory());
					this.showMessageINFO("validate.upObjectSuccess", super.readProperties(""));
				}
			}
		}
	}

	// CommandDown ContextMenu
	public void moveDownAction(NodeSelectEvent nodeSelectEvent) {
		if (nodeSelectEvent != null) {
			Action item = (Action) nodeSelectEvent.getTreeNode().getData();
			BaseEntity objParent = (BaseEntity) nodeSelectEvent.getTreeNode().getParent().getData();
			if (objParent instanceof Category) {
				moveUpDownInCat(nodeSelectEvent, false);
			} else if (objParent instanceof Offer) {
				if (actionDAO.processMoveUpDownInOffer(item, (Offer) objParent, false)) {
					Action itemAfterMove = actionDAO.get(item.getActionId());
					TreeOfferBean treeOfferBean = super.getTreeOfferBean();
					treeOfferBean.moveDownTreeNode(itemAfterMove, objParent);
					refreshCategories(getCategory());
					this.showMessageINFO("validate.downObjectSuccess", super.readProperties(""));
				}
			}
		}
	}

	public void commandHideCloneActionTree(NodeSelectEvent nodeSelectEvent) {
		super.getTreeOfferBean().onNodeSelect(nodeSelectEvent);
		TreeOfferBean treeOfferBean = super.getTreeOfferBean();
		treeOfferBean.hideCategory();
	}

	public void redirectChangeCate() {
		this.openTreeCategoryDialog(TreeType.OFFER_ACTION, "Action", 0);
	}

	public void onDialogReturnCategory(SelectEvent event) {
		Object obj = event.getObject();
		if (obj instanceof Category) {
			Category cate = (Category) obj;
			this.action.setCategoryId(cate.getCategoryId());
			if (actionDAO.moveToCate(this.action)) {
				TreeOfferBean treeOfferBean = super.getTreeOfferBean();
				treeOfferBean.updateTreeNode(this.action, cate, null);
				this.isEdit = false;
				this.showMessageINFO("common.moveCate", " Success ");
			}			
		}
	}

	public void onDialogActionReturn(SelectEvent event) {
		Object object = event.getObject();
		if (object instanceof Action) {
			TreeOfferBean treeOfferBean = super.getTreeOfferBean();
			treeOfferBean.updateTreeNode((Action) object, categoryDAO.get(((Action) object).getCategoryId()), null);
			editAction((Action) object);
			this.isEdit = false;
			super.showNotification(FacesMessage.SEVERITY_INFO, "common.success", "");
		}
	}

	public void commandCloneActionTreeOld(NodeSelectEvent nodeSelectEvent) {
		super.getTreeOfferBean().onNodeSelect(nodeSelectEvent);
		Action action = (Action) nodeSelectEvent.getTreeNode().getData();

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
		lstPara.add("action;" + action.getActionId());
		mapPara.put("param", lstPara);
		RequestContext.getCurrentInstance().openDialog("/pages/offer/context_offer/clone_common.xhtml", options,
				mapPara);
	}

	public void commandCloneActionTree() {
		Action actionClone = this.action;
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
		lstPara.add("action;" + actionClone.getActionId());
		mapPara.put("param", lstPara);
		List<String> posIndex = new ArrayList<>();
		posIndex.add("12");
		mapPara.put("index", posIndex);
		List<String> typeTreeClone = new ArrayList<>();
		typeTreeClone.add("1");
		mapPara.put("treeTypeClone", typeTreeClone);
		RequestContext.getCurrentInstance().openDialog("/pages/offer/context_offer/clone_common.xhtml", options,
				mapPara);
	}

	public void commandCloneActionTree(Action action) {
		Action actionClone = action;
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
		lstPara.add("action;" + actionClone.getActionId());
		mapPara.put("param", lstPara);
		List<String> posIndex = new ArrayList<>();
		posIndex.add("12");
		mapPara.put("index", posIndex);
		List<String> typeTreeClone = new ArrayList<>();
		typeTreeClone.add("1");
		mapPara.put("treeTypeClone", typeTreeClone);
		RequestContext.getCurrentInstance().openDialog("/pages/offer/context_offer/clone_common.xhtml", options,
				mapPara);
	}

	public void removeActionInTree(NodeSelectEvent nodeSelectEvent) {
		super.getTreeOfferBean().onNodeSelect(nodeSelectEvent);
		if (nodeSelectEvent != null) {
			Action item = (Action) nodeSelectEvent.getTreeNode().getData();
			Object object = nodeSelectEvent.getTreeNode().getParent().getData();
			OfferActionMapDAO offerActionMapDAO = new OfferActionMapDAO();
			TreeOfferBean treeOfferBean = super.getTreeOfferBean();
			if (object instanceof Category) {
				// Delete Action and Map Action - PC
				if (!offerActionMapDAO.checkActionInOffer(item.getActionId())) {
					ActionPriceComponentMapDAO actionPriceComponentMapDAO = new ActionPriceComponentMapDAO();
					actionPriceComponentMapDAO.deleteActionPCByAction(item);
					setFormType("");
					treeOfferBean.removeTreeNodeAll(item);
					FacesContext context = FacesContext.getCurrentInstance();
					context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
							this.readProperties("common.delete"), this.readProperties("action")));
				} else {
					this.showMessageWARN("common.summary.warning", super.readProperties("action.used"));
				}
			}
			if (object instanceof Offer) {
				// Delete Map Offer - Action
				Offer offer = (Offer) object;
				OfferActionMap offerActionMap = offerActionMapDAO.findOfferActionMap(offer.getOfferId(),
						item.getActionId());
				if (offerActionMap != null) {
					offerActionMapDAO.delete(offerActionMap);
					treeOfferBean.removeTreeNode(item, offer);
				}
			}
		}
	}

	public void editPriceComponent(PriceComponent priceComponent) {
		super.getTreeOfferBean().setContentTitle(super.readProperties("title.priceComponent"));
		super.getTreeOfferBean().setPriceComponentProperties(false, priceComponent.getCategoryId(), priceComponent,
				true, false);
	}

	public void commandUpOnMap(PriceComponent priceComponent) {
		int indexPC = priceComponents.indexOf(priceComponent);
		if (indexPC > 0) {
			PriceComponent itemBefore = priceComponents.get(indexPC - 1);
			priceComponents.set(indexPC - 1, priceComponent);
			priceComponents.set(indexPC, itemBefore);
		}
	}

	public void commandDownOnMap(PriceComponent priceComponent) {
		int indexPC = priceComponents.indexOf(priceComponent);
		if (indexPC < priceComponents.size() && indexPC != (priceComponents.size() - 1)) {
			PriceComponent itemBefore = priceComponents.get(indexPC + 1);
			priceComponents.set(indexPC + 1, priceComponent);
			priceComponents.set(indexPC, itemBefore);
		}
	}

	public void saveAction() {
		if (validateAction()) {
			if (actionDAO.saveActionDetailt(action, priceComponents)) {
				TreeOfferBean treeOfferBean = super.getTreeOfferBean();
				Category cat = categoryDAO.get(action.getCategoryId());
				List<BaseEntity> lstChildren = new ArrayList<>();
				lstChildren.addAll(priceComponents);
				if (dynamicReserves != null && !dynamicReserves.isEmpty()) {
					lstChildren.add(dynamicReserves.get(0));
				}
				if (sortPriceComponents != null && !sortPriceComponents.isEmpty()) {
					lstChildren.add(sortPriceComponents.get(0));
				}
				if (priorityFilters != null && !priorityFilters.isEmpty()) {
					lstChildren.add(priorityFilters.get(0));
				}

				treeOfferBean.updateTreeNode(action, cat, lstChildren);
				this.isEdit = false;
				this.showMessageINFO("validate.saveSuccess", super.readProperties("action"));
			} else {
				this.showMessageWARN("common.save", " action ");
			}
		}
	}

	private boolean insert = false;
	private List<Category> categoriesOfAction;

	public void commandAddNewPC() {
		insert = false;
		PriceComponent itemNew = new PriceComponent();
		choosePC(itemNew);
		// priceComponents.add(itemNew);
	}

	public void choosePC(PriceComponent item) {
		
		List<Long> lstId = new ArrayList<Long>();
		if(priceComponents != null) {
			for (PriceComponent pc : priceComponents) {
				lstId.add(pc.getPriceComponentId());
			}	
		}
		
		if (item == null || item.getPriceComponentId() <= 0) {			
			super.openTreeOfferDialog(TreeType.OFFER_PRICE_COMPONENT, readProperties("action.price_component"), 0, true,  lstId);
		} else {
			super.openTreeOfferDialog(TreeType.OFFER_PRICE_COMPONENT, readProperties("action.price_component"), 0, false , lstId);
		}
		priceComponent = item;
		insert = false;
	}

	public void addPcAt(PriceComponent item) {
		
		List<Long> lstId = new ArrayList<Long>();
		if(priceComponents != null) {
			for (PriceComponent pc : priceComponents) {
				lstId.add(pc.getPriceComponentId());
			}	
		}
		
		if (item == null || item.getPriceComponentId() <= 0){			
			super.openTreeOfferDialog(TreeType.OFFER_PRICE_COMPONENT, readProperties("action.price_component"), 0, true, lstId);
		} else {
			super.openTreeOfferDialog(TreeType.OFFER_PRICE_COMPONENT, readProperties("action.price_component"), 0, false, lstId);
		}
		priceComponent = item;
		insert = true;
	}

	public void onDialogPCReturn(SelectEvent event) {
		Object[] objArr = new Object[1];
		if (event.getObject() instanceof Object[]) {
			objArr = (Object[]) event.getObject();
		} else {
			objArr[0] = event.getObject();
		}

		for (Object obj : objArr) {
			if (obj instanceof PriceComponent) {
				PriceComponent priceComponentChange = (PriceComponent) obj;
				if (!exitsPCs(priceComponentChange)) {
					if (priceComponent.getPriceComponentId() != 0L) {
						if (insert) {
							priceComponents.add(priceComponents.indexOf(priceComponent) + 1, priceComponentChange);
						} else {
							priceComponents.set(priceComponents.indexOf(priceComponent), priceComponentChange);
						}
					} else {
						priceComponents.add(priceComponentChange);
					}
				} else {
					if (priceComponent.getPriceComponentId() == priceComponentChange.getPriceComponentId() && !insert) {
						priceComponents.set(priceComponents.indexOf(priceComponent), priceComponentChange);
					} else {
						this.showMessageWARN("common.summary.warning", super.readProperties("common.objAlreadyExists"));
					}
				}
			}
		}
	}

	public void chooseActionType() {
		super.openTreeOfferDialog(TreeType.OFFER_ACTION_TYPE, readProperties("action.action_type"), 0, false, null);
	}

	public void notChooseActionType() {
		action.setActionType(null);
		actionTypes.clear();
		RequestContext.getCurrentInstance().update("form-action-detail:slActionType");
	}

	public void onDialogActionTypeReturn(SelectEvent event) {
		Object object = event.getObject();
		if (object instanceof ActionType) {
			ActionType actionType = (ActionType) object;
			action.setActionType(actionType.getActionTypeId());
			actionTypes.clear();
			this.actionTypes.add(actionType);
			RequestContext.getCurrentInstance().update("form-action-detail:slActionType");
		}
	}

	public void chooseSortPC() {
		super.openTreeOfferDialog(TreeType.OFFER_SORT_PRICE_COMPONENT, readProperties("action.sort_price_component"), 0,
				false, null);
	}

	public void notChooseSortPC() {
		action.setSortPriceComponentId(null);
		sortPriceComponents.clear();
		RequestContext.getCurrentInstance().update("form-action-detail:slSortPriceComponent");
	}

	public void onDialogSortPCReturn(SelectEvent event) {
		Object object = event.getObject();
		if (object instanceof SortPriceComponent) {
			SortPriceComponent sortPriceComponent = (SortPriceComponent) object;
			action.setSortPriceComponentId(sortPriceComponent.getSortPriceComponentId());
			sortPriceComponents.clear();
			this.sortPriceComponents.add(sortPriceComponent);
			RequestContext.getCurrentInstance().update("form-action-detail:slSortPriceComponent");
		}
	}
	
	public void choosePriorityFilter() {
		super.openTreeOfferDialog(TreeType.OFFER_SORT_PRICE_COMPONENT, readProperties("action.sort_price_component"), 0,
				false, null);
	}
	
	public void notChoosePriorityFilter() {
		action.setPriorityFilterId(null);
		priorityFilters.clear();
		RequestContext.getCurrentInstance().update("form-action-detail:slPriorityFilter");
	}
	
	public void onDialogPriorityFilterReturn(SelectEvent event) {
		Object object = event.getObject();
		if (object instanceof SortPriceComponent) {
			SortPriceComponent sortPriceComponent = (SortPriceComponent) object;
			action.setPriorityFilterId(sortPriceComponent.getSortPriceComponentId());
			priorityFilters.clear();
			this.priorityFilters.add(sortPriceComponent);
			RequestContext.getCurrentInstance().update("form-action-detail:slPriorityFilter");
		}
	}

	public void chooseDynamicReseve() {
		super.openTreeOfferDialog(TreeType.OFFER_DYNAMIC_RESERVE, readProperties("action.dynamicReserve"), 0, false, null);
	}

	public void notChooseDynamicReseve() {
		action.setDynamicReserveId(null);
		dynamicReserves.clear();
		RequestContext.getCurrentInstance().update("form-action-detail:slDynamicReserve");
	}

	public void onDialogDynamicReseveReturn(SelectEvent event) {
		Object object = event.getObject();
		if (object instanceof DynamicReserve) {
			DynamicReserve dynamicReserve = (DynamicReserve) object;
			dynamicReserves.clear();
			this.dynamicReserves.add(dynamicReserve);
			action.setDynamicReserveId(dynamicReserve.getDynamicReserveId());
			RequestContext.getCurrentInstance().update("form-action-detail:slDynamicReserve");
		}
	}

	public void chooseReseveInfo() {
		super.openTreeCommonDialog(TreeType.CATALOG_RESERVE_INFO, readProperties("action.reserve_info"), 0, false, null);
	}

	public void notChooseReseveInfo() {
		reserveInfos.clear();
		action.setReserveinfoId(null);
		RequestContext.getCurrentInstance().update("form-action-detail:slReserveInfoId");
	}

	public void onDialogReseveInfoReturn(SelectEvent event) {
		Object object = event.getObject();
		if (object instanceof ReserveInfo) {
			ReserveInfo reserveInfo = (ReserveInfo) object;
			reserveInfos.clear();
			this.reserveInfos.add(reserveInfo);
			action.setReserveinfoId(reserveInfo.getReserveInfoId());
			RequestContext.getCurrentInstance().update("form-action-detail:slReserveInfoId");
		}
	}

	public void loadCategoriesOfAction() {
		categoriesOfAction = categoryDAO.findByTypeForSelectbox(CategoryType.OFF_ACTION);
	}

	private boolean exitsPCs(PriceComponent priceComponentInput) {
		for (PriceComponent priceComponent : priceComponents) {
			if (priceComponent.getPriceComponentId() == priceComponentInput.getPriceComponentId()) {
				return true;
			}
		}
		return false;
	}

	public void removePriceComponet(PriceComponent priceComponent) {
		priceComponents.remove(priceComponent);
	}

	public void removeAction(Action action) {
		OfferActionMapDAO offerActionMapDAO = new OfferActionMapDAO();
		TreeOfferBean treeOfferBean = super.getTreeOfferBean();
		// Delete Action and Map Action - PC
		if (!offerActionMapDAO.checkActionInOffer(action.getActionId())) {
			ActionPriceComponentMapDAO actionPriceComponentMapDAO = new ActionPriceComponentMapDAO();
			actionPriceComponentMapDAO.deleteActionPCByAction(action);
			actions.remove(action);
			refreshCategories(category);
			setFormType("category");
			treeOfferBean.removeTreeNodeAll(action);
			this.showMessageINFO("validate.deleteSuccess", super.readProperties("action"));
		} else {
			this.showMessageWARN("common.summary.warning", super.readProperties("action.used"));
		}
	}

	private void initPriceComponent() {
		if (action.getActionId() != 0L) {
			priceComponents = priceComponentDAO.findPCByAction(action.getActionId());
		} else {
			priceComponents.clear();
		}
	}

	// Validate
	private boolean validateAction() {

		if (action.getActionType() == null) {
			showNotification(FacesMessage.SEVERITY_WARN, this.readProperties("normalizer.validateError"),
					this.readProperties("normalizer.errorActiontypeValueName"));
			return false;
		}
		if (ValidateUtil.checkStringNullOrEmpty(action.getActionName())) {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_WARN, this.readProperties("normalizer.validateError"),
							this.readProperties("normalizer.errorDataValueName")));
			return false;
		}

		if (actionDAO.checkName(action, (action.getActionId() > 0 ? true : false))) {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
					this.readProperties("normalizer.validateError"), this.readProperties("common.nameAlreadyExists")));
			return false;
		}
		return true;
	}

	// GET, SET
	public List<Action> getActions() {
		return actions;
	}

	public void setActions(List<Action> actions) {
		this.actions = actions;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public String getFormType() {
		return formType;
	}

	public void setFormType(String formType) {
		this.formType = formType;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public List<PriceComponent> getPriceComponents() {
		return priceComponents;
	}

	public void setPriceComponents(List<PriceComponent> priceComponents) {
		this.priceComponents = priceComponents;
	}

	public List<DynamicReserve> getDynamicReserves() {
		return dynamicReserves;
	}

	public void setDynamicReserves(List<DynamicReserve> dynamicReserves) {
		this.dynamicReserves = dynamicReserves;
	}

	public List<SortPriceComponent> getSortPriceComponents() {
		return sortPriceComponents;
	}

	public void setSortPriceComponents(List<SortPriceComponent> sortPriceComponents) {
		this.sortPriceComponents = sortPriceComponents;
	}

	public List<ReserveInfo> getReserveInfos() {
		return reserveInfos;
	}

	public void setReserveInfos(List<ReserveInfo> reserveInfos) {
		this.reserveInfos = reserveInfos;
	}

	public void setActionTypes(List<ActionType> actionTypes) {
		this.actionTypes = actionTypes;
	}

	public List<ActionType> getActionTypes() {
		return actionTypes;
	}

	public boolean isEdit() {
		return isEdit;
	}

	public void setEdit(boolean isEdit) {
		this.isEdit = isEdit;
	}

	public PriceComponent getPriceComponent() {
		return priceComponent;
	}

	public void setPriceComponent(PriceComponent priceComponent) {
		this.priceComponent = priceComponent;
	}

	public void setCategoriesOfAction(List<Category> categoriesOfAction) {
		this.categoriesOfAction = categoriesOfAction;
	}

	public List<Category> getCategoriesOfAction() {
		return categoriesOfAction;
	}

	public List<SortPriceComponent> getPriorityFilters() {
		return priorityFilters;
	}

	public void setPriorityFilters(List<SortPriceComponent> priorityFilters) {
		this.priorityFilters = priorityFilters;
	}
	
}

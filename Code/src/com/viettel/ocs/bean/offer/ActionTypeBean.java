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
import com.viettel.ocs.constant.IconClass;
import com.viettel.ocs.constant.TreeType;
import com.viettel.ocs.dao.ActionDAO;
import com.viettel.ocs.dao.ActionTypeDAO;
import com.viettel.ocs.dao.CategoryDAO;
import com.viettel.ocs.dao.EventActionTypeMapDAO;
import com.viettel.ocs.dao.EventDAO;
import com.viettel.ocs.entity.ActionType;
import com.viettel.ocs.entity.BaseEntity;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.Event;
import com.viettel.ocs.entity.EventActionTypeMap;
import com.viettel.ocs.entity.RateTable;
import com.viettel.ocs.util.ValidateUtil;

/**
 * Action Type
 * 
 * @author THANHND
 *
 */
@SuppressWarnings("serial")
@ManagedBean(name = "actionTypeBean")
@ViewScoped
public class ActionTypeBean extends BaseController implements Serializable {

	private ActionType actionType;
	private List<Event> events;
	private String formType;
	private Event eventMap;

	private Category category;
	private CategoryDAO categoryDAO;
	private List<ActionType> actionTypes;
	private ActionTypeDAO actionTypeDAO;
	private EventDAO eventDAO;
	private ActionDAO actionDAO;
	private List<Category> categoriesOfAT;

	private boolean isEditting;
	private boolean isCheckId;

	@PostConstruct
	public void init() {
		this.actionType = new ActionType();
		this.events = new ArrayList<Event>();
		this.category = new Category();
		this.categoryDAO = new CategoryDAO();
		this.eventMap = new Event();
		this.categoriesOfAT = new ArrayList<Category>();

		// LIST ACTION
		this.actionTypes = new ArrayList<ActionType>();

		// DAO
		this.actionTypeDAO = new ActionTypeDAO();
		this.eventDAO = new EventDAO();
		this.actionDAO = new ActionDAO();
		this.isEditting = false;
		this.isCheckId = true;
	}

	public void refreshCategories(Category category) {
		formType = "form-action-type-list";
		this.category = category;
		this.actionTypes = actionTypeDAO.findByCategory(category);

		// For icon class
		for (ActionType actionType : actionTypes) {

			IconClass.mapActionTypeIcon.put(actionType.getActionTypeId(), actionType.getIcon());
		}
		loadCategoriesOfActionType();
	}

	public void refreshActionType(ActionType actionType, boolean editting) {
		formType = "form-action-type-detail";
		try {
			this.actionType = actionType.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			getLogger().warn(e.getMessage(), e);
		}
		if (actionType.getActionTypeId() != null) {
			events = eventDAO.findEventsByActionType(actionType);
		} else {
			events.clear();
		}
		setEditting(editting);
		loadCategoriesOfActionType();
	}

	public void chooseIcon(Integer iconNumber) {
		actionType.setIcon("action-icon-" + iconNumber);

	}

	public void chooseIconClass(String icon) {
		actionType.setIcon(icon);

	}

	public void addNewActionType() {
		TreeOfferBean treeOfferBean = super.getTreeOfferBean();
		treeOfferBean.hideCategory();
		ActionType actionType = new ActionType();
		actionType.setCategoryId(category.getCategoryId());
		actionType.setIcon("");
//		treeOfferBean.setActionTypeProperties(false, actionType, true);
		treeOfferBean.setContentTitle(super.readProperties("title.actiontype"));
		refreshActionType(actionType, true);
		this.isCheckId = false;
	}

	public void removeActionType(ActionType actionType) {
		EventActionTypeMapDAO eventATMapDAO = new EventActionTypeMapDAO();
		TreeOfferBean treeOfferBean = super.getTreeOfferBean();
		// Delete ActionType and Map ActionType - Event
		if (!eventATMapDAO.checkActionTypeInEvent(actionType.getActionTypeId())
				&& !actionDAO.checkActionTypeInAction(actionType.getActionTypeId())) {
			eventATMapDAO.deleteActionTypeEventByActionTypeId(actionType);
			category.setCategoryId(actionType.getCategoryId());
			refreshCategories(category);
			super.getTreeOfferBean().removeTreeNodeAll(actionType);
			super.showMessageINFO(this.readProperties("common.delete"), this.readProperties("title.actiontype"));
		} else {
			this.showMessageWARN("common.summary.warning", super.readProperties("validate.fieldUseIn"));
		}
		//
		// ActionDAO actionDAO = new ActionDAO();
		// if (!actionDAO.checkActionTypeInAction(actionType.getActionTypeId()))
		// {
		// actionTypeDAO.deleteActionTypeByAction(actionType);
		// category.setCategoryId(actionType.getCategoryId());
		// refreshCategories(category);
		// treeOfferBean.removeTreeNodeAll(actionType);
		// FacesContext context = FacesContext.getCurrentInstance();
		// context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
		// this.readProperties("common.delete"),
		// this.readProperties("title.actiontype")));
		// } else {
		// this.showMessageWARN("common.summary.warning",
		// super.readProperties("actiontype.objectUserAction"));
		// }
	}

	private boolean insert = false;

	public void addEventAt(Event item) {
		
		List<Long> lstId = new ArrayList<Long>();
		if(events != null) {
			for (Event e : events) {
				lstId.add(e.getEventId());
			}	
		}
		
		if (item == null || item.getEventId() <= 0){			
			super.openTreeOfferDialog(TreeType.OFFER_EVENT_EVENT, readProperties("title.event"), 0, true, lstId);
		} else {
			super.openTreeOfferDialog(TreeType.OFFER_EVENT_EVENT, readProperties("title.event"), 0, false, lstId);
		}
		eventMap = item;
		insert = true;
	}

	public void editEvent(Event event) {
		TreeOfferBean offerBean = getTreeOfferBean();
		offerBean.setContentTitle(super.readProperties("title.event"));
		offerBean.setEventProperties(false, event, true, false);
	}

	public void removeEvent(Event event) {
		events.remove(event);
	}

	public void chooseEvent(Event item) {
		
		List<Long> lstId = new ArrayList<Long>();
		if(events != null) {
			for (Event e : events) {
				lstId.add(e.getEventId());
			}	
		}
		
		if (item == null || item.getEventId() == null){			
			super.openTreeOfferDialog(TreeType.OFFER_EVENT_EVENT, readProperties("actiontype.event"), 0, true, lstId);
		} else {
			super.openTreeOfferDialog(TreeType.OFFER_EVENT_EVENT, readProperties("actiontype.event"), 0, false, lstId);
		}
		eventMap = item;
		insert = false;
	}

	public void commandAddNewEvent() {
		insert = false;
		Event itemNew = new Event();
		chooseEvent(itemNew);
	}

	// public void onDialogEventReturn(SelectEvent selectEvent) {
	// Object object = selectEvent.getObject();
	// if (object instanceof Event) {
	// Event event = (Event) object;
	// if (!exitsEvents(event)) {
	// events.add(event);
	// }
	// }
	// }

	public void onDialogEventReturn(SelectEvent event) {
		Object[] objArr = new Object[1];
		if (event.getObject() instanceof Object[]) {
			objArr = (Object[]) event.getObject();
		} else {
			objArr[0] = event.getObject();
		}

		for (Object obj : objArr) {
			if (obj instanceof Event) {
				Event eventChange = (Event) obj;
				if (!exitsEvents(eventChange)) {
					if (eventMap.getEventId() != null) {
						if (insert) {
							events.add(events.indexOf(eventMap) + 1, eventChange);
						} else {
							events.set(events.indexOf(eventMap), eventChange);
						}

					} else {
						events.add(eventChange);
					}
				} else {
					if (eventMap.getEventId() == eventChange.getEventId() && !insert) {
						events.set(events.indexOf(eventMap), eventChange);
					} else {
						this.showMessageWARN("common.summary.warning", super.readProperties("common.objAlreadyExists"));
					}
				}
			}
		}
	}

	private boolean exitsEvents(Event event) {
		for (Event item : events) {
			if (item.getEventId() == event.getEventId()) {
				return true;
			}
		}
		return false;
	}

	public void saveActionType() {
		if (validateActionType()) {
			if (actionTypeDAO.saveActionTypeAndMapping(actionType, events)) {
				TreeOfferBean treeOfferBean = super.getTreeOfferBean();
				Category cat = categoryDAO.get(actionType.getCategoryId());
				treeOfferBean.updateTreeNode(actionType, cat, null);
				isEditting = false;
				this.isCheckId = true;
				this.showMessageINFO("common.save", " action type ");

				IconClass.mapActionTypeIcon.put(actionType.getActionTypeId(), actionType.getIcon());
			} else {
				this.showMessageWARN("common.save", " action type ");
			}
		}

	}

	private boolean validateActionType() {
		boolean result = true;

		if (actionType.getActionTypeId() == null) {
			this.showMessageWARN("actiontype.actiontype_id", super.readProperties("validate.fieldNull"));
			result = false;
		}

		if (actionTypeDAO.checkId(actionType, isCheckId)) {
			this.showMessageWARN("actiontype.actiontype_id", super.readProperties("validate.checkObjectExist"));
			result = false;
		}

		if (ValidateUtil.checkStringNullOrEmpty(actionType.getActionTypeName())) {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_WARN, this.readProperties("normalizer.validateError"),
							this.readProperties("normalizer.errorDataValueName")));
			result = false;
		}
		if (actionTypeDAO.checkName(actionType)) {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
					this.readProperties("normalizer.validateError"), this.readProperties("common.nameAlreadyExists")));
			result = false;
		}
		if (ValidateUtil.checkStringNullOrEmpty(actionType.getIcon())) {
			this.showMessageWARN("actiontype.iconFeild", super.readProperties("validate.fieldNull"));
			result = false;
		}

		return result;
	}

	public void editActionType(ActionType actionType) {
		TreeOfferBean treeOfferBean = super.getTreeOfferBean();
		treeOfferBean.hideCategory();
		refreshActionType(actionType, true);
		setEditting(true);
		this.isCheckId = true;
		treeOfferBean.setContentTitle(super.readProperties("title.actiontype"));
	}

	// Move to Category

	public void redirectChangeCate() {
		this.openTreeCategoryDialog(TreeType.OFFER_ACTION_TYPE, "Action Type", 0);
	}

	public void onDialogReturnCategory(SelectEvent event) {
		Object obj = event.getObject();
		if (obj instanceof Category) {
			Category cate = (Category) obj;
			this.actionType.setCategoryId(cate.getCategoryId());
			if (actionTypeDAO.moveToCate(this.actionType)) {
				TreeOfferBean treeOfferBean = super.getTreeOfferBean();
				treeOfferBean.updateTreeNode(this.actionType, cate, null);
				this.showMessageINFO("common.moveCate", " Success ");
				this.isEditting = false;
			}
		}
	}

	// ContextMenu

	public void cloneActionTypeContext(NodeSelectEvent nodeSelectEvent) {
		ActionType actionType = (ActionType) nodeSelectEvent.getTreeNode().getData();
		showDialogClone(actionType);
	}

	private void showDialogClone(ActionType actionType) {
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
		lstPara.add("actionType;" + actionType.getActionTypeId());
		mapPara.put("param", lstPara);
		List<String> posIndex = new ArrayList<>();
		posIndex.add("8");
		mapPara.put("index", posIndex);
		List<String> typeTreeClone = new ArrayList<>();
		typeTreeClone.add("1");
		mapPara.put("treeTypeClone", typeTreeClone);
		RequestContext.getCurrentInstance().openDialog("/pages/offer/context_offer/clone_common.xhtml", options,
				mapPara);
	}

	public void onDialogContextActionTypeReturn(SelectEvent event) {
		Object object = event.getObject();
		if (object instanceof ActionType) {
			ActionType actionType = (ActionType) event.getObject();
			Category category = categoryDAO.get(actionType.getCategoryId());
			List<Event> lstEvent = eventDAO.findEventsByActionType(actionType);
			super.getTreeOfferBean().updateTreeNode(actionType, category, lstEvent);
			if (category.getCategoryId() == this.category.getCategoryId()) {
				actionTypes.add(actionType);
			}
			if (formType.equals("form-action-type-list")) {
				// clearFilter();
			}
			showNotification(FacesMessage.SEVERITY_INFO, "common.success", "");
		}
	}

	public void commandAddNewContextMenu(NodeSelectEvent nodeSelectEvent) {
		Object object = nodeSelectEvent.getTreeNode().getData();
		if (object instanceof ActionType) {
			ActionType at = (ActionType) object;
			TreeOfferBean treeOfferBean = super.getTreeOfferBean();
			treeOfferBean.hideCategory();
			ActionType actionType = new ActionType();
			actionType.setCategoryId(at.getCategoryId());
			actionType.setIcon("");
//			treeOfferBean.setActionTypeProperties(false, actionType, true);
			treeOfferBean.setContentTitle(super.readProperties("title.actiontype"));
			refreshActionType(actionType, true);
			this.isCheckId = false;
		}
	}

	public void commandEditContextMenu(NodeSelectEvent nodeSelectEvent) {
		super.getTreeOfferBean().onNodeSelect(nodeSelectEvent);
		TreeOfferBean treeOfferBean = super.getTreeOfferBean();
		treeOfferBean.hideCategory();
		refreshActionType(actionType, true);
	}

	public void commandRemoveContextMenu(NodeSelectEvent nodeSelectEvent) {
		if (nodeSelectEvent != null) {
			ActionType item = (ActionType) nodeSelectEvent.getTreeNode().getData();
			Object object = nodeSelectEvent.getTreeNode().getParent().getData();
			EventActionTypeMapDAO eventATMapDAO = new EventActionTypeMapDAO();
			if (object instanceof Category) {
				if (!eventATMapDAO.checkActionTypeInEvent(item.getActionTypeId())
						&& !actionDAO.checkActionTypeInAction(actionType.getActionTypeId())) {
					eventATMapDAO.deleteActionTypeEventByActionTypeId(item);
					Category category = categoryDAO.get(item.getCategoryId());
					category.setCategoryId(category.getCategoryId());
					if (actionTypes != null) {
						for (int i = 0; i < actionTypes.size(); i++) {
							if (item.getActionTypeId() == actionTypes.get(i).getActionTypeId()) {
								actionTypes.remove(i);
								break;
							}
						}
					}
					super.getTreeOfferBean().removeTreeNode(item, category);
					this.showMessageINFO(this.readProperties("common.delete"), this.readProperties("title.actiontype"));
				} else {
					this.showMessageWARN("common.summary.warning", super.readProperties("validate.fieldUseIn"));
				}
			}
			if (object instanceof Event) {
				Event event = (Event) object;
				EventActionTypeMap eventActionTypeMap = eventATMapDAO.findEventAT(event.getEventId(),
						item.getActionTypeId());
				if (eventActionTypeMap != null) {
					eventATMapDAO.delete(eventActionTypeMap);
					super.getTreeOfferBean().removeTreeNode(item, event);
					super.showMessageINFO(this.readProperties("common.delete"),
							this.readProperties("title.actiontype"));
				} else {
					this.showMessageWARN("common.summary.warning", super.readProperties("actiontype.objectUser"));
				}
			}
		}

	}

	// CommandCheckDependencies ContextMenu
	public void commandCheckDependencies(NodeSelectEvent nodeSelectEvent) {
		if (nodeSelectEvent != null) {
			ActionType item = (ActionType) nodeSelectEvent.getTreeNode().getData();
			showDependencies(item.getActionTypeId(), ActionType.class);
		}
	}

	// load list Action Type by Category
	public void loadActionTypeByCategory(Long categoryId) {
		actionTypes = new ArrayList<ActionType>();
		actionTypes = actionTypeDAO.findActionTypeByConditions(categoryId);
	}

	public void moveUpActionTypeContext(NodeSelectEvent nodeSelectEvent) {
		if (nodeSelectEvent != null) {
			ActionType item = (ActionType) nodeSelectEvent.getTreeNode().getData();
			BaseEntity objParent = (BaseEntity) nodeSelectEvent.getTreeNode().getParent().getData();
			if (objParent instanceof Category) {
				item = actionTypeDAO.get(item.getActionTypeId());
				List<Long> lstId = new ArrayList<>();
				lstId.add(item.getCategoryId());
				List<ActionType> listActionType = actionTypeDAO.findByListCategory(lstId);
				for (int i = 0; i < listActionType.size(); i++) {
					if (item.getActionTypeId() == listActionType.get(i).getActionTypeId() && i != 0) {
						ActionType upItem = listActionType.get(i - 1);
						if (actionTypeDAO.processMoveUpDown(upItem, item)) {
							TreeOfferBean treeOfferBean = super.getTreeOfferBean();
							treeOfferBean.moveUpTreeNode(item, objParent);
							loadActionTypeByCategory(item.getCategoryId());
							this.showMessageINFO("validate.upObjectSuccess", super.readProperties(""));
						}
						break;
					}
				}

				if (listActionType.size() == 1) {
					this.showMessageWARN("common.summary.warning", super.readProperties("validate.notMove"));
				}
			}
			if (objParent instanceof Event) {
				Event eventItem = (Event) objParent;
				EventActionTypeMapDAO mapDAO = new EventActionTypeMapDAO();
				EventActionTypeMap itemMap = mapDAO.findEventAT(eventItem.getEventId(), item.getActionTypeId());

				if (mapDAO.processMoveUpDown(itemMap, true)) {
					// EventActionTypeMap itemAfterMove =
					// mapDAO.get(itemMap.getEventActionTypeMapId());
					TreeOfferBean treeOfferBean = super.getTreeOfferBean();
					treeOfferBean.moveUpTreeNode(item, objParent);
					treeOfferBean.setEventProperties(false, eventItem, false, false);
					this.showMessageINFO("validate.upObjectSuccess", super.readProperties(""));
				} else {
					this.showMessageWARN("common.summary.warning", super.readProperties("validate.notMove"));
				}
			}
		}
	}

	public void moveDownActionTypeContext(NodeSelectEvent nodeSelectEvent) {
		if (nodeSelectEvent != null) {
			ActionType item = (ActionType) nodeSelectEvent.getTreeNode().getData();
			item = actionTypeDAO.get(item.getActionTypeId());
			BaseEntity objParent = (BaseEntity) nodeSelectEvent.getTreeNode().getParent().getData();
			if (objParent instanceof Category) {
				List<Long> lstId = new ArrayList<>();
				lstId.add(item.getCategoryId());
				List<ActionType> listActionType = actionTypeDAO.findByListCategory(lstId);
				for (int i = 0; i < listActionType.size(); i++) {
					if (item.getActionTypeId() == listActionType.get(i).getActionTypeId()
							&& i != (listActionType.size() - 1)) {
						ActionType upItem = listActionType.get(i + 1);
						if (actionTypeDAO.processMoveUpDown(upItem, item)) {
							TreeOfferBean treeOfferBean = super.getTreeOfferBean();
							treeOfferBean.moveDownTreeNode(item, objParent);
							loadActionTypeByCategory(item.getCategoryId());
							this.showMessageINFO("validate.downObjectSuccess", super.readProperties(""));
						}
						break;
					}
				}
				if (listActionType.size() == 1) {
					this.showMessageWARN("common.summary.warning", super.readProperties("validate.notMove"));
				}
			}
			if (objParent instanceof Event) {
				Event eventItem = (Event) objParent;
				EventActionTypeMapDAO mapDAO = new EventActionTypeMapDAO();
				EventActionTypeMap itemMap = mapDAO.findEventAT(eventItem.getEventId(), item.getActionTypeId());

				if (mapDAO.processMoveUpDown(itemMap, false)) {
					// EventActionTypeMap itemAfterMove = mapDAO.get(itemMap.getEventActionTypeMapId());
					TreeOfferBean treeOfferBean = super.getTreeOfferBean();
					treeOfferBean.moveDownTreeNode(item, objParent);
					treeOfferBean.setEventProperties(false, eventItem, false, false);
					this.showMessageINFO("validate.downObjectSuccess", super.readProperties(""));
				} else {
					this.showMessageWARN("common.summary.warning", super.readProperties("validate.notMove"));
				}
			}
		}
	}

	// GET SET

	public void loadCategoriesOfActionType() {
		categoriesOfAT = categoryDAO.findByTypeForSelectboxWithoutDomain(CategoryType.OFF_EVENT_ACTION_TYPE);
	}

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

	public List<ActionType> getActionTypes() {
		return actionTypes;
	}

	public void setActionTypes(List<ActionType> actionTypes) {
		this.actionTypes = actionTypes;
	}

	public ActionType getActionType() {
		return actionType;
	}

	public void setActionType(ActionType actionType) {
		this.actionType = actionType;
	}

	public List<Event> getEvents() {
		return events;
	}

	public void setEvents(List<Event> events) {
		this.events = events;
	}

	public boolean isEditting() {
		return isEditting;
	}

	public void setEditting(boolean isEditting) {
		this.isEditting = isEditting;
	}

	public Event getEventMap() {
		return eventMap;
	}

	public void setEventMap(Event eventMap) {
		this.eventMap = eventMap;
	}

	public boolean isCheckId() {
		return isCheckId;
	}

	public void setCheckId(boolean isCheckId) {
		this.isCheckId = isCheckId;
	}

	public List<Category> getCategoriesOfAT() {
		return categoriesOfAT;
	}

	public void setCategoriesOfAT(List<Category> categoriesOfAT) {
		this.categoriesOfAT = categoriesOfAT;
	}

}

package com.viettel.ocs.bean.offer;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;

import com.viettel.ocs.bean.BaseController;
import com.viettel.ocs.bean.TreeOfferBean;
import com.viettel.ocs.constant.CategoryType;
import com.viettel.ocs.constant.TreeType;
import com.viettel.ocs.dao.ActionTypeDAO;
import com.viettel.ocs.dao.CategoryDAO;
import com.viettel.ocs.dao.EventActionTypeMapDAO;
import com.viettel.ocs.dao.EventDAO;
import com.viettel.ocs.entity.ActionType;
import com.viettel.ocs.entity.BaseEntity;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.Event;
import com.viettel.ocs.entity.Offer;
import com.viettel.ocs.entity.RateTable;
import com.viettel.ocs.entity.TriggerOcs;
import com.viettel.ocs.util.ValidateUtil;

/**
 * Events
 * 
 * @author THANHND
 *
 */
@ManagedBean(name = "eventBean")
@ViewScoped
public class EventBean extends BaseController {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String formType;
	private Event event;
	private Category category;
	private ActionType actionType;

	private List<Event> events;
	private List<ActionType> actionTypes;

	private EventDAO eventDAO;
	private ActionTypeDAO actionTypeDAO;
	private CategoryDAO categoryDAO;

	private boolean isEditting;
	private boolean isCheckId;

	@PostConstruct
	public void init() {
		this.event = new Event();
		this.events = new ArrayList<Event>();
		this.actionTypes = new ArrayList<ActionType>();
		// DAO
		this.eventDAO = new EventDAO();
		this.actionTypeDAO = new ActionTypeDAO();
		this.categoryDAO = new CategoryDAO();

		this.isEditting = true;
		this.actionType = new ActionType();
		this.isCheckId = true;
	}

	public void refreshEvent(Event event, boolean editting) {
		this.isEditting = editting;
		this.formType = "form-event-detail";
		try {
			this.event = event.clone();
		} catch (CloneNotSupportedException e) {
			getLogger().warn(e.getMessage(), e);
		}
		if (event.getEventId() != null) {
			this.actionTypes = actionTypeDAO.findActionTypesByEvent(event);
		} else {
			this.actionTypes.clear();
		}

		loadCategoriesOfEvent();
	}

	public void refreshCategories(Category category) {
		this.formType = "form-event-list";
		this.category = category;
		this.events = eventDAO.findByCategory(category);
		loadCategoriesOfEvent();
	}

	public void addNewEvent() {
		TreeOfferBean treeOfferBean = super.getTreeOfferBean();
		treeOfferBean.hideCategory();
		Event event = new Event();
		event.setCategoryId(category.getCategoryId());
		event.setIsRequireRatingConfig(true);
		refreshEvent(event, true);
		this.isCheckId = false;
		treeOfferBean.setContentTitle(super.readProperties("title.event"));
	}

	public void editEvent(Event event) {
		TreeOfferBean treeOfferBean = super.getTreeOfferBean();
		treeOfferBean.hideCategory();
		refreshEvent(event, true);
		this.isCheckId = true;
		treeOfferBean.setContentTitle(super.readProperties("title.event"));
	}

	public void removeEvent(Event event) {

		EventActionTypeMapDAO eventATMapDAO = new EventActionTypeMapDAO();
		// Delete Event and Map Event - Action Type
		if (!eventATMapDAO.checkEventInActionType(event.getEventId())) {
			eventATMapDAO.deleteEventActionTypeByEventId(event);
			for (int i = 0; i < events.size(); i++) {
				if (event.getEventId() == events.get(i).getEventId()) {
					events.remove(i);
					break;
				}
			}
			category.setCategoryId(event.getCategoryId());
			// refreshCategories(category);
			super.getTreeOfferBean().removeTreeNodeAll(event);
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, this.readProperties("common.delete"),
					this.readProperties("title.event")));
		} else {
			this.showMessageWARN("common.summary.warning", super.readProperties("event.objectUser"));
		}
	}

	public void chooseActionType(ActionType item) {
		
		List<Long> lstId = new ArrayList<Long>();
		if(actionTypes != null) {
			for (ActionType at : actionTypes) {
				lstId.add(at.getActionTypeId());
			}	
		}
		
		if (item == null || item.getActionTypeId() == null){			
			super.openTreeOfferDialog(TreeType.OFFER_ACTION_TYPE, readProperties("title.actiontype"), 0, true, lstId);
		} else {
			super.openTreeOfferDialog(TreeType.OFFER_ACTION_TYPE, readProperties("title.actiontype"), 0, false, lstId);
		}
		actionType = item;
		insert = false;
	}

	public void commandAddNewActionType() {
		ActionType itemNew = new ActionType();
		chooseActionType(itemNew);
		insert = false;
	}

	public void onDialogActionTypeReturn(SelectEvent event) {
		Object[] objArr = new Object[1];
		if (event.getObject() instanceof Object[]) {
			objArr = (Object[]) event.getObject();
		} else {
			objArr[0] = event.getObject();
		}

		for (Object obj : objArr) {
			if (obj instanceof ActionType) {
				ActionType actionTypeChange = (ActionType) obj;
				if (!exitsEvents(actionTypeChange)) {
					if (actionType.getActionTypeId() != null) {
						if (insert) {
							actionTypes.add(actionTypes.indexOf(actionType) + 1, actionTypeChange);
						} else {
							actionTypes.set(actionTypes.indexOf(actionType), actionTypeChange);
						}

					} else {
						actionTypes.add(actionTypeChange);
					}
				} else {
					if (actionType.getActionTypeId() == actionTypeChange.getActionTypeId() && !insert) {
						actionTypes.set(actionTypes.indexOf(actionType), actionTypeChange);
					} else {
						this.showMessageWARN("common.summary.warning", super.readProperties("common.objAlreadyExists"));
					}
				}
			}
		}
	}

	private boolean exitsEvents(ActionType actionType) {
		for (ActionType type : actionTypes) {
			if (type.getActionTypeId() == actionType.getActionTypeId()) {
				return true;
			}
		}
		return false;
	}

	private boolean insert = false;
	private List<Category> categoriesOfEvent;

	public void addATAt(ActionType item) {
		
		List<Long> lstId = new ArrayList<Long>();
		if(actionTypes != null) {
			for (ActionType at : actionTypes) {
				lstId.add(at.getActionTypeId());
			}	
		}
		
		if (item == null || item.getActionTypeId() <= 0){			
			super.openTreeOfferDialog(TreeType.OFFER_ACTION_TYPE, readProperties("title.actiontype"), 0, true, lstId);
		} else {
			super.openTreeOfferDialog(TreeType.OFFER_ACTION_TYPE, readProperties("title.actiontype"), 0, false, lstId);
		}
		actionType = item;
		insert = true;
	}

	public void editActionType(ActionType actionType) {
		TreeOfferBean offerBean = getTreeOfferBean();
		offerBean.setContentTitle(super.readProperties("title.actiontype"));
		offerBean.setActionTypeProperties(false, actionType, true, false);
	}

	public void editRateTable(RateTable rateTable) {
		TreeOfferBean offerBean = getTreeOfferBean();
		offerBean.setContentTitle(super.readProperties("title.rateTable"));
		offerBean.setRateTableProperties(false, rateTable.getCategoryId(), rateTable, true, false);
	}

	public void removeActionType(ActionType actionType) {
		actionTypes.remove(actionType);
	}

	public void saveEvent() {
		if (validateEvent()) {
			if (eventDAO.saveEventAndMap(event, actionTypes)) {
				TreeOfferBean treeOfferBean = super.getTreeOfferBean();
				Category cat = categoryDAO.get(event.getCategoryId());
				treeOfferBean.updateTreeNode(event, cat, actionTypes);
				this.isEditting = false;
				this.isCheckId = true;
				this.showMessageINFO("common.save", " action type ");
			} else {
				this.showMessageWARN("common.save", " action type ");
			}
		}

	}

	private boolean validateEvent() {
		boolean result = true;

		if (event.getEventId() == null) {
			this.showMessageWARN("event.eventid", super.readProperties("validate.fieldNull"));
			result = false;
		}

		if (eventDAO.checkId(event, isCheckId)) {
			this.showMessageWARN("event.eventid", super.readProperties("validate.checkObjectExist"));
			result = false;
		}

		if (ValidateUtil.checkStringNullOrEmpty(event.getEventName())) {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_WARN, this.readProperties("normalizer.validateError"),
							this.readProperties("normalizer.errorDataValueName")));
			result = false;
		}
		if (eventDAO.checkName(event)) {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
					this.readProperties("normalizer.validateError"), this.readProperties("common.nameAlreadyExists")));
			result = false;
		}
		if (event.getEventType() == null) {
			this.showMessageWARN("event.eventtype", super.readProperties("validate.fieldNull"));
			result = false;
		}

		return result;
	}

	// CommandCheckDependencies ContextMenu
	public void commandCheckDependencies(NodeSelectEvent nodeSelectEvent) {
		if (nodeSelectEvent != null) {
			Event item = (Event) nodeSelectEvent.getTreeNode().getData();
			showDependencies(item.getEventId(), Event.class);
		}
	}

	// Command AddNew ContextMenu
	public void commandAddNewContextMenu(NodeSelectEvent nodeSelectEvent) {
		Object object = nodeSelectEvent.getTreeNode().getData();
		if (object instanceof Event) {
			Event ev = (Event) object;
			TreeOfferBean treeOfferBean = super.getTreeOfferBean();
			treeOfferBean.hideCategory();
			Event event = new Event();
			event.setCategoryId(ev.getCategoryId());
			event.setIsRequireRatingConfig(true);
//			treeOfferBean.setEventProperties(false, event, true);
			treeOfferBean.setContentTitle(super.readProperties("title.event"));
			refreshEvent(event, true);
			this.isCheckId = false;
		}
	}

	// Command Remove ContextMenu
	public void commandRemoveContextMenu(NodeSelectEvent nodeSelectEvent) {
		if (nodeSelectEvent != null) {
			Event item = (Event) nodeSelectEvent.getTreeNode().getData();
			Object object = nodeSelectEvent.getTreeNode().getParent().getData();
			EventActionTypeMapDAO eventATMapDAO = new EventActionTypeMapDAO();
			if (object instanceof Category) {
				// Delete Event and Map Event - Action Type
				if (!eventATMapDAO.checkEventInActionType(item.getEventId())) {
					eventATMapDAO.deleteEventActionTypeByEventId(item);
					for (int i = 0; i < events.size(); i++) {
						if (item.getEventId() == events.get(i).getEventId()) {
							events.remove(i);
							break;
						}
					}
					Category category = categoryDAO.get(item.getCategoryId());
					category.setCategoryId(category.getCategoryId());
					super.getTreeOfferBean().removeTreeNode(event, category);
					super.showMessageINFO(this.readProperties("common.delete"), this.readProperties("title.event"));
				} else {
					this.showMessageWARN("common.summary.warning", super.readProperties("event.objectUser"));
				}
			}
		}

	}

	// Command Edit ContextMenu
	public void commandEditContextMenu(NodeSelectEvent nodeSelectEvent) {
		super.getTreeOfferBean().onNodeSelect(nodeSelectEvent);
		TreeOfferBean treeOfferBean = super.getTreeOfferBean();
		treeOfferBean.hideCategory();
//		treeOfferBean.setContentTitle(super.readProperties("title.event"));
		refreshEvent(event, true);
	}

	// Move to Category

	public void redirectChangeCate() {
		this.openTreeCategoryDialog(TreeType.OFFER_EVENT_EVENT, "Event", 0);
	}

	public void onDialogReturnCategory(SelectEvent event) {
		Object obj = event.getObject();
		if (obj instanceof Category) {
			Category cate = (Category) obj;
			this.event.setCategoryId(cate.getCategoryId());
			if (eventDAO.moveToCate(this.event)) {
				TreeOfferBean treeOfferBean = super.getTreeOfferBean();
				treeOfferBean.updateTreeNode(this.event, cate, null);
				this.showMessageINFO("common.moveCate", " Success ");
				this.isEditting = false;
			}
		}
	}

	// load list Event by Category
	public void loadEventByCategory(Long categoryId) {
		events = new ArrayList<Event>();
		events = eventDAO.findEventByConditions(categoryId);
	}

	public void moveUpEventContext(NodeSelectEvent nodeSelectEvent) {
		if (nodeSelectEvent != null) {
			BaseEntity objParent = (BaseEntity) nodeSelectEvent.getTreeNode().getParent().getData();
			if (objParent instanceof Category) {
				Event item = (Event) nodeSelectEvent.getTreeNode().getData();
				item = eventDAO.get(item.getEventId());
				List<Long> lstId = new ArrayList<>();
				lstId.add(item.getCategoryId());
				List<Event> listEvent = eventDAO.findByListCategory(lstId);
				for (int i = 0; i < listEvent.size(); i++) {
					if (item.getEventId() == listEvent.get(i).getEventId() && i != 0) {
						Event upItem = listEvent.get(i - 1);
						if (eventDAO.processMoveUpDown(upItem, item)) {
							TreeOfferBean treeOfferBean = super.getTreeOfferBean();
							treeOfferBean.moveUpTreeNode(item, objParent);
							loadEventByCategory(item.getCategoryId());
							this.showMessageINFO("validate.upObjectSuccess", super.readProperties(""));
						}
						break;
					}
				}

				if (listEvent.size() == 1) {
					this.showMessageWARN("common.summary.warning", super.readProperties("validate.notMove"));
				}
			}
			if (objParent instanceof Offer) {
			}
		}
	}

	public void moveDownEventContext(NodeSelectEvent nodeSelectEvent) {
		if (nodeSelectEvent != null) {
			Event item = (Event) nodeSelectEvent.getTreeNode().getData();
			item = eventDAO.get(item.getEventId());
			BaseEntity objParent = (BaseEntity) nodeSelectEvent.getTreeNode().getParent().getData();
			if (objParent instanceof Category) {
				List<Long> lstId = new ArrayList<>();
				lstId.add(item.getCategoryId());
				List<Event> listEvent = eventDAO.findByListCategory(lstId);
				for (int i = 0; i < listEvent.size(); i++) {
					if (item.getEventId() == listEvent.get(i).getEventId() && i != (listEvent.size() - 1)) {
						Event upItem = listEvent.get(i + 1);
						if (eventDAO.processMoveUpDown(upItem, item)) {
							TreeOfferBean treeOfferBean = super.getTreeOfferBean();
							treeOfferBean.moveDownTreeNode(item, objParent);
							loadEventByCategory(item.getCategoryId());
							this.showMessageINFO("validate.downObjectSuccess", super.readProperties(""));
						}
						break;
					}
				}
				if (listEvent.size() == 1) {
					this.showMessageWARN("common.summary.warning", super.readProperties("validate.notMove"));
				}
			}
			if (objParent instanceof Offer) {
			}
		}
	}

	// GET, SET
	public String getFormType() {
		return formType;
	}

	public void setFormType(String formType) {
		this.formType = formType;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public List<Event> getEvents() {
		return events;
	}

	public void setEvents(List<Event> events) {
		this.events = events;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public void loadCategoriesOfEvent() {
		categoriesOfEvent = categoryDAO.findByTypeForSelectboxWithoutDomain(CategoryType.OFF_EVENT_EVENT);

	}

	public List<ActionType> getActionTypes() {
		return actionTypes;
	}

	public void setActionTypes(List<ActionType> actionTypes) {
		this.actionTypes = actionTypes;
	}

	public boolean isEditting() {
		return isEditting;
	}

	public void setEditting(boolean isEditting) {
		this.isEditting = isEditting;
	}

	public ActionType getActionType() {
		return actionType;
	}

	public void setActionType(ActionType actionType) {
		this.actionType = actionType;
	}

	public boolean isCheckId() {
		return isCheckId;
	}

	public void setCheckId(boolean isCheckId) {
		this.isCheckId = isCheckId;
	}

	public List<Category> getCategoriesOfEvent() {
		return categoriesOfEvent;
	}

	public void setCategoriesOfEvent(List<Category> categoriesOfEvent) {
		this.categoriesOfEvent = categoriesOfEvent;
	}

}

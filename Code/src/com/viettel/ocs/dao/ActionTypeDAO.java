package com.viettel.ocs.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.ActionType;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.DynamicReserve;
import com.viettel.ocs.entity.Event;
import com.viettel.ocs.entity.EventActionTypeMap;

@SuppressWarnings("serial")
public class ActionTypeDAO extends BaseDAO<ActionType> implements Serializable {

	@Override
	protected Class<ActionType> getEntityClass() {
		return ActionType.class;
	}

	public List<ActionType> findByListCategory(List<Long> lstCatID) {
		StringBuffer hql = new StringBuffer();
		hql.append("SELECT a FROM ActionType a");
		hql.append(" WHERE a.categoryId IN (:categoryId) order by a.index ASC");
		List<Long> lstCatIDParam = new ArrayList<Long>();
		lstCatIDParam.addAll(lstCatID);
		if (lstCatIDParam.size() == 0)
			lstCatIDParam.add(-1L);
		Session session = HibernateUtil.getOpenSession();
		try {
			Query<ActionType> query = session.createQuery(hql.toString());
			query.setParameterList("categoryId", lstCatIDParam);
			return query.getResultList();

		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public List<ActionType> findByCategory(Category category) {
		List<Long> categoryIds = new ArrayList<>();
		categoryIds.add(category.getCategoryId());
		return this.findByListCategory(categoryIds);
	}

	public boolean checkId(ActionType actionType, boolean edit) {
		List<ActionType> lst = null;
		String hql = "SELECT p FROM ActionType p";
		hql += " WHERE p.actionTypeId = :actionTypeId";

		Session session = HibernateUtil.getOpenSession();
		try {
			Query<ActionType> query = session.createQuery(hql);
			query.setParameter("actionTypeId", actionType.getActionTypeId());
			lst = query.getResultList();
			if (lst.size() > 0 && !edit) {
				return true;
			}
			return false;
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public boolean saveActionTypeAndMapping(ActionType actionType, List<Event> events) {

		if (events == null) {
			return false;
		}

		List<Long> ids = new ArrayList<Long>();
		for (Event event : events) {
			ids.add(event.getEventId());
		}
		if (ids.size() == 0) {
			ids.add(-1L);
		}

		Session session = HibernateUtil.getOpenSession();

		session.getTransaction().begin();
		try {
			// SAVE ACTION
			generateIndexByCat(actionType, "actionTypeId", session);
			session.saveOrUpdate(actionType);

			// DELETE EVENTS
			this.deleteEvents(ids, actionType, session);

			// SAVE OR UPDATE EVENTS
			for (Event event : events) {
				EventActionTypeMap actionTypeMap = this.findActionPriceComponentMapByRelations(actionType, event,
						session);
				if (actionTypeMap.getEventActionTypeMapId() == 0) {
					EventActionTypeMapDAO evenActionMapDAO = new EventActionTypeMapDAO();
					Long index = (long) evenActionMapDAO.getMaxIndexMove(session, event.getEventId()) + 1;
					actionTypeMap.setActionTypeIndex(index);
					session.save(actionTypeMap);
				}
			}

			session.getTransaction().commit();
			return true;
		} catch (Exception e) {
			
			session.getTransaction().rollback();
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	@SuppressWarnings("unchecked")
	private void deleteEvents(List<Long> ids, ActionType actionType, Session session) {
		try {
			StringBuffer hql = new StringBuffer("DELETE FROM EventActionTypeMap b");
			hql.append(" WHERE b.eventId NOT IN (:eventIds)");
			hql.append(" AND b.actionTypeId =:actionTypeId");

			Query<EventActionTypeMap> query = session.createQuery(hql.toString());
			query.setParameterList("eventIds", ids);
			query.setParameter("actionTypeId", actionType.getActionTypeId());
			query.executeUpdate();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}		
	}

	@SuppressWarnings("unchecked")
	private EventActionTypeMap findActionPriceComponentMapByRelations(ActionType actionType, Event event,
			Session session) {
		try {
			StringBuffer hql = new StringBuffer("SELECT a FROM EventActionTypeMap a");
			hql.append(" WHERE a.actionTypeId = :actionTypeId");
			hql.append(" AND a.eventId = :eventId");

			Query<EventActionTypeMap> query = session.createQuery(hql.toString());
			query.setParameter("actionTypeId", actionType.getActionTypeId());
			query.setParameter("eventId", event.getEventId());

			List<EventActionTypeMap> actionTypeMaps = query.getResultList();

			if (actionTypeMaps != null && actionTypeMaps.size() > 0) {
				return actionTypeMaps.get(0);
			}

			// clear domain
			EventActionTypeMap actionTypeMap = new EventActionTypeMap(event.getEventId(), actionType.getActionTypeId());
			return actionTypeMap;
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}		
	}

	@SuppressWarnings("unchecked")
	public List<ActionType> findActionTypesByEvent(Event event) {
		StringBuffer hql = new StringBuffer();
		hql.append("SELECT a FROM ActionType a");
		hql.append(" JOIN EventActionTypeMap b ON b.actionTypeId = a.actionTypeId");
		hql.append(" WHERE b.eventId = :eventId order by b.actionTypeIndex ASC");
		Session session = HibernateUtil.getOpenSession();
		try {
			Query<ActionType> query = session.createQuery(hql.toString());
			query.setParameter("eventId", event.getEventId());
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public int countActionTypeByEvent(long eventId) {
		StringBuffer hql = new StringBuffer();
		hql.append("SELECT COUNT(*) FROM ActionType a");
		hql.append(" JOIN EventActionTypeMap b ON b.actionTypeId = a.actionTypeId");
		hql.append(" WHERE b.eventId = :eventId");
		Session session = HibernateUtil.getOpenSession();
		try {
			Query<Long> query = session.createQuery(hql.toString());
			query.setParameter("eventId", eventId);
			return query.uniqueResult().intValue();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public List<ActionType> findActionTypeByConditions(Long categoryId) {

		List<ActionType> lst = null;
		String[] cols = { "categoryId" };
		Operator[] operators = { Operator.EQ };
		Object[] values = { categoryId };
		String oder = "index";
		lst = findByConditionsWithoutDomain(cols, operators, values, oder);
		return lst;
	}

	public boolean checkName(ActionType actionType) {
		String hql = "SELECT COUNT(*) FROM ActionType a WHERE a.actionTypeName LIKE :actionTypeName AND a.actionTypeId != :actionTypeId";

		Session session = HibernateUtil.getOpenSession();
		try {

			Query<Long> query = session.createQuery(hql);
			query.setParameter("actionTypeName", actionType.getActionTypeName());
			query.setParameter("actionTypeId", actionType.getActionTypeId());
			int count = query.uniqueResult().intValue();
			if (count > 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public boolean deleteActionTypeByAction(ActionType actionType) {
		String hql = "DELETE FROM ActionType map";
		hql += " WHERE map.actionTypeId = :actionTypeId";
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			session.delete(actionType);
			Query<DynamicReserve> query = session.createQuery(hql);
			query.setParameter("actionTypeId", actionType.getActionTypeId());
			query.executeUpdate();
			session.getTransaction().commit();
			return true;
		} catch (Exception e) {
			session.getTransaction().rollback();
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public int countActionType() {
		String hql = "SELECT COUNT(a) FROM ActionType a";
		Session session = HibernateUtil.getOpenSession();
		try {
			Query<Long> query = session.createQuery(hql, Long.class);
			return query.getSingleResult().intValue();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public ActionType cloneAT(ActionType actionType, boolean cloneNormalizer, String suffix) {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		ActionType actionTypeCloned = null;
		try {
			actionTypeCloned = this.cloneAT();
			session.getTransaction().commit();
			return actionTypeCloned;
		} catch (Exception e) {
			
			session.getTransaction().rollback();
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public ActionType cloneAT() {
		return null;
	}

	public Long getMaxIndex(Session session) {
		try {
			StringBuffer hql = new StringBuffer("SELECT COALESCE(MAX(a.index), 0) FROM ActionType a");
			Query<Long> query = session.createQuery(hql.toString(), Long.class);
			return query.getSingleResult();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}		
	}

	public Boolean processMoveUpDown(ActionType moveItem, ActionType item) {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			Long tmp = moveItem.getIndex();
			moveItem.setIndex(item.getIndex());
			item.setIndex(tmp);
			session.saveOrUpdate(moveItem);
			session.saveOrUpdate(item);
			session.getTransaction().commit();
		} catch (Exception e) {
			session.getTransaction().rollback();
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
		return true;
	}
	
	public boolean moveToCate(ActionType actionType) {
		Session session = HibernateUtil.getOpenSession();
		try {
			session.getTransaction().begin();
			generateIndexByCat(actionType, "actionTypeId", session);
			session.saveOrUpdate(actionType);
			session.getTransaction().commit();
			return true;
		} catch (Exception e) {
			
			session.getTransaction().rollback();
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}
}

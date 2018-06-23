package com.viettel.ocs.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.ActionType;
import com.viettel.ocs.entity.DynamicReserveRateTableMap;
import com.viettel.ocs.entity.Event;
import com.viettel.ocs.entity.EventActionTypeMap;
import com.viettel.ocs.entity.Parameter;
import com.viettel.ocs.entity.RateTable;

@SuppressWarnings("serial")
public class EventActionTypeMapDAO extends BaseDAO<EventActionTypeMap> implements Serializable {

	@Override
	protected Class<EventActionTypeMap> getEntityClass() {
		return EventActionTypeMap.class;
	}

	// public boolean checkEventInActionType(Long eventId) {
	// List<EventActionTypeMap> lst = null;
	// String[] cols = { "eventId" };
	// Operator[] operators = { Operator.EQ };
	// Object[] values = { eventId };
	// lst = findByConditions(cols, operators, values, "");
	// if (lst != null && lst.size() > 0) {
	// return true;
	// }
	// return false;
	// }

	public boolean checkEventInActionType(Long eventId) {
		StringBuffer hql = new StringBuffer("SELECT COUNT(a) FROM EventActionTypeMap a");
		hql.append(" WHERE a.eventId = :eventId");
		Session session = HibernateUtil.getOpenSession();
		try {
			Query<Long> query = session.createQuery(hql.toString(), Long.class);
			query.setParameter("eventId", eventId);
			if (query.getSingleResult() > 0L) {
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

	public boolean deleteEventActionTypeByEventId(Event event) {
		String hql = "DELETE FROM EventActionTypeMap map";
		hql += " WHERE map.eventId = :eventId";
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			session.delete(event);
			Query<Event> query = session.createQuery(hql);
			query.setParameter("eventId", event.getEventId());
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

	// public boolean checkActionTypeInEvent(Long actionTypeId) {
	// List<EventActionTypeMap> lst = null;
	// String[] cols = { "actionTypeId" };
	// Operator[] operators = { Operator.EQ };
	// Object[] values = { actionTypeId };
	// lst = findByConditions(cols, operators, values, "");
	// if (lst != null && lst.size() > 0) {
	// return true;
	// }
	// return false;
	// }

	public boolean checkActionTypeInEvent(Long actionTypeId) {
		StringBuffer hql = new StringBuffer("SELECT COUNT(a) FROM EventActionTypeMap a");
		hql.append(" WHERE a.actionTypeId = :actionTypeId");
		Session session = HibernateUtil.getOpenSession();
		try {
			Query<Long> query = session.createQuery(hql.toString(), Long.class);
			query.setParameter("actionTypeId", actionTypeId);
			if (query.getSingleResult() > 0L) {
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

	public boolean deleteActionTypeEventByActionTypeId(ActionType actionType) {
		String hql = "DELETE FROM EventActionTypeMap map";
		hql += " WHERE map.actionTypeId = :actionTypeId";
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			session.delete(actionType);
			Query<ActionType> query = session.createQuery(hql);
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

	// public EventActionTypeMap findEventAT(Long eventId, Long actionTypeId) {
	// List<EventActionTypeMap> lst = null;
	// String[] cols = { "eventId", "actionTypeId" };
	// Operator[] operators = { Operator.EQ, Operator.EQ };
	// Object[] values = { eventId, actionTypeId };
	// // String oder = " rateTableIndex ASC";
	// lst = findByConditions(cols, operators, values, "");
	// if (!lst.isEmpty()) {
	// return lst.get(0);
	// }
	// return null;
	// }

	public EventActionTypeMap findEventAT(Long eventId, Long actionTypeId) {
		List<EventActionTypeMap> lst = null;
		StringBuffer hql = new StringBuffer("FROM EventActionTypeMap a");
		hql.append(" WHERE a.eventId = :eventId AND a.actionTypeId=:actionTypeId");

		Session session = HibernateUtil.getOpenSession();
		try {
			Query<EventActionTypeMap> query = session.createQuery(hql.toString());
			query.setParameter("eventId", eventId);
			query.setParameter("actionTypeId", actionTypeId);
			lst = query.getResultList();
			if (!lst.isEmpty()) {
				return lst.get(0);
			}
			return null;
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}

	}
	
	public List<EventActionTypeMap> findEventActionTypeMapByAT(Long eventId) {
		List<EventActionTypeMap> lst = null;
		String[] cols = { "eventId" };
		Operator[] operators = { Operator.EQ };
		Object[] values = { eventId };
		String oder = " actionTypeIndex ASC";
		lst = findByConditionsWithoutDomain(cols, operators, values, oder);
		return lst;
	}
	
	public Boolean saveEventActionTypeMap(EventActionTypeMap itemUp, EventActionTypeMap item) {
		boolean result = true;
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			long indexUp = itemUp.getActionTypeIndex();
			itemUp.setActionTypeIndex(item.getActionTypeIndex());
			session.saveOrUpdate(itemUp);

			item.setActionTypeIndex(indexUp);
			session.saveOrUpdate(item);

			session.getTransaction().commit();
		} catch (Exception e) {
			
			session.getTransaction().rollback();
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
		
		return result;
	}
	
	public Long getMaxIndexMove(Session session, Long eventId ) {
		try {
			StringBuffer hql = new StringBuffer("SELECT COALESCE(MAX(a.actionTypeIndex), 0) FROM EventActionTypeMap a");
			hql.append(" WHERE a.eventId = " + eventId);
			Query<Long> query = session.createQuery(hql.toString(), Long.class);
			return query.getSingleResult();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}		
	}
	
	public Boolean processMoveUpDown(EventActionTypeMap item, Boolean isUp) {
		boolean result = false;
		if (item != null && item.getEventActionTypeMapId() > 0) {
			Session session = HibernateUtil.getOpenSession();
			item = get(item.getEventActionTypeMapId());
			try {

				session.getTransaction().begin();

				StringBuffer hql = new StringBuffer("SELECT a FROM EventActionTypeMap a");
				hql.append(" WHERE a.eventId = :eventId");
				if (isUp) {
					hql.append(" AND a.actionTypeIndex < :actionTypeIndex");
				} else {
					hql.append(" AND a.actionTypeIndex > :actionTypeIndex");
				}


				if (isUp) {
					hql.append(" ORDER BY a.actionTypeIndex DESC");
				} else {
					hql.append(" ORDER BY a.actionTypeIndex ASC");
				}

				Query<EventActionTypeMap> query = session.createQuery(hql.toString());
				query.setParameter("actionTypeIndex", item.getActionTypeIndex());
				query.setParameter("eventId", item.getEventId());
				query.setMaxResults(1);

				List<EventActionTypeMap> list = query.getResultList();
				if (list != null && list.size() > 0) {
					// if (!checkFieldIsExist("index", item.getIndex(), item)) {
					EventActionTypeMap itemMove = list.get(0);
					Long tmpIndex = 0l;
					tmpIndex = itemMove.getActionTypeIndex();
					itemMove.setActionTypeIndex(item.getActionTypeIndex());
					item.setActionTypeIndex(tmpIndex);
					session.update(itemMove);
					session.update(item);
					result = true;
					// } else {
					// result = false;
					// }
				} else {
					result = false;
				}

				session.getTransaction().commit();
			} catch (Exception e) {
				// TODO: handle exception
				session.getTransaction().rollback();
				getLogger().error(e.getMessage(), e);
				throw e;
			} finally {
				session.close();
			}
		}
		return result;
	}

}

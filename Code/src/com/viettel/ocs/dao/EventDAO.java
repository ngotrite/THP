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
import com.viettel.ocs.entity.Event;
import com.viettel.ocs.entity.EventActionTypeMap;
import com.viettel.ocs.entity.Offer;
import com.viettel.ocs.entity.RateTable;
import com.viettel.ocs.entity.UnitType;
import com.viettel.ocs.entity.ZoneMap;

public class EventDAO extends BaseDAO<Event> implements Serializable {

	@Override
	protected Class<Event> getEntityClass() {
		return Event.class;
	}

	@SuppressWarnings("unchecked")
	public List<Event> findEventsByActionType(ActionType actionType) {
		StringBuffer hql = new StringBuffer("SELECT c FROM EventActionTypeMap b");
		hql.append(" JOIN Event c ON c.eventId = b.eventId");
		hql.append(" WHERE b.actionTypeId =:actionTypeId");

		Session session = HibernateUtil.getOpenSession();
		try {

			Query<Event> query = session.createQuery(hql.toString());
			query.setParameter("actionTypeId", actionType.getActionTypeId());
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public boolean checkId(Event event, boolean edit) {
		List<Event> lst = null;
		String hql = "SELECT p FROM Event p";
		hql += " WHERE p.eventId = :eventId";

		Session session = HibernateUtil.getOpenSession();
		try {
			Query<Event> query = session.createQuery(hql);
			query.setParameter("eventId", event.getEventId());
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

	public List<Event> findByListCategory(List<Long> lstCatID) {
		StringBuffer hql = new StringBuffer();
		hql.append("SELECT a FROM Event a");
		hql.append(" WHERE a.categoryId IN (:categoryId) order by a.index ASC");
		List<Long> lstCatIDParam = new ArrayList<Long>();
		lstCatIDParam.addAll(lstCatID);
		if (lstCatIDParam.size() == 0)
			lstCatIDParam.add(-1L);
		Session session = HibernateUtil.getOpenSession();
		try {
			Query<Event> query = session.createQuery(hql.toString());
			query.setParameterList("categoryId", lstCatIDParam);
			return query.getResultList();

		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}
	
	public List<Event> findEventByConditions(Long categoryId) {

		List<Event> lst = null;		
		String[] cols = { "categoryId"};
		Operator[] operators = { Operator.EQ };
		Object[] values = { categoryId };
		String oder = "index";
		lst = findByConditionsWithoutDomain(cols, operators, values, oder);
		return lst;
	}

	public List<Event> findEventsByOffer(Offer offer) {
		// TODO WILL TO DO
		return new ArrayList<Event>();
	}

	public List<Event> findByCategory(Category category) {
		List<Long> categoryIds = new ArrayList<Long>();
		categoryIds.add(category.getCategoryId());
		return this.findByListCategory(categoryIds);
	}

	public boolean saveEventAndMap(Event event, List<ActionType> actionTypes) {
		List<Long> ids = new ArrayList<Long>();
		for (ActionType actionType : actionTypes) {
			ids.add(actionType.getActionTypeId());
		}
		if (ids.size() == 0) {
			ids.add(-1L);
		}

		Session session = HibernateUtil.getOpenSession();

		session.getTransaction().begin();
		try {
			// SAVE EVENT
			generateIndexByCat(event, "eventId", session);
			session.saveOrUpdate(event);

			// DELETE ACTIONTYPES
			this.deleteActionTypes(ids, event, session);

			// SAVE OR UPDATE ACTIONTYPES
			for (ActionType actionType : actionTypes) {
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
	private EventActionTypeMap findActionPriceComponentMapByRelations(ActionType actionType, Event event,
			Session session) {
		StringBuffer hql = new StringBuffer("SELECT a FROM EventActionTypeMap a");
		hql.append(" WHERE a.actionTypeId = :actionTypeId");
		hql.append(" AND a.eventId = :eventId");

		try {
			
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

	private void deleteActionTypes(List<Long> ids, Event event, Session session) {
		StringBuffer hql = new StringBuffer("DELETE FROM EventActionTypeMap b");
		hql.append(" WHERE b.eventId = :eventId");
		hql.append(" AND b.actionTypeId NOT IN (:actionTypeIds)");

		try {
			Query<EventActionTypeMap> query = session.createQuery(hql.toString());
			query.setParameter("eventId", event.getEventId());
			query.setParameterList("actionTypeIds", ids);
			query.executeUpdate();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}		
	}

	public boolean checkName(Event event) {
		String hql = "SELECT COUNT(*) FROM Event a WHERE a.eventName LIKE :eventName AND a.eventId != :eventId";

		Session session = HibernateUtil.getOpenSession();
		try {

			Query<Long> query = session.createQuery(hql);
			query.setParameter("eventName", event.getEventName());
			query.setParameter("eventId", event.getEventId());
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

	public int countEvent() {
		String hql = "SELECT COUNT(a) FROM Event a";
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

	public Long getMaxIndex(Session session) {
		try {
			StringBuffer hql = new StringBuffer("SELECT COALESCE(MAX(a.index), 0) FROM Event a");
			Query<Long> query = session.createQuery(hql.toString(), Long.class);
			return query.getSingleResult();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}		
	}

	public Boolean processMoveUpDown(Event moveItem, Event item) {
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

	public Boolean processMoveUpDown(Event item, Boolean isUp) {
		boolean result = false;
		if (item != null && item.getEventId() > 0) {
			Session session = HibernateUtil.getOpenSession();
			item = get(item.getEventId());
			try {

				session.getTransaction().begin();

				StringBuffer hql = new StringBuffer("SELECT a FROM Event a");
				hql.append(" WHERE a.domainId = :domainId");

				if (isUp) {
					hql.append(" AND a.index < :index");
				} else {
					hql.append(" AND a.index > :index");
				}

				hql.append(" AND a.categoryId = :categoryId");

				if (isUp) {
					hql.append(" ORDER BY a.index DESC");
				} else {
					hql.append(" ORDER BY a.index ASC");
				}

				Query<Event> query = session.createQuery(hql.toString());
				query.setParameter("domainId", getDomainId());
				query.setParameter("index", item.getIndex());
				query.setParameter("categoryId", item.getCategoryId());
				query.setMaxResults(1);

				List<Event> events = query.getResultList();
				if (events != null && events.size() > 0) {
					// if (!checkFieldIsExist("index", item.getIndex(), item)) {
					Event itemMove = events.get(0);
					Long tmpIndex = 0l;
					tmpIndex = itemMove.getIndex();
					itemMove.setIndex(item.getIndex());
					item.setIndex(tmpIndex);
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
	
	public boolean moveToCate(Event event) {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			generateIndexByCat(event, "eventId", session);
			session.saveOrUpdate(event);
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

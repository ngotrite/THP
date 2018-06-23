package com.viettel.ocs.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.Action;
import com.viettel.ocs.entity.ActionPriceComponentMap;
import com.viettel.ocs.entity.BalType;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.DynamicReserve;
import com.viettel.ocs.entity.Offer;
import com.viettel.ocs.entity.OfferActionMap;
import com.viettel.ocs.entity.Parameter;
import com.viettel.ocs.entity.PriceComponent;
import com.viettel.ocs.entity.ReserveInfo;
import com.viettel.ocs.entity.SortPriceComponent;
import com.viettel.ocs.entity.UnitType;
import com.viettel.ocs.model.CloneIgnoreListModel;

@SuppressWarnings("serial")
public class ActionDAO extends BaseDAO<Action> implements Serializable {

	public int countActionByOffer(Long offerId) {

		List lst = null;
		String hql = "SELECT COUNT(a.actionId) FROM Action a JOIN OfferActionMap map ON map.actionId = a.actionId ";
		hql += " WHERE map.offerId = :offerId";

		Session session = HibernateUtil.getOpenSession();
		try {

			Query query = session.createQuery(hql);
			query.setParameter("offerId", offerId);
			lst = query.getResultList();
			if (lst.size() > 0)
				return ((Number) lst.get(0)).intValue();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}

		return 0;
	}

	public List<Action> findActionByOffer(Long offerId) {

		List<Action> lst = null;
		String hql = "SELECT a FROM Action a JOIN OfferActionMap map ON map.actionId = a.actionId ";
		hql += " WHERE map.offerId = :offerId";
		hql += " ORDER BY map.mapIndex";

		Session session = HibernateUtil.getOpenSession();
		try {

			Query<Action> query = session.createQuery(hql, Action.class);
			query.setParameter("offerId", offerId);
			lst = query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}

		return lst;
	}

	@Override
	protected Class<Action> getEntityClass() {
		return Action.class;
	}

	public List<Action> findActionsByCategoryId(List<Long> lstCatID) {
		String[] col = { "categoryId" };
		Operator[] ope = { Operator.IN };
		List<Long> lstCatIDParam = new ArrayList<Long>();
		lstCatIDParam.addAll(lstCatID);
		if (lstCatIDParam.size() == 0)
			lstCatIDParam.add(-1L);
		Object[] val = { lstCatIDParam };
		String oder = " index ASC";
		return this.findByConditions(col, ope, val, oder);
	}

	public List<Action> findActionsByCategoryIdAndNotGened(List<Long> lstCatID) {
		String hql = "SELECT a FROM Action a";
		hql += " WHERE a.categoryId IN (:categoryIds) AND (a.generated is false OR a.generated is NULL)";

		Session session = HibernateUtil.getOpenSession();
		try {

			Query<Action> query = session.createQuery(hql, Action.class);
			query.setParameterList("categoryIds", lstCatID);
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public List<Action> findActionsByCategoryId(Category category) {
		List<Long> categoryIds = new ArrayList<>();
		categoryIds.add(category.getCategoryId());
		return findActionsByCategoryId(categoryIds);
	}

	public boolean saveActionDetailt(Action action, List<PriceComponent> priceComponents) {
		List<Long> priceComponentIds = new ArrayList<Long>();
		priceComponentIds.add(-1L);

		for (PriceComponent priceComponent : priceComponents) {
			priceComponentIds.add(priceComponent.getPriceComponentId());
		}

		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {

			// SAVE ACTION
			if (action.getActionId() == 0L) {
				action.setDomainId(getDomainId());
			}
			
			generateIndexByCat(action, "actionId", session);

			session.saveOrUpdate(action);

			// DELETE MAP PRICE COMPONENT
			this.deleteActionPriceComponentMapOfActionId(priceComponentIds, action, session);

			// SAVE OR UPDATE PRICE COMPONENT MAP
			for (int i = 0; i < priceComponents.size(); i++) {
				ActionPriceComponentMap actionPriceComponentMap = this.findActionPriceComponentMapByRelations(action,
						priceComponents.get(i), session);
				actionPriceComponentMap.setPcIndex(Long.valueOf(i));
				actionPriceComponentMap.setDomainId(getDomainId());
				session.saveOrUpdate(actionPriceComponentMap);
				// if (actionPriceComponentMap.getActionPriceComponentMapId() ==
				// 0) {
				// actionPriceComponentMap.setDomainId(getDomainId());
				// if (actionPriceComponentMap.getPcIndex() == null ||
				// actionPriceComponentMap.getPcIndex() == 0) {
				// actionPriceComponentMap.setPcIndex(getMaxIndexActionPriceComponentMap(session)
				// + 1);
				// }
				// session.save(actionPriceComponentMap);
				// }
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
	private void deleteActionPriceComponentMapOfActionId(List<Long> priceComponentIds, Action action, Session session) {
		try {
			StringBuffer hql = new StringBuffer("DELETE FROM ActionPriceComponentMap a");
			hql.append(" WHERE a.priceComponentId NOT IN (:priceComponentIds)");
			hql.append(" AND a.actionId = :actionId");
			hql.append(" AND a.domainId = :domainId");

			Query<ActionPriceComponentMap> query = session.createQuery(hql.toString());
			query.setParameterList("priceComponentIds", priceComponentIds);
			query.setParameter("actionId", action.getActionId());
			query.setParameter("domainId", getDomainId());
			query.executeUpdate();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}		
	}

	@SuppressWarnings("unchecked")
	private ActionPriceComponentMap findActionPriceComponentMapByRelations(Action action, PriceComponent priceComponent,
			Session session) {
		try {
			StringBuffer hql = new StringBuffer("SELECT a FROM ActionPriceComponentMap a");
			hql.append(" WHERE a.priceComponentId = :priceComponentId");
			hql.append(" AND a.actionId = :actionId");
			hql.append(" AND a.domainId = :domainId");

			Query<ActionPriceComponentMap> query = session.createQuery(hql.toString());
			query.setParameter("actionId", action.getActionId());
			query.setParameter("priceComponentId", priceComponent.getPriceComponentId());
			query.setParameter("domainId", getDomainId());

			List<ActionPriceComponentMap> actionPriceComponentMaps = query.getResultList();

			if (actionPriceComponentMaps != null && actionPriceComponentMaps.size() > 0) {
				return actionPriceComponentMaps.get(0);
			}
			ActionPriceComponentMap actionPriceComponentMap = new ActionPriceComponentMap(action.getActionId(),
					priceComponent.getPriceComponentId());
			return actionPriceComponentMap;
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}		
	}

	/**
	 * Clone Action
	 * 
	 * @author THANHND
	 * @param action
	 * @param clonePC
	 * @param cloneBlock
	 * @param cloneRateTable
	 * @param cloneDT
	 * @param parameters
	 * @param ignoreList
	 * @param usingParams
	 * @param usingType4SortPC
	 * @param usingType4PC
	 * @param usingType4DR
	 * @param session
	 * @throws CloneNotSupportedException
	 */
	public Action cloneAction(Action action, boolean clonePC, boolean cloneBlock, boolean cloneRateTable,
			boolean cloneDT, boolean cloneNormalizer, String suffix, boolean replaceParam, List<Parameter> parameters,
			CloneIgnoreListModel ignoreList, boolean isClone, CloneIgnoreListModel usingParams, Session session)
			throws CloneNotSupportedException {
		try {
			if (!isClone && usingParams.isIgnore(action) == null) {
				return action;
			}

			Action actionToClone = action.clone();
			actionToClone.setGenerated(!isClone);

			// Find price components of action
			List<PriceComponent> priceComponents = this.findPCByAction(actionToClone, session);

			if (clonePC) {
				// Clone SortPriceComponent
				if (actionToClone.getSortPriceComponentId() != null) {
					SortPriceComponentDAO sortPriceComponentDAO = new SortPriceComponentDAO();

					SortPriceComponent sortPriceComponent = session.get(SortPriceComponent.class,
							actionToClone.getSortPriceComponentId());

					Long sortPriceComponentClonedId = ignoreList.isIgnore(sortPriceComponent);
					if (sortPriceComponentClonedId == null) {
						// Set SortPriceComponentId for action
						sortPriceComponentClonedId = sortPriceComponentDAO
								.cloneSPC(sortPriceComponent, cloneRateTable, cloneDT, cloneNormalizer, suffix,
										replaceParam, parameters, ignoreList, isClone, usingParams, session)
								.getSortPriceComponentId();
						ignoreList.getSortPCs().put(sortPriceComponent.getSortPriceComponentId(),
								sortPriceComponentClonedId);
					}
					actionToClone.setSortPriceComponentId(sortPriceComponentClonedId);
				}

				if (actionToClone.getDynamicReserveId() != null) {
					DynamicReserveDAO dynamicReserveDAO = new DynamicReserveDAO();

					// Clone DynamicReserve
					DynamicReserve dynamicReserve = session.get(DynamicReserve.class, actionToClone.getDynamicReserveId());
					Long dynamicReserveClonedId = ignoreList.isIgnore(dynamicReserve);
					if (dynamicReserveClonedId == null) {
						// Set DynamicReserve for action
						dynamicReserveClonedId = dynamicReserveDAO
								.cloneDR(dynamicReserve, cloneRateTable, cloneDT, cloneNormalizer, suffix, replaceParam,
										parameters, ignoreList, isClone, usingParams, session)
								.getDynamicReserveId();
					}
					actionToClone.setDynamicReserveId(dynamicReserveClonedId);
				}
			}
			// Save action
			actionToClone.setActionId(0L);
			actionToClone
					.setActionName(generateNameObject("actionName", actionToClone.getActionName() + suffix, 0, session));
			actionToClone.setIndex(this.getMaxIndex(session) + 1);
			session.save(actionToClone);

			// Level PriceComponent - 3
			for (PriceComponent priceComponent : priceComponents) {
				// Prepare map
				ActionPriceComponentMap actionPriceComponentMap = new ActionPriceComponentMap(actionToClone.getActionId(),
						priceComponent.getPriceComponentId(), Long.valueOf(priceComponents.indexOf(priceComponent)));
				actionPriceComponentMap.setDomainId(getDomainId());

				if (clonePC) {
					Long priceComponentClonedId = ignoreList.isIgnore(priceComponent);
					if (priceComponentClonedId == null) {
						PriceComponentDAO priceComponentDAO = new PriceComponentDAO();
						priceComponentClonedId = priceComponentDAO
								.clonePC(priceComponent, cloneBlock, cloneRateTable, cloneDT, cloneNormalizer, suffix,
										replaceParam, parameters, ignoreList, isClone, usingParams, session)
								.getPriceComponentId();
						ignoreList.getPriceComponents().put(priceComponent.getPriceComponentId(), priceComponentClonedId);
					}
					actionPriceComponentMap.setPriceComponentId(priceComponentClonedId);
				}
				// Save map
				session.save(actionPriceComponentMap);
			}

			return actionToClone;
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}		
	}

	public Action cloneAction(Action action, boolean clonePC, boolean cloneBlock, boolean cloneRateTable,
			boolean cloneDT, boolean cloneNormalizer, String suffix) throws Exception {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			Action actionCloned = this.cloneAction(action, clonePC, cloneBlock, cloneRateTable, cloneDT,
					cloneNormalizer, suffix, false, new ArrayList<Parameter>(), new CloneIgnoreListModel(), true,
					new CloneIgnoreListModel(), session);
			session.getTransaction().commit();
			return actionCloned;
		} catch (Exception e) {
			
			session.getTransaction().rollback();
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	@SuppressWarnings("unchecked")
	private List<PriceComponent> findPCByAction(Action action, Session session) {
		try {
			String hql = "SELECT pc FROM PriceComponent pc JOIN ActionPriceComponentMap map ON map.priceComponentId = pc.priceComponentId ";
			hql += " WHERE map.actionId = :actionId ORDER BY map.pcIndex";

			Query<PriceComponent> query = session.createQuery(hql);
			query.setParameter("actionId", action.getActionId());
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}		
	}

	@SuppressWarnings("unchecked")
	public List<PriceComponent> findPCByAction(Action action) {
		String hql = "SELECT pc FROM PriceComponent pc JOIN ActionPriceComponentMap map ON map.priceComponentId = pc.priceComponentId ";
		hql += " WHERE map.actionId = :actionId ORDER BY map.pcIndex";
		Session session = HibernateUtil.getOpenSession();
		try {
			Query<PriceComponent> query = session.createQuery(hql);
			query.setParameter("actionId", action.getActionId());
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public List<Action> findActionByPC(List<Long> lstPCId) {
		if (lstPCId.size() == 0)
			lstPCId.add(-1L);
		List<Action> lst = null;
		String hql = "SELECT DISTINCT ac FROM Action ac JOIN ActionPriceComponentMap map ON map.actionId = ac.actionId ";
		hql += " WHERE map.priceComponentId IN (:priceComponentId)";

		Session session = HibernateUtil.getOpenSession();
		try {

			Query query = session.createQuery(hql);
			query.setParameterList("priceComponentId", lstPCId);
			lst = query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}

		return lst;
	}

	public List<Action> findActionByDR(List<Long> lstDRId) {
		if (lstDRId.size() == 0)
			lstDRId.add(-1L);
		List<Action> lst = null;
		String hql = "SELECT DISTINCT ac FROM Action ac ";
		hql += " WHERE ac.dynamicReserveId IN (:dynamicReserveId)";

		Session session = HibernateUtil.getOpenSession();
		try {

			Query query = session.createQuery(hql);
			query.setParameterList("dynamicReserveId", lstDRId);
			lst = query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}

		return lst;
	}

	public List<Action> findActionBySPC(List<Long> lstSPCId) {
		if (lstSPCId.size() == 0)
			lstSPCId.add(-1L);
		List<Action> lst = null;
		String hql = "SELECT DISTINCT ac FROM Action ac ";
		hql += " WHERE ac.sortPriceComponentId IN (:sortPriceComponentId)";

		Session session = HibernateUtil.getOpenSession();
		try {

			Query query = session.createQuery(hql);
			query.setParameterList("sortPriceComponentId", lstSPCId);
			lst = query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}

		return lst;
	}

	public List<Action> findActionByListOffer(List<Long> lstOfferId) {
		if (lstOfferId.size() == 0)
			lstOfferId.add(-1L);
		List<Action> lst = null;
		String hql = "SELECT DISTINCT a FROM Action a JOIN OfferActionMap map ON map.actionId = a.actionId ";
		hql += " WHERE map.offerId IN (:offerId)";
		Session session = HibernateUtil.getOpenSession();
		try {
			Query query = session.createQuery(hql);
			query.setParameter("offerId", lstOfferId);
			lst = query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
		return lst;
	}

	public boolean checkSortInAction(Long sortPriceComponentId) {
		List<Action> lst = null;
		String[] cols = { "sortPriceComponentId" };
		Operator[] operators = { Operator.EQ };
		Object[] values = { sortPriceComponentId };
		lst = findByConditions(cols, operators, values, "");
		if (lst != null && lst.size() > 0) {
			return true;
		}
		return false;
	}

	public boolean checkDynamicInAction(Long dynamicReserveId) {
		List<Action> lst = null;
		String[] cols = { "dynamicReserveId" };
		Operator[] operators = { Operator.EQ };
		Object[] values = { dynamicReserveId };
		lst = findByConditions(cols, operators, values, "");
		if (lst != null && lst.size() > 0) {
			return true;
		}
		return false;
	}

	public boolean checkActionTypeInAction(Long actionType) {
		List<Action> lst = null;
		String[] cols = { "actionType" };
		Operator[] operators = { Operator.EQ };
		Object[] values = { actionType };
		lst = findByConditions(cols, operators, values, "");
		if (lst != null && lst.size() > 0) {
			return true;
		}
		return false;
	}

	public void deleteTreeAction(Action action, Offer offer, Session session) {
		try {
			action = session.get(Action.class, action.getActionId());
			if (action != null && action.isGenerated() != null && action.isGenerated()) {
				if (action.getDynamicReserveId() != null) {
					DynamicReserve dynamicReserve = session.get(DynamicReserve.class, action.getDynamicReserveId());
					if (dynamicReserve != null) {
						DynamicReserveDAO dynamicReserveDAO = new DynamicReserveDAO();
						dynamicReserveDAO.deleteDynamicReserve(dynamicReserve, session);
					}
				}

				if (action.getSortPriceComponentId() != null) {
					SortPriceComponent sortPC = session.get(SortPriceComponent.class, action.getSortPriceComponentId());
					if (sortPC != null) {
						SortPriceComponentDAO sortPcDAO = new SortPriceComponentDAO();
						sortPcDAO.deleteSortPcAndItems(sortPC, session);
					}
				}

				List<PriceComponent> priceComponents = this.findPCByAction(action, session);

				PriceComponentDAO pcDAO = new PriceComponentDAO();
				for (PriceComponent priceComponent : priceComponents) {
					pcDAO.deleteBlockByPC(priceComponent, session);
				}

				session.delete(action);
			} else {
				OfferActionMapDAO offerActionMapDAO = new OfferActionMapDAO();
				OfferActionMap offerActionMap = offerActionMapDAO.findOfferActionMap(offer.getOfferId(),
						action.getActionId());
				if (offerActionMap != null) {
					offerActionMapDAO.delete(offerActionMap);
				}
			}
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}		
	}

	public Long getMaxIndexActionPriceComponentMap(Session session) {
		try {
			StringBuffer hql = new StringBuffer("SELECT COALESCE(MAX(a.pcIndex), 0) FROM ActionPriceComponentMap a");
			hql.append(" WHERE a.domainId = :domainId");

			Query<Long> query = session.createQuery(hql.toString(), Long.class);
			query.setParameter("domainId", getDomainId());
			return query.getSingleResult();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}
	}

	public Long getMaxIndex(Session session) {
		try {
			StringBuffer hql = new StringBuffer("SELECT COALESCE(MAX(a.index), 0) FROM Action a");
			hql.append(" WHERE a.domainId = :domainId");

			Query<Long> query = session.createQuery(hql.toString(), Long.class);
			query.setParameter("domainId", getDomainId());
			return query.getSingleResult();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}		
	}

	public Long getMaxIndex() {
		Session session = HibernateUtil.getOpenSession();
		try {
			StringBuffer hql = new StringBuffer("SELECT COALESCE(MAX(a.index), 0) FROM Action a");
			hql.append(" WHERE a.domainId = :domainId");

			Query<Long> query = session.createQuery(hql.toString(), Long.class);
			query.setParameter("domainId", getDomainId());
			return query.getSingleResult();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public Boolean processMoveUpDownInCat(Action item, Boolean isUp) {
		boolean result = false;
		if (item != null && item.getActionId() > 0) {
			Session session = HibernateUtil.getOpenSession();
			item = get(item.getActionId());
			try {

				session.getTransaction().begin();

				StringBuffer hql = new StringBuffer("SELECT a FROM Action a");
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

				Query<Action> query = session.createQuery(hql.toString());
				query.setParameter("domainId", getDomainId());
				query.setParameter("index", item.getIndex());
				query.setParameter("categoryId", item.getCategoryId());
				query.setMaxResults(1);

				List<Action> actions = query.getResultList();
				if (actions != null && actions.size() > 0) {
					// if (!checkFieldIsExist("index", item.getIndex(), item)) {
					Action itemMove = actions.get(0);
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
				result = false;
			} finally {
				session.close();
			}
		}
		return result;
	}

	public Boolean checkFieldIsExist(String col, Object value, Action action) {
		boolean result = false;

		int count = 0;

		if (action == null || action.getActionId() == 0) {
			String[] column = { col };
			Operator[] ope = { Operator.EQ };
			Object[] val = { value };
			count = this.countByConditions(column, ope, val);
		} else {
			String[] column = { col, "actionId" };
			Operator[] ope = { Operator.EQ, Operator.NOTEQ };
			Object[] val = { value, action.getActionId() };
			count = this.countByConditions(column, ope, val);
		}

		if (count > 0) {
			result = true;
		}

		return result;
	}

	public List<Action> findByCategoryId(Long categoryId) {
		List<Action> lst = null;
		String[] cols = { "categoryId" };
		Operator[] operators = { Operator.EQ };
		Object[] values = { categoryId };
		lst = findByConditions(cols, operators, values, " index ASC");
		return lst;
	}

	public boolean checkName(Action action, boolean edit) {
		List<Action> lst = null;
		String[] cols = { "actionName", "domainId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { action.getActionName(), getDomainId() };
		lst = findByConditions(cols, operators, values, "");
		if (lst.size() > 0) {
			if (edit && lst.get(0).getActionId() == action.getActionId()) {
				return false;
			} else {
				return true;
			}
		}
		return false;

	}

	public int countAction() {
		String hql = "SELECT COUNT(a) FROM Action a WHERE a.domainId =:domainId";
		Session session = HibernateUtil.getOpenSession();
		try {
			Query<Long> query = session.createQuery(hql, Long.class);
			query.setParameter("domainId", getDomainId());
			return query.getSingleResult().intValue();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public boolean processMoveUpDownInOffer(Action item, Offer offer, boolean isUp) {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			List<OfferActionMap> actionMaps = this.findObjectMapAround(item, offer, isUp, session);
			if (actionMaps.size() < 2) {
				return true;
			} else {
				Long index = actionMaps.get(0).getMapIndex();
				actionMaps.get(0).setMapIndex(actionMaps.get(1).getMapIndex());
				actionMaps.get(1).setMapIndex(index);
				session.update(actionMaps.get(0));
				session.update(actionMaps.get(1));
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

	private List<OfferActionMap> findObjectMapAround(Action item, Offer offer, boolean isUp, Session session) {
		try {
			StringBuffer hql = new StringBuffer("SELECT a FROM OfferActionMap a WHERE a.mapIndex " + (isUp ? "<=" : ">=")
					+ " (SELECT b.mapIndex FROM OfferActionMap b");
			hql.append(" WHERE b.offerId = :offerId  AND b.actionId = :actionId) AND a.offerId = :offerId");
			Query<OfferActionMap> query = session.createQuery(hql.toString(), OfferActionMap.class);

			query.setParameter("offerId", offer.getOfferId());
			query.setParameter("actionId", item.getActionId());
			query.setMaxResults(2);
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}		
	}
	public boolean moveToCate(Action action) {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			action.setDomainId(getDomainId());
			generateIndexByCat(action, "actionId", session);
			session.saveOrUpdate(action);
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
	
	public boolean checkReserveInfoInAction(Long reserveinfoId) {
		List<Action> lst = null;
		String[] cols = { "reserveinfoId", "domainId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { reserveinfoId, getDomainId() };
		lst = findByConditions(cols, operators, values, "");
		if (lst != null && lst.size() > 0) {
			return true;
		}
		return false;
	}
	
	public boolean checkPriorityFilterInAction(Long priorityFilterId) {
		List<Action> lst = null;
		String[] cols = { "priorityFilterId", "domainId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { priorityFilterId, getDomainId() };
		lst = findByConditions(cols, operators, values, "");
		if (lst != null && lst.size() > 0) {
			return true;
		}
		return false;
	}


}

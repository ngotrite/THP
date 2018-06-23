package com.viettel.ocs.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.DynamicReserve;
import com.viettel.ocs.entity.Normalizer;
import com.viettel.ocs.entity.Parameter;
import com.viettel.ocs.entity.RateTable;
import com.viettel.ocs.entity.SortPriceComponent;
import com.viettel.ocs.entity.SortPriceRateTableMap;
import com.viettel.ocs.entity.UnitType;
import com.viettel.ocs.model.CloneIgnoreListModel;
import com.viettel.ocs.model.SortPriceRateTableMapModal;

public class SortPriceComponentDAO extends BaseDAO<SortPriceComponent> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -962543927847073170L;

	@Override
	protected Class<SortPriceComponent> getEntityClass() {

		return SortPriceComponent.class;
	}

	public List<RateTable> findRatetableBySPC(long sortPriceComponentId) {
		List<RateTable> lst = null;
		String hql = "SELECT rt FROM RateTable rt JOIN SortPriceRateTableMap map ON map.rateTableId = rt.rateTableId ";
		hql += " WHERE map.sortPriceComponentId = :sortPriceComponentId";
		hql += " AND map.domainId=:domainId ORDER BY map.rateTableIndex ASC";
		Session session = HibernateUtil.getOpenSession();
		try {
			Query<RateTable> query = session.createQuery(hql);
			query.setParameter("sortPriceComponentId", sortPriceComponentId);
			query.setParameter("domainId", getDomainId());
			lst = query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}

		return lst;
	}

	private List<SortPriceRateTableMapModal> findSortPriceRateTableMapBySPC(SortPriceComponent sortPriceComponent,
			Session session) {
		try {

			String hql = "SELECT new com.viettel.ocs.model.SortPriceRateTableMapModal(b, a)  FROM RateTable a JOIN SortPriceRateTableMap b ON b.rateTableId = a.rateTableId ";
			hql += " WHERE b.sortPriceComponentId = :sortPriceComponentId";
			Query<SortPriceRateTableMapModal> query = session.createQuery(hql, SortPriceRateTableMapModal.class);
			query.setParameter("sortPriceComponentId", sortPriceComponent.getSortPriceComponentId());
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}
	}

	public List<SortPriceComponent> findSortPriceComponentByCatId(Long categoryId) {
		List<SortPriceComponent> lst = null;
		String[] cols = { "categoryId" };
		Operator[] operators = { Operator.EQ };
		Object[] values = { categoryId };
		lst = findByConditions(cols, operators, values, "index");
		return lst;
	}

	public List<SortPriceComponent> findSortPriceComponentByCategoryId(List<Long> lstCatID) {
		String[] col = { "categoryId" };
		Operator[] ope = { Operator.IN };
		List<Long> lstCatIDSortPC = new ArrayList<Long>();
		lstCatIDSortPC.addAll(lstCatID);
		if (lstCatIDSortPC.size() == 0)
			lstCatIDSortPC.add(-1L);
		Object[] val = { lstCatIDSortPC };
		String oder = " index ASC";
		return this.findByConditions(col, ope, val, oder);
	}

	public List<SortPriceComponent> findSortPriceComponentByListRT(List<Long> lstRatetableId) {
		if (lstRatetableId.size() == 0)
			lstRatetableId.add(-1L);
		List<SortPriceComponent> lst = null;
		String hql = "SELECT DISTINCT spc FROM SortPriceComponent spc JOIN SortPriceRateTableMap map ON map.sortPriceComponentId = spc.sortPriceComponentId ";
		hql += " WHERE map.rateTableId IN (:rateTableId)";

		Session session = HibernateUtil.getOpenSession();
		try {
			Query query = session.createQuery(hql);
			query.setParameter("rateTableId", lstRatetableId);
			lst = query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
		return lst;
	}

	public List<SortPriceComponent> findSPCByListAction(List<Long> lstActionId) {
		if (lstActionId.size() == 0)
			lstActionId.add(-1L);
		List<SortPriceComponent> lst = null;
		String hql = "SELECT DISTINCT spc FROM SortPriceComponent spc JOIN Action a ON a.sortPriceComponentId = spc.sortPriceComponentId";
		hql += " WHERE a.actionId IN (:actionId)";

		Session session = HibernateUtil.getOpenSession();
		try {
			Query query = session.createQuery(hql);
			query.setParameter("actionId", lstActionId);
			lst = query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
		return lst;
	}

	public void saveSortPrice(SortPriceComponent sortPriceComponent, List<RateTable> listNew, List<RateTable> listOld) {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {

			if (sortPriceComponent.getSortPriceComponentId() == 0L) {
				sortPriceComponent.setDomainId(getDomainId());
			}
			generateIndexByCat(sortPriceComponent, "sortPriceComponentId", session);
			session.saveOrUpdate(sortPriceComponent);
			List<Long> ids = new ArrayList<Long>();
			for (RateTable rateTable : listOld) {
				if (rateTable.getRateTableId() > 0) {
					ids.add(rateTable.getRateTableId());
				}
			}
			if (!ids.isEmpty()) {
				// If list old has data -> del list old in map -> add new all
				StringBuilder hql = new StringBuilder(
						"DELETE FROM SortPriceRateTableMap WHERE sortPriceComponentId = :sortPriceComponentId");
				hql.append(" AND rateTableId IN (:listOld)");
				hql.append(" AND domainId =:domainId");

				// Delete all map
				Query<SortPriceRateTableMap> query = session.createQuery(hql.toString());
				query.setParameterList("listOld", ids);
				query.setParameter("sortPriceComponentId", sortPriceComponent.getSortPriceComponentId());
				query.setParameter("domainId", getDomainId());
				query.executeUpdate();
			}

			// If list old no data -> only add new
			if (!listNew.isEmpty()) {
				for (RateTable rateTable : listNew) {
					SortPriceRateTableMapDAO mapDAO = new SortPriceRateTableMapDAO();
					SortPriceRateTableMap map = new SortPriceRateTableMap();
					map.setSortPriceComponentId(sortPriceComponent.getSortPriceComponentId());
					map.setRateTableId(rateTable.getRateTableId());
					Long index = (long) listNew.indexOf(rateTable) + 1;
					map.setDomainId(getDomainId());
					map.setRateTableIndex(index);
					session.saveOrUpdate(map);
				}
			}
			session.getTransaction().commit();
		} catch (Exception e) {
			session.getTransaction().rollback();
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}

	}

	public boolean checkName(SortPriceComponent sortPriceComponent, boolean edit) {
		List<SortPriceComponent> lst = null;
		String[] cols = { "sortPriceComponentName" };
		Operator[] operators = { Operator.EQ };
		Object[] values = { sortPriceComponent.getSortPriceComponentName() };
		lst = findByConditions(cols, operators, values, "");
		if (lst.size() > 0) {
			if (edit && lst.get(0).getSortPriceComponentId() == sortPriceComponent.getSortPriceComponentId()) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

	public SortPriceComponent findSortPriceComponentIndexLT(Long index) {
		Session session = HibernateUtil.getOpenSession();
		try {

			StringBuffer hql = new StringBuffer("SELECT a FROM SortPriceComponent a");
			hql.append(" WHERE a.domainId = :domainId");
			hql.append(" AND a.index < :index");
			hql.append(" ORDER BY a.index DESC");

			Query<SortPriceComponent> query = session.createQuery(hql.toString());
			query.setParameter("domainId", getDomainId());
			query.setParameter("index", index);

			List<SortPriceComponent> sortPriceComponents = query.getResultList();
			if (sortPriceComponents != null && sortPriceComponents.size() > 0) {
				return sortPriceComponents.get(0);
			}
			return null;
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public SortPriceComponent findSortPriceComponentIndexGT(Long index) {
		Session session = HibernateUtil.getOpenSession();
		try {

			StringBuffer hql = new StringBuffer("SELECT a FROM SortPriceComponent a");
			hql.append(" WHERE a.domainId = :domainId");
			hql.append(" AND a.index > :index");
			hql.append(" ORDER BY a.index DESC");

			Query<SortPriceComponent> query = session.createQuery(hql.toString());
			query.setParameter("domainId", getDomainId());
			query.setParameter("index", index);

			List<SortPriceComponent> sortPriceComponents = query.getResultList();
			if (sortPriceComponents != null && sortPriceComponents.size() > 0) {
				return sortPriceComponents.get(0);
			}
			return null;
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public Long getMaxIndex(Session session) {
		try {

			StringBuffer hql = new StringBuffer("SELECT COALESCE(MAX(a.index), 0) FROM SortPriceComponent a");
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
			StringBuffer hql = new StringBuffer("SELECT COALESCE(MAX(a.index), 0) FROM SortPriceComponent a");
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

	public Boolean processMoveUpDown(SortPriceComponent item, Boolean isUp) {
		boolean result = false;
		if (item != null && item.getSortPriceComponentId() > 0) {
			Session session = HibernateUtil.getOpenSession();
			item = get(item.getSortPriceComponentId());
			try {
				session.getTransaction().begin();
				StringBuffer hql = new StringBuffer("SELECT a FROM SortPriceComponent a");
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

				Query<SortPriceComponent> query = session.createQuery(hql.toString());
				query.setParameter("domainId", getDomainId());
				query.setParameter("index", item.getIndex());
				query.setParameter("categoryId", item.getCategoryId());
				query.setMaxResults(1);

				List<SortPriceComponent> sortPriceComponents = query.getResultList();
				if (sortPriceComponents != null && sortPriceComponents.size() > 0) {
					// if (!checkFieldIsExist("index", item.getIndex(), item)) {
					SortPriceComponent itemMove = sortPriceComponents.get(0);
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

	public Boolean checkFieldIsExist(String col, Object value, SortPriceComponent sortPriceComponent) {
		boolean result = false;

		int count = 0;

		if (sortPriceComponent == null || sortPriceComponent.getSortPriceComponentId() == 0) {
			String[] column = { col };
			Operator[] ope = { Operator.EQ };
			Object[] val = { value };
			count = this.countByConditions(column, ope, val);
		} else {
			String[] column = { col, "sortPriceComponentId" };
			Operator[] ope = { Operator.EQ, Operator.NOTEQ };
			Object[] val = { value, sortPriceComponent.getSortPriceComponentId() };
			count = this.countByConditions(column, ope, val);
		}

		if (count > 0) {
			result = true;
		}

		return result;
	}

	public SortPriceComponent cloneSPC(SortPriceComponent sortPriceComponent, boolean cloneRateTable, boolean cloneDT,
			boolean cloneNormalizer, String suffix, boolean replaceParam, List<Parameter> parameters,
			CloneIgnoreListModel ignoreList, boolean isClone, CloneIgnoreListModel usingParams, Session session)
			throws CloneNotSupportedException {
		try {

			if (!isClone && usingParams.isIgnore(sortPriceComponent) == null) {
				return sortPriceComponent;
			}

			SortPriceComponent sortPriceComponentToClone = sortPriceComponent.clone();

			sortPriceComponentToClone.setGenerated(!isClone);

			List<SortPriceRateTableMapModal> sortPriceRateTableMapModals = this
					.findSortPriceRateTableMapBySPC(sortPriceComponentToClone, session);

			sortPriceComponentToClone.setSortPriceComponentId(0L);
			sortPriceComponentToClone.setSortPriceComponentName(generateNameObject("sortPriceComponentName",
					sortPriceComponentToClone.getSortPriceComponentName() + suffix, 0, session));
			sortPriceComponentToClone.setIndex(this.getMaxIndex(session) + 1);
			session.save(sortPriceComponentToClone);

			// Level RateTable - 5
			for (SortPriceRateTableMapModal sortPriceRateTableMapModal : sortPriceRateTableMapModals) {
				// Prepare map
				SortPriceRateTableMap sortPriceRateTableMap = sortPriceRateTableMapModal.getSortPriceRateTableMap().clone();
				sortPriceRateTableMap.setSortPriceRateTableMapId(0l);
				sortPriceRateTableMap.setSortPriceComponentId(sortPriceComponentToClone.getSortPriceComponentId());

				if (cloneRateTable) {
					RateTable rateTable = sortPriceRateTableMapModal.getRateTable();

					Long rateTableClonedId = ignoreList.isIgnore(rateTable);
					if (rateTableClonedId == null) {
						RateTableDAO rateTableDAO = new RateTableDAO();
						rateTableClonedId = rateTableDAO.cloneRateTable(rateTable, cloneDT, cloneNormalizer, suffix,
								replaceParam, parameters, ignoreList, isClone, usingParams, session).getRateTableId();
						ignoreList.getRateTables().put(rateTable.getRateTableId(), rateTableClonedId);
					}
					sortPriceRateTableMap.setRateTableId(rateTableClonedId);
				}
				// Save map
				session.save(sortPriceRateTableMap);
			}
			return sortPriceComponentToClone;
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}
	}

	public SortPriceComponent cloneSPC(SortPriceComponent priceComponent, boolean cloneRateTable, boolean cloneDT,
			boolean cloneNormalizer, String suffix) throws Exception {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			SortPriceComponent sortPriceComponentCloned = this.cloneSPC(priceComponent, cloneRateTable, cloneDT,
					cloneNormalizer, suffix, false, new ArrayList<Parameter>(), new CloneIgnoreListModel(), true,
					new CloneIgnoreListModel(), session);
			session.getTransaction().commit();
			return sortPriceComponentCloned;
		} catch (Exception e) {
			
			session.getTransaction().rollback();
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public int countSortPriceComponent() {
		String hql = "SELECT COUNT(a) FROM SortPriceComponent a WHERE a.domainId =:domainId";
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

	public void deleteSortPcAndItems(SortPriceComponent sortPC, Session session) {
		try {

			sortPC = session.get(SortPriceComponent.class, sortPC.getSortPriceComponentId());
			if (sortPC != null && sortPC.isGenerated() != null && sortPC.isGenerated()) {
				RateTableDAO rateTableDAO = new RateTableDAO();
				List<RateTable> rateTables = rateTableDAO.findRateTableBySPC(sortPC.getSortPriceComponentId());
				for (RateTable rateTable : rateTables) {
					rateTableDAO.deleteFormulasAndDTByRT(rateTable, session);
				}
				session.delete(sortPC);
			}
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}
	}

	public List<SortPriceComponent> findSortPriceComponentByCategoryIdAndNotGened(List<Long> lstAllCatID) {
		String hql = "SELECT a FROM SortPriceComponent a";
		hql += " WHERE a.categoryId IN (:categoryIds) AND (a.generated is false OR a.generated is NULL)";

		Session session = HibernateUtil.getOpenSession();
		try {

			Query<SortPriceComponent> query = session.createQuery(hql, SortPriceComponent.class);
			query.setParameterList("categoryIds", lstAllCatID);
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}
	
	public boolean moveToCate(SortPriceComponent sortPriceComponent) {
		Session session = HibernateUtil.getOpenSession();
		
		try {
			session.getTransaction().begin();
			sortPriceComponent.setDomainId(getDomainId());
			generateIndexByCat(sortPriceComponent, "sortPriceComponentId", session);
			session.saveOrUpdate(sortPriceComponent);
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

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
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.DynamicReserve;
import com.viettel.ocs.entity.DynamicReserveRateTableMap;
import com.viettel.ocs.entity.Normalizer;
import com.viettel.ocs.entity.Parameter;
import com.viettel.ocs.entity.PriceComponent;
import com.viettel.ocs.entity.RateTable;
import com.viettel.ocs.entity.SortPriceComponent;
import com.viettel.ocs.entity.SortPriceRateTableMap;
import com.viettel.ocs.entity.UnitType;
import com.viettel.ocs.model.CloneIgnoreListModel;
import com.viettel.ocs.model.DynamicReserveRateTableMapModel;
import com.viettel.ocs.model.SortPriceRateTableMapModal;

public class DynamicReserveDAO extends BaseDAO<DynamicReserve> implements Serializable {
	/**
	 * categoryId domainId
	 */

	public List<DynamicReserve> findDRByCategoryId(Long categoryId) {

		List<DynamicReserve> lst = null;
		String[] cols = { "categoryId", "domainId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { categoryId, getDomainId() };
		String oder = "posIndex ASC";
		lst = findByConditions(cols, operators, values, oder);
		return lst;

	}

	public List<DynamicReserve> findDynamicReservetByCategoryId(List<Long> lstCatID) {

		String[] col = { "categoryId" };
		Operator[] ope = { Operator.IN };
		List<Long> lstCatIDParam = new ArrayList<Long>();
		lstCatIDParam.addAll(lstCatID);
		if (lstCatIDParam.size() == 0)
			lstCatIDParam.add(-1L);
		Object[] val = { lstCatIDParam };
		String oder = "posIndex ASC";
		return this.findByConditions(col, ope, val, oder);
	}

	public List<DynamicReserve> findDynamicReserveByCategoryId(List<Long> lstCatID) {
		String[] col = { "categoryId" };
		Operator[] ope = { Operator.IN };
		List<Long> lstCatIDParam = new ArrayList<Long>();
		lstCatIDParam.addAll(lstCatID);
		if (lstCatIDParam.size() == 0)
			lstCatIDParam.add(-1L);
		Object[] val = { lstCatIDParam };
		String oder = "dynamicReserveName";
		return this.findByConditions(col, ope, val, oder);
	}

	public List<DynamicReserve> findDRByListRT(List<Long> lstRatetableId) {
		if (lstRatetableId.size() == 0)
			lstRatetableId.add(-1L);
		List<DynamicReserve> lst = null;
		String hql = "SELECT DISTINCT dr FROM DynamicReserve dr JOIN DynamicReserveRateTableMap map ON map.dynamicReserveId = dr.dynamicReserveId ";
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

	public List<DynamicReserve> findDRByListAction(List<Long> lstActionId) {
		if (lstActionId.size() == 0)
			lstActionId.add(-1L);
		List<DynamicReserve> lst = null;
		String hql = "SELECT DISTINCT dr FROM DynamicReserve dr JOIN Action a ON a.dynamicReserveId = dr.dynamicReserveId";
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

	public List<DynamicReserve> findDynamicReserveByCategoryId(Category category) {
		List<Long> categoryIds = new ArrayList<>();
		categoryIds.add(category.getCategoryId());
		return findDynamicReserveByCategoryId(categoryIds);
	}

	public boolean saveDynamicReserveDetail(DynamicReserve dynamicReserve, List<RateTable> rateTables) {
		List<Long> rateTableIds = new ArrayList<Long>();
		rateTableIds.add(-1L);

		for (RateTable rateTable : rateTables) {
			rateTableIds.add(rateTable.getRateTableId());
		}

		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {

			// SAVE DYNAMIC RESERVE
			dynamicReserve.setDomainId(getDomainId());
			generateIndexByCat(dynamicReserve, "posIndex", "dynamicReserveId", session);
			session.saveOrUpdate(dynamicReserve);

			// DELETE DYNAMIC RESERVE RATETABLE MAP
			this.deleteDynamicReserveRateTableMapOfDynamicReserveId(rateTableIds, dynamicReserve, session);

			// SAVE OR UPDATE RATETABLE MAP
			for (int i = 0; i < rateTables.size(); i++) {
				DynamicReserveRateTableMap dynamicReserveRateTableMap = this
						.findDynamicReserveRateTableMapByRelations(dynamicReserve, rateTables.get(i), session);
				dynamicReserveRateTableMap.setDomainId(getDomainId());
				dynamicReserveRateTableMap.setRateTableIndex(Long.valueOf(i));
				session.saveOrUpdate(dynamicReserveRateTableMap);
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

	private void deleteDynamicReserveRateTableMapOfDynamicReserveId(List<Long> rateTableIds, DynamicReserve reserve,
			Session session) {
		StringBuffer hql = new StringBuffer("DELETE FROM DynamicReserveRateTableMap a");
		hql.append(" WHERE a.rateTableId NOT IN (:rateTableIds)");
		hql.append(" AND a.dynamicReserveId = :dynamicReserveId");
		hql.append(" AND a.domainId = :domainId");
		try {
			Query<ActionPriceComponentMap> query = session.createQuery(hql.toString());
			query.setParameterList("rateTableIds", rateTableIds);
			query.setParameter("dynamicReserveId", reserve.getDynamicReserveId());
			query.setParameter("domainId", getDomainId());
			query.executeUpdate();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}		
	}

	private DynamicReserveRateTableMap findDynamicReserveRateTableMapByRelations(DynamicReserve reserve,
			RateTable rateTable, Session session) {
		StringBuffer hql = new StringBuffer("SELECT a FROM DynamicReserveRateTableMap a");
		hql.append(" WHERE a.rateTableId = :rateTableId");
		hql.append(" AND a.dynamicReserveId = :dynamicReserveId");
		hql.append(" AND a.domainId = :domainId");
		try {
			Query<DynamicReserveRateTableMap> query = session.createQuery(hql.toString());
			query.setParameter("dynamicReserveId", reserve.getDynamicReserveId());
			query.setParameter("rateTableId", rateTable.getRateTableId());
			query.setParameter("domainId", getDomainId());

			List<DynamicReserveRateTableMap> dynamicReserveRateTableMaps = query.getResultList();

			if (dynamicReserveRateTableMaps != null && dynamicReserveRateTableMaps.size() > 0) {
				return dynamicReserveRateTableMaps.get(0);
			}
			DynamicReserveRateTableMap dynamicReserveRateTableMap = new DynamicReserveRateTableMap(
					rateTable.getRateTableId(), reserve.getDynamicReserveId());
			return dynamicReserveRateTableMap;
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}		
	}

	public DynamicReserve findDynamicReserveLastIndex() {
		Session session = HibernateUtil.getOpenSession();
		try {

			StringBuffer hql = new StringBuffer("SELECT a FROM DynamicReserve a");
			hql.append(" WHERE a.domainId = :domainId");
			hql.append(" ORDER BY a.posIndex DESC");

			Query<DynamicReserve> query = session.createQuery(hql.toString());
			query.setParameter("domainId", getDomainId());

			List<DynamicReserve> dynamicReserves = query.getResultList();
			if (dynamicReserves != null && dynamicReserves.size() > 0) {
				return dynamicReserves.get(0);
			}
			return null;
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public DynamicReserve findDynamicReserveIndexGT(Long index, Long categoryId) {
		Session session = HibernateUtil.getOpenSession();
		try {

			StringBuffer hql = new StringBuffer("SELECT a FROM DynamicReserve a");
			hql.append(" WHERE a.domainId = :domainId");
			hql.append(" AND a.posIndex > :posIndex");
			hql.append(" AND a.categoryId = :categoryId");
			hql.append(" ORDER BY a.posIndex ASC");

			Query<DynamicReserve> query = session.createQuery(hql.toString());
			query.setParameter("domainId", getDomainId());
			query.setParameter("posIndex", index);
			query.setParameter("categoryId", categoryId);

			List<DynamicReserve> dynamicReserves = query.getResultList();
			if (dynamicReserves != null && dynamicReserves.size() > 0) {
				return dynamicReserves.get(0);
			}
			return null;
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public DynamicReserve findDynamicReserveIndexLT(Long index, Long categoryId) {
		Session session = HibernateUtil.getOpenSession();
		try {
			StringBuffer hql = new StringBuffer("SELECT a FROM DynamicReserve a");
			hql.append(" WHERE a.domainId = :domainId");
			hql.append(" AND a.posIndex < :posIndex");
			hql.append(" AND a.categoryId = :categoryId");
			hql.append(" ORDER BY a.posIndex DESC");

			Query<DynamicReserve> query = session.createQuery(hql.toString());
			query.setParameter("domainId", getDomainId());
			query.setParameter("posIndex", index);
			query.setParameter("categoryId", categoryId);

			List<DynamicReserve> dynamicReserves = query.getResultList();
			if (dynamicReserves != null && dynamicReserves.size() > 0) {
				return dynamicReserves.get(0);
			}
			return null;
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public Boolean processMoveUpDown(DynamicReserve moveItem, DynamicReserve item) {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
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

	public boolean checkName(DynamicReserve dynamicReserve) {
		String hql = "SELECT COUNT(*) FROM DynamicReserve a WHERE a.dynamicReserveName LIKE :dynamicReserveName AND a.dynamicReserveId != :dynamicReserveId";
		hql += " AND a.domainId=:domainId";
		Session session = HibernateUtil.getOpenSession();
		try {

			Query<Long> query = session.createQuery(hql);
			query.setParameter("dynamicReserveName", dynamicReserve.getDynamicReserveName());
			query.setParameter("dynamicReserveId", dynamicReserve.getDynamicReserveId());
			query.setParameter("domainId", getDomainId());
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

	public boolean deleteDynamicByAction(DynamicReserve dynamicReserve) {
		String hql = "DELETE FROM DynamicReserve map";
		hql += " WHERE map.dynamicReserveId = :dynamicReserveId";
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			session.delete(dynamicReserve);
			Query<DynamicReserve> query = session.createQuery(hql);
			query.setParameter("dynamicReserveId", dynamicReserve.getDynamicReserveId());
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

	@Override
	protected Class<DynamicReserve> getEntityClass() {
		return DynamicReserve.class;
	}

	public DynamicReserve cloneDR(DynamicReserve dynamicReserve, boolean cloneRateTable, boolean cloneDT,
			boolean cloneNormalizer, String suffix, boolean replaceParam, List<Parameter> parameters,
			CloneIgnoreListModel ignoreList, boolean isClone, CloneIgnoreListModel usingParams, Session session)
			throws CloneNotSupportedException {
		try {
			if (!isClone && usingParams.isIgnore(dynamicReserve) == null) {
				return dynamicReserve;
			}

			DynamicReserve dynamicReserveToClone = dynamicReserve.clone();
			dynamicReserveToClone.setGenerated(!isClone);

			List<DynamicReserveRateTableMapModel> reserveRateTableMapModels = this
					.findDynamicReserveRateTableMapByDR(dynamicReserveToClone, session);

			dynamicReserveToClone.setDynamicReserveId(0L);
			dynamicReserveToClone.setDynamicReserveName(generateNameObject("dynamicReserveName",
					dynamicReserveToClone.getDynamicReserveName() + suffix, 0, session));
			DynamicReserve lastIndex = findDynamicReserveLastIndex();
			if (lastIndex != null) {
				dynamicReserveToClone.setPosIndex(lastIndex.getPosIndex() + 1);
			} else {
				dynamicReserveToClone.setPosIndex(1l);
			}
			session.save(dynamicReserveToClone);

			for (DynamicReserveRateTableMapModel reserveRateTableMapModel : reserveRateTableMapModels) {
				DynamicReserveRateTableMap dynamicReserveRateTableMap = reserveRateTableMapModel
						.getDynamicReserveRateTableMap().clone();
				dynamicReserveRateTableMap.setDynamicRateMapId(0L);
				dynamicReserveRateTableMap.setDynamicReserveId(dynamicReserveToClone.getDynamicReserveId());

				if (cloneRateTable) {
					RateTable rateTable = reserveRateTableMapModel.getRateTable();

					Long rateTableClonedId = ignoreList.isIgnore(rateTable);
					if (rateTableClonedId == null) {
						RateTableDAO rateTableDAO = new RateTableDAO();
						rateTableClonedId = rateTableDAO.cloneRateTable(rateTable, cloneDT, cloneNormalizer, suffix,
								replaceParam, parameters, ignoreList, isClone, usingParams, session).getRateTableId();
						ignoreList.getRateTables().put(rateTable.getRateTableId(), rateTableClonedId);
					}

					dynamicReserveRateTableMap.setRateTableId(rateTableClonedId);
				}

				session.save(dynamicReserveRateTableMap);
			}

			return dynamicReserveToClone;
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}		
	}

	public DynamicReserve cloneDR(DynamicReserve dynamicReserve, boolean cloneRateTable, boolean cloneDT,
			boolean cloneNormalizer, String suffix) throws Exception {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			DynamicReserve dynamicReserveCloned = this.cloneDR(dynamicReserve, cloneRateTable, cloneDT, cloneNormalizer,
					suffix, false, new ArrayList<Parameter>(), new CloneIgnoreListModel(), true,
					new CloneIgnoreListModel(), session);
			session.getTransaction().commit();
			return dynamicReserveCloned;
		} catch (Exception e) {
			
			session.getTransaction().rollback();
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	private List<DynamicReserveRateTableMapModel> findDynamicReserveRateTableMapByDR(
			DynamicReserve dynamicReserveToClone, Session session) {
		StringBuffer hql = new StringBuffer(
				"SELECT new com.viettel.ocs.model.DynamicReserveRateTableMapModel(a, b) FROM DynamicReserveRateTableMap a");
		hql.append(" JOIN RateTable b ON b.rateTableId = a.rateTableId");
		hql.append(" WHERE a.dynamicReserveId = :dynamicReserveId");
		try {
			Query<DynamicReserveRateTableMapModel> query = session.createQuery(hql.toString(),
					DynamicReserveRateTableMapModel.class);
			query.setParameter("dynamicReserveId", dynamicReserveToClone.getDynamicReserveId());
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}		
	}

	public int countDynamicReserve() {
		String hql = "SELECT COUNT(a) FROM DynamicReserve a WHERE a.domainId =:domainId";
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

	public Boolean moveUpDownDynamicReserve(DynamicReserve item, Boolean isUp) {
		boolean result = false;
		if (item != null && item.getDynamicReserveId() > 0) {
			Session session = HibernateUtil.getOpenSession();
			item = get(item.getDynamicReserveId());
			try {

				session.getTransaction().begin();

				StringBuffer hql = new StringBuffer("SELECT a FROM DynamicReserve a");
				hql.append(" WHERE a.domainId = :domainId");

				if (isUp) {
					hql.append(" AND a.posIndex < :posIndex");
				} else {
					hql.append(" AND a.posIndex > :posIndex");
				}

				hql.append(" AND a.categoryId = :categoryId");

				if (isUp) {
					hql.append(" ORDER BY a.posIndex DESC");
				} else {
					hql.append(" ORDER BY a.posIndex ASC");
				}

				Query<DynamicReserve> query = session.createQuery(hql.toString());
				query.setParameter("domainId", getDomainId());
				query.setParameter("posIndex", item.getPosIndex());
				query.setParameter("categoryId", item.getCategoryId());
				query.setMaxResults(1);

				List<DynamicReserve> dynamicReserves = query.getResultList();
				if (dynamicReserves != null && dynamicReserves.size() > 0) {
					// if(!checkFieldIsExist("posIndex", item.getPosIndex(),
					// item)){
					DynamicReserve itemMove = dynamicReserves.get(0);
					Long tmpIndex = 0l;
					tmpIndex = itemMove.getPosIndex();
					itemMove.setPosIndex(item.getPosIndex());
					item.setPosIndex(tmpIndex);
					session.update(itemMove);
					session.update(item);
					result = true;
					// } else {result = false;}
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

	public Boolean checkFieldIsExist(String col, Object value, DynamicReserve item) {
		boolean result = false;

		int count = 0;

		if (item == null || item.getDynamicReserveId() == 0) {
			String[] column = { col };
			Operator[] ope = { Operator.EQ };
			Object[] val = { value };
			count = this.countByConditions(column, ope, val);
		} else {
			String[] column = { col, "dynamicReserveId" };
			Operator[] ope = { Operator.EQ, Operator.NOTEQ };
			Object[] val = { value, item.getDynamicReserveId() };
			count = this.countByConditions(column, ope, val);
		}

		if (count > 0) {
			result = true;
		}

		return result;
	}

	public void deleteDynamicReserve(DynamicReserve dynamicReserve, Session session) {
		try {
			dynamicReserve = session.get(DynamicReserve.class, dynamicReserve.getDynamicReserveId());
			if (dynamicReserve != null && dynamicReserve.isGenerated() != null && dynamicReserve.isGenerated()) {
				RateTableDAO rateTableDAO = new RateTableDAO();
				List<RateTable> rateTables = rateTableDAO
						.findRateTableByDynamicReserve(dynamicReserve.getDynamicReserveId());
				for (RateTable rateTable : rateTables) {
					rateTableDAO.deleteFormulasAndDTByRT(rateTable, session);
				}
				session.delete(dynamicReserve);
			}
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}		
	}

	public List<DynamicReserve> findDynamicReservetByCategoryIdAndNotGened(List<Long> lstAllCatID) {
		String hql = "SELECT a FROM DynamicReserve a";
		hql += " WHERE a.categoryId IN (:categoryIds) AND (a.generated is false OR a.generated is NULL)";

		Session session = HibernateUtil.getOpenSession();
		try {

			Query<DynamicReserve> query = session.createQuery(hql, DynamicReserve.class);
			query.setParameterList("categoryIds", lstAllCatID);
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}
	
	public boolean moveToCate(DynamicReserve dynamicReserve) {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			dynamicReserve.setDomainId(getDomainId());
			generateIndexByCat(dynamicReserve,"posIndex","dynamicReserveId", session);
			session.saveOrUpdate(dynamicReserve);
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

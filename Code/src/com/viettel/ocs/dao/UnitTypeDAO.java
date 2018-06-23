package com.viettel.ocs.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.BalType;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.DynamicReserve;
import com.viettel.ocs.entity.Normalizer;
import com.viettel.ocs.entity.Parameter;
import com.viettel.ocs.entity.RateTable;
import com.viettel.ocs.entity.StateType;
import com.viettel.ocs.entity.UnitType;

public class UnitTypeDAO extends BaseDAO<UnitType> implements Serializable {

	public List<UnitType> findUTByCategoryId(Long categoryId) {

		List<UnitType> lst = null;
		String[] cols = { "categoryId", "domainId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { categoryId, getDomainId() };
		String oder = "index ASC";
		lst = findByConditions(cols, operators, values, oder);
		return lst;

	}

	public List<UnitType> findUnitTypeByCatId(List<Long> lstCatId) {
		List<UnitType> lstUnitType = null;		
		String[] cols = { "categoryId", "domainId" };
		Operator[] operators = { Operator.IN, Operator.EQ };
		List<Long> lstCatIDParam = new ArrayList<Long>();
		lstCatIDParam.addAll(lstCatId);
		if (lstCatIDParam.size() == 0)
			lstCatIDParam.add(-1L);
		Object[] values = { lstCatIDParam, getDomainId() };
		String oder = "index ASC";
		lstUnitType = findByConditions(cols, operators, values, oder);
		return lstUnitType;
	}

	public List<UnitType> findByCategory(Category category) {
		List<Long> categoryIds = new ArrayList<>();
		categoryIds.add(category.getCategoryId());
		return this.findUnitTypeByCatId(categoryIds);
	}

	public List<UnitType> findUTByBalType(long balTypeId) {

		List<UnitType> lst = null;		
		String[] cols = { "balTypeId", "domainId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { balTypeId, getDomainId() };
		
		lst = findByConditions(cols, operators, values, "");
		return lst;

	}

	
	public boolean checkName(UnitType unitType, String name) {
		List<UnitType> lst = null;
		String[] cols = { "name", "domainId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { name, getDomainId() };
		lst = findByConditions(cols, operators, values, "");
		if (lst.size() > 0) {
			if (lst.get(0).getUnitTypeId() == unitType.getUnitTypeId()) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}
	
	public boolean deleteUTByBalType(UnitType unitType) {
		String hql = "DELETE FROM UnitType map";
		hql += " WHERE map.unitTypeId = :unitTypeId";
		hql += " AND map.domainId = :domainId";
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			session.delete(unitType);
			Query<UnitType> query = session.createQuery(hql);
			query.setParameter("unitTypeId", unitType.getUnitTypeId());
			query.setParameter("domainId", unitType.getDomainId());
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
	
	public UnitType findUnitTypeLastIndex() {
		Session session = HibernateUtil.getOpenSession();
		try {

			StringBuffer hql = new StringBuffer("SELECT a FROM UnitType a");
			hql.append(" WHERE a.domainId = :domainId");
			hql.append(" ORDER BY a.unitTypeId DESC");

			Query<UnitType> query = session.createQuery(hql.toString());
			query.setParameter("domainId", getDomainId());
			query.setMaxResults(1);
			List<UnitType> unitTypes = query.getResultList();
			if (unitTypes.size() > 0) {
				return unitTypes.get(0);
			}
			return null;
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}
	
	public UnitType findUnitTypeLastIndexMove() {
		Session session = HibernateUtil.getOpenSession();
		try {

			StringBuffer hql = new StringBuffer("SELECT a FROM UnitType a");
			hql.append(" WHERE a.domainId = :domainId");
			hql.append(" ORDER BY a.index DESC");

			Query<UnitType> query = session.createQuery(hql.toString());
			query.setParameter("domainId", getDomainId());

			List<UnitType> unitTypes = query.getResultList();
			if (unitTypes != null && unitTypes.size() > 0) {
				return unitTypes.get(0);
			}
			return null;
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}
	
	
	public boolean checkId(UnitType unitType, boolean edit) {
		List<UnitType> lst = null;
		String hql = "SELECT p FROM UnitType p";
		hql += " WHERE p.unitTypeId = :unitTypeId";
		hql += " AND p.domainId = :domainId";

		Session session = HibernateUtil.getOpenSession();
		try {
			Query<UnitType> query = session.createQuery(hql);
			query.setParameter("unitTypeId", unitType.getUnitTypeId());
			query.setParameter("domainId", getDomainId());
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

	public Boolean processMoveUpDown(UnitType item, Boolean isUp) {
		boolean result = false;
		if (item != null && item.getUnitTypeId() > 0) {
			Session session = HibernateUtil.getOpenSession();
			item = get(item.getUnitTypeId());
			try {

				session.getTransaction().begin();

				StringBuffer hql = new StringBuffer("SELECT a FROM UnitType a");
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

				Query<UnitType> query = session.createQuery(hql.toString());
				query.setParameter("domainId", getDomainId());
				query.setParameter("index", item.getIndex());
				query.setParameter("categoryId", item.getCategoryId());
				query.setMaxResults(1);

				List<UnitType> unitTypes = query.getResultList();
				if (unitTypes != null && unitTypes.size() > 0) {
					if (!checkFieldIsExist("unitOrder", item.getIndex(), item)) {
						UnitType itemMove = unitTypes.get(0);
						Long tmpIndex = 0l;
						tmpIndex = itemMove.getIndex();
						itemMove.setIndex(item.getIndex());
						item.setIndex(tmpIndex);
						session.update(itemMove);
						session.update(item);
						result = true;
					} else {
						result = false;
					}
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
	
	public Boolean checkFieldIsExist(String col, Object value, UnitType unitType) {
		boolean result = false;

		int count = 0;

		if (unitType == null || unitType.getUnitTypeId() == 0) {
			String[] column = { col };
			Operator[] ope = { Operator.EQ };
			Object[] val = { value };
			count = this.countByConditions(column, ope, val);
		} else {
			String[] column = { col, "unitTypeId" };
			Operator[] ope = { Operator.EQ, Operator.NOTEQ };
			Object[] val = { value, unitType.getUnitTypeId() };
			count = this.countByConditions(column, ope, val);
		}

		if (count > 0) {
			result = true;
		}

		return result;
	}
	
	@Override
	protected Class<UnitType> getEntityClass() {
		return UnitType.class;
	}
	
	public int countUnitType() {
		String hql = "SELECT COUNT(a) FROM UnitType a WHERE  a.domainId =:domainId ";
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

	public boolean saveUnitType(UnitType unitType) {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			unitType.setDomainId(getDomainId());
			generateIndexByCat(unitType, "unitTypeId", session);
			session.saveOrUpdate(unitType);
			session.getTransaction().commit();
			return true;
		} catch (Exception e) {
			
			session.getTransaction().rollback();
			throw e;
		} finally {
			session.close();
		}
	}

}

package com.viettel.ocs.dao;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
//import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;

import com.viettel.ocs.context.SessionUtils;
import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.ActionType;
import com.viettel.ocs.entity.Event;
import com.viettel.ocs.entity.EventActionTypeMap;
import com.viettel.ocs.entity.OfferPackage;
import com.viettel.ocs.util.CommonUtil;

/**
 * Use manual Hibernate implement
 * 
 * @author huannn
 *
 * @param <T>
 */
public abstract class BaseDAO<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6463371152502202761L;
	private long domainId;

	public BaseDAO() {

		domainId = SessionUtils.getDomainId();
	}
	
	public Logger getLogger() {
		return Logger.getLogger(this.getClass());
	}
	
	public long getDomainId() {
		return domainId;
	}

	public List<T> findAll(String orders) {

		Session session = HibernateUtil.getOpenSession();
		List<T> lst = null;
		try {

			String queryString = "SELECT obj FROM " + getEntityClass().getName() + " obj WHERE domainId = :domainId ";
			if (!CommonUtil.isEmpty(orders))
				queryString += " ORDER BY " + orders;
			Query query = session.createQuery(queryString);
			query.setParameter("domainId", domainId);
			lst = query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}

		return lst;
	}

	public List<T> findAllWithoutDomain(String orders) {

		Session session = HibernateUtil.getOpenSession();
		List<T> lst = null;
		try {

			String queryString = "SELECT obj FROM " + getEntityClass().getName() + " obj ";
			if (!CommonUtil.isEmpty(orders))
				queryString += " ORDER BY " + orders;
			Query query = session.createQuery(queryString);
			lst = query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}

		return lst;
	}

	public int countByConditions(String[] cols, Operator[] operators, Object[] values) {
		return this.countByConditions(getEntityClass(), cols, operators, values);
	}

	public <E> int countByConditions(Class<E> clazz, String[] cols, Operator[] operators, Object[] values) {

		Session session = HibernateUtil.getOpenSession();
		List lst = null;
		try {

			// Criteria cri = session.createCriteria(clazz);
			String queryString = "SELECT COUNT(obj) FROM " + clazz.getName() + " obj WHERE domainId = :domainId ";
			queryString += getWhereString(cols, operators);

			Query query = session.createQuery(queryString);
			query.setParameter("domainId", domainId);
			setWhereParam(cols, operators, values, query);
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

	public int countByConditionsWithoutDomain(String[] cols, Operator[] operators, Object[] values) {
		return this.countByConditionsWithoutDomain(getEntityClass(), cols, operators, values);
	}

	public <E> int countByConditionsWithoutDomain(Class<E> clazz, String[] cols, Operator[] operators,
			Object[] values) {

		Session session = HibernateUtil.getOpenSession();
		List lst = null;
		try {

			// Criteria cri = session.createCriteria(clazz);
			String queryString = "SELECT COUNT(obj) FROM " + clazz.getName() + " obj WHERE 1 = 1 ";
			queryString += getWhereString(cols, operators);

			Query query = session.createQuery(queryString);
			setWhereParam(cols, operators, values, query);
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

	public List<T> findByConditions(String[] cols, Operator[] operators, Object[] values, String orders) {
		return this.findByConditions(getEntityClass(), cols, operators, values, orders);
	}

	public <E> List<E> findByConditions(Class<E> clazz, String[] cols, Operator[] operators, Object[] values,
			String orders) {

		Session session = HibernateUtil.getOpenSession();
		List<E> lst = null;
		try {

			// Criteria cri = session.createCriteria(clazz);
			String queryString = "SELECT obj FROM " + clazz.getName() + " obj WHERE domainId = :domainId ";
			queryString += getWhereString(cols, operators);

			if (!CommonUtil.isEmpty(orders))
				queryString += " ORDER BY " + orders;

			Query<E> query = session.createQuery(queryString);
			query.setParameter("domainId", domainId);
			setWhereParam(cols, operators, values, query);
			lst = query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}

		return lst;
	}

	public List<T> findByConditionsWithoutDomain(String[] cols, Operator[] operators, Object[] values, String orders) {
		return this.findByConditionsWithoutDomain(getEntityClass(), cols, operators, values, orders);
	}

	public <E> List<E> findByConditionsWithoutDomain(Class<E> clazz, String[] cols, Operator[] operators,
			Object[] values, String orders) {

		Session session = HibernateUtil.getOpenSession();
		List<E> lst = null;
		try {

			// Criteria cri = session.createCriteria(clazz);
			String queryString = "SELECT obj FROM " + clazz.getName() + " obj WHERE 1 = 1 ";
			queryString += getWhereString(cols, operators);

			if (!CommonUtil.isEmpty(orders))
				queryString += " ORDER BY " + orders;

			Query<E> query = session.createQuery(queryString);
			setWhereParam(cols, operators, values, query);
			lst = query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}

		return lst;
	}

	private <E> void setWhereParam(String[] cols, Operator[] operators, Object[] values, Query<E> query) {
		for (int i = 0; i < cols.length; i++) {
			if (Operator.IN.equals(operators[i])) {

				if (values[i] instanceof Object[]) {

					query.setParameterList(cols[i], (Object[]) values[i]);
				} else if (values[i] instanceof Collection) {

					query.setParameterList(cols[i], (Collection) values[i]);
				}
			} else if (Operator.NULL.equals(operators[i]) || Operator.NOTNULL.equals(operators[i])) {
				// do nothing
			} else {
				query.setParameter(cols[i], values[i]);
			}
		}
	}

	private String getWhereString(String[] cols, Operator[] operators) {
		String colName;
		Operator operator;
		String whereString = "";
		for (int i = 0; i < cols.length; i++) {

			colName = cols[i];
			operator = operators[i];
			if (operator == Operator.EQ) {
				whereString += " AND " + colName + " = :" + colName;
			} else if (operator == Operator.NOTEQ) {
				whereString += " AND " + colName + " <> :" + colName;
			} else if (operator == Operator.GT) {
				whereString += " AND " + colName + " > :" + colName;
			} else if (operator == Operator.GE) {
				whereString += " AND " + colName + " >= :" + colName;
			} else if (operator == Operator.LT) {
				whereString += " AND " + colName + " < :" + colName;
			} else if (operator == Operator.LE) {
				whereString += " AND " + colName + " <= :" + colName;
			} else if (operator == Operator.LIKE) {
				whereString += " AND " + colName + " LIKE :" + colName;
			} else if (operator == Operator.ILIKE) {
				whereString += " AND " + colName + " LIKE '%:" + colName + "%'";
			} else if (operator == Operator.IN) {
				whereString += " AND " + colName + " IN (:" + colName + ")";
			} else if (operator == Operator.NOTIN) {
				whereString += " AND " + colName + " NOT IN (:" + colName + ")";
			} else if (operator == Operator.NULL) {
				whereString += " AND " + colName + " IS NULL";
			} else if (operator == Operator.NOTNULL) {
				whereString += " AND " + colName + " IS NOT NULL";
			}
		}
		return whereString;
	}

	public T get(Integer id) {

		if (id == null)
			return null;

		Session session = HibernateUtil.getOpenSession();
		T t = null;
		try {
			t = (T) session.get(getEntityClass(), id);
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
		return t;
	}

	public T get(Long id) {

		if (id == null)
			return null;

		Session session = HibernateUtil.getOpenSession();
		T t = null;
		try {
			t = (T) session.get(getEntityClass(), id);
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
		return t;
	}

	public <E> E get(Long id, Class<E> clazz) {

		if (id == null)
			return null;

		Session session = HibernateUtil.getOpenSession();
		E t = null;
		try {
			t = (E) session.get(clazz, id);
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
		return t;
	}

	public T load(Integer id) {

		Session session = HibernateUtil.getOpenSession();
		T t = null;
		try {
			t = (T) session.load(getEntityClass(), id);
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
		return t;
	}

	public T load(Long id) {

		Session session = HibernateUtil.getOpenSession();
		T t = null;
		try {
			t = (T) session.load(getEntityClass(), id);
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
		return t;
	}

	public long getMax(String col) {

		return getMax(getEntityClass(), col);
	}

	public <E> long getMax(Class<E> clazz, String col) {

		Session session = HibernateUtil.getOpenSession();
		List lst = null;
		try {

			// Criteria cri = session.createCriteria(clazz);
			String queryString = "SELECT MAX(" + col + ") FROM " + clazz.getName();
			Query query = session.createQuery(queryString);
			lst = query.getResultList();
			if (lst.size() > 0 && lst.get(0) != null)
				return ((Number) lst.get(0)).longValue();
			else
				return 0;
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	protected void setDefaultDomain(T t) {
		try {
			// Set default domain

			if (!"SysDomain".equals(t.getClass().getName()) || !"SysRole".equals(t.getClass().getName())
					|| !"SysGroup".equals(t.getClass().getName()) || !"SysUser".equals(t.getClass().getName())
					|| !"SystemConfig".equals(t.getClass().getName())) {

				String strDomainId = BeanUtils.getProperty(t, "domainId");
				if (CommonUtil.isEmpty(strDomainId)) {
					BeanUtils.setProperty(t, "domainId", domainId);
				}
			}
		} catch (Exception e) {
			// 
		}
	}

	public void save(T t) {

		if (t == null)
			return;

		Session session = HibernateUtil.getOpenSession();
		try {

			setDefaultDomain(t);
			session.beginTransaction();
			session.save(t);
			session.getTransaction().commit();
		} catch (Exception e) {
			session.getTransaction().rollback();
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}

	}

	public void saveOrUpdate(T t) {

		if (t == null)
			return;
		Session session = HibernateUtil.getOpenSession();
		try {

			setDefaultDomain(t);
			session.beginTransaction();
			session.saveOrUpdate(t);
			session.getTransaction().commit();
		} catch (Exception e) {
			session.getTransaction().rollback();
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public void update(T t) {

		if (t == null)
			return;
		Session session = HibernateUtil.getOpenSession();
		try {
			session.beginTransaction();
			session.update(t);
			session.getTransaction().commit();
		} catch (Exception e) {
			session.getTransaction().rollback();
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public void deleteByConditions(String[] cols, Operator[] operators, Object[] values) {
		this.deleteByConditions(getEntityClass(), cols, operators, values);
	}

	/**
	 * 
	 * @param clazz
	 * @param whereColName
	 * @param whereColValue
	 */
	public <E> void deleteByConditions(Class<E> clazz, String[] cols, Operator[] operators, Object[] values) {

		Session session = HibernateUtil.getOpenSession();
		try {
			session.beginTransaction();
			String hql = "DELETE " + clazz.getName() + " WHERE 1=1 ";
			hql += getWhereString(cols, operators);
			Query q = session.createQuery(hql);
			setWhereParam(cols, operators, values, q);
			q.executeUpdate();
			session.getTransaction().commit();
		} catch (Exception e) {
			session.getTransaction().rollback();
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public <E> void delete(E t) {

		if (t == null)
			return;
		Session session = HibernateUtil.getOpenSession();
		try {
			session.beginTransaction();
			session.delete(t);
			session.getTransaction().commit();
		} catch (Exception e) {
			session.getTransaction().rollback();
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public String generateNameObject(String columnName, String nameValue, int increasedNumber, Session session) {
		return this.generateNameObject(getEntityClass(), columnName, nameValue, increasedNumber, session);
	}

	public String generateNameObject(Class<?> clazz, String columnName, String nameValue, int increasedNumber,
			Session session) {
		try {
			StringBuffer hql = new StringBuffer("SELECT COUNT(a) FROM " + clazz.getName() + " a");
			hql.append(" WHERE a.domainId =:domainId");
			hql.append(" AND a." + columnName + " LIKE :" + columnName);

			Query<Long> query = session.createQuery(hql.toString(), Long.class);
			query.setParameter("domainId", getDomainId());
			query.setParameter(columnName, nameValue + "(" + increasedNumber + ")");
			Long count = query.getSingleResult();

			if (count > 0L) {
				increasedNumber++;
				if (increasedNumber < count.intValue())
					increasedNumber = count.intValue();
				return generateNameObject(clazz, columnName, nameValue, increasedNumber, session);
			}

			return nameValue + "(" + increasedNumber + ")";
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}		
	}

	/**
	 * @author THANHND
	 * @param object
	 *            object you want to move
	 * @param posColumn
	 *            name index column
	 * @param isUp
	 *            if true is move up else false is move down
	 * @return if null is fail or object's position is maximum or minimum
	 */
	public Object upDownObjectInCatWithDomain(Object object, String posColumn, boolean isUp) {
		return upDownObjectInCat(object, posColumn, isUp, true);
	}

	/**
	 * @author THANHND
	 * @param object
	 *            object you want to move
	 * @param posColumn
	 *            name index column
	 * @param isUp
	 *            if true is move up else false is move down
	 * @return if null is fail or object's position is maximum or minimum
	 */
	public Object upDownObjectInCatWithoutDomain(Object object, String posColumn, boolean isUp) {
		return upDownObjectInCat(object, posColumn, isUp, false);
	}

	public Object upDownObjectInCat(Object object, String posColumn, boolean isUp, boolean useDomain) {
		Class<? extends Object> clazz = object.getClass();

		Session session = HibernateUtil.getOpenSession();
		session.beginTransaction();
		try {
			Field categoryIdFeild = clazz.getDeclaredField("categoryId");
			categoryIdFeild.setAccessible(true);
			Field posFeild = clazz.getDeclaredField(posColumn);
			posFeild.setAccessible(true);

			StringBuffer hql = new StringBuffer("SELECT a FROM " + clazz.getName() + " a");
			hql.append(" WHERE a." + posFeild.getName() + " " + (isUp ? "<" : ">") + " :index");
			hql.append(" AND a." + categoryIdFeild.getName() + " = :categoryId");
			if (useDomain) {
				hql.append(" AND a.domainId = :domainId");
			}
			hql.append(" ORDER BY a." + posFeild.getName() + (isUp ? " DESC" : " ASC"));

			Query<?> query = session.createQuery(hql.toString());
			if (useDomain) {
				query.setParameter("domainId", getDomainId());
			}
			query.setParameter("index", posFeild.get(object));
			query.setParameter("categoryId", categoryIdFeild.get(object));
			query.setMaxResults(1);

			List<?> objects = query.getResultList();

			if (objects.size() < 1) {
				return null;
			} else {
				Object objectSecond = objects.get(0);

				Long posIndex = (Long) posFeild.get(object);
				posFeild.set(object, posFeild.get(objectSecond));
				posFeild.set(objectSecond, posIndex);
				session.update(object);
				session.update(objectSecond);
				session.getTransaction().commit();
				return objectSecond;
			}

		} catch (Exception e) {
			
			session.getTransaction().rollback();
			getLogger().error(e.getMessage(), e);
			return null;
		} finally {
			session.close();
		}
	}

	public long getMaxIndex(String columIndexName, Session session) {
		try {
			StringBuffer hql = new StringBuffer(
					"SELECT COALESCE(MAX(a." + columIndexName + "), 0) FROM " + getEntityClass().getName() + " a");
			hql.append(" WHERE a.domainId = :domainId");

			Query<Long> query = session.createQuery(hql.toString(), Long.class);
			query.setParameter("domainId", getDomainId());
			return query.getSingleResult();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}		
	}

	public long getMaxIndexWithoutDomain(String columIndexName, Session session) {
		try {
			StringBuffer hql = new StringBuffer(
					"SELECT COALESCE(MAX(a." + columIndexName + "), 0) FROM " + getEntityClass().getName() + " a");

			Query<Long> query = session.createQuery(hql.toString(), Long.class);
			return query.getSingleResult();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}		
	}

	public long getMaxIndex(String columIndexName, String catIdCol, Object categoryId, Session session) {
		return getMaxIndex(getEntityClass(), columIndexName, catIdCol, categoryId, session);
	}

	private long getMaxIndex(Class<?> clazz, String columIndexName, String catIdCol, Object categoryId,
			Session session) {
		try {
			StringBuffer hql = new StringBuffer(
					"SELECT COALESCE(MAX(a." + columIndexName + "), 0) FROM " + clazz.getName() + " a");

			Query<Long> query = session.createQuery(hql.toString(), Long.class);
			return query.getSingleResult();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}		
	}

	public long getMaxIndexWithoutDomain(String columIndexName, long categoryId, Session session) {
		try {
			StringBuffer hql = new StringBuffer(
					"SELECT COALESCE(MAX(a." + columIndexName + "), 0) FROM " + getEntityClass().getName() + " a");
			hql.append(" WHERE a.categoryId = :categoryId");

			Query<Long> query = session.createQuery(hql.toString(), Long.class);
			query.setParameter("categoryId", categoryId);
			return query.getSingleResult();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}		
	}

	public void generateIndexByCat(Object object, String idCol, Session session) {
		this.generateIndexByCat(object, "index", idCol, "categoryId", session);
	}
	
	public boolean generateIndexByCat(Object object, String idCol) {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			generateIndexByCat(object, idCol, session);
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

	public void generateIndexByCat(Object object, String indexCol, String idCol, Session session) {
		this.generateIndexByCat(object, indexCol, idCol, "categoryId", session);
	}

	public void generateIndexByCat(Object object, String indexCol, String idCol, String catIdCol, Session session) {
		Class<? extends Object> clazz = object.getClass();
		try {
			Field idColFeild = clazz.getDeclaredField(idCol);
			idColFeild.setAccessible(true);
			Field catIdColFeild = clazz.getDeclaredField(catIdCol);
			catIdColFeild.setAccessible(true);
			Field indexColFeild = clazz.getDeclaredField(indexCol);
			indexColFeild.setAccessible(true);

			Object idColValue = idColFeild.get(object);
			Object catIdValue = catIdColFeild.get(object);

			StringBuffer hql = new StringBuffer();
			hql.append("SELECT COALESCE(COUNT(a), 0) = 0 AS moved FROM " + clazz.getName() + " a");
			hql.append(" WHERE a." + idCol + " = :idCol");
			hql.append(" AND a." + catIdCol + " = :catIdCol");

			Query<Boolean> query = session.createQuery(hql.toString(), Boolean.class);
			query.setParameter("idCol", idColValue);
			query.setParameter("catIdCol", catIdValue);

			if (query.getSingleResult()) {
				indexColFeild.set(object, this.getMaxIndex(clazz, indexCol, catIdCol, catIdValue, session) + 1L);
			}

		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
			getLogger().error(e.getMessage(), e);
		}
	}

	public long getMaxIndex(String columIndexName) {
		Session session = HibernateUtil.getOpenSession();
		try {
			return getMaxIndex(columIndexName, session);
		} catch (Exception e) {
			
			session.getTransaction().rollback();
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}		
	}

	public long getMaxIndexWithoutDomain(String columIndexName) {
		Session session = HibernateUtil.getOpenSession();
		try {
			return getMaxIndexWithoutDomain(columIndexName, session);
		} catch (Exception e) {
			
			session.getTransaction().rollback();
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}
	
	public String generateNameObjectWithoutDomain(String columnName, String nameValue, int increasedNumber, Session session) {
		return this.generateNameObjectWithoutDomain(getEntityClass(), columnName, nameValue, increasedNumber, session);
	}

	public String generateNameObjectWithoutDomain(Class<?> clazz, String columnName, String nameValue, int increasedNumber,
			Session session) {
		try {
			StringBuffer hql = new StringBuffer("SELECT COUNT(a) FROM " + clazz.getName() + " a");
			hql.append(" WHERE a." + columnName + " LIKE :" + columnName);

			Query<Long> query = session.createQuery(hql.toString(), Long.class);
			query.setParameter(columnName, nameValue + "(" + increasedNumber + ")");
			Long count = query.getSingleResult();

			if (count > 0L) {
				increasedNumber++;
				if (increasedNumber < count.intValue())
					increasedNumber = count.intValue();
				return generateNameObjectWithoutDomain(clazz, columnName, nameValue, increasedNumber, session);
			}

			return nameValue + "(" + increasedNumber + ")";
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}		
	}

	abstract protected Class<T> getEntityClass();
}

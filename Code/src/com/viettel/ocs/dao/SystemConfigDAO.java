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
import com.viettel.ocs.entity.Block;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.CdrProcessConfig;
import com.viettel.ocs.entity.DynamicReserve;
import com.viettel.ocs.entity.Parameter;
import com.viettel.ocs.entity.PriceComponent;
import com.viettel.ocs.entity.RateTable;
import com.viettel.ocs.entity.SortPriceComponent;
import com.viettel.ocs.entity.SortPriceRateTableMap;
import com.viettel.ocs.entity.SysMenu;
import com.viettel.ocs.entity.SystemConfig;
import com.viettel.ocs.model.CloneIgnoreListModel;
import com.viettel.ocs.model.SortPriceRateTableMapModal;
import com.viettel.ocs.util.CommonUtil;

@SuppressWarnings("serial")
public class SystemConfigDAO extends BaseDAO<SystemConfig> implements Serializable {

	@Override
	public List<SystemConfig> findAll(String orders) {

		return super.findAllWithoutDomain(orders);
	}

	public List<SystemConfig> findByCat(Long catId) {

		if (catId == null || catId <= 0)
			return new ArrayList<SystemConfig>();

		String[] col = { "categoryId" };
		Operator[] ope = { Operator.EQ };
		Object[] val = { catId };

		String order = "index";
		return this.findByConditionsWithoutDomain(col, ope, val, order);
	}

	public List<SystemConfig> findByListCat(List<Long> lstCatID) {

		if (lstCatID == null || lstCatID.size() == 0)
			return new ArrayList<SystemConfig>();

		String[] col = { "categoryId" };
		Operator[] ope = { Operator.IN };
		Object[] val = { lstCatID };

		String oder = "index";
		return this.findByConditionsWithoutDomain(col, ope, val, oder);
	}

	/***
	 * 
	 * @param key
	 * @param notId:
	 *            < 0: bo qua; >=0: tim khac id truyen vao
	 * @return
	 */
	public SystemConfig findByKey(String key, long notId) {

		if (CommonUtil.isEmpty(key))
			return null;

		List<Object> params = new ArrayList<Object>();
		String hql = "SELECT a FROM SystemConfig a WHERE LOWER(a.key) LIKE ?";
		params.add(key.toLowerCase());
		if (notId >= 0) {
			hql += " AND a.id <> ?";
			params.add(notId);
		}

		List<SystemConfig> lst;
		Session session = HibernateUtil.getOpenSession();
		try {

			Query<SystemConfig> query = session.createQuery(hql);
			int i = 0;
			for (Object obj : params) {
				query.setParameter(i, obj);
				i++;
			}

			lst = query.getResultList();
			if (lst.size() > 0)
				return lst.get(0);
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}

		return null;
	}

	@Override
	protected Class<SystemConfig> getEntityClass() {
		return SystemConfig.class;
	}

	public long getMaxIndex() {
		return getMaxIndexWithoutDomain("index");
	}

	public boolean saveSystemCfg(SystemConfig systemConfig) {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			generateIndexByCat(systemConfig, "id", session);
			session.saveOrUpdate(systemConfig);
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

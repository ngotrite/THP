package com.viettel.ocs.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.entity.CalcUnit;
public class CalcUnitDAO extends BaseDAO<CalcUnit> {

	@Override
	protected Class<CalcUnit> getEntityClass() {
		return CalcUnit.class;
	}

	public List<CalcUnit> findCalcUnit() {
		Session session = HibernateUtil.getOpenSession();
		try {
			List<CalcUnit> list = null;
			StringBuffer hql = new StringBuffer("SELECT a FROM CalcUnit a");
			Query<CalcUnit> query = session.createQuery(hql.toString());
			list = query.getResultList();
			if (list.size() > 0) {
				return list;
			}
			return null;
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}
}

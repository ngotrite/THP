package com.viettel.ocs.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.NormValue;
import com.viettel.ocs.entity.Normalizer;

public class NormValueDAO extends BaseDAO<NormValue> {

	public List<NormValue> findNormValueByNormId(long normalizerId) {
		List<NormValue> listValue = null;
		String hqlNormValue = "SELECT nv FROM NormValue nv JOIN NormalizerNormValueMap map ON nv.normValueId = map.normValueId ";
		hqlNormValue += " WHERE map.normalizerId = :normalizerId";
		Session session = HibernateUtil.getOpenSession();
		try {
			Query<NormValue> query = session.createQuery(hqlNormValue);
			query.setParameter("normalizerId", normalizerId);
			listValue = query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
		return listValue;
	}

	public NormValue findNormValue(long normValueId) {
		List<NormValue> listValue = null;
		String[] cols = { "normValueId" };
		Operator[] operators = { Operator.EQ };
		Object[] values = { normValueId };
		listValue = findByConditions(cols, operators, values, "");
		return listValue.get(0);
	}

	@Override
	protected Class<NormValue> getEntityClass() {
		// TODO Auto-generated method stub
		return NormValue.class;
	}

}

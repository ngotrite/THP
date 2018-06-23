package com.viettel.ocs.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.NormParam;
import com.viettel.ocs.entity.UnitType;

public class NormParamDAO extends BaseDAO<NormParam> {

	public List<NormParam> findNormParamByNormId(long normalizerId) {
		List<NormParam> listParam = null;
		String hqlNormParam = "SELECT np FROM NormParam np JOIN NomalizerNormParamMap map ON np.normParamId = map.normParamId ";
		hqlNormParam += " WHERE map.normalizerId = :normalizerId";
		Session sessionParam = HibernateUtil.getOpenSession();
		try {
			Query<NormParam> query = sessionParam.createQuery(hqlNormParam, NormParam.class);
			query.setParameter("normalizerId", normalizerId);
			listParam = query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			sessionParam.close();
		}
		return listParam;
	}

	public boolean checkZoneInNP(long configInput) {
		List<NormParam> lst = null;
		String[] cols = { "configInput", "domainId" };
		Operator[] operators = { Operator.LIKE, Operator.EQ };
		Object[] values = { "%zoneValueId:" + configInput + "%", getDomainId() };
		lst = findByConditions(cols, operators, values, "");
		if (lst != null && lst.size() > 0) {
			return true;
		}
		return false;
	}

	public boolean checkZoneMapInNP(long configInput) {
		List<NormParam> lst = null;
		String[] cols = { "configInput", "domainId" };
		Operator[] operators = { Operator.LIKE, Operator.EQ };
		Object[] values = { "%zoneValueId:" + configInput + "%", getDomainId() };
		lst = findByConditions(cols, operators, values, "");
		if (lst != null && lst.size() > 0) {
			return true;
		}
		return false;
	}

	public boolean checkParameterInNP(long parameterId) {
		StringBuffer hql = new StringBuffer("SELECT COUNT(a) FROM NormParam a");
		hql.append(" WHERE configInput LIKE '%start:" + parameterId + "%startIsParameter:true%'");
		hql.append(" OR configInput LIKE '%end:" + parameterId + "%endIsParameter:true%'");
		hql.append(" OR configInput LIKE '%parameterId:" + parameterId + "%isUsingParameter:true%'");
		hql.append(" AND a.domainId = :domainId");

		Session session = HibernateUtil.getOpenSession();
		try {
			Query<Long> query = session.createQuery(hql.toString(), Long.class);
			query.setParameter("domainId", getDomainId());
			if (query.getSingleResult() > 0L) {
				return true;
			} else
				return false;
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}
	
	public boolean checkGeoHomeInNP(long configInput) {
		List<NormParam> lst = null;
		String[] cols = { "configInput", "domainId" };
		Operator[] operators = { Operator.LIKE, Operator.EQ };
		Object[] values = { "%zoneValueId:" + configInput + "%", getDomainId() };
		lst = findByConditions(cols, operators, values, "");
		if (lst != null && lst.size() > 0) {
			return true;
		}
		return false;
	}

	@Override
	protected Class<NormParam> getEntityClass() {
		// TODO Auto-generated method stub
		return NormParam.class;
	}

}

package com.viettel.ocs.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.BlockRateTableMap;
import com.viettel.ocs.entity.NormValue;
import com.viettel.ocs.entity.NormalizerNormValueMap;
import com.viettel.ocs.entity.Zone;
import com.viettel.ocs.entity.ZoneMap;

public class NormalizerNormValueMapDAO extends BaseDAO<NormalizerNormValueMap> {

	public List<NormalizerNormValueMap> findMapByNormalizerId(long normalizerId) {
		List<NormalizerNormValueMap> lst = null;
		String[] cols = { "normalizerId" };
		Operator[] operators = { Operator.EQ };
		Object[] values = { normalizerId };
		lst = findByConditions(cols, operators, values, "");
		return lst;
	}
	
	public NormalizerNormValueMap findMapByNormalizerIdAndValueId(long normalizerId, long valueId) {
		String hql = " FROM NormalizerNormValueMap nnvm WHERE nnvm.normalizerId = :normalizerId AND nnvm.normValueId = :normValueId";
		Session session = HibernateUtil.getOpenSession();
		try {
			Query query = session.createQuery(hql);
			query.setParameter("normalizerId", normalizerId);
			query.setParameter("normValueId", valueId);
			return (NormalizerNormValueMap) query.uniqueResult();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}
	

	public void deleteMapByNormalizerId(long normalizerId) {
		StringBuilder hqlMap = new StringBuilder(
				"DELETE FROM NormalizerNormValueMap WHERE normalizerId = :normalizerId");
		hqlMap.append(" AND domainId =:domainId");

		NormValueDAO normValueDAO = new NormValueDAO();
		List<NormValue> listNormValue = normValueDAO.findNormValueByNormId(normalizerId);
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			// Delete all in NormValue
			for (NormValue normValue : listNormValue) {
				normValueDAO.delete(normValue);
			}
			// Delete all in map
			Query<NormalizerNormValueMap> queryMap = session.createQuery(hqlMap.toString());
			queryMap.setParameter("normalizerId", normalizerId);
			queryMap.setParameter("domainId", getDomainId());
			queryMap.executeUpdate();
			session.getTransaction().commit();
		} catch (Exception e) {
			session.getTransaction().rollback();
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}
	
	public void deleteMapByValueId(long ValueID) {
		StringBuilder hqlMap = new StringBuilder(
				"DELETE FROM NormalizerNormValueMap WHERE normValueId = :normValueId");
		hqlMap.append(" AND domainId =:domainId");

		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			// Delete all in map
			Query<NormalizerNormValueMap> queryMap = session.createQuery(hqlMap.toString());
			queryMap.setParameter("normValueId", ValueID);
			queryMap.setParameter("domainId", getDomainId());
			queryMap.executeUpdate();
			session.getTransaction().commit();
		} catch (Exception e) {
			session.getTransaction().rollback();
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	@Override
	protected Class<NormalizerNormValueMap> getEntityClass() {
		// TODO Auto-generated method stub
		return NormalizerNormValueMap.class;
	}

}

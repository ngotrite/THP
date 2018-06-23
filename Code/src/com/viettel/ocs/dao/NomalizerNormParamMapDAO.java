package com.viettel.ocs.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.BlockRateTableMap;
import com.viettel.ocs.entity.NomalizerNormParamMap;
import com.viettel.ocs.entity.NormParam;
import com.viettel.ocs.entity.NormalizerNormValueMap;

public class NomalizerNormParamMapDAO extends BaseDAO<NomalizerNormParamMap> {
	
	public List<NomalizerNormParamMap> findMapByNormalizerId(long normalizerId) {
		List<NomalizerNormParamMap> lst = null;
		String[] cols = { "normalizerId" };
		Operator[] operators = { Operator.EQ };
		Object[] values = { normalizerId };
		lst = findByConditions(cols, operators, values, "");
		return lst;
	}
	
	public NomalizerNormParamMap findMapByNormalizerIdAndValueId(long normalizerId, long paramID) {
		String hql = " FROM NomalizerNormParamMap nnvm WHERE nnvm.normalizerId = :normalizerId AND nnvm.normParamId = :normParamId";
		Session session = HibernateUtil.getOpenSession();
		try {
			Query query = session.createQuery(hql);
			query.setParameter("normalizerId", normalizerId);
			query.setParameter("normParamId", paramID);
			return (NomalizerNormParamMap) query.uniqueResult();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}
	
	public void deleteParamMapByNormalizerId(long normalizerId) {
		StringBuilder hqlMap = new StringBuilder(
				"DELETE FROM NomalizerNormParamMap WHERE normalizerId = :normalizerId");
		hqlMap.append(" AND domainId =:domainId");

		NormParamDAO normParamDAO = new NormParamDAO();
		List<NormParam> listNormParam = normParamDAO.findNormParamByNormId(normalizerId);
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			// Delete all in NormParam
			for (NormParam normParam : listNormParam) {
				normParamDAO.delete(normParam);
			}
			// Delete all in map
			Query<NomalizerNormParamMap> queryMap = session.createQuery(hqlMap.toString());
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
	
	public void deleteParamMapByParamId(long ParamID) {
		StringBuilder hqlMap = new StringBuilder("DELETE FROM NomalizerNormParamMap WHERE normParamId = :normParamId");
		hqlMap.append(" AND domainId =:domainId");

		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			// Delete all in map
			Query<NomalizerNormParamMap> queryMap = session.createQuery(hqlMap.toString());
			queryMap.setParameter("normParamId", ParamID);
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
	
	public Boolean deleteNormParamItemAndMap(NormParam normParam){
		boolean result = false;
		if(normParam != null && normParam.getNormParamId() > 0){
			Session session = HibernateUtil.getOpenSession();
			session.getTransaction().begin();
			try {
				// Delete all in map
				List<NomalizerNormParamMap> normParamMaps = new ArrayList<NomalizerNormParamMap>();
				StringBuilder hqlMap = new StringBuilder("FROM NomalizerNormParamMap WHERE normParamId = :normParamId");
				hqlMap.append(" AND domainId =:domainId");
				Query<NomalizerNormParamMap> queryMap = session.createQuery(hqlMap.toString());
				queryMap.setParameter("normParamId", normParam.getNormParamId());
				queryMap.setParameter("domainId", getDomainId());
				normParamMaps = queryMap.getResultList();
				
				if(normParamMaps.size() > 0 ){
					NormParamDAO normParamDAO = new NormParamDAO();
					for(NomalizerNormParamMap npm : normParamMaps){
						session.delete(npm);
					}
					session.delete(normParam);
				}
				result = true;
				session.getTransaction().commit();
			} catch (Exception e) {
				session.getTransaction().rollback();
				getLogger().error(e.getMessage(), e);
				throw e;
			} finally {
				session.close();
			}
		
		}
		
		return result;
	}


	@Override
	protected Class<NomalizerNormParamMap> getEntityClass() {
		// TODO Auto-generated method stub
		return NomalizerNormParamMap.class;
	}

}

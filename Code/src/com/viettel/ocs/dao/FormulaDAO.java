package com.viettel.ocs.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.entity.Formula;
import com.viettel.ocs.entity.RateTable;
import com.viettel.ocs.entity.RateTableResult;

@SuppressWarnings("serial")
public class FormulaDAO extends BaseDAO<Formula> implements Serializable {

	@Override
	protected Class<Formula> getEntityClass() {
		return Formula.class;
	}

	@SuppressWarnings("unchecked")
	public List<Formula> findFormulasByRateTable(RateTable rateTable) {
		StringBuffer hql = new StringBuffer("SELECT d FROM RateTable a");
		hql.append(" JOIN ResultRateMap b ON b.rateTableId = a.rateTableId");
		hql.append(" JOIN RateTableResult c ON c.rateTableResultId = b.rateTableResultId");
		hql.append(" JOIN Formula d ON d.formulaId = c.formulaId");
		hql.append(" WHERE a.rateTableId = :rateTableId");
		Session session = HibernateUtil.getOpenSession();
		try {
			Query<Formula> query = session.createQuery(hql.toString());
			query.setParameter("rateTableId", rateTable.getRateTableId());
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}
	
	public List<Formula> findByTriggerOcs(long triggerOcsId) {
		
		StringBuffer hql = new StringBuffer("SELECT a FROM Formula a WHERE ");
		hql.append(" a.triggerIds LIKE CONCAT(:triggerOcsId, ',%') OR a.triggerIds LIKE CONCAT('%,', :triggerOcsId)");
		hql.append(" OR a.triggerIds LIKE CONCAT('%,', :triggerOcsId, ',%') OR a.triggerIds = :triggerOcsId");
		Session session = HibernateUtil.getOpenSession();
		try {
			Query<Formula> query = session.createQuery(hql.toString(), Formula.class);
			query.setParameter("triggerOcsId", String.valueOf(triggerOcsId));
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<RateTableResult> findRateTableResultsByRateTable(RateTable rateTable) {
		StringBuffer hql = new StringBuffer("SELECT c FROM RateTable a");
		hql.append(" JOIN ResultRateMap b ON b.rateTableId = a.rateTableId");
		hql.append(" JOIN RateTableResult c ON c.rateTableResultId = b.rateTableResultId");
		hql.append(" WHERE a.rateTableId = :rateTableId");
		Session session = HibernateUtil.getOpenSession();
		try {
			Query<RateTableResult> query = session.createQuery(hql.toString());
			query.setParameter("rateTableId", rateTable.getRateTableId());
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}
}

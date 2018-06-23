package com.viettel.ocs.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.entity.ColumnDecisionTableMap;
import com.viettel.ocs.entity.ColumnDt;
import com.viettel.ocs.entity.DecisionTable;

public class ColumnDecisionTableMapDAO extends BaseDAO<ColumnDecisionTableMap> {

	@Override
	protected Class<ColumnDecisionTableMap> getEntityClass() {
		return ColumnDecisionTableMap.class;
	}

	public ColumnDecisionTableMap findByColumnIdAndDecaisionTableId(ColumnDt columnDt, DecisionTable decisionTable,
			Session session) {
		String hql = "SELECT d FROM ColumnDecisionTableMap d WHERE d.decisionTableId = :decisionTableId AND columnDtId = :columnDtId AND domainId = :domainId";
		try {
			Query<ColumnDecisionTableMap> query = session.createQuery(hql.toString());
			query.setParameter("decisionTableId", decisionTable.getDecisionTableId());
			query.setParameter("columnDtId", columnDt.getColumnId());
			query.setParameter("domainId", getDomainId());
			return query.uniqueResult();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}
	}
}

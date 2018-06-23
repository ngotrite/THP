package com.viettel.ocs.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.entity.ColumnDecisionTableMap;
import com.viettel.ocs.entity.DecisionTable;
import com.viettel.ocs.entity.RowDecisionTableMap;
import com.viettel.ocs.entity.RowDt;

public class RowDtDAO extends BaseDAO<RowDt> {

	@Override
	protected Class<RowDt> getEntityClass() {
		// TODO Auto-generated method stub
		return RowDt.class;
	}

	public void saveRowsOfDecision(List<RowDt> rowDts, DecisionTable decisionTable, Session session) {
		try {

			if (decisionTable != null && decisionTable.getDecisionTableId() != 0 && rowDts != null) {
				List<Long> ids = new ArrayList<Long>();

				for (RowDt rowDt : rowDts) {
					ids.add(rowDt.getRowId());
				}
				this.deleteRowOfDecisionTable(ids, decisionTable, session);

				for (int i = 0; i < rowDts.size(); i++) {
					boolean newRecord = rowDts.get(i).getRowId() == 0L;
					rowDts.get(i).setDomainId(getDomainId());
					rowDts.get(i).setRowIndex(i);
					session.saveOrUpdate(rowDts.get(i));
					if (newRecord) {
						RowDecisionTableMap rowDecisionTableMap = new RowDecisionTableMap(
								decisionTable.getDecisionTableId(), rowDts.get(i).getRowId());
						rowDecisionTableMap.setDomainId(getDomainId());
						session.save(rowDecisionTableMap);
					} else {
						RowDecisionTableMap rowDecisionTableMap = this.findByRowIdAndDecaisionTableId(rowDts.get(i),
								decisionTable, session);
						if (rowDecisionTableMap != null) {
							// session.update(rowDecisionTableMap);
						} else {
							rowDecisionTableMap = new RowDecisionTableMap(decisionTable.getDecisionTableId(),
									rowDts.get(i).getRowId());
							rowDecisionTableMap.setDomainId(getDomainId());
							session.save(rowDecisionTableMap);
						}
					}
				}
			}
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}
	}

	public void deleteRowOfDecisionTable(List<Long> ids, DecisionTable decisionTable, Session session) {
		if (ids.size() == 0) {
			ids.add(-1L);
		}
		// RowDt
		StringBuffer hql_rowdt = new StringBuffer("");
		hql_rowdt.append("DELETE FROM RowDt r");
		hql_rowdt.append(" WHERE r.rowId IN (");

		hql_rowdt.append("SELECT d.rowDtId FROM RowDecisionTableMap d");
		hql_rowdt.append(" WHERE d.decisionTableId = :decisionTableId");
		hql_rowdt.append(" AND d.rowDtId NOT IN (:rowDtIds)");
		hql_rowdt.append(" AND d.domainId = :domainId");

		hql_rowdt.append(")");
		hql_rowdt.append(" AND r.domainId = :domainId");

		try {

			// RowDt ExecuteUpdate
			Query<?> query_rowdt = session.createQuery(hql_rowdt.toString());
			query_rowdt.setParameter("decisionTableId", decisionTable.getDecisionTableId());
			query_rowdt.setParameter("rowDtIds", ids);
			query_rowdt.setParameter("domainId", getDomainId());
			query_rowdt.executeUpdate();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}
	}

	public RowDecisionTableMap findByRowIdAndDecaisionTableId(RowDt rowDt, DecisionTable decisionTable,
			Session session) {
		String hql = "SELECT d FROM RowDecisionTableMap d WHERE d.decisionTableId = :decisionTableId AND rowDtId = :rowDtId AND domainId = :domainId";
		try {
			Query<RowDecisionTableMap> query = session.createQuery(hql.toString());
			query.setParameter("decisionTableId", decisionTable.getDecisionTableId());
			query.setParameter("rowDtId", rowDt.getRowId());
			query.setParameter("domainId", getDomainId());
			return query.uniqueResult();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}
	}

}

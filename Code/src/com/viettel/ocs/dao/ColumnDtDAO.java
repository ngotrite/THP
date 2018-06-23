package com.viettel.ocs.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.entity.ColumnDecisionTableMap;
import com.viettel.ocs.entity.ColumnDt;
import com.viettel.ocs.entity.DecisionTable;
import com.viettel.ocs.entity.RowDt;

public class ColumnDtDAO extends BaseDAO<ColumnDt> {

	@Override
	protected Class<ColumnDt> getEntityClass() {
		return ColumnDt.class;
	}

	public boolean saveColumnsOfDecision(List<ColumnDt> columnDts, List<RowDt> rowDts, DecisionTable decisionTable) {

		if (decisionTable != null && columnDts != null) {
			ColumnDecisionTableMapDAO columnDecisionTableMapDAO = new ColumnDecisionTableMapDAO();
			Session session = HibernateUtil.getOpenSession();
			session.getTransaction().begin();
			try {
				// Save decision table
				for (int i = 0; i < rowDts.size(); i++) {
					if (rowDts.get(i).getDefaultValue()) {
						decisionTable.setDefaultFormulaIndex(Long.valueOf(i));
					}
				}
				decisionTable.setDomainId(getDomainId());
				generateIndexByCat(decisionTable, "decisionTableId", session);
				session.saveOrUpdate(decisionTable);

				// delete
				List<Long> longs = new ArrayList<Long>();
				for (ColumnDt columnDt : columnDts) {
					longs.add(columnDt.getColumnId());
				}
				this.deleteByColumnIdAndDecaisionTableId(longs, decisionTable, session);

				// save col
				for (int i = 0; i < columnDts.size(); i++) {
					boolean newRecord = columnDts.get(i).getColumnId() == 0L;
					columnDts.get(i).setDomainId(getDomainId());
					session.saveOrUpdate(columnDts.get(i));

					if (newRecord) {
						ColumnDecisionTableMap columnDecisionTableMap = new ColumnDecisionTableMap(
								decisionTable.getDecisionTableId(), columnDts.get(i).getColumnId());
						columnDecisionTableMap.setColumnIndex(Long.valueOf(i));
						columnDecisionTableMap.setDomainId(getDomainId());
						session.save(columnDecisionTableMap);
					} else {
						ColumnDecisionTableMap columnDecisionTableMap = columnDecisionTableMapDAO
								.findByColumnIdAndDecaisionTableId(columnDts.get(i), decisionTable, session);
						if (columnDecisionTableMap != null) {
							columnDecisionTableMap.setColumnIndex(Long.valueOf(i));
							session.update(columnDecisionTableMap);

						} else {
							columnDecisionTableMap = new ColumnDecisionTableMap(decisionTable.getDecisionTableId(),
									columnDts.get(i).getColumnId());
							columnDecisionTableMap.setColumnIndex(Long.valueOf(i));
							columnDecisionTableMap.setDomainId(getDomainId());
							session.save(columnDecisionTableMap);
						}
					}
				}

				// save row
				RowDtDAO rowDtDAO = new RowDtDAO();
				rowDtDAO.saveRowsOfDecision(rowDts, decisionTable, session);

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
		return false;
	}

	public void deleteByColumnIdAndDecaisionTableId(List<Long> columnDts, DecisionTable decisionTable,
			Session session) {
		if (columnDts.size() == 0) {
			columnDts.add(-1L);
		}

		StringBuffer hql_columndt = new StringBuffer("");
		hql_columndt.append("DELETE FROM ColumnDt c");
		hql_columndt.append(" WHERE c.columnId IN (");

		hql_columndt.append("SELECT d.columnDtId FROM ColumnDecisionTableMap d");
		hql_columndt.append(" WHERE d.decisionTableId = :decisionTableId");
		hql_columndt.append(" AND d.columnDtId NOT IN (:columnDtIds)");
		hql_columndt.append(" AND d.domainId = :domainId");

		hql_columndt.append(")");
		hql_columndt.append(" AND c.domainId = :domainId");
		try {
			Query<?> query_columndt = session.createQuery(hql_columndt.toString());
			query_columndt.setParameter("decisionTableId", decisionTable.getDecisionTableId());
			query_columndt.setParameter("columnDtIds", columnDts);
			query_columndt.setParameter("domainId", getDomainId());
			query_columndt.executeUpdate();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}		
	}

	public Long getMaxIndexDecisionTable(Session session) {
		try {
			StringBuffer hql = new StringBuffer("SELECT COALESCE(MAX(a.index), 0) FROM DecisionTable a");
			hql.append(" WHERE a.domainId = :domainId");
			Query<Long> query = session.createQuery(hql.toString(), Long.class);
			query.setParameter("domainId", getDomainId());
			return query.getSingleResult();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}		
	}
}

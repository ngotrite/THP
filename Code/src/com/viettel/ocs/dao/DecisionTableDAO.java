package com.viettel.ocs.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.Action;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.ColumnDecisionTableMap;
import com.viettel.ocs.entity.ColumnDt;
import com.viettel.ocs.entity.DecisionTable;
import com.viettel.ocs.entity.NomalizerNormParamMap;
import com.viettel.ocs.entity.NormParam;
import com.viettel.ocs.entity.NormValue;
import com.viettel.ocs.entity.Normalizer;
import com.viettel.ocs.entity.NormalizerNormValueMap;
import com.viettel.ocs.entity.Parameter;
import com.viettel.ocs.entity.ProfilePep;
import com.viettel.ocs.entity.RateTable;
import com.viettel.ocs.entity.RowDecisionTableMap;
import com.viettel.ocs.entity.RowDt;
import com.viettel.ocs.model.CloneIgnoreListModel;
import com.viettel.ocs.model.ColumnDecisionTableMapModel;
import com.viettel.ocs.model.RowDecisionTableMapModel;

public class DecisionTableDAO extends BaseDAO<DecisionTable> {

	@Override
	protected Class<DecisionTable> getEntityClass() {
		return DecisionTable.class;
	}

	@SuppressWarnings("unchecked")
	public List<DecisionTable> findByListCategoryId(List<Long> categoryIds) {
		if (categoryIds.size() == 0) {
			categoryIds.add(-1L);
		}
		String hql = " FROM DecisionTable d WHERE d.categoryId in :categoryIds AND domainId = :domainId order by index ASC";
		Session session = HibernateUtil.getOpenSession();
		try {
			Query<DecisionTable> query = session.createQuery(hql.toString());
			query.setParameterList("categoryIds", categoryIds);
			query.setParameter("domainId", getDomainId());
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	@SuppressWarnings("unchecked")
	public List<DecisionTable> findByCategoryId(Category category) {
		String hql = " FROM DecisionTable d WHERE d.categoryId = :categoryIds AND domainId = :domainId order by index ASC";
		Session session = HibernateUtil.getOpenSession();
		try {
			Query<DecisionTable> query = session.createQuery(hql.toString());
			query.setParameter("categoryIds", category.getCategoryId());
			query.setParameter("domainId", getDomainId());
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	@SuppressWarnings({ "unchecked" })
	public List<ColumnDt> findColumns(DecisionTable decisionTable) {
		StringBuffer hql = new StringBuffer("SELECT c FROM DecisionTable a");
		hql.append(" JOIN ColumnDecisionTableMap b ON b.decisionTableId = a.decisionTableId AND b.domainId =:domainId");
		hql.append(" JOIN ColumnDt c ON c.columnId = b.columnDtId AND c.domainId =:domainId");
		hql.append(" WHERE c.domainId =:domainId");
		hql.append(" AND a.decisionTableId =:decisionTableId");
		hql.append(" ORDER BY b.columnIndex");

		Session session = HibernateUtil.getOpenSession();
		try {
			Query<ColumnDt> query = session.createQuery(hql.toString());
			query.setParameter("domainId", getDomainId());
			query.setParameter("decisionTableId", decisionTable.getDecisionTableId());
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	@SuppressWarnings({ "unchecked" })
	public int countNormalizers(Long decisionTableId) {
		StringBuffer hql = new StringBuffer("SELECT COUNT(d.normalizerId) FROM DecisionTable a");
		hql.append(" JOIN ColumnDecisionTableMap b ON b.decisionTableId = a.decisionTableId");
		hql.append(" JOIN ColumnDt c ON c.columnId = b.columnDtId ");
		hql.append(" JOIN Normalizer d ON d.normalizerId = c.normalizerId");
		hql.append(" WHERE b.domainId =:domainId AND c.domainId =:domainId AND d.domainId =:domainId");
		hql.append(" AND a.decisionTableId =:decisionTableId");
		hql.append(" ORDER BY c.columnIndex");

		Session session = HibernateUtil.getOpenSession();
		try {
			Query<Normalizer> query = session.createQuery(hql.toString());
			query.setParameter("domainId", getDomainId());
			query.setParameter("decisionTableId", decisionTableId);
			List lst = query.getResultList();
			if (lst.size() > 0)
				return ((Number) lst.get(0)).intValue();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
		return 0;
	}

	public List<Normalizer> findNormalizers(DecisionTable decisionTable) {
		return findNormalizers(decisionTable.getDecisionTableId());
	}

	public List<Normalizer> findNormalizers(long decisionTableId) {
		StringBuffer hql = new StringBuffer("SELECT DISTINCT(d) FROM DecisionTable a");
		hql.append(" JOIN ColumnDecisionTableMap b ON b.decisionTableId = a.decisionTableId");
		hql.append(" JOIN ColumnDt c ON c.columnId = b.columnDtId ");
		hql.append(" JOIN Normalizer d ON d.normalizerId = c.normalizerId");
		hql.append(" WHERE b.domainId =:domainId AND c.domainId =:domainId AND d.domainId =:domainId");
		hql.append(" AND a.decisionTableId =:decisionTableId");
		hql.append(" ORDER BY c.columnIndex");

		Session session = HibernateUtil.getOpenSession();
		try {
			Query<Normalizer> query = session.createQuery(hql.toString(), Normalizer.class);
			query.setParameter("domainId", getDomainId());
			query.setParameter("decisionTableId", decisionTableId);
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public List<Normalizer> findNormalizers(DecisionTable decisionTable, Session session) {
		StringBuffer hql = new StringBuffer("SELECT DISTINCT(d) FROM DecisionTable a");
		hql.append(" JOIN ColumnDecisionTableMap b ON b.decisionTableId = a.decisionTableId");
		hql.append(" JOIN ColumnDt c ON c.columnId = b.columnDtId ");
		hql.append(" JOIN Normalizer d ON d.normalizerId = c.normalizerId");
		hql.append(" WHERE b.domainId =:domainId AND c.domainId =:domainId AND d.domainId =:domainId");
		hql.append(" AND a.decisionTableId =:decisionTableId");
		hql.append(" ORDER BY c.columnIndex");
		try {

			Query<Normalizer> query = session.createQuery(hql.toString(), Normalizer.class);
			query.setParameter("domainId", getDomainId());
			query.setParameter("decisionTableId", decisionTable.getDecisionTableId());
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}
	}

	public List<NormValue> findNormValueByNormalizer(Long normalizerId) {
		StringBuffer hql = new StringBuffer("SELECT c FROM Normalizer a");
		hql.append(" JOIN NormalizerNormValueMap b ON b.normalizerId = a.normalizerId");
		hql.append(" JOIN NormValue c ON c.normValueId = b.normValueId");
		hql.append(" WHERE b.domainId =:domainId AND c.domainId =:domainId");
		hql.append(" AND a.normalizerId =:normalizerId");
		hql.append(" ORDER BY c.normValueIndex");

		Session session = HibernateUtil.getOpenSession();
		try {
			Query<NormValue> query = session.createQuery(hql.toString(), NormValue.class);
			query.setParameter("domainId", getDomainId());
			query.setParameter("normalizerId", normalizerId);
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	@SuppressWarnings({ "unchecked" })
	public List<RowDt> findRowByDecisionTable(DecisionTable decisionTable) {
		StringBuffer hql = new StringBuffer("SELECT c FROM DecisionTable a");
		hql.append(" JOIN RowDecisionTableMap b ON b.decisionTableId = a.decisionTableId");
		hql.append(" JOIN RowDt c ON c.rowId = b.rowDtId ");
		hql.append(" WHERE b.domainId =:domainId AND c.domainId =:domainId");
		hql.append(" AND a.decisionTableId =:decisionTableId");
		hql.append(" ORDER BY c.rowIndex");

		Session session = HibernateUtil.getOpenSession();
		try {
			Query<RowDt> query = session.createQuery(hql.toString());
			query.setParameter("domainId", getDomainId());
			query.setParameter("decisionTableId", decisionTable.getDecisionTableId());
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	/**
	 * Clone DT
	 * 
	 * @author THANHND
	 * @param decisionTable
	 * @param parameters
	 * @param replaceParam
	 * @param ignoreList
	 * @param usingParams
	 * @param isClone
	 * @param session
	 * @throws CloneNotSupportedException
	 */
	public DecisionTable cloneDT(DecisionTable decisionTable, boolean cloneNormalizer, String suffix,
			boolean replaceParam, List<Parameter> parameters, CloneIgnoreListModel ignoreList, boolean isClone,
			CloneIgnoreListModel usingParams, Session session) throws CloneNotSupportedException {
		try {
			if (!isClone && usingParams.isIgnore(decisionTable) == null) {
				return decisionTable;
			}

			DecisionTable decisionTableToClone = decisionTable.clone();
			decisionTableToClone.setGenerated(!isClone);

			List<ColumnDecisionTableMapModel> columnDecisionTableMapModels = this
					.findColumnsDecisionTable(decisionTableToClone, session);

			List<RowDecisionTableMapModel> rowDecisionTableMapModels = this
					.findByRowIdAndDecaisionTableId(decisionTableToClone, session);

			decisionTableToClone.setDecisionTableId(0L);
			decisionTableToClone.setDecisionTableName(generateNameObject("decisionTableName",
					decisionTableToClone.getDecisionTableName() + suffix, 0, session));
			decisionTableToClone.setIndex(this.getMaxIndex(session) + 1);
			session.save(decisionTableToClone);

			for (ColumnDecisionTableMapModel columnDecisionTableMapModel : columnDecisionTableMapModels) {
				ColumnDt columnDt = columnDecisionTableMapModel.getColumnDt().clone();
				columnDt.setColumnId(0L);

				if (cloneNormalizer) {
					NormalizerDAO normalizerDAO = new NormalizerDAO();
					Normalizer normalizer = normalizerDAO.get(columnDecisionTableMapModel.getColumnDt().getNormalizerId())
							.clone();
					Long normalizerClonedId = ignoreList.isIgnore(normalizer);

					if (!isClone && usingParams.isIgnore(normalizer) == null) {
						normalizerClonedId = normalizer.getNormalizerId();
					}

					if (normalizerClonedId == null) {
						NormParamDAO normParamDAO = new NormParamDAO();
						List<NormParam> normParams = normParamDAO.findNormParamByNormId(normalizer.getNormalizerId());

						NormValueDAO normValueDAO = new NormValueDAO();
						List<NormValue> normValues = normValueDAO.findNormValueByNormId(normalizer.getNormalizerId());

						normalizer.setNormalizerId(0L);
						normalizer.setGenerated(!isClone);
						normalizer.setNormalizerName(generateNameObject(Normalizer.class, "normalizerName",
								normalizer.getNormalizerName() + suffix, 0, session));
						normalizer.setPosIndex(normalizerDAO.getMaxIndex(session) + 1);
						session.save(normalizer);

						for (NormParam normParam : normParams) {
							NormParam normParamToClone = normParam.clone();
							if (replaceParam) {
								replaceParamOfNormParam(normParamToClone, parameters);
							}
							normParamToClone.setNormParamId(0L);
							session.save(normParamToClone);

							NomalizerNormParamMap nomalizerNormParamMap = new NomalizerNormParamMap(
									normalizer.getNormalizerId(), normParamToClone.getNormParamId());
							nomalizerNormParamMap.setDomainId(getDomainId());
							session.save(nomalizerNormParamMap);
						}

						for (NormValue normValue : normValues) {
							NormValue normValueToClone = normValue.clone();
							normValueToClone.setNormValueId(0L);
							session.save(normValueToClone);

							NormalizerNormValueMap normalizerNormValueMap = new NormalizerNormValueMap(
									normalizer.getNormalizerId(), normValueToClone.getNormValueId());
							normalizerNormValueMap.setDomainId(getDomainId());
							session.save(normalizerNormValueMap);
						}
						normalizerClonedId = normalizer.getNormalizerId();
						ignoreList.getNormalizers().put(columnDecisionTableMapModel.getColumnDt().getNormalizerId(), normalizerClonedId);
					}
					columnDt.setNormalizerId(normalizerClonedId);
				}

				session.save(columnDt);

				ColumnDecisionTableMap columnDecisionTableMap = columnDecisionTableMapModel.getColumnDecisionTableMap()
						.clone();
				columnDecisionTableMap.setColumnDecisionTableMapId(0L);
				columnDecisionTableMap.setColumnDtId(columnDt.getColumnId());
				columnDecisionTableMap.setDecisionTableId(decisionTableToClone.getDecisionTableId());
				session.save(columnDecisionTableMap);
			}

			for (RowDecisionTableMapModel rowDecisionTableMapModel : rowDecisionTableMapModels) {
				RowDt rowDt = rowDecisionTableMapModel.getRowDt().clone();
				rowDt.setRowId(0L);
				session.save(rowDt);

				RowDecisionTableMap rowDecisionTableMap = rowDecisionTableMapModel.getRowDecisionTableMap().clone();
				rowDecisionTableMap.setRowDecisionTableMapId(0L);
				rowDecisionTableMap.setRowDtId(rowDt.getRowId());
				rowDecisionTableMap.setDecisionTableId(decisionTableToClone.getDecisionTableId());
				session.save(rowDecisionTableMap);
			}

			return decisionTableToClone;
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}
	}

	private void replaceParamOfNormParam(NormParam normParamToClone, List<Parameter> parameters) {
		if (normParamToClone.getConfigInput()
				.contains(com.viettel.ocs.constant.Normalizer.UsingNormParam.IS_USING_PARAMETER)) {
			String[] items = normParamToClone.getConfigInput().split(";");
			boolean replaced = false;
			for (int i = 0; i < items.length; i++) {
				if (items[i].contains(com.viettel.ocs.constant.Normalizer.UsingNormParam.PARAMETER_ID)) {
					Parameter parameter = this.findPramInList(parameters, Long.valueOf(
							items[i].replace(com.viettel.ocs.constant.Normalizer.UsingNormParam.PARAMETER_ID, "")));
					if (parameter != null) {
						items[i] = items[i].replace(
								items[i].replace(com.viettel.ocs.constant.Normalizer.UsingNormParam.PARAMETER_ID, ""),
								parameter.getParameterValue());
						replaced = true;
					}

				}

				if (items[i].contains(com.viettel.ocs.constant.Normalizer.UsingNormParam.IS_USING_PARAMETER)
						&& replaced) {
					items[i] = com.viettel.ocs.constant.Normalizer.UsingNormParam.NOT_IS_USING_PARAMETER;
				}
			}
			normParamToClone.setConfigInput(String.join(";", items));
			return;
		}

		if (normParamToClone.getConfigInput()
				.contains(com.viettel.ocs.constant.Normalizer.UsingNormParam.START_IS_PARAMETER)) {
			String[] items = normParamToClone.getConfigInput().split(";");
			boolean replaced = false;
			for (int i = 0; i < items.length; i++) {
				if (items[i].contains("start:")) {
					Parameter parameter = this.findPramInList(parameters, Long.valueOf(items[i].replace("start:", "")));
					if (parameter != null) {
						items[i] = items[i].replace(items[i].replace("start:", ""), parameter.getParameterValue());
						replaced = true;
					}
				}

				if (items[i].contains(com.viettel.ocs.constant.Normalizer.UsingNormParam.START_IS_PARAMETER)
						&& replaced) {
					items[i] = com.viettel.ocs.constant.Normalizer.UsingNormParam.NOT_START_IS_PARAMETER;
				}
			}
			normParamToClone.setConfigInput(String.join(";", items));
		}

		if (normParamToClone.getConfigInput()
				.contains(com.viettel.ocs.constant.Normalizer.UsingNormParam.END_IS_PARAMETER)) {
			String[] items = normParamToClone.getConfigInput().split(";");
			boolean replaced = false;
			for (int i = 0; i < items.length; i++) {

				if (items[i].contains("end:")) {
					Parameter parameter = this.findPramInList(parameters, Long.valueOf(items[i].replace("end:", "")));
					if (parameter != null) {
						items[i] = items[i].replace(items[i].replace("end:", ""), parameter.getParameterValue());
						replaced = true;
					}
				}

				if (items[i].contains(com.viettel.ocs.constant.Normalizer.UsingNormParam.END_IS_PARAMETER)
						&& replaced) {
					items[i] = com.viettel.ocs.constant.Normalizer.UsingNormParam.NOT_END_IS_PARAMETER;
				}
			}
			normParamToClone.setConfigInput(String.join(";", items));
		}
	}

	private Parameter findPramInList(List<Parameter> parameters, Long parameterId) {
		for (Parameter parameter : parameters) {
			if (parameter.getParameterId() == parameterId) {
				return parameter;
			}
		}
		return null;
	}

	@SuppressWarnings({ "unchecked" })
	private List<ColumnDecisionTableMapModel> findColumnsDecisionTable(DecisionTable decisionTable, Session session) {
		StringBuffer hql = new StringBuffer(
				"SELECT new com.viettel.ocs.model.ColumnDecisionTableMapModel(b, c) FROM DecisionTable a");
		hql.append(" JOIN ColumnDecisionTableMap b ON b.decisionTableId = a.decisionTableId AND b.domainId =:domainId");
		hql.append(" JOIN ColumnDt c ON c.columnId = b.columnDtId AND c.domainId =:domainId");
		hql.append(" WHERE c.domainId =:domainId");
		hql.append(" AND a.decisionTableId =:decisionTableId");
		hql.append(" ORDER BY b.columnIndex");
		try {

			Query<ColumnDecisionTableMapModel> query = session.createQuery(hql.toString());
			query.setParameter("domainId", getDomainId());
			query.setParameter("decisionTableId", decisionTable.getDecisionTableId());
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}
	}

	private List<RowDecisionTableMapModel> findByRowIdAndDecaisionTableId(DecisionTable decisionTable,
			Session session) {
		StringBuffer hql = new StringBuffer(
				"SELECT new com.viettel.ocs.model.RowDecisionTableMapModel(b, c) FROM DecisionTable a");
		hql.append(" JOIN RowDecisionTableMap b ON b.decisionTableId = a.decisionTableId");
		hql.append(" JOIN RowDt c ON c.rowId = b.rowDtId ");
		hql.append(" WHERE b.domainId =:domainId AND c.domainId =:domainId");
		hql.append(" AND a.decisionTableId =:decisionTableId");
		hql.append(" ORDER BY c.rowIndex");
		try {

			Query<RowDecisionTableMapModel> query = session.createQuery(hql.toString());
			query.setParameter("domainId", getDomainId());
			query.setParameter("decisionTableId", decisionTable.getDecisionTableId());
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}
	}

	public List<DecisionTable> findByListRateTableId(List<Long> RTIds) {
		if (RTIds.size() == 0) {
			RTIds.add(-1L);
		}
		String hql = " SELECT DISTINCT d FROM DecisionTable d JOIN RateTable rt ON rt.decisionTableId = d.decisionTableId";
		hql += " WHERE rt.rateTableId IN (:rateTableId)";
		Session session = HibernateUtil.getOpenSession();
		try {
			Query<DecisionTable> query = session.createQuery(hql.toString());
			query.setParameterList("rateTableId", RTIds);
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public List<DecisionTable> findDecisionTableByListNormalizersId(List<Long> normalizersId) {

		if (normalizersId.size() == 0)
			normalizersId.add(-1L);
		StringBuffer hql = new StringBuffer("SELECT DISTINCT(d) FROM Normalizer a");
		hql.append(" JOIN ColumnDt b ON b.normalizerId = a.normalizerId ");
		hql.append(" JOIN ColumnDecisionTableMap c ON c.columnDtId = b.columnId");
		hql.append(" JOIN DecisionTable d ON d.decisionTableId = c.decisionTableId");
		hql.append(" AND a.normalizerId IN (:normalizerId)");

		Session session = HibernateUtil.getOpenSession();
		try {
			Query<DecisionTable> query = session.createQuery(hql.toString());
			query.setParameter("normalizerId", normalizersId);
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public int countDecisionTable() {
		String hql = "SELECT COUNT(a) FROM DecisionTable a WHERE  a.domainId =:domainId ";
		Session session = HibernateUtil.getOpenSession();
		try {
			Query<Long> query = session.createQuery(hql, Long.class);
			query.setParameter("domainId", getDomainId());
			return query.getSingleResult().intValue();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public DecisionTable cloneDT(DecisionTable decisionTable, boolean cloneNormalizer, String suffix) throws Exception {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		DecisionTable decisionTableCloned = null;
		try {
			decisionTableCloned = this.cloneDT(decisionTable, cloneNormalizer, suffix, false,
					new ArrayList<Parameter>(), new CloneIgnoreListModel(), true, new CloneIgnoreListModel(), session);
			session.getTransaction().commit();
			return decisionTableCloned;
		} catch (Exception e) {
			
			session.getTransaction().rollback();
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public Long getMaxIndex(Session session) {
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

	public Long getMaxIndex() {
		Session session = HibernateUtil.getOpenSession();
		try {
			StringBuffer hql = new StringBuffer("SELECT COALESCE(MAX(a.index), 0) FROM DecisionTable a");
			hql.append(" WHERE a.domainId = :domainId");
			Query<Long> query = session.createQuery(hql.toString(), Long.class);
			query.setParameter("domainId", getDomainId());
			return query.getSingleResult();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public Boolean processMoveUpDown(DecisionTable moveItem, DecisionTable item) {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			Long tmp = moveItem.getIndex();
			moveItem.setIndex(item.getIndex());
			item.setIndex(tmp);
			session.saveOrUpdate(moveItem);
			session.saveOrUpdate(item);
			session.getTransaction().commit();
		} catch (Exception e) {
			session.getTransaction().rollback();
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
		return true;
	}

	public List<DecisionTable> findByCategoryId(Long categoryId) {
		List<DecisionTable> lst = null;
		String[] cols = { "categoryId" };
		Operator[] operators = { Operator.EQ };
		Object[] values = { categoryId };
		lst = findByConditions(cols, operators, values, " index ASC");
		return lst;
	}

	public List<DecisionTable> loadDecisionTableByDecisionTableName(Long DecisionTableID, String DecisionTableName) {
		String[] col = { "decisionTableId", "decisionTableName" };
		Operator[] ope = { Operator.NOTEQ, Operator.EQ };
		Object[] val = { DecisionTableID, DecisionTableName };
		String oder = "decisionTableName";
		return this.findByConditions(col, ope, val, oder);
	}

	public boolean moveTo(DecisionTable selectedDecisionContext) {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			generateIndexByCat(selectedDecisionContext, "decisionTableId", session);
			session.update(selectedDecisionContext);
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

	public boolean removeColumnByNormalizerAndDT(DecisionTable decisionTable, Normalizer normalizer) {
		StringBuffer hql = new StringBuffer();
		hql.append("DELETE FROM ColumnDt a");
		hql.append(" WHERE a.columnId IN (");
		hql.append(" SELECT b.columnDtId FROM ColumnDecisionTableMap b");
		hql.append(" WHERE b.decisionTableId = :decisionTableId)");
		hql.append(" AND normalizerId =:normalizerId");

		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			Query<?> query = session.createQuery(hql.toString());
			query.setParameter("decisionTableId", decisionTable.getDecisionTableId());
			query.setParameter("normalizerId", normalizer.getNormalizerId());
			query.executeUpdate();

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

	public void deleteColRowAndNormalizerByDT(DecisionTable decisionTable, Session session) {
		try {
			decisionTable = session.get(DecisionTable.class, decisionTable.getDecisionTableId());
			if (decisionTable != null && decisionTable.isGenerated() != null && decisionTable.isGenerated()) {

				ColumnDtDAO columnDtDAO = new ColumnDtDAO();

				columnDtDAO.deleteByColumnIdAndDecaisionTableId(new ArrayList<Long>(), decisionTable, session);

				RowDtDAO rowDtDAO = new RowDtDAO();
				rowDtDAO.deleteRowOfDecisionTable(new ArrayList<Long>(), decisionTable, session);

				List<Normalizer> normalizers = findNormalizers(decisionTable, session);

				NormalizerDAO normalizerDAO = new NormalizerDAO();
				for (Normalizer normalizer : normalizers) {
					normalizerDAO.deleteItemByNormalizer(normalizer, session);
				}
				session.delete(decisionTable);
			}
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}		
	}

	public List<DecisionTable> findByListCategoryIdAndGened(List<Long> lstAllCatID) {
		String hql = "SELECT a FROM DecisionTable a";
		hql += " WHERE a.categoryId IN (:categoryIds) AND (a.generated is false OR a.generated is NULL)";

		Session session = HibernateUtil.getOpenSession();
		try {

			Query<DecisionTable> query = session.createQuery(hql, DecisionTable.class);
			query.setParameterList("categoryIds", lstAllCatID);
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public void deleteDecisionAndMapColumns(DecisionTable decisionTable) {
		String hqlDeleteDT = "DELETE FROM DecisionTable a";
		hqlDeleteDT += " WHERE a.decisionTableId = :decisionTableId";

		String hqlColumns = "DELETE FROM ColumnDt a";
		hqlColumns += " WHERE a.columnId IN (SELECT b.columnDtId FROM ColumnDecisionTableMap b WHERE b.decisionTableId = :decisionTableId)";

		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			Query<?> queryDeleteColumns = session.createQuery(hqlColumns);
			queryDeleteColumns.setParameter("decisionTableId", decisionTable.getDecisionTableId());
			queryDeleteColumns.executeUpdate();

			Query<?> queryDeleteDT = session.createQuery(hqlDeleteDT);
			queryDeleteDT.setParameter("decisionTableId", decisionTable.getDecisionTableId());
			queryDeleteDT.executeUpdate();

			session.getTransaction().commit();
		} catch (Exception e) {
			
			session.getTransaction().rollback();
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}
}

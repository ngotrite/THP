package com.viettel.ocs.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.constant.Normalizer.RateTableState;
import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.ActionPriceComponentMap;
import com.viettel.ocs.entity.BalType;
import com.viettel.ocs.entity.BlockRateTableMap;
import com.viettel.ocs.entity.DecisionTable;
import com.viettel.ocs.entity.DynamicReserve;
import com.viettel.ocs.entity.DynamicReserveRateTableMap;
import com.viettel.ocs.entity.Formula;
import com.viettel.ocs.entity.Parameter;
import com.viettel.ocs.entity.RateTable;
import com.viettel.ocs.entity.RateTableDump;
import com.viettel.ocs.entity.RateTableResult;
import com.viettel.ocs.entity.ResultRateMap;
import com.viettel.ocs.entity.RowDt;
import com.viettel.ocs.model.CloneIgnoreListModel;
import com.viettel.ocs.model.FormulaViewModel;

public class RateTableDAO extends BaseDAO<RateTable> implements Serializable {

	public int countRTByDynamicReserve(Long dynamicReserveId) {
		List lst = null;
		String hql = "SELECT COUNT(rt.rateTableId) FROM RateTable rt JOIN DynamicReserveRateTableMap map ON map.rateTableId = rt.rateTableId ";
		hql += " WHERE map.dynamicReserveId = :dynamicReserveId";

		Session session = HibernateUtil.getOpenSession();
		try {
			Query query = session.createQuery(hql);
			query.setParameter("dynamicReserveId", dynamicReserveId);
			lst = query.getResultList();
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

	public int countRateTableByBlock(Long blockId) {

		List lst = null;
		String hql = "SELECT COUNT(rt.rateTableId) FROM RateTable rt JOIN BlockRateTableMap map ON map.rateTableId = rt.rateTableId ";
		hql += " WHERE map.blockId = :blockId";

		Session session = HibernateUtil.getOpenSession();
		try {

			Query query = session.createQuery(hql);
			query.setParameter("blockId", blockId);
			lst = query.getResultList();
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

	public int countRateTableBySPC(Long sortPriceComponentId) {

		List lst = null;
		String hql = "SELECT COUNT(rt.rateTableId) FROM RateTable rt JOIN SortPriceRateTableMap map ON map.rateTableId = rt.rateTableId ";
		hql += " WHERE map.sortPriceComponentId = :sortPriceComponentId";

		Session session = HibernateUtil.getOpenSession();
		try {

			Query query = session.createQuery(hql);
			query.setParameter("sortPriceComponentId", sortPriceComponentId);
			lst = query.getResultList();
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

	public List<RateTable> findRateTableByBlock(Long blockId) {

		List<RateTable> lst = null;
		String hql = "SELECT rt FROM RateTable rt JOIN BlockRateTableMap map ON map.rateTableId = rt.rateTableId ";
		hql += " WHERE map.blockId = :blockId";
		hql += " ORDER BY map.rateTableIndex";

		Session session = HibernateUtil.getOpenSession();
		try {

			Query query = session.createQuery(hql);
			query.setParameter("blockId", blockId);
			lst = query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}

		return lst;
	}

	public List<RateTable> findRateTableByDynamicReserve(Long dynamicReserveId) {

		List<RateTable> lst = null;
		String hql = "SELECT rt FROM RateTable rt JOIN DynamicReserveRateTableMap map ON map.rateTableId = rt.rateTableId ";
		hql += " WHERE map.dynamicReserveId = :dynamicReserveId AND map.domainId=:domainId ORDER BY map.rateTableIndex ASC";

		Session session = HibernateUtil.getOpenSession();
		try {
			Query<RateTable> query = session.createQuery(hql);
			query.setParameter("dynamicReserveId", dynamicReserveId);
			query.setParameter("domainId", getDomainId());
			lst = query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}

		return lst;
	}

	public List<RateTable> findRateTableBySPC(Long sortPriceComponentId) {

		List<RateTable> lst = null;
		String hql = "SELECT rt FROM RateTable rt JOIN SortPriceRateTableMap map ON map.rateTableId = rt.rateTableId ";
		hql += " WHERE map.sortPriceComponentId = :sortPriceComponentId";
		hql += " ORDER BY map.rateTableIndex";

		Session session = HibernateUtil.getOpenSession();
		try {
			Query query = session.createQuery(hql);
			query.setParameter("sortPriceComponentId", sortPriceComponentId);
			lst = query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}

		return lst;
	}

	public List<RateTable> findRateTableByCategoryId(Long categoryId) {

		List<RateTable> lst = null;
		String[] cols = { "categoryId" };
		Operator[] operators = { Operator.EQ };
		Object[] values = { categoryId };
		lst = findByConditions(cols, operators, values, "index");
		return lst;

	}

	public List<RateTable> findByListCategory(List<Long> lstCatID) {

		String[] cols = { "categoryId" };
		Operator[] operators = { Operator.IN };
		List<Long> lstCatIDParam = new ArrayList<Long>();
		lstCatIDParam.addAll(lstCatID);
		if (lstCatIDParam.size() == 0)
			lstCatIDParam.add(-1L);
		Object[] values = { lstCatIDParam };
		String oder = " index ASC";
		return super.findByConditions(cols, operators, values, oder);
	}

	public boolean deleteDynamicReserveRateTableMapByRateTableId(RateTable rateTable, Long dynamicReserveId) {
		if (rateTable != null) {
			Session session = HibernateUtil.getOpenSession();
			try {
				session.getTransaction().begin();
				session.delete(rateTable);
				StringBuffer hql = new StringBuffer("DELETE FROM DynamicReserveRateTableMap a");
				hql.append(" WHERE a.rateTableId =:rateTableId)");
				hql.append(" AND a.dynamicReserveId = :dynamicReserveId");
				hql.append(" AND a.domainId = :domainId");

				Query<ActionPriceComponentMap> query = session.createQuery(hql.toString());
				query.setParameter("rateTableId", rateTable.getRateTableId());
				query.setParameter("dynamicReserveId", dynamicReserveId);
				query.setParameter("domainId", getDomainId());
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
		return false;
	}

	@Override
	protected Class<RateTable> getEntityClass() {
		return RateTable.class;
	}

	public boolean saveRateTableAndMap(RateTable rateTable, FormulaViewModel defaultFormulaModel,
			FormulaViewModel stateFormulaModel, List<RowDt> rowDts, Map<Long, FormulaViewModel> tableFormulaModels) {
		if (rateTable != null) {
			Session session = HibernateUtil.getOpenSession();
			session.getTransaction().begin();
			try {
				// Save rate table
				rateTable.setDomainId(getDomainId());
				generateIndexByCat(rateTable, "rateTableId", session);

				// Save Formula
				if (defaultFormulaModel.isApplied()) {
					Formula defaultFormula = defaultFormulaModel.getFormula();
					defaultFormula.setDomainId(getDomainId());
					session.saveOrUpdate(defaultFormula);
					rateTable.setDefaultFormulaId(defaultFormula.getFormulaId());
				} else if (rateTable.getDefaultFormulaId() != null && rateTable.getDefaultFormulaId() != 0L) {
					Formula formula = session.get(Formula.class, rateTable.getDefaultFormulaId());
					session.delete(formula);
					rateTable.setDefaultFormulaId(null);
				}

				if (rateTable.getState() == RateTableState.FORMULA) {
					Formula stateFormula = stateFormulaModel.getFormula();
					stateFormula.setDomainId(getDomainId());
					session.saveOrUpdate(stateFormula);
					rateTable.setFormulaId(stateFormula.getFormulaId());
				} else if (rateTable.getFormulaId() != null && rateTable.getFormulaId() != 0L) {
					Formula formula = session.get(Formula.class, rateTable.getFormulaId());
					session.delete(formula);
					rateTable.setFormulaId(null);
				}

				session.saveOrUpdate(rateTable);

				// DELETE ALL
				this.deleteFormulaTable(rateTable, session);
				for (RowDt rowDt : rowDts) {
					FormulaViewModel formulaViewModel = tableFormulaModels.get(rowDt.getRowId());
					if (formulaViewModel != null) {
						Formula formula = formulaViewModel.getFormula();
						if (formula != null) {
							formula.setDomainId(getDomainId());
							session.save(formula);

							RateTableResult rateTableResult = new RateTableResult(Long.valueOf(rowDt.getRowIndex()),
									formula.getFormulaId());
							rateTableResult.setDomainId(getDomainId());
							session.save(rateTableResult);

							ResultRateMap resultRateMap = new ResultRateMap(rateTable.getRateTableId(),
									rateTableResult.getRateTableResultId(), getDomainId());
							session.save(resultRateMap);
						}
					}
				}

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

	@SuppressWarnings({ "unchecked", "deprecation" })
	public List<FormulaViewModel> findFormulaTable(RateTable rateTable) {
		StringBuffer hql = new StringBuffer(
				"SELECT new com.viettel.ocs.model.FormulaViewModel(d, c.rowIndex, f) FROM RateTable a");
		hql.append(" JOIN ResultRateMap b ON b.rateTableId = a.rateTableId");
		hql.append(" JOIN RateTableResult c ON c.rateTableResultId = b.rateTableResultId");
		hql.append(" JOIN Formula d ON d.formulaId = c.formulaId");
		hql.append(" LEFT JOIN UnitType f ON f.unitTypeId = a.unitTypeId");
		hql.append(" WHERE d.domainId =:domainId");
		hql.append(" AND a.rateTableId =:rateTableId");
		hql.append(" ORDER BY c.rowIndex");

		Session session = HibernateUtil.getOpenSession();
		try {
			Query<FormulaViewModel> query = session.createQuery(hql.toString());
			query.setParameter("domainId", getDomainId());
			query.setParameter("rateTableId", rateTable.getRateTableId());
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	@SuppressWarnings("unchecked")
	public void deleteFormulaTable(RateTable rateTable, Session session) {
		StringBuffer hql = new StringBuffer(
				"DELETE FROM Formula a WHERE a.formulaId IN (SELECT b.formulaId FROM RateTableResult b");
		hql.append(" JOIN ResultRateMap c ON c.rateTableResultId = b.rateTableResultId");
		hql.append(" JOIN RateTable d ON d.rateTableId = c.rateTableId");
		hql.append(" WHERE a.domainId =:domainId");
		hql.append(" AND d.rateTableId =:rateTableId)");
		try {

			Query<Formula> query = session.createQuery(hql.toString());
			query.setParameter("domainId", getDomainId());
			query.setParameter("rateTableId", rateTable.getRateTableId());
			query.executeUpdate();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}
	}

	public boolean checkName(RateTable rateTable) {
		String hql = "SELECT COUNT(*) FROM RateTable a WHERE a.rateTableName LIKE :rateTableName AND a.rateTableId != :rateTableId AND a.domainId = :domainId";

		Session session = HibernateUtil.getOpenSession();
		try {

			Query<Long> query = session.createQuery(hql);
			query.setParameter("rateTableName", rateTable.getRateTableName());
			query.setParameter("rateTableId", rateTable.getRateTableId());
			query.setParameter("domainId", getDomainId());
			int count = query.uniqueResult().intValue();
			if (count > 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public Long getMaxIndex(Session session) {
		StringBuffer hql = new StringBuffer("SELECT COALESCE(MAX(a.index), 0) FROM RateTable a");
		hql.append(" WHERE a.domainId = :domainId");
		try {

			Query<Long> query = session.createQuery(hql.toString(), Long.class);
			query.setParameter("domainId", getDomainId());
			return query.getSingleResult();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}
	}

	/**
	 * Clone rate table
	 * 
	 * @author THANHND
	 * @param rateTable
	 * @param cloneDT
	 * @param replaceParam
	 * @param parameters
	 * @param ignoreList
	 * @param usingParams
	 * @param session
	 * @throws CloneNotSupportedException
	 */

	public RateTable cloneRateTable(RateTable rateTable, boolean cloneDT, boolean cloneNormalizer, String suffix,
			boolean replaceParam, List<Parameter> parameters, CloneIgnoreListModel ignoreList, boolean isClone,
			CloneIgnoreListModel usingParams, Session session) throws CloneNotSupportedException {

		if (!isClone && usingParams.isIgnore(rateTable) == null) {
			return rateTable;
		}
		try {

			RateTable rateTableToClone = rateTable.clone();
			rateTableToClone.setGenerated(!isClone);

			rateTableToClone.setIndex(this.getMaxIndex(session) + 1);
			rateTableToClone.setRateTableName(
					generateNameObject("rateTableName", rateTableToClone.getRateTableName() + suffix, 0, session));

			// Level DecisionTable - 6
			if (cloneDT && rateTableToClone.getDecisionTableId() != null) {
				DecisionTable decisionTable = session.get(DecisionTable.class, rateTableToClone.getDecisionTableId())
						.clone();
				DecisionTableDAO decisionTableDAO = new DecisionTableDAO();
				Long decisionTableClonedId = ignoreList.isIgnore(decisionTable);
				if (decisionTableClonedId == null) {
					decisionTableClonedId = decisionTableDAO.cloneDT(decisionTable, cloneNormalizer, suffix, replaceParam,
							parameters, ignoreList, isClone, usingParams, session).getDecisionTableId();

					ignoreList.getDecisionTables().put(decisionTable.getDecisionTableId(), decisionTableClonedId);
				}

				rateTableToClone.setDecisionTableId(decisionTableClonedId);
			}

			FormulaDAO formulaDAO = new FormulaDAO();

			Formula defaultFormula = formulaDAO.get(rateTable.getDefaultFormulaId());
			Formula stateFormula = formulaDAO.get(rateTable.getFormulaId());
			List<Formula> formulas = this.findFormulaByRateTable(rateTableToClone, session);

			if (defaultFormula != null) {
				Formula defaultFormulaToClone = defaultFormula.clone();
				if (replaceParam) {
					this.replaceParametersOfFormula(defaultFormulaToClone, parameters);
				}
				defaultFormulaToClone.setFormulaId(0L);
				session.save(defaultFormulaToClone);
				rateTableToClone.setDefaultFormulaId(defaultFormulaToClone.getFormulaId());
			}

			if (stateFormula != null) {
				Formula stateFormulaToClone = stateFormula.clone();
				if (replaceParam) {
					this.replaceParametersOfFormula(stateFormulaToClone, parameters);
				}
				stateFormulaToClone.setFormulaId(0L);
				session.save(stateFormulaToClone);
				rateTableToClone.setFormulaId(stateFormulaToClone.getFormulaId());
			}

			rateTableToClone.setRateTableId(0L);
			session.save(rateTableToClone);

			for (Formula formula : formulas) {
				Formula formulaToClone = formula.clone();
				if (replaceParam) {
					this.replaceParametersOfFormula(formulaToClone, parameters);
				}
				formulaToClone.setFormulaId(0L);
				session.save(formulaToClone);

				RateTableResult rateTableResult = new RateTableResult(Long.valueOf(formulas.indexOf(formula)),
						formulaToClone.getFormulaId());
				rateTableResult.setDomainId(getDomainId());
				session.save(rateTableResult);

				ResultRateMap resultRateMap = new ResultRateMap(rateTableToClone.getRateTableId(),
						rateTableResult.getRateTableResultId(), getDomainId());
				session.save(resultRateMap);
			}

			return rateTableToClone;
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}
	}

	public RateTable cloneRateTable(RateTable rateTable, boolean cloneDT, boolean cloneNormalizer, String suffix) throws Exception {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			RateTable rateTableCloned = this.cloneRateTable(rateTable, cloneDT, cloneNormalizer, suffix, false,
					new ArrayList<Parameter>(), new CloneIgnoreListModel(), true, new CloneIgnoreListModel(), session);
			session.getTransaction().commit();
			return rateTableCloned;
		} catch (Exception e) {
			
			session.getTransaction().rollback();
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	private void replaceParametersOfFormula(Formula formula, List<Parameter> parameters) {
		if (formula.getTemplateBits() != null) {
			String value = Long.toBinaryString(formula.getTemplateBits());

			StringBuffer bonus = new StringBuffer("");
			for (int i = 0; i < 4 - value.length(); i++) {
				bonus.append("0");
			}
			value = bonus.toString() + value;

			if (value.charAt(0) == '1' ? true : false) {
				Parameter parameter = this.findPramInList(parameters, formula.getFormulaType());
				if (parameter != null) {
					if (StringUtils.isNumeric(parameter.getParameterValue())) {
						formula.setFormulaType(Long.valueOf(parameter.getParameterValue()));
					} else {
						formula.setFormulaType(0L);
					}
					value = "0" + value.substring(1);
				}
			}

			if (value.charAt(1) == '1' ? true : false) {
				Parameter parameter = this.findPramInList(parameters, formula.getA());
				if (parameter != null) {
					if (StringUtils.isNumeric(parameter.getParameterValue())) {
						formula.setA(Long.valueOf(parameter.getParameterValue()));
					} else {
						formula.setA(1L);
					}
					value = value.substring(0, 1) + "0" + value.substring(2);
				}
			}

			if (value.charAt(2) == '1' ? true : false) {
				Parameter parameter = this.findPramInList(parameters, formula.getB());
				if (parameter != null) {
					if (StringUtils.isNumeric(parameter.getParameterValue())) {
						formula.setB(Long.valueOf(parameter.getParameterValue()));
					} else {
						formula.setB(0L);
					}
					value = value.substring(0, 2) + "0" + value.substring(3);
				}
			}

			if (value.charAt(3) == '1' ? true : false) {
				Parameter parameter = this.findPramInList(parameters, formula.getPer());
				if (parameter != null) {
					if (StringUtils.isNumeric(parameter.getParameterValue())) {
						formula.setPer(Long.valueOf(parameter.getParameterValue()));
					} else {
						formula.setPer(1L);
					}
					value = value.substring(0, 3) + "0" + value.substring(4);
				}
			}

			formula.setTemplateBits(Long.valueOf(value.toString(), 2));
		}
	}

	public boolean checkUsingParamFormula(Formula formula, List<Parameter> parameters) {
		boolean replaced = false;
		if (formula.getTemplateBits() != null) {
			String value = Long.toBinaryString(formula.getTemplateBits());

			StringBuffer bonus = new StringBuffer("");
			for (int i = 0; i < 4 - value.length(); i++) {
				bonus.append("0");
			}
			value = bonus.toString() + value;

			if (value.charAt(0) == '1' ? true : false) {
				Parameter parameter = this.findPramInList(parameters, formula.getFormulaType());
				if (parameter != null) {
					replaced = true;
				}
			}

			if (value.charAt(1) == '1' ? true : false) {
				Parameter parameter = this.findPramInList(parameters, formula.getA());
				if (parameter != null) {
					replaced = true;
				}
			}

			if (value.charAt(2) == '1' ? true : false) {
				Parameter parameter = this.findPramInList(parameters, formula.getB());
				if (parameter != null) {
					replaced = true;
				}
			}

			if (value.charAt(3) == '1' ? true : false) {
				Parameter parameter = this.findPramInList(parameters, formula.getPer());
				if (parameter != null) {
					replaced = true;
				}
			}
		}
		return replaced;
	}

	private Parameter findPramInList(List<Parameter> parameters, Long parameterId) {
		for (Parameter parameter : parameters) {
			if (parameter.getParameterId() == parameterId) {
				return parameter;
			}
		}
		return null;
	}

	public RateTable findRateTableIndexLT(Long index, Long categoryId) {
		Session session = HibernateUtil.getOpenSession();
		try {

			StringBuffer hql = new StringBuffer("SELECT a FROM RateTable a");
			hql.append(" WHERE a.domainId = :domainId");
			hql.append(" AND a.index < :index");
			hql.append(" AND a.categoryId = :categoryId");
			hql.append(" ORDER BY a.index DESC");

			Query<RateTable> query = session.createQuery(hql.toString());
			query.setParameter("domainId", getDomainId());
			query.setParameter("index", index);
			query.setParameter("categoryId", categoryId);

			List<RateTable> rateTables = query.getResultList();
			if (rateTables != null && rateTables.size() > 0) {
				return rateTables.get(0);
			}
			return null;
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public RateTable findRateTableLastIndex() {
		Session session = HibernateUtil.getOpenSession();
		try {

			StringBuffer hql = new StringBuffer("SELECT a FROM RateTable a");
			hql.append(" WHERE a.domainId = :domainId");
			hql.append(" ORDER BY a.index DESC");

			Query<RateTable> query = session.createQuery(hql.toString());
			query.setParameter("domainId", getDomainId());

			List<RateTable> rateTables = query.getResultList();
			if (rateTables != null && rateTables.size() > 0) {
				return rateTables.get(0);
			}
			return null;
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public RateTable findRateTableIndexGT(Long index, Long categoryId) {
		Session session = HibernateUtil.getOpenSession();
		try {

			StringBuffer hql = new StringBuffer("SELECT a FROM RateTable a");
			hql.append(" WHERE a.domainId = :domainId");
			hql.append(" AND a.index > :index");
			hql.append(" AND a.categoryId = :categoryId");
			hql.append(" ORDER BY a.index DESC");

			Query<RateTable> query = session.createQuery(hql.toString());
			query.setParameter("domainId", getDomainId());
			query.setParameter("index", index);
			query.setParameter("categoryId", categoryId);

			List<RateTable> rateTables = query.getResultList();
			if (rateTables != null && rateTables.size() > 0) {
				return rateTables.get(0);
			}
			return null;
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	@SuppressWarnings({ "unchecked" })
	public List<Formula> findFormulaByRateTable(RateTable rateTable) {
		StringBuffer hql = new StringBuffer("SELECT d FROM RateTable a");
		hql.append(" JOIN ResultRateMap b ON b.rateTableId = a.rateTableId");
		hql.append(" JOIN RateTableResult c ON c.rateTableResultId = b.rateTableResultId");
		hql.append(" JOIN Formula d ON d.formulaId = c.formulaId");
		hql.append(" WHERE d.domainId =:domainId");
		hql.append(" AND a.rateTableId =:rateTableId");
		hql.append(" ORDER BY c.rowIndex");

		Session session = HibernateUtil.getOpenSession();
		try {
			Query<Formula> query = session.createQuery(hql.toString());
			query.setParameter("domainId", getDomainId());
			query.setParameter("rateTableId", rateTable.getRateTableId());
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	@SuppressWarnings({ "unchecked" })
	private List<Formula> findFormulaByRateTable(RateTable rateTable, Session session) {
		StringBuffer hql = new StringBuffer("SELECT d FROM RateTable a");
		hql.append(" JOIN ResultRateMap b ON b.rateTableId = a.rateTableId");
		hql.append(" JOIN RateTableResult c ON c.rateTableResultId = b.rateTableResultId");
		hql.append(" JOIN Formula d ON d.formulaId = c.formulaId");
		hql.append(" WHERE d.domainId =:domainId");
		hql.append(" AND a.rateTableId =:rateTableId");
		hql.append(" ORDER BY c.rowIndex");
		try {

			Query<Formula> query = session.createQuery(hql.toString());
			query.setParameter("domainId", getDomainId());
			query.setParameter("rateTableId", rateTable.getRateTableId());
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}
	}

	public List<RateTable> findRateTableByListBlock(List<Long> lstBlockId) {

		List<RateTable> lst = null;
		String hql = "SELECT DISTINCT rt FROM RateTable rt JOIN BlockRateTableMap map ON map.rateTableId = rt.rateTableId ";
		hql += " WHERE map.blockId IN (:blockId)";

		Session session = HibernateUtil.getOpenSession();
		try {

			Query query = session.createQuery(hql);
			lstBlockId.add(-1L);
			query.setParameterList("blockId", lstBlockId);
			lst = query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}

		return lst;
	}

	public List<RateTable> findRateTableByListDesId(List<Long> lstDesIdS) {

		if (lstDesIdS.size() == 0)
			lstDesIdS.add(-1L);
		List<RateTable> lst = null;
		String hql = "SELECT DISTINCT rt FROM RateTable rt WHERE  rt.decisionTableId IN (:decisionTableId) ";

		Session session = HibernateUtil.getOpenSession();
		try {
			Query query = session.createQuery(hql);
			query.setParameterList("decisionTableId", lstDesIdS);
			lst = query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}

		return lst;
	}

	public List<RateTable> findRateTableByListDR(List<Long> lstDRId) {

		if (lstDRId.size() == 0)
			lstDRId.add(-1L);
		List<RateTable> lst = null;
		String hql = "SELECT DISTINCT rt FROM RateTable rt JOIN DynamicReserveRateTableMap map ON map.rateTableId = rt.rateTableId ";
		hql += " WHERE map.dynamicReserveId IN (:dynamicReserveId)";

		Session session = HibernateUtil.getOpenSession();
		try {
			Query query = session.createQuery(hql);
			query.setParameterList("dynamicReserveId", lstDRId);
			lst = query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}

		return lst;
	}

	public List<RateTable> findRateTableByListSPC(List<Long> lstSPCId) {

		if (lstSPCId.size() == 0)
			lstSPCId.add(-1L);
		List<RateTable> lst = null;
		String hql = "SELECT DISTINCT rt FROM RateTable rt JOIN SortPriceRateTableMap map ON map.rateTableId = rt.rateTableId ";
		hql += " WHERE map.sortPriceComponentId IN (:sortPriceComponentId)";

		Session session = HibernateUtil.getOpenSession();
		try {
			Query query = session.createQuery(hql);
			query.setParameterList("sortPriceComponentId", lstSPCId);
			lst = query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}

		return lst;
	}
	
	public List<RateTable> findRateTableByListFormula(List<Long> lstFormulaId) {

		if (lstFormulaId.size() == 0)
			return new ArrayList<RateTable>();
		
		String hql = "SELECT DISTINCT rt FROM RateTable rt JOIN ResultRateMap map ON map.rateTableId = rt.rateTableId ";
		hql += " JOIN RateTableResult rs ON rs.rateTableResultId = map.rateTableResultId";
		hql += " WHERE rs.formulaId IN (:lstFormulaId)";
		
		String hql2 = " SELECT DISTINCT rt2 FROM RateTable rt2 WHERE rt2.formulaId IN (:lstFormulaId) OR rt2.defaultFormulaId IN (:lstFormulaId)";

		Session session = HibernateUtil.getOpenSession();
		List<RateTable> lst;
		List<RateTable> lst2;
		try {
			Query query = session.createQuery(hql);
			query.setParameterList("lstFormulaId", lstFormulaId);
			lst = query.getResultList();
			
			Query query2 = session.createQuery(hql2);
			query2.setParameterList("lstFormulaId", lstFormulaId);
			lst2 = query2.getResultList();
			lst.addAll(lst2);
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}

		return lst;
	}

	public Boolean processMoveUpDown(RateTable item, Boolean isUp) {
		boolean result = false;
		if (item != null && item.getRateTableId() > 0) {
			Session session = HibernateUtil.getOpenSession();
			item = get(item.getRateTableId());
			try {

				session.getTransaction().begin();

				StringBuffer hql = new StringBuffer("SELECT a FROM RateTable a");
				hql.append(" WHERE a.domainId = :domainId");

				if (isUp) {
					hql.append(" AND a.index < :index");
				} else {
					hql.append(" AND a.index > :index");
				}

				hql.append(" AND a.categoryId = :categoryId");

				if (isUp) {
					hql.append(" ORDER BY a.index DESC");
				} else {
					hql.append(" ORDER BY a.index ASC");
				}

				Query<RateTable> query = session.createQuery(hql.toString());
				query.setParameter("domainId", getDomainId());
				query.setParameter("index", item.getIndex());
				query.setParameter("categoryId", item.getCategoryId());
				query.setMaxResults(1);

				List<RateTable> rateTables = query.getResultList();
				if (rateTables != null && rateTables.size() > 0) {
					// if (!checkFieldIsExist("index", item.getIndex(), item)) {
					RateTable itemMove = rateTables.get(0);
					Long tmpIndex = 0l;
					tmpIndex = itemMove.getIndex();
					itemMove.setIndex(item.getIndex());
					item.setIndex(tmpIndex);
					session.update(itemMove);
					session.update(item);
					result = true;
					// } else {
					// result = false;
					// }
				} else {
					result = false;
				}

				session.getTransaction().commit();

			} catch (Exception e) {
				// TODO: handle exception
				session.getTransaction().rollback();
				getLogger().error(e.getMessage(), e);
				throw e;
			} finally {
				session.close();
			}
		}
		return result;
	}

	public Boolean processMoveUpDownMap(RateTable item, RateTableDump rateTableDump, Boolean isUp) {
		boolean result = false;
		if (item != null && item.getRateTableId() > 0) {

			BlockRateTableMap realItemMove = getRealItemToMove(item, rateTableDump);

			Session session = HibernateUtil.getOpenSession();

			try {

				session.getTransaction().begin();

				StringBuffer hql = new StringBuffer("SELECT a FROM BlockRateTableMap a");
				hql.append(" WHERE a.domainId = :domainId");
				hql.append(" AND a.blockId =:blockId");
				hql.append(" AND a.componentType =:componentType");

				if (isUp) {
					hql.append(" AND a.rateTableIndex < :rateTableIndex");
				} else {
					hql.append(" AND a.rateTableIndex > :rateTableIndex");
				}

				if (isUp) {
					hql.append(" ORDER BY a.rateTableIndex DESC");
				} else {
					hql.append(" ORDER BY a.rateTableIndex ASC");
				}

				Query<BlockRateTableMap> query = session.createQuery(hql.toString());
				query.setParameter("domainId", getDomainId());
				query.setParameter("rateTableIndex", realItemMove.getRateTableIndex());
				query.setParameter("blockId", rateTableDump.getBlockId());
				query.setParameter("componentType", rateTableDump.getComponentType());
				query.setMaxResults(1);

				List<BlockRateTableMap> BlockRateTables = query.getResultList();
				if (BlockRateTables != null && BlockRateTables.size() > 0) {
					BlockRateTableMap itemMove = BlockRateTables.get(0);
					Long tmpIndex = 0l;
					tmpIndex = itemMove.getRateTableIndex();
					itemMove.setRateTableIndex(realItemMove.getRateTableIndex());
					realItemMove.setRateTableIndex(tmpIndex);
					session.update(itemMove);
					session.update(realItemMove);
					result = true;
				} else {
					result = false;
				}

				session.getTransaction().commit();

			} catch (Exception e) {
				// TODO: handle exception
				session.getTransaction().rollback();
				getLogger().error(e.getMessage(), e);
				result = false;
			} finally {
				session.close();
			}
		}
		return result;
	}
	
	public BlockRateTableMap getRealItemToMove(RateTable item, RateTableDump rateTableDump) {
		BlockRateTableMap itemMove = new BlockRateTableMap();
		if (item != null && item.getRateTableId() > 0) {
			Session session = HibernateUtil.getOpenSession();

			try {

				StringBuffer hql = new StringBuffer("SELECT a FROM BlockRateTableMap a");
				hql.append(" WHERE a.domainId = :domainId");
				hql.append(" AND a.blockId =:blockId");
				hql.append(" AND a.componentType =:componentType");
				hql.append(" AND a.rateTableId =:rateTableId");

				Query<BlockRateTableMap> query = session.createQuery(hql.toString());
				query.setParameter("domainId", getDomainId());
				query.setParameter("blockId", rateTableDump.getBlockId());
				query.setParameter("componentType", rateTableDump.getComponentType());
				query.setParameter("rateTableId", item.getRateTableId());
				query.setMaxResults(1);

				List<BlockRateTableMap> blockRateTableMaps = query.getResultList();
				if (blockRateTableMaps != null && blockRateTableMaps.size() > 0) {
					itemMove = blockRateTableMaps.get(0);
				} else {
					itemMove = null;
				}
			} catch (Exception e) {
				getLogger().error(e.getMessage(), e);
				throw e;
			} finally {
				session.close();
			}
		}
		return itemMove;
	}

	public Boolean checkFieldIsExist(String col, Object value, RateTable rateTable) {
		boolean result = false;

		int count = 0;

		if (rateTable == null || rateTable.getRateTableId() == 0) {
			String[] column = { col };
			Operator[] ope = { Operator.EQ };
			Object[] val = { value };
			count = this.countByConditions(column, ope, val);
		} else {
			String[] column = { col, "rateTableId" };
			Operator[] ope = { Operator.EQ, Operator.NOTEQ };
			Object[] val = { value, rateTable.getRateTableId() };
			count = this.countByConditions(column, ope, val);
		}

		if (count > 0) {
			result = true;
		}

		return result;
	}

	public boolean deleteRateTableAndMap(RateTable rateTable) {
		String hql = "DELETE FROM RateTable map";
		hql += " WHERE map.rateTableId = :rateTableId";
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			session.delete(rateTable);
			Query<RateTable> query = session.createQuery(hql);
			query.setParameter("rateTableId", rateTable.getRateTableId());
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

	public boolean deleteRateTableAndResult(RateTable rateTable) {
		if (rateTable != null) {
			Session session = HibernateUtil.getOpenSession();
			session.getTransaction().begin();
			try {
				deleteFormulaTable(rateTable, session);

				if (rateTable.getDefaultFormulaId() != null) {
					Formula defaultFormula = session.get(Formula.class, rateTable.getDefaultFormulaId());
					if (defaultFormula != null) {
						session.delete(defaultFormula);
					}
				}

				if (rateTable.getFormulaId() != null) {
					Formula formula = session.get(Formula.class, rateTable.getFormulaId());
					if (formula != null) {
						session.delete(formula);
					}
				}

				session.delete(rateTable);
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

	public int countRateTableByDecisionTableId(Long decisionTableId) {
		String hql = "SELECT COUNT(a) FROM RateTable a WHERE a.decisionTableId =:decisionTableId AND  a.domainId =:domainId";
		Session session = HibernateUtil.getOpenSession();
		try {
			Query<Long> query = session.createQuery(hql, Long.class);
			query.setParameter("decisionTableId", decisionTableId);
			query.setParameter("domainId", getDomainId());
			return query.getSingleResult().intValue();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public int countRateTable() {
		String hql = "SELECT COUNT(a) FROM RateTable a WHERE  a.domainId =:domainId";
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

	public void deleteFormulasAndDTByRT(RateTable rateTable, Session session) {
		try {

			rateTable = session.get(RateTable.class, rateTable.getRateTableId());
			if (rateTable != null && rateTable.isGenerated() != null && rateTable.isGenerated()) {
				if (rateTable.getDefaultFormulaId() != null) {
					Formula formula = session.get(Formula.class, rateTable.getDefaultFormulaId());
					if (formula != null) {
						session.delete(formula);
					}
				}

				if (rateTable.getFormulaId() != null) {
					Formula formula = session.get(Formula.class, rateTable.getFormulaId());
					if (formula != null) {
						session.delete(formula);
					}
				}

				this.deleteFormulaTable(rateTable, session);

				DecisionTableDAO decisionTableDAO = new DecisionTableDAO();
				if (rateTable.getDecisionTableId() != null) {
					DecisionTable decisionTable = session.get(DecisionTable.class, rateTable.getDecisionTableId());
					decisionTableDAO.deleteColRowAndNormalizerByDT(decisionTable, session);
				}

				session.delete(rateTable);
			}
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}
	}

	public List<RateTable> findByCatAndNotGend(List<Long> lstAllCatID) {
		String hql = "SELECT a FROM RateTable a";
		hql += " WHERE a.categoryId IN (:categoryIds) AND (a.generated is false OR a.generated is NULL)";

		Session session = HibernateUtil.getOpenSession();
		try {

			Query<RateTable> query = session.createQuery(hql, RateTable.class);
			query.setParameterList("categoryIds", lstAllCatID);
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}
	
	public boolean checkUnitTypeInRT(Long unitTypeId) {
		List<RateTable> lst = null;
		String[] cols = { "unitTypeId"};
		Operator[] operators = { Operator.EQ };
		Object[] values = { unitTypeId };
		lst = findByConditions(cols, operators, values, "");
		if (lst != null && lst.size() > 0) {
			return true;
		}
		return false;
	}
	
	public boolean moveToCate(RateTable rateTable) {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			rateTable.setDomainId(getDomainId());
			generateIndexByCat(rateTable,"rateTableId", session);
			session.saveOrUpdate(rateTable);
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

}

package com.viettel.ocs.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.Block;
import com.viettel.ocs.entity.Formula;
import com.viettel.ocs.entity.Normalizer;
import com.viettel.ocs.entity.Parameter;

public class ParameterDAO extends BaseDAO<Parameter> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public List<Parameter> findParamByConditions(Long categoryId) {
		List<Parameter> lst = null;
		String[] cols = { "categoryId", "domainId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { categoryId, getDomainId() };
		lst = findByConditions(cols, operators, values, "index ASC");
		return lst;
	}

	public List<Parameter> findParamByCatId(List<Long> lstCatId) {
		List<Parameter> lstParameter = null;
		String[] cols = { "categoryId", "domainId" };
		Operator[] operators = { Operator.IN, Operator.EQ };
		List<Long> lstCatIDParam = new ArrayList<Long>();
		lstCatIDParam.addAll(lstCatId);
		if (lstCatIDParam.size() == 0)
			lstCatIDParam.add(-1L);
		Object[] values = { lstCatIDParam, getDomainId() };
		lstParameter = findByConditions(cols, operators, values, "");
		return lstParameter;
	}

	public List<Parameter> findParamByCatIdOlyForTemp(List<Long> lstCatId) {
		List<Parameter> lstParameter = null;
		String[] cols = { "categoryId", "forTemplate", "domainId" };
		Operator[] operators = { Operator.IN, Operator.EQ, Operator.EQ };
		List<Long> lstCatIDParam = new ArrayList<Long>();
		lstCatIDParam.addAll(lstCatId);
		if (lstCatIDParam.size() == 0)
			lstCatIDParam.add(-1L);
		Object[] values = { lstCatIDParam, true, getDomainId() };
		lstParameter = findByConditions(cols, operators, values, "");
		return lstParameter;
	}

	public List<Parameter> findParamByCatIdAndNotForTemp(List<Long> lstCatId) {
		List<Parameter> lstParameter = null;
		String[] cols = { "categoryId", "forTemplate", "domainId" };
		Operator[] operators = { Operator.IN, Operator.NOTEQ, Operator.EQ };
		List<Long> lstCatIDParam = new ArrayList<Long>();
		lstCatIDParam.addAll(lstCatId);
		if (lstCatIDParam.size() == 0)
			lstCatIDParam.add(-1L);
		Object[] values = { lstCatIDParam, true, getDomainId() };
		lstParameter = findByConditions(cols, operators, values, "");
		return lstParameter;
	}

	@Override
	protected Class<Parameter> getEntityClass() {
		return Parameter.class;
	}

	public boolean checkName(Parameter parameter, String parameterName, boolean edit) {
		List<Parameter> lst = null;
		String[] cols = { "parameterName", "domainId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { parameterName, getDomainId() };
		lst = findByConditions(cols, operators, values, "");
		if (lst.size() > 0) {
			if (edit && lst.get(0).getParameterId() == parameter.getParameterId()) {
				return false;
			} else {
				return true;
			}
		}
		return false;

	}

	public boolean checkId(Parameter parameter, boolean edit) {
		List<Parameter> lst = null;
		String hql = "SELECT p FROM Parameter p";
		hql += " WHERE p.parameterId = :parameterId";

		Session session = HibernateUtil.getOpenSession();
		try {
			Query<Parameter> query = session.createQuery(hql);
			query.setParameter("parameterId", parameter.getParameterId());
			lst = query.getResultList();
			if (lst.size() > 0 && !edit) {
				return true;
			}
			return false;
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public Parameter findParameterLastID() {
		Session session = HibernateUtil.getOpenSession();
		try {
			StringBuffer hql = new StringBuffer("SELECT a FROM Parameter a");
			hql.append(" ORDER BY a.parameterId DESC");
			Query<Parameter> query = session.createQuery(hql.toString());
			query.setMaxResults(1);
			List<Parameter> parameters = query.getResultList();
			if (parameters.size() > 0) {
				return parameters.get(0);
			}
			return null;
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public List<Parameter> findParameterByFormula(Formula formula) {
		List<Parameter> parameters = new ArrayList<Parameter>();
		if (formula.getTemplateBits() != null) {
			String value = Long.toBinaryString(formula.getTemplateBits());

			StringBuffer bonus = new StringBuffer("");
			for (int i = 0; i < 4 - value.length(); i++) {
				bonus.append("0");
			}
			value = bonus.toString() + value;

			if (value.charAt(0) == '1' ? true : false) {
				Parameter parameter = this.get(formula.getFormulaType());
				if (parameter != null && parameter.getForTemplate()) {
					parameters.add(parameter);
				}
			}

			if (value.charAt(1) == '1' ? true : false) {
				Parameter parameter = this.get(formula.getA());
				if (parameter != null && parameter.getForTemplate()) {
					parameters.add(parameter);
				}
			}

			if (value.charAt(2) == '1' ? true : false) {
				Parameter parameter = this.get(formula.getB());
				if (parameter != null && parameter.getForTemplate()) {
					parameters.add(parameter);
				}
			}

			if (value.charAt(3) == '1' ? true : false) {
				Parameter parameter = this.get(formula.getPer());
				if (parameter != null && parameter.getForTemplate()) {
					parameters.add(parameter);
				}
			}
		}

		return parameters;
	}

	public boolean checkUsingParam4Formula(Formula formula) {
		if (formula.getTemplateBits() != null) {
			String value = Long.toBinaryString(formula.getTemplateBits());

			StringBuffer bonus = new StringBuffer("");
			for (int i = 0; i < 4 - value.length(); i++) {
				bonus.append("0");
			}
			value = bonus.toString() + value;

			if (value.charAt(0) == '1' ? true : false) {
				Parameter parameter = this.get(formula.getFormulaType());
				if (parameter != null && parameter.getForTemplate()) {
					return true;
				}
			}

			if (value.charAt(1) == '1' ? true : false) {
				Parameter parameter = this.get(formula.getA());
				if (parameter != null && parameter.getForTemplate()) {
					return true;
				}
			}

			if (value.charAt(2) == '1' ? true : false) {
				Parameter parameter = this.get(formula.getB());
				if (parameter != null && parameter.getForTemplate()) {
					return true;
				}
			}

			if (value.charAt(3) == '1' ? true : false) {
				Parameter parameter = this.get(formula.getPer());
				if (parameter != null && parameter.getForTemplate()) {
					return true;
				}
			}
		}
		return false;
	}

	// Check delete
	public boolean checkParameterBlock(Parameter parameter) {
		List<Block> lstBlock = null;
		List<Formula> lstFormula = null;
		List<Normalizer> lstNormalizer = null;
		boolean checkAll = false;
		// Check block
		String hqlBlock = "SELECT p FROM Block p";
		hqlBlock += " WHERE p.affectedObjectType = :affectedObjectType ";
		hqlBlock += " AND p.affectedValue = :parameterId AND p.domainId = :domainId";
		// Check formula
		String hqlFormula = "SELECT p FROM Formula p";
		hqlFormula += " WHERE p.templateBits > 0";
		hqlFormula += " AND CASE";
		hqlFormula += " WHEN p.formulaType = :parameterId THEN 1";
		hqlFormula += " WHEN p.a = :parameterId THEN 1";
		hqlFormula += " WHEN p.b = :parameterId THEN 1";
		hqlFormula += " WHEN p.per = :parameterId THEN 1 ELSE 0 END = 1";
		hqlFormula += " AND p.domainId = :domainId";

		// Check Normalizer
		String hqlNormalizer = "SELECT p FROM Normalizer p";

		Session session = HibernateUtil.getOpenSession();
		try {
			// Check block
			Query<Block> queryBlock = session.createQuery(hqlBlock);
			queryBlock.setParameter("affectedObjectType",
					com.viettel.ocs.constant.Normalizer.BlockAffectedObjectType.PARAMETER);
			queryBlock.setParameter("parameterId", parameter.getParameterId());
			queryBlock.setParameter("domainId", getDomainId());
			lstBlock = queryBlock.getResultList();
			if (lstBlock.size() > 0) {
				checkAll = true;
			}
			// check formula
			Query<Formula> queryFormula = session.createQuery(hqlFormula);
			queryFormula.setParameter("parameterId", parameter.getParameterId());
			queryFormula.setParameter("domainId", getDomainId());
			lstFormula = queryFormula.getResultList();
			if (lstFormula.size() > 0) {
				checkAll = true;
			}
			// // Check Normalizer
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
		return checkAll;
	}

	public int countParameter() {
		String hql = "SELECT COUNT(a) FROM Parameter a WHERE  a.domainId =:domainId ";
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

	public Long getMaxIndex() {
		return getMax("index");
	}

	public boolean saveParam(Parameter parameterUI) {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			parameterUI.setDomainId(getDomainId());
			generateIndexByCat(parameterUI, "parameterId", session);
			session.saveOrUpdate(parameterUI);
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
	
	public Parameter cloneParameter(Parameter parameter, String suffix) throws CloneNotSupportedException {
		Session session = HibernateUtil.getOpenSession();
		try {
			session.getTransaction().begin();
			Parameter parameterCloned = this.cloneParameter(parameter, session, suffix);
			session.getTransaction().commit();
			return parameterCloned;
		} catch (Exception e) {
			session.getTransaction().rollback();
			getLogger().error(e.getMessage(), e);
			return null;
		} finally {
			session.close();
		}
	}
	
	private Parameter cloneParameter(Parameter parameter, Session session, String suffix) throws CloneNotSupportedException {
		try {
			Parameter parameterToClone = parameter.clone();
			parameterToClone.setParameterId(this.getNewId(session) + 1L);
			parameterToClone.setParameterName(generateNameObject("parameterName", parameterToClone.getParameterName() + suffix, 0, session));
			parameterToClone.setDomainId(getDomainId());
			parameterToClone.setIndex(this.getMaxIndex(session) + 1L);
			session.save(parameterToClone);

			return parameterToClone;
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}
	}
	
	private Long getNewId(Session session) {
		try {
			StringBuffer hql = new StringBuffer("SELECT a.parameterId FROM Parameter a");
			hql.append(" ORDER BY a.parameterId DESC");

			Query<Long> query = session.createQuery(hql.toString(), Long.class);
			query.setMaxResults(1);
			return query.getSingleResult();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}		
	}
	
	public Long getMaxIndex(Session session) {
		try {
			StringBuffer hql = new StringBuffer("SELECT COALESCE(MAX(a.index), 0) FROM Parameter a");
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

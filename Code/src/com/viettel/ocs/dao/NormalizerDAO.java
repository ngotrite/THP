package com.viettel.ocs.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.ColumnDt;
import com.viettel.ocs.entity.DecisionTable;
import com.viettel.ocs.entity.NomalizerNormParamMap;
import com.viettel.ocs.entity.NormParam;
import com.viettel.ocs.entity.NormValue;
import com.viettel.ocs.entity.Normalizer;
import com.viettel.ocs.entity.NormalizerNormValueMap;
import com.viettel.ocs.entity.TriggerOcs;

public class NormalizerDAO extends BaseDAO<Normalizer> {

	public List<Normalizer> findNormalByConditions(Long categoryId) {
		List<Normalizer> lst = null;
		String[] cols = { "categoryId" };
		Operator[] operators = { Operator.EQ };
		Object[] values = { categoryId };
		lst = findByConditions(cols, operators, values, "CAST(posIndex AS integer) ASC");
		return lst;
	}

	public List<Normalizer> findByListCategory(List<Long> lstCatID) {

		String[] cols = { "categoryId" };
		Operator[] operators = { Operator.IN };
		List<Long> lstCatIDParam = new ArrayList<Long>();
		lstCatIDParam.addAll(lstCatID);
		if (lstCatIDParam.size() == 0)
			lstCatIDParam.add(-1L);
		Object[] values = { lstCatIDParam };
		return super.findByConditions(cols, operators, values, " CAST(posIndex AS integer) ASC");
	}

	@Override
	protected Class<Normalizer> getEntityClass() {
		return Normalizer.class;
	}

	public List<ValueParam> findListValueParamByConditions(Long normalizerId) {
		List<ValueParam> lstValueParam = new ArrayList<ValueParam>();
		NormValueDAO normValueDAO = new NormValueDAO();
		List<NormValue> listValue = normValueDAO.findNormValueByNormId(normalizerId);
		NormParamDAO normParamDAO = new NormParamDAO();
		List<NormParam> listParam = normParamDAO.findNormParamByNormId(normalizerId);
		for (NormValue normValue : listValue) {
			for (NormParam normParam : listParam) {
				if (normValue.getNormValueIndex() == normParam.getNormParamIndex()) {
					ValueParam valueParam = new ValueParam();
					valueParam.setNormParam(normParam);
					valueParam.setNormValue(normValue);
					lstValueParam.add(valueParam);
				}
			}
		}
		return lstValueParam;
	}

	public List<NormParam> getNormParamUsingParameterByNormalizer(List<Long> ids) {
		StringBuffer hql = new StringBuffer("SELECT c FROM Normalizer a");
		hql.append(" JOIN NomalizerNormParamMap b ON b.normalizerId = a.normalizerId");
		hql.append(" JOIN NormParam c ON c.normParamId = b.normParamId");
		hql.append(" WHERE a.normalizerId IN (:normalizerIds)");
		hql.append(" AND (c.configInput LIKE '%" + com.viettel.ocs.constant.Normalizer.UsingNormParam.END_IS_PARAMETER
				+ "%' OR c.configInput LIKE '%" + com.viettel.ocs.constant.Normalizer.UsingNormParam.START_IS_PARAMETER
				+ "%' OR c.configInput LIKE '%" + com.viettel.ocs.constant.Normalizer.UsingNormParam.IS_USING_PARAMETER
				+ "%')");
		Session session = HibernateUtil.getOpenSession();
		try {
			Query<NormParam> query = session.createQuery(hql.toString(), NormParam.class);
			query.setParameterList("normalizerIds", ids);
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public List<Normalizer> findNormalizersByListDesId(List<Long> decisionIds) {
		if (decisionIds.size() == 0)
			decisionIds.add(-1L);
		StringBuffer hql = new StringBuffer("SELECT DISTINCT(d) FROM DecisionTable a");
		hql.append(" JOIN ColumnDecisionTableMap b ON b.decisionTableId = a.decisionTableId");
		hql.append(" JOIN ColumnDt c ON c.columnId = b.columnDtId ");
		hql.append(" JOIN Normalizer d ON d.normalizerId = c.normalizerId");
		hql.append(" WHERE b.domainId =:domainId AND c.domainId =:domainId AND d.domainId =:domainId");
		hql.append(" AND a.decisionTableId IN (:decisionTableId)");

		Session session = HibernateUtil.getOpenSession();
		try {
			Query<Normalizer> query = session.createQuery(hql.toString());
			query.setParameter("domainId", getDomainId());
			query.setParameter("decisionTableId", decisionIds);
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public Normalizer getLastNormalizer() {
		Normalizer last = new Normalizer();
		Session session = HibernateUtil.getOpenSession();
		try {
			Query query = session.createQuery("from Normalizer no order by no.posIndex DESC");
			query.setMaxResults(1);
			last = (Normalizer) query.uniqueResult();

		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
		return last;
	}

	public Normalizer getLastNormalizerID() {
		Normalizer last = new Normalizer();
		Session session = HibernateUtil.getOpenSession();
		try {
			Query query = session.createQuery("from Normalizer no order by no.normalizerId DESC");
			query.setMaxResults(1);
			last = (Normalizer) query.uniqueResult();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
		return last;
	}

	public Normalizer cloneNormalizer(Normalizer item) {
		Normalizer newNorm = null;
		Session session = HibernateUtil.getOpenSession();
		try {
			session.getTransaction().begin();

			NormalizerDAO normalizerDAO = new NormalizerDAO();
			NormalizerNormValueMapDAO normValueMapDAO = new NormalizerNormValueMapDAO();
			NomalizerNormParamMapDAO normParamMapDAO = new NomalizerNormParamMapDAO();
			NormValueDAO normValueDAO = new NormValueDAO();
			NormParamDAO normParamDAO = new NormParamDAO();
			Normalizer lastNorm = normalizerDAO.getLastNormalizerID();
			Normalizer clone = new Normalizer();
			clone.setNormalizerId(0);
			clone.setNormalizerName(
					item.getNormalizerName() + "_" + String.valueOf(lastNorm.getNormalizerId() + 12345l));
			clone.setCategoryId(item.getCategoryId());
			clone.setNormlizerType(item.getNormlizerType());
			clone.setRemark(item.getRemark());
			clone.setStartDate(new Date());
			clone.setDefaultValue(item.getDefaultValue());
			clone.setDomainId(item.getDomainId());
			clone.setInputFileds(item.getInputFileds());
			clone.setSpecialFileds(item.getSpecialFileds());
			clone.setState(item.getState());
			clone.setPosIndex(getLastNormalizer().getPosIndex() == null ? 1l : getLastNormalizer().getPosIndex() + 1l);
			session.save(clone);
			if (clone.getNormalizerId() > 0) {
				List<NormalizerNormValueMap> lstNormVLMap = new ArrayList<>();
				lstNormVLMap = normValueMapDAO.findMapByNormalizerId(item.getNormalizerId());
				if (lstNormVLMap.size() > 0) {
					for (NormalizerNormValueMap nvm : lstNormVLMap) {
						NormValue currentVl = normValueDAO.get(nvm.getNormValueId());
						if (currentVl != null) {
							NormValue nv = new NormValue();
							nv.setDomainId(currentVl.getDomainId());
							nv.setColor(currentVl.getColor());
							nv.setColorBG(currentVl.getColorBG());
							nv.setDescription(currentVl.getDescription());
							nv.setNormValueIndex(currentVl.getNormValueIndex());
							nv.setValueId(currentVl.getValueId());
							nv.setValueName(currentVl.getValueName());
							session.save(nv);
							if (nv.getNormValueId() > 0) {
								NormalizerNormValueMap nvMap = new NormalizerNormValueMap();
								nvMap.setDomainId(getDomainId());
								nvMap.setNormalizerId(clone.getNormalizerId());
								nvMap.setNormValueId(nv.getNormValueId());
								session.save(nvMap);
							}
						}
					}
				}

				List<NomalizerNormParamMap> lstNormPRMap = new ArrayList<>();
				lstNormPRMap = normParamMapDAO.findMapByNormalizerId(item.getNormalizerId());

				if (lstNormPRMap.size() > 0) {
					for (NomalizerNormParamMap nvp : lstNormPRMap) {
						NormParam currentPR = normParamDAO.get(nvp.getNormParamId());
						if (currentPR != null) {
							NormParam np = new NormParam();
							np.setDomainId(currentPR.getDomainId());
							np.setConfigInput(currentPR.getConfigInput());
							np.setNormParamIndex(currentPR.getNormParamIndex());
							session.save(np);
							if (np.getNormParamId() > 0) {
								NomalizerNormParamMap npMap = new NomalizerNormParamMap();
								npMap.setDomainId(getDomainId());
								npMap.setNormalizerId(clone.getNormalizerId());
								npMap.setNormParamId(np.getNormParamId());
								session.save(npMap);
							}
						}
					}
				}
			}

			session.getTransaction().commit();
			newNorm = clone;
		} catch (Exception e) {
			session.getTransaction().rollback();
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
		return newNorm;
	}

	public Boolean deleteNormalizer(Normalizer item) {
		boolean result = false;
		Long normId = 0l;

		if (item != null) {
			normId = item.getNormalizerId();
		}

		if (normId != null || normId != 0) {
			if (!checkInUsedNormalizer(normId)) {
				Session session = HibernateUtil.getOpenSession();
				NormalizerNormValueMapDAO normValueMapDAO = new NormalizerNormValueMapDAO();
				NomalizerNormParamMapDAO normParamMapDAO = new NomalizerNormParamMapDAO();
				NormValueDAO normValueDAO = new NormValueDAO();
				NormParamDAO normParamDAO = new NormParamDAO();
				try {

					session.getTransaction().begin();

					List<NormalizerNormValueMap> lstNormVLMap = new ArrayList<>();
					lstNormVLMap = normValueMapDAO.findMapByNormalizerId(normId);

					if (lstNormVLMap.size() > 0) {
						for (NormalizerNormValueMap vlm : lstNormVLMap) {

							session.delete(vlm);

							if (vlm.getNormValueId() != null) {
								NormValue vl = normValueDAO.get(vlm.getNormValueId());
								if (vl != null) {
									session.delete(vl);
								}
							}
						}
					}

					List<NomalizerNormParamMap> lstNormPRMap = new ArrayList<>();
					lstNormPRMap = normParamMapDAO.findMapByNormalizerId(normId);

					if (lstNormPRMap.size() > 0) {
						for (NomalizerNormParamMap prm : lstNormPRMap) {

							session.delete(prm);

							if (prm.getNormParamId() != null) {
								NormParam pr = normParamDAO.get(prm.getNormParamId());
								if (pr != null) {
									session.delete(pr);
								}
							}
						}
					}

					session.delete(item);

					session.getTransaction().commit();
					result = true;
				} catch (Exception e) {
					session.getTransaction().rollback();
					getLogger().error(e.getMessage(), e);
					throw e;
				} finally {
					session.close();
				}
			}
		}

		return result;
	}

	public Boolean checkInUsedNormalizer(Long normID) {
		boolean result = false;
		List<ColumnDt> csLst = new ArrayList<>();
		Session session = HibernateUtil.getOpenSession();
		try {
			String hql = " FROM ColumnDt cps WHERE cps.normalizerId = :normalizerId AND cps.domainId=:domainId";
			Query query = session.createQuery(hql);
			query.setParameter("normalizerId", normID);
			query.setParameter("domainId", getDomainId());
			csLst = query.getResultList();

			if (csLst.size() > 0) {

				result = true;

			}

		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
		return result;
	}

	public Boolean deleteNormValueInEditNormalizer(List<NormValue> lstNormValue, Long normID) {
		boolean result = false;

		if (normID > 0 && lstNormValue.size() > 0) {
			Session session = HibernateUtil.getOpenSession();
			NormalizerNormValueMapDAO normValueMapDAO = new NormalizerNormValueMapDAO();

			try {

				session.getTransaction().begin();

				for (NormValue nv : lstNormValue) {
					NormalizerNormValueMap nvMap = normValueMapDAO.findMapByNormalizerIdAndValueId(normID,
							nv.getNormValueId());
					if (nvMap != null) {
						session.delete(nvMap);
					}

					session.delete(nv);
				}

				session.getTransaction().commit();
				result = true;
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

	public Boolean checkFieldIsExist(String col, Object value, Normalizer norm) {
		boolean result = false;

		int count = 0;

		if (norm == null || norm.getNormalizerId() == 0) {
			String[] column = { col };
			Operator[] ope = { Operator.EQ };
			Object[] val = { value };
			count = this.countByConditions(column, ope, val);
		} else {
			String[] column = { col, "normalizerId" };
			Operator[] ope = { Operator.EQ, Operator.NOTEQ };
			Object[] val = { value, norm.getNormalizerId() };
			count = this.countByConditions(column, ope, val);
		}

		if (count > 0) {
			result = true;
		}

		return result;
	}

	public Normalizer findNormalizerLastIndex() {
		Session session = HibernateUtil.getOpenSession();
		try {

			StringBuffer hql = new StringBuffer("SELECT a FROM Normalizer a");
			hql.append(" WHERE a.domainId = :domainId");
			hql.append(" ORDER BY a.posIndex DESC");

			Query<Normalizer> query = session.createQuery(hql.toString());
			query.setParameter("domainId", getDomainId());
			query.setMaxResults(1);

			List<Normalizer> normalizers = query.getResultList();
			if (normalizers != null && normalizers.size() > 0) {
				return normalizers.get(0);
			}
			return null;
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public Boolean moveUpDownNormalizer(Normalizer item, Boolean isUp) {
		boolean result = false;
		if (item != null && item.getNormalizerId() > 0) {
			Session session = HibernateUtil.getOpenSession();
			item = get(item.getNormalizerId());
			try {

				session.getTransaction().begin();

				StringBuffer hql = new StringBuffer("SELECT a FROM Normalizer a");
				hql.append(" WHERE a.domainId = :domainId");

				if (isUp) {
					hql.append(" AND a.posIndex < :posIndex");
				} else {
					hql.append(" AND a.posIndex > :posIndex");
				}

				hql.append(" AND a.categoryId = :categoryId");

				if (isUp) {
					hql.append(" ORDER BY a.posIndex DESC");
				} else {
					hql.append(" ORDER BY a.posIndex ASC");
				}

				Query<Normalizer> query = session.createQuery(hql.toString());
				query.setParameter("domainId", getDomainId());
				query.setParameter("posIndex", item.getPosIndex());
				query.setParameter("categoryId", item.getCategoryId());
				query.setMaxResults(1);

				List<Normalizer> normalizers = query.getResultList();
				if (normalizers != null && normalizers.size() > 0) {
					// if (!checkFieldIsExist("posIndex", item.getPosIndex(),
					// item)) {
					Normalizer itemMove = normalizers.get(0);
					Long tmpIndex = 0l;
					tmpIndex = itemMove.getPosIndex();
					itemMove.setPosIndex(item.getPosIndex());
					item.setPosIndex(tmpIndex);
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

	public int countNormalizer() {
		String hql = "SELECT COUNT(a) FROM Normalizer a WHERE  a.domainId =:domainId ";
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

	public Long getMaxIndex(Session session) {
		try {
			StringBuffer hql = new StringBuffer("SELECT COALESCE(MAX(a.posIndex), 0) FROM Normalizer a");
			hql.append(" WHERE a.domainId = :domainId");
			Query<Long> query = session.createQuery(hql.toString(), Long.class);
			query.setParameter("domainId", getDomainId());
			return query.getSingleResult();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}		
	}

	private void deleteNormValue(Normalizer normalizer, Session session) {
		try {
			StringBuffer hql = new StringBuffer("DELETE FROM NormValue a");
			hql.append(" WHERE a.normValueId IN (");
			hql.append("SELECT b.normValueId FROM NormalizerNormValueMap b");
			hql.append(" WHERE b.normalizerId =:normalizerId)");
			Query<Long> query = session.createQuery(hql.toString(), Long.class);
			query.setParameter("normalizerId", normalizer.getNormalizerId());
			query.executeUpdate();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}		
	}

	private void deleteNormParam(Normalizer normalizer, Session session) {
		try {
			StringBuffer hql = new StringBuffer("DELETE FROM NormParam a");
			hql.append(" WHERE a.normParamId IN (");
			hql.append("SELECT b.normParamId FROM NomalizerNormParamMap b");
			hql.append(" WHERE b.normalizerId =:normalizerId)");
			Query<Long> query = session.createQuery(hql.toString(), Long.class);
			query.setParameter("normalizerId", normalizer.getNormalizerId());
			query.executeUpdate();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}		
	}

	public void deleteItemByNormalizer(Normalizer normalizer, Session session) {
		try {
			normalizer = session.get(Normalizer.class, normalizer.getNormalizerId());
			if (normalizer != null && normalizer.isGenerated() != null && normalizer.isGenerated()) {
				deleteNormValue(normalizer, session);
				deleteNormParam(normalizer, session);
				session.delete(normalizer);
			}
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}		
	}

	public List<Normalizer> findByListCategoryIdAndGened(List<Long> lstAllCatID) {
		String hql = "SELECT a FROM Normalizer a";
		hql += " WHERE a.categoryId IN (:categoryIds) AND (a.generated is false OR a.generated is NULL)";

		Session session = HibernateUtil.getOpenSession();
		try {

			Query<Normalizer> query = session.createQuery(hql, Normalizer.class);
			query.setParameterList("categoryIds", lstAllCatID);
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public boolean moveCatNormalizer(Normalizer normalizer) {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			normalizer.setDomainId(getDomainId());
			generateIndexByCat(normalizer, "posIndex", "normalizerId", session);
			session.saveOrUpdate(normalizer);
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

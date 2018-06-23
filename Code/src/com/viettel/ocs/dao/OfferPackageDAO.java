package com.viettel.ocs.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.OfferPackage;
import com.viettel.ocs.entity.OfferpkgOfferMap;
import com.viettel.ocs.model.OfferpkgOfferMapModel;

public class OfferPackageDAO extends BaseDAO<OfferPackage> {

	@Override
	protected Class<OfferPackage> getEntityClass() {
		return OfferPackage.class;
	}

	public List<OfferPackage> findListOfferPackageByCategoryId(List<Long> lstCatID) {
		String[] cols = { "categoryId" };
		Operator[] operators = { Operator.IN };
		List<Long> lstCatIDParam = new ArrayList<Long>();
		lstCatIDParam.addAll(lstCatID);
		if (lstCatIDParam.size() == 0)
			lstCatIDParam.add(-1L);
		Object[] values = { lstCatIDParam };
		String oder = "index";
		return super.findByConditions(cols, operators, values, oder);
	}

	public List<OfferPackage> findByCategory(Category category) {
		return super.findByConditions(new String[] { "categoryId" }, new Operator[] { Operator.EQ },
				new Object[] { category.getCategoryId() }, "index");
	}

	private Long getMaxIndex(Session session, Long categoryId) {
		StringBuffer hql = new StringBuffer("SELECT COALESCE(MAX(a.index), 0) FROM OfferPackage a");
		hql.append(" WHERE a.domainId = :domainId");
		hql.append(" AND (:categoryId IS NULL OR a.categoryId = :categoryId)");
		try {

			Query<Long> query = session.createQuery(hql.toString(), Long.class);
			query.setParameter("domainId", getDomainId());
			query.setParameter("categoryId", categoryId);
			return query.getSingleResult();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}
	}

	private Long getMaxIndex(Session session) {
		return this.getMaxIndex(session, null);
	}

	public Long getMaxIndex(Long categoryId) {
		Session session = HibernateUtil.getOpenSession();
		try {
			return this.getMaxIndex(session, categoryId);
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public Long getMaxIndex() {
		Session session = HibernateUtil.getOpenSession();
		try {
			return this.getMaxIndex(session, null);
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public OfferPackage moveObjectTo(OfferPackage offerPackage, boolean up) {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			OfferPackage offerTarget;
			if (up)
				offerTarget = this.findObjectBefore(offerPackage, session);
			else
				offerTarget = this.findObjectAfter(offerPackage, session);

			Long pos = offerTarget.getIndex();
			offerTarget.setIndex(offerPackage.getIndex());
			offerPackage.setIndex(pos);

			session.update(offerPackage);
			session.update(offerTarget);

			session.getTransaction().commit();
			return offerTarget;
		} catch (Exception e) {
			session.getTransaction().rollback();
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	private OfferPackage findObjectAfter(OfferPackage offerPackage, Session session) {
		StringBuffer hql = new StringBuffer("SELECT a FROM OfferPackage a");
		hql.append(" WHERE a.domainId = :domainId");
		hql.append(" AND a.index > :index");
		hql.append(" AND a.categoryId = :categoryId");
		try {

			Query<OfferPackage> query = session.createQuery(hql.toString(), OfferPackage.class);
			query.setParameter("domainId", getDomainId());
			query.setParameter("index", offerPackage.getIndex());
			query.setParameter("categoryId", offerPackage.getCategoryId());

			query.setMaxResults(1);
			return query.getSingleResult();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}
	}

	private OfferPackage findObjectBefore(OfferPackage offerPackage, Session session) {
		StringBuffer hql = new StringBuffer("SELECT a FROM OfferPackage a");
		hql.append(" WHERE a.domainId = :domainId");
		hql.append(" AND a.index < :index");
		hql.append(" AND a.categoryId = :categoryId");
		try {

			Query<OfferPackage> query = session.createQuery(hql.toString(), OfferPackage.class);
			query.setParameter("domainId", getDomainId());
			query.setParameter("index", offerPackage.getIndex());
			query.setParameter("categoryId", offerPackage.getCategoryId());

			query.setMaxResults(1);
			return query.getSingleResult();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}
	}

	private Long getMinIndex(Session session, Long categoryId) {
		StringBuffer hql = new StringBuffer("SELECT COALESCE(MIN(a.index), 0) FROM OfferPackage a");
		hql.append(" WHERE a.domainId = :domainId");
		hql.append(" AND (:categoryId IS NULL OR a.categoryId = :categoryId)");
		try {

			Query<Long> query = session.createQuery(hql.toString(), Long.class);
			query.setParameter("domainId", getDomainId());
			query.setParameter("categoryId", categoryId);
			return query.getSingleResult();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}
	}

	public Long getMinIndex(Long categoryId) {
		Session session = HibernateUtil.getOpenSession();
		try {
			return this.getMinIndex(session, categoryId);
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public Long getMinIndex() {
		Session session = HibernateUtil.getOpenSession();
		try {
			return this.getMinIndex(session, null);
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public boolean saveOfferPackageAndMap(OfferPackage offerPackage, List<OfferpkgOfferMapModel> normOffersOfPackage) {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			// SAVE OFFERPACKAGE
			if (offerPackage.getOfferPkgId() == 0L) {
				offerPackage.setDomainId(getDomainId());
				offerPackage.setIndex(this.getMaxIndex(session) + 1);
				session.save(offerPackage);
			} else {
				session.update(offerPackage);
			}

			List<Long> ids = new ArrayList<Long>();
			ids.add(-1L);

			for (OfferpkgOfferMapModel offerMapModel : normOffersOfPackage) {
				ids.add(offerMapModel.getOfferPackageMap().getOfferId());
			}

			this.deleteNormOfferpkgOfferMaps(offerPackage.getOfferPkgId(), ids, session);

			for (OfferpkgOfferMapModel offerMapModel : normOffersOfPackage) {
				OfferpkgOfferMap offerpkgOfferMap = offerMapModel.getOfferPackageMap();
				offerpkgOfferMap.setIndex(normOffersOfPackage.indexOf(offerMapModel));
				if (offerpkgOfferMap.getOfferpkgOfferMapId() == 0L) {
					offerpkgOfferMap.setDomainId(getDomainId());
					offerpkgOfferMap.setOfferPackageId(offerPackage.getOfferPkgId());
					session.save(offerpkgOfferMap);
				} else {
					session.update(offerpkgOfferMap);
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

	@SuppressWarnings("unchecked")
	private void deleteNormOfferpkgOfferMaps(long offerPackageId, List<Long> ids, Session session) {
		StringBuffer hql = new StringBuffer("DELETE FROM OfferpkgOfferMap a");
		hql.append(" WHERE a.offerId NOT IN (:offerIds)");
		hql.append(" AND a.offerPackageId = :offerPackageId");
		hql.append(" AND a.domainId = :domainId");
		try {

			Query<OfferpkgOfferMap> query = session.createQuery(hql.toString());
			query.setParameter("offerPackageId", offerPackageId);
			query.setParameter("domainId", getDomainId());
			query.setParameterList("offerIds", ids);
			query.executeUpdate();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}
	}

	public boolean checkDependencies(OfferPackage offerPackage) {
		StringBuffer hql_1 = new StringBuffer("SELECT COUNT(a) FROM Offer a");
		hql_1.append(" WHERE a.offerPackageId = :offerPackageId");
		hql_1.append(" AND a.domainId = :domainId");
		
		StringBuffer hql_2 = new StringBuffer("SELECT COUNT(a) FROM OfferpkgOfferMap a");
		hql_2.append(" WHERE a.offerPackageId = :offerPackageId");
		hql_2.append(" AND a.domainId = :domainId");

		Session session = HibernateUtil.getOpenSession();
		try {
			Query<Long> query_1 = session.createQuery(hql_1.toString(), Long.class);
			query_1.setParameter("offerPackageId", offerPackage.getOfferPkgId());
			query_1.setParameter("domainId", getDomainId());
			
			Query<Long> query_2 = session.createQuery(hql_2.toString(), Long.class);
			query_2.setParameter("offerPackageId", offerPackage.getOfferPkgId());
			query_2.setParameter("domainId", getDomainId());

			if (query_1.getSingleResult() > 0L || query_2.getSingleResult() > 0L) {
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

	public boolean checkExisted(OfferPackage offerPackage) {
		StringBuffer hql = new StringBuffer("SELECT COUNT(a) FROM OfferPackage a");
		hql.append(" WHERE (a.offerPkgName LIKE :offerPkgName");
		hql.append(" OR (a.externalId LIKE :externalId AND a.externalId NOT LIKE ''))");
		hql.append(" AND a.offerPkgId != :offerPkgId");
		hql.append(" AND a.domainId = :domainId");

		Session session = HibernateUtil.getOpenSession();
		try {
			Query<Long> query = session.createQuery(hql.toString(), Long.class);
			query.setParameter("offerPkgName", offerPackage.getOfferPkgName());
			query.setParameter("externalId", offerPackage.getExternalId());
			query.setParameter("offerPkgId", offerPackage.getOfferPkgId());
			query.setParameter("domainId", getDomainId());

			if (query.getSingleResult() > 0L) {
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
}

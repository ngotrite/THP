package com.viettel.ocs.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.entity.OfferpkgOfferMap;
import com.viettel.ocs.model.OfferpkgOfferMapModel;

public class OfferpkgOfferMapDAO extends BaseDAO<OfferpkgOfferMap> {
	@Override
	protected Class<OfferpkgOfferMap> getEntityClass() {
		return OfferpkgOfferMap.class;
	}

	@SuppressWarnings("unchecked")
	public List<OfferpkgOfferMapModel> findByOfferId(Long offerId) {
		StringBuffer hql = new StringBuffer(
				"SELECT new com.viettel.ocs.model.OfferpkgOfferMapModel(a, b.offerPkgName, c.offerName, c.state, c.description) FROM OfferpkgOfferMap a");
		hql.append(" JOIN OfferPackage b ON b.offerPkgId = a.offerPackageId");
		hql.append(" LEFT JOIN Offer c ON c.offerId = b.mainOfferId");
		hql.append(" WHERE a.domainId =:domainId");
		hql.append(" AND a.offerId =:offerId");

		Session session = HibernateUtil.getOpenSession();
		try {
			Query<OfferpkgOfferMapModel> query = session.createQuery(hql.toString());
			query.setParameter("domainId", getDomainId());
			query.setParameter("offerId", offerId);
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}
}

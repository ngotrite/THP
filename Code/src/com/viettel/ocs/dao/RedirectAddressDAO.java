package com.viettel.ocs.dao;

import org.hibernate.Session;

import com.viettel.ocs.entity.Offer;
import com.viettel.ocs.entity.RedirectAddress;

public class RedirectAddressDAO extends BaseDAO<RedirectAddress> {
	@Override
	protected Class<RedirectAddress> getEntityClass() {
		return RedirectAddress.class;
	}

	/**
	 * Clone RedirectAddress
	 * 
	 * @author THANHND
	 * @param redirectAddress
	 * @param offer
	 * @param session
	 * @throws CloneNotSupportedException
	 */
	public void cloneRedirectAddress(RedirectAddress redirectAddress, Offer offer, Session session)
			throws CloneNotSupportedException {
		try {

			RedirectAddress redirectAddressToClone = redirectAddress.clone();
			redirectAddressToClone.setOfferId(offer.getOfferId());
			redirectAddressToClone.setRedirectAddressId(0L);
			session.save(redirectAddressToClone);
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}
	}

}

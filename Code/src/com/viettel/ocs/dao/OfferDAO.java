package com.viettel.ocs.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.constant.ContantsUtil;
import com.viettel.ocs.constant.ContantsUtil.OfferState;
import com.viettel.ocs.constant.ContantsUtil.OfferType;
import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.Action;
import com.viettel.ocs.entity.BalType;
import com.viettel.ocs.entity.DecisionTable;
import com.viettel.ocs.entity.Formula;
import com.viettel.ocs.entity.NormParam;
import com.viettel.ocs.entity.Normalizer;
import com.viettel.ocs.entity.Offer;
import com.viettel.ocs.entity.OfferActionMap;
import com.viettel.ocs.entity.OfferDump;
import com.viettel.ocs.entity.OfferParameterMap;
import com.viettel.ocs.entity.OfferVersionStatistic;
import com.viettel.ocs.entity.OfferpkgOfferMap;
import com.viettel.ocs.entity.Parameter;
import com.viettel.ocs.entity.RateTable;
import com.viettel.ocs.entity.RedirectAddress;
import com.viettel.ocs.model.CloneIgnoreListModel;
import com.viettel.ocs.model.LogicCloneModel;
import com.viettel.ocs.model.OfferRecompiledModel;
import com.viettel.ocs.model.OfferpkgOfferMapModel;
import com.viettel.ocs.model.PrameterModel;

public class OfferDAO extends BaseDAO<Offer> {

	@Override
	protected Class<Offer> getEntityClass() {
		return Offer.class;
	}

	public List<OfferDump> findOfferByCatId(Long catId) {
		StringBuffer hql = new StringBuffer();
		hql.append("SELECT DISTINCT a.offerName, a.categoryId, a.offerExternalId FROM Offer a");
		hql.append(" WHERE a.categoryId = :categoryId");
		hql.append(" AND a.domainId = :domainId");
		hql.append(" ORDER BY a.offerName");

		List<OfferDump> offerDumps = new ArrayList<OfferDump>();

		Session session = HibernateUtil.getOpenSession();
		try {
			Query<?> query = session.createQuery(hql.toString());
			query.setParameter("domainId", getDomainId());
			query.setParameter("categoryId", catId);
			List<?> objects = query.getResultList();

			for (Object object : objects) {
				Object[] values = (Object[]) object;
				offerDumps.add(new OfferDump((String) values[0], (Long) values[1], (String) values[2]));
			}
			return offerDumps;
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public List<Offer> findOfferByListCatId(List<Long> lstCatId) {

		if(lstCatId == null || lstCatId.isEmpty())
			return new ArrayList<>();

		List<Offer> lstOffer = null;
		String[] cols = { "categoryId", "state" };
		Operator[] operators = { Operator.IN, Operator.NOTEQ };
		Object[] values = { lstCatId, OfferState.REMOVED };
		lstOffer = findByConditions(cols, operators, values, "categoryId, offerName, offerExternalId, CAST(versionInfo AS integer) DESC");
		return lstOffer;
	}
	
	public List<OfferVersionStatistic> findOfferStatistic(List<Long> lstCatId) {
		
		if(lstCatId == null || lstCatId.isEmpty())
			return new ArrayList<>();
		
		List<OfferVersionStatistic> lstOfferStatistic = null;
		String hql = "SELECT a FROM OfferVersionStatistic a JOIN Offer b ON a.offerId = b.offerId";
		hql += " WHERE b.categoryId IN (:categoryId)";		
		Session session = HibernateUtil.getOpenSession();
		try {
			Query<OfferVersionStatistic> query = session.createQuery(hql.toString(), OfferVersionStatistic.class);
			query.setParameter("categoryId", lstCatId);
			lstOfferStatistic = query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
		
		return lstOfferStatistic;
	}

	public List<List<Offer>> getGroupListOfferByListCatId(List<Long> lstCatId) {

		List<Offer> lst = this.findOfferByListCatId(lstCatId);
		List<OfferVersionStatistic> lstStatistic = this.findOfferStatistic(lstCatId);
		for(Offer offer: lst) {
			for(OfferVersionStatistic statistic : lstStatistic) {
				
				if(offer.getOfferId() == statistic.getOfferId()) {
					offer.setOfferUsage(statistic.getOfferUsage());
					lstStatistic.remove(statistic);
					break;
				}
			}
		}
		
		return groupListOfferVersionByName(lst);
	}

	public List<List<Offer>> groupListOfferVersionByName(List<Offer> lstOfferOrdered) {

		List<List<Offer>> lst = new ArrayList<>();
		List<Offer> lstSub = new ArrayList<>();
		lst.add(lstSub);

		if (lstOfferOrdered == null || lstOfferOrdered.size() == 0) {
			return lst;
		}

		Offer preOffer = lstOfferOrdered.get(0);
		lstSub.add(preOffer);
		for (int i = 1; i < lstOfferOrdered.size(); i++) {

			Offer off = lstOfferOrdered.get(i);
			if (preOffer.equalWith(off)) {

				lstSub.add(off);
				preOffer = off;
			} else {

				lstSub = new ArrayList<>();
				lst.add(lstSub);
				lstSub.add(off);
				preOffer = off;
			}
		}

		return lst;
	}

	public List<RedirectAddress> getRedirectAddress(Offer offer) {
		StringBuffer hql = new StringBuffer("SELECT a FROM RedirectAddress a");
		hql.append(" WHERE a.domainId =:domainId");
		hql.append(" AND a.offerId =:offerId");

		Session session = HibernateUtil.getOpenSession();
		try {
			Query<RedirectAddress> query = session.createQuery(hql.toString(), RedirectAddress.class);
			query.setParameter("domainId", getDomainId());
			query.setParameter("offerId", offer.getOfferId());
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public int countOfferByOfferPackage(Long offerPackageId) {

		List lst = null;
		String hql = "SELECT COUNT(a.offerId) FROM Offer a JOIN OfferpkgOfferMap map ON map.offerId = a.offerId ";
		hql += " WHERE map.offerPackageId = :offerPackageId";

		Session session = HibernateUtil.getOpenSession();
		try {

			Query query = session.createQuery(hql);
			query.setParameter("offerPackageId", offerPackageId);
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

	public List<Offer> findListByOfferPackage(Long offerPackageId) {

		List<Offer> lst = null;
		String hql = "SELECT a FROM Offer a JOIN OfferpkgOfferMap map ON map.offerId = a.offerId ";
		hql += " WHERE map.offerPackageId = :offerPackageId";

		Session session = HibernateUtil.getOpenSession();
		try {

			Query<Offer> query = session.createQuery(hql, Offer.class);
			query.setParameter("offerPackageId", offerPackageId);
			lst = query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}

		return lst;
	}

	public List<OfferpkgOfferMapModel> findByOfferPackage(Long offerPackageId) {
		StringBuffer hql = new StringBuffer(
				"SELECT new com.viettel.ocs.model.OfferpkgOfferMapModel(a, b.offerPkgName, concat(c.offerName, ' v', c.versionInfo), c.state, c.description) FROM OfferpkgOfferMap a");
		hql.append(" JOIN OfferPackage b ON b.offerPkgId = a.offerPackageId");
		hql.append(" JOIN Offer c ON c.offerId = a.offerId");
		hql.append(" WHERE a.domainId =:domainId");
		hql.append(" AND a.offerPackageId =:offerPackageId");
		hql.append(" ORDER BY a.index ASC");

		Session session = HibernateUtil.getOpenSession();
		try {
			Query<OfferpkgOfferMapModel> query = session.createQuery(hql.toString(), OfferpkgOfferMapModel.class);
			query.setParameter("domainId", getDomainId());
			query.setParameter("offerPackageId", offerPackageId);
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public List<Offer> findByOfferPkg(Long offerPackageId) {
		StringBuffer hql = new StringBuffer("SELECT c FROM OfferpkgOfferMap a");
		hql.append(" JOIN OfferPackage b ON b.offerPkgId = a.offerPackageId");
		hql.append(" JOIN Offer c ON c.offerId = a.offerId");
		hql.append(" WHERE a.domainId =:domainId");
		hql.append(" AND a.offerPackageId =:offerPackageId");

		Session session = HibernateUtil.getOpenSession();
		try {
			Query<Offer> query = session.createQuery(hql.toString(), Offer.class);
			query.setParameter("domainId", getDomainId());
			query.setParameter("offerPackageId", offerPackageId);
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public boolean updateParameterMap(List<PrameterModel> prameterModels) {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {

			for (PrameterModel prameterModel : prameterModels) {
				OfferParameterMap parameterMap = prameterModel.getOfferParameterMap();
				session.update(parameterMap);
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

	public boolean saveOfferAndMap(Offer offer, List<Action> actions, List<RedirectAddress> redirectAddresses,
			List<OfferpkgOfferMapModel> normOfferpkgOfferMaps) {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			// SAVE OFFER
			if (offer.getOfferId() == 0L) {
				offer.setDomainId(getDomainId());
				session.save(offer);
			} else {
				session.update(offer);
			}

			// SAVE REDIRECTADDRESS
			for (RedirectAddress redirectAddress : redirectAddresses) {
				if (redirectAddress.getRedirectAddressId() == 0L) {
					redirectAddress.setOfferId(offer.getOfferId());
					redirectAddress.setDomainId(getDomainId());
					session.save(redirectAddress);
				} else {
					session.update(redirectAddress);
				}
			}

			List<Long> ids = new ArrayList<Long>();
			ids.add(-1L);

			// SAVE ACTIONS
			for (Action action : actions) {
				ids.add(action.getActionId());
			}
			this.deleteOfferActionMapByActionAndOffer(offer, ids, session);
			for (Action action : actions) {
				OfferActionMap offerActionMap = this.findOfferActionMapByActionAndOffer(offer, action, session);
				offerActionMap.setDomainId(getDomainId());
				offerActionMap.setMapIndex(Long.valueOf(actions.indexOf(action)));
				session.saveOrUpdate(offerActionMap);
			}

			// SAVE NORMOFFEROFPACKAGE
			if (offer.getOfferType() != ContantsUtil.OfferType.MAIN) {
				ids.clear();
				ids.add(-1L);
				for (OfferpkgOfferMapModel offerpkgOfferMapModel : normOfferpkgOfferMaps) {
					ids.add(offerpkgOfferMapModel.getOfferPackageMap().getOfferpkgOfferMapId());
				}
				this.deleteNormOfferpkgOfferMaps(offer.getOfferId(), ids, session);

				for (OfferpkgOfferMapModel offerpkgOfferMapModel : normOfferpkgOfferMaps) {
					OfferpkgOfferMap offerpkgOfferMap = offerpkgOfferMapModel.getOfferPackageMap();
					if (offerpkgOfferMap.getOfferpkgOfferMapId() == 0L) {
						offerpkgOfferMap.setDomainId(getDomainId());
						offerpkgOfferMap.setOfferId(offer.getOfferId());
						session.save(offerpkgOfferMap);
					} else {
						session.update(offerpkgOfferMap);
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

	private void deleteNormOfferpkgOfferMaps(long offerId, List<Long> ids, Session session) {
		StringBuffer hql = new StringBuffer("DELETE FROM OfferpkgOfferMap a");
		hql.append(" WHERE a.offerpkgOfferMapId NOT IN (:offerpkgOfferMapIds)");
		hql.append(" AND a.offerId = :offerId");
		hql.append(" AND a.domainId = :domainId");
		try {
			Query<?> query = session.createQuery(hql.toString());
			query.setParameterList("offerpkgOfferMapIds", ids);
			query.setParameter("domainId", getDomainId());
			query.setParameter("offerId", offerId);
			query.executeUpdate();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}		
	}

	private void deleteNormOfferpkgOfferMaps(Offer offer, Session session) {
		StringBuffer hql = new StringBuffer("DELETE FROM OfferpkgOfferMap a");
		hql.append(" WHERE a.offerId = :offerId");
		hql.append(" AND a.domainId = :domainId");
		try {

			Query<?> query = session.createQuery(hql.toString());
			query.setParameter("domainId", getDomainId());
			query.setParameter("offerId", offer.getOfferId());
			query.executeUpdate();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}
	}

	private void deleteOfferActionMapByActionAndOffer(Offer offer, List<Long> actionIds, Session session) {
		StringBuffer hql = new StringBuffer("DELETE FROM OfferActionMap a");
		hql.append(" WHERE a.offerId = :offerId");
		hql.append(" AND a.actionId NOT IN (:actionIds)");
		hql.append(" AND a.domainId = :domainId");
		try {

			Query<?> query = session.createQuery(hql.toString());
			query.setParameterList("actionIds", actionIds);
			query.setParameter("offerId", offer.getOfferId());
			query.setParameter("domainId", getDomainId());
			query.executeUpdate();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}
	}

	private OfferActionMap findOfferActionMapByActionAndOffer(Offer offer, Action action, Session session) {
		StringBuffer hql = new StringBuffer("SELECT a FROM OfferActionMap a");
		hql.append(" WHERE a.offerId = :offerId");
		hql.append(" AND a.actionId = :actionId");
		hql.append(" AND a.domainId = :domainId");
		try {

			Query<OfferActionMap> query = session.createQuery(hql.toString(), OfferActionMap.class);
			query.setParameter("offerId", offer.getOfferId());
			query.setParameter("actionId", action.getActionId());
			query.setParameter("domainId", getDomainId());
			List<OfferActionMap> actionMaps = query.getResultList();
			if (actionMaps != null && actionMaps.size() > 0) {
				return actionMaps.get(0);
			} else {
				return new OfferActionMap(offer.getOfferId(), action.getActionId());
			}
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}
	}

	public String generateOfferVersion(Offer offer) {
		StringBuffer hql = new StringBuffer("SELECT COALESCE(MAX(CAST(a.versionInfo as long)), 0) FROM Offer a");
		hql.append(" WHERE a.domainId =:domainId");
		hql.append(" AND a.offerName LIKE :offerName");
		hql.append(" AND a.offerExternalId =:offerExternalId");
		hql.append(" AND a.categoryId =:categoryId");

		Session session = HibernateUtil.getOpenSession();
		try {
			Query<Long> query = session.createQuery(hql.toString(), Long.class);
			query.setParameter("domainId", getDomainId());
			query.setParameter("offerName", offer.getOfferName());
			query.setParameter("offerExternalId", offer.getOfferExternalId());
			query.setParameter("categoryId", offer.getCategoryId());
			return String.valueOf(query.getSingleResult() + 1L);
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	/**
	 * Clone offer
	 * 
	 * @author THANHND
	 * @param offer
	 * @param logicCloneModel
	 * @return
	 */
	public Offer cloneOffer(Offer offer, LogicCloneModel logicCloneModel, String suffix) {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			Offer offerToClone = this.cloneOffer(offer, 0L, logicCloneModel, suffix, false, new ArrayList<Parameter>(),
					true, session);
			session.getTransaction().commit();
			return offerToClone;
		} catch (Exception e) {
			
			session.getTransaction().rollback();
			getLogger().error(e.getMessage(), e);
			return null;
		} finally {
			session.close();
		}
	}

	private Offer cloneOffer(Offer offer, long newOfferId, LogicCloneModel logicCloneModel, String suffix,
			boolean replaceParam, List<Parameter> parameters, boolean isClone, Session session)
			throws CloneNotSupportedException {
		try {

			Offer offerToClone = offer.clone();

			List<RedirectAddress> redirectAddresses = this.findRedirectAddressByOffer(offerToClone, session);// Find
			// redirect
			// addresses

			List<Action> actions = this.findActionByOffer(offerToClone, session);// Find
			// actions

			List<OfferpkgOfferMap> offerpkgOfferMaps = this.findOfferpkgOfferMapByOffer(offerToClone, session);

			if (newOfferId == 0L) {
				// Save new offer clone
				offerToClone.setOfferId(newOfferId);
				session.save(offerToClone);
			} else {
				offerToClone.setOfferId(newOfferId);
				session.update(offerToClone);
			}

			// Clone redirect address
			for (RedirectAddress redirectAddress : redirectAddresses) {
				RedirectAddressDAO redirectAddressDAO = new RedirectAddressDAO();
				redirectAddressDAO.cloneRedirectAddress(redirectAddress, offerToClone, session);
			}

			// Clone OfferpkgOfferMap
			for (OfferpkgOfferMap offerpkgOfferMap : offerpkgOfferMaps) {
				OfferpkgOfferMap offerpkgOfferMapToClone = offerpkgOfferMap.clone();
				offerpkgOfferMapToClone.setOfferId(offerToClone.getOfferId());
				session.save(offerpkgOfferMapToClone);
			}

			CloneIgnoreListModel ignoreList = new CloneIgnoreListModel();
			CloneIgnoreListModel usingParams = new CloneIgnoreListModel();

			if (!isClone) {
				usingParams = findTreeUsingParam(offer, session);
			}

			// Level Action - 2
			for (Action action : actions) {
				ActionDAO actionDAO = new ActionDAO();

				// Create map between offer and action
				OfferActionMap offerActionMap = new OfferActionMap(offerToClone.getOfferId(), action.getActionId(),
						Long.valueOf(actions.indexOf(action)));
				offerActionMap.setDomainId(getDomainId());

				if (logicCloneModel.isAction()) {
					offerActionMap.setActionId(actionDAO
							.cloneAction(action, logicCloneModel.isPc(), logicCloneModel.isBlock(),
									logicCloneModel.isRateTable(), logicCloneModel.isDt(), logicCloneModel.isNormalizer(),
									suffix, replaceParam, parameters, ignoreList, isClone, usingParams, session)
							.getActionId());
				}
				// Save map
				session.save(offerActionMap);
			}
			return offerToClone;
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}		
	}

	public Offer generateOffer(Offer offer, Long newOfferId, List<Parameter> parameters, String suffix) {

		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			Offer offerToClone = this.cloneOffer(offer, newOfferId,
					new LogicCloneModel(true, true, true, true, true, true, true), suffix, true, parameters, false,
					session);
			for (Parameter parameter : parameters) {
				OfferParameterMap offerParameterMap = new OfferParameterMap(offerToClone.getOfferId(),
						parameter.getParameterId());
				offerParameterMap.setValue(parameter.getParameterValue());
				offerParameterMap.setDomainId(getDomainId());
				session.save(offerParameterMap);
			}

			session.getTransaction().commit();
			return offerToClone;
		} catch (Exception e) {
			
			session.getTransaction().rollback();
			getLogger().error(e.getMessage(), e);
			return null;
		} finally {
			session.close();
		}
	}

	public Offer reCompiledOffer(Offer compiledOffer, Offer offer, List<Parameter> parameters, String suffix) {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			this.deleteOfferParameterMapsByOffer(compiledOffer, session);

			this.deleteTreeOffer(compiledOffer, session);

			this.deleteNormOfferpkgOfferMaps(compiledOffer, session);

			Offer offerToClone = this.cloneOffer(offer, compiledOffer.getOfferId(),
					new LogicCloneModel(true, true, true, true, true, true, true), suffix, true, parameters, false,
					session);
			for (Parameter parameter : parameters) {
				OfferParameterMap offerParameterMap = new OfferParameterMap(offerToClone.getOfferId(),
						parameter.getParameterId());
				offerParameterMap.setValue(parameter.getParameterValue());
				session.save(offerParameterMap);
			}

			session.getTransaction().commit();
			return offerToClone;
		} catch (Exception e) {
			
			session.getTransaction().rollback();
			getLogger().error(e.getMessage(), e);
			return null;
		} finally {
			session.close();
		}
	}

	public boolean reCompiledOffers(List<OfferRecompiledModel> offerModels, Offer offer,
			HashMap<Long, List<Parameter>> parametersMap, String suffix) throws Exception {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			for (OfferRecompiledModel offerModel : offerModels) {

				if (!offerModel.isSelected()) {
					continue;
				}

				Offer offerToComp = offerModel.getOffer();

				offer.setOfferName(offerToComp.getOfferName());
				offer.setOfferExternalId(offerToComp.getOfferExternalId());
				offer.setCategoryId(offerToComp.getCategoryId());
				offer.setEffDate(offerToComp.getEffDate());
				offer.setState(offerToComp.getState());
				offer.setPriority(offerToComp.getPriority());
				offer.setOfferType(offerToComp.getOfferType());
				offer.setVersionInfo(offerToComp.getVersionInfo());
				offer.setOfferTemplateId(offer.getOfferId());

				this.deleteOfferParameterMapsByOffer(offerToComp, session);

				this.deleteTreeOffer(offerToComp, session);

				this.deleteNormOfferpkgOfferMaps(offerToComp, session);

				List<Parameter> parameters = parametersMap.get(offerModel.getOffer().getOfferId());

				Offer offerToClone = this.cloneOffer(offer, offerToComp.getOfferId(),
						new LogicCloneModel(true, true, true, true, true, true, true), suffix, true, parameters, false,
						session);
				for (Parameter parameter : parameters) {
					OfferParameterMap offerParameterMap = new OfferParameterMap(offerToClone.getOfferId(),
							parameter.getParameterId());
					offerParameterMap.setValue(parameter.getParameterValue());

					session.save(offerParameterMap);
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

	private void deleteTreeOffer(Offer compiledOffer, Session session) {
		this.deleteRedirectAddressByOffer(compiledOffer, session);// Delete
		// redirect
		// addresses

		List<Action> actions = this.findActionByOffer(compiledOffer, session);// Find

		// Level Action - 2
		for (Action action : actions) {
			ActionDAO actionDAO = new ActionDAO();
			actionDAO.deleteTreeAction(action, compiledOffer, session);
		}

		// session.delete(compiledOffer);
		// this.deleteActionByOffer(compiledOffer, session);// delete
	}

	/**
	 * @author THANHND
	 * @param offer
	 * @return parameters of offer
	 */
	public List<Parameter> findListParameterByOffer(Offer offer) {
		List<Parameter> parameters = new ArrayList<Parameter>();
		List<RateTable> rateTables = this.findRateTablesByOffer(offer);

		for (RateTable rateTable : rateTables) {
			RateTableDAO rateTableDAO = new RateTableDAO();
			List<Formula> formulas = rateTableDAO.findFormulaByRateTable(rateTable);

			FormulaDAO formulaDAO = new FormulaDAO();
			if (rateTable.getDefaultFormulaId() != null && rateTable.getDefaultFormulaId() != 0L) {
				Formula defaultFormula = formulaDAO.get(rateTable.getDefaultFormulaId());
				if (defaultFormula != null) {
					formulas.add(defaultFormula);
				}
			}

			if (rateTable.getFormulaId() != null && rateTable.getFormulaId() != 0L) {
				Formula stateFormula = formulaDAO.get(rateTable.getFormulaId());
				if (stateFormula != null) {
					formulas.add(stateFormula);
				}
			}

			// Get formula's parameter
			ParameterDAO parameterDAO = new ParameterDAO();
			for (Formula formula : formulas) {
				parameters.addAll(parameterDAO.findParameterByFormula(formula));
			}

			DecisionTableDAO decisionTableDAO = new DecisionTableDAO();
			if (rateTable.getDecisionTableId() != null && rateTable.getDecisionTableId() != 0L) {
				DecisionTable decisionTable = decisionTableDAO.get(rateTable.getDecisionTableId());
				List<Normalizer> normalizers = decisionTableDAO.findNormalizers(decisionTable);
				List<Long> normalizerIds = new ArrayList<Long>();

				for (Normalizer normalizer : normalizers) {
					normalizerIds.add(normalizer.getNormalizerId());
				}

				if (normalizerIds.size() > 0) {
					NormalizerDAO normalizerDAO = new NormalizerDAO();
					List<NormParam> normParams = normalizerDAO.getNormParamUsingParameterByNormalizer(normalizerIds);

					for (NormParam normParam : normParams) {
						if (normParam.getConfigInput()
								.contains(com.viettel.ocs.constant.Normalizer.UsingNormParam.IS_USING_PARAMETER)) {
							String[] items = normParam.getConfigInput().split(";");
							for (String string : items) {
								if (string.contains(com.viettel.ocs.constant.Normalizer.UsingNormParam.PARAMETER_ID)) {
									Parameter parameter = parameterDAO.get(Long.valueOf(string.replace(
											com.viettel.ocs.constant.Normalizer.UsingNormParam.PARAMETER_ID, "")));
									if (parameter != null && parameter.getForTemplate()) {
										parameters.add(parameter);
									}
								}
							}
							continue;
						}

						if (normParam.getConfigInput()
								.contains(com.viettel.ocs.constant.Normalizer.UsingNormParam.END_IS_PARAMETER)) {
							String[] items = normParam.getConfigInput().split(";");
							for (String string : items) {
								if (string.contains("end:")) {
									Parameter parameter = parameterDAO.get(Long.valueOf(string.replace("end:", "")));
									if (parameter != null && parameter.getForTemplate()) {
										parameters.add(parameter);
									}
								}
							}
						}

						if (normParam.getConfigInput()
								.contains(com.viettel.ocs.constant.Normalizer.UsingNormParam.START_IS_PARAMETER)) {
							String[] items = normParam.getConfigInput().split(";");
							for (String string : items) {
								if (string.contains("start:")) {
									Parameter parameter = parameterDAO.get(Long.valueOf(string.replace("start:", "")));
									if (parameter != null && parameter.getForTemplate()) {
										parameters.add(parameter);
									}
								}
							}
						}
					}
				}
			}
		}
		return parameters;
	}

	public List<RateTable> findRateTablesByOffer(Offer offer) {
		StringBuffer hql = new StringBuffer();
		hql.append("SELECT DISTINCT i FROM Offer a");
		hql.append(" JOIN OfferActionMap b ON b.offerId = a.offerId");
		hql.append(" JOIN Action c ON c.actionId = b.actionId");
		hql.append(" LEFT JOIN ActionPriceComponentMap d ON d.actionId = c.actionId");
		hql.append(" LEFT JOIN PriceComponentBlockMap e ON e.priceComponentId = d.priceComponentId");
		hql.append(" LEFT JOIN BlockRateTableMap f ON f.blockId = e.blockId");

		hql.append(" LEFT JOIN DynamicReserveRateTableMap g ON g.dynamicReserveId = c.dynamicReserveId");
		hql.append(" LEFT JOIN SortPriceRateTableMap h ON h.sortPriceComponentId = c.sortPriceComponentId");
		hql.append(
				" JOIN RateTable i ON i.rateTableId = f.rateTableId OR i.rateTableId = g.rateTableId OR i.rateTableId = h.rateTableId");
		hql.append(" WHERE a.offerId = :offerId");

		Session session = HibernateUtil.getOpenSession();
		try {
			Query<RateTable> query = session.createQuery(hql.toString(), RateTable.class);
			query.setParameter("offerId", offer.getOfferId());
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}

	}

	private List<Action> findActionByOffer(Offer offer, Session session) {
		try {
			String hql = "SELECT a FROM Action a JOIN OfferActionMap map ON map.actionId = a.actionId ";
			hql += " WHERE map.offerId = :offerId ORDER BY map.mapIndex";
			Query<Action> query = session.createQuery(hql, Action.class);
			query.setParameter("offerId", offer.getOfferId());
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}		
	}

	private void deleteActionByOffer(Offer offer, Session session) {
		try {
			String hql = "DELETE Action a WHERE a.actionId IN (SELECT map.actionId FROM OfferActionMap map";
			hql += " WHERE map.offerId = :offerId)";
			Query<?> query = session.createQuery(hql);
			query.setParameter("offerId", offer.getOfferId());
			query.executeUpdate();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}		
	}

	private List<OfferActionMap> findOfferActionMapByOffer(Offer offer, Session session) {
		try {
			String hql = "SELECT map FROM OfferActionMap map ";
			hql += " WHERE map.offerId = :offerId";
			Query<OfferActionMap> query = session.createQuery(hql, OfferActionMap.class);
			query.setParameter("offerId", offer.getOfferId());
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}		
	}

	private List<RedirectAddress> findRedirectAddressByOffer(Offer offer, Session session) {
		StringBuffer hql = new StringBuffer("SELECT a FROM RedirectAddress a");
		hql.append(" WHERE a.domainId =:domainId");
		hql.append(" AND a.offerId =:offerId");
		try {
			Query<RedirectAddress> query = session.createQuery(hql.toString(), RedirectAddress.class);
			query.setParameter("domainId", getDomainId());
			query.setParameter("offerId", offer.getOfferId());
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}		
	}

	private void deleteRedirectAddressByOffer(Offer offer, Session session) {
		StringBuffer hql = new StringBuffer("DELETE FROM RedirectAddress a");
		hql.append(" WHERE a.domainId =:domainId");
		hql.append(" AND a.offerId =:offerId");
		try {

			Query<?> query = session.createQuery(hql.toString());
			query.setParameter("domainId", getDomainId());
			query.setParameter("offerId", offer.getOfferId());
			query.executeUpdate();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}
	}

	public boolean deleteOffers(OfferDump offerGroup) {
		StringBuffer hql = new StringBuffer("DELETE FROM Offer a");
		hql.append(" WHERE a.domainId =:domainId");
		hql.append(" AND a.offerName LIKE :offerName");
		hql.append(" AND a.offerExternalId LIKE :offerExternalId");
		hql.append(" AND a.categoryId =:categoryId");

		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			Query<?> query = session.createQuery(hql.toString());
			query.setParameter("domainId", getDomainId());
			query.setParameter("offerName", offerGroup.getOfferName());
			query.setParameter("offerExternalId", offerGroup.getOfferExternalId());
			query.setParameter("categoryId", offerGroup.getCategoryId());
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

	public List<Offer> findOfferByActionId(List<Long> lstActionId) {

		List<Offer> lst = null;
		String hql = "SELECT DISTINCT a FROM Offer a JOIN OfferActionMap map ON map.offerId = a.offerId ";
		hql += " WHERE map.actionId IN (:actionId)";

		Session session = HibernateUtil.getOpenSession();
		try {

			Query<Offer> query = session.createQuery(hql, Offer.class);
			if (lstActionId.size() == 0)
				lstActionId.add(-1L);
			query.setParameterList("actionId", lstActionId);
			lst = query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}

		return lst;
	}

	public Object validateOfferExist(String offerExternalId, String offerName, String oldOfferName) {
		StringBuffer sql = new StringBuffer(
				"SELECT COALESCE(SUM(TRIM(a.OFFER_EXTERNAL_ID) LIKE BINARY :offerExternalId AND TRIM(a.OFFER_NAME) LIKE BINARY :offerName), 0) AS DUPLICATE_ALL");
		sql.append(
				", COALESCE(SUM(TRIM(a.OFFER_EXTERNAL_ID) LIKE :offerExternalId AND TRIM(a.OFFER_NAME) NOT LIKE :offerName), 0) AS DUPLICATE_ONLY_EXTERNAL_ID");
		sql.append(
				", COALESCE(SUM(TRIM(a.OFFER_EXTERNAL_ID) NOT LIKE :offerExternalId AND TRIM(a.OFFER_NAME) LIKE :offerName), 0) AS DUPLICATE_ONLY_NAME");
		sql.append(
				", COALESCE(SUM(TRIM(a.OFFER_EXTERNAL_ID) LIKE :offerExternalId OR TRIM(a.OFFER_NAME) LIKE :offerName), 0) AS DUPLICATE");
		sql.append(" FROM offer a");
		sql.append(" WHERE (TRIM(a.OFFER_EXTERNAL_ID) LIKE :offerExternalId OR a.OFFER_NAME LIKE :offerName)");
		sql.append(" AND a.DOMAIN_ID = :domainId AND a.OFFER_NAME NOT LIKE :oldOfferName");

		Session session = HibernateUtil.getOpenSession();
		try {
			Query<?> query = session.createNativeQuery(sql.toString());
			query.setParameter("offerExternalId", offerExternalId.trim());
			query.setParameter("offerName", offerName.trim());
			query.setParameter("oldOfferName", oldOfferName.trim());
			query.setParameter("domainId", getDomainId());
			return query.getSingleResult();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public List<PrameterModel> findListParameterByOfferCompiled(Offer offer) {
		StringBuffer hql = new StringBuffer("SELECT new com.viettel.ocs.model.PrameterModel(a, b) FROM Parameter a");
		hql.append(" JOIN OfferParameterMap b ON b.parameterId = a.parameterId");
		hql.append(" WHERE b.offerId = :offerId");

		Session session = HibernateUtil.getOpenSession();
		try {
			Query<PrameterModel> query = session.createQuery(hql.toString(), PrameterModel.class);
			query.setParameter("offerId", offer.getOfferId());
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public List<OfferParameterMap> findOfferParameterMapsByOfferCompiled(Offer offer) {
		StringBuffer hql = new StringBuffer("SELECT a FROM OfferParameterMap a");
		hql.append(" WHERE a.offerId = :offerId");

		Session session = HibernateUtil.getOpenSession();
		try {
			return this.findOfferParameterMapsByOfferCompiled(offer, session);
		} catch (Exception e) {
			session.getTransaction().rollback();
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	private List<OfferParameterMap> findOfferParameterMapsByOfferCompiled(Offer offer, Session session) {
		StringBuffer hql = new StringBuffer("SELECT a FROM OfferParameterMap a");
		hql.append(" WHERE a.offerId = :offerId");
		try {

			Query<OfferParameterMap> query = session.createQuery(hql.toString(), OfferParameterMap.class);
			query.setParameter("offerId", offer.getOfferId());
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}
	}

	private void deleteOfferParameterMapsByOffer(Offer offer, Session session) {
		StringBuffer hql = new StringBuffer("DELETE FROM OfferParameterMap a");
		hql.append(" WHERE a.offerId = :offerId");
		try {

			Query<?> query = session.createQuery(hql.toString());
			query.setParameter("offerId", offer.getOfferId());
			query.executeUpdate();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}
	}

	public Offer findOfferCompiled(OfferDump offerGroup) {
		StringBuffer hql = new StringBuffer("SELECT a FROM Offer a");
		hql.append(" WHERE a.offerName = :offerName");
		hql.append(" AND a.offerExternalId = :offerExternalId");

		Session session = HibernateUtil.getOpenSession();
		try {
			Query<Offer> query = session.createQuery(hql.toString(), Offer.class);
			query.setParameter("offerName", offerGroup.getOfferName());
			query.setParameter("offerExternalId", offerGroup.getOfferExternalId());
			return query.getResultList().get(0);
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public List<Offer> findMoreVersionByOffer(Offer offer) {
		StringBuffer hql = new StringBuffer("SELECT a FROM Offer a");
		hql.append(" WHERE a.offerName = :offerName");
		hql.append(" AND a.offerExternalId = :offerExternalId");
		hql.append(" AND a.domainId = :domainId");
		hql.append(" ORDER BY a.versionInfo DESC");

		Session session = HibernateUtil.getOpenSession();
		try {
			Query<Offer> query = session.createQuery(hql.toString(), Offer.class);
			query.setParameter("offerName", offer.getOfferName());
			query.setParameter("offerExternalId", offer.getOfferExternalId());
			query.setParameter("domainId", getDomainId());
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public Long countMoreVersionByOffer(Offer offer) {
		StringBuffer hql = new StringBuffer("SELECT COUNT(a) FROM Offer a");
		hql.append(" WHERE a.offerName = :offerName");
		hql.append(" AND a.offerExternalId = :offerExternalId");
		hql.append(" AND a.domainId = :domainId");
		hql.append(" ORDER BY a.versionInfo DESC");

		Session session = HibernateUtil.getOpenSession();
		try {
			Query<Long> query = session.createQuery(hql.toString(), Long.class);
			query.setParameter("offerName", offer.getOfferName());
			query.setParameter("offerExternalId", offer.getOfferExternalId());
			query.setParameter("domainId", getDomainId());
			return query.getSingleResult();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public List<Offer> findMoreVersionByOffer(Offer offer, Long offerType) {
		StringBuffer hql = new StringBuffer("SELECT a FROM Offer a");
		hql.append(" WHERE a.offerName = :offerName");
		hql.append(" AND a.offerExternalId = :offerExternalId");
		hql.append(" AND a.offerType = :offerType");
		hql.append(" AND a.domainId = :domainId");

		Session session = HibernateUtil.getOpenSession();
		try {
			Query<Offer> query = session.createQuery(hql.toString(), Offer.class);
			query.setParameter("offerName", offer.getOfferName());
			query.setParameter("offerExternalId", offer.getOfferExternalId());
			query.setParameter("offerType", offerType);
			query.setParameter("domainId", getDomainId());
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public int countMoreVersionByOffer(Offer offer, Long offerType) {
		StringBuffer hql = new StringBuffer("SELECT COUNT(a) FROM Offer a");
		hql.append(" WHERE a.offerName = :offerName");
		hql.append(" AND a.offerExternalId = :offerExternalId");
		hql.append(" AND a.offerType = :offerType");
		hql.append(" AND a.domainId = :domainId");

		Session session = HibernateUtil.getOpenSession();
		try {
			Query<Long> query = session.createQuery(hql.toString(), Long.class);
			query.setParameter("offerName", offer.getOfferName());
			query.setParameter("offerExternalId", offer.getOfferExternalId());
			query.setParameter("offerType", offerType);
			query.setParameter("domainId", getDomainId());
			return query.getSingleResult().intValue();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public List<OfferRecompiledModel> findAllCompiledOfferOfTemp(Offer offer, long offerType) {
		StringBuffer hql = new StringBuffer("SELECT new com.viettel.ocs.model.OfferRecompiledModel(a) FROM Offer a");
		hql.append(" WHERE a.offerTemplateId = :offerTemplateId");
		hql.append(" AND a.offerType = :offerType");
		hql.append(" AND a.domainId = :domainId");
		hql.append(" ORDER BY a.versionInfo DESC");

		Session session = HibernateUtil.getOpenSession();
		try {
			Query<OfferRecompiledModel> query = session.createQuery(hql.toString(), OfferRecompiledModel.class);
			query.setParameter("offerTemplateId", offer.getOfferId());
			query.setParameter("offerType", offerType);
			query.setParameter("domainId", getDomainId());
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public int countCompiledOfferOfTemp(Offer offer) {
		StringBuffer hql = new StringBuffer("SELECT COUNT(a) FROM Offer a");
		hql.append(" WHERE a.offerTemplateId = :offerTemplateId");
		hql.append(" AND a.offerType = :offerType");
		hql.append(" AND a.domainId = :domainId");

		Session session = HibernateUtil.getOpenSession();
		try {
			Query<Long> query = session.createQuery(hql.toString(), Long.class);
			query.setParameter("offerTemplateId", offer.getOfferId());
			query.setParameter("offerType", OfferType.COMPILED);
			query.setParameter("domainId", getDomainId());
			return query.getSingleResult().intValue();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public List<Offer> findAllCompiledOfferOfTemp(Offer offer) {
		return findAllCompiledOfferOfTemp(offer.getOfferId());
	}

	public List<Offer> findAllCompiledOfferOfTemp(long offerId) {
		StringBuffer hql = new StringBuffer("SELECT a FROM Offer a");
		hql.append(" WHERE a.offerTemplateId = :offerTemplateId");
		hql.append(" AND a.offerType = :offerType");
		hql.append(" AND a.domainId = :domainId");
		hql.append(" ORDER BY a.versionInfo DESC");

		Session session = HibernateUtil.getOpenSession();
		try {
			Query<Offer> query = session.createQuery(hql.toString(), Offer.class);
			query.setParameter("offerTemplateId", offerId);
			query.setParameter("domainId", getDomainId());
			query.setParameter("offerType", OfferType.COMPILED);
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public boolean doDeployWithoutCaseTwo(Offer selectedOfferForDeploy, List<Offer> offersForDeploy) {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			selectedOfferForDeploy.setState(ContantsUtil.OfferState.ACTIVE);
			session.update(selectedOfferForDeploy);

			for (int i = 0; i < offersForDeploy.size(); i++) {
				Offer offer = offersForDeploy.get(i);
				if (offer.getOfferId() != selectedOfferForDeploy.getOfferId()) {
					offer.setState(ContantsUtil.OfferState.IN_ACTIVE);
					session.update(offer);
				} else {
					offer.setState(ContantsUtil.OfferState.ACTIVE);
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

	@SuppressWarnings("rawtypes")
	private void switchRecord(long idA, long idB, Session session) {

		String sql = "UPDATE offer SET offer_id = :offer_id1 WHERE offer_id = :offer_id2";
		try {

			Query query1 = session.createNativeQuery(sql);
			Query query2 = session.createNativeQuery(sql);
			Query query3 = session.createNativeQuery(sql);

			query1.setParameter("offer_id1", -1);
			query1.setParameter("offer_id2", idA);

			query2.setParameter("offer_id1", idA);
			query2.setParameter("offer_id2", idB);

			query3.setParameter("offer_id1", idB);
			query3.setParameter("offer_id2", -1);

			query1.executeUpdate();
			query2.executeUpdate();
			query3.executeUpdate();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}
	}

	public boolean doDeployWithoutCaseThree(Offer selectedOfferForDeploy, List<Offer> offersForDeploy) {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			selectedOfferForDeploy.setState(ContantsUtil.OfferState.ACTIVE);
			session.update(selectedOfferForDeploy);

			List<OfferActionMap> selectedActionMaps = this.findOfferActionMapByOffer(selectedOfferForDeploy, session);
			List<OfferParameterMap> selectedParameterMaps = this
					.findOfferParameterMapsByOfferCompiled(selectedOfferForDeploy, session);
			List<OfferpkgOfferMap> selectedOfferPkgMaps = this.findOfferpkgOfferMapByOffer(selectedOfferForDeploy,
					session);
			List<RedirectAddress> selectedRedirectAddresses = this.findRedirectAddressByOffer(selectedOfferForDeploy,
					session);

			// Filter version active and sort by version. Only java 8 :)
			List<Offer> offerActive = offersForDeploy.stream()
					.filter(item -> item.getState().equals(ContantsUtil.OfferState.ACTIVE))
					.collect(Collectors.toList());
			offerActive.sort((Offer o1, Offer o2) -> Integer.valueOf(o2.getVersionInfo())
					- Integer.valueOf(o1.getVersionInfo()));
			Offer anotherMaxVersion = offerActive.get(0);

			offerActive.forEach(offer -> {
				offer.setState(OfferState.IN_ACTIVE);
				session.update(offer);
			});

			List<OfferActionMap> maxVersionActionMaps = this.findOfferActionMapByOffer(anotherMaxVersion, session);
			List<OfferParameterMap> maxVersionParameterMaps = this
					.findOfferParameterMapsByOfferCompiled(anotherMaxVersion, session);
			List<OfferpkgOfferMap> maxVersionOfferPkgMaps = this.findOfferpkgOfferMapByOffer(anotherMaxVersion,
					session);
			List<RedirectAddress> maxVersionRedirectAddresses = this.findRedirectAddressByOffer(anotherMaxVersion,
					session);

			// Step 1: Switch data between two object
			this.switchRecord(selectedOfferForDeploy.getOfferId(), anotherMaxVersion.getOfferId(), session);

			// Step 2: Switch map
			// OfferActionMap
			for (OfferActionMap offerActionMap : selectedActionMaps) {
				offerActionMap.setOfferId(anotherMaxVersion.getOfferId());
				session.update(offerActionMap);
			}
			for (OfferActionMap offerActionMap : maxVersionActionMaps) {
				offerActionMap.setOfferId(selectedOfferForDeploy.getOfferId());
				session.update(offerActionMap);
			}

			// OfferParameterMap
			for (OfferParameterMap parameterMap : selectedParameterMaps) {
				parameterMap.setOfferId(anotherMaxVersion.getOfferId());
				session.update(parameterMap);
			}
			for (OfferParameterMap parameterMap : maxVersionParameterMaps) {
				parameterMap.setOfferId(selectedOfferForDeploy.getOfferId());
				session.update(parameterMap);
			}

			// OfferpkgOfferMap
			for (OfferpkgOfferMap offerpkgOfferMap : selectedOfferPkgMaps) {
				offerpkgOfferMap.setOfferId(anotherMaxVersion.getOfferId());
				session.update(offerpkgOfferMap);
			}
			for (OfferpkgOfferMap offerpkgOfferMap : maxVersionOfferPkgMaps) {
				offerpkgOfferMap.setOfferId(selectedOfferForDeploy.getOfferId());
				session.update(offerpkgOfferMap);
			}

			// RedirectAddress
			for (RedirectAddress redirectAddress : selectedRedirectAddresses) {
				redirectAddress.setOfferId(anotherMaxVersion.getOfferId());
				session.update(redirectAddress);
			}
			for (RedirectAddress redirectAddress : maxVersionRedirectAddresses) {
				redirectAddress.setOfferId(selectedOfferForDeploy.getOfferId());
				session.update(redirectAddress);
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

	private List<OfferpkgOfferMap> findOfferpkgOfferMapByOffer(Offer offer, Session session) {
		StringBuffer hql = new StringBuffer("SELECT a FROM OfferpkgOfferMap a");
		hql.append(" JOIN OfferPackage b ON b.offerPkgId = a.offerPackageId");
		hql.append(" JOIN Offer c ON c.offerId = a.offerId");
		hql.append(" WHERE a.domainId =:domainId");
		hql.append(" AND c.offerId =:offerId");
		try {

			Query<OfferpkgOfferMap> query = session.createQuery(hql.toString(), OfferpkgOfferMap.class);
			query.setParameter("domainId", getDomainId());
			query.setParameter("offerId", offer.getOfferId());
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}
	}

	public boolean convertToNormalOffer(Offer offer) {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			this.deleteOfferParameterMapsByOffer(offer, session);
			offer.setOfferType(OfferType.NORMAL);
			session.update(offer);

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

	public boolean changeNameAndExtIdVersion(List<Offer> offerVersions, OfferDump offerGroup) {
		StringBuffer hql = new StringBuffer(
				"UPDATE Offer a SET a.offerName = :offerName, a.offerExternalId = :offerExternalId, a.categoryId = :categoryId WHERE a.offerId IN (:offerIds)");
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			List<Long> ids = new ArrayList<Long>();
			ids.add(-1L);
			for (Offer offer : offerVersions) {
				ids.add(offer.getOfferId());
			}

			Query<?> query = session.createQuery(hql.toString());
			query.setParameter("offerName", offerGroup.getOfferName().trim());
			query.setParameter("offerExternalId", offerGroup.getOfferExternalId().trim());
			query.setParameter("categoryId", offerGroup.getCategoryId());
			query.setParameterList("offerIds", ids);
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

	public boolean deleteAndConvertToNorm(Offer offer, Long categoryId, List<Offer> offersCompiled) {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			this.deleteOfferParameterMapsByOffer(offer, session);

			// this.deleteTreeOffer(offer, session);

			this.deleteNormOfferpkgOfferMaps(offer, session);

			if (offer.getOfferType().equals(OfferType.TEMPLATE)) {
				for (Offer offerCompiled : offersCompiled) {
					this.deleteOfferParameterMapsByOffer(offerCompiled, session);
					offerCompiled.setCategoryId(categoryId);
					offerCompiled.setOfferType(OfferType.NORMAL);
					session.update(offerCompiled);
				}
			}
			session.delete(offer);
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

	// nampv ve bieu do
	public int countAllOffer() {
		String hql = "SELECT COUNT(a) FROM Offer a WHERE a.domainId =:domainId";
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

	public int countOfferByType(Long offerType) {
		String hql = "SELECT COUNT(a) FROM Offer a WHERE a.offerType =:offerType AND a.domainId =:domainId";
		Session session = HibernateUtil.getOpenSession();
		try {
			Query<Long> query = session.createQuery(hql, Long.class);
			query.setParameter("domainId", getDomainId());
			query.setParameter("offerType", offerType);
			return query.getSingleResult().intValue();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public int countOfferByState(Long state) {
		String hql = "SELECT COUNT(a) FROM Offer a";
		hql += " WHERE a.state =:state and a.domainId =:domainId";

		Session session = HibernateUtil.getOpenSession();
		try {
			Query<Long> query = session.createQuery(hql, Long.class);
			query.setParameter("state", state);
			query.setParameter("domainId", getDomainId());
			return query.getSingleResult().intValue();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public Offer findJustOneOfferOfferDump(OfferDump dump) {
		String hql = "SELECT a FROM Offer a"
				+ " WHERE a.offerName = :offerName AND a.offerExternalId = :offerExternalId AND a.domainId =:domainId";
		Session session = HibernateUtil.getOpenSession();
		try {
			Query<Offer> query = session.createQuery(hql, Offer.class);
			query.setParameter("offerName", dump.getOfferName());
			query.setParameter("offerExternalId", dump.getOfferExternalId());
			query.setParameter("domainId", getDomainId());
			query.setMaxResults(1);
			List<Offer> offers = query.getResultList();
			if (offers.size() > 0) {
				return offers.get(0);
			} else {
				return null;
			}
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	@SuppressWarnings("unchecked")
	public CloneIgnoreListModel findTreeUsingParam(Offer offer, Session session) {
		CloneIgnoreListModel usingParam = new CloneIgnoreListModel();
		StringBuffer hql = new StringBuffer();
		hql.append(
				"SELECT d.actionId, e.priceComponentId, f.blockId, g.dynamicReserveId, h.sortPriceComponentId, g.rateTableId, h.rateTableId, f.rateTableId FROM Offer a");
		hql.append(" JOIN OfferActionMap b ON b.offerId = a.offerId");
		hql.append(" JOIN Action c ON c.actionId = b.actionId");
		hql.append(" LEFT JOIN ActionPriceComponentMap d ON d.actionId = c.actionId");
		hql.append(" LEFT JOIN PriceComponentBlockMap e ON e.priceComponentId = d.priceComponentId");
		hql.append(" LEFT JOIN BlockRateTableMap f ON f.blockId = e.blockId");

		hql.append(" LEFT JOIN DynamicReserveRateTableMap g ON g.dynamicReserveId = c.dynamicReserveId");
		hql.append(" LEFT JOIN SortPriceRateTableMap h ON h.sortPriceComponentId = c.sortPriceComponentId");
		hql.append(" WHERE a.offerId = :offerId");
		try {

			Query<?> query = session.createQuery(hql.toString());
			query.setParameter("offerId", offer.getOfferId());
			List<Object> objects = (List<Object>) query.getResultList();
			for (Object object : objects) {
				Object[] ids = (Object[]) object;
				Long id_0 = ids[0] != null ? (Long) ids[0] : null;
				Long id_1 = ids[1] != null ? (Long) ids[1] : null;
				Long id_2 = ids[2] != null ? (Long) ids[2] : null;
				Long id_3 = ids[3] != null ? (Long) ids[3] : null;
				Long id_4 = ids[4] != null ? (Long) ids[4] : null;
				Long id_5 = ids[5] != null ? (Long) ids[5] : null;
				Long id_6 = ids[6] != null ? (Long) ids[6] : null;
				Long id_7 = ids[7] != null ? (Long) ids[7] : null;

				if (id_5 != null && checkUsingParamRate(id_5, usingParam)) {
					if (id_0 != null && usingParam.getActions().get(id_0) == null) {
						usingParam.getActions().put(id_0, id_0);
					}

					if (id_3 != null && usingParam.getDynamicReserves().get(id_3) == null) {
						usingParam.getDynamicReserves().put(id_3, id_3);
					}
				}

				if (id_6 != null && checkUsingParamRate(id_6, usingParam)) {

					if (id_0 != null && usingParam.getActions().get(id_0) == null) {
						usingParam.getActions().put(id_0, id_0);
					}

					if (id_4 != null && usingParam.getSortPCs().get(id_4) == null) {
						usingParam.getSortPCs().put(id_4, id_4);
					}
				}

				if (id_7 != null && checkUsingParamRate(id_7, usingParam)) {

					if (id_0 != null && usingParam.getActions().get(id_0) == null) {
						usingParam.getActions().put(id_0, id_0);
					}

					if (id_1 != null && usingParam.getPriceComponents().get(id_1) == null) {
						usingParam.getPriceComponents().put(id_1, id_1);
					}

					if (id_2 != null && usingParam.getBlocks().get(id_2) == null) {
						usingParam.getBlocks().put(id_2, id_2);
					}
				}
			}
			return usingParam;
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}		
	}

	private boolean checkUsingParamRate(Long rateTableId, CloneIgnoreListModel usingParam) {
		boolean checkUsing = false;
		RateTableDAO rateTableDAO = new RateTableDAO();
		RateTable rateTable = rateTableDAO.get(rateTableId);
		List<Formula> formulas = rateTableDAO.findFormulaByRateTable(rateTable);

		FormulaDAO formulaDAO = new FormulaDAO();
		if (rateTable.getDefaultFormulaId() != null && rateTable.getDefaultFormulaId() != 0L) {
			Formula defaultFormula = formulaDAO.get(rateTable.getDefaultFormulaId());
			if (defaultFormula != null) {
				formulas.add(defaultFormula);
			}
		}

		if (rateTable.getFormulaId() != null && rateTable.getFormulaId() != 0L) {
			Formula stateFormula = formulaDAO.get(rateTable.getFormulaId());
			if (stateFormula != null) {
				formulas.add(stateFormula);
			}
		}

		// Get formula's parameter
		ParameterDAO parameterDAO = new ParameterDAO();
		for (Formula formula : formulas) {
			if (parameterDAO.checkUsingParam4Formula(formula)) {
				if (usingParam.getRateTables().get(rateTableId) == null) {
					usingParam.getRateTables().put(rateTableId, rateTableId);
				}

				checkUsing = true;
				break;
			}
		}

		if (rateTable.getDecisionTableId() != null) {
			DecisionTableDAO decisionTableDAO = new DecisionTableDAO();
			List<Normalizer> normalizers = decisionTableDAO.findNormalizers(rateTable.getDecisionTableId());
			List<Long> normalizerIds = new ArrayList<Long>();

			for (Normalizer normalizer : normalizers) {
				NormalizerDAO normalizerDAO = new NormalizerDAO();
				normalizerIds.clear();
				normalizerIds.add(normalizer.getNormalizerId());

				List<NormParam> normParams = normalizerDAO.getNormParamUsingParameterByNormalizer(normalizerIds);
				for (NormParam normParam : normParams) {
					if (normParam.getConfigInput()
							.contains(com.viettel.ocs.constant.Normalizer.UsingNormParam.IS_USING_PARAMETER)) {
						String[] items = normParam.getConfigInput().split(";");
						for (String string : items) {
							if (string.contains(com.viettel.ocs.constant.Normalizer.UsingNormParam.PARAMETER_ID)) {
								Parameter parameter = parameterDAO.get(Long.valueOf(string
										.replace(com.viettel.ocs.constant.Normalizer.UsingNormParam.PARAMETER_ID, "")));
								if (parameter != null && parameter.getForTemplate()) {

									if (usingParam.getRateTables().get(rateTableId) == null) {
										usingParam.getRateTables().put(rateTableId, rateTableId);
									}

									if (usingParam.getDecisionTables().get(rateTable.getDecisionTableId()) == null) {
										usingParam.getDecisionTables().put(rateTable.getDecisionTableId(),
												rateTable.getDecisionTableId());
									}

									if (usingParam.getNormalizers().get(normalizer.getNormalizerId()) == null) {
										usingParam.getNormalizers().put(normalizer.getNormalizerId(),
												normalizer.getNormalizerId());
									}

									checkUsing = true;
									break;
								}
							}
						}
						continue;
					}

					if (normParam.getConfigInput()
							.contains(com.viettel.ocs.constant.Normalizer.UsingNormParam.END_IS_PARAMETER)) {
						String[] items = normParam.getConfigInput().split(";");
						for (String string : items) {
							if (string.contains("end:")) {
								Parameter parameter = parameterDAO.get(Long.valueOf(string.replace("end:", "")));
								if (parameter != null && parameter.getForTemplate()) {
									if (usingParam.getRateTables().get(rateTableId) == null) {
										usingParam.getRateTables().put(rateTableId, rateTableId);
									}

									if (usingParam.getDecisionTables().get(rateTable.getDecisionTableId()) == null) {
										usingParam.getDecisionTables().put(rateTable.getDecisionTableId(),
												rateTable.getDecisionTableId());
									}

									if (usingParam.getNormalizers().get(normalizer.getNormalizerId()) == null) {
										usingParam.getNormalizers().put(normalizer.getNormalizerId(),
												normalizer.getNormalizerId());
									}

									checkUsing = true;
									break;
								}
							}
						}
					}

					if (normParam.getConfigInput()
							.contains(com.viettel.ocs.constant.Normalizer.UsingNormParam.START_IS_PARAMETER)) {
						String[] items = normParam.getConfigInput().split(";");
						for (String string : items) {
							if (string.contains("start:")) {
								Parameter parameter = parameterDAO.get(Long.valueOf(string.replace("start:", "")));
								if (parameter != null && parameter.getForTemplate()) {
									if (usingParam.getRateTables().get(rateTableId) == null) {
										usingParam.getRateTables().put(rateTableId, rateTableId);
									}

									if (usingParam.getDecisionTables().get(rateTable.getDecisionTableId()) == null) {
										usingParam.getDecisionTables().put(rateTable.getDecisionTableId(),
												rateTable.getDecisionTableId());
									}

									if (usingParam.getNormalizers().get(normalizer.getNormalizerId()) == null) {
										usingParam.getNormalizers().put(normalizer.getNormalizerId(),
												normalizer.getNormalizerId());
									}

									checkUsing = true;
									break;
								}
							}
						}
					}

				}
			}
		}
		return checkUsing;
	}
		
	public boolean checkOfferPackageInOF(Long offerPackageId) {
		List<Offer> lst = null;
		String[] cols = { "offerPackageId", "domainId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { offerPackageId, getDomainId() };
		lst = findByConditions(cols, operators, values, "");
		if (lst != null && lst.size() > 0) {
			return true;
		}
		return false;
	}

}

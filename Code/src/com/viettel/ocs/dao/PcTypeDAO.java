package com.viettel.ocs.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.Block;
import com.viettel.ocs.entity.DynamicReserveRateTableMap;
import com.viettel.ocs.entity.PcType;
import com.viettel.ocs.entity.PcTypeBlockMap;
import com.viettel.ocs.entity.PriceComponent;
import com.viettel.ocs.entity.PriceComponentBlockMap;

@SuppressWarnings("serial")
public class PcTypeDAO extends BaseDAO<PcType> implements Serializable {
	@Override
	protected Class<PcType> getEntityClass() {
		return PcType.class;
	}

	public List<PcType> findPcTypeByCategoryId(List<Long> lstCatID) {
		StringBuffer hql = new StringBuffer();
		hql.append("SELECT a FROM PcType a");
		hql.append(" WHERE a.categoryId IN (:categoryId) order by a.posIdx ASC");
		List<Long> lstCatIDParam = new ArrayList<Long>();
		lstCatIDParam.addAll(lstCatID);
		if (lstCatIDParam.size() == 0)
			lstCatIDParam.add(-1L);
		Session session = HibernateUtil.getOpenSession();
		try {
			Query<PcType> query = session.createQuery(hql.toString());
			query.setParameterList("categoryId", lstCatIDParam);
			return query.getResultList();

		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public List<PcType> findPCTByCategoryId(Long categoryId) {

		List<PcType> lst = null;
		String[] cols = { "categoryId", "domainId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { categoryId, getDomainId() };
		String oder = "posIdx ASC";
		lst = findByConditions(cols, operators, values, oder);
		return lst;

	}

	public boolean savePcType(PcType pcType, List<PcTypeBlockMap> lstMap) {
		List<Long> mapIds = new ArrayList<Long>();
		for (PcTypeBlockMap map : lstMap) {
			mapIds.add(map.getPcTypeBlockMapId());
		}

		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {

			// SAVE PC TYPE
			pcType.setDomainId(getDomainId());
			generateIndexByCat(pcType, "posIdx", "pcTypeId", session);
			session.saveOrUpdate(pcType);

			// DELETE PC TYPE BLOCK MAP
			this.deletePcTypeBlockMapOfPcTypeId(mapIds, pcType, session);

			// SAVE OR UPDATE BLOCK MAP
			for (int i = 0; i < lstMap.size(); i++) {
				
				PcTypeBlockMap pcTypeBlockMap = lstMap.get(i);
				pcTypeBlockMap.setPcTypeId(pcType.getPcTypeId());
				pcTypeBlockMap.setBlockIndex(Long.valueOf(i));
				session.saveOrUpdate(pcTypeBlockMap);
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
	
	public List<PcTypeBlockMap> findPcTypeBlockMap(Long pcTypeId) {
		List<PcTypeBlockMap> lst = null;
		String hql = "SELECT map FROM PcTypeBlockMap map ";
		hql += " WHERE map.pcTypeId = :pcTypeId ORDER BY map.blockIndex ASC";

		Session session = HibernateUtil.getOpenSession();
		try {
			Query<PcTypeBlockMap> query = session.createQuery(hql);
			query.setParameter("pcTypeId", pcTypeId);
			lst = query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}

		return lst;
	}

	public PcTypeBlockMap findPcTypeBlockMap(Long pcTypeId, Long blockId) {
		StringBuffer hql = new StringBuffer("SELECT a FROM PcTypeBlockMap a");
		hql.append(" WHERE a.blockId = :blockId");
		hql.append(" AND a.pcTypeId = :pcTypeId");

		Session session = HibernateUtil.getOpenSession();
		try {

			Query<PcTypeBlockMap> query = session.createQuery(hql.toString(), PcTypeBlockMap.class);
			query.setParameter("pcTypeId", pcTypeId);
			query.setParameter("blockId", blockId);

			List<PcTypeBlockMap> pcTypeBlockMaps = query.getResultList();

			if (pcTypeBlockMaps != null && pcTypeBlockMaps.size() > 0) {
				return pcTypeBlockMaps.get(0);
			}
			
			return null;
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
		
	}

	public List<PcType> findPCTypeByListBlock(List<Long> lstBlockId) {
		if (lstBlockId.size() == 0)
			lstBlockId.add(-1L);
		List<PcType> lst = null;
		String hql = "SELECT DISTINCT pc FROM PcType pc JOIN PcTypeBlockMap map ON map.pcTypeId = pc.pcTypeId ";
		hql += " WHERE map.blockId IN (:blockId) ";

		Session session = HibernateUtil.getOpenSession();
		try {
			Query query = session.createQuery(hql);
			query.setParameter("blockId", lstBlockId);
			lst = query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
		return lst;
	}
	
	public List<PcType> findPCTypeByListPC(List<Long> lstPcId) {
		if (lstPcId.size() == 0)
			lstPcId.add(-1L);
		List<PcType> lst = null;
		String hql = "SELECT DISTINCT pc FROM PcType pc JOIN PriceComponent map ON map.pcType = pc.pcTypeId ";
		hql += " WHERE map.priceComponentId IN (:priceComponentId) ";

		Session session = HibernateUtil.getOpenSession();
		try {
			Query query = session.createQuery(hql);
			query.setParameter("priceComponentId", lstPcId);
			lst = query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
		return lst;
	}
	
	public List<PcType> findPCTypeByListSPC(List<Long> lstSPCId) {
		if (lstSPCId.size() == 0)
			lstSPCId.add(-1L);
		List<PcType> lst = null;
		String hql = "SELECT DISTINCT pc FROM PcType pc ";
		hql += " WHERE pc.filterId IN (:filterId) ";

		Session session = HibernateUtil.getOpenSession();
		try {
			Query query = session.createQuery(hql);
			query.setParameter("filterId", lstSPCId);
			lst = query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
		return lst;
	}
	
	private void deletePcTypeBlockMapOfPcTypeId(List<Long> mapIds, PcType pcType, Session session) {
		if(mapIds.isEmpty())
			return;
		try {

			StringBuffer hql = new StringBuffer("DELETE FROM PcTypeBlockMap a");
			hql.append(" WHERE a.pcTypeBlockMapId NOT IN (:blockIds)");
			hql.append(" AND a.pcTypeId = :pcTypeId");

			Query<PcTypeBlockMap> query = session.createQuery(hql.toString());
			query.setParameterList("blockIds", mapIds);
			query.setParameter("pcTypeId", pcType.getPcTypeId());
			query.executeUpdate();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}
	}

	public boolean moveUpDownBlock(PcType pcType, Block block, boolean moveUp) {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			
			List<PcTypeBlockMap> lstMap = this.findPcTypeBlockMap(pcType.getPcTypeId());
			int currentIdx = -1;
			for(PcTypeBlockMap map : lstMap) {
				
				if(map.getBlockId() == block.getBlockId()) {
					currentIdx = lstMap.indexOf(map);
					break;
				}
			}
			if(currentIdx < 0)
				return false;
			
			PcTypeBlockMap upObj;
			PcTypeBlockMap downObj;
			if(moveUp) {				
				if(currentIdx > 0) {
					
					upObj = lstMap.get(currentIdx);
					downObj = lstMap.get(currentIdx - 1);
					
					Long upIdx = upObj.getBlockIndex();
					upObj.setBlockIndex(downObj.getBlockIndex());
					downObj.setBlockIndex(upIdx);
					session.update(upObj);
					session.update(downObj);
				}
			} else {
				if(currentIdx < lstMap.size() - 1) {
					
					upObj = lstMap.get(currentIdx + 1);
					downObj = lstMap.get(currentIdx);
					
					Long upIdx = upObj.getBlockIndex();
					upObj.setBlockIndex(downObj.getBlockIndex());
					downObj.setBlockIndex(upIdx);
					session.update(upObj);
					session.update(downObj);
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
	
	public boolean checkName(PcType pcType) {
		String hql = "SELECT COUNT(*) FROM PcType a WHERE a.pcTypeName LIKE :pcTypeName AND a.pcTypeId != :pcTypeId";
		hql += " AND a.domainId=:domainId";
		Session session = HibernateUtil.getOpenSession();
		try {

			Query<Long> query = session.createQuery(hql);
			query.setParameter("pcTypeName", pcType.getPcTypeName());
			query.setParameter("pcTypeId", pcType.getPcTypeId());
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
	
	public int countPCType() {
		String hql = "SELECT COUNT(a) FROM PcType a WHERE  a.domainId =:domainId";
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
}

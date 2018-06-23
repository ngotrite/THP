package com.viettel.ocs.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.constant.CategoryType;
import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.Action;
import com.viettel.ocs.entity.Block;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.DynamicReserve;
import com.viettel.ocs.entity.Parameter;
import com.viettel.ocs.entity.PriceComponent;
import com.viettel.ocs.entity.PriceComponentBlockMap;
import com.viettel.ocs.entity.SortPriceComponent;
import com.viettel.ocs.model.CloneIgnoreListModel;

public class PriceComponentDAO extends BaseDAO<PriceComponent> implements Serializable {

	public int countPCByAction(Long actionId) {

		List lst = null;
		String hql = "SELECT COUNT(pc.priceComponentId) FROM PriceComponent pc JOIN ActionPriceComponentMap map ON map.priceComponentId = pc.priceComponentId ";
		hql += " WHERE map.actionId = :actionId AND map.domainId = :domainId ";

		Session session = HibernateUtil.getOpenSession();
		try {

			Query query = session.createQuery(hql);
			query.setParameter("actionId", actionId);
			query.setParameter("domainId", getDomainId());
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

	public List<PriceComponent> findPCByAction(Long actionId) {

		List<PriceComponent> lst = null;
		String hql = "SELECT pc FROM PriceComponent pc JOIN ActionPriceComponentMap map ON map.priceComponentId = pc.priceComponentId ";
		hql += " WHERE map.actionId = :actionId AND  map.domainId = :domainId";
		hql += " ORDER BY map.pcIndex";

		Session session = HibernateUtil.getOpenSession();
		try {

			Query query = session.createQuery(hql);
			query.setParameter("actionId", actionId);
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

	/**
	 * categoryId domainId
	 */
	public List<PriceComponent> findPriceComponentByCategoryId(List<Long> lstCatID) {

		String[] col = { "categoryId", "domainId" };
		Operator[] ope = { Operator.IN, Operator.EQ };
		List<Long> lstCatIDParam = new ArrayList<Long>();
		lstCatIDParam.addAll(lstCatID);
		if (lstCatIDParam.size() == 0)
			lstCatIDParam.add(-1L);
		Object[] val = { lstCatIDParam, getDomainId() };
		String oder = "index ASC";
		return this.findByConditions(col, ope, val, oder);
	}

	/**
	 * load List PriceComponent by categoryId and domain
	 */
	public List<PriceComponent> loadPriceComponentByCategpryId(Long categoryId) {
		String[] col = { "categoryId", "domainId" };
		Operator[] ope = { Operator.EQ, Operator.EQ };
		Object[] val = { categoryId, getDomainId() };
		String oder = " index ASC";
		return this.findByConditions(col, ope, val, oder);
	}

	/**
	 * load List Category by categoryType
	 */
	public List<Category> loadListCategory() {
		CategoryDAO categoryDAO = new CategoryDAO();
		return categoryDAO.loadListCategoryByType(CategoryType.OFF_PC_PRICE_COMPONENT);
	}

	public void savePCWithBlock(PriceComponent pc, List<Block> lstBlock) {

		Session session = HibernateUtil.getOpenSession();
		try {

			session.beginTransaction();
			if (pc.getPriceComponentId() == 0L) {
				pc.setDomainId(getDomainId());
			}
			generateIndexByCat(pc, "priceComponentId", session);
			
			session.saveOrUpdate(pc);
			String hqlDelMap = "DELETE FROM PriceComponentBlockMap WHERE priceComponentId = ? AND domainId = :domainId ";
			Query queryDel = session.createQuery(hqlDelMap);
			queryDel.setParameter(0, pc.getPriceComponentId());
			queryDel.setParameter("domainId", getDomainId());
			queryDel.executeUpdate();

			int blockIdx = 0;
			for (Block block : lstBlock) {

				PriceComponentBlockMap pcBlockMap = new PriceComponentBlockMap();
				pcBlockMap.setBlockId(block.getBlockId());
				pcBlockMap.setPriceComponentId(pc.getPriceComponentId());
				pcBlockMap.setBlockIndex((long) blockIdx);
				pcBlockMap.setDomainId(super.getDomainId());
				session.save(pcBlockMap);

				blockIdx++;
			}

			session.getTransaction().commit();
		} catch (Exception e) {
			session.getTransaction().rollback();
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	@Override
	protected Class<PriceComponent> getEntityClass() {
		return PriceComponent.class;
	}

	public List<PriceComponent> loadPriceComponentByPCName(Long pcID, String pcName) {
		String[] col = { "priceComponentId", "priceComponentName", "domainId" };
		Operator[] ope = { Operator.NOTEQ, Operator.EQ, Operator.EQ };
		Object[] val = { pcID, pcName, getDomainId() };
		String oder = "priceComponentName";
		return this.findByConditions(col, ope, val, oder);
	}

	public boolean checkName(PriceComponent priceComponent) {
		List<PriceComponent> lst = null;
		String[] cols = { "priceComponentName", "domainId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { priceComponent.getPriceComponentName(), getDomainId() };
		lst = findByConditions(cols, operators, values, "");
		if (lst.size() > 0) {
			if (priceComponent.getPriceComponentId() > 0
					&& lst.get(0).getPriceComponentId() == priceComponent.getPriceComponentId()) {
				return false;
			} else {
				return true;
			}
		}
		return false;

	}

	/**
	 * Clone Price Component
	 * 
	 * @author THANHND
	 * @param priceComponent
	 * @param cloneBlock
	 * @param cloneRateTable
	 * @param cloneDT
	 * @param replaceParam
	 * @param parameters
	 * @param ignoreList
	 * @param session
	 * @throws CloneNotSupportedException
	 */
	public PriceComponent clonePC(PriceComponent priceComponent, boolean cloneBlock, Boolean cloneRateTable,
			boolean cloneDT, boolean cloneNormalizer, String suffix, boolean replaceParam, List<Parameter> parameters,
			CloneIgnoreListModel ignoreList, boolean isClone, CloneIgnoreListModel usingParams, Session session)
			throws CloneNotSupportedException {
		try {

			if (!isClone && usingParams.isIgnore(priceComponent) == null) {
				return priceComponent;
			}

			PriceComponent priceComponentToClone = priceComponent.clone();
			priceComponentToClone.setGenerated(!isClone);

			// Find blocks of pc
			List<Block> blocks = this.findBlockByPC(priceComponentToClone, session);

			priceComponentToClone.setPriceComponentId(0L);
			priceComponentToClone.setPriceComponentName(generateNameObject("priceComponentName",
					priceComponentToClone.getPriceComponentName() + suffix, 0, session));
			priceComponentToClone.setIndex(this.getMaxIndex(session) + 1);
			session.save(priceComponentToClone);

			// Level Block - 4
			for (Block block : blocks) {
				// Prepare map
				PriceComponentBlockMap priceComponentBlockMap = new PriceComponentBlockMap(
						priceComponentToClone.getPriceComponentId(), block.getBlockId(),
						Long.valueOf(blocks.indexOf(block)));
				priceComponentBlockMap.setDomainId(getDomainId());

				if (cloneBlock) {
					Long blockClonedId = ignoreList.isIgnore(block);
					if (blockClonedId == null) {
						BlockDAO blockDAO = new BlockDAO();
						blockClonedId = blockDAO.cloneBlock(block, cloneRateTable, cloneDT, cloneNormalizer, suffix,
								replaceParam, parameters, ignoreList, isClone, usingParams, session).getBlockId();
						priceComponentBlockMap.setBlockId(blockClonedId);
						ignoreList.getBlocks().put(block.getBlockId(), blockClonedId);
					}

					priceComponentBlockMap.setBlockId(blockClonedId);
				}

				// Save map
				session.save(priceComponentBlockMap);
			}

			return priceComponentToClone;
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}
	}

	public PriceComponent clonePC(PriceComponent priceComponent, boolean cloneBlock, boolean cloneRateTable,
			boolean cloneDT, boolean cloneNormalizer, String suffix) throws Exception {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			PriceComponent priceCloned = this.clonePC(priceComponent, cloneBlock, cloneRateTable, cloneDT,
					cloneNormalizer, suffix, false, new ArrayList<Parameter>(), new CloneIgnoreListModel(), true,
					new CloneIgnoreListModel(), session);
			session.getTransaction().commit();
			return priceCloned;
		} catch (Exception e) {
			
			session.getTransaction().rollback();
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	@SuppressWarnings("unchecked")
	private List<Block> findBlockByPC(PriceComponent priceComponent, Session session) {

		String hql = "SELECT bl FROM Block bl JOIN PriceComponentBlockMap map ON map.blockId = bl.blockId ";
		hql += " WHERE map.priceComponentId = :priceComponentId AND map.domainId = :domainId ORDER BY map.blockIndex";
		try {

			Query<Block> query = session.createQuery(hql);
			query.setParameter("priceComponentId", priceComponent.getPriceComponentId());
			query.setParameter("domainId", getDomainId());
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}
	}

	public Long getMaxIndex(Session session) {
		try {

			StringBuffer hql = new StringBuffer("SELECT COALESCE(MAX(a.index), 0) FROM PriceComponent a");
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
			StringBuffer hql = new StringBuffer("SELECT COALESCE(MAX(a.index), 0) FROM PriceComponent a");
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

	public PriceComponent findPriceComponentIndexGT(Long index) {
		Session session = HibernateUtil.getOpenSession();
		try {
			StringBuffer hql = new StringBuffer("SELECT a FROM PriceComponent a");
			hql.append(" WHERE a.domainId = :domainId");
			hql.append(" AND a.index > :index");
			hql.append(" ORDER BY a.index ASC");

			Query<PriceComponent> query = session.createQuery(hql.toString());
			query.setParameter("domainId", getDomainId());
			query.setParameter("index", index);

			List<PriceComponent> priceComponents = query.getResultList();
			if (priceComponents != null && priceComponents.size() > 0) {
				return priceComponents.get(0);
			}
			return null;
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public PriceComponent findPriceComponentIndexLT(Long index) {
		Session session = HibernateUtil.getOpenSession();
		try {

			StringBuffer hql = new StringBuffer("SELECT a FROM PriceComponent a");
			hql.append(" WHERE a.domainId = :domainId");
			hql.append(" AND a.index < :index");
			hql.append(" ORDER BY a.index DESC");

			Query<PriceComponent> query = session.createQuery(hql.toString());
			query.setParameter("domainId", getDomainId());
			query.setParameter("index", index);

			List<PriceComponent> priceComponents = query.getResultList();
			if (priceComponents != null && priceComponents.size() > 0) {
				return priceComponents.get(0);
			}
			return null;
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	@SuppressWarnings("unchecked")
	public List<Block> findBlockByPC(PriceComponent priceComponent) {

		String hql = "SELECT bl FROM Block bl JOIN PriceComponentBlockMap map ON map.blockId = bl.blockId ";
		hql += " WHERE map.priceComponentId = :priceComponentId AND map.domainId = :domainId ORDER BY map.blockIndex";
		Session session = HibernateUtil.getOpenSession();
		try {
			Query<Block> query = session.createQuery(hql);
			query.setParameter("priceComponentId", priceComponent.getPriceComponentId());
			query.setParameter("domainId", getDomainId());
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public List<PriceComponent> findPCByAction(List<Long> lstActionId) {
		if (lstActionId.size() == 0)
			lstActionId.add(-1L);
		List<PriceComponent> lst = null;
		String hql = "SELECT DISTINCT pc FROM PriceComponent pc JOIN ActionPriceComponentMap map ON map.priceComponentId = pc.priceComponentId ";
		hql += " WHERE map.actionId IN (:actionId) AND map.domainId = :domainId ";

		Session session = HibernateUtil.getOpenSession();
		try {
			Query query = session.createQuery(hql);
			query.setParameter("actionId", lstActionId);
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
	
	public List<PriceComponent> findPCByPcType(List<Long> lstPcTypeId) {
		if (lstPcTypeId.size() == 0)
			lstPcTypeId.add(-1L);
		List<PriceComponent> lst = null;
		String hql = "SELECT DISTINCT pc FROM PriceComponent pc  ";
		hql += " WHERE pc.pcType IN (:pcType) ";

		Session session = HibernateUtil.getOpenSession();
		try {
			Query query = session.createQuery(hql);
			query.setParameter("pcType", lstPcTypeId);
			lst = query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}

		return lst;
	}

	public List<PriceComponent> findPCByListBlock(List<Long> lstBlockId) {
		if (lstBlockId.size() == 0)
			lstBlockId.add(-1L);
		List<PriceComponent> lst = null;
		String hql = "SELECT DISTINCT pc FROM PriceComponent pc JOIN PriceComponentBlockMap map ON map.priceComponentId = pc.priceComponentId ";
		hql += " WHERE map.blockId IN (:blockId) AND map.domainId = :domainId ";

		Session session = HibernateUtil.getOpenSession();
		try {
			Query query = session.createQuery(hql);
			query.setParameter("blockId", lstBlockId);
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

	public Boolean processMoveUpDown(PriceComponent item, Boolean isUp) {
		boolean result = false;
		if (item != null && item.getPriceComponentId() > 0) {
			Session session = HibernateUtil.getOpenSession();
			item = get(item.getPriceComponentId());
			try {
				session.getTransaction().begin();
				StringBuffer hql = new StringBuffer("SELECT a FROM PriceComponent a");
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

				Query<PriceComponent> query = session.createQuery(hql.toString());
				query.setParameter("domainId", getDomainId());
				query.setParameter("index", item.getIndex());
				query.setParameter("categoryId", item.getCategoryId());
				query.setMaxResults(1);

				List<PriceComponent> components = query.getResultList();
				if (components != null && components.size() > 0) {
					// if (!checkFieldIsExist("index", item.getIndex(), item)) {
					PriceComponent itemMove = components.get(0);
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
				result = false;
			} finally {
				session.close();
			}
		}
		return result;
	}

	public Boolean checkFieldIsExist(String col, Object value, PriceComponent priceComponent) {
		boolean result = false;

		int count = 0;

		if (priceComponent == null || priceComponent.getPriceComponentId() == 0) {
			String[] column = { col };
			Operator[] ope = { Operator.EQ };
			Object[] val = { value };
			count = this.countByConditions(column, ope, val);
		} else {
			String[] column = { col, "priceComponentId" };
			Operator[] ope = { Operator.EQ, Operator.NOTEQ };
			Object[] val = { value, priceComponent.getPriceComponentId() };
			count = this.countByConditions(column, ope, val);
		}

		if (count > 0) {
			result = true;
		}

		return result;
	}

	public List<PriceComponent> findByCategoryId(Long categoryId) {
		List<PriceComponent> lst = null;
		String[] cols = { "categoryId", "domainId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { categoryId, getDomainId() };
		lst = findByConditions(cols, operators, values, " index ASC");
		return lst;
	}

	public int countPriceComponent() {
		String hql = "SELECT COUNT(a) FROM PriceComponent a WHERE a.domainId =:domainId ";
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

	public void deleteBlockByPC(PriceComponent priceComponent, Session session) {
		try {

			priceComponent = session.get(PriceComponent.class, priceComponent.getPriceComponentId());
			if (priceComponent != null && priceComponent.isGenerated() != null && priceComponent.isGenerated()) {
				List<Block> blocks = this.findBlockByPC(priceComponent, session);
				BlockDAO blockDAO = new BlockDAO();
				for (Block block : blocks) {
					blockDAO.deleteRT(block, session);
				}
				session.delete(priceComponent);
			}
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}
	}

	public List<PriceComponent> findPriceComponentByCategoryIdAndNotGened(List<Long> lstAllCatID) {
		String hql = "SELECT a FROM PriceComponent a";
		hql += " WHERE a.categoryId IN (:categoryIds) AND (a.generated is false OR a.generated is NULL)";

		Session session = HibernateUtil.getOpenSession();
		try {

			Query<PriceComponent> query = session.createQuery(hql, PriceComponent.class);
			query.setParameterList("categoryIds", lstAllCatID);
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}
	
	public boolean moveToCate(PriceComponent priceComponent) {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			priceComponent.setDomainId(getDomainId());
			generateIndexByCat(priceComponent,"priceComponentId", session);
			session.saveOrUpdate(priceComponent);
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
	
	public boolean checkPcTypeInPC(Long pcType) {
		List<PriceComponent> lst = null;
		String[] cols = { "pcType" };
		Operator[] operators = { Operator.EQ };
		Object[] values = { pcType };
		lst = findByConditions(cols, operators, values, "");
		if (lst != null && lst.size() > 0) {
			return true;
		}
		return false;
	}
}

package com.viettel.ocs.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.constant.Normalizer;
import com.viettel.ocs.constant.Normalizer.RateTableState;
import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.ActionPriceComponentMap;
import com.viettel.ocs.entity.Block;
import com.viettel.ocs.entity.BlockRateTableMap;
import com.viettel.ocs.entity.Formula;
import com.viettel.ocs.entity.Parameter;
import com.viettel.ocs.entity.PriceComponent;
import com.viettel.ocs.entity.PriceComponentBlockMap;
import com.viettel.ocs.entity.RateTable;
import com.viettel.ocs.entity.SortPriceRateTableMap;
import com.viettel.ocs.entity.TriggerDestination;
import com.viettel.ocs.model.BlockRateTableMapModel;
import com.viettel.ocs.model.CloneIgnoreListModel;
import com.viettel.ocs.model.ThresHoldTrigerModel;

public class BlockDAO extends BaseDAO<Block> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3348774554206769903L;

	public int countBlockByPC(Long priceComponentId) {

		List lst = null;
		String hql = "SELECT COUNT(bl.blockId) FROM Block bl JOIN PriceComponentBlockMap map ON map.blockId = bl.blockId ";
		hql += " WHERE map.priceComponentId = :priceComponentId AND map.domainId=:domainId";

		Session session = HibernateUtil.getOpenSession();
		try {

			Query<Block> query = session.createQuery(hql);
			query.setParameter("priceComponentId", priceComponentId);
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

	public List<Block> findBlockByPC(Long priceComponentId) {

		List<Block> lst = null;
		String hql = "SELECT bl FROM Block bl JOIN PriceComponentBlockMap map ON map.blockId = bl.blockId ";
		hql += " WHERE map.priceComponentId = :priceComponentId AND map.domainId=:domainId ORDER BY map.blockIndex ASC";

		Session session = HibernateUtil.getOpenSession();
		try {
			Query<Block> query = session.createQuery(hql);
			query.setParameter("priceComponentId", priceComponentId);
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

	public List<Block> findByListCategory(List<Long> lstCatID) {

		String[] cols = { "categoryId", "domainId" };
		Operator[] operators = { Operator.IN, Operator.EQ };
		List<Long> lstCatIDParam = new ArrayList<Long>();
		lstCatIDParam.addAll(lstCatID);
		if (lstCatIDParam.size() == 0)
			lstCatIDParam.add(-1L);
		Object[] values = { lstCatIDParam, getDomainId() };
		String oder = " index ASC";
		return super.findByConditions(cols, operators, values, oder);
	}

	public List<Block> findByCategoryId(Long categoryId) {
		List<Block> lst = null;
		String[] cols = { "categoryId", "domainId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { categoryId, getDomainId() };
		lst = findByConditions(cols, operators, values, " index ASC");
		return lst;
	}

	public List<RateTable> findRatetableByBlockId(long blockId) {
		List<RateTable> lst = null;
		String hql = "SELECT rt FROM RateTable rt JOIN BlockRateTableMap map ON map.rateTableId = rt.rateTableId ";
		hql += " WHERE map.blockId = :blockId AND map.domainId=:domainId ";

		Session session = HibernateUtil.getOpenSession();
		try {
			Query query = session.createQuery(hql);
			query.setParameter("blockId", blockId);
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

	public List<RateTable> findRatetableByBlockIdType(long blockId, long componentType) {
		List<RateTable> lst = null;
		String hql = "SELECT rt FROM RateTable rt JOIN BlockRateTableMap map ON map.rateTableId = rt.rateTableId ";
		hql += " WHERE map.blockId = :blockId ";
		hql += " AND map.componentType = :componentType";
		hql += " AND map.domainId=:domainId ORDER BY map.rateTableIndex ASC";

		Session session = HibernateUtil.getOpenSession();
		try {
			Query query = session.createQuery(hql);
			query.setParameter("blockId", blockId);
			query.setParameter("componentType", componentType);
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

	public void saveListRatetableBlock(Block blockUI, List<RateTable> listBasic, List<RateTable> listBasicOld,
			List<RateTable> listDiscount, List<RateTable> listDiscountOld, List<RateTable> listCondition,
			List<RateTable> listConditionOld) {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			blockUI.setDomainId(getDomainId());
			if (blockUI.getBlockId() == 0L) {
				blockUI.setDomainId(getDomainId());
			}
			generateIndexByCat(blockUI, "blockId", session);
			session.saveOrUpdate(blockUI);

			List<Long> ids = new ArrayList<Long>();
			for (RateTable rateTable : listBasicOld) {
				if (rateTable.getRateTableId() > 0) {
					ids.add(rateTable.getRateTableId());
				}
			}
			for (RateTable rateTable : listConditionOld) {
				if (rateTable.getRateTableId() > 0) {
					ids.add(rateTable.getRateTableId());
				}
			}
			for (RateTable rateTable : listDiscountOld) {
				if (rateTable.getRateTableId() > 0) {
					ids.add(rateTable.getRateTableId());
				}
			}
			List<Long> lstComponentType = new ArrayList<Long>();
			lstComponentType.add(Normalizer.TypeRateTable.BASIC);
			lstComponentType.add(Normalizer.TypeRateTable.DISCOUNT);
			lstComponentType.add(Normalizer.TypeRateTable.CONDITION);

			if (!ids.isEmpty()) {
				// If list old has data -> del list old in map -> add new all
				StringBuilder hql = new StringBuilder("DELETE FROM BlockRateTableMap WHERE blockId = :blockId");
				hql.append(" AND rateTableId IN (:listOld)");
				hql.append(" AND domainId =:domainId");
				hql.append(" AND componentType IN (:lstComponentType)");
				// Delete all map
				Query<BlockRateTableMap> query = session.createQuery(hql.toString());
				query.setParameterList("listOld", ids);
				query.setParameterList("lstComponentType", lstComponentType);
				query.setParameter("blockId", blockUI.getBlockId());
				query.setParameter("domainId", getDomainId());
				query.executeUpdate();
				// session.getTransaction().commit();
			}
			// If list old no data -> only add new
			if (!listBasic.isEmpty()) {
				for (RateTable rateTable : listBasic) {
					BlockRateTableMap map = new BlockRateTableMap();
					map.setBlockId(blockUI.getBlockId());
					map.setRateTableId(rateTable.getRateTableId());
					Long index = (long) listBasic.indexOf(rateTable) + 1;
					map.setDomainId(getDomainId());
					map.setRateTableIndex(index);
					map.setComponentType(Normalizer.TypeRateTable.BASIC);
					session.saveOrUpdate(map);
				}
			}
			if (!listDiscount.isEmpty()) {
				for (RateTable rateTable : listDiscount) {
					BlockRateTableMap map = new BlockRateTableMap();
					map.setBlockId(blockUI.getBlockId());
					map.setRateTableId(rateTable.getRateTableId());
					Long index = (long) listDiscount.indexOf(rateTable) + 1;
					map.setRateTableIndex(index);
					map.setDomainId(getDomainId());
					map.setComponentType(Normalizer.TypeRateTable.DISCOUNT);
					session.saveOrUpdate(map);
				}
			}
			if (!listCondition.isEmpty()) {
				for (RateTable rateTable : listCondition) {
					BlockRateTableMap map = new BlockRateTableMap();
					map.setBlockId(blockUI.getBlockId());
					map.setRateTableId(rateTable.getRateTableId());
					Long index = (long) listCondition.indexOf(rateTable) + 1;
					map.setDomainId(getDomainId());
					map.setRateTableIndex(index);
					map.setComponentType(Normalizer.TypeRateTable.CONDITION);
					session.saveOrUpdate(map);
				}
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
	protected Class<Block> getEntityClass() {
		return Block.class;
	}

	public boolean deletePriceComponentBlockMap(Block block, Long priceComponentId) {
		if (block != null) {
			Session session = HibernateUtil.getOpenSession();
			try {
				session.getTransaction().begin();
				StringBuffer hql = new StringBuffer("DELETE FROM PriceComponentBlockMap a");
				hql.append(" WHERE a.blockId =:blockId");
				hql.append(" AND a.priceComponentId = :priceComponentId");
				hql.append(" AND a.domainId = :domainId");

				Query<ActionPriceComponentMap> query = session.createQuery(hql.toString());
				query.setParameter("blockId", block.getBlockId());
				query.setParameter("priceComponentId", priceComponentId);
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

	public boolean checkName(Block block, boolean edit) {
		List<Block> lst = null;
		String[] cols = { "blockName", "domainId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { block.getBlockName(), getDomainId() };
		lst = findByConditions(cols, operators, values, "");
		if (lst.size() > 0) {
			if (edit && lst.get(0).getBlockId() == block.getBlockId()) {
				return false;
			} else {
				return true;
			}
		}
		return false;

	}

	/**
	 * Clone Block
	 * 
	 * @author THANHND
	 * @param block
	 * @param cloneRateTable
	 * @param cloneDT
	 * @param parameters
	 * @param ignoreList
	 * @param session
	 * @throws CloneNotSupportedException
	 */
	public Block cloneBlock(Block block, boolean cloneRateTable, boolean cloneDT, boolean cloneNormalizer,
			String suffix, boolean replaceParam, List<Parameter> parameters, CloneIgnoreListModel ignoreList,
			boolean isClone, CloneIgnoreListModel usingParams, Session session) throws CloneNotSupportedException {

		try {
			if (!isClone && usingParams.isIgnore(block) == null) {
				return block;
			}

			Block blockToClone = block.clone();
			blockToClone.setGenerated(!isClone);

			List<BlockRateTableMapModel> blockRateTableMapModels = this.findRateTableByBlock(blockToClone, session);

			blockToClone.setBlockId(0L);
			blockToClone.setBlockName(generateNameObject("blockName", blockToClone.getBlockName() + suffix, 0, session));
			blockToClone.setIndex(this.getMaxIndex(session) + 1);
			session.save(blockToClone);

			// Level RateTable - 5
			for (BlockRateTableMapModel blockRateTableMapModel : blockRateTableMapModels) {
				// Prepare map
				BlockRateTableMap blockRateTableMap = blockRateTableMapModel.getBlockRateTableMap().clone();
				blockRateTableMap.setBlockRateTableMapId(0L);
				blockRateTableMap.setBlockId(blockToClone.getBlockId());

				if (cloneRateTable) {
					RateTable rateTable = blockRateTableMapModel.getRateTable();

					Long rateTableClonedId = ignoreList.isIgnore(rateTable);
					if (rateTableClonedId == null) {
						RateTableDAO rateTableDAO = new RateTableDAO();
						rateTableClonedId = rateTableDAO.cloneRateTable(rateTable, cloneDT, cloneNormalizer, suffix,
								replaceParam, parameters, ignoreList, isClone, usingParams, session).getRateTableId();
						ignoreList.getRateTables().put(rateTable.getRateTableId(), rateTableClonedId);
					}
					blockRateTableMap.setRateTableId(rateTableClonedId);
				}
				// Save map
				session.save(blockRateTableMap);
			}

			return blockToClone;
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}
	}

	public Block cloneBlock(Block block, boolean cloneRateTable, boolean cloneDT, boolean cloneNormalizer,
			String suffix) throws Exception {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			Block blockCloned = this.cloneBlock(block, cloneRateTable, cloneDT, cloneNormalizer, suffix, false,
					new ArrayList<Parameter>(), new CloneIgnoreListModel(), true, new CloneIgnoreListModel(), session);
			session.getTransaction().commit();
			return blockCloned;
		} catch (Exception e) {
			
			session.getTransaction().rollback();
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	private List<BlockRateTableMapModel> findRateTableByBlock(Block block, Session session) {
		String hql = "SELECT new com.viettel.ocs.model.BlockRateTableMapModel(map, rt) FROM RateTable rt JOIN BlockRateTableMap map ON map.rateTableId = rt.rateTableId ";
		hql += " WHERE map.blockId = :blockId AND map.domainId=:domainId ORDER BY map.rateTableIndex";
		try {
			Query<BlockRateTableMapModel> query = session.createQuery(hql, BlockRateTableMapModel.class);
			query.setParameter("blockId", block.getBlockId());
			query.setParameter("domainId", getDomainId());
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}		
	}

	private List<RateTable> findRateTableByBlockDistinct(Block block, Session session) {
		String hql = "SELECT DISTINCT(rt) FROM RateTable rt JOIN BlockRateTableMap map ON map.rateTableId = rt.rateTableId ";
		hql += " WHERE map.blockId = :blockId AND map.domainId=:domainId ORDER BY map.rateTableIndex";
		try {
			Query<RateTable> query = session.createQuery(hql, RateTable.class);
			query.setParameter("blockId", block.getBlockId());
			query.setParameter("domainId", getDomainId());
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}		
	}

	@SuppressWarnings("unchecked")
	public List<RateTable> findRateTableByBlock(Block block) {
		String hql = "SELECT rt FROM RateTable rt JOIN BlockRateTableMap map ON map.rateTableId = rt.rateTableId ";
		hql += " WHERE map.blockId = :blockId AND map.domainId=:domainId ORDER BY map.rateTableIndex";
		Session session = HibernateUtil.getOpenSession();
		try {
			Query<RateTable> query = session.createQuery(hql);
			query.setParameter("blockId", block.getBlockId());
			query.setParameter("domainId", getDomainId());
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public List<Block> findBlockByListPC(List<Long> lstPriceComponentId) {

		if (lstPriceComponentId.size() == 0)
			lstPriceComponentId.add(-1L);
		List<Block> lst = null;
		String hql = "SELECT DISTINCT bl FROM Block bl JOIN PriceComponentBlockMap map ON map.blockId = bl.blockId ";
		hql += " WHERE map.priceComponentId IN (:priceComponentId) AND map.domainId=:domainId";

		Session session = HibernateUtil.getOpenSession();
		try {
			Query<Block> query = session.createQuery(hql);
			query.setParameterList("priceComponentId", lstPriceComponentId);
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

	public List<Block> findRatetableByListRateTableId(List<Long> lstRateTableIDs) {
		if (lstRateTableIDs.size() == 0)
			lstRateTableIDs.add(-1L);
		List<Block> lst = null;
		String hql = "SELECT DISTINCT b FROM Block b JOIN BlockRateTableMap map ON map.blockId = b.blockId ";
		hql += " WHERE map.rateTableId IN (:rateTableId) AND map.domainId=:domainId";
		Session session = HibernateUtil.getOpenSession();
		try {
			Query query = session.createQuery(hql);
			query.setParameter("rateTableId", lstRateTableIDs);
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

	public Block findBlockIndexLT(Long index, Long categoryId) {
		Session session = HibernateUtil.getOpenSession();
		try {
			
			StringBuffer hql = new StringBuffer("SELECT a FROM Block a");
			hql.append(" WHERE a.domainId = :domainId");
			hql.append(" AND a.index < :index");
			hql.append(" AND a.categoryId =:categoryId");
			hql.append(" ORDER BY a.index DESC");

			Query<Block> query = session.createQuery(hql.toString());
			query.setParameter("domainId", getDomainId());
			query.setParameter("index", index);
			query.setParameter("categoryId", categoryId);

			List<Block> blocks = query.getResultList();
			if (blocks != null && blocks.size() > 0) {
				return blocks.get(0);
			}
			return null;
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}		
	}

	public Block findBlockIndexGT(Long index, Long categoryId) {
		Session session = HibernateUtil.getOpenSession();
		try {

			StringBuffer hql = new StringBuffer("SELECT a FROM Block a");
			hql.append(" WHERE a.domainId = :domainId");
			hql.append(" AND a.index > :index");
			hql.append(" AND a.categoryId =:categoryId");
			hql.append(" ORDER BY a.index DESC");

			Query<Block> query = session.createQuery(hql.toString());
			query.setParameter("domainId", getDomainId());
			query.setParameter("index", index);
			query.setParameter("categoryId", categoryId);
			List<Block> blocks = query.getResultList();
			if (blocks != null && blocks.size() > 0) {
				return blocks.get(0);
			}
			return null;
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
		
	}

	public Long getMaxIndex(Session session) {
		try {
			StringBuffer hql = new StringBuffer("SELECT COALESCE(MAX(a.index), 0) FROM Block a");
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
			StringBuffer hql = new StringBuffer("SELECT COALESCE(MAX(a.index), 0) FROM Block a");
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

	public Boolean processMoveUpDown(Block item, Boolean isUp) {
		boolean result = false;
		if (item != null && item.getBlockId() > 0) {
			Session session = HibernateUtil.getOpenSession();
			item = get(item.getBlockId());
			try {

				session.getTransaction().begin();

				StringBuffer hql = new StringBuffer("SELECT a FROM Block a");
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

				Query<Block> query = session.createQuery(hql.toString());
				query.setParameter("domainId", getDomainId());
				query.setParameter("index", item.getIndex());
				query.setParameter("categoryId", item.getCategoryId());
				query.setMaxResults(1);

				List<Block> blocks = query.getResultList();
				if (blocks != null && blocks.size() > 0) {
					if (!checkFieldIsExist("index", item.getIndex(), item)) {
						Block itemMove = blocks.get(0);
						Long tmpIndex = 0l;
						tmpIndex = itemMove.getIndex();
						itemMove.setIndex(item.getIndex());
						item.setIndex(tmpIndex);
						session.update(itemMove);
						session.update(item);
						result = true;
					} else {
						result = false;
					}
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

	public Boolean checkFieldIsExist(String col, Object value, Block block) {
		boolean result = false;

		int count = 0;

		if (block == null || block.getBlockId() == 0) {
			String[] column = { col };
			Operator[] ope = { Operator.EQ };
			Object[] val = { value };
			count = this.countByConditions(column, ope, val);
		} else {
			String[] column = { col, "blockId" };
			Operator[] ope = { Operator.EQ, Operator.NOTEQ };
			Object[] val = { value, block.getBlockId() };
			count = this.countByConditions(column, ope, val);
		}

		if (count > 0) {
			result = true;
		}

		return result;
	}

	public int countBlock() {
		String hql = "SELECT COUNT(a) FROM Block a WHERE  a.domainId =:domainId";
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

	public Long countRatetableByBlockIdType(long blockId, long componentType) {
		List lst = null;
		String hql = "SELECT COUNT(rt) FROM RateTable rt JOIN BlockRateTableMap map ON map.rateTableId = rt.rateTableId ";
		hql += " WHERE map.blockId = :blockId ";
		hql += " AND map.componentType = :componentType";
		hql += " AND map.domainId=:domainId ORDER BY map.rateTableIndex ASC";

		Session session = HibernateUtil.getOpenSession();
		try {
			Query<Long> query = session.createQuery(hql, Long.class);
			query.setParameter("blockId", blockId);
			query.setParameter("componentType", componentType);
			query.setParameter("domainId", getDomainId());
			return query.getSingleResult();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public void deleteRT(Block block, Session session) {
		try {
			block = session.get(Block.class, block.getBlockId());
			if (block != null && block.isGenerated() != null && block.isGenerated()) {
				List<RateTable> rateTables = this.findRateTableByBlockDistinct(block, session);

				RateTableDAO rateTableDAO = new RateTableDAO();
				for (RateTable rateTable : rateTables) {
					rateTableDAO.deleteFormulasAndDTByRT(rateTable, session);
				}
				session.delete(block);
			}
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}		
	}

	public List<Block> findByCategoriesWithinGenerated(List<Long> lstAllCatID) {
		lstAllCatID.add(-1L);
		String hql = "SELECT a FROM Block a";
		hql += " WHERE a.categoryId IN (:categoryIds)";
		hql += " AND (a.generated is false OR a.generated is null)";
		hql += " AND a.domainId=:domainId ORDER BY a.index ASC";

		Session session = HibernateUtil.getOpenSession();
		try {
			Query<Block> query = session.createQuery(hql, Block.class);
			query.setParameterList("categoryIds", lstAllCatID);
			query.setParameter("domainId", getDomainId());
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}
	
	public boolean moveToCate(Block block) {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			block.setDomainId(getDomainId());
			generateIndexByCat(block,"blockId", session);
			session.saveOrUpdate(block);
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

	public List<Block> findBlockByPcType(Long pcTypeId) {
		List<Block> lst = null;
		String hql = "SELECT bl FROM Block bl JOIN PcTypeBlockMap map ON map.blockId = bl.blockId ";
		hql += " WHERE map.pcTypeId = :pcTypeId ORDER BY map.blockIndex ASC";

		Session session = HibernateUtil.getOpenSession();
		try {
			Query<Block> query = session.createQuery(hql);
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
	
	public List<Block> findBlockByPcTypeList(List<Long> lstPcTypeId) {
		
		if(lstPcTypeId == null || lstPcTypeId.isEmpty())
			return new ArrayList<Block>();
		
		List<Block> lst = null;
		String hql = "SELECT bl FROM Block bl JOIN PcTypeBlockMap map ON map.blockId = bl.blockId ";
		hql += " WHERE map.pcTypeId IN (:pcTypeId) ";
		
		Session session = HibernateUtil.getOpenSession();
		try {
			Query<Block> query = session.createQuery(hql);
			query.setParameter("pcTypeId", lstPcTypeId);
			lst = query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
		
		return lst;
	}

	public List<Block> findBlockBySPC(List<Long> lstSPCId) {
		
		if(lstSPCId == null || lstSPCId.isEmpty())
			return new ArrayList<Block>();
		
		List<Block> lst = null;
		String hql = "SELECT bl FROM Block bl ";
		hql += " WHERE bl.blockFilterId IN (:lstSPCId) ";
		
		Session session = HibernateUtil.getOpenSession();
		try {
			Query<Block> query = session.createQuery(hql);
			query.setParameter("lstSPCId", lstSPCId);
			lst = query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
		
		return lst;
	}
	
	public int countBlockByPCT(Long pcTypeId) {

		List lst = null;
		String hql = "SELECT COUNT(bl.blockId) FROM Block bl JOIN PcTypeBlockMap map ON map.blockId = bl.blockId ";
		hql += " WHERE map.pcTypeId = :pcTypeId ORDER BY map.blockIndex ASC";

		Session session = HibernateUtil.getOpenSession();
		try {

			Query<Block> query = session.createQuery(hql);
			query.setParameter("pcTypeId", pcTypeId);
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
}

package com.viettel.ocs.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.constant.CategoryType;
import com.viettel.ocs.context.SessionUtils;
import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.RateTable;
import com.viettel.ocs.entity.ReserveInfo;
import com.viettel.ocs.entity.UnitType;

/**
 * @author Nampv
 * @since 22082016
 */
public class ReserveInfoDAO extends BaseDAO<ReserveInfo> {
	public ReserveInfoDAO() {
		domainId = SessionUtils.getDomainId();
	}

	private long domainId;

	public long getDomainId() {
		return domainId;
	}

	/**
	 * categoryId domainId
	 */
	public List<ReserveInfo> findReserveInfoByConditions(List<Long> lstCatID) {

		String[] col = { "categoryId", "domainId" };
		Operator[] ope = { Operator.IN, Operator.EQ };
		Object[] val = { lstCatID, getDomainId() };
		if (lstCatID == null || lstCatID.size() == 0)
			lstCatID.add(-1L);
		String oder = "index";
		return this.findByConditions(col, ope, val, oder);
	}

	/**
	 * load List Category by categoryType
	 */
	public List<Category> loadListCategory() {
		CategoryDAO categoryDAO = new CategoryDAO();
		return categoryDAO.loadListCategoryByType(CategoryType.CTL_UT_RESERVE_INFO);
	}

	/**
	 * load List ReserveInfo by domain
	 */
	public List<ReserveInfo> loadListReserveInfo(Long categoryId) {
		String[] col = { "categoryId", "domainId" };
		Operator[] ope = { Operator.EQ, Operator.EQ };
		Object[] val = { categoryId, getDomainId() };
		String oder = "index";
		return this.findByConditions(col, ope, val, oder);
	}

	public List<ReserveInfo> loadAllListReserveInfo(Long categoryId) {
		return this.findAll("");
	}

	public void deleteReserveInfo(ReserveInfo item) {
		this.delete(item);
	}

	@Override
	protected Class<ReserveInfo> getEntityClass() {
		return ReserveInfo.class;
	}

	public boolean checkName(ReserveInfo reserveInfo, String resvName, boolean edit) {
		List<ReserveInfo> lst = null;
		String[] cols = { "resvName", "domainId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { resvName, getDomainId() };
		lst = findByConditions(cols, operators, values, "");
		if (lst.size() > 0) {
			if (edit && lst.get(0).getReserveInfoId() == reserveInfo.getReserveInfoId()) {
				return false;
			} else {
				return true;
			}
		}
		return false;

	}

	public Boolean processMoveUpDown(ReserveInfo item, Boolean isUp) {
		boolean result = false;
		if (item != null && item.getReserveInfoId() > 0) {
			Session session = HibernateUtil.getOpenSession();
			item = get(item.getReserveInfoId());
			try {

				session.getTransaction().begin();

				StringBuffer hql = new StringBuffer("SELECT a FROM ReserveInfo a");
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

				Query<ReserveInfo> query = session.createQuery(hql.toString());
				query.setParameter("domainId", getDomainId());
				query.setParameter("index", item.getIndex());
				query.setParameter("categoryId", item.getCategoryId());
				query.setMaxResults(1);

				List<ReserveInfo> reserveInfos = query.getResultList();
				if (reserveInfos != null && reserveInfos.size() > 0) {
					// if (!checkFieldIsExist("index", item.getIndex(), item)) {
					ReserveInfo itemMove = reserveInfos.get(0);
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

	public ReserveInfo findReserveInfoLastIndex() {
		Session session = HibernateUtil.getOpenSession();
		try {

			StringBuffer hql = new StringBuffer("SELECT a FROM ReserveInfo a");
			hql.append(" WHERE a.domainId = :domainId");
			hql.append(" ORDER BY a.index ASC");

			Query<ReserveInfo> query = session.createQuery(hql.toString());
			query.setParameter("domainId", getDomainId());

			List<ReserveInfo> reserveInfos = query.getResultList();
			if (reserveInfos != null && reserveInfos.size() > 0) {
				return reserveInfos.get(0);
			}
			return null;
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public int countReserveInfo() {
		String hql = "SELECT COUNT(a) FROM ReserveInfo a WHERE  a.domainId =:domainId ";
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
		return getMaxIndex("index");
	}
	
	public boolean saveReserveInfo(ReserveInfo reserveInfo) {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			reserveInfo.setDomainId(getDomainId());
			generateIndexByCat(reserveInfo, "reserveInfoId", session);
			session.saveOrUpdate(reserveInfo);
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

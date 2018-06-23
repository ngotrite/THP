package com.viettel.ocs.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.constant.CategoryType;
import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.SysMenu;

public class CategoryDAO extends BaseDAO<Category> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3737101942000396141L;

	public List<Category> findByType(Long catType) {

		String[] cols = { "categoryType" };
		Operator[] operators = { Operator.EQ };
		Object[] values = { catType };
		return super.findByConditions(cols, operators, values, "posIndex");
	}
	
	public List<Category> findByTypeWithoutDomain(Long catType) {

		String[] cols = { "categoryType" };
		Operator[] operators = { Operator.EQ };
		Object[] values = { catType };
		return super.findByConditionsWithoutDomain(cols, operators, values, "posIndex");
	}

	public List<Category> findByTypeAndcategoryParentId(Long catType, long categoryParentId) {

		String[] cols = { "categoryType", "categoryParentId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { catType, categoryParentId };
		return super.findByConditions(cols, operators, values, "posIndex");
	}

	public List<Category> findByTypeForSelectbox(Long catType) {

		String[] cols = { "categoryType" };
		Operator[] operators = { Operator.EQ };
		Object[] values = { catType };
		List<Category> lst = super.findByConditions(cols, operators, values, "posIndex");

		for (Category cat : lst) {

			int level = StringUtils.countMatches(cat.getPath(), "/");
			String name = StringUtils.repeat("--", level - 1) + " " + cat.getCategoryName();
			cat.setCategoryName(name);
		}

		List<Category> lstReturn = new ArrayList<Category>();
		sortTree(null, lst, lstReturn);
		return lstReturn;
	}
	
	public List<Category> findByTypeForSelectboxWithoutDomain(Long catType) {

		String[] cols = { "categoryType" };
		Operator[] operators = { Operator.EQ };
		Object[] values = { catType };
		List<Category> lst = super.findByConditionsWithoutDomain(cols, operators, values, "posIndex");

		for (Category cat : lst) {

			int level = StringUtils.countMatches(cat.getPath(), "/");
			String name = StringUtils.repeat("--", level - 1) + " " + cat.getCategoryName();
			cat.setCategoryName(name);
		}

		List<Category> lstReturn = new ArrayList<Category>();
		sortTree(null, lst, lstReturn);
		return lstReturn;
	}

	public List<Category> findByTypeForSelectbox(List<Long> lstCatType) {

		if (lstCatType == null || lstCatType.isEmpty())
			return new ArrayList<Category>();

		String[] cols = { "categoryType" };
		Operator[] operators = { Operator.IN };
		Object[] values = { lstCatType };
		List<Category> lst = super.findByConditions(cols, operators, values, "posIndex");

		for (Category cat : lst) {

			int level = StringUtils.countMatches(cat.getPath(), "/");
			String name = StringUtils.repeat("--", level - 1) + " " + cat.getCategoryName();
			cat.setCategoryName(name);
		}

		List<Category> lstReturn = new ArrayList<Category>();
		sortTree(null, lst, lstReturn);
		return lstReturn;
	}

	public List<Category> findByTypeAndNotEqualForSelectbox(Long catType, long catId) {

		String[] cols = { "categoryType", "categoryId" };
		Operator[] operators = { Operator.EQ, Operator.NOTEQ };
		Object[] values = { catType, catId };
		List<Category> lst = findByConditions(cols, operators, values, "posIndex");

		for (Category cat : lst) {

			int level = StringUtils.countMatches(cat.getPath(), "/");
			String name = StringUtils.repeat("--", level - 1) + " " + cat.getCategoryName();
			cat.setCategoryName(name);
		}

		List<Category> lstReturn = new ArrayList<Category>();
		sortTree(null, lst, lstReturn);
		return lstReturn;
	}
	
	public List<Category> findByTypeAndNotEqualForSelectboxWithoutDomain(Long catType, long catId) {
		
		String[] cols = { "categoryType", "categoryId" };
		Operator[] operators = { Operator.EQ, Operator.NOTEQ };
		Object[] values = { catType, catId };
		List<Category> lst = findByConditionsWithoutDomain(cols, operators, values, "posIndex");
		
		for (Category cat : lst) {
			
			int level = StringUtils.countMatches(cat.getPath(), "/");
			String name = StringUtils.repeat("--", level - 1) + " " + cat.getCategoryName();
			cat.setCategoryName(name);
		}
		
		List<Category> lstReturn = new ArrayList<Category>();
		sortTree(null, lst, lstReturn);
		return lstReturn;
	}

	public void sortTree(Category objParent, List<Category> lstSource, List<Category> lstDest) {

		if (objParent == null) {
			for (Category obj : lstSource) {
				if (obj.getCategoryParentId() == null) {

					lstDest.add(obj);
					sortTree(obj, lstSource, lstDest);
				}
			}
		} else {

			for (Category obj : lstSource) {
				if (obj.getCategoryParentId() != null && obj.getCategoryParentId() == objParent.getCategoryId()) {

					lstDest.add(obj);
					sortTree(obj, lstSource, lstDest);
				}
			}
		}
	}

	public List<Category> loadListCategoryByTypeNoParent(Long categoryType) {

		List<Category> lst = null;
		String hql = "SELECT cat FROM Category cat ";
		hql += " WHERE cat.categoryType = :categoryType AND (categoryParentId IS NULL OR categoryParentId = 0)";
		hql += " AND cat.domainId = :domainId";
		hql += " ORDER BY cat.posIndex";
		Session session = HibernateUtil.getOpenSession();
		try {
			Query<Category> query = session.createQuery(hql);
			query.setParameter("categoryType", categoryType);
			query.setParameter("domainId", super.getDomainId());
			lst = query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}

		return lst;
	}

	public List<Category> loadListCategoryByTypeNoParent(List<Long> lstCatType) {

		if (lstCatType.isEmpty())
			return new ArrayList<>();

		List<Category> lst = null;
		String hql = "SELECT cat FROM Category cat ";
		hql += " WHERE cat.categoryType IN (:categoryType) AND (categoryParentId IS NULL OR categoryParentId = 0)";
		hql += " AND cat.domainId = :domainId";
		hql += " ORDER BY cat.posIndex";
		Session session = HibernateUtil.getOpenSession();
		try {
			Query<Category> query = session.createQuery(hql);
			query.setParameterList("categoryType", lstCatType);
			query.setParameter("domainId", super.getDomainId());
			lst = query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
		return lst;
	}

	public List<Category> loadListCategoryByType(Long categoryType) {
		String[] col = { "categoryType" };
		Operator[] ope = { Operator.EQ };
		Object[] val = { categoryType };
		String oder = "posIndex";
		return this.findByConditions(col, ope, val, oder);

	}

	public List<Category> loadListCategoryRootByType(Long categoryType) {
		String[] col = { "categoryType", "categoryParentId" };
		Operator[] ope = { Operator.EQ, Operator.NULL };
		Object[] val = { categoryType };
		String oder = "posIndex";
		return this.findByConditions(col, ope, val, oder);

	}

	public List<Category> loadListCategoryByParentId(Long categoryId) {
		String[] col = { "categoryParentId" };
		Operator[] ope = { Operator.EQ };
		Object[] val = { categoryId };
		String oder = "posIndex";
		return super.findByConditions(col, ope, val, oder);

	}
	
	public List<Category> findDuplicateName(Category objCat) {
		
		String[] col = {"categoryType", "categoryName", "categoryId", "categoryParentId"};
		Operator[] ope = {Operator.EQ, Operator.LIKE, Operator.NOTEQ , Operator.EQ};
		Object[] val = {objCat.getCategoryType(), objCat.getCategoryName(), objCat.getCategoryId(), objCat.getCategoryParentId() };
		if(objCat.getCategoryParentId() == null || objCat.getCategoryParentId() <= 0) {
			ope[3] = Operator.NULL;
			val[3] = null;
		}
		
//		if(objCat.getCategoryType() != null 
//				&& (objCat.getCategoryType() == CategoryType.CTL_BL_BAL_TYPE 
//					|| objCat.getCategoryType() == CategoryType.CTL_BL_BAL_TYPE_ACC
//					|| objCat.getCategoryType() == CategoryType.CTL_BL_BAL_TYPE_ACCOUNT_MAPPING 
//					|| objCat.getCategoryType() == CategoryType.CTL_BL_BAL_TYPE_ACM_MAPPING)) {
//			
//			ope[0] = Operator.IN;
//			Object[] type = {CategoryType.CTL_BL_BAL_TYPE, CategoryType.CTL_BL_BAL_TYPE_ACC, CategoryType.CTL_BL_BAL_TYPE_ACCOUNT_MAPPING, CategoryType.CTL_BL_BAL_TYPE_ACM_MAPPING};
//			val[0] = type;
//		}
				
		return super.findByConditions(col, ope, val, "");
	}

	@Override
	public void saveOrUpdate(Category category) {

		Session session = HibernateUtil.getOpenSession();
		try {

			String oldPath = category.getPath();
			if (category.getCategoryParentId() == null || category.getCategoryParentId() == 0) {

				category.setCategoryParentId(null);
				category.setPath("/");
			} else {

				Category parentMenu = super.get(category.getCategoryParentId());
				String parentPath = parentMenu.getPath() == null ? "" : parentMenu.getPath();
				category.setPath(parentPath + category.getCategoryParentId() + "/");
			}

			session.beginTransaction();

			if (category.getCategoryId() > 0) {

				session.update(category);

				// update path for children
				oldPath += category.getCategoryId() + "/";
				String hqlUpdateChildren = "UPDATE Category SET path = REPLACE(path, ?, ?) WHERE path LIKE ?";
				Query query = session.createQuery(hqlUpdateChildren);
				query.setParameter(0, oldPath);
				query.setParameter(1, category.getPath() + category.getCategoryId() + "/");
				query.setParameter(2, oldPath + "%");
				query.executeUpdate();
			} else {

				category.setDomainId(super.getDomainId());
				long maxPosIndex = super.getMax("posIndex");
				category.setPosIndex(maxPosIndex + 1);
				session.save(category);
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

	public boolean moveUpDown(boolean moveUp, Category cat) {
		
		Category objDB = super.get(cat.getCategoryId());		
		Long currentIdx = objDB.getPosIndex();
		if (currentIdx == null)
			currentIdx = 0L;

		String[] cols = { "categoryType", "categoryId", "categoryParentId", "posIndex" };
		Operator[] ops = { Operator.EQ, Operator.NOTEQ, Operator.EQ, Operator.LT };
		Object[] vals = { cat.getCategoryType(), cat.getCategoryId(), cat.getCategoryParentId(), currentIdx };
		String orderBy = "posIndex DESC";

		if (cat.getCategoryParentId() == null) {
			ops[2] = Operator.NULL;
			vals[2] = null;
		}

		if (!moveUp) {

			ops[3] = Operator.GT;
			vals[3] = currentIdx;
			orderBy = "posIndex ASC";
		}

		List<Category> lst = super.findByConditions(cols, ops, vals, orderBy);
		if (lst.size() > 0) {

			Session session = HibernateUtil.getOpenSession();
			try {

				session.beginTransaction();

				Category catSwitch = lst.get(0);
				cat.setPosIndex(catSwitch.getPosIndex());
				catSwitch.setPosIndex(currentIdx);
				session.update(cat);
				session.update(catSwitch);

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

	/***
	 * Kiem tra cay co bi lap vong tron hay khong (co con tro toi chinh no)
	 * 
	 * @param idCheck
	 * @return
	 */
	public boolean isRecursive(Long idCheck) {

		return isContainInTree(idCheck, idCheck);
	}

	/***
	 * Kiem tra cac node con trong cay category co chua category can check hay
	 * khong
	 * 
	 * @param idCheck
	 *            - Id kiem tra
	 * @param rootCatId
	 *            - Id root trong cay muon kiem tra
	 * @return true: cay ok; false: cay bi lap vong tron
	 */
	public boolean isContainInTree(Long idCheck, Long rootCatId) {

		if (rootCatId == null)
			return false;

		List<Category> lstChild = this.loadListCategoryByParentId(rootCatId);
		for (Category child : lstChild) {
			if (child.getCategoryId() == idCheck) {

				return true;
			} else {
				if (!isContainInTree(idCheck, child.getCategoryId())) {
					// neu con hien tai khong bi lap thi tiep tuc check con tiep
					// theo
				} else {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	protected Class<Category> getEntityClass() {
		// TODO Auto-generated method stub
		return Category.class;
	}

}

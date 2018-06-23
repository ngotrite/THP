package com.viettel.ocs.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.constant.CategoryType;
import com.viettel.ocs.context.SessionUtils;
import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.CdrProp;
import com.viettel.ocs.entity.CdrService;
import com.viettel.ocs.entity.CdrTemplateProp;
import com.viettel.ocs.entity.NomalizerNormParamMap;
import com.viettel.ocs.entity.NormParam;
import com.viettel.ocs.entity.ReserveInfo;


public class CdrPropDAO extends BaseDAO<CdrProp> {
	public CdrPropDAO() {
		domainId = SessionUtils.getDomainId();
	}

	private long domainId;

	public long getDomainId() {
		return domainId;
	}

	/**
	 * categoryId domainId
	 */
	public List<CdrProp> findCdrServiceByConditions(List<Long> lstCatID) {

		String[] col = { "categoryId" };
		Operator[] ope = { Operator.IN };
		Object[] val = { lstCatID };
		if(lstCatID == null || lstCatID.size() == 0)
			lstCatID.add(-1L);
		String oder = "name";
		return this.findByConditions(col, ope, val, oder);
	}

	/**
	 * load List Category by categoryType
	 */
	public List<Category> loadListCategory() {
		CategoryDAO categoryDAO = new CategoryDAO();
		return categoryDAO.loadListCategoryByType(CategoryType.CTL_CDR_SERVICE);
	}

	/**
	 * load List ReserveInfo by domain
	 */
//	public List<CdrProp> loadListCdrService(Long categoryId) {
//		String[] col = { "categoryId" };
//		Operator[] ope = { Operator.EQ };
//		Object[] val = { categoryId };
//		String oder = "propName";
//		return this.findByConditions(col, ope, val, oder);
//	}
	
	public List<CdrProp> loadAllExclude(List<Long> lstExclude) {
		
		if(lstExclude == null || lstExclude.isEmpty())
			return this.findAll("propName");
		
		String[] col = { "cdrPropId" };
		Operator[] ope = { Operator.NOTIN };
		Object[] val = { lstExclude };
		String oder = "propName";
		return this.findByConditions(col, ope, val, oder);
	}
	
	public Boolean checkFieldIsExist(String col, String value, CdrProp cdrProp){
		boolean result = false;
		
		int count =  0;
		
		if (cdrProp == null || cdrProp.getCdrPropId() == null || cdrProp.getCdrPropId() == 0) {
			String[] column = { col };
			Operator[] ope = { Operator.EQ };
			Object[] val = { value };
			count =  this.countByConditions(column, ope, val);
		} else {
			String[] column = { col , "cdrPropId" };
			Operator[] ope = { Operator.EQ, Operator.NOTEQ };
			Object[] val = { value , cdrProp.getCdrPropId() };
			count =  this.countByConditions(column, ope, val);
		}
		
		if(count > 0){
			result = true;
		}
		
		return result;
	}

	public Boolean deleteCdrProp(CdrProp item) {
		if(!checkInUsedCdrProp(item.getCdrPropId())){
			this.delete(item);
			return true;
		}
		
		return false;
	}
	
	public Boolean checkInUsedCdrProp(Long cpID){
		boolean result = false;
		List<CdrTemplateProp> csLst = new ArrayList<>();
		Session session = HibernateUtil.getOpenSession();
		try {
			String hql = " FROM CdrTemplateProp cps WHERE cps.cdrPropId = :cdrPropId AND cps.domainId=:domainId";
			Query query = session.createQuery(hql);
			query.setParameter("cdrPropId", cpID);
			query.setParameter("domainId", getDomainId());
			csLst = query.getResultList();
			
			if(csLst.size() > 0){
				
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

	@Override
	protected Class<CdrProp> getEntityClass() {
		return CdrProp.class;
	}
	
	public int countCdrProp() {
		String hql = "SELECT COUNT(a) FROM CdrProp a WHERE  a.domainId =:domainId";
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

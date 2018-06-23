package com.viettel.ocs.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.entity.SysGroup;
import com.viettel.ocs.entity.SysRole;
import com.viettel.ocs.entity.SysUser;
import com.viettel.ocs.entity.SysUserGroupMap;
import com.viettel.ocs.util.CommonUtil;

public class SysGroupDAO extends BaseDAO<SysGroup> {

	@Override
	protected Class<SysGroup> getEntityClass() {
		return SysGroup.class;
	}

	@Override
	public List<SysGroup> findAll(String orders) { 
		
		return super.findAllWithoutDomain(orders);
	}
	
	public List<SysGroup> findByRole(Long roleId) {
		
		Session session = HibernateUtil.getOpenSession();
		try {
			
			String hql = "SELECT a FROM SysGroup a";
			hql += " JOIN SysRoleGroupMap b ON a.id = b.groupId";
			hql += " AND b.roleId = ?";
			Query<SysGroup> query = session.createQuery(hql);
			query.setParameter(0, roleId);
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}
	
	public List<SysGroup> findByUser(Long userId, boolean isActive) {
		
		Session session = HibernateUtil.getOpenSession();
		try {
			
			String hql = "SELECT a FROM SysGroup a";
			hql += " JOIN SysUserGroupMap b ON a.id = b.groupId";
			hql += " AND b.userId = ?";
			if(isActive) {
				hql += " AND a.isActive = 1";
			}
			
			Query<SysGroup> query = session.createQuery(hql);
			query.setParameter(0, userId);
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	/***
	 * 
	 * @param name
	 * @param notId: < 0: bo qua; >=0: tim khac id truyen vao
	 * @return
	 */
	public SysGroup findByName(String name, long notId) {
		
		if(CommonUtil.isEmpty(name))
			return null;
		
		List<Object> params = new ArrayList<Object>();
		String hql = "SELECT a FROM SysGroup a WHERE a.name LIKE ?";
		params.add(name.toLowerCase());
		if(notId >= 0) {
			hql += " AND a.id <> ?";
			params.add(notId);
		}
		
		List<SysGroup> lst;
		Session session = HibernateUtil.getOpenSession();
		try {
			
			Query<SysGroup> query = session.createQuery(hql);
			int i = 0;
			for(Object obj: params) {
				query.setParameter(i, obj);
				i++;
			}

			lst = query.getResultList();
			if(lst.size() > 0)
				return lst.get(0);			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
		
		return null;
	}
	
	public void saveOrUpdate(SysGroup group, List<SysUser> lstUser) {
		
		if(group == null)
			return;
		
		Session session = HibernateUtil.getOpenSession();
		try {

			session.beginTransaction();
			session.saveOrUpdate(group);
			String hqlDelMap = "DELETE FROM SysUserGroupMap WHERE groupId = ?";
			Query queryDel = session.createQuery(hqlDelMap);
			queryDel.setParameter(0, group.getId());
			queryDel.executeUpdate();

			if(lstUser != null && lstUser.size() > 0) {
				for (SysUser user : lstUser) {

					SysUserGroupMap userGroupMap = new SysUserGroupMap();
					userGroupMap.setUserId(user.getId());
					userGroupMap.setGroupId(group.getId());
					session.save(userGroupMap);
				}	
			}			

			session.getTransaction().commit();
		} catch (Exception e) {
			
			throw e;
		} finally {
			session.close();
		}
	}
}

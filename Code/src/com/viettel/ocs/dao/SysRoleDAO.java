package com.viettel.ocs.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.entity.SysGroup;
import com.viettel.ocs.entity.SysMenu;
import com.viettel.ocs.entity.SysRole;
import com.viettel.ocs.entity.SysRoleGroupMap;
import com.viettel.ocs.entity.SysRoleMenuMap;
import com.viettel.ocs.entity.SysUser;
import com.viettel.ocs.entity.SysUserGroupMap;
import com.viettel.ocs.entity.SysUserRoleMap;
import com.viettel.ocs.util.CommonUtil;

public class SysRoleDAO extends BaseDAO<SysRole> {

	@Override
	protected Class<SysRole> getEntityClass() {
		return SysRole.class;
	}

	@Override
	public List<SysRole> findAll(String orders) { 
		
		return super.findAllWithoutDomain(orders);
	}
	
	/***
	 * 
	 * @param name
	 * @param notId: < 0: bo qua; >=0: tim khac id truyen vao
	 * @return
	 */
	public SysRole findByName(String name, long notId) {
		
		if(CommonUtil.isEmpty(name))
			return null;
		
		List<Object> params = new ArrayList<Object>();
		String hql = "SELECT a FROM SysRole a WHERE a.name LIKE ?";
		params.add(name.toLowerCase());
		if(notId >= 0) {
			hql += " AND a.id <> ?";
			params.add(notId);
		}
		
		List<SysRole> lst;
		Session session = HibernateUtil.getOpenSession();
		try {
			
			Query<SysRole> query = session.createQuery(hql);
			int i = 0;
			for(Object obj: params) {
				query.setParameter(i, obj);
				i++;
			}

			lst = query.getResultList();
			if(lst.size() > 0)
				return lst.get(0);			
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
		
		return null;
	}
	
	public List<SysRole> findByUser(Long userId, boolean isActive) {
		
		Session session = HibernateUtil.getOpenSession();
		try {
			
			String hql = "SELECT a FROM SysRole a";
			hql += " JOIN SysUserRoleMap b ON a.id = b.roleId";
			hql += " AND b.userId = :userId";
			if(isActive) {
				hql += " AND a.isActive = 1";
			}
			
			Query<SysRole> query = session.createQuery(hql);
			query.setParameter("userId", userId);
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}
	
	public List<SysRole> findByGroup(Long groupId, boolean isActive) {
		
		Session session = HibernateUtil.getOpenSession();
		try {
			
			String hql = "SELECT a FROM SysRole a";
			hql += " JOIN SysRoleGroupMap b ON a.id = b.roleId";
			hql += " AND b.groupId = :groupId";
			if(isActive) {
				hql += " AND a.isActive = 1";
			}
			
			Query<SysRole> query = session.createQuery(hql);
			query.setParameter("groupId", groupId);
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}
	
	public List<SysRole> findByListGroup(List<Long> lstGroupId, boolean isActive) {
		
		if(lstGroupId.isEmpty())
			return new ArrayList<SysRole>();
		
		Session session = HibernateUtil.getOpenSession();
		try {
			
			String hql = "SELECT a FROM SysRole a";
			hql += " JOIN SysRoleGroupMap b ON a.id = b.roleId";
			hql += " AND b.groupId IN (:groupId)";
			if(isActive) {
				hql += " AND a.isActive = 1";
			}
			
			Query<SysRole> query = session.createQuery(hql);
			query.setParameter("groupId", lstGroupId);
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}
	
	public void saveOrUpdate(SysRole role, List<SysUser> lstUser, List<SysGroup> lstGroup, List<SysMenu> lstMenu) {
		
		if(role == null)
			return;
		
		Session session = HibernateUtil.getOpenSession();
		try {

			session.beginTransaction();
			session.saveOrUpdate(role);
			String hqlDelMap = "DELETE FROM SysUserRoleMap WHERE roleId = :roleId";
			Query queryDel = session.createQuery(hqlDelMap);
			queryDel.setParameter("roleId", role.getId());
			queryDel.executeUpdate();
			
			hqlDelMap = "DELETE FROM SysRoleGroupMap WHERE roleId = :roleId";
			queryDel = session.createQuery(hqlDelMap);
			queryDel.setParameter("roleId", role.getId());
			queryDel.executeUpdate();
			
			hqlDelMap = "DELETE FROM SysRoleMenuMap WHERE roleId = :roleId";
			queryDel = session.createQuery(hqlDelMap);
			queryDel.setParameter("roleId", role.getId());
			queryDel.executeUpdate();
			
			if(lstUser != null && lstUser.size() > 0) {
				for (SysUser user : lstUser) {

					SysUserRoleMap userRoleMap = new SysUserRoleMap();
					userRoleMap.setUserId(user.getId());
					userRoleMap.setRoleId(role.getId());
					session.save(userRoleMap);
				}	
			}

			if(lstGroup != null && lstGroup.size() > 0) {
				for (SysGroup group : lstGroup) {

					SysRoleGroupMap roleGroupMap = new SysRoleGroupMap();
					roleGroupMap.setGroupId(group.getId());
					roleGroupMap.setRoleId(role.getId());
					session.save(roleGroupMap);
				}	
			}
			
			if(lstMenu != null && lstMenu.size() > 0) {
				for (SysMenu menu : lstMenu) {

					SysRoleMenuMap roleMenuMap = new SysRoleMenuMap();
					roleMenuMap.setMenuId(menu.getId());
					roleMenuMap.setRoleId(role.getId());
					session.save(roleMenuMap);
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
}

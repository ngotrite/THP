package com.viettel.ocs.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.SysDomain;
import com.viettel.ocs.entity.SysGroup;
import com.viettel.ocs.entity.SysMenu;
import com.viettel.ocs.entity.SysRole;
import com.viettel.ocs.entity.SysUser;
import com.viettel.ocs.util.CommonUtil;

public class SysUserDAO extends BaseDAO<SysUser> {

	@Override
	protected Class<SysUser> getEntityClass() {
		return SysUser.class;
	}
	
	@Override
	public List<SysUser> findAll(String orders) { 
		
		return super.findAllWithoutDomain(orders);
	}
	
	public List<SysUser> findByGroup(Long groupId) {
		
		Session session = HibernateUtil.getOpenSession();
		try {
			
			String hql = "SELECT a FROM SysUser a";
			hql += " JOIN SysUserGroupMap b ON a.id = b.userId";
			hql += " AND b.groupId = :groupId";
			Query<SysUser> query = session.createQuery(hql);
			query.setParameter("groupId", groupId);
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}
	
	public List<SysUser> findByRole(Long roleId) {
		
		Session session = HibernateUtil.getOpenSession();
		try {
			
			String hql = "SELECT a FROM SysUser a";
			hql += " JOIN SysUserRoleMap b ON a.id = b.userId";
			hql += " AND b.roleId = :roldId";
			Query<SysUser> query = session.createQuery(hql);
			query.setParameter("roldId", roleId);
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}
	
	public SysUser login(String userName, String password) {
		
		if(CommonUtil.isEmpty(userName) || CommonUtil.isEmpty(password))
			return null;
		
		List<SysUser> lst;
		Session session = HibernateUtil.getOpenSession();
		try {
		
			String hql = "SELECT a FROM SysUser a WHERE lower(a.userName) LIKE :userName AND password LIKE :password";
			Query<SysUser> query = session.createQuery(hql);
			query.setParameter("userName", userName.toLowerCase());
			query.setParameter("password", password);

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
	
	public List<SysMenu> findAllMenuOfUser(Long userId) {
		
		List<SysMenu> lstAllMenu = new ArrayList<SysMenu>();
		
		SysGroupDAO groupDao = new SysGroupDAO();
		SysRoleDAO roleDao = new SysRoleDAO();
		SysMenuDAO menuDao = new SysMenuDAO();		
		
		List<SysGroup> lstGroup = groupDao.findByUser(userId, true);
		List<Long> lstGroupId = new ArrayList<>();
		for(SysGroup group: lstGroup) {			
			lstGroupId.add(group.getId());
		}
		
		List<SysRole> lstRoleOfUser = roleDao.findByUser(userId, true);
		List<SysRole> lstRoleOfGroup = roleDao.findByListGroup(lstGroupId, true);
		List<Long> lstRoleId = new ArrayList<>();
		for(SysRole role: lstRoleOfUser) {
			lstRoleId.add(role.getId());
		}
		for(SysRole role: lstRoleOfGroup) {
			lstRoleId.add(role.getId());
		}
		
		lstAllMenu = menuDao.findByListRole(lstRoleId, true);
//		List<SysMenu> lstRootMenu = new ArrayList<SysMenu>();
//		for(SysMenu menu : lstAllMenu) {
//			
//			if(menu.getParentId() == null) 
//				lstRootMenu.add(menu);
//		}
		
		return lstAllMenu;
	}
	
	/***
	 * 
	 * @param userName
	 * @param notId: < 0: bo qua; >=0: tim khac id truyen vao
	 * @return
	 */
	public SysUser findByUserName(String userName, long notId) {
		
		if(CommonUtil.isEmpty(userName))
			return null;
		
		List<Object> params = new ArrayList<Object>();
		String hql = "SELECT a FROM SysUser a WHERE lower(a.userName) LIKE ?";
		params.add(userName.toLowerCase());
		if(notId >= 0) {
			hql += " AND a.id <> ?";
			params.add(notId);
		}
		
		List<SysUser> lst;
		Session session = HibernateUtil.getOpenSession();
		try {
			
			Query<SysUser> query = session.createQuery(hql);
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
}

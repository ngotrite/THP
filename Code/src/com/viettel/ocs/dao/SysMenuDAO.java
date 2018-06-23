package com.viettel.ocs.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.SysMenu;
import com.viettel.ocs.entity.SysMenuTrl;
import com.viettel.ocs.util.CommonUtil;

public class SysMenuDAO extends BaseDAO<SysMenu> {

	@Override
	protected Class<SysMenu> getEntityClass() {
		return SysMenu.class;
	}

	public List<SysMenu> findAll(boolean padding) {
		
		Session session = HibernateUtil.getOpenSession();
		try {
			
			Query<SysMenu> query = session.createQuery("SELECT a FROM SysMenu a ORDER BY a.posIndex");
			List<SysMenu> lst = query.getResultList();
			List<SysMenu> lstReturn = new ArrayList<>();
			sortTree(null, lst, lstReturn);
			
			if(padding) {				
				paddingMenu(lstReturn);
			}
			
			return lstReturn;
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	private void paddingMenu(List<SysMenu> lstReturn) {
		for(SysMenu menu : lstReturn) {
			
			int level = StringUtils.countMatches(menu.getPath(), "/");
			String name = StringUtils.repeat("--", level - 1) + " " + menu.getName();
			menu.setName(name);
		}
	}	

	public List<SysMenu> findByRole(Long roleId, boolean isActive, boolean padding) {
		
		Session session = HibernateUtil.getOpenSession();
		try {
			
			String hql = "SELECT DISTINCT a FROM SysMenu a";
			hql += " JOIN SysRoleMenuMap b ON a.id = b.menuId";
			hql += " AND b.roleId = :roleId";
			if(isActive) {
				hql += " AND a.isActive = 1";
			}
			hql += " ORDER BY a.posIndex";
			
			Query<SysMenu> query = session.createQuery(hql);
			query.setParameter("roleId", roleId);
			List<SysMenu> lst = query.getResultList();			
			List<SysMenu> lstReturn = new ArrayList<SysMenu>();			
			sortTree(null, lst, lstReturn);
			
			if(padding) {				
				paddingMenu(lstReturn);
			}
			
			return lstReturn;			
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}
	
	public List<SysMenu> findByListRole(List<Long> lstRoleId, boolean isActive) {
		
		if(lstRoleId.isEmpty())
			return new ArrayList<SysMenu>();
		
		Session session = HibernateUtil.getOpenSession();
		try {
			
			String hql = "SELECT DISTINCT a FROM SysMenu a";
			hql += " JOIN SysRoleMenuMap b ON a.id = b.menuId";
			hql += " AND b.roleId IN (:roleId)";
			if(isActive) {
				hql += " AND a.isActive = 1";
			}
			hql += " ORDER BY a.posIndex";
			
			Query<SysMenu> query = session.createQuery(hql);
			query.setParameter("roleId", lstRoleId);			
			List<SysMenu> lst = query.getResultList();
			List<SysMenu> lstReturn = new ArrayList<SysMenu>();			
			sortTree(null, lst, lstReturn);
			return lstReturn;
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
	public SysMenu findByName(String name, long notId) {
		
		if(CommonUtil.isEmpty(name))
			return null;
		
		List<Object> params = new ArrayList<Object>();
		String hql = "SELECT a FROM SysMenu a WHERE LOWER(a.name) LIKE ?";
		params.add(name.toLowerCase());
		if(notId >= 0) {
			hql += " AND a.id <> ?";
			params.add(notId);
		}
		
		List<SysMenu> lst;
		Session session = HibernateUtil.getOpenSession();
		try {
			
			Query<SysMenu> query = session.createQuery(hql);
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
	
	public List<SysMenu> findNotEqualForSelectbox(long menuId) {
		
		String[] cols = {"id" };
		Operator[] operators = {Operator.NOTEQ };
		Object[] values = {menuId };
		List<SysMenu> lst = findByConditionsWithoutDomain(cols, operators, values, "posIndex");
		
		paddingMenu(lst);
		
		List<SysMenu> lstReturn = new ArrayList<SysMenu>();
		sortTree(null, lst, lstReturn);
		return lstReturn;
	}
	
	public void sortTree(SysMenu objParent, List<SysMenu> lstSource, List<SysMenu> lstDest) {
			
		if(objParent == null) {
			for(SysMenu menu : lstSource) {				
				if(menu.getParentId() == null) {
					
					lstDest.add(menu);
					sortTree(menu, lstSource, lstDest);
				}
			}	
		} else {
			
			for(SysMenu menu : lstSource) {				
				if(menu.getParentId() != null && menu.getParentId().longValue() == objParent.getId()) {
					
					if(objParent.getSubMenus() == null) {
						objParent.setSubMenus(new ArrayList<SysMenu>());
					}
					objParent.getSubMenus().add(menu);
					lstDest.add(menu);
					sortTree(menu, lstSource, lstDest);
				}
			}
		}		
	}
	
	public List<SysMenu> findChildren(long menuId) {
		
		String[] cols = {"parentId" };
		Operator[] operators = {Operator.EQ };
		Object[] values = {menuId};
		
		if(menuId <= 0) {
			cols[0] = "parentId";
			operators[0] = Operator.NULL;
			values[0] = null;
		}
		
		return findByConditionsWithoutDomain(cols, operators, values, "posIndex");
	}
			
	public List<SysMenu> findChildrenAll(long menuId) {
		
		String[] cols = {"path" };
		Operator[] operators = {Operator.LIKE };
		Object[] values = {"%/" + menuId + "/%"};
				
		return findByConditionsWithoutDomain(cols, operators, values, "posIndex");
	}
	
	public void findParentAll(SysMenu menu, List<SysMenu> lstParent) {
		
		if(lstParent == null || menu.getParentId() == null)
			return;
		
		SysMenu parent = super.get(menu.getParentId());
		lstParent.add(parent);
		findParentAll(parent, lstParent);
	}
	
	public SysMenu findByUrl(String url) {
		
		String[] cols = {"url" };
		Operator[] operators = {Operator.EQ};
		Object[] values = {url};
				
		List<SysMenu> lst = findByConditionsWithoutDomain(cols, operators, values, "posIndex");
		if(lst != null && lst.size() > 0)
			return lst.get(0);
		
		return null;
	}
	
	public String getNameTrl(SysMenu menu, String langCode) {
		
		List<SysMenuTrl> lstTrl = this.findTranslations(menu.getId());
		for(SysMenuTrl trl: lstTrl) {
			if(langCode.equals(trl.getLangCode()))
				return trl.getName();
		}
		
		return menu.getName();
	}
	
	public List<SysMenuTrl> findTranslations(long menuId) {
		
		String[] cols = {"menuId" };
		Operator[] operators = {Operator.EQ };
		Object[] values = {menuId};
				
		return findByConditionsWithoutDomain(SysMenuTrl.class, cols, operators, values, "");
	}
	
	public List<SysMenuTrl> findTranslationsByLang(String langCode) {
		
		String[] cols = {"langCode" };
		Operator[] operators = {Operator.EQ };
		Object[] values = {langCode};
		
		return findByConditionsWithoutDomain(SysMenuTrl.class, cols, operators, values, "");
	}
	
	public void saveOrUpdate(SysMenu menu, List<SysMenuTrl> lstTranslation) {
		
		Session session = HibernateUtil.getOpenSession();
		try {
			
			String oldPath = menu.getPath();
			if(menu.getParentId() == null || menu.getParentId() == 0) {
				
				menu.setParentId(null);
				menu.setPath("/");
			} else {
				
				SysMenu parentMenu = super.get(menu.getParentId());
				String parentPath = parentMenu.getPath() == null? "" : parentMenu.getPath();
				menu.setPath(parentPath + menu.getParentId() + "/");		
			}
			
			session.beginTransaction();
						
			if(menu.getId() > 0) {
				
				session.update(menu);
				
				// update path for children
				oldPath += menu.getId() + "/";
				String hqlUpdateChildren = "UPDATE SysMenu SET path = REPLACE(path, ?, ?) WHERE path LIKE ?";
				Query query = session.createQuery(hqlUpdateChildren);
				query.setParameter(0, oldPath);
				query.setParameter(1, menu.getPath() + menu.getId() + "/");
				query.setParameter(2, oldPath + "%");
				query.executeUpdate();	
			} else {
				
				long maxPosIndex = super.getMax("posIndex");
				menu.setPosIndex(maxPosIndex + 1);
				session.save(menu);
			}
			
			List<SysMenuTrl> lstTrlDB = this.findTranslations(menu.getId());
			if(lstTranslation == null) {
				
				for(SysMenuTrl objDB : lstTrlDB)
					session.delete(objDB);
			} else {
				
				List<SysMenuTrl> lst2Delete = new ArrayList<>();
				for(SysMenuTrl objDB : lstTrlDB) {					
					
					boolean found = false;
					for(SysMenuTrl trl: lstTranslation) {						
						if(trl.getId() == objDB.getId()) {
							found = true;
							break;
						}							
					}
					
					if(!found)
						lst2Delete.add(objDB);
				}
				
				for(SysMenuTrl objDB : lst2Delete)
					session.delete(objDB);
					
				for(SysMenuTrl trl: lstTranslation) {
					trl.setMenuId(menu.getId());
					session.saveOrUpdate(trl);
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


	public boolean moveUpDown(boolean moveUp, SysMenu menu) {
		
		Long currentIdx = menu.getPosIndex();
		if(currentIdx == null)
			currentIdx = 0L;
		
		String[] cols = {"id", "parentId", "posIndex"};
		Operator[] ops = {Operator.NOTEQ, Operator.EQ, Operator.LE};
		Object[] vals = {menu.getId(), menu.getParentId(), currentIdx};
		String orderBy = "posIndex DESC";
				
		if(menu.getParentId() == null) {
			ops[1] = Operator.NULL;
			vals[1] = null;
		}
		
		if(!moveUp) {
			
			ops[2] = Operator.GE;
			vals[2] = currentIdx;
			orderBy = "posIndex ASC";
		}
		
		List<SysMenu> lst = super.findByConditionsWithoutDomain(cols, ops, vals, orderBy);
		if(lst.size() > 0) {
			
			Session session = HibernateUtil.getOpenSession();
			try {
				
				session.beginTransaction();
				
				SysMenu menuSwitch = lst.get(0);										
				menu.setPosIndex(menuSwitch.getPosIndex());
				menuSwitch.setPosIndex(currentIdx);				
				session.update(menu);
				session.update(menuSwitch);
								
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
	 * @param idCheck
	 * @return
	 */
	public boolean isRecursive(Long idCheck) {
		
		return isContainInTree(idCheck, idCheck);
	}
	
	/***
	 * Kiem tra cac node con trong cay menu co chua menu can check hay khong 
	 * @param idCheck - Id kiem tra
	 * @param rootCatId - Id root trong cay muon kiem tra
	 * @return true: cay ok; false: cay bi lap vong tron
	 */
	public boolean isContainInTree(Long idCheck, Long rootCatId) {
		
		if(rootCatId == null)
			return false;
		
		SysMenu objCheck = this.get(idCheck);
		if(objCheck == null || objCheck.getPath() == null)
			return false;
		
		return objCheck.getPath().contains("/" + rootCatId + "/");
	}
}

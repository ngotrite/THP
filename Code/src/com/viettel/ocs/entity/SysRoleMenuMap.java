package com.viettel.ocs.entity;
// Generated Sep 2, 2016 4:54:08 PM by Hibernate Tools 3.2.1.GA


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="sys_role_menu_map"
)
public class SysRoleMenuMap extends BaseEntity  implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4357105757422223275L;

	@Override
	@Transient
	public String getNodeName() {
		return null;
	}
	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
     private long id;
     @Column(name="role_id")
     private long roleId;
     @Column(name="menu_id")
     private long menuId;

    public SysRoleMenuMap() {
    }

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getRoleId() {
		return roleId;
	}

	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}

	public long getMenuId() {
		return menuId;
	}

	public void setMenuId(long menuId) {
		this.menuId = menuId;
	}
}



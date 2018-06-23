package com.viettel.ocs.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "nested_object")
public class NestedObject extends BaseEntity implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@Basic(optional = false)
	@Column(name = "OBJ_ID")
	private Long objId;
	@Basic(optional = false)
	@Column(name = "OBJ_NAME")
	private String objName;
	@Basic(optional = false)
	@Column(name = "OBJ_CLASS_ID")
	private long objClassId;
	@Basic(optional = false)
	@Column(name = "IS_ALIST")
	private long isAlist;
	@Basic(optional = false)
	@Column(name = "PARENT_CLASS_ID")
	private long parentClassId;

	public NestedObject() {
	}

	public NestedObject(Long objId) {
		this.objId = objId;
	}

	public NestedObject(Long objId, String objName, long objClassId, long isAlist, long parentClassId) {
		this.objId = objId;
		this.objName = objName;
		this.objClassId = objClassId;
		this.isAlist = isAlist;
		this.parentClassId = parentClassId;
	}

	public Long getObjId() {
		return objId;
	}

	public void setObjId(Long objId) {
		this.objId = objId;
	}

	public String getObjName() {
		return objName;
	}

	public void setObjName(String objName) {
		this.objName = objName;
	}

	public long getObjClassId() {
		return objClassId;
	}

	public void setObjClassId(long objClassId) {
		this.objClassId = objClassId;
	}

	public long getIsAlist() {
		return isAlist;
	}

	public void setIsAlist(long isAlist) {
		this.isAlist = isAlist;
	}

	public long getParentClassId() {
		return parentClassId;
	}

	public void setParentClassId(long parentClassId) {
		this.parentClassId = parentClassId;
	}

	@Override
	@Transient
	public String getNodeName() {
		return objName;
	}

}

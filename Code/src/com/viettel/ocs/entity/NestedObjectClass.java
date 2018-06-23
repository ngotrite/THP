package com.viettel.ocs.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
@Entity
@Table(name = "nested_object_class")
public class NestedObjectClass extends BaseEntity implements java.io.Serializable {

	 private static final long serialVersionUID = 1L;
	    @Id
	    @Basic(optional = false)
	    @Column(name = "OBJ_CLASS_ID")
	    private Long objClassId;
	    @Basic(optional = false)
	    @Column(name = "OBJ_CLASS_NAME")
	    private String objClassName;

	    public NestedObjectClass() {
	    }

	    public NestedObjectClass(Long objClassId) {
	        this.objClassId = objClassId;
	    }

	    public NestedObjectClass(Long objClassId, String objClassName) {
	        this.objClassId = objClassId;
	        this.objClassName = objClassName;
	    }

	    public Long getObjClassId() {
	        return objClassId;
	    }

	    public void setObjClassId(Long objClassId) {
	        this.objClassId = objClassId;
	    }

	    public String getObjClassName() {
	        return objClassName;
	    }

	    public void setObjClassName(String objClassName) {
	        this.objClassName = objClassName;
	    }

		@Override
		public String getNodeName() {
			return objClassName;
		}

}

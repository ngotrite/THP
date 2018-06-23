package com.viettel.ocs.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
@Entity
@Table(name = "object_field")
public class ObjectField extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy =IDENTITY)
	@Column(name = "OBJECT_FIELD_ID")
	private long objectFieldId;
	@Column(name = "OBJECT_FIELD_NAME")
	private String objectFieldName;
	@Column(name = "IS_LIST")
	private long isList;
	@Column(name = "OBJECT_FIELD_PARENT")
	private long objectFieldParent;
	public ObjectField() {
	}

	public ObjectField(Long objectFieldId) {
		this.objectFieldId = objectFieldId;
	}

	public ObjectField(Long objectFieldId, String objectFieldName, long isList) {
		this.objectFieldId = objectFieldId;
		this.objectFieldName = objectFieldName;
		this.isList = isList;
	}

	public Long getObjectFieldId() {
		return objectFieldId;
	}

	public void setObjectFieldId(Long objectFieldId) {
		this.objectFieldId = objectFieldId;
	}

	public String getObjectFieldName() {
		return objectFieldName;
	}

	public void setObjectFieldName(String objectFieldName) {
		this.objectFieldName = objectFieldName;
	}

	public long getIsList() {
		return isList;
	}

	public void setIsList(long isList) {
		this.isList = isList;
	}

	public long getObjectFieldParent() {
		return objectFieldParent;
	}

	public void setObjectFieldParent(long objectFieldParent) {
		this.objectFieldParent = objectFieldParent;
	}

	@Override
	@Transient
	public String getNodeName() {
		return null;
	}
}

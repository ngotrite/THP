package com.viettel.ocs.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "AVP_DATA_TYPE")
public class AVPDataType extends BaseEntity implements Serializable{
	@Override
	@Transient
	public String getNodeName() {
		return null;
	}
	
	private long avpDataTypeId;
	private String avpDataTypeName;
	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "AVP_DATA_TYPE_ID", unique = true, nullable = false)
	public long getAvpDataTypeId() {
		return avpDataTypeId;
	}
	public void setAvpDataTypeId(long avpDataTypeId) {
		this.avpDataTypeId = avpDataTypeId;
	}
	
	@Column(name = "AVP_DATA_TYPE_NAME")
	public String getAvpDataTypeName() {
		return avpDataTypeName;
	}
	public void setAvpDataTypeName(String avpDataTypeName) {
		this.avpDataTypeName = avpDataTypeName;
	}
	
	
}

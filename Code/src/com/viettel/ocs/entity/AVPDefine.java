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
@Table(name = "AVP_DEFINE")
public class AVPDefine extends BaseEntity implements Serializable{
	@Override
	@Transient
	public String getNodeName() {
		return null;
	}

	private long avpDefineId;
	private String avpDefineCode;
	private String avpDefineName;
	private Long avpDataTypeId;
	private Long _interface;
	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "AVP_DEFINE_ID", unique = true, nullable = false)
	public long getAvpDefineId() {
		return avpDefineId;
	}
	public void setAvpDefineId(long avpDefineId) {
		this.avpDefineId = avpDefineId;
	}
	
	@Column(name = "AVP_DEFINE_CODE")
	public String getAvpDefineCode() {
		return avpDefineCode;
	}
	public void setAvpDefineCode(String avpDefineCode) {
		this.avpDefineCode = avpDefineCode;
	}
	
	@Column(name = "AVP_DEFINE_NAME")
	public String getAvpDefineName() {
		return avpDefineName;
	}
	public void setAvpDefineName(String avpDefineName) {
		this.avpDefineName = avpDefineName;
	}
	
	@Column(name = "AVP_DATA_TYPE_ID")
	public Long getAvpDataTypeId() {
		return avpDataTypeId;
	}
	public void setAvpDataTypeId(Long avpDataTypeId) {
		this.avpDataTypeId = avpDataTypeId;
	}
	
	@Column(name = "INTERFACE")
	public Long get_interface() {
		return _interface;
	}
	public void set_interface(Long _interface) {
		this._interface = _interface;
	}
	
	
}

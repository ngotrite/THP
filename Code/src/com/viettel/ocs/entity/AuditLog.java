package com.viettel.ocs.entity;

import static javax.persistence.GenerationType.IDENTITY;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
@Table(name = "auditlog")
public class AuditLog extends BaseEntity implements java.io.Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1853394949924963194L;
	private Long auditLogId;
	private String userName;
	private String action;
	private String oldDetail;
	private String newDetail;
	private Timestamp createDate;
	private Long entityId;
	private String entityName;
	private String ip;
	private Long domainId;

	public AuditLog() {
	}

	public AuditLog(String userName, String action, String oldDetail, String newDetail, Timestamp createDate, Long entityId,
			String entityName, String ip, Long domainId) {
		super();
		this.userName = userName;
		this.action = action;
		this.oldDetail = oldDetail;
		this.newDetail = newDetail;
		this.createDate = createDate;
		this.entityId = entityId;
		this.entityName = entityName;
		this.ip = ip;
		this.domainId = domainId;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "AUDIT_LOG_ID", unique = true, nullable = false)
	public Long getAuditLogId() {
		return auditLogId;
	}

	public void setAuditLogId(Long auditLogId) {
		this.auditLogId = auditLogId;
	}

	@Column(name = "USER_NAME", length = 100)
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Column(name = "ACTION", length = 100)
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	@Column(name = "OLD_DETAIL", nullable = false, length = 65535)
	public String getOldDetail() {
		return oldDetail;
	}

	public void setOldDetail(String oldDetail) {
		this.oldDetail = oldDetail;
	}

	@Column(name = "NEW_DETAIL", nullable = false, length = 65535)
	public String getNewDetail() {
		return newDetail;
	}

	public void setNewDetail(String newDetail) {
		this.newDetail = newDetail;
	}

	@Column(name = "CREATED_DATE", nullable = false)
	public Timestamp getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}

	@Column(name = "ENTITY_ID", nullable = false)
	public Long getEntityId() {
		return entityId;
	}

	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}

	@Column(name = "ENTITY_NAME", length = 255)
	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	@Column(name = "IP", length = 255)
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	@Column(name = "DOMAIN_ID")
	public Long getDomainId() {
		return domainId;
	}

	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}

	@Transient
	@Override
	public String getNodeName() {
		// TODO Auto-generated method stub
		return null;
	}

}

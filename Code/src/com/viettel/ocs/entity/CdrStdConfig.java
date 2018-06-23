package com.viettel.ocs.entity;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@SuppressWarnings("serial")
@Entity
@Table(name = "cdr_std_config")
public class CdrStdConfig extends BaseEntity implements Serializable {
	@Transient
	public String getNodeName() {
		return null;
	}

//	@Column(name = "DOMAIN_ID")
//	private Long domainId;
//
//	public Long getDomainId() {
//		return domainId;
//	}
//
//	public void setDomainId(Long domainId) {
//		this.domainId = domainId;
//	}

	private long cdrStdConfigId;
	private String sqlCommand;
	private Long cdrServiceId;
	private Long cdrTemplatedId;
	private Long maxSize;
	private Long priority;
	private String cdrScript;
	private Long sourceType;
	private String cdrStdConfigName;
	
	public CdrStdConfig() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public CdrStdConfig(long cdrStdConfigId, String sqlCommand, Long cdrServiceId, Long cdrTemplatedId,
			Long maxSize, Long priority, String cdrScript, Long sourceType, String cdrStdConfigName) {
		super();
//		this.domainId = domainId;
		this.cdrStdConfigId = cdrStdConfigId;
		this.sqlCommand = sqlCommand;
		this.cdrServiceId = cdrServiceId;
		this.cdrTemplatedId = cdrTemplatedId;
		this.maxSize = maxSize;
		this.priority = priority;
		this.cdrScript = cdrScript;
		this.sourceType = sourceType;
		this.cdrStdConfigName = cdrStdConfigName;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "CDR_STD_CONFIG_ID", unique = true, nullable = false)
	public long getCdrStdConfigId() {
		return cdrStdConfigId;
	}

	public void setCdrStdConfigId(long cdrStdConfigId) {
		this.cdrStdConfigId = cdrStdConfigId;
	}

	@Column(name = "SQL_COMMAND")
	public String getSqlCommand() {
		return sqlCommand;
	}

	public void setSqlCommand(String sqlCommand) {
		this.sqlCommand = sqlCommand;
	}

	@Column(name = "CDR_SERVICE_ID")
	public Long getCdrServiceId() {
		return cdrServiceId;
	}

	public void setCdrServiceId(Long cdrServiceId) {
		this.cdrServiceId = cdrServiceId;
	}

	@Column(name = "CDR_TEMPLATED_ID")
	public Long getCdrTemplatedId() {
		return cdrTemplatedId;
	}

	public void setCdrTemplatedId(Long cdrTemplatedId) {
		this.cdrTemplatedId = cdrTemplatedId;
	}

	@Column(name = "MAX_SIZE")
	public Long getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(Long maxSize) {
		this.maxSize = maxSize;
	}

	@Column(name = "PRIORITY")
	public Long getPriority() {
		return priority;
	}

	public void setPriority(Long priority) {
		this.priority = priority;
	}

	@Column(name = "CDR_SCRIPT")
	public String getCdrScript() {
		return cdrScript;
	}

	public void setCdrScript(String cdrScript) {
		this.cdrScript = cdrScript;
	}

	@Column(name = "SOURCE_TYPE")
	public Long getSourceType() {
		return sourceType;
	}

	public void setSourceType(Long sourceType) {
		this.sourceType = sourceType;
	}

	@Column(name = "CDR_STD_CONFIG_NAME")
	public String getCdrStdConfigName() {
		return cdrStdConfigName;
	}

	public void setCdrStdConfigName(String cdrStdConfigName) {
		this.cdrStdConfigName = cdrStdConfigName;
	}

}

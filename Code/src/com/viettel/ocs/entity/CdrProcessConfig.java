package com.viettel.ocs.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "cdr_process_config")
public class CdrProcessConfig extends BaseEntity implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	@Transient
	public String getNodeName() {
		return this.processName;
	}

	@Column(name = "DOMAIN_ID")
	private Long domainId;

	public Long getDomainId() {
		return domainId;
	}

	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "CDR_PROCESS_CONFIG_ID")
	private long cdrProcessConfigId;
	@Column(name = "PROCESS_NAME")
	private String processName;
	@Column(name = "PROCESS_CODE")
	private String processCode;
	@Column(name = "CDR_DIR")
	private String cdrDir;
	@Column(name = "RULE_SCAN")
	private String ruleScan;
	@Column(name = "BILLING_DIR")
	private String billingDir;
	@Column(name = "BACKUP_DIR")
	private String backupDir;
	@Column(name = "REJECT_DIR")
	private String rejectDir;
	@Column(name = "TYPE")
	private Long type;
	@Column(name = "CDR_PROCESSED_OPT")
	private Long cdrProcessedOpt;
	@Column(name = "TIME_PROCESS")
	private Long timeProcess;
	@Column(name = "MAX_RECS")
	private Long maxRecs;
	@Column(name = "FILENAME_PATTERN_ID")
	private Long filenamePatternId;
	@Column(name = "CDR_SERVICE_ID")
	private Long cdrServiceId;
	@Column(name = "STATUS")
	private Boolean status;
	@Column(name = "FTP_ADDRESS")
	private String ftpAddress;
	@Column(name = "FTP_USER")
	private String ftpUser;
	@Column(name = "FTP_PASS")
	private String ftpPass;
	@Column(name = "KAFKA_SERVER")
	private String kafkaServer;
	@Column(name = "TOPIC")
	private String topic;
	@Column(name = "TOPIC1")
	private String topic1;
	@Column(name = "CATEGORY_ID")
	private Long categoryId;
	@Column(name = "POS_INDEX")
	private Long index;
	@Column(name = "ENABLE_KAFKA")
	private Boolean enableKafka = false;

	public CdrProcessConfig() {
	}

	public CdrProcessConfig(Long cdrProcessConfigId) {
		this.cdrProcessConfigId = cdrProcessConfigId;
	}

	public CdrProcessConfig(Long cdrProcessConfigId, String processName, String processCode, Long filenamePatternId,
			Long cdrServiceId) {
		this.cdrProcessConfigId = cdrProcessConfigId;
		this.processName = processName;
		this.processCode = processCode;
		this.filenamePatternId = filenamePatternId;
		this.cdrServiceId = cdrServiceId;
	}

	public long getCdrProcessConfigId() {
		return cdrProcessConfigId;
	}

	public void setCdrProcessConfigId(long cdrProcessConfigId) {
		this.cdrProcessConfigId = cdrProcessConfigId;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public String getProcessCode() {
		return processCode;
	}

	public void setProcessCode(String processCode) {
		this.processCode = processCode;
	}

	public String getCdrDir() {
		return cdrDir;
	}

	public void setCdrDir(String cdrDir) {
		this.cdrDir = cdrDir;
	}

	public String getRuleScan() {
		return ruleScan;
	}

	public void setRuleScan(String ruleScan) {
		this.ruleScan = ruleScan;
	}

	public String getBillingDir() {
		return billingDir;
	}

	public void setBillingDir(String billingDir) {
		this.billingDir = billingDir;
	}

	public String getBackupDir() {
		return backupDir;
	}

	public void setBackupDir(String backupDir) {
		this.backupDir = backupDir;
	}

	public String getRejectDir() {
		return rejectDir;
	}

	public void setRejectDir(String rejectDir) {
		this.rejectDir = rejectDir;
	}

	public Long getType() {
		return type;
	}

	public void setType(Long type) {
		this.type = type;
	}

	public Long getCdrProcessedOpt() {
		return cdrProcessedOpt;
	}

	public void setCdrProcessedOpt(Long cdrProcessedOpt) {
		this.cdrProcessedOpt = cdrProcessedOpt;
	}

	public Long getTimeProcess() {
		return timeProcess;
	}

	public void setTimeProcess(Long timeProcess) {
		this.timeProcess = timeProcess;
	}

	public Long getMaxRecs() {
		return maxRecs;
	}

	public void setMaxRecs(Long maxRecs) {
		this.maxRecs = maxRecs;
	}

	public Long getFilenamePatternId() {
		return filenamePatternId;
	}

	public void setFilenamePatternId(Long filenamePatternId) {
		this.filenamePatternId = filenamePatternId;
	}

	public Long getCdrServiceId() {
		return cdrServiceId;
	}

	public void setCdrServiceId(Long cdrServiceId) {
		this.cdrServiceId = cdrServiceId;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public String getFtpAddress() {
		return ftpAddress;
	}

	public void setFtpAddress(String ftpAddress) {
		this.ftpAddress = ftpAddress;
	}

	public String getFtpUser() {
		return ftpUser;
	}

	public void setFtpUser(String ftpUser) {
		this.ftpUser = ftpUser;
	}

	public String getFtpPass() {
		return ftpPass;
	}

	public void setFtpPass(String ftpPass) {
		this.ftpPass = ftpPass;
	}

	public String getKafkaServer() {
		return kafkaServer;
	}

	public void setKafkaServer(String kafkaServer) {
		this.kafkaServer = kafkaServer;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getTopic1() {
		return topic1;
	}

	public void setTopic1(String topic1) {
		this.topic1 = topic1;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public Long getIndex() {
		return index;
	}

	public void setIndex(Long index) {
		this.index = index;
	}

	public Boolean getEnableKafka() {
		return enableKafka;
	}

	public void setEnableKafka(Boolean enableKafka) {
		this.enableKafka = enableKafka;
	}

	@Override
	public CdrProcessConfig clone() {
		try {
			return (CdrProcessConfig) super.clone();
		} catch (CloneNotSupportedException e) {
			
			return this;
		}
	}
}

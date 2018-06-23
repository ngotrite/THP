package com.viettel.ocs.entity;

import java.io.Serializable;
import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "cdr_gen_filename")
public class CdrGenFilename extends BaseEntity implements Serializable, Cloneable {

	@Transient
	public String getNodeName() {
		return this.name;
	}

	@Column(name = "DOMAIN_ID")
	private Long domainId;

	public Long getDomainId() {
		return domainId;
	}

	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "CDR_GEN_FILENAME_ID")
	private long cdrGenFilenameId;
	@Column(name = "NAME")
	private String name;
	@Column(name = "PATTERN")
	private String pattern;
	@Column(name = "TEMPLATE_ID")
	private Long templateId;
	@Column(name = "PROCESS_CODE")
	private Long processCode;
	@Column(name = "SERVICE_TYPE")
	private String serviceType;
	@Column(name = "DESCRIPTION")
	private String description;
	@Column(name = "CATEGORY_ID")
	private Long categoryId;
	@Column(name = "POS_INDEX")
	private Long index;

	public CdrGenFilename() {
	}

	public CdrGenFilename(Long cdrGenFilenameId) {
		this.cdrGenFilenameId = cdrGenFilenameId;
	}

	public CdrGenFilename(Long cdrGenFilenameId, String name) {
		this.cdrGenFilenameId = cdrGenFilenameId;
		this.name = name;
	}

	public long getCdrGenFilenameId() {
		return cdrGenFilenameId;
	}

	public void setCdrGenFilenameId(long cdrGenFilenameId) {
		this.cdrGenFilenameId = cdrGenFilenameId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public Long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}

	public Long getProcessCode() {
		return processCode;
	}

	public void setProcessCode(Long processCode) {
		this.processCode = processCode;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	@Override
	public CdrGenFilename clone() {
		try {
			return (CdrGenFilename) super.clone();
		} catch (CloneNotSupportedException e) {
			
			return this;
		}
	}

}

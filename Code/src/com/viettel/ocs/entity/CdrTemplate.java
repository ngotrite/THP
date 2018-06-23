package com.viettel.ocs.entity;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.viettel.ocs.bean.BaseController;

/**
 *
 * @author SOPEN
 */
@Entity
@Table(name = "cdr_template")

public class CdrTemplate extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Transient
	public String getNodeName() {
		return this.templateCode;
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
	@Column(name = "CDR_TEMPLATE_ID")
	private long cdrTemplateId;
	@Column(name = "TEMPLATE_CODE")
	private String templateCode;
	@Column(name = "DESCRIPTION")
	private String description;
	@Column(name = "CDR_SERVICE_ID")
	private Long cdrServiceId;
	@Column(name = "DELIMITER")
	private String delimiter;
	@Column(name = "CATEGORY_ID")
	private Long categoryId;
	@Column(name = "POS_INDEX")
	private Long index;

	public CdrTemplate() {
	}

	public CdrTemplate(Long cdrTemplateId) {
		this.cdrTemplateId = cdrTemplateId;
	}

	public CdrTemplate(Long cdrTemplateId, String templateCode, Long cdrServiceId) {
		this.cdrTemplateId = cdrTemplateId;
		this.templateCode = templateCode;
		this.cdrServiceId = cdrServiceId;
	}

	public long getCdrTemplateId() {
		return cdrTemplateId;
	}

	public void setCdrTemplateId(long cdrTemplateId) {
		this.cdrTemplateId = cdrTemplateId;
	}

	public String getTemplateCode() {
		return templateCode;
	}

	public void setTemplateCode(String templateCode) {
		this.templateCode = templateCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getCdrServiceId() {
		return cdrServiceId;
	}

	public void setCdrServiceId(Long cdrServiceId) {
		this.cdrServiceId = cdrServiceId;
	}

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
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

}

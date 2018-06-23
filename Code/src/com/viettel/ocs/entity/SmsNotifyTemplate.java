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
@Table(name = "sms_notify_template")
public class SmsNotifyTemplate extends BaseEntity implements Serializable, Cloneable {
	
	@Override
	@Transient
	public String getNodeName() {
		return this.name;
	}

	private Long domainId;

	@Column(name = "DOMAIN_ID")
	public Long getDomainId() {
		return domainId;
	}

	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}
	
	private long smsNotifyTemplateId;
	private String messageTemplate;
	private String comments;
	private Long langId;
	private Long smsTemplateCode;
	private String name;
	private Long categoryId;
	private String remark;
	private Long posIndex;
	
	

	public SmsNotifyTemplate() {
		super();
	}

	public SmsNotifyTemplate(Long domainId, long smsNotifyTemplateId, String messageTemplate, String comments,
			Long langId, Long smsTemplateCode, String name, Long categoryId, String remark, Long posIndex) {
		super();
		this.domainId = domainId;
		this.smsNotifyTemplateId = smsNotifyTemplateId;
		this.messageTemplate = messageTemplate;
		this.comments = comments;
		this.langId = langId;
		this.smsTemplateCode = smsTemplateCode;
		this.name = name;
		this.categoryId = categoryId;
		this.remark = remark;
		this.posIndex = posIndex;
	}
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "SMS_NOTIFY_TEMPLATE_ID", unique = true, nullable = false)
	public long getSmsNotifyTemplateId() {
		return smsNotifyTemplateId;
	}

	public void setSmsNotifyTemplateId(long smsNotifyTemplateId) {
		this.smsNotifyTemplateId = smsNotifyTemplateId;
	}

	@Column(name = "MESSAGE_TEMPLATE")
	public String getMessageTemplate() {
		return messageTemplate;
	}

	public void setMessageTemplate(String messageTemplate) {
		this.messageTemplate = messageTemplate;
	}

	@Column(name = "COMMENTS")
	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	@Column(name = "LANG_ID")
	public Long getLangId() {
		return langId;
	}

	public void setLangId(Long langId) {
		this.langId = langId;
	}

	@Column(name = "SMS_TEMPLATE_CODE")
	public Long getSmsTemplateCode() {
		return smsTemplateCode;
	}

	public void setSmsTemplateCode(Long smsTemplateCode) {
		this.smsTemplateCode = smsTemplateCode;
	}

	@Column(name = "NAME")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "CATEGORY_ID")
	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	@Column(name = "REMARK")
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Column(name = "POS_INDEX")
	public Long getPosIndex() {
		return posIndex;
	}

	public void setPosIndex(Long posIndex) {
		this.posIndex = posIndex;
	}
	
	@Override
	public SmsNotifyTemplate clone() throws CloneNotSupportedException {
		return (SmsNotifyTemplate) super.clone();
	}
	

}

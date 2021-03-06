package com.viettel.ocs.entity;
// Generated Sep 2, 2016 4:54:08 PM by Hibernate Tools 3.2.1.GA


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * TriggerFieldFormat generated by hbm2java
 */
@Entity
@Table(name="trigger_field_format"
)
public class TriggerFieldFormat extends BaseEntity  implements java.io.Serializable {

	@Override
	@Transient
	public String getNodeName() {
		return null;
	}
	private Long domainId;
	@Column(name="DOMAIN_ID")
    public Long getDomainId() {
		return domainId;
	}
	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}
     private long formatTemplateId;
     private long langId;
     private String text;
     private Long formatType;

    public TriggerFieldFormat() {
    }

	
    public TriggerFieldFormat(long langId) {
        this.langId = langId;
    }
    public TriggerFieldFormat(long langId, String text, Long formatType) {
       this.langId = langId;
       this.text = text;
       this.formatType = formatType;
    }
   
     @Id @GeneratedValue(strategy=IDENTITY)
    
    @Column(name="FORMAT_TEMPLATE_ID", unique=true, nullable=false)
    public long getFormatTemplateId() {
        return this.formatTemplateId;
    }
    
    public void setFormatTemplateId(long formatTemplateId) {
        this.formatTemplateId = formatTemplateId;
    }
    
    @Column(name="LANG_ID", nullable=false)
    public long getLangId() {
        return this.langId;
    }
    
    public void setLangId(long langId) {
        this.langId = langId;
    }
    
    @Column(name="TEXT", length=65535)
    public String getText() {
        return this.text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    @Column(name="FORMAT_TYPE")
    public Long getFormatType() {
        return this.formatType;
    }
    
    public void setFormatType(Long formatType) {
        this.formatType = formatType;
    }




}



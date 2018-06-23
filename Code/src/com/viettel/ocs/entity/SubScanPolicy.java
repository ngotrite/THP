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
 * SubScanPolicy generated by hbm2java
 */
@Entity
@Table(name="sub_scan_policy"
)
public class SubScanPolicy extends BaseEntity  implements java.io.Serializable {

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
     private long subScanPolicyId;
     private Long offerExternalId;
     private Long suspendDuration;
     private Long disableDuration;
     private Long poolDuration;

    public SubScanPolicy() {
    }

    public SubScanPolicy(Long offerExternalId, Long suspendDuration, Long disableDuration, Long poolDuration) {
       this.offerExternalId = offerExternalId;
       this.suspendDuration = suspendDuration;
       this.disableDuration = disableDuration;
       this.poolDuration = poolDuration;
    }
   
     @Id @GeneratedValue(strategy=IDENTITY)
    
    @Column(name="SUB_SCAN_POLICY_ID", unique=true, nullable=false)
    public long getSubScanPolicyId() {
        return this.subScanPolicyId;
    }
    
    public void setSubScanPolicyId(long subScanPolicyId) {
        this.subScanPolicyId = subScanPolicyId;
    }
    
    @Column(name="OFFER_EXTERNAL_ID")
    public Long getOfferExternalId() {
        return this.offerExternalId;
    }
    
    public void setOfferExternalId(Long offerExternalId) {
        this.offerExternalId = offerExternalId;
    }
    
    @Column(name="SUSPEND_DURATION")
    public Long getSuspendDuration() {
        return this.suspendDuration;
    }
    
    public void setSuspendDuration(Long suspendDuration) {
        this.suspendDuration = suspendDuration;
    }
    
    @Column(name="DISABLE_DURATION")
    public Long getDisableDuration() {
        return this.disableDuration;
    }
    
    public void setDisableDuration(Long disableDuration) {
        this.disableDuration = disableDuration;
    }
    
    @Column(name="POOL_DURATION")
    public Long getPoolDuration() {
        return this.poolDuration;
    }
    
    public void setPoolDuration(Long poolDuration) {
        this.poolDuration = poolDuration;
    }




}



package com.viettel.ocs.entity;
// Generated Sep 2, 2016 4:54:08 PM by Hibernate Tools 3.2.1.GA


import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * SnPayment generated by hbm2java
 */
@Entity
@Table(name="sn_payment"
)
public class SnPayment extends BaseEntity  implements java.io.Serializable {

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
     private long snPaymentId;
     private String sn;
     private Date paymentDate;

    public SnPayment() {
    }

    public SnPayment(String sn, Date paymentDate) {
       this.sn = sn;
       this.paymentDate = paymentDate;
    }
   
     @Id @GeneratedValue(strategy=IDENTITY)
    
    @Column(name="SN_PAYMENT_ID", unique=true, nullable=false)
    public long getSnPaymentId() {
        return this.snPaymentId;
    }
    
    public void setSnPaymentId(long snPaymentId) {
        this.snPaymentId = snPaymentId;
    }
    
    @Column(name="SN", length=120)
    public String getSn() {
        return this.sn;
    }
    
    public void setSn(String sn) {
        this.sn = sn;
    }
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="PAYMENT_DATE", length=19)
    public Date getPaymentDate() {
        return this.paymentDate;
    }
    
    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }




}


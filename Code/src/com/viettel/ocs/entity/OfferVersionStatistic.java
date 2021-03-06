package com.viettel.ocs.entity;
// Generated Sep 2, 2016 4:54:08 PM by Hibernate Tools 3.2.1.GA


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

import java.util.Date;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * OfferVersionStatistic generated by hbm2java
 */
@Entity
@Table(name="offer_version_statistic"
)
public class OfferVersionStatistic extends BaseEntity  implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	@Transient
	public String getNodeName() {
		return null;
	}
	
	@Id
	@Column(name="OFFER_ID")
	private long offerId;
	@Column(name="OFFER_USAGE", nullable=false)
	private long offerUsage;
	@Column(name="UPDATE_TIME", nullable=false)
	private Date updateTime;	
	
	public long getOfferId() {
		return offerId;
	}
	public void setOfferId(long offerId) {
		this.offerId = offerId;
	}
	public long getOfferUsage() {
		return offerUsage;
	}
	public void setOfferUsage(long offerUsage) {
		this.offerUsage = offerUsage;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}	
}



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
 * Event generated by hbm2java
 */
@Entity
@Table(name="event"
)
public class Event extends BaseEntity  implements java.io.Serializable,Cloneable {

	@Override
	@Transient
	public String getNodeName() {
		return this.eventName;
	}
//	private Long domainId;
//	@Column(name="DOMAIN_ID")
//    public Long getDomainId() {
//		return domainId;
//	}
//	public void setDomainId(Long domainId) {
//		this.domainId = domainId;
//	}
     private Long eventId;
     private Long eventType;
     private String eventName;
     private String description;
     private Boolean isRequireReserveInfo;
     private Long categoryId;
     private Boolean isRequireRatingConfig;
     private Long index;

    public Event() {
    }

    public Event(Long eventType, String eventName, String description, Boolean isRequireReserveInfo, Long categoryId) {
       this.eventType = eventType;
       this.eventName = eventName;
       this.description = description;
       this.isRequireReserveInfo = isRequireReserveInfo;
       this.categoryId = categoryId;
    }
   
     @Id
//     @GeneratedValue(strategy=IDENTITY)
    
    @Column(name="EVENT_ID", unique=true, nullable=false)
    public Long getEventId() {
        return this.eventId;
    }
    
    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
    
    @Column(name="EVENT_TYPE")
    public Long getEventType() {
        return this.eventType;
    }
    
    public void setEventType(Long eventType) {
        this.eventType = eventType;
    }
    
    @Column(name="EVENT_NAME")
    public String getEventName() {
        return this.eventName;
    }
    
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
    
    @Column(name="DESCRIPTION")
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    @Column(name="IS_REQUIRE_RESERVE_INFO")
    public Boolean getIsRequireReserveInfo() {
        return this.isRequireReserveInfo;
    }
    
    public void setIsRequireReserveInfo(Boolean isRequireReserveInfo) {
        this.isRequireReserveInfo = isRequireReserveInfo;
    }
    
    @Column(name="CATEGORY_ID")
    public Long getCategoryId() {
        return this.categoryId;
    }
    
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
    
    @Column(name="IS_REQUIRE_RATING_CONFIG")
	public Boolean getIsRequireRatingConfig() {
		return isRequireRatingConfig;
	}
	public void setIsRequireRatingConfig(Boolean isRequireRatingConfig) {
		this.isRequireRatingConfig = isRequireRatingConfig;
	}

	@Column(name = "POS_INDEX")
	public Long getIndex() {
		return index;
	}

	public void setIndex(Long index) {
		this.index = index;
	}

	@Override
	public Event clone() throws CloneNotSupportedException {
		return (Event) super.clone();
	}
}



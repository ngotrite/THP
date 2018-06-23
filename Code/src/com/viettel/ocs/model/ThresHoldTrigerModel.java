package com.viettel.ocs.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.viettel.ocs.entity.BalType;
import com.viettel.ocs.entity.Threshold;
import com.viettel.ocs.entity.ThresholdBaltypeMap;
import com.viettel.ocs.entity.TriggerOcs;

@SuppressWarnings("serial")
public class ThresHoldTrigerModel implements Serializable, Cloneable {
	private Threshold threshold;
	private List<TriggerOcs> triggerOcsofThresHold;

	private Boolean thresholdTypeDecrease;
	private Boolean thresholdTypeIncrease;
	private Integer index;

	public ThresHoldTrigerModel(Threshold threshold, List<TriggerOcs> triggerOcsofThresHold) {
		super();
		this.threshold = threshold;
		this.triggerOcsofThresHold = triggerOcsofThresHold;
		cal();
	}

	public ThresHoldTrigerModel(Threshold threshold) {
		super();
		this.threshold = threshold;
		this.triggerOcsofThresHold = new ArrayList<TriggerOcs>();
		cal();
	}

	public ThresHoldTrigerModel() {
		this.threshold = new Threshold();
		threshold.setIsPercentage(true);

		this.triggerOcsofThresHold = new ArrayList<TriggerOcs>();

		threshold.setThresholdType(0L);
		threshold.setValue(0L);
	}

	public void cal() {
		if (threshold.getThresholdType() == null) {
			this.thresholdTypeIncrease = false;
			this.thresholdTypeDecrease = false;
		} else if (threshold.getThresholdType() == 0L) {
			this.thresholdTypeIncrease = true;
			this.thresholdTypeDecrease = false;
		} else if (threshold.getThresholdType() == 1L) {
			this.thresholdTypeIncrease = false;
			this.thresholdTypeDecrease = true;
		} else if (threshold.getThresholdType() == 2L) {
			this.thresholdTypeIncrease = true;
			this.thresholdTypeDecrease = true;
		}
	}

	public void convertToThresholdType() {
		if (this.thresholdTypeIncrease == false && this.thresholdTypeDecrease == false) {
			threshold.setThresholdType(null);
		} else if (this.thresholdTypeIncrease == true && this.thresholdTypeDecrease == false) {
			threshold.setThresholdType(0L);
		} else if (this.thresholdTypeIncrease == false && this.thresholdTypeDecrease == true) {
			threshold.setThresholdType(1L);
		} else if (this.thresholdTypeIncrease == true && this.thresholdTypeDecrease == true) {
			threshold.setThresholdType(2L);
		}
	}

	public Threshold getThreshold() {
		return threshold;
	}

	public void setThreshold(Threshold threshold) {
		this.threshold = threshold;
	}

	public List<TriggerOcs> getTriggerOcsofThresHold() {
		return triggerOcsofThresHold;
	}

	public void setTriggerOcsofThresHold(List<TriggerOcs> triggerOcsofThresHold) {
		this.triggerOcsofThresHold = triggerOcsofThresHold;
	}

	public Boolean getThresholdTypeDecrease() {
		return thresholdTypeDecrease;
	}

	public void setThresholdTypeDecrease(Boolean thresholdTypeDecrease) {
		this.thresholdTypeDecrease = thresholdTypeDecrease;
	}

	public Boolean getThresholdTypeIncrease() {
		return thresholdTypeIncrease;
	}

	public void setThresholdTypeIncrease(Boolean thresholdTypeIncrease) {
		this.thresholdTypeIncrease = thresholdTypeIncrease;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	@Override
	public ThresHoldTrigerModel clone() {
		try {
			ThresHoldTrigerModel thresHoldTrigerModel = (ThresHoldTrigerModel) super.clone();
			thresHoldTrigerModel.setThreshold(threshold.clone());
			thresHoldTrigerModel.setTriggerOcsofThresHold(new ArrayList<TriggerOcs>(triggerOcsofThresHold));
			return thresHoldTrigerModel;
		} catch (CloneNotSupportedException e) {
			
			return this;
		}
	}

}

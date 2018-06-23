package com.viettel.ocs.constant;

import java.util.HashMap;
import java.util.Map;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import org.hibernate.engine.jdbc.connections.internal.UserSuppliedConnectionProviderImpl;

import com.viettel.ocs.constant.ContantsUtil.OfferState;
import com.viettel.ocs.constant.ContantsUtil.OfferType;
import com.viettel.ocs.dao.ActionTypeDAO;
import com.viettel.ocs.entity.Action;
import com.viettel.ocs.entity.ActionTableDump;
import com.viettel.ocs.entity.ActionType;
import com.viettel.ocs.entity.BalType;
import com.viettel.ocs.entity.BaseEntity;
import com.viettel.ocs.entity.BillingCycle;
import com.viettel.ocs.entity.BillingCycleType;
import com.viettel.ocs.entity.Block;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.CdrGenFilename;
import com.viettel.ocs.entity.CdrProcessConfig;
import com.viettel.ocs.entity.CdrService;
import com.viettel.ocs.entity.CdrTemplate;
import com.viettel.ocs.entity.CssUssdResponse;
import com.viettel.ocs.entity.DecisionTable;
import com.viettel.ocs.entity.DynamicReserve;
import com.viettel.ocs.entity.Event;
import com.viettel.ocs.entity.GeoHomeZone;
import com.viettel.ocs.entity.MapAcmbalBal;
import com.viettel.ocs.entity.MapSharebalBal;
import com.viettel.ocs.entity.NestedObject;
import com.viettel.ocs.entity.Normalizer;
import com.viettel.ocs.entity.Offer;
import com.viettel.ocs.entity.OfferDump;
import com.viettel.ocs.entity.OfferPackage;
import com.viettel.ocs.entity.Parameter;
import com.viettel.ocs.entity.PcType;
import com.viettel.ocs.entity.PccRule;
import com.viettel.ocs.entity.PriceComponent;
import com.viettel.ocs.entity.ProfilePep;
import com.viettel.ocs.entity.RateTable;
import com.viettel.ocs.entity.RateTableDump;
import com.viettel.ocs.entity.ReserveInfo;
import com.viettel.ocs.entity.Service;
import com.viettel.ocs.entity.SmsNotifyTemplate;
import com.viettel.ocs.entity.SortPriceComponent;
import com.viettel.ocs.entity.StateGroup;
import com.viettel.ocs.entity.StateType;
import com.viettel.ocs.entity.StatisticItem;
import com.viettel.ocs.entity.SystemConfig;
import com.viettel.ocs.entity.TriggerDestination;
import com.viettel.ocs.entity.TriggerMsg;
import com.viettel.ocs.entity.TriggerOcs;
import com.viettel.ocs.entity.TriggerType;
import com.viettel.ocs.entity.UnitType;
import com.viettel.ocs.entity.Zone;
import com.viettel.ocs.entity.ZoneData;
import com.viettel.ocs.entity.ZoneMap;

@ManagedBean(name = "iconClassBean")
@ApplicationScoped
public class IconClass {

	public static final String CATEGORY = "category-icon";

	public static final String OFFER_OFFER = "offer-icon";
	public static final String OFFER_EVENT = "event-icon";
	public static final String OFFER_ACTION_TYPE = "actiontype-icon";
	public static final String OFFER_ACTION = "action-icon";
	public static final String OFFER_ACTION_DUMP = "fa fa-gavel";

	public static final String OFFER_DYNAMIC_RESERVE = "dynamic-reserve-icon";
	public static final String OFFER_SORT_PRICE_COMPONENT = "sort-price-component-icon";
	public static final String OFFER_POST_PROCESS = "post-process-icon";
	public static final String OFFER_PC_TYPE = "pctype-icon";
	public static final String OFFER_PRICE_COMPONENT = "price-component-icon";
	public static final String OFFER_BLOCK = "block-icon";
	public static final String OFFER_RATE_TABLE = "rate-table-icon";
	public static final String OFFER_RATE_TABLE_GROUP = "ratetable-group-icon";
	public static final String OFFER_DECISION_TABLE = "decition-table-icon";
	public static final String OFFER_NORMALIZER = "normalizer-icon";
	public static final String OFFER_TEMPLATE = "offer-template-icon";
	public static final String OFFER_SUBSCRIPTION = "offer-subscription-icon";
	public static final String OFFER_ONETIME = "offer-onetime-icon";
	public static final String OFFER_PACKAGE = "offer-package-icon";
	public static final String OFFER_MAIN = "offer-main-icon";
	public static final String OFFER_NORMAL = "offer-normal-icon";
	public static final String OFFER_COMPILED = "offer-compiled-icon";
	public static final String OFFER_OBJECT = "fa fa-arrow-circle-right";

	public static final String OFFER_STATE_SUSPEND = "fa fa-pause-circle-o";
	public static final String OFFER_STATE_REMOVED = "fa fa-times-circle";
	public static final String OFFER_STATE_ACTIVE = "fa fa-check";
	public static final String OFFER_STATE_IN_ACTIVE = "fa fa-eyedropper";
	public static final String OFFER_STATE_TESTING = "fa fa-eye";

	public static final String CTL_STATE = "state-icon";
	public static final String CTL_STATE_GROUP = "state-group-icon";
	public static final String CTL_BILLING_CYCLE = "billing-cycle-icon";
	public static final String CTL_RESERVE_INFO = "reserve-info-icon";
	public static final String CTL_SERVICE = "service-icon";
	public static final String CTL_UNITTYPE = "unittype-icon";
	public static final String CTL_GEO_HOME_ZONE = "geo_home_zone-icon";
	public static final String CTL_ZONE_MAP = "zone_map-icon";
	public static final String CTL_ZONE_DATA = "zone_data-icon";
	public static final String CTL_TRIGGER = "trigger-icon";
	public static final String CTL_TRIGGER_DEST = "trigger-destination-icon";
	public static final String CTL_TRIGGER_MSG = "trigger-message-icon";
	public static final String CTL_CDR = "cdr-icon";
	public static final String CTL_BALANCE = "balance-icon";
	public static final String CTL_BALANCE_ACM = "balance-acm-icon";
	public static final String CTL_BALANCE_MAPPING = "balance-mapping-icon";
	public static final String CTL_BALANCE_ACM_MAPPING = "balance-acm-mapping-icon";
	public static final String CTL_PARAMETER = "parameter-icon";
	public static final String CTL_STATISTIC = "statistic-icon";
	public static final String CTL_SMS_TEMPLATE = "sms-template-icon";
	public static final String CTL_CSS_USSD = "css-ussd-icon";

	public static final String PLC_POLICY = "policy-icon";
	public static final String PLC_POLICY_RULE = "policy-rule-icon";
	public static final String PLC_QOS = "qos-icon";
	public static final String PLC_MONITOR_KEY = "monitor-key-icon";

	public static final String SYS_SYS_CONFIG = "system-config-icon";

	// Luu cac map icon & action type --> improve performance
	public static Map<Long, String> mapActionTypeIcon = new HashMap<>();

	public static class ListIconChoose {
		public static final String CTL_STATE = "state-icon";
		public static final String CTL_STATE_GROUP = "state-group-icon";
		public static final String CTL_BILLING_CYCLE = "billing-cycle-icon";
		public static final String CTL_RESERVE_INFO = "reserve-info-icon";
		public static final String CTL_SERVICE = "service-icon";
		public static final String CTL_UNITTYPE = "unittype-ic";
	}

	public String getTreeNodeIcon(BaseEntity nodeData) {

		if (nodeData instanceof Category) {

			Category cat = (Category) nodeData;
			if (cat.getCategoryId() <= 0) {

				// OFFER
				if (cat.getCategoryType() == CategoryType.OFF_OFFER_TEMPLATE) {
					return IconClass.OFFER_TEMPLATE;
				} else if (cat.getCategoryType() == CategoryType.OFF_OFFER_SUBSCRIPTION) {
					return IconClass.OFFER_SUBSCRIPTION;
				} else if (cat.getCategoryType() == CategoryType.OFF_OFFER_ONETIME) {
					return IconClass.OFFER_ONETIME;
				} else if (cat.getCategoryType() == CategoryType.OFF_OFFER_PACKAGE) {
					return IconClass.OFFER_PACKAGE;
				} else if (cat.getCategoryType() == CategoryType.OFF_OFFER_SUBSCRIPTION_MAIN) {
					return IconClass.OFFER_MAIN;
				} else if (cat.getCategoryType() == CategoryType.OFF_OFFER_SUBSCRIPTION_NORMAL
						|| cat.getCategoryType() == CategoryType.OFF_OFFER_ONETIME_NORMAL) {
					return IconClass.OFFER_NORMAL;
				} else if (cat.getCategoryType() == CategoryType.OFF_OFFER_SUBSCRIPTION_COMPILED
						|| cat.getCategoryType() == CategoryType.OFF_OFFER_ONETIME_COMPILED) {
					return IconClass.OFFER_COMPILED;
				} else if (cat.getCategoryType() == CategoryType.OFF_EVENT_EVENT) {
					return IconClass.OFFER_EVENT;
				} else if (cat.getCategoryType() == CategoryType.OFF_EVENT_ACTION_TYPE) {
					return IconClass.OFFER_ACTION_TYPE;
				} else if (cat.getCategoryType() == CategoryType.OFF_ACTION) {
					return IconClass.OFFER_ACTION;
				} else if (cat.getCategoryType() == CategoryType.OFF_DR_DYNAMIC_RESERVE) {
					return IconClass.OFFER_DYNAMIC_RESERVE;
				} else if (cat.getCategoryType() == CategoryType.OFF_SPC_SORT_PRICE_COMPONENT) {
					return IconClass.OFFER_SORT_PRICE_COMPONENT;
				} else if (cat.getCategoryType() == CategoryType.OFF_PC_TYPE) {
					return IconClass.OFFER_PC_TYPE;
				} else if (cat.getCategoryType() == CategoryType.OFF_PC_PRICE_COMPONENT) {
					return IconClass.OFFER_PRICE_COMPONENT;
				} else if (cat.getCategoryType() == CategoryType.OFF_BLOCK_BLOCK) {
					return IconClass.OFFER_BLOCK;
				} else if (cat.getCategoryType() == CategoryType.OFF_RT_RATE_TABLE) {
					return IconClass.OFFER_RATE_TABLE;
				} else if (cat.getCategoryType() == CategoryType.OFF_DT_DECISION_TABLE) {
					return IconClass.OFFER_DECISION_TABLE;
				} else if (cat.getCategoryType() == CategoryType.OFF_NOM_NORMALIZER) {
					return IconClass.OFFER_NORMALIZER;
				} else if (cat.getCategoryType() == CategoryType.CTL_STATETYPE) {
					// CATALOG

					return IconClass.CTL_STATE;
				} else if (cat.getCategoryType() == CategoryType.CTL_STATEGROUP) {
					return IconClass.CTL_STATE_GROUP;
				} else if (cat.getCategoryType() == CategoryType.CTL_BILLING_CYCLE) {
					return IconClass.CTL_BILLING_CYCLE;
				} else if (cat.getCategoryType() == CategoryType.CTL_UT_RESERVE_INFO) {
					return IconClass.CTL_RESERVE_INFO;
				} else if (cat.getCategoryType() == CategoryType.CTL_SERVICE) {
					return IconClass.CTL_SERVICE;
				} else if (cat.getCategoryType() == CategoryType.CTL_UT_UNIT_TYPE) {
					return IconClass.CTL_UNITTYPE;
				} else if (cat.getCategoryType() == CategoryType.CTL_GHZ_GEO_HOME_ZONE) {
					return IconClass.CTL_GEO_HOME_ZONE;
				} else if (cat.getCategoryType() == CategoryType.CTL_ZD_ZONE_DATA) {
					return IconClass.CTL_ZONE_MAP;
				} else if (cat.getCategoryType() == CategoryType.CTL_TO_TRIGGER_OCS
						|| cat.getCategoryType() == CategoryType.CTL_TT_TRIGGER_TYPE) {
					return IconClass.CTL_TRIGGER;
				} else if (cat.getCategoryType() == CategoryType.CTL_TD_TRIGGER_DESTINATION) {
					return IconClass.CTL_TRIGGER_DEST;
				} else if (cat.getCategoryType() == CategoryType.CTL_TM_TRIGGER_MESSAGE) {
					return IconClass.CTL_TRIGGER_MSG;
				} else if (cat.getCategoryType() == CategoryType.CTL_CDR_SERVICE
						|| cat.getCategoryType() == CategoryType.CTL_CDR_TEMPLATE
						|| cat.getCategoryType() == CategoryType.CTL_CDR_GEN_FILENAME
						|| cat.getCategoryType() == CategoryType.CTL_CDR_PROCESS_CONFIG) {
					return IconClass.CTL_CDR;
				} else if (cat.getCategoryType() == CategoryType.CTL_BL_BAL_TYPE) {
					return IconClass.CTL_BALANCE;
				} else if (cat.getCategoryType() == CategoryType.CTL_BL_BAL_TYPE_ACC) {
					return IconClass.CTL_BALANCE_ACM;
				} else if (cat.getCategoryType() == CategoryType.CTL_BL_BAL_TYPE_ACCOUNT_MAPPING) {
					return IconClass.CTL_BALANCE_MAPPING;
				} else if (cat.getCategoryType() == CategoryType.CTL_BL_BAL_TYPE_ACM_MAPPING) {
					return IconClass.CTL_BALANCE_ACM_MAPPING;
				} else if (cat.getCategoryType() == CategoryType.CTL_PARAMETER) {
					return IconClass.CTL_PARAMETER;
				} else if (cat.getCategoryType() == CategoryType.CTL_STATISTIC_ITEM) {
					return IconClass.CTL_STATISTIC;
				} else if (cat.getCategoryType() == CategoryType.CTL_SMS_NOTIFY_TEMPLATE) {
					return IconClass.CTL_SMS_TEMPLATE;
				} else if (cat.getCategoryType() == CategoryType.CTL_CSS_USSD_RESPONSE) {
					return IconClass.CTL_CSS_USSD;
				} else if (cat.getCategoryType() == CategoryType.PLC_PPP_POLICY_PROFILE) {
					// POLICY

					return IconClass.PLC_POLICY;
				} else if (cat.getCategoryType() == CategoryType.PLC_PR_PCC_RULE) {
					return IconClass.PLC_POLICY_RULE;
				} else if (cat.getCategoryType() == CategoryType.SYS_SYS_CONFIG) {
					// SYSTEM

					return IconClass.SYS_SYS_CONFIG;
				}
			}

			return IconClass.CATEGORY;
		} else if (nodeData instanceof Offer || nodeData instanceof OfferDump) {
			if (nodeData instanceof Offer) {
				Offer offer = (Offer) nodeData;
				if (offer.getState().equals(OfferState.SUSPEND)) {
					return IconClass.OFFER_STATE_SUSPEND;
				} else if (offer.getState().equals(OfferState.REMOVED)) {
					return IconClass.OFFER_STATE_REMOVED;
				} else if (offer.getState().equals(OfferState.ACTIVE)) {
					return IconClass.OFFER_STATE_ACTIVE;
				} else if (offer.getState().equals(OfferState.IN_ACTIVE)) {
					return IconClass.OFFER_STATE_IN_ACTIVE;
				} else if (offer.getState().equals(OfferState.TESTING)) {
					return IconClass.OFFER_STATE_TESTING;
				}
			}

			if (nodeData instanceof Offer && ((Offer) nodeData).getOfferType() != null
					&& ((Offer) nodeData).getOfferType() == OfferType.MAIN) {
				return IconClass.OFFER_MAIN;
			}

			return IconClass.OFFER_OFFER;
		} else if (nodeData instanceof OfferPackage) {
			return IconClass.OFFER_PACKAGE;
		} else if (nodeData instanceof Event) {
			return IconClass.OFFER_EVENT;
		} else if (nodeData instanceof ActionType) {
			return IconClass.OFFER_ACTION_TYPE;
		} else if (nodeData instanceof Action) {
			// Doi voi action thi icon lay theo action type do nguoi dung chon

			String icon = null;
			Action action = (Action) nodeData;
			icon = mapActionTypeIcon.get(action.getActionType());
			if (icon == null && action.getActionType() != null && action.getActionType().longValue() > 0) {

				ActionTypeDAO actionTypeDAO = new ActionTypeDAO();
				ActionType actionType = actionTypeDAO.get(action.getActionType());
				if (actionType != null) {

					mapActionTypeIcon.put(action.getActionType(), actionType.getIcon());
					icon = actionType.getIcon();
				}
			}

			if (icon == null)
				icon = IconClass.OFFER_ACTION;

			return icon;
		} else if (nodeData instanceof ActionTableDump) {
			return IconClass.OFFER_ACTION_DUMP;
		} else if (nodeData instanceof DynamicReserve) {
			return IconClass.OFFER_DYNAMIC_RESERVE;
		} else if (nodeData instanceof SortPriceComponent) {
			return IconClass.OFFER_SORT_PRICE_COMPONENT;
		} else if (nodeData instanceof PcType) {
			return IconClass.OFFER_PC_TYPE;
		} else if (nodeData instanceof PriceComponent) {
			return IconClass.OFFER_PRICE_COMPONENT;
		} else if (nodeData instanceof Block) {
			return IconClass.OFFER_BLOCK;
		} else if (nodeData instanceof RateTable) {
			return IconClass.OFFER_RATE_TABLE;
		} else if (nodeData instanceof RateTableDump) {
			return IconClass.OFFER_RATE_TABLE_GROUP;
		} else if (nodeData instanceof DecisionTable) {
			return IconClass.OFFER_DECISION_TABLE;
		} else if (nodeData instanceof Normalizer) {
			return IconClass.OFFER_NORMALIZER;
		} else if (nodeData instanceof NestedObject) {
			return IconClass.OFFER_OBJECT;
		} else if (nodeData instanceof StateType) {
			// CATALOG

			return IconClass.CTL_STATE;
		} else if (nodeData instanceof StateGroup) {
			return IconClass.CTL_STATE_GROUP;
		} else if (nodeData instanceof BillingCycle || nodeData instanceof BillingCycleType) {
			return IconClass.CTL_BILLING_CYCLE;
		} else if (nodeData instanceof ReserveInfo) {
			return IconClass.CTL_RESERVE_INFO;
		} else if (nodeData instanceof Service) {
			return IconClass.CTL_SERVICE;
		} else if (nodeData instanceof UnitType) {
			return IconClass.CTL_UNITTYPE;
		} else if (nodeData instanceof GeoHomeZone) {
			return IconClass.CTL_GEO_HOME_ZONE;
		} else if (nodeData instanceof ZoneMap) {
			return IconClass.CTL_ZONE_MAP;
		} else if (nodeData instanceof ZoneData || nodeData instanceof Zone) {
			return IconClass.CTL_ZONE_DATA;
		} else if (nodeData instanceof TriggerOcs || nodeData instanceof TriggerType) {
			return IconClass.CTL_TRIGGER;
		} else if (nodeData instanceof TriggerDestination) {
			return IconClass.CTL_TRIGGER_DEST;
		} else if (nodeData instanceof TriggerMsg) {
			return IconClass.CTL_TRIGGER_MSG;
		} else if (nodeData instanceof CdrService || nodeData instanceof CdrTemplate
				|| nodeData instanceof CdrGenFilename || nodeData instanceof CdrProcessConfig) {
			return IconClass.CTL_CDR;
		} else if (nodeData instanceof BalType) {

			BalType balType = (BalType) nodeData;
			if (balType.getIsAcm()) {
				return IconClass.CTL_BALANCE_ACM;
			} else {
				return IconClass.CTL_BALANCE;
			}
		} else if (nodeData instanceof MapSharebalBal) {
			return IconClass.CTL_BALANCE_MAPPING;
		} else if (nodeData instanceof MapAcmbalBal) {
			return IconClass.CTL_BALANCE_ACM_MAPPING;
		} else if (nodeData instanceof Parameter) {
			return IconClass.CTL_PARAMETER;
		} else if (nodeData instanceof StatisticItem) {
			return IconClass.CTL_STATISTIC;
		} else if (nodeData instanceof SmsNotifyTemplate) {
			return IconClass.CTL_SMS_TEMPLATE;
		} else if (nodeData instanceof CssUssdResponse) {
			return IconClass.CTL_CSS_USSD;
		} else if (nodeData instanceof ProfilePep) {
			// POLICY

			return IconClass.PLC_POLICY;
		} else if (nodeData instanceof PccRule) {
			return IconClass.PLC_POLICY_RULE;
		} else if (nodeData instanceof SystemConfig) {
			// SYSTEM

			return IconClass.SYS_SYS_CONFIG;
		}

		return "";
	}
}

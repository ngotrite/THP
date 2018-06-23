package com.viettel.ocs.constant;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

@ManagedBean(name = "categoryTypeBean")
@ApplicationScoped
public class CategoryType {

	private static final long FACTOR = 10;

	/** OFFER **/
	public static final String OFF_OFFER_NAME = "Offer";

	public static final long OFF_OFFER_TEMPLATE = TreeType.OFFER_OFFER_NUMBER * FACTOR;
	public static final String OFF_OFFER_TEMPLATE_NAME = "Offer Templates";

	public static final long OFF_OFFER_SUBSCRIPTION = TreeType.OFFER_OFFER_NUMBER * FACTOR + 10;
	public static final String OFF_OFFER_SUBSCRIPTION_NAME = "Subscription Offers";

	public static final long OFF_OFFER_SUBSCRIPTION_MAIN = OFF_OFFER_SUBSCRIPTION + 1;
	public static final String OFF_OFFER_SUBSCRIPTION_NAME_MAIN = "Main Offers";

	public static final long OFF_OFFER_SUBSCRIPTION_NORMAL = OFF_OFFER_SUBSCRIPTION + 2;
	public static final String OFF_OFFER_SUBSCRIPTION_NAME_NORMAL = "Normal Offers";

	public static final long OFF_OFFER_SUBSCRIPTION_COMPILED = OFF_OFFER_SUBSCRIPTION + 3;
	public static final String OFF_OFFER_SUBSCRIPTION_NAME_COMPILED = "Compiled Offers";

	public static final long OFF_OFFER_ONETIME = TreeType.OFFER_OFFER_NUMBER * FACTOR + 20;
	public static final String OFF_OFFER_ONETIME_NAME = "One Time Offers";

	public static final long OFF_OFFER_ONETIME_NORMAL = OFF_OFFER_ONETIME + 1;
	public static final String OFF_OFFER_ONETIME_NAME_NORMAL = "Normal Offers";

	public static final long OFF_OFFER_ONETIME_COMPILED = OFF_OFFER_ONETIME + 2;
	public static final String OFF_OFFER_ONETIME_NAME_COMPILED = "Compiled Offers";

	public static final long OFF_OFFER_PACKAGE = TreeType.OFFER_OFFER_NUMBER * FACTOR + 30;
	public static final String OFF_OFFER_PACKAGE_NAME = "Offer Package";

	public static final long OFF_OFFER_SUBSCRIPTION_DIALOG = TreeType.OFFER_OFFER_NUMBER * FACTOR + 40;
	public static final String OFF_OFFER_SUBSCRIPTION_DIALOG_NAME = "Subscription Offers";

	/** EVENT **/
	public static final long OFF_EVENT_EVENT = TreeType.OFFER_EVENT_NUMBER * FACTOR + 1;
	public static final String OFF_EVENT_EVENT_NAME = "Events";

	public static final long OFF_EVENT_ACTION_TYPE = TreeType.OFFER_EVENT_NUMBER * FACTOR + 2;
	public static final String OFF_EVENT_ACTION_TYPE_NAME = "Action Types";

	/** ACTION **/
	public static final long OFF_ACTION = TreeType.OFFER_ACTION_NUMBER * FACTOR + 1;
	public static final String OFF_ACTION_NAME = "Action";

	/** DYNAMIC RESERVE **/
	public static final long OFF_DR_DYNAMIC_RESERVE = TreeType.OFFER_DYNAMIC_RESERVE_NUMBER * FACTOR + 1;
	public static final String OFF_DR_DYNAMIC_RESERVE_NAME = "Dynamic Reserves";

	/** SORT PRICE COMPONENT **/
	public static final long OFF_SPC_SORT_PRICE_COMPONENT = TreeType.OFFER_SORT_PRICE_COMPONENT_NUMBER * FACTOR + 1;
	public static final String OFF_SPC_SORT_PRICE_COMPONENT_NAME = "Price Component Sorting";

	/** POST PROCESS **/
	public static final long OFF_PP_POST_PROCESS = TreeType.OFFER_POST_PROCESS_NUMBER * FACTOR + 1;
	public static final String OFF_PP_POST_PROCESS_NAME = "Post Process";

	/** PRICE COMPONENT **/
	public static final long OFF_PC_PRICE_COMPONENT = TreeType.OFFER_PRICE_COMPONENT_NUMBER * FACTOR + 1;
	public static final String OFF_PC_PRICE_COMPONENT_NAME = "Price Components";

	/** BLOCK **/
	public static final long OFF_BLOCK_BLOCK = TreeType.OFFER_BLOCK_NUMBER * FACTOR + 1;
	public static final String OFF_BLOCK_BLOCK_NAME = "Blocks";

	/** RATE TABLE **/
	public static final long OFF_RT_RATE_TABLE = TreeType.OFFER_RATE_TABLE_NUMBER * FACTOR + 1;
	public static final String OFF_RT_RATE_TABLE_NAME = "Rate Tables";

	/** DECISION TABLE **/
	public static final long OFF_DT_DECISION_TABLE = TreeType.OFFER_DECISION_TABLE_NUMBER * FACTOR + 1;
	public static final String OFF_DT_DECISION_TABLE_NAME = "Decision Tables";

	/** NORMALIZER **/
	public static final long OFF_NOM_NORMALIZER = TreeType.OFFER_NORMALIZER_NUMBER * FACTOR + 1;
	public static final String OFF_NOM_NORMALIZER_NAME = "Normalizers";

	/** OBJECT **/
	public static final long OFF_OBJECT = TreeType.OFFER_OBJECT_NUMBER * FACTOR + 1;
	public static final String OFF_OBJECT_NAME = "Objects";

	/** PARAMETER **/
	public static final long CTL_PARAMETER = TreeType.CATALOG_PARAMETER_NUMBER * FACTOR + 1;
	public static final String CTL_PARAMETER_NAME = "Parameter";

	/** UNITTYPE **/
	public static final long CTL_UT_UNIT_TYPE = TreeType.CATALOG_UNIT_TYPE_NUMBER * FACTOR + 1;
	public static final String CTL_UT_UNIT_TYPE_NAME = "Unit Type";

	/** RESERVE_INFO **/
	public static final long CTL_UT_RESERVE_INFO = TreeType.CATALOG_REVERSE_INFO_NUMBER * FACTOR + 1;
	public static final String CTL_UT_RESERVE_INFO_NAME = "Reserve Info";

	/** STATE GROUP **/
	public static final long CTL_STATEGROUP = TreeType.CATALOG_STATE_SET_NUMBER * FACTOR + 1;
	public static final String CTL_STATEGROUP_NAME = "State Group";

	/** STATE TYPE **/
	public static final long CTL_STATETYPE = TreeType.CATALOG_STATE_SET_NUMBER * FACTOR + 2;
	public static final String CTL_STATETYPE_NAME = "State Type";

	/** ZONE DATA **/
	public static final long CTL_ZD_ZONE_DATA = TreeType.CATALOG_ZONE_DATA_NUMBER * FACTOR + 1;
	public static final String CTL_ZD_ZONE_DATA_NAME = "Zone Data";

	/** BAL TYPE **/
	public static final long CTL_BL_BAL_TYPE = TreeType.CATALOG_BALANCE_NUMBER * FACTOR + 1;
	public static final String CTL_BL_BAL_TYPE_NAME = "Balances";

	public static final long CTL_BL_BAL_TYPE_ACC = TreeType.CATALOG_BALANCE_NUMBER * FACTOR + 2;
	public static final String CTL_BL_BAL_TYPE_ACC_NAME = "Accumulate Balances";

	public static final long CTL_BL_BAL_TYPE_ACCOUNT_MAPPING = TreeType.CATALOG_BALANCE_NUMBER * FACTOR + 3;
	public static final String CTL_BL_BAL_TYPE_ACCOUNT_MAPPING_NAME = "AccountBalance Mapping";

	public static final long CTL_BL_BAL_TYPE_ACM_MAPPING = TreeType.CATALOG_BALANCE_NUMBER * FACTOR + 4;
	public static final String CTL_BL_BAL_TYPE_ACM_MAPPING_NAME = "AcmBalance Mapping";

	/** GEO HOME ZONE **/
	public static final long CTL_GHZ_GEO_HOME_ZONE = TreeType.CATALOG_GEO_HOME_NUMBER * FACTOR + 1;
	public static final String CTL_GHZ_GEO_HOME_ZONE_NAME = "Geo Home Zone";

	/** TRIGGER **/
	public static final long CTL_TO_TRIGGER_OCS = TreeType.CATALOG_TRIGGER_NUMBER * FACTOR + 1;
	public static final String CTL_TO_TRIGGER_OCS_NAME = "Trigger";

	public static final long CTL_TM_TRIGGER_MESSAGE = TreeType.CATALOG_TRIGGER_MESSAGE_NUMBER * FACTOR + 2;
	public static final String CTL_TM_TRIGGER_MESSAGE_NAME = "Trigger Message";

	public static final long CTL_TD_TRIGGER_DESTINATION = TreeType.CATALOG_TRIGGER_DESTINATION_NUMBER * FACTOR + 3;
	public static final String CTL_TD_TRIGGER_DESTINATION_NAME = "Trigger Destination";

	public static final long CTL_TT_TRIGGER_TYPE = TreeType.CATALOG_TRIGGER_NUMBER * FACTOR + 4;
	public static final String CTL_TT_TRIGGER_TYPE_NAME = "Trigger Type";

	/** PRICE COMPONENT **/
	public static final long CTL_BILLING_CYCLE = TreeType.CATALOG_BILLING_CYCLE_NUMBER * FACTOR + 1;
	public static final String CTL_BILLING_CYCLE_NAME = "Billing Cycle Type";
	/** SERVICE **/
	public static final long CTL_SERVICE = TreeType.CATALOG_SERVICE_NUMBER * FACTOR + 1;
	public static final String CTL_SERVICE_NAME = "Service";
	/** CDR SERVICE **/
	public static final long CTL_CDR_SERVICE = TreeType.CATALOG_CDR_SERVICE_NUMBER * FACTOR + 1;
	public static final String CTL_CDR_SERVICE_NAME = "CDR Service";
	/** CDR TEMPLATE **/
	public static final long CTL_CDR_TEMPLATE = TreeType.CATALOG_CDR_TEMPLATE_NUMBER * FACTOR + 1;
	public static final String CTL_CDR_TEMPLATE_NAME = "CDR Template";
	/** CDR GEN FILENAME **/
	public static final long CTL_CDR_GEN_FILENAME = TreeType.CATALOG_CDR_GEN_FILENAME_NUMBER * FACTOR + 1;
	public static final String CTL_CDR_GEN_FILENAME_NAME = "CDR Gen File Name";
	/** CDR TEMPLATE **/
	public static final long CTL_CDR_PROCESS_CONFIG = TreeType.CATALOG_CDR_PROCESS_CONFIG_NUMBER * FACTOR + 1;
	public static final String CTL_CDR_PROCESS_CONFIG_NAME = "CDR Process Config";
	/** STATISTIC ITEM **/
	public static final long CTL_STATISTIC_ITEM = TreeType.CATALOG_STATISTIC_ITEM_NUMBER * FACTOR + 1;
	public static final String CTL_STATISTIC_ITEM_NAME = "Statistic Item";
	/** SMS_NOTIFY_TEMPLATE **/
	public static final long CTL_SMS_NOTIFY_TEMPLATE = TreeType.CATALOG_SMS_NOTIFY_TEMPLATE_NUMBER * FACTOR + 1;
	public static final String CTL_SMS_NOTIFY_TEMPLATE_NAME = "SMS Notification Template";
	/** CSS_USSD_RESPONSE **/
	public static final long CTL_CSS_USSD_RESPONSE = TreeType.CATALOG_CSS_USSD_RESPONSE_NUMBER * FACTOR + 1;
	public static final String CTL_CSS_USSD_RESPONSE_NAME = "USSD Response Template";
	/** PC TYPE **/
	public static final long OFF_PC_TYPE = TreeType.OFFER_PC_TYPE_NUMBER * FACTOR + 1;
	public static final String OFF_PC_TYPE_NAME = "Price Component Type";
	// Create CategoryType for
	// Policy------------------------------------------------------
	/** Policy Profile **/
	public static final long PLC_PPP_POLICY_PROFILE = TreeType.POLICY_POLICY_PROFILE_NUMBER * FACTOR;
	public static final String PLC_PPP_POLICY_PROFILE_NAME = "PEP Profile";
	/** PCC Rule **/
	public static final long PLC_PR_PCC_RULE = TreeType.POLICY_PCC_RULE_NUMBER * FACTOR;
	public static final String PLC_PR_PCC_RULE_NAME = "PCC Rule";
	// End Create CategoryType for
	// policy--------------------------------------------------

	public static final long SYS_SYS_CONFIG = TreeType.SYS_SYS_CONFIG_NUMBER * FACTOR;
	public static final String SYS_SYS_CONFIG_NAME = "System Config";

	public static Map<Long, String> getCatTypesByTreeType(String treeType) {

		Map<Long, String> map = new LinkedHashMap<>();
		if (treeType == null)
			return map;
		switch (treeType) {
		case TreeType.OFFER_TREE_SUBCRIPTION_MAIN:
			map.put(OFF_OFFER_SUBSCRIPTION, OFF_OFFER_SUBSCRIPTION_NAME);
			break;

		case TreeType.OFFER_TREE_NORMAL_DLG:
			map.put(OFF_OFFER_SUBSCRIPTION, OFF_OFFER_SUBSCRIPTION_NAME);
			map.put(OFF_OFFER_ONETIME, OFF_OFFER_ONETIME_NAME);
			break;

		case TreeType.OFFER_OFFER:
			map.put(OFF_OFFER_TEMPLATE, OFF_OFFER_TEMPLATE_NAME);
			map.put(OFF_OFFER_SUBSCRIPTION, OFF_OFFER_SUBSCRIPTION_NAME);
			map.put(OFF_OFFER_ONETIME, OFF_OFFER_ONETIME_NAME);
			map.put(OFF_OFFER_PACKAGE, OFF_OFFER_PACKAGE_NAME);
			break;

		case TreeType.OFFER_EVENT:
			map.put(OFF_EVENT_EVENT, OFF_EVENT_EVENT_NAME);
			map.put(OFF_EVENT_ACTION_TYPE, OFF_EVENT_ACTION_TYPE_NAME);
			break;

		case TreeType.OFFER_ACTION_TYPE:
			map.put(OFF_EVENT_ACTION_TYPE, OFF_EVENT_ACTION_TYPE_NAME);
			break;

		case TreeType.OFFER_EVENT_EVENT:
			map.put(OFF_EVENT_EVENT, OFF_EVENT_EVENT_NAME);
			break;
		case TreeType.OFFER_ACTION:
			map.put(OFF_ACTION, OFF_ACTION_NAME);
			break;

		case TreeType.OFFER_DYNAMIC_RESERVE:
			map.put(OFF_DR_DYNAMIC_RESERVE, OFF_DR_DYNAMIC_RESERVE_NAME);
			break;

		case TreeType.OFFER_SORT_PRICE_COMPONENT:
			map.put(OFF_SPC_SORT_PRICE_COMPONENT, OFF_SPC_SORT_PRICE_COMPONENT_NAME);
			break;

		case TreeType.OFFER_POST_PROCESS:
			map.put(OFF_PP_POST_PROCESS, OFF_PP_POST_PROCESS_NAME);
			break;

		case TreeType.OFFER_PRICE_COMPONENT:
			map.put(OFF_PC_PRICE_COMPONENT, OFF_PC_PRICE_COMPONENT_NAME);
			break;

		case TreeType.OFFER_BLOCK:
			map.put(OFF_BLOCK_BLOCK, OFF_BLOCK_BLOCK_NAME);
			break;

		case TreeType.OFFER_RATE_TABLE:
			map.put(OFF_RT_RATE_TABLE, OFF_RT_RATE_TABLE_NAME);
			break;

		case TreeType.OFFER_DECISION_TABLE:
			map.put(OFF_DT_DECISION_TABLE, OFF_DT_DECISION_TABLE_NAME);
			break;

		case TreeType.OFFER_NORMALIZER:
			map.put(OFF_NOM_NORMALIZER, OFF_NOM_NORMALIZER_NAME);
			break;

		case TreeType.OFFER_OBJECT:
			map.put(OFF_OBJECT, OFF_OBJECT_NAME);
			break;

		case TreeType.OFFER_PACKAGE:
			map.put(OFF_OFFER_PACKAGE, OFF_OFFER_PACKAGE_NAME);
			break;

		case TreeType.CATALOG_UNIT_TYPE:
			map.put(CTL_UT_UNIT_TYPE, CTL_UT_UNIT_TYPE_NAME);
			break;
		case TreeType.CATALOG_PARAMETER:
			map.put(CTL_PARAMETER, CTL_PARAMETER_NAME);
			break;
		case TreeType.CATALOG_ZONE_DATA:
			map.put(CTL_ZD_ZONE_DATA, CTL_ZD_ZONE_DATA_NAME);
			break;
		case TreeType.CATALOG_RESERVE_INFO:
			map.put(CTL_UT_RESERVE_INFO, CTL_UT_RESERVE_INFO_NAME);
			break;
		case TreeType.CATALOG_STATE_SET:
			map.put(CTL_STATETYPE, CTL_STATETYPE_NAME);
			map.put(CTL_STATEGROUP, CTL_STATEGROUP_NAME);
			break;
		case TreeType.CATALOG_BALANCE:
			map.put(CTL_BL_BAL_TYPE, CTL_BL_BAL_TYPE_NAME);
			map.put(CTL_BL_BAL_TYPE_ACC, CTL_BL_BAL_TYPE_ACC_NAME);
			map.put(CTL_BL_BAL_TYPE_ACCOUNT_MAPPING, CTL_BL_BAL_TYPE_ACCOUNT_MAPPING_NAME);
			map.put(CTL_BL_BAL_TYPE_ACM_MAPPING, CTL_BL_BAL_TYPE_ACM_MAPPING_NAME);
			break;
		case TreeType.CATALOG_GEO_HOME:
			map.put(CTL_GHZ_GEO_HOME_ZONE, CTL_GHZ_GEO_HOME_ZONE_NAME);
			break;
		case TreeType.CATALOG_TRIGGER_MESSAGE:
			map.put(CTL_TM_TRIGGER_MESSAGE, CTL_TM_TRIGGER_MESSAGE_NAME);
			break;
		case TreeType.CATALOG_TRIGGER_DESTINATION:
			map.put(CTL_TD_TRIGGER_DESTINATION, CTL_TD_TRIGGER_DESTINATION_NAME);
			break;
		case TreeType.CATALOG_BILLING_CYCLE:
			map.put(CTL_BILLING_CYCLE, CTL_BILLING_CYCLE_NAME);
			break;

		case TreeType.POLICY_PCC_RULE:
			map.put(PLC_PR_PCC_RULE, PLC_PR_PCC_RULE_NAME);
			break;

		case TreeType.POLICY_POLICY_PROFILE:
			map.put(PLC_PPP_POLICY_PROFILE, PLC_PPP_POLICY_PROFILE_NAME);
			break;

		case TreeType.CATALOG_BALANCES:
			map.put(CTL_BL_BAL_TYPE, CTL_BL_BAL_TYPE_NAME);
			break;
		case TreeType.CATALOG_BALANCE_ACC:
			map.put(CTL_BL_BAL_TYPE_ACC, CTL_BL_BAL_TYPE_ACC_NAME);
			break;
		case TreeType.CATALOG_TRIGGER_OCS:
			map.put(CTL_TO_TRIGGER_OCS, CTL_TO_TRIGGER_OCS_NAME);
			map.put(CTL_TT_TRIGGER_TYPE, CTL_TT_TRIGGER_TYPE_NAME);
			break;
		case TreeType.CATALOG_SERVICE:
			map.put(CTL_SERVICE, CTL_SERVICE_NAME);
			break;
		case TreeType.CATALOG_CDR_SERVICE:
			map.put(CTL_CDR_SERVICE, CTL_CDR_SERVICE_NAME);
			break;
		case TreeType.CATALOG_CDR_TEMPLATE:
			map.put(CTL_CDR_TEMPLATE, CTL_CDR_TEMPLATE_NAME);
			break;
		case TreeType.CATALOG_CDR_GEN_FILENAME:
			map.put(CTL_CDR_GEN_FILENAME, CTL_CDR_GEN_FILENAME_NAME);
			break;
		case TreeType.CATALOG_CDR_PROCESS_CONFIG:
			map.put(CTL_CDR_PROCESS_CONFIG, CTL_CDR_PROCESS_CONFIG_NAME);
			break;
		case TreeType.OFFER_SUBCRIPTION_NORMAL_ONLY:
			map.put(OFF_OFFER_SUBSCRIPTION_NORMAL, OFF_OFFER_SUBSCRIPTION_NAME_NORMAL);
			break;
		case TreeType.SYS_SYS_CONFIG:
			map.put(SYS_SYS_CONFIG, SYS_SYS_CONFIG_NAME);
			break;
		case TreeType.CATALOG_STATISTIC_ITEM:
			map.put(CTL_STATISTIC_ITEM, CTL_STATISTIC_ITEM_NAME);
			break;
		case TreeType.OFFER_PC_TYPE:
			map.put(OFF_PC_TYPE, OFF_PC_TYPE_NAME);
			break;
		case TreeType.CATALOG_SMS_NOTIFY_TEMPLATE:
			map.put(CTL_SMS_NOTIFY_TEMPLATE, CTL_SMS_NOTIFY_TEMPLATE_NAME);
			break;
		case TreeType.CATALOG_CSS_USSD_RESPONSE:
			map.put(CTL_CSS_USSD_RESPONSE, CTL_CSS_USSD_RESPONSE_NAME);
			break;
		default:
			break;
		}

		return map;
	}

	public static Map<Long, String> getCatTypeSub(long catType) {

		Map<Long, String> map = new LinkedHashMap<>();
		if (catType == OFF_OFFER_SUBSCRIPTION) {

			map.put(OFF_OFFER_SUBSCRIPTION_MAIN, OFF_OFFER_SUBSCRIPTION_NAME_MAIN);
			map.put(OFF_OFFER_SUBSCRIPTION_NORMAL, OFF_OFFER_SUBSCRIPTION_NAME_NORMAL);
			map.put(OFF_OFFER_SUBSCRIPTION_COMPILED, OFF_OFFER_SUBSCRIPTION_NAME_COMPILED);
		} else if (catType == OFF_OFFER_ONETIME) {
			map.put(OFF_OFFER_ONETIME_NORMAL, OFF_OFFER_ONETIME_NAME_NORMAL);
			map.put(OFF_OFFER_ONETIME_COMPILED, OFF_OFFER_ONETIME_NAME_COMPILED);
		} else if (catType == OFF_OFFER_SUBSCRIPTION_DIALOG) {
			map.put(OFF_OFFER_ONETIME_NORMAL, OFF_OFFER_ONETIME_NAME_NORMAL);
		}

		return map;
	}

	public static Map<Long, String> getCatTypeSubcription(long catType) {
		Map<Long, String> map = new LinkedHashMap<>();
		if (catType == OFF_OFFER_SUBSCRIPTION) {
			map.put(OFF_OFFER_SUBSCRIPTION_MAIN, OFF_OFFER_SUBSCRIPTION_NAME_MAIN);
			map.put(OFF_OFFER_SUBSCRIPTION_NORMAL, OFF_OFFER_SUBSCRIPTION_NAME_NORMAL);
			map.put(OFF_OFFER_SUBSCRIPTION_COMPILED, OFF_OFFER_SUBSCRIPTION_NAME_COMPILED);
		} else if (catType == OFF_OFFER_ONETIME) {
			map.put(OFF_OFFER_ONETIME_NORMAL, OFF_OFFER_ONETIME_NAME_NORMAL);
			map.put(OFF_OFFER_ONETIME_COMPILED, OFF_OFFER_ONETIME_NAME_COMPILED);
		}
		return map;
	}

	public static Map<Long, String> getCatTypeNormal(long catType) {
		Map<Long, String> map = new LinkedHashMap<>();
		if (catType == OFF_OFFER_SUBSCRIPTION) {
			map.put(OFF_OFFER_SUBSCRIPTION_NORMAL, OFF_OFFER_SUBSCRIPTION_NAME_NORMAL);
			map.put(OFF_OFFER_SUBSCRIPTION_COMPILED, OFF_OFFER_SUBSCRIPTION_NAME_COMPILED);
		} else if (catType == OFF_OFFER_ONETIME) {
			map.put(OFF_OFFER_ONETIME_NORMAL, OFF_OFFER_ONETIME_NAME_NORMAL);
			map.put(OFF_OFFER_ONETIME_COMPILED, OFF_OFFER_ONETIME_NAME_COMPILED);
		}
		return map;
	}

	public static Map<Long, String> getCatTypeSubcriptionMain(long catType) {
		Map<Long, String> map = new LinkedHashMap<>();
		if (catType == OFF_OFFER_SUBSCRIPTION) {
			map.put(OFF_OFFER_SUBSCRIPTION_MAIN, OFF_OFFER_SUBSCRIPTION_NAME_MAIN);
		}
		return map;
	}

	/* get for policy */
	public static long getPLC_PPP_POLICY_PROFILE() {
		return PLC_PPP_POLICY_PROFILE;
	}

	public static String getPLC_PPP_POLICY_PROFILE_NAME() {
		return PLC_PPP_POLICY_PROFILE_NAME;
	}

	public static long getPLC_PR_PCC_RULE() {
		return PLC_PR_PCC_RULE;
	}

	public static String getPLC_PR_PCC_RULE_NAME() {
		return PLC_PR_PCC_RULE_NAME;
	}

	/* end get for policy */
	public long getOFF_OFFER_TEMPLATE() {
		return OFF_OFFER_TEMPLATE;
	}

	public String getOFF_OFFER_TEMPLATE_NAME() {
		return OFF_OFFER_TEMPLATE_NAME;
	}

	public long getOFF_OFFER_SUBSCRIPTION() {
		return OFF_OFFER_SUBSCRIPTION;
	}

	public String getOFF_OFFER_SUBSCRIPTION_NAME() {
		return OFF_OFFER_SUBSCRIPTION_NAME;
	}

	public long getOFF_OFFER_ONETIME() {
		return OFF_OFFER_ONETIME;
	}

	public String getOFF_OFFER_ONETIME_NAME() {
		return OFF_OFFER_ONETIME_NAME;
	}

	public long getOFF_OFFER_PACKAGE() {
		return OFF_OFFER_PACKAGE;
	}

	public String getOFF_OFFER_PACKAGE_NAME() {
		return OFF_OFFER_PACKAGE_NAME;
	}

	public long getOFF_EVENT_EVENT() {
		return OFF_EVENT_EVENT;
	}

	public String getOFF_EVENT_EVENT_NAME() {
		return OFF_EVENT_EVENT_NAME;
	}

	public long getOFF_EVENT_ACTION_TYPE() {
		return OFF_EVENT_ACTION_TYPE;
	}

	public String getOFF_EVENT_ACTION_TYPE_NAME() {
		return OFF_EVENT_ACTION_TYPE_NAME;
	}

	public long getOffActionActiontype() {
		return OFF_ACTION;
	}

	public String getOffActionActiontypeName() {
		return OFF_ACTION_NAME;
	}

	public long getOffDrDynamicReserve() {
		return OFF_DR_DYNAMIC_RESERVE;
	}

	public String getOffDrDynamicReserveName() {
		return OFF_DR_DYNAMIC_RESERVE_NAME;
	}

	public long getOffSpcSortPriceComponent() {
		return OFF_SPC_SORT_PRICE_COMPONENT;
	}

	public String getOffSpcSortPriceComponentName() {
		return OFF_SPC_SORT_PRICE_COMPONENT_NAME;
	}

	public long getOffPpPostProcess() {
		return OFF_PP_POST_PROCESS;
	}

	public String getOffPpPostProcessName() {
		return OFF_PP_POST_PROCESS_NAME;
	}

	public long getOffPcPriceComponent() {
		return OFF_PC_PRICE_COMPONENT;
	}

	public String getOffPcPriceComponentName() {
		return OFF_PC_PRICE_COMPONENT_NAME;
	}

	public long getOFF_BLOCK_BLOCK() {
		return OFF_BLOCK_BLOCK;
	}

	public String getOffBlockBlockName() {
		return OFF_BLOCK_BLOCK_NAME;
	}

	public long getOFF_RT_RATE_TABLE() {
		return OFF_RT_RATE_TABLE;
	}

	public String getOFF_RT_RATE_TABLE_NAME() {
		return OFF_RT_RATE_TABLE_NAME;
	}

	public long getOFF_DT_DECISION_TABLE() {
		return OFF_DT_DECISION_TABLE;
	}

	public String getOFF_DT_DECISION_TABLE_NAME() {
		return OFF_DT_DECISION_TABLE_NAME;
	}

	public long getOFF_NOM_NORMALIZER() {
		return OFF_NOM_NORMALIZER;
	}

	public String getOFF_NOM_NORMALIZER_NAME() {
		return OFF_NOM_NORMALIZER_NAME;
	}

	public long getOFF_OBJECT() {
		return OFF_OBJECT;
	}

	public String getOFF_OBJECT_NAME() {
		return OFF_OBJECT_NAME;
	}

	public long getCTL_PARAMETER() {
		return CTL_PARAMETER;
	}

	public String getCTL_PARAMETER_NAME() {
		return CTL_PARAMETER_NAME;
	}

	public long getCtlUtUnitType() {
		return CTL_UT_UNIT_TYPE;
	}

	public String getCtlUtUnitTypeName() {
		return CTL_UT_UNIT_TYPE_NAME;
	}

	public long getCTL_UT_RESERVE_INFO() {

		return CTL_UT_RESERVE_INFO;
	}

	public long getCtlZdZoneData() {
		return CTL_ZD_ZONE_DATA;
	}

	public String getCtlZdZoneDataName() {
		return CTL_ZD_ZONE_DATA_NAME;
	}

	public static String getCtlUtReserveInfoName() {
		return CTL_UT_RESERVE_INFO_NAME;
	}

	public long getCTL_STATEGROUP() {
		return CTL_STATEGROUP;
	}

	public String getCTL_STATEGROUP_NAME() {
		return CTL_STATEGROUP_NAME;
	}

	public long getCTL_STATETYPE() {
		return CTL_STATETYPE;
	}

	public String getCTL_STATETYPE_NAME() {
		return CTL_STATETYPE_NAME;
	}

	public long getCTL_BL_BAL_TYPE() {
		return CTL_BL_BAL_TYPE;
	}

	public String getCTL_BL_BAL_TYPE_NAME() {
		return CTL_BL_BAL_TYPE_NAME;
	}

	public long getCTL_BL_BAL_TYPE_ACC() {
		return CTL_BL_BAL_TYPE_ACC;
	}

	public String getCTL_BL_BAL_TYPE_ACC_NAME() {
		return CTL_BL_BAL_TYPE_ACC_NAME;
	}

	public long getCTL_GHZ_GEO_HOME_ZONE() {
		return CTL_GHZ_GEO_HOME_ZONE;
	}

	public String getCTL_GHZ_GEO_HOME_ZONE_NAME() {
		return CTL_GHZ_GEO_HOME_ZONE_NAME;
	}

	public long getCTL_TM_TRIGGER_MESSAGE() {
		return CTL_TM_TRIGGER_MESSAGE;
	}

	public String getCTL_TM_TRIGGER_MESSAGE_NAME() {
		return CTL_TM_TRIGGER_MESSAGE_NAME;
	}

	public long getCTL_TD_TRIGGER_DESTINATION() {
		return CTL_TD_TRIGGER_DESTINATION;
	}

	public String getCTL_TD_TRIGGER_DESTINATION_NAME() {
		return CTL_TD_TRIGGER_DESTINATION_NAME;
	}

	public long getCTL_BILLING_CYCLE() {
		return CTL_BILLING_CYCLE;
	}

	public String getCTL_BILLING_CYCLE_NAME() {
		return CTL_BILLING_CYCLE_NAME;
	}

	public static String getUniqueKey(long catType) {

		return CategoryType.class.getName() + "-" + catType;
	}

	public long getCTL_TO_TRIGGER_OCS() {
		return CTL_TO_TRIGGER_OCS;
	}

	public String getCTL_TO_TRIGGER_OCS_NAME() {
		return CTL_TO_TRIGGER_OCS_NAME;
	}

	public long getCTL_SERVICE() {
		return CTL_SERVICE;
	}

	public String getCTL_SERVICE_NAME() {
		return CTL_SERVICE_NAME;
	}

	public long getCTL_BL_BAL_TYPE_ACCOUNT_MAPPING() {
		return CTL_BL_BAL_TYPE_ACCOUNT_MAPPING;
	}

	public String getCTL_BL_BAL_TYPE_ACCOUNT_MAPPING_NAME() {
		return CTL_BL_BAL_TYPE_ACCOUNT_MAPPING_NAME;
	}

	public long getCTL_BL_BAL_TYPE_ACM_MAPPING() {
		return CTL_BL_BAL_TYPE_ACM_MAPPING;
	}

	public String getCTL_BL_BAL_TYPE_ACM_MAPPING_NAME() {
		return CTL_BL_BAL_TYPE_ACM_MAPPING_NAME;
	}

	public long getCTL_CDR_SERVICE() {
		return CTL_CDR_SERVICE;
	}

	public String getCTL_CRD_SERVICE_NAME() {
		return CTL_CDR_SERVICE_NAME;
	}

	public long getOFF_OFFER_SUBSCRIPTION_NORMAL() {
		return OFF_OFFER_SUBSCRIPTION_NORMAL;
	}

	public long getOFF_OFFER_ONETIME_NORMAL() {
		return OFF_OFFER_ONETIME_NORMAL;
	}

	public long getCTL_STATISTIC_ITEM() {
		return CTL_STATISTIC_ITEM;
	}

	public String getCTL_STATISTIC_ITEM_NAME() {
		return CTL_STATISTIC_ITEM_NAME;
	}

	public long getOFF_PC_TYPE() {
		return OFF_PC_TYPE;
	}

	public String getOFF_PC_TYPE_NAME() {
		return OFF_PC_TYPE_NAME;
	}

	public long getCTL_SMS_NOTIFY_TEMPLATE() {
		return CTL_SMS_NOTIFY_TEMPLATE;
	}

	public String getCTL_SMS_NOTIFY_TEMPLATE_NAME() {
		return CTL_SMS_NOTIFY_TEMPLATE_NAME;
	}

	public long getCTL_CSS_USSD_RESPONSE() {
		return CTL_CSS_USSD_RESPONSE;
	}

	public String getCTL_CSS_USSD_RESPONSE_NAME() {
		return CTL_CSS_USSD_RESPONSE_NAME;
	}
	
	
	
}

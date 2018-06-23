package com.viettel.ocs.constant;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

//@ManagedBean(name = "treeTypeBean")
//@ApplicationScoped
public class TreeType {

	public static final int OFFER_OFFER_NUMBER = 10;
	public static final int OFFER_EVENT_NUMBER = 20;
	public static final int OFFER_ACTION_NUMBER = 30;
	public static final int OFFER_DYNAMIC_RESERVE_NUMBER = 40;
	public static final int OFFER_SORT_PRICE_COMPONENT_NUMBER = 50;
	public static final int OFFER_POST_PROCESS_NUMBER = 60;
	public static final int OFFER_PRICE_COMPONENT_NUMBER = 70;
	public static final int OFFER_BLOCK_NUMBER = 80;
	public static final int OFFER_RATE_TABLE_NUMBER = 90;
	public static final int OFFER_DECISION_TABLE_NUMBER = 100;
	public static final int OFFER_NORMALIZER_NUMBER = 110;
	public static final int OFFER_OBJECT_NUMBER = 120;
	public static final int OFFER_PC_TYPE_NUMBER = 440;
	
	public static final int CATALOG_STATE_SET_NUMBER = 200;
	public static final int CATALOG_UNIT_TYPE_NUMBER = 210;
	public static final int CATALOG_BILLING_CYCLE_NUMBER = 220;
	public static final int CATALOG_GEO_HOME_NUMBER = 230;
	public static final int CATALOG_CDR_NUMBER = 240;
	public static final int CATALOG_SERVICE_NUMBER = 250;
	public static final int CATALOG_TRIGGERS_NUMBER = 260;
	public static final int CATALOG_PARAMETER_NUMBER = 270;
	public static final int CATALOG_BALANCE_NUMBER = 280;
	public static final int CATALOG_INTERFACE_NUMBER = 290;
	public static final int CATALOG_ZONE_DATA_NUMBER = 300;
	public static final int CATALOG_REVERSE_INFO_NUMBER = 310;
	public static final int CATALOG_TRIGGER_NUMBER = 320;
	public static final int CATALOG_TRIGGER_MESSAGE_NUMBER = 330;
	public static final int CATALOG_TRIGGER_DESTINATION_NUMBER = 340;
	public static final int CATALOG_TRIGGER_OCS_NUMBER = 350;
	public static final int CATALOG_CDR_SERVICE_NUMBER = 360;
	public static final int CATALOG_CDR_TEMPLATE_NUMBER = 370;
	public static final int CATALOG_CDR_GEN_FILENAME_NUMBER = 380;
	public static final int CATALOG_CDR_PROCESS_CONFIG_NUMBER = 390;
	public static final int CATALOG_STATISTIC_ITEM_NUMBER = 430;
	public static final int CATALOG_SMS_NOTIFY_TEMPLATE_NUMBER = 450;
	public static final int CATALOG_CSS_USSD_RESPONSE_NUMBER = 460;

	// TREE NUMBER FOR POLICY---------------------------------------------
	public static final int POLICY_POLICY_PROFILE_NUMBER = 400;
	public static final int POLICY_PCC_RULE_NUMBER = 410;
	// END TREE NUMBE FO POLICY------------------------------------------
	
	public static final int SYS_SYS_CONFIG_NUMBER = 420;

	public static final String OFFER_TREE_NORMAL_DLG = "OFFER_TREE_NORMAL_DLG";
	public static final String OFFER_TREE_NORMAL_ALL_DLG = "OFFER_TREE_NORMAL_ALL_DLG";
	public static final String OFFER_TREE_SUBCRIPTION_MAIN = "OFFER_TREE_SUBCRIPTION_MAIN";

	public static final String OFFER_OFFER = "OFFER_OFFER";
	public static final String OFFER_EVENT = "OFFER_EVENT";
	public static final String OFFER_EVENT_EVENT = "OFFER_EVENT_EVENT";
	public static final String OFFER_ACTION = "OFFER_ACTION";
	public static final String OFFER_ACTION_TYPE = "OFFER_ACTION_TYPE";
	public static final String OFFER_DYNAMIC_RESERVE = "OFFER_DYNAMIC_RESERVE";
	public static final String OFFER_SORT_PRICE_COMPONENT = "OFFER_SORT_PRICE_COMPONENT";
	public static final String OFFER_POST_PROCESS = "OFFER_POST_PROCESS";
	public static final String OFFER_PRICE_COMPONENT = "OFFER_PRICE_COMPONENT";
	public static final String OFFER_BLOCK = "OFFER_BLOCK";
	public static final String OFFER_RATE_TABLE = "OFFER_RATE_TABLE";
	public static final String OFFER_DECISION_TABLE = "OFFER_DECISION_TABLE";
	public static final String OFFER_NORMALIZER = "OFFER_NORMALIZER";
	public static final String OFFER_OBJECT = "OFFER_OBJECT";
	public static final String OFFER_PACKAGE= "OFFER_PACKAGE";
	public static final String OFFER_PC_TYPE= "OFFER_PC_TYPE";

	public static final String CATALOG_STATE_SET = "CATALOG_STATESET";
	public static final String CATALOG_UNIT_TYPE = "CATALOG_UNIT_TYPE";
	public static final String CATALOG_BILLING_CYCLE = "CATALOG_BILLING_CYCLE";
	public static final String CATALOG_GEO_HOME = "CATALOG_GEO_HOME";
	public static final String CATALOG_GEO_NET = "CATALOG_GEO_NET";
	public static final String CATALOG_CDR = "CATALOG_CDR";
	public static final String CATALOG_SERVICE = "CATALOG_SERVICE";
	public static final String CATALOG_PARAMETER = "CATALOG_PARAMETER";
	public static final String CATALOG_BALANCE = "CATALOG_BALANCE";
	public static final String CATALOG_BALANCE_ACC = "CATALOG_BALANCE_ACC";
	public static final String CATALOG_BALANCES = "CATALOG_BALANCES";
	public static final String CATALOG_BALANCE_ACCOUNT_MAPPING = "CATALOG_BALANCE_ACCOUNT_MAPPING";
	public static final String CATALOG_BALANCE_ACM_MAPPING = "CATALOG_BALANCE_ACM_MAPPING";
	public static final String CATALOG_INTERFACE = "CATALOG_INTERFACE";
	public static final String CATALOG_ZONE_DATA = "CATALOG_ZONEDATA";
	public static final String CATALOG_ZONE_MAP = "CATALOG_ZONE_MAP";
	public static final String CATALOG_RESERVE_INFO = "CATALOG_RESERVE_INFO";
	public static final String CATALOG_CDR_SERVICE = "CATALOG_CDR_SERVICE";
	public static final String CATALOG_CDR_TEMPLATE= "CATALOG_CDR_TEMPLATE";
	public static final String CATALOG_CDR_GEN_FILENAME= "CATALOG_CDR_GEN_FILENAME";
	public static final String CATALOG_CDR_PROCESS_CONFIG= "CATALOG_CDR_PROCESS_CONFIG";
	public static final String CATALOG_STATISTIC_ITEM = "CATALOG_STATISTIC_ITEM";
	public static final String CATALOG_SMS_NOTIFY_TEMPLATE = "CATALOG_SMS_NOTIFY_TEMPLATE";
	public static final String CATALOG_CSS_USSD_RESPONSE = "CATALOG_CSS_USSD_RESPONSE";
	
	public static final String CATALOG_TRIGGER_OCS = "CATALOG_TRIGGER_OCS";
	public static final String CATALOG_TRIGGER_MESSAGE = "CATALOG_TRIGGER_MESSAGE";
	public static final String CATALOG_TRIGGER_DESTINATION = "CATALOG_TRIGGER_DESTINATION";
	// STRING TREE FOR POLICY-----------------------------------------
	public static final String POLICY_POLICY_PROFILE = "POLICY_POLICY_PROFILE";
	public static final String POLICY_PCC_RULE = "POLICY_PCC_RULE";
	// END STRING TREE FOR POLICY--------------------------------------------
	
	public static final String SYS_SYS_CONFIG = "SYS_SYS_CONFIG";
	
	//
	public static final String OFFER_SUBCRIPTION_NORMAL_ONLY = "OFFER_SUBCRIPTION_NORMAL_ONLY";
	/*public static final String CATALOG_TRIGGER_OCS = "CATALOG_TRIGGER_OCS";*/

	public static final List<String> lstTreeType = new ArrayList<String>();
	static {
		lstTreeType.add(OFFER_OFFER);
		lstTreeType.add(OFFER_EVENT);
		lstTreeType.add(OFFER_ACTION);
		lstTreeType.add(OFFER_ACTION_TYPE);
		lstTreeType.add(OFFER_DYNAMIC_RESERVE);
		lstTreeType.add(OFFER_SORT_PRICE_COMPONENT);
		lstTreeType.add(OFFER_POST_PROCESS);
		lstTreeType.add(OFFER_PRICE_COMPONENT);
		lstTreeType.add(OFFER_BLOCK);
		lstTreeType.add(OFFER_RATE_TABLE);
		lstTreeType.add(OFFER_DECISION_TABLE);
		lstTreeType.add(OFFER_NORMALIZER);
		lstTreeType.add(OFFER_OBJECT);
		lstTreeType.add(OFFER_PACKAGE);
		lstTreeType.add(OFFER_TREE_SUBCRIPTION_MAIN);
		lstTreeType.add(OFFER_PC_TYPE);

		lstTreeType.add(CATALOG_STATE_SET);
		lstTreeType.add(CATALOG_UNIT_TYPE);
		lstTreeType.add(CATALOG_BILLING_CYCLE);
		lstTreeType.add(CATALOG_RESERVE_INFO);
		lstTreeType.add(CATALOG_GEO_HOME);
		lstTreeType.add(CATALOG_CDR);
		lstTreeType.add(CATALOG_SERVICE);
		lstTreeType.add(CATALOG_PARAMETER);
		lstTreeType.add(CATALOG_BALANCE);
		lstTreeType.add(CATALOG_INTERFACE);
		lstTreeType.add(CATALOG_ZONE_DATA);
		lstTreeType.add(CATALOG_TRIGGER_OCS);
		lstTreeType.add(CATALOG_TRIGGER_MESSAGE);
		lstTreeType.add(CATALOG_TRIGGER_DESTINATION);
		lstTreeType.add(CATALOG_CDR_SERVICE);
		lstTreeType.add(CATALOG_CDR_TEMPLATE);
		lstTreeType.add(CATALOG_CDR_GEN_FILENAME);
		lstTreeType.add(CATALOG_CDR_PROCESS_CONFIG);
		lstTreeType.add(CATALOG_STATISTIC_ITEM);
		lstTreeType.add(CATALOG_SMS_NOTIFY_TEMPLATE);
		lstTreeType.add(CATALOG_CSS_USSD_RESPONSE);
		
		// Add tree policy in lstTreeType---------------------------
		lstTreeType.add(POLICY_POLICY_PROFILE);
		lstTreeType.add(POLICY_PCC_RULE);
		//end add tree policy------------------------------------------

		lstTreeType.add(CATALOG_BALANCE_ACC);
		lstTreeType.add(CATALOG_BALANCES);
		lstTreeType.add(CATALOG_BALANCE_ACCOUNT_MAPPING);
		lstTreeType.add(CATALOG_BALANCE_ACM_MAPPING);

	}
	
	
	//get for Policy-------------------------------------------------
	public  String getPolicyPolicyProfile() {
		return POLICY_POLICY_PROFILE;
	}
	public  String getPolicyPccRule() {
		return POLICY_PCC_RULE;
	}
	// end get policy---------------------------------------
	
	public static String getCatalogReserveInfo() {
		return CATALOG_RESERVE_INFO;
	}

	public List<String> getLstTreeType() {
		return lstTreeType;
	}

	public String getOFFER_OFFER() {
		return OFFER_OFFER;
	}

	public String getOFFER_EVENT() {
		return OFFER_EVENT;
	}

	public String getOFFER_ACTION() {
		return OFFER_ACTION;
	}

	public String getOFFER_DYNAMIC_RESERVE() {
		return OFFER_DYNAMIC_RESERVE;
	}

	public String getOFFER_SORT_PRICE_COMPONENT() {
		return OFFER_SORT_PRICE_COMPONENT;
	}

	public String getOFFER_POST_PROCESS() {
		return OFFER_POST_PROCESS;
	}

	public String getOFFER_PRICE_COMPONENT() {
		return OFFER_PRICE_COMPONENT;
	}

	public String getOFFER_BLOCK() {
		return OFFER_BLOCK;
	}

	public String getOFFER_RATE_TABLE() {
		return OFFER_RATE_TABLE;
	}

	public String getOFFER_DECISION_TABLE() {
		return OFFER_DECISION_TABLE;
	}

	public String getOFFER_NORMALIZER() {
		return OFFER_NORMALIZER;
	}

	public String getCATALOG_STATE_SET() {
		return CATALOG_STATE_SET;
	}

	public String getCATALOG_UNIT_TYPE() {
		return CATALOG_UNIT_TYPE;
	}

	public String getCATALOG_GEO_HOME() {
		return CATALOG_GEO_HOME;
	}

	public String getCATALOG_CDR() {
		return CATALOG_CDR;
	}

	public String getCATALOG_PARAMETER() {
		return CATALOG_PARAMETER;
	}

	public String getCATALOG_BALANCE() {
		return CATALOG_BALANCE;
	}

	public String getCATALOG_INTERFACE() {
		return CATALOG_INTERFACE;
	}

	public String getCATALOG_ZONE_DATA() {
		return CATALOG_ZONE_DATA;
	}

	public String getCATALOG_TRIGGER_MESSAGE() {
		return CATALOG_TRIGGER_MESSAGE;
	}

	public String getCATALOG_TRIGGER_DESTINATION() {
		return CATALOG_TRIGGER_DESTINATION;
	}

	public int getCATALOG_BILLING_CYCLE_NUMBER() {
		return CATALOG_BILLING_CYCLE_NUMBER;
	}

	public String getCATALOG_BILLING_CYCLE() {
		return CATALOG_BILLING_CYCLE;
	}

	public static String getOFFER_OBJECT() {
		return OFFER_OBJECT;
	}
	public String getCATALOG_BALANCES() {
		return CATALOG_BALANCES;
	}
	public String getCATALOG_BALANCE_ACC() {
		return CATALOG_BALANCE_ACC;
	}
	public int getCATALOG_TRIGGER_OCS_NUMBER() {
		return CATALOG_TRIGGER_OCS_NUMBER;
	}
	public String getCATALOG_TRIGGER_OCS() {
		return CATALOG_TRIGGER_OCS;
	}
	public int getCATALOG_SERVICE_NUMBER() {
		return CATALOG_SERVICE_NUMBER;
	}
	public String getCATALOG_SERVICE() {
		return CATALOG_SERVICE;
	}
	public String getCATALOG_BALANCE_ACCOUNT_MAPPING() {
		return CATALOG_BALANCE_ACCOUNT_MAPPING;
	}
	public  String getCATALOG_BALANCE_ACM_MAPPING() {
		return CATALOG_BALANCE_ACM_MAPPING;
	}
	public int getCATALOG_CDR_SERVICE_NUMBER() {
		return CATALOG_CDR_SERVICE_NUMBER;
	}
	public String getCATALOG_CDR_SERVICE() {
		return CATALOG_CDR_SERVICE;
	}
	
	public int getCATALOG_CDR_TEMPLATE_NUMBER() {
		return CATALOG_CDR_TEMPLATE_NUMBER;
	}
	
	public String getCATALOG_CDR_TEMPLATE() {
		return CATALOG_CDR_TEMPLATE;
	}
	
	public int getCATALOG_CDR_GEN_FILENAME_NUMBER() {
		return CATALOG_CDR_GEN_FILENAME_NUMBER;
	}
	
	public String getCATALOG_CDR_GEN_FILENAME() {
		return CATALOG_CDR_GEN_FILENAME;
	}
	
	public int getCATALOG_CDR_PROCESS_CONFIG_NUMBER() {
		return CATALOG_CDR_PROCESS_CONFIG_NUMBER;
	}
	
	public String getCATALOG_CDR_PROCESS_CONFIG() {
		return CATALOG_CDR_PROCESS_CONFIG;
	}
	
	public int getCATALOG_STATISTIC_ITEM_NUMBER() {
		return CATALOG_STATISTIC_ITEM_NUMBER;
	}
	public String getCATALOG_STATISTIC_ITEM() {
		return CATALOG_STATISTIC_ITEM;
	}
	
	public int getOFFER_PC_TYPE_NUMBER() {
		return OFFER_PC_TYPE_NUMBER;
	}
	public String getOFFER_PC_TYPE() {
		return OFFER_PC_TYPE;
	}
	
	public int getCATALOG_SMS_NOTIFY_TEMPLATE_NUMBER() {
		return CATALOG_SMS_NOTIFY_TEMPLATE_NUMBER;
	}
	public String getCATALOG_SMS_NOTIFY_TEMPLATE() {
		return CATALOG_SMS_NOTIFY_TEMPLATE;
	}
	
	public int getCATALOG_CSS_USSD_RESPONSE_NUMBER() {
		return CATALOG_CSS_USSD_RESPONSE_NUMBER;
	}
	public String getCATALOG_CSS_USSD_RESPONSE() {
		return CATALOG_CSS_USSD_RESPONSE;
	}
	
}

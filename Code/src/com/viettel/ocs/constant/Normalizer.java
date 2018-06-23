package com.viettel.ocs.constant;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

public class Normalizer {

	public static final long RETURNED_VALUE_IF_ERROR = -1001;
	public static final long NON_DECISION = -1002;
	public static final long DEFAULT_OBJECT_ID_IF_CREATE_NEW = -1000;
	public static final long COMPARISON_VALUE_FOR_TARIFF = 900000;
	// (15phut)
	public static final String STR_PRAM = "84";

	public class NormalizerType {

		public static final int STRING_NORMALIZER = 0;
		public static final int STRING_MATCH_NORMALIZER = 1;
		public static final int NUMBER_NORMALIZER = 2;
		public static final int CHECK_REGISTER_LIST_NORMALIZER = 3;
		public static final int TIME_NORMALIZER = 4;
		public static final int DATE_NORMALIZER = 5;
		public static final int QUANTITY_NORMALIZER = 6;
		public static final int BALANCE_NORMALIZER = 7;
		public static final int ZONE_NORMALIZER = 8;
		public static final int CHECK_IN_LIST_NORMALIZER = 9;
		public static final int ACMBALANCE_NORMALIZER = 10;
		public static final int NUMBER_PARAMETER_NORMALIZER = 11;
	}

	public class NormalizerZoneType {

		public static final int ZONE = 0;
		public static final int ZONE_MAP = 1;
		public static final int GEO_NET_ZONE = 2;
		public static final int GEO_HOME_ZONE = 3;
	}

	public class NormState {

		public static final int STATIC_PER_EVENT = 0;
		public static final int STATIC_PER_SESSION = 1;
		public static final int DYNAMIC = 2;
	}

	public class StringNormCompareType {

		public static final int EXACT = 1;
		public static final int PREFIX = 2;
		public static final int SUFFIX = 3;
		public static final int CONTAINS = 4;
	}

	public class NumberNormCompareType {

		public static final int EQUAL = 1;
		public static final int GREATER = 2;
		public static final int LESS = 3;
		public static final int GREATER_OR_EQUAL = 4;
		public static final int LESS_OR_EQUAL = 5;

		public static final String EQUAL_VALUE = "EQUAL";
		public static final String GREATER_VALUE = "GREATER";
		public static final String LESS_VALUE = "LESS";
		public static final String GREATER_OR_EQUAL_VALUE = "GREATER_OR_EQUAL";
		public static final String LESS_OR_EQUAL_VALUE = "LESS_OR_EQUAL";
	}

	public class DateNormDateType {

		public static final int NONE = 1;
		public static final int CURRENT_TIME = 2;
		public static final int DELTA = 3;
	}

	public class NormQueryState {

		public static final int NONE = 0;
		public static final int QUERY_SUCCESS = 1;
		public static final int NOT_EXIST = 2;
		public static final int REJECT = 3;
		public static final int NOT_ENOUGH_INFO = 4;

	}

	public class GetPathState {

		public static final int SUCCESS = 1;
		public static final int WRONG_PATH = 2;
		public static final int NOT_ENOUGH_INFO = 3;
		public static final int NOT_EXIST = 4;
	}

	public class RateTableState {

		public static final int SKIP = 1;
		public static final int DENY = 2;
		public static final int EXIT = 4;
		public static final int FORMULA = 3;

		public static final String SKIP_NAME = "Skip";
		public static final String DENY_NAME = "Deny";
		public static final String EXIT_NAME = "Exit";
		public static final String FORMULA_NAME = "Formula";
	}

	public class ParamOwnerLevel {
		public static final int CUSTOMER = 1;
		public static final int SUBSCRIBER = 2;
		public static final int GROUP = 3;
		public static final int MEMBERSHIP = 4;
		public static final int OFFER = 6;
		public static final int SYSTEM = 7;
		
		public static final String CUSTOMER_VALUE = "Customer";
		public static final String SUBSCRIBER_VALUE = "Subsriber";
		public static final String GROUP_VALUE = "Group";
		public static final String MEMBERSHIP_VALUE = "Membership";
		public static final String OFFER_VALUE = "Offer";
		public static final String SYSTEM_VALUE = "System";
	}

	public class ParamType {

		public static final int QUOTA = 1;
		public static final int TEMPLATE = 2;
		public static final int CBA100 = 100;
		public static final int CBA101 = 101;
		public static final int NORMAL = 0;

		public static final String QUOTA_VALUE = "Quota";
		public static final String TEMPLATE_VALUE = "Template";
		public static final String CBA100_VALUE = "CBA";
		public static final String CBA101_VALUE = "CBA";
		public static final String NORMAL_VALUE = "Normal";

	}

	public class FormulaType {

		public static final int NORMAL_TYPE = 0;
		public static final int SKIP = 1;
		public static final int DENY = 2;
		public static final int EXIT = 3;
		public static final int SORT_TYPE = 4;
		public static final int DYNAMIC_RESERVE = 5;

		public static final String NORMAL_TYPE_NAME = "NORMAL_TYPE";
		public static final String SKIP_NAME = "SKIP";
		public static final String DENY_NAME = "DENY";
		public static final String EXIT_NAME = "EXIT";
		public static final String SORT_TYPE_NAME = "SORT_TYPE";
		public static final String DYNAMIC_RESERVE_NAME = "DYNAMIC_RESERVE";
	}

	public class NormalizingValueType {

		public static final int NONE = 0;
		public static final int MINUTE_TO_SECOND = 1;
		public static final int HOUR_TO_SECOND = 2;
		public static final int DAY_TO_SECOND = 3;
		public static final int WEEK_TO_SECOND = 4;
		public static final int END_OF_MIN = 5;
		public static final int END_OF_HOUR = 6;
		public static final int END_OF_DAY = 7;
		public static final int END_OF_WEEK = 8;
		public static final int END_OF_MONTH = 9;
		public static final int MBYTE_TO_KBYTE = 10;
		public static final int GBYTE_TO_KBYTE = 11;
		public static final int CURR_TIME = 12;
		public static final int HOURS_FROM_CURR_TIME = 13;
		public static final int HOURS_FROM_START_OF_DAY = 14;

		public static final String NONE_NAME = "--NONE--";
		public static final String MINUTE_TO_SECOND_NAME = "MINUTE_TO_SECOND";
		public static final String HOUR_TO_SECOND_NAME = "HOUR_TO_SECOND";
		public static final String DAY_TO_SECOND_NAME = "DAY_TO_SECOND";
		public static final String WEEK_TO_SECOND_NAME = "WEEK_TO_SECOND";
		public static final String END_OF_MIN_NAME = "END_OF_MIN";
		public static final String END_OF_HOUR_NAME = "END_OF_HOUR";
		public static final String END_OF_DAY_NAME = "END_OF_DAY";
		public static final String END_OF_WEEK_NAME = "END_OF_WEEK";
		public static final String END_OF_MONTH_NAME = "END_OF_MONTH";
		public static final String MBYTE_TO_KBYTE_NAME = "MBYTE_TO_KBYTE";
		public static final String GBYTE_TO_KBYTE_NAME = "GBYTE_TO_KBYTE";
		public static final String CURR_TIME_NAME = "CURR_TIME";
		public static final String HOURS_FROM_CURR_TIME_NAME = "HOURS_FROM_CURR_TIME";
		public static final String HOURS_FROM_START_OF_DAY_NAME = "HOURS_FROM_START_OF_DAY";
	}

	public class TypeOfBalType {

		public static final int SINGLE = 0;
		public static final int MULTIBAL = 1;
		public static final int PERIODIC = 2;

		public static final String SINGLE_NAME = "Single";
		public static final String MULTIBAL_NAME = "Multibal";
		public static final String PERIODIC_NAME = "Periodic";
	}

	public class BlockType {

		public static final int GRANT = 1;
		public static final int CHARGE = 0;
		public static final int SET = 2;
		public static final int POLICY = 3;
		public static final String GRANT_NAME = "GRANT";
		public static final String CHARGE_NAME = "CHARGE";
		public static final String SET_NAME = "SET";
		public static final String POLICY_NAME = "POLICY";
	}

	public class BlockAffectedObjectType {

		public static final long NONE = 0;
		public static final long CUST = 1;
		public static final long SUB = 2;
		public static final long BALANCE = 3;
		public static final long ACMBAL = 4;
		public static final long PARAMETER = 5;
		public static final long OFFER = 6;
		public static final long GROUP = 7;
		public static final long OCSMSG = 9;

		public static final String NONE_NAME = "--NONE--";
		public static final String CUST_NAME = "CUST";
		public static final String SUB_NAME = "SUB";
		public static final String BALANCE_NAME = "BALANCE";
		public static final String ACMBAL_NAME = "ACMBAL";
		public static final String PARAMETER_NAME = "PARAMETER";
		public static final String GROUP_NAME = "GROUP";
		public static final String OFFER_NAME = "OFFER";
		public static final String OCSMSG_NAME = "OCSMSG";
	}

	public class SortType {

		public static final int NORMAL = 0;
		public static final int SORT_BY_BAL_EXPIRE_DATE = 1;
	}

	public class SpecialCalMethod {

		public static final int NORMAL_METHOD = 0;
		public static final int REST_TIME_OF_CYCLE = 1;

	}

	public class CheckCode {

		public static final int NORMAL_CHECKCODE = 0;
		public static final int WAIT_MORE_INFO = 1;
		public static final int RESORT_PC = 2;
		public static final int RESIMULATE = 3;
		public static final int OTHER_PC = 4;
		public static final int OTHER_ACTION = 5;
		public static final int STOP = 6;
		public static final int STOP_CURRENT_PC = 7;
		public static final int STOP_CURRENT_ACTION = 8;
		public static final int DISCARD_RESORT_PC = 9;
		public static final int WRONG_CONFIGURATION = 10;
		public static final int POLICYID_CHANGE = 11;
	}

	public class ErrorCode {

		public static final int SUCCESS = 201;
		public static final int ERROR = 202;
		public static final int BALANCE_NOT_ENOUGH = 203;
		public static final int INFO_NOT_ENOUGH = 204;
		public static final int WRONG_CONFIGURATION = 205;
		public static final int ERROR_CODE_IN_FORMULA = 206;

	}

	public class BlockComponentType {

		public static final int BASIC_COMPONENT = 1;
		public static final int DISCOUNT_COMPONENT = 2;
		public static final int CONDITIONAL_COMPONENENT = 3;
	}

	public class FindBlockFormulaListState {

		public static final int NONE = -1;
		public static final int NORMAL = 0;
		public static final int DENY = 2;
		public static final int EXIT = 1;
		public static final int ERROR_IN_FORMULA = 3;
	}

	public class FormulaErrorCode {

		public static final int SUCESS = 100001;
		public static final int OTHER = 100002;
	}

	public class ObjectState {

		public static final long ACTIVE = 1;
		public static final long INACTIVE = 0;
		public static final long SUSPEND = 2;
	}

	public class SubState {

		public static final int ACTIVE = 3;
		public static final int IDLE = 2;
		public static final int TERMINATE = 9;
		public static final int SUSPEND = 4;
		public static final int DISABLE = 6;
	}

	public class TriggerType {

		public static final int GGSN = 1;
		public static final int TRIGGER_PROCESS = 2;
	}

	public class CRAConstant {

		public class MsgType {

			public static final int AUTHENTICATION_MSG = 1;
			public static final int CONNECTION_MSG = 2;
			public static final int RATING_MSG = 3;
		}

		public class ProtocolId {

			public static final int DCC = 1;
			public static final int SOAP = 2;
			public static final int MML = 3;
			public static final int RADIUS = 4;
			public static final int INTERNAL = 5;
		}

		public class CCRequestType {

			public static final int INIT = 1;
			public static final int UPDATE = 2;
			public static final int TERMINATE = 3;
		}

		public class ServiceType {

			public static final int VOICE = 1;
			public static final int DATA = 2;
			public static final int SMS = 3;
			public static final int MMS = 4;
			public static final int OTHER_SERVICE = 5;
			public static final int RECURRING = 6;
		}
	}

	public class TypeRateTable {

		public static final long BASIC = 1;
		public static final long DISCOUNT = 2;
		public static final long CONDITION = 3;
	}

//	public class preFunction {
//		public static final int PREFIX = 1;
//		public static final int SUFFIX = 2;
//		public static final int SUB_STRING = 3;
//		public static final int STANDARD_MSISDN = 4;
//		public static final int GET_LIST_SIZE = 5;
//		public static final int VALIDATE_FOR_LIST = 6;
//		public static final int VALIDATE_FOR_OBJECT = 7;
//		public static final int VALIDATE_WITH_STATE_FOR_LIST = 8;
//		public static final int VALIDATE_WITH_STATE_FOR_OBJECT = 9;
//		public static final int CONTAIN_CALLED_NUMBER = 10;
//		public static final int CONTAINS_GROUP_NAME = 11;
//		public static final int CONTAINS_STATE_TYPE = 12;
//		public static final int GET_ACMBAL_BY_BILLING_CYCLE = 13;
//		public static final int CONTAINS_CALLING_NUMBER = 14;
//		public static final int GET_CELLS_FROM_LIST_ZONE_STRING = 15;
//		public static final int GET_CELLS_FROM_LIST_ZONE_STRING_BY_ZONEID = 16;
//		public static final int GET_CELLS_FROM_LIST_CELL_STRING = 17;
//		public static final int GET_ZONES_FROM_LIST_ZONE_STRING = 18;
//		public static final int GET_AVAILABLE_AMOUNT = 19;
//		public static final int GET_TOTAL_CONSUME = 20;
//		public static final int GET_TOTAL_VALUE_OF_PREVIOUS_ACMBALS = 21;
//		public static final int GET_DAY_OF_MONTH = 22;
//		public static final int GET_MONTH_OF_YEAR = 23;
//		public static final int GET_ZONE = 24;
//
//		public static final String PREFIX_NAME = "prefix(Int endIndex)";
//		public static final String SUFFIX_NAME = "suffix(Int startIndex)";
//		public static final String SUB_STRING_NAME = "subString(Int startIndex, Int endIndex)";
//		public static final String STANDARD_MSISDN_NAME = "standardMsisdn()";
//		public static final String GET_LIST_SIZE_NAME = "getListSize()";
//		public static final String VALIDATE_FOR_LIST_NAME = "validateForList()";
//		public static final String VALIDATE_FOR_OBJECT_NAME = "validateForObject()";
//		public static final String VALIDATE_WITH_STATE_FOR_LIST_NAME = "validateWithStateForList()";
//		public static final String VALIDATE_WITH_STATE_FOR_OBJECT_NAME = "validateWithStateForObject()";
//		public static final String CONTAIN_CALLED_NUMBER_NAME = "containCalledNumber()";
//		public static final String CONTAINS_GROUP_NAME_NAME = "containsGroupName()";
//		public static final String CONTAINS_STATE_TYPE_NAME = "containsStateType(Int stateTypeValue)";
//		public static final String GET_ACMBAL_BY_BILLING_CYCLE_NAME = "getAcmBalByBillingCycle(Int billingIndex)";
//		public static final String CONTAINS_CALLING_NUMBER_NAME = "containsCallingNumber()";
//		public static final String GET_CELLS_FROM_LIST_ZONE_STRING_NAME = "getCellsFromListZoneString()";
//		public static final String GET_CELLS_FROM_LIST_ZONE_STRING_BY_ZONEID_NAME = "getCellsFromListZoneStringByZoneId(int ZoneId)";
//		public static final String GET_CELLS_FROM_LIST_CELL_STRING_NAME = "getCellsFromListCellString()";
//		public static final String GET_ZONES_FROM_LIST_ZONE_STRING_NAME = "getZonesFromListZoneString()";
//		public static final String GET_AVAILABLE_AMOUNT_NAME = "getAvailableAmount()";
//		public static final String GET_TOTAL_CONSUME_NAME = "getTotalConsume()";
//		public static final String GET_TOTAL_VALUE_OF_PREVIOUS_ACMBALS_NAME = "getTotalValueOfPreviousAcmBals(int balTypeId ,int startCycleIndex,int endCycleIndex)";
//		public static final String GET_DAY_OF_MONTH_NAME = "getDayOfMonth()";
//		public static final String GET_MONTH_OF_YEAR_NAME = "getMonthOfYear()";
//		public static final String GET_ZONE_NAME = "getZone(int zoneId)";
//
//		public static final String PREFIX_CLASS_NAME = "prefix";
//		public static final String SUFFIX_CLASS_NAME = "suffix";
//		public static final String SUB_STRING_CLASS_NAME = "subString";
//		public static final String STANDARD_MSISDN_CLASS_NAME = "standardMsisdn";
//		public static final String GET_LIST_SIZE_CLASS_NAME = "getListSize";
//		public static final String VALIDATE_FOR_LIST_CLASS_NAME = "validateForList";
//		public static final String VALIDATE_FOR_OBJECT_CLASS_NAME = "validateForObject";
//		public static final String VALIDATE_WITH_STATE_FOR_LIST_CLASS_NAME = "validateWithStateForList";
//		public static final String VALIDATE_WITH_STATE_FOR_OBJECT_CLASS_NAME = "validateWithStateForObject";
//		public static final String CONTAIN_CALLED_NUMBER_CLASS_NAME = "containCalledNumber";
//		public static final String CONTAINS_GROUP_NAME_CLASS_NAME = "containsGroupName";
//		public static final String CONTAINS_STATE_TYPE_CLASS_NAME = "containsStateType";
//		public static final String GET_ACMBAL_BY_BILLING_CYCLE_CLASS_NAME = "getAcmBalByBillingCycle";
//		public static final String CONTAINS_CALLING_NUMBER_CLASS_NAME = "containsCallingNumber";
//		public static final String GET_CELLS_FROM_LIST_ZONE_STRING_CLASS_NAME = "getCellsFromListZoneString";
//		public static final String GET_CELLS_FROM_LIST_ZONE_STRING_BY_ZONEID_CLASS_NAME = "getCellsFromListZoneStringByZoneId";
//		public static final String GET_CELLS_FROM_LIST_CELL_STRING_CLASS_NAME = "getCellsFromListCellString";
//		public static final String GET_ZONES_FROM_LIST_ZONE_STRING_CLASS_NAME = "getZonesFromListZoneString";
//		public static final String GET_AVAILABLE_AMOUNT_CLASS_NAME = "getAvailableAmount";
//		public static final String GET_TOTAL_CONSUME_CLASS_NAME = "getTotalConsume";
//		public static final String GET_TOTAL_VALUE_OF_PREVIOUS_ACMBALS_CLASS_NAME = "getTotalValueOfPreviousAcmBals";
//		public static final String GET_DAY_OF_MONTH_CLASS_NAME = "getDayOfMonth";
//		public static final String GET_MONTH_OF_YEAR_CLASS_NAME = "getMonthOfYear";
//		public static final String GET_ZONE_CLASS_NAME = "getZone";
//
//	}

	public class UsingNormParam {
		public static final String END_IS_PARAMETER = "endIsParameter:true";
		public static final String START_IS_PARAMETER = "startIsParameter:true";

		public static final String NOT_END_IS_PARAMETER = "endIsParameter:false";
		public static final String NOT_START_IS_PARAMETER = "startIsParameter:false";

		public static final String PARAMETER_ID = "parameterId:";
		public static final String IS_USING_PARAMETER = "isUsingParameter:true";
		public static final String NOT_IS_USING_PARAMETER = "isUsingParameter:false";
	}

}

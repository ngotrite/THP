package com.viettel.ocs.constant;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.faces.bean.ManagedBean;

public class ContantsUtil {

	public class StateType {
		public static final int BASIC_TYPE = 0;
		public static final int EXTRA_TYPE = 1;
		public static final String BASIC_TYPE_VALUE = "Basic Type";
		public static final String EXTRA_TYPE_VALUE = "Extra Type";
	}

	public class Theme {
		public static final String DEFAULT_THEME = "defaultTheme";
		public static final String BLUE_THEME = "blueTheme";
		public static final String SEA_THEME = "seaTheme";
		public static final String PURPLE_THEME = "purpleTheme";
		public static final String ORANGE_THEME = "orangeTheme";

		public static final String DEFAULT_THEME_COLOR = "#00C292";
		public static final String BLUE_THEME_COLOR = "#03A9F3";
		public static final String SEA_THEME_COLOR = "#3C8DBC";
		public static final String PURPLE_THEME_COLOR = "#8923F9";
		public static final String ORANGE_THEME_COLOR = "#F39A0D";
	}

	public static class OfferState {
		public static final long IN_ACTIVE = 0;
		public static final long ACTIVE = 1;
		public static final long SUSPEND = 2;
		public static final long REMOVED = 3;
		public static final long TESTING = 5;

		public static final String IN_ACTIVE_NAME = "InActive";
		public static final String ACTIVE_NAME = "Active";
		public static final String SUSPEND_NAME = "Suspend";
		public static final String REMOVED_NAME = "Removed";
		public static final String TESTING_NAME = "Testing";

		public static Map<Long, String> getOfferStates() {

			Map<Long, String> map = new LinkedHashMap<>();

			map.put(IN_ACTIVE, IN_ACTIVE_NAME);
			map.put(ACTIVE, ACTIVE_NAME);
			map.put(SUSPEND, SUSPEND_NAME);
			map.put(REMOVED, REMOVED_NAME);
			map.put(TESTING, TESTING_NAME);

			return map;
		}
	}

	public static class RedirectSetting {
		public static final long NORMAL = 1;
		public static final long SUSPEND = 2;

		public static final String NORMAL_NAME = "Normal";
		public static final String SUSPEND_NAME = "Suspend";

		public static Map<Long, String> getRedirectSetting() {
			Map<Long, String> map = new LinkedHashMap<>();

			map.put(NORMAL, NORMAL_NAME);
			map.put(SUSPEND, SUSPEND_NAME);

			return map;
		}
	}

	public static class OfferPackage {

		public static class status {
			public static final long IN_ACTIVE = 0;
			public static final long ACTIVE = 1;

			public static final String IN_ACTIVE_NAME = "InActive";
			public static final String ACTIVE_NAME = "Active";

			public static Map<Long, String> getStatus() {
				Map<Long, String> map = new LinkedHashMap<>();

				map.put(IN_ACTIVE, IN_ACTIVE_NAME);
				map.put(ACTIVE, ACTIVE_NAME);

				return map;
			}
		}
		
		public static class offerPkgType {
			
			public static final long NORMAL = 1;
			public static final long ADD_ON = 2;

			public static final String NORMAL_NAME = "Normal Pkg";
			public static final String ADD_ON_NAME = "Add-On Pkg";
		}
	}

	@ManagedBean
	public static class OfferType {

		public static final long MAIN = 1;
		public static final long NORMAL = 2;
		public static final long FTTH = 3;
		public static final long SERVICE = 4;
		public static final long TEMPLATE = 5;
		public static final long COMPILED = 6;
		public static final long BLACKLIST = 7;
		public static final long FELLOW_NUMBER = 8;

		public static final String MAIN_NAME = "Main Offer";
		public static final String NORMAL_NAME = "Normal Offer";
		public static final String FTTH_NAME = "FTTH Offer";
		public static final String SERVICE_NAME = "Service Offer";
		public static final String TEMPLATE_NAME = "Template Offer";
		public static final String COMPILED_NAME = "Compiled Offer";
		public static final String BLACKLIST_NAME = "Blacklist";
		public static final String FELLOW_NUMBER_NAME = "Fellow Number";

		public static Map<Long, String> getOfferTypeAll() {

			Map<Long, String> map = new LinkedHashMap<>();

			map.put(MAIN, MAIN_NAME);
			map.put(NORMAL, NORMAL_NAME);
			map.put(FTTH, FTTH_NAME);
			map.put(SERVICE, SERVICE_NAME);
			map.put(TEMPLATE, TEMPLATE_NAME);
			map.put(COMPILED, COMPILED_NAME);
			map.put(BLACKLIST, BLACKLIST_NAME);
			map.put(FELLOW_NUMBER, FELLOW_NUMBER_NAME);

			return map;
		}

		public static Map<Long, String> getOfferTypeForNorm() {

			Map<Long, String> map = new LinkedHashMap<>();

			map.put(NORMAL, NORMAL_NAME);
			map.put(FTTH, FTTH_NAME);
			map.put(SERVICE, SERVICE_NAME);
			map.put(BLACKLIST, BLACKLIST_NAME);
			map.put(FELLOW_NUMBER, FELLOW_NUMBER_NAME);

			return map;
		}
	}

	public class Status {
		public static final int DISABLE = 0;
		public static final int ENABLE = 1;
	}

	public static class PaymentType {

		public static final long PAYMENT_TYPE_POSTPAID = 1;
		public static final long PAYMENT_TYPE_PREPAID = 2;
		public static final long PAYMENT_TYPE_FTTH = 3;
		public static final long PAYMENT_TYPE_HYBRID = 4;

		public static final String PAYMENT_TYPE_POSTPAID_NAME = "Postpaid";
		public static final String PAYMENT_TYPE_PREPAID_NAME = "Prepaid";
		public static final String PAYMENT_TYPE_FTTH_NAME = "FTTH";
		public static final String PAYMENT_TYPE_HYBRID_NAME = "Hybrid";

		public static Map<Long, String> getPaymentTypes() {

			Map<Long, String> map = new LinkedHashMap<>();

			map.put(PAYMENT_TYPE_POSTPAID, PAYMENT_TYPE_POSTPAID_NAME);
			map.put(PAYMENT_TYPE_PREPAID, PAYMENT_TYPE_PREPAID_NAME);
			map.put(PAYMENT_TYPE_FTTH, PAYMENT_TYPE_FTTH_NAME);
			map.put(PAYMENT_TYPE_HYBRID, PAYMENT_TYPE_HYBRID_NAME);

			return map;
		}
	}

	public class EffectDateType {
		public static final int START_DATE_FIELD = 0;
		public static final int FROM_PROVISIONING = 1;
		public static final int FIRST_EVENT = 2;
		public static final int BILLING_CYCLE = 3;
		public static final int PURCHASE_DATE = 4;
		public static final int START_OF_HOUR = 5;
		public static final int START_OF_DAY = 6;
		public static final int START_OF_WEEK = 7;
		public static final int START_OF_MONTH = 8;
		public static final int START_OF_YEAR = 9;
		public static final int HOURS_FROM_START_OF_DAY = 10;
		public static final int DAYS_FROM_START_OF_WEEK = 11;
		public static final int WEEKS_FROM_START_OF_MONTH = 12;
		public static final int MONTHS_FROM_START_OF_YEAR = 13;

		public static final String START_DATE_FIELD_NAME = "Start Date Field";
		public static final String FROM_PROVISIONING_NAME = "From Provisioning";
		public static final String FIRST_EVENT_NAME = "First Event";
		public static final String BILLING_CYCLE_NAME = "Billing Cycle";
		public static final String PURCHASE_DATE_NAME = "Purchase Date";
		public static final String START_OF_HOUR_NAME = "Start Of Hour";
		public static final String START_OF_DAY_NAME = "Start Of Day";
		public static final String START_OF_WEEK_NAME = "Start Of Week";
		public static final String START_OF_MONTH_NAME = "Start Of Month";
		public static final String START_OF_YEAR_NAME = "Start Of Year";
		public static final String HOURS_FROM_START_OF_DAY_NAME = "Hours From Start Of Day";
		public static final String DAYS_FROM_START_OF_WEEK_NAME = "Days From Start Of Week";
		public static final String WEEKS_FROM_START_OF_MONTH_NAME = "Weeks From Start Of Month";
		public static final String MONTHS_FROM_START_OF_YEAR_NAME = "Months From Start Of Year";
	}

	public class ExpireDateType {
		public static final int EXPIRE_DATE_FIELD = 0;
		public static final int FROM_PROVISIONING = 1;
		public static final int BILLING_CYCLE = 2;
		public static final int END_OF_HOUR = 3;
		public static final int END_OF_DAY = 4;
		public static final int END_OF_WEEK = 5;
		public static final int END_OF_MONTH = 6;
		public static final int END_OF_YEAR = 7;
		public static final int HOURS = 8;
		public static final int DAYS = 9;
		public static final int WEEKS = 10;
		public static final int MONTHS = 11;
		public static final int YEARS = 12;

		public static final String EXPIRE_DATE_FIELD_NAME = "Expire Date Field";
		public static final String FROM_PROVISIONING_NAME = "From Provisioning";
		public static final String BILLING_CYCLE_NAME = "Billing Cycle";
		public static final String END_OF_HOUR_NAME = "End Of Hour";
		public static final String END_OF_DAY_NAME = "End Of_Day";
		public static final String END_OF_WEEK_NAME = "End Of Week";
		public static final String END_OF_MONTH_NAME = "End Of Month";
		public static final String END_OF_YEAR_NAME = "End Of Year";
		public static final String HOURS_NAME = "Hours";
		public static final String DAYS_NAME = "Days";
		public static final String WEEKS_NAME = "Weeks";
		public static final String MONTHS_NAME = "Months";
		public static final String YEARS_NAME = "Years";

	}

	public class PeriodicType {
		public static final int BILLING_CYCLE = 0;
		public static final int HOURS = 1;
		public static final int DAYS = 2;
		public static final int WEEKS = 3;
		public static final int MONTHS = 4;
		public static final int YEARS = 5;

		public static final String BILLING_CYCLE_NAME = "Billing Cycle";
		public static final String HOURS_NAME = "Hours";
		public static final String DAYS_NAME = "Days";
		public static final String WEEKS_NAME = "Weeks";
		public static final String MONTHS_NAME = "Months";
		public static final String YEARS_NAME = "Years";

	}

	public class BalLevel {
		public static final int CUSTOMER = 1;
		public static final int SUBSCRIBER = 2;
		public static final int GROUP = 3;
		public static final int MEMBERSHIP = 4;

		public static final String GROUP_NAME = "Group";
		public static final String CUSTOMER_NAME = "Customer";
		public static final String SUBSCRIBER_NAME = "Subscriber";
		public static final String MEMBERSHIP_NAME = "Membership ";
	}

	public class ConstantMessage {
		public static final String STATENAME = "State name isn't empty";

	}

	public class TriggerType {
		public static final int DEFAULT = 0;
		public static final int TRIGGER_TYPE_1 = 1;

		public static final String DEFAULT_NAME = "DEFAULT";
		public static final String TRIGGER_TYPE_1_NAME = "TRIGGER_TYPE_1";
	}

	public class BillingCycle {
		public static final long ONE_DAY = 86400000l;
		public static final long ONE_WEEK = 604800000l;
		public static final long ONE_MONTH = 2592000000l;
		public static final long ONE_YEAR = 31536000000l;
	}

	public class GeoHomeZoneType {
		public static final long DEFAULT_TYPE = 1;
		public static final String DEFAULT_TYPE_NAME = "DEFAULT TYPE";
	}

	public class DefineDayWeek {
		public static final int SUNDAY = 1;
		public static final int MONDAY = 2;
		public static final int TUESDAY = 3;
		public static final int WENESDAY = 4;
		public static final int THURSDAY = 5;
		public static final int FRIDAY = 6;
		public static final int SATURDAY = 7;

		public static final String MONDAY_NAME = "MONDAY";
		public static final String TUESDAY_NAME = "TUESDAY";
		public static final String WENESDAY_NAME = "WENESDAY";
		public static final String THURSDAY_NAME = "THURSDAY";
		public static final String FRIDAY_NAME = "FRIDAY";
		public static final String SATURDAY_NAME = "SATURDAY";
		public static final String SUNDAY_NAME = "SUNDAY";

	}
	
	public class InterfaceType{
		public static final int Gy = 16777225;
		public static final int Gx = 16777238;
		public static final int Sy = 16777302;
		public static final int Rx = 16777229;
		public static final int COMMON = 1;
		
		public static final String Gy_NAME = "Gy";
		public static final String Gx_NAME = "Gx";
		public static final String Sy_NAME = "Sy";
		public static final String Rx_NAME = "Rx";
		public static final String COMMON_NAME = "COMMON";
	}
}

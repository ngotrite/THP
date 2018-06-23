package com.viettel.ocs.constant;

public class CdrType {
	
	public static final String STRING_TYPE = "String";
	public static final String LONG_TYPE = "Long";
	public static final String INTEGER_TYPE = "Integer";
	public static final String DATE_TYPE = "Date";

	public static class CdrPaymentType{
		public static final String PREPAID_TYPE = "PREPAID";
		public static final String POSTPAID_TYPE = "POSTPAID";
		public static final String HYBRID_TYPE = "HYBRID";
		
		public static final Long PREPAID = 0l;
		public static final Long POSTPAID = 1l;
		public static final Long HYBRID = 2l;
	}
	
	public static class CdrDetailType{
		public static final String GENERATE_TYPE = "Generate";
		public static final String HOLDING_TYPE = "Holding";
		public static final String PUSH_TYPE = "Push";
		
		public static final Long GENERATE = 0l;
		public static final Long HOLDING = 1l;
		public static final Long PUSH = 2l;
	}
}


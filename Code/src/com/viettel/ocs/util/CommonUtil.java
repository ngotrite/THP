package com.viettel.ocs.util;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  General Utilities
 *
 *  @author     huannn
 */
public class CommonUtil
{
	/**
	 *	Replace String values.
	 *  @param value string to be processed
	 *  @param oldPart old part
	 *  @param newPart replacement - can be null or ""
	 *  @return String with replaced values
	 */
	
	public static String BASE_CHARACTER = "abcdefghijklmlopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
	
	public static String replace (String value, String oldPart, String newPart)
	{
		if (value == null || value.length() == 0
			|| oldPart == null || oldPart.length() == 0)
			return value;
		//
		int oldPartLength = oldPart.length();
		String oldValue = value;
		StringBuilder retValue = new StringBuilder();
		int pos = oldValue.indexOf(oldPart);
		while (pos != -1)
		{
			retValue.append (oldValue.substring(0, pos));
			if (newPart != null && newPart.length() > 0)
				retValue.append(newPart);
			oldValue = oldValue.substring(pos+oldPartLength);
			pos = oldValue.indexOf(oldPart);
		}
		retValue.append(oldValue);
	//	log.fine( "Env.replace - " + value + " - Old=" + oldPart + ", New=" + newPart + ", Result=" + retValue.toString());
		return retValue.toString();
	}	//	replace
	
	/**
	 *	check phone String values.
	 *  @param value string to be processed
	 *  @param oldPart old part
	 *  @param newPart replacement - can be null or ""
	 *  @return String with replaced values
	 */
	public static String checkPhone (String value)
	{
		String ms=null;
		if(value==null || value.length()==0)
			return ms;
		if(value.length()>=21)
		{
			return ms;
		}
		for (int i = 0; i < value.length(); i++) {
			int j =  (int) value.charAt(i);
			char c=value.charAt(i);
			if(c=='*'| c=='('| c==')'| c==')'| c=='_'| c=='+'| c=='-'| c==' '| c=='.')
				;
			else if (48 > j || j > 57){
				
				return ms;
			}
		}
		boolean foundMatch = value.matches("~|!|@|#|$|%|^|&");
		if(foundMatch){
			return ms;
		}
		
		return ms;
	}	//	replace
	
	/**
	 * Remove CR / LF from String
	 * @param in input
	 * @return cleaned string
	 */
	public static String removeCRLF (String in)
	{
		char[] inArray = in.toCharArray();
		StringBuilder out = new StringBuilder (inArray.length);
		for (int i = 0; i < inArray.length; i++)
		{
			char c = inArray[i];
			if (c == '\n' || c == '\r')
				;
			else
				out.append(c);
		}
		return out.toString();
	}	//	removeCRLF

	/**
	 * Clean - Remove all white spaces
	 * @param in in
	 * @return cleaned string
	 */
	public static String cleanWhitespace (String in)
	{
		char[] inArray = in.toCharArray();
		StringBuilder out = new StringBuilder(inArray.length);
		boolean lastWasSpace = false;
		for (int i = 0; i < inArray.length; i++)
		{
			char c = inArray[i];
			if (Character.isWhitespace(c))
			{
				if (!lastWasSpace)
					out.append (' ');
				lastWasSpace = true;
			}
			else
			{
				out.append (c);
				lastWasSpace = false;
			}
		}
		return out.toString();
	}	//	cleanWhitespace

	/**
	 * Mask HTML content.
	 * i.e. replace characters with &values;
	 * CR is not masked
	 * @param content content
	 * @return masked content
	 * @see #maskHTML(String, boolean)
	 */
	public static String maskHTML (String content)
	{
		return maskHTML (content, false);
	}	//	maskHTML
	
	/**
	 * Mask HTML content.
	 * i.e. replace characters with &values;
	 * @param content content
	 * @param maskCR convert CR into <br>
	 * @return masked content or null if the <code>content</code> is null
	 */
	public static String maskHTML (String content, boolean maskCR)
	{
		// If the content is null, then return null - teo_sarca [ 1748346 ]
		if (content == null)
			return content;
		//
		StringBuilder out = new StringBuilder();
		char[] chars = content.toCharArray();
		for (int i = 0; i < chars.length; i++)
		{
			char c = chars[i];
			switch (c)
			{
				case '<':
					out.append ("&lt;");
					break;
				case '>':
					out.append ("&gt;");
					break;
				case '&':
					out.append ("&amp;");
					break;
				case '"':
					out.append ("&quot;");
					break;
				case '\'':
					out.append ("&#039;");
					break;
				case '\n':
					if (maskCR)
						out.append ("<br>");
				//
				default:
					int ii =  (int)c;
					if (ii > 255)		//	Write Unicode
						out.append("&#").append(ii).append(";");
					else
						out.append(c);
					break;
			}
		}
		return out.toString();
	}	//	maskHTML

	/**
	 * Get the number of occurances of countChar in string.
	 * @param string String to be searched
	 * @param countChar to be counted character
	 * @return number of occurances
	 */
	public static int getCount (String string, char countChar)
	{
		if (string == null || string.length() == 0)
			return 0;
		int counter = 0;
		char[] array = string.toCharArray();
		for (int i = 0; i < array.length; i++)
		{
			if (array[i] == countChar)
				counter++;
		}
		return counter;
	}	//	getCount

	/**
	 * Is String Empty
	 * @param str string
	 * @return true if >= 1 char
	 */
	public static boolean isEmpty (String str)
	{
		return isEmpty(str, true);
	}	//	isEmpty
	
	/**
	 * Is String Empty
	 * @param str string
	 * @param trimWhitespaces trim whitespaces
	 * @return true if >= 1 char
	 */
	public static boolean isEmpty (String str, boolean trimWhitespaces)
	{
		if (str == null)
			return true;
		if (trimWhitespaces)
			return str.trim().length() == 0;
		else
			return str.length() == 0;
	}	//	isEmpty
	
	/**************************************************************************
	 * Find index of search character in str.
	 * This ignores content in () and 'texts'
	 * @param str string
	 * @param search search character
	 * @return index or -1 if not found
	 */
	public static int findIndexOf (String str, char search)
	{
		return findIndexOf(str, search, search);
	}   //  findIndexOf

	/**
	 *  Find index of search characters in str.
	 *  This ignores content in () and 'texts'
	 *  @param str string
	 *  @param search1 first search character
	 *  @param search2 second search character (or)
	 *  @return index or -1 if not found
	 */
	public static int findIndexOf (String str, char search1, char search2)
	{
		if (str == null)
			return -1;
		//
		int endIndex = -1;
		int parCount = 0;
		boolean ignoringText = false;
		int size = str.length();
		while (++endIndex < size)
		{
			char c = str.charAt(endIndex);
			if (c == '\'')
				ignoringText = !ignoringText;
			else if (!ignoringText)
			{
				if (parCount == 0 && (c == search1 || c == search2))
					return endIndex;
				else if (c == ')')
						parCount--;
				else if (c == '(')
					parCount++;
			}
		}
		return -1;
	}   //  findIndexOf

	/**
	 *  Find index of search character in str.
	 *  This ignores content in () and 'texts'
	 *  @param str string
	 *  @param search search character
	 *  @return index or -1 if not found
	 */
	public static int findIndexOf (String str, String search)
	{
		if (str == null || search == null || search.length() == 0)
			return -1;
		//
		int endIndex = -1;
		int parCount = 0;
		boolean ignoringText = false;
		int size = str.length();
		while (++endIndex < size)
		{
			char c = str.charAt(endIndex);
			if (c == '\'')
				ignoringText = !ignoringText;
			else if (!ignoringText)
			{
				if (parCount == 0 && c == search.charAt(0))
				{
					if (str.substring(endIndex).startsWith(search))
						return endIndex;
				}
				else if (c == ')')
						parCount--;
				else if (c == '(')
					parCount++;
			}
		}
		return -1;
	}   //  findIndexOf

	
	/**************************************************************************
	 *  Return Hex String representation of byte b
	 *  @param b byte
	 *  @return Hex
	 */
	static public String toHex (byte b)
	{
		char hexDigit[] = {
			'0', '1', '2', '3', '4', '5', '6', '7',
			'8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
		};
		char[] array = { hexDigit[(b >> 4) & 0x0f], hexDigit[b & 0x0f] };
		return new String(array);
	}

	/**
	 *  Return Hex String representation of char c
	 *  @param c character
	 *  @return Hex
	 */
	static public String toHex (char c)
	{
		byte hi = (byte) (c >>> 8);
		byte lo = (byte) (c & 0xff);
		return toHex(hi) + toHex(lo);
	}   //  toHex
	
	/**************************************************************************
	 * Init Cap Words With Spaces
	 * @param in string
	 * @return init cap
	 */
	public static String initCap (String in)
	{
		if (in == null || in.length() == 0)
			return in;
		//
		boolean capitalize = true;
		char[] data = in.toCharArray();
		for (int i = 0; i < data.length; i++)
		{
			if (data[i] == ' ' || Character.isWhitespace(data[i]))
				capitalize = true;
			else if (capitalize)
			{
				data[i] = Character.toUpperCase (data[i]);
				capitalize = false;
			}
			else
				data[i] = Character.toLowerCase (data[i]);
		}
		return new String (data);
	}	//	initCap

	
	/**************************************************************************
	 * Return a Iterator with only the relevant attributes.
	 * Fixes implementation in AttributedString, which returns everything
	 * @param aString attributed string
	 * @param relevantAttributes relevant attributes
	 * @return iterator
	 */
	static public AttributedCharacterIterator getIterator (AttributedString aString, 
		AttributedCharacterIterator.Attribute[] relevantAttributes)
	{
		AttributedCharacterIterator iter = aString.getIterator();
		Set<?> set = iter.getAllAttributeKeys();
		if (set.size() == 0)
			return iter;
		//	Check, if there are unwanted attributes
		Set<AttributedCharacterIterator.Attribute> unwanted = new HashSet<AttributedCharacterIterator.Attribute>(iter.getAllAttributeKeys());
		for (int i = 0; i < relevantAttributes.length; i++)
			unwanted.remove(relevantAttributes[i]);
		if (unwanted.size() == 0)
			return iter;

		//	Create new String
		StringBuilder sb = new StringBuilder();
		for (char c = iter.first(); c != AttributedCharacterIterator.DONE; c = iter.next())
			sb.append(c);
		aString = new AttributedString(sb.toString());

		//	copy relevant attributes
		Iterator<AttributedCharacterIterator.Attribute> it = iter.getAllAttributeKeys().iterator();
		while (it.hasNext())
		{
			AttributedCharacterIterator.Attribute att = it.next();
			if (!unwanted.contains(att))
			{
				for (char c = iter.first(); c != AttributedCharacterIterator.DONE; c = iter.next())
				{
					Object value = iter.getAttribute(att);
					if (value != null)
					{
						int start = iter.getRunStart(att);
						int limit = iter.getRunLimit(att);
						aString.addAttribute(att, value, start, limit);
						iter.setIndex(limit);
					}
				}
			}
		}
		return aString.getIterator();
	}	//	getIterator


	/**
	 * Is 8 Bit
	 * @param str string
	 * @return true if string contains chars > 255
	 */
	public static boolean is8Bit (String str)
	{
		if (str == null || str.length() == 0)
			return true;
		char[] cc = str.toCharArray();
		for (int i = 0; i < cc.length; i++)
		{
			if (cc[i] > 255)
			{
				return false;
			}
		}
		return true;
	}	//	is8Bit
	
	/**
	 * Clean Ampersand (used to indicate shortcut) 
	 * @param in input
	 * @return cleaned string
	 */
	public static String cleanAmp (String in)
	{
		if (in == null || in.length() == 0)
			return in;
		int pos = in.indexOf('&');
		if (pos == -1)
			return in;
		//
		if (pos+1 < in.length() && in.charAt(pos+1) != ' ')
			in = in.substring(0, pos) + in.substring(pos+1);
		return in;
	}	//	cleanAmp
	
	/**
	 * Trim to max character length
	 * @param str string
	 * @param length max (incl) character length
	 * @return string
	 */
	public static String trimLength (String str, int length)
	{
		if (str == null)
			return str;
		if (length <= 0)
			throw new IllegalArgumentException("Trim length invalid: " + length);
		if (str.length() > length) 
			return str.substring(0, length);
		return str;
	}	//	trimLength
	
	/**
	 * Size of String in bytes
	 * @param str string
	 * @return size in bytes
	 */
	public static int size (String str)
	{
		if (str == null)
			return 0;
		int length = str.length();
		int size = length;
		try
		{
			size = str.getBytes("UTF-8").length;
		}
		catch (UnsupportedEncodingException e)
		{
			
		}
		return size;
	}	//	size

	/**
	 * Trim to max byte size
	 * @param str string
	 * @param size max size in bytes
	 * @return string
	 */
	public static String trimSize (String str, int size)
	{
		if (str == null)
			return str;
		if (size <= 0)
			throw new IllegalArgumentException("Trim size invalid: " + size);
		//	Assume two byte code
		int length = str.length();
		if (length < size/2)
			return str;
		try
		{
			byte[] bytes = str.getBytes("UTF-8");
			if (bytes.length <= size)
				return str;
			//	create new - may cut last character in half
			byte[] result = new byte[size];
			System.arraycopy(bytes, 0, result, 0, size);
			return new String(result, "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			
		}
		return str;
	}	//	trimSize
	
	 /**
	 * Get int value
	 * @param value object
	 * @return int value or -1
	 */
	public static int getInt(Object value)
	{
		return getInt(value, -1);
	}
	
	 /**
	 * Get int value
	 * @param value object
	 * @return int value or -1
	 */
	public static int getInt(Object value, int defaultValue)
	{
		if (value == null)
			return defaultValue;

		if (value instanceof Integer)
			return ((Integer) value).intValue();
		if (value instanceof Long)
			return ((Long) value).intValue();
		if (value instanceof Double)
			return ((Double) value).intValue();
		if (value instanceof BigDecimal)
			return ((BigDecimal) value).intValue();

		try {
			Double ii = Double.parseDouble(value.toString());
			return ii.intValue();
		} catch (NumberFormatException ex) {
		}

		return defaultValue;
	}
	
	public static long getLong(Object value) {
		
		return getLong(value, -1);
	}
	
	public static long getLong(Object value, long defaultValue)
	{
		if (value == null)
			return defaultValue;

		if (value instanceof Integer)
			return ((Integer) value).longValue();
		if (value instanceof Long)
			return ((Long) value).longValue();
		if (value instanceof Double)
			return ((Double) value).longValue();
		if (value instanceof BigDecimal)
			return ((BigDecimal) value).longValue();

		try {
			Double ii = Double.parseDouble(value.toString());
			return ii.longValue();
		} catch (NumberFormatException ex) {
		}

		return defaultValue;
	}

	/**
	 * @author hungtq24
	 * @since 14/01/2014
	 * @param value
	 */
	public static boolean getBoolean(Object value)
	{
		return getBoolean(value, false);
	}
	
	/**
	 * Get boolean value
	 * @author hungtq24
	 * @since 14/01/2014
	 * @param value
	 * @param defaultValue
	 * @return
	 */
	public static boolean getBoolean(Object value, boolean defaultValue)
	{
		if (value == null)
			return false;
		
		if (value instanceof Boolean)
			return ((Boolean)value).booleanValue();
		if (value instanceof String) {
			String val = ((String)value).toLowerCase();
			return "yes".equals(val) || "y".equals(val);
		}
			
		
		return defaultValue;
	}
	
	/**
	 * Convert date to string with format pattern
	 * @param value
	 * @param format
	 * @return
	 */
	public static String DateToString(Object value, String format) {
		if (value != null) {
			Date date = null;
			if (value instanceof Date)
				date = (Date) value;
			else if (value instanceof Timestamp)
				date = (Timestamp) value;
			else if (value instanceof String)
				date = Timestamp.valueOf((String) value);

			if (date != null) {
				SimpleDateFormat df = new SimpleDateFormat(format);

				return df.format(date);
			}
		}

		return "";
	}
	
	public static String format (BigDecimal value) {
		DecimalFormat df = new DecimalFormat();
		return value == null ? "" : df.format(value);
	}
	
	/**
	 * Check if is number
	 * @author hungtq24
	 * @date 13/02/2014
	 * @param s
	 * @return
	 */
	public static boolean isNumeric (String s) {
		return s.matches("[+-]?\\d*(\\.\\d+)?") 				//###.###
    			||s.matches("[+-]?\\d*(\\,\\d+)?")				//###,###
    			||s.matches("[+-]?\\d*(\\.\\d+)(\\,\\d+)?")		//#.###,###
    			||s.matches("[+-]?\\d*(\\,\\d+)(\\.\\d+)?"); 	//#,###.###
	}

	/***
	 * huannn: Kiem tra chuoi co ky tu dac biet
	 * @param inputStr
	 * @return
	 */
	public static boolean isContainSpecialCharacter(String inputStr) {
		
		if(inputStr == null) {
			return false;
		}		
		inputStr = inputStr.replace(" ", "");
		if("".equals(inputStr)) {
			return false;
		}
		
	    //Pattern p = Pattern.compile("[^.A-Za-z0-9]");
	    Pattern p = Pattern.compile("[~!@#$%^&*()-=+_?><,?|]");
	    Matcher m = p.matcher(inputStr);
	    boolean b = m.find();
	     
		return b;
	}

	public static Boolean CheckPress(String value) {
		for (int i = 0; i < value.trim().length(); i++) {
			char c = value.charAt(i);
			int j = (int) c;
			if ((j > 32 && j <= 46) || (j >= 58 && j <= 64)
					|| (j == 94 || j == 96) || j >= 123) {
				return false;
			}
		}
		return true;
	}
	
	public static BigDecimal getBigDecimal(String str) {
		try {
			
			return new BigDecimal(str);
		} catch (Exception ex) {
			
			return null;
		}
	}

	public static boolean checkNotNull(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj instanceof String) {
			if (((String) obj).trim().length() == 0) {
				return false;
			}
		}
		return true;
	}

	public static boolean containsNumber(String c) {
		for (int i = 0; i < c.length(); ++i) {
			if (Character.isDigit(c.charAt(i)) == false)
				return false;
		}
		return true;
	}	
		
	public static String generateRandomChars(int length) {
		
		String baseChar = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890$%#@!^&*)(";
		StringBuffer retValBuffer = new StringBuffer();
		Random rnd = new Random();
 
		while (retValBuffer.length() < length) {
			int index = (int) (rnd.nextFloat() * baseChar.length());
			retValBuffer.append(baseChar.substring(index, index + 1));
		}
 
		return retValBuffer.toString();
	}
	

    /* Method convert VietNamese String with sign charactor to
     * VietNamese String with unsign charactor. This method is
     * use when convert message to send to subcriber by sms
     * which many device can't display sms as VietNamese.
     * @param orgStr
     * @return String with all sign character is converted to unsign
     */
    public static String toUnSign(String orgStr)
    {
        if (orgStr == null || orgStr.length() == 0)
        {
            return "";
        }
        orgStr = unSignMore(orgStr);
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < orgStr.length(); i++)
        {
            buf.append(toUnsign(orgStr.charAt(i)));
        }
        String result = buf.toString();
        return result;
    }

    public static String unSignMore(String file) {
        if(file != null){
        file = file.replace('Ã ', 'a');
        file = file.replace('Ã¡', 'a');
        file = file.replace('áº£', 'a');
        file = file.replace('Ã£', 'a');
        file = file.replace('áº¡', 'a');
        file = file.replace('Äƒ', 'a');
        file = file.replace('áº±', 'a');
        file = file.replace('áº¯', 'a');
        file = file.replace('áº³', 'a');
        file = file.replace('áºµ', 'a');
        file = file.replace('áº·', 'a');
        file = file.replace('Ã¢', 'a');
        file = file.replace('áº§', 'a');
        file = file.replace('áº¥', 'a');
        file = file.replace('áº©', 'a');
        file = file.replace('áº«', 'a');
        file = file.replace('áº­', 'a');
        file = file.replace('Ã€', 'A');
        file = file.replace('Ã�', 'A');
        file = file.replace('áº¢', 'A');
        file = file.replace('Ãƒ', 'A');
        file = file.replace('áº ', 'A');
        file = file.replace('Ä‚', 'A');
        file = file.replace('áº°', 'A');
        file = file.replace('áº®', 'A');
        file = file.replace('áº²', 'A');
        file = file.replace('áº´', 'A');
        file = file.replace('áº¶', 'A');
        file = file.replace('Ã‚', 'A');
        file = file.replace('áº¦', 'A');
        file = file.replace('áº¤', 'A');
        file = file.replace('áº¨', 'A');
        file = file.replace('áºª', 'A');
        file = file.replace('áº¬', 'A');
        file = file.replace('Ä‘', 'd');
        file = file.replace('Ä�', 'D');
        file = file.replace('Ã¨', 'e');
        file = file.replace('Ã©', 'e');
        file = file.replace('áº»', 'e');
        file = file.replace('áº½', 'e');
        file = file.replace('áº¹', 'e');
        file = file.replace('Ãª', 'e');
        file = file.replace('á»�', 'e');
        file = file.replace('áº¿', 'e');
        file = file.replace('á»ƒ', 'e');
        file = file.replace('á»…', 'e');
        file = file.replace('á»‡', 'e');
        file = file.replace('Ãˆ', 'E');
        file = file.replace('Ã‰', 'E');
        file = file.replace('áºº', 'E');
        file = file.replace('áº¼', 'E');
        file = file.replace('áº¸', 'E');
        file = file.replace('ÃŠ', 'E');
        file = file.replace('á»€', 'E');
        file = file.replace('áº¾', 'E');
        file = file.replace('á»‚', 'E');
        file = file.replace('á»„', 'E');
        file = file.replace('á»†', 'E');
        file = file.replace('Ã¬', 'i');
        file = file.replace('Ã­', 'i');
        file = file.replace('á»‰', 'i');
        file = file.replace('Ä©', 'i');
        file = file.replace('á»‹', 'i');
        file = file.replace('ÃŒ', 'I');
        file = file.replace('Ã�', 'I');
        file = file.replace('á»ˆ', 'I');
        file = file.replace('Ä¨', 'I');
        file = file.replace('á»Š', 'I');
        file = file.replace('Ã²', 'o');
        file = file.replace('Ã³', 'o');
        file = file.replace('á»�', 'o');
        file = file.replace('Ãµ', 'o');
        file = file.replace('á»�', 'o');
        file = file.replace('Ã´', 'o');
        file = file.replace('á»“', 'o');
        file = file.replace('á»‘', 'o');
        file = file.replace('á»•', 'o');
        file = file.replace('á»—', 'o');
        file = file.replace('á»™', 'o');
        file = file.replace('Æ¡', 'o');
        file = file.replace('á»�', 'o');
        file = file.replace('á»›', 'o');
        file = file.replace('á»Ÿ', 'o');
        file = file.replace('á»¡', 'o');
        file = file.replace('á»£', 'o');
        file = file.replace('Ã’', 'O');
        file = file.replace('Ã“', 'O');
        file = file.replace('á»Ž', 'O');
        file = file.replace('Ã•', 'O');
        file = file.replace('á»Œ', 'O');
        file = file.replace('Ã”', 'O');
        file = file.replace('á»’', 'O');
        file = file.replace('á»�', 'O');
        file = file.replace('á»”', 'O');
        file = file.replace('á»–', 'O');
        file = file.replace('á»˜', 'O');
        file = file.replace('Æ ', 'O');
        file = file.replace('á»œ', 'O');
        file = file.replace('á»š', 'O');
        file = file.replace('á»ž', 'O');
        file = file.replace('á» ', 'O');
        file = file.replace('á»¢', 'O');
        file = file.replace('Ã¹', 'u');
        file = file.replace('Ãº', 'u');
        file = file.replace('á»§', 'u');
        file = file.replace('Å©', 'u');
        file = file.replace('á»¥', 'u');
        file = file.replace('Æ°', 'u');
        file = file.replace('á»«', 'u');
        file = file.replace('á»©', 'u');
        file = file.replace('á»­', 'u');
        file = file.replace('á»¯', 'u');
        file = file.replace('á»±', 'u');
        file = file.replace('Ã™', 'U');
        file = file.replace('Ãš', 'U');
        file = file.replace('á»¦', 'U');
        file = file.replace('Å¨', 'U');
        file = file.replace('á»¤', 'U');
        file = file.replace('Æ¯', 'U');
        file = file.replace('á»ª', 'U');
        file = file.replace('á»¨', 'U');
        file = file.replace('á»¬', 'U');
        file = file.replace('á»®', 'U');
        file = file.replace('á»°', 'U');
        file = file.replace('á»³', 'y');
        file = file.replace('Ã½', 'y');
        file = file.replace('á»·', 'y');
        file = file.replace('á»¹', 'y');
        file = file.replace('á»µ', 'y');
        file = file.replace('Y', 'Y');
        file = file.replace('á»²', 'Y');
        file = file.replace('Ã�', 'Y');
        file = file.replace('á»¶', 'Y');
        file = file.replace('á»¸', 'Y');
        file = file.replace('á»´', 'Y');
        }
        return file;
    }

    public static String unSignUpperCase(String file) {
        if(file != null){
        file = file.replace('Ã€', 'Ã ');
        file = file.replace('Ã�', 'Ã¡');
        file = file.replace('áº¢', 'áº£');
        file = file.replace('Ãƒ', 'Ã£');
        file = file.replace('áº ', 'áº¡');
        file = file.replace('Ä‚', 'Äƒ');
        file = file.replace('áº°', 'áº±');
        file = file.replace('áº®', 'áº¯');
        file = file.replace('áº²', 'áº³');
        file = file.replace('áº´', 'áºµ');
        file = file.replace('áº¶', 'áº·');
        file = file.replace('Ã‚', 'Ã¢');
        file = file.replace('áº¦', 'áº§');
        file = file.replace('áº¤', 'áº¥');
        file = file.replace('áº¨', 'áº©');
        file = file.replace('áºª', 'áº«');
        file = file.replace('áº¬', 'áº­');
        file = file.replace('Ãˆ', 'Ã¨');
        file = file.replace('Ã‰', 'Ã©');
        file = file.replace('áºº', 'áº»');
        file = file.replace('áº¼', 'áº½');
        file = file.replace('áº¸', 'áº¹');
        file = file.replace('ÃŠ', 'Ãª');
        file = file.replace('á»€', 'á»�');
        file = file.replace('áº¾', 'áº¿');
        file = file.replace('á»‚', 'á»ƒ');
        file = file.replace('á»„', 'á»…');
        file = file.replace('á»†', 'á»‡');
        file = file.replace('ÃŒ', 'Ã¬');
        file = file.replace('Ã�', 'Ã­');
        file = file.replace('á»ˆ', 'á»‰');
        file = file.replace('Ä¨', 'Ä©');
        file = file.replace('á»Š', 'á»‹');
        file = file.replace('Ã’', 'Ã²');
        file = file.replace('Ã“', 'Ã³');
        file = file.replace('á»Ž', 'á»�');
        file = file.replace('Ã•', 'Ãµ');
        file = file.replace('á»Œ', 'á»�');
        file = file.replace('Ã”', 'Ã´');
        file = file.replace('á»’', 'á»“');
        file = file.replace('á»�', 'á»‘');
        file = file.replace('á»”', 'á»•');
        file = file.replace('á»–', 'á»—');
        file = file.replace('á»˜', 'á»™');
        file = file.replace('Æ ', 'Æ¡');
        file = file.replace('á»œ', 'á»�');
        file = file.replace('á»š', 'á»›');
        file = file.replace('á»ž', 'á»Ÿ');
        file = file.replace('á» ', 'á»¡');
        file = file.replace('á»¢', 'á»£');
        file = file.replace('Ã™', 'Ã¹');
        file = file.replace('Ãš', 'Ãº');
        file = file.replace('á»¦', 'á»§');
        file = file.replace('Å¨', 'Å©');
        file = file.replace('á»¤', 'á»¥');
        file = file.replace('Æ¯', 'Æ°');
        file = file.replace('á»ª', 'á»«');
        file = file.replace('á»¨', 'á»©');
        file = file.replace('á»¬', 'á»­');
        file = file.replace('á»®', 'á»¯');
        file = file.replace('á»°', 'á»±');
        file = file.replace('á»²', 'á»³');
        file = file.replace('Ã�', 'Ã½');
        file = file.replace('á»¶', 'á»·');
        file = file.replace('á»¸', 'á»¹');
        file = file.replace('á»´', 'á»µ');
        }
        return file;
    }

    /**
     * decode a sign char to unsign char
     * @param c
     * @return unsign character
     */
    public static char toUnsign(char c)
    {
        for (char[] signChar : signChars)
        {
            for (char aSignChar : signChar)
            {
                if (aSignChar == c)
                {
                    return signChar[0];
                }
            }
        }
        return c;
    }
    
    public static char[][] signChars =
        {
            {
                97, 224, 225, 7843, 227, 7841, 259, 7857, 7855, 7859, 7861, 7863, 226, 7855, 7847, 7845, 7849, 7851, 7853
            },
            {
                111, 242, 243, 7887, 245, 7885, 244, 7891, 7889, 7893, 7895, 7897, 417, 7901, 7899, 7903, 7905, 7907
            },
            {
                101, 232, 233, 7867, 7869, 7865, 234, 7873, 7871, 7875, 7875, 7879
            },
            {
                117, 249, 250, 7911, 361, 7909, 432, 7915, 7913, 7917, 7919, 7921
            },
            {
                105, 236, 237, 7881, 297, 7883
            },
            {
                121, 7923, 253, 7927, 7929, 7925
            },
            {
                100, 273
            },
            {68, 272}
        };
}

package com.viettel.ocs.util;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class PasswordUtil {


	private static Pattern pattern;
	private static Matcher matcher;

	private static final String PASSWORD_PATTERN = "^.*(?=.{8,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*_=+-/]).*$";
//	private static final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,20})";
//	private static final String PASSWORD_PATTERN = ".*";
	private static final String[] blackList = 
	{
		"1234567890"," letmein123"," test123"," demo123"," pass123"," 123qwe"," qwe123"," 654321"," loveyou"," adminadmin123",	
		"123456a@","123456aA@","1qaz2wsx","1234qwert@","Password123@","Abc123!@#","123456","123456789","qwerty","12345678","111111","1234567890", "1234567","password","123123","987654321","zaq11234","zaq1asdf",
		"qwertyuiop","mynoob","123321","666666","18atcskd2w","7777777","1q2w3e4r","654321","555555","3rjs1la7qe","google","1q2w3e4r5t","123qwe","zxcvbnm","1q2w3", "zaq1zaq1","zaq1xsw2","zaq1cde3","zaq1zxcv","zaq1!@#$",
		"zaq1vfr4","zaq1bgt5","zaq1nhy6","zaq1mju7","zaq1,ki8","zaq1.lo9","zaq1/;p0","zaq1ZQ!","zaq1XW@","zaq1DE#","zaq1FR$","zaq1BGT%","zaq1NHY^","zaq1JU&","zaq1<KI*","zaq1LO(","zaq1?:P)","zaq1qwer",
		"232323","raiders","888888","marlboro","gandalf","asdfasdf","crystal","87654321","12344321","sexsex","golden","blowme","bigtits","8675309","panther","lauren","angela","bitch","spanky","thx1138","angels","madison",
		"winston","shannon","mike","toyota","blowjob","jordan23","canada","sophie","Password","apples","dick","tiger","razz","123abc","pokemon","qazxsw","55555","qwaszx","muffin","johnson","murphy","cooper",
		"jonathan","liverpoo","david","danielle","159357","jackie","1990","123456a","789456","turtle","horny","abcd1234","scorpion","qazwsxedc","101010","butter","carlos","password1","dennis","slipknot",
		"qwerty123","booger","asdf","1991","black","startrek","12341234","cameron","newyork","rainbow","nathan","john","1992","rocket","viking","redskins","butthead","asdfghjkl"
	};

	private final static Logger logger = Logger.getLogger(PasswordUtil.class);
	/**
	 * Validate password with regular expression
	 * 
	 * @param password
	 *            password for validation
	 * @return true valid password, false invalid password
	 */
	public static boolean validatePassword(String password) {
		
		for(String black : blackList) {
			if(black.contains(password))
				return false;
		}
		
		pattern = Pattern.compile(PASSWORD_PATTERN);
		matcher = pattern.matcher(password);
		return matcher.matches();
	}
	
	public static String getRandomSalt() {
		
		return CommonUtil.generateRandomChars(12);
	}
	
	public static String generateHash(String rawPass, String salt) {
		
		if(CommonUtil.isEmpty(rawPass))
			return null;
		
		try {
			
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update((rawPass + salt).getBytes());
			byte[] digest = md.digest();
			StringBuffer sb = new StringBuffer();
			for (byte b : digest) {
				sb.append(String.format("%02x", b & 0xff));
			}
			return sb.toString();
		} catch (Exception e) {
			logger.error("EXCEPTION: ", e);
		}
		
		return null;
	}
}

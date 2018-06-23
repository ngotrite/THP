package com.viettel.ocs.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/**
 *	Time Utilities
 *
 * 	@author 	Jorg Janke
 * 	@version 	$Id: TimeUtil.java,v 1.3 2006/07/30 00:54:35 jjanke Exp $
 */
public class DatetimeUtil
{
	public static int getYear(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		return cal.get(Calendar.YEAR);
	}
	
	/**
	 * 	Get earliest time of a day (truncate)
	 *  @param time day and time
	 *  @return day with 00:00
	 */
	static public Date getDay (long time)
	{
		if (time == 0)
			time = System.currentTimeMillis();
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeInMillis(time);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return new Date (cal.getTimeInMillis());
	}	//	getDay

	/**
	 * 	Get earliest time of a day (truncate)
	 *  @param dayTime day and time
	 *  @return day with 00:00
	 */
	static public Date getDay (Date dayTime)
	{
		if (dayTime == null)
			return getDay(System.currentTimeMillis());
		return getDay(dayTime.getTime());
	}	//	getDay

	/**
	 * 	Get earliest time of a day (truncate)
	 *	@param day day 1..31
	 *	@param month month 1..12
	 *	@param year year (if two diguts: < 50 is 2000; > 50 is 1900)
	 *	@return Date ** not too reliable
	 */
	static public Date getDay (int year, int month, int day)
	{
		if (year < 50)
			year += 2000;
		else if (year < 100)
			year += 1900;
		if (month < 1 || month > 12)
			throw new IllegalArgumentException("Invalid Month: " + month);
		if (day < 1 || day > 31)
			throw new IllegalArgumentException("Invalid Day: " + month);
		GregorianCalendar cal = new GregorianCalendar (year, month-1, day);
		return new Date (cal.getTimeInMillis());
	}	//	getDay

	/**
	 * 	Get today (truncate)
	 *  @return day with 00:00
	 */
	static public Calendar getToday ()
	{
		GregorianCalendar cal = new GregorianCalendar();
	//	cal.setTimeInMillis(System.currentTimeMillis());
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}	//	getToday

	/**
	 * 	Get earliest time of next day
	 *  @param day day
	 *  @return next day with 00:00
	 */
	static public Date getNextDay (Date day)
	{
		if (day == null)
			day = new Date(System.currentTimeMillis());
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeInMillis(day.getTime());
		cal.add(Calendar.DAY_OF_YEAR, +1);	//	next
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return new Date (cal.getTimeInMillis());
	}	//	getNextDay
	
	/**
	 * 	Get earliest time of next day
	 *  @param day day
	 *  @return next day with 00:00
	 */
	static public Date getPreviousDay (Date day)
	{
		if (day == null)
			day = new Date(System.currentTimeMillis());
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeInMillis(day.getTime());
		cal.add(Calendar.DAY_OF_YEAR, -1);	//	previous
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return new Date (cal.getTimeInMillis());
	}	//	getNextDay

	/**
	 * 	Get last date in month
	 *  @param day day
	 *  @return last day with 00:00
	 */
	static public Date getMonthLastDay (Date day)
	{
		if (day == null)
			day = new Date(System.currentTimeMillis());
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeInMillis(day.getTime());
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		//
		cal.add(Calendar.MONTH, 1);			//	next
		cal.set(Calendar.DAY_OF_MONTH, 1);	//	first
		cal.add(Calendar.DAY_OF_YEAR, -1);	//	previous
		return new Date (cal.getTimeInMillis());
	}	//	getNextDay

	/**
	 * 	Return the day and time
	 * 	@param day day part
	 * 	@param time time part
	 * 	@return day + time
	 */
	static public Date getDayTime (Date day, Date time)
	{
		GregorianCalendar cal_1 = new GregorianCalendar();
		cal_1.setTimeInMillis(day.getTime());
		GregorianCalendar cal_2 = new GregorianCalendar();
		cal_2.setTimeInMillis(time.getTime());
		//
		GregorianCalendar cal = new GregorianCalendar();
		cal.set(cal_1.get(Calendar.YEAR),
			cal_1.get(Calendar.MONTH),
			cal_1.get(Calendar.DAY_OF_MONTH),
			cal_2.get(Calendar.HOUR_OF_DAY),
			cal_2.get(Calendar.MINUTE),
			cal_2.get(Calendar.SECOND));
		cal.set(Calendar.MILLISECOND, 0);
		Date retValue = new Date(cal.getTimeInMillis());
	//	log.fine( "TimeUtil.getDayTime", "Day=" + day + ", Time=" + time + " => " + retValue);
		return retValue;
	}	//	getDayTime

	/**
	 * 	Is the _1 in the Range of _2
	 *  <pre>
	 * 		Time_1         +--x--+
	 * 		Time_2   +a+      +---b---+   +c+
	 * 	</pre>
	 *  The function returns true for b and false for a/b.
	 *  @param start_1 start (1)
	 *  @param end_1 not included end (1)
	 *  @param start_2 start (2)
	 *  @param end_2 not included (2)
	 *  @return true if in range
	 */
	static public boolean inRange (Date start_1, Date end_1, Date start_2, Date end_2)
	{
		//	validity check
		if (end_1.before(start_1))
			throw new UnsupportedOperationException ("TimeUtil.inRange End_1=" + end_1 + " before Start_1=" + start_1);
		if (end_2.before(start_2))
			throw new UnsupportedOperationException ("TimeUtil.inRange End_2=" + end_2 + " before Start_2=" + start_2);
		//	case a
		if (!end_2.after(start_1))		//	end not including
		{
	//		log.fine( "TimeUtil.InRange - No", start_1 + "->" + end_1 + " <??> " + start_2 + "->" + end_2);
			return false;
		}
		//	case c
		if (!start_2.before(end_1))		//	 end not including
		{
	//		log.fine( "TimeUtil.InRange - No", start_1 + "->" + end_1 + " <??> " + start_2 + "->" + end_2);
			return false;
		}
	//	log.fine( "TimeUtil.InRange - Yes", start_1 + "->" + end_1 + " <??> " + start_2 + "->" + end_2);
		return true;
	}	//	inRange

	/**
	 * 	Is start..end on one of the days ?
	 *  @param start start day
	 *  @param end end day (not including)
	 *  @param OnMonday true if OK
	 *  @param OnTuesday true if OK
	 *  @param OnWednesday true if OK
	 *  @param OnThursday true if OK
	 *  @param OnFriday true if OK
	 *  @param OnSaturday true if OK
	 *  @param OnSunday true if OK
	 *  @return true if on one of the days
	 */
	static public boolean inRange (Date start, Date end,
		boolean OnMonday, boolean OnTuesday, boolean OnWednesday,
		boolean OnThursday, boolean OnFriday, boolean OnSaturday, boolean OnSunday)
	{
		//	are there restrictions?
		if (OnSaturday && OnSunday && OnMonday && OnTuesday && OnWednesday && OnThursday && OnFriday)
			return false;

		GregorianCalendar calStart = new GregorianCalendar();
		calStart.setTimeInMillis(start.getTime());
		int dayStart = calStart.get(Calendar.DAY_OF_WEEK);
		//
		GregorianCalendar calEnd = new GregorianCalendar();
		calEnd.setTimeInMillis(end.getTime());
		calEnd.add(Calendar.DAY_OF_YEAR, -1);	//	not including
		int dayEnd = calEnd.get(Calendar.DAY_OF_WEEK);

		//	On same day
		if (calStart.get(Calendar.YEAR) == calEnd.get(Calendar.YEAR)
			&& calStart.get(Calendar.MONTH) == calEnd.get(Calendar.MONTH)
			&& calStart.get(Calendar.DAY_OF_MONTH) == calEnd.get(Calendar.DAY_OF_YEAR))
		{
			if ((!OnSaturday && dayStart == Calendar.SATURDAY)
				|| (!OnSunday && dayStart == Calendar.SUNDAY)
				|| (!OnMonday && dayStart == Calendar.MONDAY)
				|| (!OnTuesday && dayStart == Calendar.TUESDAY)
				|| (!OnWednesday && dayStart == Calendar.WEDNESDAY)
				|| (!OnThursday && dayStart == Calendar.THURSDAY)
				|| (!OnFriday && dayStart == Calendar.FRIDAY))
			{
		//		log.fine( "TimeUtil.InRange - SameDay - Yes", start + "->" + end + " - "
		//			+ OnMonday+"-"+OnTuesday+"-"+OnWednesday+"-"+OnThursday+"-"+OnFriday+"="+OnSaturday+"-"+OnSunday);
				return true;
			}
		//	log.fine( "TimeUtil.InRange - SameDay - No", start + "->" + end + " - "
		//		+ OnMonday+"-"+OnTuesday+"-"+OnWednesday+"-"+OnThursday+"-"+OnFriday+"="+OnSaturday+"-"+OnSunday);
			return false;
		}
		//
	//	log.fine( "TimeUtil.inRange - WeekDay Start=" + dayStart + ", Incl.End=" + dayEnd);

		//	Calendar.SUNDAY=1 ... SATURDAY=7
		BitSet days = new BitSet (8);
		//	Set covered days in BitArray
		if (dayEnd <= dayStart)
			dayEnd += 7;
		for (int i = dayStart; i < dayEnd; i++)
		{
			int index = i;
			if (index > 7)
				index -= 7;
			days.set(index);
		}

		//	Compare days to availability
		if ((!OnSaturday && days.get(Calendar.SATURDAY))
			|| (!OnSunday && days.get(Calendar.SUNDAY))
			|| (!OnMonday && days.get(Calendar.MONDAY))
			|| (!OnTuesday && days.get(Calendar.TUESDAY))
			|| (!OnWednesday && days.get(Calendar.WEDNESDAY))
			|| (!OnThursday && days.get(Calendar.THURSDAY))
			|| (!OnFriday && days.get(Calendar.FRIDAY)))
		{
	//		log.fine( "MAssignment.InRange - Yes",	start + "->" + end + " - "
	//			+ OnMonday+"-"+OnTuesday+"-"+OnWednesday+"-"+OnThursday+"-"+OnFriday+"="+OnSaturday+"-"+OnSunday);
			return true;
		}

	//	log.fine( "MAssignment.InRange - No", start + "->" + end + " - "
	//		+ OnMonday+"-"+OnTuesday+"-"+OnWednesday+"-"+OnThursday+"-"+OnFriday+"="+OnSaturday+"-"+OnSunday);
		return false;
	}	//	isRange


	/**
	 * 	Is it the same day
	 * 	@param one day
	 * 	@param two compared day
	 * 	@return true if the same day
	 */
	static public boolean isSameDay (Date one, Date two)
	{
		GregorianCalendar calOne = new GregorianCalendar();
		if (one != null)
			calOne.setTimeInMillis(one.getTime());
		GregorianCalendar calTwo = new GregorianCalendar();
		if (two != null)
			calTwo.setTimeInMillis(two.getTime());
		if (calOne.get(Calendar.YEAR) == calTwo.get(Calendar.YEAR)
			&& calOne.get(Calendar.MONTH) == calTwo.get(Calendar.MONTH)
			&& calOne.get(Calendar.DAY_OF_MONTH) == calTwo.get(Calendar.DAY_OF_MONTH))
			return true;
		return false;
	}	//	isSameDay

	/**
	 * 	Is it the same hour
	 * 	@param one day/time
	 * 	@param two compared day/time
	 * 	@return true if the same day
	 */
	static public boolean isSameHour (Date one, Date two)
	{
		GregorianCalendar calOne = new GregorianCalendar();
		if (one != null)
			calOne.setTimeInMillis(one.getTime());
		GregorianCalendar calTwo = new GregorianCalendar();
		if (two != null)
			calTwo.setTimeInMillis(two.getTime());
		if (calOne.get(Calendar.YEAR) == calTwo.get(Calendar.YEAR)
			&& calOne.get(Calendar.MONTH) == calTwo.get(Calendar.MONTH)
			&& calOne.get(Calendar.DAY_OF_MONTH) == calTwo.get(Calendar.DAY_OF_MONTH)
			&& calOne.get(Calendar.HOUR_OF_DAY) == calTwo.get(Calendar.HOUR_OF_DAY))
			return true;
		return false;
	}	//	isSameHour

	/**
	 * 	Is all day
	 * 	@param start start date
	 * 	@param end end date
	 * 	@return true if all day (00:00-00:00 next day)
	 */
	static public boolean isAllDay (Date start, Date end)
	{
		GregorianCalendar calStart = new GregorianCalendar();
		calStart.setTimeInMillis(start.getTime());
		GregorianCalendar calEnd = new GregorianCalendar();
		calEnd.setTimeInMillis(end.getTime());
		if (calStart.get(Calendar.HOUR_OF_DAY) == calEnd.get(Calendar.HOUR_OF_DAY)
			&& calStart.get(Calendar.MINUTE) == calEnd.get(Calendar.MINUTE)
			&& calStart.get(Calendar.SECOND) == calEnd.get(Calendar.SECOND)
			&& calStart.get(Calendar.MILLISECOND) == calEnd.get(Calendar.MILLISECOND)
			&& calStart.get(Calendar.HOUR_OF_DAY) == 0
			&& calStart.get(Calendar.MINUTE) == 0
			&& calStart.get(Calendar.SECOND) == 0
			&& calStart.get(Calendar.MILLISECOND) == 0
			&& start.before(end))
			return true;
		//
		return false;
	}	//	isAllDay

	/**
	 * 	Calculate the number of days between start and end.
	 * 	@param start start date
	 * 	@param end end date
	 * 	@return number of days (0 = same)
	 */
	static public int getDaysBetween (Date start, Date end)
	{
		boolean negative = false;
		if (end.before(start))
		{
			negative = true;
			Date temp = start;
			start = end;
			end = temp;
		}
		//
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(start);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		GregorianCalendar calEnd = new GregorianCalendar();
		calEnd.setTime(end);
		calEnd.set(Calendar.HOUR_OF_DAY, 0);
		calEnd.set(Calendar.MINUTE, 0);
		calEnd.set(Calendar.SECOND, 0);
		calEnd.set(Calendar.MILLISECOND, 0);

		//	in same year
		if (cal.get(Calendar.YEAR) == calEnd.get(Calendar.YEAR))
		{
			if (negative)
				return (calEnd.get(Calendar.DAY_OF_YEAR) - cal.get(Calendar.DAY_OF_YEAR)) * -1;
			return calEnd.get(Calendar.DAY_OF_YEAR) - cal.get(Calendar.DAY_OF_YEAR);
		}

		//	not very efficient, but correct
		int counter = 0;
		while (calEnd.after(cal))
		{
			cal.add (Calendar.DAY_OF_YEAR, 1);
			counter++;
		}
		if (negative)
			return counter * -1;
		return counter;
	}	//	getDaysBetween

	/**
	 * 	Return Day + offset (truncates)
	 * 	@param day Day
	 * 	@param offset day offset
	 * 	@return Day + offset at 00:00
	 */
	static public Date addDays (Date day, int offset)
	{
		if (offset == 0)
		{
			return day;
		}
		if (day == null)
		{
			day = new Date(System.currentTimeMillis());
		}
		//
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(day);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		if (offset == 0)
			return new Date (cal.getTimeInMillis());
		cal.add(Calendar.DAY_OF_YEAR, offset);			//	may have a problem with negative (before 1/1)
		return new Date (cal.getTimeInMillis());
	}	//	addDays

	/**
	 * 	Return DateTime + offset in minutes
	 * 	@param dateTime Date and Time
	 * 	@param offset minute offset
	 * 	@return dateTime + offset in minutes
	 */
	static public Date addMinutess (Date dateTime, int offset)
	{
		if (dateTime == null)
			dateTime = new Date(System.currentTimeMillis());
		if (offset == 0)
			return dateTime;
		//
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(dateTime);
		cal.add(Calendar.MINUTE, offset);			//	may have a problem with negative
		return new Date (cal.getTimeInMillis());
	}	//	addMinutes


	/**************************************************************************
	 * 	Format Elapsed Time
	 * 	@param start start time or null for now
	 * 	@param end end time or null for now
	 * 	@return formatted time string 1'23:59:59.999
	 */
	public static String formatElapsed (Date start, Date end)
	{
		long startTime = 0;
		if (start == null)
			startTime = System.currentTimeMillis();
		else
			startTime = start.getTime();
		//
		long endTime = 0;
		if (end == null)
			endTime = System.currentTimeMillis();
		else
			endTime = end.getTime();
		return formatElapsed(endTime-startTime);
	}	//	formatElapsed

	/**
	 * 	Format Elapsed Time until now
	 * 	@param start start time
	 *	@return formatted time string 1'23:59:59.999
	 */
	public static String formatElapsed (Date start)
	{
		if (start == null)
			return "NoStartTime";
		long startTime = start.getTime();
		long endTime = System.currentTimeMillis();
		return formatElapsed(endTime-startTime);
	}	//	formatElapsed

	/**
	 * 	Format Elapsed Time
	 *	@param elapsedMS time in ms
	 *	@return formatted time string 1'23:59:59.999 - d'hh:mm:ss.xxx
	 */
	public static String formatElapsed (long elapsedMS)
	{
		if (elapsedMS == 0)
			return "0";
		StringBuilder sb = new StringBuilder();
		if (elapsedMS < 0)
		{
			elapsedMS = - elapsedMS;
			sb.append("-");
		}
		//
		long miliSeconds = elapsedMS%1000;
		elapsedMS = elapsedMS / 1000;
		long seconds = elapsedMS%60;
		elapsedMS = elapsedMS / 60;
		long minutes = elapsedMS%60;
		elapsedMS = elapsedMS / 60;
		long hours = elapsedMS%24;
		long days = elapsedMS / 24;
		//
		if (days != 0)
			sb.append(days).append("'");
		//	hh
		if (hours != 0)
			sb.append(get2digits(hours)).append(":");
		else if (days != 0)
			sb.append("00:");
		//	mm
		if (minutes != 0)
			sb.append(get2digits(minutes)).append(":");
		else if (hours != 0 || days != 0)
			sb.append("00:");
		//	ss
		sb.append(get2digits(seconds))
			.append(".").append(miliSeconds);
		return sb.toString();
	}	//	formatElapsed

	/**
	 * 	Get Minimum of 2 digits
	 *	@param no number
	 *	@return String
	 */
	private static String get2digits (long no)
	{
		String s = String.valueOf(no);
		if (s.length() > 1)
			return s;
		return "0" + s;
	}	//	get2digits

	/**
	 * 	Is it valid today?
	 *	@param validFrom valid from
	 *	@param validTo valid to
	 *	@return true if walid
	 */
	public static boolean isValid (Date validFrom, Date validTo)
	{
		return isValid (validFrom, validTo, new Date (System.currentTimeMillis()));
	}	//	isValid

	/**
	 * 	Is it valid on test date
	 *	@param validFrom valid from
	 *	@param validTo valid to
	 *	@param testDate Date
	@return true if walid
	 */
	public static boolean isValid (Date validFrom, Date validTo, Date testDate)
	{
		if (testDate == null)
			return true;
		if (validFrom == null && validTo == null)
			return true;
		//	(validFrom)	ok
		if (validFrom != null && validFrom.after(testDate))
			return false;
		//	ok	(validTo)
		if (validTo != null && validTo.before(testDate))
			return false;
		return true;
	}	//	isValid
	
	/**
	 * 	Max date
	 *	@param ts1 p1
	 *	@param ts2 p2
	 *	@return max time
	 */
	public static Date max (Date ts1, Date ts2)
	{
		if (ts1 == null)
			return ts2;
		if (ts2 == null)
			return ts1;
		
		if (ts2.after(ts1))
			return ts2;
		return ts1;
	}	//	max

	/** Truncate Day - D			*/
	public static final String	TRUNC_DAY = "D";
	/** Truncate Week - W			*/
	public static final String	TRUNC_WEEK = "W";
	/** Truncate Month - MM			*/
	public static final String	TRUNC_MONTH = "MM";
	/** Truncate Quarter - Q		*/
	public static final String	TRUNC_QUARTER = "Q";
	/** Truncate Year - Y			*/
	public static final String	TRUNC_YEAR = "Y";
	
	/**
	 * 	Get truncated day/time
	 *  @param dayTime day
	 *  @param trunc how to truncate TRUNC_*
	 *  @return next day with 00:00
	 */
	static public Date trunc (Date dayTime, String trunc)
	{
		if (dayTime == null)
			dayTime = new Date(System.currentTimeMillis());
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeInMillis(dayTime.getTime());
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		//	D
		cal.set(Calendar.HOUR_OF_DAY, 0);
		if (trunc == null || trunc.equals(TRUNC_DAY))
			return new Date (cal.getTimeInMillis());
		//	W
		if (trunc.equals(TRUNC_WEEK))
		{
			cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
			return new Date (cal.getTimeInMillis());
		}
		// MM
		cal.set(Calendar.DAY_OF_MONTH, 1);
		if (trunc.equals(TRUNC_MONTH))
			return new Date (cal.getTimeInMillis());
		//	Q
		if (trunc.equals(TRUNC_QUARTER))
		{
			int mm = cal.get(Calendar.MONTH);
			if (mm < 4)
				mm = 1;
			else if (mm < 7)
				mm = 4;
			else if (mm < 10)
				mm = 7;
			else
				mm = 10;
			cal.set(Calendar.MONTH, mm);
			return new Date (cal.getTimeInMillis());
		}
		cal.set(Calendar.DAY_OF_YEAR, 1);
		return new Date (cal.getTimeInMillis());
	}	//	trunc
	
	
	/**
	 * [ ARHIPAC ] Gets calendar instance of given date
	 * @param date calendar initialization date; if null, the current date is used
	 * @return calendar
	 * @author Teo Sarca, SC ARHIPAC SERVICE SRL
	 */
	static public Calendar getCalendar(Date date)
	{
		GregorianCalendar cal = new GregorianCalendar();
		if (date != null) {
			cal.setTimeInMillis(date.getTime());
		}
		return cal;
	}
	
	/**
	 * [ ARHIPAC ] Get first date in month
	 * @param day day; if null current time will be used
	 * @return first day of the month (time will be 00:00)
	 */
	static public Date getMonthFirstDay (Date day)
	{
		if (day == null)
			day = new Date(System.currentTimeMillis());
		Calendar cal = getCalendar(day);
		cal.setTimeInMillis(day.getTime());
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		//
		cal.set(Calendar.DAY_OF_MONTH, 1);	//	first
		return new Date (cal.getTimeInMillis());
	}	//	getMonthFirstDay
	
	/**
	 * [ ARHIPAC ] Return Day + offset (truncates)
	 * @param day Day; if null current time will be used
	 * @param offset months offset
	 * @return Day + offset (time will be 00:00)
	 * @return Teo Sarca, SC ARHIPAC SERVICE SRL
	 */
	static public Date addMonths (Date day, int offset)
	{
		if (day == null)
			day = new Date(System.currentTimeMillis());
		//
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(day);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		if (offset == 0)
			return new Date (cal.getTimeInMillis());
		cal.add(Calendar.MONTH, offset);
		return new Date (cal.getTimeInMillis());
	}	//	addMonths
	
	public static int getMonthsBetween (Date start, Date end)
	{
		Calendar startCal = getCalendar(start);
		Calendar endCal = getCalendar(end);
		//
		return endCal.get(Calendar.YEAR) * 12 + endCal.get(Calendar.MONTH)
				- (startCal.get(Calendar.YEAR) * 12 + startCal.get(Calendar.MONTH));
	}
		
	public static String dateToString(int y, int m, int d, String format) {
		Date date = getDay(y, m, d);
		SimpleDateFormat df = new SimpleDateFormat(format);

		return df.format(date);
	}

	public static String dateToString(Date value, String format) {

		if (value != null) {
			
			if(format == null || format.trim().isEmpty())
				format = "yyyy-MM-dd";
			
			try {
				SimpleDateFormat df = new SimpleDateFormat(format);
				return df.format(value);	
			} catch (Exception e) {
				return "";
			}			
		}

		return "";
	}
	public static Date stringToDate(String value, String format) {
		Date ts = null;
		if (value != null && value.length() > 0) {
			try {
				SimpleDateFormat df = new SimpleDateFormat(format);
				ts = new Date(df.parse(value).getTime());
			} catch (ParseException e) {
				//
			}
		}

		return ts;
	}
	
	public static Date stringToDate(String value) {
		
		if(CommonUtil.isEmpty(value)) {
			return null;
		}
		
		if(value.length() > 10) {
			value = value.substring(0, 10);
		}
		
		Date ts = null;
		String format = "dd/MM/yyyy";
		ts = stringToDate(value, format);
		if(ts == null) {
			format = "dd-MM-yyyy";
			ts = stringToDate(value, format);
		}		

		return ts;
	}
}	//	TimeUtil

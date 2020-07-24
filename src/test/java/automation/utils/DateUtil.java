package automation.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

public class DateUtil {

	public static String getCurrentDateTime() {
		String ranNum = "";
		DateFormat formatter = new SimpleDateFormat("MMM");
		SimpleDateFormat monthParse = new SimpleDateFormat("MM");
		DateFormat dformatter = new SimpleDateFormat("DD");
		SimpleDateFormat dateParse = new SimpleDateFormat("DD");
		Calendar cal = Calendar.getInstance();
		String month = Integer.toString(cal.get(Calendar.MONTH));
		String date = Integer.toString(cal.get(Calendar.DATE));
		try {
			ranNum = "_" + dformatter.format(dateParse.parse(date)) + formatter.format(monthParse.parse(month)) + "_"
					+ Integer.toString(cal.get(Calendar.HOUR_OF_DAY)) + Integer.toString(cal.get(Calendar.MINUTE));
		} catch (Exception e) {
		}
		return ranNum;
	}

	public static String getCurrentDate() {
		DateFormat formatter = new SimpleDateFormat("MM");
		SimpleDateFormat monthParse = new SimpleDateFormat("MM");
		DateFormat dformatter = new SimpleDateFormat("DD");
		SimpleDateFormat dateParse = new SimpleDateFormat("DD");
		Calendar cal = Calendar.getInstance();
		String month = Integer.toString(cal.get(Calendar.MONTH) + 1);
		String date = Integer.toString(cal.get(Calendar.DATE));
		try {
			month = formatter.format(monthParse.parse(month));
			date = dformatter.format(dateParse.parse(date));
		} catch (ParseException e) {
		}
		String year = Integer.toString(cal.get(Calendar.YEAR));
		String calDate = month + "/" + date + "/" + year;
		return calDate;
	}

	public static String getcurrentTime() {
		DateFormat dateFormat = new SimpleDateFormat("HHmmss");
		Date date = new Date();
		return dateFormat.format(date);
	}

	/**
	 * Method returns tomorrows date according to US time Zone
	 */
	public static String getTommorrowsDate() {
		Date date = new Date();
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		df.setTimeZone(TimeZone.getTimeZone("EST"));
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(df.parse(df.format(date)));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		c.add(Calendar.DATE, 1);
		date = c.getTime();
		return df.format(date);
	}

	public static String getTommorrowsDateFne() {
		DateFormat formatter = new SimpleDateFormat("MMM");
		SimpleDateFormat monthParse = new SimpleDateFormat("MM");
		DateFormat dformatter = new SimpleDateFormat("DD");
		SimpleDateFormat dateParse = new SimpleDateFormat("DD");
		Calendar cal = Calendar.getInstance();
		String month = Integer.toString(cal.get(Calendar.MONTH) + 1);
		String date = Integer.toString(cal.get(Calendar.DATE) + 1);
		try {
			month = formatter.format(monthParse.parse(month));
			date = dformatter.format(dateParse.parse(date));
		} catch (ParseException e) {
		}
		String calDate = month + " " + date;
		return calDate;
	}

	public static String getDayAfterTommorrowsDate() {
		DateFormat formatter = new SimpleDateFormat("MM");
		SimpleDateFormat monthParse = new SimpleDateFormat("MM");
		DateFormat dformatter = new SimpleDateFormat("DD");
		SimpleDateFormat dateParse = new SimpleDateFormat("DD");
		Calendar cal = Calendar.getInstance();
		String month = Integer.toString(cal.get(Calendar.MONTH) + 1);
		String date = Integer.toString(cal.get(Calendar.DATE) + 2);
		try {
			month = formatter.format(monthParse.parse(month));
			date = dformatter.format(dateParse.parse(date));
		} catch (ParseException e) {
		}
		String year = Integer.toString(cal.get(Calendar.YEAR));
		String calDate = month + "/" + date + "/" + year;
		return calDate;
	}

	public static String getDayAfterTommorrowsDateFne() {
		DateFormat formatter = new SimpleDateFormat("MMM");
		SimpleDateFormat monthParse = new SimpleDateFormat("MM");
		DateFormat dformatter = new SimpleDateFormat("DD");
		SimpleDateFormat dateParse = new SimpleDateFormat("DD");
		Calendar cal = Calendar.getInstance();
		String month = Integer.toString(cal.get(Calendar.MONTH) + 1);
		String date = Integer.toString(cal.get(Calendar.DATE) + 2);
		try {
			month = formatter.format(monthParse.parse(month));
			date = dformatter.format(dateParse.parse(date));
		} catch (ParseException e) {
		}
		String calDate = month + " " + date;
		return calDate;
	}

	public static String getDayToDayAfterTommorrowsDate() {
		DateFormat formatter = new SimpleDateFormat("MM");
		SimpleDateFormat monthParse = new SimpleDateFormat("MM");
		DateFormat dformatter = new SimpleDateFormat("DD");
		SimpleDateFormat dateParse = new SimpleDateFormat("DD");
		Calendar cal = Calendar.getInstance();
		String month = Integer.toString(cal.get(Calendar.MONTH) + 1);
		String date = Integer.toString(cal.get(Calendar.DATE) + 3);
		try {
			month = formatter.format(monthParse.parse(month));
			date = dformatter.format(dateParse.parse(date));
		} catch (ParseException e) {
		}
		String year = Integer.toString(cal.get(Calendar.YEAR));
		String calDate = month + "/" + date + "/" + year;
		return calDate;
	}

	public static String getDayToDayAfterTommorrowsDateFne() {
		DateFormat formatter = new SimpleDateFormat("MMM");
		SimpleDateFormat monthParse = new SimpleDateFormat("MM");
		DateFormat dformatter = new SimpleDateFormat("DD");
		SimpleDateFormat dateParse = new SimpleDateFormat("DD");
		Calendar cal = Calendar.getInstance();
		String month = Integer.toString(cal.get(Calendar.MONTH) + 1);
		String date = Integer.toString(cal.get(Calendar.DATE) + 3);
		try {
			month = formatter.format(monthParse.parse(month));
			date = dformatter.format(dateParse.parse(date));
		} catch (ParseException e) {
		}
		String calDate = month + " " + date;
		return calDate;
	}

	public static String getDate(String date) {
		if (date.equalsIgnoreCase("TomorrowDate"))
			return getTommorrowsDate();
		if (date.equalsIgnoreCase("DayAfterTomorrowDate"))
			return getDayAfterTommorrowsDate();
		if (date.equalsIgnoreCase("DayToDayAfterTomorrowDate"))
			return getDayToDayAfterTommorrowsDate();
		return fixedDate();
	}

	public static String getDateForFne(String date) {
		if (date.equalsIgnoreCase("TomorrowDate"))
			return getTommorrowsDateFne();
		if (date.equalsIgnoreCase("DayAfterTomorrowDate"))
			return getDayAfterTommorrowsDateFne();
		if (date.equalsIgnoreCase("DayToDayAfterTomorrowDate"))
			return getDayToDayAfterTommorrowsDateFne();
		return fixedDate();
	}

	public static String getTimeAsPerTimeZone(String time, String timeZOne) {
		time = time.split("Minutes")[1];
		DateFormat formatter = new SimpleDateFormat("hh:mm a");
		TimeZone tz = TimeZone.getTimeZone("EST5EDT");
		Calendar cal = new GregorianCalendar(tz);
		cal.add(Calendar.MINUTE, Integer.parseInt(time));
		formatter.setTimeZone(tz);
		return formatter.format(cal.getTime());
	}

	// public static String getTommorrowsDate(){
	// Calendar cal = Calendar.getInstance();
	// String month = Integer.toString(cal.get(Calendar.MONTH)+1);
	// String date = Integer.toString(cal.get(Calendar.DATE)+1);
	// String year = Integer.toString(cal.get(Calendar.YEAR));
	// if (month.length()==1){
	// month = "0".concat(month);
	// }
	// String calDate = month+"/"+date+"/"+year;
	// return calDate;
	//
	// }

	public static String fixedDate() {
		return "12/31/2013";
	}

	public static String getCurrentTime() {
		Date date = new Date();
		String strDateFormat = "HH:mm a";
		SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
		return (sdf.format(date));
	}

	public static String getESTDateTime() {
		Date date = new Date();
		DateFormat formatter = new SimpleDateFormat("dd MM yyyy HH:mm:ss a z");
		// Set the formatter to use a different time zone
		formatter.setTimeZone(TimeZone.getTimeZone("EST"));
		// Prints the date in the EST time zone
		return formatter.format(date);
	}

	public static Date addDays(Date date, int days) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, days); // minus number would decrement the days
		return cal.getTime();
	}

	public static String getdateBefore(int dateDifference) {
		Calendar cal = Calendar.getInstance();
		Date date = new Date();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		cal.setTime(date);
		cal.add(Calendar.DATE, dateDifference);
		Date dateBefore30Days = cal.getTime();
		df.setTimeZone(TimeZone.getTimeZone("GMT-5:00"));
		// System.out.println("Current EST Time: " + df.format(cal.getTime()));
		// System.out.println("Current Time: " + df.format(dateBefore30Days));
		return df.format(dateBefore30Days);
	}
	

	public static String getCustomizedDate() {
		SimpleDateFormat df = new SimpleDateFormat("M/d/yyyy");
		TimeZone tz1 = TimeZone.getTimeZone("GMT-4:00");
		Calendar cal1 = Calendar.getInstance(tz1);
		df.setTimeZone(tz1);
		System.out.println("Current EST Time: " + df.format(cal1.getTime()));
		return df.format(cal1.getTime());
	}

	public static String getCustomizedDate(String getDate) {
		String date_s = getDate;
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		Date date = new Date();
		try {
			date = sdf.parse(date_s);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		SimpleDateFormat dt1 = new SimpleDateFormat("M/d/yyyy");
		return dt1.format(date);
	}

	public static String getCustomizedDateForOrionUI(String getDate) {
		String date_s = getDate;
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		Date date = new Date();
		try {
			date = sdf.parse(date_s);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		SimpleDateFormat dt1 = new SimpleDateFormat("MMM d yyyy");
		return dt1.format(date);
	}

	public static String getCustomizedDateForRegentUI(String getDate) {
		String date_s = getDate;
		SimpleDateFormat sdf = new SimpleDateFormat("M/d/yyyy");
		Date date = new Date();
		try {
			date = sdf.parse(date_s);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		SimpleDateFormat dt1 = new SimpleDateFormat("MM/dd/yyyy");
		return dt1.format(date);
	}

	public static String getCurrentdateInStringWithGivenFormat(String format) {
		String date = new SimpleDateFormat(format).format(new Date());
		return date;
	}

	public static String getBackDateinGivenFormat(int dateDifference, String DateFormat) {
		Calendar cal = Calendar.getInstance();
		Date date = new Date();
		DateFormat df = new SimpleDateFormat(DateFormat);
		cal.setTime(date);
		cal.add(Calendar.DATE, dateDifference);
		Date dateBefore30Days = cal.getTime();
		df.setTimeZone(TimeZone.getTimeZone("GMT-4:00"));
		// System.out.println("Current EST Time: " + df.format(cal.getTime()));
		// System.out.println("Current Time: " + df.format(dateBefore30Days));
		return df.format(dateBefore30Days);
	}

	public static String getRandomDateBetween(int startYear, int endYear) {
		GregorianCalendar gc = new GregorianCalendar();
		int year = randBetween(startYear, endYear);
		gc.set(Calendar.YEAR, year);
		int dayOfYear = randBetween(1, gc.getActualMaximum(Calendar.DAY_OF_YEAR));
		gc.set(Calendar.DAY_OF_YEAR, dayOfYear);
		int yy= gc.get(Calendar.YEAR);
		int mm = gc.get(Calendar.MONTH) + 1;
		int dd = gc.get(Calendar.DAY_OF_MONTH);
		String date = DateTimeFormatter.ofPattern("MM-dd-yyyy", Locale.ENGLISH).format(LocalDate.of(yy, mm, dd)).toString();
		return date;
	}
	
	public static List <String> getLast7Dates() {
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy ");
		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		List<String> days = new ArrayList<String>();
		for(int i = 0; i < 7; i++){
		    cal.add(Calendar.DAY_OF_MONTH, -1);
		    date = cal.getTime();
		    days.add(sdf.format(date));
		  }
		return days;
	}

	
	 public static int randBetween(int start, int end) {
	        return start + (int)Math.round(Math.random() * (end - start));
	  }
}

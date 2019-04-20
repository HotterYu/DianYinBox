
package com.znt.lib.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.text.TextUtils;

@SuppressLint("SimpleDateFormat")
public class DateUtils
{
	
	
	/*public static boolean isCurrentInTimeScope(int beginHour, int beginMin, int endHour, int endMin)
	{
		boolean result = false;
		final long aDayInMillis = 1000 * 60 * 60 * 24;
		final long currentTimeMillis = System.currentTimeMillis();
		 
		Time now = new Time();
		now.set(currentTimeMillis);
		 
		Time startTime = new Time();
		startTime.set(currentTimeMillis);
		startTime.hour = beginHour;
		startTime.minute = beginMin;
		 
		Time endTime = new Time();
		endTime.set(currentTimeMillis);
		endTime.hour = endHour;
		endTime.minute = endMin;
		 
		if (!startTime.before(endTime))
		{
			// 跨天的特殊情况（比如22:00-8:00�?
			startTime.set(startTime.toMillis(true) - aDayInMillis);
			result = !now.before(startTime) && !now.after(endTime); // startTime <= now <= endTime
			Time startTimeInThisDay = new Time();
			startTimeInThisDay.set(startTime.toMillis(true) + aDayInMillis);
			if (!now.before(startTimeInThisDay)) 
			{
				result = true;
			}
		} 
		else 
		{
			// 普�?�情�?(比如 8:00 - 14:00)
			result = !now.before(startTime) && !now.after(endTime); // startTime <= now <= endTime
		}
		return result;
	}*/
	
	public static boolean isBetween(int startHour, int endHour) {  
        boolean result = false;  
        int length;  
  
        if (startHour > endHour) {  
            length = 24 - (startHour -endHour );  
        }else {  
            length = endHour - startHour;  
        }  
        System.out.println("length : "+length);  
        try {  
            result = isBetweenL(startHour, length);//调用时长的方�?  
        } catch (ParseException e) {  
            e.printStackTrace();  
        }  
        return result;  
    }  
  
    private static boolean isBetweenL(int startHour, int timeLong) throws ParseException {  
        if (startHour<0){  
            return false;  
        }  
        if (timeLong<1){  
            return false;  
        }  
        String startTime;  
        String endTime;  
  
        Calendar calendar = Calendar.getInstance();  
  
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");  
        Date date = new Date();  
        String today = dateFormat.format(date);  
  
        if (startHour < 0) {//�?始时间小于零设置�?0  
            startHour = 0;  
        }  
        timeLong = timeLong % 24;//时长超过24小时取余  
        int end = (startHour + timeLong) % 24;//结束时间,超过24时取�?  
  
        startTime = today + " " + buildHour(startHour) + ":00:00";  
        if (startHour < end) {//在同�?�?  
            endTime = today + " " + buildHour(end) + ":00:00";  
        } else {//不在同一�?  
            calendar.add(Calendar.HOUR_OF_DAY, timeLong);  
            String nextDay = dateFormat.format(calendar.getTime());  
            endTime = nextDay + " " + buildHour(end) + ":00:00";  
        }  
  
        DateFormat dateF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
  
        System.out.println("isBetween: start " + startTime + "  " + dateF.parse(startTime));  
        System.out.println("isBetween: end " + endTime + "  " + dateF.parse(endTime));  
        System.out.println("isBetween: date " + date);  
        System.out.println("isBetween: start - cur " + dateF.parse(startTime).compareTo(date));  
        System.out.println("isBetween: cur - end " + date.compareTo(dateF.parse(endTime)));  
        return dateF.parse(startTime).compareTo(date) == date.compareTo(dateF.parse(endTime));  
    }  
  
    private static  String buildHour(int hour) {  
        String temp;  
        if (hour > 9) {  
            temp = String.valueOf(hour);  
        } else {  
            temp = "0" + hour;  
        }  
        return temp;  
    }  
	
	/**
	  * 鑾峰彇鐜板湪鏃堕�?
	  * 
	  * @return 杩斿洖鏃堕棿绫诲�? yyyy-MM-dd HH:mm:ss
	  */
	public static Date getNowDate() 
	{
	   Date currentTime = new Date();
	   SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	   formatter.setTimeZone(TimeZone.getTimeZone("GMT+8"));//如果不指定时区，在有些机器上会出现时间误差�??
	   String dateString = formatter.format(currentTime);
	   ParsePosition pos = new ParsePosition(8);
	   Date currentTime_2 = formatter.parse(dateString, pos);
	   return currentTime_2;
	}

	/**
	  * 鑾峰彇鐜板湪鏃堕�?
	  * 
	  * @return杩斿洖鐭椂闂存牸寮�? yyyy-MM-dd
	  */
	public static Date getNowDateShort() 
	{
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		formatter.setTimeZone(TimeZone.getTimeZone("GMT+8"));//如果不指定时区，在有些机器上会出现时间误差�??
		String dateString = formatter.format(currentTime);
		ParsePosition pos = new ParsePosition(8);
		Date currentTime_2 = formatter.parse(dateString, pos);
		return currentTime_2;
	}

	/**
	  * 鑾峰彇鐜板湪鏃堕�?
	  * 
	  * @return杩斿洖�?�楃涓叉牸寮� yyyy-MM-dd HH:mm:ss
	  */
	public static String getStringDate() 
	{
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		formatter.setTimeZone(TimeZone.getTimeZone("GMT+8"));//如果不指定时区，在有些机器上会出现时间误差�??
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	/**
	  * 鑾峰彇鐜板湪鏃堕�?
	  * 
	  * @return 杩斿洖鐭椂闂村瓧绗︿覆鏍煎紡yyyy-MM-dd
	  */
	public static String getStringDateShort() 
	{
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		formatter.setTimeZone(TimeZone.getTimeZone("GMT+8"));//如果不指定时区，在有些机器上会出现时间误差�??
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	/**
	  * 鑾峰彇鏃堕棿 灏忔�?:鍒�;绉� HH:mm:ss
	  * 
	  * @return
	  */
	public static String getTimeShort()
	{
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		formatter.setTimeZone(TimeZone.getTimeZone("GMT+8"));//如果不指定时区，在有些机器上会出现时间误差�??
		Date currentTime = new Date();
		String dateString = formatter.format(currentTime);
		return dateString;
	}
	/**
	 * 鑾峰彇鏃堕棿 灏忔�?:鍒�;绉� HH:mm
	 * 
	 * @return
	 */
	public static String getTimeShortHead()
	{
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm：ss");
		formatter.setTimeZone(TimeZone.getTimeZone("GMT+8"));//如果不指定时区，在有些机器上会出现时间误差�??
		Date currentTime = new Date();
		String dateString = formatter.format(currentTime);
		return dateString;
	}
	public static long getSecFromTime(long curTime)
	{
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		formatter.setTimeZone(TimeZone.getTimeZone("GMT+8"));//如果不指定时区，在有些机器上会出现时间误差�??
		Date currentTime = new Date(curTime);
		String dateString = formatter.format(currentTime);
		String h = dateString.substring(0,2);
		String m = dateString.substring(3,5);
		String s = dateString.substring(6,8);

		int hInt = Integer.parseInt(h);
		int mInt = Integer.parseInt(m);
		int sInt = Integer.parseInt(s);
		return hInt * 60 * 60 + mInt*60+sInt;
		//return dateString;
	}

	/**
	  * 灏嗛暱鏃堕棿鏍煎紡�?�楃涓茶浆鎹负鏃堕棿 yyyy-MM-dd HH:mm:ss
	  * 
	  * @param strDate
	  * @return
	  */
	public static Date strToDateLong(String strDate) 
	{
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		formatter.setTimeZone(TimeZone.getTimeZone("GMT+8"));//如果不指定时区，在有些机器上会出现时间误差�??
		ParsePosition pos = new ParsePosition(0);
		Date strtodate = formatter.parse(strDate, pos);
		return strtodate;
	}

	/**  * 灏嗛暱鏃堕棿鏍煎紡鏃堕棿杞崲涓哄瓧绗︿�? yyyy-MM-dd HH:mm:ss  *   * @param dateDate  * @return  */
	public static String dateToStrLong(Date dateDate)
	{
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		formatter.setTimeZone(TimeZone.getTimeZone("GMT+8"));//如果不指定时区，在有些机器上会出现时间误差�??
		String dateString = formatter.format(dateDate);
		return dateString;
	}

	public static String dateToStr(Date dateDate)
	{
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		formatter.setTimeZone(TimeZone.getTimeZone("GMT+8"));//如果不指定时区，在有些机器上会出现时间误差�??
		String dateString = formatter.format(dateDate);
		return dateString;
	}

	public static Date strToDate(String strDate)
	{
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		formatter.setTimeZone(TimeZone.getTimeZone("GMT+8"));//如果不指定时区，在有些机器上会出现时间误差�??
		ParsePosition pos = new ParsePosition(0);
		Date strtodate = formatter.parse(strDate, pos);
		return strtodate;
	}

	public static Date getNow()
	{
		Date currentTime = new Date();
		return currentTime;
	}

	public static Date getLastDate(long day)
	{
		Date date = new Date();
		long date_3_hm = date.getTime() - 3600000 * 34 * day;
		Date date_3_hm_date = new Date(date_3_hm);
		return date_3_hm_date;
	}

	public static String getStringToday()
	{
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd HHmmss");
		formatter.setTimeZone(TimeZone.getTimeZone("GMT+8"));//如果不指定时区，在有些机器上会出现时间误差�??
		String dateString = formatter.format(currentTime);
		return dateString;
	}
	public static String getStringToday(String format) 
	{
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		formatter.setTimeZone(TimeZone.getTimeZone("GMT+8"));//如果不指定时区，在有些机器上会出现时间误差�??
		String dateString = formatter.format(currentTime);
		return dateString;
	}
	public static String getStringTime(long time) 
	{
		Date currentTime = new Date(time);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		formatter.setTimeZone(TimeZone.getTimeZone("GMT+8"));//如果不指定时区，在有些机器上会出现时间误差�??
		String dateString = formatter.format(currentTime);
		return dateString;
	}
	public static String getStringTimeHead(long time) 
	{
		Date currentTime = new Date(time);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		formatter.setTimeZone(TimeZone.getTimeZone("GMT+8"));//如果不指定时区，在有些机器上会出现时间误差�??
		String dateString = formatter.format(currentTime);
		return dateString;
	}
	/**
	  * 寰楀埌鐜板湪灏忔�?
	  */
	public static String getHour() 
	{
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		formatter.setTimeZone(TimeZone.getTimeZone("GMT+8"));//如果不指定时区，在有些机器上会出现时间误差�??
		String dateString = formatter.format(currentTime);
		String hour;
		hour = dateString.substring(11, 13);
		return hour;
	}
	public static String getHour(long curTime) 
	{
		if(curTime <= 0)
			return "";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		formatter.setTimeZone(TimeZone.getTimeZone("GMT+8"));//如果不指定时区，在有些机器上会出现时间误差�??
		String dateString = formatter.format(curTime);
		String hour;
		hour = dateString.substring(11, 13);
		return hour;
	}

	/**
	  * 寰楀埌鐜板湪鍒嗛�?
	  * 
	  * @return
	  */
	public static String getTime() 
	{
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		formatter.setTimeZone(TimeZone.getTimeZone("GMT+8"));//如果不指定时区，在有些机器上会出现时间误差�??
		String dateString = formatter.format(currentTime);
		String min;
		min = dateString.substring(14, 16);
		return min;
	}

	/**
	  * 鏍规嵁鐢ㄦ埛浼犲叆鐨勬椂闂磋〃绀烘牸寮忥紝杩斿洖褰撳墠鏃堕棿鐨勬牸寮�? 濡傛灉鏄痽yyyMMdd锛屾敞鎰忓瓧姣峺涓嶈兘澶у啓銆�?
	  * 
	  * @param sformat
	  *            yyyyMMddhhmmss
	  * @return
	  */
	public static String getUserDate(String sformat) 
	{
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat(sformat);
		formatter.setTimeZone(TimeZone.getTimeZone("GMT+8"));//如果不指定时区，在有些机器上会出现时间误差�??
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	/**
	  * 浜屼釜灏忔椂鏃堕棿闂寸殑宸��,蹇呴』淇濊瘉浜屼釜鏃堕棿閮芥�?"HH:MM"鐨勬牸寮忥紝杩斿洖�?�楃鍨嬬殑鍒嗛�?
	  */
	public static String getTwoHour(String st1, String st2) 
	{
		String[] kk = null;
		String[] jj = null;
		kk = st1.split(":");
		jj = st2.split(":");
		if (Integer.parseInt(kk[0]) < Integer.parseInt(jj[0]))
			return "0";
		else 
		{
			double y = Double.parseDouble(kk[0]) + Double.parseDouble(kk[1]) / 60;
			double u = Double.parseDouble(jj[0]) + Double.parseDouble(jj[1]) / 60;
			if ((y - u) > 0)
				return y - u + "";
			else
				return "0";
		}
	}

	/**
	  * 寰楀埌浜屼釜鏃ユ湡闂寸殑闂撮殧澶╂暟
	  */
	public static String getTwoDay(String sj1, String sj2) 
	{
		SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
		myFormatter.setTimeZone(TimeZone.getTimeZone("GMT+8"));//如果不指定时区，在有些机器上会出现时间误差�??
		long day = 0;
		try 
		{
			Date date = myFormatter.parse(sj1);
			Date mydate = myFormatter.parse(sj2);
			day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);
		} 
		catch (Exception e) 
		{
			return "";
		}
		return day + "";
	}

	/**
	  * 鏃堕棿鍓嶆帹鎴栧悗鎺ㄥ垎閽�,鍏朵腑JJ琛ㄧず鍒嗛挓.
	  */
	public static String getPreTime(String sj1, String jj) 
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		format.setTimeZone(TimeZone.getTimeZone("GMT+8"));//如果不指定时区，在有些机器上会出现时间误差�?? 
		String mydate1 = "";
		try 
		{
			Date date1 = format.parse(sj1);
			long Time = (date1.getTime() / 1000) + Integer.parseInt(jj) * 60;
			date1.setTime(Time * 1000);
			mydate1 = format.format(date1);
		} 
		catch (Exception e) 
		{
	  
		}
		return mydate1;
	}

	/**
	  * 寰楀埌涓�涓椂闂村欢鍚庢垨鍓嶇Щ鍑犲ぉ鐨勬椂闂�,nowdate涓烘椂闂�?,delay涓哄墠绉绘垨鍚庡欢鐨勫ぉ鏁�
	  */
	public static String getNextDay(String nowdate, String delay)
	{
		try
		{
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			format.setTimeZone(TimeZone.getTimeZone("GMT+8"));//如果不指定时区，在有些机器上会出现时间误差�?? 
			String mdate = "";
			Date d = strToDate(nowdate);
			long myTime = (d.getTime() / 1000) + Integer.parseInt(delay) * 24 * 60 * 60;
			d.setTime(myTime * 1000);
			mdate = format.format(d);
			return mdate;
		}
		catch(Exception e)
		{
			return "";
		}
	}

	/**
	  * 鍒ゆ柇鏄惁娑﹀�?
	  * 
	  * @param ddate
	  * @return
	  */
	public static boolean isLeapYear(String ddate) 
	{
		/**
	   * 璇︾粏璁捐锛� 1.琚�400鏁撮櫎鏄棸骞达紝鍚�?垯锛�? 2.涓嶈兘琚�?4鏁撮櫎鍒欎笉鏄棸骞�? 3.鑳�?�4鏁撮櫎鍚屾椂涓嶈兘琚�?100鏁撮櫎鍒欐槸闂板�?
	   * 3.鑳�?�4鏁撮櫎鍚屾椂鑳�?�100鏁撮櫎鍒欎笉鏄棸骞�?
	   */
		Date d = strToDate(ddate);
		GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
		gc.setTime(d);
		int year = gc.get(Calendar.YEAR);
		if ((year % 400) == 0)
			return true;
		else if ((year % 4) == 0) 
		{
			if ((year % 100) == 0)
				return false;
			else
				return true;
		}
		else
			return false;
	}

	/**
	  * 杩斿洖缇庡浗鏃堕棿鏍煎紡 26 Apr 2006
	  * 
	  * @param str
	  * @return
	  */
	public static String getEDate(String str) 
	{
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		formatter.setTimeZone(TimeZone.getTimeZone("GMT+8"));//如果不指定时区，在有些机器上会出现时间误差�??
		ParsePosition pos = new ParsePosition(0);
		Date strtodate = formatter.parse(str, pos);
		String j = strtodate.toString();
		String[] k = j.split(" ");
		return k[2] + k[1].toUpperCase() + k[5].substring(2, 4);
	}

	/**
	  * 鑾峰彇涓�涓湀鐨勬渶鍚庝竴澶�
	  * 
	  * @param dat
	  * @return
	  */
	public static String getEndDateOfMonth(String dat) 
	{
		// yyyy-MM-dd
		String str = dat.substring(0, 8);
		String month = dat.substring(5, 7);
		int mon = Integer.parseInt(month);
		if (mon == 1 || mon == 3 || mon == 5 || mon == 7 || mon == 8 || mon == 10 || mon == 12) 
		{
			str += "31";
		} 
		else if (mon == 4 || mon == 6 || mon == 9 || mon == 11)
		{
			str += "30";
		} 
		else 
		{
			if (isLeapYear(dat)) 
			{
				str += "29";
			} 
			else 
			{
				str += "28";
			}
		}
		return str;
	}

	/**
	  * 鍒ゆ柇浜屼釜鏃堕棿鏄惁鍦ㄥ悓涓�涓懆
	  * 
	  * @param date1
	  * @param date2
	  * @return
	  */
	public static boolean isSameWeekDates(Date date1, Date date2) 
	{
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(date1);
		cal2.setTime(date2);
		int subYear = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
		if (0 == subYear) 
		{
			if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
				return true;
		} 
		else if (1 == subYear && 11 == cal2.get(Calendar.MONTH)) 
		{
			// 濡傛�?12鏈堢殑鏈�鍚庝竴鍛ㄦí璺ㄦ潵骞寸涓�鍛ㄧ殑璇濆垯鏈�鍚庝竴鍛ㄥ嵆绠楀仛鏉ュ勾鐨勭涓�鍛�?
			if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
				return true;
		}
		else if (-1 == subYear && 11 == cal1.get(Calendar.MONTH)) 
		{
			if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
				return true;
		}
		return false;
	}

	/**
	  * 浜х敓鍛ㄥ簭鍒�?,鍗冲緱鍒板綋鍓嶆椂闂存墍鍦ㄧ殑骞村害鏄鍑犲懆
	  * 
	  * @return
	  */
	public static String getSeqWeek()
	{
		Calendar c = Calendar.getInstance(Locale.CHINA);
		String week = Integer.toString(c.get(Calendar.WEEK_OF_YEAR));
		if (week.length() == 1)
			week = "0" + week;
		String year = Integer.toString(c.get(Calendar.YEAR));
		return year + week;
	}


	public static String getWeek(String sdate, String num) 
	{
		// 鍐嶈浆鎹负鏃堕�?
		Date dd = strToDate(sdate);
		Calendar c = Calendar.getInstance();
		c.setTime(dd);
		if (num.equals("1")) // 杩斿洖鏄熸湡涓�鎵�鍦ㄧ殑鏃ユ湡
			c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		else if (num.equals("2")) // 杩斿洖鏄熸湡浜屾墍鍦ㄧ殑鏃ユ�?
			c.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
		else if (num.equals("3")) // 杩斿洖鏄熸湡涓夋墍鍦ㄧ殑鏃ユ�?
			c.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
		else if (num.equals("4")) // 杩斿洖鏄熸湡鍥涙墍鍦ㄧ殑鏃ユ�?
			c.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
		else if (num.equals("5")) // 杩斿洖鏄熸湡浜旀墍鍦ㄧ殑鏃ユ�?
			c.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
		else if (num.equals("6")) // 杩斿洖鏄熸湡鍏墍鍦ㄧ殑鏃ユ�?
			c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
		else if (num.equals("0")) // 杩斿洖鏄熸湡鏃ユ墍鍦ㄧ殑鏃ユ�?
			c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		return new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
	}

	public static int getWeekBylong(long time)
	{
		Date date = new Date(time);
		int week = date.getDay();
		if(week == 0)
			week = 7;
		return week;
		/*Calendar c = Calendar.getInstance();
		c.setTime(date);
		int wek=c.get(Calendar.DAY_OF_WEEK);
		return new SimpleDateFormat("EEEE").format(c.getTime());*/
	}

	public static String getWeek(String sdate)
	{

		Date date = strToDate(sdate);
		int week = date.getDay();
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return new SimpleDateFormat("EEEE").format(c.getTime());
	}
	public static String getWeekStr(String sdate)
	{
		String str = "";
		str = getWeek(sdate);
		if("1".equals(str))
		{
			str = "鏄熸湡鏃�?";
		}
		else if("2".equals(str))
		{
			str = "鏄熸湡涓�?";
		}
		else if("3".equals(str))
		{
			str = "鏄熸湡浜�?";
		}
		else if("4".equals(str))
		{
			str = "鏄熸湡涓�?";
		}
		else if("5".equals(str))
		{
			str = "鏄熸湡鍥�?";
		}
		else if("6".equals(str))
		{
			str = "鏄熸湡浜�?";
		}
		else if("7".equals(str))
		{
			str = "鏄熸湡鍏�?";
		}
		return str;
	}


	public static long getDays(String date1, String date2)
	{
		if (date1 == null || date1.equals(""))
			return 0;
		if (date2 == null || date2.equals(""))
			return 0;
		// 杞崲涓烘爣鍑嗘椂闂�?
		SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
		myFormatter.setTimeZone(TimeZone.getTimeZone("GMT+8"));//如果不指定时区，在有些机器上会出现时间误差�??
		Date date = null;
		Date mydate = null;
		try 
		{
			date = myFormatter.parse(date1);
			mydate = myFormatter.parse(date2);
		} 
		catch (Exception e) 
		{
			return 0;
		}
		long day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);
		return day;
	}


	public static String getNowMonth(String sdate)
	{
		// 鍙栬鏃堕棿鎵�鍦ㄦ�?鐨勪竴鍙�?
		sdate = sdate.substring(0, 8) + "01";
		// 寰楀埌杩欎釜鏈堢�?1鍙锋槸鏄熸湡鍑�
		Date date = strToDate(sdate);
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int u = c.get(Calendar.DAY_OF_WEEK);
		String newday = getNextDay(sdate, (1 - u) + "");
		return newday;
	}

	public static String getNo(int k)
	{
		return getUserDate("yyyyMMddhhmmss") + getRandom(k);
	}

	public static String getRandom(int i)
	{
		Random jjj = new Random();
		// int suiJiShu = jjj.nextInt(9);
		if (i == 0)
			return "";
		String jj = "";
		for (int k = 0; k < i; k++) 
		{
			jj = jj + jjj.nextInt(9);
		}
		return jj;
	}
    public static String getTimeDisplay(long time)
    {  
  
        final long currTime = System.currentTimeMillis();  
        final Date formatSysDate = new Date(currTime);  
  
        // 鍒ゆ柇褰撳墠鎬诲ぉ鏁�?  
        final int sysMonth = formatSysDate.getMonth() + 1;  
        final int sysYear = formatSysDate.getYear();  
  
        // 璁＄畻鏈嶅姟鍣ㄨ繑鍥炴椂闂翠笌褰撳墠鏃堕棿宸��?  
        final long seconds = (currTime - time) / 1000;  
        final long minute = seconds / 60;  
        final long hours = minute / 60;  
        final long day = hours / 24;  
        final long month = day / calculationDaysOfMonth(sysYear, sysMonth);  
        final long year = month / 12;  
  
        if (year > 0 || month > 0 || day > 0) 
        {  
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(  
                    "yyyy-MM-dd HH:mm");  
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));//如果不指定时区，在有些机器上会出现时间误差�?? 
            return simpleDateFormat.format(new Date(time));  
        }
        else if (hours > 0) 
        {  
            return hours + "灏忔椂鍓�?";  
        } else if (minute > 0) 
        {  
            return minute + "鍒嗛挓鍓�?";  
        } 
        else if (seconds > 0) 
        {  
            return "1" + "鍒嗛挓鍓�?";  
            // return seconds + context.getString(R.string.str_secondago);  
        } 
        else 
        {  
//          return "1" + context.getString(R.string.str_secondago);  
            return "1" + "鍒嗛挓鍓�?"; //閮芥崲鎴愬垎閽熷�?  
        }  
    }  
      
      
    private static SimpleDateFormat getFormat(String partten) 
    {  
        return new SimpleDateFormat(partten);  
    }  
      
    /** 
     * 璁＄畻鏈堟暟 
     *  
     * @return 
     */  
    private static int calculationDaysOfMonth(int year, int month) 
    {  
        int day = 0;  
        switch (month) 
        {  
        // 31澶�  
        case 1:  
        case 3:  
        case 5:  
        case 7:  
        case 8:  
        case 10:  
        case 12:  
            day = 31;  
            break;  
        // 30澶�  
        case 4:  
        case 6:  
        case 9:  
        case 11:  
            day = 30;  
            break;  
        // 璁＄�?2鏈堝ぉ鏁�?  
        case 2:  
            day = year % 100 == 0 ? year % 400 == 0 ? 29 : 28  
                    : year % 4 == 0 ? 29 : 28;  
            break;  
        }  
  
        return day;  
    }  
    
    public static String getDateFromLong(long time)
    {
    	Date currentTime = new Date(time);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		formatter.setTimeZone(TimeZone.getTimeZone("GMT+8"));//如果不指定时区，在有些机器上会出现时间误差�??
		String dateString = formatter.format(currentTime);
		return dateString;
    }
    public static String getEndDateFromLong(long time)
    {
    	Date currentTime = new Date(time);
    	SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
    	formatter.setTimeZone(TimeZone.getTimeZone("GMT+8"));//如果不指定时区，在有些机器上会出现时间误差�??
    	String dateString = formatter.format(currentTime);
    	return dateString;
    }
    
    public static String getTimeFromLong(long time)
    {
    	String result = "";
    	
    	int day = (int) (time / (1000 * 60 * 60 * 24));
    	time = (int) (time % (1000 * 60 * 60 * 24));
    	if(day > 0)
    		result += day + "澶� ";
    	
    	int hour = (int) (time / (1000 * 60 * 60));
    	time = (int) (time % (1000 * 60 * 60));
    	result += hour + "灏忔�? ";
    	
    	int min = (int) (time / (1000 * 60));
    	time = (int) (time % (1000 * 60));
    	result += min + "鍒嗛�? ";
    	
    	int sec = (int) (time / (1000));
    	result += sec + "绉�";
    	
    	return result;
    }
    public static String getDeliverTime(long time)
    {
    	String result = "";
    	
    	int day = (int) (time / (1000 * 60 * 60 * 24));
    	time = (int) (time % (1000 * 60 * 60 * 24));
    	if(day > 0)
    		result += day + "澶� ";
    	
    	int hour = (int) (time / (1000 * 60 * 60));
    	time = (int) (time % (1000 * 60 * 60));
    	if(hour > 0)
    		result += hour + "灏忔�? ";
    	
    	int min = (int) (time / (1000 * 60));
    	time = (int) (time % (1000 * 60));
    	result += min + "鍒嗛�? ";
    	
    	/*int sec = (int) (time / (1000));
    	result += sec + "绉�";*/
    	
    	return result;
    }
    
    public static long timeToInt(String time, String tag)
    {
    	/*String s="2014-06-09 10:44:39";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d2=null;
        try 
        {
			d2 = sdf.parse(s);
			return d2.getTime();
		} 
        catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//灏哠tring to Date绫诲�?
        return 0;*/
    	int timeLong = 0;
    	String[] strs = TextUtils.split(time, tag);
    	int hour = 0;
    	int min = 0;
    	int sec = 0;
    	if(strs.length > 0)
    	{
    		String tHour = strs[0];
    		if(!TextUtils.isEmpty(tHour))
    			hour = Integer.parseInt(tHour);
    		String tMin = strs[1];
    		if(!TextUtils.isEmpty(tMin))
    			min = Integer.parseInt(tMin);
    		if(strs.length > 1)
    		{
    			String tSec = strs[2];
    			if(!TextUtils.isEmpty(tSec))
    				sec = Integer.parseInt(tSec);
    		}
    	}
    	timeLong = hour * 60 * 60 + min * 60 + sec;
    	return timeLong;
    }
    
    public static String formateTime(long millis)
	{
		String str = "";
		int hour = 0;
		int time = (int) (millis / 1000);
		int second = time % 60;
		int minute = time / 60;
		if (minute >= 60){
			hour = minute / 60;
			minute %= 60;
			str = String.format("%02d:%02d:%02d", hour, minute, second);
		}else{
			str = String.format("%02d:%02d", minute, second);
		}

		
		return str;
		
	}
}
 

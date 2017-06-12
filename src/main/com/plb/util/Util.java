package com.plb.util;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;



public class Util {

	
	
	public static boolean equals(Object o1, Object o2) {
		if ( o1 == null && o2 == null ) {
			return true;
		} else if ( o1 == null && o2 != null ) {
			return false;
		} else if ( o1 != null && o2 == null ) {
			return false;
		} else {
			return o1.equals(o2);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static String getCollectionAsString(Collection c) {
		boolean bFirst = true;
		StringBuffer sbf = new StringBuffer();
		for ( Object o : c) {
			if ( bFirst ) {
				sbf.append(o.toString());
				bFirst = false;
			} else {
				sbf.append(" ,"+o);
			}
		}
		return sbf.toString();
	}
	
	
	
	public static boolean equalsWithNull(Object o1, Object o2) {
		if ( o1 == null && o2 == null ) {
			return true;
		} else if ( o1 == null && o2 != null ) {
			return false;
		} else if ( o1 != null && o2 == null ) {
			return false;
		} else {
			return o1.equals(o2);
		}
	}
	@SuppressWarnings("deprecation")
	public static boolean dateEqualsWithNull(Date o1, Date o2) {
		if ( o1 == null && o2 == null ) {
			return true;
		} else if ( o1 == null && o2 != null ) {
			return false;
		} else if ( o1 != null && o2 == null ) {
			return false;
		} else {
			return o1.getYear() == o2.getYear() && o1.getMonth() == o2.getMonth() && o1.getDay() == o2.getDay();
		}
	}
	
	public static boolean monthEqual(Date d1, Date d2) {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		
		cal1.setTime(d1);
		cal2.setTime(d2);
		
		return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
	}
	
	@SuppressWarnings("rawtypes")
	public static boolean identicalList(List l1, List l2) {
		if ( l1 == null && l2 == null ) {
			return true;
		} else if ( l1 == null && l2 != null ) {
			return false;
		} else if ( l1 != null && l2 == null ) {
			return false;
		}
		if ( l1.size() != l2.size() )  {
			return false;
		}
		for ( Object o : l1) {
			if ( !l2.contains(o) ) {
				return false;
			}
		}
		return true;
	}

}

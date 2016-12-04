package com.plb.si.util;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class Labels {

	public static ResourceBundle labelsBundle=ResourceBundle.getBundle("labels");
	
	public static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	public static SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm");
	public static SimpleDateFormat timestampFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	
	public static String getString(String key) {	
		return labelsBundle.getString(key);
	}
	public static String getString(String key, Object ...arguments ) {	
		return MessageFormat.format(getString(key), arguments);
	}
	
	public static String formatDate(Date date) {
		return dateFormat.format(date);
	}
	public static String formatHour(Date date) {
		return hourFormat.format(date);
	}
	public static String formatTimestamp(Date date) {
		return timestampFormat.format(date);
	}
}

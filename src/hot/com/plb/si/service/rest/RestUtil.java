package com.plb.si.service.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.map.ObjectMapper;

public class RestUtil {

	public static String ALL = "ALL";
	public static String TWO_YEAR = "2Y";
	public static String ONE_YEAR = "1Y";
	public static String SIX_MONTH = "6M";
	public static String THREE_MONTH = "3M";
	public static String ONE_MONTH = "1M";
	
	public static String JSON_TYPE="application/json;charset=utf-8";
	
	public static Date getDate(String selectDate) {
		if ( selectDate.equals(ALL) ) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		if (selectDate.equals(TWO_YEAR)) {
			cal.add(Calendar.YEAR, -2);
		} else if (selectDate.equals(ONE_YEAR)) {
			cal.add(Calendar.YEAR, -1);
		} else if (selectDate.equals(SIX_MONTH)) {
			cal.add(Calendar.MONTH, -6);
		} else if (selectDate.equals(THREE_MONTH)) {
			cal.add(Calendar.MONTH, -3);
		} else if (selectDate.equals(ONE_MONTH)) {
			cal.add(Calendar.MONTH, -1);
		}
		return cal.getTime();
	}
	public static <T> T parsePost(HttpServletRequest request, Class<T> classe) throws IOException {
		InputStreamReader isr = new InputStreamReader(request.getInputStream());
		BufferedReader in = new BufferedReader(isr);
		String line = in.readLine();
		
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(line, classe);
	}
}

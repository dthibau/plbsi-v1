package com.plb.dto;

import java.text.DateFormat;
import java.util.Date;

public class DateFormatFrench{

	public String convertDatetoSTring(Date d){
		DateFormat shortDateFormat = DateFormat.getDateTimeInstance(
				DateFormat.SHORT,
				DateFormat.SHORT);
		return shortDateFormat.format(d);
	}

}

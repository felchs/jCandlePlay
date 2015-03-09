/*
 * This source file is part of jCandlePlay
 * 
 * jCandlePlay is free software: you can redistribute it
 * and/or modify it under the terms of the MIT License.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.jcandleplay.graph.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * This is a utility class for date handling
 * The default time zone is UTC
 * @author Felipe Santos
 */
public class GraphDateUtils
{
	/**
	 * Default TimeZone to use on date conversions
	 */
	private static TimeZone defaultTimeZone = TimeZone.getTimeZone("UTC");
	
	/**
	 * A default date format
	 */
	private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * Constructor
	 */
	public GraphDateUtils() 
	{
		dateFormat.setTimeZone(defaultTimeZone);
	}
    
	/**
	 * Gets the default TimeZone
	 * @return the default TimeZone
	 */
    public static TimeZone getDefaultTimeZone()
	{
		return defaultTimeZone;
	}
    
    /**
     * Sets the default TimeZone
     * @param defaultTimeZone the default TimeZone
     */
    public static void setDefaultTimeZone(TimeZone defaultTimeZone) {
		GraphDateUtils.defaultTimeZone = defaultTimeZone;
	}
    
    /**
     * Gets an instance of {@link Calendar}
     * @return an instance of {@link Calendar}
     */
	public static Calendar getCalendar()
	{
		TimeZone timeZone = getDefaultTimeZone();
		return Calendar.getInstance(timeZone);
	}
	
	/**
	 * Converts a long date to a formated string
	 * @param date the long date to convert
	 * @return a formated date string using {@link GraphDateUtils#dateFormat}
	 */
    public static String longToStrDate(long date)
    {
    	return longToStrDate(date, getDefaultTimeZone());
    }
    
    /**
	 * Converts a long date to a formated string
	 * @param date the long date to convert
     * @param timeZone the {@link TimeZone} to use when converting the data
     * @return a formated date string using {@link GraphDateUtils#dateFormat}
     */
    public static String longToStrDate(long date, TimeZone timeZone)
    {
    	Calendar cal = Calendar.getInstance(timeZone);
    	cal.setTimeInMillis(date);
    	Date dt = cal.getTime();
    	dateFormat.setTimeZone(timeZone);
    	return dateFormat.format(dt);
    }

    /**
     * Converts a {@link Date} to a String one passing by {@link GraphDateUtils#dateFormat}
     * @param currDate a {@link Date} to be converted as string
     * @return a {@link Date} to be converted as string
     */
	public static String dateToStrDate(Date currDate)
	{
		return dateFormat.format(currDate);
	}
	
	/**
	 * Converts formated string date to long
	 * @param stringDateTime a formated string date format: {@link GraphDateUtils#dateFormat}
	 * @return a long date
	 */
	public static long getDateTimeWithString(String stringDateTime)
	{
		return getDateTimeWithString(getDefaultTimeZone(), stringDateTime);
	}
	
	/**
	 * Converts formated string date to long
	 * @param timeZone the {@link TimeZone} to use when converting the data
	 * @param stringDateTime a formated string date format: {@link GraphDateUtils#dateFormat} 
	 * @param stringDateTime
	 * @return a long date
	 */
	public static long getDateTimeWithString(TimeZone timeZone, String stringDateTime)
	{
	    try
	    {
	    	dateFormat.setTimeZone(timeZone);
	    	Date parse = dateFormat.parse(stringDateTime);
			return parse.getTime();
		}
	    catch (ParseException e)
	    {
			e.printStackTrace();
		}
	    
		throw new RuntimeException();
	}
}
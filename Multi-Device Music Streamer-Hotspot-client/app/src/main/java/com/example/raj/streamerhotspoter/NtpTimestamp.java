/*******************************************************************************
 *        File: Timestamp.java
 *      Author: Morteza Ansarinia <ansarinia@me.com>
 *  Created on: November 9, 2013
 *     Project: No Time Protocol <http://time.onto.ir>
 *   Copyright: See the file "LICENSE" for the full license governing this code.
 *******************************************************************************/
package com.example.raj.streamerhotspoter;

import java.util.Date;

/** 
 * Number of seconds since 1-1-1900 00:00:00 (Standard NTP timestamp). 
 */
public class NtpTimestamp {
	
	double value;
	
	/**
	 * Seconds between NTP and POSIX time (1-1-1900 -- 1-1-1970 UTC)
	 */
	public static final long POSIX_TIME_CORRECTION = 2208988800l;
	
	/**
	 * Maximum time that can be represented with 4 bytes - 2^32
	 */
	public static final double MAX_TIME = 4294967296.0;
	
	/**
	 * Constructor for NTP packet timestamps (64 bits)
	 * @param data
	 */
	public NtpTimestamp(byte[] data) {
		if (data == null || data.length != 8) {
			throw new IllegalArgumentException("Invalid input!");
		}

		double d = 0.0;

		for (int i = 0; i < 8; ++i) {
			int usigned = (data[i] < 0) ? 256 + data[i] : data[i];
			d = 256.0 * d + usigned;
		}

		this.value = d/MAX_TIME;
	}


	//  Converts POSIX time to NTP timestamp
	 //@param posixMillis;
	//
	public NtpTimestamp(long posixTime) {
		double posixSec = ((double)posixTime) / 1000.0;
		this.value = posixSec + POSIX_TIME_CORRECTION;
	}
	
	public long toPOSIX() {
		long time = (long)((this.value - POSIX_TIME_CORRECTION) * 1000);
		return time;
	}
	
	public static NtpTimestamp now() {
		return new NtpTimestamp(System.currentTimeMillis());
	}
	
	public String toString() {
		return new Date(toPOSIX()).toString();
	}
	
	/**
	 * Create the NTP timestamp array (64 bits)
	 * @return
	 */
	public byte[] toByteArray() {
		byte[] data = new byte[8];
		
		double d = this.value/MAX_TIME;
		int k = 0;
		
		for (int i = 0; i < 8; ++i) {
			if ((k = (int) (d *= 256.0)) >= 256) {
				k = 255;
			}
			data[i] = (byte) k;
			d -= k;
		}
				
		return data;
	}
	
}

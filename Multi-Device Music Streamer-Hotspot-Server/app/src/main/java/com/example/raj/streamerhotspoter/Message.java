/*******************************************************************************
 *        File: Message.java
 *      Author: Morteza Ansarinia <ansarinia@me.com>
 *  Created on: November 9, 2013
 *     Project: No Time Protocol <http://time.onto.ir>
 *   Copyright: See the file "LICENSE" for the full license governing this code.
 *******************************************************************************/
package com.example.raj.streamerhotspoter;

import java.util.Arrays;

/**
 * SNTP v4 - RFC 2030
 * More info: http://www.eecis.udel.edu/~mills/database/reports/ntp4/ntp4.pdf
 */

/**
 * RFC 2030,Section 4
 *
 *                         1                   2                   3
 *       0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 *      +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *      |LI | VN  |Mode |    Stratum    |     Poll      |   Precision   |
 *      +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *      |                          Root Delay                           |
 *      +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *      |                       Root Dispersion                         |
 *      +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *      |                     Reference Identifier                      |
 *      +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *      |                                                               |
 *      |                   Reference Timestamp (64)                    |
 *      |                                                               |
 *      +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *      |                                                               |
 *      |                   Originate Timestamp (64)                    |
 *      |                                                               |
 *      +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *      |                                                               |
 *      |                    Receive Timestamp (64)                     |
 *      |                                                               |
 *      +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *      |                                                               |
 *      |                    Transmit Timestamp (64)                    |
 *      |                                                               |
 *      +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *      |                 Key Identifier (optional) (32)                |
 *      +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *      |                                                               |
 *      |                                                               |
 *      |                 Message Digest (optional) (128)               |
 *      |                                                               |
 *      |                                                               |
 *      +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 */

public class Message {
	
	/** Leap Indicator (2 bits). */
	public byte leap;
	
	/** Version Number (3 bits). */
	public byte version;
	
	/** Mode (3 bits). */
	public byte mode;
	
	/** Stratum. */
	public byte stratum;
	
	/** Poll Interval. */
	public byte poll;
	
	/** Precision. */
	public byte precision;
	
	/** Root Delay. */
	public double delay;
	
	/** Root Dispersion. */
	public double dispersion;
	
	/** Reference Identifier. */
	public byte[] refId;
	
	/** Reference Timestamp (NTP format). */
	public NtpTimestamp refTime;
	
	/** Originate Timestamp (NTP format). */
	public NtpTimestamp orgTime;
	
	/** Receive Timestamp (NTP format). */
	public NtpTimestamp recTime;
	
	/** Transmit Timestamp (NTP format). */
	public NtpTimestamp xmtTime;

	/** Destination Timestamp (NTP format). */
	public NtpTimestamp dstTime;

	public Message() {
		
	}
	
	public Message(byte[] data) {
	    leap = (byte)(data[0] >> 6);
	    version = (byte)((data[0] & 0x38) >> 3);
	    mode = (byte)(data[0] & 0x07);
	    stratum = data[1];
	    poll = data[2];
	    precision = data[3];
	    refTime = new NtpTimestamp(Arrays.copyOfRange(data, 16, 24));
	    orgTime = new NtpTimestamp(Arrays.copyOfRange(data, 24, 32));
	    recTime = new NtpTimestamp(Arrays.copyOfRange(data, 32, 40));    
	    xmtTime = new NtpTimestamp(Arrays.copyOfRange(data, 40, 48));
	}

	public byte[] toByteArray() {
		byte[] message = new byte[48];
		
		message[0] = (byte) (leap << 6 | version << 3 | mode);
		message[1] = stratum;
		message[2] = poll;
		message[3] = precision;
		
		byte[] bOrigin = null;
		byte[] bRecv = null;
		byte[] bTrans = null;
		
		if(orgTime != null) 	bOrigin = orgTime.toByteArray();
		if(recTime != null) 	bRecv = recTime.toByteArray();
		if(xmtTime != null) 	bTrans = xmtTime.toByteArray();
		
		for (int i = 0; i < 8; i++) {
			message[24 + i] = message[32 + i] = message[32 + i] = 0;
			
			if(bOrigin != null)	message[24 + i] = bOrigin[i];
			if(bRecv != null)	message[32 + i] = bRecv[i];
			if(bTrans != null)	message[40 + i] = bTrans[i];
		}
		
		return message;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("SNTP Message(").append(version).append(")");
		//TODO
		return sb.toString();
	}
	
}

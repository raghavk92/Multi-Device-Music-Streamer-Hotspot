/*******************************************************************************
 *        File: Client.java
 *      Author: Morteza Ansarinia <ansarinia@me.com>
 *  Created on: November 9, 2013
 *     Project: No Time Protocol <http://time.onto.ir>
 *   Copyright: See the file "LICENSE" for the full license governing this code.
 *******************************************************************************/
package com.example.raj.streamerhotspoter;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Date;

/**
 * SNTP client algorithm, specified in RFC 4330.
 */
public class Client {
    private Player mActivity;
	private int _port = -1;
	private String _host;
	private int _timeout;
    Globals globals= Globals.getInstance();


	public Client(int _port, String _host, int _timeout, Player mactivity) {
		this._port = _port;
		this._host = new String(_host);
		this._timeout = _timeout;
        this.mActivity=mactivity;
	}

	public long run() throws Exception {

		if (_port<=0 || _host==null || _timeout<0) {
			throw new IllegalArgumentException("Invalid parameters!");
		}

		final DatagramSocket socket = new DatagramSocket();
		socket.setSoTimeout(_timeout);
		final InetAddress hostAddr = InetAddress.getByName(_host);

		// Create request
		Message req = new Message();
		req.version = 4;
		req.mode = 3; //client
		req.stratum = 3;
		req.refId = "LOCL".getBytes();
		req.xmtTime = NtpTimestamp.now(); // returns as originate timestamp
		final byte[] buffer = req.toByteArray();
		req = null;

		DatagramPacket packet = new DatagramPacket(buffer, buffer.length, hostAddr, _port);
		socket.send(packet);

		packet = new DatagramPacket(buffer, buffer.length);
		socket.receive(packet);

		// Set the time response arrived
		final NtpTimestamp destTime = NtpTimestamp.now();

		Message resp = new Message(packet.getData());
		socket.close();

        // Timestamp Name          ID   When Generated
        // ------------------------------------------------------------
        // Originate Timestamp     T1   time request sent by client
        // Receive Timestamp       T2   time request received by server
        // Transmit Timestamp      T3   time reply sent by server
        // Destination Timestamp   T4   time reply received by client
        //
        // The roundtrip delay d, and system clock offset t are defined as:
        //
        // d = (T4 - T1) - (T3 - T2)     t = ((T2 - T1) + (T3 - T4)) / 2
		
		double t = ((resp.recTime.value - resp.orgTime.value) + (resp.xmtTime.value - destTime.value)) / 2;
		
		// System clock offset in millis
		long offset = (long)(t*1000);
        globals.showMessage(mActivity,mActivity,"Offset:      " + offset + "ms");
		//System.out.println("Offset:      " + offset + "ms");
        double d= destTime.value - resp.orgTime.value  - (resp.xmtTime.value - resp.recTime.value);
        final double delay= d/2;
		final long client = System.currentTimeMillis();
		final long server = (long) (client + offset);

        globals.showMessage(mActivity, mActivity, "Local Time:  " + new Date(client).toString());
        globals.showMessage(mActivity, mActivity, "Server Time: " + new Date(server).toString());
        return offset+ (long)delay;
	}
}
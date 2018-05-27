/*******************************************************************************
 *        File: Server.java
 *      Author: Morteza Ansarinia <ansarinia@me.com>
 *  Created on: November 9, 2013
 *     Project: No Time Protocol <http://time.onto.ir>
 *   Copyright: See the file "LICENSE" for the full license governing this code.
 *******************************************************************************/
package com.example.raj.streamerhotspoter;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;


public class Server {
    private Player mActivity;
	private Message response;
	private Message request;
	private byte[] buffer;
Globals globals= Globals.getInstance();
	private int _port = -1; // Default is 123 UDP

	private DatagramPacket packet;  
	private DatagramSocket socket;

	public void run(){  
		try{
			response = new Message();
			response.stratum = 1;
			response.precision = -6;
			response.delay = 0.0;
			response.refId = "LOCL".getBytes();
			buffer = response.toByteArray();
            globals.showMessage(mActivity,mActivity,"Server started!");

			packet = new DatagramPacket(buffer, buffer.length);        
			socket = new DatagramSocket(null);
            SocketAddress socketAddr;
            socketAddr = new InetSocketAddress(_port);
            socket.setReuseAddress(true);
            socket.setBroadcast(true); socket.bind(socketAddr);

			while(true){

                globals.showMessage(mActivity, mActivity, "thread running");
				socket.receive(packet);
                globals.showMessage(mActivity,mActivity,"Request from "+packet.getAddress()+":"+packet.getPort());


				buffer = packet.getData();
				request = new Message(buffer);

				response.orgTime = request.xmtTime;
				response.recTime = NtpTimestamp.now();
				response.xmtTime = NtpTimestamp.now();
            //    while(!globals.getPlayer().isPlaying());
              //  response.currentPosition= globals.getPlayer().getCurrentPosition();

				buffer = response.toByteArray();
				DatagramPacket resp = new DatagramPacket(buffer,buffer.length,packet.getAddress(),packet.getPort());
				socket.send(resp);
			}  
		}    

		catch(Exception e){  
			e.printStackTrace();
			System.exit(1);  
		}   
	}

	public Server(int port, Player mActivity) {
		this._port = port;
        this.mActivity=mActivity;
	}
   
}

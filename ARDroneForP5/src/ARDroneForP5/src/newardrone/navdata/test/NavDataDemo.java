/*
 * Copyright 2010 Cliff L. Biffle.  All Rights Reserved.
 * Use of this source code is governed by a BSD-style license that can be found
 * in the LICENSE file.
 */
package newardrone.navdata.test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;

import newardrone.navdata.DroneState;
import newardrone.navdata.NavDataParser;
import newardrone.navdata.exception.NavDataException;
import newardrone.navdata.listener.AttitudeListener;
import newardrone.navdata.listener.StateListener;

public class NavDataDemo {
	private DatagramSocket navdataSocket=null;
	
	public NavDataDemo(DatagramSocket navdataSocket){
		this.navdataSocket=navdataSocket;
		
		System.out.println("Navdata demo running.");
		
		
	}
	
	public void run() throws IOException, NavDataException{
		tickleNavdataPort();
		enableDemoData();
		tickleNavdataPort();
		sendControlAck();
		NavDataParser parser=new NavDataParser();
		
		parser.setAttitudeListener(new AttitudeListener() {
			@Override
			public void attitudeUpdated(float pitch, float roll, float yaw, int altitude) {
				System.out.println("Attitude: pitch="+pitch+" roll="+roll+" yaw="+yaw+" altitude="+altitude);
			}
		});
		
		parser.setStateListener(new StateListener() {
			@Override
			public void stateChanged(DroneState state) {
				System.out.println("State: "+state);
			}
		});
		
		while(true){
			tickleNavdataPort();
			DatagramPacket packet=new DatagramPacket(new byte[1024], 1024);
			navdataSocket.receive(packet);
			System.out.println("Received packet");
			
			ByteBuffer buffer=ByteBuffer.wrap(packet.getData(), 0, packet.getLength());
			parser.parseNavData(buffer);
		}
	}
	
	private void tickleNavdataPort() throws IOException{
		byte[] buf=new byte[1];
		buf[0]='\n';
		DatagramPacket packet=new DatagramPacket(buf, 1);
		navdataSocket.send(packet);
	}
	
	private void enableDemoData() throws IOException{
	}
	private void sendControlAck() throws IOException{
	}
	
	
	
}

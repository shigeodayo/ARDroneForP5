/*
 * Copyright (c) <2011>, <Shigeo Yoshida>
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
The names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, 
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package newardrone.navdata;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
//import java.nio.ByteOrder;

import newardrone.ARDroneCtrl;
import newardrone.navdata.exception.NavDataException;
import newardrone.navdata.listener.AttitudeListener;
import newardrone.navdata.listener.BatteryListener;
import newardrone.navdata.listener.StateListener;
import newardrone.navdata.listener.VelocityListener;


public class ARDroneNav extends Thread{
	private ARDroneCtrl control=null;
	private DatagramSocket navdataSocket=null;
	private InetAddress inetaddr=null;
	
	/*private float pitch=0;
	private float roll=0;
	private float yaw=0;
	private int altitude=0;
	
	private int batteryPercentage=0;
	
	private float vx=0;
	private float vy=0;
	private float vz=0;*/
	
	//listener
	private AttitudeListener attitudeListener=null;
	private StateListener stateListener=null;
	private VelocityListener velocityListener=null;
	private BatteryListener batteryListener=null;

	
	public ARDroneNav(ARDroneCtrl control, InetAddress inetaddr){
		this.control=control;
		this.inetaddr=inetaddr;
		initialize();
	}
	
	private void initialize(){
		try {
			navdataSocket=new DatagramSocket(5554);
			navdataSocket.setSoTimeout(3000);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	//set listeners
	public void setBatteryListener(BatteryListener batteryListener){
		this.batteryListener=batteryListener;
	}
	public void setAttitudeListener(AttitudeListener attitudeListener){
		this.attitudeListener=attitudeListener;
	}
	public void setStateListener(StateListener stateListener){
		this.stateListener=stateListener;
	}
	public void setVelocityListener(VelocityListener velocityListener){
		this.velocityListener=velocityListener;
	}
	
	
	public void run(){
		tickleNavdataPort();
		enableDemoData();
		tickleNavdataPort();
		sendControlAck();
		NavDataParser parser=new NavDataParser();
		
		//System.out.println("set listeenr");
		parser.setAttitudeListener(new AttitudeListener() {
			@Override
			public void attitudeUpdated(float pitch, float roll, float yaw, int altitude) {
				//System.out.println("Bbbbbb");
				//System.out.println("Attitude: pitch="+pitch+" roll="+roll+" yaw="+yaw+" altitude="+altitude);
				//setAttitudeInfo(pitch, roll, yaw, altitude);
				if(attitudeListener!=null){
					attitudeListener.attitudeUpdated(pitch, roll, yaw, altitude);
					//System.out.println("update in nav");
				}/*else{
					System.out.println("!!!aaa!");
				}*/
			}
		});
		
		parser.setStateListener(new StateListener() {
			@Override
			public void stateChanged(DroneState state) {
				//System.out.println("State: "+state);
				//setStateInfo();
				if(stateListener!=null){
					stateListener.stateChanged(state);
				}
			}
		});
		
		parser.setBatteryListener(new BatteryListener() {
			@Override
			public void batteryLevelChanged(int percentage) {
				//System.out.println("Battery: "+percentage+"%");
				//setBatteryInfo(percentage);
				if(batteryListener!=null){
					batteryListener.batteryLevelChanged(percentage);
				}
			}
		});
		
		parser.setVelocityListener(new VelocityListener() {
			@Override
			public void velocityChanged(float vx, float vy, float vz) {
				//System.out.println("in Velocity: vx="+vx+" vy="+vy+" vz="+vz);
				//setVelocityInfo(vx, vy, vz);
				if(velocityListener!=null){
					velocityListener.velocityChanged(vx, vy, vz);
				}
			}
		});
		
		while(true){
			try {
				tickleNavdataPort();
				//DatagramPacket packet=new DatagramPacket(new byte[1024], 1024);
				DatagramPacket packet=new DatagramPacket(new byte[1024], 1024, inetaddr, 5554);

				navdataSocket.receive(packet);
				//System.out.println("Received packet");
				
				ByteBuffer buffer=ByteBuffer.wrap(packet.getData(), 0, packet.getLength());
				
				/*for(int i=0; i<buffer.limit(); i++){
					buffer.order(ByteOrder.LITTLE_ENDIAN);
					byte b=buffer.get();
				//	System.out.printf("%d\n", buffer.get());
					//System.out.println("b: "+b);
					System.out.print(fullZero(Integer.toBinaryString(b), 8));
				}
				System.out.println("E");*/

				//System.out.println("parse");
				parser.parseNavData(buffer);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NavDataException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void tickleNavdataPort(){
		byte[] buf=new byte[1];
		buf[0]='\n';
		DatagramPacket packet=new DatagramPacket(buf, 1, inetaddr, 5554);
		try {
			navdataSocket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void enableDemoData(){
		control.enableDemoData();
	}
	private void sendControlAck(){
		control.sendControlAck();
	}
	
/*	private void setAttitudeInfo(float pitch, float roll, float yaw, int altitude){
		this.pitch=pitch;
		this.roll=roll;
		this.yaw=yaw;
		this.altitude=altitude;
	}
	
	private void setBatteryInfo(int percentage){
		this.batteryPercentage=percentage;
	}
	
	private void setVelocityInfo(float vx, float vy, float vz){
		this.vx=vx;
		this.vy=vy;
		this.vz=vz;
	}
	
	public float getPitch(){
		return pitch;
	}
	
	public float getRoll(){
		return roll;
	}
	
	public float getYaw(){
		return yaw;
	}
	
	public int getAltitude(){
		return altitude;
	}
	
	public int getBatteryPercentage(){
		return batteryPercentage;
	}
	
	public float getVelocityX(){
		return vx;
	}
	
	public float getVelocityY(){
		return vy;
	}
	
	public float getVelocityZ(){
		return vz;
	}*/
	
	public void close(){
		navdataSocket.close();
	}
	
	/*
	private static String fullZero(String tgt, int figure){
		StringBuffer buf = new StringBuffer();
		for(int i=0 ; i<figure - tgt.length() ; i++){
			buf.append("0");
		}
		buf.append(tgt);
		return buf.toString();
	}*/

}

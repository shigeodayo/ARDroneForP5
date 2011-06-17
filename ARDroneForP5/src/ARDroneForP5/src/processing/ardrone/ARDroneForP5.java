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
package processing.ardrone;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.StringTokenizer;

import processing.core.PConstants;
import processing.core.PImage;
import processing.navdata.ARDroneNavP5;
import processing.video.ARDroneVideoP5;

import newardrone.video.ImageListener;

public class ARDroneForP5 {

	/** default IP Address */
	private static final String IP_ADDRESS="192.168.1.1";
	private static final String CR="\r";

	/** for video stream */
	private ARDroneVideoP5 ardroneVideo=null;
	/** image from video streaming */
	private BufferedImage videoImage=null;
	/** for nav data stream */
	private ARDroneNavP5 ardroneNav=null;
	
	/** socket for AT command  */
	private DatagramSocket socket=null;
	/** sequence number */
	private static int seq=1;
	
	/** thread for send command */
	private PacketSendThread pst=null;

	private InetAddress inetaddr=null;
	
	/** ARDrone speed */
	private float speed=(float) 0.1;

	private float pitch=0;
	private float roll=0;
	private float yaw=0;
	private float gaz=0;

	private FloatBuffer fb=null;
	private IntBuffer ib=null;

	private boolean landing=true;
	
	private boolean resizeImage=false;

	/**
	 * constructor
	 */
	public ARDroneForP5(){
		initialize();
	}

	/**
	 * initializer
	 */
	private void initialize(){

	}

	/**
	 * connection
	 * @return
	 */
	public boolean connect(){
		return connect(IP_ADDRESS);
	}
	/**
	 * connection
	 * @param ipaddr
	 * @return
	 */
	public boolean connect(String ipaddr){
		StringTokenizer st=new StringTokenizer(ipaddr, ".");

		byte[] ip_bytes=new byte[4];
		if(st.countTokens()==4){
			for(int i=0; i<4; i++){
				ip_bytes[i]=(byte) Integer.parseInt(st.nextToken());
			}
		}else{
			System.out.println("Incorrect IP address format: "+ipaddr);
			return false;
		}

		System.out.println("IP: "+ipaddr);
		System.out.println("Speed: "+ speed);

		ByteBuffer bb=ByteBuffer.allocate(4);
		fb=bb.asFloatBuffer();
		ib=bb.asIntBuffer();

		try {
			inetaddr=InetAddress.getByAddress(ip_bytes);
			socket=new DatagramSocket(5556);
			socket.setSoTimeout(3000);

			pst=new PacketSendThread();

		} catch (UnknownHostException e) {
			e.printStackTrace();
			return false;
		} catch (SocketException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
	
	/**
	 * if you want to get video data,
	 * you should call this method
	 * @return
	 */
	public boolean connectVideo(){
		if(inetaddr==null){
			System.err.println("please call \"connect\" method, before calling this method");
			return false;
		}		
		if(!landing){
			System.err.println("calling this method not in flight");
			return false;			
		}

		ardroneVideo=new ARDroneVideoP5(this, inetaddr);
		ardroneVideo.setImageListener(new ImageListener(){
			@Override
			public void imageUpdated(BufferedImage image) {
				if(resizeImage)
					if(image.getWidth()==176)
						image=resize(image, 320, 240);
				setVideoImage(image);
			}
		});
		return true;
	}
		
	/**
	 * if you want to get nav data,
	 * you should call this method
	 * @return
	 */
	public boolean connectNav(){
		if(inetaddr==null){
			System.err.println("please call \"connect\" method, before calling this method");
			return false;
		}
		if(!landing){
			System.err.println("calling this method not in flight");
			return false;			
		}
		
		ardroneNav=new ARDroneNavP5(this, inetaddr);

		return true;
	}
	
	/**
	 * only called from ARDroneNav
	 * send AT*CONFIG=sequence number,"general:navdata_demo","TRUE"
	 */
	public void enableDemoData(){
		pst.setATCommand("AT*CONFIG="+(seq++)+",\"general:navdata_demo\",\"TRUE\""+CR+"AT*FTRIM="+(seq++), false);
	}
	
	/**
	 * only called from ARDroneVideo
	 * send AT*CONFIG=sequence number,"general:video_enable","TRUE"
	 */
	public void enableVideoData(){
		pst.setATCommand("AT*CONFIG="+(seq++)+",\"general:video_enable\",\"TRUE\""+CR+"AT*FTRIM="+(seq++), false);
	}
	
	/**
	 * send Ack
	 * AT*CTRL=sequence number,0
	 */
	public void sendControlAck(){
		pst.setATCommand("AT*CTRL="+(seq++)+",0", false);
	}
	
	/**
	 * get pitch
	 * @return
	 */
	public float getPitch(){
		if(ardroneNav==null)
			return 0;
		return ardroneNav.getPitch();
	}
	/**
	 * get roll
	 * @return
	 */
	public float getRoll(){
		if(ardroneNav==null)
			return 0;
		return ardroneNav.getRoll();
	}
	/**
	 * get yaw
	 * @return
	 */
	public float getYaw(){
		if(ardroneNav==null)
			return 0;
		return ardroneNav.getYaw();
	}
	/**
	 * get altitude
	 * @return
	 */
	public float getAltitude(){
		if(ardroneNav==null)
			return 0;
		return ardroneNav.getAltitude();
	}
	/**
	 * get velocity vx and vy
	 * vz is always zero
	 * @return
	 */
	public float[] getVelocity(){
		if(ardroneNav==null)
			return new float[]{0, 0, 0};
		return new float[]{ardroneNav.getVelocityX(),
						   ardroneNav.getVelocityY(),
						   ardroneNav.getVelocityZ()};
	}
	/**
	 * get battery percentage 0:100%
	 * @return
	 */
	public int getBatteryPercentage(){
		if(ardroneNav==null)
			return 0;
		return ardroneNav.getBatteryPercentage();
	}
	
	/**
	 * print current ardrone information
	 */
	public void printARDroneInfo(){
		if(ardroneNav==null){
			return;
		}
		System.out.println("--------------------------------------------------------------------");
		System.out.println("Attitude: pitch="+getPitch()+" roll="+getRoll()+" yaw="+getYaw()+" altitude="+getAltitude());
		System.out.println("Battery: "+getBatteryPercentage()+"%");
		System.out.println("Velocity: vx="+ardroneNav.getVelocityX()+" vy="+ardroneNav.getVelocityY()+" vz="+ardroneNav.getVelocityZ());
		System.out.println("--------------------------------------------------------------------");
	}

	private void setVideoImage(BufferedImage videoImage){
		this.videoImage=videoImage;
	}
	/**
	 * get video image as bufferedimage 
	 * @return
	 */
	public BufferedImage getVideoImageAsBufferedImage(){
		return videoImage;
	}
	
	/**
	 * get ardrone view as pimage
	 * @return
	 */
	public PImage getVideoImageAsPImage() {
		try {
			BufferedImage bimg=getVideoImageAsBufferedImage();
			PImage img=new PImage(bimg.getWidth(), bimg.getHeight(), PConstants.ARGB);
			bimg.getRGB(0, 0, img.width, img.height, img.pixels, 0, img.width);
			img.updatePixels();
			return img;
		}
		catch(Exception e) {
			//System.err.println("Can't create image from buffer");
			//e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Broadcast video from the front camera
	 */
	public void setFrontCameraStreaming(){//hori
		pst.setATCommand("AT*ZAP="+(seq++)+",0", false);//correct
	}
	/**
	 * Broadcast video from the belly camera, showing the ground
	 */
	public void setBellyCameraStreaming(){//vert
		resizeImage=false;
		pst.setATCommand("AT*ZAP="+(seq++)+",1", false);		
	}
	/**
	 * Broadcast video from the belly camera, showing the ground
	 * resize QCIF to VGA
	 */
	public void setBellyCameraStreamingResize(){//vert
		resizeImage=true;
		pst.setATCommand("AT*ZAP="+(seq++)+",1", false);		
	}
	/**
	 * Broadcast video from the front camera, 
	 * with the belly camera encrusted in the top-left corner
	 */
	public void setFrontCameraWithSmallBellyStreaming(){//large hori, small vert
		pst.setATCommand("AT*ZAP="+(seq++)+",2", false);//correct
	}
	/**
	 * Broadcast video from the belly camera,
	 * with the front camera picture encrusted in the top-left corner
	 */
	public void setBellyCameraWithSmallFrontStreaming(){//large vert, small hori
		resizeImage=false;
		pst.setATCommand("AT*ZAP="+(seq++)+",3", false);
	}
	/**
	 * Broadcast video from the belly camera,
	 * with the front camera picture encrusted in the top-left corner
	 * resize QCIF to VGA
	 */
	public void setBellyCameraWithSmallFrontStreamingResize(){//large vert, small hori
		resizeImage=true;
		pst.setATCommand("AT*ZAP="+(seq++)+",3", false);
	}
	/**
	 * Switch to the next possible camera combination
	 */
	public void setNextCamera(){//next possible camera
		resizeImage=false;
		pst.setATCommand("AT*ZAP="+(seq++)+",4", false);
	}
	/**
	 * Switch to the next possible camera combination
	 * resize QCIF to VGA
	 */
	public void setNextCameraResize(){//next possible camera
		resizeImage=true;
		pst.setATCommand("AT*ZAP="+(seq++)+",4", false);
	}

	/**
	 * resize bufferedimage
	 * @param image
	 * @param width
	 * @param height
	 * @return
	 */
	private BufferedImage resize(BufferedImage image, int width, int height) {
		BufferedImage resizedImage = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(image, 0, 0, width, height, null);
		g.dispose();
		return resizedImage;
	}
	
	/**
	 * disconnection
	 */
	public void disconnect(){
		stop();
		landing();
		pst=null;
		socket.close();
		
		if(ardroneNav!=null){
			ardroneNav.close();
			ardroneNav=null;
		}
		if(ardroneVideo!=null){
			ardroneVideo.close();
			ardroneVideo=null;
		}
	}
	
	/**
	 * start threads
	 * if you want to control ARDrone, you have to call this method.
	 */
	public void start(){
		//start control thread
		pst.start();
		//start navdata thread
		if(ardroneNav!=null)
			ardroneNav.start();
		//start video thread
		if(ardroneVideo!=null)
			ardroneVideo.start();
		
		System.out.println("thread start!!!!");
	}

	/**
	 * landing
	 */
	public void landing(){
		pst.setATCommand("AT*REF=" + (seq++) + ",290717696", false);
		landing=true;
	}

	/**
	 * take off
	 */
	public void takeOff(){
		pst.setATCommand("AT*REF=" + (seq++) + ",290718208", false);
		landing=false;
	}

	/**
	 * backward
	 * pitch-
	 */
	public void backward() {
		if(landing)
			return;
		//pst.setATCommand("AT*PCMD=" + (seq++) + ",1," + intOfFloat(-speed) + ",0,0,0", true);
		pst.setATCommand("AT*PCMD="+(seq++)+",1,0,"+intOfFloat(speed)+",0,0"+"\r"+"AT*REF=" + (seq++) + ",290718208", true);
	}
	/**
	 * backward with specified speed
	 * @param speed
	 */
	public void backward(int speed){
		setSpeed(speed);
		backward();
	}


	/**
	 * forward
	 * pitch+
	 */
	public void forward() {
		if(landing)
			return;
		//pst.setATCommand("AT*PCMD="+(seq++)+",1,0,"+(-1082130432)+",0,0"+"\r"+"AT*REF=" + (seq++) + ",290718208", true);	
		pst.setATCommand("AT*PCMD="+(seq++)+",1,0,"+intOfFloat(-speed)+",0,0"+"\r"+"AT*REF=" + (seq++) + ",290718208", true);
	}
	/**
	 * forward with specified speed
	 * @param speed
	 */
	public void forward(int speed){
		setSpeed(speed);
		forward();
	}

	
	/**
	 * ccw
	 * yaw-
	 */
	public void spinLeft() {
		if(landing)
			return;
		pst.setATCommand("AT*PCMD=" + (seq++) + ",1,0,0,0," + intOfFloat(-speed)+"\r"+"AT*REF=" + (seq++) + ",290718208", true);
	}
	/**
	 * ccw with specified speed
	 * @param speed
	 */
	public void spinLeft(int speed){
		setSpeed(speed);
		spinLeft();
	}

	/**
	 * cw
	 * yaw+
	 */
	public void spinRight() {
		if(landing)
			return;
		pst.setATCommand("AT*PCMD=" + (seq++) + ",1,0,0,0," + intOfFloat(speed)+"\r"+"AT*REF=" + (seq++) + ",290718208", true);
	}
	/**
	 * cw with specified speed
	 * @param speed
	 */
	public void spinRight(int speed){
		setSpeed(speed);
		spinRight();
	}


	/**
	 * hovering in the air
	 */
	public void stop() {
		pst.setATCommand("AT*PCMD="+(seq++)+",1,0,0,0,0", true);
	}

	/**
	 * get current speed
	 */
	public int getSpeed() {
		return (int)(speed*100);
	}

	/**
	 * set speed
	 * speed:0-100%
	 */
	public void setSpeed(int speed) {
		if(speed>100)
			speed=100;
		else if(speed<0)
			speed=0;

		this.speed=(float) (speed/100.0);
	}

	/**
	 * down
	 * gaz-
	 */
	public void down(){
		if(landing)
			return;
		pitch = 0;
		roll = 0;
		gaz = -speed;
		yaw = 0;
		pst.setATCommand("AT*PCMD=" + (seq++) + ",1," + intOfFloat(pitch) + "," + intOfFloat(roll)
				+ "," + intOfFloat(gaz) + "," + intOfFloat(yaw)+"\r"+"AT*REF=" + (seq++) + ",290718208", true);
	}
	/**
	 * move down with specified speed
	 * @param speed
	 */
	public void down(int speed){
		setSpeed(speed);
		down();
	}


	/**
	 * up
	 * gaz+
	 */
	public void up(){
		if(landing)
			return;
		pitch = 0; 
		roll = 0; 
		gaz = speed;
		yaw = 0;
		pst.setATCommand("AT*PCMD=" + (seq++) + ",1," + intOfFloat(pitch) + "," + intOfFloat(roll)
				+ "," + intOfFloat(gaz) + "," + intOfFloat(yaw)+"\r"+"AT*REF=" + (seq++) + ",290718208", true);
	}
	/**
	 * move up with specified speed
	 * @param speed
	 */
	public void up(int speed){
		setSpeed(speed);
		up();
	}


	/**
	 * move to left
	 * roll-
	 */
	public void goLeft(){
		if(landing)
			return;
		//pst.setATCommand("AT*PCMD=" + (seq++) + ",1,0," + intOfFloat(-speed) + ",0,0", true);
		pst.setATCommand("AT*PCMD="+(seq++)+",1,"+intOfFloat(-speed)+",0,0,0"+"\r"+"AT*REF=" + (seq++) + ",290718208", true);
	}
	/**
	 * move left with specified speed
	 * @param speed
	 */
	public void goLeft(int speed){
		setSpeed(speed);
		goLeft();
	}


	/**
	 * move to right
	 * roll+
	 */
	public void goRight(){
		if(landing)
			return;
		//pst.setATCommand("AT*PCMD=" + (seq++) + ",1,0," + intOfFloat(speed) + ",0,0", true);
		pst.setATCommand("AT*PCMD="+(seq++)+",1,"+intOfFloat(speed)+",0,0,0"+"\r"+"AT*REF=" + (seq++) + ",290718208", true);
	}
	/**
	 * move right with specified speed
	 * @param speed
	 */
	public void goRight(int speed){
		setSpeed(speed);
		goRight();
	}



	private int intOfFloat(float f) {
		fb.put(0, f);
		return ib.get(0);
	}	

	/**
	 * not implemented 
	 * @param radius
	 */
	public void turnLeft(int radius) {
		if(landing)
			return;
	}

	/**
	 * not implemented
	 * @param radius
	 */
	public void turnRight(int radius) {
		if(landing)
			return;
	}


	/**
	 * for socket
	 * @author shigeo
	 *
	 */
	private class PacketSendThread extends Thread{

		//private int thCount=0;

		private boolean continuance=false;
		private String atCommand=null;

		private boolean init=false;
		private ArrayList<String> initCommands=null;

		public PacketSendThread(){
			initialize();
		}

		private void initialize(){
			init=true;
			initCommands=new ArrayList<String>();
			initCommands.add("AT*CONFIG="+(seq++)+",\"general:navdata_demo\",\"TRUE\""+CR+"AT*FTRIM="+(seq++));//1
			//initCommands.add("AT*CONFIG="+(seq++)+",\"general:navdata_demo\",\"TRUE\"");//1
			initCommands.add("AT*PMODE="+(seq++)+",2"+CR+"AT*MISC="+(seq++)+",2,20,2000,3000"+CR+"AT*FTRIM="+(seq++)+CR+"AT*REF="+(seq++)+",290717696");//2-5
			initCommands.add("AT*PCMD="+(seq++)+",1,0,0,0,0"+CR+"AT*REF="+(seq++)+",290717696"+CR+"AT*COMWDG="+(seq++));//6-8
			initCommands.add("AT*PCMD="+(seq++)+",1,0,0,0,0"+CR+"AT*REF="+(seq++)+",290717696"+CR+"AT*COMWDG="+(seq++));//6-8
			//initCommands.add("AT*PCMD="+(seq++)+",1,0,0,0,0"+CR+"AT*REF="+(seq++)+",290717696");
			//initCommands.add("AT*PCMD="+(seq++)+",1,0,0,0,0"+CR+"AT*REF="+(seq++)+",290717696");//11-12
			//initCommands.add("AT*CTRL="+(seq++)+",5,0"+CR+"AT*CTRL="+(seq++)+",5,0"+CR+"AT*CTRL="+(seq++)+",5,0"+CR+"AT*CTRL="+(seq++)+",5,0"+CR+"AT*CTRL="+(seq++)+",5,0"+CR+"AT*CTRL="+(seq++)+",5,0"+CR+"AT*CTRL="+(seq++)+",5,0"+CR+"AT*CTRL="+(seq++)+",5,0"+CR+"AT*CTRL="+(seq++)+",5,0"+CR+"AT*REF="+(seq++)+",290717696"+CR+"AT*COMWDG="+(seq++));//13-23
			//initCommands.add("AT*CTRL="+(seq++)+",5,0"+CR+"AT*CTRL="+(seq++)+",5,0"+CR+"AT*CTRL="+(seq++)+",5,0"+CR+"AT*CTRL="+(seq++)+",5,0"+CR+"AT*CTRL="+(seq++)+",5,0"+CR+"AT*CTRL="+(seq++)+",5,0"+CR+"AT*CTRL="+(seq++)+",5,0"+CR+"AT*CTRL="+(seq++)+",5,0"+CR+"AT*CTRL="+(seq++)+",5,0"+CR+"AT*PCMD="+(seq++)+",1,0,0,0,0"+CR+"AT*REF="+(seq++)+",290717696");//24-34
			//initCommands.add("AT*CTRL="+(seq++)+",5,0"+CR+"AT*CTRL="+(seq++)+",5,0"+CR+"AT*REF="+(seq++)+",290717696");//35-37
			//initCommands.add("AT*CTRL="+(seq++)+",5,0"+CR+"AT*CTRL="+(seq++)+",5,0"+CR+"AT*CTRL="+(seq++)+",5,0"+CR+"AT*CONFIG="+(seq++)+",\"general:navdata_demo\",\"FALSE\"");//38-41
			//initCommands.add("AT*CTRL="+(seq++)+",5,0"+CR+"AT*CTRL="+(seq++)+",5,0"+CR+"AT*CTRL="+(seq++)+",5,0"+CR+"AT*CTRL="+(seq++)+",5,0"+CR+"AT*CTRL="+(seq++)+",5,0"+CR+"AT*CTRL="+(seq++)+",5,0"+CR+"AT*PCMD="+(seq++)+",1,0,0,0,0"+CR+"AT*REF="+(seq++)+",290717696");//42-49
			//initCommands.add("AT*CTRL="+(seq++)+",5,0"+CR+"AT*CTRL="+(seq++)+",5,0"+CR+"AT*PCMD="+(seq++)+",1,0,0,0,0"+CR+"AT*REF="+(seq++)+",290717696");//50-53

		}

		/**
		 * AR.Droneにコマンドを送る
		 * Send command to ARDrone
		 * @param atCommand
		 * @return
		 */
		private synchronized boolean sendATCommand(String atCommand){
			//System.out.println("AT command:\n"+atCommand);
			//System.out.println("Speed: "+this.getSpeed());
			byte[] buffer=(atCommand+CR).getBytes();
			DatagramPacket packet=new DatagramPacket(buffer, buffer.length, inetaddr, 5556);
			try {
				socket.send(packet);
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}

		/**
		 * AR.Droneに送るコマンドを保持する
		 * Hold command temporary 
		 * @param atCommand
		 * @param continuance
		 */
		public void setATCommand(String atCommand, boolean continuance){
			this.atCommand=atCommand;
			this.continuance=continuance;
		}

		/**
		 * AR.Droneにコマンドを20ms毎に送る
		 * 
		 */
		public void run(){
			while(true){
				try {
					if(init){//AR.Droneの初期設定をする。初期設定中は初期設定以外のコマンドは送らない。
						if(initCommands.size()==0){
							init=false;
							initCommands=null;
							System.out.println("initialize completed!!!!");
							continue;
						}
						sendATCommand(initCommands.remove(0));
						sleep(20);
						continue;
					}

					//continuanceがtrueの場合は次の命令が送られてくるまで、
					//その命令を送り続ける。
					if(this.atCommand!=null){
						sendATCommand(this.atCommand);
						sleep(20);
						if(!this.continuance){
							this.atCommand=null;
							continue;
						}
						//thCount=0;
						continue;
					}
					/*thCount++;
					if(thCount==12){
						for(int i=0; i<5; i++){
							sendATCommand("AT*COMWDG="+(seq++));
							sleep(20);
						}
					}*/

					/*if(landing){//着陸状態の場合はAT*REFは着陸
						sendATCommand("AT*PCMD="+(seq++)+",1,0,0,0,0"+CR+"AT*REF="+(seq++)+",290717696");
						sleep(20);
						sendATCommand("AT*PCMD="+(seq++)+",1,0,0,0,0"+CR+"AT*REF="+(seq++)+",290717696"+CR+"AT*COMWDG="+(seq++));
						sleep(20);						
					}else{//離陸状態の場合はAT*REFは離陸
						sendATCommand("AT*PCMD="+(seq++)+",1,0,0,0,0"+CR+"AT*REF="+(seq++)+",290718208");
						sleep(20);
						//sendATCommand("AT*PCMD="+(seq++)+",1,0,0,0,0"+CR+"AT*REF="+(seq++)+CR+"AT*COMWDG="+(seq++));
						//sleep(20);
					}*/
					if(landing){//着陸状態の場合はAT*REFは着陸
						sendATCommand("AT*PCMD="+(seq++)+",1,0,0,0,0"+CR+"AT*REF="+(seq++)+",290717696");
						sleep(20);
						/*if((thCount++)==5){//240ms
							//sendATCommand("AT*PCMD="+(seq++)+",1,0,0,0,0"+CR+"AT*REF="+(seq++)+",290717696"+CR+"AT*COMWDG="+(seq++));
							for(int i=0; i<5; i++){
								sendATCommand("AT*COMWDG="+(seq++));
								sleep(20);
							}
							thCount=0;
						}*/
					}else{//離陸状態の場合はAT*REFは離陸
						sendATCommand("AT*PCMD="+(seq++)+",1,0,0,0,0"+CR+"AT*REF="+(seq++)+",290718208");
						sleep(20);
						/*if((thCount++)==5){
							//sendATCommand("AT*PCMD="+(seq++)+",1,0,0,0,0"+CR+"AT*REF="+(seq++)+",290718208"+CR+"AT*COMWDG="+(seq++));
							for(int i=0; i<5; i++){
								sendATCommand("AT*COMWDG="+(seq++));
								sleep(20);
							}
							thCount=0;
						}*/
					}
					//printARDroneInfo();

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}	
}
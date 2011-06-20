/*
 *
  Copyright (c) <2011>, <Shigeo Yoshida>
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
package com.shigeodayo.ardrone.processing;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import processing.core.PConstants;
import processing.core.PImage;

import com.shigeodayo.ardrone.ARDrone;
import com.shigeodayo.ardrone.navdata.AttitudeListener;
import com.shigeodayo.ardrone.navdata.BatteryListener;
import com.shigeodayo.ardrone.navdata.DroneState;
import com.shigeodayo.ardrone.navdata.StateListener;
import com.shigeodayo.ardrone.navdata.VelocityListener;
import com.shigeodayo.ardrone.video.ImageListener;

/**
 * AR.Drone library for Processing
 * 
 * @author shigeo
 *
 */
public class ARDroneForP5 extends ARDrone implements ImageListener, AttitudeListener, BatteryListener, StateListener, VelocityListener{

	private BufferedImage videoImage=null;
	
	private float pitch=0.0f;
	private float roll=0.0f;
	private float yaw=0.0f;
	private float altitude=0.0f;

	private int battery=0;

	private DroneState state=null;
	
	private float vx=0.0f;
	private float vy=0.0f;
	private float[] velocity=new float[2];
	
	
	/** constructor */
	public ARDroneForP5(){
		super();
	}
	/**
	 * constructor
	 * @param ipaddr
	 */
	public ARDroneForP5(String ipaddr){
		super(ipaddr);
	}
	
	@Override
	public boolean connectVideo(){
		addImageUpdateListener(this);
		return super.connectVideo();
	}
	
	@Override
	public boolean connectNav(){
		addAttitudeUpdateListener(this);
		addBatteryUpdateListener(this);
		addStateUpdateListener(this);
		addVelocityUpdateListener(this);
		return super.connectNav();
	}
	
	@Override
	public void imageUpdated(BufferedImage image) {
		this.videoImage=image;		
	}
	
	@Override
	public void velocityChanged(float vx, float vy, float vz) {
		this.vx=vx;
		this.vy=vy;
		velocity[0]=vx;
		velocity[1]=vy;
	}
	
	@Override
	public void stateChanged(DroneState state) {
		this.state=state;
	}
	
	@Override
	public void batteryLevelChanged(int percentage) {
		this.battery=percentage;
	}
	
	@Override
	public void attitudeUpdated(float pitch, float roll, float yaw, int altitude) {
		this.pitch=pitch;
		this.yaw=yaw;
		this.roll=roll;
		this.altitude=altitude;
	}
	
	public void printARDroneInfo(){
		System.out.println("--------------------------------------------------------------------");
		System.out.println("Attitude: pitch="+pitch+" roll="+roll+" yaw="+yaw+" altitude="+altitude);
		System.out.println("Battery: "+battery+"%");
		System.out.println("Velocity: vx="+vx+" vy="+vy);
		System.out.println("--------------------------------------------------------------------");
	}
	
	public PImage getVideoImage(boolean autoResize){
		if(videoImage==null)
			return null;
		if(autoResize){
			if(videoImage.getWidth()==176){
				return convertToPImage(resize(videoImage, 320, 240));	
			}
		}
		return convertToPImage(videoImage);

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
	
	public float getAltitude(){
		return altitude;
	}
	
	public float getVelocityX(){
		return vx;
	}
	
	public float getVelocityY(){
		return vy;
	}
	
	public float[] getVelocity(){
		return velocity;
	}
	
	public int getBatteryPercentage(){
		return battery;
	}
	
	private PImage convertToPImage(BufferedImage bimg){
		try {
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
}
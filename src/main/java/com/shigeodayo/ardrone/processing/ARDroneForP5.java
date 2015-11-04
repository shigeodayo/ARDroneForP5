/**
   ARDroneForP5
   https://github.com/shigeodayo/ARDroneForP5
   Copyright (C) 2013, Shigeo YOSHIDA.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
*/
package com.shigeodayo.ardrone.processing;

import java.awt.Graphics2D;
//import java.awt.Point;
import java.awt.image.BufferedImage;
//import java.awt.image.DataBufferInt;
//import java.awt.image.Raster;
//import java.awt.image.WritableRaster;

import processing.core.PConstants;
import processing.core.PImage;

import com.shigeodayo.ardrone.ARDrone;
import com.shigeodayo.ardrone.navdata.AttitudeListener;
import com.shigeodayo.ardrone.navdata.BatteryListener;
import com.shigeodayo.ardrone.navdata.DroneState;
import com.shigeodayo.ardrone.navdata.StateListener;
import com.shigeodayo.ardrone.navdata.VelocityListener;
import com.shigeodayo.ardrone.utils.ARDroneVersion;
import com.shigeodayo.ardrone.video.ImageListener;

/**
 * AR.Drone library for Processing
 * 
 * @author shigeo
 * 
 */
public class ARDroneForP5 extends ARDrone implements ImageListener,
		AttitudeListener, BatteryListener, StateListener, VelocityListener {

	private BufferedImage videoImage = null;

	private float pitch = 0.0f;
	private float roll = 0.0f;
	private float yaw = 0.0f;
	private float altitude = 0.0f;

	private int battery = 0;

	private DroneState state = null;

	private float vx = 0.0f;
	private float vy = 0.0f;
	private float[] velocity = new float[2];

	private PImage pimg = null;
	//private WritableRaster wr = null;

	/** constructor */
	public ARDroneForP5() {
		super();
	}

	/**
	 * constructor
	 * 
	 * @param ipaddr
	 */
	public ARDroneForP5(String ipaddr) {
		super(ipaddr);
	}

	/**
	 * constructor
	 * 
	 * @param ardroneType
	 */
	public ARDroneForP5(ARDroneVersion ardroneType) {
		super(ardroneType);
	}

	/**
	 * constructor
	 * 
	 * @param ipaddr
	 * @param ardroneType
	 */
	public ARDroneForP5(String ipaddr, ARDroneVersion ardroneType) {
		super(ipaddr, ardroneType);
	}

	@Override
	public boolean connectVideo() {
		addImageUpdateListener(this);
		// pimg = new PImage(320, 240);
		return super.connectVideo();
	}

	@Override
	public boolean connectNav() {
		addAttitudeUpdateListener(this);
		addBatteryUpdateListener(this);
		addStateUpdateListener(this);
		addVelocityUpdateListener(this);
		return super.connectNav();
	}

	@Override
	public void imageUpdated(BufferedImage image) {
		this.videoImage = image;
	}

	@Override
	public void velocityChanged(float vx, float vy, float vz) {
		this.vx = vx;
		this.vy = vy;
		velocity[0] = vx;
		velocity[1] = vy;
	}

	@Override
	public void stateChanged(DroneState state) {
		this.state = state;
	}

	@Override
	public void batteryLevelChanged(int percentage) {
		this.battery = percentage;
	}

	@Override
	public void attitudeUpdated(float pitch, float roll, float yaw, int altitude) {
		this.pitch = pitch;
		this.yaw = yaw;
		this.roll = roll;
		this.altitude = altitude;
	}

	public void printARDroneInfo() {
		System.out
				.println("--------------------------------------------------------------------");
		System.out.println("Attitude: pitch=" + pitch + " roll=" + roll
				+ " yaw=" + yaw + " altitude=" + altitude);
		System.out.println("Battery: " + battery + "%");
		System.out.println("Velocity: vx=" + vx + " vy=" + vy);
		System.out
				.println("--------------------------------------------------------------------");
	}

	public PImage getVideoImage(boolean autoResize) {
		if (videoImage == null)
			return null;
		if (autoResize) {
			if (videoImage.getWidth() == 176) {
				return convertToPImage(resize(videoImage, 320, 240));
			}
		}
		return convertToPImage(videoImage);

	}

	public float getPitch() {
		return pitch;
	}

	public float getRoll() {
		return roll;
	}

	public float getYaw() {
		return yaw;
	}

	public float getAltitude() {
		return altitude;
	}

	public float getVelocityX() {
		return vx;
	}

	public float getVelocityY() {
		return vy;
	}

	public float[] getVelocity() {
		return velocity;
	}

	public int getBatteryPercentage() {
		return battery;
	}

	private PImage convertToPImage(BufferedImage bufImg) {
		if (bufImg == null)
			return null;
		try {

			/*
			 * if (pimg == null) { System.out.println("new pimage"); //pimg =
			 * new PImage(bufImg); //pimg = new PImage(); pimg = new PImage(320,
			 * 240, PConstants.ARGB); DataBufferInt dbi = new
			 * DataBufferInt(pimg.pixels, pimg.pixels.length); wr =
			 * Raster.createWritableRaster(bufImg.getSampleModel(), dbi, new
			 * Point(0, 0)); } else { System.out.println("update pimage");
			 * bufImg.copyData(wr); pimg.updatePixels(); }
			 * 
			 * return pimg;
			 */

			if (pimg == null) {
				pimg = new PImage(bufImg.getWidth(), bufImg.getHeight(),
						PConstants.ARGB);
				//DataBufferInt dbi = new DataBufferInt(pimg.pixels,
					//	pimg.pixels.length);
				//wr = Raster.createWritableRaster(bufImg.getSampleModel(), dbi,
						//new Point(0, 0));
			}
			//bufImg.copyData(wr);
			bufImg.getRGB(0, 0, pimg.width, pimg.height, pimg.pixels, 0,
					pimg.width);
			pimg.updatePixels();
			return pimg;

			/*
			 * PImage img = new PImage(bufImg.getWidth(), bufImg.getHeight(),
			 * PConstants.ARGB); bufImg.getRGB(0, 0, img.width, img.height,
			 * img.pixels, 0, img.width); img.updatePixels(); return img;
			 */

		} catch (Exception e) {
			// System.err.println("Can't create image from buffer");
			// e.printStackTrace();
		}
		return null;
	}

	/**
	 * resize bufferedimage
	 * 
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
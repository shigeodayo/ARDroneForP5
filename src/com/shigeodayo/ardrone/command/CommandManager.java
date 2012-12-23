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
package com.shigeodayo.ardrone.command;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.shigeodayo.ardrone.manager.AbstractManager;

public class CommandManager extends AbstractManager {

	private static final String CR = "\r";
	private static final String SEQ = "$SEQ$";

	private static int seq = 1;

	private FloatBuffer fb = null;
	private IntBuffer ib = null;

	private boolean landing = true;
	private boolean continuance = false;
	private String command = null;

	/** speed */
	private float speed = 0.05f; // 0.01f - 1.0f

	public CommandManager(InetAddress inetaddr) {
		this.inetaddr = inetaddr;
		initialize();
	}

	public void setHorizontalCamera() {
		command = "AT*CONFIG=" + SEQ + ",\"video:video_channel\",\"0\"";
		continuance = false;
	}

	public void setVerticalCamera() {
		command = "AT*CONFIG=" + SEQ + ",\"video:video_channel\",\"1\"";
		continuance = false;
	}

	public void setHorizontalCameraWithVertical() {
		command = "AT*CONFIG=" + SEQ + ",\"video:video_channel\",\"2\"";
		continuance = false;
	}

	public void setVerticalCameraWithHorizontal() {
		command = "AT*CONFIG=" + SEQ + ",\"video:video_channel\",\"3\"";
		continuance = false;
	}

	public void toggleCamera() {
		command = "AT*CONFIG=" + SEQ + ",\"video:video_channel\",\"4\"";
		continuance = false;
	}

	public void landing() {
		command = "AT*REF=" + SEQ + ",290717696";
		continuance = false;
		landing = true;
	}

	public void takeOff() {
		sendCommand("AT*FTRIM=" + SEQ);
		command = "AT*REF=" + SEQ + ",290718208";
		continuance = false;
		landing = false;
	}

	public void reset() {
		command = "AT*REF=" + SEQ + ",290717952";
		continuance = true;
		landing = true;
	}

	public void forward() {
		command = "AT*PCMD=" + SEQ + ",1,0," + intOfFloat(-speed) + ",0,0"
				+ "\r" + "AT*REF=" + SEQ + ",290718208";
		continuance = true;
	}

	public void forward(int speed) {
		setSpeed(speed);
		forward();
	}

	public void backward() {
		command = "AT*PCMD=" + SEQ + ",1,0," + intOfFloat(speed) + ",0,0"
				+ "\r" + "AT*REF=" + SEQ + ",290718208";
		continuance = true;
	}

	public void backward(int speed) {
		setSpeed(speed);
		backward();
	}

	public void spinRight() {
		command = "AT*PCMD=" + SEQ + ",1,0,0,0," + intOfFloat(speed) + "\r"
				+ "AT*REF=" + SEQ + ",290718208";
		continuance = true;
	}

	public void spinRight(int speed) {
		setSpeed(speed);
		spinRight();
	}

	public void spinLeft() {
		command = "AT*PCMD=" + SEQ + ",1,0,0,0," + intOfFloat(-speed)
				+ "\r" + "AT*REF=" + SEQ + ",290718208";
		continuance = true;
	}

	public void spinLeft(int speed) {
		setSpeed(speed);
		spinLeft();
	}

	public void up() {
		command = "AT*PCMD=" + SEQ + ",1," + intOfFloat(0) + ","
				+ intOfFloat(0) + "," + intOfFloat(speed) + "," + intOfFloat(0)
				+ "\r" + "AT*REF=" + SEQ + ",290718208";
		continuance = true;
	}

	public void up(int speed) {
		setSpeed(speed);
		up();
	}

	public void down() {
		command = "AT*PCMD=" + SEQ + ",1," + intOfFloat(0) + ","
				+ intOfFloat(0) + "," + intOfFloat(-speed) + ","
				+ intOfFloat(0) + "\r" + "AT*REF=" + SEQ + ",290718208";
		continuance = true;
	}

	public void down(int speed) {
		setSpeed(speed);
		down();
	}

	public void goRight() {
		command = "AT*PCMD=" + SEQ + ",1," + intOfFloat(speed) + ",0,0,0"
				+ "\r" + "AT*REF=" + SEQ + ",290718208";
		continuance = true;
	}

	public void goRight(int speed) {
		setSpeed(speed);
		goRight();
	}

	public void goLeft() {
		command = "AT*PCMD=" + SEQ + ",1," + intOfFloat(-speed) + ",0,0,0"
				+ "\r" + "AT*REF=" + SEQ + ",290718208";
		continuance = true;
	}

	public void goLeft(int speed) {
		setSpeed(speed);
		goLeft();
	}

	public void stop() {
		command = "AT*PCMD=" + SEQ + ",1,0,0,0,0";
		continuance = true;
	}

	public void setSpeed(int speed) {
		if (speed > 100)
			speed = 100;
		else if (speed < 1)
			speed = 1;

		this.speed = (float) (speed / 100.0);
	}

	public void enableVideoData() {
		command = "AT*CONFIG=" + SEQ + ",\"general:video_enable\",\"TRUE\""
				+ CR + "AT*FTRIM=" + SEQ;
		continuance = false;
	}

	public void enableDemoData() {
		command = "AT*CONFIG=" + SEQ + ",\"general:navdata_demo\",\"TRUE\""
				+ CR + "AT*FTRIM=" + SEQ;
		continuance = false;
	}

	public void sendControlAck() {
		command = "AT*CTRL=" + SEQ + ",0";
		continuance = false;
	}

	public int getSpeed() {
		return (int) (speed * 100);
	}

	public void disableAutomaticVideoBitrate() {
		command = "AT*CONFIG=" + SEQ + ",\"video:bitrate_ctrl_mode\",\"0\"";
		continuance = false;
	}

	public void setMaxAltitude(int altitude) {
		command = "AT*CONFIG=" + SEQ + ",\"control:altitude_max\",\""
				+ altitude + "\"";
		continuance = false;
	}

	public void setMinAltitude(int altitude) {
		command = "AT*CONFIG=" + SEQ + ",\"control:altitude_min\",\""
				+ altitude + "\"";
		continuance = false;
	}

	/*
	 * Thanks, Tarqu’nio.
	 */
	public void move3D(int speedX, int speedY, int speedZ, int speedSpin) {
		if (speedX > 100)
			speedX = 100;
		else if (speedX < -100)
			speedX = -100;
		if (speedY > 100)
			speedY = 100;
		else if (speedY < -100)
			speedY = -100;
		if (speedZ > 100)
			speedZ = 100;
		else if (speedZ < -100)
			speedZ = -100;

		command = "AT*PCMD=" + SEQ + ",1," + intOfFloat(-speedY / 100.0f)
				+ "," + intOfFloat(-speedX / 100.0f) + ","
				+ intOfFloat(-speedZ / 100.0f) + ","
				+ intOfFloat(-speedSpin / 100.0f) + "\r" + "AT*REF=" + SEQ
				+ ",290718208";
		continuance = true;
	}

	@Override
	public void run() {
		initARDrone();
		while (true) {
			if (this.command != null) {
				sendCommand();
				if (!continuance) {
					command = null;
				}
			} else {
				if (landing) {
					sendCommand("AT*PCMD=" + SEQ + ",1,0,0,0,0" + CR
							+ "AT*REF=" + SEQ + ",290717696");
				} else {
					sendCommand("AT*PCMD=" + SEQ + ",1,0,0,0,0" + CR
							+ "AT*REF=" + SEQ + ",290718208");
				}
			}
			if (seq % 5 == 0) { // <2000ms
				sendCommand("AT*COMWDG=" + SEQ);
			}
		}
	}

	private void initialize() {
		ByteBuffer bb = ByteBuffer.allocate(4);
		fb = bb.asFloatBuffer();
		ib = bb.asIntBuffer();
	}

	private void initARDrone() {
		sendCommand("AT*CONFIG=" + SEQ
				+ ",\"general:navdata_demo\",\"TRUE\"" + CR + "AT*FTRIM="
				+ SEQ); // 1
		sendCommand("AT*PMODE=" + SEQ + ",2" + CR + "AT*MISC=" + SEQ
				+ ",2,20,2000,3000" + CR + "AT*FTRIM=" + SEQ + CR
				+ "AT*REF=" + SEQ + ",290717696"); // 2-5
		sendCommand("AT*PCMD=" + SEQ + ",1,0,0,0,0" + CR + "AT*REF="
				+ SEQ + ",290717696" + CR + "AT*COMWDG=" + SEQ); // 6-8
		sendCommand("AT*PCMD=" + SEQ + ",1,0,0,0,0" + CR + "AT*REF="
				+ SEQ + ",290717696" + CR + "AT*COMWDG=" + SEQ); // 6-8
		sendCommand("AT*FTRIM=" + SEQ);
		System.out.println("Initialize completed!");
	}

	private void sendCommand() {
		sendCommand(this.command);
	}

	private synchronized void sendCommand(String command) {
		int seqIndex = -1;
		while ((seqIndex = command.indexOf(SEQ)) != -1)
			command = command.substring(0, seqIndex) + (seq++)
					+ command.substring(seqIndex + SEQ.length());

		byte[] buffer = (command + CR).getBytes();
		// System.out.println(command);
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length,
				inetaddr, 5556);
		try {
			socket.send(packet);
			Thread.sleep(20); // < 50ms
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private int intOfFloat(float f) {
		fb.put(0, f);
		return ib.get(0);
	}
}
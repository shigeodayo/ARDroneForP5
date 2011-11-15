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
package com.shigeodayo.ardrone;

public interface ARDroneInterface {

	//connection
	public boolean connect();
	public boolean connectVideo();
	public boolean connectNav();
	public void disconnect();

	public void start();
	
	//camera
	public void setHorizontalCamera();//setFrontCameraStreaming()
	public void setVerticalCamera();//setBellyCameraStreaming()
	public void setHorizontalCameraWithVertical();//setFrontCameraWithSmallBellyStreaming()
	public void setVerticalCameraWithHorizontal();//setBellyCameraWithSmallFrontStreaming()
	public void toggleCamera();
	
	//control command
	public void landing();
	public void takeOff();
	public void reset();
	public void forward();
	public void forward(int speed);
	public void backward();
	public void backward(int speed);
	public void spinRight();
	public void spinRight(int speed);
	public void spinLeft();
	public void spinLeft(int speed);
	public void up();
	public void up(int speed);
	public void down();
	public void down(int speed);
	public void goRight();
	public void goRight(int speed);
	public void goLeft();
	public void goLeft(int speed);
	public void stop();
	
	//getter
	public int getSpeed();
	//setter
	public void setSpeed(int speed);
	
	//set max/min altitude
	public void setMaxAltitude(int altitude);
	public void setMinAltitude(int altitude);
	
	public void move3D(int speedX, int speedY, int speedZ, int speedSpin);
}

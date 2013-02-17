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

package com.shigeodayo.ardrone;

public interface ARDroneInterface {

	
	// connection
	public boolean connect();

	public boolean connectVideo();

	public boolean connectNav();

	public void disconnect();

	public void start();

	// camera
	public void setHorizontalCamera();// setFrontCameraStreaming()

	public void setVerticalCamera();// setBellyCameraStreaming()

	public void setHorizontalCameraWithVertical();// setFrontCameraWithSmallBellyStreaming()

	public void setVerticalCameraWithHorizontal();// setBellyCameraWithSmallFrontStreaming()

	public void toggleCamera();

	// control command
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

	public void move3D(int speedX, int speedY, int speedZ, int speedSpin);

	// speed
	public int getSpeed();
	public void setSpeed(int speed);

	// set max altitude
	public void setMaxAltitude(int altitude);
	// set min altitude
	public void setMinAltitude(int altitude);
}
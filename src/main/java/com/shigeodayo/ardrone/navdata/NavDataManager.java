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
package com.shigeodayo.ardrone.navdata;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

import com.shigeodayo.ardrone.command.CommandManager;
import com.shigeodayo.ardrone.manager.AbstractManager;
import com.shigeodayo.ardrone.utils.ARDroneConstants;

public abstract class NavDataManager extends AbstractManager {

	protected CommandManager manager = null;

	// listeners
	private AttitudeListener attitudeListener = null;
	private StateListener stateListener = null;
	private VelocityListener velocityListener = null;
	private BatteryListener batteryListener = null;

	public NavDataManager(InetAddress inetaddr, CommandManager manager) {
		this.inetaddr = inetaddr;
		this.manager = manager;
	}

	public void setAttitudeListener(AttitudeListener attitudeListener) {
		this.attitudeListener = attitudeListener;
	}

	public void setBatteryListener(BatteryListener batteryListener) {
		this.batteryListener = batteryListener;
	}

	public void setStateListener(StateListener stateListener) {
		this.stateListener = stateListener;
	}

	public void setVelocityListener(VelocityListener velocityListener) {
		this.velocityListener = velocityListener;
	}

	@Override
	public void run() {
		initializeDrone();
		
		NavDataParser parser = new NavDataParser();

		parser.setAttitudeListener(attitudeListener);
		parser.setBatteryListener(batteryListener);
		parser.setStateListener(stateListener);
		parser.setVelocityListener(velocityListener);

		while (true) {
			try {
				ticklePort(ARDroneConstants.NAV_PORT);
				DatagramPacket packet = new DatagramPacket(new byte[1024],
						1024, inetaddr, ARDroneConstants.NAV_PORT);

				socket.receive(packet);

				ByteBuffer buffer = ByteBuffer.wrap(packet.getData(), 0,
						packet.getLength());

				parser.parseNavData(buffer);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NavDataException e) {
				e.printStackTrace();
			}
		}
	}
	
	protected abstract void initializeDrone();
}
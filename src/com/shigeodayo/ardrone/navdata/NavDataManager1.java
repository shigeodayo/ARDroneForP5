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

import java.net.InetAddress;

import com.shigeodayo.ardrone.command.CommandManager;
import com.shigeodayo.ardrone.utils.ARDroneConstants;

public class NavDataManager1 extends NavDataManager {

	public NavDataManager1(InetAddress inetaddr, CommandManager manager) {
		super(inetaddr, manager);
		
		//System.out.println("navdata manager 1");
	}

	@Override
	protected void initializeDrone() {
		ticklePort(ARDroneConstants.NAV_PORT);
		manager.enableDemoData();
		ticklePort(ARDroneConstants.NAV_PORT);
		manager.sendControlAck();		
	}
}

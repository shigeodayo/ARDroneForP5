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

package com.shigeodayo.ardrone.navdata;

import java.net.InetAddress;

import com.shigeodayo.ardrone.command.CommandManager;
import com.shigeodayo.ardrone.utils.ARDroneConstants;

public class NavDataManager2 extends NavDataManager {

	public NavDataManager2(InetAddress inetaddr, CommandManager manager) {
		super(inetaddr, manager);
		
		//System.out.println("navdata manager 2");
	}

	@Override
	protected void initializeDrone() {
		ticklePort(ARDroneConstants.NAV_PORT);
		//manager.disableBootStrap();
		manager.enableDemoData();
		ticklePort(ARDroneConstants.NAV_PORT);
		manager.sendControlAck();		
	}	
}

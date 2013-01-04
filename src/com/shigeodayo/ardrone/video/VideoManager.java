package com.shigeodayo.ardrone.video;

import java.net.InetAddress;

import com.shigeodayo.ardrone.command.CommandManager;
import com.shigeodayo.ardrone.manager.AbstractManager;
import com.shigeodayo.ardrone.utils.ARDroneConstants;

public abstract class VideoManager extends AbstractManager {
	protected CommandManager manager = null;

	protected ImageListener listener = null;

	public VideoManager(InetAddress inetaddr, CommandManager manager) {
		this.inetaddr = inetaddr;
		this.manager = manager;
	}
	
	public void setImageListener(ImageListener listener) {
		this.listener = listener;
	}

	public void removeImageListener() {
		listener = null;
	}
	
	
	protected void setVideoPort() {
		ticklePort(ARDroneConstants.VIDEO_PORT);
		manager.enableVideoData();
		ticklePort(ARDroneConstants.VIDEO_PORT);
		manager.disableAutomaticVideoBitrate();		
	}
}
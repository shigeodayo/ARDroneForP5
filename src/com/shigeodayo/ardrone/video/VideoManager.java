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
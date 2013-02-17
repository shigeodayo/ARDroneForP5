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
package com.shigeodayo.ardrone.command;

import java.net.InetAddress;

import com.shigeodayo.ardrone.utils.ARDroneConstants;

/**
 * CommandManager for AR.Drone 1.0
 * 
 * @author shigeo
 * 
 */
public class CommandManager1 extends CommandManager {

	public CommandManager1(InetAddress inetaddr) {
		this(inetaddr, false);
	}

	public CommandManager1(InetAddress inetaddr, boolean useHighRezVideoStreaming) {
		super(inetaddr);
		if (useHighRezVideoStreaming) // high resolution version is not implemented yet
			VIDEO_CODEC = ARDroneConstants.VIDEO_CODEC_H264;
		else
			VIDEO_CODEC = ARDroneConstants.VIDEO_CODEC_UVLC;
		

		//System.out.println("command manager 1");
	}

	@Override
	protected void initializeDrone() {
		try {
			sendCommand("AT*CONFIG=" + SEQ +
					  ",\"general:navdata_demo\",\"TRUE\"" + CR + "AT*FTRIM=" + SEQ);
					  
			Thread.sleep(20);
			
			sendCommand("AT*PMODE=" + SEQ + ",2" + CR);

			Thread.sleep(20);

			sendCommand("AT*MISC=" + SEQ + ",2,20,2000,3000" + CR);

			Thread.sleep(20);

			// enable video
			sendCommand("AT*CONFIG=" + SEQ
					+ ",\"general:video_enable\",\"TRUE\"" + CR);

			Thread.sleep(20);

			// fix bit rate
			sendCommand("AT*CONFIG=" + SEQ
					+ ",\"video:bitrate_ctrl_mode\",\"0\"" + CR);
			
			Thread.sleep(20);

			// video codec
			sendCommand("AT*CONFIG=" + SEQ + ",\"video:video_codec\",\""
					+ VIDEO_CODEC + "\"" + CR);

			Thread.sleep(20);

			// set front camera
			sendCommand("AT*CONFIG=" + SEQ + ",\"video:video_channel\",\"0\""
					+ CR);

			Thread.sleep(20);

			// trim
			sendCommand("AT*FTRIM=" + SEQ + CR);

			Thread.sleep(20);

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("Initialize AR.Drone 1.0 !!");
	}
}

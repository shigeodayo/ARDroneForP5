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

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

import com.shigeodayo.ardrone.command.CommandManager;
import com.shigeodayo.ardrone.utils.ARDroneConstants;

/**
 * VideoManager for AR.Drone 1.0
 * 
 * @author shigeo
 *
 */
public class VideoManager1 extends VideoManager {

	private ReadRawFileImage rrfi = null;

	public VideoManager1(InetAddress inetaddr, CommandManager manager) {
		super(inetaddr, manager);
		rrfi = new ReadRawFileImage();
		
		//System.out.println("video manager 1");
	}

	@Override
	public void run() {
		setVideoPort();
		
		byte[] buf = new byte[153600];
		DatagramPacket packet = new DatagramPacket(buf, buf.length);
		BufferedImage image = null;
		while (true) {
			try {
				ticklePort(ARDroneConstants.VIDEO_PORT);
				socket.receive(packet);
				image = rrfi.readUINT_RGBImage(buf);
				if (listener != null) {
					listener.imageUpdated(image);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
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
package com.shigeodayo.ardrone.utils;

public class ARDroneConstants {
	
	/** default IP address */
	public static final String IP_ADDRESS = "192.168.1.1";
	
	/** default PORT */
	public static final int PORT = 5556;
	public static final int VIDEO_PORT = 5555;
	public static final int NAV_PORT = 5554;
	public static final int FTP_PORT = 5551;
	
	/** default ID, for AR.Drone 2.0 */
	public static final String SESSION_ID = "d2e081a3"; 
	public static final String PROFILE_ID = "be27e2e4";
	public static final String APPLICATION_ID = "d87f7e0c";
	
	/** video codec */
	public static final String VIDEO_CODEC_UVLC = "0x20"; // 320x240, 15fps for AR.Drone 1.0
	public static final String VIDEO_CODEC_H264 = "0x40"; // 640x360, 20fps for AR.Drone 1.0
	public static final String VIDEO_CODEC_360P = "0x81"; // 360p, for AR.Drone 2.0
	public static final String VIDEO_CODEC_720P = "0x83"; // 720p, for AR.Drone 2.0

}

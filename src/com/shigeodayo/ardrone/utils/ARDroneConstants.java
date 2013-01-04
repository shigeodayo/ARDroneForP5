/*
 *
  Copyright (c) <2011>, <Shigeo Yoshida>
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
The names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, 
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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

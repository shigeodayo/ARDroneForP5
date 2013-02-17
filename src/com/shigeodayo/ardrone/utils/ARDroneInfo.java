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

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import com.shigeodayo.ardrone.ARDrone;


public class ARDroneInfo {

	private static final String VERSION_FILE_NAME = "version.txt";
	
	private int major = -1;
	private int minor = -1;
	private int revision = -1;

	private int count = 0;
	
	public ARDroneInfo() {
		connectToDroneThroughFtp();
	}

	public ARDroneVersion getDroneVersion() {
		//System.out.println("major:" + major);
		switch (major) {
		case 1:
			return ARDroneVersion.ARDRONE1;
		case 2:
			return ARDroneVersion.ARDRONE2;
		default:
			return null;
		}
	}
	
	private boolean connectToDroneThroughFtp() {
		FTPClient client = new FTPClient();
		BufferedOutputStream bos = null;
		
        try {
        	client.connect(ARDroneConstants.IP_ADDRESS, ARDroneConstants.FTP_PORT);

        	if (!client.login("anonymous", "")) {
        		ARDrone.error("Login failed", this);
        		return false;
        	}
 
        	client.setFileType(FTP.BINARY_FILE_TYPE);

        	bos = new BufferedOutputStream(new OutputStream() {
				
				@Override
				public void write(int arg0) throws IOException {
					//System.out.println("aa:" + (char)arg0);
					switch (count) {
					case 0:
						major = arg0 - '0';
						break;
					case 2:
						minor = arg0 - '0';
						break;
					case 4:
						revision = arg0 - '0';
						break;
					default:
						break;
					}
					count++;
				}
			});
        	
        	
        	if (!client.retrieveFile("/" + VERSION_FILE_NAME, bos)) {
            	ARDrone.error("Cannot find \"" + VERSION_FILE_NAME + "\"", this);
            	return false;
            }
            
            bos.flush();
        	
			//System.out.print("major:" + major);
			//System.out.print(" minor:" + minor);
			//System.out.println(" revision:" + revision);
            
            //System.out.println("done");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
            	if (bos != null) {
            		bos.flush();
            		bos.close();
            	}
                client.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
	}
}

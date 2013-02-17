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
package exmaples;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import com.shigeodayo.ardrone.utils.ARDroneConstants;

import java.io.IOException;
import java.io.FileOutputStream;
 
public class FtpClientExample {
	
    public static void main(String[] args) {
    	
        FTPClient client = new FTPClient();
        FileOutputStream fos = null;
 
        try {
        	client.connect(ARDroneConstants.IP_ADDRESS, ARDroneConstants.FTP_PORT);

        	if (!client.login("anonymous", ""))
        		System.err.println("login failed");
 
        	client.setFileType(FTP.BINARY_FILE_TYPE);

        	String filename = "version.txt";
            fos = new FileOutputStream(filename);
 
            if (!client.retrieveFile("/" + filename, fos))
            	System.err.println("cannot find file");
            
            System.out.println("done");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                	fos.flush();
                    fos.close();
                }
                client.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
 
    }
}
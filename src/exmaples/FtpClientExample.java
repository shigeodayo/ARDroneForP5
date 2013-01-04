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
package com.shigeodayo.ardrone.command;

import java.net.InetAddress;

import com.shigeodayo.ardrone.utils.ARDroneConstants;

/**
 * CommandManager for AR.Drone 2.0
 * 
 * @author shigeo
 * 
 */
public class CommandManager2 extends CommandManager {

	private final String SESSION_ID = ARDroneConstants.SESSION_ID;
	private final String PROFILE_ID = ARDroneConstants.PROFILE_ID;
	private final String APPLICATION_ID = ARDroneConstants.APPLICATION_ID;

	public CommandManager2(InetAddress inetaddr) {
		this(inetaddr, false);
	}

	public CommandManager2(InetAddress inetaddr,
			boolean useHighRezVideoStreaming) {
		super(inetaddr);
		if (useHighRezVideoStreaming) // high resolution version is not implemented yet
			VIDEO_CODEC = ARDroneConstants.VIDEO_CODEC_720P;
		else
			VIDEO_CODEC = ARDroneConstants.VIDEO_CODEC_360P;

		// System.out.println("command manager 2");
	}

	@Override
	protected void initializeDrone() {
		try {

			sendCommand("AT*CONFIG=" + SEQ
					+ ",\"general:navdata_demo\",\"TRUE\"" + CR + "AT*FTRIM="
					+ SEQ); // 1

			Thread.sleep(20);

			sendCommand("AT*PMODE=" + SEQ + ",2" + CR);

			Thread.sleep(20);

			sendCommand("AT*MISC=" + SEQ + ",2,20,2000,3000" + CR);

			Thread.sleep(20);

			sendCommand("AT*CONFIG_IDS=" + SEQ + ",\"" + SESSION_ID + "\",\""
					+ PROFILE_ID + "\",\"" + APPLICATION_ID + "\"" + CR);
			sendCommand("AT*CONFIG=" + SEQ + ",\"custom:session_id\",\""
					+ SESSION_ID + "\"" + CR);

			Thread.sleep(20);

			sendCommand("AT*CONFIG_IDS=" + SEQ + ",\"" + SESSION_ID + "\",\""
					+ PROFILE_ID + "\",\"" + APPLICATION_ID + "\"" + CR);
			sendCommand("AT*CONFIG=" + SEQ + ",\"custom:profile_id\",\""
					+ PROFILE_ID + "\"" + CR);

			Thread.sleep(20);

			sendCommand("AT*CONFIG_IDS=" + SEQ + ",\"" + SESSION_ID + "\",\""
					+ PROFILE_ID + "\",\"" + APPLICATION_ID + "\"" + CR);
			sendCommand("AT*CONFIG=" + SEQ + ",\"custom:application_id\",\""
					+ APPLICATION_ID + "\"" + CR);

			Thread.sleep(20);

			// enable video
			sendCommand("AT*CONFIG_IDS=" + SEQ + ",\"" + SESSION_ID + "\",\""
					+ PROFILE_ID + "\",\"" + APPLICATION_ID + "\"" + CR);
			sendCommand("AT*CONFIG=" + SEQ
					+ ",\"general:video_enable\",\"TRUE\"" + CR);

			Thread.sleep(20);

			// fix bit rate
			sendCommand("AT*CONFIG_IDS=" + SEQ + ",\"" + SESSION_ID + "\",\""
					+ PROFILE_ID + "\",\"" + APPLICATION_ID + "\"" + CR);
			sendCommand("AT*CONFIG=" + SEQ
					+ ",\"video:bitrate_ctrl_mode\",\"0\"" + CR);

			Thread.sleep(20);

			// video codec
			sendCommand("AT*CONFIG_IDS=" + SEQ + ",\"" + SESSION_ID + "\",\""
					+ PROFILE_ID + "\",\"" + APPLICATION_ID + "\"" + CR);
			sendCommand("AT*CONFIG=" + SEQ + ",\"video:video_codec\"," + "\""
					+ VIDEO_CODEC + "\"" + CR);

			Thread.sleep(20);

			// set front camera
			sendCommand("AT*CONFIG_IDS=" + SEQ + ",\"" + SESSION_ID + "\",\""
					+ PROFILE_ID + "\",\"" + APPLICATION_ID + "\"" + CR);
			sendCommand("AT*CONFIG=" + SEQ + ",\"video:video_channel\",\"0\""
					+ CR);

			Thread.sleep(20);

			// trim
			sendCommand("AT*FTRIM=" + SEQ + CR);

			Thread.sleep(20);

		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("Initialize AR.Drone 2.0 !!");

	}

	@Override
	public void setHorizontalCamera() {
		sendCommand("AT*CONFIG_IDS=" + SEQ + ",\"" + SESSION_ID + "\",\""
				+ PROFILE_ID + "\",\"" + APPLICATION_ID + "\"" + CR);
		super.setHorizontalCamera();
	}

	@Override
	public void setVerticalCamera() {
		sendCommand("AT*CONFIG_IDS=" + SEQ + ",\"" + SESSION_ID + "\",\""
				+ PROFILE_ID + "\",\"" + APPLICATION_ID + "\"" + CR);
		super.setVerticalCamera();
	}

	@Override
	public void setHorizontalCameraWithVertical() {
	}

	@Override
	public void setVerticalCameraWithHorizontal() {
	}

	@Override
	public void toggleCamera() {
		sendCommand("AT*CONFIG_IDS=" + SEQ + ",\"" + SESSION_ID + "\",\""
				+ PROFILE_ID + "\",\"" + APPLICATION_ID + "\"" + CR);
		super.toggleCamera();
	}

}

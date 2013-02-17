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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.shigeodayo.ardrone.ARDrone;
import com.shigeodayo.ardrone.navdata.AttitudeListener;
import com.shigeodayo.ardrone.navdata.BatteryListener;
import com.shigeodayo.ardrone.navdata.DroneState;
import com.shigeodayo.ardrone.navdata.StateListener;
import com.shigeodayo.ardrone.navdata.VelocityListener;
import com.shigeodayo.ardrone.video.ImageListener;

/**
 * example for Java application
 * 
 * @author shigeo
 * 
 */
public class ARDroneTest extends JFrame {

	private static final long serialVersionUID = 1L;

	private ARDrone ardrone = null;

	private MyPanel myPanel = null;

	public ARDroneTest() {
		initialize();
	}

	private void initialize() {
		ardrone = new ARDrone("192.168.1.1");
		System.out.println("connect drone controller");
		ardrone.connect();
		System.out.println("connect drone navdata");
		ardrone.connectNav();
		System.out.println("connect drone video");
		ardrone.connectVideo();
		System.out.println("start drone");
		ardrone.start();

		ardrone.addImageUpdateListener(new ImageListener() {
			@Override
			public void imageUpdated(BufferedImage image) {
				if (myPanel != null) {
					myPanel.setImage(image);
					myPanel.repaint();
				}
			}
		});

		ardrone.addAttitudeUpdateListener(new AttitudeListener() {
			@Override
			public void attitudeUpdated(float pitch, float roll, float yaw,
					int altitude) {
				System.out.println("pitch: " + pitch + ", roll: " + roll
				 + ", yaw: " + yaw + ", altitude: " + altitude);
			}
		});

		ardrone.addBatteryUpdateListener(new BatteryListener() {
			@Override
			public void batteryLevelChanged(int percentage) {
				System.out.println("battery: " + percentage + " %");
			}
		});

		ardrone.addStateUpdateListener(new StateListener() {
			@Override
			public void stateChanged(DroneState state) {
				 //System.out.println("state: " + state);
			}
		});

		ardrone.addVelocityUpdateListener(new VelocityListener() {
			@Override
			public void velocityChanged(float vx, float vy, float vz) {
				 System.out.println("vx: " + vx + ", vy: " + vy + ", vz: " +
				 vz);
			}
		});

		addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				ardrone.stop();
			}

			public void keyPressed(KeyEvent e) {
				int key = e.getKeyCode();
				int mod = e.getModifiersEx();

				boolean shiftflag = false;
				if ((mod & InputEvent.SHIFT_DOWN_MASK) != 0) {
					shiftflag = true;
				}

				switch (key) {
				case KeyEvent.VK_ENTER:
					ardrone.takeOff();
					break;
				case KeyEvent.VK_SPACE:
					ardrone.landing();
					break;
				case KeyEvent.VK_S:
					ardrone.stop();
					break;
				case KeyEvent.VK_LEFT:
					if (shiftflag)
						ardrone.spinLeft();
					else 
						ardrone.goLeft();
					break;
				case KeyEvent.VK_RIGHT:
					if (shiftflag)
						ardrone.spinRight();
					else
						ardrone.goRight();
					break;
				case KeyEvent.VK_UP:
					if (shiftflag)
						ardrone.up();
					else
						ardrone.forward();
					break;
				case KeyEvent.VK_DOWN:
					if (shiftflag)
						ardrone.down();
					else
						ardrone.backward();
					break;
				case KeyEvent.VK_1:
					ardrone.setHorizontalCamera();
					// System.out.println("1");
					break;
				case KeyEvent.VK_2:
					ardrone.setHorizontalCameraWithVertical();
					// System.out.println("2");
					break;
				case KeyEvent.VK_3:
					ardrone.setVerticalCamera();
					// System.out.println("3");
					break;
				case KeyEvent.VK_4:
					ardrone.setVerticalCameraWithHorizontal();
					// System.out.println("4");
					break;
				case KeyEvent.VK_5:
					ardrone.toggleCamera();
					// System.out.println("5");
					break;
				case KeyEvent.VK_R:
					ardrone.spinRight();
					break;
				case KeyEvent.VK_L:
					ardrone.spinLeft();
					break;
				case KeyEvent.VK_U:
					ardrone.up();
					break;
				case KeyEvent.VK_D:
					ardrone.down();
					break;
				case KeyEvent.VK_E:
					ardrone.reset();
					break;
				}
			}
		});

		this.setTitle("ardrone");
		this.setSize(400, 400);
		this.add(getMyPanel());
	}

	private JPanel getMyPanel() {
		if (myPanel == null) {
			myPanel = new MyPanel();
		}
		return myPanel;
	}

	/**
	 * 
	 * @author shigeo
	 * 
	 */
	private class MyPanel extends JPanel {
		private static final long serialVersionUID = -7635284252404123776L;

		/** ardrone video image */
		private BufferedImage image = null;

		public void setImage(BufferedImage image) {
			this.image = image;
		}

		public void paint(Graphics g) {
			g.setColor(Color.white);
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
			if (image != null)
				g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(),
						null);
		}
	}

	public static void main(String args[]) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				final ARDroneTest thisClass = new ARDroneTest();
				thisClass.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				thisClass.addWindowListener(new WindowAdapter() {
					@Override
					public void windowOpened(WindowEvent e) {
						// System.out.println("WindowOpened");
					}

					@Override
					public void windowClosing(WindowEvent e) {
						thisClass.dispose();
					}
				});
				thisClass.setVisible(true);
			}
		});
	}
}
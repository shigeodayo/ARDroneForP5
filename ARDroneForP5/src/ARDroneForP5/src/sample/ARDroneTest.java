/*
 * Copyright (c) <2011>, <Shigeo Yoshida>
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
package sample;

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
 * sample program for Java application
 * @author shigeo
 *
 */
public class ARDroneTest extends JFrame{
	
	private static final long serialVersionUID = 1L;

	private ARDrone ardrone=null;
	private boolean shiftflag=false;
	
	private MyPanel myPanel;
	
	public ARDroneTest(){
		initialize();
	}
	
	private void initialize(){
		ardrone=new ARDrone("192.168.1.1");
		System.out.println("connect drone controller");
		ardrone.connect();
		System.out.println("connect drone navdata");
		ardrone.connectNav();
		System.out.println("connect drone video");
		ardrone.connectVideo();
		System.out.println("start drone");
		ardrone.start();
		
		
		ardrone.addImageUpdateListener(new ImageListener(){
			@Override
			public void imageUpdated(BufferedImage image) {
				if(myPanel!=null){
					myPanel.setImage(image);
					myPanel.repaint();
				}
			}
		});
		
		ardrone.addAttitudeUpdateListener(new AttitudeListener() {
			@Override
			public void attitudeUpdated(float pitch, float roll, float yaw, int altitude) {
				System.out.println("pitch: "+pitch+", roll: "+roll+", yaw: "+yaw+", altitude: "+altitude);
			}
		});
		
		ardrone.addBatteryUpdateListener(new BatteryListener() {
			@Override
			public void batteryLevelChanged(int percentage) {
				System.out.println("battery: "+percentage+" %");
			}
		});
				
		ardrone.addStateUpdateListener(new StateListener() {
			@Override
			public void stateChanged(DroneState state) {
				System.out.println("state: "+state);
			}
		});
		
		ardrone.addVelocityUpdateListener(new VelocityListener() {
			@Override
			public void velocityChanged(float vx, float vy, float vz) {
				System.out.println("vx: "+vx+", vy: "+vy+", vz: "+vz);
			}
		});
		
		addKeyListener(new KeyAdapter(){
			public void keyReleased(KeyEvent e){
				ardrone.stop();
			}
			public void keyPressed(KeyEvent e){
				int key=e.getKeyCode();
				int mod=e.getModifiersEx();
				
				shiftflag=false;
				if((mod & InputEvent.SHIFT_DOWN_MASK)!=0){
					shiftflag=true;
				}

				switch(key){
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
					if(shiftflag){
						ardrone.spinLeft();
						shiftflag=false;
					}else
						ardrone.goLeft();
					break;
				case KeyEvent.VK_RIGHT:
					if(shiftflag){
						ardrone.spinRight();
						shiftflag=false;
					}else
						ardrone.goRight();
					break;
				case KeyEvent.VK_UP:
					if(shiftflag){
						ardrone.up();
						shiftflag=false;
					}else
						ardrone.forward();
					break;
				case KeyEvent.VK_DOWN:
					if(shiftflag){
						ardrone.down();
						shiftflag=false;
					}else
						ardrone.backward();
					break;
				case KeyEvent.VK_1:
					ardrone.setHorizontalCamera();
					//System.out.println("1");
					break;
				case KeyEvent.VK_2:
					ardrone.setHorizontalCameraWithVertical();
					//System.out.println("2");
					break;
				case KeyEvent.VK_3:
					ardrone.setVerticalCamera();
					//System.out.println("3");
					break;
				case KeyEvent.VK_4:
					ardrone.setVerticalCameraWithHorizontal();
					//System.out.println("4");
					break;
				case KeyEvent.VK_5:
					ardrone.toggleCamera();
					//System.out.println("5");
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
	
	
	private JPanel getMyPanel(){
		if(myPanel==null){
			myPanel=new MyPanel();
		}
		return myPanel;
	}
	
	/**
	 * •`‰æ—p‚Ìƒpƒlƒ‹
	 * @author shigeo
	 *
	 */
	private class MyPanel extends JPanel{
		private static final long serialVersionUID = -7635284252404123776L;

		/** ardrone video image */
		private BufferedImage image=null;
		
		public void setImage(BufferedImage image){
			this.image=image;
		}
		
		public void paint(Graphics g){
			g.setColor(Color.white);
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
			if(image!=null)
				g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
		}
	}
	
	public static void main(String args[]){
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				final ARDroneTest thisClass=new ARDroneTest();
				thisClass.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				thisClass.addWindowListener(new WindowAdapter(){
					@Override
					public void windowOpened(WindowEvent e) {
						System.out.println("WindowOpened");
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
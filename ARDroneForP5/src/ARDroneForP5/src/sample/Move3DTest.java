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

public class Move3DTest extends JFrame {
	private static final long serialVersionUID = 1L;

	private ARDrone ardrone=null;
	private boolean shiftflag=false;
	
	private MyPanel myPanel;
	
	public Move3DTest(){
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
						//ardrone.spinLeft();
						ardrone.move3D(0, 0, 0, 10);
						shiftflag=false;
					}else
						ardrone.move3D(0, 10, 0, 0);
						//ardrone.goLeft();
					break;
				case KeyEvent.VK_RIGHT:
					if(shiftflag){
						//ardrone.spinRight();
						ardrone.move3D(0, 0, 0, -10);
						shiftflag=false;
					}else
						ardrone.move3D(0, -10, 0, 0);
						//ardrone.goRight();
					break;
				case KeyEvent.VK_UP:
					if(shiftflag){
						//ardrone.up();
						ardrone.move3D(0, 0, -10, 0);
						shiftflag=false;
					}else
						ardrone.move3D(10, 0, 0, 0);
						//ardrone.forward();
					break;
				case KeyEvent.VK_DOWN:
					if(shiftflag){
						//ardrone.down();
						ardrone.move3D(0, 0, 10, 0);
						shiftflag=false;
					}else
						ardrone.move3D(-10, 0, 0, 0);
						//ardrone.backward();
					break;
				case KeyEvent.VK_R:
					//ardrone.spinRight();
					ardrone.move3D(0, 0, 0, 10);
					break;
				case KeyEvent.VK_L:
					//ardrone.spinLeft();
					ardrone.move3D(0, 0, 0, -10);
					break;
				case KeyEvent.VK_U:
					//ardrone.up();
					ardrone.move3D(0, 0, -10, 0);
					break;
				case KeyEvent.VK_D:
					//ardrone.down();
					ardrone.move3D(0, 0, 10, 0);
					break;
				case KeyEvent.VK_E:
					ardrone.reset();
					break;
				case KeyEvent.VK_Z:
					ardrone.move3D(10, 10, 10, 10);
					break;
				case KeyEvent.VK_X:
					ardrone.move3D(-10, -10, -10, -10);
					break;
				case KeyEvent.VK_C:
					ardrone.move3D(10, 10, 0, 0);
					break;
				case KeyEvent.VK_V:
					ardrone.move3D(-10, -10, 0, 0);
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
				final Move3DTest thisClass=new Move3DTest();
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

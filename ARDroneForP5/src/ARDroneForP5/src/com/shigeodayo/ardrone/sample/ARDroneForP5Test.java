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
package com.shigeodayo.ardrone.sample;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.shigeodayo.ardrone.processing.ARDroneForP5;


public class ARDroneForP5Test extends JFrame{
	private static final long serialVersionUID = 1L;

	private ARDroneForP5 ardrone=null;
	private boolean shiftflag=false;

	public ARDroneForP5Test(){
		initialize();
	}
	
	private void initialize(){
		ardrone=new ARDroneForP5("192.168.1.1");
		System.out.println("connect drone controller");
		ardrone.connect();
		System.out.println("connect drone navdata");
		ardrone.connectNav();
		System.out.println("connect drone video");
		ardrone.connectVideo();
		System.out.println("start drone");
		ardrone.start();
		
		this.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e){
				int key=e.getKeyCode();
				
				switch(key){
				case KeyEvent.VK_SHIFT:
					shiftflag=true;
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
				case KeyEvent.VK_P:
					ardrone.printARDroneInfo();
					break;
				}
			}
		});
	}
	
	public static void main(String args[]){
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				final ARDroneForP5Test thisClass=new ARDroneForP5Test();
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
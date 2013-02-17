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
package com.shigeodayo.ardrone.manager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public abstract class AbstractManager implements Runnable {

	protected InetAddress inetaddr = null;
	protected DatagramSocket socket = null;

	public boolean connect(int port) {
		try {
			socket = new DatagramSocket(port);
			socket.setSoTimeout(3000);
		} catch (SocketException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void close() {
		socket.close();
	}

	protected void ticklePort(int port) {
		byte[] buf = { 0x01, 0x00, 0x00, 0x00 };
		DatagramPacket packet = new DatagramPacket(buf, buf.length, inetaddr,
				port);
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

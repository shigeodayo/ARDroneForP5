/*
 * Copyright 2010 Cliff L. Biffle.  All Rights Reserved.
 * Use of this source code is governed by a BSD-style license that can be found
 * in the LICENSE file.
 */

package com.shigeodayo.ardrone.navdata;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class NavDataParser {
	private AttitudeListener attitudeListener;
	private StateListener stateListener;
	private VelocityListener velocityListener;
	private BatteryListener batteryListener;

	long lastSequenceNumber = 1;

	// set listeners
	public void setBatteryListener(BatteryListener batteryListener) {
		this.batteryListener = batteryListener;
	}

	public void setAttitudeListener(AttitudeListener attitudeListener) {
		this.attitudeListener = attitudeListener;
	}

	public void setStateListener(StateListener stateListener) {
		this.stateListener = stateListener;
	}

	public void setVelocityListener(VelocityListener velocityListener) {
		this.velocityListener = velocityListener;
	}

	public void parseNavData(ByteBuffer buffer) throws NavDataException {
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		int magic = buffer.getInt();
		// System.out.printf("%02x\n", magic);
		requireEquals("Magic must be correct", 0x55667788, magic);

		int state = buffer.getInt();
		long sequence = buffer.getInt() & 0xFFFFFFFFL;
		@SuppressWarnings("unused")
		int vision = buffer.getInt();

		if (sequence <= lastSequenceNumber && sequence != 1) {
			return;
		}
		lastSequenceNumber = sequence;

		if (stateListener != null) {
			stateListener.stateChanged(new DroneState(state));
		}

		while (buffer.position() < buffer.limit()) {
			int tag = buffer.getShort() & 0xFFFF;
			int payloadSize = (buffer.getShort() & 0xFFFF) - 4;
			ByteBuffer optionData = buffer.slice().order(
					ByteOrder.LITTLE_ENDIAN);
			optionData.limit(payloadSize);
			buffer.position(buffer.position() + payloadSize);

			dispatch(tag, optionData);
		}
	}

	private void dispatch(int tag, ByteBuffer optionData) {
		switch (tag) {
		case 0:
			processNavDataDemo(optionData);
			break;
		}
	}

	private void processNavDataDemo(ByteBuffer optionData) {
		@SuppressWarnings("unused")
		int controlState = optionData.getInt();
		int batteryPercentage = optionData.getInt();

		float theta = optionData.getFloat() / 1000;
		float phi = optionData.getFloat() / 1000;
		float psi = optionData.getFloat() / 1000;

		int altitude = optionData.getInt();

		float vx = optionData.getFloat();
		float vy = optionData.getFloat();
		float vz = optionData.getFloat();

		if (batteryListener != null) {
			batteryListener.batteryLevelChanged(batteryPercentage);
		}

		if (attitudeListener != null) {
			attitudeListener.attitudeUpdated(theta, phi, psi, altitude);
			// System.out.println("update in parser");
		}

		if (velocityListener != null) {
			velocityListener.velocityChanged(vx, vy, vz);
		}
	}

	private void requireEquals(String message, int expected, int actual)
			throws NavDataException {
		if (expected != actual) {
			throw new NavDataException(message + " : expected " + expected
					+ ", was " + actual);
		}
	}
}
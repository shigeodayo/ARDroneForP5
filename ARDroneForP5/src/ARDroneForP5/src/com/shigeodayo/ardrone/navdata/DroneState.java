/*
 * Copyright 2010 Cliff L. Biffle.  All Rights Reserved.
 * Use of this source code is governed by a BSD-style license that can be found
 * in the LICENSE file.
 */
package com.shigeodayo.ardrone.navdata;

public class DroneState {
	private final int bits;
	
	public DroneState(int bits){
		this.bits=bits;
	}
	
	public String toString(){
		return "DroneState("+Integer.toHexString(bits)+")";
	}
	
	public boolean equals(Object o){
		if(o==null || o.getClass()!=getClass())
			return false;
		return bits==((DroneState)o).bits;
	}
	
	public int hashCode(){
		return 31*bits;
	}
}
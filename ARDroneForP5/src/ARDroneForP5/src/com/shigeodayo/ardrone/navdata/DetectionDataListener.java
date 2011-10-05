package com.shigeodayo.ardrone.navdata;

import java.awt.Dimension;
import java.awt.Point;

public interface DetectionDataListener {
	void detectionDataChanged(DetectType type, Point pos, Dimension size,int distance);
}

/**
 Requirement
 You need to add Picking.jar.
 (http://code.google.com/p/processing-picking-library/)
 
 You need to add NyAR4psg.jar and NyARToolkit.jar.
 You need to put camera_para.dat and patt.hiro into "data" folder.
 (http://nyatla.jp/nyartoolkit/wiki/index.php?NyAR4psg)
 
 You need to add PPopupMenu.jar.
 (https://github.com/hixi-hyi/ppopupmenu)
 */
import processing.click.*;

import picking.*;

import com.shigeodayo.ardrone.manager.*;
import com.shigeodayo.ardrone.navdata.*;
import com.shigeodayo.ardrone.utils.*;
import com.shigeodayo.ardrone.processing.*;
import com.shigeodayo.ardrone.command.*;
import com.shigeodayo.ardrone.*;
import com.shigeodayo.ardrone.video.*;

import processing.video.*;
import jp.nyatla.nyar4psg.*;
import processing.opengl.*;
import javax.media.opengl.*;

ARDroneForP5 ardrone;

MultiMarker nya;
PFont font = createFont("FFScala", 32);

Picker picker;

PPopupMenu menu;

int r = 255;
int g = 255;
int b = 255;

void setup() {
  size(320, 240, P3D);
  colorMode(RGB, 100);
  println(MultiMarker.VERSION);

  nya = new MultiMarker(this, width, height, "camera_para.dat", NyAR4PsgConfig.CONFIG_PSG);
  nya.addARMarker("patt.hiro", 80);

  ardrone = new ARDroneForP5("192.168.1.1");
  ardrone.connect();
  ardrone.connectNav();
  ardrone.connectVideo();
  ardrone.start();

  picker = new Picker(this);

  menu = new PPopupMenu(this);

  menu.addMenuItem("red", "changeBoxColorRed");
  menu.addMenuItem("green", "changeBoxColorGreen");
  menu.addMenuItem("blue", "changeBoxColorBlue");
  menu.addMenuItem("yellow", "changeBoxColorYellow");
  menu.addMenuItem("purple", "changeBoxColorPurple");
  menu.addMenuItem("cyan", "changeBoxColorCyan");
}

void draw() {
  PImage img = ardrone.getVideoImage(true);
  if (img == null)
    return;

  hint(DISABLE_DEPTH_TEST);
  image(img, 0, 0);
  hint(ENABLE_DEPTH_TEST);

  float pitch = ardrone.getPitch();
  float roll = ardrone.getRoll();
  float yaw = ardrone.getYaw();
  float altitude = ardrone.getAltitude();
  float[] velocity = ardrone.getVelocity();
  int battery = ardrone.getBatteryPercentage();

  String attitude = "pitch:" + pitch + "\nroll:" + roll + "\nyaw:" + yaw + "\naltitude:" + altitude;
  text(attitude, 20, 85);
  String vel = "vx:" + velocity[0] + "\nvy:" + velocity[1];
  text(vel, 20, 140);
  String bat = "battery:" + battery + " %";
  text(bat, 20, 170);

  nya.detect(img);
  background(0);
  nya.drawBackground(img);
  if ((!nya.isExistMarker(0))) {
    return;
  }

  picker.start(0);
  nya.beginTransform(0);
  fill(r, g, b);
  translate(0, 0, 20);
  box(40);
  nya.endTransform();
}

void keyPressed() {
  if (key == CODED) {
    if (keyCode == UP) {
      ardrone.forward();
    }
    else if (keyCode == DOWN) {
      ardrone.backward();
    }
    else if (keyCode == LEFT) {
      ardrone.goLeft();
    }
    else if (keyCode == RIGHT) {
      ardrone.goRight();
    }
    else if (keyCode == SHIFT) {
      ardrone.takeOff();
    }
    else if (keyCode == CONTROL) {
      ardrone.landing();
    }
  }
  else {
    if (key == 's') {
      ardrone.stop();
    }
    else if (key == 'r') {
      ardrone.spinRight();
    }
    else if (key == 'l') {
      ardrone.spinLeft();
    }
    else if (key == 'u') {
      ardrone.up();
    }
    else if (key == 'd') {
      ardrone.down();
    }
    else if (key == '1') {
      ardrone.setHorizontalCamera();
    }
    else if (key == '2') {
      ardrone.setHorizontalCameraWithVertical();
    }
    else if (key == '3') {
      ardrone.setVerticalCamera();
    }
    else if (key == '4') {
      ardrone.setVerticalCameraWithHorizontal();
    }
    else if (key == '5') {
      ardrone.toggleCamera();
    }
  }
}

void mouseClicked() {
  if (mouseButton == RIGHT) {
    int id = picker.get(mouseX, mouseY);
    println("id:" + id);
    switch(id) {
    case 0:
      if (!nya.isExistMarker(0))
        return;
      menu.show();
      break;
    }
  }
  else {
    r = 255;
    g = 255;
    b = 255;
  }
}

void changeBoxColorRed() {
  r = 255;
  g = 0;
  b = 0;
}
void changeBoxColorGreen() {
  r = 0;
  g = 255;
  b = 0;
}
void changeBoxColorBlue() {
  r = 0;
  g = 0;
  b = 255;
}
void changeBoxColorYellow() {
  r = 255;
  g = 255;
  b = 0;
}
void changeBoxColorPurple() {
  r = 255;
  g = 0;
  b = 255;
}
void changeBoxColorCyan() {
  r = 0;
  g = 255;
  b = 255;
}


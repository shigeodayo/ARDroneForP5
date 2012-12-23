/**
 Requirement
 You need to add Picking.jar.
 (http://code.google.com/p/processing-picking-library/)
 
 You need to add NyAR4psg.jar and NyARToolkit.jar.
 You need to put camera_para.dat and patt.hiro into "data" folder.
 (http://nyatla.jp/nyartoolkit/wiki/index.php?NyAR4psg)
 */
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

int cr, cg, cb;
int c = 0;

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

  cr = cg = cb = 100;
  picker = new Picker(this);
}

void draw() {
  c++;
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
  fill(cr, cg, cb);
  translate(0, 0, 20);
  box(40);
  nya.endTransform();
  picker.stop();
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
      ardrone.setHorizontalCameraWithVertical(); // front camera and second camera
    }
    else if (key == '3') {
      ardrone.setVerticalCamera(); // second camera
    }
    else if (key == '4') {
      ardrone.setVerticalCameraWithHorizontal(); // second camra and front camera
    }
    else if (key == '5') {
      ardrone.toggleCamera(); // set next camera
    }
  }
}

void mouseClicked() {
  int id = picker.get(mouseX, mouseY);
  if (id == 0) {
    cr = int(random(0, 100));
    cg = int(random(0, 100));
    cb = int(random(0, 100));
  }
}


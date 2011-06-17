import processing.video.*;
import jp.nyatla.nyar4psg.*;
import processing.opengl.*;
import javax.media.opengl.*;

//ardrone
ARDroneForP5 ardrone;
//artoolkit
NyARBoard nya;
PFont font;

int threshold=100;

void setup() {
  size(320, 240, OPENGL);
  hint(ENABLE_OPENGL_4X_SMOOTH);
  background(204);
  colorMode(RGB, 100);
  font=createFont("FFScala", 32);

  setUpARToolKit(); 
  setUpDrone();
}

void setUpARToolKit() {
  nya=new NyARBoard(this,width,height,"camera_para.dat","patt.hiro",80);
  nya.gsThreshold=threshold;
  nya.cfThreshold=0.4;
}

void drawMarkerPos(int[][] points) {
  textFont(font,10.0);
  stroke(100,0,0);
  fill(100,0,0);
  for(int i=0;i<4;i++) {
    ellipse(nya.pos2d[i][0], nya.pos2d[i][1],5,5);
  }
  fill(0,0,0);
  for(int i=0;i<4;i++) {
    text("("+nya.pos2d[i][0]+","+nya.pos2d[i][1]+")",nya.pos2d[i][0],nya.pos2d[i][1]);
  }
}

String angle2text(float a) {
  int i=(int)degrees(a);
  i=(i>0?i:i+360);
  return (i<100?"  ":i<10?" ":"")+Integer.toString(i);
}
String trans2text(float i) {
  return (i<100?"  ":i<10?" ":"")+Integer.toString((int)i);
}

void setUpDrone() {
  //ardrone
  ardrone=new ARDroneForP5();
  ardrone.connect("192.168.1.1");
  ardrone.connectNav();
  ardrone.connectVideo();
  ardrone.start();
}

void draw() {
  PImage img=ardrone.getVideoImageAsPImage();
  if(img==null)
    return;

  hint(DISABLE_DEPTH_TEST);
  image(img, 0, 0);
  hint(ENABLE_DEPTH_TEST);

  float pitch=ardrone.getPitch();
  float roll=ardrone.getRoll();
  float yaw=ardrone.getYaw();
  float altitude=ardrone.getAltitude();
  float[] velocity=ardrone.getVelocity();
  int battery=ardrone.getBatteryPercentage();

  String attitude="pitch:"+pitch+"\nroll:"+roll+"\nyaw:"+yaw+"\naltitude:"+altitude;
  text(attitude, 20, 85);
  String vel="vx:"+velocity[0]+"\nvy:"+velocity[1];
  text(vel, 20, 140);
  String bat="battery:"+battery+" %";
  text(bat, 20, 170);
  text(threshold, 20, 200);

  if(nya.detect(img)) {
    hint(DISABLE_DEPTH_TEST);

    textFont(font,25.0);
    fill((int)((1.0-nya.confidence)*100),(int)(nya.confidence*100),0);
    text((int)(nya.confidence*100)+"%",width-60,height-20);

    pushMatrix();
    textFont(font,10.0);
    fill(0,100,0,80);
    translate((nya.pos2d[0][0]+nya.pos2d[1][0]+nya.pos2d[2][0]+nya.pos2d[3][0])/4+50,(nya.pos2d[0][1]+nya.pos2d[1][1]+nya.pos2d[2][1]+nya.pos2d[3][1])/4+50);
    text("TRANS "+trans2text(nya.trans.x)+","+trans2text(nya.trans.y)+","+trans2text(nya.trans.z),0,0);
    text("ANGLE "+angle2text(nya.angle.x)+","+angle2text(nya.angle.y)+","+angle2text(nya.angle.z),0,15);
    popMatrix();    

    drawMarkerPos(nya.pos2d);
    hint(ENABLE_DEPTH_TEST);

    PGraphicsOpenGL pgl = (PGraphicsOpenGL) g;
    nya.beginTransform(pgl);

    stroke(255,200,0);
    translate(0,0,20);
    box(40);
    nya.endTransform();
  }
}

void keyPressed(){
  if(key==CODED){
    if(keyCode==UP){
      ardrone.forward();
    }else if(keyCode==DOWN){
      ardrone.backward();
    }else if(keyCode==LEFT){
      ardrone.goLeft();
    }else if(keyCode==RIGHT){
      ardrone.goRight();
    }else if(keyCode==SHIFT){
      ardrone.takeOff();
    }else if(keyCode==CONTROL){
      ardrone.landing();
    }
  }else{
    if(key=='s'){
      ardrone.stop();
    }else if(key=='r'){
     ardrone.spinRight(); 
    }else if(key=='l'){
      ardrone.spinLeft();
    }else if(key=='u'){
      ardrone.up();
    }else if(key=='d'){
      ardrone.down();
    }    if(key=='1') {
      ardrone.setFrontCameraStreaming();
    }else if(key=='2') {
      ardrone.setFrontCameraWithSmallBellyStreaming();
    }else if(key=='3'){
      ardrone.setBellyCameraStreamingResize();
    }else if(key=='4'){
      ardrone.setBellyCameraWithSmallFrontStreamingResize();
    }else if(key=='5'){
      ardrone.setNextCameraResize();
    }else if(key=='q'){
        threshold+=10;
        nya.gsThreshold=threshold;
    }else if(key=='w'){
      if(threshold>10) {
        threshold-=10;
        nya.gsThreshold=threshold;
      }
    }
  }
}

import processing.video.*;
import jp.nyatla.nyar4psg.*;
import processing.opengl.*;
import javax.media.opengl.*;


ARDroneForP5 ardrone;

SingleARTKMarker nya;
PFont font;

int threshold=100;

void setup() {
  size(320,240,OPENGL);
  hint(ENABLE_OPENGL_4X_SMOOTH);

  colorMode(RGB, 100);
  font=createFont("FFScala", 32);
  
    setUpARToolKit();
    setUpDrone();
}
void setUpARToolKit(){
  //平面検出クラスを作成
  //Left hand projection matrix
  nya=new SingleARTKMarker(this,width,height,"camera_para.dat",SingleARTKMarker.CS_LEFT);
  println(nya.VERSION);
  String[] marker={"patt.hiro","patt.kanji"};
  nya.setARCodes(marker,80);
  nya.setConfidenceThreshold(0.6,0.5); 
}

void setUpDrone() {
  //ardrone
  ardrone=new ARDroneForP5();
  ardrone.connect("192.168.1.1");
  ardrone.connectNav();
  ardrone.connectVideo();
  ardrone.start();
  ardrone.setFrontCameraStreaming();
}


//この関数は、マーカ頂点の情報を描画します。
void drawMarkerPos(int[][] points)
{
  textFont(font,10.0);
  stroke(100,0,0);
  fill(100,0,0);
  for(int i=0;i<4;i++){
    ellipse(nya.pos2d[i][0], nya.pos2d[i][1],5,5);
  }
  fill(0,0,0);
  for(int i=0;i<4;i++){
    text("("+nya.pos2d[i][0]+","+nya.pos2d[i][1]+")",nya.pos2d[i][0],nya.pos2d[i][1]);
  }
}

String angle2text(float a)
{
  int i=(int)degrees(a);
  i=(i>0?i:i+360);
  return (i<100?"  ":i<10?" ":"")+Integer.toString(i);
}
String trans2text(float i)
{
  return (i<100?"  ":i<10?" ":"")+Integer.toString((int)i);
}

void draw() {
  background(255);
 
  PImage img=ardrone.getVideoImageAsPImage();
  if(img==null)
    return;


  //背景を描画
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

  
  //detect結果で処理を分ける。
  switch(nya.detect(img)){
  case SingleARTKMarker.ST_NOMARKER:
    //マーカ見つかんない
    break;
  case SingleARTKMarker.ST_NEWMARKER:
    //マーカが見つかったらしい
    println("Marker appeared. #"+nya.markerid);
    break;
  case SingleARTKMarker.ST_UPDATEMARKER:
    //マーカの位置を更新中・・・
    
    //マーカの位置を描画
    hint(DISABLE_DEPTH_TEST);
    drawMarkerPos(nya.pos2d);
    hint(ENABLE_DEPTH_TEST);
    
    //キューブを描画
    PGraphicsOpenGL pgl = (PGraphicsOpenGL) g;
    nya.beginTransform(pgl);//マーカ座標系での描画を開始する。
    //ここからマーカ座標系
    stroke(255,200,0);
    translate(0,0,20);
    if(nya.markerid==0){fill(255,0,0);}else if(nya.markerid==1){fill(0,0,255);}
    box(40);
    nya.endTransform();//マーカ座標系での描画を終了する。（必ず呼んで！）

    break;
  case SingleARTKMarker.ST_REMOVEMARKER:
    //マーカなくなったでござる。
    println("Marker disappeared.");
    break;
  }
}

void keyPressed() {
    if(key=='1') {
      ardrone.setFrontCameraStreaming();
    }else if(key=='2') {
      ardrone.setFrontCameraWithSmallBellyStreaming();
    }else if(key=='3'){
      ardrone.setBellyCameraStreamingResize();
    }else if(key=='4'){
      ardrone.setBellyCameraWithSmallFrontStreamingResize();
    }else if(key=='5'){
      ardrone.setNextCameraResize();
    }else if(key=='q') {
      if(threshold<255) {
        threshold+=10;
      }
    }else if(key=='w'){
      if(threshold>10){
        threshold-=10;
      }
    }
}

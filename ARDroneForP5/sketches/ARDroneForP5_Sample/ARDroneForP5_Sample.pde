ARDroneForP5 ardrone;

void setup(){
  size(320, 240);
  
  ardrone=new ARDroneForP5();
  //AR.Droneに接続，操縦するために必要
  ardrone.connect("192.168.1.1");
  //AR.Droneからのセンサ情報を取得するために必要
  ardrone.connectNav();
  //AR.Droneからの画像情報を取得するために必要
  ardrone.connectVideo();
  //これを宣言すると上でconnectした3つが使えるようになる．
  ardrone.start();
}

void draw(){
  background(204);  

  //AR.Droneからの画像を取得
  PImage img=ardrone.getVideoImageAsPImage();
  if(img==null)
    return;
  image(img, 0, 0);

  //AR.Droneからのセンサ情報を標準出力する．
  //ardrone.printARDroneInfo();
  //各種センサ情報を取得する
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
}

//PCのキーに応じてAR.Droneを操作できる．
void keyPressed(){
  if(key==CODED){
    if(keyCode==UP){
      ardrone.forward();//前
    }else if(keyCode==DOWN){
      ardrone.backward();//後
    }else if(keyCode==LEFT){
      ardrone.goLeft();//左
    }else if(keyCode==RIGHT){
      ardrone.goRight();//右
    }/*else if(keyCode==SHIFT){
      ardrone.takeOff();//離陸，離陸した状態でないと移動は出来ない．
    }else if(keyCode==CONTROL){
      ardrone.landing();//着陸
    }*/
  }else{
    if(key=='s'){
      ardrone.stop();//停止
    }else if(key=='r'){
     ardrone.spinRight(); //右方向に回転
    }else if(key=='l'){
      ardrone.spinLeft();//左方向に回転
    }else if(key=='u'){
      ardrone.up();//上昇
    }else if(key=='d'){
      ardrone.down();//下降
    }else if(key=='1'){
      ardrone.setFrontCameraStreaming();//前カメラ
    }else if(key=='2'){
      ardrone.setFrontCameraWithSmallBellyStreaming();//前カメラとお腹カメラ
    }else if(key=='3'){
      ardrone.setBellyCameraStreaming();//お腹カメラ
    }else if(key=='4'){
      ardrone.setBellyCameraWithSmallFrontStreaming();//お腹カメラと前カメラ
    }else if(key=='5'){
      ardrone.setNextCamera();//次のカメラ
    }else if(key=='6'){
      ardrone.setBellyCameraStreamingResize();//QCIF->VGAにサイズを変換する
    }else if(key=='7'){
      ardrone.setBellyCameraWithSmallFrontStreamingResize();//QCIF->VGAにサイズを変換する
    }else if(key=='8'){
      ardrone.setNextCameraResize();//QCIF->VGAにサイズを変換する
    }
  }
}

import org.openkinect.*;
import org.openkinect.processing.*;

//kinect
KinectTracker tracker;
Kinect kinect;

//ardrone
ARDroneForP5 ardrone;

boolean flag=false;

void setup() {
  size(640,520);
  //size(320, 240);
  
  setupKinect();
  setupARDrone();
}

void setupKinect(){
  kinect = new Kinect(this);
  tracker = new KinectTracker();  
}  

void setupARDrone(){
  ardrone=new ARDroneForP5();
  ardrone.connect("192.168.1.1");
  ardrone.connectNav();
  ardrone.connectVideo();
  ardrone.start();
}

void draw() {
  background(255);

  // Run the tracking analysis
  tracker.track();
  // Show the image
  //tracker.display();
  
  if(flag){
  PImage img=ardrone.getVideoImageAsPImage();
  if(img==null)
    return;
  image(img, 0, 0);

  }else{
  tracker.display();
    
  }

  // Let's draw the raw location
  PVector v1 = tracker.getPos();
  
  fill(50,100,250,200);
  //fill(255, 0, 0, 200);
  noStroke();
  //ellipse(v1.x,v1.y,20,20);
 // ellipse((int)(v1.x*320/640),
   //       (int)(v1.y*240/480), 20, 20);

 // print(v1.x+","+v1.y+","+v1.z);

  // Let's draw the "lerped" location
  PVector v2 = tracker.getLerpedPos();
  fill(100,250,50,200);
  noStroke();
  //ellipse(v2.x,v2.y,20,20);
  // ellipse((int)(v2.x*320/640),
    //      (int)(v2.y*240/480), 20, 20);
 // print(v2.x+","+v2.y+","+v2.z);

  // Display some info
  int t = tracker.getThreshold();
  fill(0);
  text("threshold: " + t + "    " +  "framerate: " + (int)frameRate + "    " + "UP increase threshold, DOWN decrease threshold",10,500);


  int x=(int)v2.x;
  int y=(int)v2.y;
  //float z=v2.z;
  int width=640;
  int height=480;
  
  
  // Get the raw depth as array of integers
  int[] depth = kinect.getRawDepth();
  //PVector v = depthToWorld(x,y,depth[x+y*width]);
  
  //print("z:"+depth[x+y*width]+"\n");
  int z=(int)depth[x+y*width];
  
  if(200<y && y<300){
    if(x<270){
      fill(0, 255, 0, 200);
      print("left\n");
      ardrone.goLeft();
    }else if(370<x){
      fill(0, 255, 0, 200);
      print("right\n");
      ardrone.goRight();  
    }else{
     // print("z:"+depth[x+y*width]+"\n");
      //800<z<860
      if(z<810){
        fill(255, 0, 0, 200);
        print("forward\n");
        ardrone.forward();
      }else if(830<z){
        fill(255, 0, 0, 200);
        print("backward\n");
        ardrone.backward();
      }
    }
  }else{
   if(290<y){
     fill(0, 0, 255, 200);
     print("down\n");
     ardrone.down();
   }else if(y<190){
     fill(0, 0, 255, 200);
     print("up\n");
     ardrone.up();
   }
  }
  
  if(flag){
    ellipse((int)(v1.x*320/640),
          (int)(v1.y*240/480), 20, 20);

   ellipse((int)(v2.x*320/640),
          (int)(v2.y*240/480), 20, 20);
  }else{
      fill(50,100,250,200);

       ellipse(v1.x, v1.y, 20, 20);
  fill(100,250,50,200);

   ellipse(v2.x, v2.y, 20, 20);
 
  }


}


void keyPressed(){
  int t = tracker.getThreshold();
  if(key==CODED){
    if(keyCode==UP){
      ardrone.forward();
    }else if(keyCode==DOWN){
      ardrone.backward();
    }else if(keyCode==LEFT){
      ardrone.goLeft();
    }else if(keyCode==RIGHT){
      ardrone.goRight();
    }/*else if(keyCode==SHIFT){
      ardrone.takeOff();
    }else if(keyCode==CONTROL){
      ardrone.landing();
    }*/
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
    }else if(key=='1'){
      ardrone.setFrontCameraStreaming();
    }else if(key=='2'){
      ardrone.setFrontCameraWithSmallBellyStreaming();
    }else if(key=='q'){
      t+=5;
      tracker.setThreshold(t);
    }else if(key=='w'){
      t-=5;
      tracker.setThreshold(t);      
    }else if(key=='i'){
      if(flag)
        flag=false;
      else
        flag=true;
    }
  }
}

void stop() {
  tracker.quit();
  super.stop();
}


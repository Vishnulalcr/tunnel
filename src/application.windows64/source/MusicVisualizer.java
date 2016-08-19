import processing.core.*;
import ddf.minim.*;
import ddf.minim.analysis.*;


public class MusicVisualizer extends PApplet {

    int   width;
    int   height;
    
    float trackX = 0;
    float trackY = 0;
    float trackZ = 0;

    Tunnel tunnel;

    Minim minim;
    BeatDetect beat;

    int  r = 200;
    float rad = 70;
    int periode = 5000;
    int displace = 250;
    int displace2 = 500;

    AudioInput player;

    public MusicVisualizer(Tunnel t, int w, int h) {
        width  = w;
        height = h;
        tunnel = t;
        player = tunnel.in;

    }

  public void settings() {
    size(width, height);
  }
  public void setup()
  {
    minim = new Minim(this);
    beat = new BeatDetect();
    background(0);
  }
  
  public void track() {
   if(Main.kinect != null) {
       Main.kinect.update();
       trackX = (float)width * Main.kinect.RightHandDepthRatio;
       trackY = (float)height * Main.kinect.RightHandRaisedRatio;
       trackZ = 0;
   } else {
       trackX = mouseX;
       trackY = mouseY;
   }
  }

  public void draw()
  {
    synchronized (Tunnel.class) {
        background(0);
        track();
     
       if (trackX/2 > 100)
         r = (int)trackX/2;

       beat.detect(player.mix);
       //fill(#1A1F18, 20);
       noStroke();
       rect(0, 0, width, height);
       translate(width/2, height/2);
       noFill();

       float time = millis();
       int red = (int)(128+127*cos(2*PI/periode*time));
       int green = (int)(128+127*cos(2*PI/periode*(displace-time)));
       int blue = (int)(128+127*cos(2*PI/periode*(displace2-time)));
       fill(red, green, blue);
       if (beat.isOnset()) rad = (float)(rad*0.9);
       else rad = 70 + trackY/8;
       ellipse(0, 0, 2*rad, 2*rad);
       stroke(random(255), random(255), random(255));
       int bsize = player.bufferSize();
       for (int i = 0; i < bsize - 1; i+=5)
       {
         float x = (r)*cos(i*2*PI/bsize);
         float y = (r)*sin(i*2*PI/bsize);
         float x2 = (r + player.left.get(i)*100)*cos(i*2*PI/bsize);
         float y2 = (r + player.left.get(i)*100)*sin(i*2*PI/bsize);
         line(x, y, x2, y2);
       }
       beginShape();
       noFill();
         stroke(random(255), random(255), random(255));
       for (int i = 0; i < bsize; i+=30)
       {
         float x2 = (r + player.left.get(i)*100)*cos(i*2*PI/bsize);
         float y2 = (r + player.left.get(i)*100)*sin(i*2*PI/bsize);
         vertex(x2, y2);
         pushStyle();
         stroke(random(255), random(255), random(255));
         strokeWeight(2);
         point(x2, y2);
         popStyle();
       }

       endShape();
    }
  }
}
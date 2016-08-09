import processing.core.*;

public class Field extends PApplet {

    int NUM_PARTICLES = 100;
    ParticleSystem p;

    int   width;
    int   height;
    Tunnel tunnel;


    public Field(Tunnel t, int w, int h) {
        width  = w;
        height = h;
        tunnel = t;
    }

    public void settings() {
        size(width, height);
    }

    public void setup()
    {
        frameRate(30);
        smooth();
        background(0);
        p = new ParticleSystem(tunnel, width, height);
    }

    public void draw()
    {
        synchronized (Tunnel.class) {
            noStroke();
            fill(0);
            rect(0, 0, width, height);
            p.update();
            p.render();
        }
    }

    class ParticleSystem
    {
        Particle[] particles;
        int width;
        int height;
        int[] disruptor = { 0, 0 };

        ParticleSystem(Tunnel t, int w, int h)
        {
            tunnel = t;
            width  = w;
            height = h;

            particles = new Particle[NUM_PARTICLES];
            for(int i = 0; i < NUM_PARTICLES; i++)
            {
                particles[i]= new Particle();
            }
        }

        void update()
        {

            if (tunnel.getAudioAverage() > 2) {
                disruptor[0] = (int) random(0,width);
                disruptor[1] = (int) random(0,height);
            }

            for(int i = 0; i < NUM_PARTICLES; i++)
            {
                particles[i].update(disruptor[0], disruptor[1]);
            }
        }

        void render()
        {
            for(int i = 0; i < NUM_PARTICLES; i++)
            {
                particles[i].render();
            }
        }
    }


    class Particle
    {
        PVector position, velocity;

        int color;

        Particle()
        {
            position = new PVector(random(width),random(height));
            velocity = new PVector();
            color    = color(random(0,255), random(0,255), random(0,255));
        }

        void update(int x, int y)
        {
            velocity.x = 20*(noise(x/10+position.y/100) - (float) 0.5);
            velocity.y = 20*(noise(y/10+position.x/100) - (float) 0.5);
            position.add(velocity);

            if(position.x<0)position.x+=width;
            if(position.x>width)position.x-=width;
            if(position.y<0)position.y+=height;
            if(position.y>height)position.y-=height;
        }

        void render()
        {
            stroke(color);
            line(position.x,position.y,position.x-velocity.x,position.y-velocity.y);
        }
    }

}
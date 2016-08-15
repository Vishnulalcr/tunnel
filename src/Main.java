import processing.core.*;
import java.util.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;


class Main {

    //static String playlistPath = "/Users/skryl/Dropbox/dev/projects/gravity/tunnel/src/data/playlist.txt";
    static String playlistPath = "C:/TunnelGit2/src/data/playlist.txt";
    static Tunnel tunnel;
    static ArrayList <HashMap <String, String>> queue = new ArrayList();
    static int queueIndex = 0;
    static int duration = 100000;
    static int elapsedTime = 0;
    static Wire wire = new Wire();

    public static void main(String... args) {

        wire.SetupCom();
        loadPlaylist();
        loadNextInQueue();

        while (true) {
            if (elapsedTime < 1000*Integer.parseInt(queue.get(queueIndex-1).get("Time")))  {
                elapsedTime = tunnel.millis();
            } else {
              int x= 1000*Integer.parseInt(queue.get(queueIndex).get("Time"));
                tunnel.kill();
                loadNextInQueue();
            }
        }
    }



    public static void loadNextInQueue() {
        tunnel = new Tunnel(queue.get(queueIndex), wire);
        PApplet.runSketch(new String[]{"Tunnel"}, tunnel);

        elapsedTime = 0;
        queueIndex = ++queueIndex % queue.size();
    }


    public static void loadPlaylist() {
        try (Stream<String> stream = Files.lines(Paths.get(playlistPath))) {
            Object[] lines = stream.toArray();

            for (Object line: lines) {
                HashMap<String, String> mapping = new HashMap();

                String[] sketches = ((String) line).split(",");
                mapping.put("Time", sketches[0].trim());
                if (sketches.length == 2) {
                   mapping.put("Tunnel", sketches[1].trim());
                } else if (sketches.length == 3) {
                    mapping.put("Wall", sketches[1].trim());
                    mapping.put("Ceil", sketches[2].trim());
                } else {
                    mapping.put("RWall", sketches[1].trim());
                    mapping.put("LWall", sketches[2].trim());
                    mapping.put("Ceil",  sketches[3].trim());

                }
                queue.add(mapping);
            }
        } catch (IOException e) {
        }
    }


}

package Main;

import ObjectSystem.Rigidbody;
import com.sun.source.tree.NewArrayTree;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class DebugText {

    private static Map<String,String> debugInfo = new HashMap<>();

    public static void log(String key, String value) {
        debugInfo.put(key,value);
    }

    public static void drawDebugText(Graphics g){
        int i = 0;
        for (Map.Entry<String, String> entry : debugInfo.entrySet()) {
            i++;
            g.drawString(entry.getKey()+":"+entry.getValue(), 10 ,11*i);
        }
    }

}

package Main;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DebugText {

    private static Map<String,String> permanentInfo = new HashMap<>();
    private static ArrayList<String> tempInfo = new ArrayList<>(10);
    public static void logPermanently(String key, String value) {
        permanentInfo.put(key,value);
    }
    public static void logTemporarily(String info)
    {
        tempInfo.add(info);
        if(tempInfo.size() > 10){
            tempInfo = new ArrayList<>(tempInfo.subList(tempInfo.size() - 10, tempInfo.size()));
        }
    }

    public static void drawDebugText(Graphics g){
        int i = 0;
        for (Map.Entry<String, String> entry : permanentInfo.entrySet()) {
            i++;
            g.drawString(entry.getKey()+":"+entry.getValue(), 10 ,11*i);
        }
        i+=5;
        for(String entry : tempInfo){
            i++;
            g.drawString(entry, 10, 11*i);
        }
    }
}

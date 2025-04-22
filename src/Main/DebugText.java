package Main;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DebugText {

    private static final Map<String,String> permanentInfo = new HashMap<>();
    private static final ArrayList<String> tempInfo = new ArrayList<>(10);
    public static void logPermanently(String key, String value) {
        permanentInfo.put(key,value);
    }
    private static int width, height;
    public static void logTemporarily(String info)
    {
        tempInfo.add(LocalDateTime.now().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ": " + info);
        if(tempInfo.size() > 10){
            ArrayList<String> splicedTempInfo = new ArrayList<>(tempInfo.subList(tempInfo.size()-10,tempInfo.size()));
            tempInfo.clear();
            tempInfo.addAll(splicedTempInfo);
        }
    }

    public static void drawDebugText(Graphics g){
        int i = 0;
        int w = 0;
        g.setColor(Color.white);
        g.fillRect(10, 0, width, height);
        g.setColor(Color.blue);
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        synchronized (permanentInfo) {
            for (Map.Entry<String, String> entry : permanentInfo.entrySet()) {
                i++;
                String text = entry.getKey()+":"+entry.getValue();
                if(text.length()>w){w=text.length();}
                g.drawString(text, 10 ,11*i);
            }
        }
        i+=2;
        synchronized (tempInfo) {
            for(String entry : tempInfo){
                i++;
                if(entry.length()>w){w=entry.length();}
                g.drawString(entry, 10, 11*i);
            }
        }
        width = Math.round(w*6.1f);
        height = (11*i)+5;

    }
}

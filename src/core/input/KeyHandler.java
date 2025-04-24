package core.input;

import main.Main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

public class KeyHandler implements KeyListener {

    public KeyHandler(){
        Main.gamePanel.addKeyListener(this);
    }

    private final Map<Integer, Key> keys = new HashMap<>();

    @Override
    public void keyTyped(KeyEvent e) {}

    public Key addKey(int keyCode, Runnable onPressed, Runnable onReleased) {
        Key key = new Key(onPressed, onReleased);
        keys.put(keyCode, key);
        return key;
    }

    public Key addKey(int keyCode){
        Key key = new Key();
        keys.put(keyCode, key);
        return key;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (keys.containsKey(e.getKeyCode())) {
            keys.get(e.getKeyCode()).press();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (keys.containsKey(e.getKeyCode())) {
            keys.get(e.getKeyCode()).release();
        }
    }
}

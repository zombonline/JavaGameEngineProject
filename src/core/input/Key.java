package core.input;

public class Key {
    private boolean held = false;
    private boolean pressedThisFrame = false;
    private boolean releasedThisFrame = false;
    private final Runnable onPressed;
    private final Runnable onReleased;

    public Key(Runnable onPressed, Runnable onReleased) {
        this.onPressed = onPressed != null ? onPressed : () -> {};
        this.onReleased = onReleased != null ? onReleased : () -> {};
    }

    public Key(){
        this.onPressed = null;
        this.onReleased = null;
    }

    public void press() {
        if (!held) {
            held = true;
            if(onPressed!= null){onPressed.run();}
        }
    }

    public void release() {
        if (held) {
            held = false;
            if(onReleased!= null){onReleased.run();}
        }
    }

    public boolean isHeld() {
        return held;
    }

    public boolean isPressedThisFrame() {
        return pressedThisFrame;
    }

    public void setPressedThisFrame(boolean pressedThisFrame) {
        this.pressedThisFrame = pressedThisFrame;
    }

    public boolean isReleasedThisFrame() {
        return releasedThisFrame;
    }

    public void setReleasedThisFrame(boolean releasedThisFrame) {
        this.releasedThisFrame = releasedThisFrame;
    }
}

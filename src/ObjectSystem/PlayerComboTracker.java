package ObjectSystem;

import Main.Main;
import Main.DebugText;
import Main.GamePanel;
import Main.SessionManager;
import ObjectSystem.Crate.Crate;
import Utility.Vector2;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class PlayerComboTracker extends Component{
    // Component references
    private Rigidbody rb;

    // Variables
    private int comboCount = 0;
    private float comboDisplayTimer;
    private final float comboDisplayTime;
    private float startFontSize;
    private float fontSize;
    private float fontSizeIncrement;

    public PlayerComboTracker(float comboDisplayTime, float startFontSize, float fontSizeIncrement) {
        this.comboDisplayTime = comboDisplayTime;
        this.startFontSize = startFontSize;
        this.fontSizeIncrement = fontSizeIncrement;
    }


    @Override
    public void awake() {
        super.awake();
        getRequiredComponentReferences();
    }

    @Override
    protected void getRequiredComponentReferences() {
        rb = fetchRequiredComponent(Rigidbody.class);
    }

    @Override
    public void update() {
        if(rb.isGrounded() && rb.velocity.getY() >= 0 && comboCount > 0){
            boolean resetCombo = true;
            for(Collider collider: rb.getGroundedColliders()){
                if(checkIfBreakableCrate(collider.gameObject)){
                    resetCombo = false;
                }
            }
            if(resetCombo){
                comboCount = 0;
                comboDisplayTimer = 0;
            }
        }
        DebugText.logPermanently("Combo count", String.valueOf(comboCount));
        comboDisplayTimer-= GamePanel.getDeltaTime();
    }

    private boolean checkIfBreakableCrate(GameObject crate){
        return crate.getComponent(Crate.class) != null && crate.getComponent(Crate.class).isBreakable();
    }

    public void onCrateHit(){
        comboCount++;
        if(comboCount> SessionManager.getCurrentLevel().getHighestCombo()){
            SessionManager.getCurrentLevel().setHighestCombo(comboCount);
        }
        comboDisplayTimer = comboDisplayTime;
        fontSize = GamePanel.WORLD_SCALE*startFontSize;
    }

    @Override
    public void draw(Graphics2D g2d) {
        if(comboDisplayTimer<0){return;}
        Vector2 screenPos = gameObject.getTransform().getScreenPosition();
        Vector2 screenScale = gameObject.getTransform().getScreenScale();
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, Math.round(fontSize)));
        fontSize += GamePanel.WORLD_SCALE*fontSizeIncrement;
        FontMetrics metrics = g2d.getFontMetrics();
        int textWidth = metrics.stringWidth(String.valueOf(comboCount));
        int textHeight = metrics.getHeight();
        g2d.drawString(String.valueOf(comboCount), screenPos.getX() - textWidth / 2, screenPos.getY() - screenScale.getY() / 2 + textHeight / 2);
    }
    public static Map<String, Object> getDefaultValues(){
        Map<String,Object> defaultValues = new HashMap<>();
        defaultValues.put("comboDisplayTime", 1f);
        defaultValues.put("startFontSize", .2f);
        defaultValues.put("fontSizeIncrement", .01f);
        return defaultValues;
    }
}

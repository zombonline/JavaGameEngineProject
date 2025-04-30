package game.components.player;

import main.GamePanel;
import core.utils.DebugText;
import core.scene.SessionManager;
import game.components.crate.core.Crate;
import core.utils.Vector2;
import game.components.Collider;
import game.components.Rigidbody;
import game.components.core.Component;
import game.entities.GameObject;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class PlayerComboTracker extends Component {
    // Component references
    private Rigidbody rb;

    // Variables
    private int comboCount = 0;
    private float comboDisplayTimer;
    private final float comboDisplayTime;
    private final float startFontSize;
    private float fontSize;
    private final float fontSizeIncrement;

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
                if(checkIfBreakableCrate(collider.getGameObject())){
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
        g2d.drawString(String.valueOf(comboCount), (int) (screenPos.getX() - textWidth / 2), (int) (screenPos.getY() - screenScale.getY() / 2 + textHeight / 2));
    }
    public static Map<String, Object> getDefaultValues(){
        Map<String,Object> defaultValues = new HashMap<>();
        defaultValues.put("comboDisplayTime", 1f);
        defaultValues.put("startFontSize", .2f);
        defaultValues.put("fontSizeIncrement", .01f);
        return defaultValues;
    }
}

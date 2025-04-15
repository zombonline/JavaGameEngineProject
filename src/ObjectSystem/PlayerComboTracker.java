package ObjectSystem;

import Main.DebugText;
import Main.GamePanel;
import Main.SFXPlayer;
import ObjectSystem.Crate.Crate;
import Utility.Vector2;

import java.awt.*;

public class PlayerComboTracker extends Component{
    private int comboCount = 0;
    Rigidbody rb;

    private float comboDisplayTimer;
    private float comboDisplayTime = 1f;
    private int startFontSize = 20;
    private int fontSize;
    private int fontSizeIncrement = 1;
    @Override
    public void awake() {
        super.awake();
        rb = getComponent(Rigidbody.class);
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
        SFXPlayer.playSound("hello", 1);
        comboCount++;
        comboDisplayTimer = comboDisplayTime;
        fontSize = startFontSize;
    }

    @Override
    public void draw(Graphics2D g2d) {
        if(comboDisplayTimer<0){return;}
        Vector2 screenPos = gameObject.transform.getScreenPosition();
        Vector2 screenScale = gameObject.transform.getScreenScale();
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, fontSize));
        fontSize+=fontSizeIncrement;
        FontMetrics metrics = g2d.getFontMetrics();
        int textWidth = metrics.stringWidth(String.valueOf(comboCount));
        int textHeight = metrics.getHeight();
        g2d.drawString(String.valueOf(comboCount), screenPos.getX() - textWidth / 2, screenPos.getY() - screenScale.getY() / 2 + textHeight / 2);
    }
}

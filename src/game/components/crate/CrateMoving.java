package game.components.crate;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import core.asset.AssetLoader;
import core.asset.Assets;
import core.audio.SFXPlayer;
import core.scene.SessionManager;
import game.components.Collider;
import game.components.crate.behaviours.BounceBehavior;
import game.components.crate.behaviours.DestroyedByExplosionBehaviour;
import game.components.crate.behaviours.HitCounterBehavior;
import game.components.crate.behaviours.MovementBehaviour;
import game.components.crate.core.Crate;
import game.entities.GameObject;
import game.components.player.PlayerComboTracker;
import game.components.rendering.SpriteRenderer;
import core.utils.Vector2;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrateMoving extends Crate {

    SpriteRenderer spriteRenderer;
    BufferedImage horizontalImage, verticalImage;
    public CrateMoving(float bounceStrength, int hitsToDestroy, float moveSpeed, float moveDistance, Vector2 dir, BufferedImage horizontalImage, BufferedImage verticalImage){
        super(true, List.of(
                new MovementBehaviour(moveSpeed, moveDistance, dir),
                new BounceBehavior(bounceStrength, true),
                new HitCounterBehavior(hitsToDestroy),
                new DestroyedByExplosionBehaviour()
        ));
        this.horizontalImage = horizontalImage;
        this.verticalImage = verticalImage;

    }

    @Override
    public void awake() {
        super.awake();
        getRequiredComponentReferences();
        grabExtraData();
        setUpHitCounterListener();
        setUpMovementListener();
    }

    private void grabExtraData() {
        float newMoveDistance = Float.parseFloat(gameObject.getExtraData("moveDistance").toString());
        Vector2 newMoveDirection = new Vector2(gameObject.getExtraData("moveDirection").toString());
        getBehavior(MovementBehaviour.class).setMoveDistance(newMoveDistance);
        getBehavior(MovementBehaviour.class).setMoveDirection(newMoveDirection);
        if(newMoveDirection.getY()!=0){
            spriteRenderer.setSpriteImage(verticalImage);
        } else {
            spriteRenderer.setSpriteImage(horizontalImage);
        }
    }

    @Override
    protected void getRequiredComponentReferences() {
        super.getRequiredComponentReferences();
        spriteRenderer = fetchRequiredComponent(SpriteRenderer.class);
    }
    private void setUpHitCounterListener(){
        getBehavior(HitCounterBehavior.class).addListener(
                new HitCounterBehavior.HitCounterListener() {
                    @Override
                    public void onHit(int current, int start, Collider other) {
                        SessionManager.getCurrentLevel().getObjectByName("Player").getComponent(PlayerComboTracker.class).onCrateHit();
                    }
                    @Override
                    public void onHitsReachedZero(Collider other) {
                        if(!isBreakable()){return;}
                        SFXPlayer.playSound(Assets.SFXClips.CRATE_DESTROYED);
                        GameObject.destroy(gameObject);
                    }
                }
        );
    }
    private void setUpMovementListener(){
        getBehavior(MovementBehaviour.class).addListener(
                new MovementBehaviour.MovementListener() {
                    @Override
                    public void onChangeDirection(Vector2 newDirection) {
                        spriteRenderer.setFlipHorizontally(newDirection.equals(Vector2.left));
                        spriteRenderer.setFlipVertically(newDirection.equals(Vector2.up));
                    }
                }
        );
    }
    public static Map<String, Object> getDefaultValues(){
        Map<String,Object> defaultValues = new HashMap<>();
        defaultValues.put("bounceStrength", 20f);
        defaultValues.put("hitsToDestroy",1);
        defaultValues.put("moveSpeed", 0.8f);
        defaultValues.put("moveDistance", 0.5f);
        defaultValues.put("direction", Vector2.right);
        defaultValues.put("horizontalSprite", AssetLoader.getInstance().getImage(Assets.Images.CRATE_MOVING_HORIZONTAL));
        defaultValues.put("verticalSprite", AssetLoader.getInstance().getImage(Assets.Images.CRATE_MOVING_VERTICAL));
        return defaultValues;
    }

}

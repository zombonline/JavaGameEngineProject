package ObjectSystem.Crate;

import Main.GameUI;
import Main.SessionManager;
import ObjectSystem.Collider;
import ObjectSystem.Crate.Behaviours.BounceBehavior;
import ObjectSystem.Crate.Behaviours.DestroyedByExplosionBehaviour;
import ObjectSystem.Crate.Behaviours.HitCounterBehavior;
import ObjectSystem.Crate.Behaviours.MovementBehaviour;
import ObjectSystem.GameObject;
import ObjectSystem.PlayerComboTracker;
import ObjectSystem.SpriteRenderer;
import Utility.Vector2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrateHorizontalMoving extends Crate{

    SpriteRenderer spriteRenderer;

    public CrateHorizontalMoving(float bounceStrength, int hitsToDestroy, float moveSpeed, float moveDistance, Vector2 dir){
        super(true, List.of(
                new MovementBehaviour(moveSpeed, moveDistance, dir),
                new BounceBehavior(bounceStrength, true),
                new HitCounterBehavior(hitsToDestroy),
                new DestroyedByExplosionBehaviour()
        ));

    }

    @Override
    public void awake() {
        super.awake();
        grabExtraData();
        getRequiredComponentReferences();
        setUpHitCounterListener();
        setUpMovementListener();
    }

    private void grabExtraData() {
        float newMoveDistance = Float.parseFloat(gameObject.getExtraData("moveDistance").toString());
        getBehavior(MovementBehaviour.class).setMoveDistance(newMoveDistance);
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
                    public void onHit(int current, int start) {
                        SessionManager.getCurrentLevel().getObjectByName("Player").getComponent(PlayerComboTracker.class).onCrateHit();
                    }
                    @Override
                    public void onHitsReachedZero() {
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
                        spriteRenderer.setFlipHorizontally(!spriteRenderer.getFlipHorizontally());
                    }
                }
        );
    }
    public static Map<String, Object> getDefaultValues(){
        Map<String,Object> defaultValues = new HashMap<>();
        defaultValues.put("bounceStrength", 20f);
        defaultValues.put("hitsToDestroy",1);
        defaultValues.put("moveSpeed", 0.4f);
        defaultValues.put("moveDistance", 0.5f);
        defaultValues.put("direction", Vector2.right);
        return defaultValues;
    }

}

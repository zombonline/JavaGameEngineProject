package ObjectSystem;

import Main.Assets;
import Main.PrefabReader;
import Utility.Vector2;

public class CrateExplosive extends Crate{
    float bounceStrength;
    boolean triggered;
    SpriteAnimator spriteAnimator;
    SpriteAnimator.AnimatorListener animatorListener;
    public CrateExplosive(float bounceStrength){
        super(true);
        this.bounceStrength = bounceStrength;
    }

    @Override
    public void awake() {
        super.awake();

        spriteAnimator = getComponent(SpriteAnimator.class);
        animatorListener = new SpriteAnimator.AnimatorListener() {
            @Override
            public void onAnimationEvent(String eventKey) {}
            @Override
            public void onAnimationComlete() {
                ExplodeImmediate();
            }
        };
        spriteAnimator.addListener(animatorListener);
    }

    @Override
    public void onCrateTouchTop(Collider other) {
        if(!other.gameObject.name.equals("Player")){ return; }
        if(triggered){return;}
        if(other.getComponent(Rigidbody.class).velocity.getY()<0.1f){return;}
        other.getComponent(Rigidbody.class).velocity = Vector2.zero;
        other.getComponent(Rigidbody.class).addForce(Vector2.up.mul(bounceStrength));
        Explode();
    }

    @Override
    public void onCrateTouchBottom(Collider other) {
        if(!other.gameObject.name.equals("Player")){ return; }
        if(triggered){return;}
        if(other.getComponent(Rigidbody.class).velocity.getY()>-0.1f){ return;}
        other.getComponent(Rigidbody.class).velocity = Vector2.zero;
        other.getComponent(Rigidbody.class).addForce(Vector2.down.mul(.25f));
        Explode();
    }
    public void Explode(){

        triggered = true;
        spriteAnimator.loadAnimation(Assets.Animations.CRATE_EXPLOSIVE_TRIGGER);
    }
    public void ExplodeImmediate(){
        GameObject explosion = PrefabReader.getObject(Assets.Prefabs.EXPLOSION);
        explosion.transform.setPosition(gameObject.transform.getPosition());
        GameObject.destroy(gameObject);
    }

}

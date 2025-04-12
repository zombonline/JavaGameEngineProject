package ObjectSystem;

import Main.Assets;
import Utility.Vector2;

public class PlayerAnimation extends Component{
    Player player;
    Rigidbody rb;
    SpriteAnimator animator;
    SpriteRenderer renderer;
    String currentAnimation;
    String prevAnimation;
    @Override
    public void awake() {
        player = getComponent(Player.class);
        rb = getComponent(Rigidbody.class);
        animator = getComponent(SpriteAnimator.class);
        renderer = getComponent(SpriteRenderer.class);
    }

    @Override
    public void update() {
        if(Math.abs(rb.velocity.getX()) > 0.2){
            renderer.setFlipHorizontally(Math.signum(rb.velocity.getX()) == -1);
            if(Math.abs(rb.velocity.getX())<4f){
                currentAnimation = Assets.Animations.PLAYER_TEST_WALK;
            } else {
                currentAnimation = Assets.Animations.PLAYER_TEST_RUN;
            }
        }
        else {
            currentAnimation = Assets.Animations.PLAYER_IDLE;
        }
        if(!rb.isGrounded() && rb.velocity.getY()>1){
            currentAnimation = Assets.Animations.PLAYER_TEST_FALL;
        }

        if(!currentAnimation.equals(prevAnimation)){
            animator.loadAnimation(currentAnimation);
        }
        prevAnimation =currentAnimation;
    }
}

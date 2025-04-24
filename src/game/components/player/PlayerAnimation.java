package game.components.player;

import core.asset.Assets;
import game.components.Rigidbody;
import game.components.rendering.SpriteAnimator;
import game.components.rendering.SpriteRenderer;
import game.components.core.Component;

public class PlayerAnimation extends Component {
    // Component references
    private Player player;
    private Rigidbody rb;
    private SpriteAnimator animator;
    private SpriteRenderer renderer;

    //Variables
    private String currentAnimation;
    private String prevAnimation;
    @Override
    public void awake() {
        player = getComponent(Player.class);
        rb = getComponent(Rigidbody.class);
        animator = getComponent(SpriteAnimator.class);
        renderer = getComponent(SpriteRenderer.class);
    }

    @Override
    protected void getRequiredComponentReferences() {
        player = fetchRequiredComponent(Player.class);
        rb = fetchRequiredComponent(Rigidbody.class);
        animator = fetchRequiredComponent(SpriteAnimator.class);
        renderer = fetchRequiredComponent(SpriteRenderer.class);

    }

    @Override
    public void update() {
        if(Math.abs(rb.velocity.getX()) > 0.2){
            renderer.setFlipHorizontally(Math.signum(rb.velocity.getX()) == -1);
            if(Math.abs(rb.velocity.getX())<4f){
                currentAnimation = Assets.Animations.PLAYER_WALK;
            } else {
                currentAnimation = Assets.Animations.PLAYER_RUN;
            }
        }
        else {
            currentAnimation = Assets.Animations.PLAYER_IDLE;
        }
        if(!rb.isGrounded() && rb.velocity.getY()>1){
            currentAnimation = Assets.Animations.PLAYER_FALL;
        }

        if(!currentAnimation.equals(prevAnimation)){
            animator.loadAnimation(currentAnimation);
        }
        prevAnimation =currentAnimation;
    }
}

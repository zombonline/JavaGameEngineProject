package game.components.player;

import core.asset.Assets;
import core.audio.SFXPlayer;
import game.components.Rigidbody;
import game.components.rendering.SpriteAnimator;
import game.components.rendering.SpriteRenderer;
import game.components.core.Component;

import java.util.Random;

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
        getRequiredComponentReferences();
        setUpAnimatorListener();
    }

    @Override
    protected void getRequiredComponentReferences() {
        player = fetchRequiredComponent(Player.class);
        rb = fetchRequiredComponent(Rigidbody.class);
        animator = fetchRequiredComponent(SpriteAnimator.class);
        renderer = fetchRequiredComponent(SpriteRenderer.class);

    }

    private void setUpAnimatorListener(){
        animator.addListener(new SpriteAnimator.AnimatorListener() {
            @Override
            public void onAnimationEvent(String eventKey) {
                switch(eventKey){

                    case "sfx_step":
                        double random = Math.random();
                        if(random>0.5){
                            SFXPlayer.playSound(Assets.SFXClips.PLAYER_RUN_1);
                        } else {
                            SFXPlayer.playSound(Assets.SFXClips.PLAYER_RUN_2);
                        }
                }
            }

            @Override
            public void onAnimationComplete() {

            }
        });
    }

    @Override
    public void update() {
        renderer.setFlipHorizontally(Math.signum(rb.velocity.getX()) == -1);

        if(Math.abs(rb.velocity.getX()) > 0.2 && rb.isGrounded()){
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

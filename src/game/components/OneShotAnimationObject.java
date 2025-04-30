package game.components;

import game.components.core.Component;
import game.components.rendering.SpriteAnimator;
import game.entities.GameObject;

public class OneShotAnimationObject extends Component {
    private SpriteAnimator spriteAnimator;
    private SpriteAnimator.AnimatorListener animatorListener;

    @Override
    public void awake() {
        getRequiredComponentReferences();
        setUpAnimatorListener();
    }

    @Override
    protected void getRequiredComponentReferences() {
        spriteAnimator = fetchRequiredComponent(SpriteAnimator.class);
    }

    private void setUpAnimatorListener() {
        animatorListener = new SpriteAnimator.AnimatorListener() {
            @Override
            public void onAnimationEvent(String eventKey) {}
            @Override
            public void onAnimationComplete() {
                GameObject.destroy(gameObject);
            }
        };
        spriteAnimator.addListener(animatorListener);
    }


    @Override
    public void onDestroy() {
        spriteAnimator.removeListener(animatorListener);
    }

}

package ObjectSystem.Crate.Behaviours;

import Main.GamePanel;
import ObjectSystem.Collider;
import ObjectSystem.Crate.Crate;
import ObjectSystem.GameObject;
import Utility.Vector2;

import java.util.ArrayList;
import java.util.List;

public class MovementBehaviour implements CrateBehavior{
    Vector2 initialPosition;
    Vector2 dir;
    float moveSpeed;
    float moveDistance;
    public boolean active = true;

    public MovementBehaviour(float moveSpeed, float moveDistance, Vector2 dir){
        this.moveSpeed = moveSpeed;
        this.moveDistance = moveDistance;
        this.dir = dir;
    }

    public void awake(Crate crate){
        initialPosition = crate.getGameObject().getTransform().getPosition();
    }
    @Override
    public void update(Crate crate) {
        if(!active){return;}
        GameObject crateGameObject = crate.getGameObject();

        crateGameObject.getTransform().translate(dir.mul(moveSpeed * GamePanel.getDeltaTime()));

        if(Vector2.dist(initialPosition, crateGameObject.getTransform().getPosition()) > moveDistance){
            crateGameObject.getTransform().setPosition(initialPosition.add(dir.mul(moveDistance)));
            dir = dir.invert();
            notifyChangeDirection();
        }
    }

    @Override
    public void onTouchTop(Collider other, Crate crate) {

    }

    @Override
    public void onTouchBottom(Collider other, Crate crate) {

    }

    @Override
    public void onExplosionNearby(Crate crate) {

    }

    public void setMoveDistance(float moveDistance){
        this.moveDistance = moveDistance;
    }

    public interface MovementListener
    {
        void onChangeDirection(Vector2 newDirection);
    }
    private List<MovementBehaviour.MovementListener> listeners = new ArrayList<>();
    public void addListener(MovementBehaviour.MovementListener listener) {
        listeners.add(listener);
    }
    public void removeListener(MovementBehaviour.MovementListener listener) {
        listeners.remove(listener);
    }
    public void notifyChangeDirection() {
        for (MovementBehaviour.MovementListener listener : listeners) {
            listener.onChangeDirection(dir);
        }
    }

}

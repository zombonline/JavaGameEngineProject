package game.components.crate.behaviours;

import game.components.Collider;
import game.components.crate.core.Crate;
import game.components.crate.behaviours.core.CrateBehavior;
import game.entities.GameObject;
import core.utils.Vector2;

import java.util.ArrayList;
import java.util.List;

public class MovementBehaviour implements CrateBehavior {
    Collider collider;

    Vector2 initialPosition;
    Vector2 dir;
    float moveSpeed;
    float moveDistance;
    public boolean active = true;

    public ArrayList<GameObject> objectsOnTop = new ArrayList<>();


    public MovementBehaviour(float moveSpeed, float moveDistance, Vector2 dir){
        this.moveSpeed = moveSpeed;
        this.moveDistance = moveDistance;
        this.dir = dir;
    }

    public void awake(Crate crate){
        collider = crate.getComponent(Collider.class);
        initialPosition = crate.getGameObject().getTransform().getPosition();
        setUpColliderListener();
    }

    private void setUpColliderListener(){
        collider.addListener(new Collider.CollisionListener() {
            @Override
            public void onCollisionEnter(Collider other, Vector2 contactNormal) {}

            @Override
            public void onCollisionExit(Collider other, Vector2 contactNormal) {
                objectsOnTop.remove(other.getGameObject());
            }

            @Override
            public void onCollisionStay(Collider other, Vector2 contactNormal) {}
        });
    }
    @Override
    public void update(Crate crate) {
        if(!active){return;}
        GameObject crateGameObject = crate.getGameObject();

        crateGameObject.getTransform().translate(dir.mul(moveSpeed * main.GamePanel.getDeltaTime()));
        for(GameObject object : objectsOnTop){
            object.getTransform().translate(dir.mul(moveSpeed * main.GamePanel.getDeltaTime()));
        }

        if(Vector2.dist(initialPosition, crateGameObject.getTransform().getPosition()) > moveDistance){
            crateGameObject.getTransform().setPosition(initialPosition.add(dir.mul(moveDistance)));
            dir = dir.invert();
            notifyChangeDirection();
        }
    }

    @Override
    public void onTouchTop(Collider other, Crate crate) {
        crate.getBehavior(MovementBehaviour.class).objectsOnTop.add(other.getGameObject());
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

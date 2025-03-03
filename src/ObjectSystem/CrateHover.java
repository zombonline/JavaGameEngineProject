package ObjectSystem;

import Main.GamePanel;
import Utility.Vector2;

import java.util.ArrayList;
import java.util.List;

public class CrateHover extends Crate{
    Vector2 initialPosition;
    Vector2 dir = Vector2.down;
    float hoverSpeed= 1.5f;
    float hoverDistance = .4f;
    float sinkSpeed = 2.25f;
    float sinkingDistance = 5f;
    boolean isSinking = false;
    List<GameObject> objectsAttached = new ArrayList<GameObject>();

    @Override
    public void awake() {
        super.awake();
        initialPosition = gameObject.transform.getPosition();
    }
    @Override
    public void update() {
        float speed = isSinking ? sinkSpeed : hoverSpeed;
        float distance = isSinking ? sinkingDistance : hoverDistance;
        gameObject.transform.translate(dir.mul(speed* GamePanel.getDeltaTime()));
        for(GameObject attachedObject : objectsAttached){
            attachedObject.transform.translate(dir.add(Vector2.up.mul(0.01f)).mul(speed* GamePanel.getDeltaTime()));
        }
        if(Vector2.dist(initialPosition, gameObject.transform.getPosition()) > distance){
            if (isSinking){
                GameObject.destroy(gameObject);
            } else {
            gameObject.transform.setPosition(initialPosition.add(dir.mul(distance)));
            dir = dir.invert();
            }
        }
    }
    @Override
    public void onCrateEnter(Collider other) {
        if(other.getComponent(Rigidbody.class)!=null){
            objectsAttached.add(other.gameObject);
        }
    }

    @Override
    public void onCrateStay(Collider other) {
        if(other.gameObject.name.equals("Player")){
            double otherBottom = Math.floor(other.getBounds().maxY * 10) / 10;
            double colliderTop = Math.floor(collider.getBounds().minY*10)/10;
            System.out.println("Player Bottom: " + otherBottom);
            System.out.println("Crate Top: " + colliderTop);
            if(otherBottom <= colliderTop){
                if(other.getComponent(Rigidbody.class).velocity.getY() > 0){
                    other.getComponent(Rigidbody.class).velocity.setY(0);
                }
                isSinking = true;
                dir = Vector2.down;
            }
        }
    }

    @Override
    public void onCrateExit(Collider other) {
        if(other.getComponent(Rigidbody.class)!=null){
            objectsAttached.remove(other.gameObject);
        }
    }
}

package ObjectSystem;

import Main.GamePanel;
import Utility.Vector2;

import java.util.ArrayList;
import java.util.List;

public class CrateHover extends Crate{
    Vector2 initialPosition;
    Vector2 dir = Vector2.down;
    float currentSpeed;
    float hoverSpeed= .5f;
    float hoverDistance = .4f;
    float sinkSpeed = 2.25f;
    float sinkingDistance = 5f;
    boolean isSinking = false;
    List<GameObject> objectsAttached = new ArrayList<GameObject>();

    public CrateHover(){
        super(true);
    }
    @Override
    public void awake() {
        super.awake();
        initialPosition = gameObject.transform.getPosition();
    }
    @Override
    public void update() {
        currentSpeed = isSinking ? sinkSpeed : hoverSpeed;
        float distance = isSinking ? sinkingDistance : hoverDistance;
        gameObject.transform.translate(dir.mul(currentSpeed* GamePanel.getDeltaTime()));

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
    public void onCrateTouchTop(Collider other) {
        if(other.getComponent(Rigidbody.class)!=null){
            objectsAttached.add(other.gameObject);
            if(other.gameObject.name.equals("Player")){
                isSinking = true;
                dir = Vector2.down;
            }
        }
    }


    @Override
    public void onCrateStay(Collider other) {
        for(GameObject attachedObject : objectsAttached){
            attachedObject.transform.translate(dir.add(Vector2.up.mul(0.01f)).mul(currentSpeed* GamePanel.getDeltaTime()));
        }
    }

    @Override
    public void onCrateExit(Collider other) {
        if(other.getComponent(Rigidbody.class)!=null){
            objectsAttached.remove(other.gameObject);
        }
    }
}

package ObjectSystem.Crate.Behaviours;

import Main.GamePanel;
import ObjectSystem.Collider;
import ObjectSystem.Crate.Crate;
import ObjectSystem.GameObject;
import Utility.Vector2;

import java.util.ArrayList;
import java.util.List;

public class HoverBehavior implements CrateBehavior{
    Vector2 initialPosition;
    Vector2 dir = Vector2.down;
    float hoverSpeed= .5f;
    float hoverDistance = .4f;
    public boolean active = true;

    public HoverBehavior(float hoverSpeed, float hoverDistance){
        this.hoverSpeed = hoverSpeed;
        this.hoverDistance = hoverDistance;
    }

    public void awake(Crate crate){
        initialPosition = crate.getGameObject().transform.getPosition();
    }
    @Override
    public void update(Crate crate) {
        if(!active){return;}
        GameObject crateGameObject = crate.getGameObject();

        crateGameObject.transform.translate(dir.mul(hoverSpeed* GamePanel.getDeltaTime()));

        if(Vector2.dist(initialPosition, crateGameObject.transform.getPosition()) > hoverDistance){
            crateGameObject.transform.setPosition(initialPosition.add(dir.mul(hoverDistance)));
            dir = dir.invert();
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


}

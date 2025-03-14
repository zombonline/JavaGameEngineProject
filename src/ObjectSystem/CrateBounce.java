package ObjectSystem;

import Utility.Vector2;

public class CrateBounce extends Crate{

    @Override
    public void onCrateEnter(Collider other) {
        System.out.println("Hello!");
        if(other.gameObject.name.equals("Player")){
            double otherBottom = Math.floor(other.getBounds().maxY * 10) / 10;
            double colliderTop = Math.floor(collider.getBounds().minY*10)/10;
            System.out.println("Player Bottom: " + otherBottom);
            System.out.println("Crate Top: " + colliderTop);
            if(otherBottom <= colliderTop){
                other.getComponent(Rigidbody.class).velocity = Vector2.zero;
                other.getComponent(Rigidbody.class).addForce(Vector2.up.mul(.25f));
                GameObject.destroy(gameObject);
            }
        }
    }
}

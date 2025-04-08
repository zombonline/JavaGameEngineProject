package ObjectSystem;

import Main.DebugText;
import Utility.Vector2;

public class Crate extends Component{
    Collider collider;
    Collider.CollisionListener listener;

    boolean breakable;
    public Crate(boolean breakable){
        this.breakable = breakable;
    }
    @Override
    public void awake() {
        SetupCollisionEvents();
    }

    private void SetupCollisionEvents() {
        collider = getComponent(Collider.class);
        if(collider!=null){
            listener = new Collider.CollisionListener() {
                @Override
                public void onCollisionEnter(Collider other) {
                    double otherBottom = Math.floor(other.getBounds().maxY * 10) / 10;
                    double colliderTop = Math.floor(collider.getBounds().minY * 10)/10;
                    DebugText.logTemporarily("CRATE COLLISION: Other Bottom (" + otherBottom + ")" + ", Crate Top (" + colliderTop + ")");
                    if(otherBottom <= colliderTop+0.25f){
                        DebugText.logTemporarily("TOUCHING TOP");
                        onCrateTouchTop(other);
                    }
                    double playerTop = Math.floor(other.getBounds().minY * 10) / 10;
                    double colliderBottom = Math.floor(collider.getBounds().maxY * 10) / 10;
                    if(playerTop >= colliderBottom-0.25f){
                       onCrateTouchBottom(other);
                    }
                }
                @Override
                public void onCollisionExit(Collider other) {
                    onCrateExit(other);
                }
                @Override
                public void onCollisionStay(Collider other) {
                    onCrateStay(other);
                }
            };
            collider.addListener(listener);
        }
    }

    public void onCrateTouchTop(Collider other){
    }
    public void onCrateTouchBottom(Collider other){
    }
    public void onCrateExit(Collider other){}
    public void onCrateStay(Collider other){}
}

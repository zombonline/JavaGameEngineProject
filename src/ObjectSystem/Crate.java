package ObjectSystem;

import Utility.Vector2;

public class Crate extends Component{

    Collider collider;
    Collider.CollisionListener listener;
    @Override
    public void awake() {
        collider = getComponent(Collider.class);
        if(collider!=null){
            listener = new Collider.CollisionListener() {
                @Override
                public void onCollisionEnter(Collider other) {
                    onCrateEnter(other);
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
    public void onCrateEnter(Collider other){}
    public void onCrateExit(Collider other){}
    public void onCrateStay(Collider other){}
}

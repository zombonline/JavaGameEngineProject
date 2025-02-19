package ObjectSystem;

import Main.Main;

public class CameraFollow extends Component{

    Camera camera;
    Transform transform;
    public CameraFollow(){
        camera = Main.camera;
    }

    @Override
    public void awake() {
        transform = getGameObject().transform;
    }

    @Override
    public void update() {
        camera.setPosition(transform.getScreenPosition());
    }
}

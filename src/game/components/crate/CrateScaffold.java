package game.components.crate;

import core.utils.Bounds;
import game.components.Collider;
import game.components.crate.core.Crate;
import game.components.player.Player;
import game.entities.GameObject;

public class CrateScaffold extends Crate {
    GameObject player;
    Collider collider;

    @Override
    public void awake() {
        getRequiredComponentReferences();
    }

    @Override
    protected void getRequiredComponentReferences() {
        collider = fetchRequiredComponent(Collider.class);
    }

    public CrateScaffold(){

    }

    @Override
    public void start() {
        player = GameObject.findFirstObjectByType(Player.class);
    }

    @Override
    public void update() {
        super.update();
        Bounds playerBounds = player.getComponent(Collider.class).getBounds();
        double playerBottom = Math.floor(playerBounds.maxY * 10) / 10;
        Bounds thisBounds = collider.getBounds();
        double thisTop = Math.floor(thisBounds.minY * 10) / 10;
        if (playerBottom < thisTop+0.25f) {
            collider.setIsTrigger(false);
        } else {
            collider.setIsTrigger(true);
        }

    }
}

package game.components.crate;

import core.asset.Assets;
import core.audio.SFXPlayer;
import core.utils.Vector2;
import game.components.CameraFollow;
import game.components.Collider;
import game.components.crate.behaviours.BounceBehavior;
import game.components.crate.behaviours.DestroyedByExplosionBehaviour;
import game.components.crate.behaviours.HitCounterBehavior;
import game.components.crate.core.Crate;
import game.components.player.Player;
import game.entities.GameObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrateTeleport extends Crate {
    Collider collider;

    private String teleportKey;
    private CrateTeleport pairedCrate;
    private GameObject player;
    private boolean cameraSnap = false;

    public CrateTeleport(float bounceStrength) {
        super(true, List.of(
                new BounceBehavior(bounceStrength, true),
                new DestroyedByExplosionBehaviour(),
                new HitCounterBehavior(1)
        ));
    }

    @Override
    public void awake() {
        super.awake();
        getExtraData();
        getRequiredComponentReferences();
        setUpHitCounterListener();
    }

    @Override
    protected void getRequiredComponentReferences() {
        collider = fetchRequiredComponent(Collider.class);
    }

    @Override
    public void start() {
        super.start();
        findPairedCrate();
        player = GameObject.findFirstObjectByType(Player.class);
    }

    private void getExtraData() {
        if(gameObject.hasExtraData("teleportKey")){
            teleportKey = gameObject.getExtraData("teleportKey").toString();
        } else {
            System.out.println("Teleport key not found on crate at: " + getGameObject().getTransform().getPosition().toString());
        }
        if(gameObject.hasExtraData("cameraSnap")){
            cameraSnap = gameObject.getExtraData("cameraSnap").toString().equals("true");
        }
    }

    private void findPairedCrate() {
        for(GameObject otherCrate : GameObject.findAllObjectsByType(CrateTeleport.class)){
            if(otherCrate == this.gameObject){continue;}
            CrateTeleport otherTeleport = otherCrate.getComponent(CrateTeleport.class);
            if(otherTeleport.getTeleportKey().equals(this.teleportKey)){
                pairedCrate = otherTeleport;
                break;
            }
        }
    }

    private void setUpHitCounterListener(){
        getBehavior(HitCounterBehavior.class).addListener(
                new HitCounterBehavior.HitCounterListener() {
                    @Override
                    public void onHit(int current, int start, Collider other) {

                    }

                    @Override
                    public void onHitsReachedZero(Collider other) {
                        Vector2 contactNormal = Collider.getContactNormal(collider, other);
                        player.getTransform().setPosition(pairedCrate.getGameObject().getTransform().getPosition().add(contactNormal.invert()));

                        if(cameraSnap) {player.getComponent(CameraFollow.class).snapToTarget();}
                        getBehavior(HitCounterBehavior.class).active =false;
                        getBehavior(BounceBehavior.class).active = false;

                        if(!pairedCrate.getBehavior(BounceBehavior.class).active){
                            SFXPlayer.playSound(Assets.SFXClips.CRATE_DESTROYED);
                            GameObject.destroy(pairedCrate.gameObject);
                            GameObject.destroy(gameObject);
                        } else{
                            SFXPlayer.playSound(Assets.SFXClips.CRATE_BOUNCE);
                        }
                    }
                }
        );
    }
    public String getTeleportKey(){
        return teleportKey;
    }

    public static Map<String, Object> getDefaultValues(){
        Map<String,Object> defaultValues = new HashMap<>();
        defaultValues.put("bounceStrength", 10f);
        return defaultValues;
    }
}

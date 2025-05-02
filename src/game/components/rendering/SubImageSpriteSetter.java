package game.components.rendering;

import core.asset.AssetLoader;
import core.utils.Vector2;
import game.components.core.Component;
import game.components.core.Transform;
import game.entities.GameObject;

import java.awt.image.BufferedImage;

public class SubImageSpriteSetter extends Component {
    // Component references
    SpriteRenderer spriteRenderer;
    BufferedImage spriteSheetImage, subImage;
    int tileId, tileWidth, tilesAcross;
    public SubImageSpriteSetter(GameObject parentObject){
        String name = parentObject.getExtraData("spriteSheetName").toString();
        tileId = Integer.parseInt(parentObject.getExtraData("tileId").toString());
        try {
            tileWidth = Integer.parseInt(parentObject.getExtraData("tileWidth").toString());
        } catch (Exception e){
            tileWidth = 512;
        }
        spriteSheetImage = AssetLoader.getInstance().getImage("Images."+name);
        tilesAcross = spriteSheetImage.getWidth()/tileWidth;
        subImage = AssetLoader.getInstance().getSubImage("Images."+name, tileWidth*(tileId%tilesAcross),tileWidth*(tileId/tilesAcross),tileWidth,tileWidth);
    }

    @Override
    public void awake() {
        getRequiredComponentReferences();
        getComponent(Transform.class).setScale(new Vector2(tileWidth/512,tileWidth/512));
        spriteRenderer.setSpriteImage(subImage);
    }

    @Override
    protected void getRequiredComponentReferences() {
        spriteRenderer = fetchRequiredComponent(SpriteRenderer.class);
    }
}

package game.components.rendering;

import core.asset.AssetLoader;
import core.utils.Vector2;
import game.components.core.Component;
import game.components.core.Transform;

import java.awt.image.BufferedImage;

public class SubImageSpriteSetter extends Component {
    // Component references
    SpriteRenderer spriteRenderer;
    @Override
    public void awake() {
        getRequiredComponentReferences();
        String name = gameObject.getExtraData("spriteSheetName").toString();
        int tileId = Integer.parseInt(gameObject.getExtraData("tileId").toString());
        int tileWidth;
        try {
            tileWidth = Integer.parseInt(gameObject.getExtraData("tileWidth").toString());
        } catch (Exception e){
            tileWidth = 512;
        }
        getComponent(Transform.class).setScale(new Vector2(tileWidth/512,tileWidth/512));
        BufferedImage spriteSheetImage = AssetLoader.getInstance().getImage("Images."+name);
        int tilesAcross = spriteSheetImage.getWidth()/tileWidth;
        BufferedImage image = AssetLoader.getInstance().getSubImage("Images."+name, tileWidth*(tileId%tilesAcross),tileWidth*(tileId/tilesAcross),tileWidth,tileWidth);
        spriteRenderer.setSpriteImage(image);
    }

    @Override
    protected void getRequiredComponentReferences() {
        spriteRenderer = fetchRequiredComponent(SpriteRenderer.class);
    }
}

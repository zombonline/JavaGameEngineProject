package game.components.rendering;

import core.asset.AssetLoader;
import game.components.core.Component;

import java.awt.image.BufferedImage;

public class SubImageSpriteSetter extends Component {
    // Component references
    SpriteRenderer spriteRenderer;
    @Override
    public void awake() {
        getRequiredComponentReferences();
        String name = gameObject.getExtraData("spriteSheetName").toString();
        int tileId = Integer.parseInt(gameObject.getExtraData("tileId").toString());
        BufferedImage spriteSheetImage = AssetLoader.getInstance().getImage("Images."+name);
        int tilesAcross = spriteSheetImage.getWidth()/512;
        BufferedImage image = AssetLoader.getInstance().getSubImage("Images."+name, 512*(tileId%tilesAcross),512*(tileId/tilesAcross),512,512);
        spriteRenderer.setSpriteImage(image);
    }

    @Override
    protected void getRequiredComponentReferences() {
        spriteRenderer = fetchRequiredComponent(SpriteRenderer.class);
    }
}

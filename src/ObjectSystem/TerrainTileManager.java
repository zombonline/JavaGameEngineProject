package ObjectSystem;

import Main.AssetLoader;
import Main.Assets;

import java.awt.image.BufferedImage;

public class TerrainTileManager extends Component{
    // Component references
    SpriteRenderer spriteRenderer;
    @Override
    public void awake() {
        getRequiredComponentReferences();
        String name = gameObject.getExtraData("spriteSheetName").toString();
        int tileId = Integer.parseInt(gameObject.getExtraData("tileId").toString());
        BufferedImage image = AssetLoader.getInstance().getSubImage("Images."+name, 512*(tileId%7),512*(tileId/7),512,512);
        spriteRenderer.spriteImage = image;
    }

    @Override
    protected void getRequiredComponentReferences() {
        spriteRenderer = fetchRequiredComponent(SpriteRenderer.class);
    }
}

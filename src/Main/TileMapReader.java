package Main;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import ObjectSystem.GameObject;
import Utility.Tile;
import Utility.Vector2;
import org.w3c.dom.*;

import java.io.InputStream;
import java.util.ArrayList;

public class TileMapReader {
    public static ArrayList<GameObject> parse() {
        ArrayList<GameObject> tiles = new ArrayList<>();
        try {
            InputStream file = TileMapReader.class.getResourceAsStream("/Resources/Tilesets/test_level_1.tmx");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);
            document.getDocumentElement().normalize();

            // Get root element
            Element mapElement = document.getDocumentElement();
//            System.out.println("Map Width: " + mapElement.getAttribute("width"));
//            System.out.println("Map Height: " + mapElement.getAttribute("height"));
//            System.out.println("Tile Width: " + mapElement.getAttribute("tilewidth"));
//            System.out.println("Tile Height: " + mapElement.getAttribute("tileheight"));

            // Get all tile layers

            int mapWidth = Integer.parseInt(mapElement.getAttribute("width"));
            int mapHeight = Integer.parseInt(mapElement.getAttribute("height"));
            NodeList layers = document.getElementsByTagName("layer");
            for (int i = 0; i < layers.getLength(); i++) {
                Element layer = (Element) layers.item(i);

                NodeList dataNodes = layer.getElementsByTagName("data");
                if (dataNodes.getLength() > 0) {
                    Element dataElement = (Element) dataNodes.item(0);
                    String[] values = dataElement.getTextContent().trim().split(",");
                    for (int y = 0; y < mapHeight; y++) {
                        for (int x = 0; x < mapWidth; x++) {
                            int value = Integer.parseInt(values[y * mapWidth + x].trim()); // Convert to int
                            GameObject newTile = null;
                                Tile tile = Tile.fromValue(value);
                                if(tile==null){continue;}
                                System.out.println("Resources/Prefabs/"+tile+".json");
                                newTile = PrefabReader.getObject("/Resources/Prefabs/"+tile+".json");
//                            switch (value){
//                                case 1: newTile = PrefabReader.getObject("/Resources/Prefabs/prefab_crate_basic.json");
//                                    break;
//                                case 4: newTile = PrefabReader.getObject("/Resources/Prefabs/prefab_crate_hover.json");
//                                    break;
//                                case 5: newTile = PrefabReader.getObject("/Resources/Prefabs/prefab_crate_basic.json");
//                                    break;
//                                case 8: newTile = PrefabReader.getObject("/Resources/Prefabs/prefab_crate_explosive.json");
//                                    break;
//                                case 15: newTile = PrefabReader.getObject("/Resources/Prefabs/prefab_crate_basic.json");
//                                    break;
//                            }
                            if (newTile == null){continue;}
                            newTile.transform.setPosition(new Vector2(x,y));
                            tiles.add(newTile);
                        }
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Couldn't load level");
        }
        return tiles;
    }
}

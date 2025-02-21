package Main;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import ObjectSystem.GameObject;
import Utility.Vector2;
import org.w3c.dom.*;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.jar.JarEntry;

public class TMXParser {
    public static ArrayList<GameObject> parse() {
        ArrayList<GameObject> tiles = new ArrayList<>();
        try {
            InputStream file = TMXParser.class.getResourceAsStream("/Resources/Level_Test.tmx");
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
                System.out.println("Layer: " + layer.getAttribute("name"));

                NodeList dataNodes = layer.getElementsByTagName("data");
                if (dataNodes.getLength() > 0) {
                    Element dataElement = (Element) dataNodes.item(0);
                    System.out.println("Tile Data: " + dataElement.getTextContent().trim()); // If encoding="csv"
                    String[] values = dataElement.getTextContent().trim().split(",");
                    for (int y = 0; y < mapHeight; y++) {
                        for (int x = 0; x < mapWidth; x++) {
                            int value = Integer.parseInt(values[y * mapWidth + x].trim()); // Convert to int
                            if (value == 1) {
                                System.out.println("1 at (" + x + ", " + y + ")");
                                GameObject newTile = PrefabReader.getObject("prefab_basic_tile.json");
                                newTile.transform.setPosition(new Vector2(x,y));
                                tiles.add(newTile);
                            } else {
                                System.out.println("0 at (" + x + ", " + y + ")");
                                // Perform action for 0
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return tiles;
    }
}

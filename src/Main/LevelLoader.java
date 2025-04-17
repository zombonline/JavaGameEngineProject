package Main;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import ObjectSystem.Crate.Crate;
import ObjectSystem.GameObject;
import Utility.TileValue;
import Utility.Vector2;
import org.w3c.dom.*;

import java.io.InputStream;
import java.util.ArrayList;

public class LevelLoader {
    public static LevelData parse(String levelString) {
        ArrayList<GameObject> gameObjects = new ArrayList<>();
        int breakableCrates = 0;
        int mapWidth = -1;
        int mapHeight = -1;
        try {
            InputStream file = LevelLoader.class.getResourceAsStream(levelString);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);
            document.getDocumentElement().normalize();

            Element mapElement = document.getDocumentElement();

            mapWidth = Integer.parseInt(mapElement.getAttribute("width"));
            mapHeight = Integer.parseInt(mapElement.getAttribute("height"));
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
                            GameObject newObject = null;
                                TileValue tileValue = TileValue.fromValue(value);
                                if(tileValue==null){continue;}
                                newObject = AssetLoader.getInstance().getPrefab("/Resources/Prefabs/" + tileValue + ".json");
                            if (newObject == null){
                                System.out.println("New Object IS NULL");
                                continue;
                            }

                            if(newObject.getComponent(Crate.class)!=null){
                                if(newObject.getComponent(Crate.class).isBreakable()){
                                    breakableCrates++;
                                }
                            }
                            newObject.getTransform().setPosition(new Vector2(x,y));
                            System.out.println("New Object: " + newObject.getName());
                            System.out.println("New Object Memory Location: " + newObject.hashCode());
                            System.out.println("New Object Position: " + newObject.getTransform().getPosition());

                            gameObjects.add(newObject);
                        }
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Couldn't load level");
            e.printStackTrace();
        }
        return new LevelData(levelString, gameObjects, breakableCrates, mapWidth, mapHeight);
    }
}

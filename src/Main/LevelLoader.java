package Main;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import ObjectSystem.GameObject;
import ObjectSystem.NPCDialogueHandler;
import ObjectSystem.SpriteRenderer;
import Utility.Vector2;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LevelLoader {
    static int mapWidth = -1;
    static int mapHeight = -1;
    static HashMap<Integer, String> idToPrefabName;
    public static LevelData parse(String levelString) {
        mapWidth = -1;
        mapHeight = -1;
        ArrayList<GameObject> gameObjects = new ArrayList<>();
        try {
            InputStream tileMapFile = LevelLoader.class.getResourceAsStream(Assets.Tilemaps.LEVEL_TEST);
            InputStream tileSetFile = LevelLoader.class.getResourceAsStream(Assets.Tilesets.TILES);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document tileMapDocument = builder.parse(tileMapFile);
            tileMapDocument.getDocumentElement().normalize();
            Element mapElement = tileMapDocument.getDocumentElement();
            mapWidth = Integer.parseInt(mapElement.getAttribute("width"));
            mapHeight = Integer.parseInt(mapElement.getAttribute("height"));

            NodeList children = mapElement.getChildNodes();
            System.out.println(tileSetFile);
            idToPrefabName = buildIDtoPrefabNameMap(builder, tileSetFile);
            System.out.println("ID to Prefab Name: " + idToPrefabName);
            for (int i = 0; i < children.getLength(); i++) {
                Node node = children.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    switch (element.getTagName()) {
                        case "layer":
                            gameObjects.addAll(parseTileLayer(element));
                            System.out.println("Parsing layer: " + element.getAttribute("name"));
                            break;

                        case "objectgroup":
                            gameObjects.addAll(parseObjectLayer(element));
                            System.out.println("Parsing object group: " + element.getAttribute("name"));
                            break;
                        default:
                            break;
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return new LevelData(levelString, gameObjects, mapWidth, mapHeight);
    }

    private static HashMap<Integer, String> buildIDtoPrefabNameMap(DocumentBuilder builder, InputStream tileSet) throws IOException, SAXException {
        HashMap<Integer, String> idToPrefabName = new HashMap<>();
        System.out.println(tileSet);
        Document tileSetDocument = builder.parse(tileSet);
        tileSetDocument.getDocumentElement().normalize();
        NodeList tileNodes = tileSetDocument.getElementsByTagName("tile");
        for(int i = 0; i < tileNodes.getLength(); i++){
            Element tile = (Element) tileNodes.item(i);
            String tileId = tile.getAttribute("id");
            String prefabName = "";
            NodeList propertyNodes = tile.getElementsByTagName("property");
            for (int j = 0; j < propertyNodes.getLength(); j++) {
                Element propertyElement = (Element) propertyNodes.item(j);
                if ("prefabName".equals(propertyElement.getAttribute("name"))) {
                    prefabName = propertyElement.getAttribute("value");
                }
            }
            idToPrefabName.put(Integer.parseInt(tileId)+1, prefabName);
        }
        return idToPrefabName;
    }

    private static String getProperty(Element element, String propertyName){
        String result = "";
        try{
            NodeList propertyNodes = element.getElementsByTagName("property");
            for (int j = 0; j < propertyNodes.getLength(); j++) {
                Element propertyElement = (Element) propertyNodes.item(j);
                if (propertyName.equals(propertyElement.getAttribute("name"))) {
                    result = propertyElement.getAttribute("value");
                }
            }
        } catch (Exception e){
            System.out.println("Couldn't find property '" + propertyName + "' on '" + element.getTagName()+"'");
        }
        return result;
    }
    private static HashMap<String, String> getPropertiesAsMap(Element element){
        HashMap<String,String> map = new HashMap<>();
        try{
            NodeList propertyNodes = element.getElementsByTagName("property");
            for (int j = 0; j < propertyNodes.getLength(); j++) {
                Element propertyElement = (Element) propertyNodes.item(j);
                map.put(propertyElement.getAttribute("name"), propertyElement.getAttribute("value"));
            }
        } catch (Exception e){
            return null;
        }
        return map;
    }

    private static ArrayList<GameObject> parseTileLayer(Element layer) {
        String parallaxFactorString = getProperty(layer, "parallax");
        float parallaxFactor = parallaxFactorString.isEmpty() ? 1.0f : Float.parseFloat(parallaxFactorString);

        ArrayList<GameObject> gameObjects = new ArrayList<>();
        NodeList dataNodes = layer.getElementsByTagName("data");
        if (dataNodes.getLength() > 0) {
            Element dataElement = (Element) dataNodes.item(0);
            String[] values = dataElement.getTextContent().trim().split(",");
            for (int y = 0; y < mapHeight; y++) {
                for (int x = 0; x < mapWidth; x++) {
                    int tileID = Integer.parseInt(values[y * mapWidth + x].trim());
                    if(tileID == 0){continue;}
                    GameObject newObject = null;
                    String prefabName = idToPrefabName.get(tileID);
                    if(prefabName == null){
                        System.out.println("No prefab found for tile id: " + tileID);
                    }
                    if (prefabName == null) {
                        continue;
                    }
                    newObject = AssetLoader.getInstance().getPrefab("Prefabs."+prefabName);
                    if (newObject == null) {
                        System.out.println("New Object IS NULL");
                        continue;
                    }
                    ApplyParallax(newObject, parallaxFactor);
                    newObject.getTransform().setPosition(new Vector2(x, y));
                    gameObjects.add(newObject);
                }
            }
        }
    return gameObjects;
    }

    private static ArrayList<GameObject> parseObjectLayer(Element objectGroup){
        ArrayList<GameObject> gameObjects = new ArrayList<>();
        NodeList dataNodes = objectGroup.getElementsByTagName("object");
        for(int i = 0; i< dataNodes.getLength(); i++){
            Element item = (Element) dataNodes.item(i);
            int gid = Integer.parseInt(item.getAttribute("gid"));
            String prefabName = idToPrefabName.get(gid);
            GameObject newObject = AssetLoader.getInstance().getPrefab("Prefabs."+prefabName);
            float x = Float.parseFloat(item.getAttribute("x"))/512;
            float y = Float.parseFloat(item.getAttribute("y"))/512;
            newObject.getTransform().setPosition(new Vector2(x,y-1));
            for(Map.Entry entry : getPropertiesAsMap(item).entrySet()){
                System.out.println("Object has " + entry.getKey());
                newObject.insertExtraData(entry.getKey(),entry.getValue());
            }
            gameObjects.add(newObject);
        }
        return gameObjects;
    }

    private static void ApplyParallax(GameObject gameObject, float parallaxFactor){
        if(gameObject.getComponent(SpriteRenderer.class) != null){
            gameObject.getComponent(SpriteRenderer.class).setParallaxFactor(parallaxFactor);
        }
    }
}

package Main;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import ObjectSystem.GameObject;
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
    private static class PrefabReference{
        public String prefabName = "";
        public final HashMap<String,String> data = new HashMap<>();
        public void setPrefabName(String name){this.prefabName=name;}
        public void addData(String key,String val){data.put(key,val);}
    }

    static int mapWidth = -1;
    static int mapHeight = -1;
    static HashMap<Integer, PrefabReference> idToPrefabName;
    public static LevelData parse(String levelString) {
        mapWidth = -1;
        mapHeight = -1;
        ArrayList<GameObject> gameObjects = new ArrayList<>();
        try {
            InputStream tileMapFile = LevelLoader.class.getResourceAsStream("/Resources/Tilesets/AutoTileTest.tmx");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document tileMapDocument = builder.parse(tileMapFile);
            tileMapDocument.getDocumentElement().normalize();
            Element mapElement = tileMapDocument.getDocumentElement();
            mapWidth = Integer.parseInt(mapElement.getAttribute("width"));
            mapHeight = Integer.parseInt(mapElement.getAttribute("height"));

            NodeList children = mapElement.getChildNodes();

            NodeList tileSetFiles = mapElement.getElementsByTagName("tileset");

            idToPrefabName = buildIDtoPrefabNameMap(builder, tileSetFiles);

            printIdToPrefabMap();
            for (int i = 0; i < children.getLength(); i++) {
                Node node = children.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    switch (element.getTagName()) {
                        case "layer":
                            if(element.getAttribute("name").contains("Input")){
                                System.out.println("Skipping input layer");
                                continue;
                            }
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

    private static void printIdToPrefabMap() {
        String printValue = "";
        for(Map.Entry entry : idToPrefabName.entrySet()){
            String prefabName = idToPrefabName.get(entry.getKey()).prefabName;
            printValue+= entry.getKey()+"="+ prefabName +", ";
        }

        System.out.println("ID to Prefab Name: " + printValue);
    }


    private static HashMap<Integer, PrefabReference> buildIDtoPrefabNameMap(DocumentBuilder builder, NodeList tileSets) throws IOException, SAXException {

        HashMap<Integer, PrefabReference> idToPrefabName = new HashMap<>();
        for(int i = 0; i < tileSets.getLength(); i++) {
            Element tileSetElement = (Element) tileSets.item(i);
            int firstgid = Integer.parseInt(tileSetElement.getAttribute("firstgid"));
            String source = tileSetElement.getAttribute("source");
            System.out.println("Tileset: " + source +", " + firstgid);
            InputStream tileSetFile = LevelLoader.class.getResourceAsStream("/Resources/Tilesets/"+source);
            Document tileSetDocument = builder.parse(tileSetFile);
            tileSetDocument.getDocumentElement().normalize();
            NodeList tileNodes = tileSetDocument.getElementsByTagName("tile");
            for (int j = 0; j < tileNodes.getLength(); j++) {
                Element tile = (Element) tileNodes.item(j);
                String tileId = tile.getAttribute("id");
                NodeList propertyNodes = tile.getElementsByTagName("property");
                PrefabReference prefabReference = new PrefabReference();
                prefabReference.addData("tileId", tileId);
                for (int k = 0; k < propertyNodes.getLength(); k++) {
                    Element propertyElement = (Element) propertyNodes.item(k);
                    if ("prefabName".equals(propertyElement.getAttribute("name"))) {
                        prefabReference.setPrefabName(propertyElement.getAttribute("value"));
                    } else {
                        prefabReference.addData(propertyElement.getAttribute("name"), propertyElement.getAttribute("value"));
                    }
                }
                idToPrefabName.put(Integer.parseInt(tileId) + firstgid, prefabReference);
            }
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
                    PrefabReference prefabReference = idToPrefabName.get(tileID);
                    if(prefabReference==null){
                        continue;
                    }
                    if(prefabReference.prefabName.isEmpty()){
                        System.out.println("No prefab found for tile id: " + tileID);
                        continue;
                    }
                    newObject = AssetLoader.getInstance().getPrefab("Prefabs."+prefabReference.prefabName);
                    if (newObject == null) {
                        System.out.println("New Object IS NULL");
                        continue;
                    }
                    ApplyParallax(newObject, parallaxFactor);
                    newObject.getTransform().setPosition(new Vector2(x, y));
                    for(Map.Entry entry : idToPrefabName.get(tileID).data.entrySet()){
                        newObject.insertExtraData(entry.getKey(),entry.getValue());
                    }
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
            String prefabName = idToPrefabName.get(gid).prefabName;
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

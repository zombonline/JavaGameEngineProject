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
    private static int mapWidth;
    private static int mapHeight;
    private static HashMap<Integer, PrefabReference> idToPrefabName;
    public static LevelData parse(String levelString) {
        
        ArrayList<GameObject> levelContents;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            //load tilemap file
            InputStream tileMapFile = LevelLoader.class.getResourceAsStream("/Resources/Tilesets/AutoTileTest.tmx");
            Document tileMapDocument = builder.parse(tileMapFile);
            tileMapDocument.getDocumentElement().normalize();

            //get map element of tilemap file to read map data from
            Element mapElement = tileMapDocument.getDocumentElement();
            //set width and height of current tilemap
            setMapWidthHeight(mapElement);

            //get the elements that list which tilesets the map needs
            NodeList tileSetFiles = mapElement.getElementsByTagName("tileset");
            //send these elements to be parsed so the correct tilesets can be loaded
            idToPrefabName = buildIDtoPrefabNameMap(builder, tileSetFiles);
            //print the tile id -> prefab name map for debugging
            printIdToPrefabMap();

            //get all the children of the mapElement (so we can parse the layers in the order they appear in.
            //this is so if an object layer is sandwiched between two tile layers it will be parsed in that order
            NodeList children = mapElement.getChildNodes();
            levelContents = parseMapChildElements(children);
            return new LevelData(levelString, levelContents, mapWidth, mapHeight);

        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private static ArrayList<GameObject> parseMapChildElements(NodeList children) {
        ArrayList<GameObject> result = new ArrayList<>();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            Element element = (Element) node;
            switch (element.getTagName()) {
                case "layer":
                    if(element.getAttribute("name").contains("Input")){
                        System.out.println("Skipping input layer");
                        continue;
                    }
                    result.addAll(parseTileLayer(element));
                    System.out.println("Parsing layer: " + element.getAttribute("name"));
                    break;
                case "objectgroup":
                    result.addAll(parseObjectLayer(element));
                    System.out.println("Parsing object group: " + element.getAttribute("name"));
                    break;
            }
        }
        return result;
    }

    private static HashMap<Integer, PrefabReference> buildIDtoPrefabNameMap(DocumentBuilder builder, NodeList tileSets) throws IOException, SAXException {
        HashMap<Integer, PrefabReference> result = new HashMap<>();
        for(int i = 0; i < tileSets.getLength(); i++) {
            //turn node into element
            Element tileSetElement = (Element) tileSets.item(i);
            //grab relevant attributes from tile set reference element
            int firstgid = Integer.parseInt(tileSetElement.getAttribute("firstgid"));
            String source = tileSetElement.getAttribute("source");
            //load the tile set file the reference is referring to
            InputStream tileSetFile = LevelLoader.class.getResourceAsStream("/Resources/Tilesets/"+source);
            Document tileSetDocument = builder.parse(tileSetFile);
            tileSetDocument.getDocumentElement().normalize();

            //loop through each tile in the set
            NodeList tileNodes = tileSetDocument.getElementsByTagName("tile");
            for (int j = 0; j < tileNodes.getLength(); j++) {
                Element tile = (Element) tileNodes.item(j);
                //create a new prefab refenrce object (stores the prefab name connected to the tile plus any extra
                // data tiles (not instances of tiles) might hold.
                PrefabReference prefabReference = new PrefabReference();
                //get the id number of the tile (this is then later added to the firstgid attribute to get the sets ID inside the tile map
                String tileId = tile.getAttribute("id");
                prefabReference.addData("tileId", tileId);
                //read the properties to store a prefab name adding any extra data to the prefab reference data map.
                for(Map.Entry<String,String> entry: getPropertiesAsMap(tile).entrySet()){
                    if(entry.getKey().equals("prefabName")){
                        prefabReference.setPrefabName(entry.getValue());
                    }
                    prefabReference.addData(entry.getKey(), entry.getValue());
                }
                result.put(Integer.parseInt(tileId) + firstgid, prefabReference);
            }
        }
        return result;
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
                String value = !propertyElement.hasAttribute("value") ? propertyElement.getTextContent() : propertyElement.getAttribute("value");
                map.put(propertyElement.getAttribute("name"), value);
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
                    newObject.insertExtraData("parallaxFactor", parallaxFactor);
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
            if(!item.hasAttribute("gid")){
                System.out.println("Item " + item.getTagName() + "doesn't have gid");
                continue;
            }
            int gid = Integer.parseInt(item.getAttribute("gid"));
            String prefabName = idToPrefabName.get(gid).prefabName;
            if(prefabName.isEmpty()){
                continue;
            }
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
    private static void setMapWidthHeight(Element mapElement) {
        mapWidth = Integer.parseInt(mapElement.getAttribute("width"));
        mapHeight = Integer.parseInt(mapElement.getAttribute("height"));
    }
    private static void printIdToPrefabMap() {
        String printValue = "";
        for(Map.Entry entry : idToPrefabName.entrySet()){
            String prefabName = idToPrefabName.get(entry.getKey()).prefabName;
            printValue+= entry.getKey()+"="+ prefabName +", ";
        }

        System.out.println("ID to Prefab Name: " + printValue);
    }
}

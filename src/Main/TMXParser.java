package Main;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;

import java.io.File;
import java.io.InputStream;
import java.util.jar.JarEntry;

public class TMXParser {
    public static void parse() {
        try {
            InputStream file = TMXParser.class.getResourceAsStream("/Resources/Level_Test.tmx");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);
            document.getDocumentElement().normalize();

            // Get root element
            Element mapElement = document.getDocumentElement();
            System.out.println("Map Width: " + mapElement.getAttribute("width"));
            System.out.println("Map Height: " + mapElement.getAttribute("height"));
            System.out.println("Tile Width: " + mapElement.getAttribute("tilewidth"));
            System.out.println("Tile Height: " + mapElement.getAttribute("tileheight"));

            // Get all tile layers
            NodeList layers = document.getElementsByTagName("layer");
            for (int i = 0; i < layers.getLength(); i++) {
                Element layer = (Element) layers.item(i);
                System.out.println("Layer: " + layer.getAttribute("name"));

                // Get the tile data
                NodeList dataNodes = layer.getElementsByTagName("data");
                if (dataNodes.getLength() > 0) {
                    Element dataElement = (Element) dataNodes.item(0);
//                    System.out.println("Tile Data: " + dataElement.getTextContent().trim()); // If encoding="csv"
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

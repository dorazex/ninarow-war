package XmlLoader;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;


public class XmlLoader {

    public static HashMap<String, Object> getGameInitParameters(InputStream is) throws ConfigXmlException {

//        if (!filePath.toLowerCase().endsWith(".xml")) throw new ConfigXmlException("File is not an XML file");

        HashMap<String, Object> parametersMap = new HashMap<>();
        try {

//            File fXmlFile = new File(filePath);
//            if (!fXmlFile.exists()) throw new ConfigXmlException("File does not exist");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);
            doc.getDocumentElement().normalize();

            // GameType
            Element gameTypeElement = (Element) doc.getElementsByTagName("GameType").item(0);
            String gameType = gameTypeElement.getTextContent();
            // Game global properties
            NodeList gameNodeList = doc.getElementsByTagName("Game");
            Element gameElement = (Element) gameNodeList.item(0);
            Element boardElement = (Element) gameElement.getElementsByTagName("Board").item(0);
            Element variantElement = (Element) gameElement.getElementsByTagName("Variant").item(0);
            String variant = variantElement.getTextContent();
            String target = gameElement.getAttribute("target");
            String rows = boardElement.getAttribute("rows");
            String columns = boardElement.getAttribute("columns");
            // Players
            NodeList dynamicPlayersNodeList = (NodeList) doc.getElementsByTagName("DynamicPlayers");
            if (dynamicPlayersNodeList == null){
                throw new ConfigXmlException("Dynamic Players must be declared in the configuration file");
            }
            Element dynamicPlayersElement = (Element) dynamicPlayersNodeList.item(0);
            String gameTitle = dynamicPlayersElement.getAttribute("game-title");
            String totalPlayersString = dynamicPlayersElement.getAttribute("total-players");


            if (!variant.equals("Regular") && !variant.equals("Circular") && !variant.equals("Popout")){
                throw new ConfigXmlException("Game variant is not supported");
            }
            parametersMap.put("variant", variant);
            parametersMap.put("game-title", gameTitle);
            try {
                parametersMap.put("target", Integer.parseInt(target));
                parametersMap.put("rows", Integer.parseInt(rows));
                parametersMap.put("columns", Integer.parseInt(columns));
                Integer totalPlayers = Integer.parseInt(totalPlayersString);
                if (totalPlayers < 2 || totalPlayers > 6)
                    throw new ConfigXmlException("Players count must be between 2 to 6");
                parametersMap.put("total-players", totalPlayers);
            } catch (Exception e){
                throw new ConfigXmlException("Target, rows, columns and total players attributes must be integers");
            }

            if (!(((Integer)parametersMap.get("rows")) >= 5 && ((Integer)parametersMap.get("rows")) <= 50)){
                throw new ConfigXmlException("Rows value must be in range 5-50 inclusive");
            }
            if (!(((Integer)parametersMap.get("columns")) >= 6 && ((Integer)parametersMap.get("columns")) <= 60)){
                throw new ConfigXmlException("Columns value must be in range 6-60 inclusive");
            }
            if (!(((Integer)parametersMap.get("target")) < ((Integer)parametersMap.get("columns")) &&
                    ((Integer)parametersMap.get("target")) < ((Integer)parametersMap.get("rows")))){
                throw new ConfigXmlException("Target value must be less than both rows and columns value");
            }


        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
        return parametersMap;
    }

}

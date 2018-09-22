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
import java.util.ArrayList;
import java.util.HashMap;


public class XmlLoader {

    public static HashMap<String, Object> getGameInitParameters(String filePath) throws ConfigXmlException {

        if (!filePath.toLowerCase().endsWith(".xml")) throw new ConfigXmlException("File is not an XML file");

        HashMap<String, Object> parametersMap = new HashMap<>();
        try {

            File fXmlFile = new File(filePath);
            if (!fXmlFile.exists()) throw new ConfigXmlException("File does not exist");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();

            // GameType
            Element gameTypeElement = (Element) doc.getElementsByTagName("GameType").item(0);
            String gameType = gameTypeElement.getTextContent();
            // Game global properties
            NodeList gameNodeList = doc.getElementsByTagName("Game");
            Element gameElement = (Element) gameNodeList.item(0);
            Element boardElement = (Element) gameElement.getElementsByTagName("GameEngine.Board").item(0);
            Element variantElement = (Element) gameElement.getElementsByTagName("Variant").item(0);
            String variant = variantElement.getTextContent();
            String target = gameElement.getAttribute("target");
            String rows = boardElement.getAttribute("rows");
            String columns = boardElement.getAttribute("columns");
            // Players
            NodeList playersNodeList = (NodeList) doc.getElementsByTagName("Players");
            if (playersNodeList == null || playersNodeList.getLength() == 0){
                throw new ConfigXmlException("Players must be declared in the configuration file");
            }
            Element playersElement = (Element) playersNodeList.item(0);
            NodeList playersTempList = playersElement.getChildNodes();
            ArrayList<Node> playersList = new ArrayList<>();
            for (int i = 0; i < playersTempList.getLength(); i++) {
                Node node = playersTempList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE){
                    playersList.add(node);
                }
            }
            ArrayList<HashMap<String, String>> playersMap = new ArrayList<>();
            if (playersList != null) {
                int length = playersList.size();
                if (length < 2 || length > 6 ){
                    throw new ConfigXmlException("Players count must be between 2 to 6");
                }
                for (int i = 0; i < length; i++) {
                    if (playersList.get(i).getNodeType() == Node.ELEMENT_NODE) {
                        Element el = (Element) playersList.get(i);
                        String id = el.getAttribute("id");
                        String name = el.getElementsByTagName("Name").item(0).getTextContent();
                        String type = el.getElementsByTagName("Type").item(0).getTextContent();
                        HashMap<String, String> playerPropertiesMap = new HashMap<>();
                        playerPropertiesMap.put("id", id);
                        playerPropertiesMap.put("name", name);
                        playerPropertiesMap.put("type", type);
                        playersMap.add(playerPropertiesMap);
                    }
                }
            } else {
                throw new ConfigXmlException("Players count must be between 2 to 6");
            }

            for (int i = 0; i < playersMap.size(); i++) {
                for (int j = 0; j < playersMap.size(); j++) {
                    if(j!=i){
                        if (playersMap.get(i).get("id").equals(playersMap.get(j).get("id")))
                            throw new ConfigXmlException("Each player must have a unique ID");
                    }
                }
            }

            if (!variant.equals("Regular") && !variant.equals("Circular") && !variant.equals("Popout")){
                throw new ConfigXmlException("Game variant is not supported");
            }
            parametersMap.put("variant", variant);
            parametersMap.put("players", playersMap);
            try {
                parametersMap.put("target", Integer.parseInt(target));
                parametersMap.put("rows", Integer.parseInt(rows));
                parametersMap.put("columns", Integer.parseInt(columns));
            } catch (Exception e){
                throw new ConfigXmlException("Target, rows and columns attributes must be integers");
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import java.util.HashMap;

// Parses the network from the XML format
public class ParserXML {
    private static String NodesTagName = "VARIABLE";
    private static String DefinitionTagName = "DEFINITION";

    // Method that parses given Bayesian network from XML file and returns HashMap that contains name of the node as a key and 
    // object of the node with its variables as a value
    public static HashMap<String, NetworkNode> parseXML(File file) {

        // Hash Map that stores parsed Bayesian Network
        HashMap<String, NetworkNode> network = new HashMap<String, NetworkNode>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);

            // Both variableList and definitionList have the same length and index i coresponds to the same node
            // Getting the name and values of each node in the network
            NodeList variableList = doc.getElementsByTagName(NodesTagName);
            // Getting probabilities - prior and conditional probability tables for each node
            NodeList definitionList = doc.getElementsByTagName(DefinitionTagName);
            for (int i = 0; i < variableList.getLength(); i++) {
                Node v = variableList.item(i);
                Node d = definitionList.item(i);

                if (v.getNodeType() == Node.ELEMENT_NODE && d.getNodeType() == Node.ELEMENT_NODE) {
                    Element variable = (Element) v;
                    NodeList varList = variable.getChildNodes();
                    Element definition = (Element) d;
                    NodeList defList = definition.getChildNodes();

                    // Varibles of one node declared and initiated
                    String name = "";
                    String value1 = "";
                    String value2 = "";
                    String probaTable = "";
                    ArrayList<String> parents = new ArrayList<String>();

                    System.out.println("Node: " + i);
                    // Geting name and values of the node
                    for (int j = 0; j < varList.getLength(); j++) {
                        Node n = varList.item(j);
                        if(n.getNodeType()==Node.ELEMENT_NODE) {
                            Element node = (Element) n;
                            if (j == 1) {
                                name = node.getTextContent();
                                System.out.println("Name = " + name);
                            } else if (j == 3) {
                                // Event value 1
                                value1 = node.getTextContent();
                                System.out.println("Value1 = " + value1);
                            } else if (j == 5) {
                                // Event value 2
                                value2 = node.getTextContent();
                                System.out.println("Value2 = " + value2);
                            }
                        }
                    }

                    // Getting parents and probabilities of the node
                    // k starts at 2 because 1 is a name of the node and it was already obtained from varList
                    for (int k = 2; k < defList.getLength(); k++) {
                        Node n = defList.item(k);
                        if(n.getNodeType()==Node.ELEMENT_NODE) {
                            Element node = (Element) n;
                            // This gives last children which is always probability table
                            if (k == defList.getLength() - 2) {
                                // Event value 1
                                probaTable = node.getTextContent();
                                System.out.println("Probability table = " + probaTable);
                            // Otheres are "GIVEN" -> parent nodes
                            } else {
                                // Event value 2
                                String parent = node.getTextContent();
                                parents.add(parent);
                            }
                        }
                    }
                    System.out.println("Parents = " + parents);
                    System.out.println();

                    // Converting String probability table to Float array list
                    String[] splittedProba = probaTable.split(" ");
                    ArrayList<Float> probability = new ArrayList<Float>();
                    for(int index = 0; index < splittedProba.length; index++) {
                        float num = Float.parseFloat(splittedProba[index]);
                        probability.add(num);
                    }

                    // Creates a node with its variables and adds it into the network HashMap that stores the whole network
                    NetworkNode newNode = new NetworkNode(name, value1, value2, parents, probability);
                    network.put(name, newNode);
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return network;
    }
}

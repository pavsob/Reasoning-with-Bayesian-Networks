import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class A4main {
    
    public static void main(String[] args) {
        // Specify the filename
        File file1 = new File("networks/" + args[0] + ".xml");
        System.out.println("##### First network to merge #####");
        HashMap<String, NetworkNode> network1 = ParserXML.parseXML(file1);

        // Specify the filename
        File file2 = new File("networks/" + args[1] + ".xml");
        System.out.println("##### Second network to merge #####");
        HashMap<String, NetworkNode> network2 = ParserXML.parseXML(file2);

        System.out.println("##### Information about the merging #####");
        HashMap<String, NetworkNode> mergedNetwork = MergeBN.mergeNetworks(network1, network2);
        System.out.println();
        System.out.println("##### Final merged network #####");
        ArrayList<NetworkNode> mergedNodes = new ArrayList<NetworkNode>(mergedNetwork.values());
        for (int i = 0; i < mergedNodes.size(); i++ ) {
            System.out.println();
            System.out.println("Node: " + i);
            System.out.println("Name = " + mergedNodes.get(i).getName());
            System.out.println("Value1 = " + mergedNodes.get(i).getValue1());
            System.out.println("Value2 = " + mergedNodes.get(i).getValue2());
            System.out.println("Probability table = " + mergedNodes.get(i).getProbability());
            System.out.println("Parents = " + mergedNodes.get(i).getParents());
        }
    }
}

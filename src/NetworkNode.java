import java.util.ArrayList;

// Class that describes the node in the network
public class NetworkNode {

    private String name;
    private String value1;
    private String value2;
    private ArrayList<String> parents = new ArrayList<String>();
    private ArrayList<Float> probability = new ArrayList<Float>();


    public NetworkNode(String name, String value1, String value2, ArrayList<String> parents, ArrayList<Float> probability) {
        this.name = name;
        this.value1 = value1;
        this.value2 = value2;
        this.parents = parents;
        this.probability = probability;
    }

    public ArrayList<String> getParents() {
        return parents;
    }

    public ArrayList<Float> getProbability() {
        return probability;
    }

    public String getValue1() {
        return value1;
    }

    public String getValue2() {
        return value2;
    }

    public String getName() {
        return name;
    }
}
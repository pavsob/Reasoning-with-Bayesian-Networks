import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class MergeBN {

    public static HashMap<String, NetworkNode> mergeNetworks(HashMap<String, NetworkNode> network1, HashMap<String, NetworkNode> network2) {

        // Merged network that is returned by this function
        HashMap<String, NetworkNode> mergedNetwork = new HashMap<String, NetworkNode>();

        // Categorise nodes into categories - notIntersection, internal and external
        System.out.println("## Algorithm categorizes the nodes: ");
        ArrayList<Set<String>> categorised = categoriseNodes(network1, network2);
        Set<String> notIntersection = categorised.get(0);
        Set<String> Internal = categorised.get(1);
        Set<String> External = categorised.get(2);
        System.out.println("Not intersection nodes: " + notIntersection);
        System.out.println("Internal nodes: " + Internal);
        System.out.println("External nodes: " + External);

        // Treating non-intersection nodes - taking the values from their previous networks
        System.out.println("## Algorithm puts not intersection nodes into the merged network...");
        for(String notI : notIntersection){
            if (network1.keySet().contains(notI)) {
                mergedNetwork.put(notI, network1.get(notI));
            } else if (network2.keySet().contains(notI)) {
                mergedNetwork.put(notI, network2.get(notI));
            }
        }

        // DELETE rule for Internal nodes
        // The probabilities are transfered to the merged network from the previous network according to the delete rule
        for(String inter : Internal) {
            System.out.println("## Algorithm applies the delete rule to internal nodes...");
            ArrayList<String> parents1 = new ArrayList<String>(network1.get(inter).getParents());
            ArrayList<String> parents2 = new ArrayList<String>(network2.get(inter).getParents());
            // Removing all parents that are in intersection, so we are left only with parents that are not in intersection
            parents1.removeAll(Internal);
            parents1.removeAll(External);
            parents2.removeAll(Internal);
            parents2.removeAll(External);
            // When the parents are not empty it means they are not entirely contained in the intersection
            if (parents1.isEmpty() && !parents2.isEmpty()) {
                mergedNetwork.put(inter, network2.get(inter));
                System.out.println("Keeps this internal node: " + inter + " with its parents from the second network");
            } else if (!parents1.isEmpty() && parents2.isEmpty()) {
                mergedNetwork.put(inter, network1.get(inter));
            } else {
                if (parents1.size() > parents2.size()) {
                    mergedNetwork.put(inter, network1.get(inter));
                    System.out.println("Keeps this internal node: " + inter + " with its parents from the first network");
                } else {
                    mergedNetwork.put(inter, network2.get(inter));
                    System.out.println("Keeps this internal node: " + inter + " with its parents from the first network");
                }
            }
        }

        // External Nodes
        for(String ext : External) {
            System.out.println("## Algorithm deals with external nodes...");
            ArrayList<String> parents1 = new ArrayList<String>(network1.get(ext).getParents());
            ArrayList<String> parents2 = new ArrayList<String>(network2.get(ext).getParents());
            // New probability calculated
            ArrayList<Float> newProb = new ArrayList<Float>();
            // ArrayList of new parents - order of parents is important to align with probabilities
            ArrayList<String> listNewParents = new ArrayList<String>();

            System.out.println("Inspects this external node: " + ext);
            System.out.println("It merges parents from both networks: "+ parents1 + parents2);
            // It finds out how many parents each network has and then it applies suitable pattern of indexis for calculating conditional probability
            if (parents1.size()==1 && parents2.size() == 1) {
                // This means that the table from each network has two lines; therefore, the new one will have 2^2=4 -> four rows
                // (because there are four different combinations of T/F)
                System.out.println("External node - " + ext + " with one parent from each network");
                // This is pattern of indexes that are used to calculate each row of the new probability table
                int[][] pattern = {{0, 0}, {0, 2}, {2, 0}, {2, 2}};
                // It is important to keep correct order of the parents, bacause of true tables
                listNewParents.addAll(parents1);
                listNewParents.addAll(parents2);
                newProb = countNewProba(pattern, network1, network2, ext);
                System.out.println("Finally counts conditional probability: " + newProb);
            } else if(parents1.size()==2 && parents2.size() == 1) {
                int[][] pattern = {{0, 0}, {0, 2}, {2, 0}, {2, 2}, {4, 0}, {4, 2}, {6, 0}, {6, 2}};
                // It is important to keep correct order of the parents, bacause of true tables (according to the pattern)
                listNewParents.addAll(parents1);
                listNewParents.addAll(parents2);
                newProb = countNewProba(pattern, network1, network2, ext);
                System.out.println("Finally counts conditional probability: " + newProb);
            } else if(parents2.size()==2 && parents1.size() == 1) {
                int[][] pattern = {{0, 0}, {0, 2}, {2, 0}, {2, 2}, {4, 0}, {4, 2}, {6, 0}, {6, 2}};
                // It is important to keep correct order of the parents, bacause of true tables (according to the pattern)
                listNewParents.addAll(parents2);
                listNewParents.addAll(parents1);
                newProb = countNewProba(pattern, network2, network1, ext);
                System.out.println("Finally counts conditional probability: " + newProb);
            } else if(parents1.size()==2 && parents2.size() == 2) {
                int[][] pattern = {{0,0},{0,2},{0,4},{0,6},{2,0},{2,2},{2,4},{2,6},{4,0},{4,2},{4,4},{4,6},{6,0},{6,2},{6,4},{6,6}};
                // It is important to keep correct order of the parents, bacause of true tables (according to the pattern)
                listNewParents.addAll(parents1);
                listNewParents.addAll(parents2);
                newProb = countNewProba(pattern, network1, network2, ext);
                System.out.println("Finally counts conditional probability: " + newProb);
            }  else if(parents1.size()==3 && parents2.size() == 2) {
                int[][] pattern = {{0,0},{0,2},{0,4},{0,6},{2,0},{2,2},{2,4},{2,6},{4,0},{4,2},{4,4},{4,6},{6,0},{6,2},{6,4},{6,6},
                {8,0},{8,2},{8,4},{8,6},{10,0},{10,2},{10,4},{10,6},{12,0},{12,2},{12,4},{12,6},{14,0},{14,2},{14,4},{14,6}};
                // It is important to keep correct order of the parents, bacause of true tables (according to the pattern)
                listNewParents.addAll(parents1);
                listNewParents.addAll(parents2);
                newProb = countNewProba(pattern, network1, network2, ext);
                System.out.println("Finally counts conditional probability: " + newProb);
            } else if(parents2.size()==3 && parents1.size() == 2) {
                int[][] pattern = {{0,0},{0,2},{0,4},{0,6},{2,0},{2,2},{2,4},{2,6},{4,0},{4,2},{4,4},{4,6},{6,0},{6,2},{6,4},{6,6},
                {8,0},{8,2},{8,4},{8,6},{10,0},{10,2},{10,4},{10,6},{12,0},{12,2},{12,4},{12,6},{14,0},{14,2},{14,4},{14,6}};
                // It is important to keep correct order of the parents, bacause of true tables (according to the pattern)
                listNewParents.addAll(parents2);
                listNewParents.addAll(parents1);
                newProb = countNewProba(pattern, network2, network1, ext);
                System.out.println("Finally counts conditional probability: " + newProb);
            } else {
                System.out.println("This algorithm cannot resolve external nodes with more than five nodes after the merge -> this node cannot be resolved: " + ext);
            }
            // Adding external node with new probabilities and new parents to the merged network
            NetworkNode newNode = new NetworkNode(ext, network1.get(ext).getValue1(), network1.get(ext).getValue2(), listNewParents, newProb);
            mergedNetwork.put(ext, newNode);
        }

        return mergedNetwork;
    }

    // This function takes pattern of indexes that were derived on paper and calculates new probability accordingly
    private static ArrayList<Float> countNewProba(int[][] pattern, HashMap<String, NetworkNode> network1, HashMap<String, NetworkNode> network2, String ext) {
        ArrayList<Float> prob1 = new ArrayList<Float>(network1.get(ext).getProbability());
        ArrayList<Float> prob2 = new ArrayList<Float>(network2.get(ext).getProbability());
        // New probability calculated
        ArrayList<Float> newProb = new ArrayList<Float>();
        for (int[] pat : pattern) {
            // True (first) column - (it does not always has to be True/False value)
            float res1 = prob1.get(pat[0]) + prob2.get(pat[1]) - prob1.get(pat[0])*prob2.get(pat[1]);
            // False (second) column - here it plus one because that is always one index higher for False column
            float res2 = prob1.get(pat[0]+1) + prob2.get(pat[1]+1) - prob1.get(pat[0]+1)*prob2.get(pat[1]+1);
            // For normalized result
            float normalize = res1+res2;
            newProb.add(res1/normalize);
            newProb.add(res2/normalize);
        }
        return newProb;
    }

    private static ArrayList<Set<String>> categoriseNodes(HashMap<String, NetworkNode> network1, HashMap<String, NetworkNode> network2) {

        // ArrayList of categorised nodes
        ArrayList<Set<String>> categorised = new ArrayList<Set<String>>();

        // Step1 - nodes of both networks are merged
        Set<String> mergedNodes = new HashSet<>();
        mergedNodes.addAll(network1.keySet());
        mergedNodes.addAll(network2.keySet());

        // Step2 - Finds not intersection nodes in each network
        Set<String> notIntersectionNet2 = new HashSet<>(mergedNodes);
        notIntersectionNet2.removeAll(network1.keySet());
        Set<String> notIntersectionNet1 = new HashSet<>(mergedNodes);
        notIntersectionNet1.removeAll(network2.keySet());

        // Step3 - Gets nodes that are not in intersection
        Set<String> notIntersection = new HashSet<>();
        notIntersection.addAll(notIntersectionNet1);
        notIntersection.addAll(notIntersectionNet2);

        // Step4 -Nodes that are in intersection
        Set<String> Intersection = new HashSet<>(mergedNodes);
        Intersection.removeAll(notIntersection);

        // Step5 Get internal nodes
        Set<String> Internal = new HashSet<>();
        for (String n : Intersection) {
            // Extracting parents for the node from each network
            ArrayList<String> parents1 = new ArrayList<String>(network1.get(n).getParents());
            ArrayList<String> parents2 = new ArrayList<String>(network2.get(n).getParents());

            // Finding out whether the parents of the given node are entirely contained in the intersection
            parents1.removeAll(Intersection);
            parents2.removeAll(Intersection);
            if (parents1.isEmpty() || parents2.isEmpty()) {
                Internal.add(n);
            }
        }

        // Step6 Get external nodes
        Set<String> External = new HashSet<>(Intersection);
        External.removeAll(Internal);

        categorised.add(notIntersection);
        categorised.add(Internal);
        categorised.add(External);
        return categorised;
    }
}
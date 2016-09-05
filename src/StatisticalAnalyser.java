import rbr.Region;
import util.Edge;
import util.Graph;
import util.Path;
import util.Vertex;

import java.util.*;

public class StatisticalAnalyser {

    private Graph graph;
    private Map<Vertex, ArrayList<Region>> regionsForVertex;

    public StatisticalAnalyser(Graph graph, Map<Vertex, ArrayList<Region>> regionsForVertex) {
        this.graph = graph;
        this.regionsForVertex = regionsForVertex;
    }
    // Link weight stats [0] - mean / [1] - standard deviation
    public double[] linkWeightStats() {

        double mean = averageLinkWeight();

        double temp = 0.0;
        for (Edge link : graph.getEdges())
            temp += (link.weight() - mean) * (link.weight() - mean);

        double variance = (temp / (double) (graph.getEdges().size()));
        // size-1 for sample. We have population
        double standardVariation = Math.sqrt(variance);

        double[] stats = new double[2];
        stats[0] = mean;
        stats[1] = standardVariation;
        return stats;
    }

    private double averageLinkWeight() {
        double accumulatedWeight = 0.0;
        for (Edge link : graph.getEdges())
            accumulatedWeight += link.weight();
        return accumulatedWeight / (double) graph.getEdges().size();
    }

    // Calculates the regions stats - [0] - Max / [1] - Min / [2] - Average
    public double[] getRegionsStats() {

        double[] stats = new double[3];
        double average;
        List<Integer> regSizes = new ArrayList<>();

        for (Vertex r : graph.getVertices()) {
            regSizes.add(regionsForVertex.get(r).size());
        }
        Collections.sort(regSizes);

        int sum = 0;
        for (int size : regSizes) {
            sum += size;
        }
        average = sum / regSizes.size();

        stats[0] = (double) regSizes.get(regSizes.size() - 1);
        stats[1] = (double) regSizes.get(0);
        stats[2] = (double) average;
        return stats;
    }

    public double averageLinkWeight(ArrayList<ArrayList<Path>> paths) {
        double totalOfLoadedEdges = 0;
        for(ArrayList<Path> alp : paths) {
            Path path = alp.get(0);
            totalOfLoadedEdges += (double) path.size() - 1.0;
        }
        return totalOfLoadedEdges / (double)graph.getEdges().size();
    }

    public double averagePathWeight(ArrayList<ArrayList<Path>> paths) {
        int AccumulatedPathLength = 0;
        for(ArrayList<Path> alp : paths)
            AccumulatedPathLength += (alp.get(0).size()-1);
        double AccumulatedPathWeight = (double) AccumulatedPathLength * averageLinkWeight(paths);
        return AccumulatedPathWeight /(double)paths.size();
    }

    public double[] pathWeightStats(ArrayList<ArrayList<Path>> paths) {
        double[] stats = new double[2];
        double acc = 0;
        int nPaths = 0;
        for(ArrayList<Path> alp : paths) {
            nPaths += alp.size();
            for(Path path: alp)
                acc += path.getWeight();
        }
        stats[0] = acc/(double)nPaths; // media

        acc = 0; double dev;
        for(ArrayList<Path> alp : paths) {
            for(Path path: alp) {
                dev = path.getWeight()-stats[0];
                acc += dev*dev;
            }
        }
        stats[1] = Math.sqrt(acc/(double)nPaths); // desvio padrao
        return stats;
    }

    public double[] pathNormWeightStats(ArrayList<ArrayList<Path>> paths) {
        double[] stats = new double[2];
        double acc = 0;
        int nPaths = 0;
        for(ArrayList<Path> alp : paths) {
            nPaths += alp.size();
            for(Path path: alp)
                acc += path.getWeight()/(double)(path.size()-1);
        }
        stats[0] = acc/(double)nPaths; // media

        acc = 0; double dev;
        for(ArrayList<Path> alp : paths) {
            for(Path path: alp) {
                dev = stats[0]-path.getWeight()/(double)(path.size()-1);
                acc += dev*dev;
            }
        }
        stats[1] = Math.sqrt(acc/(double)nPaths); // desvio padrao
        return stats;
    }
}

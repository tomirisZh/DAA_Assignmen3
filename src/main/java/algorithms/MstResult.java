package algorithms;

import graph.Graph;

import java.util.List;

public class MstResult {
    public final long totalWeight;
    public final List<Graph.Edge> edges;
    public final int components;

    public MstResult(long totalWeight, List<Graph.Edge> edges, int components) {
        this.totalWeight = totalWeight;
        this.edges = edges;
        this.components = components;
    }
}
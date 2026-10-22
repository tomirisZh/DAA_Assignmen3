package algorithms;

import graph.Graph;
import metrics.Metrics;

import java.util.Arrays;

public final class DagShortestPath {
    private DagShortestPath() {
    }

    public static PathResult run(Graph g, Metrics m) {
        TopoResult topo = Kahn.run(g, new Metrics());
        if (topo.hasCycle) {
            throw new IllegalArgumentException("graph has a cycle");
        }
        int n = g.n();
        long[] dist = new long[n];
        int[] parent = new int[n];
        Arrays.fill(dist, Long.MAX_VALUE);
        Arrays.fill(parent, -1);
        boolean[] hasIncoming = new boolean[n];
        for (Graph.Edge e : g.edges()) {
            hasIncoming[e.to] = true;
        }
        for (int v = 0; v < n; v++) {
            if (!hasIncoming[v]) {
                dist[v] = 0;
            }
        }
        for (int u : topo.order) {
            for (Graph.Edge e : g.adj(u)) {
                m.edgeScans++;
                m.comparisons++;
                if (dist[u] + e.weight < dist[e.to]) {
                    dist[e.to] = dist[u] + e.weight;
                    parent[e.to] = u;
                }
            }
        }
        long sum = 0;
        for (long d : dist) {
            sum += d;
        }
        return new PathResult(dist, parent, sum);
    }
}
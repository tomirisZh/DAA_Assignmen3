package algorithms;

import graph.Graph;
import metrics.Metrics;

import java.util.ArrayList;
import java.util.List;

public final class Kruskal {
    private Kruskal() {
    }

    public static MstResult run(Graph g, Metrics m) {
        if (g.isDirected()) {
            throw new IllegalArgumentException("MST needs an undirected graph");
        }
        List<Graph.Edge> sorted = new ArrayList<>(g.edges());
        sorted.sort((a, b) -> {
            m.comparisons++;
            return Integer.compare(a.weight, b.weight);
        });
        DSU dsu = new DSU(g.n());
        List<Graph.Edge> tree = new ArrayList<>();
        long total = 0;
        for (Graph.Edge e : sorted) {
            if (dsu.components() <= 1) {
                break;
            }
            m.edgeScans++;
            if (dsu.find(e.from) != dsu.find(e.to)) {
                dsu.union(e.from, e.to);
                tree.add(e);
                total += e.weight;
            }
        }
        m.dsuOps += dsu.ops();
        return new MstResult(total, tree, dsu.components());
    }
}
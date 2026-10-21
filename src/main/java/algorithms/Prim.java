package algorithms;

import graph.Graph;
import metrics.Metrics;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public final class Prim {
    private Prim() {
    }

    public static MstResult run(Graph g, Metrics m) {
        if (g.isDirected()) {
            throw new IllegalArgumentException("MST needs an undirected graph");
        }
        int n = g.n();
        boolean[] inTree = new boolean[n];
        PriorityQueue<Graph.Edge> pq = new PriorityQueue<>((a, b) -> {
            m.comparisons++;
            return Integer.compare(a.weight, b.weight);
        });
        List<Graph.Edge> tree = new ArrayList<>();
        long total = 0;
        int components = 0;
        for (int s = 0; s < n; s++) {
            if (inTree[s]) {
                continue;
            }
            components++;
            inTree[s] = true;
            for (Graph.Edge e : g.adj(s)) {
                m.edgeScans++;
                if (!inTree[e.to]) {
                    pq.add(e);
                }
            }
            while (!pq.isEmpty()) {
                Graph.Edge e = pq.poll();
                if (inTree[e.to]) {
                    continue;
                }
                inTree[e.to] = true;
                total += e.weight;
                tree.add(e);
                for (Graph.Edge f : g.adj(e.to)) {
                    m.edgeScans++;
                    if (!inTree[f.to]) {
                        pq.add(f);
                    }
                }
            }
        }
        return new MstResult(total, tree, components);
    }
}
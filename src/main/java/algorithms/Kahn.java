package algorithms;

import graph.Graph;
import metrics.Metrics;

import java.util.Arrays;
import java.util.List;

public final class Kahn {
    private Kahn() {
    }

    public static TopoResult run(Graph g, Metrics m) {
        if (!g.isDirected()) {
            throw new IllegalArgumentException("topological order needs a directed graph");
        }
        int n = g.n();
        int[] indeg = new int[n];
        for (Graph.Edge e : g.edges()) {
            indeg[e.to]++;
        }
        int[] queue = new int[n];
        int head = 0;
        int tail = 0;
        for (int v = 0; v < n; v++) {
            if (indeg[v] == 0) {
                queue[tail++] = v;
            }
        }
        while (head < tail) {
            int u = queue[head++];
            List<Graph.Edge> adj = g.adj(u);
            for (Graph.Edge e : adj) {
                m.edgeScans++;
                if (--indeg[e.to] == 0) {
                    queue[tail++] = e.to;
                }
            }
        }
        return new TopoResult(Arrays.copyOf(queue, tail), tail < n);
    }

    public static boolean isValid(Graph g, int[] order) {
        int n = g.n();
        if (order.length != n) {
            return false;
        }
        int[] pos = new int[n];
        Arrays.fill(pos, -1);
        for (int i = 0; i < n; i++) {
            if (order[i] < 0 || order[i] >= n || pos[order[i]] != -1) {
                return false;
            }
            pos[order[i]] = i;
        }
        for (Graph.Edge e : g.edges()) {
            if (pos[e.from] >= pos[e.to]) {
                return false;
            }
        }
        return true;
    }
}
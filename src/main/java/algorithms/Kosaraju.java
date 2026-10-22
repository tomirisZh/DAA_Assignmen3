package algorithms;

import graph.Graph;
import metrics.Metrics;

import java.util.Arrays;
import java.util.List;

public final class Kosaraju {
    private Kosaraju() {
    }

    private static int explore(Graph g, int s, boolean[] visited, int[] stack, int[] next,
                               int[] out, int outPos, Metrics m) {
        int sp = 0;
        visited[s] = true;
        stack[sp++] = s;
        if (sp > m.maxDepth) {
            m.maxDepth = sp;
        }
        while (sp > 0) {
            int u = stack[sp - 1];
            List<Graph.Edge> adj = g.adj(u);
            if (next[u] < adj.size()) {
                Graph.Edge e = adj.get(next[u]++);
                m.edgeScans++;
                if (!visited[e.to]) {
                    visited[e.to] = true;
                    stack[sp++] = e.to;
                    if (sp > m.maxDepth) {
                        m.maxDepth = sp;
                    }
                }
            } else {
                sp--;
                out[outPos++] = u;
            }
        }
        return outPos;
    }

    public static SccResult run(Graph g, Metrics m) {
        if (!g.isDirected()) {
            throw new IllegalArgumentException("SCC needs a directed graph");
        }
        int n = g.n();
        boolean[] visited = new boolean[n];
        int[] stack = new int[n];
        int[] next = new int[n];
        int[] order = new int[n];
        int pos = 0;
        for (int s = 0; s < n; s++) {
            if (!visited[s]) {
                pos = explore(g, s, visited, stack, next, order, pos, m);
            }
        }
        Graph r = g.reversed();
        Arrays.fill(visited, false);
        Arrays.fill(next, 0);
        int[] component = new int[n];
        int[] out = new int[n];
        int outPos = 0;
        int count = 0;
        for (int i = n - 1; i >= 0; i--) {
            int s = order[i];
            if (!visited[s]) {
                int from = outPos;
                outPos = explore(r, s, visited, stack, next, out, outPos, m);
                for (int j = from; j < outPos; j++) {
                    component[out[j]] = count;
                }
                count++;
            }
        }
        return new SccResult(count, component);
    }
}
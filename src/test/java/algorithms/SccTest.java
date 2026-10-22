package algorithms;

import graph.Graph;
import graph.GraphIO;
import metrics.Metrics;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class SccTest {

    private static boolean[][] reachability(Graph g) {
        int n = g.n();
        boolean[][] reach = new boolean[n][n];
        for (int s = 0; s < n; s++) {
            ArrayDeque<Integer> queue = new ArrayDeque<>();
            reach[s][s] = true;
            queue.add(s);
            while (!queue.isEmpty()) {
                int u = queue.poll();
                for (Graph.Edge e : g.adj(u)) {
                    if (!reach[s][e.to]) {
                        reach[s][e.to] = true;
                        queue.add(e.to);
                    }
                }
            }
        }
        return reach;
    }

    @Test
    void matchesBruteForceOnRandomGraphs() {
        Random rnd = new Random(3);
        for (int t = 0; t < 100; t++) {
            int n = 1 + rnd.nextInt(200);
            int m = rnd.nextInt(3 * n + 1);
            Graph g = new Graph(n, true);
            for (int k = 0; k < m; k++) {
                g.addEdge(rnd.nextInt(n), rnd.nextInt(n), 1);
            }
            Metrics metrics = new Metrics();
            SccResult r = Kosaraju.run(g, metrics);
            boolean[][] reach = reachability(g);
            for (int a = 0; a < n; a++) {
                for (int b = 0; b < n; b++) {
                    assertEquals(reach[a][b] && reach[b][a], r.component[a] == r.component[b]);
                }
            }
            Set<Integer> distinct = new HashSet<>();
            for (int c : r.component) {
                distinct.add(c);
            }
            assertEquals(distinct.size(), r.count);
            assertEquals(2L * g.m(), metrics.edgeScans);
        }
    }

    @Test
    void cyclicFileHasFourComponents() throws IOException {
        Graph g = GraphIO.read(Path.of("data/directed_cyclic.txt"));
        SccResult r = Kosaraju.run(g, new Metrics());
        assertEquals(4, r.count);
        assertEquals(r.component[0], r.component[1]);
        assertEquals(r.component[1], r.component[2]);
        assertNotEquals(r.component[2], r.component[3]);
    }

    @Test
    void pathOf100000VerticesDoesNotOverflowStack() {
        int n = 100000;
        Graph g = new Graph(n, true);
        for (int i = 0; i + 1 < n; i++) {
            g.addEdge(i, i + 1, 1);
        }
        Metrics metrics = new Metrics();
        SccResult r = Kosaraju.run(g, metrics);
        assertEquals(n, r.count);
        assertEquals(n, metrics.maxDepth);
        assertEquals(2L * (n - 1), metrics.edgeScans);
    }
}
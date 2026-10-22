package algorithms;

import graph.Graph;
import graph.GraphIO;
import metrics.Metrics;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TopoTest {

    @Test
    void validOrderOnDagFile() throws IOException {
        Graph g = GraphIO.read(Path.of("data/dag_small.txt"));
        TopoResult r = Kahn.run(g, new Metrics());
        assertFalse(r.hasCycle);
        assertEquals(6, r.order.length);
        assertTrue(Kahn.isValid(g, r.order));
    }

    @Test
    void cycleDetectedOnCyclicFile() throws IOException {
        Graph g = GraphIO.read(Path.of("data/directed_cyclic.txt"));
        TopoResult r = Kahn.run(g, new Metrics());
        assertTrue(r.hasCycle);
        assertTrue(r.order.length < g.n());
        assertFalse(Kahn.isValid(g, r.order));
    }

    @Test
    void validOrderOnRandomDags() {
        Random rnd = new Random(5);
        for (int t = 0; t < 100; t++) {
            int n = 1 + rnd.nextInt(100);
            int m = rnd.nextInt(4 * n + 1);
            Graph g = new Graph(n, true);
            for (int k = 0; k < m; k++) {
                int a = rnd.nextInt(n);
                int b = rnd.nextInt(n);
                if (a == b) {
                    continue;
                }
                if (a > b) {
                    int tmp = a;
                    a = b;
                    b = tmp;
                }
                g.addEdge(a, b, 1);
            }
            Metrics metrics = new Metrics();
            TopoResult r = Kahn.run(g, metrics);
            assertFalse(r.hasCycle);
            assertTrue(Kahn.isValid(g, r.order));
            assertEquals(g.m(), metrics.edgeScans);
        }
    }

    @Test
    void cycleAfterLongPathIsDetected() {
        int n = 50;
        Graph g = new Graph(n, true);
        for (int i = 0; i + 1 < n; i++) {
            g.addEdge(i, i + 1, 1);
        }
        g.addEdge(n - 1, 0, 1);
        TopoResult r = Kahn.run(g, new Metrics());
        assertTrue(r.hasCycle);
    }

    @Test
    void selfLoopIsCycle() {
        Graph g = new Graph(2, true);
        g.addEdge(0, 0, 1);
        assertTrue(Kahn.run(g, new Metrics()).hasCycle);
    }
}
package algorithms;

import graph.Graph;
import graph.GraphIO;
import metrics.Metrics;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MstTest {

    private static Graph randomConnected(Random rnd, int n, int extra) {
        Graph g = new Graph(n, false);
        for (int i = 1; i < n; i++) {
            g.addEdge(rnd.nextInt(i), i, 1 + rnd.nextInt(20));
        }
        for (int k = 0; k < extra; k++) {
            g.addEdge(rnd.nextInt(n), rnd.nextInt(n), 1 + rnd.nextInt(20));
        }
        return g;
    }

    @Test
    void kruskalAndPrimAgreeOnRandomConnectedGraphs() {
        Random rnd = new Random(7);
        for (int t = 0; t < 100; t++) {
            int n = 2 + rnd.nextInt(60);
            Graph g = randomConnected(rnd, n, rnd.nextInt(150));
            MstResult k = Kruskal.run(g, new Metrics());
            MstResult p = Prim.run(g, new Metrics());
            assertEquals(k.totalWeight, p.totalWeight);
            assertEquals(n - 1, k.edges.size());
            assertEquals(n - 1, p.edges.size());
            assertEquals(1, k.components);
            assertEquals(1, p.components);
        }
    }

    @Test
    void disconnectedGraphGivesForest() throws IOException {
        Graph g = GraphIO.read(Path.of("data/undirected_disconnected.txt"));
        MstResult k = Kruskal.run(g, new Metrics());
        MstResult p = Prim.run(g, new Metrics());
        assertEquals(8, k.totalWeight);
        assertEquals(8, p.totalWeight);
        assertEquals(3, k.components);
        assertEquals(3, p.components);
        assertEquals(4, k.edges.size());
        assertEquals(4, p.edges.size());
    }

    @Test
    void graphWithoutEdgesDoesNotCrash() {
        Graph g = new Graph(3, false);
        MstResult k = Kruskal.run(g, new Metrics());
        MstResult p = Prim.run(g, new Metrics());
        assertEquals(0, k.totalWeight);
        assertEquals(0, p.totalWeight);
        assertEquals(3, k.components);
        assertEquals(3, p.components);
    }
}
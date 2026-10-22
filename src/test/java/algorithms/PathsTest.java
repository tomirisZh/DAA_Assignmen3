package algorithms;

import graph.Graph;
import graph.GraphIO;
import metrics.Metrics;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PathsTest {

    private static long[] bellmanFord(Graph g) {
        int n = g.n();
        long inf = Long.MAX_VALUE / 4;
        boolean[] hasIncoming = new boolean[n];
        for (Graph.Edge e : g.edges()) {
            hasIncoming[e.to] = true;
        }
        long[] dist = new long[n];
        for (int v = 0; v < n; v++) {
            dist[v] = hasIncoming[v] ? inf : 0;
        }
        for (int round = 0; round < n; round++) {
            for (Graph.Edge e : g.edges()) {
                if (dist[e.from] < inf && dist[e.from] + e.weight < dist[e.to]) {
                    dist[e.to] = dist[e.from] + e.weight;
                }
            }
        }
        return dist;
    }

    @Test
    void matchesBellmanFordOnRandomDagsWithNegativeWeights() {
        Random rnd = new Random(11);
        for (int t = 0; t < 200; t++) {
            int n = 1 + rnd.nextInt(60);
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
                g.addEdge(a, b, rnd.nextInt(71) - 20);
            }
            Metrics metrics = new Metrics();
            PathResult r = DagShortestPath.run(g, metrics);
            long[] expected = bellmanFord(g);
            for (int v = 0; v < n; v++) {
                assertEquals(expected[v], r.dist[v]);
            }
            assertEquals(g.m(), metrics.edgeScans);
        }
    }

    @Test
    void smallDagFileGivesKnownAnswer() throws IOException {
        Graph g = GraphIO.read(Path.of("data/dag_small.txt"));
        PathResult r = DagShortestPath.run(g, new Metrics());
        assertEquals(Arrays.asList(0L, 2L, 1L, 9L, 4L, 2L), Arrays.stream(r.dist).boxed().toList());
        assertEquals(18, r.sum);
        assertEquals(List.of(0, 1, 2, 4, 5), r.pathTo(5));
    }

    @Test
    void cycleIsRejected() throws IOException {
        Graph g = GraphIO.read(Path.of("data/directed_cyclic.txt"));
        assertThrows(IllegalArgumentException.class, () -> DagShortestPath.run(g, new Metrics()));
    }
}
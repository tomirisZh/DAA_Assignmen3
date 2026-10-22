package graph;

import java.util.Random;

public final class GraphGenerator {
    private GraphGenerator() {
    }

    private static int[] shuffled(int n, Random rnd) {
        int[] p = new int[n];
        for (int i = 0; i < n; i++) {
            p[i] = i;
        }
        for (int i = n - 1; i > 0; i--) {
            int j = rnd.nextInt(i + 1);
            int t = p[i];
            p[i] = p[j];
            p[j] = t;
        }
        return p;
    }

    public static Graph undirected(int n, int m) {
        Random rnd = new Random(42);
        Graph g = new Graph(n, false);
        int[] perm = shuffled(n, rnd);
        int added = 0;
        for (int i = 1; i < n; i++) {
            g.addEdge(perm[rnd.nextInt(i)], perm[i], 1 + rnd.nextInt(1000));
            added++;
        }
        while (added < m) {
            int a = rnd.nextInt(n);
            int b = rnd.nextInt(n);
            if (b == a) {
                continue;
            }
            g.addEdge(a, b, 1 + rnd.nextInt(1000));
            added++;
        }
        return g;
    }

    public static Graph directed(int n, int m) {
        Random rnd = new Random(42);
        Graph g = new Graph(n, true);
        int added = 0;
        while (added < m) {
            int a = rnd.nextInt(n);
            int b = rnd.nextInt(n);
            while (b == a) {
                b = rnd.nextInt(n);
            }
            g.addEdge(a, b, 1 + rnd.nextInt(1000));
            added++;
        }
        return g;
    }

    public static Graph dag(int n, int m) {
        Random rnd = new Random(42);
        Graph g = new Graph(n, true);
        int[] rank = shuffled(n, rnd);
        int added = 0;
        while (added < m) {
            int a = rnd.nextInt(n);
            int b = rnd.nextInt(n);
            if (b == a) {
                continue;
            }
            if (rank[a] > rank[b]) {
                int t = a;
                a = b;
                b = t;
            }
            g.addEdge(a, b, 1 + rnd.nextInt(1000));
            added++;
        }
        return g;
    }
}
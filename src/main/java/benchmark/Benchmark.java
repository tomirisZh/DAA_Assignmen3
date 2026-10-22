package benchmark;

import algorithms.DagShortestPath;
import algorithms.Kahn;
import algorithms.Kosaraju;
import algorithms.Kruskal;
import algorithms.MstResult;
import algorithms.Prim;
import algorithms.TopoResult;
import graph.Graph;
import graph.GraphGenerator;
import metrics.Metrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.ToLongFunction;

public final class Benchmark {
    private static final int[] SIZES = {1000, 3162, 10000, 31623, 100000};
    private static final int[] DENSITIES = {4, 20};
    private static final String[] DENSITY_NAMES = {"sparse", "dense"};
    private static final int RUNS = 5;

    private static final long[] REF_MST = {
            147970, 30363, 468911, 97589, 1486139, 309550, 4715515, 968517, 15012094, 3056681};
    private static final long[] REF_SCC = {27, 1, 137, 1, 373, 1, 1188, 1, 3923, 1};
    private static final long[] REF_DAG = {
            396274, 252411, 1271911, 723157, 3941828, 2381342, 12551891, 7376612, 40096539, 23512087};

    private record Row(String algorithm, String graph, int n, int m, double timeMs, Metrics metrics, long result) {
    }

    private Benchmark() {
    }

    private static void fail(String message) {
        throw new IllegalStateException("CHECK FAILED: " + message);
    }

    private static Row measure(String algorithm, String graph, int n, int m, ToLongFunction<Metrics> task) {
        double[] times = new double[RUNS];
        Metrics counted = null;
        long result = 0;
        for (int r = 0; r < RUNS; r++) {
            Metrics metrics = new Metrics();
            long start = System.nanoTime();
            long value = task.applyAsLong(metrics);
            times[r] = (System.nanoTime() - start) / 1e6;
            if (r == 0) {
                counted = metrics;
                result = value;
            } else if (value != result || metrics.edgeScans != counted.edgeScans
                    || metrics.comparisons != counted.comparisons || metrics.dsuOps != counted.dsuOps) {
                fail(algorithm + " on " + graph + " is not deterministic");
            }
        }
        Arrays.sort(times);
        return new Row(algorithm, graph, n, m, times[RUNS / 2], counted, result);
    }

    private static List<Row> mstRows(int n, int m, String label, int index) {
        Graph g = GraphGenerator.undirected(n, m);
        Row kruskal = measure("Kruskal", label, n, m, mt -> Kruskal.run(g, mt).totalWeight);
        Row prim = measure("Prim", label, n, m, mt -> Prim.run(g, mt).totalWeight);
        MstResult check = Kruskal.run(g, new Metrics());
        if (check.components != 1 || check.edges.size() != n - 1) {
            fail("spanning tree of " + label + " is not a tree");
        }
        if (kruskal.result() != prim.result()) {
            fail("Kruskal " + kruskal.result() + " and Prim " + prim.result() + " disagree on " + label);
        }
        if (kruskal.result() != REF_MST[index]) {
            fail("MST weight of " + label + " is " + kruskal.result() + ", expected " + REF_MST[index]);
        }
        if (prim.metrics().edgeScans != 2L * m) {
            fail("Prim scans on " + label);
        }
        return List.of(kruskal, prim);
    }

    private static List<Row> sccRows(int n, int m, String label, int index) {
        Graph g = GraphGenerator.directed(n, m);
        Row kosaraju = measure("Kosaraju", label, n, m, mt -> Kosaraju.run(g, mt).count);
        if (kosaraju.result() != REF_SCC[index]) {
            fail("SCC count of " + label + " is " + kosaraju.result() + ", expected " + REF_SCC[index]);
        }
        if (kosaraju.metrics().edgeScans != 2L * m) {
            fail("Kosaraju scans on " + label);
        }
        return List.of(kosaraju);
    }

    private static List<Row> dagRows(int n, int m, String label, int index) {
        Graph g = GraphGenerator.dag(n, m);
        Row kahn = measure("Kahn", label, n, m, mt -> Kahn.run(g, mt).order.length);
        Row paths = measure("DagShortestPath", label, n, m, mt -> DagShortestPath.run(g, mt).sum);
        TopoResult topo = Kahn.run(g, new Metrics());
        if (topo.hasCycle || !Kahn.isValid(g, topo.order)) {
            fail("topological order of " + label + " is not valid");
        }
        if (kahn.result() != n) {
            fail("Kahn on " + label + " returned " + kahn.result() + " vertices");
        }
        if (paths.result() != REF_DAG[index]) {
            fail("DAG distance sum of " + label + " is " + paths.result() + ", expected " + REF_DAG[index]);
        }
        if (kahn.metrics().edgeScans != m || paths.metrics().edgeScans != m) {
            fail("Kahn or DAG scans on " + label);
        }
        return List.of(kahn, paths);
    }

    public static void main(String[] args) throws IOException {
        Path out = Path.of(args.length > 0 ? args[0] : "results/results.csv");
        List<Row> rows = new ArrayList<>();
        int index = 0;
        for (int n : SIZES) {
            for (int d = 0; d < DENSITIES.length; d++) {
                int m = n * DENSITIES[d];
                String label = "n" + n + "_" + DENSITY_NAMES[d];
                rows.addAll(mstRows(n, m, label, index));
                rows.addAll(sccRows(n, m, label, index));
                rows.addAll(dagRows(n, m, label, index));
                System.out.printf(Locale.ROOT,
                        "OK %s n=%d m=%d mst=%d scc=%d dagSum=%d%n",
                        label, n, m, REF_MST[index], REF_SCC[index], REF_DAG[index]);
                index++;
            }
        }
        if (rows.size() != 50) {
            fail("expected 50 rows, got " + rows.size());
        }
        StringBuilder csv = new StringBuilder();
        csv.append("algorithm,graph,n,m,time_ms,dsu_ops,edge_scans,comparisons,max_depth,result\n");
        for (Row r : rows) {
            csv.append(r.algorithm()).append(',')
                    .append(r.graph()).append(',')
                    .append(r.n()).append(',')
                    .append(r.m()).append(',')
                    .append(String.format(Locale.ROOT, "%.3f", r.timeMs())).append(',')
                    .append(r.metrics().dsuOps).append(',')
                    .append(r.metrics().edgeScans).append(',')
                    .append(r.metrics().comparisons).append(',')
                    .append(r.metrics().maxDepth).append(',')
                    .append(r.result()).append('\n');
        }
        if (out.getParent() != null) {
            Files.createDirectories(out.getParent());
        }
        Files.writeString(out, csv.toString());
        System.out.println("All reference values reproduced. Wrote " + rows.size() + " rows to " + out);
    }
}
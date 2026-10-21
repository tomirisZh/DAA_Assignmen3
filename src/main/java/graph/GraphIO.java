package graph;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class GraphIO {
    private GraphIO() {
    }

    public static Graph read(Path path) throws IOException {
        try (BufferedReader br = Files.newBufferedReader(path)) {
            String header = nextLine(br);
            if (header == null) {
                throw new IOException("empty file");
            }
            String[] h = header.trim().split("\\s+");
            if (h.length != 3) {
                throw new IOException("bad header: " + header);
            }
            int n = Integer.parseInt(h[0]);
            int m = Integer.parseInt(h[1]);
            boolean directed;
            if (h[2].equals("directed")) {
                directed = true;
            } else if (h[2].equals("undirected")) {
                directed = false;
            } else {
                throw new IOException("bad type: " + h[2]);
            }
            Graph g = new Graph(n, directed);
            for (int i = 0; i < m; i++) {
                String line = nextLine(br);
                if (line == null) {
                    throw new IOException("expected " + m + " edges, got " + i);
                }
                String[] p = line.trim().split("\\s+");
                g.addEdge(Integer.parseInt(p[0]), Integer.parseInt(p[1]), Integer.parseInt(p[2]));
            }
            return g;
        }
    }

    private static String nextLine(BufferedReader br) throws IOException {
        String line;
        while ((line = br.readLine()) != null) {
            if (!line.isBlank()) {
                return line;
            }
        }
        return null;
    }
}
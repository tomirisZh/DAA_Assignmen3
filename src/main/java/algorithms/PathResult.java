package algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PathResult {
    public final long[] dist;
    public final int[] parent;
    public final long sum;

    public PathResult(long[] dist, int[] parent, long sum) {
        this.dist = dist;
        this.parent = parent;
        this.sum = sum;
    }

    public List<Integer> pathTo(int v) {
        List<Integer> path = new ArrayList<>();
        for (int x = v; x != -1; x = parent[x]) {
            path.add(x);
        }
        Collections.reverse(path);
        return path;
    }
}
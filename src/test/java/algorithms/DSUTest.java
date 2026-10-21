package algorithms;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DSUTest {

    @Test
    void agreesWithReferenceOnRandomUnions() {
        Random rnd = new Random(1);
        for (int t = 0; t < 50; t++) {
            int n = 1 + rnd.nextInt(100);
            DSU dsu = new DSU(n);
            int[] label = new int[n];
            for (int i = 0; i < n; i++) {
                label[i] = i;
            }
            int ops = rnd.nextInt(200);
            for (int k = 0; k < ops; k++) {
                int a = rnd.nextInt(n);
                int b = rnd.nextInt(n);
                dsu.union(a, b);
                int la = label[a];
                int lb = label[b];
                if (la != lb) {
                    for (int i = 0; i < n; i++) {
                        if (label[i] == lb) {
                            label[i] = la;
                        }
                    }
                }
            }
            Set<Integer> distinct = new HashSet<>();
            for (int i = 0; i < n; i++) {
                distinct.add(label[i]);
            }
            assertEquals(distinct.size(), dsu.components());
            for (int a = 0; a < n; a++) {
                for (int b = 0; b < n; b++) {
                    assertEquals(label[a] == label[b], dsu.find(a) == dsu.find(b));
                }
            }
        }
    }

    @Test
    void countsFindAndSuccessfulUnions() {
        DSU dsu = new DSU(4);
        dsu.union(0, 1);
        dsu.union(0, 1);
        assertEquals(1, dsu.unionCalls());
        assertEquals(4, dsu.findCalls());
        assertEquals(3, dsu.components());
    }

    @Test
    void longChainDoesNotOverflow() {
        int n = 100000;
        DSU dsu = new DSU(n);
        for (int i = 0; i + 1 < n; i++) {
            dsu.union(i, i + 1);
        }
        assertEquals(1, dsu.components());
        assertEquals(dsu.find(0), dsu.find(n - 1));
    }
}
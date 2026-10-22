package algorithms;

public class TopoResult {
    public final int[] order;
    public final boolean hasCycle;

    public TopoResult(int[] order, boolean hasCycle) {
        this.order = order;
        this.hasCycle = hasCycle;
    }
}
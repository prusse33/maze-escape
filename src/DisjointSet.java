/**
 * Mission 0. Disjoint set union (DSU).
 * Cells are numbered 0 .. n-1. Uses union by size and path compression.
 */
public class DisjointSet {
    private final int[] parent;
    private final int[] size;
    private int sets;

    public DisjointSet(int n) {
        if (n < 0) throw new IllegalArgumentException("n must not be negative");
        parent = new int[n];
        size = new int[n];
        sets = n;
        for (int i = 0; i < n; i++) { parent[i] = i; size[i] = 1; }
    }

    /** The root of the set that contains x. */
    public int find(int x) {
        if (x < 0 || x >= parent.length) throw new IndexOutOfBoundsException("cell: " + x);
        while (parent[x] != x) {
            parent[x] = parent[parent[x]];
            x = parent[x];
        }
        return x;
    }

    /** Joins the sets of a and b. Returns true if they were different sets. */
    public boolean union(int a, int b) {
        int ra = find(a), rb = find(b);
        if (ra == rb) return false;
        if (size[ra] < size[rb]) { int t = ra; ra = rb; rb = t; }
        parent[rb] = ra;
        size[ra] += size[rb];
        sets--;
        return true;
    }

    public boolean connected(int a, int b) { return find(a) == find(b); }
    /** Number of sets now. */
    public int count() { return sets; }
}

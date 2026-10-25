import java.util.Arrays;
import java.util.PriorityQueue;

/** Missions 1 and 2. Building the maze out of the walls. */
public class Maze {
    /** Mission 1: take walls in file order and break a wall if it joins two components. */
    public static int[] breakWalls(Input.Walls w) {
        DisjointSet dsu = new DisjointSet(w.cells());
        int[] chosen = new int[Math.max(0, w.cells() - 1)];
        int count = 0;
        for (int i = 0; i < w.count() && count < chosen.length; i++) {
            if (dsu.union(w.a()[i], w.b()[i])) chosen[count++] = i;
        }
        return Arrays.copyOf(chosen, count);
    }

    /** Mission 2: Kruskal's minimum spanning tree. */
    public static int[] cheapestMaze(Input.Walls w) {
        Integer[] order = new Integer[w.count()];
        for (int i = 0; i < order.length; i++) order[i] = i;
        Arrays.sort(order, (i, j) -> Integer.compare(w.cost()[i], w.cost()[j]));
        DisjointSet dsu = new DisjointSet(w.cells());
        int[] chosen = new int[Math.max(0, w.cells() - 1)];
        int count = 0;
        for (int k : order) {
            if (dsu.union(w.a()[k], w.b()[k])) {
                chosen[count++] = k;
                if (count == chosen.length) break;
            }
        }
        return Arrays.copyOf(chosen, count);
    }

    private record Edge(int to, int cost) {}

    /** Mission 2: Prim's algorithm using a priority queue and compact adjacency arrays. */
    public static long primCost(Input.Walls w) {
        int n = w.cells(), m = w.count();
        if (n <= 1) return 0;
        int[] head = new int[n]; Arrays.fill(head, -1);
        int[] to = new int[2 * m], cost = new int[2 * m], next = new int[2 * m];
        int edge = 0;
        for (int i = 0; i < m; i++) {
            int a = w.a()[i], b = w.b()[i], c = w.cost()[i];
            to[edge] = b; cost[edge] = c; next[edge] = head[a]; head[a] = edge++;
            to[edge] = a; cost[edge] = c; next[edge] = head[b]; head[b] = edge++;
        }
        boolean[] seen = new boolean[n];
        PriorityQueue<Edge> pq = new PriorityQueue<>((x, y) -> Integer.compare(x.cost(), y.cost()));
        seen[0] = true;
        for (int e = head[0]; e != -1; e = next[e]) pq.add(new Edge(to[e], cost[e]));
        long total = 0; int taken = 1;
        while (!pq.isEmpty() && taken < n) {
            Edge e = pq.poll();
            if (seen[e.to()]) continue;
            int u = e.to(); seen[u] = true; taken++; total += e.cost();
            for (int k = head[u]; k != -1; k = next[k])
                if (!seen[to[k]]) pq.add(new Edge(to[k], cost[k]));
        }
        if (taken != n) throw new IllegalArgumentException("The maze graph is disconnected");
        return total;
    }

}

import java.util.Arrays;

/** Missions 4 and 5. Strongly connected components and routes in their DAG. */
public class Zones {
    /** Kosaraju with iterative DFS passes. Zone IDs are in topological order. */
    public static int[] kosaraju(Input.Slides s) {
        int n = s.n();
        int[][] from = Graphs.slidesFrom(s), into = Graphs.slidesInto(s);
        boolean[] seen = new boolean[n];
        int[] order = new int[n], stack = new int[n], next = new int[n];
        int orderSize = 0;
        for (int start = 0; start < n; start++) {
            if (seen[start]) continue;
            int top = 0; stack[0] = start; next[0] = 0; seen[start] = true;
            while (top >= 0) {
                int u = stack[top];
                if (next[top] < from[u].length) {
                    int edge = from[u][next[top]++];
                    int v = s.to()[edge];
                    if (!seen[v]) { seen[v] = true; stack[++top] = v; next[top] = 0; }
                } else {
                    order[orderSize++] = u; top--;
                }
            }
        }
        int[] zoneOf = new int[n]; Arrays.fill(zoneOf, -1);
        int zones = 0;
        for (int i = orderSize - 1; i >= 0; i--) {
            int start = order[i];
            if (zoneOf[start] != -1) continue;
            int top = 0; stack[0] = start; zoneOf[start] = zones;
            while (top >= 0) {
                int u = stack[top--];
                for (int edge : into[u]) {
                    int v = s.from()[edge];
                    if (zoneOf[v] == -1) { zoneOf[v] = zones; stack[++top] = v; }
                }
            }
            zones++;
        }
        return zoneOf;
    }

    /** Calculates the longest chain length and cheapest route in the zone DAG. */
    public static long[] routes(Input.Slides s, int[] zoneOf, int zoneCount) {
        int n = s.n();
        int start = zoneOf[0], exit = zoneOf[n - 1];
        long[] stages = new long[zoneCount];
        long[] distance = new long[zoneCount];
        Arrays.fill(stages, 1);
        Arrays.fill(distance, Long.MAX_VALUE / 4);
        distance[start] = 0;

        // Group each cross-zone slide under its source zone in O(zones + slides) time.
        int[] head = new int[zoneCount]; Arrays.fill(head, -1);
        int[] next = new int[s.count()];
        for (int i = 0; i < s.count(); i++) {
            int a = zoneOf[s.from()[i]], b = zoneOf[s.to()[i]];
            next[i] = head[a]; head[a] = i;
        }

        // Zone IDs are topological, so every incoming zone is processed first.
        for (int z = 0; z < zoneCount; z++) {
            for (int i = head[z]; i != -1; i = next[i]) {
                int b = zoneOf[s.to()[i]];
                if (b == z) continue; // walking inside one zone is free
                stages[b] = Math.max(stages[b], stages[z] + 1);
                if (distance[z] < Long.MAX_VALUE / 4)
                    distance[b] = Math.min(distance[b], distance[z] + s.time()[i]);
            }
        }
        long longest = 0;
        for (long stage : stages) longest = Math.max(longest, stage);
        return new long[]{longest, distance[exit] >= Long.MAX_VALUE / 4 ? 0 : distance[exit]};
    }

}

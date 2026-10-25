import java.util.Arrays;

/** Mission 3. Iterative depth-first search through the maze of Mission 1. */
public class Escape {
    public record Result(int[] path, int visited, int deadEnds) {}

    public static Result escape(Input.Walls w, int[] broken) {
        int n = w.cells(), exit = n - 1, cols = w.cols();
        int[][] graph = Graphs.open(w, broken);
        int deadEnds = 0;
        for (int[] neighbours : graph) if (neighbours.length == 1) deadEnds++;

        boolean[] seen = new boolean[n];
        int[] parent = new int[n]; Arrays.fill(parent, -1);
        int[] stack = new int[n];
        int[] nextDirection = new int[n];
        int top = 0, visited = 1;
        stack[0] = 0; seen[0] = true;
        int[] direction = {-cols, 1, cols, -1}; // up, right, down, left
        while (top >= 0 && !seen[exit]) {
            int u = stack[top];
            if (nextDirection[top] >= 4) { top--; continue; }
            int v = u + direction[nextDirection[top]++];
            if (v < 0 || v >= n) continue;
            if ((v == u + 1 && u / cols != v / cols) || (v == u - 1 && u / cols != v / cols)) continue;
            if (seen[v] || !hasNeighbour(graph[u], v)) continue;
            seen[v] = true; visited++; parent[v] = u;
            stack[++top] = v; nextDirection[top] = 0;
        }
        if (!seen[exit]) return new Result(new int[0], visited, deadEnds);
        int length = 1;
        for (int v = exit; v != 0; v = parent[v]) length++;
        int[] path = new int[length];
        int v = exit;
        for (int i = length - 1; i >= 0; i--) { path[i] = v; if (v != 0) v = parent[v]; }
        return new Result(path, visited, deadEnds);
    }

    private static boolean hasNeighbour(int[] neighbours, int target) {
        for (int v : neighbours) if (v == target) return true;
        return false;
    }
}

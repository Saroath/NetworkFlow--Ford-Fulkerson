package networkflow;

//20231686
//SaroathJahan

import java.util.ArrayList;
import java.util.List;

public class FlowNetwork {
    private final int V;
    private final List<FlowEdge>[] adj;

    @SuppressWarnings("unchecked")
    public FlowNetwork(int V) {
        this.V = V;
        adj = (List<FlowEdge>[]) new ArrayList[V];
        for (int v = 0; v < V; v++) {
            adj[v] = new ArrayList<>();
        }
    }

    public int V() { return V; }

    public void addEdge(FlowEdge edge) {
        int v = edge.from();
        int w = edge.to();
        adj[v].add(edge);
        adj[w].add(edge);
    }

    public Iterable<FlowEdge> adj(int v) {
        return adj[v];
    }
}
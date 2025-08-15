package networkflow;

//20231686
//SaroathJahan
public class FlowEdge {
    private final int from;
    private final int to;
    private final int capacity;
    private int flow;

    public FlowEdge(int from, int to, int capacity) {
        this.from = from;
        this.to = to;
        this.capacity = capacity;
        this.flow = 0;
    }

    public int from() { return from; }
    public int to() { return to; }
    public int capacity() { return capacity; }
    public int flow() { return flow; }

    public int residualCapacityTo(int vertex) {
        if (vertex == from) return flow;
        else if (vertex == to) return capacity - flow;
        else throw new IllegalArgumentException("Invalid vertex");
    }

    public void addResidualFlowTo(int vertex, int delta) {
        if (vertex == from) flow -= delta;
        else if (vertex == to) flow += delta;
        else throw new IllegalArgumentException("Invalid vertex");
    }
}
package networkflow;

//20231686
//SaroathJahan

import java.io.File;
import java.util.*;

public class MaxFlow {
    static class PathInfo {
        List<Integer> path;
        int bottleneck;

        PathInfo(List<Integer> path, int bottleneck) {
            this.path = path;
            this.bottleneck = bottleneck;
        }
    }

    // Parser method to read the network from a file
    public static FlowNetwork readNetwork(String filename) throws Exception {
        File file = new File("input/" + filename);
        Scanner scanner = new Scanner(file);

        // Read number of nodes
        int V = scanner.nextInt();
        FlowNetwork network = new FlowNetwork(V);

        // Read edges
        while (scanner.hasNextInt()) {
            int from = scanner.nextInt();
            int to = scanner.nextInt();
            int capacity = scanner.nextInt();
            if (from < 0 || from >= V || to < 0 || to >= V || capacity < 0) {
                throw new IllegalArgumentException("Invalid input");
            }
            network.addEdge(new FlowEdge(from, to, capacity));
        }

        scanner.close();
        return network;
    }

    // Find augmenting path using BFS
    private static PathInfo findAugmentingPath(FlowNetwork network, int source, int sink) {
        int V = network.V();
        int[] parent = new int[V];
        Arrays.fill(parent, -1);
        int[] minCapacity = new int[V];
        Arrays.fill(minCapacity, Integer.MAX_VALUE);

        Queue<Integer> queue = new LinkedList<>();
        queue.add(source);
        parent[source] = source;

        while (!queue.isEmpty()) {
            int u = queue.poll();
            for (FlowEdge edge : network.adj(u)) {
                int v = edge.to();
                if (parent[v] == -1 && edge.residualCapacityTo(v) > 0) {
                    parent[v] = u;
                    minCapacity[v] = Math.min(minCapacity[u], edge.residualCapacityTo(v));
                    queue.add(v);
                }
            }
        }

        if (parent[sink] == -1) return null;

        List<Integer> path = new ArrayList<>();
        int v = sink;
        while (v != source) {
            path.add(v);
            v = parent[v];
        }
        path.add(source);
        Collections.reverse(path);

        return new PathInfo(path, minCapacity[sink]);
    }

    // Compute maximum flow with detailed output
    public static void computeMaxFlow(FlowNetwork network, String filename) {
        int source = 0;
        int sink = network.V() - 1;
        int maxFlow = 0;
        int pathNumber = 0;

        // Start total time measurement
        long totalStartTime = System.nanoTime();

        // Find augmenting paths and update flows
        while (true) {
            // Start time for this path
            long pathStartTime = System.nanoTime();

            PathInfo pathInfo = findAugmentingPath(network, source, sink);
            if (pathInfo == null) break;

            pathNumber++;
            int bottleneck = pathInfo.bottleneck;
            maxFlow += bottleneck;

            // Print path and bottleneck
            System.out.printf("Path %d: %s | Bottleneck: %d | Flow Update: ",
                    pathNumber, pathInfo.path, bottleneck);

            // Update flows and print edge updates
            List<Integer> path = pathInfo.path;
            List<String> edgeUpdates = new ArrayList<>();
            for (int i = 1; i < path.size(); i++) {
                int u = path.get(i - 1);
                int v = path.get(i);
                for (FlowEdge edge : network.adj(u)) {
                    if (edge.to() == v && edge.residualCapacityTo(v) >= 0) {
                        int prevFlow = edge.flow();
                        edge.addResidualFlowTo(v, bottleneck);
                        int newFlow = edge.flow();
                        if (newFlow > prevFlow) { // Only show forward edges
                            edgeUpdates.add(String.format("Edge %d->%d: %d/%d",
                                    u, v, newFlow, edge.capacity()));
                        }
                        break;
                    }
                }
            }
            System.out.print(String.join(", ", edgeUpdates));

            // Measure time for this path
            long pathEndTime = System.nanoTime();
            double pathTimeMs = (pathEndTime - pathStartTime) / 1e6;

            // Print total flow and time for this path
            System.out.printf(" | Total Flow: %d | Time: %.2f ms%n", maxFlow, pathTimeMs);
        }

        // End total time measurement
        long totalEndTime = System.nanoTime();
        double totalTimeMs = (totalEndTime - totalStartTime) / 1e6;

        // Print final maximum flow and total time
        System.out.printf("--- FINAL MAX FLOW: %d | TOTAL TIME: %.2f ms ---\n", maxFlow, totalTimeMs);

        // Print flow distribution
        System.out.println("\nFINAL FLOW DISTRIBUTION:");
        System.out.println("EDGE | FLOW/CAPACITY");
        System.out.println("--------------------");
        Set<String> processedEdges = new HashSet<>();
        for (int v = 0; v < network.V(); v++) {
            for (FlowEdge edge : network.adj(v)) {
                if (edge.flow() > 0 && edge.from() == v) { // Only show forward edges with flow
                    String edgeKey = edge.from() + "->" + edge.to();
                    if (!processedEdges.contains(edgeKey)) {
                        System.out.printf("Edge %d->%d | Flow/Capacity | %d/%d\n",
                                edge.from(), edge.to(), edge.flow(), edge.capacity());
                        processedEdges.add(edgeKey);
                    }
                }
            }
        }
    }
}
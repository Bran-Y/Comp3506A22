// @edu:student-assignment

package uq.comp3506.a2;

// You may wish to import more/other structures too
import uq.comp3506.a2.structures.Edge;
import uq.comp3506.a2.structures.Vertex;
import uq.comp3506.a2.structures.Entry;
import uq.comp3506.a2.structures.TopologyType;
import uq.comp3506.a2.structures.Tunnel;
import uq.comp3506.a2.structures.UnorderedMap;
import java.util.ArrayList;
import java.util.List;

// This is part of COMP3506 Assignment 2. Students must implement their own solutions.

/**
 * Supplied by the COMP3506/7505 teaching team, Semester 2, 2025.
 * No bounds are provided. You should maximize efficiency where possible.
 * Below we use `S` and `U` to represent the generic data types that a Vertex
 * and an Edge can have, respectively, to avoid confusion between V and E in
 * typical graph nomenclature. That is, Vertex objects store data of type `S`
 * and Edge objects store data of type `U`.
 */
public class Problems {

    /**
     * Return a double representing the minimum radius of illumination required
     * to light the entire tunnel. Your answer will be accepted if
     * |your_ans - true_ans| is less than or equal to 0.000001
     * @param tunnelLength The length of the tunnel in question
     * @param lightIntervals The list of light intervals in [0, tunnelLength];
     * that is, all light interval values are >= 0 and <= tunnelLength
     * @return The minimum radius value required to illuminate the tunnel
     * or -1 if no light fittings are provided
     * Note: We promise that the input List will be an ArrayList.
     */
    public static double tunnelLighting(int tunnelLength, List<Integer> lightIntervals) {
        if (tunnelLength <= 0) {
            return -1.0;
        }
        if (lightIntervals == null || lightIntervals.size() == 0) {
            return -1.0;
        }
        //sort
        ArrayList<Integer> sorted = new ArrayList<>(lightIntervals);
        sorted.sort(null);
        //binary search
        double left = 0.0;
        double right = (double) tunnelLength;
        double delta = 0.000001;
        while (right - left > delta) {
            //mid overflow(要避免大数溢出)
            double mid = (left + right) / 2.0;
            //if mid satisfies the condition, update to right
            //if mid dees not
            //left = mid
            if (satisfiesCondition(sorted, mid, tunnelLength)) {
                right = mid;
            } else {
                left = mid;
            }
        } 
        return right;
    }
    private static boolean satisfiesCondition(ArrayList<Integer> sorted, double radius, int tunnelLength) {
        double coveredUpTo = 0.0;
        int lightIndex = 0;
        while (coveredUpTo < tunnelLength) {
            double maxReach = coveredUpTo;
            while (lightIndex < sorted.size()) {
                int lightPosition = sorted.get(lightIndex);
                if (lightPosition - radius <= coveredUpTo) {
                    maxReach = Math.max(maxReach, lightPosition + radius);
                    lightIndex++;
                } else {
                    break;
                }
            }
            if (maxReach != coveredUpTo) {
                coveredUpTo = maxReach;
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * Compute the TopologyType of the graph as represented by the given edgeList.
     * @param edgeList The list of edges making up the graph G; each is of type
     *              Edge, which stores two vertices and a value. Vertex identifiers
     *              are NOT GUARANTEED to be contiguous or in a given range.
     * @return The corresponding TopologyType.
     * Note: We promise not to provide any self loops, double edges, or isolated
     * vertices.
     */
    public static <S, U> TopologyType topologyDetection(List<Edge<S, U>> edgeList) {
        if (edgeList == null || edgeList.size() == 0) {
            return TopologyType.UNKNOWN;
        }
        
        // Build adjacency list and collect vertices
        UnorderedMap<Integer, ArrayList<Integer>> graph = new UnorderedMap<>();
        ArrayList<Integer> vertices = new ArrayList<>();
        
        for (Edge<S, U> edge : edgeList) {
            int v1 = edge.getVertex1().getId();
            int v2 = edge.getVertex2().getId();
            
            if (graph.get(v1) == null) {
                graph.put(v1, new ArrayList<>());
            }
            if (graph.get(v2) == null) {
                graph.put(v2, new ArrayList<>());
            }
            
            graph.get(v1).add(v2);
            graph.get(v2).add(v1);
            
            if (!vertices.contains(v1)) {
                vertices.add(v1);
            }
            if (!vertices.contains(v2)) {
                vertices.add(v2);
            }
        }
        
        int V = vertices.size(); // number of vertices
        int E = edgeList.size(); // number of edges
        
        // Check connectivity using DFS/BFS
        boolean isConnected = isGraphConnected(graph, vertices);
        
        // Check if it's a tree (connected and E = V - 1)
        boolean isTree = isConnected && (E == V - 1);
        
        // Determine topology type
        if (isConnected) {
            if (isTree) {
                return TopologyType.CONNECTED_TREE;
            } else {
                return TopologyType.CONNECTED_GRAPH;
            }
        } else {
            // Disconnected graph
            if (hasTreeComponent(graph, vertices)) {
                return TopologyType.DISCONNECTED_HYBRID;
            } else {
                return TopologyType.DISCONNECTED_TREE;
            }
        }
    }
    // Helper method to check if graph is connected
    private static boolean isGraphConnected(UnorderedMap<Integer, ArrayList<Integer>> graph, 
                                           ArrayList<Integer> vertices) {
        if (vertices.size() == 0) {
            return true;
        }
        
        // Use BFS to traverse from first vertex
        ArrayList<Boolean> visited = new ArrayList<>();
        for (int i = 0; i < vertices.size(); i++) {
            visited.add(false);
        }
        
        // Start BFS from first vertex
        ArrayList<Integer> queue = new ArrayList<>();
        queue.add(vertices.get(0));
        visited.set(0, true);
        int visitedCount = 1;
        
        while (queue.size() > 0) {
            int current = queue.remove(0);
            ArrayList<Integer> neighbors = graph.get(current);
            
            if (neighbors != null) {
                for (int neighbor : neighbors) {
                    int index = vertices.indexOf(neighbor);
                    if (index != -1 && !visited.get(index)) {
                        visited.set(index, true);
                        queue.add(neighbor);
                        visitedCount++;
                    }
                }
            }
        }
        
        return visitedCount == vertices.size();
    }
    
    // Helper method to check if disconnected graph has at least one tree component
    private static boolean hasTreeComponent(UnorderedMap<Integer, ArrayList<Integer>> graph, 
                                           ArrayList<Integer> vertices) {
        ArrayList<Boolean> visited = new ArrayList<>();
        for (int i = 0; i < vertices.size(); i++) {
            visited.add(false);
        }
        
        // Find all connected components
        for (int i = 0; i < vertices.size(); i++) {
            if (!visited.get(i)) {
                // Check if this component is a tree
                ArrayList<Integer> component = new ArrayList<>();
                int edgeCount = getComponent(graph, vertices, visited, i, component);
                
                // A tree has edges = vertices - 1
                if (edgeCount == component.size() - 1) {
                    return true; // Found a tree component
                }
            }
        }
        
        return false; // All components have cycles, so it's a disconnected tree (forest)
    }
    
    // Helper method to get a connected component and count its edges
    private static int getComponent(UnorderedMap<Integer, ArrayList<Integer>> graph,
                                    ArrayList<Integer> vertices,
                                    ArrayList<Boolean> visited,
                                    int startIndex,
                                    ArrayList<Integer> component) {
        ArrayList<Integer> queue = new ArrayList<>();
        queue.add(vertices.get(startIndex));
        visited.set(startIndex, true);
        component.add(vertices.get(startIndex));
        int edgeCount = 0;
        
        while (queue.size() > 0) {
            int current = queue.remove(0);
            ArrayList<Integer> neighbors = graph.get(current);
            
            if (neighbors != null) {
                edgeCount += neighbors.size();
                for (int neighbor : neighbors) {
                    int index = vertices.indexOf(neighbor);
                    if (index != -1 && !visited.get(index)) {
                        visited.set(index, true);
                        queue.add(neighbor);
                        component.add(neighbor);
                    }
                }
            }
        }
        
        // Each edge is counted twice (once from each endpoint)
        return edgeCount / 2;
    }
    
 
    /**
     * Compute the list of reachable destinations and their minimum costs.
     * @param edgeList The list of edges making up the graph G; each is of type
     *              Edge, which stores two vertices and a value. Vertex identifiers
     *              are NOT GUARANTEED to be contiguous or in a given range.
     * @param origin The origin vertex object.
     * @param threshold The total time the driver can drive before a break is required.
     * @return an ArrayList of Entry types, where the first element is the identifier
     *         of a reachable station (within the time threshold), and the second
     *         element is the minimum cost of reaching that given station. The
     *         order of the list is not important.
     * Note: We promise that S will be of Integer type.
     * Note: You should return the origin in your result with a cost of zero.
     */
    public static <S, U> List<Entry<Integer, Integer>> routeManagement(List<Edge<S, U>> edgeList,
                                                          Vertex<S> origin, int threshold) {
        ArrayList<Entry<Integer, Integer>> answers = new ArrayList<>();
        return answers;
    }

    /**
     * Compute the tunnel that if flooded will cause the maximal flooding of 
     * the network
     * @param tunnels A list of the tunnels to consider; see Tunnel.java
     * @return The identifier of the Tunnel that would cause maximal flooding.
     * Note that for Tunnel A to drain into some other tunnel B, the distance
     * from A to B must be strictly less than the radius of A plus an epsilon
     * allowance of 0.000001. 
     * Note also that all identifiers in tunnels are GUARANTEED to be in the
     * range [0, n-1] for n unique tunnels.
     */
    public static int totallyFlooded(List<Tunnel> tunnels) {
        return -1;
    }

    /**
     * Compute the number of sites that cannot be infiltrated from the given starting sites.
     * @param sites The list of unique site identifiers. A site identifier is GUARANTEED to be
     *              non-negative, starting from 0 and counting upwards to n-1.
     * @param rules The infiltration rule. The right-hand side of a rule is represented by a list
     *             of lists of site identifiers (as is done in the assignment specification). The
     *             left-hand side of a rule is given by the rule's index in the parameter `rules`
     *             (i.e. the rule whose left-hand side is 4 will be at index 4 in the parameter
     *              `rules` and can be accessed with `rules.get(4)`).
     * @param startingSites The list of site identifiers to begin your infiltration from.
     * @return The number of sites which cannot be infiltrated.
     */
    public static int susDomination(List<Integer> sites, List<List<List<Integer>>> rules,
                                     List<Integer> startingSites) {
        return -1;
    }
}

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
        // TopologyType dummy = TopologyType.UNKNOWN;
        // return dummy;
        if (edgeList == null || edgeList.size() == 0) {
            return TopologyType.UNKNOWN;
        }
        //build an unordered map to store the vertices ->[v1,v2..]
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
        
        int V = vertices.size(); // 顶点数
        int E = edgeList.size(); // 边数
        
        // 找出所有连通分量
        ArrayList<Component> components = findComponents(graph, vertices);
        
        // 根据连通分量的数量和性质判断拓扑类型
        if (components.size() == 1) {
            // 只有一个连通分量
            Component comp = components.get(0);
            if (comp.edgeCount == comp.vertexCount - 1) {
                return TopologyType.CONNECTED_TREE;
            } else {
                return TopologyType.CONNECTED_GRAPH;
            }
        } else {
            // 多个连通分量
            int treeCount = 0;
            int graphCount = 0;
            
            for (Component comp : components) {
                if (comp.edgeCount == comp.vertexCount - 1) {
                    treeCount++;
                } else {
                    graphCount++;
                }
            }
            
            if (treeCount == components.size()) {
                return TopologyType.FOREST; // 所有分量都是树
            } else if (graphCount == components.size()) {
                return TopologyType.DISCONNECTED_GRAPH; // 所有分量都有环
            } else {
                return TopologyType.HYBRID; // 混合：有树也有图
            }
        }
    }
    
    // 辅助类：存储连通分量信息
    private static class Component {
        int vertexCount;
        int edgeCount;
        
        Component(int vertexCount, int edgeCount) {
            this.vertexCount = vertexCount;
            this.edgeCount = edgeCount;
        }
    }
    
    // 找出所有连通分量
    private static ArrayList<Component> findComponents(
            UnorderedMap<Integer, ArrayList<Integer>> graph, 
            ArrayList<Integer> vertices) {
        
        ArrayList<Component> components = new ArrayList<>();
        UnorderedMap<Integer, Boolean> visited = new UnorderedMap<>();
        
        // 初始化 visited
        for (int v : vertices) {
            visited.put(v, false);
        }
        
        // 对每个未访问的顶点进行 BFS
        for (int v : vertices) {
            if (!visited.get(v)) {
                Component comp = bfsComponent(graph, v, visited);
                components.add(comp);
            }
        }
        
        return components;
    }
    
    // BFS 遍历一个连通分量，返回该分量的信息
    private static Component bfsComponent(
            UnorderedMap<Integer, ArrayList<Integer>> graph,
            int start,
            UnorderedMap<Integer, Boolean> visited) {
        
        ArrayList<Integer> queue = new ArrayList<>();
        queue.add(start);
        visited.put(start, true);
        
        int vertexCount = 0;
        int edgeCount = 0;
        
        while (queue.size() > 0) {
            int current = queue.remove(0);
            vertexCount++;
            
            ArrayList<Integer> neighbors = graph.get(current);
            if (neighbors != null) {
                // 统计边数（每条边会被两个端点各计一次）
                edgeCount += neighbors.size();
                
                for (int neighbor : neighbors) {
                    Boolean isVisited = visited.get(neighbor);
                    if (isVisited != null && !isVisited) {
                        visited.put(neighbor, true);
                        queue.add(neighbor);
                    }
                }
            }
        }
        
        // 每条边被计数两次，所以除以2
        edgeCount = edgeCount / 2;
        
        return new Component(vertexCount, edgeCount);
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

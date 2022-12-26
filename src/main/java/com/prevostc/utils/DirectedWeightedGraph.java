package com.prevostc.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.val;

public class DirectedWeightedGraph<TNodeData> {

    @Getter
    private Map<String, TNodeData> nodes; // id to flow rate map
    private Map<String, Map<String, Integer>> edges; // "from" to "to" to "distance" map

    public DirectedWeightedGraph() {
        nodes = new HashMap<>();
        edges = new HashMap<>();
    }

    public void addNode(String id, TNodeData data) {
        nodes.put(id, data);
    }

    public TNodeData getNode(String id) {
        return nodes.get(id);
    }

    public void addEdge(String from, String to, Integer distance) {
        edges.computeIfAbsent(from, k -> new HashMap<>()).put(to, distance);
    }

    public Integer edgeCost(String from, String to) {
        if (from.equals(to)) {
            return 0;
        }
        return edges.get(from).get(to);
    }

    public void removeNode(String id) {
        nodes.remove(id);
        edges.remove(id);
        edges.values().forEach(m -> m.remove(id));
    }

    public Set<String> children(String id) {
        return edges.get(id).keySet();
    }

    /**
     * Find the shortest path length from a node to all other node
     */
    public Map<String, Integer> allShortestPathLengthFrom(String from) {
        val visited = new HashSet<String>();
        visited.add(from);

        Set<String> current = new HashSet<>();
        current.add(from);

        var depth = 1;

        Map<String, Integer> shortestPathLengths = new HashMap<>();
        while (!current.isEmpty()) {
            Set<String> allChildren = current.stream().flatMap(id -> this.children(id).stream())
                    .filter(Predicate.not(visited::contains)).collect(Collectors.toSet());
            for (val id : allChildren) {
                visited.add(id);
                shortestPathLengths.put(id, depth);
            }
            current = allChildren;
            depth++;
        }
        return shortestPathLengths;
    }

}
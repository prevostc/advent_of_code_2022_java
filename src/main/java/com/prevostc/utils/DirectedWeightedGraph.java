package com.prevostc.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import lombok.val;

public class DirectedWeightedGraph<TNodeId, TNodeData> {

    private Map<TNodeId, TNodeData> nodes; // id to flow rate map
    private Map<TNodeId, Map<TNodeId, Integer>> edges; // "from" to "to" to "distance" map

    private Map<TNodeId, Set<TNodeId>> childrenCache;
    // it is considerably cheaper to iterate a list than a map keyset
    private List<TNodeId> nodeListCache;

    public DirectedWeightedGraph() {
        nodes = new HashMap<>();
        nodeListCache = new ArrayList<>();
        edges = new HashMap<>();
        childrenCache = new HashMap<>();
    }

    public void addNode(TNodeId id, TNodeData data) {
        nodes.put(id, data);
        nodeListCache.add(id);
    }

    public TNodeData getNode(TNodeId id) {
        return nodes.get(id);
    }

    public List<TNodeId> getNodes() {
        return nodeListCache;
    }

    public void addEdge(TNodeId from, TNodeId to, Integer distance) {
        edges.computeIfAbsent(from, k -> new HashMap<>()).put(to, distance);
        childrenCache.computeIfAbsent(from, k -> new HashSet<>()).add(to);
    }

    public void removeIncidentEdges(TNodeId to) {
        edges.values().forEach(m -> m.remove(to));
        for (val children : childrenCache.values()) {
            children.remove(to);
        }
    }

    public void removeEdge(TNodeId from, TNodeId to) {
        edges.get(from).remove(to);
        childrenCache.get(from).remove(to);
    }

    public Integer edgeCost(TNodeId from, TNodeId to) {
        if (from.equals(to)) {
            return 0;
        }
        return edges.get(from).get(to);
    }

    public void removeNode(TNodeId id) {
        nodes.remove(id);
        nodeListCache.remove(id);
        edges.remove(id);
        edges.values().forEach(m -> m.remove(id));
        childrenCache.remove(id);
        childrenCache.values().forEach(s -> s.remove(id));
    }

    public Set<TNodeId> children(TNodeId id) {
        return childrenCache.get(id);
    }

    /**
     * Find the shortest path length from a node to all other node
     */
    public Map<TNodeId, Integer> allShortestPathLengthFrom(TNodeId from) {
        Set<TNodeId> visited = new HashSet<>();
        visited.add(from);

        Set<TNodeId> current = new HashSet<>();
        current.add(from);

        var depth = 1;

        Map<TNodeId, Integer> shortestPathLengths = new HashMap<>();
        while (!current.isEmpty()) {
            Set<TNodeId> allChildren = current.stream().flatMap(id -> this.children(id).stream())
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
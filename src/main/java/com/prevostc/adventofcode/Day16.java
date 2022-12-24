package com.prevostc.adventofcode;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.prevostc.utils.FileReader;

import lombok.Setter;
import lombok.val;

public class Day16 {

    FileReader fileReader = new FileReader();

    public Integer part1(String inputFilePath) throws IOException {
        Graph g = parseInput(inputFilePath);
        g.setSearchTime(30);
        // AA always has flow rate 0 so it will be compressed out
        // set it to 1 so we can start from it
        g.nodes.put("AA", 1);
        g.compress();
        g.nodes.put("AA", 0);

        return g.findMaxFlow(0, 0, Set.of(), "AA");
    }

    public Integer part2(String inputFilePath) throws IOException {
        Graph g = parseInput(inputFilePath);
        g.setSearchTime(26);
        // AA always has flow rate 0 so it will be compressed out
        // set it to 1 so we can start from it
        g.nodes.put("AA", 1);
        g.compress();
        g.nodes.put("AA", 0);

        return g.findMaxFlow2Headed(0, 0, 0, Set.of(), "AA", "AA");
    }

    private class Graph {
        @Setter
        private int searchTime;
        private Map<String, Integer> nodes; // id to flow rate map
        private Map<String, Map<String, Integer>> edges; // "from" to "to" to "distance" map

        Graph() {
            nodes = new HashMap<>();
            edges = new HashMap<>();
            this.searchTime = 0;
        }

        void addNode(String id, int flowRate) {
            nodes.put(id, flowRate);
        }

        void addEdge(String from, String to, int distance) {
            edges.computeIfAbsent(from, k -> new HashMap<>()).put(to, distance);
        }

        void addEdgeIfShorter(String from, String to, int distance) {
            val f = edges.computeIfAbsent(from, k -> new HashMap<>());
            val d = f.get(to);
            if (d == null || d > distance) {
                f.put(to, distance);
            }
        }

        int edgeWeight(String from, String to) {
            return edges.get(from).get(to);
        }

        int nodeFlowRate(String id) {
            return nodes.get(id);
        }

        void removeNode(String id) {
            nodes.remove(id);
            edges.remove(id);
            edges.values().forEach(m -> m.remove(id));
        }

        Set<String> children(String id) {
            return edges.get(id).keySet();
        }

        // remove nodes with flow rate 0
        void compress() {
            // for each non zero nodes
            val nonZero = nodes.entrySet().stream().filter(e -> e.getValue() != 0).map(Map.Entry::getKey)
                    .collect(Collectors.toSet());

            Map<String, Map<String, Integer>> newEdges = new HashMap<>();

            // find the distance to other nonZero nodes
            for (val id : nonZero) {
                this.minDepthFrom(id, (toId, depth) -> {
                    if (id.equals(toId)) {
                        return;
                    }
                    if (nonZero.contains(toId)) {
                        newEdges.computeIfAbsent(id, k -> new HashMap<>()).put(toId, depth);
                    }
                });
            }

            // remove zero nodes
            val toRemove = nodes.entrySet().stream().filter(e -> e.getValue() == 0).map(Map.Entry::getKey)
                    .collect(Collectors.toSet());
            toRemove.forEach(this::removeNode);
            // merge new edges into edges
            newEdges.forEach(
                    (from, toMap) -> toMap.forEach((to, distance) -> this.addEdgeIfShorter(from, to, distance)));
        }

        @FunctionalInterface
        private interface DfsCallback {
            void apply(String id, int depth);
        }

        void minDepthFrom(String from, DfsCallback callback) {
            val visited = new HashSet<String>();
            visited.add(from);

            Set<String> current = new HashSet<String>();
            current.add(from);

            var depth = 1;

            while (!current.isEmpty()) {
                val allChildren = current.stream().flatMap(id -> this.children(id).stream())
                        .filter(Predicate.not(visited::contains)).collect(Collectors.toSet());
                for (val id : allChildren) {
                    visited.add(id);
                    callback.apply(id, depth);
                }
                current = allChildren;
                depth++;
            }
        }

        public int findMaxFlow(int distance, int depth, Set<String> visited, String pos) {
            if (distance + depth > this.searchTime) {
                return 0;
            }
            val v = new HashSet<String>(visited);
            v.add(pos);

            int posFlow = (this.searchTime - distance - depth) * nodeFlowRate(pos);
            val c = this.children(pos).stream().filter(Predicate.not(v::contains)).collect(Collectors.toSet());
            if (c.isEmpty()) {
                return posFlow;
            }

            var maxFlow = 0;
            for (val child : c) {
                val flow = posFlow + findMaxFlow(distance + edgeWeight(pos, child), depth + 1, v, child);
                maxFlow = Math.max(maxFlow, flow);
            }
            return maxFlow;
        }

        // same logic as above, except we have 2 heads that share a "visit" history
        public int findMaxFlow2Headed(int distance1, int distance2, int depth, Set<String> visited, String pos1,
                String pos2) {
            // one of the heads is done, it's an invalid combination
            if (distance1 + depth > this.searchTime) {
                return findMaxFlow(distance2, depth, visited, pos2);
            } else if (distance2 + depth > this.searchTime) {
                return findMaxFlow(distance1, depth, visited, pos1);
            }

            val v = new HashSet<String>(visited);
            v.add(pos1);
            v.add(pos2);
            int posFlow1 = (this.searchTime - distance1 - depth) * nodeFlowRate(pos1);
            int posFlow2 = (this.searchTime - distance2 - depth) * nodeFlowRate(pos2);

            val c1 = this.children(pos1).stream().filter(Predicate.not(v::contains)).collect(Collectors.toSet());
            val c2 = this.children(pos2).stream().filter(Predicate.not(v::contains)).collect(Collectors.toSet());

            if (c1.isEmpty()) {
                return posFlow1 + findMaxFlow(distance2, depth, v, pos2);
            } else if (c2.isEmpty()) {
                return posFlow2 + findMaxFlow(distance1, depth, v, pos1);
            }

            var maxFlow = 0;
            // hopefully that is not too much
            for (val cc1 : c1) {
                for (val cc2 : c2) {
                    if (cc1.compareTo(cc2) == 0) {
                        continue;
                    }
                    val flow = posFlow1 + posFlow2 + findMaxFlow2Headed(
                            distance1 + edgeWeight(pos1, cc1),
                            distance2 + edgeWeight(pos2, cc2),
                            depth + 1, v, cc1, cc2);
                    maxFlow = Math.max(maxFlow, flow);
                }
            }
            return maxFlow;
        }
    }

    private final static Pattern INPUT_PATTERN = Pattern
            .compile("Valve (\\w+) has flow rate=(\\d+); tunnels? leads? to valves? ([A-Z, ]+)");

    private Graph parseInput(String inputFilePath) throws IOException {
        var g = new Graph();
        fileReader.readAllLines(inputFilePath).stream().filter(Predicate.not(String::isEmpty))
                .map(INPUT_PATTERN::matcher)
                .filter(Matcher::matches)
                .forEach(m -> {
                    var id = m.group(1);
                    var flowRate = Integer.parseInt(m.group(2));
                    g.addNode(id, flowRate);
                    var tos = m.group(3).split(", ");
                    for (var to : tos) {
                        g.addEdge(id, to, 1);
                    }
                });
        return g;
    }
}

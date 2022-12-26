package com.prevostc.adventofcode;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.prevostc.utils.DirectedWeightedGraph;
import com.prevostc.utils.FileReader;

import lombok.val;

public class Day16 {

    FileReader fileReader = new FileReader();

    private record State1Head(Set<String> visited, int minutesLeft, String pos, int flow) {
    }

    public Integer part1(String inputFilePath) throws IOException {
        val graph = parseInput(inputFilePath);

        // AA always has flow rate 0 so it will be compressed out
        // set it to 1 so we can start from it
        graph.addNode("AA", 1);
        this.compressGraph(graph);
        graph.addNode("AA", 0);

        return solve1head(graph, new State1Head(Set.of("AA"), 30, "AA", 0));
    }

    private int solve1head(DirectedWeightedGraph<Integer> graph, State1Head initialState) {

        Set<State1Head> stateCache = new HashSet<>();
        val sDeque = new ArrayDeque<State1Head>();
        sDeque.add(initialState);

        int maxFlow = 0;
        while (!sDeque.isEmpty()) {
            val s = sDeque.pop();

            if (stateCache.contains(s)) {
                continue;
            }
            stateCache.add(s);

            if (s.minutesLeft <= 0) {
                continue;
            }

            int posFlow = s.minutesLeft * graph.getNode(s.pos);
            int totalFlow = s.flow() + posFlow;
            for (val c : graph.children(s.pos())) {
                if (s.visited().contains(c)) {
                    continue;
                }
                val visited = new HashSet<>(s.visited);
                visited.add(s.pos());
                sDeque.add(new State1Head(visited, s.minutesLeft - graph.edgeCost(s.pos(), c) - 1 /* open time */, c,
                        totalFlow));
            }

            maxFlow = Math.max(maxFlow, totalFlow);
        }

        return maxFlow;
    }

    // same logic as 1 headed but we share a visited state
    private record State2Head(Set<String> visited, int minutesLeftH1, String posH1, int minutesLeftH2, String posH2,
            int flow) {

        public State2Head simplify() {
            if (minutesLeftH1 <= 0) {
                return new State2Head(visited, 0, "AA", minutesLeftH2, posH2, flow);
            }
            if (minutesLeftH2 <= 0) {
                return new State2Head(visited, 0, "AA", minutesLeftH1, posH1, flow);
            }
            return this;
        }

    }

    public Integer part2(String inputFilePath) throws IOException {
        val graph = parseInput(inputFilePath);
        val startPos = "AA";
        // AA always has flow rate 0 so it will be compressed out
        // set it to 1 so we can start from it
        graph.addNode(startPos, 1);
        this.compressGraph(graph);
        graph.addNode(startPos, 0);

        Set<State2Head> stateCache = new HashSet<>();
        val sDeque = new ArrayDeque<State2Head>();

        int time = 26;
        sDeque.add(new State2Head(Set.of(startPos), time, startPos, time, startPos, 0));

        int maxFlow = 0;
        while (!sDeque.isEmpty()) {
            val s = sDeque.removeLast().simplify();

            if (stateCache.contains(s)) {
                continue;
            }
            stateCache.add(s);

            // System.out.println(sDeque.size());
            // System.out.println(s.visited + " " + s.posH1 + " " + s.posH2);

            val visited = new HashSet<>(s.visited);
            visited.add(s.posH1);
            visited.add(s.posH2);

            int totalFlow = s.flow;
            int posFlowH1 = s.minutesLeftH1 * graph.getNode(s.posH1);
            int posFlowH2 = s.minutesLeftH2 * graph.getNode(s.posH2);
            if (s.minutesLeftH1 <= 0) {
                totalFlow += solve1head(graph, new State1Head(visited, s.minutesLeftH2, s.posH2, 0));
            } else if (s.minutesLeftH2 <= 0) {
                totalFlow += solve1head(graph, new State1Head(visited, s.minutesLeftH1, s.posH1, 0));
            } else {
                totalFlow += posFlowH1 + posFlowH2;

                // test if we have enough time left to beat our current score
                int maxMinutesLeft = Math.max(s.minutesLeftH1, s.minutesLeftH2);
                int totalFlowLeft = graph.getNodes().keySet().stream().filter(n -> !visited.contains(n))
                        .mapToInt(n -> (maxMinutesLeft - 1) * graph.getNode(n)).sum();
                if (totalFlow + totalFlowLeft < maxFlow) {
                    continue;
                }

                val h1Children = graph.children(s.posH1).stream()
                        .filter(c -> !visited.contains(c))
                        .filter(c -> !c.equals(s.posH2) && !c.equals(s.posH1))
                        .sorted((a, b) -> graph.getNode(b) - graph.getNode(a)) // order by flow rate
                        .collect(Collectors.toList());
                val h2Children = graph.children(s.posH2).stream()
                        .filter(c -> !visited.contains(c))
                        .filter(c -> !c.equals(s.posH2) && !c.equals(s.posH1)) // order by flow rate
                        .sorted((a, b) -> graph.getNode(b) - graph.getNode(a))
                        .collect(Collectors.toList());

                for (val cH1 : h1Children) {
                    for (val cH2 : h2Children) {
                        if (cH1.equals(cH2)) {
                            continue;
                        }

                        // only consider half of the graph
                        if (s.posH1.equals("AA") && s.posH2.equals("AA") && s.posH1.compareTo(s.posH2) > 0) {
                            continue;
                        }

                        val h1MinutesLeft = s.minutesLeftH1 - graph.edgeCost(s.posH1, cH1) - 1 /* open time */;
                        val h2MinutesLeft = s.minutesLeftH2 - graph.edgeCost(s.posH2, cH2) - 1 /* open time */;
                        sDeque.add(new State2Head(visited, h1MinutesLeft, cH1, h2MinutesLeft, cH2, totalFlow));
                    }
                }

            }

            if (totalFlow > maxFlow) {
                System.out.println("Better solution: " + totalFlow);
            }

            maxFlow = Math.max(maxFlow, totalFlow);
        }

        return maxFlow;
    }

    /**
     * Transform a large graph into a smaller one by removing zero nodes
     * and adding edges where the edge value is the time to travel to this edge
     */
    private void compressGraph(DirectedWeightedGraph<Integer> graph) {
        // for each non zero nodes
        val nonZero = graph.getNodes().entrySet().stream().filter(e -> e.getValue() != 0).map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        Map<String, Map<String, Integer>> newEdges = new HashMap<>();

        // find the distance to other nonZero nodes
        for (val id : nonZero) {
            val shortestPathsLength = graph.allShortestPathLengthFrom(id);
            for (val entry : shortestPathsLength.entrySet()) {
                val toId = entry.getKey();
                if (nonZero.contains(toId) && !id.equals(toId)) {
                    newEdges.computeIfAbsent(id, k -> new HashMap<>()).put(toId, shortestPathsLength.get(toId));
                }
            }
        }

        // remove zero nodes
        val toRemove = graph.getNodes().entrySet().stream().filter(e -> e.getValue() == 0).map(Map.Entry::getKey)
                .collect(Collectors.toSet());
        toRemove.forEach(graph::removeNode);
        // merge new edges into edges
        newEdges.forEach((from, toMap) -> toMap.forEach((to, distance) -> graph.addEdge(from, to, distance)));
    }

    private final static Pattern INPUT_PATTERN = Pattern
            .compile("Valve (\\w+) has flow rate=(\\d+); tunnels? leads? to valves? ([A-Z, ]+)");

    private DirectedWeightedGraph<Integer> parseInput(String inputFilePath) throws IOException {
        val g = new DirectedWeightedGraph<Integer>();
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

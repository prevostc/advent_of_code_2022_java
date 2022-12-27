package com.prevostc.adventofcode;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
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
    private static final String startNode = "AA";
    private static int startNodeBit;

    FileReader fileReader = new FileReader();
    Set<State1Head> stateCache1Head = new HashSet<>();
    Set<State2Head> stateCache2Head = new HashSet<>();
    DirectedWeightedGraph<Integer, Integer> graph;
    Map<String, Integer> nodeIdToBit = new HashMap<>();

    private record State1Head(BitSet visited, int minutesLeft, int pos, int flow) {
    }

    public Integer part1(String inputFilePath) throws IOException {
        init(inputFilePath);
        val visited = new BitSet();
        visited.set(startNodeBit);
        return solve1head(graph, new State1Head(visited, 30, startNodeBit, 0));
    }

    private int solve1head(DirectedWeightedGraph<Integer, Integer> graph, State1Head initialState) {

        val sDeque = new ArrayDeque<State1Head>();
        sDeque.add(initialState);

        int maxFlow = 0;
        while (!sDeque.isEmpty()) {
            val s = sDeque.pop();

            if (stateCache1Head.contains(s)) {
                continue;
            }
            stateCache1Head.add(s);

            if (s.minutesLeft <= 0) {
                continue;
            }

            int posFlow = s.minutesLeft * graph.getNode(s.pos);
            int totalFlow = s.flow() + posFlow;
            for (val c : graph.children(s.pos())) {
                if (s.visited().get(c)) {
                    continue;
                }
                val visited = new BitSet();
                visited.or(s.visited());
                visited.set(c);

                sDeque.add(new State1Head(visited, s.minutesLeft - graph.edgeCost(s.pos(), c) - 1 /* open time */, c,
                        totalFlow));
            }

            maxFlow = Math.max(maxFlow, totalFlow);
        }

        return maxFlow;
    }

    // same logic as 1 headed but we share a visited state
    private record State2Head(BitSet visited, int minutesLeftH1, int posH1, int minutesLeftH2, int posH2,
            int flow) {

        public State2Head simplify() {
            if (minutesLeftH1 <= 0) {
                return new State2Head(visited, 0, startNodeBit, minutesLeftH2, posH2, flow);
            }
            if (minutesLeftH2 <= 0) {
                return new State2Head(visited, 0, startNodeBit, minutesLeftH1, posH1, flow);
            }
            return this;
        }

    }

    public Integer part2(String inputFilePath) throws IOException {
        init(inputFilePath);

        val sDeque = new ArrayDeque<State2Head>();

        int time = 26;
        val visitedInit = new BitSet();
        visitedInit.set(startNodeBit);
        sDeque.add(new State2Head(visitedInit, time, startNodeBit, time, startNodeBit, 0));

        int maxFlow = 0;
        while (!sDeque.isEmpty()) {
            val s = sDeque.removeLast().simplify();

            if (stateCache2Head.contains(s)) {
                continue;
            }
            stateCache2Head.add(s);

            val visited = new BitSet();
            visited.or(s.visited);
            visited.set(s.posH1);
            visited.set(s.posH2);

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
                int totalFlowLeft = 0;
                for (val node : graph.getNodes()) {
                    if (!visited.get(node)) {
                        totalFlowLeft += (maxMinutesLeft - 1) * graph.getNode(node);
                    }
                }
                if (totalFlow + totalFlowLeft < maxFlow) {
                    continue;
                }
                for (val cH1 : graph.children(s.posH1)) {
                    if (s.visited.get(cH1) || cH1.equals(s.posH1) || cH1.equals(s.posH2)) {
                        continue;
                    }
                    for (val cH2 : graph.children(s.posH2)) {
                        if (s.visited.get(cH2) || cH2.equals(s.posH1) || cH2.equals(s.posH2)) {
                            continue;
                        }
                        if (cH1.equals(cH2)) {
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
    private void compressGraph(DirectedWeightedGraph<Integer, Integer> graph) {
        // for each non zero nodes
        val nonZero = graph.getNodes().stream().filter(e -> graph.getNode(e) != 0)
                .collect(Collectors.toSet());

        Map<Integer, Map<Integer, Integer>> newEdges = new HashMap<>();

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
        val toRemove = graph.getNodes().stream().filter(e -> graph.getNode(e) == 0).collect(Collectors.toSet());
        toRemove.forEach(graph::removeNode);
        // merge new edges into edges
        newEdges.forEach((from, toMap) -> toMap.forEach((to, distance) -> graph.addEdge(from, to, distance)));
    }

    private final static Pattern INPUT_PATTERN = Pattern
            .compile("Valve (\\w+) has flow rate=(\\d+); tunnels? leads? to valves? ([A-Z, ]+)");

    private void init(String inputFilePath) throws IOException {
        this.graph = new DirectedWeightedGraph<>();

        fileReader.readAllLines(inputFilePath).stream().filter(Predicate.not(String::isEmpty))
                .map(INPUT_PATTERN::matcher)
                .filter(Matcher::matches)
                .forEach(m -> {
                    var id = m.group(1);
                    var flowRate = Integer.parseInt(m.group(2));
                    val nodeBit = nodeIdToBit.computeIfAbsent(id, k -> nodeIdToBit.size());
                    nodeIdToBit.put(id, nodeBit);
                    graph.addNode(nodeBit, flowRate);
                    var tos = m.group(3).split(", ");
                    for (var to : tos) {
                        val toBit = nodeIdToBit.computeIfAbsent(to, k -> nodeIdToBit.size());
                        nodeIdToBit.put(to, toBit);
                        graph.addEdge(nodeBit, toBit, 1);
                    }
                });

        startNodeBit = nodeIdToBit.get(startNode);
        // AA always has flow rate 0 so it will be compressed out
        // set it to 1 so we can start from it
        graph.addNode(startNodeBit, 1);
        this.compressGraph(graph);
        graph.addNode(startNodeBit, 0);

        // remove edged going to AA so we don't consider getting back to it
        graph.removeIncidentEdges(startNodeBit);

    }
}

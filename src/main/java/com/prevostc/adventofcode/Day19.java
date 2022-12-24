package com.prevostc.adventofcode;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.prevostc.utils.FileReader;

import lombok.val;

public class Day19 {

    private record Blueprint(
            int id,
            int oreRobotOreCost,
            int clayRobotOreCost,
            int obsidianRobotOreCost,
            int obsidianRobotClayCost,
            int geodeRobotOreCost,
            int geodeRobotObsidianCost) {
        public int maxOreCost() {
            return Math.max(oreRobotOreCost,
                    Math.max(clayRobotOreCost, Math.max(obsidianRobotOreCost, geodeRobotOreCost)));
        }

        public int maxClayCost() {
            return Math.max(obsidianRobotClayCost, 0);
        }

        public int maxObsidianCost() {
            return Math.max(geodeRobotObsidianCost, 0);
        }
    }

    private record State(
            int ore,
            int clay,
            int obsidian,
            int geode,
            int oreRobots,
            int clayRobots,
            int obsidianRobots,
            int geodeRobots,
            int time) {

        public State toCacheable() {
            // we don't care at which time we are when we cache
            return new State(ore, clay, obsidian, geode, oreRobots, clayRobots, obsidianRobots, geodeRobots, 0);
        }

        // help out cut the search space
        public State simplify(Blueprint blueprint) {
            int or = ore;
            int cl = clay;
            int ob = obsidian;
            int ge = geode;

            int orR = oreRobots;
            int clR = clayRobots;
            int obR = obsidianRobots;
            int geR = geodeRobots;

            int orMc = blueprint.maxOreCost();
            int clMc = blueprint.maxClayCost();
            int obMc = blueprint.maxObsidianCost();

            // no point in having more robots that we can build each round
            orR = Math.min(orR, orMc);
            clR = Math.min(clR, clMc);
            obR = Math.min(obR, obMc);

            // if we have not build something and we reached the max cost, no need to
            // explore further
            or = Math.min(or, time * orMc - orR * (time - 1));
            cl = Math.min(cl, time * clMc - clR * (time - 1));
            ob = Math.min(ob, time * obMc - obR * (time - 1));

            return new State(or, cl, ob, ge, orR, clR, obR, geR, time);
        }

        public State buildNothing() {
            return new State(ore + oreRobots, clay + clayRobots, obsidian + obsidianRobots,
                    geode + geodeRobots, oreRobots, clayRobots, obsidianRobots, geodeRobots,
                    time + 1);
        }

        public State buildOreRobot(int oreCost) {
            return new State(ore - oreCost + oreRobots, clay + clayRobots, obsidian + obsidianRobots,
                    geode + geodeRobots, oreRobots + 1, clayRobots, obsidianRobots, geodeRobots,
                    time + 1);
        }

        public State buildClayRobot(int oreCost) {
            return new State(ore - oreCost + oreRobots, clay + clayRobots, obsidian + obsidianRobots,
                    geode + geodeRobots, oreRobots, clayRobots + 1, obsidianRobots, geodeRobots,
                    time + 1);
        }

        public State buildObsidianRobot(int oreCost, int clayCost) {
            return new State(ore - oreCost + oreRobots, clay - clayCost + clayRobots, obsidian + obsidianRobots,
                    geode + geodeRobots, oreRobots, clayRobots, obsidianRobots + 1, geodeRobots,
                    time + 1);
        }

        public State buildGeodeRobot(int oreCost, int obsidianCost) {
            return new State(ore - oreCost + oreRobots, clay + clayRobots, obsidian - obsidianCost + obsidianRobots,
                    geode + geodeRobots, oreRobots, clayRobots, obsidianRobots, geodeRobots + 1,
                    time + 1);
        }
    }

    FileReader fileReader = new FileReader();

    public Integer part1(String inputFilePath) throws IOException {
        val blueprints = parse(inputFilePath);
        var total = 0;
        for (val blueprint : blueprints) {
            val maxGeodes = solve(blueprint, 24);
            val quality = maxGeodes * blueprint.id();
            total += quality;
            System.out.println(blueprint.id() + " maxGeodes = " + maxGeodes);
        }
        return total;
    }

    public Integer part2(String inputFilePath) throws IOException {
        var blueprints = parse(inputFilePath);
        blueprints = blueprints.subList(0, Math.min(blueprints.size(), 3));

        var total = 1;
        for (val blueprint : blueprints) {
            val maxGeodes = solve(blueprint, 32);
            total *= maxGeodes;
            System.out.println(blueprint.id() + " maxGeodes = " + maxGeodes);
        }
        return total;
    }

    private int solve(Blueprint blueprint, int minutes) {

        Deque<State> sDeque = new ArrayDeque<>();
        Set<State> visited = new HashSet<>();
        sDeque.add(new State(0, 0, 0, 0, 1, 0, 0, 0, 0));

        int maxGeode = 0;

        while (!sDeque.isEmpty()) {
            val s = sDeque.pop().simplify(blueprint);

            val cs = s.toCacheable();
            if (visited.contains(cs)) {
                continue;
            }
            visited.add(cs);

            val ore = s.ore();
            val clay = s.clay();
            val obsidian = s.obsidian();
            val geode = s.geode();
            val time = s.time();

            if (time == minutes) {
                maxGeode = Math.max(maxGeode, geode);
                continue;
            }

            // if we can build a geode robot, DO IT
            if (ore >= blueprint.geodeRobotOreCost() && obsidian >= blueprint.geodeRobotObsidianCost()) {
                sDeque.add(s.buildGeodeRobot(blueprint.geodeRobotOreCost(), blueprint.geodeRobotObsidianCost()));
                continue;
            }

            sDeque.add(s.buildNothing());

            // maybe we can build something to get closer to the geode robot
            if (ore >= blueprint.obsidianRobotOreCost() && clay >= blueprint.obsidianRobotClayCost()) {
                sDeque.add(s.buildObsidianRobot(blueprint.obsidianRobotOreCost(), blueprint.obsidianRobotClayCost()));
            }
            if (ore >= blueprint.clayRobotOreCost()) {
                sDeque.add(s.buildClayRobot(blueprint.clayRobotOreCost()));
            }
            if (ore >= blueprint.oreRobotOreCost()) {
                sDeque.add(s.buildOreRobot(blueprint.oreRobotOreCost()));
            }
        }

        return maxGeode;
    }

    private final static Pattern INPUT_PATTERN = Pattern
            .compile(
                    "Blueprint (\\d+): Each ore robot costs (\\d+) ore. Each clay robot costs (\\d+) ore. Each obsidian robot costs (\\d+) ore and (\\d+) clay. Each geode robot costs (\\d+) ore and (\\d+) obsidian.");

    private List<Blueprint> parse(String inputFilePath) throws IOException {
        return fileReader.readAllLines(inputFilePath).stream().filter(Predicate.not(String::isEmpty))
                .map(INPUT_PATTERN::matcher)
                .filter(Matcher::matches)
                .map(m -> {
                    return new Blueprint(
                            Integer.parseInt(m.group(1)),
                            Integer.parseInt(m.group(2)),
                            Integer.parseInt(m.group(3)),
                            Integer.parseInt(m.group(4)),
                            Integer.parseInt(m.group(5)),
                            Integer.parseInt(m.group(6)),
                            Integer.parseInt(m.group(7)));
                }).toList();
    }
}

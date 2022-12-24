package com.prevostc.adventofcode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.prevostc.utils.FileReader;

import lombok.EqualsAndHashCode;
import lombok.val;

public class Day19 {

    FileReader fileReader = new FileReader();

    public Integer part1(String inputFilePath) throws IOException {
        val blueprints = parse(inputFilePath);

        for (val blueprint : blueprints) {
            val game = new Game(blueprint);
            val maxQuality = game.findMaxQuality(24);
            System.out.println(blueprint.id + ": " + maxQuality);
        }
        return 0;
    }

    public Integer part2(String inputFilePath) throws IOException {
        return 0;
    }

    private enum Resource {
        ORE, CLAY, OBSIDIAN, GEODE;
    }

    private record Blueprint(int id,
            Map<Resource /* robot type */, Map<Resource /* cost in */, Integer /* cost */>> robotCosts) {
    }

    @EqualsAndHashCode
    private class Game {
        Map<String, Integer> maxQualityCache;
        private Blueprint blueprint;
        private EnumMap<Resource, Integer> bank;
        private EnumMap<Resource, Integer> army;

        Game(Blueprint blueprint) {
            this.maxQualityCache = new HashMap<>();
            this.blueprint = blueprint;
            this.bank = new EnumMap<>(Resource.class);
            this.army = new EnumMap<>(Resource.class);
            for (Resource resource : Resource.values()) {
                bank.put(resource, 0);
                army.put(resource, 0);
            }
            // initial robot
            army.put(Resource.ORE, 1);
        }

        private Game(Map<String, Integer> maxQualityCache, Blueprint blueprint, EnumMap<Resource, Integer> bank,
                EnumMap<Resource, Integer> army) {
            this.maxQualityCache = maxQualityCache;
            this.blueprint = blueprint;
            this.bank = bank;
            this.army = army;
        }

        public int getQuality() {
            return bank.get(Resource.GEODE);
        }

        public int findMaxQuality(int ticksLeft) {
            if (ticksLeft == 0) {
                return this.getQuality();
            }

            int maxForkQuality = 0;

            // see if we can build something, starting with the most expensive
            for (Resource robotType : Resource.values()) {
                // here, we have a fork, we can either build or not
                if (canBuildRobot(robotType)) {
                    Game fork = new Game(this.maxQualityCache, blueprint, bank.clone(), bank.clone());
                    fork.buildRobot(robotType);

                    String forkKey = fork.toString() + ticksLeft;
                    if (!maxQualityCache.containsKey(forkKey)) {
                        val forkQuality = fork.findMaxQuality(ticksLeft);
                        maxQualityCache.put(forkKey, forkQuality);
                        maxForkQuality = Math.max(maxForkQuality, forkQuality);
                    }

                    maxForkQuality = Math.max(maxForkQuality, maxQualityCache.get(forkKey));
                }
            }

            // if we can buy the top robot but decided not to, no need to continue
            if (canBuildRobot(Resource.GEODE)) {
                return maxForkQuality;
            }
            if (canBuildRobot(Resource.OBSIDIAN)) {
                return maxForkQuality;
            }

            // see if we can collect something
            for (Resource resource : Resource.values()) {
                harvest(resource);
            }

            return Math.max(maxForkQuality, findMaxQuality(ticksLeft - 1));
        }

        boolean canBuildRobot(Resource robotType) {
            val costs = blueprint.robotCosts.get(robotType);

            for (Resource costType : Resource.values()) {
                val cost = costs.get(costType);
                val inBank = bank.get(costType);
                if (inBank < cost) {
                    return false;
                }
            }
            return true;
        }

        void buildRobot(Resource robotType) {
            val costs = blueprint.robotCosts.get(robotType);

            army.put(robotType, army.get(robotType) + 1);
            for (Resource costType : Resource.values()) {
                val cost = costs.get(costType);
                bank.put(costType, bank.get(costType) - cost);
            }
        }

        void harvest(Resource resource) {
            val armySize = army.get(resource);
            if (armySize > 0) {
                bank.put(resource, bank.get(resource) + armySize);
            }
        }

        @Override
        public String toString() {
            return "Game [bank=" + bank + ", army=" + army + "]";
        }
    }

    private final static Pattern INPUT_PATTERN = Pattern
            .compile(
                    "Blueprint (\\d+): Each ore robot costs (\\d+) ore. Each clay robot costs (\\d+) ore. Each obsidian robot costs (\\d+) ore and (\\d+) clay. Each geode robot costs (\\d+) ore and (\\d+) obsidian.");

    private List<Blueprint> parse(String inputFilePath) throws IOException {
        return fileReader.readTestLines(inputFilePath).stream().filter(Predicate.not(String::isEmpty))
                .map(INPUT_PATTERN::matcher)
                .filter(Matcher::matches)
                .map(m -> {
                    var id = Integer.parseInt(m.group(1));
                    Map<Resource, Map<Resource, Integer>> robotCosts = new EnumMap<>(Resource.class);
                    // init all costs to zero
                    for (Resource r : Resource.values()) {
                        robotCosts.put(r, new EnumMap<>(Resource.class));
                        for (Resource r2 : Resource.values()) {
                            robotCosts.get(r).put(r2, 0);
                        }
                    }

                    // Each ore robot costs (\\d+) ore.
                    robotCosts.get(Resource.ORE).put(Resource.ORE, Integer.parseInt(m.group(2)));
                    // Each clay robot costs (\\d+) ore.
                    robotCosts.get(Resource.CLAY).put(Resource.ORE, Integer.parseInt(m.group(3)));
                    // Each obsidian robot costs (\\d+) ore and (\\d+) clay.
                    robotCosts.get(Resource.OBSIDIAN).put(Resource.ORE, Integer.parseInt(m.group(4)));
                    robotCosts.get(Resource.OBSIDIAN).put(Resource.CLAY, Integer.parseInt(m.group(5)));
                    // Each geode robot costs (\\d+) ore and (\\d+) obsidian.
                    robotCosts.get(Resource.GEODE).put(Resource.ORE, Integer.parseInt(m.group(6)));
                    robotCosts.get(Resource.GEODE).put(Resource.OBSIDIAN, Integer.parseInt(m.group(7)));

                    return new Blueprint(id, robotCosts);
                }).toList();
    }
}

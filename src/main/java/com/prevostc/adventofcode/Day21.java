package com.prevostc.adventofcode;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.prevostc.utils.BigInt;
import com.prevostc.utils.Either;
import com.prevostc.utils.FileReader;

import lombok.val;

public class Day21 {

    FileReader fileReader = new FileReader();

    public BigInteger part1(String inputFilePath) throws IOException {
        parse(inputFilePath);
        return solve("root");
    }

    public BigInteger part2(String inputFilePath, int dir) throws IOException {
        parse(inputFilePath);

        val rootOp = operators.get("root").left();

        // this never changes
        val right = solve(rootOp.rightId);

        // when humn increases, solve(right) decreases so we can only search one
        var min = BigInteger.ZERO;
        var max = BigInt.ofPow(10, 15); // a previous attempt told us it's < 10^14
        val two = BigInt.of(2);
        BigInteger found = null;
        while (min.compareTo(max) < 0) {
            val mid = min.add(max).divide(two);
            operators.put("humn", new Either.Right<>(mid));
            val left = solve(rootOp.leftId);
            val cmp = left.compareTo(right);

            if (cmp == 0) {
                found = mid;
                break;
            } else if (cmp * dir < 0) {
                min = mid;
            } else {
                max = mid;
            }
        }

        // find all solutions "around" the one we found due to division loss of
        // precision
        // it's probably the lowest one
        for (int i = -100; i < 100; i++) {
            val humn = found.add(BigInteger.valueOf(i));
            operators.put("humn", new Either.Right<>(humn));
            val left = solve(rootOp.leftId);
            val cmp = left.compareTo(right);
            if (cmp == 0) {
                return humn;
            }
        }
        throw new IllegalStateException("No solution found");
    }

    private Map<String, Either<Operator, BigInteger>> operators;

    private record Operator(String id, String leftId, String rightId, char op) {
    }

    private BigInteger solve(String id) {
        val oop = operators.get(id);
        if (oop.isLeft()) {
            val op = oop.left();
            val left = solve(op.leftId);
            val right = solve(op.rightId);
            return switch (op.op) {
                case '+' -> left.add(right);
                case '-' -> left.subtract(right);
                case '*' -> left.multiply(right);
                case '/' -> left.divide(right);
                default -> throw new IllegalStateException("Unexpected value: " + op.op);
            };
        } else {
            return oop.right();
        }
    }

    private final static Pattern INPUT_PATTERN = Pattern
            .compile("(?<id>[a-z]+): (?<left>[a-z]+|\\d+)(?: (?<operator>[+/*-]) (?<right>[a-z]+))?");

    private void parse(String inputFilePath) throws IOException {
        val lines = fileReader.readAllLines(inputFilePath);
        this.operators = new HashMap<>();

        for (val line : lines) {
            val matcher = INPUT_PATTERN.matcher(line);
            if (matcher.matches()) {
                val id = matcher.group("id");
                val left = matcher.group("left");
                val operator = matcher.group("operator");
                val right = matcher.group("right");
                if (operator == null) {
                    operators.put(id, new Either.Right<>(BigInt.of(left)));
                } else {
                    operators.put(id, new Either.Left<>(new Operator(id, left, right, operator.charAt(0))));
                }
            } else {
                throw new IllegalStateException("Unexpected line: " + line);
            }
        }
    }
}

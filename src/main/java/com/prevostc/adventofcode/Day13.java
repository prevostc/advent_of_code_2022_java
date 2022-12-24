package com.prevostc.adventofcode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prevostc.utils.Either;
import com.prevostc.utils.FileReader;
import com.prevostc.utils.StreamUtil;

import lombok.Data;
import lombok.val;

public class Day13 {

    FileReader fileReader = new FileReader();

    public Integer part1(String inputFilePath) throws IOException {
        val lines = fileReader.readAllLines(inputFilePath);
        ObjectMapper mapper = new ObjectMapper();
        int ans = 0;
        int pairIdx = 1;
        for (int i = 0; i < lines.size(); i = i + 3) {
            val msg1 = treeToMessage(mapper.readTree(lines.get(i)));
            val msg2 = treeToMessage(mapper.readTree(lines.get(i + 1)));

            val cmp = Message.compare(msg1, msg2);
            if (cmp <= 0) {
                ans += pairIdx;
            }
            pairIdx++;
        }
        return ans;
    }

    public Integer part2(String inputFilePath) throws IOException {
        val lines = fileReader.readAllLines(inputFilePath);
        val dividers = List.of("[[2]]", "[[6]]");
        dividers.stream().forEach(lines::add);

        ObjectMapper mapper = new ObjectMapper();
        val sortedMessages = lines.stream().filter(l -> l.length() > 0).map(StreamUtil.unchecked(mapper::readTree))
                .map(this::treeToMessage)
                .sorted(Message::compare)
                .toList();
        int ans = 1;
        for (int i = 0; i < sortedMessages.size(); i++) {
            String msg = sortedMessages.get(i).toString();
            boolean isDivider = dividers.stream().anyMatch(d -> msg.equals(d));
            if (isDivider) {
                ans *= i + 1;
            }
        }
        return ans;
    }

    @Data
    private static class Message {
        private Either<Integer, List<Message>> data;

        Message(Integer val) {
            this.data = new Either.Left<>(val);
        }

        Message() {
            this.data = new Either.Right<>(new ArrayList<>());
        }

        public static Message toList(Integer val) {
            Message msg = new Message();
            msg.data = new Either.Right<>(List.of(new Message(val)));
            return msg;
        }

        @Override
        public String toString() {
            if (data.isLeft()) {
                return data.left().toString();
            } else {
                return data.right().toString();
            }
        }

        public static Integer compare(Message a, Message b) {
            /**
             * If both values are integers, the lower integer should come first. If the left
             * integer is lower than the right integer, the inputs are in the right order.
             * If the left integer is higher than the right integer, the inputs are not in
             * the right order. Otherwise, the inputs are the same integer; continue
             * checking the next part of the input.
             */
            if (a.data.isLeft() && b.data.isLeft()) {
                if (a.data.left() < b.data.left()) {
                    return -1;
                } else if (a.data.left() > b.data.left()) {
                    return 1;
                }
            }

            /**
             * If both values are lists, compare the first value of each list, then the
             * second value, and so on. If the left list runs out of items first, the inputs
             * are in the right order. If the right list runs out of items first, the inputs
             * are not in the right order. If the lists are the same length and no
             * comparison makes a decision about the order, continue checking the next part
             * of the input.
             */
            else if (a.data.isRight() && b.data.isRight()) {
                for (int i = 0; i < a.data.right().size(); i++) {
                    if (i >= b.data.right().size()) {
                        return 1;
                    }
                    int compare = compare(a.data.right().get(i), b.data.right().get(i));
                    if (compare != 0) {
                        return compare;
                    }
                }
                if (a.data.right().size() < b.data.right().size()) {
                    return -1;
                }
            }

            /**
             * If exactly one value is an integer, convert the integer to a list which
             * contains that integer as its only value, then retry the comparison. For
             * example, if comparing [0,0,0] and 2, convert the right value to [2] (a list
             * containing 2); the result is then found by instead comparing [0,0,0] and [2].
             */
            else if (a.data.isLeft() && b.data.isRight()) {
                return compare(Message.toList(a.data.left()), b);
            } else if (a.data.isRight() && b.data.isLeft()) {
                return compare(a, Message.toList(b.data.left()));
            }

            return 0;
        }
    }

    private Message treeToMessage(JsonNode tree) {
        if (tree.isArray()) {
            Message message = new Message();
            for (JsonNode node : tree) {
                message.getData().right().add(treeToMessage(node));
            }
            return message;
        } else if (tree.isInt()) {
            return new Message(tree.asInt());
        }
        return null;
    }
}

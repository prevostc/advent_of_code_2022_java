package com.prevostc.adventofcode;

import java.io.IOException;
import java.math.BigInteger;

import com.prevostc.utils.FileReader;

import lombok.Setter;
import lombok.val;

public class Day20 {

    public BigInteger part1(String inputFilePath) throws IOException {
        val state = parse(inputFilePath, BigInteger.ONE);

        for (int i = 0; i < state.data.length; i++) {
            state.move(i);
        }

        return state.answer();
    }

    public BigInteger part2(String inputFilePath) throws IOException {
        val state = parse(inputFilePath, BigInteger.valueOf(811589153));

        for (int passes = 0; passes < 10; passes++) {
            for (int i = 0; i < state.data.length; i++) {
                state.move(i);
            }
        }

        return state.answer();
    }

    private class State {
        BigInteger[] data;
        BigInteger wrap;
        int[] oIdx2CurIdx;

        public State(int[] data, BigInteger decryptKey) {
            this.data = new BigInteger[data.length];
            for (int i = 0; i < data.length; i++) {
                this.data[i] = BigInteger.valueOf(data[i]).multiply(decryptKey);
            }
            this.wrap = BigInteger.valueOf(data.length - 1);

            this.oIdx2CurIdx = new int[data.length];
            for (int i = 0; i < data.length; i++) {
                oIdx2CurIdx[i] = i;
            }
        }

        public BigInteger answer() {
            // find the idx of 0
            int i = 0;
            for (; i < data.length; i++) {
                if (data[i].equals(BigInteger.ZERO)) {
                    break;
                }
            }
            return data[wrapIdx(i + 1000)].add(data[wrapIdx(i + 2000)]).add(data[wrapIdx(i + 3000)]);
        }

        public int wrapIdx(int idx) {
            return ((idx % data.length) + data.length) % data.length;
        }

        public void move(int originalIdx) {
            int currentIdx = oIdx2CurIdx[originalIdx];
            val value = data[currentIdx];
            int targetIdx = value.add(BigInteger.valueOf(currentIdx)).mod(wrap).intValue();

            if (currentIdx == targetIdx) {
                return;
            }

            // there must be a better way to do this
            if (currentIdx < targetIdx) {
                // move left
                for (int i = currentIdx; i < targetIdx; i++) {
                    data[i] = data[wrapIdx(i + 1)];
                }
                data[targetIdx] = value;
                // update indices map
                for (int i = 0; i < oIdx2CurIdx.length; i++) {
                    if (oIdx2CurIdx[i] > currentIdx && oIdx2CurIdx[i] <= targetIdx) {
                        oIdx2CurIdx[i]--;
                    }
                }
                oIdx2CurIdx[originalIdx] = targetIdx;
            } else {
                // move right
                for (int i = currentIdx; i >= targetIdx; i--) {
                    data[i] = data[wrapIdx(i - 1)];
                }
                data[targetIdx] = value;
                // update indices map
                for (int i = 0; i < oIdx2CurIdx.length; i++) {
                    if (oIdx2CurIdx[i] >= targetIdx && oIdx2CurIdx[i] < currentIdx) {
                        oIdx2CurIdx[i]++;
                    }
                }
                oIdx2CurIdx[originalIdx] = targetIdx;
            }
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (val i : data) {
                sb.append(i);
                sb.append(" ");
            }
            return sb.toString();
        }
    }

    FileReader fileReader = new FileReader();

    private State parse(String inputFilePath, BigInteger decryptKey) throws IOException {
        val data = fileReader.readAllLines(inputFilePath)
                .stream()
                .mapToInt(Integer::parseInt)
                .toArray();
        return new State(data, decryptKey);
    }
}

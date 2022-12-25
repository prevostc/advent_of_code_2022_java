package com.prevostc.adventofcode;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.prevostc.utils.FileReader;

import lombok.val;

public class Day25 {

    FileReader fileReader = new FileReader();

    private final String[] digits = new String[] { "=", "-", "0", "1", "2" };
    Map<String, Integer> digitValue = new HashMap<>();

    public String part1(String inputFilePath) throws IOException {
        for (int i = 0; i < digits.length; i++) {
            digitValue.put(digits[i], i - 2);
        }

        String tot = "0";
        for (val n : fileReader.readAllLines(inputFilePath)) {
            tot = snafuAdd(tot, n);
        }
        return tot;
    }

    public Integer part2(String inputFilePath) throws IOException {
        val nums = fileReader.readAllLines(inputFilePath);
        return 0;
    }

    private String snafuAdd(String a, String b) {
        var carry = 0;
        val result = new StringBuilder();
        for (int i = 0; i < a.length() || i < b.length(); i++) {
            val c1 = i < a.length() ? a.charAt(a.length() - i - 1) : '0';
            val c2 = i < b.length() ? b.charAt(b.length() - i - 1) : '0';
            val c1v = digitValue.get(String.valueOf(c1));
            val c2v = digitValue.get(String.valueOf(c2));
            val sum = c1v + c2v + carry;
            var digitV = sum % 5;
            carry = sum / 5;
            if (digitV <= -3) {
                carry--;
                digitV += 5;
            } else if (digitV >= 3) {
                carry++;
                digitV -= 5;
            }
            val digit = digits[digitV + 2];
            result.append(digit);
        }
        return result.reverse().toString();
    }
}
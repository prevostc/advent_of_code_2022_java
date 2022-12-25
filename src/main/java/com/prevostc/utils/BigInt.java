package com.prevostc.utils;

import java.math.BigInteger;

public class BigInt {

    public static BigInteger of(String s) {
        var take = 5; // 5 digits at a time
        var v = BigInteger.ZERO;
        while (s.length() > 0) {
            var n = s.substring(0, Math.min(take, s.length()));
            s = s.substring(n.length());
            v = v.multiply(BigInteger.valueOf(10).pow(n.length())).add(new BigInteger(n));
        }
        return v;
    }

    public static BigInteger of(int i) {
        return BigInteger.valueOf(i);
    }

    public static BigInteger ofPow(int base, int exp) {
        return BigInteger.valueOf(base).pow(exp);
    }
}

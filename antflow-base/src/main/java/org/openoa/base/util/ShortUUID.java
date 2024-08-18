package org.openoa.base.util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ShortUUID {

    private final static char[] DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
            'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U',
            'V', 'W', 'X', 'Y', 'Z'};

    private static class Numbers {

        private final static Map<Character, Integer> DIGIT_MAP = new HashMap<>();

        static {
            for (int i = 0; i < DIGITS.length; i++) {
                DIGIT_MAP.put(DIGITS[i], i);
            }
        }
        // 支持的最小进制数
        static final int MIN_RADIX = 2;
        // 支持的最大进制数
        // static final int MAX_RADIX = DIGITS.length;

        /**
         * 将长整型数值转换为指定的进制数（最大支持62进制，字母数字已经用尽）
         *
         * @param i
         * @param radix
         * @return
         */
        static String toString(long i, int radix, char[] digits) {
            if (radix < MIN_RADIX || radix > digits.length) {
                radix = 10;
            }
            if (radix == 10) {
                return Long.toString(i);
            }

            final int size = 65;
            int charPos = 64;

            char[] buf = new char[size];
            boolean negative = (i < 0);

            if (!negative) {
                i = -i;
            }

            while (i <= -radix) {
                buf[charPos--] = digits[(int) (-(i % radix))];
                i = i / radix;
            }
            buf[charPos] = digits[(int) (-i)];

            if (negative) {
                buf[--charPos] = '-';
            }

            return new String(buf, charPos, (size - charPos));
        }
    }

        static NumberFormatException forInputString(String s) {
            return new NumberFormatException("For input string: \"" + s + "\"");
        }


        private static String digits(long val, int digits, char[] digitsArr) {
            long hi = 1L << (digits * 4);
            return Numbers.toString(hi | (val & (hi - 1)), digitsArr.length, digitsArr).substring(1);
        }

        /**
         * 以62进制（字母加数字）生成19位UUID，最短的UUID
         *
         * @return
         */
        public static String uuid() {
            return uuid(DIGITS);
        }

        /**
         * 以62进制（字母加数字）生成19位UUID，最短的UUID
         *
         * @param digits 种子数组
         * @return
         */
        public static String uuid(char[] digits) {
            UUID uuid = UUID.randomUUID();
            StringBuilder sb = new StringBuilder();
            sb.append(digits(uuid.getMostSignificantBits() >> 32, 8, digits));
            sb.append(digits(uuid.getMostSignificantBits() >> 16, 4, digits));
            sb.append(digits(uuid.getMostSignificantBits(), 4, digits));
            sb.append(digits(uuid.getLeastSignificantBits() >> 48, 4, digits));
            sb.append(digits(uuid.getLeastSignificantBits(), 12, digits));
            return sb.toString();
        }
    }


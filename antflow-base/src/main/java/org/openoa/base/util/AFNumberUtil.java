package org.openoa.base.util;

import com.google.common.hash.Hashing;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AFNumberUtil {

    // 62 进制字符集：0-9, A-Z, a-z
    private static final char[] BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final int BASE = BASE62_CHARS.length;

    /**
     * 将长整数转换为62进制字符串
     *
     * @param number 原始长整数
     * @return 转换后的62进制字符串
     */
    public static String encodeLong(long number) {
        if (number < 0) {
            throw new IllegalArgumentException("Number must be non-negative.");
        }

        StringBuilder encoded = new StringBuilder();
        do {
            int remainder = (int) (number % BASE);
            encoded.append(BASE62_CHARS[remainder]);
            number /= BASE;
        } while (number > 0);

        // 字符串需要反转，因为我们是从低位到高位生成的
        return encoded.reverse().toString();
    }

    /**
     * 将62进制字符串解码回长整数
     *
     * @param encoded 62进制字符串
     * @return 解码后的长整数
     */
    public static long decodeLong(String encoded) {
        if (encoded == null || encoded.isEmpty()) {
            throw new IllegalArgumentException("Encoded string cannot be null or empty.");
        }

        long number = 0;
        for (char c : encoded.toCharArray()) {
            int value = charToValue(c);
            number = number * BASE + value;
        }

        return number;
    }
    public static String shortBusinessId(String businessId){
        if(businessId==null){
            return null;
        }
        return  businessId.length()>20?generateShortHash(businessId):businessId;
    }
    // 使用 MurmurHash 生成哈希值并转换为 Base62
    public static String generateShortHash(String input) {
        long murmurHash = Hashing.murmur3_128().hashString(input, StandardCharsets.UTF_8).asLong();
        // 确保 hash 值为正数
        murmurHash = murmurHash < 0 ? -murmurHash : murmurHash;
        return encodeLong(murmurHash);
    }
    public static String md5ToBase62(String input)  {
        // 计算 MD5
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] md5Bytes = md.digest(input.getBytes());

        // 转为大整数
        BigInteger bigInt = new BigInteger(1, md5Bytes);

        // 转为 62 进制字符串
        StringBuilder base62 = new StringBuilder();
        while (bigInt.compareTo(BigInteger.ZERO) > 0) {
            int remainder = bigInt.mod(BigInteger.valueOf(62)).intValue();
            base62.append(BASE62_CHARS[remainder]);
            bigInt = bigInt.divide(BigInteger.valueOf(62));
        }

        // 字符串逆序（因为取余从低位开始生成）
        return base62.reverse().toString();
    }
    /**
     * 将字符转换为对应的值
     *
     * @param c 输入字符
     * @return 对应的数值
     */
    private static int charToValue(char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        } else if (c >= 'A' && c <= 'Z') {
            return c - 'A' + 10;
        } else if (c >= 'a' && c <= 'z') {
            return c - 'a' + 36;
        } else {
            throw new IllegalArgumentException("Invalid character in encoded string: " + c);
        }
    }

}

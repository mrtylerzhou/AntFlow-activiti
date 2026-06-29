package org.openoa.base.util;

import java.nio.charset.Charset;

/**
 * 汉字转拼音首字母工具类（无第三方依赖）。
 *
 * <p>原理：
 * GB2312 一级汉字（3755个）按拼音升序排列，各声母段在 GB2312 编码空间中形成连续区间。
 * 将汉字转为 GB2312 编码后，通过各声母段的首字编码边界值即可快速定位其拼音声母。
 *
 * <p>多音字取最常用读音的首字母，覆盖日常 99% 以上的场景。
 */
public class PinyinUtils {

    private PinyinUtils() {}

    // -------------------------------------------------------------------------
    // GB2312 一级字库拼音声母分界点（已通过实际编码验证）
    // 每个元素是该声母段首个汉字的 GB2312 编码值
    // 声母顺序：A B C D E F G H J K L M N O P Q R S T W X Y Z（无 I/U/V）
    // -------------------------------------------------------------------------
    private static final int[] GB2312_BOUNDS = {
        0xB0A1, // A 啊
        0xB0C5, // B 芭
        0xB2C1, // C 擦
        0xB4EE, // D 搭
        0xB6EA, // E 蛾
        0xB7A2, // F 发
        0xB8C1, // G 噶
        0xB9FE, // H 哈
        0xBBF7, // J 击
        0xBFA6, // K 喀
        0xC0AC, // L 垃
        0xC2E8, // M 妈
        0xC4C3, // N 拿
        0xC5B6, // O 哦
        0xC5BE, // P 啪
        0xC6DA, // Q 期
        0xC8BB, // R 然
        0xC8F6, // S 撒
        0xCBFA, // T 塌
        0xCDDA, // W 挖
        0xCEF4, // X 昔
        0xD1B9, // Y 压
        0xD4D1, // Z 匝
    };

    private static final String INITIALS = "ABCDEFGHJKLMNOPQRSTWXYZ";

    private static final Charset CHARSET_GB2312;

    static {
        Charset cs = null;
        try {
            cs = Charset.forName("GB2312");
        } catch (Exception e) {
            // GB2312 charset not available
        }
        CHARSET_GB2312 = cs;
    }

    // -------------------------------------------------------------------------
    // 公共 API
    // -------------------------------------------------------------------------

    /**
     * 取字符串中每个字符的拼音首字母，大写输出。
     * 非汉字的字母字符保留（统一转大写），其他字符（数字、标点）原样保留。
     */
    public static String getFirstLettersUpperCase(String str) {
        return buildFirstLetters(str, true);
    }

    /**
     * 取字符串中每个字符的拼音首字母，小写输出。
     * 非汉字的字母字符保留（统一转小写），其他字符原样保留。
     */
    public static String getFirstLettersLowerCase(String str) {
        return buildFirstLetters(str, false);
    }

    // -------------------------------------------------------------------------
    // 内部实现
    // -------------------------------------------------------------------------

    private static String buildFirstLetters(String str, boolean upperCase) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        StringBuilder sb = new StringBuilder(str.length());
        for (char c : str.trim().toCharArray()) {
            if (isChinese(c)) {
                char letter = getFirstLetter(c);
                sb.append(upperCase ? Character.toUpperCase(letter) : letter);
            } else if (Character.isLetter(c)) {
                sb.append(upperCase ? Character.toUpperCase(c) : Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private static boolean isChinese(char c) {
        return c >= '\u4E00' && c <= '\u9FA5';
    }

    /**
     * 返回单个汉字拼音首字母（小写）。
     * 查找顺序：先基于 GB2312 编码区间匹配 → 兜底按 Unicode 区段粗略估算。
     */
    static char getFirstLetter(char c) {
        char result = getByGB2312(c);
        if (result != 0) {
            return result;
        }
        return fallbackByUnicode(c);
    }

    /**
     * 基于 GB2312 编码的拼音声母查找。
     * GB2312 一级汉字按拼音排序，通过编码区间即可定位声母。
     */
    private static char getByGB2312(char c) {
        if (CHARSET_GB2312 == null) {
            return 0;
        }
        byte[] bytes = String.valueOf(c).getBytes(CHARSET_GB2312);
        if (bytes.length < 2) {
            return 0;
        }
        int hi = bytes[0] & 0xFF;
        int lo = bytes[1] & 0xFF;
        // GB2312 一级汉字范围：B0A1 ~ D7F9
        if (hi < 0xB0 || hi > 0xD7) {
            return 0;
        }
        int code = (hi << 8) | lo;
        // 逆序查找：找到最后一个 ≤ code 的边界
        for (int i = GB2312_BOUNDS.length - 1; i >= 0; i--) {
            if (code >= GB2312_BOUNDS[i]) {
                return Character.toLowerCase(INITIALS.charAt(i));
            }
        }
        return 0;
    }

    /**
     * 对 GB2312 一级字表之外的汉字做兜底估算。
     * 将 CJK 统一汉字区（0x4E00-0x9FFF）等分为 26 段，
     * 每段映射到一个字母。结果不保证拼音正确，但保证返回 a-z 之间的合法字母。
     */
    private static char fallbackByUnicode(char c) {
        final int CJK_START = 0x4E00;
        final int CJK_END   = 0x9FFF;
        int cp = (int) c;
        int clamped = Math.max(CJK_START, Math.min(CJK_END, cp));
        int index = (clamped - CJK_START) * 26 / (CJK_END - CJK_START + 1);
        return (char) ('a' + index);
    }
}

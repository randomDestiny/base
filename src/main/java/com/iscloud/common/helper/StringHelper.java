package com.iscloud.common.helper;

import com.alibaba.fastjson.JSON;
import com.iscloud.common.cst.BaseCst;
import com.iscloud.common.utils.GsonUtils;
import com.iscloud.common.utils.MathUtils;
import lombok.SneakyThrows;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import java.util.regex.Matcher;

/**
 * @Desc: TODO
 * @Author: Yu.Hua
 * @Date: 2020/3/10 0010 16:33
 */
@SuppressWarnings("unused")
public class StringHelper implements BaseCst.SymbolCst, BaseCst.FileTypeCst, BaseCst.PatternCst {

    public static String sha1(String s) {
        MessageDigest crypt = DigestUtils.getSha1Digest();
        crypt.reset();
        crypt.update(s.getBytes(StandardCharsets.UTF_8));
        return bytes2Hex(crypt.digest(), StringUtils.EMPTY, false);
    }

    /**
     * @Desc:   无分割符的hex转byte数组
     * @Params: [src]
     * @Return: byte[]
     * @Author: HYbrid
     * @Date:   2022/7/2
     */
    public static byte[] hex2Bytes(String src) {
        if (StringUtils.isNotBlank(src)) {
            byte[] res = new byte[src.length() / 2], tmp = src.getBytes();
            for (int i = 0, l = tmp.length / 2; i < l; i++) {
                res[i] = MathUtils.uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
            }
            return res;
        }
        return null;
    }

    public static byte[] hex2Bytes(String src, String split) {
        if (StringUtils.isNotBlank(src)) {
            String[] strings = src.split(split);
            if (ArrayUtils.isNotEmpty(strings)) {
                List<Byte> list = new LinkedList<>();
                byte[] bytes;
                for (String s : strings) {
                    if (StringUtils.isNotBlank(s)) {
                        bytes = s.getBytes(StandardCharsets.UTF_8);
                        list.add(MathUtils.uniteBytes(bytes[0], bytes[1]));
                    }
                }
                return ListHelper.list2Bytes(list);
            }
        }
        return null;
    }

    public static String bytes2Hex(byte[] bytes) {
        return bytes2Hex(bytes, null, true);
    }

    public static String bytes2Hex(byte[] bytes, String split, boolean upper) {
        if (ArrayUtils.isNotEmpty(bytes)) {
            Formatter formatter = new Formatter();
            String s = split == null ? StringUtils.SPACE : split,
                    f = upper ? LEFT_FILL_2_ZERO_UP : LEFT_FILL_2_ZERO,
                    hex;
            for (int i = 0, l = bytes.length, p = l -2; i < l; i++) {
                formatter.format(f + (i <= p ? s : StringUtils.EMPTY), bytes[i]);
            }
            String result = formatter.toString();
            formatter.close();
            return result;
        }
        return StringUtils.EMPTY;
    }

    public static String bytesStr2Hex(String bytes) {
        return bytes2Hex(GsonUtils.parse(bytes, byte[].class), LINE, true);
    }

    /**
     * @Desc: 字符串转16进制字符串
     * @Author:      Yu.Hua
     * @Date:        2020/6/29 0029 16:03
     * @Params:      [strPart]
     * @Return:      java.lang.String
     */
    public static String string2Hex(String strPart) {
        if (StringUtils.isNumeric(strPart)) {
            int hex = Integer.parseInt(strPart);
            if (0 != hex) {
                return Integer.toHexString(hex);
            }
        }
        return StringUtils.EMPTY;
    }

    /**
     * @Desc:   byte数组转二进制
     * @Params: [bytes]
     * @Return: java.lang.String
     * @Author: HYbrid
     * @Date:   2022/7/2
     */
    public static String bytes2BinaryStr(byte[] bytes) {
        StringBuilder binaryStr = new StringBuilder();
        if (ArrayUtils.isNotEmpty(bytes)) {
            for (byte aByte : bytes) {
                String str = Integer.toBinaryString((aByte & 0xFF) + 0x100).substring(1);
                binaryStr.append(str);
            }
        }
        return binaryStr.toString();
    }

    @SneakyThrows
    public static void saveFileByBase64(String base64, String filePath) {
        if (StringUtils.isAnyBlank(base64, filePath)) {
            return;
        }
        BufferedImage bi = ImageIO.read(new ByteArrayInputStream(Base64.decodeBase64(base64)));
        File file = new File(filePath);
        ImageIO.write(bi, JPG, file);
    }

    public static String uuid() {
        char[] d = new char[32], s = UUID.randomUUID().toString().toCharArray();
        System.arraycopy(s, 0, d, 0, 8);
        System.arraycopy(s, 9, d, 8, 4);
        System.arraycopy(s, 14, d, 12, 4);
        System.arraycopy(s, 19, d, 16, 4);
        System.arraycopy(s, 24, d, 20, 12);
        return new String(d);
    }

    /**
     * @Desc: 左补齐0
     * @Author:      Yu.Hua
     * @Date:        2020/10/6 0006 15:28
     * @Params:      [num, val]
     * @Return:      java.lang.String
     */
    public static String leftFillZero(int num, long val) {
        return String.format(String.format(LEFT_FILL_ZERO_FORMATTER, num), val);
    }

    /**
     * @Desc: 去空
     * @Author:      Yu.Hua
     * @Date:        2020/9/9 0009 20:39
     * @Params:      [arr]
     * @Return:      java.lang.String[]
     */
    public static String[] trim(String[] arr) {
        if (ArrayUtils.isNotEmpty(arr)) {
            List<String> list = new LinkedList<>();
            for (String ss : arr) {
                if (StringUtils.isNotBlank(ss)) {
                    list.add(ss.trim());
                }
            }
            return list.toArray(new String[] {});
        }
        return arr;
    }

    /**
     * @Desc: 获取有效的str的集合
     * @Author:      Yu.Hua
     * @Date:        2020/10/12 0012 18:11
     * @Params:      [str]
     * @Return:      java.util.List<java.lang.String>
     */
    public static List<String> trim2List(String str) {
        List<String> list = new LinkedList<>();
        if (StringUtils.isNotBlank(str)) {
            String[] s = str.split(COMMA);
            for (String ss : s) {
                if (StringUtils.isNotBlank(ss)) {
                    list.add(ss);
                }
            }
        }
        return list;
    }

    /**
     * @Desc: 去空
     * @Author:      Yu.Hua
     * @Date:        2020/9/9 0009 20:38
     * @Params:      [str]
     * @Return:      java.lang.String
     */
    public static String trim(String str) {
        if (StringUtils.isNotBlank(str)) {
            String[] s = str.split(COMMA);
            StringBuilder sb = new StringBuilder();
            for (String ss : s) {
                if (StringUtils.isNotBlank(ss)) {
                    sb.append(ss.trim()).append(COMMA);
                }
            }
            sb.setLength(sb.length() -1);
            return sb.toString();
        }
        return str;
    }

    /**
     * @Desc: 将数值字符串转成BigDecimal集合
     * @Author:      Yu.Hua
     * @Date:        2020/5/19 0019 16:56
     * @Params:      [str]
     * @Return:      java.util.List<java.math.BigDecimal>
     */
    public static List<BigDecimal> strToBigDecimalList(String str, String split, int size) {
        if (StringUtils.isNotBlank(str)) {
            String[] arr = str.split(split);
            List<BigDecimal> res = new LinkedList<>();
            for (String s : arr) {
                res.add(StringUtils.isNotBlank(s) ? new BigDecimal(s) : BigDecimal.ZERO);
            }
            return res;
        }
        return ListHelper.build(BigDecimal.ZERO, size);
    }

    public static String append(Collection<String> strs, String split, boolean showNotNull) {
        if (CollectionUtils.isNotEmpty(strs)) {
            StringBuilder sb = new StringBuilder();
            int i = 1, len = strs.size();
            for (String s : strs) {
                if (i == 1) {
                    if (!showNotNull || StringUtils.isNotBlank(s)) {
                        sb.append(s);
                    }
                } else {
                    if (i <= len && (!showNotNull || StringUtils.isNotBlank(s))) {
                        sb.append(split).append(s);
                    }
                }
                i++;
            }
            return sb.toString();
        }
        return StringUtils.EMPTY;
    }

    public static String append(Collection<String> strings, boolean showNotNull) {
        return append(strings, COMMA, showNotNull);
    }

    public static String append(boolean showNotNull, String... strings) {
        return appendSplit(COMMA, showNotNull, strings);
    }

    public static String appendSplit(String split, boolean showNotNull, String... strings) {
        return append(Arrays.asList(strings), split, showNotNull);
    }

    public static String appendSqlSimple(Collection<?> values) {
        StringBuilder sql = new StringBuilder();
        if (CollectionUtils.isNotEmpty(values)) {
            boolean a = false, b;
            String v;
            for (Object o : values) {
                if (o != null) {
                    b = o instanceof String;
                    v = b ? (String) o : JSON.toJSONString(o);
                    if (StringUtils.isNotBlank(v)) {
                        if (a) {
                            sql.append(COMMA);
                        }
                        if (b) {
                            sql.append(QUOTATION).append(v).append(QUOTATION);
                        } else {
                            sql.append(v);
                        }
                        a = true;
                    }
                }
            }
        }
        return sql.toString();
    }

    public static String appendSql(Collection<?> values) {
        String sql = appendSqlSimple(values);
        return StringUtils.isNotBlank(sql) ? LEFT_PARENTHESIS + sql + RIGHT_PARENTHESIS : sql;
    }

    public static String appendSql(Object... values) {
        StringBuilder sql = new StringBuilder("(");
        boolean a = false, b;
        String v;
        for (Object o : values) {
            if (o != null) {
                b = o instanceof String;
                v = b ? (String) o : JSON.toJSONString(o);
                if (StringUtils.isNotBlank(v)) {
                    if (a) {
                        sql.append(COMMA);
                    }
                    if (b) {
                        sql.append(QUOTATION).append(JSON.toJSONString(o)).append(QUOTATION);
                    } else {
                        sql.append(JSON.toJSONString(o));
                    }
                    a = true;
                }
            }
        }
        if (sql.length() == 1) {
            return StringUtils.EMPTY;
        } else {
            sql.append(")");
        }
        return sql.toString();
    }

    public static boolean heartbeatFormat(String heart) {
        return StringUtils.isNotBlank(heart) && heart.matches(HEART_BEAT_FORMAT);
    }

    public static boolean emailFormat(String email) {
        return StringUtils.isNotBlank(email) && email.matches(EMAIL_FORMAT);
    }

    public static boolean base64Format(String header) {
        return StringUtils.isNotBlank(header) && header.matches(BASE64_FORMAT);
    }

    public static boolean ipFormat(String ip) {
        return StringUtils.isNotBlank(ip) && ip.matches(IP_FORMAT);
    }

    /**
     * @Desc: 驼峰命名转下划线
     * @Author     : HYbrid
     * @Date       : 2021/6/21 19:11
     * @Params     : [hump]
     * @Return     : java.lang.String
     */
    public static String toUnderLine(String hump) {
        if (StringUtils.isBlank(hump)) {
            return StringUtils.EMPTY;
        }
        Matcher matcher = P_AZ.matcher(hump);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.start() == 0 ? matcher.group(0).toLowerCase() : UNDER_LINE + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * @Desc: 将下划线格式字符串转驼峰格式
     * @Author     : HYbrid
     * @Date       : 2021/3/18 14:04
     * @Params     : [str]
     * @Return     : java.lang.String
     */
    public static String camel(String str) {
        if (StringUtils.isBlank(str)) {
            return StringUtils.EMPTY;
        }
        StringBuilder sb = new StringBuilder();
        boolean nextUpperCase = false;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            switch (c) {
                case '_':
                case '-':
                case '@':
                case '$':
                case '#':
                case ' ':
                case '/':
                case '&':
                    if (sb.length() > 0) {
                        nextUpperCase = true;
                    }
                    break;
                default:
                    if (nextUpperCase) {
                        sb.append(Character.toUpperCase(c));
                        nextUpperCase = false;
                    } else {
                        sb.append(Character.toLowerCase(c));
                    }
                    break;
            }
        }
        return sb.toString();
    }

    /**
     * @Desc: 将下划线格式字符串转驼峰格式，首字母大写
     * @Author     : HYbrid
     * @Date       : 2021/3/18 14:04
     * @Params     : [str]
     * @Return     : java.lang.String
     */
    public static String camel1stUpper(String str) {
        String s = camel(str);
        return s.length() > 0 ? s.substring(0, 1).toUpperCase() + s.substring(1) : s;
    }

    public static String bytes2Str(byte[] bytes) {
        return ArrayUtils.isNotEmpty(bytes) ? new String(bytes, StandardCharsets.UTF_8).trim() : StringUtils.EMPTY;
    }

    public static String bytes4Str(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        if (ArrayUtils.isNotEmpty(bytes)) {
            for (byte b : bytes) {
                sb.append(String.valueOf(b)).append(COMMA);
            }
        }
        return sb.toString();
    }

}

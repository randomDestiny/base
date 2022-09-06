package com.iscloud.common.utils;

import com.iscloud.common.cst.BaseCst;
import com.iscloud.common.helper.StringHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * @Desc: 计算工具类
 * @Author ：HYbrid
 * @Date ：2020/12/29 0029 10:58
 */
@SuppressWarnings("unused")
public class MathUtils {

    public static byte uniteBytes(byte src0, byte src1) {
        byte b0 = (byte) (Byte.decode(BaseCst.HEX_PRE + new String(new byte[]{src0})) << 4);
        byte b1 = Byte.decode(BaseCst.HEX_PRE + new String(new byte[]{src1}));
        return (byte) (b0 ^ b1);
    }

    public static short toShort(byte b1, byte b2) {
        return (short) (b1 << 8 | b2 & 0xFF);
    }

    public static float bytes2Float(byte[] bytes) {
        String binaryStr = StringHelper.bytes2BinaryStr(bytes);
        // 符号位S
        long s = Long.parseLong(binaryStr.substring(0, 1));
        // 指数位E
        long e = Long.parseLong(binaryStr.substring(1, 9), 2);
        // 位数p
        String p = binaryStr.substring(9);
        float m = 0, a, b;
        for (int i = 0; i < p.length(); i++) {
            a = Integer.parseInt(p.charAt(i) + StringUtils.EMPTY);
            b = (float) Math.pow(2, i + 1);
            m = m + (a / b);
        }
        return (float) ((Math.pow(-1, s)) * (1 + m) * (Math.pow(2, (e - 127))));
    }

    public static int bigSetInt(Object o, Integer bit) {
        Boolean b = bigSetVal(o, bit);
        return b == null ? -1 : b ? 1 : 0;
    }

    public static Boolean bigSetVal(Object o, Integer bit) {
        BitSet s;
        if (bit == null || (s = buildBigSet(o)) == null) {
            return null;
        }
        return s.get(bit);
    }

    /**
     * @Desc:   将对象转换成位运算对象后，校验第n位bit位是否与检验位一致
     * @Params: [o, index, chk]
     * @Return: boolean
     * @Author: HYbrid
     * @Date:   2022/4/24
     */
    public static boolean chkBigSet(Object o, int index, boolean chk) {
        BitSet s = buildBigSet(o);
        return s != null && Objects.equals(s.get(index), chk);
    }

    /**
     * @Desc:   将对象转换成位运算对象后，校验第n位bit位是否为1
     * @Params: [o, index]
     * @Return: boolean
     * @Author: HYbrid
     * @Date:   2022/4/24
     */
    public static boolean chkBigSet(Object o, int index) {
        BitSet s = buildBigSet(o);
        return s != null && s.get(index);
    }

    /**
     * @Desc:   将对象转换成位运算对象
     * @Params: [o]
     * @Return: java.util.BitSet
     * @Author: HYbrid
     * @Date:   2022/4/24
     */
    public static BitSet buildBigSet(Object o) {
        long[] l = null;
        if (o instanceof String && StringUtils.isNumeric((String) o)) {
            l = new long[] {Long.parseLong((String) o)};
        } else if (o instanceof Number) {
            l = new long[] {NumberUtils.createLong(String.valueOf(o).split(BaseCst.SymbolCst.POINT_SUFFIX)[0])};
        } else if (o instanceof Number[]) {
            List<String> list = new LinkedList<>();
            for (Number ll : (Number[]) o) {
                if (ll != null) {
                    list.add(String.valueOf(ll).split(BaseCst.SymbolCst.POINT_SUFFIX)[0]);
                }
            }
            if (CollectionUtils.isNotEmpty(list)) {
                l = new long[list.size()];
                for (int i = 0, len = list.size(); i < len; i++) {
                    l[i] = NumberUtils.createLong(list.get(i));
                }
            }
        }
        return ArrayUtils.isEmpty(l) ? null : BitSet.valueOf(Objects.requireNonNull(l));
    }

    /**
     * @Desc: BigDecimal初始化
     * @Author     : HYbrid
     * @Date       : 2020/12/29 0029 11:00
     * @Params     : [num, init]
     * @Return     : java.math.BigDecimal
     */
    public static BigDecimal init(BigDecimal num, BigDecimal init) {
        return num == null ? Objects.requireNonNull(init) : num;
    }

    /**
     * @Desc: BigDecimal初始化
     * @Author     : HYbrid
     * @Date       : 2020/12/29 0029 11:00
     * @Params     : [num]
     * @Return     : java.math.BigDecimal
     */
    public static BigDecimal init0(BigDecimal num) {
        return num == null ? BigDecimal.ZERO : num;
    }

    /**
     * @Desc: 统计list的数据总和
     * @Author     : HYbrid
     * @Date       : 2021/8/10 15:16
     * @Params     : [list]
     * @Return     : java.math.BigDecimal
     */
    public static BigDecimal sum(List<BigDecimal> list) {
        return CollectionUtils.isEmpty(list) ? BigDecimal.ZERO : list.stream().filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static BigDecimal sum(BigDecimal... array) {
        return ArrayUtils.isEmpty(array) ? BigDecimal.ZERO : Arrays.stream(array).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * @Desc: 获取最大值
     * @Author     : HYbrid
     * @Date       : 2021/8/10 15:16
     * @Params     : [numbers]
     * @Return     : double
     */
    public static double max(List<? extends Number> numbers) {
        return numbers.stream().filter(Objects::nonNull).mapToDouble(Number::doubleValue).max().orElse(Double.MIN_VALUE);
    }
}

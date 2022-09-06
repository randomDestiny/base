package com.iscloud.common.utils;

import com.alibaba.fastjson.JSON;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @Desc: Gzip压缩与解压缩工具类
 * @Author ：HYbrid
 * @Date ：2020/12/22 14:20
 */
@SuppressWarnings("unused")
public final class GZIPUtils {

    public static byte[] compress2Byte(final Object o) {
        if (o != null) {
            return o instanceof byte[] ? compress2ByteFromByte((byte[]) o) : compress2ByteFromStr(JSON.toJSONString(o));
        }
        return null;
    }

    public static byte[] compress2ByteFromByte(final byte[] info) {
        if (ArrayUtils.isEmpty(info)) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = null;
        try {
            gzip = new GZIPOutputStream(out);
            gzip.write(info);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(gzip, out);
        }
        return out.toByteArray();
    }

    public static byte[] compress2ByteFromStr(final String info) {
        if (StringUtils.isBlank(info)) {
            return null;
        }
        return compress2Byte(info.getBytes(StandardCharsets.UTF_8));
    }

    public static byte[] uncompress2Byte(byte[] compressed) {
        byte[] res = null;
        if (ArrayUtils.isNotEmpty(compressed)) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteArrayInputStream in = null;
            GZIPInputStream ginzip = null;
            try {
                in = new ByteArrayInputStream(compressed);
                ginzip = new GZIPInputStream(in);
                byte[] buffer = new byte[1024];
                int offset;
                while ((offset = ginzip.read(buffer)) != -1) {
                    out.write(buffer, 0, offset);
                }
                res = out.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                close(ginzip, in, out);
            }
        }
        return res;
    }

    public static String uncompress(byte[] compressed) {
        byte[] bytes = uncompress2Byte(compressed);
        return ArrayUtils.isNotEmpty(bytes) ? new String(bytes, StandardCharsets.UTF_8) : null;
    }

    public static String compress(final String primStr) {
        final byte[] bytes = compress2Byte(primStr);
        return bytes == null ? null : Base64.encodeBase64String(bytes);
    }

    public static void close(Closeable obj) {
        if (obj != null) {
            try {
                obj.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void close(Closeable... obj) {
        if (ArrayUtils.isNotEmpty(obj)) {
            try {
                for (Closeable o : obj) {
                    if (o != null) {
                        o.close();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String uncompress(final String compressedStr) {
        String decompressed = null;
        if (StringUtils.isNotBlank(compressedStr)) {
            decompressed = uncompress(Base64.decodeBase64(compressedStr));
        }
        return decompressed;
    }

}

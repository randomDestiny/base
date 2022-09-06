package com.iscloud.common.utils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;

/**
 * @Desc: 7z压缩工具类
 * @Author: HYbrid
 * @Date: 2022/5/30 15:35
 */
@Slf4j
@SuppressWarnings("unused")
public class SevenzUtils {

    @SneakyThrows
    public static void compress(String compressPath, boolean force, File... files) {
        if (ArrayUtils.isNotEmpty(files)) {
            for (File f : files) {
                if (f == null || !f.exists() || !f.canRead()) {
                    log.error("待压缩的文件[{}]不存在或不可读！", f == null ? StringUtils.EMPTY : f.getAbsolutePath());
                    return;
                }
            }
            File compressFile = new File(compressPath);
            if (force) {
                if (!compressFile.delete()) {
                    log.error("删除文件[{}]失败！", compressPath);
                }
            }
            if (compressFile.exists()) {
                log.error("文件{}[{}]已存在！", compressFile.isFile() ? StringUtils.EMPTY : "夹", compressPath);
                return;
            }
            if (compressFile.createNewFile()) {
                SevenZOutputFile out = new SevenZOutputFile(compressFile);
                for (File f : files) {
                    compress(out, f, null);
                }
            } else {
                log.error("创建文件[{}]失败！", compressPath);
            }
        }
    }

    @SneakyThrows
    public static void compress(SevenZOutputFile out, File input, String name) {
        if (StringUtils.isBlank(name)) {
            name = input.getName();
        }
        SevenZArchiveEntry entry;
        if (input.isDirectory()) {
            File[] array = input.listFiles();
            if (array != null) {
                if (array.length == 0) {
                    entry = out.createArchiveEntry(input,name + "/");
                    out.putArchiveEntry(entry);
                } else {
                    for (File f : array) {
                        compress(out, f, name + "/" + f.getName());
                    }
                }
            }
        } else {
            FileInputStream fos = new FileInputStream(input);
            BufferedInputStream bis = new BufferedInputStream(fos);
            entry = out.createArchiveEntry(input, name);
            out.putArchiveEntry(entry);
            int len;
            byte[] buf = new byte[1024];
            while ((len = bis.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            bis.close();
            fos.close();
            out.closeArchiveEntry();
        }
    }

    @SneakyThrows
    public static void uncompress(String srcPath, String targetPath) {
        File srcFile = new File(srcPath);
        // 判断源文件是否存在
        if (!srcFile.exists()) {
            throw new Exception(srcFile.getPath() + "所指文件不存在");
        }
        //开始解压
        SevenZFile zIn = new SevenZFile(srcFile);
        SevenZArchiveEntry entry;
        File file;
        while ((entry = zIn.getNextEntry()) != null) {
            if (!entry.isDirectory()) {
                file = new File(targetPath, entry.getName());
                if (!file.exists()) {
                    if (!new File(file.getParent()).mkdirs()) {
                        log.error("创建目录[{}]失败！", file.getParent());
                    }
                }
                OutputStream out = new FileOutputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(out);
                int len;
                byte[] buf = new byte[1024];
                while ((len = zIn.read(buf)) != -1) {
                    bos.write(buf, 0, len);
                }
                bos.close();
                out.close();
            }
        }
    }
}

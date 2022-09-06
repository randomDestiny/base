package com.iscloud.common.helper;

import com.iscloud.common.cst.BaseCst;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * @author Hybrid
 */
@SuppressWarnings("unused")
public class FileHelper implements BaseCst {

    /**
     * @Desc:   根据包路径创建文件夹，返回文件夹path
     * @Params: [path]
     * @Return: java.lang.String
     * @Author: HYbrid
     * @Date:   2022/2/26
     */
    public static String buildPackage(String pack) {
        if (StringUtils.isNotBlank(pack)) {
            // TODO:格式化路径
            pack = pack.replace(SymbolCst.SEMICOLON, StringUtils.EMPTY);
            String[] paths = pack.split(PatternCst.POINT);
            // 获取项目根目录，递归创建文件夹
            return StringHelper.appendSplit(SymbolCst.OBLIQUE_LINE, true, paths);
        }
        return StringUtils.EMPTY;
    }

    @SneakyThrows
    public static boolean forceWrite(String path, String content) {
        File bean = new File(path);
        FileUtils.forceMkdirParent(bean);
        FileUtils.deleteQuietly(bean);
        if (bean.createNewFile()) {
            byte[] bytes = StringUtils.isNotBlank(content) ? content.getBytes(StandardCharsets.UTF_8) : new byte[0];
            if (bytes.length > 0) {
                FileUtils.writeByteArrayToFile(bean, bytes);
                return true;
            }
        }
        return false;
    }

    public static boolean createFile(String path, String fileName, String fileType, String content) {
        if (StringUtils.isNotBlank(fileName)) {
            String beanFileName = fileName + SymbolCst.POINT + fileType,
                    filePath = StringUtils.isNotBlank(path) ? path + SymbolCst.OBLIQUE_LINE + beanFileName : beanFileName;
            return forceWrite(filePath, content);
        }
        return false;
    }

    public static boolean createJava4Package(String pack, String beanName, String content) {
        return createFile(buildPackage(pack), beanName, FileTypeCst.JAVA, content);
    }

    public static boolean createJava4Path(String path, String beanName, String content) {
        return createFile(path, beanName, FileTypeCst.JAVA, content);
    }

}

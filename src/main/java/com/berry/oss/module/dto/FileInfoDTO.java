package com.berry.oss.module.dto;

import com.alibaba.fastjson.JSON;
import com.berry.oss.common.exceptions.UploadException;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Title FileInfoDTO
 * Description
 * Copyright (c) 2019
 * Company  上海思贤信息技术股份有限公司
 *
 * @author berry_cooper
 * @version 1.0
 * @date 2019/6/25 16:12
 */
@Data
public class FileInfoDTO {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String DEFAULT_FILE_PATH = "/";

    private String fileName;

    private String filePath;

    /**
     * 将全路径 转化为 路径 和 文件名
     * @param fullPath
     */
    public FileInfoDTO(String fullPath) {
        if (fullPath.contains("//")) {
            logger.debug("路径不正确：{}", fullPath);
            throw new RuntimeException("路径不正确");
        }
        if (fullPath.contains(DEFAULT_FILE_PATH)) {
            this.fileName = fullPath.substring(fullPath.lastIndexOf(DEFAULT_FILE_PATH) + 1);
            String path = fullPath.substring(0, fullPath.lastIndexOf(this.fileName));
            if (path.length() > 1 && path.endsWith(DEFAULT_FILE_PATH)) {
                path = path.substring(0, path.length() - 1);
            }
            if(!path.startsWith(DEFAULT_FILE_PATH)) {
                path = DEFAULT_FILE_PATH + path;
            }
            this.filePath = path;
        }

    }
}

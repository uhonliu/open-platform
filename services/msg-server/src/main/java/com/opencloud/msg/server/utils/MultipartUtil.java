package com.opencloud.msg.server.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.opencloud.common.utils.DateUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author: liuyadu
 * @date: 2019/7/17 16:26
 * @description:
 */
public class MultipartUtil {
    /**
     * 获取附件路径
     *
     * @throws MessagingException
     */
    public static List<Map<String, String>> getMultipartFilePaths(MultipartFile[] multipartFiles) {
        List<Map<String, String>> paths = Lists.newArrayList();
        try {
            if (multipartFiles != null) {
                String dir = System.getProperty("user.dir") + File.separator + "temp" + File.separator + "upload";
                for (MultipartFile multipartFile : multipartFiles) {
                    String filename = multipartFile.getOriginalFilename();
                    String ext = null;
                    if (filename.contains(".")) {
                        ext = filename.substring(filename.lastIndexOf("."));
                    } else {
                        ext = "";
                    }
                    String uuid = UUID.randomUUID().toString().replaceAll("-", "");
                    String fileName = uuid + ext;
                    String dirPath = dir + File.separator + DateUtils.formatDate(new Date(), "yyyyMMdd");
                    File dirFile = new File(dirPath);
                    if (!dirFile.exists()) {
                        dirFile.mkdirs();
                    }
                    String filepath = dirPath + File.separator + fileName;
                    File targetFile = new File(filepath);
                    multipartFile.transferTo(targetFile);
                    Map fileMap = Maps.newHashMap();
                    fileMap.put("fileName", fileName);
                    fileMap.put("filePath", filepath);
                    fileMap.put("originalFilename", multipartFile.getOriginalFilename());
                    paths.add(fileMap);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return paths;
    }
}

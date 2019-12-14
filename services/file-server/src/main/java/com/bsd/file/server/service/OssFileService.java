package com.bsd.file.server.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * 文件处理器.
 *
 * @author liujianhong
 * @date 2019-07-02 16:12
 */
public interface OssFileService {
    /**
     * 删除一个文件
     *
     * @param realName 相对路径名.
     */
    void deleteFile(String realName);

    /**
     * 上传文件
     *
     * @param file     文件
     * @param realName 相对路径名(访问时候使用,也是存放在数据库中的字段)
     * @return 文件访问路径
     */
    String saveFile(File file, String realName);

    String saveFile(MultipartFile file, String realName) throws IOException;

    /**
     * 获得一个文件的web访问url
     *
     * @param realPath 文件的存放路径,在数据库中保存该信息.
     * @return 文件访问路径
     */
    String getFileUrl(String realPath);
}



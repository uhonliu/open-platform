package com.bsd.file.server.service;

import com.bsd.file.server.dto.UploadDto;

import java.io.InputStream;

/**
 * 七牛云存储
 *
 * @author liujianhong
 * @date 2019-07-02 16:12
 */
public interface QiNiuService {
    /**
     * 上传文件
     *
     * @param data byte[]
     * @param path 文件key
     * @return String
     */
    String upload(byte[] data, String path);

    /**
     * 上传文件
     *
     * @param inputStream InputStream
     * @param path        文件key
     * @return String
     */
    String upload(InputStream inputStream, String path);

    /**
     * 上传自定义后缀
     *
     * @param data   byte[]
     * @param suffix 后缀
     * @return String
     */
    String uploadSuffix(byte[] data, String suffix);

    /**
     * 上传自定义后缀
     *
     * @param inputStream InputStream
     * @param suffix      后缀
     * @return String
     */
    String uploadSuffix(InputStream inputStream, String suffix);

    /**
     * 获取文件访问地址
     *
     * @param fileName 文件名
     * @return String 文件地址
     */
    String getFileUrl(String fileName);

    /**
     * 获取token，前端上传使用
     *
     * @return
     */
    String getToken();

    /**
     * 上传成功回调处理
     *
     * @param authorization 授权
     * @param paramString   回调值
     * @return boolean true表示成功；false表示失败
     */
    UploadDto fileCallback(String authorization, String paramString);
}

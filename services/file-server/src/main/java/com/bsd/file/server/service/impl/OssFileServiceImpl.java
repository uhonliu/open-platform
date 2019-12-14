package com.bsd.file.server.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectResult;
import com.bsd.file.server.configuration.AliOssProperties;
import com.bsd.file.server.service.OssFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Date;

/**
 * 文件OSS操作
 *
 * @author liujianhong
 * @date 2019-07-02 16:12
 */
@Service
public class OssFileServiceImpl implements OssFileService {
    @Autowired
    private AliOssProperties aliOssProperties;

    @Override
    public void deleteFile(String realName) {
        // 创建OSSClient实例
        OSS client = new OSSClientBuilder().build(aliOssProperties.getEndpoint(), aliOssProperties.getAccessKeyId(), aliOssProperties.getAccessKeySecret());

        client.deleteObject(aliOssProperties.getBucket(), realName);

        // 关闭client
        client.shutdown();
    }

    @Override
    public String saveFile(File file, String realName) {
        // 创建OSSClient实例
        OSS client = new OSSClientBuilder().build(aliOssProperties.getEndpoint(), aliOssProperties.getAccessKeyId(), aliOssProperties.getAccessKeySecret());

        PutObjectResult putResult = client.putObject(aliOssProperties.getBucket(), realName, file);

        // 关闭client
        client.shutdown();

        file.delete();

        if (putResult.getETag() != null && !"".equals(putResult.getETag())) {
            return aliOssProperties.getCdnDomain() + '/' + realName;
        }

        return null;
    }

    @Override
    public String saveFile(MultipartFile file, String realName) throws IOException {
        // 创建OSSClient实例
        OSS client = new OSSClientBuilder().build(aliOssProperties.getEndpoint(), aliOssProperties.getAccessKeyId(), aliOssProperties.getAccessKeySecret());

        PutObjectResult putResult = client.putObject(aliOssProperties.getBucket(), realName, file.getInputStream());

        // 关闭client
        client.shutdown();

        if (putResult.getETag() != null && !"".equals(putResult.getETag())) {
            return aliOssProperties.getCdnDomain() + '/' + realName;
        }

        return null;
    }

    @Override
    public String getFileUrl(String realPath) {
        // 创建OSSClient实例
        OSS client = new OSSClientBuilder().build(aliOssProperties.getEndpoint(), aliOssProperties.getAccessKeyId(), aliOssProperties.getAccessKeySecret());

        // 设置URL过期时间为10年  3600L* 1000*24*365*10
        Date expiration = new Date(System.currentTimeMillis() + 3600L * 1000 * 24 * 365 * 10);
        // 生成URL
        URL url = client.generatePresignedUrl(aliOssProperties.getBucket(), realPath, expiration);
        if (url != null) {
            return url.toString();
        }
        return null;
    }
}

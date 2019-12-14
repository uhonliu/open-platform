package com.bsd.file.server.service.impl;

import com.alibaba.fastjson.JSON;
import com.bsd.file.server.configuration.QiNiuProperties;
import com.bsd.file.server.dto.CallbackBodyDto;
import com.bsd.file.server.dto.UploadDto;
import com.bsd.file.server.service.QiNiuService;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

/**
 * 七牛云存储
 *
 * @author liujianhong
 * @date 2019-07-02 16:12
 */
@Service
@Slf4j
public class QiNiuServiceImpl implements QiNiuService {
    @Autowired
    private QiNiuProperties qiNiuProperties;
    private UploadManager uploadManager;
    private Auth auth;
    private String token;
    private String callbackBodyType = "application/json";

    @PostConstruct
    private void init() {
        uploadManager = new UploadManager(new Configuration(Zone.autoZone()));
        auth = Auth.create(qiNiuProperties.getAccessKey(), qiNiuProperties.getSecretKey());
    }

    /**
     * 文件路径
     *
     * @param prefix 前缀
     * @param suffix 后缀
     * @return 返回上传路径
     */
    public String getPath(String prefix, String suffix) {
        //生成uuid
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        //文件路径
        String path = DateFormatUtils.format(new Date(), "yyyyMMdd") + "/" + uuid;

        if (StringUtils.isNotBlank(prefix)) {
            path = prefix + "/" + path;
        }

        return path + suffix;
    }

    @Override
    public String upload(byte[] data, String path) {
        try {
            Response res = uploadManager.put(data, path, getToken());
            if (!res.isOK()) {
                throw new RuntimeException("上传七牛出错：" + res.toString());
            }
        } catch (Exception e) {
            throw new RuntimeException("上传文件失败，请核对七牛配置信息", e);
        }

        return path;
    }

    @Override
    public String upload(InputStream inputStream, String path) {
        try {
            byte[] data = IOUtils.toByteArray(inputStream);
            return this.upload(data, path);
        } catch (IOException e) {
            throw new RuntimeException("上传文件失败", e);
        }
    }

    @Override
    public String uploadSuffix(byte[] data, String suffix) {
        return upload(data, getPath(qiNiuProperties.getPrefix(), suffix));
    }

    @Override
    public String getFileUrl(String fileName) {
        return qiNiuProperties.getDomain() + "/" + fileName;
    }

    @Override
    public String uploadSuffix(InputStream inputStream, String suffix) {
        return upload(inputStream, getPath(qiNiuProperties.getPrefix(), suffix));
    }

    @Override
    public String getToken() {
        token = auth.uploadToken(qiNiuProperties.getBucket());
        return token;
    }

    @Override
    public UploadDto fileCallback(String authorization, String paramString) {
        // 接收七牛回调过来的内容
        try {
            log.info("回调入参：authorization=[{}],paramString=[{}]", authorization, paramString);
            boolean validCallback = auth.isValidCallback(authorization, qiNiuProperties.getCallbackUrl(), paramString.getBytes("UTF-8"), callbackBodyType);
            log.info("回调验证结果validCallback=[{}]", validCallback);
            if (validCallback) {
                CallbackBodyDto dto = JSON.parseObject(paramString, CallbackBodyDto.class);
                if (dto != null) {
                    //开始保存文件数据
                    UploadDto uploadDto = new UploadDto();
                    //TODO 可以把文件信息保存到资源表里，这边返回资源表的id
                    //uploadDto.setFileId(id);
                    uploadDto.setHash(dto.getHash());
                    uploadDto.setUrl(getFileUrl(dto.getFilename()));
                    return uploadDto;
                } else {
                    throw new RuntimeException(String.format("回调参数失败:%s", paramString));
                }
            } else {
                throw new RuntimeException(String.format("回调authorization:%s验证 失败", authorization));
            }
        } catch (Exception ex) {
            log.error("回调异常", ex);
            throw new RuntimeException(String.format("回调authorization:%s验证 失败", authorization));
        }
    }
}

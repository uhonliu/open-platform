package com.bsd.dingtalk.server.util;

import com.bsd.dingtalk.server.constants.URLConstant;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiMediaUploadRequest;
import com.dingtalk.api.response.OapiMediaUploadResponse;
import com.taobao.api.ApiException;
import com.taobao.api.FileItem;

import java.io.File;

public class FileHelper {
    //上传媒体文件
    public static String uploadFile(String accessToken, String fileType, String path) {
        try {
            DingTalkClient client = new DefaultDingTalkClient(URLConstant.URL_UPLOAD_FILE);
            OapiMediaUploadRequest request = new OapiMediaUploadRequest();
            request.setType(fileType);
            request.setMedia(new FileItem(path));
            OapiMediaUploadResponse response = client.execute(request, accessToken);
            if (response.getErrcode() != 0) {
                return null;
            }
            return response.getMediaId();
        } catch (ApiException e) {
            e.printStackTrace();
            return null;
        }
    }

    //上传媒体文件
    public static String uploadFile(String accessToken, String fileType, File file) {
        try {
            DingTalkClient client = new DefaultDingTalkClient(URLConstant.URL_UPLOAD_FILE);
            OapiMediaUploadRequest request = new OapiMediaUploadRequest();
            request.setType(fileType);
            request.setMedia(new FileItem(file));
            OapiMediaUploadResponse response = client.execute(request, accessToken);
            if (response.getErrcode() != 0) {
                return null;
            }
            return response.getMediaId();
        } catch (ApiException e) {
            e.printStackTrace();
            return null;
        }
    }
}

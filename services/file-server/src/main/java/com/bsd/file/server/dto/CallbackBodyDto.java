package com.bsd.file.server.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class CallbackBodyDto implements Serializable {
    private static final long serialVersionUID = -7442461452046496787L;
    /**
     * 文件上传成功后的 HTTPETag。若上传时未指定资源ID，Etag将作为资源ID使用。
     */
    private String etag;
    /**
     * 上传资源的后缀名，通过自动检测的mimeType 或者原文件的后缀来获取。
     */
    private String ext;
    /**
     * 上传的原始文件名。
     */
    private String filename;
    /**
     * 资源类型，例如JPG图片的资源类型为image/jpg。
     */
    private String mimeType;
    /**
     * 获得文件保存在空间中的资源名。
     */
    private String key;
    /**
     * 值同etag
     */
    private String hash;
    /**
     * 获得上传的目标空间名。
     */
    private String bucket;
    /**
     * 资源尺寸，单位为字节。
     */
    private String fsize;
    /**
     * 音视频转码持久化的进度查询ID。
     */
    private String persistentId;
    /**
     * 音频时长
     */
    private String audioDuration;
    /**
     * 视频时长
     */
    private String videoDuration;
    /**
     * 用户id
     */
    private String userId;


    public CallbackBodyDto() {

    }


    /**
     * {\"etag\":\"$(etag)\",\"ext\":\"$(ext)\",\"filename\":\"$(fname)\",\"mimeType\":\"$(mimeType)\",\"key\":\"$(key)\",\"hash\":\"$(etag)\",\"bucket\":\"$(bucket)\",\"fsize\":\"$(fsize)\", \"userId\" : \"" + userId +"\"}";
     */
    public CallbackBodyDto(String userId) {
        this.etag = "$(etag)";
        this.ext = "$(ext)";
        this.filename = "$(fname)";
        this.mimeType = "$(mimeType)";
        this.key = "$(key)";
        this.hash = "$(etag)";
        this.bucket = "$(bucket)";
        this.fsize = "$(fsize)";
        this.persistentId = "$(persistentId)";
        this.audioDuration = "$(avinfo.audio.duration)";
        this.videoDuration = "$(avinfo.video.duration)";
        this.userId = userId;
    }
}

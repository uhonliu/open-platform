package com.bsd.file.server.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UploadDto implements Serializable {
    private static final long serialVersionUID = -7442461452046496737L;

    private Long fileId;
    private String url;
    private String hash;
    private String convertErrMsg;
}

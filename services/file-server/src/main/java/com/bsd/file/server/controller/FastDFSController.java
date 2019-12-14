package com.bsd.file.server.controller;

import com.bsd.file.server.service.FastDFSService;
import com.google.common.collect.Maps;
import com.opencloud.common.exception.OpenAlertException;
import com.opencloud.common.model.ResultBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * FastDFS文件操作
 *
 * @author liujianhong
 * @date 2019-07-01
 */
@Api(tags = "FastDFS服务接口")
@RequestMapping("/dfs")
@Slf4j
@RestController
public class FastDFSController {
    @Autowired
    private FastDFSService fastDFSService;

    /**
     * 文件上传
     *
     * @return
     */
    @ApiOperation(value = "文件上传", notes = "文件上传")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file", required = true, value = "文件流对象,接收数组格式", dataType = "__File", paramType = "form"),
            @ApiImplicitParam(name = "prefix", value = "上传文件夹,默认group0", paramType = "form")
    })
    @PostMapping("/upload")
    @ResponseBody
    public ResultBody fileUpload(@RequestParam("file") MultipartFile file, @RequestParam(value = "prefix", required = false) String prefix) {
        try {
            if (file.isEmpty()) {
                throw new OpenAlertException("上传文件不能为空");
            }

            if (prefix == null) {
                prefix = "";
            }

            String[] fileAbsolutePath = fastDFSService.uploadFile(prefix, file);
            if (fileAbsolutePath == null) {
                log.error("upload file failed,please upload again!");
                throw new OpenAlertException("上传失败");
            }
            String path = fastDFSService.getTrackerUrl() + fileAbsolutePath[0] + "/" + fileAbsolutePath[1];

            Map<String, String> map = Maps.newHashMap();
            map.put("fileName", fileAbsolutePath[1]);
            map.put("fileUrl", path);
            return ResultBody.ok().data(map);
        } catch (Exception e) {
            return ResultBody.failed().msg(e.getMessage());
        }
    }
}



package com.bsd.file.server.controller;

import com.bsd.file.server.service.OssFileService;
import com.google.common.collect.Maps;
import com.opencloud.common.exception.OpenAlertException;
import com.opencloud.common.model.ResultBody;
import com.opencloud.common.utils.FileHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Objects;

/**
 * 阿里云OSS文件操作
 *
 * @author liujianhong
 * @date 2019-07-01
 */
@Api(tags = "阿里云OSS服务接口")
@RequestMapping("/oss")
@RestController
public class AliOssController {
    @Autowired
    private OssFileService ossFileService;

    /**
     * 文件上传
     *
     * @return
     */
    @ApiOperation(value = "文件上传", notes = "文件上传")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file", required = true, value = "文件流对象,接收数组格式", dataType = "__File", paramType = "form"),
            @ApiImplicitParam(name = "prefix", value = "上传文件夹", paramType = "form")
    })
    @PostMapping("/upload")
    @ResponseBody
    public ResultBody saveFile(@RequestParam("file") MultipartFile file, @RequestParam(value = "prefix") String prefix) {
        try {
            if (file.isEmpty()) {
                throw new OpenAlertException("上传文件不能为空");
            }

            if (prefix == null) {
                prefix = "";
            }

            String fileName = FileHelper.getPath(prefix, Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf(".")));
            String url = ossFileService.saveFile(file, fileName);
            if (url == null) {
                throw new OpenAlertException("上传失败");
            }
            Map<String, String> map = Maps.newHashMap();
            map.put("fileName", fileName);
            map.put("fileUrl", url);
            return ResultBody.ok().data(map);
        } catch (Exception e) {
            return ResultBody.failed().msg(e.getMessage());
        }
    }

    /**
     * 删除文件
     *
     * @param name 文件名
     */
    @ApiOperation(value = "删除文件", notes = "删除文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", required = true, value = "文件名", paramType = "form")
    })
    @PostMapping(value = "/delete")
    @ResponseBody
    public ResultBody deleteFile(@RequestParam(value = "name") String name) {
        ossFileService.deleteFile(name);

        //返回结果
        return ResultBody.ok().msg("删除成功");
    }

    /**
     * 获取文件访问地址
     */
    @ApiOperation(value = "获取文件访问地址", notes = "获取文件访问地址")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", required = true, value = "文件名", paramType = "form")
    })
    @GetMapping(value = "/url")
    @ResponseBody
    public ResultBody getFileUrl(@RequestParam(value = "name") String name) {
        String url = ossFileService.getFileUrl(name);
        if (url != null) {
            Map<String, String> map = Maps.newHashMap();
            map.put("url", url);
            return ResultBody.ok().data(map);
        }

        //返回结果
        return ResultBody.failed().msg("获取失败");
    }
}



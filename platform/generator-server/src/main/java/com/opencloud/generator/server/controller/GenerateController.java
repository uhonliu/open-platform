package com.opencloud.generator.server.controller;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.google.common.collect.Maps;
import com.opencloud.common.model.ResultBody;
import com.opencloud.common.utils.DateUtils;
import com.opencloud.generator.server.service.GenerateConfig;
import com.opencloud.generator.server.service.GeneratorService;
import com.opencloud.generator.server.utils.ZipUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @author: liuyadu
 * @date: 2019/7/19 15:26
 * @description:
 */
@Api(tags = "在线代码生成器")
@RestController
@RequestMapping("/generate")
public class GenerateController {
    /**
     * 获取所有表信息
     *
     * @return
     */
    @ApiOperation(value = "获取所有表信息", notes = "获取所有表信息")
    @PostMapping("/tables")
    public ResultBody<List<TableInfo>> tables(
            @RequestParam(value = "type") String type,
            @RequestParam(value = "driverName") String driverName,
            @RequestParam(value = "url") String url,
            @RequestParam(value = "username") String username,
            @RequestParam(value = "password") String password
    ) {
        GlobalConfig gc = new GlobalConfig();
        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setDbType(DbType.getDbType(type));
        dsc.setDriverName(driverName);
        dsc.setUrl(url);
        dsc.setUsername(username);
        dsc.setPassword(password);
        StrategyConfig strategy = new StrategyConfig();
        TemplateConfig templateConfig = new TemplateConfig();
        ConfigBuilder config = new ConfigBuilder(new PackageConfig(), dsc, strategy, templateConfig, gc);
        List<TableInfo> list = config.getTableInfoList();
        return ResultBody.ok().data(list);
    }


    @ApiOperation(value = "代码生成并下载", notes = "代码生成并下载")
    @PostMapping("/execute")
    public ResultBody<List<TableInfo>> execute(
            @RequestParam(value = "type") String type,
            @RequestParam(value = "driverName") String driverName,
            @RequestParam(value = "url") String url,
            @RequestParam(value = "username") String username,
            @RequestParam(value = "password") String password,
            @RequestParam(value = "author") String author,
            @RequestParam(value = "parentPackage") String parentPackage,
            @RequestParam(value = "moduleName") String moduleName,
            @RequestParam(value = "includeTables") String includeTables,
            @RequestParam(value = "tablePrefix") String tablePrefix
    ) throws Exception {
        String outputDir = System.getProperty("user.dir") + File.separator + "temp" + File.separator + "generator" + File.separator + DateUtils.getCurrentTimestampStr();
        GenerateConfig config = new GenerateConfig();
        config.setDbType(DbType.getDbType(type));
        config.setJdbcUrl(url);
        config.setJdbcUserName(username);
        config.setJdbcPassword(password);
        config.setJdbcDriver(driverName);
        config.setAuthor(author);
        config.setParentPackage(parentPackage);
        config.setModuleName(moduleName);
        config.setIncludeTables(includeTables.split(","));
        config.setTablePrefix(tablePrefix.split(","));
        config.setOutputDir(outputDir);
        GeneratorService.execute(config);
        String fileName = moduleName + ".zip";
        String filePath = outputDir + File.separator + fileName;
        // 压缩目录
        String[] srcDir = {outputDir + File.separator + (parentPackage.substring(0, parentPackage.indexOf("."))), outputDir + File.separator + "src"};
        ZipUtil.toZip(srcDir, filePath, true);
        Map data = Maps.newHashMap();
        data.put("filePath", filePath);
        data.put("fileName", fileName);
        return ResultBody.ok().data(data);
    }
}

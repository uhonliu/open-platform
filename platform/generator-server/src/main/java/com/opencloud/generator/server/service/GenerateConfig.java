package com.opencloud.generator.server.service;

import com.baomidou.mybatisplus.annotation.DbType;
import lombok.Data;

import java.io.Serializable;

/**
 * 代码生成参数配置
 *
 * @author liuyadu
 */
@Data
public class GenerateConfig implements Serializable {
    /**
     * 模块名称
     */
    private String moduleName;

    /**
     * 代码生成的类的父包名称
     */
    private String parentPackage;
    /**
     * 去掉表的前缀
     */
    private String[] tablePrefix;

    /**
     * 代码生成包含的表，可为空，为空默认生成所有
     */
    private String[] includeTables;

    /**
     * 生成代码里，注释的作者
     */
    private String author;

    /**
     * 数据库类型
     */
    private DbType dbType;

    /**
     * jdbc驱动
     */
    private String jdbcDriver;

    /**
     * 数据库连接地址
     */
    private String jdbcUrl;

    /**
     * 数据库账号
     */
    private String jdbcUserName;

    /**
     * 数据库密码
     */
    private String jdbcPassword;

    /**
     * 代码生成目录
     */
    private String outputDir;
}

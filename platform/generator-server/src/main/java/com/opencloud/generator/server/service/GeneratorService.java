package com.opencloud.generator.server.service;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.po.TableFill;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 代码生成器
 */
public class GeneratorService {
    public static void execute(GenerateConfig generateConfig) {
        AutoGenerator mpg = new AutoGenerator();
        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        gc.setOutputDir(generateConfig.getOutputDir());
        gc.setFileOverride(true);
        //ActiveRecord特性
        gc.setActiveRecord(false);
        // XML ResultMap
        gc.setBaseResultMap(true);
        // XML columList
        gc.setBaseColumnList(true);
        gc.setEnableCache(false);
        // 自动打开输出目录
        gc.setOpen(false);
        gc.setAuthor(generateConfig.getAuthor());
        gc.setSwagger2(true);
        //主键策略
        gc.setIdType(IdType.ASSIGN_ID);
        // 自定义文件命名，注意 %s 会自动填充表实体属性！
        gc.setServiceName("%sService");
        gc.setServiceImplName("%sServiceImpl");

        mpg.setGlobalConfig(gc);

        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setDbType(generateConfig.getDbType());
        dsc.setDriverName(generateConfig.getJdbcDriver());
        dsc.setUrl(generateConfig.getJdbcUrl());
        dsc.setUsername(generateConfig.getJdbcUserName());
        dsc.setPassword(generateConfig.getJdbcPassword());
        dsc.setTypeConvert(new MySqlTypeConvert() {
            // 自定义数据库表字段类型转换【可选】
            @Override
            public DbColumnType processTypeConvert(GlobalConfig globalConfig, String fieldType) {
                //将数据库中datetime转换成date
                if (fieldType.toLowerCase().contains("datetime")) {
                    return DbColumnType.DATE;
                }
                return (DbColumnType) super.processTypeConvert(globalConfig, fieldType);
            }
        });
        mpg.setDataSource(dsc);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setCapitalMode(false);
        strategy.setRestControllerStyle(true);
        strategy.setEntityLombokModel(true);
        strategy.setEntityTableFieldAnnotationEnable(false);
        // 此处可以移除表前缀表前缀
        strategy.setTablePrefix(generateConfig.getTablePrefix());

        // 表名生成策略
        strategy.setNaming(NamingStrategy.underline_to_camel);
        // 字段生成策略
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        strategy.setSuperEntityColumns("create_time", "update_time");
        // mapper 父类
        strategy.setSuperMapperClass("com.opencloud.common.mybatis.base.mapper.SuperMapper");
        // 实体父类
        strategy.setSuperEntityClass("com.opencloud.common.mybatis.base.entity.AbstractEntity");
        // 接口父类
        strategy.setSuperServiceClass("com.opencloud.common.mybatis.base.service.IBaseService");
        // 接口实现类父类
        strategy.setSuperServiceImplClass("com.opencloud.common.mybatis.base.service.impl.BaseServiceImpl");
        // 需要生成的表
        strategy.setInclude(generateConfig.getIncludeTables());

        ConfigBuilder config = new ConfigBuilder(new PackageConfig(), dsc, strategy, new TemplateConfig(), gc);
        List<TableInfo> list = config.getTableInfoList();

        // 公共字段填充
        ArrayList<TableFill> tableFills = new ArrayList<>();
        tableFills.add(new TableFill("createTime", FieldFill.INSERT));
        tableFills.add(new TableFill("updateTime", FieldFill.UPDATE));
        strategy.setTableFillList(tableFills);
        mpg.setStrategy(strategy);
        // 包配置
        PackageConfig pc = new PackageConfig();
        //父包名
        pc.setParent(generateConfig.getParentPackage());
        //父包模块名
        pc.setModuleName(generateConfig.getModuleName());
        //实体类父包
        pc.setEntity("client.model.entity");
        //controller父包
        pc.setController("server.controller");
        //mapper父包
        pc.setMapper("server.mapper");
        //xml父包
        pc.setXml("resources.mapper");
        pc.setServiceImpl("server.service.impl");
        pc.setService("server.service");

        // 自定义配置
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                // to do nothing
            }
        };
        String jsPath = "/templates/api.js.vm";
        String vuePath = "/templates/index.vue.vm";
        List<FileOutConfig> focList = new ArrayList<>();
        // 自定义配置会被优先输出
        focList.add(new FileOutConfig(jsPath) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
                String path = gc.getOutputDir() + File.separator + "src/api" + File.separator + tableInfo.getEntityName() + ".js";
                return path;
            }
        });
        focList.add(new FileOutConfig(vuePath) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                String path = gc.getOutputDir() + File.separator + "src/view/module" + File.separator + pc.getModuleName() + File.separator + tableInfo.getEntityName() + File.separator + "index.vue";
                return path;
            }
        });

        cfg.setFileOutConfigList(focList);
        mpg.setCfg(cfg);
        mpg.setPackageInfo(pc);
        // 执行生成
        mpg.execute();
    }
}

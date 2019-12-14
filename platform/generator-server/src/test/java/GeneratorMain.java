import com.opencloud.generator.server.service.GenerateConfig;
import com.opencloud.generator.server.service.GeneratorService;

import java.io.File;

public class GeneratorMain {
    public static void main(String[] args) {
        String outputDir = System.getProperty("user.dir") + File.separator + "generator";
        GenerateConfig config = new GenerateConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/open-platform?useSSL=false&useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai");
        config.setJdbcUserName("root");
        config.setJdbcPassword("root");
        config.setJdbcDriver("com.mysql.cj.jdbc.Driver");
        config.setAuthor("liuyadu");
        config.setParentPackage("com.opencloud");
        config.setModuleName("base");
        config.setIncludeTables(new String[]{"base_tentant"});
        config.setTablePrefix(new String[]{"base_"});
        config.setOutputDir(outputDir);
        GeneratorService.execute(config);
    }
}

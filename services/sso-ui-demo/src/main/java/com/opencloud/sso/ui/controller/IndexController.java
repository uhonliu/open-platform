package com.opencloud.sso.ui.controller;

import com.opencloud.common.security.OpenHelper;
import com.opencloud.common.security.OpenUserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author: liuyadu
 * @date: 2018/10/29 15:59
 * @description:
 */
@Controller
public class IndexController {
    /**
     * 欢迎页
     *
     * @return
     */
    @GetMapping("/")
    public String index(ModelMap modelMap) {
        OpenUserDetails user = OpenHelper.getUser();
        modelMap.put("user", user);
        return "index";
    }
}

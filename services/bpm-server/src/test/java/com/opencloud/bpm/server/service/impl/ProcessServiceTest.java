package com.opencloud.bpm.server.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.opencloud.common.test.BaseTest;
import org.activiti.engine.repository.ProcessDefinition;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author: liuyadu
 * @date: 2019/4/4 11:05
 * @description:
 */
public class ProcessServiceTest extends BaseTest {
    @Autowired
    private ProcessEngineService processEngineService;

    @Test
    public void findProcessDefinition() {
        IPage<ProcessDefinition> result = processEngineService.findProcessDefinition("", 1, 10);
        System.out.println(JSONObject.toJSONString(result));
    }
}
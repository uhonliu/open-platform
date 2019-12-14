package com.opencloud.msg.server.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.opencloud.common.model.PageParams;
import com.opencloud.common.model.ResultBody;
import com.opencloud.msg.client.model.WebHookMessage;
import com.opencloud.msg.client.model.entity.WebHookLogs;
import com.opencloud.msg.client.service.IWebHookClient;
import com.opencloud.msg.server.service.DelayMessageService;
import com.opencloud.msg.server.service.WebHookLogsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author woodev
 */
@RestController
@Api(value = "异步通知", tags = "异步通知")
public class WebHookController implements IWebHookClient {
    @Autowired
    private DelayMessageService delayMessageService;
    @Autowired
    private WebHookLogsService webHookLogsService;

    @ApiOperation(value = "Webhook异步通知", notes = "即时推送，重试通知时间间隔为 5s、10s、2min、5min、10min、30min、1h、2h、6h、15h，直到你正确回复状态 200 并且返回 success 或者超过最大重发次数")
    @Override
    @PostMapping("/webhook")
    public ResultBody<String> send(
            @RequestBody WebHookMessage message
    ) throws Exception {
        delayMessageService.send(message);
        return ResultBody.ok();
    }

    /**
     * 获取分页异步通知列表
     *
     * @return
     */
    @ApiOperation(value = "获取分页异步通知列表", notes = "获取分页异步通知列表")
    @GetMapping("/webhook/logs")
    public ResultBody<IPage<WebHookLogs>> getLogsListPage(@RequestParam(required = false) Map map) {
        return ResultBody.ok().data(webHookLogsService.findListPage(new PageParams(map)));
    }
}

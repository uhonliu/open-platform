package com.opencloud.msg.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.opencloud.common.model.PageParams;
import com.opencloud.msg.client.model.entity.WebHookLogs;
import com.opencloud.msg.server.mapper.WebHookLogsMapper;
import com.opencloud.msg.server.service.WebHookLogsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 异步通知日志接口
 *
 * @author: liuyadu
 * @date: 2019/2/13 14:39
 * @description:
 */
@Service
public class WebHookLogsServiceImpl implements WebHookLogsService {
    @Autowired
    private WebHookLogsMapper webHookLogsMapper;

    /**
     * 添加日志
     *
     * @param log
     */
    @Override
    public void addLog(WebHookLogs log) {
        webHookLogsMapper.insert(log);
    }

    /**
     * 更细日志
     *
     * @param log
     */
    @Override
    public void modifyLog(WebHookLogs log) {
        webHookLogsMapper.updateById(log);
    }

    /**
     * 根据主键获取日志
     *
     * @param logId
     * @return
     */
    @Override
    public WebHookLogs getLog(String logId) {
        return webHookLogsMapper.selectById(logId);
    }

    /**
     * 分页查询
     *
     * @param pageParams
     * @return
     */
    @Override
    public IPage<WebHookLogs> findListPage(PageParams pageParams) {
        WebHookLogs query = pageParams.mapToObject(WebHookLogs.class);
        QueryWrapper<WebHookLogs> queryWrapper = new QueryWrapper();
        queryWrapper.lambda()
                .likeRight(ObjectUtils.isNotEmpty(query.getUrl()), WebHookLogs::getUrl, query.getUrl())
                .eq(ObjectUtils.isNotEmpty(query.getType()), WebHookLogs::getType, query.getType())
                .eq(ObjectUtils.isNotEmpty(query.getResult()), WebHookLogs::getResult, query.getResult());
        return webHookLogsMapper.selectPage(new Page(pageParams.getPage(), pageParams.getLimit()), queryWrapper);
    }
}

package com.opencloud.msg.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.opencloud.common.mybatis.base.service.impl.BaseServiceImpl;
import com.opencloud.msg.client.model.entity.EmailTemplate;
import com.opencloud.msg.server.mapper.EmailTemplateMapper;
import com.opencloud.msg.server.service.EmailTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 邮件模板配置 服务实现类
 *
 * @author liuyadu
 * @date 2019-07-17
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class EmailTemplateServiceImpl extends BaseServiceImpl<EmailTemplateMapper, EmailTemplate> implements EmailTemplateService {
    @Autowired
    private EmailTemplateMapper emailTemplateMapper;

    /**
     * 根据模板编号获取模板
     *
     * @param code
     * @return
     */
    @Override
    public EmailTemplate getByCode(String code) {
        QueryWrapper<EmailTemplate> queryWrapper = new QueryWrapper();
        queryWrapper.eq("code", code);
        return emailTemplateMapper.selectOne(queryWrapper);
    }
}

package com.opencloud.msg.server.service;

import com.opencloud.common.mybatis.base.service.IBaseService;
import com.opencloud.msg.client.model.entity.EmailTemplate;

/**
 * 邮件模板配置 服务类
 *
 * @author admin
 * @date 2019-07-25
 */
public interface EmailTemplateService extends IBaseService<EmailTemplate> {
    EmailTemplate getByCode(String code);
}

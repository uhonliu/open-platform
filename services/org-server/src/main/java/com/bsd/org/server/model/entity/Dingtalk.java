package com.bsd.org.server.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.opencloud.common.mybatis.base.entity.AbstractEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 钉钉配置信息
 *
 * @author lrx
 * @date 2019-08-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("org_dingtalk")
public class Dingtalk extends AbstractEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 企业ID
     */
    @TableId(value = "company_id", type = IdType.INPUT)
    private Long companyId;

    /**
     * 企业corpid
     */
    private String corpId;

    /**
     * 应用的agentdId
     */
    private String agentdId;

    /**
     * 应用的AppKey
     */
    private String appKey;

    /**
     * 应用的AppSecret
     */
    private String appSecret;

    /**
     * 数据加密密钥
     */
    private String encodingAesKey;

    /**
     * 加解密需要用到的token
     */
    private String token;

    /**
     * 创建人
     */
    private Long createBy;

    /**
     * 最后修改人
     */
    private Long updateBy;
}

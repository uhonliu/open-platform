package com.opencloud.msg.client.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.opencloud.common.mybatis.base.entity.AbstractEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 邮件发送配置
 *
 * @author admin
 * @date 2019-07-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("msg_email_config")
@ApiModel(value = "EmailConfig对象", description = "邮件发送配置")
public class EmailConfig extends AbstractEntity {
    private static final long serialVersionUID = 1L;

    @TableId(value = "config_id", type = IdType.ASSIGN_ID)
    private Long configId;

    @ApiModelProperty(value = "配置名称")
    private String name;

    @ApiModelProperty(value = "发件服务器域名")
    private String smtpHost;

    @ApiModelProperty(value = "发件服务器账户")
    private String smtpUsername;

    @ApiModelProperty(value = "发件服务器密码")
    private String smtpPassword;

    @ApiModelProperty(value = "保留数据0-否 1-是 不允许删除")
    private Integer isPersist;

    @ApiModelProperty(value = "是否为默认 0-否 1-是 ")
    private Integer isDefault;
}

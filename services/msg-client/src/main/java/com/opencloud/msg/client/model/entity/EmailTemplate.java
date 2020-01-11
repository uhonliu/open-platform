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
 * 邮件模板配置
 *
 * @author admin
 * @date 2019-07-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("msg_email_template")
@ApiModel(value = "EmailTemplate对象", description = "邮件模板配置")
public class EmailTemplate extends AbstractEntity {
    private static final long serialVersionUID = 1L;

    @TableId(value = "tpl_id", type = IdType.ASSIGN_ID)
    private Long tplId;

    @ApiModelProperty(value = "模板名称")
    private String name;

    @ApiModelProperty(value = "模板编码")
    private String code;

    @ApiModelProperty(value = "发送服务器配置")
    private Long configId;

    @ApiModelProperty(value = "模板")
    private String template;

    @ApiModelProperty(value = "模板参数")
    private String params;
}

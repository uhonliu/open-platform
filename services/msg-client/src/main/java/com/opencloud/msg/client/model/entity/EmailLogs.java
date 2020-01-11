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
 * 邮件发送日志
 *
 * @author admin
 * @date 2019-07-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("msg_email_logs")
@ApiModel(value = "EmailLogs对象", description = "邮件发送日志")
public class EmailLogs extends AbstractEntity {
    private static final long serialVersionUID = 1L;

    @TableId(value = "log_id", type = IdType.ASSIGN_ID)
    private Long logId;

    private String subject;

    private String sendTo;

    private String sendCc;

    private String content;

    @ApiModelProperty(value = "附件路径")
    private String attachments;

    @ApiModelProperty(value = "发送次数")
    private Integer sendNums;

    @ApiModelProperty(value = "错误信息")
    private String error;

    @ApiModelProperty(value = "0-失败 1-成功")
    private Integer result;

    @ApiModelProperty(value = "发送配置")
    private String config;

    @ApiModelProperty(value = "模板编号")
    private String tplCode;
}

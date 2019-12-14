package com.bsd.payment.server.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.bsd.payment.server.model.entity.TransOrder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author LJH-PC
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TransOrderPo extends TransOrder {
    //===================非表字段扩展=====================
    @TableField(exist = false)
    private String channelName;

    @TableField(exist = false)
    private int queryCount = 0;
}
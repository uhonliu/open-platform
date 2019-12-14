package com.bsd.user.server.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import com.bsd.user.server.enums.UserSourceEnum;
import com.opencloud.common.exception.OpenAlertException;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户导出属性模型
 *
 * @Author: linrongxin
 * @Date: 2019/8/27 12:18
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ExcelPropertyUserModel extends BaseRowModel {
    /**
     * 用户ID
     */
    @ExcelProperty(value = "用户ID", index = 0)
    private String userIdStr;

    /**
     * 手机号
     */
    @ExcelProperty(value = "手机号码", index = 1)
    private String mobile;


    /**
     * 用户昵称
     */
    @ExcelProperty(value = "用户昵称", index = 2)
    private String nickname;


    /**
     * 0-跨境知道 1-卖家成长 3-人工录入
     */
    @ExcelProperty(value = "用户来源", index = 3)
    private String sourceStr;

    /**
     * 状态:0-禁用 1-启用 2-锁定
     */
    @ExcelProperty(value = "账号状态", index = 4)
    private String statusStr;


    private Integer status;

    public String getStatusStr() {
        if (this.status.intValue() == 0) {
            return "禁用";
        } else if (this.status.intValue() == 1) {
            return "启用";
        } else if (this.status.intValue() == 2) {
            return "锁定";
        } else {
            throw new OpenAlertException("状态出错");
        }
    }


    private Long userId;

    public String getUserIdStr() {
        return String.valueOf(userId);
    }

    private Integer source;

    public String getSourceStr() {
        return UserSourceEnum.userSourceMap().get(this.source);
    }
}

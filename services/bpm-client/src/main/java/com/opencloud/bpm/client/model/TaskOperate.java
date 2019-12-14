package com.opencloud.bpm.client.model;

import com.opencloud.bpm.client.constants.TaskOperateType;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * 流程操作封装类
 *
 * @author admin
 * @date 2018/5/31
 */
public class TaskOperate implements Serializable {
    private static final long serialVersionUID = -6778931794162163750L;

    /**
     * 任务Id
     */
    private String taskId;

    /**
     * 流程操作类型
     */
    private TaskOperateType operateType = TaskOperateType.TASK_APPROVAL;

    /**
     * 审批结果 true|false
     */
    private Boolean pass = false;

    /**
     * 批注
     */
    private String comment = "";

    /**
     * 备注
     */
    private String remark = "";

    /**
     * 操作时间
     */
    private Date createTime = new Date();

    /**
     * 操作员
     */
    private String user;

    /**
     * 上传附件可访问路径
     */
    private Set<String> attachmentUrls = new HashSet<>();


    public TaskOperateType getOperateType() {
        return operateType;
    }

    public void setOperateType(TaskOperateType operateType) {
        this.operateType = operateType;
    }

    public Boolean getPass() {
        return pass;
    }

    public void setPass(Boolean pass) {
        this.pass = pass;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Set<String> getAttachmentUrls() {
        return attachmentUrls;
    }

    public void setAttachmentUrls(Set<String> attachmentUrls) {
        this.attachmentUrls = attachmentUrls;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}

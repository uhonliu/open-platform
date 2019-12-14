package com.opencloud.msg.client.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

/**
 * @author liuyadu
 */
@TableName("msg_webhook_logs")
public class WebHookLogs implements Serializable {
    @TableId
    private String msgId;

    /**
     * 重试次数
     */
    private Integer retryNums;

    /**
     * 通知总次数
     */
    private Integer totalNums;

    /**
     * 延迟时间
     */
    private Long delay;

    /**
     * 通知结果
     */
    private Short result;

    /**
     * 通知类型
     */
    private String type;

    private Date createTime;

    private Date updateTime;

    /**
     * 通知地址
     */
    private String url;

    /**
     * 请求数据
     */
    private String data;

    private static final long serialVersionUID = 1L;

    /**
     * @return msg_id
     */
    public String getMsgId() {
        return msgId;
    }

    /**
     * @param msgId
     */
    public void setMsgId(String msgId) {
        this.msgId = msgId == null ? null : msgId.trim();
    }

    /**
     * 获取重试次数
     *
     * @return retry_nums - 重试次数
     */
    public Integer getRetryNums() {
        return retryNums;
    }

    /**
     * 设置重试次数
     *
     * @param retryNums 重试次数
     */
    public void setRetryNums(Integer retryNums) {
        this.retryNums = retryNums;
    }

    /**
     * 获取通知总次数
     *
     * @return total_nums - 通知总次数
     */
    public Integer getTotalNums() {
        return totalNums;
    }

    /**
     * 设置通知总次数
     *
     * @param totalNums 通知总次数
     */
    public void setTotalNums(Integer totalNums) {
        this.totalNums = totalNums;
    }

    /**
     * 获取延迟时间
     *
     * @return delay - 延迟时间
     */
    public Long getDelay() {
        return delay;
    }

    /**
     * 设置延迟时间
     *
     * @param delay 延迟时间
     */
    public void setDelay(Long delay) {
        this.delay = delay;
    }

    /**
     * 获取通知结果
     *
     * @return result - 通知结果
     */
    public Short getResult() {
        return result;
    }

    /**
     * 设置通知结果
     *
     * @param result 通知结果
     */
    public void setResult(Short result) {
        this.result = result;
    }

    /**
     * 获取通知类型
     *
     * @return type - 通知类型
     */
    public String getType() {
        return type;
    }

    /**
     * 设置通知类型
     *
     * @param type 通知类型
     */
    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    /**
     * @return create_time
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * @return update_time
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * @param updateTime
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 获取通知地址
     *
     * @return url - 通知地址
     */
    public String getUrl() {
        return url;
    }

    /**
     * 设置通知地址
     *
     * @param url 通知地址
     */
    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    /**
     * 获取请求数据
     *
     * @return data - 请求数据
     */
    public String getData() {
        return data;
    }

    /**
     * 设置请求数据
     *
     * @param data 请求数据
     */
    public void setData(String data) {
        this.data = data == null ? null : data.trim();
    }
}

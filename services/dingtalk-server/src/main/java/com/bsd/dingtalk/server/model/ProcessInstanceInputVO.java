package com.bsd.dingtalk.server.model;

import com.alibaba.fastjson.JSON;
import com.dingtalk.api.request.OapiProcessinstanceCreateRequest;
import com.dingtalk.api.request.OapiProcessinstanceCreateRequest.FormComponentValueVo;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author liujianhong
 * @date 2019-07-01
 * 审批实例
 */
public class ProcessInstanceInputVO {
    /**
     * 审批流的唯一码
     */
    private String processCode;

    /**
     * 审批实例发起人的userid
     */
    private String originatorUserId;

    /**
     * 发起人所在的部门id
     */
    private Long deptId;

    /**
     * 审批人userid列表
     */
    private String approvers;

    /**
     * 审批人列表
     */
    private OapiProcessinstanceCreateRequest.ProcessInstanceApproverVo[] approversV2;

    /**
     * 审批人userid列表
     */
    private String[] userIds;

    /**
     * 审批类型，会签：AND；或签：OR；单人：NONE
     */
    private String taskActionType;

    /**
     * 抄送人userid列表
     */
    private String ccList;

    /**
     * 抄送时间，分为（START, FINISH, START_FINISH）
     */
    private String ccPosition;

    /**
     * 单行输入框、多行输入框的表单数据
     */
    private List<TextForm> textForms;

    /**
     * 图片表单数据
     */
    private List<PictureForm> pictureForms;

    /**
     * 明细表单数据
     */
    private List<DetailForm> detailForms;

    public String getProcessCode() {
        return processCode;
    }

    public void setProcessCode(String processCode) {
        this.processCode = processCode;
    }

    public String getOriginatorUserId() {
        return originatorUserId;
    }

    public void setOriginatorUserId(String originatorUserId) {
        this.originatorUserId = originatorUserId;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public String getApprovers() {
        return approvers;
    }

    public void setApprovers(String approvers) {
        this.approvers = approvers;
    }

    public OapiProcessinstanceCreateRequest.ProcessInstanceApproverVo[] getApproversV2() {
        return approversV2;
    }

    public void setApproversV2(OapiProcessinstanceCreateRequest.ProcessInstanceApproverVo[] approversV2) {
        this.approversV2 = approversV2;
    }

    public String[] getUserIds() {
        return userIds;
    }

    public void setUserIds(String[] userIds) {
        this.userIds = userIds;
    }

    public String getTaskActionType() {
        return taskActionType;
    }

    public void setTaskActionType(String taskActionType) {
        this.taskActionType = taskActionType;
    }

    public String getCcList() {
        return ccList;
    }

    public void setCcList(String ccList) {
        this.ccList = ccList;
    }

    public String getCcPosition() {
        return ccPosition;
    }

    public void setCcPosition(String ccPosition) {
        this.ccPosition = ccPosition;
    }

    public List<TextForm> getTextForms() {
        return textForms;
    }

    public void setTextForms(List<TextForm> textForms) {
        this.textForms = textForms;
    }

    public List<PictureForm> getPictureForms() {
        return pictureForms;
    }

    public void setPictureForms(List<PictureForm> pictureForms) {
        this.pictureForms = pictureForms;
    }

    public List<DetailForm> getDetailForms() {
        return detailForms;
    }

    public void setDetailForms(List<DetailForm> detailForms) {
        this.detailForms = detailForms;
    }

    public static class TextForm {
        /**
         * 表单控件名称
         */
        private String name;

        /**
         * 表单值
         */
        private String value;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class PictureForm {
        /**
         * 表单控件名称
         */
        private String name;

        /**
         * 表单值，数组格式
         */
        private List<String> value;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getValue() {
            return value;
        }

        public void setValue(List<String> value) {
            this.value = value;
        }
    }

    public static class DetailForm {
        /**
         * 表单控件名称
         */
        private String name;

        /**
         * 明细里的文本控件列表
         */
        private List<TextForm> textForms;

        /**
         * 明细里的图片控件列表
         */
        private List<PictureForm> pictureForms;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<TextForm> getTextForms() {
            return textForms;
        }

        public void setTextForms(List<TextForm> textForms) {
            this.textForms = textForms;
        }

        public List<PictureForm> getPictureForms() {
            return pictureForms;
        }

        public void setPictureForms(List<PictureForm> pictureForms) {
            this.pictureForms = pictureForms;
        }
    }

    /**
     * 生成FormComponentValueVo，用于调用发起审批实例的接口
     *
     * @return
     */
    public List<FormComponentValueVo> generateForms() {
        List<FormComponentValueVo> result = new ArrayList<>();
        if (!CollectionUtils.isEmpty(textForms)) {
            for (TextForm textForm : textForms) {
                result.add(generateFormWithTextForm(textForm));
            }
        }

        if (!CollectionUtils.isEmpty(pictureForms)) {
            for (PictureForm pictureForm : pictureForms) {
                result.add(generateFormWithPictureForm(pictureForm));
            }
        }

        if (!CollectionUtils.isEmpty(detailForms)) {
            for (DetailForm detailForm : detailForms) {
                result.add(generateFormWithDetailForm(detailForm));
            }
        }
        return result;
    }

    private FormComponentValueVo generateFormWithTextForm(TextForm textForm) {
        FormComponentValueVo form = new FormComponentValueVo();
        form.setName(textForm.getName());
        form.setValue(textForm.getValue());
        return form;
    }

    private FormComponentValueVo generateFormWithPictureForm(PictureForm pictureForm) {
        FormComponentValueVo form = new FormComponentValueVo();
        form.setName(pictureForm.getName());
        form.setValue(JSON.toJSONString(pictureForm.getValue()));
        return form;
    }

    private FormComponentValueVo generateFormWithDetailForm(DetailForm detailForm) {
        FormComponentValueVo form = new FormComponentValueVo();
        form.setName(detailForm.getName());

        List<FormComponentValueVo> forms = new ArrayList<>();
        if (!CollectionUtils.isEmpty(detailForm.getTextForms())) {
            for (TextForm textForm : detailForm.getTextForms()) {
                forms.add(generateFormWithTextForm(textForm));
            }
        }

        if (!CollectionUtils.isEmpty(detailForm.pictureForms)) {
            for (PictureForm pictureForm : detailForm.pictureForms) {
                forms.add(generateFormWithPictureForm(pictureForm));
            }
        }

        form.setValue(JSON.toJSONString(Arrays.asList(forms)));

        return form;
    }
}

package com.bsd.payment.server.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.bsd.payment.server.enumm.ChannelEnum;
import com.bsd.payment.server.model.dto.PayChannelDto;
import com.bsd.payment.server.model.entity.PayChannel;
import com.bsd.payment.server.service.IPayChannelService;
import com.bsd.payment.server.util.MyLog;
import com.bsd.payment.server.util.ObjectValidUtil;
import com.opencloud.common.model.PageParams;
import com.opencloud.common.model.ResultBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author liujianhong
 */
@Api(tags = "支付渠道")
@RestController
@RequestMapping("/channel")
public class PayChannelController {
    private final static MyLog _log = MyLog.getLog(PayChannelController.class);

    @Autowired
    private IPayChannelService payChannelService;

    /**
     * 渠道列表
     *
     * @return ResultBody
     */
    @ApiOperation(value = "渠道列表", notes = "点击支付渠道进入列表页面")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "channelCode", value = "渠道编码", paramType = "form"),
            @ApiImplicitParam(name = "channelName", value = "渠道名称,如:alipay,wechat", paramType = "form"),
            @ApiImplicitParam(name = "channelMchId", value = "渠道商户ID", paramType = "form"),
            @ApiImplicitParam(name = "mchId", value = "商户ID", paramType = "form"),
            @ApiImplicitParam(name = "state", value = "渠道状态,0-停止使用,1-使用中", paramType = "form"),
            @ApiImplicitParam(name = "createTimeStart", value = "创建时间开始", paramType = "form"),
            @ApiImplicitParam(name = "createTimeEnd", value = "创建时间截止", paramType = "form"),
            @ApiImplicitParam(name = "pageIndex", value = "页数", paramType = "form"),
            @ApiImplicitParam(name = "pageSize", value = "每页数量", paramType = "form")
    })
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBody<IPage<PayChannel>> list(@RequestParam(value = "channelCode", required = false) String channelCode,
                                              @RequestParam(value = "channelName", required = false) String channelName,
                                              @RequestParam(value = "channelMchId", required = false) String channelMchId,
                                              @RequestParam(value = "mchId", required = false) String mchId,
                                              @RequestParam(value = "state", required = false) String state,
                                              @RequestParam(value = "createTimeStart", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss") Date createTimeStart,
                                              @RequestParam(value = "createTimeEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss") Date createTimeEnd,
                                              @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
                                              @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        HashMap<String, Object> map = new HashMap<>();
        if (ObjectUtils.isNotEmpty(channelCode)) {
            map.put("channelCode", channelCode);
        }
        if (ObjectUtils.isNotEmpty(channelName)) {
            map.put("channelName", channelName);
        }
        if (ObjectUtils.isNotEmpty(mchId)) {
            map.put("mchId", mchId);
        }
        if (ObjectUtils.isNotEmpty(channelMchId)) {
            map.put("channelMchId", channelMchId);
        }
        if (ObjectUtils.isNotEmpty(state)) {
            map.put("state", state);
        }
        if (ObjectUtils.isNotEmpty(createTimeStart)) {
            map.put("createTimeStart", createTimeStart);
        }
        if (ObjectUtils.isNotEmpty(createTimeEnd)) {
            map.put("createTimeEnd", createTimeEnd);
        }
        map.put("page", pageIndex);
        map.put("limit", pageSize);

        return ResultBody.ok().data(payChannelService.findListPage(new PageParams(map)));
    }

    @ApiOperation(value = "新增渠道信息", notes = "点击新增按钮保存渠道信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mchId", value = "商户ID", required = true, paramType = "form"),
            @ApiImplicitParam(name = "channelMchId", value = "渠道商户ID", required = true, paramType = "form"),
            @ApiImplicitParam(name = "channelCode", value = "渠道编码", required = true, paramType = "form"),
            @ApiImplicitParam(name = "state", value = "渠道状态,0-停止使用,1-使用中", required = true, paramType = "form"),
            @ApiImplicitParam(name = "param", value = "配置参数,json字符串", paramType = "form"),
            @ApiImplicitParam(name = "remark", value = "备注", paramType = "form")
    })
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResultBody<PayChannel> save(@RequestParam(value = "mchId") String mchId,
                                       @RequestParam(value = "channelMchId") String channelMchId,
                                       @RequestParam(value = "channelCode") String channelCode,
                                       @RequestParam(value = "state") Byte state,
                                       @RequestParam(value = "param", required = false) String param,
                                       @RequestParam(value = "remark", required = false) String remark
    ) {
        PayChannel payChannel = new PayChannel();
        payChannel.setMchId(mchId);
        payChannel.setChannelMchId(channelMchId);
        payChannel.setChannelCode(channelCode);
        payChannel.setChannelName(ChannelEnum.getTypeByCode(channelCode));
        payChannel.setState((byte) ("1".equalsIgnoreCase(state.toString()) ? 1 : 0));
        payChannel.setParam(param);
        payChannel.setRemark(remark);
        payChannel.setCreateTime(new Date());
        ObjectValidUtil.checkStrLength("channelCode", payChannel.getChannelCode(), 24);
        int result = payChannelService.addPayChannel(payChannel);
        if (result > 0) {
            return ResultBody.ok().msg("保存成功！");
        } else {
            return ResultBody.failed().msg("保存失败！");
        }
    }

    @ApiOperation(value = "修改渠道信息", notes = "点击修改按钮修改渠道信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "channelId", value = "渠道ID", required = true, paramType = "form"),
            @ApiImplicitParam(name = "mchId", value = "商户ID", required = true, paramType = "form"),
            @ApiImplicitParam(name = "channelMchId", value = "渠道商户ID", required = true, paramType = "form"),
            @ApiImplicitParam(name = "channelCode", value = "渠道编码", required = true, paramType = "form"),
            @ApiImplicitParam(name = "state", value = "渠道状态,0-停止使用,1-使用中", required = true, paramType = "form"),
            @ApiImplicitParam(name = "param", value = "配置参数,json字符串", required = false, paramType = "form"),
            @ApiImplicitParam(name = "remark", value = "备注", paramType = "form")
    })
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResultBody<PayChannel> update(@RequestParam(value = "channelId") Integer channelId,
                                         @RequestParam(value = "mchId") String mchId,
                                         @RequestParam(value = "channelMchId") String channelMchId,
                                         @RequestParam(value = "channelCode") String channelCode,
                                         @RequestParam(value = "state") Byte state,
                                         @RequestParam(value = "param", required = false) String param,
                                         @RequestParam(value = "remark", required = false) String remark
    ) {
        PayChannel channel = payChannelService.selectByPayChannelId(channelId);
        if (channel == null) {
            return ResultBody.failed().msg("渠道信息不存在");
        }
        PayChannel payChannel = new PayChannel();
        payChannel.setChannelId(channelId.longValue());
        payChannel.setMchId(mchId);
        payChannel.setChannelMchId(channelMchId);
        payChannel.setChannelCode(channelCode);
        payChannel.setChannelName(ChannelEnum.getTypeByCode(channelCode));
        payChannel.setParam(param);
        payChannel.setState((byte) ("1".equalsIgnoreCase(state.toString()) ? 1 : 0));
        payChannel.setRemark(remark);
        payChannel.setUpdateTime(new Date());
        ObjectValidUtil.checkStrLength("channelCode", payChannel.getChannelCode(), 24);
        int result = payChannelService.updatePayChannel(payChannel);
        if (result > 0) {
            return ResultBody.ok().msg("修改成功！");
        } else {
            return ResultBody.failed().msg("修改失败！");
        }
    }

    @ApiOperation(value = "渠道信息详情", notes = "点击详情按钮进入渠道信息详情页面")
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public ResultBody<PayChannel> detail(@RequestParam String channelId) {
        PayChannel payChannel = payChannelService.findPayChannel(channelId);
        if (payChannel == null) {
            return ResultBody.failed().msg("未查找到ID为" + channelId + "的渠道信息");
        }
        return ResultBody.ok().data(payChannel);
    }

    @InitBinder
    public void initBinder(WebDataBinder binder, WebRequest request) {
        //转换日期 注意这里的转化要和传进来的字符串的格式一致
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // CustomDateEditor为自定义日期编辑器
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    @ApiOperation(value = "查询所有渠道", notes = "后台把code转为name发给前端,前台不需要传参数")
    @RequestMapping(value = "/codeToName", method = RequestMethod.GET)
    public ResultBody<PayChannel> codeToName() {
        List<PayChannelDto> channelNameList = new ArrayList<>();
        for (ChannelEnum channelEnum : ChannelEnum.values()) {
            PayChannelDto dto = new PayChannelDto();
            dto.setChannelCode(channelEnum.getCode());
            dto.setChannelName(channelEnum.getValue());
            channelNameList.add(dto);
        }
        return ResultBody.ok().data(channelNameList);
    }
}
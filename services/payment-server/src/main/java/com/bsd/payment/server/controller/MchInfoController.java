package com.bsd.payment.server.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.bsd.payment.server.model.entity.MchInfo;
import com.bsd.payment.server.service.IMchInfoService;
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
import java.util.Date;
import java.util.HashMap;

/**
 * @author liujianhong
 */
@Api(tags = "商户信息")
@RestController
@RequestMapping("/mch_info")
public class MchInfoController {
    private final static MyLog _log = MyLog.getLog(MchInfoController.class);

    @Autowired
    private IMchInfoService mchInfoService;

    /**
     * 分页获取商户列表
     *
     * @param mchId
     * @param name
     * @param type
     * @param state
     * @param createTimeStart
     * @param createTimeEnd
     * @param pageIndex
     * @param pageSize
     * @return ResultBody
     */
    @ApiOperation(value = "商户列表", notes = "点击商户信息进入列表页面")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mchId", value = "商户ID", paramType = "form"),
            @ApiImplicitParam(name = "name", value = "名称", paramType = "form"),
            @ApiImplicitParam(name = "type", value = "类型", paramType = "form"),
            @ApiImplicitParam(name = "state", value = "商户状态,0-停止使用,1-使用中", paramType = "form"),
            @ApiImplicitParam(name = "createTimeStart", value = "创建时间开始", paramType = "form"),
            @ApiImplicitParam(name = "createTimeEnd", value = "创建时间截止", paramType = "form"),
            @ApiImplicitParam(name = "pageIndex", value = "页数", paramType = "form"),
            @ApiImplicitParam(name = "pageSize", value = "每页数量", paramType = "form")
    })
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBody<IPage<MchInfo>> list(@RequestParam(value = "mchId", required = false) String mchId,
                                           @RequestParam(value = "name", required = false) String name,
                                           @RequestParam(value = "type", required = false) String type,
                                           @RequestParam(value = "state", required = false) String state,
                                           @RequestParam(value = "createTimeStart", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss") Date createTimeStart,
                                           @RequestParam(value = "createTimeEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss") Date createTimeEnd,
                                           @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
                                           @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        HashMap<String, Object> map = new HashMap<>();
        if (ObjectUtils.isNotEmpty(mchId)) {
            map.put("mchId", mchId);
        }
        if (ObjectUtils.isNotEmpty(name)) {
            map.put("name", name);
        }
        if (ObjectUtils.isNotEmpty(type)) {
            map.put("type", type);
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

        return ResultBody.ok().data(mchInfoService.findListPage(new PageParams(map)));
    }

    /**
     * 保存商户信息
     *
     * @param name  商户名称
     * @param type  商户类型
     * @param state 商户状态
     * @return
     */
    @ApiOperation(value = "新增商户信息", notes = "新增商户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "名称", required = true, paramType = "form"),
            @ApiImplicitParam(name = "type", value = "类型", required = true, paramType = "form"),
            @ApiImplicitParam(name = "state", value = "商户状态,0-停止使用,1-使用中", required = true, paramType = "form"),
    })
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResultBody<MchInfo> save(@RequestParam(value = "name") String name,
                                    @RequestParam(value = "type") String type,
                                    @RequestParam(value = "state") Byte state) {
        MchInfo mchInfo = new MchInfo();
        mchInfo.setName(name);
        mchInfo.setType(type);
        mchInfo.setState(state);
        mchInfo.setReqKey(ObjectValidUtil.getRandomString(62));
        mchInfo.setResKey(ObjectValidUtil.getRandomString(62));
        mchInfo.setCreateTime(new Date());
        ObjectValidUtil.checkStrLength("name", mchInfo.getName(), 30);
        int result = mchInfoService.addMchInfo(mchInfo);
        if (result > 0) {
            return ResultBody.ok().msg("保存成功");
        } else {
            return ResultBody.failed().msg("保存失败");
        }
    }

    /**
     * 保存/编辑商户信息
     *
     * @param mchId 商户ID
     * @param name  商户名称
     * @param type  商户类型
     * @param state 商户状态
     * @return
     */
    @ApiOperation(value = "修改商户信息", notes = "修改商户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mchId", value = "商户ID", required = true, paramType = "form"),
            @ApiImplicitParam(name = "name", value = "名称", required = true, paramType = "form"),
            @ApiImplicitParam(name = "type", value = "类型", required = true, paramType = "form"),
            @ApiImplicitParam(name = "state", value = "商户状态,0-停止使用,1-使用中", required = true, paramType = "form"),
    })
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResultBody<MchInfo> update(@RequestParam(value = "mchId") String mchId,
                                      @RequestParam(value = "name") String name,
                                      @RequestParam(value = "type") String type,
                                      @RequestParam(value = "state") Byte state) {
        MchInfo info = mchInfoService.findMchInfo(mchId);
        if (info == null) {
            return ResultBody.failed().msg("商户信息不存在");
        }
        MchInfo mchInfo = new MchInfo();
        mchInfo.setMchId(info.getMchId());
        mchInfo.setName(name);
        mchInfo.setType(type);
        mchInfo.setState(state);
        mchInfo.setUpdateTime(new Date());
        ObjectValidUtil.checkStrLength("name", mchInfo.getName(), 30);
        int result = mchInfoService.updateMchInfo(mchInfo);
        if (result > 0) {
            return ResultBody.ok().msg("修改成功");
        } else {
            return ResultBody.failed().msg("修改失败");
        }
    }

    /**
     * 查看商户详情
     *
     * @param mchId
     * @return
     */
    @ApiOperation(value = "查看商户详情", notes = "点击编辑按钮查看商户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mchId", value = "商户ID", required = true, paramType = "form"),
    })
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public ResultBody<MchInfo> detail(@RequestParam(value = "mchId") String mchId) {
        MchInfo mchinfo = mchInfoService.findMchInfo(mchId);
        if (mchinfo == null) {
            return ResultBody.failed().msg("未查找到ID为" + mchId + "的商户信息");
        }
        return ResultBody.ok().data(mchinfo);
    }

    @InitBinder
    public void initBinder(WebDataBinder binder, WebRequest request) {
        //转换日期 注意这里的转化要和传进来的字符串的格式一直 如2015-9-9 就应该为yyyy-MM-dd
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // CustomDateEditor为自定义日期编辑器
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }
}
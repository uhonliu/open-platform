package com.bsd.user.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bsd.user.server.constants.UserConstants;
import com.bsd.user.server.mapper.ConsigneeAddressMapper;
import com.bsd.user.server.model.ConsigneeAddressPo;
import com.bsd.user.server.model.entity.ConsigneeAddress;
import com.bsd.user.server.model.entity.User;
import com.bsd.user.server.service.ConsigneeAddressService;
import com.bsd.user.server.service.UserService;
import com.opencloud.common.exception.OpenAlertException;
import com.opencloud.common.mybatis.base.service.impl.BaseServiceImpl;
import com.opencloud.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 收货地址 服务实现类
 *
 * @author lisongmao
 * @date 2019-07-18
 */
@Service
public class ConsigneeAddressServiceImpl extends BaseServiceImpl<com.bsd.user.server.mapper.ConsigneeAddressMapper, ConsigneeAddress> implements ConsigneeAddressService {
    @Autowired
    private UserService userService;
    @Resource
    private ConsigneeAddressMapper consigneeAddressMapper;


    @Override
    public void saveOrUpdateConsigneeAddress(ConsigneeAddressPo po) {
        if (StringUtils.isEmpty(po.getLoginMobile()) || !Arrays.asList(UserConstants.CONSIGNEE_ADDRESS_IS_DEFAULT).contains(po.getIsDefault())) {
            throw new OpenAlertException("参数错误");
        }
        User user = userService.getUserInfoByMobile(po.getLoginMobile());
        if (user == null) {
            throw new OpenAlertException("用户不存在");
        }
        if (po.getId() != null) {
            ConsigneeAddress address = consigneeAddressMapper.selectById(po.getId());
            if (address == null || address.getUserId() == null || !address.getUserId().equals(user.getUserId())) {
                throw new OpenAlertException("修改的收件地址不存在");
            }
        }
        if (UserConstants.CONSIGNEE_ADDRESS_IS_DEFAULT1.equals(po.getIsDefault())) {
            //如果设置默认地址，先修改其他默认地址为非默认地址
            updateAddressIsDefault(user.getUserId(), UserConstants.CONSIGNEE_ADDRESS_IS_DEFAULT1, UserConstants.CONSIGNEE_ADDRESS_IS_DEFAULT0);
        }
        po.setUpdateTime(new Date());
        if (po.getId() == null) {
            //新增
            po.setUserId(user.getUserId());
            po.setCreateTime(new Date());
            consigneeAddressMapper.insert(po);
        } else {
            //修改
            consigneeAddressMapper.updateById(po);
        }

    }

    @Override
    public List<ConsigneeAddress> queryUserConsigneeAddress(ConsigneeAddressPo po) {
        User user = userService.getUserInfoByMobile(po.getLoginMobile());
        if (user == null) {
            throw new OpenAlertException("用户不存在");
        }
        return queryUserConsigneeAddressByUserId(user.getUserId());
    }

    @Override
    public List<ConsigneeAddress> queryUserConsigneeAddressByUserId(Long userId) {
        QueryWrapper<ConsigneeAddress> queryWrapper = new QueryWrapper<ConsigneeAddress>();
        queryWrapper.lambda().eq(ConsigneeAddress::getUserId, userId);
        List<ConsigneeAddress> list = consigneeAddressMapper.selectList(queryWrapper);
        return list;
    }

    @Override
    public void deleteUserConsigneeAddress(ConsigneeAddressPo po) {
        User user = userService.getUserInfoByMobile(po.getLoginMobile());
        if (user == null) {
            throw new OpenAlertException("用户不存在");
        }
        ConsigneeAddress address = consigneeAddressMapper.selectById(po.getId());
        if (address == null || address.getUserId() == null || !address.getUserId().equals(user.getUserId())) {
            throw new OpenAlertException("收件地址不存在");
        }
        consigneeAddressMapper.deleteById(address.getId());
    }

    @Override
    public void setDefaultAddress(ConsigneeAddressPo po) {
        User user = userService.getUserInfoByMobile(po.getLoginMobile());
        if (user == null) {
            throw new OpenAlertException("用户不存在");
        }
        ConsigneeAddress address = consigneeAddressMapper.selectById(po.getId());
        if (address == null || address.getUserId() == null || !address.getUserId().equals(user.getUserId())) {
            throw new OpenAlertException("收件地址不存在");
        }
        if (UserConstants.CONSIGNEE_ADDRESS_IS_DEFAULT0.equals(address.getIsDefault())) {
            //修改其他默认地址为非默认地址
            updateAddressIsDefault(user.getUserId(), UserConstants.CONSIGNEE_ADDRESS_IS_DEFAULT1, UserConstants.CONSIGNEE_ADDRESS_IS_DEFAULT0);
            //修改当前地址为默认地址
            address.setIsDefault(UserConstants.CONSIGNEE_ADDRESS_IS_DEFAULT1);
            address.setUpdateTime(new Date());
            consigneeAddressMapper.updateById(address);
        }
    }

    /**
     * 修改收货地址的默认状态
     *
     * @param userId              用户id
     * @param whereDefaultStatus  修改条件
     * @param updateDefaultStatus 收货地址是否为默认地址：0-否 1-是
     */
    private void updateAddressIsDefault(Long userId, Integer whereDefaultStatus, Integer updateDefaultStatus) {
        ConsigneeAddress entity = new ConsigneeAddress();
        entity.setIsDefault(updateDefaultStatus);
        QueryWrapper<ConsigneeAddress> queryWrapper = new QueryWrapper<ConsigneeAddress>();
        queryWrapper.lambda().eq(ConsigneeAddress::getUserId, userId)
                .eq(ConsigneeAddress::getIsDefault, whereDefaultStatus);
        consigneeAddressMapper.update(entity, queryWrapper);
    }
}

package com.bsd.user.server.service;

import com.bsd.user.server.model.ConsigneeAddressPo;
import com.bsd.user.server.model.entity.ConsigneeAddress;
import com.opencloud.common.mybatis.base.service.IBaseService;

import java.util.List;

/**
 * 收货地址 服务类
 *
 * @author lisongmao
 * @date 2019-07-18
 */
public interface ConsigneeAddressService extends IBaseService<ConsigneeAddress> {
    /**
     * 新增或修改收货地址
     *
     * @param po
     */
    void saveOrUpdateConsigneeAddress(ConsigneeAddressPo po);

    /**
     * 获取当前登录用户的收货地址列表
     *
     * @param po
     * @return
     */
    List<ConsigneeAddress> queryUserConsigneeAddress(ConsigneeAddressPo po);

    /**
     * 根据用户id获取用户收货地址列表
     *
     * @param userId
     * @return
     */
    List<ConsigneeAddress> queryUserConsigneeAddressByUserId(Long userId);

    /**
     * 删除用户收货地址
     *
     * @param po
     */
    void deleteUserConsigneeAddress(ConsigneeAddressPo po);

    /**
     * 设置默认的收货地址
     *
     * @param po
     */
    void setDefaultAddress(ConsigneeAddressPo po);
}

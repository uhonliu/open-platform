package com.opencloud.base.server.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.opencloud.base.client.model.entity.BaseApi;
import com.opencloud.common.model.PageParams;
import com.opencloud.common.mybatis.base.service.IBaseService;

import java.util.List;

/**
 * 接口资源管理
 *
 * @author liuyadu
 */
public interface BaseApiService extends IBaseService<BaseApi> {
    /**
     * 分页查询
     *
     * @param pageParams
     * @return
     */
    IPage<BaseApi> findListPage(PageParams pageParams);

    /**
     * 查询列表
     *
     * @return
     */
    List<BaseApi> findAllList(String serviceId);

    /**
     * 根据主键获取接口
     *
     * @param apiId
     * @return
     */
    BaseApi getApi(Long apiId);


    /**
     * 检查接口编码是否存在
     *
     * @param apiCode
     * @return
     */
    Boolean isExist(String apiCode);

    /**
     * 添加接口
     *
     * @param api
     * @return
     */
    void addApi(BaseApi api);

    /**
     * 修改接口
     *
     * @param api
     * @return
     */
    void updateApi(BaseApi api);

    /**
     * 查询接口
     *
     * @param apiCode
     * @return
     */
    BaseApi getApi(String apiCode);

    /**
     * 移除接口
     *
     * @param apiId
     * @return
     */
    void removeApi(Long apiId);


    /**
     * 获取数量
     *
     * @param queryWrapper
     * @return
     */
    int getCount(QueryWrapper<BaseApi> queryWrapper);
}

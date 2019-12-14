package com.bsd.org.server.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bsd.org.server.model.entity.Dingtalk;
import com.bsd.org.server.model.vo.DingtalkVO;
import com.opencloud.common.mybatis.base.service.IBaseService;

/**
 * 钉钉配置信息 服务类
 *
 * @author lrx
 * @date 2019-08-14
 */
public interface DingtalkService extends IBaseService<Dingtalk> {
    /**
     * 更新钉钉配置信息
     *
     * @param dingtalk
     * @return
     */
    Boolean updateDingtalk(Dingtalk dingtalk);

    /**
     * 添加钉钉配置信息
     *
     * @param dingtalk
     * @return
     */
    Boolean saveDingtalk(Dingtalk dingtalk);

    /**
     * 分页获取钉钉数据
     *
     * @param page
     * @return
     */
    IPage<DingtalkVO> pageByParam(DingtalkVO dingtalkVO, IPage<DingtalkVO> page);
}

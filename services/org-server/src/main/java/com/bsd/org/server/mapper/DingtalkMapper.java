package com.bsd.org.server.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.bsd.org.server.model.entity.Dingtalk;
import com.bsd.org.server.model.vo.DingtalkVO;
import com.opencloud.common.mybatis.base.mapper.SuperMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 钉钉配置信息 Mapper 接口
 *
 * @author lrx
 * @date 2019-08-14
 */
@Mapper
public interface DingtalkMapper extends SuperMapper<Dingtalk> {

    /**
     * 分页获取钉钉信息
     *
     * @param page
     * @param wrapper
     * @return
     */
    IPage<DingtalkVO> pageByParam(IPage<DingtalkVO> page, @Param(Constants.WRAPPER) Wrapper wrapper);

}

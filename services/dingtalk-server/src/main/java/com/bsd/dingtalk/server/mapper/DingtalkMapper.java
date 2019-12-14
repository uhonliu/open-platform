package com.bsd.dingtalk.server.mapper;

import com.bsd.dingtalk.server.model.entity.Dingtalk;
import com.opencloud.common.mybatis.base.mapper.SuperMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 钉钉配置信息 Mapper 接口
 *
 * @author liujianhong
 * @date 2019-07-01
 */
@Mapper
public interface DingtalkMapper extends SuperMapper<Dingtalk> {

}

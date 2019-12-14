package com.bsd.dingtalk.server.mapper;

import com.bsd.dingtalk.server.model.entity.Company;
import com.opencloud.common.mybatis.base.mapper.SuperMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 企业信息表 Mapper 接口
 *
 * @author liujianhong
 * @date 2019-07-01
 */
@Mapper
public interface CompanyMapper extends SuperMapper<Company> {

}

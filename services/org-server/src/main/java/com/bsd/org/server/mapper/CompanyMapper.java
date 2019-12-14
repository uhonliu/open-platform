package com.bsd.org.server.mapper;

import com.bsd.org.server.model.entity.Company;
import com.opencloud.common.mybatis.base.mapper.SuperMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 企业信息表 Mapper 接口
 *
 * @author lrx
 * @date 2019-08-14
 */
@Mapper
public interface CompanyMapper extends SuperMapper<Company> {

}

package com.bsd.dingtalk.server.service.impl;

import com.bsd.dingtalk.server.mapper.CompanyMapper;
import com.bsd.dingtalk.server.model.entity.Company;
import com.bsd.dingtalk.server.service.CompanyService;
import com.opencloud.common.mybatis.base.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 企业信息表 服务实现类
 *
 * @author liujianhong
 * @date 2019-07-01
 */
@Service
public class CompanyServiceImpl extends BaseServiceImpl<CompanyMapper, Company> implements CompanyService {

}

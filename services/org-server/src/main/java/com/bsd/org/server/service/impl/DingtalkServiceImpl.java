package com.bsd.org.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bsd.org.server.mapper.CompanyMapper;
import com.bsd.org.server.mapper.DingtalkMapper;
import com.bsd.org.server.model.entity.Company;
import com.bsd.org.server.model.entity.Dingtalk;
import com.bsd.org.server.model.vo.DingtalkVO;
import com.bsd.org.server.service.DingtalkService;
import com.opencloud.common.exception.OpenAlertException;
import com.opencloud.common.mybatis.base.service.impl.BaseServiceImpl;
import com.opencloud.common.utils.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 钉钉配置信息 服务实现类
 *
 * @author lrx
 * @date 2019-08-14
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DingtalkServiceImpl extends BaseServiceImpl<DingtalkMapper, Dingtalk> implements DingtalkService {
    @Resource
    private DingtalkMapper dingtalkMapper;

    @Resource
    private CompanyMapper companyMapper;

    @Override
    public Boolean updateDingtalk(Dingtalk dingtalk) {
        if (!checkDingtalk(dingtalk)) {
            return false;
        }

        Dingtalk dbDingtalk = dingtalkMapper.selectById(dingtalk.getCompanyId());
        if (dbDingtalk == null) {
            throw new OpenAlertException("钉钉配置信息不存在");
        }

        int count = dingtalkMapper.updateById(dingtalk);
        if (count <= 0) {
            throw new OpenAlertException("更新钉钉配置信息失败");
        }
        return true;
    }


    @Override
    public Boolean saveDingtalk(Dingtalk dingtalk) {
        if (!checkDingtalk(dingtalk)) {
            return false;
        }

        Dingtalk dbDingtalk = dingtalkMapper.selectById(dingtalk.getCompanyId());
        if (dbDingtalk != null) {
            throw new OpenAlertException("该公司已经存在钉钉配置信息");
        }

        int count = dingtalkMapper.insert(dingtalk);
        if (count <= 0) {
            throw new OpenAlertException("保存钉钉配置信息失败");
        }
        return true;
    }

    /**
     * 分页获取钉钉配置
     *
     * @param dingtalkVO
     * @param page
     * @return
     */
    @Override
    public IPage<DingtalkVO> pageByParam(DingtalkVO dingtalkVO, IPage<DingtalkVO> page) {
        Wrapper wrapper = creatWrapperByDingtalkVO(dingtalkVO);
        return dingtalkMapper.pageByParam(page, wrapper);
    }

    /**
     * 根据dingtalkVO创建查询条件
     *
     * @param dingtalkVO
     * @return
     */
    private Wrapper creatWrapperByDingtalkVO(DingtalkVO dingtalkVO) {
        QueryWrapper<DingtalkVO> queryWrapper = Wrappers.query();
        if (dingtalkVO == null) {
            return queryWrapper;
        }
        //条件拼接
        queryWrapper.eq(dingtalkVO.getCompanyId() != null, "A.company_id", dingtalkVO.getCompanyId());
        queryWrapper.eq(StringUtils.isNotEmpty(dingtalkVO.getCompanyName()), "B.company_name", dingtalkVO.getCompanyName());
        queryWrapper.eq(StringUtils.isNotEmpty(dingtalkVO.getCorpId()), "A.corp_id", dingtalkVO.getCorpId());
        queryWrapper.eq(StringUtils.isNotEmpty(dingtalkVO.getAgentdId()), "A.agentd_id", dingtalkVO.getAgentdId());
        queryWrapper.eq(StringUtils.isNotEmpty(dingtalkVO.getAppKey()), "A.app_key", dingtalkVO.getAppKey());
        queryWrapper.eq(StringUtils.isNotEmpty(dingtalkVO.getAppSecret()), "A.app_secret", dingtalkVO.getAppSecret());
        queryWrapper.eq(StringUtils.isNotEmpty(dingtalkVO.getEncodingAesKey()), "A.encoding_aes_key", dingtalkVO.getEncodingAesKey());
        queryWrapper.eq(StringUtils.isNotEmpty(dingtalkVO.getToken()), "A.token", dingtalkVO.getToken());
        return queryWrapper;
    }

    /**
     * 检查参数
     *
     * @param dingtalk
     * @return
     */
    private Boolean checkDingtalk(Dingtalk dingtalk) {
        Long companyId = dingtalk.getCompanyId();
        Company company = companyMapper.selectById(companyId);
        if (company == null) {
            throw new OpenAlertException("公司信息不存在");
        }
        return true;
    }
}

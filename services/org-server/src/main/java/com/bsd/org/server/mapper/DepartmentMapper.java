package com.bsd.org.server.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.bsd.org.server.model.entity.Department;
import com.bsd.org.server.model.vo.DepartmentVO;
import com.opencloud.common.mybatis.base.mapper.SuperMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 部门信息表 Mapper 接口
 *
 * @author lrx
 * @date 2019-08-14
 */
@Mapper
public interface DepartmentMapper extends SuperMapper<Department> {
    /**
     * 分页获取部门信息
     *
     * @param page
     * @param wrapper
     * @return
     */
    IPage<DepartmentVO> pageByParam(IPage<DepartmentVO> page, @Param(Constants.WRAPPER) Wrapper wrapper);

    /**
     * 获取部门列表
     *
     * @param wrapper
     * @return
     */
    List<DepartmentVO> listByParam(@Param(Constants.WRAPPER) Wrapper wrapper);
}

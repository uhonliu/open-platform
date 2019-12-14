package com.bsd.org.server.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.bsd.org.server.model.entity.Position;
import com.bsd.org.server.model.vo.PositionVO;
import com.opencloud.common.mybatis.base.mapper.SuperMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 岗位体系表 Mapper 接口
 *
 * @author lrx
 * @date 2019-08-14
 */
@Mapper
public interface PositionMapper extends SuperMapper<Position> {

    /**
     * 分页获取岗位信息
     *
     * @param page
     * @param wrapper
     * @return
     */
    IPage<PositionVO> pageByParam(IPage<PositionVO> page, @Param(Constants.WRAPPER) Wrapper wrapper);

    /**
     * 获取岗位列表
     *
     * @param wrapper
     * @return
     */
    List<PositionVO> listByParam(@Param(Constants.WRAPPER) Wrapper wrapper);
}

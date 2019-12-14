package com.bsd.org.server.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bsd.org.server.model.entity.Position;
import com.bsd.org.server.model.vo.PositionVO;
import com.opencloud.common.mybatis.base.service.IBaseService;

import java.util.List;

/**
 * 岗位体系表 服务类
 *
 * @author lrx
 * @date 2019-08-14
 */
public interface PositionService extends IBaseService<Position> {
    /**
     * 新增岗位信息
     *
     * @param position
     * @return
     */
    Boolean savePosition(Position position);

    /**
     * 更新岗位信息
     *
     * @param position
     * @return
     */
    Boolean updatePosition(Position position);


    /**
     * 启用/禁用 岗位信息
     *
     * @param id
     * @param status
     * @return
     */
    Boolean status(Long id, Boolean status);


    /**
     * 根据部门ID跟岗位状态获取岗位信息
     *
     * @param departmentId
     * @param status
     * @return
     */
    List<PositionVO> findByDepartmentIdAndStatus(Long departmentId, Boolean status);

    /**
     * 根据岗位代码查找岗位信息
     *
     * @param positionCode
     * @return
     */
    Position findByPositionCode(String positionCode);

    /**
     * 根据岗位名称查找岗位信息
     *
     * @param positionName
     * @return
     */
    Position findByPositionName(String positionName);

    /**
     * 分页查询
     *
     * @param page
     * @param positionVO
     * @return
     */
    IPage<PositionVO> page(IPage<PositionVO> page, PositionVO positionVO);

    /**
     * 获取所有岗位信息
     *
     * @return
     */
    List<PositionVO> listByParam(PositionVO positionVO);

    /**
     * 根据部门ID列表跟岗位状态获取岗位信息
     *
     * @param departmentIds
     * @return
     */
    List<PositionVO> findByDepartmentIds(List<String> departmentIds);
}

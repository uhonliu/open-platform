package com.bsd.org.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bsd.org.server.mapper.DepartmentMapper;
import com.bsd.org.server.mapper.PositionMapper;
import com.bsd.org.server.model.entity.Department;
import com.bsd.org.server.model.entity.Position;
import com.bsd.org.server.model.vo.PositionVO;
import com.bsd.org.server.service.PositionService;
import com.opencloud.common.exception.OpenAlertException;
import com.opencloud.common.mybatis.base.service.impl.BaseServiceImpl;
import com.opencloud.common.utils.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 岗位体系表 服务实现类
 *
 * @author lrx
 * @date 2019-08-14
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PositionServiceImpl extends BaseServiceImpl<PositionMapper, Position> implements PositionService {
    @Resource
    private PositionMapper positionMapper;

    @Resource
    private DepartmentMapper departmentMapper;

    /**
     * 新增岗位信息
     *
     * @param position
     * @return
     */
    @Override
    public Boolean savePosition(Position position) {
        //检查参数
        if (!checkPosition(position)) {
            return false;
        }

        //保存数据
        int effectCount = positionMapper.insert(position);
        if (effectCount <= 0) {
            throw new OpenAlertException("保存岗位信息失败");
        }

        return true;
    }


    /**
     * 更新岗位信息
     *
     * @param position
     * @return
     */
    @Override
    public Boolean updatePosition(Position position) {
        //检查参数
        if (!checkPosition(position)) {
            return false;
        }

        int effectCount = positionMapper.updateById(position);
        if (effectCount <= 0) {
            throw new OpenAlertException("更新岗位信息失败");
        }

        return true;
    }


    /**
     * 根据岗位代码查询岗位信息
     *
     * @param positionCode
     * @return
     */
    @Override
    public Position findByPositionCode(String positionCode) {
        return positionMapper.selectOne(Wrappers.<Position>lambdaQuery().eq(Position::getPositionCode, positionCode));
    }

    /**
     * 启用/禁用 岗位信息
     *
     * @param id
     * @param status
     * @return
     */
    @Override
    public Boolean status(Long id, Boolean status) {
        Position position = positionMapper.selectById(id);
        if (position == null) {
            throw new OpenAlertException("岗位信息存在");
        }

        position.setStatus(status);
        int effectCount = positionMapper.updateById(position);
        if (effectCount <= 0) {
            throw new OpenAlertException("修改岗位状态失败");
        }

        return true;
    }

    /**
     * 根据部门ID跟岗位状态查找部门信息
     *
     * @param departmentId
     * @param status
     * @return
     */
    @Override
    public List<PositionVO> findByDepartmentIdAndStatus(Long departmentId, Boolean status) {
        PositionVO positionVO = new PositionVO();
        positionVO.setStatus(status);
        positionVO.setDepartmentId(departmentId);
        return listByParam(positionVO);
    }

    /**
     * 根据岗位名称查询岗位信息
     *
     * @param positionName
     * @return
     */
    @Override
    public Position findByPositionName(String positionName) {
        return positionMapper.selectOne(Wrappers.<Position>lambdaQuery().eq(Position::getPositionName, positionName));
    }

    /**
     * 分页获取岗位信息
     *
     * @param page
     * @param positionVO
     * @return
     */
    @Override
    public IPage<PositionVO> page(IPage<PositionVO> page, PositionVO positionVO) {
        Wrapper wrapper = creatWrapperByPositionVO(positionVO);
        return positionMapper.pageByParam(page, wrapper);
    }

    /**
     * 获取所有岗位信息
     *
     * @return
     */
    @Override
    public List<PositionVO> listByParam(PositionVO positionVO) {
        return positionMapper.listByParam(creatWrapperByPositionVO(positionVO));
    }

    @Override
    public List<PositionVO> findByDepartmentIds(List<String> departmentIds) {
        return positionMapper.listByParam(Wrappers.query().in("A.department_id", departmentIds));
    }

    /**
     * 根据PositionVO对象创建Wrapper查询条件
     *
     * @param positionVO
     * @return
     */
    private Wrapper creatWrapperByPositionVO(PositionVO positionVO) {
        QueryWrapper<PositionVO> queryWrapper = Wrappers.query();
        if (positionVO == null) {
            return queryWrapper;
        }
        //条件拼接
        queryWrapper.eq(positionVO.getPositionId() != null, "A.position_id", positionVO.getPositionId());
        queryWrapper.eq(StringUtils.isNotEmpty(positionVO.getPositionCode()), "A.position_code", positionVO.getPositionCode());
        queryWrapper.eq(StringUtils.isNotEmpty(positionVO.getPositionName()), "A.position_name", positionVO.getPositionName());
        queryWrapper.eq(positionVO.getStatus() != null, "A.status", positionVO.getStatus());
        queryWrapper.eq(positionVO.getDepartmentId() != null, "A.department_id", positionVO.getDepartmentId());
        queryWrapper.eq(StringUtils.isNotEmpty(positionVO.getDepartmentName()), "B.department_name", positionVO.getDepartmentName());
        queryWrapper.eq(positionVO.getCompanyId() != null, "C.company_id", positionVO.getCompanyId());
        return queryWrapper;
    }


    /**
     * 检查参数
     *
     * @param position
     * @return
     */
    private Boolean checkPosition(Position position) {
        Position dbPosition = null;
        Long positionId = position.getPositionId();

        //岗位ID不为空的情况下,查询岗位信息是否存在
        if (positionId != null) {
            dbPosition = positionMapper.selectById(positionId);
            if (dbPosition == null) {
                throw new OpenAlertException("部岗位信息不存在");
            }
        }
        //判断部门是否存在
        Department department = departmentMapper.selectById(position.getDepartmentId());
        if (department == null) {
            throw new OpenAlertException("部门信息不存在");
        }

        //判断岗位代码是否已经存在
        dbPosition = this.findByPositionCode(position.getPositionCode());
        if (dbPosition != null) {
            if (positionId == null || (positionId.longValue() != dbPosition.getPositionId().longValue())) {
                throw new OpenAlertException("岗位代码已经存在");
            }
        }

        //判断岗位名称是否已经存在
        dbPosition = this.findByPositionName(position.getPositionName());
        if (dbPosition != null) {
            if (positionId == null || (positionId.longValue() != dbPosition.getPositionId().longValue())) {
                throw new OpenAlertException("岗位名称已经存在");
            }
        }

        return true;
    }
}

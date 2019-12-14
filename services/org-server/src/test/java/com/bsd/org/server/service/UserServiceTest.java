package com.bsd.org.server.service;

import com.bsd.org.server.model.vo.UserDetailVO;
import com.opencloud.common.test.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class UserServiceTest extends BaseTest {
    @Autowired
    private UserService userService;

    @Test
    public void userDetailList() {
        UserDetailVO userDetailVO = new UserDetailVO();
        userDetailVO.setDepartmentId(111610413L);
        List<UserDetailVO> list = userService.userDetailList(userDetailVO);
        System.out.println(list);
    }


    @Test
    public void userIdList() {
        UserDetailVO userDetailVO = new UserDetailVO();
        userDetailVO.setDepartmentId(111610413L);
        List<String> list = userService.userIdList(userDetailVO);
        System.out.println(list);
    }
}
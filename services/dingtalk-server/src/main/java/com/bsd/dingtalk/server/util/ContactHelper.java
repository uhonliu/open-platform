package com.bsd.dingtalk.server.util;

import com.bsd.dingtalk.server.constants.URLConstant;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.*;
import com.dingtalk.api.response.*;
import com.taobao.api.ApiException;

import java.util.List;

public class ContactHelper {
    //获取用户信息
    public static OapiUserGetResponse getUserInfo(String accessToken, String userId) {
        try {
            DingTalkClient client = new DefaultDingTalkClient(URLConstant.URL_USER_GET);
            OapiUserGetRequest request = new OapiUserGetRequest();
            request.setUserid(userId);
            request.setHttpMethod("GET");
            OapiUserGetResponse response = client.execute(request, accessToken);
            if (response.getErrcode() != 0) {
                return null;
            }
            return response;
        } catch (ApiException e) {
            e.printStackTrace();
            return null;
        }
    }

    //获取部门用户信息
    public static List<OapiUserListResponse.Userlist> getDepartmentUserList(String accessToken, long departmentId, long offset, long size) {
        try {
            DingTalkClient client = new DefaultDingTalkClient(URLConstant.URL_GET_DEPARTMENT_USER_DETAIL);
            OapiUserListRequest request = new OapiUserListRequest();
            request.setDepartmentId(departmentId);
            request.setOffset(offset);
            request.setSize(size);
            request.setHttpMethod("GET");
            OapiUserListResponse response = client.execute(request, accessToken);
            if (response.getErrcode() != 0) {
                return null;
            }
            return response.getUserlist();
        } catch (ApiException e) {
            e.printStackTrace();
            return null;
        }
    }

    //获取子部门ID列表
    public static List<Long> getDepartmentIdList(String accessToken, String parentId) {
        try {
            DingTalkClient client = new DefaultDingTalkClient(URLConstant.URL_GET_DEPARTMENT_ID_LIST);
            OapiDepartmentListIdsRequest request = new OapiDepartmentListIdsRequest();
            request.setId(parentId);
            request.setHttpMethod("GET");
            OapiDepartmentListIdsResponse response = client.execute(request, accessToken);
            if (response.getErrcode() != 0) {
                return null;
            }
            return response.getSubDeptIdList();
        } catch (ApiException e) {
            e.printStackTrace();
            return null;
        }
    }

    //获取部门列表
    public static List<OapiDepartmentListResponse.Department> getDepartmentList(String accessToken, String parentId) {
        try {
            DingTalkClient client = new DefaultDingTalkClient(URLConstant.URL_GET_DEPARTMENT_LIST);
            OapiDepartmentListRequest request = new OapiDepartmentListRequest();
            request.setId(parentId);
            request.setHttpMethod("GET");
            OapiDepartmentListResponse response = client.execute(request, accessToken);
            if (response.getErrcode() != 0) {
                return null;
            }
            return response.getDepartment();
        } catch (ApiException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 获取用户部门列表
     *
     * @param accessToken
     * @param userId
     * @return
     */
    public static String getUserDepts(String accessToken, String userId) {
        try {
            DingTalkClient client = new DefaultDingTalkClient(URLConstant.URL_GET_USER_DEPTS);
            OapiDepartmentListParentDeptsRequest request = new OapiDepartmentListParentDeptsRequest();
            request.setUserId(userId);
            request.setHttpMethod("GET");
            OapiDepartmentListParentDeptsResponse response = client.execute(request, accessToken);
            if (response.getErrcode() != 0) {
                return null;
            }
            return response.getDepartment();
        } catch (ApiException e) {
            e.printStackTrace();
            return null;
        }
    }

    //获取部门详情
    public static OapiDepartmentGetResponse getDepartmentDetail(String accessToken, String departmentId) {
        try {
            DingTalkClient client = new DefaultDingTalkClient(URLConstant.URL_GET_DEPARTMENT_DETAIL);
            OapiDepartmentGetRequest request = new OapiDepartmentGetRequest();
            request.setId(departmentId);
            request.setHttpMethod("GET");
            OapiDepartmentGetResponse response = client.execute(request, accessToken);
            if (response.getErrcode() != 0) {
                return null;
            }
            return response;
        } catch (ApiException e) {
            e.printStackTrace();
            return null;
        }
    }

    //获取企业员工人数
    public static Long getOrgUserCount(String accessToken) {
        try {
            DingTalkClient client = new DefaultDingTalkClient(URLConstant.URL_GET_ORG_USER_COUNT);
            OapiUserGetOrgUserCountRequest request = new OapiUserGetOrgUserCountRequest();
            request.setOnlyActive(1L);
            request.setHttpMethod("GET");
            OapiUserGetOrgUserCountResponse response = client.execute(request, accessToken);
            if (response.getErrcode() != 0) {
                return null;
            }
            return response.getCount();
        } catch (ApiException e) {
            e.printStackTrace();
            return null;
        }
    }

    //获取企业角色列表
    public static List<OapiRoleListResponse.OpenRoleGroup> getRoleList(String accessToken) {
        try {
            DingTalkClient client = new DefaultDingTalkClient(URLConstant.URL_GET_ROLE_LIST);
            OapiRoleListRequest request = new OapiRoleListRequest();
            request.setOffset(0L);
            request.setSize(10L);
            OapiRoleListResponse response = client.execute(request, accessToken);
            if (response.getErrcode() != 0) {
                return null;
            }
            return response.getResult().getList();
        } catch (ApiException e) {
            e.printStackTrace();
            return null;
        }
    }

    //获取企业角色下的员工列表
    public static List<OapiRoleSimplelistResponse.OpenEmpSimple> getRoleSimpleList(String accessToken, long roleId) {
        try {
            DingTalkClient client = new DefaultDingTalkClient(URLConstant.URL_GET_ROLE_SIMPLE_LIST);
            OapiRoleSimplelistRequest request = new OapiRoleSimplelistRequest();
            request.setRoleId(roleId);
            request.setOffset(0L);
            request.setSize(10L);
            OapiRoleSimplelistResponse response = client.execute(request, accessToken);
            if (response.getErrcode() != 0) {
                return null;
            }
            return response.getResult().getList();
        } catch (ApiException e) {
            e.printStackTrace();
            return null;
        }
    }

    //获取外部联系人标签列表
    //企业使用此接口可获取企业外部联系人标签列表，例如这个外部联系人是公司的客户，那么标签可能就是客户。
    public static List<OapiExtcontactListlabelgroupsResponse.OpenLabelGroup> getExtcontactListlabelgroups(String accessToken) {
        try {
            DingTalkClient client = new DefaultDingTalkClient(URLConstant.URL_GET_EXTCONTACT_LIST_LABEL_GRUOPS);
            OapiExtcontactListlabelgroupsRequest request = new OapiExtcontactListlabelgroupsRequest();
            request.setOffset(0L);
            request.setSize(20L);
            OapiExtcontactListlabelgroupsResponse response = client.execute(request, accessToken);
            if (response.getErrcode() != 0) {
                return null;
            }
            return response.getResults();
        } catch (ApiException e) {
            e.printStackTrace();
            return null;
        }
    }

    //获取外部联系人列表
    public static List<OapiExtcontactListResponse.OpenExtContact> getExtcontactList(String accessToken) {
        try {
            DingTalkClient client = new DefaultDingTalkClient(URLConstant.URL_GET_EXTCONTACT_LIST);
            OapiExtcontactListRequest request = new OapiExtcontactListRequest();
            request.setOffset(0L);
            request.setSize(20L);
            OapiExtcontactListResponse response = client.execute(request, accessToken);
            if (response.getErrcode() != 0) {
                return null;
            }
            return response.getResults();
        } catch (ApiException e) {
            e.printStackTrace();
            return null;
        }
    }

    //获取外部联系人详情
    public static OapiExtcontactGetResponse getExtcontactUser(String accessToken, String userId) {
        try {
            DingTalkClient client = new DefaultDingTalkClient(URLConstant.URL_GET_EXTCONTACT_USER);
            OapiExtcontactGetRequest request = new OapiExtcontactGetRequest();
            request.setUserId(userId);
            OapiExtcontactGetResponse response = client.execute(request, accessToken);
            if (response.getErrcode() != 0) {
                return null;
            }
            return response;
        } catch (ApiException e) {
            e.printStackTrace();
            return null;
        }
    }
}

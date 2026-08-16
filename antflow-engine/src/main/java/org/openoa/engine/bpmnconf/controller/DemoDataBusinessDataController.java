package org.openoa.engine.bpmnconf.controller;

import org.openoa.base.dto.BusinessDataListPageReq;
import org.openoa.base.dto.DemoDataMgmtPageReq;
import org.openoa.base.entity.Result;
import org.openoa.base.vo.BusinessDataListVo;
import org.openoa.base.vo.ResultAndPage;
import org.openoa.engine.bpmnconf.service.biz.DemoDataBusinessDataBizServiceImpl;
import org.openoa.engine.vo.DemoDataDepartmentVo;
import org.openoa.engine.vo.DemoDataRoleUserVo;
import org.openoa.engine.vo.DemoDataRoleVo;
import org.openoa.engine.vo.DemoDataUserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 演示数据-业务数据 动态列表
 * <p>低代码流程业务数据以流程维度查看,分表兼容</p>
 */
@RestController
@RequestMapping("/demoData/businessData")
public class DemoDataBusinessDataController {

    @Autowired
    private DemoDataBusinessDataBizServiceImpl demoDataBusinessDataBizService;

    /**
     * 分页列表(columns + rows + total)
     */
    @PostMapping("/listPage")
    public Result<BusinessDataListVo> listPage(@RequestBody BusinessDataListPageReq req) {
        return Result.newSuccessResult(demoDataBusinessDataBizService.listPage(req));
    }

    /**
     * 流程详情查看权限校验
     *
     * @param processNumber 流程编号
     * @return 是否有权查看
     */
    @PostMapping("/checkPermission")
    public Result<Boolean> checkPermission(@RequestParam String processNumber) {
        return Result.newSuccessResult(demoDataBusinessDataBizService.checkPermission(processNumber));
    }

    /**
     * 人员管理分页列表(姓名/手机号模糊搜索)
     */
    @PostMapping("/userListPage")
    public ResultAndPage<DemoDataUserVo> userListPage(@RequestBody DemoDataMgmtPageReq req) {
        return demoDataBusinessDataBizService.userListPage(req);
    }

    /**
     * 部门管理分页列表(名称模糊搜索)
     */
    @PostMapping("/departmentListPage")
    public ResultAndPage<DemoDataDepartmentVo> departmentListPage(@RequestBody DemoDataMgmtPageReq req) {
        return demoDataBusinessDataBizService.departmentListPage(req);
    }

    /**
     * 角色管理分页列表(名称模糊搜索,含关联人数)
     */
    @PostMapping("/roleListPage")
    public ResultAndPage<DemoDataRoleVo> roleListPage(@RequestBody DemoDataMgmtPageReq req) {
        return demoDataBusinessDataBizService.roleListPage(req);
    }

    /**
     * 角色详情:角色下人员分页列表
     */
    @PostMapping("/roleUsers")
    public ResultAndPage<DemoDataRoleUserVo> roleUsers(@RequestBody DemoDataMgmtPageReq req) {
        return demoDataBusinessDataBizService.roleUsers(req);
    }
}

package org.openoa.engine.bpmnconf.controller;

import org.openoa.base.entity.Result;
import org.openoa.base.vo.BpmProcessDeptVo;
import org.openoa.engine.bpmnconf.service.impl.BpmProcessDeptServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmProcessNoticeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/taskMgmt")
@RestController
public class BpmProcessDeptController {
    @Autowired
    private BpmProcessDeptServiceImpl processDeptService;

    /**
     * 流程图标下面弄一个配置选项,保存流程权限(目前尚未实现),流程通知类型
     * @param vo
     * @return
     */
    @PostMapping("/taskMgmt")
    public Result saveProcessNotices(@RequestBody BpmProcessDeptVo vo){
        processDeptService.editProcessConf(vo);
        return Result.success();
    }
}
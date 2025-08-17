package org.openoa.engine.bpmnconf.controller;

import org.openoa.base.constant.enums.NodeFormAssigneePropertyEnum;
import org.openoa.base.entity.Result;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BaseNumIdStruVo;
import org.openoa.base.vo.BpmProcessDeptVo;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmProcessDeptBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: jwz
 * @Date: 2024/9/9 18:02
 * @Description:
 * @Version: 1.0.0
 */
@RequestMapping("/taskMgmt")
@RestController
public class BpmProcessControlController {

    @Autowired
    private BpmProcessDeptBizService processDeptBizService;

    /**
     * 流程图标下面弄一个配置选项,保存流程权限(目前尚未实现),流程通知类型
     *
     * @param vo
     * @return
     */
    @PostMapping("/taskMgmt")
    public Result saveProcessNotices(@RequestBody BpmProcessDeptVo vo) {
        processDeptBizService.editProcessConf(vo);
        return Result.success();
    }
    @GetMapping("/getFormRelatedOptions")
    public Result<List<BaseNumIdStruVo>> getFormRelatedOptions(){
        List<BaseNumIdStruVo> list=new ArrayList<>();
        for (NodeFormAssigneePropertyEnum value : NodeFormAssigneePropertyEnum.values()) {
            list.add(BaseNumIdStruVo.builder().id(value.getCode().longValue()).name(value.getDesc()).build());
        }
        return Result.newSuccessResult(list);
    }
}

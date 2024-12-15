package org.openoa.engine.bpmnconf.controller;

import org.apache.commons.lang3.StringUtils;
import org.openoa.base.entity.Result;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.vo.BaseKeyValueStruVo;
import org.openoa.engine.bpmnconf.confentity.BpmnConfLfFormdata;
import org.openoa.engine.bpmnconf.service.biz.LowCodeFlowBizService;
import org.openoa.engine.lowflow.service.BpmnConfLFFormDataBizServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lowcode")
public class LowCodeFlowController {
    @Autowired
    private BpmnConfLFFormDataBizServiceImpl lfFormDataBizService;
    @Autowired(required = false)
    private LowCodeFlowBizService lowCodeFlowBizService;

    /**
     * 低代码表单根据formcode查询对应的表单框架
     * @param formCode
     * @return
     */
    @GetMapping("/getformDataByFormCode")
    public Result<String> getLFFormDataByFormCode(String formCode){
        if(StringUtils.isEmpty(formCode)){
            throw new JiMuBizException("请传入formcode");
        }
        BpmnConfLfFormdata lfFormDataByFormCode = lfFormDataBizService.getLFFormDataByFormCode(formCode);
        return Result.newSuccessResult(lfFormDataByFormCode.getFormdata());
    }
    @GetMapping("/getLowCodeFlowFormCodes")
    public Result<List<BaseKeyValueStruVo>> getLowCodeFormCodes(){
        return Result.newSuccessResult(lowCodeFlowBizService.getLowCodeFlowFormCodes());
    }
    @PostMapping("/createLowCodeFormCode")
    public Result createLowCodeFormCode(@RequestBody BaseKeyValueStruVo vo){
        return Result.newSuccessResult(lowCodeFlowBizService.addFormCode(vo));
    }

}

package org.openoa.controller;

import org.openoa.base.entity.Result;
import org.openoa.base.interf.anno.IgnoreLog;
import org.openoa.entity.BizDemo;
import org.openoa.service.impl.BizDemoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@IgnoreLog
@RequestMapping("/bizdemo")
@RestController
public class BizDemoController {
    @Autowired
    private BizDemoServiceImpl demoService;
    @GetMapping("/getBizDemoByFlowKey")
    public Result getBizDemoByFlowKey(@RequestParam("flowkey") String flowkey){
        BizDemo info = demoService.getBizDemoByFlowKey(flowkey);
        return Result.newSuccessResult(info);
    }
    @PostMapping("/addBizDemo")
    public Result addBizDemo(@RequestBody BizDemo dto){
        String a= dto.getBizFormJson();
        String b= dto.getFlowKey();

        Boolean result = demoService.addBizDemo(dto);
        return Result.newSuccessResult(result);
    }
}

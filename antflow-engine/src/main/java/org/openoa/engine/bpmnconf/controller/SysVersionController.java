package org.openoa.engine.bpmnconf.controller;

import org.openoa.base.entity.CommonError;
import org.openoa.base.entity.Result;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.OperationResp;
import org.openoa.base.vo.ResultAndPage;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmProcessAppDataService;
import org.openoa.engine.bpmnconf.service.interf.repository.SysVersionService;
import org.openoa.engine.vo.AppVersionVo;
import org.openoa.engine.vo.SysVersionVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import static org.openoa.base.exception.BusinessErrorEnum.INSERT_FAIL;
import static org.openoa.base.vo.OperationResp.UPDATE_FAIL;

@RestController
@RequestMapping("/appVersion")
public class SysVersionController {
    @Autowired
    private SysVersionService sysVersionService;
    @Autowired
    private BpmProcessAppDataService bpmProcessAppDataService;

    @GetMapping("/appVersion")
    public Result appVersion(@RequestParam("application") String application, @RequestParam("appVersion") String appVersion){


        AppVersionVo appVersionVo = sysVersionService.getAppVersion(application, appVersion);
        if (appVersionVo!=null){
            return Result.newSuccessResult(appVersionVo);
        }else {
            return Result.newFailureResult("","未找到应用版本信息");
        }

    }
    @GetMapping("/getQrCode")
    public Result getCode() {
        return Result.newSuccessResult(sysVersionService.getDownloadQRcode());
    }

    @GetMapping("/versionList")
    public Result list(SysVersionVo vo) {
        ResultAndPage<SysVersionVo> sysVersionVoResultAndPage = sysVersionService.listSysVersion(vo);
        return Result.newSuccessResult(sysVersionVoResultAndPage);
    }

    @PostMapping(value = "/{id}")
    public Result updateById(@PathVariable("id") Long id ,@RequestBody SysVersionVo sysVersionVo) {

        if(id==null) {
            throw new AFBizException("id不能为空");
        }
        sysVersionVo.setId(id);
        sysVersionVo.setUpdateUser(SecurityUtils.getLogInEmpNameSafe());
        if (sysVersionService.edit(sysVersionVo)) {
            return Result.success();
        }

        throw new AFBizException(UPDATE_FAIL.getCode(), UPDATE_FAIL.getDesc());
    }

    /**
     * 保存系统版本配制表
     * @return
     */
    @PostMapping("/save")
    public Result save(@RequestBody SysVersionVo sysVersionVo) {

        sysVersionVo.setCreateUser(SecurityUtils.getLogInEmpNameSafe());
        sysVersionVo.setUpdateUser(SecurityUtils.getLogInEmpNameSafe());
        if (sysVersionService.edit(sysVersionVo)) {
            return Result.success();
        }

        throw new AFBizException(INSERT_FAIL.getCodeStr(), INSERT_FAIL.getMsg());
    }
}

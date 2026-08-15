package org.openoa.engine.bpmnconf.controller;

import org.openoa.base.entity.CommonError;
import org.openoa.base.entity.Result;
import org.openoa.base.constant.enums.AppApplicationType;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.OperationResp;
import org.openoa.base.vo.ResultAndPage;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmProcessAppApplicationService;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmProcessAppDataService;
import org.openoa.engine.bpmnconf.service.interf.repository.QuickEntryService;
import org.openoa.engine.bpmnconf.service.interf.repository.SysVersionService;
import org.openoa.engine.vo.AppDataSaveVo;
import org.openoa.engine.vo.AppVersionVo;
import org.openoa.engine.vo.QuickEntryVo;
import org.openoa.engine.vo.SysVersionVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.openoa.base.exception.BusinessErrorEnum.INSERT_FAIL;
import static org.openoa.base.vo.OperationResp.UPDATE_FAIL;

@RestController
@RequestMapping("/appVersion")
public class SysVersionController {
    @Autowired
    private SysVersionService sysVersionService;
    @Autowired
    private BpmProcessAppDataService bpmProcessAppDataService;
    @Autowired
    private BpmProcessAppApplicationService bpmProcessAppApplicationService;
    @Autowired
    private QuickEntryService quickEntryService;

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

    /**
     * candidate objects for version related data
     *
     * @param type      1:icon application 2:process data 3:quick entry
     * @param search    name keyword
     * @param limitSize max size,default 50
     */
    @GetMapping("/candidates")
    public Result candidates(@RequestParam("type") Integer type,
                             @RequestParam(value = "search", required = false) String search,
                             @RequestParam(value = "limitSize", required = false, defaultValue = "50") Integer limitSize) {
        if (AppApplicationType.APP_QUICK_ENTRY.getCode().equals(type)) {
            List<QuickEntryVo> entries = quickEntryService.searchQuickEntry(search, limitSize);
            if (entries == null) {
                entries = java.util.Collections.emptyList();
            }
            return Result.newSuccessResult(entries.stream()
                    .map(o -> BaseIdTranStruVo.builder()
                            .id(String.valueOf(o.getId()))
                            .name(o.getTitle())
                            .build())
                    .collect(Collectors.toList()));
        }
        return Result.newSuccessResult(bpmProcessAppApplicationService.listProcessAppApplication(search, limitSize));
    }

    /**
     * get a version's related data(ordered by sort)
     *
     * @param versionId version id
     * @param type      1:icon application 2:process data 3:quick entry
     */
    @GetMapping("/appDatas")
    public Result appDatas(@RequestParam("versionId") Long versionId, @RequestParam("type") Integer type) {
        List<org.openoa.base.entity.BpmProcessAppData> rows = bpmProcessAppDataService.getProcessAppData(versionId, 0, type);
        if (rows == null) {
            rows = java.util.Collections.emptyList();
        }
        List<AppDataSaveVo.AppDataItem> items = rows.stream()
                .sorted((a, b) -> Integer.compare(
                        a.getSort() == null ? Integer.MAX_VALUE : a.getSort(),
                        b.getSort() == null ? Integer.MAX_VALUE : b.getSort()))
                .map(o -> AppDataSaveVo.AppDataItem.builder()
                        .id(o.getApplicationId())
                        .name(o.getProcessName())
                        .sort(o.getSort())
                        .build())
                .collect(Collectors.toList());
        return Result.newSuccessResult(items);
    }

    /**
     * full replacement save of a version's related data,only draft version is allowed
     */
    @PostMapping("/saveAppDatas")
    public Result saveAppDatas(@RequestBody AppDataSaveVo vo) {
        if (sysVersionService.saveAppDatas(vo)) {
            return Result.success();
        }
        throw new AFBizException(INSERT_FAIL.getCodeStr(), INSERT_FAIL.getMsg());
    }

    /**
     * publish a draft version
     */
    @PostMapping("/publish/{id}")
    public Result publish(@PathVariable("id") Long id) {
        if (sysVersionService.publish(id)) {
            return Result.success();
        }
        throw new AFBizException(UPDATE_FAIL.getCode(), UPDATE_FAIL.getDesc());
    }

    /**
     * logically delete a draft version with its related data
     */
    @PostMapping("/delete/{id}")
    public Result deleteDraft(@PathVariable("id") Long id) {
        if (sysVersionService.deleteDraft(id)) {
            return Result.success();
        }
        throw new AFBizException(UPDATE_FAIL.getCode(), UPDATE_FAIL.getDesc());
    }
}

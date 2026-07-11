package org.openoa.engine.bpmnconf.controller;


import org.apache.commons.lang3.StringUtils;
import org.openoa.base.dto.PageDto;
import org.openoa.base.entity.Result;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.vo.BaseKeyValueStruVo;
import org.openoa.base.vo.BpmnConfVo;
import org.openoa.base.vo.DetailRequestDto;
import org.openoa.base.vo.LfStartFormVo;
import org.openoa.base.vo.ResultAndPage;
import org.openoa.base.vo.TaskMgmtVO;
import org.openoa.base.constant.enums.BpmnConfFlagsEnum;
import org.openoa.base.entity.BpmnConfLfFormdata;
import org.openoa.base.vo.LfFormManageVo;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmnConfBizService;
import org.openoa.engine.bpmnconf.service.interf.biz.LfFormManageBizService;
import org.openoa.engine.bpmnconf.service.interf.biz.LowCodeFlowBizService;
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
    @Autowired
    private LfFormManageBizService lfFormManageBizService;
    @Autowired
    private BpmnConfBizService bpmnConfBizService;

    /**
     * 获取全部 LF FormCodes 在流程设计时选择使用
     *
     * @return
     */
    @GetMapping("/getLowCodeFlowFormCodes")
    public Result<List<BaseKeyValueStruVo>> getLowCodeFormCodes() {
        return Result.newSuccessResult(lowCodeFlowBizService.getLowCodeFlowFormCodes());
    }

    /**
     * 获取LF FormCode Page List 模板列表使用
     *
     * @param requestDto
     * @return
     */
    @PostMapping("/getLFFormCodePageList")
    public ResultAndPage<BaseKeyValueStruVo> getLFFormCodePageList(@RequestBody DetailRequestDto requestDto) {
        PageDto pageDto = requestDto.getPageDto();
        TaskMgmtVO taskMgmtVO = requestDto.getTaskMgmtVO();
        return lowCodeFlowBizService.selectLFFormCodePageList(pageDto, taskMgmtVO);
    }

    /**
     * 获取 已设计流程并且启用的 LF FormCode Page List 发起页面使用
     *
     * @param requestDto
     * @return
     */
    @PostMapping("/getLFActiveFormCodePageList")
    public ResultAndPage<BaseKeyValueStruVo> getLFActiveFormCodePageList(@RequestBody DetailRequestDto requestDto) {
        PageDto pageDto = requestDto.getPageDto();
        TaskMgmtVO taskMgmtVO = requestDto.getTaskMgmtVO();
        return lowCodeFlowBizService.selectLFActiveFormCodePageList(pageDto, taskMgmtVO);
    }

    /**
     * 低代码表单根据formcode查询对应的表单框架
     *
     * @param formCode
     * @return
     */
    @GetMapping("/getformDataByFormCode")
    public Result<String> getLFFormDataByFormCode(String formCode) {
        if (StringUtils.isEmpty(formCode)) {
            throw new AFBizException("请传入formcode");
        }
        BpmnConfLfFormdata lfFormDataByFormCode = lfFormDataBizService.getLFFormDataByFormCode(formCode);
        return Result.newSuccessResult(lfFormDataByFormCode.getFormdata());
    }

    /**
     * 发起流程页: 根据 formCode 获取表单数据(兼容内联/外部表单模式)
     * 内联模式返回 useExternalForm=false + lfFormData
     * 外部模式返回 useExternalForm=true + lfFormdataList
     *
     * @param formCode 流程表单代码
     * @return LfStartFormVo
     */
    @GetMapping("/getStartFormData")
    public Result<LfStartFormVo> getStartFormData(String formCode) {
        if (StringUtils.isEmpty(formCode)) {
            throw new AFBizException("请传入formcode");
        }
        BpmnConfVo confVo = bpmnConfBizService.detailByFormCode(formCode);
        LfStartFormVo result = new LfStartFormVo();
        boolean useExternal = BpmnConfFlagsEnum.USE_EXTERNAL_FORM.flagsContainsCurrent(confVo.getExtraFlags());
        result.setUseExternalForm(useExternal);
        if (useExternal) {
            result.setLfFormdataList(confVo.getLfFormdataList());
        } else {
            result.setLfFormData(confVo.getLfFormData());
        }
        return Result.newSuccessResult(result);
    }

    @PostMapping("/createLowCodeFormCode")
    public Result createLowCodeFormCode(@RequestBody BaseKeyValueStruVo vo) {
        return Result.newSuccessResult(lowCodeFlowBizService.addFormCode(vo));
    }

    // ===================== 独立表单管理 =====================

    /**
     * 分页查询独立表单（家族分组，每族一行生效版本）
     */
    @PostMapping("/form/listPage")
    public ResultAndPage<LfFormManageVo> listFormPage(@RequestBody DetailRequestDto requestDto) {
        PageDto pageDto = requestDto.getPageDto();
        LfFormManageVo vo = new LfFormManageVo();
        if (requestDto.getTaskMgmtVO() != null) {
            vo.setSearch(requestDto.getTaskMgmtVO().getSearch());
        }
        return lfFormManageBizService.listPage(pageDto, vo);
    }

    /**
     * 按 id 查询表单版本（编辑回显 / 审批按 id 取 formdata）
     */
    @GetMapping("/form/{id}")
    public Result<LfFormManageVo> getFormById(@PathVariable Long id) {
        return Result.newSuccessResult(lfFormManageBizService.getById(id));
    }

    /**
     * 保存表单：无 formCode => 新建家族+首版本；有 formCode => 新建版本
     */
    @PostMapping("/form/save")
    public Result<Long> saveForm(@RequestBody LfFormManageVo vo) {
        return Result.newSuccessResult(lfFormManageBizService.save(vo));
    }

    /**
     * 软删除单个版本（被生效流程引用时拒绝）
     */
    @DeleteMapping("/form/{id}")
    public Result<Void> deleteForm(@PathVariable Long id) {
        lfFormManageBizService.delete(id);
        return Result.newSuccessResult(null);
    }

    /**
     * 查询某家族所有版本（历史版本查看）
     */
    @GetMapping("/form/history")
    public Result<List<LfFormManageVo>> listFormHistory(String formCode) {
        if (StringUtils.isEmpty(formCode)) {
            throw new AFBizException("请传入formCode");
        }
        return Result.newSuccessResult(lfFormManageBizService.listHistory(formCode));
    }

    /**
     * 列出所有生效独立表单（流程设计多选下拉框）
     */
    @GetMapping("/form/listEffectiveForSelect")
    public Result<List<LfFormManageVo>> listEffectiveForSelect() {
        return Result.newSuccessResult(lfFormManageBizService.listEffectiveForSelect());
    }

    /**
     * 生效指定表单版本（同族其他版本自动置为非生效）
     */
    @PutMapping("/form/effective/{id}")
    public Result effective(@PathVariable("id") Long id) {
        lfFormManageBizService.effective(id);
        return Result.success();
    }

}

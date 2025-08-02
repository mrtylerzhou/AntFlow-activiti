package org.openoa.engine.bpmnconf.controller;


import org.apache.commons.lang3.StringUtils;
import org.openoa.base.dto.PageDto;
import org.openoa.base.entity.Result;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.vo.BaseKeyValueStruVo;
import org.openoa.base.vo.DetailRequestDto;
import org.openoa.base.vo.ResultAndPage;
import org.openoa.base.vo.TaskMgmtVO;
import org.openoa.base.entity.BpmnConfLfFormdata;
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
            throw new JiMuBizException("请传入formcode");
        }
        BpmnConfLfFormdata lfFormDataByFormCode = lfFormDataBizService.getLFFormDataByFormCode(formCode);
        return Result.newSuccessResult(lfFormDataByFormCode.getFormdata());
    }

    @PostMapping("/createLowCodeFormCode")
    public Result createLowCodeFormCode(@RequestBody BaseKeyValueStruVo vo) {
        return Result.newSuccessResult(lowCodeFlowBizService.addFormCode(vo));
    }

}

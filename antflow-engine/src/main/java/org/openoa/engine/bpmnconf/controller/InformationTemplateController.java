package org.openoa.engine.bpmnconf.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.enums.ProcessNoticeEnum;
import org.openoa.base.constant.enums.WildcardCharacterEnum;
import org.openoa.base.dto.PageDto;
import org.openoa.base.entity.Result;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.*;
import org.openoa.engine.bpmnconf.confentity.BpmProcessNotice;
import org.openoa.engine.bpmnconf.confentity.InformationTemplate;
import org.openoa.engine.bpmnconf.constant.enus.EventTypeEnum;
import org.openoa.engine.bpmnconf.service.impl.BpmProcessNoticeServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmVariableApproveRemindServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.InformationTemplateServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/informationTemplates")
public class InformationTemplateController {
    @Resource
    private InformationTemplateServiceImpl informationTemplateService;

    @Resource
    private BpmVariableApproveRemindServiceImpl bpmVariableApproveRemindService;
    @Autowired
    private BpmProcessNoticeServiceImpl processNoticeService;

    /**
     * query information template vos;
     *
     * @return resp
     */
    @PostMapping("/listPage")
    public ResultAndPage list(InformationPgeRequestDto dto)  {
        PageDto pageDto = dto.getPageDto();
        InformationTemplateVo informationTemplateVo = dto.getEntity();
        if(informationTemplateVo==null){
            informationTemplateVo = new InformationTemplateVo();
        }
        return informationTemplateService.list(pageDto, informationTemplateVo);
    }

    /**
     * modify information template
     *
     * @return resp
     */
    @PostMapping("/updateById")
    public Result updateById(@RequestBody InformationTemplateVo informationTemplateVo) {
        informationTemplateService.edit(informationTemplateVo);
        return Result.success();
    }

    /**
     * save information template
     *
     * @return resp
     */
    @PostMapping("/save")
    public Result save(@RequestBody InformationTemplateVo informationTemplateVo) {
        informationTemplateService.edit(informationTemplateVo);
        return Result.success();
    }

    /**
     * delete information template
     *
     * @return resp
     */
    @PostMapping("/deleteById")
    public Result deleteById(@RequestParam Long id) {
        informationTemplateService.updateById(InformationTemplate
                .builder()
                .id(id)
                .updateUser(SecurityUtils.getLogInEmpNameSafe())
                .isDel(1)
                .build());
        return Result.success();
    }

    /**
     * notice template list
     *
     * @return resp
     */
    @GetMapping("/listByName")
    public Result list(@RequestParam(required = false) String name) {
        QueryWrapper<InformationTemplate> queryWrapper = new QueryWrapper<InformationTemplate>()
                .eq("is_del", 0)
                .eq("status", 0)
                .like(!StringUtils.isEmpty(name), "name", name);
        List<InformationTemplate> results = informationTemplateService.list(queryWrapper);
        return Result.newSuccessResult(results);
    }

    /**
     * get default template
     *
     * @return resp
     */
    @GetMapping("/defaultTemplates")
    public Result getList() {
        return Result.newSuccessResult(informationTemplateService.getList());
    }

    /**
     * set default template
     *
     * @return resp
     */
    @PostMapping("/setDefaultTemplates")
    public Result setList(@RequestBody DefaultTemplateVo[] vos) {
        informationTemplateService.setList(Arrays.asList(vos));
        return Result.success();
    }

    /**
     * wildcard list
     *
     * @param name name
     * @return resp
     */
    @GetMapping("/getWildcardCharacter")
    public Result getWildcardCharacter(@RequestParam(required = false) String name) {
        return Result.newSuccessResult(
                !StringUtils.isEmpty(name)
                        ? new ArrayList<>(Arrays.asList(WildcardCharacterEnum.values()))
                        .stream()
                        .filter(o -> o.getDesc().contains(name))
                        .map(o -> EnumerateVo
                                .builder()
                                .code(o.getCode())
                                .desc(o.getDesc())
                                .build())
                        .collect(Collectors.toList())
                        : new ArrayList<>(Arrays.asList(WildcardCharacterEnum.values()))
                        .stream()
                        .map(o -> EnumerateVo
                                .builder()
                                .code(o.getCode())
                                .desc(o.getDesc())
                                .build())
                        .collect(Collectors.toList())
        );
    }
    @GetMapping("/getProcessEvents")
    public Result getAllProcessEvents(){
        List<BaseNumIdStruVo> lists=new ArrayList<>();
        for (EventTypeEnum anEnum : EventTypeEnum.values()) {
            lists.add(BaseNumIdStruVo.builder().id(anEnum.getCode().longValue()).name(anEnum.getDesc()).build());
        }
        return Result.newSuccessResult(lists);
    }

    @GetMapping("/getAllNoticeTypes")
    public Result getAllNoticeTypes(){
        ProcessNoticeEnum[] processNoticeEnums = ProcessNoticeEnum.values();
        List<BaseNumIdStruVo> lists = new ArrayList<>();
        for (ProcessNoticeEnum processNoticeEnum : processNoticeEnums) {
            lists.add(BaseNumIdStruVo.builder().id(((long)processNoticeEnum.getCode())).name(processNoticeEnum.getDesc()).build());
        }
        return Result.newSuccessResult(lists);
    }

    @GetMapping("/getNoticeTypeByFormCode")
    public Result getNoticeTypeByFormCode(@RequestParam String formCode){
        if(StringUtils.isEmpty(formCode)){
            throw new JiMuBizException("请传入表单编码");
        }
        List<BpmProcessNotice> bpmProcessNotices = processNoticeService.processNoticeList(formCode);
        List<BaseNumIdStruVo> lists = new ArrayList<>();
        for (BpmProcessNotice bpmProcessNotice : bpmProcessNotices) {
            Integer type = bpmProcessNotice.getType();
            String descByCode = ProcessNoticeEnum.getDescByCode(type);
            lists.add(BaseNumIdStruVo.builder().id(Long.valueOf(type)).name(descByCode).build());
        }
        return Result.newSuccessResult(lists);
    }

    /**
     * test timeout remind
     */
    @GetMapping("/testDoTimeoutReminder")
    public void testDoTimeoutReminder() {
        bpmVariableApproveRemindService.doTimeoutReminder();
    }

}
package org.openoa.engine.bpmnconf.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.enums.NodePropertyEnum;
import org.openoa.base.dto.PageDto;
import org.openoa.base.entity.Result;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.interf.ActivitiService;
import org.openoa.base.interf.ActivitiServiceAnno;
import org.openoa.base.interf.FormOperationAdaptor;
import org.openoa.base.util.SpringBeanUtils;
import org.openoa.base.vo.*;
import org.openoa.engine.bpmnconf.adp.bpmnnodeadp.BpmnNodeAdaptor;
import org.openoa.engine.bpmnconf.confentity.BpmnNode;
import org.openoa.engine.bpmnconf.confentity.UserEntrust;
import org.openoa.engine.bpmnconf.mapper.BpmnNodeMapper;
import org.openoa.engine.bpmnconf.service.biz.LowCodeFlowBizService;
import org.openoa.engine.bpmnconf.service.impl.UserEntrustServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author TylerZhou
 * @Date 2024/6/30 6:43
 * @Version 1.0
 */
@Slf4j
@Tag(name="工作流业务管理",description = "")
@RestController
@RequestMapping(value = "/bpmnBusiness")
public class BpmnBusinessController {

    @Autowired(required = false)
    Map<String, FormOperationAdaptor> formOperationAdaptorMap;

    @Autowired
    private UserEntrustServiceImpl userEntrustService;
    @Autowired(required = false)
    private LowCodeFlowBizService lowCodeFlowBizService;
    @Autowired
    private BpmnNodeMapper bpmnNodeMapper;

    @Operation(summary ="" )
    @RequestMapping("/listFormCodes")
    public Result listFormCodes(String desc){
        List<String> formCodes = baseFormInfo(desc).stream().map(a -> a.getKey()).collect(Collectors.toList());
        return Result.newSuccessResult(formCodes);
    }
    /**
     * get processes's basic form code and desc information
     * @param desc
     * @return
     */
    @GetMapping("/listFormInfo")
    public Result listFormInfo(String desc){

        return Result.newSuccessResult(baseFormInfo(desc));
    }
    /**
     * get all form code and desc information
     * @return
     */
    @GetMapping("/allFormCodes")
    public Result allFormCodes(){
        return Result.newSuccessResult(allFormInfo());
    }
    @GetMapping("/listNodeProperties")
    public Result listPersonnelProperties(){
        Collection<BpmnNodeAdaptor> beans = SpringBeanUtils.getBeans(BpmnNodeAdaptor.class);
        List<PersonnelRuleVO> personnelRuleVOS=new ArrayList<>();
        for (BpmnNodeAdaptor bean : beans) {
            PersonnelRuleVO personnelRuleVO = bean.formaFieldAttributeInfoVO();
           if(personnelRuleVO!=null){
               personnelRuleVOS.add(personnelRuleVO);
           }
        }
        return Result.newSuccessResult(personnelRuleVOS);
    }

    /**
     * 获取委托列表
     * @param requestDto
     * @param type
     * @return
     */
    @PostMapping("/entrustlist/{type}")
    public ResultAndPage<Entrust> entrustlist(@RequestBody DetailRequestDto requestDto, @PathVariable("type") Integer type){

        PageDto pageDto = requestDto.getPageDto();
        Entrust vo = new Entrust();
        return userEntrustService.getEntrustPageList(pageDto,vo,type);
    }

    /**
     * 获取委托详情
     * @param id
     * @return
     */

    @Operation(summary ="获取委托详情")
    @GetMapping("/entrustDetail/{id}")
    public Result entrustDetail(@PathVariable("id") Integer id){
        UserEntrust detail = userEntrustService.getEntrustDetail(id);
        return Result.newSuccessResult(detail);
    }

    /**
     * 编辑委托
     * @param dataVo
     * @return
     */
    @PostMapping("/editEntrust")
    public Result editEntrust(@RequestBody DataVo dataVo){
        userEntrustService.updateEntrustList(dataVo);
        return Result.newSuccessResult("ok");
    }

    @GetMapping("/getchooseModules")
    public Result getStartUserPickAssigneeNodes(String formCode){
        if(StringUtils.isEmpty(formCode)){
            throw new JiMuBizException("请传入formCode");
        }
        List<BpmnNode> nodes = bpmnNodeMapper.getNodesByFormCodeAndProperty(formCode, NodePropertyEnum.NODE_PROPERTY_CUSTOMIZE.getCode());
        return Result.newSuccessResult(nodes);
    }
    private List<BaseKeyValueStruVo> baseFormInfo(String desc){
        List<BaseKeyValueStruVo> results=new ArrayList<>();
        for (Map.Entry<String, FormOperationAdaptor> stringFormOperationAdaptorEntry : formOperationAdaptorMap.entrySet()) {
            String key=stringFormOperationAdaptorEntry.getKey();
            ActivitiServiceAnno annotation = stringFormOperationAdaptorEntry.getValue().getClass().getAnnotation(ActivitiServiceAnno.class);
            if (StringUtils.isEmpty(annotation.desc())){
                continue;
            }
            if(!StringUtils.isEmpty(desc)){
                if(annotation.desc().contains(desc)){
                    results.add(
                            BaseKeyValueStruVo
                                    .builder()
                                    .key(key)
                                    .value(annotation.desc())
                                    .type("DIY")
                                    .build()
                    );
                }
            }
            else{
                results.add(
                        BaseKeyValueStruVo
                                .builder()
                                .key(key)
                                .value(annotation.desc())
                                .type("DIY")
                                .build()
                );
            }
        }
        return results;
    }

    private List<BaseKeyValueStruVo> allFormInfo(){
        List<BaseKeyValueStruVo> results= baseFormInfo("");
        List<BaseKeyValueStruVo> lfFormCodes= lowCodeFlowBizService.getLFActiveFormCodes();
        if (lfFormCodes != null){
            results.addAll(lfFormCodes);
        }
        return results;
    }
}

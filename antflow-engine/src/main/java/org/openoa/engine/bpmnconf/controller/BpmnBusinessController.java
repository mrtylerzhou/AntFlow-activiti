package org.openoa.engine.bpmnconf.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.enums.NodePropertyEnum;
import org.openoa.base.dto.PageDto;
import org.openoa.base.entity.Result;
import org.openoa.base.exception.JiMuBizException;
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
import org.openoa.engine.vo.PsPreCheckVO;
import org.openoa.engine.vo.PsPreRespVO;
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
    /**
     * 获取自定义表单DIY FormCode List
     * @param desc
     * @return
     */
    @GetMapping("/getDIYFormCodeList")
    public Result getDIYFormCodeList(String desc){
        return Result.newSuccessResult(baseFormInfo(desc));
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
    /**私有方法 */
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

}

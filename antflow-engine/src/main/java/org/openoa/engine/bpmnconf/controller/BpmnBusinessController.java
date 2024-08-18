package org.openoa.engine.bpmnconf.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.entity.Result;
import org.openoa.base.interf.ActivitiServiceAnno;
import org.openoa.base.interf.FormOperationAdaptor;
import org.openoa.base.util.SpringBeanUtils;
import org.openoa.base.vo.BaseKeyValueStruVo;
import org.openoa.base.vo.PersonnelRuleVO;
import org.openoa.engine.bpmnconf.adp.bpmnnodeadp.BpmnNodeAdaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author TylerZhou
 * @Date 2024/6/30 6:43
 * @Version 1.0
 */
@Slf4j
@RestController
@RequestMapping(value = "/bpmnBusiness")
public class BpmnBusinessController {

    @Autowired
    Map<String, FormOperationAdaptor> formOperationAdaptorMap;

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
    private List<BaseKeyValueStruVo> baseFormInfo(String desc){
        List<BaseKeyValueStruVo> results=new ArrayList<>();
        for (Map.Entry<String, FormOperationAdaptor> stringFormOperationAdaptorEntry : formOperationAdaptorMap.entrySet()) {
            String key=stringFormOperationAdaptorEntry.getKey();
            ActivitiServiceAnno annotation = stringFormOperationAdaptorEntry.getValue().getClass().getAnnotation(ActivitiServiceAnno.class);
            if(!StringUtils.isEmpty(desc)){
                if(annotation.desc().contains(desc)){
                    results.add(
                            BaseKeyValueStruVo
                                    .builder()
                                    .key(key)
                                    .value(annotation.desc())
                                    .build()
                    );
                }
            }else{
                results.add(
                        BaseKeyValueStruVo
                                .builder()
                                .key(key)
                                .value(annotation.desc())
                                .build()
                );
            }
        }
        return results;
    }
}

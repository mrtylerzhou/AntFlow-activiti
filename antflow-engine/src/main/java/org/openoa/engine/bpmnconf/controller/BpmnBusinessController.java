package org.openoa.engine.bpmnconf.controller;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.enums.NodePropertyEnum;
import org.openoa.base.dto.PageDto;
import org.openoa.base.entity.Result;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.vo.*;
import org.openoa.engine.bpmnconf.common.TaskMgmtServiceImpl;
import org.openoa.engine.bpmnconf.confentity.BpmnNode;
import org.openoa.engine.bpmnconf.confentity.UserEntrust;
import org.openoa.engine.bpmnconf.mapper.BpmnNodeMapper;
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

@RestController
@RequestMapping(value = "/bpmnBusiness")
public class BpmnBusinessController {

    @Autowired
    private TaskMgmtServiceImpl taskMgmtService;
    @Autowired
    private UserEntrustServiceImpl userEntrustService;
    @Autowired
    private BpmnNodeMapper bpmnNodeMapper;

    /**
     * 获取自定义表单DIY FormCode List
     *
     * @param desc
     * @return
     */
    @GetMapping("/getDIYFormCodeList")
    public Result getDIYFormCodeList(String desc) {
        List<DIYProcessInfoDTO> diyProcessInfoDTOS = taskMgmtService.viewProcessInfo(desc);
        return Result.newSuccessResult(diyProcessInfoDTOS);
    }

    /**
     * 获取委托列表
     *
     * @param requestDto
     * @param type
     * @return
     */
    @PostMapping("/entrustlist/{type}")
    public ResultAndPage<Entrust> entrustlist(@RequestBody DetailRequestDto requestDto, @PathVariable("type") Integer type) {

        PageDto pageDto = requestDto.getPageDto();
        Entrust vo = new Entrust();
        return userEntrustService.getEntrustPageList(pageDto, vo, type);
    }

    /**
     * 获取委托详情
     *
     * @param id
     * @return
     */

    @GetMapping("/entrustDetail/{id}")
    public Result entrustDetail(@PathVariable("id") Integer id) {
        UserEntrust detail = userEntrustService.getEntrustDetail(id);
        return Result.newSuccessResult(detail);
    }

    /**
     * 编辑委托
     *
     * @param dataVo
     * @return
     */
    @PostMapping("/editEntrust")
    public Result editEntrust(@RequestBody DataVo dataVo) {
        userEntrustService.updateEntrustList(dataVo);
        return Result.newSuccessResult("ok");
    }

    /**
     * 获取流程自选审批人节点
     *
     * @param formCode
     * @return
     */
    @GetMapping("/getStartUserChooseModules")
    public Result getStartUserChooseModules(String formCode) {
        if (StringUtils.isEmpty(formCode)) {
            throw new JiMuBizException("参数formCode不能为空!");
        }
        List<BpmnNode> nodesByFormCodeAndProperty = bpmnNodeMapper.getNodesByFormCodeAndProperty(formCode, NodePropertyEnum.NODE_PROPERTY_CUSTOMIZE.getCode());
        List<BpmnNodeVo> nodeVos = nodesByFormCodeAndProperty.stream().map(a -> {
            BpmnNodeVo bpmnNodeVo = new BpmnNodeVo();
            bpmnNodeVo.setId(a.getId());
            bpmnNodeVo.setNodeName(a.getNodeName());
            return bpmnNodeVo;
        }).collect(Collectors.toList());
        return Result.newSuccessResult(nodeVos);
    }


}

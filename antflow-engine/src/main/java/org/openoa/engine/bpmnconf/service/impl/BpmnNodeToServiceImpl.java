package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.base.util.SecurityUtils;
import org.openoa.engine.bpmnconf.confentity.BpmnNodeTo;
import org.openoa.engine.bpmnconf.mapper.BpmnNodeToMapper;
import org.openoa.base.vo.BpmnNodeVo;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BpmnNodeToServiceImpl extends ServiceImpl<BpmnNodeToMapper, BpmnNodeTo> {


    public void editNodeTo(BpmnNodeVo bpmnNodeVo, Long bpmnNodeId) {
        List<String> nodeTo = bpmnNodeVo.getNodeTo();
        if (ObjectUtils.isEmpty(bpmnNodeVo)) {
            return;
        }

        //delete existing data
        this.getBaseMapper().delete(new QueryWrapper<BpmnNodeTo>()
                .eq("bpmn_node_id", bpmnNodeId));

        List<BpmnNodeTo> bpmnNodeToList = nodeTo.stream()
                .map(o -> BpmnNodeTo
                        .builder()
                        .bpmnNodeId(bpmnNodeId)
                        .nodeTo(o)
                        .createUser(SecurityUtils.getLogInEmpNameSafe())
                        .createTime(new Date())
                        .build())
                .collect(Collectors.toList());

        this.saveBatch(bpmnNodeToList);
    }

}

package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.entity.BpmnTemplate;
import org.openoa.base.util.AntCollectionUtil;
import org.openoa.base.util.SecurityUtils;
import org.openoa.engine.bpmnconf.mapper.BpmnTemplateMapper;
import org.openoa.base.vo.BpmnConfVo;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.base.vo.BpmnTemplateVo;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnTemplateService;
import org.openoa.base.util.MultiTenantUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class BpmnTemplateServiceImpl extends ServiceImpl<BpmnTemplateMapper, BpmnTemplate> implements BpmnTemplateService {

    @Override
    public void editBpmnTemplate(BpmnConfVo bpmnConfVo, Long confId) {
        List<BpmnTemplateVo> templateVos = bpmnConfVo.getTemplateVos();
        if (ObjectUtils.isEmpty(templateVos)) {
            return;
        }

        List<BpmnTemplate> bpmnTemplateList = bpmnConfVo.getTemplateVos()
                .stream()
                .map(o -> {
                    BpmnTemplate bpmnTemplate = new BpmnTemplate();
                    BeanUtils.copyProperties(o, bpmnTemplate);
                    bpmnTemplate.setConfId(confId);
                    bpmnTemplate.setInforms(StringUtils.join(o.getInformIdList(), ","));
                    bpmnTemplate.setEmps(StringUtils.join(o.getEmpIdList(), ","));
                    bpmnTemplate.setRoles(StringUtils.join(o.getRoleIdList(), ","));
                    bpmnTemplate.setFuncs(StringUtils.join(o.getFuncIdList(), ","));
                    bpmnTemplate.setMessageSendType(AntCollectionUtil.joinBaseNumIdTransVoToString(o.getMessageSendTypeList()));
                    bpmnTemplate.setFormCode(bpmnConfVo.getFormCode());
                    bpmnTemplate.setCreateUser(SecurityUtils.getLogInEmpNameSafe());
                    bpmnTemplate.setTenantId(MultiTenantUtil.getCurrentTenantId());
                    return bpmnTemplate;
                })
                .collect(Collectors.toList());

        this.saveBatch(bpmnTemplateList);
    }

    @Override
    public void editBpmnTemplate(BpmnNodeVo bpmnNodeVo) {
        List<BpmnTemplateVo> templateVos = bpmnNodeVo.getTemplateVos();
        if (ObjectUtils.isEmpty(templateVos)) {
            return;
        }
        this.saveBatch(
                templateVos
                        .stream()
                        .map(o -> {
                            BpmnTemplate bpmnTemplate = new BpmnTemplate();
                            BeanUtils.copyProperties(o, bpmnTemplate);
                            bpmnTemplate.setId(null);
                            bpmnTemplate.setConfId(bpmnNodeVo.getConfId());
                            bpmnTemplate.setNodeId(bpmnNodeVo.getId());
                            bpmnTemplate.setInforms(StringUtils.join(o.getInformIdList(), ","));
                            bpmnTemplate.setEmps(StringUtils.join(o.getEmpIdList(), ","));
                            bpmnTemplate.setRoles(StringUtils.join(o.getRoleIdList(), ","));
                            bpmnTemplate.setFuncs(StringUtils.join(o.getFuncIdList(), ","));
                            bpmnTemplate.setMessageSendType(AntCollectionUtil.joinBaseNumIdTransVoToString(o.getMessageSendTypeList()));
                            bpmnTemplate.setFormCode(bpmnNodeVo.getFormCode());
                            bpmnTemplate.setCreateUser(SecurityUtils.getLogInEmpNameSafe());
                            bpmnTemplate.setTenantId(MultiTenantUtil.getCurrentTenantId());
                            return bpmnTemplate;
                        })
                        .collect(Collectors.toList()));
    }

}
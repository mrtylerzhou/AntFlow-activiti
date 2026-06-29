package org.openoa.engine.bpmnconf.service.biz;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.openoa.base.constant.enums.EventTypeEnum;
import org.openoa.base.entity.*;
import org.openoa.base.entity.jsonconf.BpmnConfConfigJson;
import org.openoa.base.entity.jsonconf.BpmnNodeConfigJson;
import org.openoa.base.entity.jsonconf.BpmnNodeTemplateConfJson;
import org.openoa.base.entity.jsonconf.JsonConfUtil;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.DefaultTemplateVo;
import org.openoa.base.vo.InformationTemplateVo;
import org.openoa.engine.bpmnconf.service.interf.biz.InformationTemplateBizService;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnApproveRemindService;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnConfService;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnNodeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class InformationTemplateBizServiceImpl implements InformationTemplateBizService {

    @Autowired
    private BpmnApproveRemindService bpmnApproveRemindService;
    @Autowired
    private BpmnConfService bpmnConfService;
    @Autowired
    private BpmnNodeService bpmnNodeService;

    /**
     * modify
     *
     * @param informationTemplateVo informationTemplateVo
     */
    @Transactional
    @Override
    public long edit(InformationTemplateVo informationTemplateVo) {
        //to check whether the template's name is duplicated
        List<InformationTemplate> list = this.getMapper().selectList(
                new QueryWrapper<InformationTemplate>()
                        .eq("name", informationTemplateVo.getName())
                        .eq("is_del", 0)
                        .ne(!ObjectUtils.isEmpty(informationTemplateVo.getId()),
                                "id", informationTemplateVo.getId()));

        if (!ObjectUtils.isEmpty(list)) {
            throw new AFBizException("模板名称重复");
        }

        InformationTemplate informationTemplate = new InformationTemplate();
        BeanUtils.copyProperties(informationTemplateVo, informationTemplate);
        if (!ObjectUtils.isEmpty(informationTemplate.getId())) {
            //modify
            if (informationTemplate.getStatus().equals(1)) {

                boolean usedInJson = isTemplateUsedInJson(informationTemplate.getId());
                if (usedInJson) {
                    throw new AFBizException("该模板正在使用中，不可禁用！");
                }

            }
            informationTemplate.setUpdateUser(SecurityUtils.getLogInEmpIdSafe());
        } else {
            //add
            informationTemplate.setCreateUser(SecurityUtils.getLogInEmpNameSafe());
            informationTemplate.setUpdateUser(SecurityUtils.getLogInEmpNameSafe());
            this.getMapper().insert(informationTemplate);
            informationTemplate.setNum("LCTZ_" + String.format("%03d", informationTemplate.getId()));
        }
        getService().updateById(informationTemplate);
        return informationTemplate.getId();
    }
    /**
     * get template list by no condition
     *
     * @return list
     */
    @Override
    public List<DefaultTemplateVo> getList() {
        Map<Integer, InformationTemplate> defaultMap = getMapper().selectList(
                        new QueryWrapper<InformationTemplate>()
                                .eq("is_del", 0)
                                .eq("is_default", 1))
                .stream()
                .filter(o -> !ObjectUtils.isEmpty(o.getEvent()))
                .collect(Collectors.toMap(InformationTemplate::getEvent,
                        v -> v,
                        (a, b) -> a));
        return Arrays.stream(EventTypeEnum.values())
                .map(o -> {
                    InformationTemplate template = defaultMap.get(o.getCode());
                    return DefaultTemplateVo
                            .builder()
                            .event(o.getCode())
                            .eventValue(o.getDesc())
                            .templateId(template != null ? template.getId() : null)
                            .templateName(template != null ? template.getName() : null)
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * set default template list
     */
    @Override
    public void setList(List<DefaultTemplateVo> vos) {
        // clear all existing defaults
        getMapper().update(null, new UpdateWrapper<InformationTemplate>()
                .eq("is_del", 0)
                .eq("is_default", 1)
                .set("is_default", 0));
        // set new defaults
        vos.forEach(o -> {
            if (!ObjectUtils.isEmpty(o.getTemplateId())) {
                getMapper().update(null, new UpdateWrapper<InformationTemplate>()
                        .eq("id", o.getTemplateId())
                        .eq("is_del", 0)
                        .set("is_default", 1));
            }
        });
    }

    private boolean isTemplateUsedInJson(Long templateId) {
        List<BpmnConf> confs = bpmnConfService.list(new QueryWrapper<BpmnConf>()
                .eq("is_del", 0)
                .eq("effective_status", 1)
                .isNotNull("conf_config_json"));
        for (BpmnConf conf : confs) {
            BpmnConfConfigJson confConfig = JsonConfUtil.parseConfConfig(conf.getConfConfigJson());
            if (confConfig == null) {
                continue;
            }
            if (!CollectionUtils.isEmpty(confConfig.getConfTemplates())) {
                for (BpmnConfConfigJson.ConfTemplateConf tc : confConfig.getConfTemplates()) {
                    if (templateId.equals(tc.getTemplateId())) {
                        return true;
                    }
                }
            }
        }

        List<BpmnNode> nodes = bpmnNodeService.list(new QueryWrapper<BpmnNode>()
                .eq("is_del", 0)
                .isNotNull("node_config_json"));
        for (BpmnNode node : nodes) {
            BpmnNodeConfigJson nodeConfig = JsonConfUtil.parseNodeConfig(node.getNodeConfigJson());
            if (nodeConfig == null || nodeConfig.getTemplateConf() == null) {
                continue;
            }
            BpmnNodeTemplateConfJson templateConf = nodeConfig.getTemplateConf();
            if (!CollectionUtils.isEmpty(templateConf.getTemplates())) {
                for (BpmnNodeTemplateConfJson.TemplateConf tc : templateConf.getTemplates()) {
                    if (templateId.equals(tc.getTemplateId())) {
                        return true;
                    }
                }
            }
            if (templateConf.getApproveRemind() != null
                    && templateId.equals(templateConf.getApproveRemind().getTemplateId())) {
                return true;
            }
        }
        return false;
    }

}

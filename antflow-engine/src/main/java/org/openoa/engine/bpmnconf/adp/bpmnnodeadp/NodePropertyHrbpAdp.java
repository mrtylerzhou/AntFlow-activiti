package org.openoa.engine.bpmnconf.adp.bpmnnodeadp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.openoa.base.constant.enums.FieldValueTypeEnum;
import org.openoa.base.constant.enums.NodePropertyEnum;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.*;
import org.openoa.base.entity.BpmnNodeHrbpConf;
import org.openoa.engine.bpmnconf.constant.enus.BpmnNodeAdpConfEnum;
import org.openoa.engine.bpmnconf.service.impl.BpmnNodeHrbpConfServiceImpl;
import org.openoa.base.util.MultiTenantUtil;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnNodeHrbpConfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author AntFlow
 * @since 0.5
 */
@Slf4j
@Component
public class NodePropertyHrbpAdp implements BpmnNodeAdaptor {

    @Autowired
    private BpmnNodeHrbpConfService bpmnNodeHrbpConfService;

    @Override
    public BpmnNodeVo formatToBpmnNodeVo(BpmnNodeVo bpmnNodeVo) {

        BpmnNodeHrbpConf bpmnNodeHrbpConf = bpmnNodeHrbpConfService.getOne(new QueryWrapper<BpmnNodeHrbpConf>()
                .eq("bpmn_node_id", bpmnNodeVo.getId()));

        if (bpmnNodeHrbpConf!=null) {
            bpmnNodeVo.setProperty(BpmnNodePropertysVo
                    .builder()
                    .hrbpConfType(bpmnNodeHrbpConf.getHrbpConfType())
                    .build());
        }

        return bpmnNodeVo;
    }

    @Override
    public PersonnelRuleVO formaFieldAttributeInfoVO() {


        try {
            Class<BpmnNodeHrbpConf> entityCls=BpmnNodeHrbpConf.class;
            PersonnelRuleVO rule = new PersonnelRuleVO();
            NodePropertyEnum nodePropertyHrbp = NodePropertyEnum.NODE_PROPERTY_HRBP;
            rule.setNodeProperty(nodePropertyHrbp.getCode());
            rule.setNodePropertyName(nodePropertyHrbp.getDesc());
            String hrbpConfTypeName = entityCls.getDeclaredField("hrbpConfType").getName();
            FieldAttributeInfoVO vo=new FieldAttributeInfoVO();
            vo.setFieldName(hrbpConfTypeName);
            vo.setFieldLabel("hrbp类型");
            vo.setFieldType(FieldValueTypeEnum.NUMBERCHOICE.getDesc());
            List<BaseIdTranStruVo> values= Lists.newArrayList(
                    new BaseIdTranStruVo("0","请选择"),
                    new BaseIdTranStruVo("1","hrbp"),
                    new BaseIdTranStruVo("2","hrbp leader")
            );
            vo.setValue(values);
            rule.setFieldInfos(Lists.newArrayList(vo));
            return rule;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void editBpmnNode(BpmnNodeVo bpmnNodeVo) {

        BpmnNodePropertysVo bpmnNodePropertysVo = Optional.ofNullable(bpmnNodeVo.getProperty())
                .orElse(new BpmnNodePropertysVo());


        BpmnNodeHrbpConf bpmnNodeHrbpConf = new BpmnNodeHrbpConf();
        bpmnNodeHrbpConf.setBpmnNodeId(bpmnNodeVo.getId());
        bpmnNodeHrbpConf.setHrbpConfType(bpmnNodePropertysVo.getHrbpConfType());
        bpmnNodeHrbpConf.setCreateTime(new Date());
        bpmnNodeHrbpConf.setCreateUser(SecurityUtils.getLogInEmpName());
        bpmnNodeHrbpConf.setUpdateTime(new Date());
        bpmnNodeHrbpConf.setUpdateUser(SecurityUtils.getLogInEmpName());
        bpmnNodeHrbpConf.setTenantId(MultiTenantUtil.getCurrentTenantId());
        bpmnNodeHrbpConfService.save(bpmnNodeHrbpConf);
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(BpmnNodeAdpConfEnum.ADP_CONF_NODE_PROPERTY_HRBP);
    }
}

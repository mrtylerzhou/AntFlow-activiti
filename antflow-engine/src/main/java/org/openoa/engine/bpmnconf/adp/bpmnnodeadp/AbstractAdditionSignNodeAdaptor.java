package org.openoa.engine.bpmnconf.adp.bpmnnodeadp;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.openoa.base.constant.enums.NodePropertyEnum;
import org.openoa.base.entity.BpmnNodeAdditionalSignConf;
import org.openoa.base.entity.jsonconf.BpmnNodeButtonSignConfJson;
import org.openoa.base.entity.jsonconf.BpmnNodeConfigJson;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.service.AfRoleService;
import org.openoa.base.util.AfNodeUtils;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.*;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnNodeAdditionalSignConfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractAdditionSignNodeAdaptor implements BpmnNodeAdaptor {


    @Autowired
    private AfRoleService roleService;

    /**
     * format BpmnNodeVo
     *
     * @param bpmnNodeVo
     * @return
     */
    @Override
    public void formatToBpmnNodeVo(BpmnNodeVo bpmnNodeVo) {
        Integer extraFlags = bpmnNodeVo.getExtraFlags();
        if (extraFlags == null) {
            return;
        }

        // Prefer JSON config if available
        BpmnNodeConfigJson nodeConfig = bpmnNodeVo.getNodeConfigJsonObj();
        if (nodeConfig != null && nodeConfig.getButtonSignConf() != null
                && !CollectionUtils.isEmpty(nodeConfig.getButtonSignConf().getAdditionalSignConfList())) {
            formatAdditionalSignFromJson(bpmnNodeVo, nodeConfig.getButtonSignConf().getAdditionalSignConfList());
        }

    }

    private void formatAdditionalSignFromJson(BpmnNodeVo bpmnNodeVo, List<BpmnNodeButtonSignConfJson.AdditionalSignConf> addSignConfs) {
        List<ExtraSignInfoVo> additionalSignInfoList = new ArrayList<>();
        for (BpmnNodeButtonSignConfJson.AdditionalSignConf conf : addSignConfs) {
            List<BaseIdTranStruVo> baseIdTranStruVos = JSON.parseArray(conf.getSignInfos(), BaseIdTranStruVo.class);
            ExtraSignInfoVo extraSignInfoVo = new ExtraSignInfoVo();
            extraSignInfoVo.setNodeProperty(conf.getSignProperty());
            extraSignInfoVo.setPropertyType(conf.getSignPropertyType());
            extraSignInfoVo.setSignInfos(baseIdTranStruVos);
            if (conf.getSignPropertyType() != null && conf.getSignPropertyType().equals(NodePropertyEnum.NODE_PROPERTY_ROLE.getCode())) {
                List<String> roleIds = baseIdTranStruVos.stream().map(BaseIdTranStruVo::getId).collect(Collectors.toList());
                List<BaseIdTranStruVo> roleInfos = roleService.queryUserByRoleIds(roleIds);
                extraSignInfoVo.setOtherSignInfos(roleInfos);
            }
            additionalSignInfoList.add(extraSignInfoVo);
        }
        AfNodeUtils.addOrEditProperty(bpmnNodeVo, a -> a.setAdditionalSignInfoList(additionalSignInfoList));
    }

}

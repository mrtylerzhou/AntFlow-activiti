package org.openoa.engine.bpmnconf.adp.bpmnnodeadp;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.openoa.base.constant.enums.NodePropertyEnum;
import org.openoa.base.entity.BpmnNodeAdditionalSignConf;
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
    private BpmnNodeAdditionalSignConfService additionalSignConfService;
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
        List<BpmnNodeAdditionalSignConf> bpmnNodeAdditionalSignConfs = additionalSignConfService.getMapper().selectList(Wrappers.<BpmnNodeAdditionalSignConf>lambdaQuery()
                .eq(BpmnNodeAdditionalSignConf::getBpmnNodeId, bpmnNodeVo.getId()));
        if (CollectionUtils.isEmpty(bpmnNodeAdditionalSignConfs)) {
            return;
        }
        BpmnNodePropertysVo bpmnNodePropertysVo = new BpmnNodePropertysVo();
        List<ExtraSignInfoVo> additionalSignInfoList = new ArrayList<>();

        for (BpmnNodeAdditionalSignConf bpmnNodeAdditionalSignConf : bpmnNodeAdditionalSignConfs) {
            Integer signProperty = bpmnNodeAdditionalSignConf.getSignProperty();
            Integer signPropertyType = bpmnNodeAdditionalSignConf.getSignPropertyType();
            String signInfos = bpmnNodeAdditionalSignConf.getSignInfos();
            List<BaseIdTranStruVo> baseIdTranStruVos = JSON.parseArray(signInfos, BaseIdTranStruVo.class);
            ExtraSignInfoVo extraSignInfoVo = new ExtraSignInfoVo();
            extraSignInfoVo.setNodeProperty(signProperty);
            extraSignInfoVo.setPropertyType(signPropertyType);
            extraSignInfoVo.setSignInfos(baseIdTranStruVos);
            if (signPropertyType.equals(NodePropertyEnum.NODE_PROPERTY_ROLE.getCode())) {
                List<String> roleIds = baseIdTranStruVos.stream().map(BaseIdTranStruVo::getId).collect(Collectors.toList());
                List<BaseIdTranStruVo> roleInfos = roleService.queryUserByRoleIds(roleIds);//这个是用于存储角色下的人信息的
                extraSignInfoVo.setOtherSignInfos(roleInfos);
            }
            additionalSignInfoList.add(extraSignInfoVo);
        }
        bpmnNodePropertysVo.setAdditionalSignInfoList(additionalSignInfoList);//这个是给前端用于反显设计数据的,其中指定人员直接从这里获取
        AfNodeUtils.addOrEditProperty(bpmnNodeVo, a -> a.setAdditionalSignInfoList(additionalSignInfoList));
    }

    /**
     * edit bpmn node info
     *
     * @param bpmnNodeVo
     */
    @Override
    public void editBpmnNode(BpmnNodeVo bpmnNodeVo) {
        Integer extraFlags = bpmnNodeVo.getExtraFlags();
        if (extraFlags == null) {
            return;
        }
        List<ExtraSignInfoVo> additionalSignInfoList = bpmnNodeVo.getProperty().getAdditionalSignInfoList();
        if (CollectionUtils.isEmpty(additionalSignInfoList)) {
            return;
        }
        List<BpmnNodeAdditionalSignConf> bpmnNodeAdditionalSignConfs = new ArrayList<>();
        for (ExtraSignInfoVo extraSignInfoVo : additionalSignInfoList) {
            Integer signProperty = extraSignInfoVo.getNodeProperty();
            Integer signPropertyType = extraSignInfoVo.getPropertyType();
            List<BaseIdTranStruVo> signInfos = extraSignInfoVo.getSignInfos();
            String signInfosStr = JSON.toJSONString(signInfos);

            BpmnNodeAdditionalSignConf bpmnNodeAdditionalSignConf = new BpmnNodeAdditionalSignConf();
            bpmnNodeAdditionalSignConf.setBpmnNodeId(bpmnNodeVo.getId());
            bpmnNodeAdditionalSignConf.setSignInfos(signInfosStr);
            bpmnNodeAdditionalSignConf.setSignProperty(signProperty);
            bpmnNodeAdditionalSignConf.setSignPropertyType(signPropertyType);
            bpmnNodeAdditionalSignConf.setCreateUser(SecurityUtils.getLogInEmpIdStr());
            bpmnNodeAdditionalSignConf.setCreateTime(new Date());
            bpmnNodeAdditionalSignConf.setUpdateUser(SecurityUtils.getLogInEmpIdStr());
            bpmnNodeAdditionalSignConf.setUpdateTime(new Date());
            bpmnNodeAdditionalSignConfs.add(bpmnNodeAdditionalSignConf);
        }
        additionalSignConfService.saveBatch(bpmnNodeAdditionalSignConfs);
    }
}

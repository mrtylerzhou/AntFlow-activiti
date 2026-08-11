package org.openoa.engine.bpmnconf.service.biz;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BpmnNodeParamsAssigneeVo;
import org.openoa.base.vo.BpmnNodePropertysVo;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.engine.bpmnconf.service.biz.personnelinfoprovider.DirectLeaderPersonnelProvider;
import org.openoa.engine.bpmnconf.service.biz.personnelinfoprovider.HrbpPersonnelProvider;
import org.openoa.engine.bpmnconf.service.biz.personnelinfoprovider.LevelPersonnelProvider;
import org.openoa.engine.bpmnconf.service.biz.personnelinfoprovider.RolePersonnelProvider;
import org.openoa.engine.bpmnconf.service.biz.personnelinfoprovider.StartUserPersonnelProvider;
import org.openoa.engine.bpmnconf.service.biz.personnelinfoprovider.UDRPersonnelProvider;
import org.openoa.engine.bpmnconf.service.biz.personnelinfoprovider.UserPointedPersonnelProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 条件自动加批节点加批规则解析器:
 * 把前端保存的 autoSignUpConf({nodeProperty, resolvedProperty})按审批人规则类型
 * 复用现有 personnel provider 运行期解析为具体用户列表.
 * 支持: 5指定人员 / 4指定角色 / 13直属领导 / 3指定层级 / 6HRBP / 12发起人自己 / 17自定义.
 * 直属领导/层级/HRBP 的基准人为流程发起人(与审批人节点语义一致).
 */
@Slf4j
@Service
public class AutoSignUpAssigneeResolver {

    @Autowired
    private UserPointedPersonnelProvider userPointedPersonnelProvider;
    @Autowired
    private RolePersonnelProvider rolePersonnelProvider;
    @Autowired
    private DirectLeaderPersonnelProvider directLeaderPersonnelProvider;
    @Autowired
    private LevelPersonnelProvider levelPersonnelProvider;
    @Autowired
    private HrbpPersonnelProvider hrbpPersonnelProvider;
    @Autowired
    private StartUserPersonnelProvider startUserPersonnelProvider;
    @Autowired
    private UDRPersonnelProvider udrPersonnelProvider;

    /**
     * 解析加批规则为具体用户列表.
     *
     * @param autoSignUpConf 前端保存的规则子配置 JSON({nodeProperty, resolvedProperty})
     * @param startUserId    流程发起人ID(领导链/HRBP 基准)
     * @param businessDataVo 业务数据(UDR 等 provider 需要)
     * @return 解析出的用户列表; null=配置缺失或类型不支持; 空列表=解析结果为空(调用方视为条件不满足)
     */
    public List<BaseIdTranStruVo> resolve(Object autoSignUpConf, String startUserId, BusinessDataVo businessDataVo) {
        if (autoSignUpConf == null) {
            return null;
        }
        JSONObject confJson;
        try {
            confJson = JSON.parseObject(JSON.toJSONString(autoSignUpConf));
        } catch (Exception e) {
            log.error("条件自动加批: autoSignUpConf 解析失败", e);
            return null;
        }
        if (confJson == null) {
            return null;
        }
        Integer nodeProperty = confJson.getInteger("nodeProperty");
        if (nodeProperty == null) {
            nodeProperty = confJson.getInteger("setType");
        }
        if (nodeProperty == null) {
            return null;
        }

        // 合成虚拟节点 VO: nodeProperty + resolvedProperty→property
        BpmnNodeVo conf = new BpmnNodeVo();
        conf.setNodeProperty(nodeProperty);
        conf.setNodeName("条件自动加批");
        JSONObject propJson = confJson.getJSONObject("resolvedProperty");
        if (propJson != null) {
            conf.setProperty(JSON.parseObject(propJson.toJSONString(), BpmnNodePropertysVo.class));
        }

        BpmnStartConditionsVo sc = new BpmnStartConditionsVo();
        sc.setStartUserId(startUserId);
        sc.setBusinessDataVo(businessDataVo);

        List<BpmnNodeParamsAssigneeVo> assignees;
        try {
            switch (nodeProperty) {
                case 5:
                    assignees = userPointedPersonnelProvider.getAssigneeList(conf, sc);
                    break;
                case 4:
                    assignees = rolePersonnelProvider.getAssigneeList(conf, sc);
                    break;
                case 13:
                    assignees = directLeaderPersonnelProvider.getAssigneeList(conf, sc);
                    break;
                case 3:
                    assignees = levelPersonnelProvider.getAssigneeList(conf, sc);
                    break;
                case 6:
                    assignees = hrbpPersonnelProvider.getAssigneeList(conf, sc);
                    break;
                case 12:
                    assignees = startUserPersonnelProvider.getAssigneeList(conf, sc);
                    break;
                case 17:
                    assignees = udrPersonnelProvider.getAssigneeList(conf, sc);
                    break;
                default:
                    log.warn("条件自动加批: 不支持的审批人规则类型 setType={}", nodeProperty);
                    return null;
            }
        } catch (Exception e) {
            log.error("条件自动加批: 规则解析异常, setType={}, startUserId={}", nodeProperty, startUserId, e);
            return null;
        }
        if (assignees == null) {
            return null;
        }
        return assignees.stream()
                .filter(a -> a.getAssignee() != null)
                .map(a -> new BaseIdTranStruVo(a.getAssignee(), a.getAssigneeName()))
                .collect(Collectors.toList());
    }
}

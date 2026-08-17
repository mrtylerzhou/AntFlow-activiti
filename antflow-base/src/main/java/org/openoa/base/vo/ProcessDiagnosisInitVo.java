package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * 流程诊断初始化数据 (GET /bpmnConf/diagnosisInit)
 *
 * <p>前端打开诊断 dialog 首屏调用, 用于:</p>
 * <ul>
 *   <li>定位流程模板: 前端拿 confId 再调 /bpmnConf/detail/{confId} 渲染设计时流程图</li>
 *   <li>固定调试发起人: bpm_business_process.create_user, 只读</li>
 *   <li>调试表单预填 + 条件求值展示: formValues 为当前业务表单真实值
 *       (LF: lfFields/lfFieldsMulti 合并, key=fieldId; DIY: 业务实体 declared fields, key=字段名)</li>
 * </ul>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessDiagnosisInitVo implements Serializable {

    private String processNumber;

    /**
     * t_bpmn_conf.id, 前端用它调 detail 接口
     */
    private Long confId;

    /**
     * 版本号 (= bpm_business_process.version = t_bpmn_conf.bpmn_code)
     */
    private String bpmnCode;

    private String formCode;

    private Integer isLowCodeFlow;

    /**
     * 流程是否已结束(9 已完成 / 6 拒绝 等终态为 true)
     */
    private Boolean processFinished;

    /**
     * 发起人 id (bpm_business_process.create_user)
     */
    private String initiatorUserId;

    /**
     * 发起人姓名 (bpm_business_process.user_name)
     */
    private String initiatorUserName;

    /**
     * 当前业务表单值, 供调试预填与条件实际值展示
     */
    private Map<String, Object> formValues;
}

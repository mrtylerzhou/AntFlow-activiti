package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * "根据标签选择"审批人规则配置
 * - labelName/labelKey: 用户选择的流程标签(labelKey 作为 AfUserService 的标签标识)
 * - customVars: 用户自定义变量组(最多5组),发起时转换为 Map<String,String> 透传给 AfUserService
 *
 * @Author JimuOffice
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabelBasedApproverRuleVo implements Serializable {
    /**
     * 标签显示名(与流程标签字典的 name 对应)
     */
    private String labelName;

    /**
     * 标签 value/ID(与流程标签字典的 id 对应,作为 AfUserService 的标签标识)
     */
    private String labelKey;

    /**
     * 自定义变量组(0-5组,每组 displayName/varName/varValue)
     */
    private List<CustomVarGroup> customVars;
}

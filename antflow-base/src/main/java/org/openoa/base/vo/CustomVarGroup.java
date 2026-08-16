package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 自定义变量组: 显示标签 + 变量名(Map key) + 变量值(Map value)
 * 用于"根据标签选择"审批人规则,用户在设计期填写固定值,发起时透传给 AfUserService
 *
 * @Author JimuOffice
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomVarGroup implements Serializable {
    /**
     * 显示标签(可选,仅前端展示用)
     */
    private String displayName;

    /**
     * 变量名(必填,作为 Map key,同一规则内不可重复)
     */
    private String varName;

    /**
     * 变量值(必填非空,作为 Map value)
     */
    private String varValue;
}

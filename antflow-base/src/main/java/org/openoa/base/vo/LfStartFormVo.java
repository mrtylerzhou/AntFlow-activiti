package org.openoa.base.vo;

import lombok.Data;
import org.openoa.base.entity.BpmnConfLfFormdata;

import java.util.List;

/**
 * 发起流程时的表单数据响应 VO
 * 兼容内联表单(单表单)和外部表单(多表单)两种模式
 */
@Data
public class LfStartFormVo {
    /**
     * 是否使用外部表单模式
     */
    private Boolean useExternalForm;

    /**
     * 内联表单模式: 单个表单定义 JSON 字符串
     * 外部表单模式: null
     */
    private String lfFormData;

    /**
     * 外部表单模式: 引用的表单版本列表(含 formdata JSON 定义)
     * 内联表单模式: null
     */
    private List<BpmnConfLfFormdata> lfFormdataList;
}

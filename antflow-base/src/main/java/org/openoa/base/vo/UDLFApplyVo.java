package org.openoa.base.vo;

import lombok.Data;
import org.openoa.base.entity.BpmnConfLfFormdata;

import java.util.List;
import java.util.Map;

@Data
public class UDLFApplyVo extends BusinessDataVo {
   private String remark;
   private String lfFormData;
   /**
    * 多表单模式: 按表单版本id分组的字段值
    * Key = formdataId(字符串形式, 保证 JSON 合法), Value = 该表单的字段值Map<fieldId, value>
    * 仅外部表单模式使用; 内联模式为 null
    */
   private Map<String, Map<String, Object>> lfFieldsMulti;
   /**
    * 多表单模式: 引用的表单版本列表(含formdata JSON定义),供前端渲染多tab
    * 由 queryData 在外部表单模式下填充; 内联模式为 null(使用 lfFormData)
    */
   private List<BpmnConfLfFormdata> lfFormdataList;
}

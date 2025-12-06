package org.openoa.base.vo;

import lombok.Data;

import java.util.Map;

@Data
public class UDLFApplyVo extends BusinessDataVo {
   private String remark;
   private Map<String,Object> lfFields;
   private String lfFormData;;
}

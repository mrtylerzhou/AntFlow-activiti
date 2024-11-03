package org.openoa.engine.lowflow.vo;

import lombok.Data;
import org.openoa.base.vo.BusinessDataVo;

import java.util.Map;

@Data
public class UDLFApplyVo extends BusinessDataVo {

   private Map<String,Object> lfFields;
}

package org.openoa.engine.vo;

import lombok.Data;
import org.openoa.base.vo.ProcessNodeVo;

import java.util.List;

@Data
public class PsPreRespVO {
    //发起人自选模块
    private List<ProcessNodeVo> startUserChooseNodes;
}

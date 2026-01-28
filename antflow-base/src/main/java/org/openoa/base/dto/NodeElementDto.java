package org.openoa.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BaseInfoTranStructVo;

import java.util.List;

@Data
public class NodeElementDto
{
    private String nodeId;
    private String elementId;
    private List<BaseIdTranStruVo> assigneeInfoList;
    private Boolean isSingle;
    private String varName;
}
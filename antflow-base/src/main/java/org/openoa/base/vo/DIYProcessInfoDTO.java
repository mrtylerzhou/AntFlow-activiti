package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.activiti.validation.ProcessValidator;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DIYProcessInfoDTO {
    private String key;
    private String value;
    private String type;
    private String remark;
    private Date createTime;
    /**
     * 是否包含发起人自选模块,否为不包含,true为包含
     */
    private Boolean hasStarUserChooseModule=false;
    private List<BaseNumIdStruVo> processNotices;
    private List<BpmnTemplateVo> templateVos;
}

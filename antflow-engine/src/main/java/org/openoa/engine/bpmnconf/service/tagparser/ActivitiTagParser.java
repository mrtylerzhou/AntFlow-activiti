package org.openoa.engine.bpmnconf.service.tagparser;

import org.openoa.base.constant.StringConstants;
import org.openoa.engine.factory.TagParser;
import org.openoa.base.vo.BusinessDataVo;

public class ActivitiTagParser implements TagParser<String,BusinessDataVo> {
    @Override
    public String parseTag(BusinessDataVo data) {
        if(Boolean.TRUE.equals(data.getIsLowCodeFlow())){
            return StringConstants.LOWFLOW_FORM_CODE;
        }
        return data.getFormCode();
    }
}

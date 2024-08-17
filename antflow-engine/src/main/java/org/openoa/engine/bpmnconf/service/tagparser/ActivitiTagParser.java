package org.openoa.engine.bpmnconf.service.tagparser;

import org.openoa.engine.factory.TagParser;
import org.openoa.base.vo.BusinessDataVo;

public class ActivitiTagParser implements TagParser<String,BusinessDataVo> {
    @Override
    public String parseTag(BusinessDataVo data) {
        return data.getFormCode();
    }
}

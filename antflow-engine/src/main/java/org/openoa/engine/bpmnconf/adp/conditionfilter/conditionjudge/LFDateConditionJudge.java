package org.openoa.engine.bpmnconf.adp.conditionfilter.conditionjudge;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.FastDateFormat;
import org.openoa.base.util.DateUtil;
import org.openoa.base.vo.BpmnNodeConditionsConfBaseVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.springframework.stereotype.Service;

@Service
public class LFDateConditionJudge extends AbstractLFDateTimeConditionJudge{
    @Override
    protected FastDateFormat currentDateFormatter() {
        return DateUtil.SDF_DATE_PATTERN;
    }

}

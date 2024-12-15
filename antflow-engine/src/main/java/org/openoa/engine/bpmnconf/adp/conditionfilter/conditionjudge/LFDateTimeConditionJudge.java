package org.openoa.engine.bpmnconf.adp.conditionfilter.conditionjudge;

import org.apache.commons.lang3.time.FastDateFormat;
import org.openoa.base.util.DateUtil;
import org.springframework.stereotype.Service;

@Service
public class LFDateTimeConditionJudge extends AbstractLFDateTimeConditionJudge{
    @Override
    protected FastDateFormat currentDateFormatter() {
        return DateUtil.SDF_DATETIME_PATTERN;
    }

}

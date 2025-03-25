package org.openoa.engine.bpmnconf.adp.conditionfilter.conditionjudge;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AskLeaveJudge extends AbstractBinaryComparableJudge {


    @Override
    protected String fieldNameInDb() {
        return "leaveHour";
    }

    @Override
    protected String fieldNameInStartConditions() {
        return "leaveHour";
    }
}

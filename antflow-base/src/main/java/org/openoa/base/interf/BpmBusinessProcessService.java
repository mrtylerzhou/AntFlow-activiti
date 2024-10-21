package org.openoa.base.interf;

import org.openoa.base.entity.BpmBusinessProcess;

public interface BpmBusinessProcessService {
    BpmBusinessProcess getBpmBusinessProcess(String processCode);
}

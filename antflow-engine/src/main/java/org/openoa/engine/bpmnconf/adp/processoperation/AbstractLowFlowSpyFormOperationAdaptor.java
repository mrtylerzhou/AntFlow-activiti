package org.openoa.engine.bpmnconf.adp.processoperation;

import org.openoa.base.interf.ActivitiService;
import org.openoa.base.interf.FormOperationAdaptor;
import org.openoa.base.vo.BusinessDataVo;

/**
 * @since 1.6.0
 *
 * @param <T>
 */
public abstract class AbstractLowFlowSpyFormOperationAdaptor<T extends BusinessDataVo>  implements FormOperationAdaptor<T>, ActivitiService {
}

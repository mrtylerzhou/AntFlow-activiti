package org.openoa.base.interf;

import org.openoa.base.vo.BusinessDataVo;

/**
 * it is a core interface for process operation
 */
public interface ProcessOperationAdaptor extends AdaptorService{

    void doProcessButton(BusinessDataVo vo);

}

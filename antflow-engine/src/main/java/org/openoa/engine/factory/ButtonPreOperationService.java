package org.openoa.engine.factory;

import org.openoa.base.vo.BusinessDataVo;

/**
 * @Author TylerZhou
 * @Date 2024/7/7 9:27
 * @Version 1.0
 */
public interface ButtonPreOperationService {
    BusinessDataVo buttonsPreOperation(String params, String formCode);
}

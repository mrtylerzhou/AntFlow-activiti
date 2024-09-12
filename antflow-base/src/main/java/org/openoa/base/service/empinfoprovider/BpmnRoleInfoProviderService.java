package org.openoa.base.service.empinfoprovider;

import java.util.Collection;
import java.util.Map;

/**
 * @Author TylerZhou
 * @Date 2024/7/17 22:30
 * @Version 1.0
 */
public interface BpmnRoleInfoProviderService {
    Map<String,String> provideRoleInfo(Collection<String> roleIds);
    Map<String,String> provideRoleEmployeeInfo(Collection<String> roleIds);
}

package org.openoa.base.service.empinfoprovider;

import java.util.Collection;
import java.util.Map;

/**
 * used for getting employee info from data source
 *@Author JimuOffice
 * @Description  generic employee info provider
 * @since 0.5
 * @Param
 * @return
 * @Version 0.5
 */
public interface BpmnEmployeeInfoProviderService {
    Map<String,String> provideEmployeeInfo(Collection<String> empIds);
}

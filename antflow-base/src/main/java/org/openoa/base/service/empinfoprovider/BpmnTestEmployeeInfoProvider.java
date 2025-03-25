package org.openoa.base.service.empinfoprovider;

import com.google.common.collect.Maps;
import org.openoa.base.service.AfUserService;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *@Author JimuOffice
 * @Description //TODO $
 * @Date 2022-05-01 10:22
 * @Param
 * @return
 * @Version 1.0
 */
@Component
public class BpmnTestEmployeeInfoProvider implements BpmnEmployeeInfoProviderService {
    @Autowired
    private AfUserService userService;

    @Override
    public Map<String, String> provideEmployeeInfo(Collection<String> empIds) {

        List<BaseIdTranStruVo> users = userService.queryUserByIds(empIds);
        if(CollectionUtils.isEmpty(users)){
            return Maps.newHashMap();
        }
        Map<String,String>empIdAndNameMap = users.stream().collect(Collectors.toMap(BaseIdTranStruVo::getId,BaseIdTranStruVo::getName,(k1, k2)->k1));

        return empIdAndNameMap;
    }
}

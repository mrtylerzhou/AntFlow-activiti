package org.openoa.base.util;

import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.entity.Employee;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author TylerZhou
 * @Date 2024/7/17 7:22
 * @Version 1.0
 */
public class EmployeeUtil {
    public static List<Employee> basicEmployeeInfos(List<BaseIdTranStruVo> litemployeeList){
        if(CollectionUtils.isEmpty(litemployeeList)){
            return new ArrayList<>();
        }
       return litemployeeList
               .stream()
               .map(EmployeeUtil::basicEmployeeInfo)
               .collect(Collectors.toList());
    }

    public static Employee basicEmployeeInfo(BaseIdTranStruVo litemployee){
      return   Employee.builder()
                .id(litemployee.getId())
                .username(litemployee.getName())
                .build();
    }
}

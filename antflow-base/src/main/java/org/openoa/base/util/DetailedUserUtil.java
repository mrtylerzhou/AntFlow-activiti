package org.openoa.base.util;

import org.openoa.base.entity.DetailedUser;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author TylerZhou
 * @Date 2024/7/17 7:22
 * @Version 1.0
 */
public class DetailedUserUtil {
    public static List<DetailedUser> basicEmployeeInfos(List<BaseIdTranStruVo> litemployeeList){
        if(CollectionUtils.isEmpty(litemployeeList)){
            return new ArrayList<>();
        }
       return litemployeeList
               .stream()
               .map(DetailedUserUtil::basicEmployeeInfo)
               .collect(Collectors.toList());
    }

    public static DetailedUser basicEmployeeInfo(BaseIdTranStruVo liteUserInfo){
      return   DetailedUser.builder()
                .id(liteUserInfo.getId())
                .username(liteUserInfo.getName())
                .build();
    }
}

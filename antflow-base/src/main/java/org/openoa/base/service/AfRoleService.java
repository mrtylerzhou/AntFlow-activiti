package org.openoa.base.service;

import org.openoa.base.entity.Role;
import org.openoa.base.entity.User;
import org.openoa.base.vo.BaseIdTranStruVo;

import java.util.Collection;
import java.util.List;

public interface AfRoleService {
    List<BaseIdTranStruVo> queryRoleByIds(Collection<String> roleIds);

    List<BaseIdTranStruVo> queryUserByRoleIds(Collection<String> roleIds);
    //如果用户是将流程引擎嵌入到系统中,或者不是完全意义上的SaSS流程,则留空不实现即可,至于何谓完全意义上的SaSS流程,application.properties里的antflow.sass.full-sass-mode配置项的描述
    List<BaseIdTranStruVo> querySassUserByRoleIds(Collection<String> roleIds);
}

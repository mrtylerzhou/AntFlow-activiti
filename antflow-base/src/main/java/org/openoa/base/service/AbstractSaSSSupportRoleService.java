package org.openoa.base.service;

import org.apache.commons.lang3.NotImplementedException;
import org.openoa.base.vo.BaseIdTranStruVo;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class AbstractSaSSSupportRoleService implements  AfRoleService{
    @Override
    public List<BaseIdTranStruVo> querySassUserByRoleIds(Collection<String> roleIds) {
        throw new NotImplementedException("暂未实现");
    }
}

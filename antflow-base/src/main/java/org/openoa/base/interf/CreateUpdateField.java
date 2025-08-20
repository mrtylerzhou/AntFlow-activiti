package org.openoa.base.interf;

import org.openoa.base.util.SecurityUtils;

import java.util.Date;

public interface CreateUpdateField {
    String getCreateUser();
    void  setCreateUser(String createUser);
    Date getCreateTime();
    void  setCreateTime(Date createTime);
    String getUpdateUser();
    void setUpdateUser(String updateUser);
    Date getUpdateTime();
    void  setUpdateTime(Date updateTime);
    default void setCreateInfo(){
        setCreateUser(SecurityUtils.getLogInEmpIdStr());
        setCreateTime(new Date());
    }
    default void  setUpdateInfo(){
        setUpdateUser(SecurityUtils.getLogInEmpIdSafe());
        setUpdateTime(new Date());
    }
}

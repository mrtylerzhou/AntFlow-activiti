package org.openoa.base.vo;

import lombok.Data;

import java.util.List;

@Data
public class FormRelatedAssignee {
    List<BaseIdTranStruVo> formUsers;
    List<String> formRoles;
    List<String> formUserHrbp;
    List<String> formUserDirectLeaders;
    List<String> formUserDepartLeaders;
    List<String> formDepartLeaders;
    List<String> formUserLevelLeaders;
    List<String> formUserLoopLeaders;
}

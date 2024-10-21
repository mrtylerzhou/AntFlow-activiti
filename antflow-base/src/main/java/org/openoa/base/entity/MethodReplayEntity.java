package org.openoa.base.entity;

import lombok.Data;

@Data
public class MethodReplayEntity {

    private Long id;
    private String projectName;
    private String className;
    private String methodName;
    private String paramType;
    private String args;
    private String nowTime;
    private String errorMsg;
    private Integer alreadyReplayTimes;
    private Integer maxReplayTimes;

}

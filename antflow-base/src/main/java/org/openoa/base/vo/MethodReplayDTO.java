package org.openoa.base.vo;

import lombok.Data;

import java.util.List;

@Data
public class MethodReplayDTO {

    private List<Long> delIds;
    private Long id;
    private String projectName;
    private String className;
    private String methodName;
    private String args;
    private String nowTime;
    private String errorMsg;

}

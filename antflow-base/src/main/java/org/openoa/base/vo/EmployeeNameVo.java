package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeNameVo implements Serializable {

    private String name;

    private String username;

    private Integer id;

    private String jobNum;

    private String jobName;

    private String userInfo;
    //====================
    private String leader;
    private Integer leaderId;

    private Integer departmentId;
    private String department;

    private Integer status;

    private String curDepartmentLeader;
    //===================
    private String path;
    private String pathStr;
}
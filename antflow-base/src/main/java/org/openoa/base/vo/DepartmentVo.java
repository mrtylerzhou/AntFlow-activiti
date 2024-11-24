package org.openoa.base.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_NULL;

/**
 * 部门信息
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonSerialize(include = NON_NULL)
public class DepartmentVo implements Serializable {

    private Integer id;
    private Date createTime;
    private Date updateTime;
    /**
     * 名称
     */
    private String name;
    /**
     * 父级编号
     */
    private Integer parentId;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 排序
     */
    private Integer level;
    /**
     * 是否已删除
     */
    private Integer isDel;
    /**
     * 是否隐藏，0，显示；1，隐藏；
     */
    private Integer isHide;
    /**
     * 部门ID数组（拖动部门后在父部门内和兄弟部门的排序）
     */
    private List<Integer> ids;


    /**
     * 部门枚举路径
     */
    private String path;
    //部门的中文路径
    private String pathStr;

    private String parentPath;
    //父部门的str
    private String parentPathStr;

    private String departmentLeader;
    //部门人数
    private Integer peopleNum;


    //组织负责人
    private Integer departmentLeaderId;

    private String curDepartmentLeaderUsername;

    private List<EmployeeVo> curDepartmentLeaderList;

    private Integer employeeId;
    private String employeeName;

    //=========================

    private Integer departmentId;
    private String departmentName;
    private String departmentNamePath;

    /**
     * 是否有效，0，无效；1，有效；
     */
    private Integer isValid;

    private String oneDept;
    private String twoDept;
    private String threeDept;
    private String fourDept;
    private String fiveDept;
    private String sixDept;
    private String sevenDept;
}

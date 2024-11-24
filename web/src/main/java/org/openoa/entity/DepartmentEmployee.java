package org.openoa.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 员工和部门关系表
 * 此表为demo表，主要用于展示部门负责人审批，组织线审批以及部门hrbp审批功能功能
 */
@Data
@TableName("t_department_employee")
public class DepartmentEmployee implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField("department_id")
    private Integer departmentId;
    @TableField("employee_id")
    private Integer employeeId;
    /**
     * 是否组织领导
     */
    @TableField("is_leader")
    private Integer isLeader;
    /**
     * 是否是部门主负责人
     */
    @TableField("is_master")
    private Integer isMaster;
    /**
     * 是否是hrbp
     */
    @TableField("is_hrbp")
    private Integer isHrbp;
    @TableField("create_time")
    private Date createTime;
    @TableField("update_time")
    private Date updateTime;

}

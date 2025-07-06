package org.openoa.base.vo;

import com.sun.corba.se.spi.ior.IORTemplateList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.activiti.bpmn.converter.export.CollaborationExport;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Classname BpmnTemplateVo
 * @Description notice template vo
 * @since 0.5
 * @Created by AntOffice
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BpmnTemplateVo implements Serializable {
    private Long id;
    /**
     * conf id
     */
    private Long confId;
    /**
     * node id
     */
    private Long nodeId;
    /**
     * event
     */
    private Integer event;
    private String eventValue;
    /**
     * who to inform
     */
    private String informs;
    private List<String> informIdList;
    private List<BaseIdTranStruVo> informList;
    /**
     * specified employee
     */
    private String emps;
    private List<String> empIdList;
    private List<BaseIdTranStruVo> empList;
    /**
     * specified roles
     */
    private String roles;
    private List<String> roleIdList;
    private List<BaseIdTranStruVo> roleList;
    /**
     * specified functions
     */
    private String funcs;
    private List<String> funcIdList;
    private List<BaseIdTranStruVo> funcList;
    /**
     * template id
     */
    private Long templateId;
    private String templateName;
    private List<BaseNumIdStruVo> messageSendTypeList;
    private String formCode;
    /**
     * 0 for not deleted, 1 for deleted
     */
    private Integer isDel;
    /**
     * create time
     */
    private Date createTime;
    /**
     * create user
     */
    private String createUser;
    /**
     * update time
     */
    private Date updateTime;
    /**
     * update user
     */
    private String updateUser;

    public void setEmpList(List<BaseIdTranStruVo> empList) {
        this.empList = empList;
        if(!CollectionUtils.isEmpty(empList)){
            this.empIdList = empList.stream().map(BaseIdTranStruVo::getId).collect(Collectors.toList());
        }
    }
    public void setRoleList(List<BaseIdTranStruVo> roleList) {
        this.roleList = roleList;
        if(!CollectionUtils.isEmpty(roleList)){
            this.roleIdList = roleList.stream().map(BaseIdTranStruVo::getId).collect(Collectors.toList());
        }
    }
}

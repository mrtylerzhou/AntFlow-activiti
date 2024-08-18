package org.openoa.engine.bpmnconf.confentity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openoa.base.constant.enums.ButtonTypeEnum;
import org.openoa.base.util.SecurityUtils;

import java.util.Date;

/**
 * @author AntFlow
 * @since 0.5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_bpmn_view_page_button")
public class BpmnViewPageButton {

    /**
     * auto incr id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * conf id
     */
    @TableField("conf_id")
    private Long confId;
    /**
     * view type 1 start user 2 other approver
     */
    @TableField("view_type")
    private Integer viewType;
    /**
     * button type 1-submit 2-reSubmit 3-agree 4-disagree 5-back 6-backToPrevNode 7-cancel 8-print 9-forward
     */
    @TableField("button_type")
    private Integer buttonType;
    /**
     * button name
     */
    @TableField("button_name")
    private String buttonName;
    /**
     * remark
     */
    private String remark;

    @TableField("is_del")
    private Integer isDel;
    /**
     * create user
     */
    @TableField("create_user")
    private String createUser;
    /**
     * create time
     */
    @TableField("create_time")
    private Date createTime;
    /**
     * update user
     */
    @TableField("update_user")
    private String updateUser;
    /**
     * update time
     */
    @TableField("update_time")
    private Date updateTime;

    public static BpmnViewPageButton buildViewPageButton(Long confId, Integer buttonTypeCode, Integer viewPageTypeCode) {
        return BpmnViewPageButton
                .builder()
                .confId(confId)
                .viewType(viewPageTypeCode)
                .buttonType(buttonTypeCode)
                .buttonName(ButtonTypeEnum.getDescByCode(buttonTypeCode))
                .createUser(SecurityUtils.getLogInEmpNameSafe())
                .createTime(new Date())
                .updateUser(SecurityUtils.getLogInEmpNameSafe())
                .updateTime(new Date())
                .build();
    }
}

package org.openoa.engine.bpmnconf.confentity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.exception.JiMuBizException;

import java.util.Date;
import java.util.regex.Pattern;

import static org.openoa.base.constant.NumberConstants.BPMN_NAME_MAX_LEN;
import static org.openoa.base.constant.StringConstants.SPECIAL_CHARACTERS;

/**
 * @author AntFlow
 * @since 0.0.1
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_bpmn_conf")
public class BpmnConf {


    /**
     * auto incr id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * bpmn Code
     */
    @TableField("bpmn_code")
    private String bpmnCode;
    /**
     * bpmn Name
     */
    @TableField("bpmn_name")
    private String bpmnName;
    /**
     * bpmn Type
     */
    @TableField("bpmn_type")
    private Integer bpmnType;
    /**
     * formCode
     */
    @TableField("form_code")
    private String formCode;
    /**
     * appId
     */
    @TableField("app_id")
    private Integer appId;
    /**
     * dedup type(1 - no dedup; 2 - dedup forward; 3 - dedup backward)
     */
    @TableField("deduplication_type")
    private Integer deduplicationType;
    /**
     * effective status 0 for no and 1 for yes
     */
    @TableField("effective_status")
    private Integer effectiveStatus;
    /**
     * is for all 0 no and 1 yes
     */
    @TableField("is_all")
    private Integer isAll;

    /**
     * is third party process 0 for no and 1 yes
     */
    @TableField("is_out_side_process")
    private Integer isOutSideProcess;

    /**
     * business party mark
     */
    @TableField("business_party_id")
    private Integer businessPartyId;

    /**
     * remark
     */
    private String remark;
    /**
     * is del 0 for no and 1 yes
     */
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

    public void setBpmnName(String bpmnName) {
        validateBpmnName(bpmnName);
        this.bpmnName=bpmnName;
    }

    public static void validateBpmnName(String bpmnName) {
        if (StringUtils.isBlank(bpmnName)) {
            throw new JiMuBizException("审批流名称必须存在!");
        }

        if (Pattern.matches(PATTERN, bpmnName)) {
            throw new JiMuBizException("审批流名称不合法");
        }
        if (StringUtils.isAnyEmpty(bpmnName)) {
            throw new JiMuBizException("审批流名称不得包含空格");
        }
        if (Pattern.matches(SPECIAL_CHARACTERS, bpmnName)) {
            throw new JiMuBizException("审批流名称中不得包含特殊字符!");
        }
        if (bpmnName.length() > BPMN_NAME_MAX_LEN) {
            throw new JiMuBizException("审批流名称过长");
        }
    }

    public static final Integer BPMN_CODE_LEN = 5;
    //.*-([0-9]{5})
    public static final String PATTERN = ".*-([0-9]{" + BPMN_CODE_LEN + "})";
    @TableField(exist = false)
    public static final String formatMark = "%0" + BPMN_CODE_LEN + "d";
}

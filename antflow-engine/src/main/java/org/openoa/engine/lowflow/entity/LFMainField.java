package org.openoa.engine.lowflow.entity;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.annotation.*;
import com.google.common.base.Strings;
import lombok.Data;
import lombok.Value;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.enums.LFControlTypeEnum;
import org.openoa.base.constant.enums.LFFieldTypeEnum;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.util.DateUtil;
import org.openoa.base.util.SnowFlake;
import org.openoa.engine.bpmnconf.confentity.BpmnConfLfFormdataField;
import org.springframework.util.CollectionUtils;

import java.io.PipedReader;
import java.math.BigDecimal;
import java.util.*;

@Data
@TableName("t_lf_main_field")
public class LFMainField {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;
    @TableField("main_id")
    private Long mainId;
    @TableField("form_code")
    private String formCode;
    @TableField("field_id")
    private String fieldId;
    @TableField("field_name")
    private String fieldName;
    @TableField("parent_field_id")
    private String parentFieldId;
    @TableField("parent_field_name")
    private String parentFieldName;
    @TableField("field_value")
    private String fieldValue;
    @TableField("field_value_number")
    private Double fieldValueNumber;
    @TableField("field_value_dt")
    private Date fieldValueDt;
    @TableField(" field_value_text")
    private String fieldValueText;
    private Integer sort=0;

    /**
     * 逻辑删除标记（0：未删除，1：已删除）
     */
    @TableLogic
    @TableField("is_del")
    private Integer isDel;

    /**
     * 创建人
     */
    @TableField("create_user")
    private String createUser;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新人
     */
    @TableField("update_user")
    private String updateUser;
    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;


    public static List<LFMainField> parseFromMap(Map<String,Object> fieldMap, Map<String,BpmnConfLfFormdataField> fieldConfigMap, Long mainId,String formCode){
        if(CollectionUtils.isEmpty(fieldMap)){
            throw new JiMuBizException("form data has no value");
        }
        if(CollectionUtils.isEmpty(fieldConfigMap)){
            throw new JiMuBizException("field configs are empty,please check your logic");
        }
        List<LFMainField> mainFields=new ArrayList<>(fieldMap.size());
        for (Map.Entry<String, Object> fieldId2ValueEntry : fieldMap.entrySet()) {
            String fieldId = fieldId2ValueEntry.getKey();
            BpmnConfLfFormdataField fieldConfig = fieldConfigMap.get(fieldId);
            if(fieldConfig==null){
                continue;
                //throw new JiMuBizException(Strings.lenientFormat("field %s has no config",fieldId));
            }
            Object value = fieldId2ValueEntry.getValue();
            LFMainField mainField = buildMainField(value, mainId, 0, fieldConfig);
            mainField.setFormCode(formCode);
            mainFields.add(mainField);
        }
        return mainFields;
    }
    public static LFMainField buildMainField(Object fieldValue, Long mainId,int sort,BpmnConfLfFormdataField fieldConfig){
        String fieldValueStr=null;
        if(fieldValue!=null){
            fieldValueStr=fieldValue.toString();
        }
        LFMainField mainField=new LFMainField();
        mainField.setId(SnowFlake.nextId());
        mainField.setMainId(mainId);
        mainField.setFieldId(fieldConfig.getFieldId());
        mainField.setFieldName(fieldConfig.getFieldName());
        Integer fieldType = fieldConfig.getFieldType();
        LFFieldTypeEnum fieldTypeEnum = LFFieldTypeEnum.getByType(fieldType);
        if(fieldTypeEnum==null){
            throw new JiMuBizException(Strings.lenientFormat("field type can not be empty,%s",fieldConfig));
        }
        switch (fieldTypeEnum){
            case STRING:
                mainField.setFieldValue(fieldValueStr);
                break;
            case NUMBER:
               if(LFControlTypeEnum.SELECT.getName().equals(fieldConfig.getFieldName())){
                   mainField.setFieldValue(fieldValueStr);
               }else{
                   Double fieldValueNumber = !StringUtils.isEmpty(fieldValueStr) ? Double.parseDouble(fieldValueStr) : null;
                   mainField.setFieldValueNumber(fieldValueNumber);
               }
                break;
            case DATE:
            case DATE_TIME:
                Date fieldValueDt = !StringUtils.isEmpty(fieldValueStr) ? DateUtil.parseStandard(fieldValueStr) : null;
                mainField.setFieldValueDt(fieldValueDt);
                break;
            case TEXT:
                mainField.setFieldValueText(fieldValueStr);
                break;
            case BOOLEAN:
                mainField.setFieldValue(String.valueOf(fieldValueStr));
                break;
        }
        mainField.setSort(sort);
        return mainField;
    }
}

package org.openoa.base.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class BpmnNodeLabelVO {

    public BpmnNodeLabelVO(){}
    public BpmnNodeLabelVO(String labelValue,String labelName){
        this.labelName=labelName;
        this.labelValue=labelValue;
    }
    private String labelName;


    private String labelValue;

}

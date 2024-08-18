package org.openoa.engine.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_NULL;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize(include=NON_NULL)
public class ProcessTypeInforVo implements Serializable {
    /**
     * 流程类别名称
     */
    private String processTypeName;
    /**
     * 流程icon
     */
    public List<BpmProcessAppApplicationVo> applicationList;
    /**
     * 流程icon
     */
    public List<BpmProcessAppApplicationVo> iconList;

    /**
     * 常用功能
     */
    public List<BpmProcessAppApplicationVo> commonFunction;
    /**
     *类别id
     */
    private Integer typeId;

}

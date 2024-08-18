package org.openoa.base.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 *  process button config
 * @since 0.5
 * @author AntFlow
 */
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ProcessActionButtonVo {
    /**
     * button type
     */
    public Integer buttonType;
    /**
     * 1、处理 2、查看
     * 1 for operation 2 for view
     */
    public Integer show;
    /**
     * primary：color default: default color
     */
    public String type;
    /**
     * button name
     */
    public String name;
    /**
     * app show way 1 show on top 2 show on bottom 3 show on both
     */
    public Integer appShow;
}

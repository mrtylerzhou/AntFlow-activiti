package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @since 0.5
 * @author AntFlow
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BpmnConfCommonButtonPropertyVo {

    /**
     * button type
     */
    private Integer buttonType;

    /**
     * button name
     */
    private String buttonName;

}
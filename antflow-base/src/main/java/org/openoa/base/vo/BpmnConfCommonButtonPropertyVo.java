package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @since 0.5
 * @author AntFlow
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BpmnConfCommonButtonPropertyVo  implements Serializable {

    /**
     * button type
     */
    private Integer buttonType;

    /**
     * button name
     */
    private String buttonName;

}
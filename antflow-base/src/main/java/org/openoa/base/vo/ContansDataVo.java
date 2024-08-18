package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: AntFlow
 * @since 0.0.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContansDataVo {
    /**
     * data id
     */
    private Integer id;

    /**
     * data name
     */
    private String name;
    /***
     * task id
     */
    private String taskId;
}

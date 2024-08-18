package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * flow entry vo
 * @since 0.5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BpmFlowrunEntrustVo implements Serializable {

    private Integer id;
    /**
     * process instance id
     */
    private String runinfoid;
    /**
     * task id
     */
    private String runtaskid;
    /**
     * original assignee
     */
    private Integer original;
    /**
     * actual assignee
     */
    private Integer actual;
    /**
     * type 1 for entrust task, 2 for circulate task
     */
    private Integer type;
    /**
     * 0 for no and 1 for yes
     */
    private Integer idDel;
    /**
     * process definition id
     */
    private String procDefId;
    /**
     * is read
     */
    private Integer isView;

}

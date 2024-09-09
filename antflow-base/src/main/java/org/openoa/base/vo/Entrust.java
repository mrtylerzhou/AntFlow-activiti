package org.openoa.base.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author AntFlow
 * @since 0.5
 */
@Data
public class Entrust implements Serializable {

    /**
     * start time
     */
    private Date beginTime;

    private String powerId;
    /**
     * end time
     */
    private Date endTime;
    private Integer sender;
    /**
     * entrust name
     */
    private String name;

    private Integer id;

    /**
     * receiver id
     */
    private String receiverId;

    /**
     * receiver name
     */
    private String receiverName;
    /**
     */
    private Date createTime;

}

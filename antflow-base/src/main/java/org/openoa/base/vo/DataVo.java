package org.openoa.base.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * @author AntFlow
 * @since  0.0.1
 */
@Data
public class DataVo implements Serializable {

    private List<IdsVo> ids;

    private Integer receiverId;
    /**
     * entrust user name
     */
    private String receiverName;

    /**
     * start time
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date beginTime;

    /**
     * end time
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endTime;
}

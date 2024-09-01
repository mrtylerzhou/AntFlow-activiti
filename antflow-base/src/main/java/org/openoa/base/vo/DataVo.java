package org.openoa.base.vo;

import lombok.Data;
import java.util.Date;
import java.util.List;
/**
 * @author AntFlow
 * @since  0.0.1
 */
@Data
public class DataVo  {

    private List<IdsVo> ids;
    private Integer sender;
    private Integer receiverId;
    /**
     * entrust user name
     */
    private String receiverName;

    /**
     * start time
     */
    private Date beginTime;

    /**
     * end time
     */
    private Date endTime;
}
